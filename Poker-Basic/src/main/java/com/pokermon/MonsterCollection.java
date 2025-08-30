package com.pokermon;

import java.util.*;

/**
 * Manages a collection of monsters for a player.
 * This class handles monster inventory, active monster selection, and collection management.
 */
public class MonsterCollection {
    private final List<Monster> ownedMonsters;
    private Monster activeMonster;
    private final int maxActiveMonsters;
    
    /**
     * Creates a new empty monster collection.
     */
    public MonsterCollection() {
        this(1); // Default to one active monster
    }
    
    /**
     * Creates a new monster collection with specified active monster limit.
     * @param maxActiveMonsters maximum number of monsters that can be active at once
     */
    public MonsterCollection(int maxActiveMonsters) {
        if (maxActiveMonsters < 0) {
            throw new IllegalArgumentException("Max active monsters cannot be negative");
        }
        this.ownedMonsters = new ArrayList<>();
        this.activeMonster = null;
        this.maxActiveMonsters = maxActiveMonsters;
    }
    
    /**
     * Adds a monster to the collection.
     * @param monster the monster to add
     * @return true if the monster was added, false if it was already in the collection
     */
    public boolean addMonster(Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("Monster cannot be null");
        }
        
        if (ownedMonsters.contains(monster)) {
            return false; // Already have this monster
        }
        
        ownedMonsters.add(monster);
        
        // If no active monster and we can have one, make this the active monster
        if (activeMonster == null && maxActiveMonsters > 0) {
            activeMonster = monster;
        }
        
        return true;
    }
    
    /**
     * Removes a monster from the collection.
     * @param monster the monster to remove
     * @return true if the monster was removed, false if it wasn't in the collection
     */
    public boolean removeMonster(Monster monster) {
        if (monster == null) {
            return false;
        }
        
        boolean removed = ownedMonsters.remove(monster);
        
        // If we removed the active monster, clear it
        if (removed && monster.equals(activeMonster)) {
            activeMonster = null;
        }
        
        return removed;
    }
    
    /**
     * Sets the active monster for gameplay effects.
     * @param monster the monster to make active (must be in the collection)
     * @return true if the monster was set as active, false otherwise
     */
    public boolean setActiveMonster(Monster monster) {
        if (monster == null) {
            activeMonster = null;
            return true;
        }
        
        if (maxActiveMonsters == 0) {
            return false; // No active monsters allowed
        }
        
        if (!ownedMonsters.contains(monster)) {
            return false; // Don't own this monster
        }
        
        activeMonster = monster;
        return true;
    }
    
    /**
     * Gets the currently active monster.
     * @return the active monster, or null if none is active
     */
    public Monster getActiveMonster() {
        return activeMonster;
    }
    
    /**
     * Gets a list of all owned monsters.
     * @return an unmodifiable list of owned monsters
     */
    public List<Monster> getOwnedMonsters() {
        return Collections.unmodifiableList(ownedMonsters);
    }
    
    /**
     * Gets the number of monsters in the collection.
     * @return the number of owned monsters
     */
    public int getMonsterCount() {
        return ownedMonsters.size();
    }
    
    /**
     * Checks if the collection contains a specific monster.
     * @param monster the monster to check for
     * @return true if the monster is in the collection
     */
    public boolean hasMonster(Monster monster) {
        return monster != null && ownedMonsters.contains(monster);
    }
    
    /**
     * Gets monsters of a specific rarity.
     * @param rarity the rarity to filter by
     * @return a list of monsters with the specified rarity
     */
    public List<Monster> getMonstersByRarity(Monster.Rarity rarity) {
        if (rarity == null) {
            return Collections.emptyList();
        }
        
        return ownedMonsters.stream()
                .filter(monster -> monster.getRarity() == rarity)
                .toList();
    }
    
    /**
     * Gets monsters with a specific effect type.
     * @param effectType the effect type to filter by
     * @return a list of monsters with the specified effect type
     */
    public List<Monster> getMonstersByEffect(Monster.EffectType effectType) {
        if (effectType == null) {
            return Collections.emptyList();
        }
        
        return ownedMonsters.stream()
                .filter(monster -> monster.getEffectType() == effectType)
                .toList();
    }
    
    /**
     * Checks if there's an active monster with a specific effect type.
     * @param effectType the effect type to check for
     * @return true if the active monster has the specified effect type
     */
    public boolean hasActiveEffect(Monster.EffectType effectType) {
        return activeMonster != null && activeMonster.getEffectType() == effectType;
    }
    
    /**
     * Gets the power of the active monster's effect.
     * @return the effective power of the active monster's effect, or 0 if no active monster
     */
    public int getActiveEffectPower() {
        return activeMonster != null ? activeMonster.getEffectiveEffectPower() : 0;
    }
    
    /**
     * Clears all monsters from the collection.
     */
    public void clearCollection() {
        ownedMonsters.clear();
        activeMonster = null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monster Collection (").append(ownedMonsters.size()).append(" monsters):\n");
        
        if (activeMonster != null) {
            sb.append("Active: ").append(activeMonster.toString()).append("\n");
        }
        
        if (!ownedMonsters.isEmpty()) {
            sb.append("Owned Monsters:\n");
            for (Monster monster : ownedMonsters) {
                sb.append("  - ").append(monster.toString());
                if (monster.equals(activeMonster)) {
                    sb.append(" [ACTIVE]");
                }
                sb.append("\n");
            }
        } else {
            sb.append("No monsters owned\n");
        }
        
        return sb.toString();
    }
}