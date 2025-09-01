package com.pokermon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CardPackManager functionality.
 */
class CardPackManagerTest {
    
    @Test
    void testCardPackDiscovery() {
        CardPackManager manager = CardPackManager.getInstance();
        
        // Test that text symbols is available
        assertTrue(manager.isCardPackAvailable(CardPackManager.TEXT_SYMBOLS), 
            "TEXT_SYMBOLS should always be available");
            
        // Test that TET pack is discovered
        assertTrue(manager.isCardPackAvailable("TET"), 
            "TET card pack should be discovered");
            
        // Test display names
        assertEquals("Text + Symbols (Classic)", 
            manager.getDisplayName(CardPackManager.TEXT_SYMBOLS));
        assertEquals("Eternal Tortoise Cards", 
            manager.getDisplayName("TET"));
    }
    
    @Test
    void testCardImagePaths() {
        CardPackManager manager = CardPackManager.getInstance();
        
        // Test text symbols returns null
        assertNull(manager.getCardImagePath(CardPackManager.TEXT_SYMBOLS, "Ace", "Spades"));
        assertNull(manager.getCardBackImagePath(CardPackManager.TEXT_SYMBOLS));
        
        // Test TET pack returns valid paths
        String cardPath = manager.getCardImagePath("TET", "Ace", "Spades");
        assertEquals("Cards/TET/Ace of Spades.jpg", cardPath);
        
        String backPath = manager.getCardBackImagePath("TET");
        assertEquals("Cards/TET/card_back.jpg", backPath);
    }
    
    @Test
    void testAvailableCardPacks() {
        CardPackManager manager = CardPackManager.getInstance();
        var packs = manager.getAvailableCardPacks();
        
        // Should have at least TEXT_SYMBOLS and TET
        assertTrue(packs.size() >= 2);
        assertTrue(packs.containsKey(CardPackManager.TEXT_SYMBOLS));
        assertTrue(packs.containsKey("TET"));
        
        // TEXT_SYMBOLS should be first (LinkedHashMap preserves order)
        assertEquals(CardPackManager.TEXT_SYMBOLS, 
            packs.keySet().iterator().next());
    }
}