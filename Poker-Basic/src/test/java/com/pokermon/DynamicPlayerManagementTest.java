package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for dynamic player management to ensure the removal of hard-coded player logic works correctly.
 * These tests validate that player management is flexible and properly object-oriented.
 */
class DynamicPlayerManagementTest {

    private Player[] testPlayers;
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        testDeck = Main.setDeck();
    }
    
    @Test
    void testSetupPlayersListDynamically() {
        // Test that setupPlayersList works with different array sizes
        for (int numPlayers = 1; numPlayers <= 4; numPlayers++) {
            Player[] players = new Player[numPlayers];
            
            // Call the new dynamic setup method
            Main.setupPlayersList(players);
            
            // Verify all players are initialized
            for (int i = 0; i < numPlayers; i++) {
                assertNotNull(players[i], "Player at index " + i + " should be initialized");
                assertEquals(0, players[i].getChips(), "New player should start with 0 chips");
                assertFalse(players[i].isFold(), "New player should not be folded");
            }
        }
    }
    
    @Test
    void testPlayersStatsDynamicIteration() {
        // Test that playersStats works with different numbers of players
        for (int numPlayers = 1; numPlayers <= 4; numPlayers++) {
            Player[] players = new Player[numPlayers];
            Main.setupPlayersList(players);
            
            // Set up players with some data
            String[] playerNames = {"TestPlayer1", "TestPlayer2", "TestPlayer3", "TestPlayer4"};
            for (int i = 0; i < numPlayers; i++) {
                players[i].setupPlayer(playerNames[i], 1000, testDeck);
            }
            
            // This should not throw any exceptions and handle any number of players
            assertDoesNotThrow(() -> Main.playersStats(players), 
                "playersStats should handle " + numPlayers + " players without errors");
        }
    }
    
    @Test
    void testPlayerArrayFlexibilityWithDifferentSizes() {
        // Test that the system works with different array sizes
        Player[] players = new Player[3];
        Main.setupPlayersList(players);
        
        // Initialize all players properly
        String[] names = {"Player1", "Player2", "Player3"};
        for (int i = 0; i < players.length; i++) {
            players[i].setupPlayer(names[i], 1000, testDeck);
        }
        
        // The method should handle different array sizes gracefully
        assertDoesNotThrow(() -> Main.playersStats(players), 
            "playersStats should handle different numbers of players gracefully");
    }
    
    @Test
    void testDynamicPlayerInitializationScalability() {
        // Test that we can create varying numbers of players without hardcoded limits
        int[] testSizes = {1, 2, 3, 4, 5, 6}; // Test beyond traditional 4-player limit
        
        for (int size : testSizes) {
            Player[] players = new Player[size];
            Main.setupPlayersList(players);
            
            assertEquals(size, players.length, "Array should maintain requested size");
            
            // All players should be properly initialized
            for (int i = 0; i < size; i++) {
                assertNotNull(players[i], "Player " + i + " should be initialized in " + size + "-player game");
            }
        }
    }
    
    @Test
    void testNoHardCodedPlayerReferences() {
        // Test that the system works without hard-coded CPU1, CPU2, CPU3 references
        Player[] players = new Player[4];
        Main.setupPlayersList(players);
        
        String[] names = {"Human", "AI_1", "AI_2", "AI_3"};
        
        // Initialize all players dynamically
        for (int i = 0; i < players.length; i++) {
            players[i].setupPlayer(names[i], 1000, testDeck);
            assertEquals(names[i], players[i].getName(), 
                "Player " + i + " should have correct name without hard-coded references");
        }
        
        // Test betting logic works with any number of AI players
        int initialPot = 0;
        int resultPot = Main.bet(players, initialPot);
        
        assertTrue(resultPot >= initialPot, 
            "Betting should work with dynamically created players");
    }
    
    @Test
    void testAIPlayerLogicWithDynamicArrays() {
        // Test that AI betting logic works with dynamically created players
        Player[] aiPlayers = new Player[3]; // All AI players
        Main.setupPlayersList(aiPlayers);
        
        String[] aiNames = {"AI_Alpha", "AI_Beta", "AI_Gamma"};
        
        for (int i = 0; i < aiPlayers.length; i++) {
            aiPlayers[i].setupPlayer(aiNames[i], 1000, testDeck);
            // Verify each player has a hand and proper initialization
            assertNotNull(aiPlayers[i].getHand(), "AI player " + i + " should have a hand");
            assertTrue(aiPlayers[i].getChips() > 0, "AI player " + i + " should have chips");
        }
        
        // Test AI logic with these dynamically created players
        int initialPot = 0;
        int finalPot = Main.bet(aiPlayers, initialPot);
        
        assertTrue(finalPot >= initialPot, "AI betting should work with dynamic player arrays");
    }
    
    @Test
    void testPlayerManagementObjectOriented() {
        // Test that player management follows object-oriented principles
        Player[] players = new Player[2];
        Main.setupPlayersList(players);
        
        // Each player should be an independent object
        players[0].setupPlayer("Player1", 500, testDeck);
        players[1].setupPlayer("Player2", 800, testDeck);
        
        // Verify independence
        assertNotEquals(players[0].getName(), players[1].getName());
        assertNotEquals(players[0].getChips(), players[1].getChips());
        
        // Operations on one shouldn't affect the other
        players[0].placeBet(100);
        assertEquals(400, players[0].getChips());
        assertEquals(800, players[1].getChips()); // Should be unchanged
    }
    
    @Test
    void testBackwardsCompatibilityWith4Players() {
        // Ensure the new system still works correctly with the traditional 4-player setup
        Player[] players = new Player[4];
        Main.setupPlayersList(players);
        
        String[] names = {"Human", "CPU1", "CPU2", "CPU3"};
        
        for (int i = 0; i < 4; i++) {
            players[i].setupPlayer(names[i], 1000, testDeck);
        }
        
        // Test that all traditional game operations still work
        assertDoesNotThrow(() -> {
            Main.playersStats(players);
            Main.bet(players, 0);
            Main.dividePot(players, 1000);
        }, "Traditional 4-player game operations should still work");
    }
}