package com.pokermon.ai

/**
 * Represents the core traits that define a personality using Kotlin-native patterns.
 * These traits can be reused across different game modes and provide
 * a modular way to create personality variations.
 * 
 * Each trait is scored from 0.0 to 10.0.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
data class PersonalityTraits(
    val bravery: Float,       // How willing to take risks
    val tenacity: Float,      // How persistent and stubborn
    val intelligence: Float,  // How strategic and analytical
    val confidence: Float,    // How self-assured
    val tactfulness: Float,   // How diplomatic and subtle
    val empathy: Float,       // How well they read others
    val patience: Float,      // How tolerant of slow play
    val adaptability: Float   // How well they adjust strategies
) {
    
    init {
        // Validate all traits are in valid range during construction
        require(bravery in 0.0f..10.0f) { "Bravery must be between 0.0 and 10.0" }
        require(tenacity in 0.0f..10.0f) { "Tenacity must be between 0.0 and 10.0" }
        require(intelligence in 0.0f..10.0f) { "Intelligence must be between 0.0 and 10.0" }
        require(confidence in 0.0f..10.0f) { "Confidence must be between 0.0 and 10.0" }
        require(tactfulness in 0.0f..10.0f) { "Tactfulness must be between 0.0 and 10.0" }
        require(empathy in 0.0f..10.0f) { "Empathy must be between 0.0 and 10.0" }
        require(patience in 0.0f..10.0f) { "Patience must be between 0.0 and 10.0" }
        require(adaptability in 0.0f..10.0f) { "Adaptability must be between 0.0 and 10.0" }
    }

    companion object {
        /**
         * Creates personality traits from a Personality enum.
         * This maps the personality's generalized traits to the trait system.
         */
        fun fromPersonality(personality: Personality): PersonalityTraits {
            return PersonalityTraits(
                bravery = personality.courage,
                tenacity = (personality.confidence + (10.0f - personality.timidness)) / 2.0f,
                intelligence = personality.intelligence,
                confidence = personality.confidence,
                tactfulness = (personality.guile + personality.caution) / 2.0f,
                empathy = personality.empathy,
                patience = personality.patience,
                adaptability = (personality.guile + personality.intelligence) / 2.0f
            )
        }

        /**
         * Clamps a trait value to the valid range of 0.0-10.0.
         */
        fun clamp(value: Float): Float = value.coerceIn(0.0f, 10.0f)
    }

    /**
     * Calculate an overall "strength" score based on all traits.
     * This can be used for opponent difficulty assessment.
     * 
     * @return overall strength score (0.0-10.0)
     */
    val overallStrength: Float
        get() = (bravery + tenacity + intelligence + confidence + 
                tactfulness + empathy + patience + adaptability) / 8.0f

    /**
     * Creates a modified version of these traits with multipliers applied.
     * Useful for monster rarity bonuses or special circumstances.
     * 
     * @param multiplier the factor to apply to all traits
     * @return new PersonalityTraits with modified values
     */
    fun applyMultiplier(multiplier: Float): PersonalityTraits {
        return PersonalityTraits(
            bravery = clamp(bravery * multiplier),
            tenacity = clamp(tenacity * multiplier),
            intelligence = clamp(intelligence * multiplier),
            confidence = clamp(confidence * multiplier),
            tactfulness = clamp(tactfulness * multiplier),
            empathy = clamp(empathy * multiplier),
            patience = clamp(patience * multiplier),
            adaptability = clamp(adaptability * multiplier)
        )
    }

    /**
     * Creates a trait combination that emphasizes specific aspects.
     * Useful for creating specialized AI behaviors.
     */
    fun emphasize(
        braveryBoost: Float = 1.0f,
        tenacityBoost: Float = 1.0f,
        intelligenceBoost: Float = 1.0f,
        confidenceBoost: Float = 1.0f,
        tactfulnessBoost: Float = 1.0f,
        empathyBoost: Float = 1.0f,
        patienceBoost: Float = 1.0f,
        adaptabilityBoost: Float = 1.0f
    ): PersonalityTraits {
        return PersonalityTraits(
            bravery = clamp(bravery * braveryBoost),
            tenacity = clamp(tenacity * tenacityBoost),
            intelligence = clamp(intelligence * intelligenceBoost),
            confidence = clamp(confidence * confidenceBoost),
            tactfulness = clamp(tactfulness * tactfulnessBoost),
            empathy = clamp(empathy * empathyBoost),
            patience = clamp(patience * patienceBoost),
            adaptability = clamp(adaptability * adaptabilityBoost)
        )
    }

    override fun toString(): String {
        return "PersonalityTraits(bravery=%.1f, tenacity=%.1f, intelligence=%.1f, " +
                "confidence=%.1f, tactfulness=%.1f, empathy=%.1f, patience=%.1f, adaptability=%.1f)".format(
                    bravery, tenacity, intelligence, confidence, tactfulness, empathy, patience, adaptability
                )
    }
}