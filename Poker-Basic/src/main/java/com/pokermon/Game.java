package com.pokermon;

/**
 * Represents the overall game state and configuration for a poker game.
 * This class manages game settings that can be customized for different variations.
 */
public class Game {
    private static final int DEFAULT_HAND_SIZE = 5;
    private static final int DEFAULT_STARTING_CHIPS = 1000;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 1;
    
    private final int handSize;
    private final int maxPlayers;
    private final int startingChips;
    private final int maxBettingRounds;
    private final GameMode gameMode;
    
    /**
     * Creates a game with default settings (5-card poker, 4 max players, 1000 starting chips).
     */
    public Game() {
        this(DEFAULT_HAND_SIZE, MAX_PLAYERS, DEFAULT_STARTING_CHIPS, 2, GameMode.CLASSIC);
    }
    
    /**
     * Creates a customized poker game.
     * @param handSize number of cards in each player's hand
     * @param maxPlayers maximum number of players allowed
     * @param startingChips initial chips for each player
     * @param maxBettingRounds maximum number of betting rounds per game
     */
    public Game(int handSize, int maxPlayers, int startingChips, int maxBettingRounds) {
        this(handSize, maxPlayers, startingChips, maxBettingRounds, GameMode.CLASSIC);
    }
    
    /**
     * Creates a customized poker game with specific game mode.
     * @param handSize number of cards in each player's hand
     * @param maxPlayers maximum number of players allowed
     * @param startingChips initial chips for each player
     * @param maxBettingRounds maximum number of betting rounds per game
     * @param gameMode the game mode to use
     */
    public Game(int handSize, int maxPlayers, int startingChips, int maxBettingRounds, GameMode gameMode) {
        if (handSize < 1 || handSize > 10) {
            throw new IllegalArgumentException("Hand size must be between 1 and 10");
        }
        if (maxPlayers < MIN_PLAYERS || maxPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException("Player count must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
        if (startingChips < 1) {
            throw new IllegalArgumentException("Starting chips must be positive");
        }
        if (maxBettingRounds < 1) {
            throw new IllegalArgumentException("Must have at least one betting round");
        }
        if (gameMode == null) {
            throw new IllegalArgumentException("Game mode cannot be null");
        }
        
        this.handSize = handSize;
        this.maxPlayers = maxPlayers;
        this.startingChips = startingChips;
        this.maxBettingRounds = maxBettingRounds;
        this.gameMode = gameMode;
    }
    
    // Getters
    public int getHandSize() { return handSize; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getStartingChips() { return startingChips; }
    public int getMaxBettingRounds() { return maxBettingRounds; }
    public GameMode getGameMode() { return gameMode; }
    
    /**
     * Validates if a player count is valid for this game.
     * @param playerCount the number of players to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidPlayerCount(int playerCount) {
        return playerCount >= MIN_PLAYERS && playerCount <= maxPlayers;
    }
    
    /**
     * Creates a game configuration for 3-card poker.
     * @return a Game instance configured for 3-card poker
     */
    public static Game createThreeCardPoker() {
        return new Game(3, 4, 500, 1);
    }
    
    /**
     * Creates a game configuration for 7-card stud.
     * @return a Game instance configured for 7-card stud
     */
    public static Game createSevenCardStud() {
        return new Game(7, 4, 1500, 3);
    }
    
    /**
     * Creates a game configuration for heads-up (2-player) poker.
     * @return a Game instance configured for heads-up play
     */
    public static Game createHeadsUp() {
        return new Game(5, 2, 1000, 2);
    }
    
    /**
     * Creates a game configuration for Adventure mode.
     * @return a Game instance configured for monster battles
     */
    public static Game createAdventureMode() {
        return new Game(5, 4, 1000, 2, GameMode.ADVENTURE);
    }
    
    /**
     * Creates a game configuration for Safari mode.
     * @return a Game instance configured for monster capturing
     */
    public static Game createSafariMode() {
        return new Game(5, 4, 1000, 2, GameMode.SAFARI);
    }
    
    /**
     * Creates a game configuration for Ironman mode.
     * @return a Game instance configured for gacha-style monster rewards
     */
    public static Game createIronmanMode() {
        return new Game(5, 4, 1000, 2, GameMode.IRONMAN);
    }
    
    @Override
    public String toString() {
        return String.format("Game[mode=%s, handSize=%d, maxPlayers=%d, startingChips=%d, maxBettingRounds=%d]",
                gameMode.getDisplayName(), handSize, maxPlayers, startingChips, maxBettingRounds);
    }
}