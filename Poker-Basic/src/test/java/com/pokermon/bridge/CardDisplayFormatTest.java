package com.pokermon.bridge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test suite for card display format in GameLogicBridge.
 * Ensures cards are represented in the correct format for Android graphics.
 */
public class CardDisplayFormatTest {
    
    private GameLogicBridge bridge;
    
    @BeforeEach
    void setUp() {
        bridge = new GameLogicBridge();
        bridge.initializeGame("TestPlayer", 2, 1000);
    }
    
    @Test
    void testCardNamesAreInFullFormat() {
        List<String> playerHand = bridge.getPlayerHand();
        
        // Should have cards after initialization
        assertFalse(playerHand.isEmpty(), "Player should have cards");
        
        // Check that card names are in full format (not poker notation)
        for (String card : playerHand) {
            assertTrue(card.contains(" of "), 
                "Card should be in full format: " + card);
            
            // Verify the format is "Rank of Suit"
            String[] parts = card.split(" of ");
            assertEquals(2, parts.length, 
                "Card should have exactly one ' of ' separator: " + card);
            
            String rank = parts[0];
            String suit = parts[1];
            
            // Check that rank is valid
            assertTrue(rank.matches("Ace|King|Queen|Jack|Ten|Nine|Eight|Seven|Six|Five|Four|Three|Two"), 
                "Rank should be a valid name: " + rank);
            
            // Check that suit is valid
            assertTrue(suit.matches("Spades|Hearts|Diamonds|Clubs"), 
                "Suit should be a valid name: " + suit);
        }
    }
    
    @Test
    void testCardNamesMatchResourceNames() {
        List<String> playerHand = bridge.getPlayerHand();
        
        for (String card : playerHand) {
            // The card names should match what CardGraphicsManager expects
            String expectedResourceName = card.toLowerCase()
                .replace(" ", "_")
                .replace("ace_of_", "ace_of_")
                .replace("king_of_", "king_of_")
                .replace("queen_of_", "queen_of_")
                .replace("jack_of_", "jack_of_")
                .replace("ten_of_", "ten_of_")
                .replace("nine_of_", "nine_of_")
                .replace("eight_of_", "eight_of_")
                .replace("seven_of_", "seven_of_")
                .replace("six_of_", "six_of_")
                .replace("five_of_", "five_of_")
                .replace("four_of_", "four_of_")
                .replace("three_of_", "three_of_")
                .replace("two_of_", "two_of_");
            
            // Check that it's a format that would result in a valid resource name
            assertTrue(expectedResourceName.matches("[a-z]+_of_[a-z]+"), 
                "Card name should convert to valid resource format: " + card + " -> " + expectedResourceName);
        }
    }
}