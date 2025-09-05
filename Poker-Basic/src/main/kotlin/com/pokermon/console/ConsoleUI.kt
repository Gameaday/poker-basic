package com.pokermon.console

import com.pokermon.modern.CardUtils
import com.pokermon.HandEvaluator
import com.pokermon.players.Player
import com.pokermon.GameMode
import java.util.*

/**
 * Console user interface utilities for the Pokermon game.
 * Provides clean, formatted output and input handling for the text-based interface.
 * 
 * @author Pokermon Console System
 * @version 1.1.0
 */
object ConsoleUI {
    
    private val scanner = Scanner(System.`in`)
    
    /**
     * Displays the main menu with game mode selection
     */
    fun displayMainMenu() {
        println("\n" + "=".repeat(60))
        println("                    🃏 POKERMON 🃏")
        println("         Advanced Poker with Monster Companions")
        println("=".repeat(60))
        println()
        println("Game Modes:")
        println("1. Classic Poker   - Traditional 5-card draw poker")
        println("2. Adventure Mode  - Story-driven with monster battles")
        println("3. Safari Mode     - Catch wild monsters while playing")
        println("4. Ironman Mode    - Hardcore permadeath challenge")
        println("5. Settings        - Configure game preferences")
        println("6. Exit")
        println()
        print("Select game mode (1-6): ")
    }
    
    /**
     * Displays player's hand with formatted card representation
     */
    fun displayHand(player: Player, showAll: Boolean = true) {
        println("\n${player.name}'s Hand:")
        println("-".repeat(20))
        
        if (showAll) {
            player.hand.forEachIndexed { index, card ->
                val cardName = CardUtils.cardName(card)
                val rank = CardUtils.rankName(CardUtils.cardRank(card))
                val suit = CardUtils.suitName(CardUtils.cardSuit(card))
                println("${index + 1}. $cardName ($rank of $suit)")
            }
            
            // Display hand evaluation
            val handResult = HandEvaluator.evaluateHand(player.hand)
            println()
            println("Hand Type: ${handResult.handType}")
            println("Hand Score: ${handResult.score}")
            if (handResult.description.isNotEmpty()) {
                println("Description: ${handResult.description}")
            }
        } else {
            println("Hand hidden (${player.hand.size} cards)")
        }
    }
    
    /**
     * Displays current pot and betting information
     */
    fun displayPotInfo(pot: Int, currentBet: Int, minimumBet: Int) {
        println("\n💰 Pot Information:")
        println("Current Pot: $$pot")
        println("Current Bet: $$currentBet")
        println("Minimum Bet: $$minimumBet")
    }
    
    /**
     * Displays player status including chips and current bet
     */
    fun displayPlayerStatus(players: List<Player>, activePlayerIndex: Int? = null) {
        println("\n👥 Player Status:")
        println("-".repeat(50))
        players.forEachIndexed { index, player ->
            val activeMarker = if (index == activePlayerIndex) "👉 " else "   "
            val statusIcon = if (player.folded) "❌" else "✅"
            println("$activeMarker$statusIcon ${player.name}: $${player.chips} chips")
        }
    }
    
    /**
     * Gets player input for betting actions
     */
    fun getBettingAction(player: Player, currentBet: Int, minimumBet: Int): String {
        println("\n${player.name}'s turn:")
        println("Current bet to call: $$currentBet")
        println("Your chips: $${player.chips}")
        println()
        println("Actions:")
        println("1. Call ($currentBet)")
        println("2. Raise")
        println("3. Fold")
        if (currentBet == 0) println("4. Check")
        println()
        print("Choose action (1-${if (currentBet == 0) 4 else 3}): ")
        
        return scanner.nextLine().trim()
    }
    
    /**
     * Gets raise amount from player
     */
    fun getRaiseAmount(player: Player, minimumRaise: Int): Int {
        println("\nEnter raise amount (minimum $minimumRaise): ")
        print("Raise amount: $")
        
        return try {
            val amount = scanner.nextInt()
            scanner.nextLine() // consume newline
            maxOf(amount, minimumRaise)
        } catch (e: Exception) {
            scanner.nextLine() // consume invalid input
            println("Invalid amount. Using minimum raise.")
            minimumRaise
        }
    }
    
    /**
     * Gets cards to exchange from player
     */
    fun getCardsToExchange(player: Player): List<Int> {
        displayHand(player)
        println("\nCard Exchange Phase:")
        println("Enter card numbers to exchange (1-5), separated by spaces")
        println("Example: '1 3 5' to exchange cards 1, 3, and 5")
        println("Press Enter without typing anything to keep all cards")
        print("Cards to exchange: ")
        
        val input = scanner.nextLine().trim()
        if (input.isEmpty()) return emptyList()
        
        return try {
            input.split(" ")
                .map { it.toInt() - 1 } // Convert to 0-based index
                .filter { it in 0..4 } // Validate range
        } catch (e: Exception) {
            println("Invalid input. Keeping all cards.")
            emptyList()
        }
    }
    
    /**
     * Displays game mode selection and configuration
     */
    fun configureGameMode(gameMode: GameMode): Map<String, Any> {
        println("\n🎮 Configuring ${gameMode.displayName}")
        println("-".repeat(40))
        
        val config = mutableMapOf<String, Any>()
        
        // Number of AI opponents
        print("Number of AI opponents (1-3): ")
        val aiCount = try {
            scanner.nextInt().coerceIn(1, 3)
        } catch (e: Exception) {
            scanner.nextLine()
            2 // default
        }
        scanner.nextLine() // consume newline
        config["aiOpponents"] = aiCount
        
        // Starting chips
        print("Starting chips (100-10000): ")
        val startingChips = try {
            scanner.nextInt().coerceIn(100, 10000)
        } catch (e: Exception) {
            scanner.nextLine()
            1000 // default
        }
        scanner.nextLine() // consume newline
        config["startingChips"] = startingChips
        
        // Difficulty level
        println("Difficulty level:")
        println("1. Easy")
        println("2. Medium") 
        println("3. Hard")
        print("Select difficulty (1-3): ")
        val difficulty = try {
            scanner.nextInt().coerceIn(1, 3)
        } catch (e: Exception) {
            scanner.nextLine()
            2 // default medium
        }
        scanner.nextLine() // consume newline
        config["difficulty"] = difficulty
        
        return config
    }
    
    /**
     * Displays session statistics
     */
    fun displaySessionStats(stats: Map<String, Any>) {
        println("\n📊 Session Statistics:")
        println("-".repeat(30))
        stats.forEach { (key, value) ->
            println("$key: $value")
        }
    }
    
    /**
     * Displays game over screen with results
     */
    fun displayGameOver(winner: Player?, finalScores: Map<String, Int>) {
        println("\n" + "=".repeat(60))
        println("                    🏆 GAME OVER 🏆")
        println("=".repeat(60))
        
        if (winner != null) {
            println("Winner: ${winner.name} with $${winner.chips} chips!")
        } else {
            println("Game ended in a draw!")
        }
        
        println("\nFinal Scores:")
        finalScores.entries
            .sortedByDescending { it.value }
            .forEachIndexed { index, (name, chips) ->
                val medal = when (index) {
                    0 -> "🥇"
                    1 -> "🥈" 
                    2 -> "🥉"
                    else -> "  "
                }
                println("$medal $name: $$chips")
            }
    }
    
    /**
     * Waits for user input to continue
     */
    fun waitForContinue() {
        print("\nPress Enter to continue...")
        scanner.nextLine()
    }
    
    /**
     * Clears the console screen (works on most terminals)
     */
    fun clearScreen() {
        repeat(50) { println() }
    }
}