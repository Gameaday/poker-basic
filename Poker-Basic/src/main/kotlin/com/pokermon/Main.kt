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
        
        // Create user player
        user = Player(playerName, startingChips)
        players[0] = user
        
        // Create AI players with personality system integration
        for (i in 1..opponentCount) {
            val aiName = POSSIBLE_NAMES.random()
            val aiPlayer = Player(aiName, startingChips)
            players[i] = aiPlayer
            
            when (i) {
                1 -> cpu1 = aiPlayer
                2 -> cpu2 = aiPlayer  
                3 -> cpu3 = aiPlayer
            }
        }
        
        println("\\n=== Game Started ===")
        println("Players: ${players.filterNotNull().joinToString { it.name }}")
        println("Starting chips: $startingChips each")
        
        // Main game loop with enhanced error handling
        try {
            while (shouldContinue && !shouldQuit) {
                // Deal new hands using CardUtils for DRY compliance
                players.filterNotNull().forEach { player ->
                    player.hand = dealHand()
                }
                
                // Display user's hand
                user?.let { player ->
                    println("\\nYour hand:")
                    displayHand(player.hand)
                    println("Hand value: ${calculateHandValue(player.hand)}")
                }
                
                // Betting round implementation
                val betResult = conductBettingRound(players.filterNotNull(), workingPot, topBet)
                workingPot = betResult.first
                topBet = betResult.second
                
                // Determine winner and distribute pot
                val winner = determineWinner(players.filterNotNull())
                winner?.let {
                    it.chips += workingPot
                    println("\\n🎉 ${it.name} wins with ${getHandDescription(it.hand)}!")
                    println("Pot won: $workingPot chips")
                }
                
                // Reset for next round
                workingPot = 0
                topBet = 0
                
                // Check if anyone is out of chips
                val playersStillIn = players.filterNotNull().filter { it.chips > 0 }
                if (playersStillIn.size <= 1) {
                    println("\\n🏆 Game Over! Final winner: ${playersStillIn.firstOrNull()?.name ?: "Nobody"}")
                    shouldQuit = true
                } else {
                    println("\\nContinue playing? (y/n)")
                    shouldContinue = readLine()?.lowercase()?.startsWith("y") ?: false
                }
            }
        } catch (e: Exception) {
            println("Game error: ${e.message}")
            e.printStackTrace()
        }
        
        println("\\nThanks for playing Pokermon!")
    }
    
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
     * Calculate hand value using enhanced Kotlin algorithms.
     */
    fun calculateHandValue(hand: IntArray): Int {
        // Use the same logic as original but with Kotlin enhancements
        val multiples = cardMultiples(hand)
        
        return when {
            isStraightFlush(hand) -> 8
            is4Kind(multiples) -> 7
            isFullHouse(multiples) -> 6
            isFlush(hand) -> 5
            isStraight(hand) -> 4
            is3Kind(multiples) -> 3
            is2Pair(multiples) -> 2
            is2Kind(multiples) -> 1
            else -> 0
        }
    }
    
    /**
     * Get hand description for display.
     */
    private fun getHandDescription(hand: IntArray): String {
        return when (calculateHandValue(hand)) {
            8 -> "Straight Flush"
            7 -> "Four of a Kind"
            6 -> "Full House"
            5 -> "Flush"
            4 -> "Straight"
            3 -> "Three of a Kind"
            2 -> "Two Pair"
            1 -> "One Pair"
            else -> "High Card"
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
    
    /**
     * Check for flush using Kotlin collection operations.
     */
    fun isFlush(hand: IntArray): Boolean {
        val suits = hand.map { CardUtils.cardSuit(it) }.distinct()
        return suits.size == 1
    }
    
    /**
     * Check for straight using Kotlin range operations.
     */
    fun isStraight(hand: IntArray): Boolean {
        val ranks = hand.map { CardUtils.cardRank(it) }.sorted()
        return ranks.zipWithNext().all { (a, b) -> b - a == 1 }
    }
    
    /**
     * Check for straight flush combining both checks.
     */
    fun isStraightFlush(hand: IntArray): Boolean = isFlush(hand) && isStraight(hand)
    
    // ==================================================================
    // AI BETTING LOGIC (Enhanced with Kotlin algorithms)
    // ==================================================================
    
    /**
     * Calculate AI bet using personality system integration.
     */
    fun calculateAdvancedAIBet(player: Player, currentBet: Int, potSize: Int): Int {
        val handValue = calculateHandValue(player.hand)
        val handStrength = handValue / 8.0 // Normalize to 0-1
        
        // Base bet calculation with personality influence
        val personalityFactor = PersonalityManager.getPersonalityAggression(player.name)
        val baseBet = (handStrength * player.chips * personalityFactor).toInt()
        
        // Apply strategic considerations
        val strategicBet = when {
            handValue >= 6 -> maxOf(baseBet, currentBet * 2) // Strong hand - aggressive
            handValue >= 3 -> maxOf(baseBet, currentBet) // Medium hand - match bet
            handValue >= 1 -> minOf(baseBet, currentBet / 2) // Weak hand - conservative
            else -> minOf(10, currentBet / 4) // Very weak - minimal bet
        }
        
        return minOf(strategicBet, player.chips)
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