package com.pokermon.bridge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.pokermon.CardPackManager;

/**
 * Test card pack functionality in the GameLogicBridge.
 */
class CardPackBridgeTest {
    
    private GameLogicBridge bridge;
    
    @BeforeEach
    void setUp() {
        bridge = new GameLogicBridge();
        bridge.initializeGame("TestPlayer", 3, 1000);
    }
    
    @Test
    void testCardPackSetting() {
        // Test default card pack
        assertEquals("TET", bridge.getSelectedCardPack());
        
        // Test changing to text symbols
        bridge.setSelectedCardPack(CardPackManager.TEXT_SYMBOLS);
        assertEquals(CardPackManager.TEXT_SYMBOLS, bridge.getSelectedCardPack());
        
        // Test image path with text symbols returns TEXT_ prefix
        String textPath = bridge.getCardImagePath(1);
        assertTrue(textPath.startsWith("TEXT_"));
        
        // Test back to TET
        bridge.setSelectedCardPack("TET");
        assertEquals("TET", bridge.getSelectedCardPack());
        
        String tetPath = bridge.getCardImagePath(1);
        assertTrue(tetPath.startsWith("Cards/TET/"));
        assertTrue(tetPath.endsWith(".jpg"));
    }
    
    @Test
    void testCardBackHandling() {
        // Test TET card back
        bridge.setSelectedCardPack("TET");
        String tetBack = bridge.getCardImagePath(0);
        assertEquals("Cards/TET/card_back.jpg", tetBack);
        
        // Test text symbols card back
        bridge.setSelectedCardPack(CardPackManager.TEXT_SYMBOLS);
        String textBack = bridge.getCardImagePath(0);
        assertEquals("TEXT_BACK", textBack);
    }
}