package com.pokermon.api;

/**
 * Defines the specific phases of a poker game for state-driven UI control.
 * This enum tracks the detailed game flow to enable precise UI behavior.
 * 
 * Game Flow:
 * INITIALIZATION → PLAYER_SETUP → DECK_CREATION → ROUND_START → 
 * HAND_DEALING → HAND_EVALUATION → BETTING_ROUND → PLAYER_ACTIONS → 
 * POT_MANAGEMENT → CARD_EXCHANGE → HAND_REEVALUATION → FINAL_BETTING → 
 * WINNER_DETERMINATION → POT_DISTRIBUTION → ROUND_END
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public enum GamePhase {
    /**
     * Game is being initialized, no players yet set up.
     */
    INITIALIZATION("Initializing game...", "Setting up game configuration", false, false, false, false),
    
    /**
     * Players are being set up with names and starting chips.
     */
    PLAYER_SETUP("Setting up players...", "Configuring player information", false, false, false, false),
    
    /**
     * Deck is being created and shuffled.
     */
    DECK_CREATION("Preparing deck...", "Shuffling cards", false, false, false, false),
    
    /**
     * New round is starting.
     */
    ROUND_START("Starting new round", "Beginning round %d", false, false, false, false),
    
    /**
     * Cards are being dealt to all players.
     */
    HAND_DEALING("Dealing cards...", "Distributing cards to players", false, false, false, false),
    
    /**
     * Initial hand evaluation is taking place.
     */
    HAND_EVALUATION("Evaluating hands...", "Analyzing card values", true, false, false, false),
    
    /**
     * Betting round is active, players can make betting decisions.
     */
    BETTING_ROUND("Betting round", "Place your bet", true, true, false, false),
    
    /**
     * Waiting for player actions during betting.
     */
    PLAYER_ACTIONS("Your turn", "Choose your action: call, raise, or fold", true, true, false, false),
    
    /**
     * Managing pot after betting actions.
     */
    POT_MANAGEMENT("Processing bets...", "Updating pot and player chips", true, false, false, false),
    
    /**
     * Card exchange phase - players can select cards to discard.
     */
    CARD_EXCHANGE("Card exchange", "Select cards to exchange", true, false, true, false),
    
    /**
     * Re-evaluating hands after card exchange.
     */
    HAND_REEVALUATION("Re-evaluating hands...", "Analyzing new card values", true, false, false, false),
    
    /**
     * Final betting round after card exchange.
     */
    FINAL_BETTING("Final betting", "Final chance to bet", true, true, false, false),
    
    /**
     * Determining the winner of the round.
     */
    WINNER_DETERMINATION("Determining winner...", "Comparing final hands", true, false, false, false),
    
    /**
     * Distributing the pot to the winner(s).
     */
    POT_DISTRIBUTION("Distributing pot...", "Awarding chips to winner", true, false, false, false),
    
    /**
     * Round has ended, preparing for next round or game end.
     */
    ROUND_END("Round complete", "Round finished. Continue or end game?", true, false, false, true),
    
    /**
     * Game has ended completely.
     */
    GAME_END("Game over", "Thank you for playing!", true, false, false, false);
    
    private final String displayName;
    private final String description;
    private final boolean showCards;
    private final boolean allowBetting;
    private final boolean allowCardExchange;
    private final boolean allowRoundProgression;
    
    GamePhase(String displayName, String description, boolean showCards, 
              boolean allowBetting, boolean allowCardExchange, boolean allowRoundProgression) {
        this.displayName = displayName;
        this.description = description;
        this.showCards = showCards;
        this.allowBetting = allowBetting;
        this.allowCardExchange = allowCardExchange;
        this.allowRoundProgression = allowRoundProgression;
    }
    
    /**
     * Gets the human-readable display name for this phase.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of what's happening in this phase.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if cards should be visible to the player in this phase.
     * @return true if cards should be shown
     */
    public boolean shouldShowCards() {
        return showCards;
    }
    
    /**
     * Checks if betting actions should be available in this phase.
     * @return true if betting is allowed
     */
    public boolean allowsBetting() {
        return allowBetting;
    }
    
    /**
     * Checks if card exchange should be available in this phase.
     * @return true if card exchange is allowed
     */
    public boolean allowsCardExchange() {
        return allowCardExchange;
    }
    
    /**
     * Checks if round progression controls should be available in this phase.
     * @return true if round progression is allowed
     */
    public boolean allowsRoundProgression() {
        return allowRoundProgression;
    }
    
    /**
     * Gets the next logical phase in the game flow.
     * @return the next phase, or null if this is the final phase
     */
    public GamePhase getNextPhase() {
        switch (this) {
            case INITIALIZATION: return PLAYER_SETUP;
            case PLAYER_SETUP: return DECK_CREATION;
            case DECK_CREATION: return ROUND_START;
            case ROUND_START: return HAND_DEALING;
            case HAND_DEALING: return HAND_EVALUATION;
            case HAND_EVALUATION: return BETTING_ROUND;
            case BETTING_ROUND: return PLAYER_ACTIONS;
            case PLAYER_ACTIONS: return POT_MANAGEMENT;
            case POT_MANAGEMENT: return CARD_EXCHANGE;
            case CARD_EXCHANGE: return HAND_REEVALUATION;
            case HAND_REEVALUATION: return FINAL_BETTING;
            case FINAL_BETTING: return WINNER_DETERMINATION;
            case WINNER_DETERMINATION: return POT_DISTRIBUTION;
            case POT_DISTRIBUTION: return ROUND_END;
            case ROUND_END: return ROUND_START; // or GAME_END
            case GAME_END: return null;
            default: return null;
        }
    }
    
    /**
     * Checks if this phase represents an active game state where players can interact.
     * @return true if the game is in an active state
     */
    public boolean isActivePhase() {
        return this != INITIALIZATION && this != PLAYER_SETUP && 
               this != DECK_CREATION && this != GAME_END;
    }
    
    /**
     * Checks if this phase is part of the setup process.
     * @return true if this is a setup phase
     */
    public boolean isSetupPhase() {
        return this == INITIALIZATION || this == PLAYER_SETUP || this == DECK_CREATION;
    }
    
    /**
     * Checks if this phase involves player decision making.
     * @return true if player input is expected
     */
    public boolean requiresPlayerInput() {
        return this == BETTING_ROUND || this == PLAYER_ACTIONS || 
               this == CARD_EXCHANGE || this == ROUND_END;
    }
}