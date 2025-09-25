package com.pokermon.ui

import com.pokermon.*
import com.pokermon.GameFlows.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

/**
 * Desktop UI application logic for Pokermon.
 * Provides the main application framework for desktop platforms.
 * Enhanced with reactive state management and mode-specific UI handling.
 *
 * @author Pokermon Desktop System
 * @version 1.2.0 - Enhanced with state management integration
 */
class PokerApp {
    private val stateManager = GameStateManager()
    private val uiScope = CoroutineScope(Dispatchers.Main.immediate)

    /**
     * Starts the desktop application with reactive state management.
     */
    fun start() =
        runBlocking {
            println("🎮 Pokermon Desktop Application Starting...")
            println("📱 State Management: ENABLED")
            println("🎯 Reactive UI: READY")

            // Initialize application state
            stateManager.processAction(GameActions.StartGame)

            // Set up state observers for reactive UI updates
            setupStateObservers()

            // For now, fall back to console interface with state integration
            // TODO: Implement proper desktop GUI with reactive components
            println("Desktop GUI not yet implemented. Starting console mode with state management...")

            val consoleGame = com.pokermon.console.ConsoleGame()
            consoleGame.start()
        }

    /**
     * Sets up reactive observers for game state changes.
     * This enables automatic UI updates when game state changes occur.
     */
    private suspend fun setupStateObservers() {
        // Launch coroutines to observe state changes
        uiScope.launch {
            stateManager.gameState.collect { state ->
                handleStateChange(state)
            }
        }

        uiScope.launch {
            stateManager.gameEvents.collect { event ->
                handleGameEvent(event)
            }
        }

        println("✅ State observers configured - UI will respond to all game state changes")
    }

    /**
     * Handles game state changes and updates UI accordingly.
     * This is where UI components would be updated in a full GUI implementation.
     */
    private fun handleStateChange(state: GameState) {
        when (state) {
            is GameState.Initializing -> {
                updateUI("Initializing game...", "LOADING")
            }
            is GameState.Playing -> {
                val mode = state.gameMode.displayName
                val phase = state.currentPhase.displayName
                val pot = state.pot
                val round = state.roundNumber
                
                updateUI("Playing: $mode - $phase", "ACTIVE")
                updateUI("Round: $round, Pot: $pot", "INFO")
                
                // Handle sub-states for mode-specific UI
                state.subState?.let { subState ->
                    handleSubStateUI(subState)
                }
            }
            is GameState.WaitingForInput -> {
                updateUI("Waiting: ${state.message}", "INPUT_REQUIRED")
                if (state.validOptions.isNotEmpty()) {
                    updateUI("Options: ${state.validOptions.joinToString(", ")}", "OPTIONS")
                }
            }
            is GameState.GameOver -> {
                val winner = state.winner?.name ?: "No winner"
                val mode = state.gameMode.displayName
                updateUI("Game Over: $winner wins! ($mode)", "GAME_OVER")
            }
            is GameState.Paused -> {
                updateUI("Game Paused: ${state.pauseReason}", "PAUSED")
            }
            is GameState.Error -> {
                val recovery = if (state.recoverable) " (Recoverable)" else " (Fatal)"
                updateUI("Error: ${state.message}$recovery", "ERROR")
            }
        }
    }

    /**
     * Handles game events for UI notifications and updates.
     */
    private fun handleGameEvent(event: GameEvents) {
        when (event) {
            is GameEvents.GameStarted -> {
                showNotification("🎮 Game Started!", "SUCCESS")
            }
            is GameEvents.GameModeSelected -> {
                showNotification("🎯 Mode Selected: ${event.mode.displayName}", "INFO")
            }
            is GameEvents.PlayerJoined -> {
                showNotification("👤 ${event.player.name} joined", "INFO")
            }
            is GameEvents.PlayerFolded -> {
                showNotification("❌ ${event.player.name} folded", "WARNING")
            }
            is GameEvents.PlayerRaised -> {
                showNotification("📈 ${event.player.name} raised ${event.amount}", "ACTION")
            }
            is GameEvents.CardsExchanged -> {
                showNotification("🔄 ${event.player.name} exchanged ${event.exchangedCount} cards", "ACTION")
            }
            is GameEvents.PhaseChanged -> {
                showNotification("⏩ Phase: ${event.newPhase.displayName}", "PHASE")
            }
            is GameEvents.SubStateEntered -> {
                handleSubStateEvent("Entered", event.subState)
            }
            is GameEvents.SubStateExited -> {
                handleSubStateEvent("Exited", event.subState)
            }
            
            // Mode-specific event handling
            is GameEvents.AdventureEvents.MonsterEncountered -> {
                showNotification("🐲 Monster Encountered: ${event.monsterName}", "ADVENTURE")
            }
            is GameEvents.SafariEvents.WildMonsterSighted -> {
                showNotification("👀 Wild ${event.monsterName} appeared!", "SAFARI")
            }
            is GameEvents.IronmanEvents.PermadeathTriggered -> {
                showNotification("💀 PERMADEATH: ${event.player.name}", "IRONMAN_CRITICAL")
            }
            
            else -> {
                // Handle other events as needed
                println("Event: ${event::class.simpleName}")
            }
        }
    }

    /**
     * Updates sub-state specific UI elements.
     */
    private fun handleSubStateUI(subState: PlayingSubState) {
        when (subState) {
            is PlayingSubState.WaitingForPlayerAction -> {
                updateUI("Your turn: ${subState.validActions.joinToString("/")}", "PLAYER_ACTION")
            }
            is PlayingSubState.ProcessingAI -> {
                updateUI("AI thinking... (${subState.aiPlayers.size} players)", "AI_PROCESSING")
            }
            is PlayingSubState.CardExchangePhase -> {
                updateUI("Card Exchange: ${subState.exchangesRemaining} remaining", "CARD_EXCHANGE")
            }
            is PlayingSubState.AdventureMode -> {
                updateUI("🐲 ${subState.currentMonster}: ${subState.monsterHealth} HP", "ADVENTURE")
            }
            is PlayingSubState.SafariMode -> {
                updateUI("🎯 ${subState.wildMonster} - Balls: ${subState.safariBallsRemaining}", "SAFARI")
            }
            is PlayingSubState.IronmanMode -> {
                val warning = if (subState.permadeathWarning) " ⚠️ DANGER!" else ""
                updateUI("💰 Gacha: ${subState.gachaPoints}pts - Risk: ${subState.riskLevel}x$warning", "IRONMAN")
            }
            is PlayingSubState.ShowingResults -> {
                updateUI("📊 Results: ${subState.handResults.size} players", "RESULTS")
            }
        }
    }

    /**
     * Handles sub-state specific events.
     */
    private fun handleSubStateEvent(action: String, subState: PlayingSubState) {
        val message = when (subState) {
            is PlayingSubState.AdventureMode -> "$action Adventure: ${subState.currentMonster}"
            is PlayingSubState.SafariMode -> "$action Safari: ${subState.wildMonster}"
            is PlayingSubState.IronmanMode -> "$action Ironman: Risk ${subState.riskLevel}x"
            else -> "$action: ${subState::class.simpleName}"
        }
        showNotification(message, "SUBSTATE")
    }

    /**
     * Updates the UI with new information (placeholder for actual GUI).
     * In a real GUI implementation, this would update specific UI components.
     */
    private fun updateUI(message: String, category: String) {
        val timestamp = System.currentTimeMillis()
        val prefix = when (category) {
            "LOADING" -> "⏳"
            "ACTIVE" -> "🎮"
            "INFO" -> "ℹ️"
            "INPUT_REQUIRED" -> "⌨️"
            "OPTIONS" -> "📋"
            "GAME_OVER" -> "🏁"
            "PAUSED" -> "⏸️"
            "ERROR" -> "❌"
            "PLAYER_ACTION" -> "👤"
            "AI_PROCESSING" -> "🤖"
            "CARD_EXCHANGE" -> "🔄"
            "ADVENTURE" -> "🐲"
            "SAFARI" -> "🏞️"
            "IRONMAN" -> "💀"
            "RESULTS" -> "📊"
            "PHASE" -> "⚡"
            else -> "📢"
        }
        
        // In a real GUI, this would update actual UI components
        // For now, we demonstrate the reactive pattern with console output
        println("[$prefix UI UPDATE] $message")
    }

    /**
     * Shows a notification to the user (placeholder for actual GUI notifications).
     */
    private fun showNotification(message: String, type: String) {
        val icon = when (type) {
            "SUCCESS" -> "✅"
            "WARNING" -> "⚠️"
            "ERROR" -> "❌"
            "ACTION" -> "⚡"
            "ADVENTURE" -> "🐲"
            "SAFARI" -> "🎯"
            "IRONMAN_CRITICAL" -> "💀"
            "SUBSTATE" -> "🔄"
            else -> "📢"
        }
        
        // In a real GUI, this would show actual notifications
        println("[$icon NOTIFICATION] $message")
    }

    /**
     * Initializes the desktop UI components (future GUI implementation).
     */
    private fun initializeUI() {
        // TODO: Initialize desktop UI components with reactive bindings
        // This will include:
        // - Main window setup with state-driven updates
        // - Game board visualization that responds to GameState.Playing
        // - Player hand display that updates on CardsDealt events
        // - Betting interface that adapts to current GamePhase
        // - Mode-specific UI panels that respond to sub-states
        // - Notification system for GameEvents
        // - Settings panels with mode selection
        // - Real-time pot and chip counters
        // - Progress indicators for AI processing
        // - Modal dialogs for game mode transitions
    }

    /**
     * Exposes the state manager for external integration.
     */
    fun getStateManager(): GameStateManager = stateManager
}
