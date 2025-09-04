package com.pokermon.ai;

/**
 * Defines the 24 distinct personalities that influence AI poker behavior.
 * Each personality has unique weights that modify how the AI makes decisions.
 * 
 * Values range from 0.0 (almost never) to 10.0 (almost always).
 * Most values should be in the 2.0-8.0 range to keep gameplay interesting.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
public enum Personality {
    FOOLHARDY("Foolhardy", 8.5f, 3.0f, 8.0f, 7.0f, 2.0f, 9.0f, 4.0f),
    GULLIBLE("Gullible", 2.0f, 8.5f, 3.0f, 4.0f, 7.0f, 2.5f, 6.0f),
    BRASH("Brash", 8.0f, 4.0f, 7.5f, 8.0f, 3.0f, 7.5f, 3.0f),
    PENSIVE("Pensive", 3.0f, 3.0f, 4.0f, 3.5f, 8.5f, 4.0f, 8.0f),
    MEEK("Meek", 2.0f, 6.0f, 2.5f, 2.0f, 6.0f, 2.0f, 7.5f),
    ANXIOUS("Anxious", 3.5f, 5.0f, 3.0f, 2.5f, 7.0f, 3.0f, 8.5f),
    HAPPY("Happy", 6.0f, 5.0f, 6.5f, 6.0f, 5.0f, 6.0f, 4.0f),
    DOUBTFUL("Doubtful", 3.0f, 4.0f, 3.5f, 3.0f, 7.5f, 3.5f, 8.0f),
    TRUSTING("Trusting", 5.0f, 7.5f, 5.5f, 5.0f, 4.0f, 5.0f, 3.0f),
    BLISSFUL("Blissful", 7.0f, 6.0f, 7.5f, 7.0f, 2.0f, 7.0f, 2.5f),
    UNAWARE("Unaware", 5.0f, 8.0f, 6.0f, 5.5f, 2.5f, 5.5f, 3.0f),
    INSINCERE("Insincere", 6.5f, 2.0f, 7.0f, 7.5f, 6.0f, 8.0f, 5.0f),
    SHY("Shy", 2.5f, 4.0f, 2.0f, 2.5f, 6.5f, 2.0f, 8.0f),
    BRAINY("Brainy", 4.0f, 2.5f, 5.0f, 4.5f, 9.0f, 5.5f, 7.0f),
    MUSCLE_HEADED("Muscle-headed", 8.0f, 6.0f, 8.5f, 8.0f, 2.5f, 7.0f, 2.0f),
    FIGHTER("Fighter", 7.5f, 3.0f, 7.0f, 7.5f, 5.0f, 6.5f, 4.0f),
    LIVELY("Lively", 7.0f, 5.5f, 7.5f, 7.0f, 4.0f, 6.5f, 3.5f),
    INDECISIVE("Indecisive", 4.0f, 5.0f, 4.5f, 4.0f, 6.0f, 4.0f, 7.5f),
    DEFENSIVE("Defensive", 3.0f, 3.5f, 3.0f, 3.5f, 7.0f, 3.0f, 8.0f),
    SELF_ASSURED("Self-assured", 7.0f, 3.0f, 7.5f, 8.0f, 6.0f, 7.0f, 3.0f),
    CONFIDENT("Confident", 7.5f, 3.5f, 7.0f, 8.5f, 6.5f, 7.0f, 3.5f),
    SMARMY("Smarmy", 6.0f, 2.5f, 6.5f, 7.0f, 7.0f, 8.5f, 4.5f),
    CONDESCENDING("Condescending", 5.5f, 2.0f, 6.0f, 8.0f, 7.5f, 7.5f, 4.0f),
    HUMBLE("Humble", 4.0f, 5.0f, 4.5f, 4.0f, 6.5f, 3.0f, 6.0f);

    private final String displayName;
    private final float aggressiveness;    // How likely to bet/raise aggressively
    private final float gullibility;      // How likely to call bluffs
    private final float bluffTendency;    // How often they bluff
    private final float confidence;       // How they assess their hand strength
    private final float caution;          // How conservative they are
    private final float deception;        // How good they are at misleading
    private final float foldTendency;     // How likely to fold under pressure

    Personality(String displayName, float aggressiveness, float gullibility, float bluffTendency,
                float confidence, float caution, float deception, float foldTendency) {
        this.displayName = displayName;
        this.aggressiveness = aggressiveness;
        this.gullibility = gullibility;
        this.bluffTendency = bluffTendency;
        this.confidence = confidence;
        this.caution = caution;
        this.deception = deception;
        this.foldTendency = foldTendency;
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public float getAggressiveness() { return aggressiveness; }
    public float getGullibility() { return gullibility; }
    public float getBluffTendency() { return bluffTendency; }
    public float getConfidence() { return confidence; }
    public float getCaution() { return caution; }
    public float getDeception() { return deception; }
    public float getFoldTendency() { return foldTendency; }

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