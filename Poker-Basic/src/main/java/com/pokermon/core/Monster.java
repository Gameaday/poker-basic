package com.pokermon.core;

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
     * Defines the types of monsters, providing advantages against certain opponent types.
     */
    public enum Type {
        FIRE("Fire", "Strong against Nature, weak against Water"),
        WATER("Water", "Strong against Fire, weak against Electric"),
        NATURE("Nature", "Strong against Water, weak against Fire"),
        ELECTRIC("Electric", "Strong against Water, weak against Nature"),
        NORMAL("Normal", "Balanced type with no special advantages or weaknesses");
        
        private final String displayName;
        private final String description;
        
        Type(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        /**
         * Calculates the effectiveness multiplier against another type.
         * @param opponent the opponent's type
         * @return damage multiplier (2.0 = super effective, 0.5 = not very effective, 1.0 = normal)
         */
        public double getEffectivenessAgainst(Type opponent) {
            switch (this) {
                case FIRE:
                    return opponent == NATURE ? 2.0 : (opponent == WATER ? 0.5 : 1.0);
                case WATER:
                    return opponent == FIRE ? 2.0 : (opponent == ELECTRIC ? 0.5 : 1.0);
                case NATURE:
                    return opponent == WATER ? 2.0 : (opponent == FIRE ? 0.5 : 1.0);
                case ELECTRIC:
                    return opponent == WATER ? 2.0 : (opponent == NATURE ? 0.5 : 1.0);
                case NORMAL:
                default:
                    return 1.0;
            }
        }
    }
    
    private final String name;
    private final Type type;
    private final Rarity rarity;
    private final int baseHP;
    private final int baseAttack;
    private final int baseDefense;
    private final int baseSpeed;
    
    private int currentHP;
    private int level;
    private int experience;
    
    /**
     * Creates a new monster with the specified properties.
     * @param name the monster's name
     * @param type the monster's type
     * @param rarity the monster's rarity
     * @param baseHP base health points
     * @param baseAttack base attack power
     * @param baseDefense base defense power
     * @param baseSpeed base speed
     */
    public Monster(String name, Type type, Rarity rarity, int baseHP, int baseAttack, int baseDefense, int baseSpeed) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Monster name cannot be null or empty");
        }
        if (type == null || rarity == null) {
            throw new IllegalArgumentException("Monster type and rarity cannot be null");
        }
        if (baseHP <= 0 || baseAttack <= 0 || baseDefense <= 0 || baseSpeed <= 0) {
            throw new IllegalArgumentException("Monster stats must be positive");
        }
        
        this.name = name.trim();
        this.type = type;
        this.rarity = rarity;
        this.baseHP = baseHP;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseSpeed = baseSpeed;
        
        this.currentHP = calculateHP();
        this.level = 1;
        this.experience = 0;
    }
    
    // Getters
    public String getName() { return name; }
    public Type getType() { return type; }
    public Rarity getRarity() { return rarity; }
    public int getBaseHP() { return baseHP; }
    public int getBaseAttack() { return baseAttack; }
    public int getBaseDefense() { return baseDefense; }
    public int getBaseSpeed() { return baseSpeed; }
    public int getCurrentHP() { return currentHP; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    
    /**
     * Calculates the monster's actual HP based on base HP, level, and rarity.
     * @return the calculated HP
     */
    public int calculateHP() {
        return (int) (baseHP * (1 + level * 0.1) * rarity.getPowerMultiplier());
    }
    
    /**
     * Calculates the monster's actual attack based on base attack, level, and rarity.
     * @return the calculated attack
     */
    public int calculateAttack() {
        return (int) (baseAttack * (1 + level * 0.1) * rarity.getPowerMultiplier());
    }
    
    /**
     * Calculates the monster's actual defense based on base defense, level, and rarity.
     * @return the calculated defense
     */
    public int calculateDefense() {
        return (int) (baseDefense * (1 + level * 0.1) * rarity.getPowerMultiplier());
    }
    
    /**
     * Calculates the monster's actual speed based on base speed, level, and rarity.
     * @return the calculated speed
     */
    public int calculateSpeed() {
        return (int) (baseSpeed * (1 + level * 0.1) * rarity.getPowerMultiplier());
    }
    
    /**
     * Heals the monster to full HP.
     */
    public void healToFull() {
        this.currentHP = calculateHP();
    }
    
    /**
     * Heals the monster by a specific amount.
     * @param amount the amount to heal
     */
    public void heal(int amount) {
        this.currentHP = Math.min(calculateHP(), currentHP + Math.max(0, amount));
    }
    
    /**
     * Damages the monster by a specific amount.
     * @param damage the damage amount
     * @return true if the monster is still alive
     */
    public boolean takeDamage(int damage) {
        this.currentHP = Math.max(0, currentHP - Math.max(0, damage));
        return isAlive();
    }
    
    /**
     * Checks if the monster is alive.
     * @return true if current HP > 0
     */
    public boolean isAlive() {
        return currentHP > 0;
    }
    
    /**
     * Adds experience to the monster and levels up if enough experience is gained.
     * @param exp the experience to add
     */
    public void addExperience(int exp) {
        this.experience += Math.max(0, exp);
        
        // Simple leveling: 100 exp per level
        int newLevel = 1 + (this.experience / 100);
        if (newLevel > level) {
            levelUp(newLevel);
        }
    }
    
    /**
     * Levels up the monster to the specified level.
     * @param newLevel the new level
     */
    private void levelUp(int newLevel) {
        int oldHP = calculateHP();
        this.level = newLevel;
        int newHP = calculateHP();
        
        // Increase current HP proportionally
        this.currentHP = (int) (currentHP * ((double) newHP / oldHP));
    }
    
    /**
     * Gets the experience needed for the next level.
     * @return experience needed for next level
     */
    public int getExperienceToNextLevel() {
        return (level * 100) - experience;
    }
    
    /**
     * Creates a copy of this monster for battle simulation.
     * @return a new monster instance with the same properties
     */
    public Monster copy() {
        Monster copy = new Monster(name, type, rarity, baseHP, baseAttack, baseDefense, baseSpeed);
        copy.level = this.level;
        copy.experience = this.experience;
        copy.currentHP = this.currentHP;
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Lv.%d %s %s) HP:%d/%d ATK:%d DEF:%d SPD:%d",
                name, level, rarity.getDisplayName(), type.getDisplayName(),
                currentHP, calculateHP(), calculateAttack(), calculateDefense(), calculateSpeed());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Monster monster = (Monster) obj;
        return Objects.equals(name, monster.name) &&
               type == monster.type &&
               rarity == monster.rarity &&
               level == monster.level;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type, rarity, level);
    }
    
    /**
     * Factory method to create common monsters.
     */
    public static class Factory {
        
        /**
         * Creates a basic fire-type monster.
         * @param name the monster's name
         * @return a new fire monster
         */
        public static Monster createFireMonster(String name) {
            return new Monster(name, Type.FIRE, Rarity.COMMON, 100, 80, 60, 70);
        }
        
        /**
         * Creates a basic water-type monster.
         * @param name the monster's name
         * @return a new water monster
         */
        public static Monster createWaterMonster(String name) {
            return new Monster(name, Type.WATER, Rarity.COMMON, 110, 70, 80, 60);
        }
        
        /**
         * Creates a basic nature-type monster.
         * @param name the monster's name
         * @return a new nature monster
         */
        public static Monster createNatureMonster(String name) {
            return new Monster(name, Type.NATURE, Rarity.COMMON, 120, 75, 85, 50);
        }
        
        /**
         * Creates a basic electric-type monster.
         * @param name the monster's name
         * @return a new electric monster
         */
        public static Monster createElectricMonster(String name) {
            return new Monster(name, Type.ELECTRIC, Rarity.COMMON, 90, 85, 55, 90);
        }
        
        /**
         * Creates a random monster with the specified rarity.
         * @param rarity the desired rarity
         * @return a new random monster
         */
        public static Monster createRandomMonster(Rarity rarity) {
            String[] names = {"Flare", "Aqua", "Leafy", "Spark", "Mighty", "Swift", "Rocky", "Mystic"};
            Type[] types = Type.values();
            
            java.util.Random random = new java.util.Random();
            String name = names[random.nextInt(names.length)];
            Type type = types[random.nextInt(types.length - 1)]; // Exclude NORMAL for random generation
            
            int baseHP = 80 + random.nextInt(40);
            int baseAttack = 60 + random.nextInt(40);
            int baseDefense = 60 + random.nextInt(40);
            int baseSpeed = 60 + random.nextInt(40);
            
            return new Monster(name, type, rarity, baseHP, baseAttack, baseDefense, baseSpeed);
        }
    }
}

// Import Objects class properly
class Objects {
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
    
    public static int hash(Object... values) {
        return java.util.Arrays.hashCode(values);
    }
}