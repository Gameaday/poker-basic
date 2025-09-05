package com.pokermon;

import com.pokermon.ai.Personality;

/**
 * Represents a monster that can be encountered, battled, or captured in the game.
 * Monsters have various properties that affect gameplay and provide different advantages.
 */
public class Monster {
    
    /**
     * Defines the rarity levels of monsters, affecting their power and capture difficulty.
     */
    public enum Rarity {
        COMMON("Common", 1.0),
        UNCOMMON("Uncommon", 1.5),
        RARE("Rare", 2.0),
        EPIC("Epic", 3.0),
        LEGENDARY("Legendary", 5.0);
        
        private final String displayName;
        private final double powerMultiplier;
        
        Rarity(String displayName, double powerMultiplier) {
            this.displayName = displayName;
            this.powerMultiplier = powerMultiplier;
        }
        
        public String getDisplayName() { return displayName; }
        public double getPowerMultiplier() { return powerMultiplier; }
    }
    
    /**
     * Defines the types of effects monsters can provide to gameplay.
     */
    public enum EffectType {
        CHIP_BONUS("Increases starting chips"),
        CARD_ADVANTAGE("Provides extra card draws"),
        BETTING_BOOST("Improves betting effectiveness"),
        LUCK_ENHANCEMENT("Increases chance of good hands"),
        VISUAL_THEME("Changes game appearance and theme");
        
        private final String description;
        
        EffectType(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    private final String name;
    private final Rarity rarity;
    private final int baseHealth;
    private final EffectType effectType;
    private final int effectPower;
    private final String description;
    private final Personality personality;
    
    /**
     * Creates a new monster with the specified properties.
     * @param name the monster's name
     * @param rarity the rarity level of the monster
     * @param baseHealth the base health points (used as chips in battles)
     * @param effectType the type of effect this monster provides
     * @param effectPower the strength of the monster's effect
     * @param description a description of the monster
     */
    public Monster(String name, Rarity rarity, int baseHealth, EffectType effectType, int effectPower, String description) {
        this(name, rarity, baseHealth, effectType, effectPower, description, Personality.getRandomPersonality());
    }
    
    /**
     * Creates a new monster with the specified properties and personality.
     * @param name the monster's name
     * @param rarity the rarity level of the monster
     * @param baseHealth the base health points (used as chips in battles)
     * @param effectType the type of effect this monster provides
     * @param effectPower the strength of the monster's effect
     * @param description a description of the monster
     * @param personality the monster's AI personality for poker behavior
     */
    public Monster(String name, Rarity rarity, int baseHealth, EffectType effectType, int effectPower, String description, Personality personality) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Monster name cannot be null or empty");
        }
        if (rarity == null) {
            throw new IllegalArgumentException("Monster rarity cannot be null");
        }
        if (baseHealth <= 0) {
            throw new IllegalArgumentException("Monster health must be greater than zero");
        }
        if (effectType == null) {
            throw new IllegalArgumentException("Monster effect type cannot be null");
        }
        if (effectPower < 0) {
            throw new IllegalArgumentException("Monster effect power cannot be negative");
        }
        if (personality == null) {
            throw new IllegalArgumentException("Monster personality cannot be null");
        }
        
        this.name = name.trim();
        this.rarity = rarity;
        this.baseHealth = baseHealth;
        this.effectType = effectType;
        this.effectPower = effectPower;
        this.description = description != null ? description.trim() : "";
        this.personality = personality;
    }
    
    // Getters
    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
    public int getBaseHealth() { return baseHealth; }
    public EffectType getEffectType() { return effectType; }
    public int getEffectPower() { return effectPower; }
    public String getDescription() { return description; }
    public Personality getPersonality() { return personality; }
    
    /**
     * Calculates the effective health of this monster based on its rarity.
     * @return the effective health (base health * rarity multiplier)
     */
    public int getEffectiveHealth() {
        return (int) (baseHealth * rarity.getPowerMultiplier());
    }
    
    /**
     * Calculates the effective power of this monster's effect based on its rarity.
     * @return the effective effect power
     */
    public int getEffectiveEffectPower() {
        return (int) (effectPower * rarity.getPowerMultiplier());
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s, %s) - Health: %d, Effect: %s (+%d)", 
                name, rarity.getDisplayName(), personality.getDisplayName(), getEffectiveHealth(), 
                effectType.name(), getEffectiveEffectPower());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Monster monster = (Monster) obj;
        return name.equals(monster.name) && rarity == monster.rarity;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + rarity.hashCode();
    }
}