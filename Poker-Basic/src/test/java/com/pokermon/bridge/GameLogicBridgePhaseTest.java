package com.pokermon.bridge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.pokermon.*;
import com.pokermon.api.GamePhase;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the enhanced GameLogicBridge with game phase integration.
 */
class GameLogicBridgePhaseTest {
    private GameLogicBridge bridge;
    
    @BeforeEach
    void setUp() {
        bridge = new GameLogicBridge();
    }
    
    @Test
    void testInitialPhase() {
        // Bridge should start in initialization phase
        assertEquals(GamePhase.INITIALIZATION, bridge.getCurrentPhase(),
            "Bridge should start in INITIALIZATION phase");
        
        assertEquals("Initializing game...", bridge.getPhaseDisplayName(),
            "Should have correct initial display name");
        
        assertFalse(bridge.shouldShowCards(),
            "Should not show cards during initialization");
        assertFalse(bridge.canBet(),
            "Should not allow betting during initialization");
        assertFalse(bridge.canExchangeCards(),
            "Should not allow card exchange during initialization");
    }
    
    @Test
    void testGameInitializationPhases() {
        // Initialize game
        boolean success = bridge.initializeGame("TestPlayer", 3, 1000);
        assertTrue(success, "Game initialization should succeed");
        
        // Debug: Check what phase we're in
        GamePhase currentPhase = bridge.getCurrentPhase();
        System.out.println("Current phase after initialization: " + currentPhase);
        System.out.println("Should show cards: " + bridge.shouldShowCards());
        System.out.println("Phase should show cards: " + currentPhase.shouldShowCards());
        
        // Should have advanced past initialization
        assertNotEquals(GamePhase.INITIALIZATION, bridge.getCurrentPhase(),
            "Should have advanced past initialization");
        
        // Should be showing cards now if the phase allows it
        if (currentPhase.shouldShowCards()) {
            assertTrue(bridge.shouldShowCards(),
                "Should show cards when phase allows it");
        }
        
        // Should have valid phase information
        assertNotNull(bridge.getPhaseDisplayName(),
            "Should have valid phase display name");
        assertNotNull(bridge.getPhaseDescription(),
            "Should have valid phase description");
    }
    
    @Test
    void testPhaseTransitions() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        GamePhase initialPhase = bridge.getCurrentPhase();
        
        // Test manual phase advancement
        GameActionResult result = bridge.advancePhase();
        if (initialPhase.getNextPhase() != null) {
            assertTrue(result.getSuccess(), "Should be able to advance phase");
            assertNotEquals(initialPhase, bridge.getCurrentPhase(),
                "Phase should have changed");
        } else {
            assertFalse(result.getSuccess(), "Should not be able to advance from final phase");
        }
    }
    
    @Test
    void testCardExchangeCompletion() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        // Test card exchange completion
        GameActionResult result = bridge.completeCardExchange();
        assertTrue(result.getSuccess(), "Card exchange completion should succeed");
        assertEquals(GamePhase.FINAL_BETTING, bridge.getCurrentPhase(),
            "Should move to FINAL_BETTING after card exchange");
    }
    
    @Test
    void testPhaseBasedUIControl() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        // Test that the bridge properly exposes phase information
        GamePhase currentPhase = bridge.getCurrentPhase();
        
        // Verify UI control methods work
        boolean shouldShow = bridge.shouldShowCards();
        boolean canBet = bridge.canBet();
        boolean canExchange = bridge.canExchangeCards();
        boolean canProgress = bridge.canProgressRound();
        boolean needsInput = bridge.needsPlayerInput();
        boolean isActive = bridge.isActivePhase();
        
        // These should return valid values
        assertNotNull(bridge.getPhaseDisplayName(),
            "Phase display name should not be null");
        assertNotNull(bridge.getPhaseDescription(),
            "Phase description should not be null");
        
        // At least one of the control methods should be true for an active game
        if (isActive) {
            assertTrue(shouldShow, "Active phase should show cards");
        }
    }
    
    @Test
    void testCardImagePaths() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        // Test card image path generation
        String imagePath = bridge.getCardImagePath(1); // Should map to some card based on our mapping
        System.out.println("Card 1 image path: " + imagePath);
        
        // Basic validation - should be a proper path format
        assertTrue(imagePath.endsWith(".jpg"), 
            "Image path should end with .jpg");
        assertTrue(imagePath.startsWith("Cards/TET/"), 
            "Image path should start with correct directory");
        assertTrue(imagePath.contains(" of "),
            "Image path should contain ' of ' to separate rank and suit");
        
        // Test empty card
        String backPath = bridge.getCardImagePath(0);
        assertTrue(backPath.contains("card_back") || backPath.contains("back"),
            "Empty card should have back image path");
    }
    
    @Test
    void testPlayerHandImagePaths() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        java.util.List<String> imagePaths = bridge.getPlayerHandImagePaths();
        assertNotNull(imagePaths, "Image paths should not be null");
        
        // Should have 5 cards in hand
        assertEquals(5, imagePaths.size(), "Should have 5 card image paths");
        
        // All paths should be valid
        for (String path : imagePaths) {
            assertNotNull(path, "Each image path should not be null");
            assertTrue(path.startsWith("Cards/TET/"), 
                "Each path should start with correct directory");
            assertTrue(path.endsWith(".jpg"), 
                "Each path should end with .jpg");
        }
    }
    
    @Test
    void testPhaseTransitionMethods() {
        bridge.initializeGame("TestPlayer", 3, 1000);
        
        // Test phase transition methods exist and work
        GamePhase beforePhase = bridge.getCurrentPhase();
        
        // Try advancing phase
        GameActionResult result = bridge.advancePhase();
        assertNotNull(result, "Advance phase should return result");
        assertNotNull(result.getMessage(), "Result should have message");
        
        // Verify we can get phase information
        assertNotNull(bridge.getCurrentPhase(), "Current phase should not be null");
        assertNotNull(bridge.getPhaseDisplayName(), "Phase display name should not be null");
        assertNotNull(bridge.getPhaseDescription(), "Phase description should not be null");
    }
}