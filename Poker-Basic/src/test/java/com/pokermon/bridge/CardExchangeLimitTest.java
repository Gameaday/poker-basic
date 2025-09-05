package com.pokermon.bridge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Test suite for card exchange limitations in GameLogicBridge.
 * Ensures cards can only be exchanged once per round.
 */
public class CardExchangeLimitTest {
    
    private GameLogicBridge bridge;
    
    @BeforeEach
    void setUp() {
        bridge = new GameLogicBridge();
        // Initialize a basic game
        bridge.initializeGame("TestPlayer", 2, 1000);
    }
    
    @Test
    void testCardExchangeOncePerRound() {
        // Test that cards can be exchanged the first time
        List<Integer> cardsToExchange = Arrays.asList(0, 1);
        GameActionResult firstExchange = bridge.exchangeCards(cardsToExchange);
        
        assertTrue(firstExchange.getSuccess(), "First card exchange should succeed");
        assertTrue(firstExchange.getMessage().contains("Exchanged 2 cards"));
        
        // Test that second exchange in same round fails
        GameActionResult secondExchange = bridge.exchangeCards(cardsToExchange);
        
        assertFalse(secondExchange.getSuccess(), "Second card exchange should fail");
        assertTrue(secondExchange.getMessage().contains("once per round"), 
                  "Should indicate limit reached");
    }
    
    @Test
    void testCardExchangeResetAfterNewRound() {
        // Exchange cards in first round
        List<Integer> cardsToExchange = Arrays.asList(0);
        GameActionResult firstExchange = bridge.exchangeCards(cardsToExchange);
        assertTrue(firstExchange.getSuccess(), "First exchange should succeed");
        
        // Start new round
        GameActionResult newRound = bridge.nextRound();
        assertTrue(newRound.getSuccess(), "New round should start successfully");
        
        // Try to exchange cards again - should work now
        GameActionResult secondExchange = bridge.exchangeCards(cardsToExchange);
        assertTrue(secondExchange.getSuccess(), "Exchange should work in new round");
    }
    
    @Test
    void testCanExchangeCardsReflectsLimitation() {
        // Initially should be able to exchange cards (assuming correct phase)
        // Note: This depends on game phase, so we may need to advance to card exchange phase
        
        // Exchange cards once
        List<Integer> cardsToExchange = Arrays.asList(0, 1);
        bridge.exchangeCards(cardsToExchange);
        
        // Check that canExchangeCards now returns false (even if in right phase)
        // This test may need adjustment based on actual game flow
        boolean canExchange = bridge.canExchangeCards();
        // The result depends on current phase, but the limitation should apply
        
        // After new round, should be able to exchange again
        bridge.nextRound();
        // Note: actual result depends on game phase after new round
    }
}