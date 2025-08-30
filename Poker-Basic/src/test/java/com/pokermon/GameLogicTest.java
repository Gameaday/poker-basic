package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the refactored game logic to ensure improved reusability works correctly.
 */
class GameLogicTest {
    
    private Player[] players;
    private String[] playerNames;
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        playerNames = new String[]{"Human", "CPU1", "CPU2", "CPU3"};
        players = new Player[playerNames.length];
        testDeck = Main.setDeck();
    }
    
    @Test
    void testDynamicPlayerInitialization() {
        // Test that InitializePlayers works with any number of players
        for (int numPlayers = 1; numPlayers <= 4; numPlayers++) {
            Player[] testPlayers = new Player[numPlayers];
            String[] testNames = new String[numPlayers];
            
            for (int i = 0; i < numPlayers; i++) {
                testNames[i] = "Player" + (i + 1);
            }
            
            // Use reflection to access the private method
            try {
                java.lang.reflect.Method initMethod = Main.class.getDeclaredMethod(
                    "InitializePlayers", Player[].class, String[].class, int.class, int[].class);
                initMethod.setAccessible(true);
                initMethod.invoke(null, testPlayers, testNames, 1000, testDeck);
                
                // Verify all players were initialized
                for (int i = 0; i < numPlayers; i++) {
                    assertNotNull(testPlayers[i]);
                    assertEquals(testNames[i], testPlayers[i].getName());
                    assertEquals(1000, testPlayers[i].getChips());
                    assertNotNull(testPlayers[i].getHand());
                }
            } catch (Exception e) {
                fail("Failed to test dynamic player initialization: " + e.getMessage());
            }
        }
    }
    
    @Test
    void testPlayerArrayFlexibility() {
        // Test that the game can handle different array sizes
        int[] playerCounts = {1, 2, 3, 4};
        
        for (int count : playerCounts) {
            Player[] flexPlayers = new Player[count];
            String[] flexNames = new String[count];
            
            for (int i = 0; i < count; i++) {
                flexNames[i] = "Flex" + (i + 1);
                flexPlayers[i] = new Player();
                flexPlayers[i].setupPlayer(flexNames[i], 1000, testDeck);
            }
            
            // Test that all players are properly set up
            assertEquals(count, flexPlayers.length);
            for (int i = 0; i < count; i++) {
                assertTrue(flexPlayers[i].getChips() > 0);
                assertNotNull(flexPlayers[i].getName());
                assertFalse(flexPlayers[i].isFold());
            }
        }
    }
    
    @Test
    void testHandValueConsistency() {
        // Test that hand value calculation is consistent
        Player testPlayer = new Player();
        testPlayer.setupPlayer("TestPlayer", 1000, testDeck);
        
        int handValue1 = testPlayer.getHandValue();
        testPlayer.performAllChecks(); // Recalculate
        int handValue2 = testPlayer.getHandValue();
        
        assertEquals(handValue1, handValue2, "Hand value should be consistent");
    }
    
    @Test
    void testBettingLogicScalability() {
        // Test that betting logic works with different numbers of players
        for (int numPlayers = 2; numPlayers <= 4; numPlayers++) {
            Player[] bettingPlayers = new Player[numPlayers];
            
            for (int i = 0; i < numPlayers; i++) {
                bettingPlayers[i] = new Player();
                bettingPlayers[i].setupPlayer("Player" + (i + 1), 1000, testDeck);
            }
            
            // Test betting with this number of players
            int initialPot = 0;
            int resultPot = Main.bet(bettingPlayers, initialPot);
            
            // Pot should be greater than initial (someone should have bet)
            assertTrue(resultPot >= initialPot, 
                "Pot should increase after betting round with " + numPlayers + " players");
        }
    }
    
    @Test
    void testGameStateConsistency() {
        // Test that game state remains consistent across operations
        Player player = new Player();
        player.setupPlayer("TestPlayer", 1000, testDeck);
        
        int initialChips = player.getChips();
        int betAmount = 100;
        
        player.placeBet(betAmount);
        assertEquals(initialChips - betAmount, player.getChips());
        
        player.addChips(50);
        assertEquals(initialChips - betAmount + 50, player.getChips());
    }
    
    @Test
    void testCodeReusabilityImprovements() {
        // Test that the refactored code handles edge cases better
        
        // Test with minimum players
        Player[] minPlayers = new Player[1];
        minPlayers[0] = new Player();
        minPlayers[0].setupPlayer("Solo", 1000, testDeck);
        assertNotNull(minPlayers[0].getName());
        
        // Test with maximum reasonable players
        Player[] maxPlayers = new Player[4];
        String[] maxNames = {"P1", "P2", "P3", "P4"};
        for (int i = 0; i < 4; i++) {
            maxPlayers[i] = new Player();
            maxPlayers[i].setupPlayer(maxNames[i], 1000, testDeck);
        }
        
        // All should be properly initialized
        for (int i = 0; i < 4; i++) {
            assertEquals(maxNames[i], maxPlayers[i].getName());
            assertEquals(1000, maxPlayers[i].getChips());
        }
    }
    
    @Test
    void testEncapsulationIntegrity() {
        // Test that encapsulation prevents unwanted modifications
        Player player = new Player();
        player.setupPlayer("TestPlayer", 1000, testDeck);
        
        // Getting hand should return a copy, not original
        int[] hand1 = player.getHand();
        int[] hand2 = player.getHand();
        
        // They should have the same content but be different objects
        assertArrayEquals(hand1, hand2);
        assertNotSame(hand1, hand2, "getHand() should return copies, not the same reference");
        
        // Modifying the returned array should not affect the original
        hand1[0] = 999;
        assertNotEquals(999, player.getHand()[0]);
    }
}