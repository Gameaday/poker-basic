package com.pokermon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameMode enum.
 */
public class GameModeTest {
    
    @Test
    public void testGameModeProperties() {
        assertEquals("Classic Poker", GameMode.CLASSIC.getDisplayName());
        assertEquals("Adventure Mode", GameMode.ADVENTURE.getDisplayName());
        assertEquals("Safari Mode", GameMode.SAFARI.getDisplayName());
        assertEquals("Ironman Mode", GameMode.IRONMAN.getDisplayName());
    }
    
    @Test
    public void testGameModeDescriptions() {
        assertTrue(GameMode.CLASSIC.getDescription().contains("Traditional poker"));
        assertTrue(GameMode.ADVENTURE.getDescription().contains("Battle monsters"));
        assertTrue(GameMode.SAFARI.getDescription().contains("Capture monsters"));
        assertTrue(GameMode.IRONMAN.getDescription().contains("gacha"));
    }
    
    @Test
    public void testHasMonsters() {
        assertFalse(GameMode.CLASSIC.hasMonsters());
        assertTrue(GameMode.ADVENTURE.hasMonsters());
        assertTrue(GameMode.SAFARI.hasMonsters());
        assertTrue(GameMode.IRONMAN.hasMonsters());
    }
}