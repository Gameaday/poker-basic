package com.pokermon.android.data

import com.pokermon.database.Monster
import com.pokermon.database.MonsterDatabase
import kotlin.random.Random

/**
 * Manages monster opponents for poker games, replacing generic "CPU" names
 * with engaging monster characters from the Pokermon universe.
 */
class MonsterOpponentManager {
    companion object {
        // Pool of opponent monsters for different difficulty levels
        private val beginnerOpponents =
            listOf(
                "PixelPup",
                "ByteBird",
                "CodeCat",
                "DataDog",
            )

        private val intermediateOpponents =
            listOf(
                "FireFox.exe",
                "AquaApp",
                "TechTurtle",
                "CloudCrawler",
            )

        private val advancedOpponents =
            listOf(
                "NeuralNinja",
                "QuantumQuokka",
                "CyberShark",
                "RoboRaven",
            )

        private val expertOpponents =
            listOf(
                "MegaMind.AI",
                "DragonDrive",
                "PhoenixProtocol",
            )

        private val legendaryOpponents =
            listOf(
                "The Compiler",
                "Daemon.exe",
                "The Algorithm",
            )
    }

    private val seenMonsters = mutableSetOf<String>()
    private val defeatedMonsters = mutableSetOf<String>()
    private val random = Random.Default

    /**
     * Generate monster opponents for a poker game based on player skill level.
     * @param opponentCount number of opponents needed
     * @param playerSkillLevel estimated skill level (0-4, 0=beginner, 4=legendary)
     * @return list of monster opponent data
     */
    fun generateOpponents(
        opponentCount: Int,
        playerSkillLevel: Int = 0,
    ): List<MonsterOpponent> {
        val opponents = mutableListOf<MonsterOpponent>()

        repeat(opponentCount) {
            val monster = selectOpponentMonster(playerSkillLevel)
            val difficulty = calculateDifficulty(monster)

            opponents.add(
                MonsterOpponent(
                    monster = monster,
                    displayName = monster.name,
                    difficulty = difficulty,
                    aggressiveness = calculateAggressiveness(monster),
                    bluffFrequency = calculateBluffFrequency(monster),
                    isRevealed = seenMonsters.contains(monster.name),
                    isDefeated = defeatedMonsters.contains(monster.name),
                ),
            )

            // Mark monster as seen
            seenMonsters.add(monster.name)
        }

        return opponents
    }

    /**
     * Select an appropriate opponent monster based on skill level.
     * @param skillLevel estimated skill level (0-4, 0=beginner, 4=legendary)
     * @return a monster suitable for the player's skill level
     */
    private fun selectOpponentMonster(skillLevel: Int): Monster {
        val monsterPool =
            when (skillLevel) {
                0 -> beginnerOpponents
                1 -> beginnerOpponents + intermediateOpponents
                2 -> intermediateOpponents + advancedOpponents
                3 -> advancedOpponents + expertOpponents
                4 -> expertOpponents + legendaryOpponents
                else -> beginnerOpponents
            }

        val selectedName = monsterPool.random(random)
        return MonsterDatabase.getMonster(selectedName)
            ?: run {
                // Fallback to PixelPup if selected monster doesn't exist
                MonsterDatabase.getMonster("PixelPup")
                    ?: throw IllegalStateException("Critical error: Base monster PixelPup not found in database")
            }
    }

    /**
     * Calculate opponent difficulty based on monster rarity and attributes.
     */
    private fun calculateDifficulty(monster: Monster): OpponentDifficulty {
        return when (monster.rarity) {
            Monster.Rarity.COMMON -> OpponentDifficulty.EASY
            Monster.Rarity.UNCOMMON -> OpponentDifficulty.MEDIUM
            Monster.Rarity.RARE -> OpponentDifficulty.HARD
            Monster.Rarity.EPIC -> OpponentDifficulty.EXPERT
            Monster.Rarity.LEGENDARY -> OpponentDifficulty.LEGENDARY
        }
    }

    /**
     * Calculate aggressiveness based on monster type and effect.
     */
    private fun calculateAggressiveness(monster: Monster): Float {
        val baseAggressiveness =
            when (monster.effectType) {
                Monster.EffectType.BETTING_BOOST -> 0.8f
                Monster.EffectType.CHIP_BONUS -> 0.6f
                Monster.EffectType.CARD_ADVANTAGE -> 0.7f
                Monster.EffectType.LUCK_ENHANCEMENT -> 0.5f
                Monster.EffectType.VISUAL_THEME -> 0.4f
                Monster.EffectType.DEFENSIVE_SHIELD -> 0.3f
                Monster.EffectType.AI_ENHANCEMENT -> 0.9f
                Monster.EffectType.ULTIMATE_POWER -> 1.0f
            }

        // Adjust by rarity
        val rarityMultiplier = (monster.rarity.powerMultiplier * 0.1).toFloat()
        return (baseAggressiveness + rarityMultiplier).coerceIn(0.1f, 1.0f)
    }

    /**
     * Calculate bluff frequency based on monster characteristics.
     */
    private fun calculateBluffFrequency(monster: Monster): Float {
        // Monsters with certain effects are more likely to bluff
        val baseBluffing =
            when (monster.effectType) {
                Monster.EffectType.LUCK_ENHANCEMENT -> 0.3f
                Monster.EffectType.CARD_ADVANTAGE -> 0.4f
                Monster.EffectType.BETTING_BOOST -> 0.5f
                Monster.EffectType.CHIP_BONUS -> 0.2f
                Monster.EffectType.VISUAL_THEME -> 0.1f
                Monster.EffectType.DEFENSIVE_SHIELD -> 0.2f
                Monster.EffectType.AI_ENHANCEMENT -> 0.6f
                Monster.EffectType.ULTIMATE_POWER -> 0.7f
            }

        // Rare monsters are more sophisticated
        return (baseBluffing * (monster.rarity.powerMultiplier * 0.2).toFloat())
            .coerceIn(0.05f, 0.6f)
    }

    /**
     * Mark a monster as defeated (for encyclopedia tracking).
     */
    fun markMonsterDefeated(monsterName: String) {
        defeatedMonsters.add(monsterName)
    }

    /**
     * Mark a monster as seen (for encyclopedia tracking).
     */
    fun markMonsterSeen(monsterName: String) {
        seenMonsters.add(monsterName)
    }

    /**
     * Get all seen monsters for encyclopedia display.
     */
    fun getSeenMonsters(): Set<String> = seenMonsters.toSet()

    /**
     * Get all defeated monsters.
     */
    fun getDefeatedMonsters(): Set<String> = defeatedMonsters.toSet()

    /**
     * Get discovery progress (seen monsters / total monsters).
     */
    fun getDiscoveryProgress(): Float {
        val totalMonsters = MonsterDatabase.getTotalMonsterCount()
        return if (totalMonsters > 0) {
            seenMonsters.size.toFloat() / totalMonsters.toFloat()
        } else {
            0f
        }
    }

    /**
     * Reset progress (for new game or testing).
     */
    fun resetProgress() {
        seenMonsters.clear()
        defeatedMonsters.clear()
    }
}

/**
 * Data class representing a monster opponent in a poker game.
 */
data class MonsterOpponent(
    val monster: Monster,
    val displayName: String,
    val difficulty: OpponentDifficulty,
    val aggressiveness: Float, // 0.0 to 1.0, how likely to bet/raise
    val bluffFrequency: Float, // 0.0 to 1.0, how often they bluff
    val isRevealed: Boolean = false, // Has player encountered this monster before?
    val isDefeated: Boolean = false, // Has player defeated this monster?
)

/**
 * Opponent difficulty levels.
 */
enum class OpponentDifficulty(val displayName: String, val description: String) {
    EASY("Rookie", "Predictable and passive"),
    MEDIUM("Trainer", "Balanced playstyle"),
    HARD("Expert", "Aggressive and strategic"),
    EXPERT("Master", "Highly skilled with advanced tactics"),
    LEGENDARY("Champion", "Legendary skill with unpredictable strategies"),
}
