package com.pokermon.ai;

/**
 * Represents the core traits that define a personality.
 * These traits can be reused across different game modes and provide
 * a modular way to create personality variations.
 * 
 * Each trait is scored from 0.0 to 10.0.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
public class PersonalityTraits {
    
    private final float bravery;       // How willing to take risks
    private final float tenacity;     // How persistent and stubborn
    private final float intelligence; // How strategic and analytical
    private final float confidence;   // How self-assured
    private final float tactfulness;  // How diplomatic and subtle
    private final float empathy;      // How well they read others
    private final float patience;     // How tolerant of slow play
    private final float adaptability; // How well they adjust strategies

    /**
     * Creates a new set of personality traits.
     * 
     * @param bravery willingness to take risks (0.0-10.0)
     * @param tenacity persistence and stubbornness (0.0-10.0)
     * @param intelligence strategic thinking ability (0.0-10.0)
     * @param confidence self-assurance level (0.0-10.0)
     * @param tactfulness diplomatic subtlety (0.0-10.0)
     * @param empathy ability to read others (0.0-10.0)
     * @param patience tolerance for slow play (0.0-10.0)
     * @param adaptability strategic flexibility (0.0-10.0)
     */
    public PersonalityTraits(float bravery, float tenacity, float intelligence, 
                           float confidence, float tactfulness, float empathy, 
                           float patience, float adaptability) {
        this.bravery = clamp(bravery);
        this.tenacity = clamp(tenacity);
        this.intelligence = clamp(intelligence);
        this.confidence = clamp(confidence);
        this.tactfulness = clamp(tactfulness);
        this.empathy = clamp(empathy);
        this.patience = clamp(patience);
        this.adaptability = clamp(adaptability);
    }

    /**
     * Creates personality traits from a Personality enum.
     * This maps the personality's generalized traits to the trait system.
     * 
     * @param personality the personality to convert
     * @return personality traits derived from the personality
     */
    public static PersonalityTraits fromPersonality(Personality personality) {
        // Map personality's generalized traits to trait system
        float bravery = personality.getCourage();
        float tenacity = (personality.getConfidence() + (10.0f - personality.getTimidness())) / 2.0f;
        float intelligence = personality.getIntelligence();
        float confidence = personality.getConfidence();
        float tactfulness = (personality.getGuile() + personality.getCaution()) / 2.0f;
        float empathy = personality.getEmpathy();
        float patience = personality.getPatience();
        float adaptability = (personality.getGuile() + personality.getIntelligence()) / 2.0f;
        
        return new PersonalityTraits(bravery, tenacity, intelligence, confidence, 
                                   tactfulness, empathy, patience, adaptability);
    }

    /**
     * Clamps a trait value to the valid range of 0.0-10.0.
     */
    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(10.0f, value));
    }

    // Getters
    public float getBravery() { return bravery; }
    public float getTenacity() { return tenacity; }
    public float getIntelligence() { return intelligence; }
    public float getConfidence() { return confidence; }
    public float getTactfulness() { return tactfulness; }
    public float getEmpathy() { return empathy; }
    public float getPatience() { return patience; }
    public float getAdaptability() { return adaptability; }

    /**
     * Calculate an overall "strength" score based on all traits.
     * This can be used for opponent difficulty assessment.
     * 
     * @return overall strength score (0.0-10.0)
     */
    public float getOverallStrength() {
        return (bravery + tenacity + intelligence + confidence + 
                tactfulness + empathy + patience + adaptability) / 8.0f;
    }

    /**
     * Creates a modified version of these traits with multipliers applied.
     * Useful for monster rarity bonuses or special circumstances.
     * 
     * @param multiplier the factor to apply to all traits
     * @return new PersonalityTraits with modified values
     */
    public PersonalityTraits applyMultiplier(float multiplier) {
        return new PersonalityTraits(
            bravery * multiplier,
            tenacity * multiplier,
            intelligence * multiplier,
            confidence * multiplier,
            tactfulness * multiplier,
            empathy * multiplier,
            patience * multiplier,
            adaptability * multiplier
        );
    }

    @Override
    public String toString() {
        return String.format("PersonalityTraits{bravery=%.1f, tenacity=%.1f, intelligence=%.1f, " +
                           "confidence=%.1f, tactfulness=%.1f, empathy=%.1f, patience=%.1f, adaptability=%.1f}",
                           bravery, tenacity, intelligence, confidence, tactfulness, empathy, patience, adaptability);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PersonalityTraits that = (PersonalityTraits) obj;
        return Float.compare(that.bravery, bravery) == 0 &&
               Float.compare(that.tenacity, tenacity) == 0 &&
               Float.compare(that.intelligence, intelligence) == 0 &&
               Float.compare(that.confidence, confidence) == 0 &&
               Float.compare(that.tactfulness, tactfulness) == 0 &&
               Float.compare(that.empathy, empathy) == 0 &&
               Float.compare(that.patience, patience) == 0 &&
               Float.compare(that.adaptability, adaptability) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(bravery);
        result = 31 * result + Float.hashCode(tenacity);
        result = 31 * result + Float.hashCode(intelligence);
        result = 31 * result + Float.hashCode(confidence);
        result = 31 * result + Float.hashCode(tactfulness);
        result = 31 * result + Float.hashCode(empathy);
        result = 31 * result + Float.hashCode(patience);
        result = 31 * result + Float.hashCode(adaptability);
        return result;
    }
}