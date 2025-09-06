package com.pokermon.GameFlows

import com.pokermon.players.Player

/**
 * Action definitions for game interactions using modern Kotlin patterns.
 * Actions represent things that players or the system want to do.
 *
 * @author Pokermon Flow System
 * @version 1.1.0
 */
sealed class GameActions {
    object StartGame : GameActions()

    object DealCards : GameActions()

    data class JoinGame(val player: Player) : GameActions()

    data class LeaveGame(val player: Player) : GameActions()

    data class PlaceBet(
        val player: Player,
        val amount: Int,
    ) : GameActions()

    data class Call(val player: Player) : GameActions()

    data class Raise(
        val player: Player,
        val amount: Int,
    ) : GameActions()

    data class Fold(val player: Player) : GameActions()

    data class Check(val player: Player) : GameActions()

    data class ExchangeCards(
        val player: Player,
        val cardIndices: List<Int>,
    ) : GameActions()

    data class ShowCards(val player: Player) : GameActions()

    object NextRound : GameActions()

    object EndGame : GameActions()

    object PauseGame : GameActions()

    data class ResumeGame(val savedState: String) : GameActions()

    data class ProcessAITurn(val aiPlayer: Player) : GameActions()

    data class ValidateAction(
        val action: GameActions,
        val player: Player,
    ) : GameActions()
}
