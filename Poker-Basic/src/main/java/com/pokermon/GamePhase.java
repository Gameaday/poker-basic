package com.pokermon;

import com.pokermon.api.GamePhase;

/**
 * Backward compatibility wrapper for GamePhase enum.
 * This class maintains compatibility with existing code while redirecting to the new api package.
 * 
 * @deprecated Use com.pokermon.api.GamePhase instead
 */
@Deprecated
public enum GamePhase {
    INITIALIZATION(com.pokermon.api.GamePhase.INITIALIZATION),
    PLAYER_SETUP(com.pokermon.api.GamePhase.PLAYER_SETUP),
    DECK_CREATION(com.pokermon.api.GamePhase.DECK_CREATION),
    ROUND_START(com.pokermon.api.GamePhase.ROUND_START),
    HAND_DEALING(com.pokermon.api.GamePhase.HAND_DEALING),
    HAND_EVALUATION(com.pokermon.api.GamePhase.HAND_EVALUATION),
    BETTING_ROUND(com.pokermon.api.GamePhase.BETTING_ROUND),
    PLAYER_ACTIONS(com.pokermon.api.GamePhase.PLAYER_ACTIONS),
    POT_MANAGEMENT(com.pokermon.api.GamePhase.POT_MANAGEMENT),
    CARD_EXCHANGE(com.pokermon.api.GamePhase.CARD_EXCHANGE),
    HAND_REEVALUATION(com.pokermon.api.GamePhase.HAND_REEVALUATION),
    FINAL_BETTING(com.pokermon.api.GamePhase.FINAL_BETTING),
    WINNER_DETERMINATION(com.pokermon.api.GamePhase.WINNER_DETERMINATION),
    POT_DISTRIBUTION(com.pokermon.api.GamePhase.POT_DISTRIBUTION),
    ROUND_END(com.pokermon.api.GamePhase.ROUND_END),
    GAME_END(com.pokermon.api.GamePhase.GAME_END);
    
    private final com.pokermon.api.GamePhase delegate;
    
    GamePhase(com.pokermon.api.GamePhase delegate) {
        this.delegate = delegate;
    }
    
    public String getDisplayName() {
        return delegate.getDisplayName();
    }
    
    public String getDescription() {
        return delegate.getDescription();
    }
    
    public boolean shouldShowCards() {
        return delegate.shouldShowCards();
    }
    
    public boolean allowsBetting() {
        return delegate.allowsBetting();
    }
    
    public boolean allowsCardExchange() {
        return delegate.allowsCardExchange();
    }
    
    public boolean allowsRoundProgression() {
        return delegate.allowsRoundProgression();
    }
    
    public GamePhase getNextPhase() {
        com.pokermon.api.GamePhase next = delegate.getNextPhase();
        if (next == null) return null;
        
        // Convert back to wrapper enum
        for (GamePhase phase : values()) {
            if (phase.delegate == next) {
                return phase;
            }
        }
        return null;
    }
    
    public boolean isActivePhase() {
        return delegate.isActivePhase();
    }
    
    public boolean isSetupPhase() {
        return delegate.isSetupPhase();
    }
    
    public boolean requiresPlayerInput() {
        return delegate.requiresPlayerInput();
    }
    
    /**
     * Gets the actual API GamePhase instance.
     * @return the API GamePhase
     */
    public com.pokermon.api.GamePhase getApiGamePhase() {
        return delegate;
    }
}