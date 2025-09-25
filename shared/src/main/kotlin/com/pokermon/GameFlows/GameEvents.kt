package com.pokermon.GameFlows

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
    // Core game events
    object GameStarted : GameEvents()
    data class PlayerJoined(val player: Player) : GameEvents()
    data class PlayerLeft(val player: Player) : GameEvents()
    data class CardsDealt(val playerCount: Int) : GameEvents()
    
    // Betting events
    data class BetPlaced(val player: Player, val amount: Int, val totalBet: Int) : GameEvents()
    data class PlayerFolded(val player: Player) : GameEvents()
    data class PlayerCalled(val player: Player, val amount: Int) : GameEvents()
    data class PlayerRaised(val player: Player, val amount: Int, val newTotal: Int) : GameEvents()
    data class PlayerChecked(val player: Player) : GameEvents()
    
    // Card events
    data class CardsExchanged(val player: Player, val exchangedCount: Int) : GameEvents()
    data class CardsShown(val player: Player, val handDescription: String) : GameEvents()
    
    // Round events
    data class RoundStarted(val roundNumber: Int) : GameEvents()
    data class RoundEnded(val winner: Player?, val potAmount: Int) : GameEvents()
    data class PhaseChanged(val newPhase: com.pokermon.GamePhase, val previousPhase: com.pokermon.GamePhase?) : GameEvents()
    
    // Game completion events
    data class GameEnded(val finalWinner: Player?, val sessionDuration: Long) : GameEvents()
    data class GamePaused(val reason: String) : GameEvents()
    data class GameResumed(val fromState: String) : GameEvents()
    
    // Mode events
    data class GameModeSelected(val mode: GameMode) : GameEvents()
    data class GameModeSwitched(val from: GameMode, val to: GameMode) : GameEvents()
    
    // Sub-state events
    data class SubStateEntered(val subState: PlayingSubState) : GameEvents()
    data class SubStateExited(val subState: PlayingSubState, val reason: String) : GameEvents()
    data class SubStateTransition(val from: PlayingSubState, val to: PlayingSubState) : GameEvents()
    
    // Mode-specific events
    sealed class AdventureEvents : GameEvents() {
        data class MonsterEncountered(val monsterName: String, val health: Int) : AdventureEvents()
        data class MonsterAttacked(val player: Player, val damage: Int, val remainingHealth: Int) : AdventureEvents()
        data class MonsterDefeated(val monsterName: String, val reward: Int) : AdventureEvents()
        data class QuestCompleted(val questName: String, val player: Player, val reward: Int) : AdventureEvents()
        data class QuestFailed(val questName: String, val player: Player, val reason: String) : AdventureEvents()
    }
    
    sealed class SafariEvents : GameEvents() {
        data class WildMonsterSighted(val monsterName: String) : SafariEvents()
        data class SafariBallThrown(val player: Player, val ballsRemaining: Int) : SafariEvents()
        data class MonsterCaptured(val player: Player, val monsterName: String) : SafariEvents()
        data class MonsterEscaped(val monsterName: String, val reason: String) : SafariEvents()
        data class SafariEnded(val monstersCapture: Int, val ballsUsed: Int) : SafariEvents()
    }
    
    sealed class IronmanEvents : GameEvents() {
        data class ChipsConverted(val chips: Int, val gachaPoints: Int) : IronmanEvents()
        data class GachaPullPerformed(val player: Player, val pointsSpent: Int, val result: String) : IronmanEvents()
        data class RareMonsterWon(val player: Player, val monsterName: String, val rarity: String) : IronmanEvents()
        data class PermadeathTriggered(val player: Player, val reason: String) : IronmanEvents()
    }
    
    // System events
    data class ErrorOccurred(val message: String, val exception: Throwable?, val errorCode: String? = null) : GameEvents()
    data class WarningIssued(val message: String, val severity: String = "LOW") : GameEvents()
    data class SystemNotification(val message: String, val category: String = "INFO") : GameEvents()
}
