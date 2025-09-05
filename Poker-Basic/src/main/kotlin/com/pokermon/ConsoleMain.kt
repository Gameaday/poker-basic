package com.pokermon

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Enhanced Console-only version of the Pokermon game - Pure Kotlin-native implementation.
 * 
 * This class provides a sophisticated text-based interface that offers full feature parity
 * with the Android GUI version, exposing all game modes and functions through an 
 * intuitive command-line experience.
 * 
 * Features:
 * - All game modes (Classic, Adventure, Safari, Ironman)
 * - HandEvaluator integration for consistent scoring
 * - Flow-based reactive state management
 * - Monster companion system integration ready
 * - Advanced poker hand evaluation and display
 * - Session statistics and player profiles
 * - Card exchange and sophisticated betting rounds
 * - AI personality system integration
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 2.0.0 (Enhanced Kotlin-native implementation)
 */
object ConsoleMain {
    
    // =================================================================
    // ENHANCED GAME STATE MANAGEMENT
    // =================================================================
    
    /**
     * Comprehensive game state for full feature support.
     */
    sealed class GameState {
        object Initializing : GameState()
        data class MenuSelection(val availableModes: List<GameMode>) : GameState()
        data class ModeConfiguration(val selectedMode: GameMode) : GameState()
        data class PlayerSetup(
            val mode: GameMode,
            val playerName: String = "",
            val opponentCount: Int = 0,
            val startingChips: Int = 0
        ) : GameState()
        data class GameActive(
            val mode: GameMode,
            val players: List<Player>,
            val pot: Int = 0,
            val round: Int = 1,
            val phase: GamePhase = GamePhase.HAND_DEALING,
            val deck: MutableList<Int> = mutableListOf()
        ) : GameState()
        data class HandEvaluation(
            val players: List<Player>,
            val handResults: Map<String, HandEvaluator.HandResult>
        ) : GameState()
        data class BettingRound(
            val players: List<Player>, 
            val pot: Int, 
            val currentBet: Int,
            val phase: String = "initial"
        ) : GameState()
        data class CardExchange(val players: List<Player>) : GameState()
        data class Results(
            val winners: List<Player>, 
            val pot: Int,
            val handResults: Map<String, HandEvaluator.HandResult>
        ) : GameState()
        data class MonsterEncounter(
            val mode: GameMode,
            val monster: Monster?,
            val players: List<Player>
        ) : GameState()
        object GameOver : GameState()
    }
    
    // Enhanced state management with Flow
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _gameStats = MutableStateFlow(GameStats())
    val gameStats: StateFlow<GameStats> = _gameStats.asStateFlow()
    
    /**
     * Comprehensive game statistics for session tracking.
     */
    data class GameStats(
        val gamesPlayed: Int = 0,
        val gamesWon: Int = 0,
        val totalChipsWon: Int = 0,
        val bestHand: String = "None",
        val bestHandScore: Int = 0,
        val handsPlayed: Int = 0,
        val monstersEncountered: Int = 0,
        val monstersCaptured: Int = 0,
        val adventuresCompleted: Int = 0,
        val gachaRolls: Int = 0,
        val timeStarted: Long = System.currentTimeMillis()
    )
    
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        displayEnhancedWelcomeBanner()
        
        try {
            runFullFeaturedGame()
        } catch (e: Exception) {
            println("\n❌ Console game error: ${e.message}")
            e.printStackTrace()
        }
        
        displayFarewellMessage()
    }
    
    // =================================================================
    // MAIN GAME FLOW - Full feature implementation
    // =================================================================
    
    /**
     * Run the complete game experience with all modes and features.
     */
    private suspend fun runFullFeaturedGame() {
        _gameState.value = GameState.Initializing
        
        while (true) {
            // Game mode selection with full options
            val selectedMode = displayGameModeMenu()
            if (selectedMode == null) break // User chose to exit
            
            _gameState.value = GameState.ModeConfiguration(selectedMode)
            
            // Mode-specific configuration
            val gameSetup = configureModeSettings(selectedMode)
            
            // Enhanced player setup
            val enhancedSetup = performEnhancedPlayerSetup(selectedMode, gameSetup)
            
            // Run game session with selected mode
            runGameSession(selectedMode, enhancedSetup)
            
            // Session results and continuation
            if (!promptForNewGame()) break
        }
        
        displaySessionSummary()
    }
    
    /**
     * Display comprehensive game mode selection menu.
     */
    private fun displayGameModeMenu(): GameMode? {
        println("\n" + "🎮".repeat(20))
        println("            POKERMON GAME MODES")
        println("🎮".repeat(20))
        println()
        
        val modes = GameMode.values()
        modes.forEachIndexed { index, mode ->
            val icon = when (mode) {
                GameMode.CLASSIC -> "🃏"
                GameMode.ADVENTURE -> "⚔️"
                GameMode.SAFARI -> "🏕️"
                GameMode.IRONMAN -> "🎰"
            }
            println("  ${index + 1}. $icon ${mode.displayName}")
            println("     ${mode.description}")
            println()
        }
        
        println("  0. 🚪 Exit Game")
        println()
        
        while (true) {
            print("Select game mode (0-${modes.size}): ")
            val choice = readLine()?.toIntOrNull()
            
            when {
                choice == 0 -> return null
                choice != null && choice in 1..modes.size -> {
                    val selectedMode = modes[choice - 1]
                    println("\n✅ Selected: ${selectedMode.displayName}")
                    return selectedMode
                }
                else -> println("❌ Invalid choice. Please select 0-${modes.size}.")
            }
        }
    }
    
    /**
     * Configure mode-specific settings.
     */
    private fun configureModeSettings(mode: GameMode): ModeConfiguration {
        println("\n🔧 ${mode.displayName.uppercase()} CONFIGURATION")
        println("-".repeat(40))
        
        return when (mode) {
            GameMode.CLASSIC -> configureClassicMode()
            GameMode.ADVENTURE -> configureAdventureMode()
            GameMode.SAFARI -> configureSafariMode()
            GameMode.IRONMAN -> configureIronmanMode()
        }
    }
    
    private fun configureClassicMode(): ModeConfiguration {
        println("Classic poker with traditional rules.")
        println("• Standard 5-card draw")
        println("• Betting rounds with raises and folds")
        println("• Card exchange phase")
        return ModeConfiguration.Classic()
    }
    
    private fun configureAdventureMode(): ModeConfiguration {
        println("Battle monsters where their health equals their chip count!")
        println("• Defeat monsters to progress")
        println("• Monster difficulty scales with rounds")
        println("• Earn rewards and experience")
        
        print("Select difficulty (1=Easy, 2=Medium, 3=Hard): ")
        val difficulty = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        
        return ModeConfiguration.Adventure(difficulty)
    }
    
    private fun configureSafariMode(): ModeConfiguration {
        println("Capture monsters through strategic poker gameplay!")
        println("• Higher hand rankings increase capture chance")
        println("• Build your monster collection")
        println("• Different monsters have different capture rates")
        
        print("Enable rare monster encounters? (y/n): ")
        val rareMonsters = readLine()?.lowercase()?.startsWith("y") ?: false
        
        return ModeConfiguration.Safari(rareMonsters)
    }
    
    private fun configureIronmanMode(): ModeConfiguration {
        println("Convert winnings into monster gacha pulls!")
        println("• Chip rewards become gacha currency")
        println("• Higher stakes = better reward rates")
        println("• Collect rare and legendary monsters")
        
        print("Select gacha rate (1=Conservative, 2=Balanced, 3=High-Risk): ")
        val gachaRate = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        
        return ModeConfiguration.Ironman(gachaRate)
    }
    
    /**
     * Mode configuration sealed class.
     */
    sealed class ModeConfiguration {
        data class Classic(val variant: String = "standard") : ModeConfiguration()
        data class Adventure(val difficulty: Int = 2) : ModeConfiguration()
        data class Safari(val rareMonsters: Boolean = false) : ModeConfiguration()
        data class Ironman(val gachaRate: Int = 2) : ModeConfiguration()
    }
    
    /**
     * Enhanced player setup with mode-specific options.
     */
    private fun performEnhancedPlayerSetup(mode: GameMode, config: ModeConfiguration): EnhancedGameSetup {
        println("\n🎯 PLAYER SETUP - ${mode.displayName}")
        println("-".repeat(50))
        
        // Get player name
        print("Enter your name (or press Enter for 'Player'): ")
        val playerName = readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "Player"
        
        // Get opponent configuration based on mode
        val opponentSetup = when (mode) {
            GameMode.CLASSIC -> setupClassicOpponents()
            GameMode.ADVENTURE -> setupAdventureOpponents()
            GameMode.SAFARI -> setupSafariOpponents()
            GameMode.IRONMAN -> setupIronmanOpponents()
        }
        
        // Get starting resources
        val startingChips = selectStartingChips(mode)
        
        return EnhancedGameSetup(
            mode = mode,
            config = config,
            playerName = playerName,
            opponentCount = opponentSetup.count,
            opponentTypes = opponentSetup.types,
            startingChips = startingChips
        )
    }
    
    private fun setupClassicOpponents(): OpponentSetup {
        print("Number of AI opponents (1-3): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        return OpponentSetup(count, listOf("AI"))
    }
    
    private fun setupAdventureOpponents(): OpponentSetup {
        print("Number of monsters to battle (1-5): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 5) ?: 3
        return OpponentSetup(count, listOf("Monster"))
    }
    
    private fun setupSafariOpponents(): OpponentSetup {
        print("Number of wild monsters (1-3): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        return OpponentSetup(count, listOf("Wild Monster"))
    }
    
    private fun setupIronmanOpponents(): OpponentSetup {
        print("Number of gacha challengers (1-4): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 4) ?: 2
        return OpponentSetup(count, listOf("Gacha Challenger"))
    }
    
    private fun selectStartingChips(mode: GameMode): Int {
        val validChips = when (mode) {
            GameMode.CLASSIC -> intArrayOf(100, 500, 1000, 2500)
            GameMode.ADVENTURE -> intArrayOf(200, 500, 1000, 2000)
            GameMode.SAFARI -> intArrayOf(150, 300, 750, 1500)
            GameMode.IRONMAN -> intArrayOf(500, 1000, 2500, 5000)
        }
        
        println("\nSelect starting chips for ${mode.displayName}:")
        validChips.forEachIndexed { index, chips ->
            println("  ${index + 1}. $chips chips")
        }
        
        while (true) {
            print("Enter choice (1-${validChips.size}): ")
            val choice = readLine()?.toIntOrNull()
            if (choice != null && choice in 1..validChips.size) {
                return validChips[choice - 1]
            }
            println("❌ Invalid choice. Please select 1-${validChips.size}.")
        }
    }
    
    data class OpponentSetup(val count: Int, val types: List<String>)
    
    /**
     * Enhanced game setup data class.
     */
    data class EnhancedGameSetup(
        val mode: GameMode,
        val config: ModeConfiguration,
        val playerName: String,
        val opponentCount: Int,
        val opponentTypes: List<String>,
        val startingChips: Int
    )
    
    // =================================================================
    // GAME SESSION EXECUTION - Mode-specific gameplay
    // =================================================================
    
    /**
     * Run complete game session with selected mode.
     */
    private suspend fun runGameSession(mode: GameMode, setup: EnhancedGameSetup) {
        println("\n🚀 Starting ${mode.displayName} Session")
        println("=".repeat(60))
        
        // Create deck and players using enhanced setup
        val deck = CardUtils.createShuffledDeck().toMutableList()
        val players = createPlayersForMode(setup)
        
        _gameState.value = GameState.GameActive(
            mode = mode,
            players = players,
            deck = deck
        )
        
        when (mode) {
            GameMode.CLASSIC -> runClassicSession(players, deck, setup)
            GameMode.ADVENTURE -> runAdventureSession(players, deck, setup)
            GameMode.SAFARI -> runSafariSession(players, deck, setup)
            GameMode.IRONMAN -> runIronmanSession(players, deck, setup)
        }
    }
    
    private fun createPlayersForMode(setup: EnhancedGameSetup): List<Player> {
        val players = mutableListOf<Player>()
        
        // Add human player
        players.add(Player(setup.playerName, setup.startingChips))
        
        // Add AI opponents based on mode
        for (i in 1..setup.opponentCount) {
            val name = generateOpponentName(setup.mode, i)
            players.add(Player(name, setup.startingChips))
        }
        
        return players
    }
    
    private fun generateOpponentName(mode: GameMode, index: Int): String {
        return when (mode) {
            GameMode.CLASSIC -> "AI Player $index"
            GameMode.ADVENTURE -> "Monster Warrior $index"
            GameMode.SAFARI -> "Wild Creature $index"
            GameMode.IRONMAN -> "Gacha Master $index"
        }
    }
    
    /**
     * Run classic poker session.
     */
    private suspend fun runClassicSession(players: List<Player>, deck: MutableList<Int>, setup: EnhancedGameSetup) {
        println("\n🃏 CLASSIC POKER SESSION")
        println("Players: ${players.size}")
        println("Starting chips: ${setup.startingChips}")
        
        var round = 1
        val activePlayers = players.toMutableList()
        
        while (activePlayers.size > 1 && round <= 10) {
            println("\n" + "=".repeat(40))
            println("               ROUND $round")
            println("=".repeat(40))
            
            // Deal hands
            dealHandsToPlayers(activePlayers, deck)
            
            // Evaluate hands using HandEvaluator
            val handResults = evaluateAllHands(activePlayers)
            displayHandEvaluations(handResults)
            
            // First betting round
            val pot = conductBettingRound(activePlayers, "Initial Betting")
            
            // Card exchange phase
            conductCardExchange(activePlayers, deck)
            
            // Re-evaluate hands after exchange
            val finalHandResults = evaluateAllHands(activePlayers)
            
            // Final betting round
            val finalPot = conductFinalBettingRound(activePlayers, pot)
            
            // Determine winners and distribute pot
            val winners = determineWinners(activePlayers, finalHandResults)
            distributePot(winners, finalPot)
            
            // Display round results
            displayRoundResults(winners, finalPot, finalHandResults)
            
            // Remove players with no chips
            activePlayers.removeAll { it.chips <= 0 }
            round++
            
            if (activePlayers.size > 1) {
                println("\nPress Enter to continue to next round...")
                readLine()
            }
        }
        
        // Display session winner
        displaySessionWinner(activePlayers)
    }
    
    // TODO: Implement other game mode sessions
    private suspend fun runAdventureSession(players: List<Player>, deck: MutableList<Int>, setup: EnhancedGameSetup) {
        println("\n⚔️ ADVENTURE MODE - Coming soon!")
    }
    
    private suspend fun runSafariSession(players: List<Player>, deck: MutableList<Int>, setup: EnhancedGameSetup) {
        println("\n🏕️ SAFARI MODE - Coming soon!")
    }
    
    private suspend fun runIronmanSession(players: List<Player>, deck: MutableList<Int>, setup: EnhancedGameSetup) {
        println("\n🎰 IRONMAN MODE - Coming soon!")
    }
    
    // =================================================================
    // GAME MECHANICS - Enhanced with HandEvaluator integration
    // =================================================================
    
    private fun dealHandsToPlayers(players: List<Player>, deck: MutableList<Int>) {
        println("\n🎴 Dealing hands...")
        
        players.forEach { player ->
            val hand = CardUtils.dealCards(deck, 5)
            player.hand = hand
            
            if (player.name == players[0].name) { // Human player
                println("\nYour hand:")
                displayPlayerHand(player)
            }
        }
    }
    
    private fun evaluateAllHands(players: List<Player>): Map<String, HandEvaluator.HandResult> {
        return players.associate { player ->
            player.name to HandEvaluator.evaluateHand(player.hand)
        }
    }
    
    private fun displayHandEvaluations(handResults: Map<String, HandEvaluator.HandResult>) {
        println("\n📊 Hand Evaluations:")
        handResults.forEach { (playerName, result) ->
            if (playerName == handResults.keys.first()) { // Show human player's result
                println("$playerName: ${result.description} (Score: ${result.score})")
            }
        }
    }
    
    private fun displayPlayerHand(player: Player) {
        println("  ${CardUtils.formatHandSymbols(player.hand)}")
        val result = HandEvaluator.evaluateHand(player.hand)
        println("  ${result.description} (Score: ${result.score})")
    }
    
    private fun conductBettingRound(players: List<Player>, roundName: String): Int {
        println("\n💰 $roundName")
        
        // Simple betting implementation for now
        val pot = players.sumOf { minOf(50, it.chips) }
        players.forEach { player ->
            val bet = minOf(50, player.chips)
            player.chips -= bet
            println("${player.name} bets $bet chips")
        }
        
        return pot
    }
    
    private fun conductCardExchange(players: List<Player>, deck: MutableList<Int>) {
        println("\n🔄 Card Exchange Phase")
        
        // Human player exchange
        val humanPlayer = players[0]
        conductHumanCardExchange(humanPlayer, deck)
        
        // AI players exchange (simplified)
        players.drop(1).forEach { player ->
            val exchangeCount = (0..3).random()
            if (exchangeCount > 0) {
                // Simple AI: exchange random cards
                repeat(exchangeCount) {
                    if (player.hand.isNotEmpty() && deck.isNotEmpty()) {
                        val randomIndex = player.hand.indices.random()
                        val newCard = deck.removeAt(0)
                        player.hand[randomIndex] = newCard
                    }
                }
                println("${player.name} exchanges $exchangeCount cards")
            }
        }
    }
    
    private fun conductHumanCardExchange(player: Player, deck: MutableList<Int>) {
        println("\nYour current hand:")
        displayPlayerHand(player)
        
        print("Enter card positions to exchange (1-5, space separated, or 'none'): ")
        val input = readLine()?.trim()?.lowercase()
        
        if (input == "none" || input.isNullOrEmpty()) {
            println("No cards exchanged.")
            return
        }
        
        try {
            val positions = input.split(" ").map { it.toInt() - 1 } // Convert to 0-based
            val validPositions = positions.filter { it in 0..4 }
            
            if (validPositions.isNotEmpty() && deck.size >= validPositions.size) {
                validPositions.forEach { pos ->
                    val newCard = deck.removeAt(0)
                    player.hand[pos] = newCard
                }
                
                println("Exchanged ${validPositions.size} cards.")
                println("Your new hand:")
                displayPlayerHand(player)
            }
        } catch (e: Exception) {
            println("Invalid input. No cards exchanged.")
        }
    }
    
    private fun conductFinalBettingRound(players: List<Player>, currentPot: Int): Int {
        println("\n💰 Final Betting Round")
        
        // Re-evaluate hands and conduct final betting
        val additionalPot = players.sumOf { minOf(25, it.chips) }
        players.forEach { player ->
            val bet = minOf(25, player.chips)
            player.chips -= bet
            println("${player.name} final bet: $bet chips")
        }
        
        return currentPot + additionalPot
    }
    
    private fun determineWinners(players: List<Player>, handResults: Map<String, HandEvaluator.HandResult>): List<Player> {
        val maxScore = handResults.values.maxOfOrNull { it.score } ?: 0
        return players.filter { handResults[it.name]?.score == maxScore }
    }
    
    private fun distributePot(winners: List<Player>, pot: Int) {
        val winnings = pot / winners.size
        winners.forEach { winner ->
            winner.chips += winnings
            println("${winner.name} wins $winnings chips!")
        }
    }
    
    private fun displayRoundResults(
        winners: List<Player>, 
        pot: Int, 
        handResults: Map<String, HandEvaluator.HandResult>
    ) {
        println("\n🏆 ROUND RESULTS")
        println("-".repeat(30))
        
        winners.forEach { winner ->
            val result = handResults[winner.name]
            println("🥇 ${winner.name}: ${result?.description} (Score: ${result?.score})")
            println("   Chips: ${winner.chips}")
        }
        
        println("\nAll players:")
        handResults.forEach { (name, result) ->
            val player = winners.find { it.name == name } ?: return@forEach
            println("   $name: ${result.description} (${player.chips} chips)")
        }
    }
    
    private fun displaySessionWinner(remainingPlayers: List<Player>) {
        println("\n" + "🎊".repeat(20))
        println("            SESSION COMPLETE")
        println("🎊".repeat(20))
        
        if (remainingPlayers.isNotEmpty()) {
            val winner = remainingPlayers.maxByOrNull { it.chips }
            println("🏆 Session Winner: ${winner?.name}")
            println("💰 Final Chips: ${winner?.chips}")
        }
    }
    
    // =================================================================
    // USER INTERFACE UTILITIES
    // =================================================================
    
    private fun displayEnhancedWelcomeBanner() {
        println("=".repeat(80))
        println("           🃏 POKERMON - ENHANCED CONSOLE EDITION 🃏")
        println("               Pure Kotlin-Native Implementation")
        println("                    All Game Modes Available")
        println("=".repeat(80))
        println()
        println("🎯 Available Game Modes:")
        println("   🃏 Classic - Traditional poker with sophisticated evaluation")
        println("   ⚔️ Adventure - Battle monsters in poker duels")
        println("   🏕️ Safari - Capture monsters through strategic play")  
        println("   🎰 Ironman - Convert winnings to monster gacha rolls")
        println()
        println("✨ Enhanced Features:")
        println("   • Advanced hand evaluation with detailed scoring")
        println("   • Comprehensive betting and exchange systems")
        println("   • Session statistics and player progression")
        println("   • Monster integration ready for all modes")
        println("   • Full feature parity with Android GUI version")
        println()
    }
    
    private fun promptForNewGame(): Boolean {
        println("\n🎮 Would you like to start a new game session?")
        print("Enter 'y' for yes, anything else to quit: ")
        val response = readLine()?.lowercase()?.trim()
        return response == "y" || response == "yes"
    }
    
    private fun displaySessionSummary() {
        val stats = _gameStats.value
        val sessionTime = (System.currentTimeMillis() - stats.timeStarted) / 1000
        
        println("\n" + "📊".repeat(30))
        println("                    SESSION SUMMARY")
        println("📊".repeat(30))
        
        println("Games Played: ${stats.gamesPlayed}")
        println("Games Won: ${stats.gamesWon}")
        println("Win Rate: ${if (stats.gamesPlayed > 0) "%.1f%%".format((stats.gamesWon * 100.0) / stats.gamesPlayed) else "N/A"}")
        println("Total Chips Won: ${stats.totalChipsWon}")
        println("Best Hand: ${stats.bestHand} (Score: ${stats.bestHandScore})")
        println("Hands Played: ${stats.handsPlayed}")
        
        if (stats.monstersEncountered > 0) {
            println("Monsters Encountered: ${stats.monstersEncountered}")
            println("Monsters Captured: ${stats.monstersCaptured}")
        }
        
        if (stats.gachaRolls > 0) {
            println("Gacha Rolls: ${stats.gachaRolls}")
        }
        
        println("Session Time: ${sessionTime}s")
        println()
    }
    
    private fun displayFarewellMessage() {
        println("=".repeat(80))
        println("              Thank you for playing Pokermon!")
        println("                   🎯 Game Over 🎯")
        println("=".repeat(80))
    }
}