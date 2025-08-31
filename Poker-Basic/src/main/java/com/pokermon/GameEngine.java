package com.pokermon;

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
    
    /**
     * Creates a new game engine with the specified configuration.
     * @param gameConfig the game configuration to use
     */
    public GameEngine(Game gameConfig) {
        this.gameConfig = gameConfig;
        this.currentPot = 0;
        this.currentRound = 0;
        this.gameActive = false;
    }
    
    /**
     * Initializes a new game with the specified players.
     * @param playerNames array of player names
     * @return true if initialization was successful
     */
    public boolean initializeGame(String[] playerNames) {
        if (playerNames == null || !gameConfig.isValidPlayerCount(playerNames.length)) {
            return false;
        }
        
        this.players = new Player[playerNames.length];
        this.deck = Main.setDeck();
        this.currentPot = 0;
        this.currentRound = 0;
        
        // Initialize all players
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player();
            players[i].setupPlayer(playerNames[i], gameConfig.getStartingChips(), deck, gameConfig.getHandSize());
        }
        
        this.gameActive = true;
        return true;
    }
    
    /**
     * Starts a new round by dealing new hands to all players.
     */
    public void startNewRound() {
        if (!gameActive || players == null) {
            return;
        }
        
        // Reset deck for new round
        this.deck = Main.setDeck();
        this.currentPot = 0;
        this.currentRound++;
        
        // Deal new hands to all active players
        for (Player player : players) {
            if (!player.isFold() && player.getChips() > 0) {
                player.resetFold();
                player.updateHand(generateHand(deck, gameConfig.getHandSize()));
                player.performAllChecks();
            }
        }
    }
    
    /**
     * Generates a hand of the specified size from the deck.
     * @param deck the deck to draw from
     * @param handSize the number of cards to draw
     * @return an array representing the hand
     */
    private int[] generateHand(int[] deck, int handSize) {
        int[] hand = new int[handSize];
        for (int i = 0; i < handSize; i++) {
            hand[i] = Main.drawCard(deck);
        }
        return hand;
    }
    
    /**
     * Conducts a betting round with all active players.
     * @return the total pot after betting
     */
    public int conductBettingRound() {
        if (!gameActive || players == null) {
            return currentPot;
        }
        
        currentPot = Main.bet(players, currentPot);
        return currentPot;
    }
    
    /**
     * Allows a player to exchange cards (for draw poker variants).
     * @param playerIndex the index of the player
     * @param cardIndices the indices of cards to replace
     */
    public void exchangeCards(int playerIndex, int[] cardIndices) {
        if (!gameActive || players == null || playerIndex < 0 || playerIndex >= players.length) {
            return;
        }
        
        Player player = players[playerIndex];
        if (cardIndices != null) {
            for (int index : cardIndices) {
                player.removeCardAtIndex(index);
            }
        }
        
        Main.replaceCards(player.getHandForModification(), deck);
        player.performAllChecks();
    }
    
    /**
     * Determines the winner(s) of the current round.
     * @return array of winning player indices
     */
    public int[] determineWinners() {
        if (!gameActive || players == null) {
            return new int[0];
        }
        
        return new int[]{Main.decideWinner(players)};
    }
    
    /**
     * Distributes the pot to the winner(s).
     * @param winners array of winning player indices
     */
    public void distributePot(int[] winners) {
        if (!gameActive || players == null || winners == null || winners.length == 0) {
            return;
        }
        
        int potShare = currentPot / winners.length;
        for (int winnerIndex : winners) {
            if (winnerIndex >= 0 && winnerIndex < players.length) {
                players[winnerIndex].addChips(potShare);
            }
        }
        
        currentPot = 0;
    }
    
    /**
     * Checks if the game should continue (more than one player with chips).
     * @return true if game can continue
     */
    public boolean canContinue() {
        if (!gameActive || players == null) {
            return false;
        }
        
        int playersWithChips = 0;
        for (Player player : players) {
            if (player.getChips() > 0) {
                playersWithChips++;
            }
        }
        
        return playersWithChips > 1;
    }
    
    /**
     * Gets the current game state summary.
     * @return string representation of current game state
     */
    public String getGameState() {
        if (!gameActive || players == null) {
            return "Game not active";
        }
        
        StringBuilder state = new StringBuilder();
        state.append(String.format("Round %d, Pot: %d%n", currentRound, currentPot));
        state.append("Players:%n");
        
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            state.append(String.format("  %d. %s - Chips: %d, Hand Value: %d, Folded: %s%n",
                    i + 1, player.getName(), player.getChips(), player.getHandValue(), player.isFold()));
        }
        
        return state.toString();
    }
    
    /**
     * Gets the active players in the game.
     * @return array of players
     */
    public Player[] getPlayers() {
        return players != null ? players.clone() : new Player[0];
    }
    
    /**
     * Gets the current pot value.
     * @return current pot amount
     */
    public int getCurrentPot() {
        return currentPot;
    }
    
    /**
     * Gets the current round number.
     * @return current round
     */
    public int getCurrentRound() {
        return currentRound;
    }
    
    /**
     * Gets the game configuration.
     * @return the game configuration
     */
    public Game getGameConfig() {
        return gameConfig;
    }
    
    /**
     * Checks if the game is currently active.
     * @return true if game is active
     */
    public boolean isGameActive() {
        return gameActive;
    }
    
    /**
     * Ends the current game.
     */
    public void endGame() {
        this.gameActive = false;
    }
    
    /**
     * Gets the current highest bet amount.
     * @return current high bet
     */
    public int getCurrentHighBet() {
        if (!gameActive || players == null) {
            return 0;
        }
        
        int highBet = 0;
        for (Player player : players) {
            if (player.getBet() > highBet) {
                highBet = player.getBet();
            }
        }
        return highBet;
    }
    
    /**
     * Sets the current highest bet amount.
     * @param amount the new high bet amount
     */
    public void setCurrentHighBet(int amount) {
        // This is tracked implicitly by player bets, but we can validate
        if (amount >= 0) {
            // The high bet is maintained by checking all player bets
        }
    }
    
    /**
     * Adds amount to the current pot.
     * @param amount the amount to add
     */
    public void addToPot(int amount) {
        if (amount > 0) {
            this.currentPot += amount;
        }
    }
    
    /**
     * Gets the game deck.
     * @return the current deck
     */
    public int[] getDeck() {
        return deck != null ? deck.clone() : new int[0];
    }
    
    /**
     * Advances to the next round.
     * @return true if successfully advanced
     */
    public boolean nextRound() {
        if (!gameActive) {
            return false;
        }
        
        currentRound++;
        // Reset bets for next round
        if (players != null) {
            for (Player player : players) {
                player.resetBet();
            }
        }
        return true;
    }
    
    /**
     * Checks if the current round is complete.
     * @return true if round is complete
     */
    public boolean isRoundComplete() {
        // A round is complete when all players have either folded or matched the highest bet
        if (!gameActive || players == null) {
            return true;
        }
        
        int highBet = getCurrentHighBet();
        int activePlayers = 0;
        int playersMatchingBet = 0;
        
        for (Player player : players) {
            if (!player.isFold() && player.getChips() > 0) {
                activePlayers++;
                if (player.getBet() >= highBet || player.getChips() == 0) {
                    playersMatchingBet++;
                }
            }
        }
        
        return activePlayers <= 1 || playersMatchingBet == activePlayers;
    }
}