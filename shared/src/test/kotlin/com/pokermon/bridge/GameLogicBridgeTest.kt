package com.pokermon.bridge

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for the GameLogicBridge that connects the modern UI to game logic.
 */
class GameLogicBridgeTest {
    private lateinit var bridge: GameLogicBridge

    @BeforeEach
    fun setUp() {
        bridge = GameLogicBridge()
    }

    @Test
    fun `should initialize game successfully`() {
        val result = bridge.initializeGame("TestPlayer", 4, 1000)
        assertTrue(result, "Game should initialize successfully")

        assertEquals(1000, bridge.getPlayerChips(), "Player should have starting chips")
        assertEquals(0, bridge.getCurrentPot(), "Pot should start at 0")
    }

    @Test
    fun `should return player hand after initialization`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val hand = bridge.getPlayerHand()

        assertFalse(hand.isEmpty(), "Player should have cards after initialization")
        assertEquals(5, hand.size, "Player should have 5 cards")
    }

    @Test
    fun `should perform call action correctly`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val result = bridge.performCall()

        assertTrue(result.success, "Call should be successful")
        assertEquals(50, bridge.getCurrentPot(), "Pot should increase by call amount")
        assertEquals(950, bridge.getPlayerChips(), "Player chips should decrease by call amount")
    }

    @Test
    fun `should perform raise action correctly`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val result = bridge.performRaise(100)

        assertTrue(result.success, "Raise should be successful")
        assertEquals(100, bridge.getCurrentPot(), "Pot should increase by raise amount")
        assertEquals(900, bridge.getPlayerChips(), "Player chips should decrease by raise amount")
    }

    @Test
    fun `should not allow raise when insufficient chips`() {
        bridge.initializeGame("TestPlayer", 4, 500)
        val result = bridge.performRaise(1000)

        assertFalse(result.success, "Raise should fail with insufficient chips")
        assertEquals(0, bridge.getCurrentPot(), "Pot should not change")
        assertEquals(500, bridge.getPlayerChips(), "Player chips should not change")
    }

    @Test
    fun `should perform fold action correctly`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val result = bridge.performFold()

        assertTrue(result.success, "Fold should be successful")
        assertTrue(result.message.contains("Folded"), "Result message should indicate fold")
    }

    @Test
    fun `should perform check action correctly`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val result = bridge.performCheck()

        assertTrue(result.success, "Check should be successful")
        assertTrue(result.message.contains("Checked"), "Result message should indicate check")
    }

    @Test
    fun `should exchange cards correctly`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val result = bridge.exchangeCards(listOf(0, 1, 2))

        assertTrue(result.success, "Card exchange should be successful")
        assertTrue(result.message.contains("3"), "Result should mention number of cards exchanged")
    }

    @Test
    fun `should return all players information`() {
        bridge.initializeGame("TestPlayer", 4, 1000)
        val players = bridge.getAllPlayers()

        assertEquals(4, players.size, "Should return 4 players (human + 3 CPUs)")

        val humanPlayer = players.first { it.isCurrentPlayer }
        assertEquals("TestPlayer", humanPlayer.name, "Human player should have correct name")
        assertEquals(1000, humanPlayer.chips, "Human player should have starting chips")
        assertFalse(humanPlayer.isFolded, "Human player should not be folded initially")
    }

    @Test
    fun `should fail actions when game not initialized`() {
        val callResult = bridge.performCall()
        val raiseResult = bridge.performRaise(100)
        val foldResult = bridge.performFold()
        val checkResult = bridge.performCheck()
        val exchangeResult = bridge.exchangeCards(listOf(0))

        assertFalse(callResult.success, "Call should fail when game not initialized")
        assertFalse(raiseResult.success, "Raise should fail when game not initialized")
        assertFalse(foldResult.success, "Fold should fail when game not initialized")
        assertFalse(checkResult.success, "Check should fail when game not initialized")
        assertFalse(exchangeResult.success, "Exchange should fail when game not initialized")
    }
}
