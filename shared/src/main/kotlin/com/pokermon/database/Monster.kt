package com.pokermon.database

import com.pokermon.ai.AIPersonality

/**
 * Kotlin-native data class representing a monster that can be encountered, battled, or captured.
 *
 * Provides immutable monster properties with null safety and modern Kotlin patterns.
 * Integrates seamlessly with the unified MonsterDatabase as part of the DRY architecture.
 * Enhanced with comprehensive stats, abilities, and evolution support.
 *
 * @author Carl Nelson (@Gameaday)
 * @version 2.0.0 - Enhanced with battle system
 */
data class Monster(
    val name: String,
    val rarity: Rarity,
    val baseHealth: Int,
    val effectType: EffectType,
    val effectPower: Int,
    val description: String,
    val personality: AIPersonality = AIPersonality.values().random(),
    val stats: MonsterStats,
    val abilities: List<MonsterAbility> = emptyList(),
    val evolutionChain: EvolutionChain? = null,
    val currentHp: Int = stats.effectiveHp,
    val isShiny: Boolean = false,
    val captureLocation: String = "Unknown",
    val trainerId: String? = null
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
        LEGENDARY("Legendary", 5.0),
        ;

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
        ULTIMATE_POWER("Provides multiple powerful effects"),
        ;

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
        description: String,
    ) : this(
        name, rarity, baseHealth, effectType, effectPower, description, 
        AIPersonality.values().random(),
        MonsterStats(baseHealth, 50, 50, 50, 50),
        emptyList(),
        null,
        baseHealth
    )

    /**
     * Calculate effective health based on rarity multiplier.
     */
    val effectiveHealth: Int
        get() = (stats.effectiveHp * rarity.powerMultiplier).toInt()

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
     * Check if monster is ready to evolve
     */
    val canEvolve: Boolean
        get() = evolutionChain?.canEvolve(this) ?: false

    /**
     * Get next evolution if available
     */
    fun evolve(): Monster? = evolutionChain?.evolve(this)

    /**
     * Heal monster to full HP
     */
    fun heal(): Monster = copy(currentHp = stats.effectiveHp)

    /**
     * Take damage in battle
     */
    fun takeDamage(damage: Int): Monster {
        val newHp = kotlin.math.max(0, currentHp - damage)
        return copy(currentHp = newHp)
    }

    /**
     * Check if monster has fainted
     */
    val isFainted: Boolean get() = currentHp <= 0

    /**
     * Get HP percentage for UI displays
     */
    val hpPercentage: Double get() = currentHp.toDouble() / stats.effectiveHp

    /**
     * Learn a new ability if level requirement is met
     */
    fun learnAbility(ability: MonsterAbility): Monster {
        return if (stats.level >= ability.levelRequired && ability !in abilities) {
            copy(abilities = abilities + ability)
        } else {
            this
        }
    }

    /**
     * Gain experience and potentially level up
     */
    fun gainExperience(exp: Int): Monster {
        val newStats = stats.gainExperience(exp)
        return copy(stats = newStats)
    }

    /**
     * Get monster's color theme based on rarity.
     */
    val colorTheme: String
        get() =
            when (rarity) {
                Rarity.COMMON -> "gray"
                Rarity.UNCOMMON -> "green"
                Rarity.RARE -> "blue"
                Rarity.EPIC -> "purple"
                Rarity.LEGENDARY -> "gold"
            }

    /**
     * Create a copy of this monster with modified stats for balancing.
     */
    fun withModifiedStats(
        healthModifier: Int = 0,
        powerModifier: Int = 0,
    ): Monster {
        return copy(
            baseHealth = kotlin.math.max(1, baseHealth + healthModifier),
            effectPower = kotlin.math.max(0, effectPower + powerModifier),
        )
    }

    /**
     * Get monster's AI difficulty rating.
     */
    val aiDifficultyRating: Int
        get() =
            when (rarity) {
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

/**
 * Evolution chain definition for monsters
 */
data class EvolutionChain(
    val species: String,
    val evolutions: List<Evolution>
) {
    fun canEvolve(monster: Monster): Boolean {
        return evolutions.any { it.condition.isMet(monster) }
    }
    
    fun evolve(monster: Monster): Monster? {
        val availableEvolution = evolutions.firstOrNull { it.condition.isMet(monster) }
        return availableEvolution?.createEvolved(monster)
    }
}

/**
 * Individual evolution within a chain
 */
data class Evolution(
    val toSpecies: String,
    val condition: EvolutionCondition,
    val statModifiers: Map<String, Double> = emptyMap()
) {
    fun createEvolved(baseMonster: Monster): Monster {
        // Create evolved monster with enhanced stats
        val newStats = baseMonster.stats.copy(
            baseHp = (baseMonster.stats.baseHp * (statModifiers["hp"] ?: 1.2)).toInt(),
            baseAttack = (baseMonster.stats.baseAttack * (statModifiers["attack"] ?: 1.2)).toInt(),
            baseDefense = (baseMonster.stats.baseDefense * (statModifiers["defense"] ?: 1.2)).toInt(),
            baseSpeed = (baseMonster.stats.baseSpeed * (statModifiers["speed"] ?: 1.2)).toInt(),
            baseSpecial = (baseMonster.stats.baseSpecial * (statModifiers["special"] ?: 1.2)).toInt()
        )
        
        return baseMonster.copy(
            name = toSpecies,
            stats = newStats,
            rarity = if (baseMonster.rarity != Monster.Rarity.LEGENDARY) {
                Monster.Rarity.values()[kotlin.math.min(baseMonster.rarity.ordinal + 1, Monster.Rarity.values().size - 1)]
            } else baseMonster.rarity
        )
    }
}

/**
 * Conditions required for evolution
 */
sealed class EvolutionCondition {
    abstract fun isMet(monster: Monster): Boolean
    
    data class LevelRequirement(val level: Int) : EvolutionCondition() {
        override fun isMet(monster: Monster): Boolean = monster.stats.level >= level
    }
    
    data class ExperienceRequirement(val experience: Int) : EvolutionCondition() {
        override fun isMet(monster: Monster): Boolean = monster.stats.experience >= experience
    }
    
    data class CombinedCondition(val conditions: List<EvolutionCondition>) : EvolutionCondition() {
        override fun isMet(monster: Monster): Boolean = conditions.all { it.isMet(monster) }
    }
}
