package com.pokermon.database

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Test class for MonsterBattleSystem functionality.
 * Tests configurable turn limits and battle mechanics.
 */
class MonsterBattleSystemTest {

    @Test
    fun testDefaultMaxTurns() {
        // Test that default constructor uses DEFAULT_MAX_TURNS
        val battleSystem = MonsterBattleSystem()
        val playerMonster = createTestMonster("Player Monster", 100)
        val enemyMonster = createTestMonster("Enemy Monster", 100)

        val result = battleSystem.executeBattle(playerMonster, enemyMonster)

        assertNotNull(result)
        // Battle should end before or at DEFAULT_MAX_TURNS (50)
        assertTrue(result.turnCount <= MonsterBattleSystem.DEFAULT_MAX_TURNS)
    }

    @Test
    fun testCustomMaxTurns() {
        // Test that custom maxTurns is respected
        val customMaxTurns = 10
        val battleSystem = MonsterBattleSystem(maxTurns = customMaxTurns)
        
        // Create two monsters with very high HP to force a draw
        val playerMonster = createTestMonster("Player Monster", 10000)
        val enemyMonster = createTestMonster("Enemy Monster", 10000)

        val result = battleSystem.executeBattle(playerMonster, enemyMonster)

        assertNotNull(result)
        // Battle should end at or before custom maxTurns
        assertTrue(result.turnCount <= customMaxTurns, 
            "Expected turn count <= $customMaxTurns, but got ${result.turnCount}")
        // Since monsters have very high HP, battle should hit the turn limit
        assertEquals(customMaxTurns, result.turnCount,
            "Expected battle to hit turn limit of $customMaxTurns")
        // Battle should end in a draw (no winner)
        assertTrue(result.isDraw, "Expected battle to end in a draw")
    }

    @Test
    fun testBriefBattleForSafariMode() {
        // Test short battles suitable for Safari mode
        val briefMaxTurns = 5
        val battleSystem = MonsterBattleSystem(maxTurns = briefMaxTurns)
        
        val playerMonster = createTestMonster("Player Monster", 5000)
        val enemyMonster = createTestMonster("Enemy Monster", 5000)

        val result = battleSystem.executeBattle(playerMonster, enemyMonster)

        assertNotNull(result)
        assertTrue(result.turnCount <= briefMaxTurns)
    }

    @Test
    fun testExtendedBattleForBossMode() {
        // Test longer battles suitable for boss encounters
        val extendedMaxTurns = 100
        val battleSystem = MonsterBattleSystem(maxTurns = extendedMaxTurns)
        
        val playerMonster = createTestMonster("Player Monster", 10000)
        val enemyMonster = createTestMonster("Enemy Monster", 10000)

        val result = battleSystem.executeBattle(playerMonster, enemyMonster)

        assertNotNull(result)
        assertTrue(result.turnCount <= extendedMaxTurns)
    }

    @Test
    fun testNormalBattleCompletesBeforeLimit() {
        // Test that normal battles complete within reasonable time
        val battleSystem = MonsterBattleSystem()
        
        // Create monsters with normal HP that should resolve
        val playerMonster = createTestMonster("Player Monster", 50)
        val enemyMonster = createTestMonster("Enemy Monster", 50)

        val result = battleSystem.executeBattle(playerMonster, enemyMonster)

        assertNotNull(result)
        // Normal battle should complete at or before the limit
        assertTrue(result.turnCount <= MonsterBattleSystem.DEFAULT_MAX_TURNS)
        // Battle should either have a winner or be a draw
        assertTrue(result.playerWon || result.enemyWon || result.isDraw)
    }

    @Test
    fun testCompanionConstantValue() {
        // Verify the companion object constant is set correctly
        assertEquals(50, MonsterBattleSystem.DEFAULT_MAX_TURNS)
    }

    /**
     * Helper method to create a test monster
     */
    private fun createTestMonster(name: String, hp: Int): Monster {
        return Monster(
            name = name,
            rarity = Monster.Rarity.COMMON,
            baseHealth = hp,
            effectType = Monster.EffectType.CHIP_BONUS,
            effectPower = 10,
            description = "Test monster",
            stats = MonsterStats(
                baseHp = hp,
                baseAttack = 20,
                baseDefense = 15,
                baseSpeed = 10,
                baseSpecial = 10,
                level = 1
            ),
            abilities = listOf(
                MonsterAbility(
                    name = "Test Attack",
                    description = "A test attack",
                    type = MoveType.PHYSICAL,
                    basePower = 10,
                    accuracy = 1.0,
                    ppCost = 0,
                    levelRequired = 1
                )
            )
        )
    }
}
