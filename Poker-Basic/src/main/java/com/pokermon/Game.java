package com.pokermon;

import com.pokermon.api.Game;

/**
 * Backward compatibility wrapper for Game class.
 * This class maintains compatibility with existing code while redirecting to the new api package.
 * 
 * @deprecated Use com.pokermon.api.Game instead
 */
@Deprecated
public class Game extends com.pokermon.api.Game {
    
    /**
     * Creates a game with default settings.
     */
    public Game() {
        super();
    }
    
    /**
     * Creates a customized poker game.
     * @param handSize number of cards in each player's hand
     * @param maxPlayers maximum number of players allowed
     * @param startingChips initial chips for each player
     * @param maxBettingRounds maximum number of betting rounds per game
     */
    public Game(int handSize, int maxPlayers, int startingChips, int maxBettingRounds) {
        super(handSize, maxPlayers, startingChips, maxBettingRounds);
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
        super(handSize, maxPlayers, startingChips, maxBettingRounds, gameMode.getApiGameMode());
    }
    
    /**
     * Override getGameMode to return wrapper type.
     */
    @Override
    public com.pokermon.api.GameMode getGameMode() {
        return super.getGameMode();
    }
    
    /**
     * Gets the game mode as the wrapper type.
     * @return the wrapper GameMode
     */
    public GameMode getWrapperGameMode() {
        com.pokermon.api.GameMode apiMode = super.getGameMode();
        for (GameMode mode : GameMode.values()) {
            if (mode.getApiGameMode() == apiMode) {
                return mode;
            }
        }
        return GameMode.CLASSIC; // fallback
    }
}