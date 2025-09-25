package com.pokermon.database

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Enhanced monster statistics system supporting battling, training, and evolution.
 * Provides comprehensive stat tracking with experience and leveling mechanics.
 * 
 * @author Pokermon Monster System
 * @version 1.0.0
 */
data class MonsterStats(
    val baseHp: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val baseSpeed: Int,
    val baseSpecial: Int,
    val level: Int = 1,
    val experience: Int = 0,
    val nature: MonsterNature = MonsterNature.BALANCED
) {
    companion object {
        private const val EXP_CURVE_BASE = 100
        private const val STAT_GROWTH_FACTOR = 2.0
        private const val MAX_LEVEL = 100
        
        fun expToNextLevel(level: Int): Int {
            if (level >= MAX_LEVEL) return 0
            return (level * level * EXP_CURVE_BASE) / 10
        }
        
        fun totalExpForLevel(level: Int): Int {
            var total = 0
            for (i in 1 until level) {
                total += expToNextLevel(i)
            }
            return total
        }
    }
    
    // Calculate effective stats with level, nature, and IV bonuses
    val effectiveHp: Int get() = calculateStat(baseHp, level, nature.hpModifier)
    val effectiveAttack: Int get() = calculateStat(baseAttack, level, nature.attackModifier) 
    val effectiveDefense: Int get() = calculateStat(baseDefense, level, nature.defenseModifier)
    val effectiveSpeed: Int get() = calculateStat(baseSpeed, level, nature.speedModifier)
    val effectiveSpecial: Int get() = calculateStat(baseSpecial, level, nature.specialModifier)
    
    val expToNextLevel: Int get() = expToNextLevel(level)
    val expProgress: Double get() {
        if (level >= MAX_LEVEL) return 1.0
        val currentLevelExp = totalExpForLevel(level)
        val nextLevelExp = totalExpForLevel(level + 1)
        return (experience - currentLevelExp).toDouble() / (nextLevelExp - currentLevelExp)
    }
    
    val canLevelUp: Boolean get() = level < MAX_LEVEL && experience >= totalExpForLevel(level + 1)
    
    private fun calculateStat(baseStat: Int, level: Int, natureModifier: Double): Int {
        val growthBonus = (baseStat * STAT_GROWTH_FACTOR * (level - 1)) / MAX_LEVEL
        val natureBonus = baseStat * (natureModifier - 1.0)
        return max(1, (baseStat + growthBonus + natureBonus).toInt())
    }
    
    /**
     * Level up the monster, returning new stats
     */
    fun levelUp(): MonsterStats {
        if (!canLevelUp) return this
        return copy(level = level + 1)
    }
    
    /**
     * Add experience, potentially leveling up
     */
    fun gainExperience(exp: Int): MonsterStats {
        val newExp = experience + exp
        var newStats = copy(experience = newExp)
        
        // Handle multiple level ups
        while (newStats.canLevelUp) {
            newStats = newStats.levelUp()
        }
        
        return newStats
    }
    
    /**
     * Calculate damage in battle based on attacker/defender stats
     */
    fun calculateDamage(defender: MonsterStats, moveType: MoveType): Int {
        val baseDamage = when (moveType) {
            MoveType.PHYSICAL -> this.effectiveAttack - defender.effectiveDefense
            MoveType.SPECIAL -> this.effectiveSpecial - defender.effectiveDefense
            MoveType.STATUS -> 0
        }
        
        val levelModifier = (2.0 * this.level + 10) / 250.0
        val randomFactor = Random.nextDouble(0.85, 1.0)
        
        return max(1, (baseDamage * levelModifier * randomFactor).toInt())
    }
}

/**
 * Monster nature affects stat growth and battle behavior
 */
enum class MonsterNature(
    val displayName: String,
    val hpModifier: Double,
    val attackModifier: Double,
    val defenseModifier: Double,
    val speedModifier: Double,
    val specialModifier: Double
) {
    HARDY("Hardy", 1.0, 1.0, 1.0, 1.0, 1.0),
    BOLD("Bold", 1.1, 0.9, 1.1, 1.0, 1.0),
    MODEST("Modest", 1.0, 0.9, 1.0, 1.0, 1.1),
    ADAMANT("Adamant", 1.0, 1.1, 1.0, 1.0, 0.9),
    IMPISH("Impish", 1.0, 0.9, 1.1, 1.0, 1.0),
    CAREFUL("Careful", 1.0, 1.0, 0.9, 1.0, 1.1),
    JOLLY("Jolly", 1.0, 1.0, 1.0, 1.1, 0.9),
    TIMID("Timid", 1.0, 0.9, 1.0, 1.1, 1.0),
    HASTY("Hasty", 1.0, 1.0, 0.9, 1.1, 1.0),
    NAIVE("Naive", 1.0, 1.0, 0.9, 1.1, 1.0),
    BRAVE("Brave", 1.1, 1.1, 1.0, 0.9, 1.0),
    RELAXED("Relaxed", 1.1, 1.0, 1.1, 0.9, 1.0),
    QUIET("Quiet", 1.1, 1.0, 1.0, 0.9, 1.1),
    CALM("Calm", 1.1, 0.9, 1.0, 1.0, 1.1),
    GENTLE("Gentle", 1.1, 0.9, 1.1, 1.0, 1.0),
    SASSY("Sassy", 1.1, 1.0, 1.1, 0.9, 1.0),
    BALANCED("Balanced", 1.05, 1.05, 1.05, 1.05, 1.05);
}

/**
 * Types of moves/abilities monsters can use
 */
enum class MoveType {
    PHYSICAL,  // Uses Attack vs Defense
    SPECIAL,   // Uses Special vs Defense  
    STATUS     // No damage, applies effects
}

/**
 * Monster abilities that can be learned and used in battle
 */
data class MonsterAbility(
    val name: String,
    val description: String,
    val type: MoveType,
    val basePower: Int,
    val accuracy: Double,
    val ppCost: Int,
    val levelRequired: Int = 1,
    val effect: AbilityEffect? = null
)

/**
 * Effects that abilities can apply
 */
sealed class AbilityEffect {
    data class StatModifier(val stat: String, val modifier: Double, val duration: Int) : AbilityEffect()
    data class Healing(val amount: Int) : AbilityEffect()
    data class StatusCondition(val condition: String, val duration: Int) : AbilityEffect()
    object CriticalHit : AbilityEffect()
}