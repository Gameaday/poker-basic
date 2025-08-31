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
        // Test that the card display shows proper poker notation
        boolean success = bridge.initializeGame("TestPlayer", 2, 1000);
        assertTrue(success, "Game should initialize successfully");
        
        List<String> playerHand = bridge.getPlayerHand();
        assertFalse(playerHand.isEmpty(), "Player should have cards");
        
        // Verify card format (should be like "A♠", "K♥", "Q♦", "J♣", "10♠", etc.)
        for (String card : playerHand) {
            assertNotNull(card, "Card should not be null");
            assertTrue(card.length() >= 2, "Card should have at least 2 characters (rank + suit)");
            assertTrue(card.length() <= 3, "Card should have at most 3 characters (10 + suit)");
            
            // Should contain a suit symbol
            assertTrue(card.contains("♠") || card.contains("♥") || card.contains("♦") || card.contains("♣"), 
                      "Card should contain a suit symbol: " + card);
                      
            // Should not be the old format (full words)
            assertFalse(card.contains(" of "), "Card should not contain old format ' of ': " + card);
            assertFalse(card.contains("Ace"), "Card should not contain full word 'Ace': " + card);
            assertFalse(card.contains("King"), "Card should not contain full word 'King': " + card);
        }
        
        System.out.println("Sample card formats: " + playerHand);
    }
}