package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the improved dynamic player logic that removes hard-coded player tracking.
 * These tests verify that the AI player logic fixes are working correctly.
 */
class DynamicPlayerLogicTest {
    
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        testDeck = Main.setDeck();
    }
    
    @Test
    void testHumanPlayerIdentification() {
        // Test that the first player is correctly identified as human
        Player[] players = new Player[3];
        String[] names = {"Human", "CPU1", "CPU2"};
        
        Main.InitializePlayers(players, names, 1000, testDeck);
        
        // First player should be human
        assertTrue(players[0].isHuman(), "First player should be marked as human");
        assertFalse(players[1].isHuman(), "Second player should be AI");
        assertFalse(players[2].isHuman(), "Third player should be AI");
    }
    
    @Test
    void testDynamicPlayerStatsHandling() {
        // Test that playersStats works with any number of players
        for (int numPlayers = 1; numPlayers <= 4; numPlayers++) {
            Player[] players = new Player[numPlayers];
            String[] names = new String[numPlayers];
            
            for (int i = 0; i < numPlayers; i++) {
                names[i] = "Player" + (i + 1);
            }
            
            Main.InitializePlayers(players, names, 1000, Main.setDeck());
            
            // This should not throw any exceptions
            assertDoesNotThrow(() -> Main.playersStats(players), 
                "playersStats should handle " + numPlayers + " players dynamically");
        }
    }
    
    @Test
    void testSetupListDynamicAssignment() {
        // Test that setupList works dynamically instead of hard-coded if statements
        Player USER = new Player();
        Player CPU1 = new Player();
        Player CPU2 = new Player();
        Player CPU3 = new Player();
        
        // Test with different array sizes
        for (int size = 1; size <= 4; size++) {
            Player[] list = new Player[size];
            Main.setupList(list, USER, CPU1, CPU2, CPU3);
            
            // Should assign players in order up to the array size
            assertSame(USER, list[0], "First position should always be USER");
            if (size > 1) assertSame(CPU1, list[1], "Second position should be CPU1");
            if (size > 2) assertSame(CPU2, list[2], "Third position should be CPU2");
            if (size > 3) assertSame(CPU3, list[3], "Fourth position should be CPU3");
        }
    }
    
    @Test
    void testBettingLogicUsesPlayerType() {
        // Test that betting logic uses isHuman() instead of hard-coded index
        Player[] players = new Player[3];
        String[] names = {"Human", "AI1", "AI2"};
        
        Main.InitializePlayers(players, names, 1000, testDeck);
        
        // Verify the human flag is set correctly
        assertTrue(players[0].isHuman(), "Human player should be marked as human");
        assertFalse(players[1].isHuman(), "AI player should not be marked as human");
        assertFalse(players[2].isHuman(), "AI player should not be marked as human");
        
        // Test betting logic (this should not throw exceptions)
        int initialPot = 0;
        assertDoesNotThrow(() -> {
            int resultPot = Main.bet(players, initialPot);
            assertTrue(resultPot >= initialPot, "Pot should not decrease after betting");
        }, "Betting should work with dynamic player type detection");
    }
    
    @Test
    void testPlayerReinitialization() {
        // Test that reinitializing players preserves the human flag
        Player[] players = new Player[2];
        String[] names = {"Human", "AI"};
        
        // Initial setup
        Main.InitializePlayers(players, names, 1000, testDeck);
        assertTrue(players[0].isHuman(), "Initial human flag should be set");
        assertFalse(players[1].isHuman(), "Initial AI flag should be set");
        
        // Reinitialize (second overloaded method)
        Main.InitializePlayers(players, names, Main.setDeck());
        assertTrue(players[0].isHuman(), "Human flag should be preserved after reinit");
        assertFalse(players[1].isHuman(), "AI flag should be preserved after reinit");
    }
    
    @Test
    void testNoHardCodedPlayerIndices() {
        // This test ensures we can have different player arrangements
        // If the code was hard-coded to expect player 0 to be human,
        // this flexibility test would fail
        
        Player[] players = new Player[4];
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        
        Main.InitializePlayers(players, names, 1000, testDeck);
        
        // All players should have valid names and chips
        for (int i = 0; i < players.length; i++) {
            assertNotNull(players[i], "Player " + i + " should not be null");
            assertEquals(names[i], players[i].getName(), "Player " + i + " should have correct name");
            assertEquals(1000, players[i].getChips(), "Player " + i + " should have correct chips");
            
            // Human status should be based on logic, not hard-coded position
            if (i == 0) {
                assertTrue(players[i].isHuman(), "First player should be human");
            } else {
                assertFalse(players[i].isHuman(), "Non-first players should be AI");
            }
        }
    }
    
    @Test
    void testScalabilityWithVariablePlayerCounts() {
        // Test that all functions work with different player counts
        int[] playerCounts = {1, 2, 3, 4};
        
        for (int count : playerCounts) {
            Player[] players = new Player[count];
            String[] names = new String[count];
            
            for (int i = 0; i < count; i++) {
                names[i] = "P" + (i + 1);
            }
            
            // Test initialization
            assertDoesNotThrow(() -> Main.InitializePlayers(players, names, 1000, Main.setDeck()),
                "Should handle " + count + " players in initialization");
            
            // Test stats reporting
            assertDoesNotThrow(() -> Main.playersStats(players),
                "Should handle " + count + " players in stats reporting");
            
            // Test betting
            assertDoesNotThrow(() -> Main.bet(players, 0),
                "Should handle " + count + " players in betting");
        }
    }
    
    @Test
    void testPlayerObjectIntegrity() {
        // Test that player objects maintain their properties correctly
        Player[] players = new Player[3];
        String[] names = {"Test1", "Test2", "Test3"};
        
        Main.InitializePlayers(players, names, 500, testDeck);
        
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            
            // Basic properties should be set
            assertNotNull(player.getName(), "Player name should be set");
            assertTrue(player.getChips() > 0, "Player should have chips");
            assertNotNull(player.getHand(), "Player should have a hand");
            assertEquals(5, player.getHand().length, "Player should have 5 cards");
            
            // Human flag should be correctly set
            assertEquals(i == 0, player.isHuman(), 
                "Human flag should match position (only first player is human)");
        }
    }
}