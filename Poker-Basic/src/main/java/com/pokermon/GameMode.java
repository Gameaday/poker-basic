package com.pokermon;

import com.pokermon.api.GameMode;

/**
 * Backward compatibility wrapper for GameMode enum.
 * This class maintains compatibility with existing code while redirecting to the new api package.
 * 
 * @deprecated Use com.pokermon.api.GameMode instead
 */
@Deprecated
public enum GameMode {
    CLASSIC(com.pokermon.api.GameMode.CLASSIC),
    ADVENTURE(com.pokermon.api.GameMode.ADVENTURE),
    SAFARI(com.pokermon.api.GameMode.SAFARI),
    IRONMAN(com.pokermon.api.GameMode.IRONMAN);
    
    private final com.pokermon.api.GameMode delegate;
    
    GameMode(com.pokermon.api.GameMode delegate) {
        this.delegate = delegate;
    }
    
    public String getDisplayName() {
        return delegate.getDisplayName();
    }
    
    public String getDescription() {
        return delegate.getDescription();
    }
    
    public boolean hasMonsters() {
        return delegate.hasMonsters();
    }
    
    /**
     * Gets the actual API GameMode instance.
     * @return the API GameMode
     */
    public com.pokermon.api.GameMode getApiGameMode() {
        return delegate;
    }
}