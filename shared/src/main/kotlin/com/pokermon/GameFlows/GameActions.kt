package com.pokermon.GameFlows

import com.pokermon.Game
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
    // Setup and navigation actions
    object StartGame : GameActions()
    data class SelectMode(val mode: GameMode) : GameActions()
    data class ConfigurePlayers(val playerName: String, val playerCount: Int, val startingChips: Int) : GameActions()
    data class ConfirmSetup(val gameConfig: Game) : GameActions()
    
    // Core game actions
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
    data class SelectCardsForExchange(val player: Player, val cardIndices: List<Int>) : GameActions()
    
    // Flow control actions
    object NextRound : GameActions()
    object ContinueGame : GameActions()
    object EndGame : GameActions()
    object PauseGame : GameActions()
    data class ResumeGame(val savedState: String = "") : GameActions()
    data class RestartGame(val sameSettings: Boolean = true) : GameActions()
    
    // Navigation actions
    object ShowStats : GameActions()
    object ShowHelp : GameActions()
    data class ShowHelpCategory(val category: String) : GameActions()
    object ReturnToGame : GameActions()
    object ReturnToMainMenu : GameActions()
    data class NavigateToState(val targetState: String) : GameActions()
    
    // AI actions
    data class ProcessAITurn(val aiPlayer: Player) : GameActions()
    data class ValidateAction(val action: GameActions, val player: Player) : GameActions()
    object CompleteAIProcessing : GameActions()
    
    // Mode selection and switching
    data class SelectGameMode(val mode: GameMode) : GameActions()
    data class SwitchToMode(val mode: GameMode, val preserveState: Boolean = false) : GameActions()
    
    // Victory and completion actions
    data class TriggerVictory(val winner: Player, val victoryType: VictoryType) : GameActions()
    data class CelebrateVictory(val celebrationData: CelebrationData) : GameActions()
    object AcknowledgeGameOver : GameActions()
    
    // Sub-state management actions  
    data class EnterSubState(val subState: PlayingSubState) : GameActions()
    data class ExitSubState(val reason: String = "Completed") : GameActions()
    data class TransitionSubState(val from: PlayingSubState, val to: PlayingSubState) : GameActions()
    
    // Mode-specific actions
    sealed class AdventureActions : GameActions() {
        data class EncounterMonster(val monsterName: String, val monsterHealth: Int) : AdventureActions()
        data class AttackMonster(val player: Player, val damage: Int) : AdventureActions()
        data class UseSpecialAbility(val player: Player, val abilityName: String) : AdventureActions()
        data class FleeFromBattle(val player: Player) : AdventureActions()
        data class CompleteQuest(val questName: String, val reward: Int) : AdventureActions()
        data class StartQuest(val questName: String) : AdventureActions()
        data class UpdateQuestProgress(val questName: String, val progress: Map<String, Int>) : AdventureActions()
    }
    
    sealed class SafariActions : GameActions() {
        data class EncounterWildMonster(val monsterName: String) : SafariActions()
        data class ThrowSafariBall(val player: Player) : SafariActions()
        data class AttemptCapture(val player: Player, val captureChance: Double) : SafariActions()
        data class UseSpecialBall(val player: Player, val ballType: String) : SafariActions()
        data class ApproachMonster(val player: Player, val cautious: Boolean = true) : SafariActions()
        object FleeFromWildMonster : SafariActions()
        data class CheckMonsterStats(val monsterName: String) : SafariActions()
    }
    
    sealed class IronmanActions : GameActions() {
        data class ConvertToGachaPoints(val chips: Int, val points: Int) : IronmanActions()
        data class PerformGachaPull(val player: Player, val pointsSpent: Int, val pullType: String = "standard") : IronmanActions()
        data class TriggerPermadeath(val player: Player, val reason: String) : IronmanActions()
        data class AcknowledgeRisk(val player: Player, val riskLevel: Double) : IronmanActions()
        data class ActivateRiskMode(val player: Player, val multiplier: Double) : IronmanActions()
        object CheckSurvivalStatus : IronmanActions()
    }
    
    // Special event actions
    data class TriggerSpecialEvent(val eventType: String, val eventName: String) : GameActions()
    data class MakeEventChoice(val choiceId: String, val player: Player) : GameActions()
    data class CompleteSpecialEvent(val eventName: String, val outcome: Map<String, Any>) : GameActions()
    
    // Achievement actions
    data class UnlockAchievement(val achievementName: String, val player: Player) : GameActions()
    object ShowAchievements : GameActions()
    
    // Error and recovery actions
    data class ReportError(val error: String, val context: String) : GameActions()
    data class AttemptRecovery(val recoveryMethod: String) : GameActions()
    object ConfirmExit : GameActions()
}
