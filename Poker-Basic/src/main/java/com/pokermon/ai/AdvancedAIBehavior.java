package com.pokermon.ai;

import com.pokermon.Player;
import java.util.Random;

/**
 * Advanced AI behavior system that uses personality traits and game context
 * to make sophisticated poker decisions. This replaces the simple hand-value
 * based AI with a more nuanced system.
 * 
 * This class handles the core poker decision-making algorithms while keeping
 * personality modifiers separate for reusability across game modes.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
public class AdvancedAIBehavior {
    
    private final Random random;
    
    /**
     * Represents possible AI actions in poker.
     */
    public enum AIAction {
        FOLD,
        CALL,
        RAISE_SMALL,   // 25-50% of current bet
        RAISE_MEDIUM,  // 50-100% of current bet
        RAISE_LARGE,   // 100-200% of current bet
        ALL_IN
    }

    /**
     * Represents the context of the current game situation.
     */
    public static class GameContext {
        public final int currentBet;
        public final int potSize;
        public final int playersRemaining;
        public final int bettingRound;  // 1 = pre-flop, 2 = post-flop, etc.
        public final boolean lastToAct;
        public final int chipRatio;    // player chips / average chips
        
        public GameContext(int currentBet, int potSize, int playersRemaining, 
                         int bettingRound, boolean lastToAct, int chipRatio) {
            this.currentBet = currentBet;
            this.potSize = potSize;
            this.playersRemaining = playersRemaining;
            this.bettingRound = bettingRound;
            this.lastToAct = lastToAct;
            this.chipRatio = chipRatio;
        }
    }

    /**
     * Creates a new advanced AI behavior system.
     */
    public AdvancedAIBehavior() {
        this.random = new Random();
    }

    /**
     * Creates a new advanced AI behavior system with a specific random seed.
     * Useful for testing deterministic behavior.
     * 
     * @param seed the random seed to use
     */
    public AdvancedAIBehavior(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Calculates the AI's betting decision based on personality and game context.
     * This is the main entry point for AI decision-making.
     * 
     * @param player the AI player making the decision
     * @param personality the player's personality traits
     * @param context the current game context
     * @param handStrength the AI's assessment of hand strength (0.0-1.0)
     * @return the bet amount the AI wants to place
     */
    public int calculateAIBet(Player player, Personality personality, GameContext context, float handStrength) {
        if (player.getChips() <= 0) {
            return context.currentBet; // Must call if no chips (shouldn't happen)
        }

        // First, decide what action to take
        AIAction action = decideAction(personality, context, handStrength);
        
        // Then calculate the specific bet amount based on the action
        return calculateBetForAction(action, player, personality, context, handStrength);
    }

    /**
     * Decides what action the AI should take based on personality and context.
     */
    private AIAction decideAction(Personality personality, GameContext context, float handStrength) {
        // Calculate base probabilities for each action
        float foldProbability = calculateFoldProbability(personality, context, handStrength);
        float callProbability = calculateCallProbability(personality, context, handStrength);
        float raiseProbability = calculateRaiseProbability(personality, context, handStrength);
        
        // Normalize probabilities
        float total = foldProbability + callProbability + raiseProbability;
        if (total <= 0) {
            return AIAction.CALL; // Fallback
        }
        
        foldProbability /= total;
        callProbability /= total;
        raiseProbability /= total;
        
        // Select action based on probability
        float roll = random.nextFloat();
        
        if (roll < foldProbability) {
            return AIAction.FOLD;
        } else if (roll < foldProbability + callProbability) {
            return AIAction.CALL;
        } else {
            // Decide how much to raise based on personality
            return decideRaiseSize(personality, context, handStrength);
        }
    }

    /**
     * Calculates the probability of folding based on personality and context.
     */
    private float calculateFoldProbability(Personality personality, GameContext context, float handStrength) {
        float baseFold = personality.getFoldTendency() / 10.0f;
        
        // Modify based on hand strength
        float handModifier = (1.0f - handStrength) * 1.5f; // Weak hands more likely to fold
        
        // Modify based on bet size relative to pot
        float betPressure = context.currentBet > 0 ? 
            Math.min(2.0f, (float) context.currentBet / Math.max(1, context.potSize)) : 0.0f;
        
        // Modify based on caution
        float cautionModifier = personality.getCaution() / 20.0f; // 0.0-0.5 bonus
        
        return Math.max(0.0f, baseFold + handModifier + betPressure * cautionModifier);
    }

    /**
     * Calculates the probability of calling (not raising) based on personality and context.
     */
    private float calculateCallProbability(Personality personality, GameContext context, float handStrength) {
        float baseCall = 0.4f; // Base tendency to call
        
        // Gullible personalities more likely to call
        float gullibilityBonus = personality.getGullibility() / 20.0f; // 0.0-0.5 bonus
        
        // Moderate hands favor calling over raising
        float handModifier = 0.0f;
        if (handStrength >= 0.3f && handStrength <= 0.7f) {
            handModifier = 0.3f; // Sweet spot for calling
        }
        
        // Conservative personalities prefer calling to raising
        float conservativeBonus = personality.getCaution() / 25.0f; // 0.0-0.4 bonus
        
        return baseCall + gullibilityBonus + handModifier + conservativeBonus;
    }

    /**
     * Calculates the probability of raising based on personality and context.
     */
    private float calculateRaiseProbability(Personality personality, GameContext context, float handStrength) {
        float baseRaise = personality.getAggressiveness() / 10.0f;
        
        // Strong hands favor raising
        float handModifier = handStrength * 0.8f;
        
        // Bluffing tendency can cause raises with weak hands
        float bluffModifier = 0.0f;
        if (handStrength < 0.3f && random.nextFloat() < personality.getBluffTendency() / 10.0f) {
            bluffModifier = 0.4f; // Bluff raise
        }
        
        // Confidence affects willingness to raise
        float confidenceModifier = personality.getConfidence() / 20.0f; // 0.0-0.5 bonus
        
        return baseRaise + handModifier + bluffModifier + confidenceModifier;
    }

    /**
     * Decides how much to raise based on personality.
     */
    private AIAction decideRaiseSize(Personality personality, GameContext context, float handStrength) {
        float aggressiveness = personality.getAggressiveness();
        float confidence = personality.getConfidence();
        
        // Very aggressive or confident personalities prefer bigger raises
        if (aggressiveness >= 8.0f || confidence >= 8.0f) {
            if (handStrength >= 0.8f && random.nextFloat() < 0.3f) {
                return AIAction.ALL_IN;
            } else if (random.nextFloat() < 0.6f) {
                return AIAction.RAISE_LARGE;
            } else {
                return AIAction.RAISE_MEDIUM;
            }
        } else if (aggressiveness >= 6.0f || confidence >= 6.0f) {
            if (random.nextFloat() < 0.7f) {
                return AIAction.RAISE_MEDIUM;
            } else {
                return AIAction.RAISE_LARGE;
            }
        } else {
            // Conservative personalities prefer smaller raises
            return AIAction.RAISE_SMALL;
        }
    }

    /**
     * Calculates the actual bet amount for a given action.
     */
    private int calculateBetForAction(AIAction action, Player player, Personality personality, 
                                    GameContext context, float handStrength) {
        int chips = player.getChips();
        int currentBet = context.currentBet;
        
        switch (action) {
            case FOLD:
                // In our simple system, folding means calling with 0 additional bet
                // The actual folding logic is handled by the game engine
                return Math.min(currentBet, chips / 10); // Minimal bet if forced to act
                
            case CALL:
                return Math.min(currentBet, chips);
                
            case RAISE_SMALL:
                int smallRaise = currentBet + Math.max(25, currentBet / 4);
                return Math.min(smallRaise, chips);
                
            case RAISE_MEDIUM:
                int mediumRaise = currentBet + Math.max(50, currentBet / 2);
                return Math.min(mediumRaise, chips);
                
            case RAISE_LARGE:
                int largeRaise = currentBet + Math.max(100, currentBet);
                return Math.min(largeRaise, chips);
                
            case ALL_IN:
                return chips;
                
            default:
                return Math.min(currentBet, chips);
        }
    }

    /**
     * Assesses hand strength for AI decision making.
     * This is a simplified version - in a real implementation, this would
     * analyze the actual cards and poker probabilities.
     * 
     * @param handValue the numeric hand value from the existing system
     * @return hand strength assessment (0.0 = terrible, 1.0 = unbeatable)
     */
    public static float assessHandStrength(int handValue) {
        // Convert the existing hand value system to a 0.0-1.0 scale
        // This maintains compatibility with the existing poker logic
        
        if (handValue <= 0) return 0.1f;      // Very weak hand
        if (handValue <= 18) return 0.2f;     // Weak hand
        if (handValue <= 38) return 0.4f;     // Below average
        if (handValue <= 55) return 0.6f;     // Average hand
        if (handValue <= 70) return 0.7f;     // Good hand
        if (handValue <= 85) return 0.8f;     // Strong hand
        if (handValue <= 95) return 0.9f;     // Very strong hand
        return 1.0f;                          // Exceptional hand
    }

    /**
     * Creates a simple game context for basic scenarios.
     * This is used when detailed game state information isn't available.
     */
    public static GameContext createSimpleContext(int currentBet, int potSize) {
        return new GameContext(
            currentBet,
            potSize,
            2,     // Assume 2 players
            1,     // First betting round
            false, // Not last to act
            1      // Average chip ratio
        );
    }
}