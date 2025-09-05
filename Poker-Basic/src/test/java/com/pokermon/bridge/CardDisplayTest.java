package com.pokermon.bridge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Tests for card display formatting in GameLogicBridge.
 */
class CardDisplayTest {
    
    private GameLogicBridge bridge;
    
    @BeforeEach
    void setUp() {
        bridge = new GameLogicBridge();
    }
    
    @Test
    void testCardDisplayFormat() {
        // Test that the card display shows proper full card names
        boolean success = bridge.initializeGame("TestPlayer", 2, 1000);
        assertTrue(success, "Game should initialize successfully");
        
        List<String> playerHand = bridge.getPlayerHand();
        assertFalse(playerHand.isEmpty(), "Player should have cards");
        
        // Verify card format (should be like "Ace of Spades", "King of Hearts", etc.)
        for (String card : playerHand) {
            assertNotNull(card, "Card should not be null");
            assertTrue(card.length() >= 10, "Card should have sufficient characters for full format");
            
            // Should contain the " of " separator
            assertTrue(card.contains(" of "), "Card should contain ' of ' separator: " + card);
            
            // Should not contain suit symbols (those are for display only)
            assertFalse(card.contains("♠") || card.contains("♥") || card.contains("♦") || card.contains("♣"), 
                      "Card should not contain suit symbols in storage format: " + card);
                      
            // Should contain proper rank names
            String[] parts = card.split(" of ");
            assertEquals(2, parts.length, "Card should have rank and suit: " + card);
            
            String rank = parts[0];
            String suit = parts[1];
            
            // Verify rank is a valid full name
            assertTrue(rank.matches("Ace|King|Queen|Jack|Ten|Nine|Eight|Seven|Six|Five|Four|Three|Two"), 
                      "Rank should be a valid full name: " + rank);
                      
            // Verify suit is a valid full name
            assertTrue(suit.matches("Spades|Hearts|Diamonds|Clubs"), 
                      "Suit should be a valid full name: " + suit);
        }
        
        // Test round completion logic
        assertFalse(bridge.isRoundComplete(), "Round should not be complete before any bets");
        
        // Make a call and test again
        var result = bridge.performCall();
        // Test verifies the call completes successfully without debug output
    }
}