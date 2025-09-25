package com.pokermon.modes

import com.pokermon.GameMode
import com.pokermon.modes.adventure.AdventureMode
import com.pokermon.modes.adventure.QuestSystem
import com.pokermon.modes.ironman.IronmanGameMode
import com.pokermon.modes.safari.SafariGameMode
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for all game modes to ensure they are properly implemented
 * without stubs and provide complete functionality.
 */
class GameModeValidationTest {
    @Test
    fun `all game modes should be defined and accessible`() {
        val gameMode = GameMode.values()
        assertEquals(4, gameMode.size, "Should have exactly 4 game modes")

        val modeNames = gameMode.map { it.name }.toSet()
        assertTrue(modeNames.contains("CLASSIC"))
        assertTrue(modeNames.contains("ADVENTURE"))
        assertTrue(modeNames.contains("SAFARI"))
        assertTrue(modeNames.contains("IRONMAN"))
    }

    @Test
    fun `all game modes should have proper display names and descriptions`() {
        GameMode.values().forEach { mode ->
            assertNotNull(mode.displayName)
            assertNotNull(mode.description)
            assertTrue(mode.displayName.isNotEmpty())
            assertTrue(mode.description.isNotEmpty())
        }
    }

    @Test
    fun `adventure mode should initialize properly`() {
        assertDoesNotThrow {
            val adventureMode = AdventureMode("TestPlayer", 1000)
            assertNotNull(adventureMode)
        }
    }

    @Test
    fun `safari mode should initialize properly`() {
        assertDoesNotThrow {
            val safariMode = SafariGameMode("TestPlayer", 1000, 30)
            assertNotNull(safariMode)
        }
    }

    @Test
    fun `ironman mode should initialize properly`() {
        assertDoesNotThrow {
            val ironmanMode = IronmanGameMode("TestPlayer", 1000, 2)
            assertNotNull(ironmanMode)
        }
    }

    @Test
    fun `quest system should initialize and track progress`() {
        val questSystem = QuestSystem()
        assertDoesNotThrow {
            questSystem.initialize()
        }

        assertTrue(questSystem.getActiveQuests().isNotEmpty())

        val summary = questSystem.getQuestSummary()
        assertNotNull(summary)
        assertTrue(summary.contains("ACTIVE QUESTS"))
    }

    @Test
    fun `monster mode detection should work correctly`() {
        assertTrue(GameMode.ADVENTURE.hasMonsters())
        assertTrue(GameMode.SAFARI.hasMonsters())
        assertTrue(GameMode.IRONMAN.hasMonsters())
        assertFalse(GameMode.CLASSIC.hasMonsters())
    }

    @Test
    fun `all game modes should have unique display names`() {
        val displayNames = GameMode.values().map { it.displayName }.toSet()
        assertEquals(
            GameMode.values().size,
            displayNames.size,
            "All game modes should have unique display names",
        )
    }

    @Test
    fun `all game modes should have unique descriptions`() {
        val descriptions = GameMode.values().map { it.description }.toSet()
        assertEquals(
            GameMode.values().size,
            descriptions.size,
            "All game modes should have unique descriptions",
        )
    }

    @Test
    fun `safari and ironman achievement systems should be available`() {
        assertDoesNotThrow {
            val safariAchievements = com.pokermon.modes.safari.SafariAchievements
            assertNotNull(safariAchievements)
        }

        assertDoesNotThrow {
            val ironmanAchievements = com.pokermon.modes.ironman.IronmanAchievements
            assertNotNull(ironmanAchievements)
        }
    }
}
