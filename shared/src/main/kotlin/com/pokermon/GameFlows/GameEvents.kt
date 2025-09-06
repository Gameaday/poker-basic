package com.pokermon.GameFlows

import com.pokermon.players.Player

/**
 * Event definitions for game actions using modern Kotlin patterns.
 * Events represent things that have happened in the game.
 *
 * @author Pokermon Flow System
 * @version 1.1.0
 */
sealed class GameEvents {
    object GameStarted : GameEvents()

    data class PlayerJoined(val player: Player) : GameEvents()

    data class PlayerLeft(val player: Player) : GameEvents()

    data class CardsDealt(val playerCount: Int) : GameEvents()

    data class BetPlaced(
        val player: Player,
        val amount: Int,
        val totalBet: Int,
    ) : GameEvents()

    data class PlayerFolded(val player: Player) : GameEvents()

    data class PlayerCalled(
        val player: Player,
        val amount: Int,
    ) : GameEvents()

    data class PlayerRaised(
        val player: Player,
        val amount: Int,
        val newTotal: Int,
    ) : GameEvents()

    data class CardsExchanged(
        val player: Player,
        val exchangedCount: Int,
    ) : GameEvents()

    data class RoundEnded(
        val winner: Player?,
        val potAmount: Int,
    ) : GameEvents()

    data class GameEnded(
        val finalWinner: Player?,
        val sessionDuration: Long,
    ) : GameEvents()

    data class ErrorOccurred(
        val message: String,
        val exception: Throwable?,
    ) : GameEvents()
}
