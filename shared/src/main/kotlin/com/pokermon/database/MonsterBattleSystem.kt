package com.pokermon.database

import kotlin.math.max
import kotlin.random.Random

/**
 * Unified monster battle system for all game modes.
 * Handles turn-based combat, ability usage, and battle resolution.
 * Integrates poker hand strength with monster battle mechanics.
 * 
 * @author Pokermon Battle System
 * @version 1.0.0
 */
class MonsterBattleSystem {
    private val random = Random.Default
    
    /**
     * Execute a complete battle between two monsters
     */
    fun executeBattle(
        playerMonster: Monster,
        enemyMonster: Monster,
        playerHandStrength: Int = 0
    ): BattleResult {
        var player = playerMonster.heal()
        var enemy = enemyMonster.heal()
        val battleLog = mutableListOf<BattleAction>()
        var turnCount = 0
        val maxTurns = 50 // Prevent infinite battles
        
        // Apply poker hand strength bonus to player monster
        val handBonus = calculateHandBonus(playerHandStrength)
        if (handBonus > 0) {
            battleLog.add(BattleAction.Message("Poker hand strength grants attack bonus: +${handBonus}%"))
        }
        
        while (!player.isFainted && !enemy.isFainted && turnCount < maxTurns) {
            turnCount++
            
            // Determine turn order based on speed
            val playerFirst = player.stats.effectiveSpeed >= enemy.stats.effectiveSpeed
            
            if (playerFirst) {
                val playerAction = executePlayerTurn(player, enemy, handBonus)
                battleLog.add(playerAction)
                if (playerAction is BattleAction.Attack) {
                    enemy = playerAction.target
                }
                
                if (!enemy.isFainted) {
                    val enemyAction = executeEnemyTurn(enemy, player)
                    battleLog.add(enemyAction)
                    if (enemyAction is BattleAction.Attack) {
                        player = enemyAction.target
                    }
                }
            } else {
                val enemyAction = executeEnemyTurn(enemy, player)
                battleLog.add(enemyAction)
                if (enemyAction is BattleAction.Attack) {
                    player = enemyAction.target
                }
                
                if (!player.isFainted) {
                    val playerAction = executePlayerTurn(player, enemy, handBonus)
                    battleLog.add(playerAction)
                    if (playerAction is BattleAction.Attack) {
                        enemy = playerAction.target
                    }
                }
            }
        }
        
        return BattleResult(
            playerMonster = player,
            enemyMonster = enemy,
            winner = when {
                player.isFainted -> enemy
                enemy.isFainted -> player
                else -> null // Draw or timeout
            },
            battleLog = battleLog,
            turnCount = turnCount,
            experienceGained = if (enemy.isFainted) calculateExpReward(enemy) else 0
        )
    }
    
    private fun executePlayerTurn(player: Monster, enemy: Monster, handBonus: Int): BattleAction {
        // For now, always use basic attack. Could be enhanced with ability selection
        val ability = getBasicAttackAbility()
        val baseDamage = player.stats.calculateDamage(enemy.stats, ability.type)
        val bonusDamage = (baseDamage * handBonus) / 100
        val totalDamage = baseDamage + bonusDamage
        
        val newEnemy = enemy.takeDamage(totalDamage)
        return BattleAction.Attack(
            attacker = player,
            target = newEnemy,
            ability = ability,
            damage = totalDamage,
            message = "${player.name} attacks ${enemy.name} for $totalDamage damage!"
        )
    }
    
    private fun executeEnemyTurn(enemy: Monster, player: Monster): BattleAction {
        val ability = enemy.abilities.randomOrNull() ?: getBasicAttackAbility()
        val damage = enemy.stats.calculateDamage(player.stats, ability.type)
        
        val newPlayer = player.takeDamage(damage)
        return BattleAction.Attack(
            attacker = enemy,
            target = newPlayer,
            ability = ability,
            damage = damage,
            message = "${enemy.name} attacks ${player.name} for $damage damage!"
        )
    }
    
    private fun calculateHandBonus(handStrength: Int): Int {
        return when {
            handStrength > 900 -> 50  // Royal flush bonus
            handStrength > 800 -> 40  // Straight flush bonus
            handStrength > 700 -> 30  // Four of a kind bonus
            handStrength > 600 -> 25  // Full house bonus
            handStrength > 500 -> 20  // Flush bonus
            handStrength > 400 -> 15  // Straight bonus
            handStrength > 300 -> 10  // Three of a kind bonus
            handStrength > 200 -> 5   // Two pair bonus
            else -> 0
        }
    }
    
    private fun calculateExpReward(defeatedMonster: Monster): Int {
        val baseExp = when (defeatedMonster.rarity) {
            Monster.Rarity.COMMON -> 50
            Monster.Rarity.UNCOMMON -> 75
            Monster.Rarity.RARE -> 125
            Monster.Rarity.EPIC -> 200
            Monster.Rarity.LEGENDARY -> 350
        }
        
        return baseExp + (defeatedMonster.stats.level * 5)
    }
    
    private fun getBasicAttackAbility(): MonsterAbility {
        return MonsterAbility(
            name = "Basic Attack",
            description = "A standard physical attack",
            type = MoveType.PHYSICAL,
            basePower = 40,
            accuracy = 1.0,
            ppCost = 0,
            levelRequired = 1
        )
    }
}

/**
 * Result of a monster battle
 */
data class BattleResult(
    val playerMonster: Monster,
    val enemyMonster: Monster,
    val winner: Monster?,
    val battleLog: List<BattleAction>,
    val turnCount: Int,
    val experienceGained: Int
) {
    val playerWon: Boolean get() = winner == playerMonster
    val enemyWon: Boolean get() = winner == enemyMonster
    val isDraw: Boolean get() = winner == null
}

/**
 * Individual actions that happen during battle
 */
sealed class BattleAction {
    data class Attack(
        val attacker: Monster,
        val target: Monster,
        val ability: MonsterAbility,
        val damage: Int,
        val message: String
    ) : BattleAction()
    
    data class Heal(
        val target: Monster,
        val amount: Int,
        val message: String
    ) : BattleAction()
    
    data class StatusEffect(
        val target: Monster,
        val effect: String,
        val message: String
    ) : BattleAction()
    
    data class Message(val text: String) : BattleAction()
}

/**
 * Monster battle arena with environmental effects
 */
enum class BattleArena(val displayName: String, val effect: ArenaEffect) {
    GRASSLAND("Grassland", ArenaEffect.None),
    VOLCANO("Volcano", ArenaEffect.SpeedBoost),
    OCEAN("Ocean", ArenaEffect.DefenseBoost),
    MOUNTAIN("Mountain", ArenaEffect.AttackBoost),
    FOREST("Forest", ArenaEffect.HealingBoost),
    CAVE("Cave", ArenaEffect.SpecialBoost)
}

/**
 * Environmental effects in battle arenas
 */
sealed class ArenaEffect {
    object None : ArenaEffect()
    object AttackBoost : ArenaEffect()
    object DefenseBoost : ArenaEffect()
    object SpeedBoost : ArenaEffect()
    object SpecialBoost : ArenaEffect()
    object HealingBoost : ArenaEffect()
}