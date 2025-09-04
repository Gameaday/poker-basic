package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

/**
 * Test suite for the MonsterDatabase class to ensure proper monster data management.
 */
public class MonsterDatabaseTest {
    
    @BeforeEach
    void setUp() {
        // MonsterDatabase is static and self-initializing, no setup needed
    }
    
    @Test
    void testDatabaseInitialization() {
        // Test that the database contains monsters
        assertTrue(MonsterDatabase.getTotalMonsterCount() > 0, 
            "Database should contain monsters after initialization");
        
        // Test that we have a reasonable number of monsters
        assertTrue(MonsterDatabase.getTotalMonsterCount() >= 15, 
            "Should have at least 15 monsters in database");
    }
    
    @Test
    void testGetMonsterByName() {
        // Test getting a known monster
        Monster pixelPup = MonsterDatabase.getMonster("PixelPup");
        assertNotNull(pixelPup, "PixelPup should exist in database");
        assertEquals("PixelPup", pixelPup.getName());
        assertEquals(Monster.Rarity.COMMON, pixelPup.getRarity());
        
        // Test getting a non-existent monster
        Monster nonExistent = MonsterDatabase.getMonster("NonExistentMonster");
        assertNull(nonExistent, "Non-existent monster should return null");
    }
    
    @Test
    void testGetAllMonsters() {
        List<Monster> allMonsters = MonsterDatabase.getAllMonsters();
        assertNotNull(allMonsters, "getAllMonsters should not return null");
        assertFalse(allMonsters.isEmpty(), "Monster list should not be empty");
        
        // Test that the list is read-only
        assertThrows(UnsupportedOperationException.class, () -> {
            allMonsters.add(new Monster("TestMonster", Monster.Rarity.COMMON, 100, 
                Monster.EffectType.CHIP_BONUS, 10, "Test description"));
        }, "Monster list should be unmodifiable");
    }
    
    @Test
    void testGetMonstersByRarity() {
        // Test getting common monsters
        List<Monster> commonMonsters = MonsterDatabase.getMonstersByRarity(Monster.Rarity.COMMON);
        assertNotNull(commonMonsters, "Common monsters list should not be null");
        assertFalse(commonMonsters.isEmpty(), "Should have at least one common monster");
        
        // Verify all returned monsters are actually common
        for (Monster monster : commonMonsters) {
            assertEquals(Monster.Rarity.COMMON, monster.getRarity(), 
                "All monsters in common list should be common rarity");
        }
        
        // Test getting legendary monsters
        List<Monster> legendaryMonsters = MonsterDatabase.getMonstersByRarity(Monster.Rarity.LEGENDARY);
        assertNotNull(legendaryMonsters, "Legendary monsters list should not be null");
        assertFalse(legendaryMonsters.isEmpty(), "Should have at least one legendary monster");
        
        // Verify all returned monsters are actually legendary
        for (Monster monster : legendaryMonsters) {
            assertEquals(Monster.Rarity.LEGENDARY, monster.getRarity(), 
                "All monsters in legendary list should be legendary rarity");
        }
    }
    
    @Test
    void testGetRandomMonster() {
        Random testRandom = new Random(42); // Fixed seed for reproducible tests
        
        // Test that random monster selection works
        Monster randomMonster1 = MonsterDatabase.getRandomMonster(testRandom);
        assertNotNull(randomMonster1, "Random monster should not be null");
        
        Monster randomMonster2 = MonsterDatabase.getRandomMonster(testRandom);
        assertNotNull(randomMonster2, "Second random monster should not be null");
        
        // Test with many iterations to check distribution
        int commonCount = 0;
        int uncommonCount = 0;
        int rareCount = 0;
        int epicCount = 0;
        int legendaryCount = 0;
        
        Random distributionRandom = new Random(123);
        for (int i = 0; i < 1000; i++) {
            Monster monster = MonsterDatabase.getRandomMonster(distributionRandom);
            switch (monster.getRarity()) {
                case COMMON -> commonCount++;
                case UNCOMMON -> uncommonCount++;
                case RARE -> rareCount++;
                case EPIC -> epicCount++;
                case LEGENDARY -> legendaryCount++;
            }
        }
        
        // Common should be most frequent (around 50%)
        assertTrue(commonCount > uncommonCount, "Common monsters should be most frequent");
        assertTrue(commonCount > 400, "Common monsters should be roughly 50% of selection");
        
        // Legendary should be least frequent (around 2%)
        assertTrue(legendaryCount < commonCount, "Legendary monsters should be less frequent than common");
        assertTrue(legendaryCount < 50, "Legendary monsters should be roughly 2% of selection");
    }
    
    @Test
    void testHasMonster() {
        // Test existing monster
        assertTrue(MonsterDatabase.hasMonster("PixelPup"), "PixelPup should exist");
        
        // Test non-existing monster
        assertFalse(MonsterDatabase.hasMonster("NonExistentMonster"), 
            "Non-existent monster should not exist");
        
        // Test case sensitivity
        assertFalse(MonsterDatabase.hasMonster("pixelpup"), 
            "Monster names should be case sensitive");
    }
    
    @Test
    void testGetMonsterNamesStartingWith() {
        List<String> monstersStartingWithP = MonsterDatabase.getMonsterNamesStartingWith("P");
        assertNotNull(monstersStartingWithP, "Result should not be null");
        
        // Should include PixelPup and PhoenixProtocol
        assertTrue(monstersStartingWithP.contains("PixelPup"), 
            "Should include PixelPup");
        assertTrue(monstersStartingWithP.contains("PhoenixProtocol"), 
            "Should include PhoenixProtocol");
        
        // Test case insensitivity
        List<String> monstersStartingWithLowerP = MonsterDatabase.getMonsterNamesStartingWith("p");
        assertEquals(monstersStartingWithP.size(), monstersStartingWithLowerP.size(), 
            "Case insensitive search should return same number of results");
        
        // Test empty prefix
        List<String> allMonsterNames = MonsterDatabase.getMonsterNamesStartingWith("");
        assertEquals(MonsterDatabase.getTotalMonsterCount(), allMonsterNames.size(), 
            "Empty prefix should return all monster names");
    }
    
    @Test
    void testSpecificMonsters() {
        // Test some specific monsters to ensure they're properly configured
        
        // Test The Algorithm (legendary)
        Monster algorithm = MonsterDatabase.getMonster("The Algorithm");
        assertNotNull(algorithm, "The Algorithm should exist");
        assertEquals(Monster.Rarity.LEGENDARY, algorithm.getRarity());
        assertEquals(Monster.EffectType.LUCK_ENHANCEMENT, algorithm.getEffectType());
        assertEquals(700, algorithm.getBaseHealth());
        
        // Test FireFox.exe (uncommon)
        Monster firefox = MonsterDatabase.getMonster("FireFox.exe");
        assertNotNull(firefox, "FireFox.exe should exist");
        assertEquals(Monster.Rarity.UNCOMMON, firefox.getRarity());
        assertEquals(Monster.EffectType.CHIP_BONUS, firefox.getEffectType());
        
        // Test DragonDrive (epic)
        Monster dragonDrive = MonsterDatabase.getMonster("DragonDrive");
        assertNotNull(dragonDrive, "DragonDrive should exist");
        assertEquals(Monster.Rarity.EPIC, dragonDrive.getRarity());
        assertEquals(Monster.EffectType.CHIP_BONUS, dragonDrive.getEffectType());
    }
    
    @Test
    void testRarityDistribution() {
        List<Monster> allMonsters = MonsterDatabase.getAllMonsters();
        
        // Count monsters by rarity
        int commonCount = 0;
        int uncommonCount = 0;
        int rareCount = 0;
        int epicCount = 0;
        int legendaryCount = 0;
        
        for (Monster monster : allMonsters) {
            switch (monster.getRarity()) {
                case COMMON -> commonCount++;
                case UNCOMMON -> uncommonCount++;
                case RARE -> rareCount++;
                case EPIC -> epicCount++;
                case LEGENDARY -> legendaryCount++;
            }
        }
        
        // Ensure we have monsters of each rarity
        assertTrue(commonCount > 0, "Should have common monsters");
        assertTrue(uncommonCount > 0, "Should have uncommon monsters");
        assertTrue(rareCount > 0, "Should have rare monsters");
        assertTrue(epicCount > 0, "Should have epic monsters");
        assertTrue(legendaryCount > 0, "Should have legendary monsters");
        
        // Ensure legendary monsters are actually rare
        assertTrue(legendaryCount <= epicCount, "Legendary should be rarest or tied");
        assertTrue(commonCount >= uncommonCount, "Common should be most numerous or tied");
    }
}