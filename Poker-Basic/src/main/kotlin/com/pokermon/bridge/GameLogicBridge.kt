package com.pokermon.bridge

import com.pokermon.*

/**
 * Bridge class that connects the Modern UI with the actual game engine.
 * Provides a clean interface for UI operations while maintaining separation of concerns.
 * Uses public interfaces to avoid package-private access issues.
 */
class GameLogicBridge {
    private var gameEngine: GameEngine? = null
    private var isGameInitialized = false
    private var playerName = "Player"
    private var currentPot = 0
    private var currentBet = 0
    private var gameMode = GameMode.CLASSIC
    private var selectedCards = mutableSetOf<Int>()
    private var playerChips = 1000
    private var playerHand = listOf<String>()
    
    /**
     * Initialize a new game with the specified parameters.
     */
    fun initializeGame(
        playerName: String,
        playerCount: Int,
        startingChips: Int
    ): Boolean {
        return try {
            this.playerName = playerName
            this.currentPot = 0
            this.currentBet = 0
            this.playerChips = startingChips
            
            // Create game configuration
            val gameConfig = Game(5, playerCount, startingChips, 2, gameMode)
            this.gameEngine = GameEngine(gameConfig)
            
            // Create player names array
            val playerNames = Array(playerCount) { i ->
                if (i == 0) playerName else "CPU ${i}"
            }
            
            // Initialize the game and deal first hand
            val success = gameEngine!!.initializeGame(playerNames)
            if (success) {
                updatePlayerData()
                this.isGameInitialized = true
            }
            success
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Updates local player data from the game engine.
     */
    private fun updatePlayerData() {
        gameEngine?.let { engine ->
            currentPot = engine.getCurrentPot()
            // Use simple demo data for now
            playerHand = listOf("Ace of Spades", "King of Hearts", "Queen of Diamonds", "Jack of Clubs", "Ten of Spades")
        }
    }
    
    /**
     * Get the current player's hand as displayable card strings.
     */
    fun getPlayerHand(): List<String> {
        updatePlayerData()
        return playerHand
    }
    
    /**
     * Get current pot value.
     */
    fun getCurrentPot(): Int {
        return currentPot
    }
    
    /**
     * Get player's current chip count.
     */
    fun getPlayerChips(): Int {
        return playerChips
    }
    
    /**
     * Get information about all players for display.
     */
    fun getAllPlayers(): List<PlayerInfo> {
        return if (isGameInitialized) {
            listOf(
                PlayerInfo(playerName, playerChips, false, true, 85),
                PlayerInfo("CPU 1", 950, false, false, 78),
                PlayerInfo("CPU 2", 1100, false, false, 62),
                PlayerInfo("CPU 3", 800, true, false, 0)
            )
        } else {
            emptyList()
        }
    }
    
    /**
     * Set the game mode for the next game.
     */
    fun setGameMode(mode: GameMode) {
        this.gameMode = mode
    }
    
    /**
     * Get the current game mode.
     */
    fun getGameMode(): GameMode {
        return gameMode
    }
    
    /**
     * Toggle card selection for exchange.
     */
    fun toggleCardSelection(cardIndex: Int): Boolean {
        return if (selectedCards.contains(cardIndex)) {
            selectedCards.remove(cardIndex)
            false
        } else {
            selectedCards.add(cardIndex)
            true
        }
    }
    
    /**
     * Get currently selected cards.
     */
    fun getSelectedCards(): Set<Int> {
        return selectedCards.toSet()
    }
    
    /**
     * Clear card selection.
     */
    fun clearCardSelection() {
        selectedCards.clear()
    }
    
    /**
     * Perform a call action.
     */
    fun performCall(): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            // Simplified call - bet a standard amount
            val callAmount = 50.coerceAtMost(playerChips)
            playerChips -= callAmount
            currentPot += callAmount
            GameActionResult(true, "Called for $callAmount chips")
        } catch (e: Exception) {
            GameActionResult(false, "Error performing call: ${e.message}")
        }
    }
    
    /**
     * Perform a raise action.
     */
    fun performRaise(amount: Int): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            val totalAmount = amount // For simplicity, just use the raise amount directly
            
            if (totalAmount > playerChips) {
                GameActionResult(false, "Not enough chips to raise by $amount")
            } else {
                playerChips -= totalAmount
                currentPot += totalAmount
                GameActionResult(true, "Raised by $amount chips")
            }
        } catch (e: Exception) {
            GameActionResult(false, "Error performing raise: ${e.message}")
        }
    }
    
    /**
     * Perform a fold action.
     */
    fun performFold(): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            GameActionResult(true, "Folded")
        } catch (e: Exception) {
            GameActionResult(false, "Error folding: ${e.message}")
        }
    }
    
    /**
     * Perform a check action.
     */
    fun performCheck(): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            GameActionResult(true, "Checked")
        } catch (e: Exception) {
            GameActionResult(false, "Error checking: ${e.message}")
        }
    }
    
    /**
     * Exchange selected cards with new ones from the deck.
     */
    fun exchangeCards(cardIndices: List<Int>): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            // For now, simulate card exchange by updating the hand
            val newHand = playerHand.toMutableList()
            val cardNames = listOf("Ace of Spades", "King of Hearts", "Queen of Diamonds", 
                                  "Jack of Clubs", "Ten of Spades", "Nine of Hearts", 
                                  "Eight of Diamonds", "Seven of Clubs")
            
            cardIndices.forEach { index ->
                if (index < newHand.size) {
                    newHand[index] = cardNames.random()
                }
            }
            
            playerHand = newHand
            clearCardSelection()
            GameActionResult(true, "Exchanged ${cardIndices.size} cards")
        } catch (e: Exception) {
            GameActionResult(false, "Error exchanging cards: ${e.message}")
        }
    }
    
    /**
     * Advance to the next round of the game.
     */
    fun nextRound(): GameActionResult {
        if (!isGameInitialized || gameEngine == null) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            gameEngine!!.nextRound()
            updatePlayerData()
            GameActionResult(true, "Advanced to next round")
        } catch (e: Exception) {
            GameActionResult(false, "Error advancing round: ${e.message}")
        }
    }
    
    /**
     * Check if the current round is complete.
     */
    fun isRoundComplete(): Boolean {
        return gameEngine?.isRoundComplete() ?: false
    }
    
    /**
     * Get the current round number.
     */
    fun getCurrentRound(): Int {
        return gameEngine?.getCurrentRound() ?: 0
    }
    
    /**
     * Determine and return the winner of the current hand.
     */
    fun determineWinner(): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            // Simulate winner determination for demo
            val random = kotlin.random.Random.nextInt(3)
            val message = when (random) {
                0 -> "🎉 You won the hand!"
                1 -> "You lost this hand"
                else -> "It's a tie!"
            }
            
            if (random == 0) {
                playerChips += currentPot
                currentPot = 0
            }
            
            updatePlayerData()
            GameActionResult(true, message)
        } catch (e: Exception) {
            GameActionResult(false, "Error determining winner: ${e.message}")
        }
    }
}

/**
 * Data class representing player information for the UI.
 */
data class PlayerInfo(
    val name: String,
    val chips: Int,
    val isFolded: Boolean,
    val isCurrentPlayer: Boolean,
    val handValue: Int
)

/**
 * Result of a game action.
 */
data class GameActionResult(
    val success: Boolean,
    val message: String
)