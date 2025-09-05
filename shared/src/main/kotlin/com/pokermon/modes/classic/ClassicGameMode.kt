package com.pokermon.modes.classic

import com.pokermon.*
import com.pokermon.players.Player
import com.pokermon.database.MonsterDatabase
import com.pokermon.HandEvaluator
import com.pokermon.modes.Achievement

/**
 * Classic mode implementation - Traditional 5-card draw poker with monster companions.
 * This is the foundational game mode that other modes build upon.
 * 
 * @author Pokermon Classic Mode
 * @version 1.1.0
 */
class ClassicGameMode {
    
    private val monsterDatabase = MonsterDatabase()
    
    /**
     * Creates a classic game engine instance
     */
    fun createEngine(): GameEngine {
        return GameEngine.Builder()
            .gameMode(GameMode.CLASSIC)
            .enableMonsters(true)
            .difficultyLevel(2) // Medium difficulty default
            .build()
    }
    
    /**
     * Initializes classic mode with specified settings
     */
    fun initialize(
        playerCount: Int = 4,
        startingChips: Int = 1000,
        difficultyLevel: Int = 2
    ): Game {
        val players = Array(playerCount) { index ->
            if (index == 0) {
                Player("Human Player", startingChips)
            } else {
                Player("AI Player ${index}", startingChips, isAI = true)
            }
        }
        
        return Game.Builder()
            .players(*players)
            .gameMode(GameMode.CLASSIC)
            .startingChips(startingChips)
            .build()
    }
    
    /**
     * Handles classic mode specific rules and logic
     */
    fun processRound(game: Game): RoundResult {
        val activePlayers = game.players.filter { !it.folded && it.chips > 0 }
        
        if (activePlayers.size <= 1) {
            return RoundResult(
                winner = activePlayers.firstOrNull(),
                potWon = game.pot,
                gameEnded = true
            )
        }
        
        // Classic poker hand evaluation
        val handResults = activePlayers.map { player ->
            val handEvaluation = HandEvaluator.evaluateHand(player.hand)
            PlayerHandResult(player, handEvaluation)
        }
        
        // Determine winner
        val winner = handResults.maxByOrNull { it.evaluation.score }?.player
        
        return RoundResult(
            winner = winner,
            potWon = game.pot,
            gameEnded = false,
            handResults = handResults
        )
    }
    
    /**
     * Applies classic mode specific monster abilities
     */
    fun applyMonsterAbilities(player: Player, context: GameContext): List<MonsterEffect> {
        val effects = mutableListOf<MonsterEffect>()
        
        // Get player's monster companion
        val monster = player.currentMonster
        if (monster != null) {
            val monsterData = monsterDatabase.getMonster(monster.name)
            
            // Apply classic mode abilities
            when (monsterData?.type) {
                "Psychic" -> {
                    // Psychic types can see one opponent's card
                    effects.add(MonsterEffect.CardReveal(1))
                }
                "Fire" -> {
                    // Fire types increase bet aggression
                    effects.add(MonsterEffect.BetModifier(1.2f))
                }
                "Water" -> {
                    // Water types provide chip protection
                    effects.add(MonsterEffect.ChipProtection(0.1f))
                }
                "Electric" -> {
                    // Electric types speed up game pace
                    effects.add(MonsterEffect.SpeedBoost(1.5f))
                }
            }
        }
        
        return effects
    }
    
    /**
     * Gets classic mode specific achievements
     */
    fun checkAchievements(player: Player, roundResult: RoundResult): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        roundResult.handResults?.forEach { handResult ->
            if (handResult.player == player) {
                when (handResult.evaluation.handType) {
                    HandEvaluator.HandType.ROYAL_FLUSH -> {
                        achievements.add(Achievement.ROYAL_FLUSH)
                    }
                    HandEvaluator.HandType.STRAIGHT_FLUSH -> {
                        achievements.add(Achievement.STRAIGHT_FLUSH)
                    }
                    HandEvaluator.HandType.FOUR_OF_A_KIND -> {
                        achievements.add(Achievement.FOUR_OF_A_KIND)
                    }
                    HandEvaluator.HandType.HIGH_CARD,
                    HandEvaluator.HandType.ONE_PAIR,
                    HandEvaluator.HandType.TWO_PAIR,
                    HandEvaluator.HandType.THREE_OF_A_KIND,
                    HandEvaluator.HandType.STRAIGHT,
                    HandEvaluator.HandType.FLUSH,
                    HandEvaluator.HandType.FULL_HOUSE -> {
                        // No special achievements for these hand types
                    }
                }
            }
        }
        
        return achievements
    }
}

/**
 * Represents the result of a game round
 */
data class RoundResult(
    val winner: Player?,
    val potWon: Int,
    val gameEnded: Boolean,
    val handResults: List<PlayerHandResult>? = null
)

/**
 * Represents a player's hand evaluation result
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
 * Monster effects that can be applied in classic mode
 */
sealed class MonsterEffect {
    data class CardReveal(val cardCount: Int) : MonsterEffect()
    data class BetModifier(val multiplier: Float) : MonsterEffect()
    data class ChipProtection(val percentage: Float) : MonsterEffect()
    data class SpeedBoost(val multiplier: Float) : MonsterEffect()
}