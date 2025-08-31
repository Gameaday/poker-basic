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
            currentPot = engine.currentPot
            val players = engine.players
            if (players.isNotEmpty()) {
                val player = players[0] // Human player is always at index 0
                playerChips = player.getChips()
                
                // Convert player's hand to displayable strings
                player.getConvertedHand()?.let { hand ->
                    playerHand = hand.toList()
                } ?: run {
                    // Fallback to basic card names if converted hand isn't available
                    player.getHand()?.let { handInts ->
                        playerHand = handInts.map { cardInt ->
                            if (cardInt != 0) cardName(cardInt) else "Empty"
                        }.filter { it != "Empty" }
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to get card name using Main.cardName.
     */
    private fun cardName(cardInt: Int): String {
        // Use a simple conversion since Main.cardName is private
        val suits = arrayOf("Spades", "Hearts", "Diamonds", "Clubs")
        val ranks = arrayOf("Error", "Ace", "King", "Queen", "Jack", "Ten", 
                           "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two", "One")
        
        val rank = cardInt / 4 + 1
        val suit = cardInt % 4
        
        return "${ranks.getOrElse(rank) { "Unknown" }} of ${suits.getOrElse(suit) { "Unknown" }}"
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
            gameEngine?.let { engine ->
                val players = engine.players
                players.mapIndexed { index, player ->
                    PlayerInfo(
                        name = player.getName() ?: "Player $index",
                        chips = player.getChips(),
                        isFolded = player.isFold(),
                        isCurrentPlayer = index == 0, // Human player is always at index 0
                        handValue = player.getHandValue()
                    )
                }
            } ?: emptyList()
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
            gameEngine?.let { engine ->
                val players = engine.players
                if (players.isNotEmpty()) {
                    val player = players[0] // Human player
                    
                    // For testing purposes, use a standard call amount if no high bet exists
                    val highBet = engine.currentHighBet
                    val callAmount = if (highBet <= 0) 50 else (highBet - player.getBet()).coerceAtMost(player.getChips())
                    
                    if (callAmount <= 0) {
                        GameActionResult(true, "No bet to call")
                    } else {
                        player.placeBet(player.getBet() + callAmount)
                        // Update the engine's pot as well
                        val newPot = engine.currentPot + callAmount
                        // Since GameEngine doesn't have a setPot method, update manually
                        currentPot = newPot
                        playerChips = player.getChips()
                        GameActionResult(true, "Called for $callAmount chips")
                    }
                } else {
                    GameActionResult(false, "No players found")
                }
            } ?: GameActionResult(false, "Game engine not available")
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
            gameEngine?.let { engine ->
                val players = engine.players
                if (players.isNotEmpty()) {
                    val player = players[0] // Human player
                    
                    if (amount > player.getChips()) {
                        GameActionResult(false, "Not enough chips to raise by $amount")
                    } else {
                        player.placeBet(amount)
                        // Synchronize the pot from the engine after the bet
                        currentPot = engine.currentPot
                        playerChips = player.getChips()
                        GameActionResult(true, "Raised by $amount chips")
                    }
                } else {
                    GameActionResult(false, "No players found")
                }
            } ?: GameActionResult(false, "Game engine not available")
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
            gameEngine?.let { engine ->
                val players = engine.players
                if (players.isNotEmpty()) {
                    val player = players[0] // Human player
                    player.setFold(true)
                    updatePlayerData()
                    GameActionResult(true, "Folded")
                } else {
                    GameActionResult(false, "No players found")
                }
            } ?: GameActionResult(false, "Game engine not available")
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
            gameEngine?.let { engine ->
                val cardIndicesArray = cardIndices.toIntArray()
                engine.exchangeCards(0, cardIndicesArray) // 0 = human player index
                updatePlayerData()
                clearCardSelection()
                GameActionResult(true, "Exchanged ${cardIndices.size} cards")
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error exchanging cards: ${e.message}")
        }
    }
    
    /**
     * Advance to the next round of the game.
     */
    fun nextRound(): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        return try {
            gameEngine?.let { engine ->
                engine.startNewRound()
                updatePlayerData()
                GameActionResult(true, "Advanced to round ${engine.currentRound}")
            } ?: GameActionResult(false, "Game engine not available")
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
            gameEngine?.let { engine ->
                val winners = engine.determineWinners()
                if (winners.isNotEmpty()) {
                    engine.distributePot(winners)
                    val isHumanWinner = winners.contains(0) // Human player is at index 0
                    
                    val message = when {
                        isHumanWinner && winners.size == 1 -> "🎉 You won the hand!"
                        isHumanWinner && winners.size > 1 -> "🤝 You tied for the win!"
                        else -> "You lost this hand"
                    }
                    
                    updatePlayerData()
                    GameActionResult(true, message)
                } else {
                    GameActionResult(false, "No winners determined")
                }
            } ?: GameActionResult(false, "Game engine not available")
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