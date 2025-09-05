package com.pokermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.pokermon.api.GameMode;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Adventure Mode implementation.
 * Validates monster generation, battle mechanics, and integration with poker system.
 */
public class AdventureModeTest {
    
    private AdventureMode adventureMode;
    
    @BeforeEach
    void setUp() {
        adventureMode = new AdventureMode("TestPlayer", 1000);
    }
    
    @Test
    void testAdventureModeCreation() {
        assertNotNull(adventureMode, "Adventure mode should be created successfully");
    }
    
    @Test
    void testAdventureModeCanBeInstantiated() {
        // Test that Adventure Mode can be instantiated with valid parameters
        AdventureMode mode1 = new AdventureMode("Player1", 500);
        AdventureMode mode2 = new AdventureMode("Player2", 1000);
        AdventureMode mode3 = new AdventureMode("Player3", 2000);
        
        assertNotNull(mode1, "Adventure mode with 500 chips should be created");
        assertNotNull(mode2, "Adventure mode with 1000 chips should be created");
        assertNotNull(mode3, "Adventure mode with 2000 chips should be created");
    }
    
    @Test
    void testAdventureModeWithDifferentPlayerNames() {
        // Test that Adventure Mode works with different player names
        AdventureMode mode1 = new AdventureMode("Alice", 1000);
        AdventureMode mode2 = new AdventureMode("Bob", 1000);
        AdventureMode mode3 = new AdventureMode("Player with Spaces", 1000);
        
        assertNotNull(mode1, "Adventure mode with 'Alice' should be created");
        assertNotNull(mode2, "Adventure mode with 'Bob' should be created");
        assertNotNull(mode3, "Adventure mode with spaced name should be created");
    }
    
    @Test
    void testMonsterCreationIntegration() {
        // Test that monsters can be created with the parameters used in Adventure Mode
        Monster testMonster = new Monster(
            "Test Monster Lv.1",
            Monster.Rarity.COMMON,
            100,
            Monster.EffectType.CHIP_BONUS,
            20,
            "A test monster for adventure mode"
        );
        
        assertNotNull(testMonster, "Monster should be created successfully");
        assertEquals("Test Monster Lv.1", testMonster.getName(), "Monster name should match");
        assertEquals(Monster.Rarity.COMMON, testMonster.getRarity(), "Monster rarity should be COMMON");
        assertEquals(100, testMonster.getBaseHealth(), "Monster base health should be 100");
        assertEquals(Monster.EffectType.CHIP_BONUS, testMonster.getEffectType(), "Monster effect should be CHIP_BONUS");
    }
    
    @Test
    void testMonsterRarityEffects() {
        // Test that different rarities have different effective health
        Monster common = new Monster("Common", Monster.Rarity.COMMON, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        Monster uncommon = new Monster("Uncommon", Monster.Rarity.UNCOMMON, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        Monster rare = new Monster("Rare", Monster.Rarity.RARE, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        Monster epic = new Monster("Epic", Monster.Rarity.EPIC, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        Monster legendary = new Monster("Legendary", Monster.Rarity.LEGENDARY, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        
        assertTrue(common.getEffectiveHealth() < uncommon.getEffectiveHealth(), 
                  "Uncommon should have higher effective health than common");
        assertTrue(uncommon.getEffectiveHealth() < rare.getEffectiveHealth(), 
                  "Rare should have higher effective health than uncommon");
        assertTrue(rare.getEffectiveHealth() < epic.getEffectiveHealth(), 
                  "Epic should have higher effective health than rare");
        assertTrue(epic.getEffectiveHealth() < legendary.getEffectiveHealth(), 
                  "Legendary should have higher effective health than epic");
    }
    
    @Test
    void testMonsterCollectionIntegration() {
        // Test that MonsterCollection can be used as expected in Adventure Mode
        MonsterCollection collection = new MonsterCollection();
        Monster testMonster = new Monster("Test", Monster.Rarity.COMMON, 100, Monster.EffectType.CHIP_BONUS, 10, "Test");
        
        collection.addMonster(testMonster);
        assertEquals(1, collection.getMonsterCount(), "Collection should have 1 monster after adding");
        
        // Test that we can add multiple monsters
        Monster testMonster2 = new Monster("Test2", Monster.Rarity.UNCOMMON, 150, Monster.EffectType.CARD_ADVANTAGE, 15, "Test2");
        collection.addMonster(testMonster2);
        assertEquals(2, collection.getMonsterCount(), "Collection should have 2 monsters after adding another");
    }
    
    @Test
    void testPlayerSetupForAdventure() {
        // Test that Player class methods used in Adventure Mode work correctly
        Player testPlayer = new Player();
        testPlayer.setName("AdventurePlayer");
        testPlayer.setChipsCurrent(1000);
        
        assertEquals("AdventurePlayer", testPlayer.getName(), "Player name should be set correctly");
        assertEquals(1000, testPlayer.getChips(), "Player chips should be set correctly");
        
        // Test hand creation
        int[] deck = Main.setDeck();
        int[] hand = Main.newHand(deck);
        
        assertNotNull(hand, "Hand should be created successfully");
        assertEquals(5, hand.length, "Hand should have 5 cards by default");
        
        testPlayer.updateHand(hand);
        testPlayer.convertHand();
        testPlayer.calculateHandValue();
        
        assertNotNull(testPlayer.getConvertedHand(), "Converted hand should not be null");
        assertTrue(testPlayer.getHandValue() >= 0, "Hand value should be non-negative");
    }
    
    @Test
    void testGameModeFactoryForAdventure() {
        // Test that Game class factory method for Adventure Mode works
        Game adventureGame = Game.createAdventureMode();
        
        assertNotNull(adventureGame, "Adventure game configuration should be created");
        assertEquals(GameMode.ADVENTURE, adventureGame.getGameMode(), "Game mode should be ADVENTURE");
        assertTrue(adventureGame.getGameMode().hasMonsters(), "Adventure mode should have monsters enabled");
    }
    
    @Test
    void testGameEngineWithAdventureMode() {
        // Test that GameEngine can handle Adventure Mode configuration
        Game adventureConfig = Game.createAdventureMode();
        GameEngine engine = new GameEngine(adventureConfig);
        
        assertNotNull(engine, "GameEngine should be created with Adventure Mode config");
        
        String[] playerNames = {"AdventurePlayer", "CPU1"};
        boolean initialized = engine.initializeGame(playerNames);
        assertTrue(initialized, "Game should initialize with valid player names");
    }
}