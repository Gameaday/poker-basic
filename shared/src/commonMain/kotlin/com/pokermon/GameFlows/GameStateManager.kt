package com.pokermon.GameFlows

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.pokermon.players.Player
import com.pokermon.Game
import com.pokermon.GameMode

/**
 * Flow-based game state manager for modern reactive patterns.
 * Manages game state transitions and event handling using Kotlin coroutines and Flow.
 * 
 * @author Pokermon Flow System
 * @version 1.1.0
 */
class GameStateManager {
    
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _gameEvents = MutableSharedFlow<GameEvents>()
    val gameEvents: SharedFlow<GameEvents> = _gameEvents.asSharedFlow()
    
    private val _gameActions = MutableSharedFlow<GameActions>()
    val gameActions: SharedFlow<GameActions> = _gameActions.asSharedFlow()
    
    /**
     * Updates the current game state and emits the change to all subscribers.
     */
    suspend fun updateGameState(newState: GameState) {
        _gameState.emit(newState)
    }
    
    /**
     * Emits a game event to all subscribers.
     */
    suspend fun emitEvent(event: GameEvents) {
        _gameEvents.emit(event)
    }
    
    /**
     * Processes a game action and potentially updates state.
     */
    suspend fun processAction(action: GameActions) {
        _gameActions.emit(action)
        
        // Process the action and update state accordingly
        when (action) {
            is GameActions.StartGame -> {
                updateGameState(GameState.Initializing)
                emitEvent(GameEvents.GameStarted)
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
                            sessionStats = generateSessionStats(currentState)
                        )
                    )
                    emitEvent(GameEvents.GameEnded(winner, System.currentTimeMillis()))
                }
            }
            
            is GameActions.PauseGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val savedGame = Game(
                        gameMode = GameMode.CLASSIC // TODO: Get actual game mode
                    )
                    updateGameState(GameState.Paused(savedGame))
                }
            }
            
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
     * Resets the game state to initializing.
     */
    suspend fun resetGame() {
        updateGameState(GameState.Initializing)
    }
    
    private fun determineWinner(players: List<Player>): Player? {
        return players.maxByOrNull { it.chips }
    }
    
    private fun generateSessionStats(state: GameState.Playing): Map<String, Any> {
        return mapOf(
            "totalPlayers" to state.players.size,
            "totalPot" to state.pot,
            "currentPhase" to state.currentPhase.name
        )
    }
}