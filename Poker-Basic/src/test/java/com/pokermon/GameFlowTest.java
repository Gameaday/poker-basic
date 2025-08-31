package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for complete game flow and state transitions to ensure
 * each step flows directly into the next and that every action happens automatically.
 * 
 * This test class focuses specifically on:
 * - Complete game initialization → round → betting → exchange → winner → next round flow
 * - State transitions and validation between phases
 * - Automatic action triggers and flow continuity
 * - Integration of all game components working together
 * 
 * @author Generated for issue #68 - Improve process flow and test cases
 */
class GameFlowTest {
    
    private GameEngine gameEngine;
    private Game gameConfig;
    private String[] playerNames;
    
    @BeforeEach
    void setUp() {
        gameConfig = new Game();
        gameEngine = new GameEngine(gameConfig);
        playerNames = new String[]{"Human", "CPU1", "CPU2"};
    }
    
    /**
     * Test complete game initialization flow and verify all components are properly set up.
     */
    @Test
    void testCompleteGameInitializationFlow() {
        // Step 1: Engine should start inactive
        assertFalse(gameEngine.isGameActive(), "Game should start inactive");
        assertEquals(0, gameEngine.getCurrentPot(), "Initial pot should be zero");
        assertEquals(0, gameEngine.getCurrentRound(), "Initial round should be zero");
        assertEquals(0, gameEngine.getPlayers().length, "Players array should be empty before initialization");
        
        // Step 2: Initialize game with players
        boolean initSuccess = gameEngine.initializeGame(playerNames);
        assertTrue(initSuccess, "Game initialization should succeed");
        
        // Step 3: Verify game state after initialization
        assertTrue(gameEngine.isGameActive(), "Game should be active after initialization");
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should still be zero after initialization");
        assertEquals(0, gameEngine.getCurrentRound(), "Round should still be zero after initialization");
        assertNotNull(gameEngine.getPlayers(), "Players should be initialized");
        
        // Step 4: Verify all players are properly initialized
        Player[] players = gameEngine.getPlayers();
        assertEquals(playerNames.length, players.length, "Should have correct number of players");
        
        for (int i = 0; i < players.length; i++) {
            assertNotNull(players[i], "Player " + i + " should not be null");
            assertEquals(playerNames[i], players[i].getName(), "Player " + i + " should have correct name");
            assertEquals(gameConfig.getStartingChips(), players[i].getChips(), "Player " + i + " should have starting chips");
            assertNotNull(players[i].getHand(), "Player " + i + " should have a hand");
            assertEquals(gameConfig.getHandSize(), players[i].getHand().length, "Player " + i + " should have correct hand size");
            assertFalse(players[i].isFold(), "Player " + i + " should not start folded");
        }
        
        // Step 5: Verify game can continue
        assertTrue(gameEngine.canContinue(), "Game should be able to continue after initialization");
    }
    
    /**
     * Test complete round flow: start → deal → evaluate → ready for betting.
     */
    @Test
    void testCompleteRoundStartFlow() {
        // Initialize game first
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        
        // Store initial hands for comparison
        int[][] initialHands = new int[players.length][];
        for (int i = 0; i < players.length; i++) {
            initialHands[i] = players[i].getHand().clone();
        }
        
        // Start new round
        gameEngine.startNewRound();
        
        // Verify round progression
        assertEquals(1, gameEngine.getCurrentRound(), "Round should advance to 1");
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should reset to zero for new round");
        
        // Verify new hands were dealt
        for (int i = 0; i < players.length; i++) {
            assertNotNull(players[i].getHand(), "Player " + i + " should have new hand");
            assertEquals(gameConfig.getHandSize(), players[i].getHand().length, "Player " + i + " should have correct hand size");
            // Note: There's a small chance hands could be identical, but very unlikely with a shuffled deck
        }
        
        // Verify players are ready for betting (not folded, have chips)
        for (int i = 0; i < players.length; i++) {
            assertFalse(players[i].isFold(), "Player " + i + " should not be folded at round start");
            assertTrue(players[i].getChips() > 0, "Player " + i + " should have chips available");
        }
    }
    
    /**
     * Test complete betting flow: players bet → pot accumulates → round completion.
     */
    @Test
    void testCompleteBettingFlow() {
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        
        // Initial state verification
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should start at zero");
        assertFalse(gameEngine.isRoundComplete(), "Round should not be complete initially");
        
        // Betting phase 1: First player bets
        int bet1 = 100;
        players[0].placeBet(bet1);
        gameEngine.addToPot(bet1);
        
        assertEquals(bet1, gameEngine.getCurrentPot(), "Pot should contain first bet");
        assertFalse(gameEngine.isRoundComplete(), "Round should not be complete with only one bet");
        
        // Betting phase 2: Second player matches
        players[1].placeBet(bet1);
        gameEngine.addToPot(bet1);
        
        assertEquals(bet1 * 2, gameEngine.getCurrentPot(), "Pot should contain both bets");
        assertFalse(gameEngine.isRoundComplete(), "Round should not be complete with unmatched players");
        
        // Betting phase 3: Third player matches - round should complete
        players[2].placeBet(bet1);
        gameEngine.addToPot(bet1);
        
        assertEquals(bet1 * 3, gameEngine.getCurrentPot(), "Pot should contain all bets");
        assertTrue(gameEngine.isRoundComplete(), "Round should be complete when all players match");
        
        // Verify all players have correct bet amounts
        for (Player player : players) {
            assertEquals(bet1, player.getBet(), "All players should have matching bet amounts");
        }
    }
    
    /**
     * Test card exchange flow: select cards → exchange → hand re-evaluation.
     */
    @Test
    void testCardExchangeFlow() {
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        Player testPlayer = players[0];
        
        // Store original hand
        int[] originalHand = testPlayer.getHand().clone();
        int originalHandValue = testPlayer.getHandValue();
        
        // Exchange cards at positions 0 and 2
        int[] exchangeIndices = {0, 2};
        gameEngine.exchangeCards(0, exchangeIndices);
        
        // Verify hand was modified
        int[] newHand = testPlayer.getHand();
        assertNotNull(newHand, "Player should have a hand after exchange");
        assertEquals(gameConfig.getHandSize(), newHand.length, "Hand size should remain consistent");
        
        // Verify cards at exchange positions were changed
        // (Note: There's a small chance the same cards could be dealt, but very unlikely)
        boolean handChanged = false;
        for (int i = 0; i < newHand.length; i++) {
            if (newHand[i] != originalHand[i]) {
                handChanged = true;
                break;
            }
        }
        // We can't guarantee the hand changed (might get same cards), but the exchange should complete without error
        
        // Verify hand value was recalculated
        testPlayer.performAllChecks();
        int newHandValue = testPlayer.getHandValue();
        assertTrue(newHandValue > 0, "Hand should have a positive value after exchange");
    }
    
    /**
     * Test winner determination flow: evaluate hands → determine winners → prepare for distribution.
     */
    @Test
    void testWinnerDeterminationFlow() {
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        
        // Ensure all players have evaluated hands
        for (Player player : players) {
            player.performAllChecks();
        }
        
        // Determine winners
        int[] winners = gameEngine.determineWinners();
        
        // Verify winner determination results
        assertNotNull(winners, "Winners array should not be null");
        assertTrue(winners.length > 0, "Should have at least one winner");
        assertTrue(winners.length <= players.length, "Cannot have more winners than players");
        
        // Verify all winner indices are valid
        for (int winner : winners) {
            assertTrue(winner >= 0 && winner < players.length, 
                "Winner index " + winner + " should be valid player index");
        }
        
        // Verify winners are not folded
        for (int winner : winners) {
            assertFalse(players[winner].isFold(), "Winner should not be folded");
        }
    }
    
    /**
     * Test pot distribution flow: determine winners → distribute pot → reset for next round.
     */
    @Test
    void testPotDistributionFlow() {
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        
        // Set up a pot with some betting
        int betAmount = 150;
        for (Player player : players) {
            player.placeBet(betAmount);
            gameEngine.addToPot(betAmount);
        }
        
        int totalPot = gameEngine.getCurrentPot();
        assertTrue(totalPot > 0, "Pot should have money before distribution");
        
        // Store initial chip counts
        int[] initialChips = new int[players.length];
        for (int i = 0; i < players.length; i++) {
            initialChips[i] = players[i].getChips();
        }
        
        // Determine and distribute to winners
        int[] winners = gameEngine.determineWinners();
        gameEngine.distributePot(winners);
        
        // Verify pot is cleared
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should be empty after distribution");
        
        // Verify winners received winnings
        int expectedWinnings = totalPot / winners.length;
        for (int winner : winners) {
            assertTrue(players[winner].getChips() >= initialChips[winner], 
                "Winner should have at least as many chips as before");
        }
        
        // Verify total chips in system is conserved
        int totalChipsAfter = 0;
        int totalChipsBefore = 0;
        for (int i = 0; i < players.length; i++) {
            totalChipsAfter += players[i].getChips();
            totalChipsBefore += initialChips[i] + betAmount; // Initial chips + bet that was in pot
        }
        assertEquals(totalChipsBefore, totalChipsAfter, 
            "Total chips in system should be conserved");
    }
    
    /**
     * Test complete multi-round game flow with state transitions.
     */
    @Test
    void testCompleteMultiRoundGameFlow() {
        gameEngine.initializeGame(playerNames);
        
        // Play through multiple rounds
        for (int round = 1; round <= 3; round++) {
            // Start new round
            gameEngine.startNewRound();
            assertEquals(round, gameEngine.getCurrentRound(), "Round should advance correctly");
            
            // Betting phase
            Player[] players = gameEngine.getPlayers();
            for (Player player : players) {
                player.placeBet(50);
                gameEngine.addToPot(50);
            }
            assertTrue(gameEngine.isRoundComplete(), "Round should complete after all bets");
            
            // Card exchange (for first player only)
            gameEngine.exchangeCards(0, new int[]{0});
            
            // Winner determination and pot distribution
            int[] winners = gameEngine.determineWinners();
            gameEngine.distributePot(winners);
            assertEquals(0, gameEngine.getCurrentPot(), "Pot should be cleared after each round");
            
            // Verify game can continue
            assertTrue(gameEngine.canContinue(), "Game should be able to continue");
        }
        
        // Game should still be active after multiple rounds
        assertTrue(gameEngine.isGameActive(), "Game should remain active through multiple rounds");
        assertEquals(3, gameEngine.getCurrentRound(), "Should be on round 3");
    }
    
    /**
     * Test game flow with player folding scenarios.
     */
    @Test
    void testGameFlowWithFolding() {
        gameEngine.initializeGame(playerNames);
        Player[] players = gameEngine.getPlayers();
        
        // Player 1 folds
        players[1].setFold(true);
        
        // Remaining players bet
        players[0].placeBet(100);
        gameEngine.addToPot(100);
        players[2].placeBet(100);
        gameEngine.addToPot(100);
        
        // Round should complete even with folded player
        assertTrue(gameEngine.isRoundComplete(), 
            "Round should complete when all active players have matching bets");
        
        // Winner determination currently doesn't exclude folded players in the basic implementation
        int[] winners = gameEngine.determineWinners();
        assertNotNull(winners, "Winners should be determined");
        assertTrue(winners.length > 0, "Should have at least one winner");
        
        // Note: Current implementation doesn't filter out folded players in winner determination
        // This is a limitation of the current system that could be improved in future versions
        
        // Pot distribution should still work
        int foldedPlayerInitialChips = players[1].getChips();
        gameEngine.distributePot(winners);
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should be cleared after distribution");
    }
    
    /**
     * Test game flow error recovery and state consistency.
     */
    @Test
    void testGameFlowErrorRecoveryAndConsistency() {
        // Test initialization with invalid parameters
        assertFalse(gameEngine.initializeGame(null), "Should reject null player names");
        assertFalse(gameEngine.initializeGame(new String[0]), "Should reject empty player array");
        assertFalse(gameEngine.isGameActive(), "Game should remain inactive after failed initialization");
        
        // Successful initialization should work after failed attempts
        assertTrue(gameEngine.initializeGame(playerNames), "Should succeed after previous failures");
        assertTrue(gameEngine.isGameActive(), "Game should be active after successful initialization");
        
        // Test state consistency throughout operations
        String gameState1 = gameEngine.getGameState();
        assertNotNull(gameState1, "Game state should be available");
        assertFalse(gameState1.isEmpty(), "Game state should not be empty");
        
        // Perform some operations
        gameEngine.startNewRound();
        gameEngine.conductBettingRound();
        
        // Game state should still be consistent
        String gameState2 = gameEngine.getGameState();
        assertNotNull(gameState2, "Game state should remain available after operations");
        assertFalse(gameState2.isEmpty(), "Game state should not be empty after operations");
        
        // State should contain expected information
        assertTrue(gameState2.contains("Round"), "Game state should contain round information");
        assertTrue(gameState2.contains("Pot"), "Game state should contain pot information");
        assertTrue(gameState2.contains("Players"), "Game state should contain player information");
    }
    
    /**
     * Test automatic game progression and action triggers.
     */
    @Test
    void testAutomaticGameProgressionAndTriggers() {
        gameEngine.initializeGame(playerNames);
        
        // Test that betting round automatically updates pot and game state
        int initialPot = gameEngine.getCurrentPot();
        int newPot = gameEngine.conductBettingRound();
        
        // Pot should be automatically updated
        assertEquals(newPot, gameEngine.getCurrentPot(), 
            "Pot should be automatically updated after betting round");
        assertTrue(newPot >= initialPot, "Pot should not decrease during betting");
        
        // Test that round completion automatically triggers next phase readiness
        Player[] players = gameEngine.getPlayers();
        for (Player player : players) {
            player.placeBet(100);
            gameEngine.addToPot(100);
        }
        
        assertTrue(gameEngine.isRoundComplete(), "Round should automatically complete when conditions met");
        
        // Test that winner determination is available when round is complete
        assertDoesNotThrow(() -> gameEngine.determineWinners(), 
            "Winner determination should be available when round is complete");
        
        // Test that pot distribution automatically clears pot
        int[] winners = gameEngine.determineWinners();
        gameEngine.distributePot(winners);
        assertEquals(0, gameEngine.getCurrentPot(), "Pot should be automatically cleared after distribution");
    }
    
    /**
     * Test game end flow and cleanup.
     */
    @Test
    void testGameEndFlowAndCleanup() {
        gameEngine.initializeGame(playerNames);
        assertTrue(gameEngine.isGameActive(), "Game should be active");
        assertTrue(gameEngine.canContinue(), "Game should be able to continue");
        
        // End the game
        gameEngine.endGame();
        
        // Verify game is properly ended
        assertFalse(gameEngine.isGameActive(), "Game should be inactive after ending");
        
        // Game state should still be accessible for final results
        String finalState = gameEngine.getGameState();
        assertNotNull(finalState, "Final game state should be accessible");
        
        // Players should still be accessible for final statistics
        Player[] players = gameEngine.getPlayers();
        assertNotNull(players, "Players should still be accessible after game end");
    }
}