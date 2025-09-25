package com.pokermon.GameFlows

import com.pokermon.GameMode
import com.pokermon.players.Player

/**
 * Action definitions for game interactions using modern Kotlin patterns.
 * Actions represent things that players or the system want to do.
 * Enhanced to support mode-specific actions and sub-state transitions.
 *
 * @author Pokermon Flow System
 * @version 1.2.0 - Enhanced with mode-specific actions
 */
sealed class GameActions {
    // Core game actions
    object StartGame : GameActions()
    object DealCards : GameActions()
    data class JoinGame(val player: Player) : GameActions()
    data class LeaveGame(val player: Player) : GameActions()
    
    // Betting actions
    data class PlaceBet(val player: Player, val amount: Int) : GameActions()
    data class Call(val player: Player) : GameActions()
    data class Raise(val player: Player, val amount: Int) : GameActions()
    data class Fold(val player: Player) : GameActions()
    data class Check(val player: Player) : GameActions()
    
    // Card actions
    data class ExchangeCards(val player: Player, val cardIndices: List<Int>) : GameActions()
    data class ShowCards(val player: Player) : GameActions()
    
    // Flow control
    object NextRound : GameActions()
    object EndGame : GameActions()
    object PauseGame : GameActions()
    data class ResumeGame(val savedState: String) : GameActions()
    
    // AI actions
    data class ProcessAITurn(val aiPlayer: Player) : GameActions()
    data class ValidateAction(val action: GameActions, val player: Player) : GameActions()
    
    // Mode selection and switching
    data class SelectGameMode(val mode: GameMode) : GameActions()
    data class SwitchToMode(val mode: GameMode, val preserveState: Boolean = false) : GameActions()
    
    // Mode-specific actions
    sealed class AdventureActions : GameActions() {
        data class EncounterMonster(val monsterName: String, val monsterHealth: Int) : AdventureActions()
        data class AttackMonster(val player: Player, val damage: Int) : AdventureActions()
        data class CompleteQuest(val questName: String, val reward: Int) : AdventureActions()
    }
    
    sealed class SafariActions : GameActions() {
        data class EncounterWildMonster(val monsterName: String) : SafariActions()
        data class ThrowSafariBall(val player: Player) : SafariActions()
        data class AttemptCapture(val player: Player, val captureChance: Double) : SafariActions()
    }
    
    sealed class IronmanActions : GameActions() {
        data class ConvertToGachaPoints(val chips: Int, val points: Int) : IronmanActions()
        data class PerformGachaPull(val player: Player, val pointsSpent: Int) : IronmanActions()
        data class TriggerPermadeath(val player: Player) : IronmanActions()
    }
    
    // Sub-state transition actions
    data class EnterSubState(val subState: PlayingSubState) : GameActions()
    data class ExitSubState(val reason: String = "Completed") : GameActions()
    data class TransitionSubState(val from: PlayingSubState, val to: PlayingSubState) : GameActions()
}
