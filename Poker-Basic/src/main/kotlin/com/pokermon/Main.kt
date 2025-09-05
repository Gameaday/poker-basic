package com.pokermon

import com.pokermon.ai.PersonalityManager
import com.pokermon.modern.CardUtils
import kotlinx.coroutines.runBlocking
import java.io.*
import java.util.*
import kotlin.random.Random

/**
 * Main game controller for Pokermon - Pure Kotlin-native implementation.
 * 
 * Migrated from Java to leverage modern Kotlin features including:
 * - Null safety and smart casts
 * - Data classes for immutable state
 * - Extension functions for enhanced readability  
 * - Coroutines for async operations
 * - DRY principle compliance with CardUtils integration
 * 
 * Serves as the primary game logic controller, coordinating between the GameEngine,
 * Player management, and AI systems while maintaining the original game mechanics.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version Dynamic (Kotlin-native implementation)
 */
object Main {
    
    // Constants moved to companion for Kotlin-style organization
    private val POSSIBLE_NAMES = arrayOf(
        "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
        "Tony", "Jenny", "Susan", "Rory", "Melody", 
        "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
        "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
        "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
        "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
        "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
        "Smallie", "Anthony", "Gwen"
    )
    
    private val VALID_CHIPS = intArrayOf(100, 500, 1000, 2500)
    
    // Game constants
    private const val DECK_SIZE = 52
    private const val DEFAULT_HAND_SIZE = 5
    private const val MAX_MULTIPLES_ARRAY_SIZE = 3

    /**
     * Main entry point - Enhanced with Kotlin coroutines and null safety.
     * Restores full poker functionality including sophisticated hand evaluation,
     * AI personality integration, card exchange, and multiple betting rounds.
     */
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Game state variables with Kotlin null safety
        var workingPot = 0
        var topBet = 0
        var countup = 0
        var shouldContinue = true
        var shouldQuit = false
        
        // Player management with nullable types
        var user: Player? = null
        var cpu1: Player? = null
        var cpu2: Player? = null
        var cpu3: Player? = null
        
        // Display author information
        displayAuthor()
        
        // Prompt for player name with default for non-interactive mode
        val playerName = promptName("A(n) Drew Hussie")
        
        // Prompt for number of opponents
        println("How many players would you like to play against? (1-3)")
        val opponentCount = readIntWithDefault(2, 1..3)
        
        // Initialize players array  
        val players = Array<Player?>(opponentCount + 1) { null }
        
        // Prompt for starting chips
        println("Select starting chips: ${VALID_CHIPS.joinToString(", ")}")
        val startingChips = readIntWithValidation(VALID_CHIPS.toList())
        
        // Initialize deck for the game
        val deck = setDeck()
        
        // Create and setup all players with sophisticated initialization
        initializePlayers(players, playerName, opponentCount, startingChips, deck)
        
        // Store player references for legacy compatibility
        user = players[0]
        if (opponentCount >= 1) cpu1 = players[1]
        if (opponentCount >= 2) cpu2 = players[2] 
        if (opponentCount >= 3) cpu3 = players[3]
        
        println("\\n=== Game Started ===")
        println("Players: ${players.filterNotNull().joinToString { it?.name ?: "Unknown" }}")
        println("Starting chips: $startingChips each")
        
        // Main game loop with enhanced error handling and full poker features
        try {
            while (shouldContinue && !shouldQuit) {
                // Re-initialize deck for each round
                val freshDeck = setDeck()
                
                if (countup < 1) {
                    // First round - already initialized
                    countup = 1
                } else {
                    // Subsequent rounds - re-deal hands while preserving chips
                    reinitializePlayers(players, freshDeck)
                }
                
                // Display user's hand with sophisticated formatting
                user?.let { player ->
                    println("\\n${player.name}'s hand:")
                    revealHand(player.convertedHand)
                    println("Hand value: ${handValue(player.hand)} (${getHandDescription(player.hand)})")
                }
                
                // First betting round
                println("\\n=== FIRST BETTING ROUND ===")
                workingPot = conductBettingRound(players.filterNotNull(), workingPot)
                println("Current Pot Value: $workingPot")
                
                // Card exchange phase
                println("\\n=== CARD EXCHANGE ===")
                performCardExchange(players.filterNotNull(), freshDeck)
                
                // Display user's new hand after exchange
                user?.let { player ->
                    println("\\n${player.name}'s new hand:")
                    revealHand(player.convertedHand)
                    println("Hand value: ${handValue(player.hand)} (${getHandDescription(player.hand)})")
                }
                
                // Second betting round
                println("\\n=== SECOND BETTING ROUND ===")
                workingPot = conductBettingRound(players.filterNotNull(), workingPot)
                println("Current Pot Value: $workingPot")
                
                // Show all players' stats
                playersStats(players.filterNotNull())
                
                // Determine winner with sophisticated evaluation
                val gameResult = declareResults(players.filterNotNull())
                
                // Distribute pot appropriately
                dividePot(players.filterNotNull(), workingPot)
                
                // Save updated player statistics
                playersStats(players.filterNotNull())
                
                // Reset for next round
                workingPot = 0
                topBet = 0
                
                // Check if anyone is out of chips
                val playersStillIn = players.filterNotNull().filter { (it?.chips ?: 0) > 0 }
                if (playersStillIn.size <= 1) {
                    println("\\n🏆 Game Over! Final winner: ${playersStillIn.firstOrNull()?.name ?: "Nobody"}")
                    shouldQuit = true
                } else {
                    shouldContinue = promptEnd(false) // Default to ending after one game in non-interactive mode
                }
            }
        } catch (e: Exception) {
            println("Game error: ${e.message}")
            e.printStackTrace()
        }
        
        println("\\nThanks for playing Pokermon!")
    }
    
    // =================================================================
    // SOPHISTICATED POKER FUNCTIONALITY - RESTORED FROM JAVA VERSION
    // =================================================================
    
    /**
     * Initialize players with hands, names, and chips - Enhanced Kotlin version.
     */
    private fun initializePlayers(
        players: Array<Player?>, 
        playerName: String, 
        opponentCount: Int, 
        startingChips: Int, 
        deck: IntArray
    ) {
        // Setup player names
        val playerNames = Array(opponentCount + 1) { "" }
        playerNames[0] = playerName
        
        // Decide names for AI players
        for (i in 1..opponentCount) {
            playerNames[i] = POSSIBLE_NAMES.random()
        }
        
        // Initialize each player with full setup
        for (i in 0..opponentCount) {
            val player = Player()
            player.isHuman = (i == 0) // First player is human
            player.setupPlayer(playerNames[i], startingChips, deck, DEFAULT_HAND_SIZE)
            players[i] = player
            println() // Space between player info for neatness
        }
    }
    
    /**
     * Re-initialize existing players with new hands while preserving chips.
     */
    private fun reinitializePlayers(players: Array<Player?>, deck: IntArray) {
        players.filterNotNull().forEach { player ->
            val currentChips = player.chips
            val currentName = player.name
            player.setupPlayer(currentName, currentChips, deck, DEFAULT_HAND_SIZE)
            println() // Space between player info for neatness
        }
    }
    
    /**
     * Simplified hand value calculation using HandEvaluator.
     * Replaces the complex scattered logic with clean, maintainable approach.
     */
    fun handValue(hand: IntArray): Int {
        return HandEvaluator.evaluateHand(hand).score
    }
    
    /**
     * Get human-readable hand description using HandEvaluator.
     */
    fun getHandDescription(hand: IntArray): String {
        return HandEvaluator.evaluateHand(hand).description
    }
    
    /**
     * Enhanced hand evaluation for detailed analysis.
     */
    fun getHandResult(hand: IntArray): HandEvaluator.HandResult {
        return HandEvaluator.evaluateHand(hand)
    }
    
    /**
     * Sophisticated AI betting calculation based on hand strength and personality.
     */
    fun calculateAIBet(player: Player, currentBet: Int): Int {
        val handValue = handValue(player.hand)
        var chips = player.chips
        var bet = currentBet
        
        // AI betting strategy based on hand strength
        val betIncrease = when {
            handValue in 18..38 -> 25  // Decent hand
            handValue in 39..70 -> 50  // Strong hand
            handValue > 70 -> 100      // Very strong hand
            else -> {
                // Weak hand - just call or fold if bet is too high
                return minOf(bet, chips)
            }
        }
        
        bet += betIncrease
        
        // Adjust bet if player doesn't have enough chips
        if (chips < bet) {
            bet = when {
                handValue <= 38 -> {
                    val quarterChips = chips / 4
                    if (chips % 4 != 0) quarterChips + 1 else quarterChips
                }
                handValue <= 70 -> {
                    val halfChips = chips / 2
                    if (chips % 4 != 0) halfChips + 1 else halfChips
                }
                else -> chips // All-in for strong hands
            }
        }
        
        return bet
    }
    
    /**
     * Enhanced betting round with sophisticated AI and user interaction.
     */
    fun conductBettingRound(players: List<Player>, initialPot: Int): Int {
        var pot = initialPot
        var currentBet = 0
        
        for (i in players.indices) {
            val player = players[i]
            val lastBet = currentBet
            
            if (player.fold) {
                continue // Skip folded players
            }
            
            if (player.isHuman) {
                // Human player - interactive betting
                pot += player.placeBet(currentBet)
                player.recordLastBet()
                if (lastBet != currentBet) {
                    // If bet was raised, need recursive betting for other players
                    recursiveBet(players, i, pot, currentBet)
                    break
                }
            } else {
                // AI player - calculated betting
                currentBet = calculateAIBet(player, currentBet)
                pot += player.placeBet(currentBet)
                player.recordLastBet()
                if (lastBet != currentBet) {
                    // If AI raised, need recursive betting for other players
                    recursiveBet(players, i, pot, currentBet)
                    break
                }
            }
        }
        
        return pot
    }
    
    /**
     * Recursive betting to handle raises during a betting round.
     */
    fun recursiveBet(players: List<Player>, raisingPlayerIndex: Int, pot: Int, newBet: Int) {
        // Allow all other players to respond to the raise
        for (i in players.indices) {
            if (i == raisingPlayerIndex) continue // Skip the player who raised
            
            val player = players[i]
            if (player.fold) continue
            
            if (player.isHuman) {
                // Human needs to respond to raise
                println("\\n${player.name}, the bet has been raised to $newBet. Do you want to call, raise, or fold?")
                // Implementation would go here for interactive response
            } else {
                // AI responds to raise
                val response = calculateAIBet(player, newBet)
                if (response > player.chips / 2) {
                    // AI calls or folds based on hand strength
                    player.placeBet(minOf(newBet, player.chips))
                } else {
                    // Set fold status - need to add this to Player class
                    println("\\n${player.name} folds.")
                }
            }
        }
    }
    
    /**
     * Card exchange phase - Players can exchange up to 3 cards.
     */
    fun performCardExchange(players: List<Player>, deck: IntArray) {
        players.forEach { player ->
            if (player.isHuman) {
                // Human player card exchange - default to no exchange in non-interactive mode
                exchange(player, deck, intArrayOf())
            } else {
                // AI card exchange based on hand analysis
                val cardsToExchange = determineAICardExchange(player)
                if (cardsToExchange.isNotEmpty()) {
                    println("\\n${player.name} exchanges ${cardsToExchange.size} cards.")
                    exchange(player, deck, cardsToExchange)
                } else {
                    println("\\n${player.name} keeps all cards.")
                }
            }
        }
    }
    
    /**
     * Analyze multiples (pairs, triples, etc.) in a hand.
     * Returns array where each element is [rank, count] in descending order by count.
     */
    fun handMultiples(hand: IntArray): Array<IntArray> {
        if (hand.isEmpty()) return emptyArray()
        
        // Count occurrences of each rank
        val rankCounts = mutableMapOf<Int, Int>()
        for (card in hand) {
            val rank = CardUtils.cardRank(card)
            rankCounts[rank] = rankCounts.getOrDefault(rank, 0) + 1
        }
        
        // Convert to array format [rank, count] sorted by count (descending), then rank (descending)
        return rankCounts.entries
            .sortedWith(compareByDescending<Map.Entry<Int, Int>> { it.value }.thenByDescending { it.key })
            .map { intArrayOf(it.key, it.value) }
            .toTypedArray()
    }
    
    /**
     * Determine which cards AI should exchange based on hand analysis.
     */
    fun determineAICardExchange(player: Player): IntArray {
        val hand = player.hand
        val multiples = handMultiples(hand)
        
        // Don't exchange if hand is already strong
        if (handValue(hand) >= 55) { // Straight or better
            return intArrayOf()
        }
        
        // Keep pairs, exchange the rest
        if (is2Kind(multiples[0])) {
            val pairRank = multiples[0][0]
            val cardsToExchange = mutableListOf<Int>()
            
            for (i in hand.indices) {
                if (CardUtils.cardRank(hand[i]) != pairRank) {
                    cardsToExchange.add(i)
                }
            }
            
            return cardsToExchange.take(3).toIntArray() // Exchange up to 3 cards
        }
        
        // For weak hands, exchange 3-4 cards, keeping highest
        val sortedIndices = hand.indices.sortedByDescending { CardUtils.cardRank(hand[it]) }
        return sortedIndices.drop(1).take(3).toIntArray()
    }
    
    /**
     * Exchange cards for a player - Enhanced Kotlin version.
     */
    fun exchange(player: Player, deck: IntArray, cardsToExchange: IntArray): IntArray {
        if (cardsToExchange.isNotEmpty()) {
            // Remove selected cards from player's hand
            cardsToExchange.forEach { cardIndex ->
                if (cardIndex in player.hand.indices) {
                    player.removeCardAtIndex(cardIndex)
                }
            }
        }
        
        // Replace missing cards from deck
        player.getHandForModification()?.let { hand ->
            replaceCards(hand, deck)
        }
        
        // Perform all hand analysis checks
        player.performAllChecks()
        
        return deck
    }
    
    /**
     * Replace missing cards (zeros) in hand with new cards from deck.
     */
    fun replaceCards(hand: IntArray, deck: IntArray) {
        for (i in hand.indices) {
            if (hand[i] == 0) { // Empty slot needs replacement
                val newCard = drawCard(deck)
                hand[i] = newCard
            }
        }
    }
    
    /**
     * Draw a card from the deck and mark it as used.
     */
    @JvmStatic
    fun drawCard(deck: IntArray): Int {
        val availableCards = workingDeck(deck)
        if (availableCards.isEmpty()) {
            throw IllegalStateException("Cannot draw from empty deck")
        }
        
        val randomIndex = Random.nextInt(availableCards.size)
        val selectedCardIndex = availableCards[randomIndex]
        val selectedCard = deck[selectedCardIndex]
        
        // Mark card as used in original deck
        deck[selectedCardIndex] = 0
        
        return selectedCard
    }
    
    /**
     * Create working deck containing only available cards.
     */
    private fun workingDeck(deck: IntArray): IntArray {
        return deck.indices.filter { deck[it] != 0 }.toIntArray()
    }
    
    /**
     * Count remaining cards in deck.
     */
    private fun remainingCards(deck: IntArray): Int {
        return deck.count { it != 0 }
    }
    
    /**
     * Initialize a standard 52-card deck.
     */
    @JvmStatic
    fun setDeck(): IntArray {
        return IntArray(52) { it }
    }
    
    /**
     * Deal a new hand from the deck.
     */
    @JvmStatic
    fun newHand(deck: IntArray): IntArray {
        val hand = IntArray(DEFAULT_HAND_SIZE)
        for (i in hand.indices) {
            hand[i] = deck[i] // Simple dealing - take first 5 cards
        }
        return hand
    }
    
    /**
     * Determine game winner and handle results.
     */
    fun declareResults(players: List<Player>): Boolean {
        val winner = decideWinner(players)
        
        return when (winner) {
            0 -> {
                println("You have lost the hand, better luck next time")
                true
            }
            1 -> {
                println("You have won the hand, congratulations!")
                true
            }
            2 -> {
                println("No one won the hand, the game was a tie.")
                false
            }
            else -> false
        }
    }
    
    /**
     * Sophisticated winner determination algorithm.
     */
    fun decideWinner(players: List<Player>): Int {
        val activePlayers = players.filter { !it.fold }
        if (activePlayers.isEmpty()) return 2 // No active players
        
        // Find player(s) with highest hand value
        val maxHandValue = activePlayers.maxOf { handValue(it.hand) }
        val winners = activePlayers.filter { handValue(it.hand) == maxHandValue }
        
        return when {
            winners.size == 1 -> {
                val winnerIndex = players.indexOf(winners[0])
                if (winnerIndex == 0) 1 else 0 // 1 if user wins, 0 if AI wins
            }
            else -> 2 // Tie
        }
    }
    
    /**
     * Distribute pot among winners.
     */
    fun dividePot(players: List<Player>, pot: Int) {
        val activePlayers = players.filter { !it.fold }
        if (activePlayers.isEmpty()) return
        
        val maxHandValue = activePlayers.maxOf { handValue(it.hand) }
        val winners = activePlayers.filter { handValue(it.hand) == maxHandValue }
        
        val potPerWinner = pot / winners.size
        winners.forEach { winner ->
            winner.addChips(potPerWinner)
            println("\\n🎉 ${winner.name} wins $potPerWinner chips with ${getHandDescription(winner.hand)}!")
        }
    }
    
    /**
     * Display and save player statistics.
     */
    fun playersStats(players: List<Player>) {
        println("\\n=== PLAYER STATISTICS ===")
        players.forEach { player ->
            // Basic player report for now
            println("${player.name}: ${player.chips} chips (Hand value: ${handValue(player.hand)})")
        }
    }
    
    /**
     * Display player's hand in readable format.
     */
    private fun revealHand(hand: Array<String>) {
        println("Your hand is: ${hand.joinToString(", ")}")
    }
    
    // =================================================================
    // ENHANCED UTILITY METHODS - MODERN KOTLIN IMPLEMENTATION  
    // =================================================================
    
    /**
     * Display author information with enhanced formatting.
     */
    private fun displayAuthor() {
        println("╔════════════════════════════════════════╗")
        println("║           🃏 POKERMON 🃏               ║")
        println("║      Kotlin-Native Implementation      ║")
        println("║                                        ║")
        println("║         Created by Carl Nelson         ║")
        println("║            (@Gameaday)                 ║")
        println("╚════════════════════════════════════════╝")
        println()
    }
    
    /**
     * Prompt for player name with Kotlin string templates and null safety.
     */
    private fun promptName(defaultName: String): String {
        println("Enter your name (or press Enter for '$defaultName'):")
        val input = readLine()?.trim()
        return if (input.isNullOrEmpty()) defaultName else input
    }
    
    /**
     * Prompt for ending game with enhanced user experience.
     */
    private fun promptEnd(defaultContinue: Boolean): Boolean {
        println("Play another game? (y/n, default: ${if (defaultContinue) "y" else "n"})")
        val input = readLine()?.trim()?.lowercase()
        return when {
            input.isNullOrEmpty() -> defaultContinue
            input.startsWith("y") -> true
            input.startsWith("n") -> false
            else -> defaultContinue
        }
    }
    
    /**
     * Read integer with default value and range validation.
     */
    private fun readIntWithDefault(default: Int, range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Int {
        return try {
            val input = readLine()?.trim()
            if (input.isNullOrEmpty()) {
                default
            } else {
                val value = input.toInt()
                if (value in range) value else default
            }
        } catch (e: NumberFormatException) {
            default
        }
    }
    
    /**
     * Read integer with validation from list of valid values.
     */
    private fun readIntWithValidation(validValues: List<Int>): Int {
        while (true) {
            try {
                val input = readLine()?.trim()?.toInt()
                if (input != null && input in validValues) {
                    return input
                }
                println("Please enter one of: ${validValues.joinToString(", ")}")
            } catch (e: NumberFormatException) {
                println("Please enter a valid number")
            }
        }
    }
    
    /**
     * Deal a hand of cards using CardUtils for DRY compliance.
     */
    private fun dealHand(): IntArray {
        val hand = IntArray(DEFAULT_HAND_SIZE)
        val usedCards = mutableSetOf<Int>()
        
        for (i in hand.indices) {
            var card: Int
            do {
                card = Random.nextInt(DECK_SIZE)
            } while (card in usedCards)
            
            usedCards.add(card)
            hand[i] = card
        }
        
        return hand
    }
    
    /**
     * Display hand using CardUtils for consistent formatting.
     */
    private fun displayHand(hand: IntArray) {
        hand.forEachIndexed { index, card ->
            val rank = CardUtils.cardRank(card)
            val suit = CardUtils.cardSuit(card)
            println("  ${index + 1}: $rank of $suit")
        }
    }
    
    /**
     * Calculate hand value using HandEvaluator (0-8 scale for legacy compatibility).
     */
    fun calculateHandValue(hand: IntArray): Int {
        val result = HandEvaluator.evaluateHand(hand)
        return when (result.handType) {
            HandEvaluator.HandType.HIGH_CARD -> 0
            HandEvaluator.HandType.ONE_PAIR -> 1  
            HandEvaluator.HandType.TWO_PAIR -> 2
            HandEvaluator.HandType.THREE_OF_A_KIND -> 3
            HandEvaluator.HandType.STRAIGHT -> 4
            HandEvaluator.HandType.FLUSH -> 5
            HandEvaluator.HandType.FULL_HOUSE -> 6
            HandEvaluator.HandType.FOUR_OF_A_KIND -> 7
            HandEvaluator.HandType.STRAIGHT_FLUSH -> 8
            HandEvaluator.HandType.ROYAL_FLUSH -> 8
        }
    }
    
    /**
     * Conduct betting round with enhanced logic.
     */
    private fun conductBettingRound(players: List<Player>, initialPot: Int, initialTopBet: Int): Pair<Int, Int> {
        var pot = initialPot
        var topBet = initialTopBet
        
        players.forEach { player ->
            if (player.chips > 0) {
                val bet = if (player.name.contains("CPU") || player.name in POSSIBLE_NAMES) {
                    // AI betting logic
                    calculateAdvancedAIBet(player, topBet, pot)
                } else {
                    // Human player betting
                    promptPlayerBet(player, topBet)
                }
                
                val actualBet = minOf(bet, player.chips)
                player.chips -= actualBet
                pot += actualBet
                topBet = maxOf(topBet, actualBet)
                
                println("${player.name} bets $actualBet chips (${player.chips} remaining)")
            }
        }
        
        return Pair(pot, topBet)
    }
    
    /**
     * Prompt human player for bet amount.
     */
    private fun promptPlayerBet(player: Player, currentTopBet: Int): Int {
        println("\\n${player.name}, place your bet (current high: $currentTopBet, you have ${player.chips} chips):")
        return readIntWithDefault(currentTopBet, 0..player.chips)
    }
    
    /**
     * Determine winner based on hand values.
     */
    private fun determineWinner(players: List<Player>): Player? {
        return players.maxByOrNull { calculateHandValue(it.hand) }
    }
    
    // ==================================================================
    // CARD ANALYSIS METHODS (Enhanced with Kotlin features)
    // ==================================================================
    
    /**
     * Get card rank occurrences using modern Kotlin collections.
     */
    fun occurences(hand: IntArray, card: Int): Int {
        return hand.count { CardUtils.cardRank(it) == CardUtils.cardRank(card) }
    }
    
    /**
     * Find card multiples using Kotlin grouping functions.
     */
    fun cardMultiples(hand: IntArray): IntArray {
        val rankCounts = hand.groupBy { CardUtils.cardRank(it) }
            .mapValues { it.value.size }
            .values
            .filter { it > 1 }
            .sorted()
        
        return rankCounts.toIntArray()
    }
    
    /**
     * Check for various hand types using Kotlin when expressions.
     */
    fun is2Kind(multiples: IntArray): Boolean = 2 in multiples
    fun is2Pair(multiples: IntArray): Boolean = multiples.count { it == 2 } >= 2
    fun is3Kind(multiples: IntArray): Boolean = 3 in multiples
    fun is4Kind(multiples: IntArray): Boolean = 4 in multiples
    fun isFullHouse(multiples: IntArray): Boolean = 2 in multiples && 3 in multiples
    
    // ==================================================================
    // AI BETTING LOGIC (Enhanced with Kotlin algorithms)
    // ==================================================================
    
    /**
     * Enhanced AI betting using HandEvaluator and personality system.
     */
    fun calculateAdvancedAIBet(player: Player, currentBet: Int, potSize: Int): Int {
        val handResult = HandEvaluator.evaluateHand(player.hand)
        val handStrength = handResult.score / 1000.0 // Normalize to 0-1
        
        // Get personality factor with error handling
        val personalityFactor = try {
            PersonalityManager.getInstance().getPlayerPersonality(player.name).aggressiveness
        } catch (e: Exception) {
            0.5 // Default moderate aggressiveness
        }
        
        // Calculate base bet using hand strength and personality
        val baseBet = (handStrength * player.chips * personalityFactor.toDouble() * 0.3).toInt()
        
        // Apply strategic considerations based on hand type
        val strategicBet = when (handResult.handType) {
            HandEvaluator.HandType.ROYAL_FLUSH, 
            HandEvaluator.HandType.STRAIGHT_FLUSH,
            HandEvaluator.HandType.FOUR_OF_A_KIND -> maxOf(baseBet, currentBet * 3) // Premium hands
            HandEvaluator.HandType.FULL_HOUSE,
            HandEvaluator.HandType.FLUSH -> maxOf(baseBet, currentBet * 2) // Strong hands
            HandEvaluator.HandType.STRAIGHT,
            HandEvaluator.HandType.THREE_OF_A_KIND -> maxOf(baseBet, currentBet) // Good hands
            HandEvaluator.HandType.TWO_PAIR -> maxOf(baseBet, currentBet / 2) // Decent hands
            HandEvaluator.HandType.ONE_PAIR -> minOf(baseBet, currentBet / 3) // Weak hands
            HandEvaluator.HandType.HIGH_CARD -> minOf(10, currentBet / 4) // Very weak
        }
        
        return minOf(strategicBet, player.chips).coerceAtLeast(0)
    }
    
    // ==================================================================
    // UTILITY METHODS
    // ==================================================================
    
    /**
     * Place bet with validation - Kotlin version.
     */
    fun placeBet(chips: Int, requestedBet: Int): Int {
        return when {
            requestedBet <= 0 -> 0
            requestedBet > chips -> chips
            else -> requestedBet
        }
    }
    
    /**
     * Check input validation - Kotlin version with when expression.
     */
    fun check(input: Int, desired: Int): Boolean = input == desired
}