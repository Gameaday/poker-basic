package com.pokermon.core;

import com.pokermon.api.Game;
import com.pokermon.api.GamePhase;
import java.util.Arrays;

/**
 * Manages the core game logic and flow for poker games.
 * This class provides a centralized way to handle game operations with improved reusability.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class GameEngine {
    private final Game gameConfig;
    private Player[] players;
    private int[] deck;
    private int currentPot;
    private int currentRound;
    private boolean gameActive;
    private int currentPlayerIndex;
    private GamePhase currentPhase;
    
    /**
     * Creates a new game engine with the specified configuration.
     * @param gameConfig the game configuration to use
     */
    public GameEngine(Game gameConfig) {
        this.gameConfig = gameConfig;
        this.currentPot = 0;
        this.currentRound = 0;
        this.gameActive = false;
        this.currentPlayerIndex = 0;
        this.currentPhase = GamePhase.INITIALIZATION;
    }
    
    /**
     * Initializes a new game with the specified players.
     * @param playerNames array of player names
     */
    public void initializeGame(String[] playerNames) {
        if (playerNames == null || playerNames.length == 0) {
            throw new IllegalArgumentException("Must have at least one player");
        }
        if (!gameConfig.isValidPlayerCount(playerNames.length)) {
            throw new IllegalArgumentException("Invalid player count: " + playerNames.length);
        }
        
        this.players = new Player[playerNames.length];
        this.deck = createDeck();
        
        // Initialize players with starting chips
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i], gameConfig.getStartingChips());
            players[i].setHuman(i == 0); // First player is human by default
        }
        
        this.currentPhase = GamePhase.PLAYER_SETUP;
        this.gameActive = true;
        this.currentRound = 1;
    }
    
    /**
     * Creates a standard 52-card deck.
     * @return array representing the deck
     */
    private int[] createDeck() {
        int[] deck = new int[52];
        for (int i = 0; i < 52; i++) {
            deck[i] = i;
        }
        return deck;
    }
    
    /**
     * Shuffles the deck using Fisher-Yates algorithm.
     */
    public void shuffleDeck() {
        if (deck == null) return;
        
        java.util.Random random = new java.util.Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
        this.currentPhase = GamePhase.DECK_CREATION;
    }
    
    /**
     * Starts a new round of the game.
     */
    public void startNewRound() {
        if (!gameActive) {
            throw new IllegalStateException("Game is not active");
        }
        
        shuffleDeck();
        dealHands();
        evaluateHands();
        this.currentPhase = GamePhase.ROUND_START;
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Deals hands to all players.
     */
    private void dealHands() {
        if (players == null || deck == null) return;
        
        java.util.Random random = new java.util.Random();
        boolean[] usedCards = new boolean[deck.length];
        
        for (Player player : players) {
            int[] hand = new int[gameConfig.getHandSize()];
            for (int i = 0; i < hand.length; i++) {
                int cardIndex;
                do {
                    cardIndex = random.nextInt(deck.length);
                } while (usedCards[cardIndex]);
                
                usedCards[cardIndex] = true;
                hand[i] = deck[cardIndex];
            }
            player.setHand(hand);
        }
        
        this.currentPhase = GamePhase.HAND_DEALING;
    }
    
    /**
     * Evaluates all player hands.
     */
    private void evaluateHands() {
        if (players == null) return;
        
        for (Player player : players) {
            player.performAllChecks();
        }
        
        this.currentPhase = GamePhase.HAND_EVALUATION;
    }
    
    /**
     * Starts the betting round.
     */
    public void startBettingRound() {
        this.currentPhase = GamePhase.BETTING_ROUND;
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Processes a player's bet.
     * @param playerIndex the index of the player making the bet
     * @param betAmount the amount to bet
     * @return true if the bet was successful
     */
    public boolean processBet(int playerIndex, int betAmount) {
        if (playerIndex < 0 || playerIndex >= players.length) {
            return false;
        }
        
        Player player = players[playerIndex];
        if (player.getChips() < betAmount) {
            return false; // Insufficient chips
        }
        
        int actualBet = player.removeChips(betAmount);
        player.setBet(player.getBet() + actualBet);
        this.currentPot += actualBet;
        
        return true;
    }
    
    /**
     * Processes a player fold.
     * @param playerIndex the index of the player folding
     */
    public void processFold(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < players.length) {
            players[playerIndex].setFold(true);
        }
    }
    
    /**
     * Advances to the next player in the betting round.
     * @return true if there are more players to act
     */
    public boolean advanceToNextPlayer() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length) {
            currentPlayerIndex = 0;
            this.currentPhase = GamePhase.POT_MANAGEMENT;
            return false;
        }
        return true;
    }
    
    /**
     * Starts the card exchange phase.
     */
    public void startCardExchange() {
        this.currentPhase = GamePhase.CARD_EXCHANGE;
    }
    
    /**
     * Completes card exchange and moves to hand re-evaluation.
     */
    public void completeCardExchange() {
        this.currentPhase = GamePhase.HAND_REEVALUATION;
        // Re-evaluate all hands after card exchange
        if (players != null) {
            for (Player player : players) {
                if (!player.isFold()) {
                    player.performAllChecks();
                }
            }
        }
        this.currentPhase = GamePhase.FINAL_BETTING;
    }
    
    /**
     * Determines the winner(s) of the current round.
     * @return array of winning player indices
     */
    public int[] determineWinners() {
        if (players == null) return new int[0];
        
        this.currentPhase = GamePhase.WINNER_DETERMINATION;
        
        // Find the highest hand value among non-folded players
        int highestValue = 0;
        int winnerCount = 0;
        
        for (Player player : players) {
            if (!player.isFold() && player.getHandValue() > highestValue) {
                highestValue = player.getHandValue();
            }
        }
        
        // Count winners with the highest value
        for (Player player : players) {
            if (!player.isFold() && player.getHandValue() == highestValue) {
                winnerCount++;
            }
        }
        
        // Collect winner indices
        int[] winners = new int[winnerCount];
        int index = 0;
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isFold() && players[i].getHandValue() == highestValue) {
                winners[index++] = i;
            }
        }
        
        return winners;
    }
    
    /**
     * Distributes the pot to the winner(s).
     * @param winners array of winning player indices
     */
    public void distributePot(int[] winners) {
        if (winners == null || winners.length == 0) return;
        
        int winningsPerPlayer = currentPot / winners.length;
        int remainder = currentPot % winners.length;
        
        for (int i = 0; i < winners.length; i++) {
            int winnings = winningsPerPlayer;
            if (i < remainder) {
                winnings++; // Distribute remainder among first few winners
            }
            players[winners[i]].addChips(winnings);
        }
        
        this.currentPot = 0;
        this.currentPhase = GamePhase.POT_DISTRIBUTION;
    }
    
    /**
     * Ends the current round and prepares for the next.
     */
    public void endRound() {
        // Reset player bets and fold status
        if (players != null) {
            for (Player player : players) {
                player.setBet(0);
                player.setFold(false);
            }
        }
        
        this.currentRound++;
        this.currentPhase = GamePhase.ROUND_END;
    }
    
    /**
     * Ends the current game.
     */
    public void endGame() {
        this.gameActive = false;
        this.currentPhase = GamePhase.GAME_END;
    }
    
    // Getters
    
    /**
     * Gets the game configuration.
     * @return the game configuration
     */
    public Game getGameConfig() {
        return gameConfig;
    }
    
    /**
     * Gets all players.
     * @return array of players
     */
    public Player[] getPlayers() {
        return players != null ? players.clone() : null;
    }
    
    /**
     * Gets the current pot amount.
     * @return the current pot
     */
    public int getCurrentPot() {
        return currentPot;
    }
    
    /**
     * Gets the current round number.
     * @return the current round
     */
    public int getCurrentRound() {
        return currentRound;
    }
    
    /**
     * Checks if the game is active.
     * @return true if the game is active
     */
    public boolean isGameActive() {
        return gameActive;
    }
    
    /**
     * Gets the current player's index.
     * @return the index of the current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Gets the current game phase.
     * @return the current phase
     */
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * Gets the current player.
     * @return the current player or null if game not active
     */
    public Player getCurrentPlayer() {
        if (!gameActive || players == null || currentPlayerIndex < 0 || currentPlayerIndex >= players.length) {
            return null;
        }
        return players[currentPlayerIndex];
    }
    
    /**
     * Gets a player by index.
     * @param index the player index
     * @return the player or null if invalid index
     */
    public Player getPlayer(int index) {
        if (players == null || index < 0 || index >= players.length) {
            return null;
        }
        return players[index];
    }
    
    /**
     * Gets the number of active (non-folded) players.
     * @return the count of active players
     */
    public int getActivePlayerCount() {
        if (players == null) return 0;
        
        int count = 0;
        for (Player player : players) {
            if (!player.isFold()) {
                count++;
            }
        }
        return count;
    }
}