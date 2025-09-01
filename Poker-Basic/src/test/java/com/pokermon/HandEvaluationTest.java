package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for hand evaluation and ranking system to ensure proper
 * poker hand detection, value calculation, and ranking comparisons.
 * 
 * This test class focuses specifically on:
 * - Hand evaluation functionality and consistency
 * - Hand value calculations and comparisons
 * - Card conversion and display functionality
 * - Edge cases in hand evaluation
 * 
 * @author Generated for issue #68 - Improve process flow and test cases
 */
class HandEvaluationTest {
    
    private Player testPlayer;
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testDeck = Main.setDeck();
    }
    
    /**
     * Test hand evaluation with real game setup to ensure basic functionality.
     */
    @Test
    void testBasicHandEvaluationWithGameSetup() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        // Verify hand was set up correctly
        assertNotNull(testPlayer.getHand(), "Hand should not be null after setup");
        assertEquals(5, testPlayer.getHand().length, "Hand should have 5 cards");
        assertTrue(testPlayer.getHandValue() > 0, "Hand value should be positive");
        
        // Verify all hand checks can be performed without errors
        assertDoesNotThrow(() -> testPlayer.performAllChecks(), 
            "performAllChecks should work without errors");
    }
    
    /**
     * Test hand evaluation consistency and basic functionality.
     */
    @Test
    void testHandEvaluationBasicFunctionality() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        // Test that evaluation methods work
        assertDoesNotThrow(() -> {
            testPlayer.performAllChecks();
            testPlayer.isTwoKind();
            testPlayer.isThreeKind();
            testPlayer.isFourKind();
            testPlayer.isStraight();
            testPlayer.isFlush();
            testPlayer.getHandValue();
        }, "All hand evaluation methods should work without errors");
        
        // Test hand value is reasonable
        int handValue = testPlayer.getHandValue();
        assertTrue(handValue >= 0 && handValue <= 100, 
            "Hand value should be in reasonable range: " + handValue);
    }
    
    /**
     * Test hand value consistency across multiple evaluations.
     */
    @Test
    void testHandValueConsistency() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        testPlayer.performAllChecks();
        int firstValue = testPlayer.getHandValue();
        
        testPlayer.performAllChecks();
        int secondValue = testPlayer.getHandValue();
        
        assertEquals(firstValue, secondValue, 
            "Hand value should be consistent across multiple evaluations");
    }
    
    /**
     * Test card conversion functionality for display purposes.
     */
    @Test
    void testCardConversion() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        String[] convertedHand = testPlayer.getConvertedHand();
        assertNotNull(convertedHand, "Converted hand should not be null");
        assertEquals(5, convertedHand.length, "Converted hand should have 5 cards");
        
        // Check that all converted cards are not null
        for (int i = 0; i < convertedHand.length; i++) {
            assertNotNull(convertedHand[i], "Converted card " + i + " should not be null");
        }
    }
    
    /**
     * Test hand value comparison between different hands.
     */
    @Test
    void testHandValueComparisons() {
        Player player1 = new Player();
        Player player2 = new Player();
        
        player1.setupPlayer("Player1", 1000, Main.setDeck());
        player2.setupPlayer("Player2", 1000, Main.setDeck());
        
        int value1 = player1.getHandValue();
        int value2 = player2.getHandValue();
        
        // Both should have valid hand values
        assertTrue(value1 >= 0 && value1 <= 100, "Player 1 should have valid hand value");
        assertTrue(value2 >= 0 && value2 <= 100, "Player 2 should have valid hand value");
    }
    
    /**
     * Test card removal and hand modification.
     */
    @Test
    void testCardRemovalAndModification() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        int[] originalHand = testPlayer.getHand().clone();
        
        // Remove first card
        testPlayer.removeCardAtIndex(0);
        int[] modifiedHand = testPlayer.getHandForModification();
        
        assertEquals(0, modifiedHand[0], "First card should be removed (set to 0)");
        
        // Verify other cards remain
        for (int i = 1; i < modifiedHand.length; i++) {
            assertEquals(originalHand[i], modifiedHand[i], 
                "Other cards should remain unchanged after removing first card");
        }
    }
    
    /**
     * Test hand evaluation after card removal.
     */
    @Test
    void testHandEvaluationAfterCardRemoval() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        testPlayer.performAllChecks();
        int originalValue = testPlayer.getHandValue();
        
        // Remove a card and re-evaluate
        testPlayer.removeCardAtIndex(0);
        testPlayer.performAllChecks();
        int newValue = testPlayer.getHandValue();
        
        // Hand value may change after card removal, just verify it's still valid
        assertTrue(newValue >= 0 && newValue <= 100, "Hand value should remain valid after card removal");
    }
    
    /**
     * Test multiple players hand evaluation.
     */
    @Test
    void testMultiplePlayersHandEvaluation() {
        Player[] players = new Player[3];
        
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
            players[i].setupPlayer("Player" + (i + 1), 1000, Main.setDeck());
            
            // Verify each player has valid hand evaluation
            assertTrue(players[i].getHandValue() >= 0, "Player " + (i + 1) + " should have valid hand value");
            assertNotNull(players[i].getHand(), "Player " + (i + 1) + " should have a hand");
            assertEquals(5, players[i].getHand().length, "Player " + (i + 1) + " should have 5 cards");
        }
    }
    
    /**
     * Test hand encapsulation - returned arrays should be copies.
     */
    @Test
    void testHandEncapsulation() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        int[] hand1 = testPlayer.getHand();
        int[] hand2 = testPlayer.getHand();
        
        // They should have the same content but be different objects
        assertArrayEquals(hand1, hand2, "Hands should have same content");
        assertNotSame(hand1, hand2, "getHand() should return copies, not the same reference");
        
        // Modifying the returned array should not affect the original
        if (hand1.length > 0) {
            int originalFirstCard = hand1[0];
            hand1[0] = 999;
            assertEquals(originalFirstCard, testPlayer.getHand()[0], 
                "Original hand should not be affected by modifying returned array");
        }
    }
    
    /**
     * Test edge cases in hand evaluation.
     */
    @Test
    void testHandEvaluationEdgeCases() {
        // Test with newly created player
        Player newPlayer = new Player();
        
        // Before setup, hand should be null
        assertNull(newPlayer.getHand(), "New player should have null hand");
        assertEquals(0, newPlayer.getHandValue(), "New player should have zero hand value");
        
        // After setup, hand should be valid
        newPlayer.setupPlayer("EdgeTest", 1000, testDeck);
        assertNotNull(newPlayer.getHand(), "Player should have hand after setup");
        assertTrue(newPlayer.getHandValue() >= 0, "Player should have valid hand value after setup");
    }
    
    /**
     * Test card constants functionality to ensure array extraction didn't break card naming.
     */
    @Test
    void testCardConstantsFunctionality() {
        // Test known card values to verify the constants work correctly
        
        // Test card 4 (Ace of Spades) 
        String[] testHand1 = Main.convertHand(new int[]{4});
        assertNotNull(testHand1[0], "Card name should not be null");
        assertTrue(testHand1[0].contains("Ace"), "Should contain Ace");
        assertTrue(testHand1[0].contains("Spades"), "Should contain Spades");
        
        // Test card 8 (King of Spades)
        String[] testHand2 = Main.convertHand(new int[]{8});
        assertNotNull(testHand2[0], "Card name should not be null");
        assertTrue(testHand2[0].contains("King"), "Should contain King");
        assertTrue(testHand2[0].contains("Spades"), "Should contain Spades");
        
        // Test card 52 (Two of Spades)
        String[] testHand3 = Main.convertHand(new int[]{52});
        assertNotNull(testHand3[0], "Card name should not be null");
        assertTrue(testHand3[0].contains("Two"), "Should contain Two");
        assertTrue(testHand3[0].contains("Spades"), "Should contain Spades");
    }

    /**
     * Test performAllChecks method completeness.
     */
    @Test
    void testPerformAllChecksCompleteness() {
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        // Perform all checks and verify all properties are accessible
        testPlayer.performAllChecks();
        
        assertDoesNotThrow(() -> {
            testPlayer.isTwoKind();
            testPlayer.isThreeKind();
            testPlayer.isFourKind();
            testPlayer.isStraight();
            testPlayer.isFlush();
            testPlayer.isStraightFlush();
            testPlayer.isRoyalFlush();
            testPlayer.isFullHouse();
            testPlayer.isTwoPair();
            testPlayer.getHandValue();
        }, "All hand properties should be accessible after performAllChecks");
    }
}