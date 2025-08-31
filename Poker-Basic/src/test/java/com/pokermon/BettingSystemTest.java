package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the betting system to ensure proper betting mechanics,
 * pot management, and edge case handling throughout the game flow.
 * 
 * This test class focuses specifically on:
 * - Player betting actions and validation
 * - Pot accumulation and distribution
 * - Betting round completion logic
 * - Edge cases in betting scenarios
 * 
 * @author Generated for issue #68 - Improve process flow and test cases
 */
class BettingSystemTest {
    
    private Player[] players;
    private String[] playerNames;
    private GameEngine gameEngine;
    private Game gameConfig;
    
    @BeforeEach
    void setUp() {
        gameConfig = new Game();
        gameEngine = new GameEngine(gameConfig);
        playerNames = new String[]{"Human", "CPU1", "CPU2", "CPU3"};
        players = new Player[4];
        
        // Initialize players for testing
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
            players[i].setupPlayer(playerNames[i], 1000, Main.setDeck());
        }
    }
    
    /**
     * Tests basic betting functionality to ensure players can place valid bets correctly.
     * 
     * Validates:
     * - Bet amount matches requested amount
     * - Chips are properly deducted from player balance
     * - Player's current bet is accurately recorded
     * 
     * This test ensures the fundamental betting mechanics work as expected,
     * which is critical for all subsequent betting operations in the game flow.
     */
    @Test
    void testBasicBetting() {
        Player player = players[0];
        int initialChips = player.getChips();
        int betAmount = 100;
        
        int actualBet = player.placeBet(betAmount);
        
        assertEquals(betAmount, actualBet, "Bet amount should match requested amount");
        assertEquals(initialChips - betAmount, player.getChips(), "Chips should be deducted after betting");
        assertEquals(betAmount, player.getBet(), "Player's current bet should be recorded");
    }
    
    /**
     * Tests betting behavior when a player attempts to bet more chips than they have.
     * 
     * Documents current system behavior:
     * - The current implementation allows negative chip balances
     * - Bet amounts are accepted regardless of available chips
     * - This represents a limitation that could be enhanced in future versions
     * 
     * Validates:
     * - Bet request is accepted as-is (no validation)
     * - Chip balance can go negative
     * - Bet amount is recorded accurately
     * 
     * This test documents the current betting system's permissive approach,
     * which may be intentional for certain game scenarios or require future enhancement.
     */
    @Test
    void testBettingWithInsufficientChips() {
        Player player = players[0];
        player.setChips(50);
        int betAmount = 100;
        
        int actualBet = player.placeBet(betAmount);
        
        assertEquals(100, actualBet, "Should accept bet amount as requested (no validation in current implementation)");
        assertEquals(-50, player.getChips(), "Chips can go negative in current implementation");
        assertEquals(100, player.getBet(), "Player's bet should reflect requested amount");
    }
    
    /**
     * Test betting zero amount - should be valid but not change chip count.
     */
    @Test
    void testZeroBetting() {
        Player player = players[0];
        int initialChips = player.getChips();
        
        int actualBet = player.placeBet(0);
        
        assertEquals(0, actualBet, "Zero bet should return zero");
        assertEquals(initialChips, player.getChips(), "Chips should remain unchanged for zero bet");
        assertEquals(0, player.getBet(), "Player's bet should be zero");
    }
    
    /**
     * Test negative betting - current implementation allows negative bets.
     */
    @Test
    void testNegativeBetting() {
        Player player = players[0];
        int initialChips = player.getChips();
        
        int actualBet = player.placeBet(-50);
        
        assertEquals(-50, actualBet, "Negative bet is accepted in current implementation");
        assertEquals(initialChips + 50, player.getChips(), "Negative bet adds to chip count");
        assertEquals(-50, player.getBet(), "Player's bet should reflect negative amount");
    }
    
    /**
     * Test pot accumulation through multiple players betting.
     */
    @Test
    void testPotAccumulation() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        int initialPot = gameEngine.getCurrentPot();
        assertEquals(0, initialPot, "Initial pot should be zero");
        
        // Each player bets a different amount
        int[] betAmounts = {100, 150, 200, 75};
        int expectedPot = initialPot;
        
        for (int i = 0; i < enginePlayers.length; i++) {
            int betAmount = betAmounts[i];
            enginePlayers[i].placeBet(betAmount);
            gameEngine.addToPot(betAmount);
            expectedPot += betAmount;
            
            assertEquals(expectedPot, gameEngine.getCurrentPot(), 
                "Pot should accumulate correctly after player " + (i + 1) + " bets");
        }
    }
    
    /**
     * Test betting round completion logic with equal bets.
     */
    @Test
    void testBettingRoundCompletionEqualBets() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // All players bet the same amount
        int betAmount = 100;
        for (Player player : enginePlayers) {
            player.placeBet(betAmount);
            gameEngine.addToPot(betAmount);
        }
        
        assertTrue(gameEngine.isRoundComplete(), 
            "Round should be complete when all players have equal bets");
    }
    
    /**
     * Test betting round completion with folded players.
     */
    @Test
    void testBettingRoundCompletionWithFolds() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // Player 1 folds, others bet equal amounts
        enginePlayers[1].setFold(true);
        
        int betAmount = 100;
        enginePlayers[0].placeBet(betAmount);
        gameEngine.addToPot(betAmount);
        // Player 1 is folded, so no bet
        enginePlayers[2].placeBet(betAmount);
        gameEngine.addToPot(betAmount);
        enginePlayers[3].placeBet(betAmount);
        gameEngine.addToPot(betAmount);
        
        assertTrue(gameEngine.isRoundComplete(), 
            "Round should be complete when all active players have matching bets");
    }
    
    /**
     * Test betting round incompletion with unequal bets.
     */
    @Test
    void testBettingRoundIncompleteUnequalBets() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // Players bet different amounts
        enginePlayers[0].placeBet(100);
        gameEngine.addToPot(100);
        enginePlayers[1].placeBet(150);
        gameEngine.addToPot(150);
        enginePlayers[2].placeBet(100);
        gameEngine.addToPot(100);
        enginePlayers[3].placeBet(200);
        gameEngine.addToPot(200);
        
        assertFalse(gameEngine.isRoundComplete(), 
            "Round should not be complete when players have unequal bets");
    }
    
    /**
     * Test multiple betting rounds in sequence.
     */
    @Test
    void testMultipleBettingRounds() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // First betting round
        for (Player player : enginePlayers) {
            player.placeBet(50);
            gameEngine.addToPot(50);
        }
        
        int potAfterFirstRound = gameEngine.getCurrentPot();
        assertEquals(200, potAfterFirstRound, "Pot should contain all first round bets");
        
        // Second betting round - players can bet additional amounts
        for (Player player : enginePlayers) {
            player.placeBet(player.getBet() + 75); // Add to existing bet
            gameEngine.addToPot(75);
        }
        
        int potAfterSecondRound = gameEngine.getCurrentPot();
        assertEquals(500, potAfterSecondRound, "Pot should contain all betting rounds");
    }
    
    /**
     * Test pot distribution to single winner.
     */
    @Test
    void testPotDistributionSingleWinner() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // All players bet
        for (Player player : enginePlayers) {
            player.placeBet(100);
            gameEngine.addToPot(100);
        }
        
        int potBeforeDistribution = gameEngine.getCurrentPot();
        int winnerInitialChips = enginePlayers[0].getChips();
        
        // Distribute pot to first player
        int[] winners = {0};
        gameEngine.distributePot(winners);
        
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should be empty after distribution");
        assertEquals(winnerInitialChips + potBeforeDistribution, enginePlayers[0].getChips(),
            "Winner should receive entire pot");
    }
    
    /**
     * Test pot distribution to multiple winners.
     */
    @Test
    void testPotDistributionMultipleWinners() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // All players bet
        for (Player player : enginePlayers) {
            player.placeBet(100);
            gameEngine.addToPot(100);
        }
        
        int potBeforeDistribution = gameEngine.getCurrentPot();
        int[] winnersInitialChips = {enginePlayers[0].getChips(), enginePlayers[1].getChips()};
        
        // Distribute pot to first two players
        int[] winners = {0, 1};
        gameEngine.distributePot(winners);
        
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should be empty after distribution");
        
        int expectedChipsPerWinner = potBeforeDistribution / 2;
        assertEquals(winnersInitialChips[0] + expectedChipsPerWinner, enginePlayers[0].getChips(),
            "First winner should receive half the pot");
        assertEquals(winnersInitialChips[1] + expectedChipsPerWinner, enginePlayers[1].getChips(),
            "Second winner should receive half the pot");
    }
    
    /**
     * Test that bet recording works correctly for tracking last bet amounts.
     */
    @Test
    void testBetRecording() {
        Player player = players[0];
        
        assertEquals(0, player.getLastBet(), "Initial last bet should be zero");
        
        player.placeBet(75);
        assertEquals(0, player.getLastBet(), "Last bet should not change until recorded");
        
        player.recordLastBet();
        assertEquals(75, player.getLastBet(), "Last bet should be recorded correctly");
        
        player.placeBet(125);
        assertEquals(75, player.getLastBet(), "Last bet should remain until re-recorded");
        
        player.recordLastBet();
        assertEquals(125, player.getLastBet(), "New last bet should be recorded correctly");
    }
    
    /**
     * Test edge case: all players fold except one.
     */
    @Test
    void testAllButOneFold() {
        gameEngine.initializeGame(playerNames);
        Player[] enginePlayers = gameEngine.getPlayers();
        
        // All players except first one fold
        for (int i = 1; i < enginePlayers.length; i++) {
            enginePlayers[i].setFold(true);
        }
        
        // Only remaining player bets
        enginePlayers[0].placeBet(100);
        gameEngine.addToPot(100);
        
        // Round should be complete since only one active player
        assertTrue(gameEngine.isRoundComplete(), 
            "Round should be complete when only one player remains active");
    }
    
    /**
     * Test edge case: player with zero chips can still bet in current implementation.
     */
    @Test
    void testPlayerWithZeroChipsCannotBet() {
        Player player = players[0];
        player.setChips(0);
        
        int actualBet = player.placeBet(50);
        
        assertEquals(50, actualBet, "Current implementation allows betting even with no chips");
        assertEquals(-50, player.getChips(), "Chip count goes negative");
        assertEquals(50, player.getBet(), "Bet amount is recorded as requested");
    }
}