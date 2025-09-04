package com.pokermon.ai;

/**
 * Defines the 24 distinct personalities that influence AI behavior across all game modes.
 * Each personality has unique weights for generalized traits that can be combined
 * to determine specific behaviors in different contexts.
 * 
 * Values range from 0.0 (almost never) to 10.0 (almost always).
 * Most values should be in the 2.0-8.0 range to keep gameplay interesting.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
public enum Personality {
    FOOLHARDY("Foolhardy", 9.0f, 3.0f, 8.0f, 7.0f, 2.0f, 7.0f, 1.0f, 3.0f, 8.5f, 4.0f),
    GULLIBLE("Gullible", 4.0f, 8.5f, 3.0f, 4.0f, 7.0f, 6.0f, 6.0f, 5.0f, 3.0f, 5.0f),
    BRASH("Brash", 8.5f, 4.0f, 7.5f, 8.0f, 3.0f, 5.0f, 2.0f, 4.0f, 8.0f, 6.0f),
    PENSIVE("Pensive", 4.0f, 3.0f, 4.0f, 3.5f, 8.5f, 8.0f, 8.0f, 7.5f, 4.0f, 8.5f),
    MEEK("Meek", 2.5f, 6.0f, 2.5f, 2.0f, 6.0f, 7.0f, 8.5f, 6.0f, 2.0f, 5.0f),
    ANXIOUS("Anxious", 3.0f, 5.0f, 3.0f, 2.5f, 7.0f, 6.5f, 8.0f, 5.5f, 3.5f, 6.0f),
    HAPPY("Happy", 6.5f, 5.0f, 6.5f, 6.0f, 5.0f, 7.5f, 3.0f, 6.0f, 6.5f, 6.0f),
    DOUBTFUL("Doubtful", 3.5f, 4.0f, 3.5f, 3.0f, 7.5f, 6.0f, 7.0f, 6.5f, 3.0f, 7.0f),
    TRUSTING("Trusting", 5.5f, 7.5f, 5.5f, 5.0f, 4.0f, 8.0f, 4.0f, 7.0f, 5.0f, 6.0f),
    BLISSFUL("Blissful", 7.5f, 6.0f, 7.5f, 7.0f, 2.0f, 8.5f, 2.0f, 5.0f, 7.0f, 4.0f),
    UNAWARE("Unaware", 5.5f, 8.0f, 6.0f, 5.5f, 2.5f, 6.0f, 4.0f, 3.0f, 5.0f, 3.5f),
    INSINCERE("Insincere", 6.0f, 2.0f, 8.5f, 7.5f, 6.0f, 4.0f, 5.0f, 4.0f, 6.5f, 7.5f),
    SHY("Shy", 2.0f, 4.0f, 2.0f, 2.5f, 6.5f, 6.0f, 8.5f, 5.5f, 2.5f, 5.0f),
    BRAINY("Brainy", 4.5f, 2.5f, 5.0f, 4.5f, 9.0f, 7.0f, 6.0f, 8.0f, 4.0f, 9.5f),
    MUSCLE_HEADED("Muscle-headed", 8.5f, 6.0f, 4.0f, 8.0f, 2.5f, 3.0f, 1.5f, 3.0f, 8.0f, 3.0f),
    FIGHTER("Fighter", 8.0f, 3.0f, 6.5f, 7.5f, 5.0f, 5.0f, 3.0f, 5.5f, 7.5f, 6.0f),
    LIVELY("Lively", 7.5f, 5.5f, 7.0f, 7.0f, 4.0f, 7.0f, 2.5f, 7.5f, 7.0f, 6.5f),
    INDECISIVE("Indecisive", 4.0f, 5.0f, 4.5f, 4.0f, 6.0f, 5.0f, 6.5f, 4.0f, 4.0f, 6.0f),
    DEFENSIVE("Defensive", 3.0f, 3.5f, 3.0f, 3.5f, 7.0f, 5.0f, 7.5f, 6.0f, 3.0f, 6.5f),
    SELF_ASSURED("Self-assured", 7.5f, 3.0f, 7.0f, 8.0f, 6.0f, 6.0f, 2.5f, 6.5f, 7.0f, 7.0f),
    CONFIDENT("Confident", 8.0f, 3.5f, 6.5f, 8.5f, 6.5f, 6.5f, 2.0f, 7.0f, 7.5f, 7.5f),
    SMARMY("Smarmy", 6.0f, 2.5f, 8.0f, 7.0f, 7.0f, 4.5f, 4.0f, 5.0f, 6.0f, 7.0f),
    CONDESCENDING("Condescending", 5.5f, 2.0f, 7.5f, 8.0f, 7.5f, 4.0f, 3.5f, 4.5f, 5.5f, 8.0f),
    HUMBLE("Humble", 4.5f, 5.0f, 3.0f, 4.0f, 6.5f, 7.5f, 6.0f, 7.0f, 4.0f, 6.5f);

    private final String displayName;
    private final float courage;          // Willingness to take risks and face challenges
    private final float gullibility;     // How easily influenced or deceived
    private final float guile;           // Cunning, deceptiveness, tactical cleverness  
    private final float confidence;      // Self-assurance and belief in abilities
    private final float caution;         // Careful consideration and prudence
    private final float empathy;         // Ability to understand and read others
    private final float timidness;       // Tendency to avoid confrontation or risk
    private final float patience;        // Tolerance for waiting and deliberation
    private final float ambition;        // Drive to succeed and take initiative
    private final float intelligence;    // Analytical thinking and strategic planning

    Personality(String displayName, float courage, float gullibility, float guile,
                float confidence, float caution, float empathy, float timidness, 
                float patience, float ambition, float intelligence) {
        this.displayName = displayName;
        this.courage = courage;
        this.gullibility = gullibility;
        this.guile = guile;
        this.confidence = confidence;
        this.caution = caution;
        this.empathy = empathy;
        this.timidness = timidness;
        this.patience = patience;
        this.ambition = ambition;
        this.intelligence = intelligence;
    }

    // Getters for generalized traits
    public String getDisplayName() { return displayName; }
    public float getCourage() { return courage; }
    public float getGullibility() { return gullibility; }
    public float getGuile() { return guile; }
    public float getConfidence() { return confidence; }
    public float getCaution() { return caution; }
    public float getEmpathy() { return empathy; }
    public float getTimidness() { return timidness; }
    public float getPatience() { return patience; }
    public float getAmbition() { return ambition; }
    public float getIntelligence() { return intelligence; }

    // Poker-specific behavior calculations using multiple trait combinations
    /**
     * Calculate aggressiveness for poker based on multiple traits.
     * @return effective aggressiveness (0.0-10.0)
     */
    public float getAggressiveness() {
        return clamp(courage * 0.4f + ambition * 0.3f + confidence * 0.3f);
    }

    /**
     * Calculate bluff tendency for poker based on multiple traits.
     * @return effective bluff tendency (0.0-10.0)
     */
    public float getBluffTendency() {
        return clamp(guile * 0.5f + confidence * 0.3f + courage * 0.2f);
    }

    /**
     * Calculate fold tendency for poker based on multiple traits.
     * @return effective fold tendency (0.0-10.0)
     */
    public float getFoldTendency() {
        return clamp(timidness * 0.4f + caution * 0.3f + (10.0f - confidence) * 0.3f);
    }

    /**
     * Calculate deception ability for poker based on multiple traits.
     * @return effective deception (0.0-10.0)
     */
    public float getDeception() {
        return clamp(guile * 0.6f + intelligence * 0.2f + (10.0f - empathy) * 0.2f);
    }

    /**
     * Clamps a value to the 0.0-10.0 range.
     */
    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(10.0f, value));
    }

    /**
     * Get a random personality for monster generation.
     * @return a randomly selected personality
     */
    public static Personality getRandomPersonality() {
        Personality[] personalities = values();
        return personalities[(int) (Math.random() * personalities.length)];
    }

    /**
     * Get a personality by name (case-insensitive).
     * @param name the personality name to find
     * @return the matching personality, or null if not found
     */
    public static Personality getByName(String name) {
        if (name == null) return null;
        
        for (Personality personality : values()) {
            if (personality.displayName.equalsIgnoreCase(name) || 
                personality.name().equalsIgnoreCase(name)) {
                return personality;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}