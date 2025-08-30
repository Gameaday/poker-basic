package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class to verify that the drawCard method correctly returns card values (1-52)
 * instead of card indices (0-51).
 * 
 * This addresses issue #13: The drawCard method was returning cardIndex instead of cardValue.
 */
public class DrawCardTest {
    
    private int[] deck;
    private Method drawCardMethod;
    private Method setDeckMethod;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Use reflection to access private methods for testing
        drawCardMethod = Main.class.getDeclaredMethod("drawCard", int[].class);
        drawCardMethod.setAccessible(true);
        
        setDeckMethod = Main.class.getDeclaredMethod("setDeck");
        setDeckMethod.setAccessible(true);
        
        // Initialize a fresh deck for each test
        deck = (int[]) setDeckMethod.invoke(null);
    }
    
    @Test
    public void testDrawCardReturnsValidCardValues() throws Exception {
        // Test that drawCard returns values in the range 1-52 (card values)
        // not 0-51 (indices)
        
        Set<Integer> drawnCards = new HashSet<>();
        
        // Draw several cards and verify they are all valid card values
        for (int i = 0; i < 10; i++) {
            int card = (Integer) drawCardMethod.invoke(null, deck);
            
            // Card should be between 1 and 52 (inclusive)
            assertTrue(card >= 1, "Card value should be >= 1, but got: " + card);
            assertTrue(card <= 52, "Card value should be <= 52, but got: " + card);
            
            // Card should not be an index (0-51)
            assertNotEquals(0, card, "Card should not be 0 (that would be an index)");
            
            drawnCards.add(card);
        }
        
        // Should have drawn different cards
        assertTrue(drawnCards.size() > 1, "Should have drawn multiple different cards");
    }
    
    @Test
    public void testDrawCardRemovesCardFromDeck() throws Exception {
        // Get initial deck state
        int[] originalDeck = deck.clone();
        
        // Draw a card
        int drawnCard = (Integer) drawCardMethod.invoke(null, deck);
        
        // Verify the card was removed from the deck
        // The deck position corresponding to the drawn card should now be 0
        int cardIndex = drawnCard - 1; // Convert card value to index
        assertEquals(0, deck[cardIndex], "Card should be removed from deck (set to 0)");
        
        // Verify other cards are still in the deck
        int remainingCards = 0;
        for (int i = 0; i < deck.length; i++) {
            if (deck[i] != 0) {
                remainingCards++;
            }
        }
        assertEquals(51, remainingCards, "Should have 51 cards remaining after drawing 1");
    }
    
    @Test
    public void testMultipleDrawsReturnUniqueCards() throws Exception {
        Set<Integer> drawnCards = new HashSet<>();
        
        // Draw multiple cards
        for (int i = 0; i < 20; i++) {
            int card = (Integer) drawCardMethod.invoke(null, deck);
            
            assertFalse(drawnCards.contains(card), "Should not draw the same card twice: " + card);
            drawnCards.add(card);
            
            // Each card should be a valid card value (1-52)
            assertTrue(card >= 1 && card <= 52, "Card value should be between 1-52, got: " + card);
        }
        
        assertEquals(20, drawnCards.size(), "Should have drawn 20 unique cards");
    }
    
    @Test
    public void testEntireDeckCanBeDrawn() throws Exception {
        Set<Integer> drawnCards = new HashSet<>();
        
        // Draw all 52 cards
        for (int i = 0; i < 52; i++) {
            int card = (Integer) drawCardMethod.invoke(null, deck);
            drawnCards.add(card);
            
            // Each card should be a valid card value (1-52)
            assertTrue(card >= 1 && card <= 52, "Card value should be between 1-52, got: " + card);
        }
        
        // Should have drawn all 52 unique cards
        assertEquals(52, drawnCards.size(), "Should have drawn all 52 unique cards");
        
        // Should have drawn exactly cards 1 through 52
        for (int i = 1; i <= 52; i++) {
            assertTrue(drawnCards.contains(i), "Should have drawn card " + i);
        }
    }
}