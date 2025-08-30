package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for flexible hand size functionality.
 */
class FlexibleHandTest {
    
    private int[] testDeck;
    
    @BeforeEach
    void setUp() {
        testDeck = Main.setDeck();
    }
    
    @Test
    void testFlexibleNewHand() {
        // Test default 5-card hand
        int[] defaultHand = Main.newHand(testDeck);
        assertEquals(5, defaultHand.length);
        
        // Test custom hand sizes
        int[] threeCardHand = Main.newHand(Main.setDeck(), 3);
        assertEquals(3, threeCardHand.length);
        
        int[] sevenCardHand = Main.newHand(Main.setDeck(), 7);
        assertEquals(7, sevenCardHand.length);
        
        int[] oneCardHand = Main.newHand(Main.setDeck(), 1);
        assertEquals(1, oneCardHand.length);
        
        // All cards should be valid (card indices should be between 0 and 51)
        for (int card : threeCardHand) {
            assertTrue(card >= 0 && card <= 51, "Card should be valid index: " + card);
        }
        
        for (int card : sevenCardHand) {
            assertTrue(card >= 0 && card <= 51, "Card should be valid index: " + card);
        }
    }
    
    @Test
    void testInvalidHandSizes() {
        // Invalid hand sizes should throw exceptions
        assertThrows(IllegalArgumentException.class, () -> Main.newHand(testDeck, 0));
        assertThrows(IllegalArgumentException.class, () -> Main.newHand(testDeck, -1));
        assertThrows(IllegalArgumentException.class, () -> Main.newHand(testDeck, 53));
    }
    
    @Test
    void testPlayerWithCustomHandSize() {
        Player player = new Player();
        
        // Test that player can handle different hand sizes through game engine
        Game threeCardGame = Game.createThreeCardPoker();
        GameEngine engine = new GameEngine(threeCardGame);
        
        assertTrue(engine.initializeGame(new String[]{"TestPlayer"}));
        
        Player[] players = engine.getPlayers();
        assertEquals(1, players.length);
        assertEquals(3, players[0].getHand().length);
        
        // Verify the player methods still work with different hand sizes
        assertDoesNotThrow(() -> players[0].performAllChecks());
        assertTrue(players[0].getHandValue() > 0);
    }
    
    @Test
    void testGameFlexibilityWithDifferentConfigurations() {
        // Test multiple game configurations
        Game[] gameConfigs = {
            new Game(3, 2, 500, 1),    // 3-card, 2 players
            new Game(5, 4, 1000, 2),   // Traditional 5-card
            new Game(7, 3, 1500, 3)    // 7-card, 3 players
        };
        
        String[] playerNames = {"P1", "P2"};
        
        for (Game config : gameConfigs) {
            GameEngine engine = new GameEngine(config);
            assertTrue(engine.initializeGame(playerNames));
            
            Player[] players = engine.getPlayers();
            for (Player player : players) {
                assertEquals(config.getHandSize(), player.getHand().length);
                assertEquals(config.getStartingChips(), player.getChips());
                assertNotNull(player.getName());
            }
        }
    }
    
    @Test
    void testHandEvaluationWithDifferentSizes() {
        // Test that hand evaluation works with different hand sizes
        int[] threeCardHand = Main.newHand(Main.setDeck(), 3);
        int[] fiveCardHand = Main.newHand(Main.setDeck(), 5);
        int[] sevenCardHand = Main.newHand(Main.setDeck(), 7);
        
        // All should produce valid hand values (though the evaluation logic 
        // may not be optimal for non-5-card hands, it shouldn't crash)
        assertDoesNotThrow(() -> Main.handValue(threeCardHand));
        assertDoesNotThrow(() -> Main.handValue(fiveCardHand));
        assertDoesNotThrow(() -> Main.handValue(sevenCardHand));
        
        assertTrue(Main.handValue(threeCardHand) > 0);
        assertTrue(Main.handValue(fiveCardHand) > 0);
        assertTrue(Main.handValue(sevenCardHand) > 0);
    }
}