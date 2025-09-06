package com.pokermon.modes

import com.pokermon.HandEvaluator.HandType

/**
 * Kotlin-native achievement system for game modes.
 * Provides unified achievement definitions and progress tracking.
 *
 * @author Pokermon Achievement System
 * @version 1.1.0
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val category: AchievementCategory = AchievementCategory.GAMEPLAY,
) {
    companion object {
        // Classic poker achievements
        val FOUR_OF_A_KIND =
            Achievement(
                id = "four_of_a_kind",
                name = "Quadruple Power",
                description = "Win a hand with Four of a Kind",
            )

        val STRAIGHT_FLUSH =
            Achievement(
                id = "straight_flush",
                name = "Perfect Sequence",
                description = "Win a hand with a Straight Flush",
            )

        val ROYAL_FLUSH =
            Achievement(
                id = "royal_flush",
                name = "Royal Victory",
                description = "Win a hand with a Royal Flush",
            )

        val BIG_WIN =
            Achievement(
                id = "big_win",
                name = "High Roller",
                description = "Win a pot worth 1000+ chips",
            )

        val COMEBACK_VICTORY =
            Achievement(
                id = "comeback",
                name = "Phoenix Rising",
                description = "Win a game after being down to 50 chips or less",
            )

        // Get achievement by hand type
        fun getAchievementForHandType(handType: HandType): Achievement? {
            return when (handType) {
                HandType.FOUR_OF_A_KIND -> FOUR_OF_A_KIND
                HandType.STRAIGHT_FLUSH -> STRAIGHT_FLUSH
                HandType.ROYAL_FLUSH -> ROYAL_FLUSH
                else -> null
            }
        }
    }
}

/**
 * Categories for organizing achievements
 */
enum class AchievementCategory {
    GAMEPLAY,
    HANDS,
    STRATEGIC,
    COLLECTION,
    SOCIAL,
}

/**
 * Achievement manager for tracking and unlocking achievements
 */
object AchievementManager {
    private val unlockedAchievements = mutableSetOf<String>()

    fun unlockAchievement(achievement: Achievement): Boolean {
        return if (!unlockedAchievements.contains(achievement.id)) {
            unlockedAchievements.add(achievement.id)
            println("🏆 Achievement Unlocked: ${achievement.name}")
            println("   ${achievement.description}")
            true
        } else {
            false
        }
    }

    fun isUnlocked(achievement: Achievement): Boolean {
        return unlockedAchievements.contains(achievement.id)
    }

    fun getUnlockedCount(): Int = unlockedAchievements.size
}
