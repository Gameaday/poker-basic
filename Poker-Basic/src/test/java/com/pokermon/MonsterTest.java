package com.pokermon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Monster class foundation.
 */
public class MonsterTest {
    
    @Test
    public void testMonsterCreation() {
        Monster monster = new Monster("Fire Dragon", Monster.Rarity.RARE, 100, 
                Monster.EffectType.CHIP_BONUS, 50, "A fierce dragon that boosts chip earnings");
        
        assertEquals("Fire Dragon", monster.getName());
        assertEquals(Monster.Rarity.RARE, monster.getRarity());
        assertEquals(100, monster.getBaseHealth());
        assertEquals(Monster.EffectType.CHIP_BONUS, monster.getEffectType());
        assertEquals(50, monster.getEffectPower());
        assertEquals("A fierce dragon that boosts chip earnings", monster.getDescription());
    }
    
    @Test
    public void testEffectiveHealth() {
        Monster common = new Monster("Goblin", Monster.Rarity.COMMON, 100, 
                Monster.EffectType.CHIP_BONUS, 10, "A small goblin");
        Monster legendary = new Monster("Phoenix", Monster.Rarity.LEGENDARY, 100, 
                Monster.EffectType.CHIP_BONUS, 10, "A legendary phoenix");
        
        assertEquals(100, common.getEffectiveHealth()); // 100 * 1.0
        assertEquals(500, legendary.getEffectiveHealth()); // 100 * 5.0
    }
    
    @Test
    public void testEffectiveEffectPower() {
        Monster uncommon = new Monster("Wolf", Monster.Rarity.UNCOMMON, 50, 
                Monster.EffectType.BETTING_BOOST, 20, "A cunning wolf");
        
        assertEquals(30, uncommon.getEffectivePower()); // 20 * 1.5
    }
    
    @Test
    public void testMonsterEquality() {
        Monster monster1 = new Monster("Dragon", Monster.Rarity.RARE, 100, 
                Monster.EffectType.CHIP_BONUS, 50, "A dragon");
        Monster monster2 = new Monster("Dragon", Monster.Rarity.RARE, 200, 
                Monster.EffectType.BETTING_BOOST, 30, "Another dragon");
        Monster monster3 = new Monster("Phoenix", Monster.Rarity.RARE, 100, 
                Monster.EffectType.CHIP_BONUS, 50, "A phoenix");
        
        assertEquals(monster1, monster2); // Same name and rarity
        assertNotEquals(monster1, monster3); // Different name
    }
    
    @Test
    public void testMonsterValidation() {
        // Test null name
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster(null, Monster.Rarity.COMMON, 100, Monster.EffectType.CHIP_BONUS, 50, ""));
        
        // Test empty name
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster("", Monster.Rarity.COMMON, 100, Monster.EffectType.CHIP_BONUS, 50, ""));
        
        // Test null rarity
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster("Test", null, 100, Monster.EffectType.CHIP_BONUS, 50, ""));
        
        // Test negative health
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster("Test", Monster.Rarity.COMMON, -1, Monster.EffectType.CHIP_BONUS, 50, ""));
        
        // Test null effect type
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster("Test", Monster.Rarity.COMMON, 100, null, 50, ""));
        
        // Test negative effect power
        assertThrows(IllegalArgumentException.class, () -> 
                new Monster("Test", Monster.Rarity.COMMON, 100, Monster.EffectType.CHIP_BONUS, -1, ""));
    }
    
    @Test
    public void testToString() {
        Monster monster = new Monster("Fire Dragon", Monster.Rarity.RARE, 100, 
                Monster.EffectType.CHIP_BONUS, 50, "A fierce dragon");
        
        String str = monster.toString();
        assertTrue(str.contains("Fire Dragon"));
        assertTrue(str.contains("Rare"));
        assertTrue(str.contains("200")); // Effective health
        assertTrue(str.contains("100")); // Effective effect power
    }
    
    @Test
    public void testRarityProperties() {
        assertEquals("Common", Monster.Rarity.COMMON.getDisplayName());
        assertEquals(1.0, Monster.Rarity.COMMON.getPowerMultiplier());
        
        assertEquals("Legendary", Monster.Rarity.LEGENDARY.getDisplayName());
        assertEquals(5.0, Monster.Rarity.LEGENDARY.getPowerMultiplier());
    }
    
    @Test
    public void testEffectTypeProperties() {
        assertEquals("Increases starting chips", Monster.EffectType.CHIP_BONUS.getDescription());
        assertEquals("Changes game appearance and theme", Monster.EffectType.VISUAL_THEME.getDescription());
    }
}