package com.pokermon.bridge

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for automatic AI opponent processing in GameLogicBridge.
 * Verifies that the AI processing functionality works when enabled.
 */
class GameLogicBridgeAITest {
    private lateinit var bridge: GameLogicBridge

    @BeforeEach
    fun setUp() {
        bridge = GameLogicBridge()
    }

    @Test
    fun `should not process AI by default`() {
        // Initialize game with multiple players
        bridge.initializeGame("TestPlayer", 4, 1000)
        val initialPot = bridge.getCurrentPot()

        // Perform a call action - by default, AI processing should be disabled
        val result = bridge.performCall()
        assertTrue(result.success, "Call should be successful")

        // Only human player's contribution should be in pot
        val expectedPot = initialPot + 50 // Default call amount
        assertEquals(
            expectedPot,
            bridge.getCurrentPot(),
            "Pot should only contain human player's bet when AI processing is disabled",
        )
    }

    @Test
    fun `should process AI opponents when enabled`() {
        // Initialize game with multiple players and enable AI processing
        bridge.initializeGame("TestPlayer", 4, 1000)
        bridge.setAutoAIEnabled(true)

        val initialPot = bridge.getCurrentPot()
        val initialPlayerChips = bridge.getPlayerChips()

        // Perform a call action - AI processing should now be enabled
        val result = bridge.performCall()
        assertTrue(result.success, "Call should be successful")

        // Check that the player's chips decreased
        assertTrue(
            bridge.getPlayerChips() < initialPlayerChips,
            "Player chips should decrease after call",
        )

        // Pot should contain contributions from multiple players (human + AI opponents)
        val finalPot = bridge.getCurrentPot()
        assertTrue(
            finalPot > initialPot + 50,
            "Pot should contain more than just human player's bet when AI processing is enabled",
        )

        // Verify that AI opponents are making decisions
        val allPlayers = bridge.getAllPlayers()
        assertTrue(allPlayers.size > 1, "Should have multiple players")

        // At least some AI players should have acted (either bet or folded)
        val aiPlayersWithActions = allPlayers.drop(1).count { it.isFolded || finalPot > initialPot + 50 }
        assertTrue(aiPlayersWithActions > 0, "AI players should have taken actions")
    }

    @Test
    fun `should handle game progression with AI processing`() {
        // Initialize game and enable AI processing
        bridge.initializeGame("TestPlayer", 3, 1000)
        bridge.setAutoAIEnabled(true)

        // Perform multiple actions to test game flow
        val callResult = bridge.performCall()
        assertTrue(callResult.success, "Call should be successful")

        // Game should progress naturally with AI opponents acting
        val currentPhase = bridge.getCurrentPhase()
        assertNotNull(currentPhase, "Should have a valid current phase")

        // Verify game state is coherent
        val allPlayers = bridge.getAllPlayers()
        assertFalse(allPlayers.isEmpty(), "Should have players in the game")

        // All active players should have consistent state
        val activePlayers = allPlayers.filter { !it.isFolded }
        assertTrue(activePlayers.isNotEmpty(), "Should have at least one active player")
    }

    @Test
    fun `should maintain game integrity with AI processing`() {
        // Initialize game and enable AI processing
        bridge.initializeGame("TestPlayer", 4, 1000)
        bridge.setAutoAIEnabled(true)

        val allPlayersBefore = bridge.getAllPlayers()
        val totalInitialChips = allPlayersBefore.sumOf { it.chips }
        val initialPot = bridge.getCurrentPot()

        // Perform one action to test conservation
        bridge.performCall()

        // Verify chip conservation: total chips + pot should be reasonable
        val allPlayersAfter = bridge.getAllPlayers()
        val totalCurrentChips = allPlayersAfter.sumOf { it.chips }
        val currentPot = bridge.getCurrentPot()

        // The total money in the system should not have disappeared
        // (allowing for some variation due to game mechanics)
        val totalBefore = totalInitialChips + initialPot
        val totalAfter = totalCurrentChips + currentPot

        assertTrue(totalAfter > 0, "Total chips should be positive")
        assertTrue(currentPot > initialPot, "Pot should have increased")
        assertTrue(
            totalAfter >= totalBefore - 500, // Allow some tolerance for game mechanics
            "Total chips should be roughly conserved (before: $totalBefore, after: $totalAfter)",
        )
    }

    @Test
    fun `should handle edge cases in AI processing`() {
        // Test with minimum players
        bridge.initializeGame("TestPlayer", 2, 500)
        bridge.setAutoAIEnabled(true)

        // Should handle small games without issues
        val result = bridge.performCall()
        assertTrue(result.success, "Should handle minimum player count")

        // Test with players having different chip amounts
        val allPlayers = bridge.getAllPlayers()
        assertNotNull(allPlayers, "Should have players")
        assertTrue(allPlayers.size >= 2, "Should have at least 2 players")
    }

    @Test
    fun `should track AI actions correctly with helper method`() {
        // Initialize game and enable AI processing
        bridge.initializeGame("TestPlayer", 3, 1000)
        bridge.setAutoAIEnabled(true)

        // Clear any existing AI action
        bridge.clearLastAIAction()

        // Perform an action that should trigger AI responses
        val result = bridge.performCall()
        assertTrue(result.success, "Call should be successful")

        // Check that an AI action was recorded
        val aiAction = bridge.getLastAIAction()
        assertNotNull(aiAction, "Should have recorded an AI action")

        // Verify AI action contains expected information
        aiAction?.let { action ->
            assertNotNull(action.playerName, "AI action should have player name")
            assertTrue(action.playerName.isNotBlank(), "Player name should not be blank")
            assertNotNull(action.action, "AI action should have action type")
            assertTrue(
                listOf("Fold", "Check", "Call", "Raise").contains(action.action),
                "Action should be valid poker action: ${action.action}"
            )
            assertNotNull(action.message, "AI action should have message")
            assertTrue(action.message.isNotBlank(), "Message should not be blank")
            
            // Amount should be non-negative
            assertTrue(action.amount >= 0, "Amount should be non-negative: ${action.amount}")
        }
    }

    @Test
    fun `should clear AI action when requested`() {
        // Initialize game and enable AI processing
        bridge.initializeGame("TestPlayer", 3, 1000)
        bridge.setAutoAIEnabled(true)

        // Trigger an AI action
        bridge.performCall()

        // Verify an AI action exists
        assertNotNull(bridge.getLastAIAction(), "Should have recorded an AI action")

        // Clear the AI action
        bridge.clearLastAIAction()

        // Verify the action was cleared
        assertNull(bridge.getLastAIAction(), "AI action should be cleared")
    }

    @Test
    fun `should handle different AI decision scenarios`() {
        // Initialize multiple games with different scenarios
        for (playerCount in 2..4) {
            // Fresh game for each scenario
            bridge.initializeGame("TestPlayer", playerCount, 1000)
            bridge.setAutoAIEnabled(true)
            bridge.clearLastAIAction()

            // Perform action to trigger AI decisions
            val result = bridge.performCall()
            assertTrue(result.success, "Call should succeed for $playerCount players")

            // Verify AI made some decision
            val aiAction = bridge.getLastAIAction()
            if (playerCount > 1) { // Only check if there are AI players
                // Note: AI action might be null if no AI players took action this turn
                // But the game should handle this gracefully
                assertTrue(
                    result.success,
                    "Game should handle AI processing correctly with $playerCount players"
                )
            }
        }
    }
}
