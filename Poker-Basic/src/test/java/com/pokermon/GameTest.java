package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Game class to verify flexible game configuration.
 */
class GameTest {
    
    @Test
    void testDefaultGameConfiguration() {
        Game game = new Game();
        
        assertEquals(5, game.getHandSize());
        assertEquals(4, game.getMaxPlayers());
        assertEquals(1000, game.getStartingChips());
        assertEquals(2, game.getMaxBettingRounds());
    }
    
    @Test
    void testCustomGameConfiguration() {
        Game game = new Game(7, 3, 1500, 3);
        
        assertEquals(7, game.getHandSize());
        assertEquals(3, game.getMaxPlayers());
        assertEquals(1500, game.getStartingChips());
        assertEquals(3, game.getMaxBettingRounds());
    }
    
    @Test
    void testInvalidGameConfiguration() {
        // Invalid hand size
        assertThrows(IllegalArgumentException.class, () -> new Game(0, 4, 1000, 2));
        assertThrows(IllegalArgumentException.class, () -> new Game(11, 4, 1000, 2));
        
        // Invalid player count
        assertThrows(IllegalArgumentException.class, () -> new Game(5, 0, 1000, 2));
        assertThrows(IllegalArgumentException.class, () -> new Game(5, 5, 1000, 2));
        
        // Invalid starting chips
        assertThrows(IllegalArgumentException.class, () -> new Game(5, 4, 0, 2));
        
        // Invalid betting rounds
        assertThrows(IllegalArgumentException.class, () -> new Game(5, 4, 1000, 0));
    }
    
    @Test
    void testPlayerCountValidation() {
        Game game = new Game();
        
        assertTrue(game.isValidPlayerCount(1));
        assertTrue(game.isValidPlayerCount(2));
        assertTrue(game.isValidPlayerCount(3));
        assertTrue(game.isValidPlayerCount(4));
        
        assertFalse(game.isValidPlayerCount(0));
        assertFalse(game.isValidPlayerCount(5));
    }
    
    @Test
    void testGameVariants() {
        Game threeCard = Game.createThreeCardPoker();
        assertEquals(3, threeCard.getHandSize());
        assertEquals(4, threeCard.getMaxPlayers());
        assertEquals(500, threeCard.getStartingChips());
        assertEquals(1, threeCard.getMaxBettingRounds());
        
        Game sevenCard = Game.createSevenCardStud();
        assertEquals(7, sevenCard.getHandSize());
        assertEquals(4, sevenCard.getMaxPlayers());
        assertEquals(1500, sevenCard.getStartingChips());
        assertEquals(3, sevenCard.getMaxBettingRounds());
        
        Game headsUp = Game.createHeadsUp();
        assertEquals(5, headsUp.getHandSize());
        assertEquals(2, headsUp.getMaxPlayers());
        assertEquals(1000, headsUp.getStartingChips());
        assertEquals(2, headsUp.getMaxBettingRounds());
    }
    
    @Test
    void testToString() {
        Game game = new Game(5, 4, 1000, 2);
        String gameString = game.toString();
        
        assertTrue(gameString.contains("handSize=5"));
        assertTrue(gameString.contains("maxPlayers=4"));
        assertTrue(gameString.contains("startingChips=1000"));
        assertTrue(gameString.contains("maxBettingRounds=2"));
    }
}