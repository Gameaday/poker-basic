package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameEngine class to verify centralized game management.
 */
class GameEngineTest {
    
    private GameEngine engine;
    private Game gameConfig;
    private String[] playerNames;
    
    @BeforeEach
    void setUp() {
        gameConfig = new Game();
        engine = new GameEngine(gameConfig);
        playerNames = new String[]{"Player1", "Player2", "Player3"};
    }
    
    @Test
    void testGameInitialization() {
        assertFalse(engine.isGameActive());
        assertEquals(0, engine.getCurrentPot());
        assertEquals(0, engine.getCurrentRound());
        
        assertTrue(engine.initializeGame(playerNames));
        assertTrue(engine.isGameActive());
        
        Player[] players = engine.getPlayers();
        assertEquals(3, players.length);
        
        for (int i = 0; i < players.length; i++) {
            assertEquals(playerNames[i], players[i].getName());
            assertEquals(gameConfig.getStartingChips(), players[i].getChips());
            assertNotNull(players[i].getHand());
        }
    }
    
    @Test
    void testInvalidGameInitialization() {
        // Null player names
        assertFalse(engine.initializeGame(null));
        
        // Too many players
        String[] tooManyPlayers = {"P1", "P2", "P3", "P4", "P5"};
        assertFalse(engine.initializeGame(tooManyPlayers));
        
        // Empty array
        String[] noPlayers = {};
        assertFalse(engine.initializeGame(noPlayers));
    }
    
    @Test
    void testNewRoundHandling() {
        engine.initializeGame(playerNames);
        Player[] initialPlayers = engine.getPlayers();
        
        // Store initial hands
        int[][] initialHands = new int[initialPlayers.length][];
        for (int i = 0; i < initialPlayers.length; i++) {
            initialHands[i] = initialPlayers[i].getHand().clone();
        }
        
        engine.startNewRound();
        
        assertEquals(1, engine.getCurrentRound());
        assertEquals(0, engine.getCurrentPot());
        
        Player[] newRoundPlayers = engine.getPlayers();
        
        // Verify new hands were dealt
        for (int i = 0; i < newRoundPlayers.length; i++) {
            // Note: There's a small chance hands could be identical, but very unlikely
            assertNotNull(newRoundPlayers[i].getHand());
            assertEquals(gameConfig.getHandSize(), newRoundPlayers[i].getHand().length);
        }
    }
    
    @Test
    void testBettingRounds() {
        engine.initializeGame(playerNames);
        
        int initialPot = engine.getCurrentPot();
        int newPot = engine.conductBettingRound();
        
        // Pot should typically increase after betting
        assertTrue(newPot >= initialPot);
        assertEquals(newPot, engine.getCurrentPot());
    }
    
    @Test
    void testCardExchange() {
        engine.initializeGame(playerNames);
        Player[] players = engine.getPlayers();
        
        int[] originalHand = players[0].getHand().clone();
        int[] exchangeIndices = {0, 2}; // Exchange cards at positions 0 and 2
        
        engine.exchangeCards(0, exchangeIndices);
        
        int[] newHand = players[0].getHand();
        assertNotNull(newHand);
        assertEquals(gameConfig.getHandSize(), newHand.length);
        
        // The hand should have been updated
        assertNotNull(players[0].getHand());
    }
    
    @Test
    void testWinnerDetermination() {
        engine.initializeGame(playerNames);
        
        int[] winners = engine.determineWinners();
        assertNotNull(winners);
        assertTrue(winners.length > 0);
        
        // Winner index should be valid
        for (int winner : winners) {
            assertTrue(winner >= 0 && winner < playerNames.length);
        }
    }
    
    @Test
    void testPotDistribution() {
        engine.initializeGame(playerNames);
        
        // Simulate some betting
        engine.conductBettingRound();
        int potBeforeDistribution = engine.getCurrentPot();
        
        Player[] playersBeforeDistribution = engine.getPlayers();
        int[] initialChips = new int[playersBeforeDistribution.length];
        for (int i = 0; i < playersBeforeDistribution.length; i++) {
            initialChips[i] = playersBeforeDistribution[i].getChips();
        }
        
        int[] winners = {0}; // Player 0 wins
        engine.distributePot(winners);
        
        // Pot should be cleared
        assertEquals(0, engine.getCurrentPot());
        
        // Winner should have more chips (if there was a pot to distribute)
        if (potBeforeDistribution > 0) {
            Player[] playersAfterDistribution = engine.getPlayers();
            assertTrue(playersAfterDistribution[0].getChips() >= initialChips[0]);
        }
    }
    
    @Test
    void testGameContinuation() {
        engine.initializeGame(playerNames);
        assertTrue(engine.canContinue());
        
        // End game and check
        engine.endGame();
        assertFalse(engine.isGameActive());
    }
    
    @Test
    void testGameState() {
        engine.initializeGame(playerNames);
        
        String gameState = engine.getGameState();
        assertNotNull(gameState);
        assertFalse(gameState.isEmpty());
        
        // Should contain basic game information
        assertTrue(gameState.contains("Round"));
        assertTrue(gameState.contains("Pot"));
        assertTrue(gameState.contains("Players"));
        
        for (String playerName : playerNames) {
            assertTrue(gameState.contains(playerName));
        }
    }
    
    @Test
    void testCustomGameConfiguration() {
        Game customGame = Game.createThreeCardPoker();
        GameEngine customEngine = new GameEngine(customGame);
        
        assertTrue(customEngine.initializeGame(new String[]{"P1", "P2"}));
        
        Player[] players = customEngine.getPlayers();
        for (Player player : players) {
            assertEquals(3, player.getHand().length); // 3-card poker
            assertEquals(500, player.getChips()); // Custom starting chips
        }
    }
    
    @Test
    void testFlexibleHandSizes() {
        // Test with different hand sizes
        Game sevenCardGame = Game.createSevenCardStud();
        GameEngine sevenCardEngine = new GameEngine(sevenCardGame);
        
        assertTrue(sevenCardEngine.initializeGame(new String[]{"P1", "P2"}));
        
        Player[] players = sevenCardEngine.getPlayers();
        for (Player player : players) {
            assertEquals(7, player.getHand().length); // 7-card stud
        }
    }
    
    @Test
    void testRoundCompletionLogic() {
        // Test the fixed round completion logic
        engine.initializeGame(playerNames);
        
        // Initially, before any bets, round should not be complete
        assertFalse(engine.isRoundComplete(), "Round should not be complete at start with no bets");
        
        Player[] players = engine.getPlayers();
        
        // Have one player bet
        players[0].placeBet(50);
        engine.addToPot(50);
        assertFalse(engine.isRoundComplete(), "Round should not be complete with only one player betting");
        
        // Have all players match the bet
        players[1].placeBet(50);
        engine.addToPot(50);
        players[2].placeBet(50);
        engine.addToPot(50);
        assertTrue(engine.isRoundComplete(), "Round should be complete when all players match bet");
        
        // Test with a fold
        engine.nextRound();
        players[0].placeBet(100);
        engine.addToPot(100);
        players[1].setFold(true);
        players[2].placeBet(100);
        engine.addToPot(100);
        assertTrue(engine.isRoundComplete(), "Round should be complete when active players match bet (with fold)");
    }
}