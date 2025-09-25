package com.pokermon.GameFlows

import com.pokermon.Game
import com.pokermon.GamePhase
import com.pokermon.GameMode
import com.pokermon.players.Player

/**
 * Sealed class for game states using modern Kotlin patterns.
 * Provides type-safe game state transitions and result handling with support for sub-states.
 * Enhanced to support modular game modes and detailed state tracking.
 *
 * @author Pokermon Flow System
 * @version 1.2.0 - Enhanced with sub-states and mode-specific data
 */
sealed class GameState {
    object Initializing : GameState()

    data class Playing(
        val players: List<Player>,
        val currentPhase: GamePhase,
        val pot: Int = 0,
        val currentBet: Int = 0,
        val activePlayerIndex: Int = 0,
        val gameMode: GameMode = GameMode.CLASSIC,
        val roundNumber: Int = 1,
        val subState: PlayingSubState? = null
    ) : GameState()

    data class Paused(
        val savedState: Game,
        val pauseReason: String = "User requested"
    ) : GameState()

    data class WaitingForInput(
        val message: String,
        val validOptions: List<String> = emptyList(),
        val targetPlayer: Player? = null,
        val timeoutMs: Long? = null
    ) : GameState()

    data class GameOver(
        val winner: Player?,
        val finalScores: Map<String, Int>,
        val sessionStats: Map<String, Any>,
        val gameMode: GameMode = GameMode.CLASSIC,
        val totalRounds: Int = 1
    ) : GameState()

    data class Error(
        val message: String,
        val recoverable: Boolean = true,
        val errorCode: String? = null
    ) : GameState()
}

/**
 * Sub-states for the Playing state to provide more granular control.
 * Enables mode-specific behaviors and UI adaptations.
 */
sealed class PlayingSubState {
    // Betting sub-states
    data class WaitingForPlayerAction(
        val player: Player,
        val validActions: List<String> = listOf("call", "raise", "fold"),
        val minimumBet: Int = 0,
        val maximumBet: Int = Int.MAX_VALUE
    ) : PlayingSubState()
    
    data class ProcessingAI(val aiPlayers: List<Player>) : PlayingSubState()
    
    // Card exchange sub-states  
    data class CardExchangePhase(
        val player: Player,
        val maxExchanges: Int = 3,
        val exchangesRemaining: Int = 1
    ) : PlayingSubState()
    
    // Mode-specific sub-states
    data class AdventureMode(
        val currentMonster: String,
        val monsterHealth: Int,
        val questProgress: Map<String, Int> = emptyMap()
    ) : PlayingSubState()
    
    data class SafariMode(
        val wildMonster: String,
        val captureChance: Double,
        val safariBallsRemaining: Int
    ) : PlayingSubState()
    
    data class IronmanMode(
        val gachaPoints: Int,
        val riskLevel: Double,
        val permadeathWarning: Boolean = false
    ) : PlayingSubState()
    
    // Result sub-states
    data class ShowingResults(
        val handResults: Map<Player, String>,
        val winnings: Map<Player, Int>
    ) : PlayingSubState()
}
