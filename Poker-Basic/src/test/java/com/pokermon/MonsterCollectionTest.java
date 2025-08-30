package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the MonsterCollection class.
 */
public class MonsterCollectionTest {
    
    private MonsterCollection collection;
    private Monster dragon;
    private Monster goblin;
    private Monster phoenix;
    
    @BeforeEach
    public void setUp() {
        collection = new MonsterCollection();
        dragon = new Monster("Dragon", Monster.Rarity.RARE, 100, Monster.EffectType.CHIP_BONUS, 50, "A dragon");
        goblin = new Monster("Goblin", Monster.Rarity.COMMON, 50, Monster.EffectType.BETTING_BOOST, 10, "A goblin");
        phoenix = new Monster("Phoenix", Monster.Rarity.LEGENDARY, 200, Monster.EffectType.LUCK_ENHANCEMENT, 100, "A phoenix");
    }
    
    @Test
    public void testEmptyCollection() {
        assertEquals(0, collection.getMonsterCount());
        assertNull(collection.getActiveMonster());
        assertTrue(collection.getOwnedMonsters().isEmpty());
    }
    
    @Test
    public void testAddMonster() {
        assertTrue(collection.addMonster(dragon));
        assertEquals(1, collection.getMonsterCount());
        assertTrue(collection.hasMonster(dragon));
        assertEquals(dragon, collection.getActiveMonster()); // First monster becomes active
    }
    
    @Test
    public void testAddDuplicateMonster() {
        assertTrue(collection.addMonster(dragon));
        assertFalse(collection.addMonster(dragon)); // Can't add same monster twice
        assertEquals(1, collection.getMonsterCount());
    }
    
    @Test
    public void testAddNullMonster() {
        assertThrows(IllegalArgumentException.class, () -> collection.addMonster(null));
    }
    
    @Test
    public void testRemoveMonster() {
        collection.addMonster(dragon);
        collection.addMonster(goblin);
        
        assertTrue(collection.removeMonster(dragon));
        assertEquals(1, collection.getMonsterCount());
        assertFalse(collection.hasMonster(dragon));
        assertNull(collection.getActiveMonster()); // Active monster was removed
    }
    
    @Test
    public void testRemoveNonExistentMonster() {
        collection.addMonster(dragon);
        assertFalse(collection.removeMonster(goblin));
        assertEquals(1, collection.getMonsterCount());
    }
    
    @Test
    public void testSetActiveMonster() {
        collection.addMonster(dragon);
        collection.addMonster(goblin);
        
        assertTrue(collection.setActiveMonster(goblin));
        assertEquals(goblin, collection.getActiveMonster());
        
        // Set to null
        assertTrue(collection.setActiveMonster(null));
        assertNull(collection.getActiveMonster());
    }
    
    @Test
    public void testSetActiveMonsterNotOwned() {
        collection.addMonster(dragon);
        assertFalse(collection.setActiveMonster(goblin)); // Don't own goblin
        assertEquals(dragon, collection.getActiveMonster()); // Should remain dragon
    }
    
    @Test
    public void testCollectionWithNoActiveMonsters() {
        MonsterCollection noActiveCollection = new MonsterCollection(0);
        noActiveCollection.addMonster(dragon);
        
        assertNull(noActiveCollection.getActiveMonster());
        assertFalse(noActiveCollection.setActiveMonster(dragon));
    }
    
    @Test
    public void testGetMonstersByRarity() {
        collection.addMonster(dragon); // RARE
        collection.addMonster(goblin); // COMMON
        collection.addMonster(phoenix); // LEGENDARY
        
        assertEquals(1, collection.getMonstersByRarity(Monster.Rarity.RARE).size());
        assertEquals(1, collection.getMonstersByRarity(Monster.Rarity.COMMON).size());
        assertEquals(1, collection.getMonstersByRarity(Monster.Rarity.LEGENDARY).size());
        assertEquals(0, collection.getMonstersByRarity(Monster.Rarity.EPIC).size());
    }
    
    @Test
    public void testGetMonstersByEffect() {
        collection.addMonster(dragon); // CHIP_BONUS
        collection.addMonster(goblin); // BETTING_BOOST
        collection.addMonster(phoenix); // LUCK_ENHANCEMENT
        
        assertEquals(1, collection.getMonstersByEffect(Monster.EffectType.CHIP_BONUS).size());
        assertEquals(1, collection.getMonstersByEffect(Monster.EffectType.BETTING_BOOST).size());
        assertEquals(1, collection.getMonstersByEffect(Monster.EffectType.LUCK_ENHANCEMENT).size());
        assertEquals(0, collection.getMonstersByEffect(Monster.EffectType.VISUAL_THEME).size());
    }
    
    @Test
    public void testHasActiveEffect() {
        collection.addMonster(dragon); // CHIP_BONUS becomes active
        
        assertTrue(collection.hasActiveEffect(Monster.EffectType.CHIP_BONUS));
        assertFalse(collection.hasActiveEffect(Monster.EffectType.BETTING_BOOST));
    }
    
    @Test
    public void testGetActiveEffectPower() {
        collection.addMonster(dragon); // Rare dragon with 50 effect power = 100 effective
        
        assertEquals(100, collection.getActiveEffectPower()); // 50 * 2.0 (rare multiplier)
        
        collection.setActiveMonster(null);
        assertEquals(0, collection.getActiveEffectPower());
    }
    
    @Test
    public void testClearCollection() {
        collection.addMonster(dragon);
        collection.addMonster(goblin);
        
        collection.clearCollection();
        assertEquals(0, collection.getMonsterCount());
        assertNull(collection.getActiveMonster());
        assertTrue(collection.getOwnedMonsters().isEmpty());
    }
    
    @Test
    public void testToString() {
        collection.addMonster(dragon);
        collection.addMonster(goblin);
        
        String str = collection.toString();
        assertTrue(str.contains("Monster Collection"));
        assertTrue(str.contains("2 monsters"));
        assertTrue(str.contains("Dragon"));
        assertTrue(str.contains("ACTIVE"));
    }
    
    @Test
    public void testImmutableMonsterList() {
        collection.addMonster(dragon);
        
        var monsters = collection.getOwnedMonsters();
        assertThrows(UnsupportedOperationException.class, () -> monsters.add(goblin));
    }
}