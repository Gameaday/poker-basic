package com.pokermon.GameFlows

import com.pokermon.Game
import com.pokermon.GameMode
import com.pokermon.players.Player

/**
 * Event definitions for game actions using modern Kotlin patterns.
 * Events represent things that have happened in the game.
 * Enhanced to support mode-specific events and sub-state notifications.
 *
 * @author Pokermon Flow System
 * @version 1.2.0 - Enhanced with mode-specific events
 */
sealed class GameEvents {
    // Setup and configuration events
    object GameStarted : GameEvents()
    data class ModeSelected(val mode: GameMode) : GameEvents()
    data class PlayersConfigured(val playerCount: Int, val startingChips: Int) : GameEvents()
    data class GameConfigured(val gameConfig: Game) : GameEvents()
    data class LoadingProgress(val operation: String, val progress: Float) : GameEvents()
    
    // Player events
    data class PlayerJoined(val player: Player) : GameEvents()
    data class PlayerLeft(val player: Player, val reason: String = "quit") : GameEvents()
    data class PlayerEliminated(val player: Player, val reason: String) : GameEvents()
    
    // Card events
    data class CardsDealt(val playerCount: Int) : GameEvents()
    data class HandEvaluated(val player: Player, val handStrength: String) : GameEvents()
    data class CardsExchanged(val player: Player, val exchangedCount: Int) : GameEvents()
    data class CardsShown(val player: Player, val handDescription: String) : GameEvents()
    data class CardExchangeComplete(val player: Player) : GameEvents()
    
    // Betting events
    data class BetPlaced(val player: Player, val amount: Int, val totalBet: Int) : GameEvents()
    data class PlayerFolded(val player: Player) : GameEvents()
    data class PlayerCalled(val player: Player, val amount: Int) : GameEvents()
    data class PlayerRaised(val player: Player, val amount: Int, val newTotal: Int) : GameEvents()
    data class PlayerChecked(val player: Player) : GameEvents()
    data class BettingRoundComplete(val activePlayers: List<Player>, val totalPot: Int) : GameEvents()
    
    // Round and phase events
    data class RoundStarted(val roundNumber: Int, val gameMode: GameMode) : GameEvents()
    data class RoundEnded(val winner: Player?, val potAmount: Int, val roundNumber: Int) : GameEvents()
    data class PhaseChanged(val newPhase: com.pokermon.GamePhase, val previousPhase: com.pokermon.GamePhase?) : GameEvents()
    data class PotDistributed(val winners: List<Player>, val amounts: Map<Player, Int>) : GameEvents()
    
    // Game completion events
    data class GameEnded(val finalWinner: Player?, val sessionDuration: Long, val reason: String = "completed") : GameEvents()
    data class VictoryTriggered(val winner: Player, val victoryType: VictoryType, val achievements: List<String> = emptyList()) : GameEvents()
    data class GamePaused(val reason: String, val canResume: Boolean = true) : GameEvents()
    data class GameResumed(val fromState: String) : GameEvents()
    data class GameRestarted(val newConfig: Game) : GameEvents()
    
    // Mode events
    data class GameModeSelected(val mode: GameMode) : GameEvents()
    data class GameModeSwitched(val from: GameMode, val to: GameMode) : GameEvents()
    
    // Navigation events
    data class StateChanged(val fromState: String, val toState: String) : GameEvents()
    data class StatsDisplayed(val stats: Map<String, Any>) : GameEvents()
    data class HelpDisplayed(val category: String, val content: Map<String, String>) : GameEvents()
    data class MenuShown(val menuType: String, val options: List<String>) : GameEvents()
    
    // Sub-state events
    data class SubStateEntered(val subState: PlayingSubState) : GameEvents()
    data class SubStateExited(val subState: PlayingSubState, val reason: String) : GameEvents()
    data class SubStateTransition(val from: PlayingSubState, val to: PlayingSubState) : GameEvents()
    
    // AI events
    data class AIProcessingStarted(val aiPlayers: List<Player>) : GameEvents()
    data class AIActionPerformed(val aiPlayer: Player, val action: String, val amount: Int = 0) : GameEvents()
    data class AIProcessingComplete(val actionsPerformed: Int) : GameEvents()
    
    // Mode-specific events
    sealed class AdventureEvents : GameEvents() {
        data class MonsterEncountered(val monsterName: String, val health: Int, val type: String) : AdventureEvents()
        data class MonsterAttacked(val player: Player, val damage: Int, val remainingHealth: Int) : AdventureEvents()
        data class MonsterDefeated(val monsterName: String, val reward: Int, val experience: Int = 0) : AdventureEvents()
        data class PlayerDamaged(val player: Player, val damage: Int, val remainingHealth: Int) : AdventureEvents()
        data class BattleFled(val player: Player, val monsterName: String) : AdventureEvents()
        data class QuestStarted(val questName: String, val objectives: List<String>) : AdventureEvents()
        data class QuestCompleted(val questName: String, val player: Player, val reward: Int) : AdventureEvents()
        data class QuestFailed(val questName: String, val player: Player, val reason: String) : AdventureEvents()
        data class QuestProgressUpdated(val questName: String, val progress: Map<String, Int>) : AdventureEvents()
        data class SpecialAbilityUsed(val player: Player, val abilityName: String, val effect: String) : AdventureEvents()
    }
    
    sealed class SafariEvents : GameEvents() {
        data class WildMonsterSighted(val monsterName: String, val rarity: String, val behavior: String) : SafariEvents()
        data class SafariBallThrown(val player: Player, val ballType: String, val ballsRemaining: Int) : SafariEvents()
        data class MonsterCaptured(val player: Player, val monsterName: String, val rarity: String) : SafariEvents()
        data class MonsterEscaped(val monsterName: String, val reason: String, val captureAttempts: Int) : SafariEvents()
        data class CaptureAttempted(val player: Player, val successChance: Double, val actualResult: Boolean) : SafariEvents()
        data class SafariEnded(val monstersCapture: Int, val ballsUsed: Int, val rareFinds: List<String>) : SafariEvents()
        data class MonsterBehaviorChanged(val monsterName: String, val newBehavior: String, val reason: String) : SafariEvents()
        data class WeatherChanged(val newWeather: String, val effects: Map<String, Double>) : SafariEvents()
    }
    
    sealed class IronmanEvents : GameEvents() {
        data class ChipsConverted(val chips: Int, val gachaPoints: Int, val conversionRate: Double) : IronmanEvents()
        data class GachaPullPerformed(val player: Player, val pointsSpent: Int, val result: String, val rarity: String) : IronmanEvents()
        data class RareMonsterWon(val player: Player, val monsterName: String, val rarity: String, val value: Int) : IronmanEvents()
        data class RiskLevelChanged(val player: Player, val newRiskLevel: Double, val factors: Map<String, Double>) : IronmanEvents()
        data class PermadeathTriggered(val player: Player, val reason: String, val finalStats: Map<String, Any>) : IronmanEvents()
        data class PermadeathWarning(val player: Player, val riskLevel: Double, val consequences: List<String>) : IronmanEvents()
        data class SurvivalStreakUpdated(val player: Player, val streakLength: Int, val bonus: Int) : IronmanEvents()
        data class GachaJackpot(val player: Player, val jackpotType: String, val value: Int) : IronmanEvents()
    }
    
    // Special events
    data class SpecialEventTriggered(val eventType: String, val eventName: String, val description: String) : GameEvents()
    data class EventChoiceMade(val player: Player, val choiceId: String, val consequences: Map<String, Any>) : GameEvents()
    data class SpecialEventCompleted(val eventName: String, val outcome: String, val rewards: Map<String, Any>) : GameEvents()
    
    // Achievement events
    data class AchievementUnlocked(val player: Player, val achievementName: String, val description: String, val reward: String) : GameEvents()
    data class AchievementProgress(val player: Player, val achievementName: String, val progress: Float, val max: Float) : GameEvents()
    data class MilestoneReached(val player: Player, val milestoneName: String, val value: Int) : GameEvents()
    
    // System events
    data class ErrorOccurred(val message: String, val exception: Throwable?, val errorCode: String? = null, val recoverable: Boolean = true) : GameEvents()
    data class WarningIssued(val message: String, val severity: String = "LOW", val category: String = "GENERAL") : GameEvents()
    data class SystemNotification(val message: String, val category: String = "INFO", val priority: Int = 0) : GameEvents()
    data class PerformanceWarning(val operation: String, val duration: Long, val threshold: Long) : GameEvents()
    
    // UI and presentation events  
    data class AnimationTriggered(val animationType: String, val target: String, val duration: Long) : GameEvents()
    data class SoundTriggered(val soundName: String, val volume: Float = 1.0f, val category: String = "SFX") : GameEvents()
    data class EffectTriggered(val effectName: String, val parameters: Map<String, Any> = emptyMap()) : GameEvents()
    data class CelebrationStarted(val celebrationType: String, val duration: Long, val data: CelebrationData) : GameEvents()
}
