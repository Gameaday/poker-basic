package com.pokermon.ai

import kotlin.math.max
import kotlin.math.min

// Forward declaration for GameContext used in methods

/**
 * Defines the 24 distinct personalities that influence AI behavior across all game modes.
 * Each personality has unique weights for generalized traits that can be combined
 * to determine specific behaviors in different contexts.
 *
 * Values range from 0.0 (almost never) to 10.0 (almost always).
 * Most values should be in the 2.0-8.0 range to keep gameplay interesting.
 *
 * @author Pokermon AI System
 * @version 1.0.0 (Kotlin-native)
 */
enum class AIPersonality(
    val displayName: String,
    val courage: Float, // Willingness to take risks and face challenges
    val gullibility: Float, // How easily influenced or deceived
    val guile: Float, // Cunning, deceptiveness, tactical cleverness
    val confidence: Float, // Self-assurance and belief in abilities
    val caution: Float, // Careful consideration and prudence
    val empathy: Float, // Ability to understand and read others
    val timidness: Float, // Tendency to avoid confrontation or risk
    val patience: Float, // Tolerance for waiting and deliberation
    val ambition: Float, // Drive to succeed and take initiative
    val intelligence: Float, // Analytical thinking and strategic planning
) {
    FOOLHARDY("Foolhardy", 9.0f, 3.0f, 8.0f, 7.0f, 2.0f, 7.0f, 1.0f, 3.0f, 8.5f, 4.0f),
    GULLIBLE("Gullible", 4.0f, 8.5f, 3.0f, 4.0f, 7.0f, 6.0f, 6.0f, 5.0f, 3.0f, 5.0f),
    BRASH("Brash", 8.5f, 4.0f, 7.5f, 8.0f, 3.0f, 5.0f, 2.0f, 4.0f, 8.0f, 6.0f),
    PENSIVE("Pensive", 4.0f, 3.0f, 4.0f, 3.5f, 8.5f, 8.0f, 8.0f, 7.5f, 4.0f, 8.5f),
    MEEK("Meek", 2.5f, 6.0f, 2.5f, 2.0f, 6.0f, 7.0f, 8.5f, 6.0f, 2.0f, 5.0f),
    ANXIOUS("Anxious", 3.0f, 5.0f, 3.0f, 2.5f, 7.0f, 6.5f, 8.0f, 5.5f, 3.5f, 6.0f),
    HAPPY("Happy", 6.5f, 5.0f, 6.5f, 6.0f, 5.0f, 7.5f, 3.0f, 6.0f, 6.5f, 6.0f),
    DOUBTFUL("Doubtful", 3.5f, 4.0f, 3.5f, 3.0f, 7.5f, 6.0f, 7.0f, 6.5f, 3.0f, 7.0f),
    TRUSTING("Trusting", 5.5f, 7.5f, 5.5f, 5.0f, 4.0f, 8.0f, 4.0f, 7.0f, 5.0f, 6.0f),
    BLISSFUL("Blissful", 7.5f, 6.0f, 7.5f, 7.0f, 2.0f, 8.5f, 2.0f, 5.0f, 7.0f, 4.0f),
    UNAWARE("Unaware", 5.5f, 8.0f, 6.0f, 5.5f, 2.5f, 6.0f, 4.0f, 3.0f, 5.0f, 3.5f),
    INSINCERE("Insincere", 6.0f, 2.0f, 8.5f, 7.5f, 6.0f, 4.0f, 5.0f, 4.0f, 6.5f, 7.5f),
    SHY("Shy", 2.0f, 4.0f, 2.0f, 2.5f, 6.5f, 6.0f, 8.5f, 5.5f, 2.5f, 5.0f),
    BRAINY("Brainy", 4.5f, 2.5f, 5.0f, 4.5f, 9.0f, 7.0f, 6.0f, 8.0f, 4.0f, 9.5f),
    MUSCLE_HEADED("Muscle-headed", 8.5f, 6.0f, 4.0f, 8.0f, 2.5f, 3.0f, 1.5f, 3.0f, 8.0f, 3.0f),
    FIGHTER("Fighter", 8.0f, 3.0f, 6.5f, 7.5f, 5.0f, 5.0f, 3.0f, 5.5f, 7.5f, 6.0f),
    LIVELY("Lively", 7.5f, 5.5f, 7.0f, 7.0f, 4.0f, 7.0f, 2.5f, 7.5f, 7.0f, 6.5f),
    INDECISIVE("Indecisive", 4.0f, 5.0f, 4.5f, 4.0f, 6.0f, 5.0f, 6.5f, 4.0f, 4.0f, 6.0f),
    DEFENSIVE("Defensive", 3.0f, 3.5f, 3.0f, 3.5f, 7.0f, 5.0f, 7.5f, 6.0f, 3.0f, 6.5f),
    SELF_ASSURED("Self-assured", 7.5f, 3.0f, 7.0f, 8.0f, 6.0f, 6.0f, 2.5f, 6.5f, 7.0f, 7.0f),
    CONFIDENT("Confident", 8.0f, 3.5f, 6.5f, 8.5f, 6.5f, 6.5f, 2.0f, 7.0f, 7.5f, 7.5f),
    SMARMY("Smarmy", 6.0f, 2.5f, 8.0f, 7.0f, 7.0f, 4.5f, 4.0f, 5.0f, 6.0f, 7.0f),
    CONDESCENDING("Condescending", 5.5f, 2.0f, 7.5f, 8.0f, 7.5f, 4.0f, 3.5f, 4.5f, 5.5f, 8.0f),
    HUMBLE("Humble", 4.5f, 5.0f, 3.0f, 4.0f, 6.5f, 7.5f, 6.0f, 7.0f, 4.0f, 6.5f),
    ;

    // Poker-specific behavior calculations using multiple trait combinations

    /**
     * Calculate aggressiveness for poker based on multiple traits.
     * @return effective aggressiveness (0.0-10.0)
     */
    val aggressiveness: Float
        get() = clamp(courage * 0.4f + ambition * 0.3f + confidence * 0.3f)

    /**
     * Calculate bluff tendency for poker based on multiple traits.
     * @return effective bluff tendency (0.0-10.0)
     */
    val bluffTendency: Float
        get() = clamp(guile * 0.5f + confidence * 0.3f + courage * 0.2f)

    /**
     * Calculate fold tendency for poker based on multiple traits.
     * @return effective fold tendency (0.0-10.0)
     */
    val foldTendency: Float
        get() = clamp(timidness * 0.4f + caution * 0.3f + (10.0f - confidence) * 0.3f)

    /**
     * Calculate deception ability for poker based on multiple traits.
     * @return effective deception (0.0-10.0)
     */
    val deception: Float
        get() = clamp(guile * 0.6f + intelligence * 0.2f + (10.0f - empathy) * 0.2f)

    /**
     * Calculate aggression level for betting decisions.
     * @return effective aggression (0.0-10.0)
     */
    val aggression: Float
        get() = clamp(courage * 0.4f + confidence * 0.3f + ambition * 0.3f - timidness * 0.2f)

    /**
     * Calculate cautiousness level for risk assessment.
     * @return effective cautiousness (0.0-10.0)
     */
    val cautiousness: Float
        get() = clamp(caution * 0.6f + intelligence * 0.2f + timidness * 0.2f)

    /**
     * Calculate risk tolerance based on personality traits.
     */
    fun calculateRiskTolerance(context: Any): Double {
        val baseRisk = (courage + confidence - caution - timidness) / 40.0
        return baseRisk.coerceIn(0.0, 1.0)
    }

    /**
     * Calculate confidence level based on hand strength and chips.
     */
    fun calculateConfidence(
        handStrength: Double,
        chips: Int,
    ): Double {
        val baseConfidence = confidence / 10.0
        val handBonus = handStrength * 0.3
        val chipBonus =
            if (chips > 1000) {
                0.1
            } else if (chips < 200) {
                -0.1
            } else {
                0.0
            }
        return (baseConfidence + handBonus + chipBonus).coerceIn(0.0, 1.0)
    }

    /**
     * Calculate maximum call amount based on pot size and personality.
     */
    fun calculateMaxCallAmount(potSize: Int): Int {
        val riskFactor = (courage + confidence - caution) / 30.0
        return (potSize * riskFactor).toInt().coerceAtLeast(10)
    }

    companion object {
        /**
         * Clamps a value to the 0.0-10.0 range.
         */
        private fun clamp(value: Float): Float = max(0.0f, min(10.0f, value))

        /**
         * Get a random personality for monster generation.
         * @return a randomly selected personality
         */
        fun getRandomPersonality(): AIPersonality = values().random()

        /**
         * Get a personality by name (case-insensitive).
         * @param name the personality name to find
         * @return the matching personality, or null if not found
         */
        fun getByName(name: String?): AIPersonality? {
            name ?: return null

            return values().find { personality ->
                personality.displayName.equals(name, ignoreCase = true) ||
                    personality.name.equals(name, ignoreCase = true)
            }
        }
    }

    override fun toString(): String = displayName
}
