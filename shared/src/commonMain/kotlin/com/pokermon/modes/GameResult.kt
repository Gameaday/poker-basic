package com.pokermon.modes

import com.pokermon.players.Player
import com.pokermon.HandEvaluator
import com.pokermon.GamePhase

/**
 * Result of a poker round
 */
data class RoundResult(
    val winner: Player?,
    val potWon: Int,
    val gameEnded: Boolean = false,
    val handResults: List<PlayerHandResult>? = null
)

/**
 * Player hand evaluation result
 */
data class PlayerHandResult(
    val player: Player,
    val evaluation: HandEvaluator.HandResult
)

/**
 * Represents the game context for monster abilities
 */
data class GameContext(
    val currentPhase: GamePhase,
    val pot: Int,
    val currentBet: Int,
    val playersRemaining: Int
)

/**
 * Monster effects that can be applied in game modes
 */
sealed class MonsterEffect {
    data class CardReveal(val cardCount: Int) : MonsterEffect()
    data class BetModifier(val multiplier: Float) : MonsterEffect()
    data class ChipProtection(val percentage: Float) : MonsterEffect()
    data class SpeedBoost(val multiplier: Float) : MonsterEffect()
}