package com.pokermon.modes.classic

import com.pokermon.Game
import com.pokermon.GameEngine
import com.pokermon.GameMode
import com.pokermon.HandEvaluator
import com.pokermon.database.Monster
import com.pokermon.database.MonsterDatabase
import com.pokermon.modes.Achievement
import com.pokermon.modes.GameContext
import com.pokermon.modes.MonsterEffect
import com.pokermon.modes.PlayerHandResult
import com.pokermon.modes.RoundResult
import com.pokermon.players.Player

/**
 * Classic mode implementation - Traditional 5-card draw poker with monster companions.
 * This is the foundational game mode that other modes build upon.
 *
 * @author Pokermon Classic Mode
 * @version 1.1.0
 */
class ClassicGameMode {
    private val monsterDatabase = MonsterDatabase

    /**
     * Creates a classic game engine instance
     */
    fun createEngine(): GameEngine {
        val gameConfig =
            Game(
                gameMode = GameMode.CLASSIC,
                enableMonsters = true,
                difficultyLevel = 2, // Medium difficulty default
            )
        return GameEngine(gameConfig)
    }

    /**
     * Initializes classic mode with specified settings
     */
    fun initialize(
        playerCount: Int = 4,
        startingChips: Int = 1000,
        difficultyLevel: Int = 2,
    ): Game {
        // Create game configuration
        return Game(
            maxPlayers = playerCount,
            startingChips = startingChips,
            gameMode = GameMode.CLASSIC,
            enableMonsters = true,
            difficultyLevel = difficultyLevel,
        )
    }

    /**
     * Handles classic mode specific rules and logic
     */
    fun processRound(engine: GameEngine): RoundResult {
        val players = engine.players ?: emptyArray()
        val activePlayers = players.filter { !it.fold && it.chips > 0 }

        if (activePlayers.size <= 1) {
            return RoundResult(
                winner = activePlayers.firstOrNull(),
                potWon = engine.currentPot,
                gameEnded = true,
            )
        }

        // Classic poker hand evaluation
        val handResults =
            activePlayers.map { player ->
                val handEvaluation = HandEvaluator.evaluateHand(player.hand)
                PlayerHandResult(player, handEvaluation)
            }

        // Determine winner
        val winner = handResults.maxByOrNull { it.evaluation.score }?.player

        return RoundResult(
            winner = winner,
            potWon = engine.currentPot,
            gameEnded = false,
            handResults = handResults,
        )
    }

    /**
     * Applies classic mode specific monster abilities
     */
    fun applyMonsterAbilities(
        player: Player,
        context: GameContext,
    ): List<MonsterEffect> {
        val effects = mutableListOf<MonsterEffect>()

        // Get player's monster companion
        val monster = player.currentMonster
        if (monster != null) {
            val monsterData = monsterDatabase.getMonster(monster.name)

            // Apply classic mode abilities based on effect type
            when (monsterData?.effectType) {
                Monster.EffectType.CARD_ADVANTAGE -> {
                    // Card advantage can see one opponent's card
                    effects.add(MonsterEffect.CardReveal(1))
                }
                Monster.EffectType.BETTING_BOOST -> {
                    // Betting boost increases bet effectiveness
                    effects.add(MonsterEffect.BetModifier(1.2f))
                }
                Monster.EffectType.DEFENSIVE_SHIELD -> {
                    // Defensive shield provides chip protection
                    effects.add(MonsterEffect.ChipProtection(0.1f))
                }
                Monster.EffectType.AI_ENHANCEMENT -> {
                    // AI enhancement speeds up game pace
                    effects.add(MonsterEffect.SpeedBoost(1.5f))
                }
                else -> {
                    // Other effect types don't have special classic mode effects
                }
            }
        }

        return effects
    }

    /**
     * Gets classic mode specific achievements
     */
    fun checkAchievements(
        player: Player,
        roundResult: RoundResult,
    ): List<Achievement> {
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
                    HandEvaluator.HandType.FULL_HOUSE,
                    -> {
                        // No special achievements for these hand types
                    }
                }
            }
        }

        return achievements
    }
}
