package com.pokermon.modern

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Kotlin-native test class for CardUtils functionality.
 * Tests the consolidated card logic following DRY principles.
 */
class CardUtilsTest {
    @Test
    fun testCardRankAndSuit() {
        // Test basic card encoding/decoding (1-52 encoding)
        val aceOfSpades = 52 // Should be Ace of Spades in 1-52 encoding

        val rank = CardUtils.cardRank(aceOfSpades)
        val suit = CardUtils.cardSuit(aceOfSpades)

        assertEquals(13, rank) // Ace should be rank 13 in this encoding
        assertEquals(3, suit) // Spades should be suit 3
    }

    @Test
    fun testCardNames() {
        val testCard = 1 // Two of Clubs (first card in 1-52 encoding)

        val rankName = CardUtils.rankName(testCard)
        val suitName = CardUtils.suitName(testCard)
        val fullName = CardUtils.cardName(testCard)
        val compactName = CardUtils.compactCardName(testCard)

        assertNotNull(rankName)
        assertNotNull(suitName)
        assertNotNull(fullName)
        assertNotNull(compactName)

        assertTrue(fullName.contains(" of "))
        assertTrue(compactName.length <= 3) // Should be compact
    }

    @Test
    fun testCardValidation() {
        // Valid cards (1-52 encoding)
        assertTrue(CardUtils.isValidCard(1))
        assertTrue(CardUtils.isValidCard(26))
        assertTrue(CardUtils.isValidCard(52))

        // Invalid cards
        assertFalse(CardUtils.isValidCard(0))
        assertFalse(CardUtils.isValidCard(53))
        assertFalse(CardUtils.isValidCard(-1))
    }

    @Test
    fun testCardComparison() {
        val card1 = 1 // Two of Spades (1-52 encoding)
        val card2 = 5 // Three of Spades
        val card3 = 2 // Two of Hearts

        // Test rank comparison
        assertTrue(CardUtils.compareByRank(card1, card2) < 0) // Two < Three
        assertEquals(0, CardUtils.compareByRank(card1, card3)) // Same rank

        // Test suit comparison
        assertTrue(CardUtils.compareBySuit(card1, card2) == 0) // Same suit
        assertTrue(CardUtils.compareBySuit(card1, card3) < 0) // Spades < Hearts
    }

    @Test
    fun testSameRankAndSuit() {
        val card1 = 1 // Two of Spades
        val card2 = 5 // Three of Spades
        val card3 = 2 // Two of Hearts

        assertFalse(CardUtils.sameRank(card1, card2))
        assertTrue(CardUtils.sameRank(card1, card3))

        assertTrue(CardUtils.sameSuit(card1, card2))
        assertFalse(CardUtils.sameSuit(card1, card3))
    }

    @Test
    fun testDeckOperations() {
        val deck = CardUtils.createDeck()

        assertEquals(52, deck.size)

        // Verify all cards are unique
        val uniqueCards = deck.toSet()
        assertEquals(52, uniqueCards.size)

        // Test shuffling
        CardUtils.shuffleDeck(deck)

        // Should still have same cards but likely different order
        assertEquals(52, deck.size)
        assertEquals(uniqueCards, deck.toSet())
    }

    @Test
    fun testHandFormatting() {
        val hand = intArrayOf(1, 14, 27, 40, 52) // Various cards in 1-52 encoding

        val formatted = CardUtils.formatHand(hand, compact = false)
        val compactFormatted = CardUtils.formatHand(hand, compact = true)

        assertNotNull(formatted)
        assertNotNull(compactFormatted)
        assertTrue(formatted.contains(" of "))
        assertTrue(compactFormatted.length < formatted.length)
    }

    @Test
    fun testHandAnalysis() {
        val hand = intArrayOf(1, 5, 9, 13, 17) // Sequential ranks, same suit

        val uniqueRanks = CardUtils.getUniqueRanks(hand)
        val uniqueSuits = CardUtils.getUniqueSuits(hand)

        assertEquals(5, uniqueRanks.size) // All different ranks
        assertEquals(1, uniqueSuits.size) // All same suit

        // Test counting
        val rankCount = CardUtils.countRank(hand, 1)
        val suitCount = CardUtils.countSuit(hand, 0)

        assertEquals(1, rankCount) // One card of rank 1
        assertEquals(5, suitCount) // All cards of suit 0
    }

    @Test
    fun testSorting() {
        val hand = intArrayOf(52, 1, 26, 13, 39) // Random order (1-52 encoding)

        val sortedByRank = CardUtils.sortByRank(hand)
        val sortedBySuit = CardUtils.sortBySuit(hand)

        assertEquals(5, sortedByRank.size)
        assertEquals(5, sortedBySuit.size)

        // Verify sorting worked
        for (i in 0 until sortedByRank.size - 1) {
            assertTrue(CardUtils.cardRank(sortedByRank[i]) <= CardUtils.cardRank(sortedByRank[i + 1]))
        }

        for (i in 0 until sortedBySuit.size - 1) {
            assertTrue(CardUtils.cardSuit(sortedBySuit[i]) <= CardUtils.cardSuit(sortedBySuit[i + 1]))
        }
    }

    @Test
    fun testChipUtilities() {
        assertTrue(CardUtils.isValidChipAmount(500))
        assertTrue(CardUtils.isValidChipAmount(1000))
        assertFalse(CardUtils.isValidChipAmount(600))

        assertEquals(500, CardUtils.getClosestValidChipAmount(450))
        assertEquals(1000, CardUtils.getClosestValidChipAmount(1100))
    }

    @Test
    fun testHandDescriptions() {
        assertEquals("High Card", CardUtils.getHandDescription(1500))
        assertEquals("Pair", CardUtils.getHandDescription(2500))
        assertEquals("Three of a Kind", CardUtils.getHandDescription(4500))
        assertEquals("Royal Flush", CardUtils.getHandDescription(10500))
    }

    @Test
    fun testPlayerNameGeneration() {
        val usedNames = setOf("Carl", "Jeff", "James")
        val newName = CardUtils.generateAIPlayerName(usedNames)

        assertNotNull(newName)
        assertFalse(newName in usedNames)
        assertTrue(newName.isNotEmpty())
    }

    @Test
    fun testChipFormatting() {
        assertEquals("100", CardUtils.formatChips(100))
        assertEquals("1.5K", CardUtils.formatChips(1500))
        assertEquals("2.5M", CardUtils.formatChips(2500000))
    }

    @Test
    fun testDirectMethodCalls() {
        val testCard = 26 // Middle card in 1-52 encoding

        // Test direct Kotlin-native methods
        val cardName = CardUtils.cardName(testCard)
        assertNotNull(cardName)
        assertTrue(cardName.contains(" of "))

        // Test consistency of core methods
        val rank = CardUtils.cardRank(testCard)
        val suit = CardUtils.cardSuit(testCard)
        assertTrue(rank >= 1 && rank <= 13) // 1-52 encoding uses ranks 1-13
        assertTrue(suit >= 0 && suit < 4)

        // Verify card name contains expected rank and suit
        assertTrue(cardName.isNotEmpty())
    }

    @Test
    fun testEdgeCases() {
        // Test invalid card handling with direct methods
        assertThrows<IllegalArgumentException> {
            CardUtils.cardName(0)
        }
        assertThrows<IllegalArgumentException> {
            CardUtils.cardName(53)
        }

        // Test empty arrays
        val emptyHand = intArrayOf()
        val emptyFormatted = CardUtils.formatHand(emptyHand)
        assertEquals("", emptyFormatted)

        // Test null safety for names generation
        val emptySet = emptySet<String>()
        val name = CardUtils.generateAIPlayerName(emptySet)
        assertTrue(name in CardUtils.POSSIBLE_NAMES)
    }
}
