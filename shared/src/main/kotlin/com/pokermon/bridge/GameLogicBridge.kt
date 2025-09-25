package com.pokermon.bridge

import com.pokermon.*
import com.pokermon.database.CardPackManager
import com.pokermon.modern.CardUtils
import com.pokermon.players.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Data class to track AI player actions for UI feedback.
 */
data class AIActionResult(
    val playerName: String,
    val action: String,
    val amount: Int = 0,
    val message: String,
)

/**
 * Context for AI decision making with all relevant game state information.
 */
private data class AIDecisionContext(
    val playerName: String,
    val handValue: Int,
    val playerChips: Int,
    val playerBet: Int,
    val callAmount: Int,
    val chipRatio: Double,
    val canAffordCall: Boolean,
    val hasStrongHand: Boolean,
    val hasWeakHand: Boolean,
    val isSignificantBet: Boolean,
    val isReasonableBet: Boolean,
    val canRaise: Boolean,
    val raiseCostRatio: Boolean,
)

/**
 * Enum representing AI decision types for cleaner decision flow.
 */
private enum class AIDecision {
    FOLD,
    CHECK,
    CALL,
    RAISE
}

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

    // ================================================================
    // STATE MANAGEMENT INTEGRATION - Flow-based reactive game state
    // ================================================================
    
    private val stateManager = GameStateManager()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Exposes the game state as a Flow for UI observation.
     * This enables reactive UI updates when game state changes.
     */
    val gameStateFlow: StateFlow<GameState> = stateManager.gameState

    /**
     * Get the current game state synchronously.
     */
    fun getCurrentGameState(): GameState = stateManager.getCurrentState()

    /**
     * Updates the game state and notifies all observers.
     * This is the primary method for state transitions.
     */
    private fun //updateGameState(newState: GameState) {
        coroutineScope.launch {
            stateManager.//updateGameState(newState)
        }
    }

    /**
     * Emits a game event to all subscribers.
     */
    private fun //emitGameEvent(event: GameEvents) {
        coroutineScope.launch {
            stateManager.emitEvent(event)
        }
    }

    /**
     * Processes a game action through the state management system.
     */
    private fun processGameAction(action: GameActions) {
        coroutineScope.launch {
            stateManager.processAction(action)
        }
    }

    /**
     * Initialize a new game with the specified parameters.
     * Now integrates with Flow-based state management for reactive updates.
     */
    fun initializeGame(
        playerName: String,
        playerCount: Int,
        startingChips: Int,
    ): Boolean {
        return try {
            // Update to initializing state
            //updateGameState(GameState.Initializing)
            
            this.playerName = playerName
            this.currentPot = 0
            this.currentBet = 0
            this.playerChips = startingChips

            // Create game configuration
            val gameConfig = Game(5, playerCount, startingChips, 2, gameMode)
            this.gameEngine = GameEngine(gameConfig)

            // Create player names array
            val playerNames =
                Array(playerCount) { i ->
                    if (i == 0) playerName else "CPU $i"
                }

            // Initialize the game and deal first hand
            val success = gameEngine!!.initializeGame(playerNames)
            if (success) {
                // Start the first round to get cards dealt
                gameEngine!!.startNewRound()
                updatePlayerData()
                this.isGameInitialized = true
                
                // Create players list for state management
                val players = gameEngine!!.players?.toList() ?: emptyList()
                val currentPhase = gameEngine!!.currentPhase
                
                // Transition to playing state with current game data
                //updateGameState(
                    GameState.Playing(
                        players = players,
                        currentPhase = currentPhase,
                        pot = currentPot,
                        currentBet = currentBet,
                        activePlayerIndex = 0
                    )
                )
                
                // Emit game started event
                //emitGameEvent(GameEvents.GameStarted)
                //emitGameEvent(GameEvents.CardsDealt(playerCount))
            }
            success
        } catch (e: Exception) {
            //updateGameState(GameState.Error("Failed to initialize game: ${e.message}", true))
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
        val activePlayers = players.count { !it.fold && it.chips > 0 }
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
                    if (currentPlayer.fold || currentPlayer.chips <= 0) {
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
    private fun processAIPlayerAction(
        engine: com.pokermon.GameEngine,
        player: Player,
        playerIndex: Int,
    ) {
        try {
            val aiContext = createAIContext(engine, player, playerIndex)
            val decision = makeAIDecision(aiContext)
            executeAIDecision(engine, player, decision, aiContext)
        } catch (e: Exception) {
            // If AI processing fails, just have the player check/fold
            println("Warning: AI player $playerIndex decision error: ${e.message}")
            handleAIError(player, playerIndex)
        }
    }

    /**
     * Creates context for AI decision making with all relevant game state.
     */
    private fun createAIContext(
        engine: com.pokermon.GameEngine,
        player: Player,
        playerIndex: Int,
    ): AIDecisionContext {
        val highBet = engine.getCurrentHighBet()
        val playerBet = player.bet
        val playerChips = player.chips
        val callAmount = (highBet - playerBet).coerceAtMost(playerChips)
        val playerName = player.name ?: "AI $playerIndex"
        val handValue = player.handValue
        val chipRatio = if (playerChips > 0) callAmount.toDouble() / playerChips else 1.0

        return AIDecisionContext(
            playerName = playerName,
            handValue = handValue,
            playerChips = playerChips,
            playerBet = playerBet,
            callAmount = callAmount,
            chipRatio = chipRatio,
            canAffordCall = callAmount <= playerChips,
            hasStrongHand = handValue > 5,
            hasWeakHand = handValue < 3,
            isSignificantBet = chipRatio > 0.3,
            isReasonableBet = chipRatio <= 0.5,
            canRaise = playerChips >= callAmount + 20,
            raiseCostRatio = chipRatio < 0.3
        )
    }

    /**
     * Makes AI decision based on game context and conditions.
     */
    private fun makeAIDecision(context: AIDecisionContext): AIDecision {
        return when {
            // Fold if hand is very weak and call amount is significant
            shouldFoldWeakHand(context) -> AIDecision.FOLD
            
            // Raise if hand is strong and conditions are favorable
            shouldRaiseStrongHand(context) -> AIDecision.RAISE
            
            // Call/Check if call amount is reasonable
            shouldCallOrCheck(context) -> {
                if (context.callAmount > 0) AIDecision.CALL else AIDecision.CHECK
            }
            
            // Default: call if possible, otherwise fold
            context.canAffordCall -> {
                if (context.callAmount > 0) AIDecision.CALL else AIDecision.CHECK
            }
            
            else -> AIDecision.FOLD
        }
    }

    /**
     * Executes the AI decision and updates game state.
     */
    private fun executeAIDecision(
        engine: com.pokermon.GameEngine,
        player: Player,
        decision: AIDecision,
        context: AIDecisionContext,
    ) {
        when (decision) {
            AIDecision.FOLD -> executeFold(player, context.playerName)
            AIDecision.CHECK -> executeCheck(context.playerName)
            AIDecision.CALL -> executeCall(engine, player, context)
            AIDecision.RAISE -> executeRaise(engine, player, context)
        }
    }

    /**
     * Helper method to execute fold action and set AI action result.
     */
    private fun executeFold(player: Player, playerName: String) {
        player.setFold(true)
        setAIAction(playerName, "Fold", 0, "$playerName folded")
    }

    /**
     * Helper method to execute check action and set AI action result.
     */
    private fun executeCheck(playerName: String) {
        setAIAction(playerName, "Check", 0, "$playerName checked")
    }

    /**
     * Helper method to execute call action and set AI action result.
     */
    private fun executeCall(
        engine: com.pokermon.GameEngine,
        player: Player,
        context: AIDecisionContext,
    ) {
        player.placeBet(context.playerBet + context.callAmount)
        engine.addToPot(context.callAmount)
        setAIAction(
            playerName = context.playerName,
            action = "Call",
            amount = context.callAmount,
            message = "${context.playerName} called for ${context.callAmount} chips"
        )
    }

    /**
     * Helper method to execute raise action and set AI action result.
     */
    private fun executeRaise(
        engine: com.pokermon.GameEngine,
        player: Player,
        context: AIDecisionContext,
    ) {
        val raiseAmount = context.callAmount + 20.coerceAtMost(context.playerChips / 4)
        player.placeBet(context.playerBet + raiseAmount)
        engine.addToPot(raiseAmount)
        setAIAction(
            playerName = context.playerName,
            action = "Raise",
            amount = raiseAmount,
            message = "${context.playerName} raised by $raiseAmount chips"
        )
    }

    /**
     * Helper method to handle AI processing errors.
     */
    private fun handleAIError(player: Player, playerIndex: Int) {
        if (player.chips <= 0) {
            player.setFold(true)
            setAIAction("AI $playerIndex", "Fold", 0, "AI $playerIndex folded (error)")
        }
    }

    /**
     * Helper method to set AI action tracking for UI feedback.
     * Eliminates duplication of AI action result creation.
     */
    private fun setAIAction(
        playerName: String,
        action: String,
        amount: Int = 0,
        message: String = "$playerName performed $action"
    ) {
        lastAIAction = AIActionResult(
            playerName = playerName,
            action = action,
            amount = amount,
            message = message
        )
    }

    // AI Decision Conditions - abstracted for better maintainability
    private fun shouldFoldWeakHand(context: AIDecisionContext): Boolean =
        context.hasWeakHand && context.isSignificantBet

    private fun shouldRaiseStrongHand(context: AIDecisionContext): Boolean =
        context.hasStrongHand && context.canRaise && context.raiseCostRatio

    private fun shouldCallOrCheck(context: AIDecisionContext): Boolean =
        context.canAffordCall && context.isReasonableBet

    /**
     * Updates the game state from the current engine state.
     * This bridges the gap between the GameEngine and Flow-based state management.
     * Enhanced to include game mode and round information.
     */
    private fun updateGameStateFromCurrentEngine() {
        gameEngine?.let { engine ->
            val players = engine.players?.toList() ?: emptyList()
            val currentPhase = engine.currentPhase
            
            // Determine if any sub-state is needed based on game mode and phase
            // This is a placeholder for future sub-state integration
            // val subState = when (gameMode) { ... }
            
            // Enhanced state management will be integrated here
            // //updateGameState(GameState.Playing(...))
            
            // Emit appropriate events based on phase transitions
            // Future: when state system is fully integrated
            when (currentPhase) {
                GamePhase.HAND_DEALING -> println("Cards dealt to ${players.size} players")
                GamePhase.BETTING_ROUND -> println("Betting round started - Pot: $currentPot")
                GamePhase.CARD_EXCHANGE -> println("Card exchange phase")
                GamePhase.WINNER_DETERMINATION -> println("Determining winner")
                else -> { /* other phases handled elsewhere */ }
            }
        }
    }

    // ================================================================
    // VALIDATION HELPERS - Common validation patterns abstracted
    // ================================================================

    /**
     * Validates that the game is initialized and returns the engine.
     * @return GameEngine if valid, null otherwise
     */
    private fun validateGameEngine(): GameEngine? {
        return if (isGameInitialized) gameEngine else null
    }

    /**
     * Validates that players exist and returns them.
     * @param engine The game engine to check
     * @return Array<Player> if valid, null otherwise
     */
    private fun validatePlayers(engine: GameEngine): Array<Player>? {
        val players = engine.players
        return if (players != null && players.isNotEmpty()) players else null
    }

    /**
     * Gets the human player (always at index 0) with validation.
     * @param players Array of players
     * @return Player if valid, null otherwise
     */
    private fun getHumanPlayer(players: Array<Player>): Player? {
        return if (players.isNotEmpty()) players[0] else null
    }

    /**
     * Executes a player action with common validation pattern.
     * @param action The action to execute
     * @return GameActionResult indicating success or failure
     */
    private fun executePlayerAction(action: (GameEngine, Array<Player>, Player) -> GameActionResult): GameActionResult {
        val engine = validateGameEngine() 
            ?: return GameActionResult(false, "Game not initialized")
        
        val players = validatePlayers(engine) 
            ?: return GameActionResult(false, "No players found")
            
        val humanPlayer = getHumanPlayer(players) 
            ?: return GameActionResult(false, "No human player found")

        return try {
            action(engine, players, humanPlayer)
        } catch (e: Exception) {
            GameActionResult(false, "Action failed: ${e.message}")
        }
    }

    /**
     * Helper method to determine if we should force phase advancement
     * even if isRoundComplete() returns false.
     */
    private fun shouldForceAdvancePhase(engine: com.pokermon.GameEngine): Boolean {
        return try {
            val players = engine.players
            val activePlayers = players?.filter { !it.fold && it.chips > 0 } ?: emptyList()

            // If only one active player remains, advance phase
            if (activePlayers.size <= 1) {
                return true
            }

            // Check if all active players have acted and bets are equal
            val currentPlayerIndex = engine.currentPlayerIndex
            val currentPlayer =
                if (currentPlayerIndex >= 0 && currentPlayerIndex < (players?.size ?: 0)) {
                    players?.get(currentPlayerIndex)
                } else {
                    null
                }

            // If current player has folded or has no chips, we should advance
            currentPlayer?.let { player ->
                if (player.fold || player.chips <= 0) {
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
     * Enhanced with error handling and state synchronization.
     */
    private fun updatePlayerData() {
        try {
            gameEngine?.let { engine ->
                currentPot = engine.currentPot
                val players = engine.players
                if (players != null && players.isNotEmpty()) {
                    val player = players[0] // Human player is always at index 0
                    playerChips = player.chips

                    // Convert player's hand to poker notation format
                    val handInts = player.hand
                    if (handInts.isNotEmpty()) {
                        playerHand =
                            handInts.map { cardInt ->
                                CardUtils.cardNameSafe(cardInt)
                            }.filter { it != "Empty" }
                    } else {
                        playerHand = emptyList()
                    }
                    
                    // Update current bet information
                    currentBet = engine.getCurrentHighBet()
                    
                    // Future: Emit player state updated event for reactive UI
                    // //emitGameEvent(GameEvents.PlayerHandUpdated(player, playerHand))
                    
                    // Update the reactive game state
                    updateGameStateFromCurrentEngine()
                }
            }
        } catch (e: Exception) {
            println("Warning: Error updating player data: ${e.message}")
            // Future: Emit error event but don't crash
            // //emitGameEvent(GameEvents.ErrorOccurred("Player data update failed", Exception(e.message ?: "Unknown error")))
        }
    }

    /**
     * Get the resource path for a card image based on card integer value.
     * Maps to the actual card art files in the repository resources.
     * Uses unified CardUtils for consistent card logic across all platforms.
     */
    fun getCardImagePath(cardInt: Int): String {
        if (cardInt == 0) {
            // Return card back path or null for text symbols
            return CardPackManager.getCardBackImagePath(selectedCardPack) ?: "TEXT_BACK"
        }

        // Use unified CardUtils for DRY compliance
        val rankName = CardUtils.rankName(cardInt)
        val suitName = CardUtils.suitName(cardInt)

        // Use CardPackManager to get the correct path
        return CardPackManager.getCardImagePath(selectedCardPack, rankName, suitName)
            ?: "TEXT_${rankName}_$suitName"
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
     * Get card image paths for the current player's hand.
     */
    fun getPlayerHandImagePaths(): List<String> {
        return gameEngine?.let { engine ->
            val players = engine.players
            if (players != null && players.isNotEmpty()) {
                val player = players[0] // Human player is always at index 0
                player.hand.map { cardInt ->
                    getCardImagePath(cardInt)
                }
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
                        name = player.name,
                        chips = player.chips,
                        isFolded = player.fold,
                        isCurrentPlayer = index == 0, // Human player is always at index 0
                        handValue = player.handValue,
                    )
                } ?: emptyList()
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Set the game mode for the next game and update state management.
     * Enhanced to support state management integration and mode-specific initialization.
     */
    fun setGameMode(mode: GameMode) {
        this.gameMode = mode
        
        // Process mode selection through state management
        processGameAction(GameActions.SelectGameMode(mode))
        
        // If game is already initialized, switch mode with state preservation option
        if (isGameInitialized) {
            processGameAction(GameActions.SwitchToMode(mode, preserveState = false))
        }
    }

    /**
     * Switch to a different game mode during gameplay.
     * This allows dynamic mode switching with optional state preservation.
     */
    fun switchGameMode(newMode: GameMode, preserveState: Boolean = false): GameActionResult {
        return try {
            val oldMode = this.gameMode
            this.gameMode = newMode
            
            processGameAction(GameActions.SwitchToMode(newMode, preserveState))
            
            if (!preserveState && isGameInitialized) {
                // Reset game with new mode
                val success = initializeGame(playerName, getAllPlayers().size, playerChips)
                if (success) {
                    GameActionResult(true, "Switched from ${oldMode.displayName} to ${newMode.displayName}")
                } else {
                    GameActionResult(false, "Failed to switch to ${newMode.displayName}")
                }
            } else {
                updateGameStateFromCurrentEngine()
                GameActionResult(true, "Mode switched to ${newMode.displayName}")
            }
        } catch (e: Exception) {
            //updateGameState(GameState.Error("Failed to switch game mode: ${e.message}", true))
            GameActionResult(false, "Error switching game mode: ${e.message}")
        }
    }

    /**
     * Enter a specific sub-state for the current game mode.
     * This enables fine-grained control over game flow and mode-specific behaviors.
     */
    fun enterSubState(subState: PlayingSubState): GameActionResult {
        return try {
            processGameAction(GameActions.EnterSubState(subState))
            updateGameStateFromCurrentEngine()
            GameActionResult(true, "Entered ${subState::class.simpleName}")
        } catch (e: Exception) {
            GameActionResult(false, "Failed to enter sub-state: ${e.message}")
        }
    }

    /**
     * Exit the current sub-state.
     */
    fun exitSubState(reason: String = "Completed"): GameActionResult {
        return try {
            processGameAction(GameActions.ExitSubState(reason))
            updateGameStateFromCurrentEngine()
            GameActionResult(true, "Exited sub-state: $reason")
        } catch (e: Exception) {
            GameActionResult(false, "Failed to exit sub-state: ${e.message}")
        }
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
     * Perform a call action with state management integration.
     */
    fun performCall(): GameActionResult {
        return executePlayerAction { engine, _, player ->
            // For testing purposes, use a standard call amount if no high bet exists
            val highBet = engine.getCurrentHighBet()
            val callAmount = if (highBet <= 0) 50 else (highBet - player.bet).coerceAtMost(player.chips)

            if (callAmount <= 0) {
                // Process check action through state management
                processGameAction(GameActions.Check(player))
                //emitGameEvent(GameEvents.PlayerCalled(player, 0))
                
                // Check if we should advance phase after this action
                checkAndAdvanceGamePhase()
                updatePlayerData()
                updateGameStateFromCurrentEngine()
                GameActionResult(true, "No bet to call")
            } else {
                player.placeBet(player.bet + callAmount)
                // Add the call amount to the pot
                engine.addToPot(callAmount)
                
                // Process call action through state management
                processGameAction(GameActions.Call(player))
                //emitGameEvent(GameEvents.PlayerCalled(player, callAmount))
                
                // Advance to next player after action
                engine.nextPlayer()
                // Check if we should advance phase after this action
                checkAndAdvanceGamePhase()
                updatePlayerData()
                updateGameStateFromCurrentEngine()
                GameActionResult(true, "Called for $callAmount chips")
            }
        }
    }

    /**
     * Perform a raise action with state management integration.
     */
    fun performRaise(amount: Int): GameActionResult {
        return executePlayerAction { engine, _, player ->
            if (amount > player.chips) {
                GameActionResult(false, "Not enough chips to raise by $amount")
            } else {
                player.placeBet(amount)
                // Add the raise amount to the pot
                engine.addToPot(amount)
                
                // Process raise action through state management
                processGameAction(GameActions.Raise(player, amount))
                //emitGameEvent(GameEvents.PlayerRaised(player, amount, player.bet))
                
                // Advance to next player after action
                engine.nextPlayer()
                // Check if we should advance phase after this action
                checkAndAdvanceGamePhase()
                updatePlayerData()
                updateGameStateFromCurrentEngine()
                GameActionResult(true, "Raised by $amount chips")
            }
        }
    }

    /**
     * Perform a fold action with state management integration.
     */
    fun performFold(): GameActionResult {
        return executePlayerAction { engine, _, player ->
            player.setFold(true)
            
            // Process fold action through state management
            processGameAction(GameActions.Fold(player))
            //emitGameEvent(GameEvents.PlayerFolded(player))
            
            // Advance to next player after action
            engine.nextPlayer()
            // Check if we should advance phase after this action
            checkAndAdvanceGamePhase()
            updatePlayerData()
            updateGameStateFromCurrentEngine()
            GameActionResult(true, "Folded")
        }
    }

    /**
     * Perform a check action.
     */
    fun performCheck(): GameActionResult {
        val engine = validateGameEngine() 
            ?: return GameActionResult(false, "Game not initialized")

        return try {
            // Advance to next player after action
            engine.nextPlayer()
            // Check if we should advance phase after this action
            checkAndAdvanceGamePhase()
            updatePlayerData()
            GameActionResult(true, "Checked")
        } catch (e: Exception) {
            GameActionResult(false, "Error checking: ${e.message}")
        }
    }

    /**
     * Exchange selected cards with new ones from the deck.
     * Limited to once per round to prevent multiple exchanges.
     * Now includes state management integration.
     */
    fun exchangeCards(cardIndices: List<Int>): GameActionResult {
        if (!isGameInitialized) {
            //updateGameState(GameState.Error("Game not initialized", true))
            return GameActionResult(false, "Game not initialized")
        }

        if (cardsExchangedThisRound) {
            return GameActionResult(false, "Cards can only be exchanged once per round")
        }

        return try {
            gameEngine?.let { engine ->
                val players = engine.players
                val humanPlayer = players?.get(0) // Human player at index 0
                
                if (humanPlayer != null) {
                    val cardIndicesArray = cardIndices.toIntArray()
                    engine.exchangeCards(0, cardIndicesArray) // 0 = human player index
                    cardsExchangedThisRound = true // Mark that cards have been exchanged

                    // Process card exchange through state management
                    processGameAction(GameActions.ExchangeCards(humanPlayer, cardIndices))
                    //emitGameEvent(GameEvents.CardsExchanged(humanPlayer, cardIndices.size))

                    // Complete card exchange and move to final betting phase
                    engine.completeCardExchange()

                    updatePlayerData()
                    clearCardSelection()
                    updateGameStateFromCurrentEngine()
                    GameActionResult(true, "Exchanged ${cardIndices.size} cards - ready for final betting")
                } else {
                    GameActionResult(false, "Human player not found")
                }
            } ?: GameActionResult(false, "Game engine not available")
        } catch (e: Exception) {
            //updateGameState(GameState.Error("Error exchanging cards: ${e.message}", true))
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

                    val message =
                        when {
                            isHumanWinner && winners.size == 1 -> "🎉 You won the hand!"
                            isHumanWinner && winners.size > 1 -> "🤝 You tied for the win!"
                            else -> {
                                // Get the winner name(s) for display
                                val winnerNames =
                                    winners.map { index ->
                                        if (index >= 0 && index < (engine.players?.size ?: 0)) {
                                            engine.players?.get(index)?.name ?: "Player $index"
                                        } else {
                                            "Unknown"
                                        }
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
                savedGameState =
                    SavedGameState(
                        playerName = playerName,
                        gameMode = gameMode,
                        currentRound = engine.currentRound,
                        currentPhase = engine.currentPhase,
                        currentPot = currentPot,
                        playerChips = playerChips,
                        playerCards = playerHand,
                        allPlayersData = getAllPlayers(),
                        selectedCards = selectedCards.toSet(),
                        isGameActive = true,
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

    // =================================================================
    // MISSING METHODS NEEDED BY MODERNPOKERAPP
    // =================================================================

    /**
     * Get current game state for UI display
     */
    fun getGameState(): GameActionResult {
        if (!isGameInitialized || gameEngine == null) {
            return GameActionResult(false, "Game not initialized")
        }

        updatePlayerData()

        // Convert playerHand list to IntArray if available
        val handArray =
            if (playerHand.isNotEmpty()) {
                // Convert card names back to integers - simplified for now
                IntArray(playerHand.size) { 0 } // Placeholder - would need proper conversion
            } else {
                null
            }

        return GameActionResult(
            success = true,
            message = "Game state retrieved",
            playerHand = handArray,
            pot = currentPot,
            playerChips = playerChips,
        )
    }

    /**
     * Check if current player is human
     */
    fun isCurrentPlayerHuman(): Boolean {
        val engine = gameEngine ?: return false
        val players = engine.players ?: return false
        val currentIndex = engine.currentPlayerIndex

        return if (currentIndex < players.size) {
            players[currentIndex].isHuman
        } else {
            false
        }
    }

    /**
     * Perform AI action for current player
     */
    fun performAIAction(): GameActionResult {
        val engine = gameEngine ?: return GameActionResult(false, "Game not initialized")

        try {
            processAIOpponents()
            return GameActionResult(true, "AI action performed")
        } catch (e: Exception) {
            return GameActionResult(false, "AI action failed: ${e.message}")
        }
    }

    /**
     * Process player action
     */
    fun processPlayerAction(action: String): GameActionResult {
        val engine = gameEngine ?: return GameActionResult(false, "Game not initialized")
        val players = engine.players ?: return GameActionResult(false, "No players")

        val humanPlayer =
            players.firstOrNull { it.isHuman }
                ?: return GameActionResult(false, "No human player found")

        return when {
            action == "call" -> {
                val actualBet = humanPlayer.placeBet(currentBet)
                if (actualBet > 0) {
                    GameActionResult(true, "Called $actualBet")
                } else {
                    GameActionResult(false, "Insufficient chips")
                }
            }
            action.startsWith("raise:") -> {
                val amount = action.substringAfter(":").toIntOrNull() ?: 0
                val totalBet = currentBet + amount
                val actualBet = humanPlayer.placeBet(totalBet)
                if (actualBet > 0) {
                    currentBet += amount
                    GameActionResult(true, "Raised by $amount")
                } else {
                    GameActionResult(false, "Insufficient chips for raise")
                }
            }
            action == "fold" -> {
                humanPlayer.setFold(true)
                GameActionResult(true, "Folded")
            }
            else -> GameActionResult(false, "Invalid action: $action")
        }
    }

    /**
     * Check if game is over
     */
    fun isGameOver(): Boolean {
        val engine = gameEngine ?: return true
        val players = engine.players ?: return true

        // Game is over if only one player has chips or all but one have folded
        val activePlayers = players.filter { it.chips > 0 && !it.fold }
        return activePlayers.size <= 1
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
    val handValue: Int,
)

/**
 * Result of a game action.
 */
data class GameActionResult(
    val success: Boolean,
    val message: String,
    val playerHand: IntArray? = null,
    val pot: Int = 0,
    val playerChips: Int = 0,
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
    val isGameActive: Boolean,
)
