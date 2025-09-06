package com.pokermon.modern

import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.GameActionResult
import com.pokermon.GameMode
import com.pokermon.Version
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Modern console-based UI for the Pokermon game.
 * Provides a sophisticated text interface with enhanced user experience.
 * 
 * This replaces the JavaFX implementation with a pure console interface
 * suitable for server deployment and cross-platform compatibility.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object ModernPokerApp {
    
    private val gameBridge = GameLogicBridge()
    private val scanner = Scanner(System.`in`)
    
    // Game state
    private var currentPot = 0
    private var playerChips = 1000
    private var statusMessage = "Welcome to ${Version.APP_NAME}!"
    
    /**
     * Launch the modern console interface
     */
    fun launch() {
        runBlocking {
            showMainMenu()
        }
    }
    
    /**
     * Display the main menu and handle user interaction
     */
    private suspend fun showMainMenu() {
        println("=".repeat(60))
        println("   🎮 ${Version.APP_NAME} - Modern Console Interface 🎮")
        println("=".repeat(60))
        println()
        
        while (true) {
            println(statusMessage)
            println()
            println("🃏 Main Menu:")
            println("  [1] 🎲 New Game")
            println("  [2] ⚙️  Settings")
            println("  [3] ❓ Help")
            println("  [4] ❌ Exit")
            println()
            print("Select option (1-4): ")
            
            when (scanner.nextLine().trim()) {
                "1" -> startNewGame()
                "2" -> showSettings()
                "3" -> showHelp()
                "4" -> {
                    println("👋 Thanks for playing ${Version.APP_NAME}!")
                    return
                }
                else -> statusMessage = "❌ Invalid option. Please select 1-4."
            }
        }
    }
    
    /**
     * Start a new game with user configuration
     */
    private suspend fun startNewGame() {
        println("\n🎲 New Game Setup")
        println("-".repeat(40))
        
        // Get player name
        print("Enter your name: ")
        val playerName = scanner.nextLine().trim().ifEmpty { "Player" }
        
        // Get number of AI opponents
        print("Number of AI opponents (1-3): ")
        val aiCount = scanner.nextLine().trim().toIntOrNull()?.coerceIn(1, 3) ?: 2
        
        // Get starting chips
        print("Starting chips (500, 1000, 2500): ")
        val startingChips = when (scanner.nextLine().trim().toIntOrNull()) {
            500, 1000, 2500 -> scanner.nextLine().trim().toInt()
            else -> 1000
        }
        
        // Initialize game
        val gameResult = gameBridge.initializeGame(playerName, aiCount + 1, startingChips)
        
        if (gameResult) {
            runGameLoop()
        } else {
            statusMessage = "❌ Failed to start game"
        }
    }
    
    /**
     * Main game loop
     */
    private suspend fun runGameLoop() {
        println("\n🃏 Game Started!")
        
        while (true) {
            val gameState = gameBridge.getGameState()
            
            // Display game state
            displayGameState(gameState)
            
            // Handle player input or AI turn
            val action = if (gameBridge.isCurrentPlayerHuman()) {
                getPlayerAction()
            } else {
                // AI turn - auto-play
                gameBridge.performAIAction()
                continue
            }
            
            // Process action
            val result = gameBridge.processPlayerAction(action)
            if (!result.success) {
                println("❌ ${result.message}")
                continue
            }
            
            // Check if game is over
            if (gameBridge.isGameOver()) {
                displayGameResults()
                break
            }
        }
        
        statusMessage = "✅ Game completed!"
    }
    
    /**
     * Display current game state
     */
    private fun displayGameState(gameState: GameActionResult) {
        println("\n" + "=".repeat(50))
        println("🎮 Game State")
        println("-".repeat(50))
        println("💰 Pot: $${currentPot}")
        println("💳 Your Chips: $${playerChips}")
        println("🃏 Your Hand: ${formatHand(gameState.playerHand)}")
        println("=".repeat(50))
    }
    
    /**
     * Get player action
     */
    private fun getPlayerAction(): String {
        println("\n🎯 Your Turn:")
        println("  [1] Call")
        println("  [2] Raise")
        println("  [3] Fold")
        print("Select action (1-3): ")
        
        return when (scanner.nextLine().trim()) {
            "1" -> "call"
            "2" -> {
                print("Raise amount: ")
                val amount = scanner.nextLine().trim().toIntOrNull() ?: 0
                "raise:$amount"
            }
            "3" -> "fold"
            else -> "call" // Default action
        }
    }
    
    /**
     * Display game results
     */
    private fun displayGameResults() {
        println("\n🏆 Game Results")
        println("=".repeat(50))
        // Implementation would show winners, final standings, etc.
        println("Thanks for playing!")
    }
    
    /**
     * Show settings menu
     */
    private fun showSettings() {
        println("\n⚙️ Settings")
        println("-".repeat(20))
        println("Settings functionality coming soon!")
        println()
    }
    
    /**
     * Show help information
     */
    private fun showHelp() {
        println("\n❓ Help - ${Version.APP_NAME}")
        println("=".repeat(50))
        println("This is a sophisticated poker game with monster collection mechanics.")
        println()
        println("🎮 Game Modes:")
        println("  • Classic Poker - Traditional 5-card draw poker")
        println("  • Adventure Mode - Poker with monster battles")
        println()
        println("🃏 How to Play:")
        println("  1. Choose your starting chips")
        println("  2. Play against AI opponents")
        println("  3. Win chips through poker hands")
        println("  4. Collect and battle monsters in Adventure mode")
        println()
        println("Press Enter to continue...")
        scanner.nextLine()
    }
    
    /**
     * Generate AI player names
     */
    private fun generateAINames(count: Int): Array<String> {
        val aiNames = arrayOf("CardShark", "PokerBot", "BluffMaster", "ChipChaser")
        return aiNames.take(count).toTypedArray()
    }
    
    /**
     * Format hand for display
     */
    private fun formatHand(hand: IntArray?): String {
        return hand?.joinToString(" ") { cardToString(it) } ?: "No cards"
    }
    
    /**
     * Convert card number to string representation
     */
    private fun cardToString(card: Int): String {
        val suits = arrayOf("♠", "♥", "♦", "♣")
        val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        
        val suit = suits[card / 13]
        val rank = ranks[card % 13]
        return "$rank$suit"
    }
}