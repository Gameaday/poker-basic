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
    // Initial setup states
    object Initializing : GameState()
    
    data class ModeSelection(
        val availableModes: List<GameMode> = GameMode.values().toList(),
        val selectedMode: GameMode? = null,
        val playerName: String = ""
    ) : GameState()
    
    data class PlayerSetup(
        val selectedMode: GameMode,
        val playerName: String,
        val playerCount: Int = 2,
        val startingChips: Int = 1000,
        val setupComplete: Boolean = false
    ) : GameState()
    
    data class GameStarting(
        val gameConfig: Game,
        val loadingProgress: Float = 0.0f,
        val loadingMessage: String = "Preparing game..."
    ) : GameState()

    // Active gameplay states
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

    data class RoundTransition(
        val completedRound: Int,
        val nextRound: Int,
        val roundWinner: Player?,
        val roundWinnings: Int,
        val continueOptions: List<String> = listOf("Next Round", "View Stats", "Quit Game")
    ) : GameState()

    // Information and interaction states
    data class WaitingForInput(
        val message: String,
        val validOptions: List<String> = emptyList(),
        val targetPlayer: Player? = null,
        val timeoutMs: Long? = null,
        val defaultOption: String? = null
    ) : GameState()
    
    data class ShowingStats(
        val sessionStats: Map<String, Any>,
        val playerStats: Map<Player, Map<String, Any>>,
        val gameMode: GameMode,
        val canReturn: Boolean = true
    ) : GameState()
    
    data class ShowingHelp(
        val helpCategory: String = "general",
        val helpContent: Map<String, String> = emptyMap(),
        val canReturn: Boolean = true
    ) : GameState()

    // Pause and continuation states
    data class Paused(
        val savedState: Game,
        val pauseReason: String = "User requested",
        val pauseTime: Long = System.currentTimeMillis(),
        val resumeOptions: List<String> = listOf("Resume", "Save & Quit", "Settings")
    ) : GameState()

    // End game states
    data class GameOver(
        val winner: Player?,
        val finalScores: Map<String, Int>,
        val sessionStats: Map<String, Any>,
        val gameMode: GameMode = GameMode.CLASSIC,
        val totalRounds: Int = 1,
        val gameOverReason: String = "Game completed",
        val nextOptions: List<String> = listOf("Play Again", "Change Mode", "View Stats", "Main Menu")
    ) : GameState()
    
    data class Victory(
        val winner: Player,
        val victoryType: VictoryType,
        val achievements: List<String> = emptyList(),
        val rewards: Map<String, Any> = emptyMap(),
        val celebrationData: CelebrationData? = null
    ) : GameState()

    // System states
    data class Error(
        val message: String,
        val recoverable: Boolean = true,
        val errorCode: String? = null,
        val suggestedActions: List<String> = emptyList()
    ) : GameState()
    
    data class Loading(
        val operation: String,
        val progress: Float = 0.0f,
        val cancellable: Boolean = false
    ) : GameState()
    
    object Exiting : GameState()
}

/**
 * Types of victory for different game modes and scenarios.
 */
enum class VictoryType {
    CHIPS_VICTORY,      // Won by accumulating most chips
    ELIMINATION_VICTORY, // Won by eliminating all opponents
    QUEST_COMPLETION,   // Adventure mode quest completed
    MONSTER_CAPTURE,    // Safari mode capture goal achieved
    GACHA_JACKPOT,     // Ironman mode major prize
    SURVIVAL_VICTORY,   // Ironman mode permadeath survival
    TIME_VICTORY,      // Won within time limit
    SPECIAL_CONDITION  // Mode-specific victory condition
}

/**
 * Data for victory celebration and presentation.
 */
data class CelebrationData(
    val animationType: String = "default",
    val duration: Long = 3000,
    val sounds: List<String> = emptyList(),
    val effects: Map<String, Any> = emptyMap(),
    val messages: List<String> = emptyList()
)

/**
 * Sub-states for the Playing state to provide more granular control.
 * Enables mode-specific behaviors and UI adaptations.
 * Comprehensive implementation of all game flow sub-states.
 */
sealed class PlayingSubState {
    // Pre-game sub-states
    data class DealingCards(
        val cardsDealt: Int = 0,
        val totalCards: Int,
        val dealingToPlayer: String = ""
    ) : PlayingSubState()
    
    data class EvaluatingHands(
        val evaluatedPlayers: Int = 0,
        val totalPlayers: Int,
        val currentEvaluation: String = ""
    ) : PlayingSubState()

    // Betting sub-states
    data class WaitingForPlayerAction(
        val player: Player,
        val validActions: List<String> = listOf("call", "raise", "fold"),
        val minimumBet: Int = 0,
        val maximumBet: Int = Int.MAX_VALUE,
        val timeRemaining: Long? = null,
        val actionHistory: List<String> = emptyList()
    ) : PlayingSubState()
    
    data class ProcessingAI(
        val aiPlayers: List<Player>,
        val currentAIIndex: Int = 0,
        val processingComplete: Boolean = false
    ) : PlayingSubState()
    
    data class BettingRoundComplete(
        val totalBets: Map<Player, Int>,
        val activePlayers: List<Player>,
        val nextPhase: String
    ) : PlayingSubState()

    // Card exchange sub-states  
    data class CardExchangePhase(
        val player: Player,
        val maxExchanges: Int = 3,
        val exchangesRemaining: Int = 1,
        val selectedCards: List<Int> = emptyList(),
        val exchangeComplete: Boolean = false
    ) : PlayingSubState()
    
    data class ProcessingCardExchange(
        val exchangingPlayer: Player,
        val cardsToExchange: Int,
        val newCardsDealt: Boolean = false
    ) : PlayingSubState()

    // Round conclusion sub-states
    data class ShowingResults(
        val handResults: Map<Player, String>,
        val winnings: Map<Player, Int>,
        val showDuration: Long = 5000,
        val detailedView: Boolean = false
    ) : PlayingSubState()
    
    data class PotDistribution(
        val potAmount: Int,
        val winners: List<Player>,
        val distribution: Map<Player, Int> = emptyMap(),
        val distributionComplete: Boolean = false
    ) : PlayingSubState()

    // Mode-specific sub-states - Adventure Mode
    data class AdventureMode(
        val currentMonster: String,
        val monsterHealth: Int,
        val maxHealth: Int = monsterHealth,
        val questProgress: Map<String, Int> = emptyMap(),
        val battlePhase: AdventureBattlePhase = AdventureBattlePhase.ENCOUNTER,
        val playerDamage: Int = 0,
        val monsterAttacks: List<String> = emptyList()
    ) : PlayingSubState()
    
    data class MonsterEncounter(
        val monster: MonsterData,
        val encounterType: String = "random",
        val fleeOption: Boolean = true
    ) : PlayingSubState()
    
    data class QuestProgress(
        val questName: String,
        val questObjectives: Map<String, Boolean>,
        val questRewards: List<String>,
        val canContinue: Boolean = true
    ) : PlayingSubState()

    // Mode-specific sub-states - Safari Mode
    data class SafariMode(
        val wildMonster: String,
        val captureChance: Double,
        val safariBallsRemaining: Int,
        val captureAttempts: Int = 0,
        val monsterBehavior: MonsterBehavior = MonsterBehavior.NEUTRAL,
        val weather: String = "clear",
        val bonusCaptureChance: Double = 0.0
    ) : PlayingSubState()
    
    data class MonsterCapture(
        val monster: MonsterData,
        val captureInProgress: Boolean = false,
        val captureSuccess: Boolean? = null,
        val ballsUsed: Int = 0
    ) : PlayingSubState()

    // Mode-specific sub-states - Ironman Mode
    data class IronmanMode(
        val gachaPoints: Int,
        val riskLevel: Double,
        val permadeathWarning: Boolean = false,
        val riskFactors: Map<String, Double> = emptyMap(),
        val survivalStreak: Int = 0,
        val deathsThisSession: Int = 0
    ) : PlayingSubState()
    
    data class GachaPull(
        val pointsToSpend: Int,
        val pullType: String = "standard",
        val guaranteedRarity: String? = null,
        val pullInProgress: Boolean = false,
        val results: List<String> = emptyList()
    ) : PlayingSubState()
    
    data class PermadeathRisk(
        val riskLevel: Double,
        val consequences: List<String>,
        val canCancel: Boolean = true,
        val warningAcknowledged: Boolean = false
    ) : PlayingSubState()

    // Special event sub-states
    data class SpecialEvent(
        val eventType: String,
        val eventName: String,
        val eventDescription: String,
        val eventChoices: List<EventChoice> = emptyList(),
        val eventRewards: Map<String, Any> = emptyMap(),
        val eventComplete: Boolean = false
    ) : PlayingSubState()
    
    data class Achievement(
        val achievementName: String,
        val achievementDescription: String,
        val achievementReward: String,
        val displayDuration: Long = 3000
    ) : PlayingSubState()
}

/**
 * Adventure mode battle phases for structured combat flow.
 */
enum class AdventureBattlePhase {
    ENCOUNTER,      // Monster appears
    PLAYER_TURN,    // Player's action phase
    MONSTER_TURN,   // Monster's response
    DAMAGE_CALC,    // Calculate and apply damage
    VICTORY_CHECK,  // Check for battle end conditions
    VICTORY,        // Battle won
    DEFEAT,         // Battle lost
    ESCAPE          // Fled from battle
}

/**
 * Monster behavior patterns for Safari mode dynamics.
 */
enum class MonsterBehavior {
    AGGRESSIVE,     // Harder to capture, higher flee chance
    NEUTRAL,        // Standard behavior
    DOCILE,         // Easier to capture
    RARE,           // Special behavior for rare encounters
    LEGENDARY       // Unique legendary behavior
}

/**
 * Monster data for mode-specific encounters.
 */
data class MonsterData(
    val name: String,
    val type: String,
    val rarity: String = "common",
    val health: Int = 100,
    val attacks: List<String> = emptyList(),
    val captureRate: Double = 0.5,
    val rewards: Map<String, Int> = emptyMap(),
    val special: Boolean = false
)

/**
 * Event choice for special events and decision points.
 */
data class EventChoice(
    val choiceId: String,
    val choiceText: String,
    val consequences: Map<String, Any> = emptyMap(),
    val requirements: Map<String, Any> = emptyMap(),
    val available: Boolean = true
)
