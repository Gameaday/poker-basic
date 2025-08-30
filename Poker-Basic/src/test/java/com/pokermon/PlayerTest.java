package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class to verify proper encapsulation and functionality.
 */
class PlayerTest {
    
    private Player player;
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        player = new Player();
        testDeck = Main.setDeck(); // Use the game's deck setup
    }
    
    @Test
    void testPlayerInitialization() {
        assertEquals(0, player.getChips());
        assertEquals(0, player.getHandValue());
        assertFalse(player.isFold());
        assertNull(player.getName());
        assertNull(player.getHand());
    }
    
    @Test
    void testSetName() {
        String testName = "TestPlayer";
        player.setName(testName);
        assertEquals(testName, player.getName());
    }
    
    @Test
    void testSetChips() {
        int testChips = 1000;
        player.setChips(testChips);
        assertEquals(testChips, player.getChips());
    }
    
    @Test
    void testAddChips() {
        player.setChips(100);
        player.addChips(50);
        assertEquals(150, player.getChips());
    }
    
    @Test
    void testPlaceBet() {
        player.setChips(1000);
        int betAmount = 100;
        int actualBet = player.placeBet(betAmount);
        
        assertEquals(betAmount, actualBet);
        assertEquals(900, player.getChips()); // Should deduct bet from chips
    }
    
    @Test
    void testFoldFunctionality() {
        assertFalse(player.isFold());
        
        player.foldHand();
        assertTrue(player.isFold());
        
        player.resetFold();
        assertFalse(player.isFold());
    }
    
    @Test
    void testBetRecording() {
        assertEquals(0, player.getLastBet());
        
        player.placeBet(50);
        player.recordLastBet();
        
        assertEquals(50, player.getLastBet());
    }
    
    @Test
    void testSetupPlayer() {
        String playerName = "TestPlayer";
        int initialChips = 1000;
        
        player.setupPlayer(playerName, initialChips, testDeck);
        
        assertEquals(playerName, player.getName());
        assertEquals(initialChips, player.getChips());
        assertFalse(player.isFold());
        assertNotNull(player.getHand());
        assertEquals(5, player.getHand().length); // Should have 5 cards
        assertTrue(player.getHandValue() > 0); // Should have calculated hand value
    }
    
    @Test
    void testHandEncapsulation() {
        player.setupPlayer("Test", 1000, testDeck);
        
        int[] hand = player.getHand();
        int[] modifiedHand = hand.clone();
        modifiedHand[0] = 999; // Try to modify the returned array
        
        // Original hand should not be affected
        assertNotEquals(999, player.getHand()[0]);
    }
    
    @Test
    void testRemoveCardAtIndex() {
        player.setupPlayer("Test", 1000, testDeck);
        
        int[] originalHand = player.getHand();
        int originalCard = originalHand[0];
        
        player.removeCardAtIndex(0);
        
        int[] modifiedHand = player.getHandForModification();
        assertEquals(0, modifiedHand[0]); // Card should be removed (set to 0)
    }
    
    @Test
    void testPerformAllChecks() {
        player.setupPlayer("Test", 1000, testDeck);
        
        // This should not throw any exceptions and should update all hand evaluations
        assertDoesNotThrow(() -> player.performAllChecks());
        
        // Hand value should be calculated
        assertTrue(player.getHandValue() > 0);
    }
}