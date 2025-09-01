package com.pokermon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the enhanced game phase system.
 * Validates that game phases transition properly and control UI behavior correctly.
 */
class GamePhaseTest {
    private GameEngine gameEngine;
    private Game gameConfig;
    private String[] playerNames;
    
    @BeforeEach
    void setUp() {
        gameConfig = new Game();
        gameEngine = new GameEngine(gameConfig);
        playerNames = new String[]{"Player", "CPU1", "CPU2"};
    }
    
    @Test
    void testInitialGamePhase() {
        // New game engine should start in initialization phase
        assertEquals(GamePhase.INITIALIZATION, gameEngine.getCurrentPhase(),
            "New game engine should start in INITIALIZATION phase");
    }
    
    @Test
    void testGameInitializationPhases() {
        // Initialize game should progress through setup phases
        boolean success = gameEngine.initializeGame(playerNames);
        assertTrue(success, "Game initialization should succeed");
        
        // After initialization, should be in ROUND_START phase
        assertEquals(GamePhase.ROUND_START, gameEngine.getCurrentPhase(),
            "After initialization, should be in ROUND_START phase");
    }
    
    @Test
    void testRoundStartPhases() {
        gameEngine.initializeGame(playerNames);
        
        // Start new round should progress through phases
        gameEngine.startNewRound();
        
        // Should end up in BETTING_ROUND phase
        assertEquals(GamePhase.BETTING_ROUND, gameEngine.getCurrentPhase(),
            "After starting round, should be in BETTING_ROUND phase");
    }
    
    @Test
    void testBettingPhases() {
        gameEngine.initializeGame(playerNames);
        gameEngine.startNewRound();
        
        // Conduct betting should progress through betting phases
        gameEngine.conductBettingRound();
        
        // Should end up in POT_MANAGEMENT phase
        assertEquals(GamePhase.POT_MANAGEMENT, gameEngine.getCurrentPhase(),
            "After betting round, should be in POT_MANAGEMENT phase");
    }
    
    @Test
    void testCardExchangePhases() {
        gameEngine.initializeGame(playerNames);
        gameEngine.startNewRound();
        
        // Begin card exchange
        gameEngine.beginCardExchange();
        assertEquals(GamePhase.CARD_EXCHANGE, gameEngine.getCurrentPhase(),
            "After beginning card exchange, should be in CARD_EXCHANGE phase");
        
        // Exchange some cards
        gameEngine.exchangeCards(0, new int[]{0, 1});
        assertEquals(GamePhase.CARD_EXCHANGE, gameEngine.getCurrentPhase(),
            "During card exchange, should remain in CARD_EXCHANGE phase");
        
        // Complete card exchange
        gameEngine.completeCardExchange();
        assertEquals(GamePhase.FINAL_BETTING, gameEngine.getCurrentPhase(),
            "After completing card exchange, should be in FINAL_BETTING phase");
    }
    
    @Test
    void testWinnerDeterminationPhases() {
        gameEngine.initializeGame(playerNames);
        gameEngine.startNewRound();
        
        // Determine winners
        int[] winners = gameEngine.determineWinners();
        assertEquals(GamePhase.WINNER_DETERMINATION, gameEngine.getCurrentPhase(),
            "During winner determination, should be in WINNER_DETERMINATION phase");
        
        // Distribute pot
        gameEngine.distributePot(winners);
        assertEquals(GamePhase.ROUND_END, gameEngine.getCurrentPhase(),
            "After pot distribution, should be in ROUND_END phase");
    }
    
    @Test
    void testGameEndPhase() {
        gameEngine.initializeGame(playerNames);
        
        // End game
        gameEngine.endGame();
        assertEquals(GamePhase.GAME_END, gameEngine.getCurrentPhase(),
            "After ending game, should be in GAME_END phase");
        assertFalse(gameEngine.isGameActive(),
            "Game should not be active after ending");
    }
    
    @Test
    void testPhaseAdvancement() {
        gameEngine.initializeGame(playerNames);
        
        GamePhase initialPhase = gameEngine.getCurrentPhase();
        boolean advanced = gameEngine.advancePhase();
        
        if (initialPhase.getNextPhase() != null) {
            assertTrue(advanced, "Should be able to advance from " + initialPhase);
            assertEquals(initialPhase.getNextPhase(), gameEngine.getCurrentPhase(),
                "Should advance to next phase");
        } else {
            assertFalse(advanced, "Should not be able to advance from " + initialPhase);
        }
    }
    
    @Test
    void testManualPhaseSet() {
        gameEngine.initializeGame(playerNames);
        
        // Manually set phase
        gameEngine.setPhase(GamePhase.CARD_EXCHANGE);
        assertEquals(GamePhase.CARD_EXCHANGE, gameEngine.getCurrentPhase(),
            "Should be able to manually set phase");
    }
    
    @Test
    void testPhaseProperties() {
        // Test BETTING_ROUND phase properties
        assertTrue(GamePhase.BETTING_ROUND.shouldShowCards(),
            "BETTING_ROUND should show cards");
        assertTrue(GamePhase.BETTING_ROUND.allowsBetting(),
            "BETTING_ROUND should allow betting");
        assertFalse(GamePhase.BETTING_ROUND.allowsCardExchange(),
            "BETTING_ROUND should not allow card exchange");
        
        // Test CARD_EXCHANGE phase properties
        assertTrue(GamePhase.CARD_EXCHANGE.shouldShowCards(),
            "CARD_EXCHANGE should show cards");
        assertFalse(GamePhase.CARD_EXCHANGE.allowsBetting(),
            "CARD_EXCHANGE should not allow betting");
        assertTrue(GamePhase.CARD_EXCHANGE.allowsCardExchange(),
            "CARD_EXCHANGE should allow card exchange");
        
        // Test INITIALIZATION phase properties
        assertFalse(GamePhase.INITIALIZATION.shouldShowCards(),
            "INITIALIZATION should not show cards");
        assertFalse(GamePhase.INITIALIZATION.allowsBetting(),
            "INITIALIZATION should not allow betting");
        assertFalse(GamePhase.INITIALIZATION.allowsCardExchange(),
            "INITIALIZATION should not allow card exchange");
    }
    
    @Test
    void testPhaseClassification() {
        // Test setup phases
        assertTrue(GamePhase.INITIALIZATION.isSetupPhase(),
            "INITIALIZATION should be a setup phase");
        assertTrue(GamePhase.PLAYER_SETUP.isSetupPhase(),
            "PLAYER_SETUP should be a setup phase");
        assertTrue(GamePhase.DECK_CREATION.isSetupPhase(),
            "DECK_CREATION should be a setup phase");
        assertFalse(GamePhase.BETTING_ROUND.isSetupPhase(),
            "BETTING_ROUND should not be a setup phase");
        
        // Test active phases
        assertFalse(GamePhase.INITIALIZATION.isActivePhase(),
            "INITIALIZATION should not be an active phase");
        assertTrue(GamePhase.BETTING_ROUND.isActivePhase(),
            "BETTING_ROUND should be an active phase");
        assertTrue(GamePhase.CARD_EXCHANGE.isActivePhase(),
            "CARD_EXCHANGE should be an active phase");
        assertFalse(GamePhase.GAME_END.isActivePhase(),
            "GAME_END should not be an active phase");
        
        // Test phases requiring player input
        assertTrue(GamePhase.BETTING_ROUND.requiresPlayerInput(),
            "BETTING_ROUND should require player input");
        assertTrue(GamePhase.CARD_EXCHANGE.requiresPlayerInput(),
            "CARD_EXCHANGE should require player input");
        assertTrue(GamePhase.ROUND_END.requiresPlayerInput(),
            "ROUND_END should require player input");
        assertFalse(GamePhase.HAND_DEALING.requiresPlayerInput(),
            "HAND_DEALING should not require player input");
    }
    
    @Test
    void testPhaseFlow() {
        // Test that phases flow in logical order
        assertEquals(GamePhase.PLAYER_SETUP, GamePhase.INITIALIZATION.getNextPhase(),
            "INITIALIZATION should lead to PLAYER_SETUP");
        assertEquals(GamePhase.DECK_CREATION, GamePhase.PLAYER_SETUP.getNextPhase(),
            "PLAYER_SETUP should lead to DECK_CREATION");
        assertEquals(GamePhase.ROUND_START, GamePhase.DECK_CREATION.getNextPhase(),
            "DECK_CREATION should lead to ROUND_START");
        assertEquals(GamePhase.HAND_DEALING, GamePhase.ROUND_START.getNextPhase(),
            "ROUND_START should lead to HAND_DEALING");
        
        // Test that GAME_END has no next phase
        assertNull(GamePhase.GAME_END.getNextPhase(),
            "GAME_END should have no next phase");
    }
}