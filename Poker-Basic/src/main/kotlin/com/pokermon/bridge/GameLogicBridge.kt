package com.pokermon.bridge

/**
 * Simplified bridge class that provides game state for the new Modern UI.
 * This will be expanded to connect with actual game logic once interface issues are resolved.
 * For now, it provides static/mock data to demonstrate the UI functionality.
 */
class GameLogicBridge {
    private var isGameInitialized = false
    private var playerName = "Player"
    private var playerChips = 1000
    private var currentPot = 0
    private var currentBet = 0
    
    // Sample data for demonstration
    private val mockPlayers = listOf(
        PlayerInfo("CPU 1", 950, false, false, 45),
        PlayerInfo("CPU 2", 1100, false, false, 52),
        PlayerInfo("CPU 3", 800, true, false, 0)
    )
    
    private val mockPlayerHand = listOf("AS", "KH", "QD", "JC", "10S")
    
    /**
     * Initialize a new game with the specified parameters.
     */
    fun initializeGame(
        playerName: String,
        playerCount: Int,
        startingChips: Int
    ): Boolean {
        this.playerName = playerName
        this.playerChips = startingChips
        this.currentPot = 0
        this.isGameInitialized = true
        return true
    }
    
    /**
     * Get the current player's hand as displayable card strings.
     */
    fun getPlayerHand(): List<String> {
        return if (isGameInitialized) mockPlayerHand else emptyList()
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
                PlayerInfo(playerName, playerChips, false, true, 67)
            ) + mockPlayers
        } else {
            emptyList()
        }
    }
    
    /**
     * Perform a call action.
     */
    fun performCall(): GameActionResult {
        if (!isGameInitialized) return GameActionResult(false, "Game not initialized")
        currentPot += 50
        playerChips -= 50
        return GameActionResult(true, "Called $50")
    }
    
    /**
     * Perform a raise action.
     */
    fun performRaise(amount: Int): GameActionResult {
        if (!isGameInitialized) return GameActionResult(false, "Game not initialized")
        if (amount > playerChips) return GameActionResult(false, "Not enough chips")
        currentPot += amount
        playerChips -= amount
        return GameActionResult(true, "Raised by $amount")
    }
    
    /**
     * Perform a fold action.
     */
    fun performFold(): GameActionResult {
        if (!isGameInitialized) return GameActionResult(false, "Game not initialized")
        return GameActionResult(true, "Folded")
    }
    
    /**
     * Perform a check action.
     */
    fun performCheck(): GameActionResult {
        if (!isGameInitialized) return GameActionResult(false, "Game not initialized")
        return GameActionResult(true, "Checked")
    }
    
    /**
     * Exchange selected cards.
     */
    fun exchangeCards(cardIndices: List<Int>): GameActionResult {
        if (!isGameInitialized) return GameActionResult(false, "Game not initialized")
        return GameActionResult(true, "Exchanged ${cardIndices.size} cards")
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