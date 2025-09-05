package com.pokermon.GameFlows

import com.pokermon.Game
import com.pokermon.GamePhase
import com.pokermon.players.Player

/**
 * Sealed class for game states using modern Kotlin patterns.
 * Provides type-safe game state transitions and result handling.
 * 
 * @author Pokermon Flow System
 * @version 1.1.0
 */
sealed class GameState {
    object Initializing : GameState()
    
    data class Playing(
        val players: List<Player>, 
        val currentPhase: GamePhase,
        val pot: Int = 0,
        val currentBet: Int = 0,
        val activePlayerIndex: Int = 0
    ) : GameState()
    
    data class Paused(val savedState: Game) : GameState()
    
    data class WaitingForInput(
        val message: String,
        val validOptions: List<String> = emptyList()
    ) : GameState()
    
    data class GameOver(
        val winner: Player?,
        val finalScores: Map<String, Int>,
        val sessionStats: Map<String, Any>
    ) : GameState()
    
    data class Error(
        val message: String,
        val recoverable: Boolean = true
    ) : GameState()
}