package com.pokermon.GameFlows

import com.pokermon.Game
import com.pokermon.GameMode
import com.pokermon.players.Player
import kotlinx.coroutines.flow.*

/**
 * Flow-based game state manager for modern reactive patterns.
 * Manages game state transitions and event handling using Kotlin coroutines and Flow.
 * Enhanced to support modular game modes, sub-states, and mode-specific behaviors.
 *
 * @author Pokermon Flow System
 * @version 1.2.0 - Enhanced with sub-state support and mode-specific handling
 */
class GameStateManager {
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameEvents = MutableSharedFlow<GameEvents>()
    val gameEvents: SharedFlow<GameEvents> = _gameEvents.asSharedFlow()

    private val _gameActions = MutableSharedFlow<GameActions>()
    val gameActions: SharedFlow<GameActions> = _gameActions.asSharedFlow()

    // Track current game mode for mode-specific behavior
    private var currentGameMode: GameMode = GameMode.CLASSIC

    /**
     * Updates the current game state and emits the change to all subscribers.
     */
    suspend fun updateGameState(newState: GameState) {
        val previousState = _gameState.value
        _gameState.emit(newState)
        
        // Emit phase change events when transitioning between Playing states
        if (previousState is GameState.Playing && newState is GameState.Playing &&
            previousState.currentPhase != newState.currentPhase) {
            emitEvent(GameEvents.PhaseChanged(newState.currentPhase, previousState.currentPhase))
        }
        
        // Emit sub-state transition events
        handleSubStateTransitions(previousState, newState)
    }

    /**
     * Emits a game event to all subscribers.
     */
    suspend fun emitEvent(event: GameEvents) {
        _gameEvents.emit(event)
    }

    /**
     * Processes a game action and potentially updates state.
     * Enhanced to handle mode-specific actions and sub-state transitions.
     */
    suspend fun processAction(action: GameActions) {
        _gameActions.emit(action)

        // Process the action and update state accordingly
        when (action) {
            is GameActions.StartGame -> {
                updateGameState(GameState.Initializing)
                emitEvent(GameEvents.GameStarted)
            }

            is GameActions.SelectGameMode -> {
                currentGameMode = action.mode
                emitEvent(GameEvents.GameModeSelected(action.mode))
            }

            is GameActions.SwitchToMode -> {
                val previousMode = currentGameMode
                currentGameMode = action.mode
                emitEvent(GameEvents.GameModeSwitched(previousMode, action.mode))
                
                if (!action.preserveState) {
                    updateGameState(GameState.Initializing)
                }
            }

            is GameActions.JoinGame -> {
                emitEvent(GameEvents.PlayerJoined(action.player))
            }

            is GameActions.EndGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val winner = determineWinner(currentState.players)
                    updateGameState(
                        GameState.GameOver(
                            winner = winner,
                            finalScores = currentState.players.associate { it.name to it.chips },
                            sessionStats = generateSessionStats(currentState),
                            gameMode = currentState.gameMode,
                            totalRounds = currentState.roundNumber
                        ),
                    )
                    emitEvent(GameEvents.GameEnded(winner, System.currentTimeMillis()))
                }
            }

            is GameActions.PauseGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val savedGame = Game(gameMode = currentState.gameMode)
                    updateGameState(GameState.Paused(savedGame, "User requested"))
                    emitEvent(GameEvents.GamePaused("User requested"))
                }
            }

            // Handle betting actions
            is GameActions.Call -> {
                emitEvent(GameEvents.PlayerCalled(action.player, 0)) // Amount will be set by bridge
            }

            is GameActions.Raise -> {
                emitEvent(GameEvents.PlayerRaised(action.player, action.amount, action.player.bet + action.amount))
            }

            is GameActions.Fold -> {
                emitEvent(GameEvents.PlayerFolded(action.player))
            }

            is GameActions.Check -> {
                emitEvent(GameEvents.PlayerChecked(action.player))
            }

            is GameActions.ExchangeCards -> {
                emitEvent(GameEvents.CardsExchanged(action.player, action.cardIndices.size))
            }

            // Handle sub-state actions
            is GameActions.EnterSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGameState(currentState.copy(subState = action.subState))
                    emitEvent(GameEvents.SubStateEntered(action.subState))
                }
            }

            is GameActions.ExitSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing && currentState.subState != null) {
                    val exitedSubState = currentState.subState
                    updateGameState(currentState.copy(subState = null))
                    emitEvent(GameEvents.SubStateExited(exitedSubState, action.reason))
                }
            }

            is GameActions.TransitionSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGameState(currentState.copy(subState = action.to))
                    emitEvent(GameEvents.SubStateTransition(action.from, action.to))
                }
            }

            // Handle mode-specific actions
            is GameActions.AdventureActions -> handleAdventureAction(action)
            is GameActions.SafariActions -> handleSafariAction(action)
            is GameActions.IronmanActions -> handleIronmanAction(action)

            else -> {
                // Handle other actions as needed
            }
        }
    }

    /**
     * Gets the current game state synchronously.
     */
    fun getCurrentState(): GameState = _gameState.value

    /**
     * Gets the current game mode.
     */
    fun getCurrentMode(): GameMode = currentGameMode

    /**
     * Resets the game state to initializing.
     */
    suspend fun resetGame() {
        updateGameState(GameState.Initializing)
    }

    /**
     * Handles sub-state transitions and emits appropriate events.
     */
    private suspend fun handleSubStateTransitions(previousState: GameState, newState: GameState) {
        if (previousState is GameState.Playing && newState is GameState.Playing) {
            val prevSubState = previousState.subState
            val newSubState = newState.subState
            
            when {
                prevSubState == null && newSubState != null -> {
                    emitEvent(GameEvents.SubStateEntered(newSubState))
                }
                prevSubState != null && newSubState == null -> {
                    emitEvent(GameEvents.SubStateExited(prevSubState, "State transition"))
                }
                prevSubState != null && newSubState != null && prevSubState != newSubState -> {
                    emitEvent(GameEvents.SubStateTransition(prevSubState, newSubState))
                }
            }
        }
    }

    /**
     * Handles adventure mode specific actions.
     */
    private suspend fun handleAdventureAction(action: GameActions.AdventureActions) {
        when (action) {
            is GameActions.AdventureActions.EncounterMonster -> {
                emitEvent(GameEvents.AdventureEvents.MonsterEncountered(action.monsterName, action.monsterHealth))
            }
            is GameActions.AdventureActions.AttackMonster -> {
                emitEvent(GameEvents.AdventureEvents.MonsterAttacked(action.player, action.damage, 0))
            }
            is GameActions.AdventureActions.CompleteQuest -> {
                emitEvent(GameEvents.AdventureEvents.QuestCompleted(action.questName, Player(), action.reward))
            }
        }
    }

    /**
     * Handles safari mode specific actions.
     */
    private suspend fun handleSafariAction(action: GameActions.SafariActions) {
        when (action) {
            is GameActions.SafariActions.EncounterWildMonster -> {
                emitEvent(GameEvents.SafariEvents.WildMonsterSighted(action.monsterName))
            }
            is GameActions.SafariActions.ThrowSafariBall -> {
                emitEvent(GameEvents.SafariEvents.SafariBallThrown(action.player, 0))
            }
            is GameActions.SafariActions.AttemptCapture -> {
                // Simulate capture logic
                val success = action.captureChance > 0.5
                if (success) {
                    emitEvent(GameEvents.SafariEvents.MonsterCaptured(action.player, "Unknown"))
                } else {
                    emitEvent(GameEvents.SafariEvents.MonsterEscaped("Unknown", "Failed capture"))
                }
            }
        }
    }

    /**
     * Handles ironman mode specific actions.
     */
    private suspend fun handleIronmanAction(action: GameActions.IronmanActions) {
        when (action) {
            is GameActions.IronmanActions.ConvertToGachaPoints -> {
                emitEvent(GameEvents.IronmanEvents.ChipsConverted(action.chips, action.points))
            }
            is GameActions.IronmanActions.PerformGachaPull -> {
                emitEvent(GameEvents.IronmanEvents.GachaPullPerformed(action.player, action.pointsSpent, "Common Monster"))
            }
            is GameActions.IronmanActions.TriggerPermadeath -> {
                emitEvent(GameEvents.IronmanEvents.PermadeathTriggered(action.player, "Health reached zero"))
            }
        }
    }

    private fun determineWinner(players: List<Player>): Player? {
        return players.maxByOrNull { it.chips }
    }

    private fun generateSessionStats(state: GameState.Playing): Map<String, Any> {
        return mapOf(
            "totalPlayers" to state.players.size,
            "totalPot" to state.pot,
            "currentPhase" to state.currentPhase.name,
            "gameMode" to state.gameMode.name,
            "roundNumber" to state.roundNumber,
            "hasSubState" to (state.subState != null)
        )
    }
}
