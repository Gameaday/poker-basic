package com.pokermon.console

import com.pokermon.*
import com.pokermon.GameFlows.*
import com.pokermon.players.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Console game flow controller that manages the complete poker game experience.
 * Integrates with GameStateManager for reactive state management.
 *
 * @author Pokermon Console System
 * @version 1.1.0
 */
class ConsoleGame {
    private val stateManager = GameStateManager()
    private val scanner = Scanner(System.`in`)

    /**
     * Starts the main console game loop
     */
    fun start() =
        runBlocking {
            ConsoleUI.clearScreen()

            while (true) {
                try {
                    ConsoleUI.displayMainMenu()
                    val choice = scanner.nextLine().trim()

                    when (choice) {
                        "1" -> startGame(GameMode.CLASSIC)
                        "2" -> startGame(GameMode.ADVENTURE)
                        "3" -> startGame(GameMode.SAFARI)
                        "4" -> startGame(GameMode.IRONMAN)
                        "5" -> showSettings()
                        "6" -> {
                            println("Thanks for playing Pokermon! 🃏")
                            break
                        }
                        else -> {
                            println("Invalid choice. Please select 1-6.")
                            ConsoleUI.waitForContinue()
                        }
                    }
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}")
                    ConsoleUI.waitForContinue()
                }
            }
        }

    /**
     * Starts a game with the specified mode
     */
    private suspend fun startGame(gameMode: GameMode) {
        ConsoleUI.clearScreen()

        // Configure game
        val config = ConsoleUI.configureGameMode(gameMode)

        // Get player name
        print("\nEnter your name: ")
        val playerName = scanner.nextLine().trim().ifEmpty { "Player" }

        // Handle mode-specific gameplay
        when (gameMode) {
            GameMode.SAFARI -> {
                startSafariMode(playerName, config["startingChips"] as Int)
            }
            GameMode.IRONMAN -> {
                startIronmanMode(playerName, config["startingChips"] as Int, config["difficulty"] as? Int ?: 2)
            }
            GameMode.ADVENTURE -> {
                startAdventureMode(playerName, config["startingChips"] as Int)
            }
            GameMode.CLASSIC -> {
                startClassicMode(playerName, config)
            }
        }
    }

    /**
     * Starts Safari Mode gameplay
     */
    private suspend fun startSafariMode(
        playerName: String,
        startingChips: Int,
    ) {
        val safariBalls = 30
        println("\n🏕️ Starting Safari Mode!")
        println("You have $safariBalls safari balls to capture monsters.")
        println()

        // Use the Safari mode implementation
        val safariMode = com.pokermon.modes.safari.SafariGameMode(playerName, startingChips, safariBalls)
        safariMode.startSafari()
    }

    /**
     * Starts Ironman Mode gameplay
     */
    private suspend fun startIronmanMode(
        playerName: String,
        startingChips: Int,
        difficulty: Int,
    ) {
        println("\n⚡ Starting Ironman Mode!")
        println("High stakes survival with gacha rewards!")
        println()

        // Use the Ironman mode implementation
        val ironmanMode = com.pokermon.modes.ironman.IronmanGameMode(playerName, startingChips, difficulty)
        ironmanMode.startIronman()
    }

    /**
     * Starts Adventure Mode gameplay
     */
    private fun startAdventureMode(
        playerName: String,
        startingChips: Int,
    ) {
        println("\n⚔️ Starting Adventure Mode!")
        println("Battle monsters and explore the world!")
        println()

        // Use the Adventure mode implementation
        val adventureMode = com.pokermon.modes.adventure.AdventureMode(playerName, startingChips)
        adventureMode.startAdventure()
    }

    /**
     * Starts Classic Mode gameplay
     */
    private suspend fun startClassicMode(
        playerName: String,
        config: Map<String, Any>,
    ) {
        // Create players
        val humanPlayer = Player(playerName, config["startingChips"] as Int)
        val players = mutableListOf(humanPlayer)

        // Add AI players
        val aiCount = config["aiOpponents"] as Int
        repeat(aiCount) { index ->
            players.add(Player("AI-${index + 1}", config["startingChips"] as Int, isAI = true))
        }

        // Start game state management
        stateManager.processAction(GameActions.StartGame)
        stateManager.updateGameState(
            GameState.Playing(
                players = players,
                currentPhase = GamePhase.BETTING_ROUND,
            ),
        )

        // Run game loop
        runGameLoop(players, GameMode.CLASSIC)
    }

    /**
     * Main game loop that handles rounds and player actions
     */
    private suspend fun runGameLoop(
        players: List<Player>,
        gameMode: GameMode,
    ) {
        var roundNumber = 1

        while (players.count { !it.fold && it.chips > 0 } > 1) {
            println("\n" + "=".repeat(50))
            println("                 ROUND $roundNumber")
            println("=".repeat(50))

            // Deal new hand
            dealCards(players)
            stateManager.emitEvent(GameEvents.CardsDealt(players.size))

            // Ante up
            val ante = 10
            players.forEach { player ->
                if (player.chips >= ante) {
                    player.setBet(ante)
                }
            }

            // Betting round 1
            val pot1 = bettingRound(players, "Initial Betting")

            // Card exchange
            cardExchangePhase(players)

            // Betting round 2
            val pot2 = bettingRound(players, "Final Betting")

            // Showdown
            val winner = showdown(players)
            val totalPot = pot1 + pot2

            if (winner != null) {
                winner.chips += totalPot
                println("\n🏆 ${winner.name} wins the round with $totalPot chips!")
                ConsoleUI.displayHand(winner)
            }

            // Reset for next round
            players.forEach { it.resetForNewRound() }
            roundNumber++

            ConsoleUI.waitForContinue()
        }

        // Game over
        val finalWinner = players.maxByOrNull { it.chips }
        val finalScores = players.associate { it.name to it.chips }

        stateManager.updateGameState(
            GameState.GameOver(
                winner = finalWinner,
                finalScores = finalScores,
                sessionStats =
                    mapOf(
                        "rounds" to roundNumber - 1,
                        "gameMode" to gameMode.displayName,
                    ),
            ),
        )

        ConsoleUI.displayGameOver(finalWinner, finalScores)
        ConsoleUI.waitForContinue()
    }

    /**
     * Deals cards to all active players
     */
    private fun dealCards(players: List<Player>) {
        players.forEach { player ->
            if (!player.fold) {
                val newHand = IntArray(5) { (1..52).random() }
                player.hand = newHand
            }
        }
    }

    /**
     * Handles a betting round
     */
    private suspend fun bettingRound(
        players: List<Player>,
        roundName: String,
    ): Int {
        println("\n🎰 $roundName Round")
        println("-".repeat(30))

        var pot = 0
        var currentBet = 0
        val playersBet = mutableMapOf<Player, Int>()

        players.forEach { playersBet[it] = 0 }

        var activePlayerIndex = 0
        var passCount = 0

        while (passCount < players.count { !it.fold }) {
            val player = players[activePlayerIndex]

            if (!player.fold && player.chips > 0) {
                ConsoleUI.displayPlayerStatus(players, activePlayerIndex)
                ConsoleUI.displayPotInfo(pot, currentBet, 10)

                if (player.isAI) {
                    // AI decision making
                    val action = makeAIDecision(player, currentBet, pot)
                    handlePlayerAction(player, action, currentBet, playersBet)
                    delay(1000) // Brief pause for readability
                } else {
                    // Human player
                    ConsoleUI.displayHand(player)
                    val actionInput = ConsoleUI.getBettingAction(player, currentBet, 10)
                    val action = parsePlayerAction(actionInput, player, currentBet)
                    handlePlayerAction(player, action, currentBet, playersBet)
                }

                // Update pot and bet tracking
                val playerTotalBet = playersBet[player] ?: 0
                pot += player.bet - playerTotalBet
                playersBet[player] = player.bet

                if (player.bet > currentBet) {
                    currentBet = player.bet
                    passCount = 0 // Reset pass count when bet is raised
                } else {
                    passCount++
                }
            } else {
                passCount++
            }

            activePlayerIndex = (activePlayerIndex + 1) % players.size
        }

        return pot
    }

    /**
     * Handles the card exchange phase
     */
    private suspend fun cardExchangePhase(players: List<Player>) {
        println("\n🔄 Card Exchange Phase")
        println("-".repeat(25))

        players.forEach { player ->
            if (!player.fold) {
                if (player.isAI) {
                    // AI card exchange logic
                    val cardsToExchange = (0..2).random() // Simple AI: exchange 0-2 cards
                    if (cardsToExchange > 0) {
                        repeat(cardsToExchange) {
                            if (player.hand.isNotEmpty()) {
                                val indexToReplace = player.hand.indices.random()
                                player.hand[indexToReplace] = (1..52).random()
                            }
                        }
                        println("${player.name} exchanged $cardsToExchange cards")
                        stateManager.emitEvent(GameEvents.CardsExchanged(player, cardsToExchange))
                    }
                } else {
                    // Human player exchange
                    val cardsToExchange = ConsoleUI.getCardsToExchange(player)
                    cardsToExchange.forEach { index ->
                        if (index in player.hand.indices) {
                            player.hand[index] = (1..52).random()
                        }
                    }
                    if (cardsToExchange.isNotEmpty()) {
                        println("Exchanged ${cardsToExchange.size} cards")
                        stateManager.emitEvent(GameEvents.CardsExchanged(player, cardsToExchange.size))
                    }
                }
            }
        }
    }

    /**
     * Determines the winner in showdown
     */
    private fun showdown(players: List<Player>): Player? {
        val activePlayers = players.filter { !it.fold }
        if (activePlayers.isEmpty()) return null
        if (activePlayers.size == 1) return activePlayers.first()

        println("\n🔥 Showdown!")
        println("-".repeat(15))

        var bestPlayer: Player? = null
        var bestScore = 0

        activePlayers.forEach { player ->
            val handResult = HandEvaluator.evaluateHand(player.hand)
            println("${player.name}: ${handResult.handType} (Score: ${handResult.score})")

            if (handResult.score > bestScore) {
                bestScore = handResult.score
                bestPlayer = player
            }
        }

        return bestPlayer
    }

    /**
     * Makes AI decision based on hand strength
     */
    private fun makeAIDecision(
        player: Player,
        currentBet: Int,
        pot: Int,
    ): String {
        val handResult = HandEvaluator.evaluateHand(player.hand)
        val handStrength = handResult.score / 999.0 // Normalize to 0-1

        return when {
            handStrength > 0.7 -> "raise"
            handStrength > 0.4 && currentBet <= player.chips * 0.1 -> "call"
            handStrength > 0.2 && currentBet == 0 -> "check"
            else -> "fold"
        }
    }

    /**
     * Parses human player action input
     */
    private fun parsePlayerAction(
        input: String,
        player: Player,
        currentBet: Int,
    ): String {
        return when (input.lowercase()) {
            "1" -> if (currentBet > 0) "call" else "check"
            "2" -> "raise"
            "3" -> "fold"
            "4" -> if (currentBet == 0) "check" else "fold"
            else -> "fold"
        }
    }

    /**
     * Handles the actual player action
     */
    private suspend fun handlePlayerAction(
        player: Player,
        action: String,
        currentBet: Int,
        playersBet: MutableMap<Player, Int>,
    ) {
        when (action) {
            "call" -> {
                val callAmount = currentBet - (playersBet[player] ?: 0)
                player.setBet(callAmount)
                println("${player.name} calls $callAmount")
                stateManager.emitEvent(GameEvents.PlayerCalled(player, callAmount))
            }
            "raise" -> {
                val raiseAmount =
                    if (player.isAI) {
                        (player.chips * 0.1).toInt().coerceAtLeast(10)
                    } else {
                        ConsoleUI.getRaiseAmount(player, 10)
                    }
                player.setBet(currentBet + raiseAmount)
                println("${player.name} raises by $raiseAmount")
                stateManager.emitEvent(GameEvents.PlayerRaised(player, raiseAmount, player.bet))
            }
            "fold" -> {
                player.setFold(true)
                println("${player.name} folds")
                stateManager.emitEvent(GameEvents.PlayerFolded(player))
            }
            "check" -> {
                println("${player.name} checks")
            }
        }
    }

    /**
     * Shows settings menu
     */
    private fun showSettings() {
        println("\n⚙️ Settings")
        println("-".repeat(15))
        println("1. Display Rules")
        println("2. About Pokermon")
        println("3. Back to Main Menu")
        print("Select option (1-3): ")

        when (scanner.nextLine().trim()) {
            "1" -> displayRules()
            "2" -> displayAbout()
        }
    }

    private fun displayRules() {
        println("\n📋 Pokermon Rules")
        println("-".repeat(20))
        println("Standard 5-card draw poker with monster companions!")
        println("• Each player gets 5 cards")
        println("• Betting rounds before and after card exchange")
        println("• Best hand wins the pot")
        println("• Monster companions provide special abilities")
        ConsoleUI.waitForContinue()
    }

    private fun displayAbout() {
        println("\n🎮 About Pokermon")
        println("-".repeat(20))
        println("Version: 1.1.0")
        println("A modern poker game with monster collection mechanics")
        println("Built with Kotlin-native for cross-platform compatibility")
        ConsoleUI.waitForContinue()
    }
}
