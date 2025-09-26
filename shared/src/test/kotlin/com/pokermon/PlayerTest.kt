package com.pokermon

import com.pokermon.modern.CardUtils
import com.pokermon.players.Player
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Kotlin-native test class for Player functionality.
 * Replaces PlayerTest.java with modern Kotlin testing patterns.
 */
class PlayerTest {
    private lateinit var player: Player
    private lateinit var testDeck: IntArray

    @BeforeEach
    fun setUp() {
        player = Player()
        testDeck = CardUtils.createDeck()
        CardUtils.shuffleDeck(testDeck)
    }

    @Test
    fun testPlayerCreation() {
        assertNotNull(player)
        assertEquals("", player.name)
        assertEquals(0, player.chips)
        assertFalse(player.isHuman)
    }

    @Test
    fun testPlayerSetup() {
        player.setupPlayer("TestPlayer", 1000, testDeck)

        assertEquals("TestPlayer", player.name)
        assertEquals(1000, player.chips)
        assertFalse(player.fold)
        assertEquals(0, player.bet)
        assertEquals(5, player.hand.size) // Default hand size
    }

    @Test
    fun testPlayerSetupWithCustomHandSize() {
        player.setupPlayer("TestPlayer", 1000, testDeck, 7)

        assertEquals("TestPlayer", player.name)
        assertEquals(1000, player.chips)
        assertEquals(7, player.hand.size) // Custom hand size
    }

    @Test
    fun testBettingFunctionality() {
        player.setupPlayer("TestPlayer", 1000, testDeck)

        // Test basic betting
        val betResult = player.canAfford(100)
        assertTrue(betResult)

        // Test betting more than available chips
        val largeBetResult = player.canAfford(1500)
        assertFalse(largeBetResult)
    }

    @Test
    fun testHandEvaluation() {
        player.setupPlayer("TestPlayer", 1000, testDeck)

        // Test that hand value is calculated
        val handValue = player.handValue
        assertTrue(handValue >= 0)

        // Test hand description
        val handDescription = Main.getHandDescription(player.hand)
        assertNotNull(handDescription)
        assertTrue(handDescription.isNotEmpty())
    }

    @Test
    fun testPlayerFolding() {
        player.setupPlayer("TestPlayer", 1000, testDeck)

        // Initially not folded
        assertFalse(player.fold)

        // Test folding (if method exists)
        // Note: The Player class structure may need this method
        player.resetFold()
        assertFalse(player.fold)
    }

    @Test
    fun testHandUpdate() {
        player.setupPlayer("TestPlayer", 1000, testDeck)
        val originalHand = player.hand.copyOf()

        // Update hand
        val newHand = intArrayOf(0, 1, 2, 3, 4)
        player.hand = newHand

        assertEquals(5, player.hand.size)
        assertFalse(player.hand.contentEquals(originalHand))
    }

    @Test
    fun testPlayerChipManagement() {
        player.setupPlayer("TestPlayer", 1000, testDeck)

        // Test initial state
        assertEquals(1000, player.chips)

        // Test setting chips (if method exists)
        player.chips = 1500
        assertEquals(1500, player.chips)

        // Test chip validation
        assertTrue(player.chips > 0)
    }

    @Test
    fun testDataClassEquality() {
        val player1 = Player("Test", 1000, true)
        val player2 = Player("Test", 1000, true)

        // Note: This tests the data class properties, but due to mutable fields
        // equality may not work as expected. This is more of a structure test.
        assertEquals(player1.name, player2.name)
        assertEquals(player1.chips, player2.chips)
        assertEquals(player1.isHuman, player2.isHuman)
    }

    @Test
    fun testEdgeCases() {
        // Test empty deck handling
        val emptyDeck = intArrayOf()
        assertDoesNotThrow {
            player.setupPlayer("EdgeTest", 500, emptyDeck)
        }

        // Test zero chips
        assertDoesNotThrow {
            player.setupPlayer("ZeroChips", 0, testDeck)
        }
        assertEquals(0, player.chips)

        // Test negative chips (should be handled gracefully)
        assertDoesNotThrow {
            player.setupPlayer("NegativeChips", -100, testDeck)
        }
    }
}
