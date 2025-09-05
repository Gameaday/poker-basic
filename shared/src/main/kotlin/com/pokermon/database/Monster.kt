package com.pokermon.database

import com.pokermon.ai.AIPersonality

/**
 * Kotlin-native data class representing a monster that can be encountered, battled, or captured.
 * 
 * Provides immutable monster properties with null safety and modern Kotlin patterns.
 * Integrates seamlessly with the unified MonsterDatabase as part of the DRY architecture.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
data class Monster(
    val name: String,
    val rarity: Rarity,
    val baseHealth: Int,
    val effectType: EffectType,
    val effectPower: Int,
    val description: String,
    val personality: AIPersonality = AIPersonality.values().random()
) {
    
    /**
     * Sealed class defining monster rarity levels with Kotlin-native power multipliers.
     * Provides type safety and enhanced functionality over Java enums.
     */
    enum class Rarity(val displayName: String, val powerMultiplier: Double) {
        COMMON("Common", 1.0),
        UNCOMMON("Uncommon", 1.5),
        RARE("Rare", 2.0),
        EPIC("Epic", 3.0),
        LEGENDARY("Legendary", 5.0);
        
        // Enhanced Kotlin functionality
        val isRare: Boolean get() = this >= RARE
        val isCommon: Boolean get() = this == COMMON
        val isLegendary: Boolean get() = this == LEGENDARY
    }
    
    /**
     * Enhanced effect types with additional gameplay mechanics.
     */
    enum class EffectType(val description: String) {
        CHIP_BONUS("Increases starting chips"),
        CARD_ADVANTAGE("Provides extra card draws"),
        BETTING_BOOST("Improves betting effectiveness"),
        LUCK_ENHANCEMENT("Increases chance of good hands"),
        VISUAL_THEME("Changes game appearance and theme"),
        DEFENSIVE_SHIELD("Provides protection from losses"),
        AI_ENHANCEMENT("Improves AI decision making"),
        ULTIMATE_POWER("Provides multiple powerful effects");
        
        // Enhanced Kotlin functionality
        val isOffensive: Boolean get() = this in listOf(CHIP_BONUS, BETTING_BOOST, LUCK_ENHANCEMENT)
        val isDefensive: Boolean get() = this in listOf(DEFENSIVE_SHIELD, CARD_ADVANTAGE)
        val isUtility: Boolean get() = this in listOf(VISUAL_THEME, AI_ENHANCEMENT, ULTIMATE_POWER)
    }
    
    /**
     * Constructor for Java compatibility during migration.
     */
    constructor(
        name: String,
        rarity: Rarity,
        baseHealth: Int,
        effectType: EffectType,
        effectPower: Int,
        description: String
    ) : this(name, rarity, baseHealth, effectType, effectPower, description, AIPersonality.values().random())
    
    /**
     * Calculate effective health based on rarity multiplier.
     */
    val effectiveHealth: Int
        get() = (baseHealth * rarity.powerMultiplier).toInt()
    
    /**
     * Calculate effective power based on rarity multiplier.
     */
    val effectivePower: Int
        get() = (effectPower * rarity.powerMultiplier).toInt()
    
    /**
     * Get monster's battle value for poker integration.
     */
    val battleValue: Int
        get() = effectiveHealth + effectivePower
    
    /**
     * Check if monster can provide specific effect.
     */
    fun canProvideEffect(effect: EffectType): Boolean = effectType == effect
    
    /**
     * Get monster's display string for UI.
     */
    val displayString: String
        get() = "$name (${rarity.displayName}) - $description"
    
    /**
     * Get short description for compact displays.
     */
    val shortDescription: String
        get() = "$name: ${effectType.description}"
    
    /**
     * Check if this is a boss-level monster.
     */
    val isBoss: Boolean
        get() = rarity.isRare && effectiveHealth > 200
    
    /**
     * Get monster's color theme based on rarity.
     */
    val colorTheme: String
        get() = when (rarity) {
            Rarity.COMMON -> "gray"
            Rarity.UNCOMMON -> "green"
            Rarity.RARE -> "blue"
            Rarity.EPIC -> "purple"
            Rarity.LEGENDARY -> "gold"
        }
    
    /**
     * Create a copy of this monster with modified stats for balancing.
     */
    fun withModifiedStats(healthModifier: Int = 0, powerModifier: Int = 0): Monster {
        return copy(
            baseHealth = maxOf(1, baseHealth + healthModifier),
            effectPower = maxOf(0, effectPower + powerModifier)
        )
    }
    
    /**
     * Get monster's AI difficulty rating.
     */
    val aiDifficultyRating: Int
        get() = when (rarity) {
            Rarity.COMMON -> 1
            Rarity.UNCOMMON -> 2
            Rarity.RARE -> 3
            Rarity.EPIC -> 4
            Rarity.LEGENDARY -> 5
        }
    
    /**
     * Enhanced toString for debugging and logging.
     */
    override fun toString(): String {
        return "Monster(name='$name', rarity=$rarity, health=$effectiveHealth, power=$effectivePower, effect=${effectType.description})"
    }
}