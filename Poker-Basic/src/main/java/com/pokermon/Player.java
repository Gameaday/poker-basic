package com.pokermon;

import com.pokermon.core.Player;

/**
 * Backward compatibility wrapper for Player class.
 * This class maintains compatibility with existing code while redirecting to the new core package.
 * 
 * @deprecated Use com.pokermon.core.Player instead
 */
@Deprecated
public class Player extends com.pokermon.core.Player {
    
    /**
     * Default constructor.
     */
    public Player() {
        super();
    }
    
    /**
     * Constructor with name.
     * @param name the player's name
     */
    public Player(String name) {
        super(name);
    }
    
    /**
     * Constructor with name and chips.
     * @param name the player's name
     * @param chips the initial number of chips
     */
    public Player(String name, int chips) {
        super(name, chips);
    }
}