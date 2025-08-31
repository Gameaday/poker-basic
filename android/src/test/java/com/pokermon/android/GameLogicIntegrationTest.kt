package com.pokermon.android

import com.pokermon.GameMode
import com.pokermon.bridge.GameLogicBridge
import org.junit.Test
import org.junit.Assert.*

/**
 * Integration test to verify Android module can access shared game logic.
 */
class GameLogicIntegrationTest {

    @Test
    fun testGameModeEnumAccess() {
        // Test that we can access GameMode enum from shared logic
        val classicMode = GameMode.CLASSIC
        assertEquals("Classic Poker", classicMode.displayName)
        assertEquals("Traditional poker gameplay with betting and card exchange", classicMode.description)
        assertFalse(classicMode.hasMonsters())
        
        val adventureMode = GameMode.ADVENTURE
        assertEquals("Adventure Mode", adventureMode.displayName)
        assertTrue(adventureMode.hasMonsters())
    }

    @Test
    fun testGameLogicBridgeInitialization() {
        // Test that we can create and use GameLogicBridge
        val gameBridge = GameLogicBridge()
        
        // Test game initialization
        val success = gameBridge.initializeGame("TestPlayer", 3, 1000)
        assertTrue("Game should initialize successfully", success)
        
        // Test getting game info
        val gameInfo = gameBridge.getGameInfo()
        assertEquals("Player should have correct starting chips", 1000, gameInfo.playerChips)
        assertEquals("Initial pot should be 0", 0, gameInfo.currentPot)
        assertNotNull("Player hand should not be null", gameInfo.playerHand)
    }

    @Test
    fun testGameActions() {
        // Test that game actions work through the bridge
        val gameBridge = GameLogicBridge()
        gameBridge.initializeGame("TestPlayer", 3, 1000)
        
        // Test call action
        val callResult = gameBridge.performCall()
        assertTrue("Call should succeed", callResult.success)
        assertNotNull("Call should have a message", callResult.message)
        
        // Test fold action
        val foldResult = gameBridge.performFold()
        assertTrue("Fold should succeed", foldResult.success)
        assertNotNull("Fold should have a message", foldResult.message)
    }

    @Test
    fun testAllGameModes() {
        // Test that all game modes are accessible
        val allModes = GameMode.values()
        assertEquals("Should have 4 game modes", 4, allModes.size)
        
        val modeNames = allModes.map { it.name }.toSet()
        assertTrue("Should include CLASSIC", modeNames.contains("CLASSIC"))
        assertTrue("Should include ADVENTURE", modeNames.contains("ADVENTURE"))
        assertTrue("Should include SAFARI", modeNames.contains("SAFARI"))
        assertTrue("Should include IRONMAN", modeNames.contains("IRONMAN"))
    }
}