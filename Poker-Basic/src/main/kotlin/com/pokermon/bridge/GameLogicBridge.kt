package com.pokermon.bridge

import com.pokermon.*

/**
 * Data class to track AI player actions for UI feedback.
 */
data class AIActionResult(
    val playerName: String,
    val action: String,
    val amount: Int = 0,
    val message: String
)

/**
 * Bridge class that connects the Modern UI with the actual game engine.
 * Provides a clean interface for UI operations while maintaining separation of concerns.
 * Uses public interfaces to avoid package-private access issues.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
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
    private var selectedCardPack = "TET" // Default to TET for backward compatibility
    
    // Game state for save/load functionality
    private var gameStateSaved = false
    private var savedGameState: SavedGameState? = null
    
    // Flag to enable automatic AI processing (disabled by default for compatibility)
    private var enableAutoAI = false
    
    // Track if cards have been exchanged in the current round (limit to once per round)
    private var cardsExchangedThisRound = false
    
    // Track last AI action for UI feedback
    private var lastAIAction: AIActionResult? = null
    
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
                // Start the first round to get cards dealt
                gameEngine!!.startNewRound()
                updatePlayerData()
                this.isGameInitialized = true
            }
            success
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if the game should advance to the next phase after a player action.
     * This handles automatic game flow progression.
     */
    private fun checkAndAdvanceGamePhase() {
        gameEngine?.let { engine ->
            try {
                // Only process AI opponents if we have more than 1 player and this isn't a test scenario
                if (shouldProcessAIOpponents(engine)) {
                    processAIOpponents()
                }
                
                // Check if current betting round is complete or should advance
                val shouldAdvance = engine.isRoundComplete() || shouldForceAdvancePhase(engine)
                
                if (shouldAdvance) {
                    val currentPhase = engine.currentPhase
                    when (currentPhase) {
                        GamePhase.BETTING_ROUND, GamePhase.PLAYER_ACTIONS -> {
                            // Move to card exchange phase after initial betting
                            engine.beginCardExchange()
                        }
                        GamePhase.FINAL_BETTING -> {
                            // Move to winner determination after final betting
                            engine.setPhase(GamePhase.WINNER_DETERMINATION)
                        }
                        else -> {
                            // For other phases, use the standard advance method
                            engine.advancePhase()
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error but don't crash the game
                println("Warning: Error in phase advancement: ${e.message}")
            }
        }
    }
    
    /**
     * Determine if we should automatically process AI opponents.
     * Skip AI processing during tests or when only one player exists.
     */
    private fun shouldProcessAIOpponents(engine: com.pokermon.GameEngine): Boolean {
        val players = engine.players
        if (players == null || players.size <= 1) {
            return false
        }
        
        // Only enable AI processing when explicitly requested (e.g., from Android UI)
        // To maintain compatibility with existing tests and console mode, we need
        // an explicit flag to enable automatic AI processing
        return enableAutoAI && hasMultipleActivePlayers(engine)
    }
    
    /**
     * Check if there are multiple active players in the game.
     */
    private fun hasMultipleActivePlayers(engine: com.pokermon.GameEngine): Boolean {
        val players = engine.players ?: return false
        val activePlayers = players.count { !it.isFold() && it.chips > 0 }
        return activePlayers > 1
    }
    
    /**
     * Get the last AI action for UI feedback.
     */
    fun getLastAIAction(): AIActionResult? {
        return lastAIAction
    }
    
    /**
     * Clear the last AI action (called after UI has processed it).
     */
    fun clearLastAIAction() {
        lastAIAction = null
    }
    
    /**
     * Enable or disable automatic AI processing.
     * This should be enabled for Android UI but disabled for tests and console mode.
     */
    fun setAutoAIEnabled(enabled: Boolean) {
        this.enableAutoAI = enabled
    }
    
    /**
     * Process AI opponent turns automatically after a human player action.
     * This ensures the game flows properly through all AI players before 
     * returning control to the human player.
     */
    private fun processAIOpponents() {
        gameEngine?.let { engine ->
            try {
                val players = engine.players
                if (players == null || players.isEmpty()) return
                
                var safetyCounter = 0
                val maxIterations = players.size * 2 // Prevent infinite loops
                
                // Keep processing until we either reach the human player (index 0) 
                // or determine the round is complete
                while (safetyCounter < maxIterations) {
                    val currentPlayerIndex = engine.currentPlayerIndex
                    
                    // If we're back to human player (index 0) or invalid index, stop
                    if (currentPlayerIndex <= 0 || currentPlayerIndex >= players.size) {
                        break
                    }
                    
                    val currentPlayer = players[currentPlayerIndex]
                    
                    // If current player has folded or is out of chips, advance to next
                    if (currentPlayer.isFold() || currentPlayer.chips <= 0) {
                        engine.nextPlayer()
                        safetyCounter++
                        continue
                    }
                    
                    // Process AI player action based on simple logic
                    processAIPlayerAction(engine, currentPlayer, currentPlayerIndex)
                    
                    // Move to next player
                    engine.nextPlayer()
                    safetyCounter++
                    
                    // Check if round is complete after this AI action
                    if (engine.isRoundComplete()) {
                        break
                    }
                }
            } catch (e: Exception) {
                println("Warning: Error processing AI opponents: ${e.message}")
            }
        }
    }
    
    /**
     * Process a single AI player's action using simple poker AI logic.
     */
    private fun processAIPlayerAction(engine: com.pokermon.GameEngine, player: com.pokermon.Player, playerIndex: Int) {
        try {
            val highBet = engine.getCurrentHighBet()
            val playerBet = player.bet
            val playerChips = player.chips
            val callAmount = (highBet - playerBet).coerceAtMost(playerChips)
            val playerName = player.name ?: "AI $playerIndex"
            
            // Simple AI decision making based on hand strength and chips
            val handValue = player.handValue
            val chipRatio = if (playerChips > 0) callAmount.toDouble() / playerChips else 1.0
            
            when {
                // Fold if hand is very weak and call amount is significant
                handValue < 3 && chipRatio > 0.3 -> {
                    player.setFold(true)
                    lastAIAction = AIActionResult(
                        playerName = playerName,
                        action = "Fold",
                        message = "$playerName folded"
                    )
                }
                // Call if call amount is reasonable
                callAmount <= playerChips && chipRatio <= 0.5 -> {
                    if (callAmount > 0) {
                        player.placeBet(playerBet + callAmount)
                        engine.addToPot(callAmount)
                        lastAIAction = AIActionResult(
                            playerName = playerName,
                            action = "Call",
                            amount = callAmount,
                            message = "$playerName called for $callAmount chips"
                        )
                    } else {
                        lastAIAction = AIActionResult(
                            playerName = playerName,
                            action = "Check",
                            message = "$playerName checked"
                        )
                    }
                }
                // Raise if hand is strong and has chips
                handValue > 5 && playerChips >= callAmount + 20 && chipRatio < 0.3 -> {
                    val raiseAmount = callAmount + 20.coerceAtMost(playerChips / 4)
                    player.placeBet(playerBet + raiseAmount)
                    engine.addToPot(raiseAmount)
                    lastAIAction = AIActionResult(
                        playerName = playerName,
                        action = "Raise",
                        amount = raiseAmount,
                        message = "$playerName raised by $raiseAmount chips"
                    )
                }
                // Default: call if possible, otherwise fold
                callAmount <= playerChips -> {
                    if (callAmount > 0) {
                        player.placeBet(playerBet + callAmount)
                        engine.addToPot(callAmount)
                        lastAIAction = AIActionResult(
                            playerName = playerName,
                            action = "Call",
                            amount = callAmount,
                            message = "$playerName called for $callAmount chips"
                        )
                    } else {
                        lastAIAction = AIActionResult(
                            playerName = playerName,
                            action = "Check",
                            message = "$playerName checked"
                        )
                    }
                }
                else -> {
                    player.setFold(true)
                    lastAIAction = AIActionResult(
                        playerName = playerName,
                        action = "Fold",
                        message = "$playerName folded"
                    )
                }
            }
        } catch (e: Exception) {
            // If AI processing fails, just have the player check/fold
            println("Warning: AI player $playerIndex decision error: ${e.message}")
            if (player.chips <= 0) {
                player.setFold(true)
                lastAIAction = AIActionResult(
                    playerName = "AI $playerIndex",
                    action = "Fold",
                    message = "AI $playerIndex folded (error)"
                )
            }
        }
    }
    
    /**
     * Helper method to determine if we should force phase advancement
     * even if isRoundComplete() returns false.
     */
    private fun shouldForceAdvancePhase(engine: com.pokermon.GameEngine): Boolean {
        return try {
            val players = engine.players
            val activePlayers = players?.filter { !it.isFold() && it.chips > 0 } ?: emptyList()
            
            // If only one active player remains, advance phase
            if (activePlayers.size <= 1) {
                return true
            }
            
            // Check if all active players have acted and bets are equal
            val currentPlayerIndex = engine.currentPlayerIndex
            val currentPlayer = if (currentPlayerIndex >= 0 && currentPlayerIndex < (players?.size ?: 0)) {
                players?.get(currentPlayerIndex)
            } else null
            
            // If current player has folded or has no chips, we should advance
            currentPlayer?.let { player ->
                if (player.isFold() || player.chips <= 0) {
                    return true
                }
            }
            
            false
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
            if (players != null && players.isNotEmpty()) {
                val player = players[0] // Human player is always at index 0
                playerChips = player.getChips()
                
                // Convert player's hand to poker notation format
                player.getHand()?.let { handInts ->
                    playerHand = handInts.map { cardInt ->
                        if (cardInt != 0) cardName(cardInt) else "Empty"
                    }.filter { it != "Empty" }
                } ?: run {
                    playerHand = emptyList()
                }
            }
        }
    }
    
    /**
     * Helper method to get card name in full notation (e.g., "Ace of Spades", "King of Hearts", etc.).
     * This format matches the CardGraphicsManager expectations for proper image loading.
     * Uses the same logic as Main.cardRank and Main.cardSuit for consistency.
     */
    private fun cardName(cardInt: Int): String {
        // Convert to full card names that match the drawable resources
        val suits = arrayOf("Spades", "Hearts", "Diamonds", "Clubs")
        val ranks = arrayOf("error", "Ace", "King", "Queen", "Jack", "Ten", 
                           "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two")
        
        // Validate input range to prevent out-of-bounds access
        if (cardInt < 0 || cardInt >= 52) {
            return "Invalid Card"
        }
        
        // Use the same logic as Main.cardRank - this matches the original card encoding
        var rank = cardInt / 4
        if (cardInt % 4 != 0) {
            rank++
        }
        
        // Use the same logic as Main.cardSuit  
        val suit = cardInt % 4
        
        // Ensure indices are within bounds  
        val rankName = if (rank >= 0 && rank < ranks.size) ranks[rank] else "Unknown"
        val suitName = if (suit >= 0 && suit < suits.size) suits[suit] else "Unknown"
        
        return "$rankName of $suitName"
    }
    
    /**
     * Set the selected card pack for image display.
     */
    fun setSelectedCardPack(cardPack: String) {
        selectedCardPack = cardPack
    }
    
    /**
     * Get the currently selected card pack.
     */
    fun getSelectedCardPack(): String {
        return selectedCardPack
    }
    
    /**
     * Get the resource path for a card image based on card integer value.
     * Maps to the actual card art files in the repository resources.
     * Uses the same mapping as Main.cardRank and Main.cardSuit methods.
     */
    fun getCardImagePath(cardInt: Int): String {
        val cardPackManager = CardPackManager.getInstance()
        
        if (cardInt == 0) {
            // Return card back path or null for text symbols
            return cardPackManager.getCardBackImagePath(selectedCardPack) ?: "TEXT_BACK"
        }
        
        val suits = arrayOf("Spades", "Hearts", "Diamonds", "Clubs")
        val ranks = arrayOf("error", "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", 
                           "Seven", "Six", "Five", "Four", "Three", "Two")
        
        // Use the same logic as Main.cardRank
        var rank = cardInt / 4
        if (cardInt % 4 != 0) {
            rank++
        }
        
        // Use the same logic as Main.cardSuit  
        val suit = cardInt % 4
        
        val rankName = ranks.getOrElse(rank) { "Unknown" }
        val suitName = suits.getOrElse(suit) { "Unknown" }
        
        // Use CardPackManager to get the correct path
        return cardPackManager.getCardImagePath(selectedCardPack, rankName, suitName) 
            ?: "TEXT_${rankName}_${suitName}"
    }
    
    /**
     * Get card image paths for the current player's hand.
     */
    fun getPlayerHandImagePaths(): List<String> {
        return gameEngine?.let { engine ->
            val players = engine.players
            if (players != null && players.isNotEmpty()) {
                val player = players[0] // Human player is always at index 0
                player.getHand()?.map { cardInt ->
                    getCardImagePath(cardInt)
                } ?: emptyList()
            } else {
                emptyList()
            }
        } ?: emptyList()
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
                players?.mapIndexed { index, player ->
                    PlayerInfo(
                        name = player.getName() ?: "Player $index",
                        chips = player.getChips(),
                        isFolded = player.isFold(),
                        isCurrentPlayer = index == 0, // Human player is always at index 0
                        handValue = player.getHandValue()
                    )
                } ?: emptyList()
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
                if (players != null && players.isNotEmpty()) {
                    val player = players[0] // Human player
                    
                    // For testing purposes, use a standard call amount if no high bet exists
                    val highBet = engine.getCurrentHighBet()
                    val callAmount = if (highBet <= 0) 50 else (highBet - player.getBet()).coerceAtMost(player.getChips())
                    
                    if (callAmount <= 0) {
                        // Check if we should advance phase after this action
                        checkAndAdvanceGamePhase()
                        updatePlayerData()
                        GameActionResult(true, "No bet to call")
                    } else {
                        player.placeBet(player.getBet() + callAmount)
                        // Add the call amount to the pot
                        engine.addToPot(callAmount)
                        // Advance to next player after action
                        engine.nextPlayer()
                        // Check if we should advance phase after this action
                        checkAndAdvanceGamePhase()
                        updatePlayerData()
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
                if (players != null && players.isNotEmpty()) {
                    val player = players[0] // Human player
                    
                    if (amount > player.getChips()) {
                        GameActionResult(false, "Not enough chips to raise by $amount")
                    } else {
                        player.placeBet(amount)
                        // Add the raise amount to the pot
                        engine.addToPot(amount)
                        // Advance to next player after action
                        engine.nextPlayer()
                        // Check if we should advance phase after this action
                        checkAndAdvanceGamePhase()
                        updatePlayerData()
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
                if (players != null && players.isNotEmpty()) {
                    val player = players[0] // Human player
                    player.setFold(true)
                    // Advance to next player after action
                    engine.nextPlayer()
                    // Check if we should advance phase after this action
                    checkAndAdvanceGamePhase()
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
            gameEngine?.let { engine ->
                // Advance to next player after action
                engine.nextPlayer()
                // Check if we should advance phase after this action
                checkAndAdvanceGamePhase()
                updatePlayerData()
                GameActionResult(true, "Checked")
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error checking: ${e.message}")
        }
    }
    
    /**
     * Exchange selected cards with new ones from the deck.
     * Limited to once per round to prevent multiple exchanges.
     */
    fun exchangeCards(cardIndices: List<Int>): GameActionResult {
        if (!isGameInitialized) {
            return GameActionResult(false, "Game not initialized")
        }
        
        if (cardsExchangedThisRound) {
            return GameActionResult(false, "Cards can only be exchanged once per round")
        }
        
        return try {
            gameEngine?.let { engine ->
                val cardIndicesArray = cardIndices.toIntArray()
                engine.exchangeCards(0, cardIndicesArray) // 0 = human player index
                cardsExchangedThisRound = true // Mark that cards have been exchanged
                
                // Complete card exchange and move to final betting phase
                engine.completeCardExchange()
                
                updatePlayerData()
                clearCardSelection()
                GameActionResult(true, "Exchanged ${cardIndices.size} cards - ready for final betting")
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
                cardsExchangedThisRound = false // Reset exchange flag for new round
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
     * Get the current player's turn index.
     */
    fun getCurrentPlayerIndex(): Int {
        return gameEngine?.getCurrentPlayerIndex() ?: 0
    }
    
    /**
     * Determine and return the winner of the current hand.
     * Automatically advances to the next round after determining winner.
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
                        else -> {
                            // Get the winner name(s) for display
                            val winnerNames = winners.map { index ->
                                if (index > 0 && index < (engine.players?.size ?: 0)) {
                                    engine.players?.get(index)?.name ?: "Player $index"
                                } else "Unknown"
                            }
                            "🏆 Winner(s): ${winnerNames.joinToString(", ")}"
                        }
                    }
                    
                    updatePlayerData()
                    
                    // Check if we can continue the game
                    if (engine.canContinue()) {
                        // Game continues - prepare for next round
                        GameActionResult(true, "$message\nPreparing next round...")
                    } else {
                        // Game over
                        GameActionResult(true, "$message\nGame Over!")
                    }
                } else {
                    GameActionResult(false, "No winners determined")
                }
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error determining winner: ${e.message}")
        }
    }
    
    /**
     * Get the current game phase.
     */
    fun getCurrentPhase(): GamePhase {
        return gameEngine?.getCurrentPhase() ?: GamePhase.INITIALIZATION
    }
    
    /**
     * Get the current game phase display name.
     */
    fun getPhaseDisplayName(): String {
        return getCurrentPhase().displayName
    }
    
    /**
     * Get the current game phase description.
     */
    fun getPhaseDescription(): String {
        return getCurrentPhase().description
    }
    
    /**
     * Check if cards should be visible in the current phase.
     */
    fun shouldShowCards(): Boolean {
        return getCurrentPhase().shouldShowCards()
    }
    
    /**
     * Check if betting actions are allowed in the current phase.
     */
    fun canBet(): Boolean {
        return getCurrentPhase().allowsBetting()
    }
    
    /**
     * Check if cards can be exchanged in the current phase.
     */
    fun canExchangeCards(): Boolean {
        return getCurrentPhase().allowsCardExchange() && !cardsExchangedThisRound
    }
    
    /**
     * Get information about card exchange status for UI display.
     */
    fun getCardExchangeStatus(): String {
        return when {
            !getCurrentPhase().allowsCardExchange() -> "Not in card exchange phase"
            cardsExchangedThisRound -> "Cards already exchanged this round"
            else -> "Ready to exchange cards"
        }
    }
    
    /**
     * Check if round progression is allowed in the current phase.
     */
    fun canProgressRound(): Boolean {
        return getCurrentPhase().allowsRoundProgression()
    }
    
    /**
     * Check if the current phase requires player input.
     */
    fun needsPlayerInput(): Boolean {
        return getCurrentPhase().requiresPlayerInput()
    }
    
    /**
     * Check if the game is in an active phase.
     */
    fun isActivePhase(): Boolean {
        return getCurrentPhase().isActivePhase()
    }
    
    /**
     * Advance to the next game phase manually.
     */
    fun advancePhase(): GameActionResult {
        return try {
            gameEngine?.let { engine ->
                val success = engine.advancePhase()
                if (success) {
                    updatePlayerData()
                    GameActionResult(true, "Advanced to ${engine.getCurrentPhase().displayName}")
                } else {
                    GameActionResult(false, "Cannot advance from current phase")
                }
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error advancing phase: ${e.message}")
        }
    }
    
    /**
     * Complete card exchange phase and move to final betting.
     */
    fun completeCardExchange(): GameActionResult {
        return try {
            gameEngine?.let { engine ->
                engine.completeCardExchange()
                updatePlayerData()
                clearCardSelection()
                GameActionResult(true, "Card exchange completed, moving to final betting")
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error completing card exchange: ${e.message}")
        }
    }
    
    /**
     * Get the underlying game engine for testing purposes.
     * This method should only be used in tests.
     */
    internal fun getGameEngine(): GameEngine? {
        return gameEngine
    }
    
    /**
     * Save the current game state.
     */
    fun saveGameState(): GameActionResult {
        return try {
            if (!isGameInitialized) {
                return GameActionResult(false, "No game to save")
            }
            
            gameEngine?.let { engine ->
                savedGameState = SavedGameState(
                    playerName = playerName,
                    gameMode = gameMode,
                    currentRound = engine.currentRound,
                    currentPhase = engine.currentPhase,
                    currentPot = currentPot,
                    playerChips = playerChips,
                    playerCards = playerHand,
                    allPlayersData = getAllPlayers(),
                    selectedCards = selectedCards.toSet(),
                    isGameActive = true
                )
                gameStateSaved = true
                GameActionResult(true, "Game saved successfully")
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            GameActionResult(false, "Error saving game: ${e.message}")
        }
    }
    
    /**
     * Load a previously saved game state.
     */
    fun loadGameState(): GameActionResult {
        return try {
            savedGameState?.let { saved ->
                // Restore basic game parameters
                playerName = saved.playerName
                gameMode = saved.gameMode
                currentPot = saved.currentPot
                playerChips = saved.playerChips
                playerHand = saved.playerCards
                selectedCards = saved.selectedCards.toMutableSet()
                
                // Reinitialize game with saved parameters
                val success = initializeGame(saved.playerName, saved.allPlayersData.size, saved.playerChips)
                if (success) {
                    // Try to restore phase (may not be perfect but gives basic restoration)
                    gameEngine?.setPhase(saved.currentPhase)
                    updatePlayerData()
                    GameActionResult(true, "Game loaded successfully")
                } else {
                    GameActionResult(false, "Failed to restore game state")
                }
            } ?: GameActionResult(false, "No saved game found")
        } catch (e: Exception) {
            GameActionResult(false, "Error loading game: ${e.message}")
        }
    }
    
    /**
     * Check if there is a saved game available.
     */
    fun hasSavedGame(): Boolean {
        return savedGameState != null
    }
    
    /**
     * Clear the saved game state.
     */
    fun clearSavedGame(): GameActionResult {
        return try {
            savedGameState = null
            gameStateSaved = false
            GameActionResult(true, "Saved game cleared")
        } catch (e: Exception) {
            GameActionResult(false, "Error clearing saved game: ${e.message}")
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

/**
 * Saved game state for persistence functionality.
 */
data class SavedGameState(
    val playerName: String,
    val gameMode: GameMode,
    val currentRound: Int,
    val currentPhase: GamePhase,
    val currentPot: Int,
    val playerChips: Int,
    val playerCards: List<String>,
    val allPlayersData: List<PlayerInfo>,
    val selectedCards: Set<Int>,
    val isGameActive: Boolean
)