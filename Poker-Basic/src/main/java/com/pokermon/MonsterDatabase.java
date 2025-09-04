package com.pokermon;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Centralized database of all available monsters in the Pokermon universe.
 * This class provides a hardcoded collection of monsters with their attributes,
 * following a digital pixel design theme reminiscent of classic digital pets.
 */
public class MonsterDatabase {
    
    private static final Map<String, Monster> MONSTER_DATABASE = new HashMap<>();
    private static final List<Monster> MONSTERS_BY_RARITY = new ArrayList<>();
    
    static {
        initializeMonsterDatabase();
    }
    
    /**
     * Initialize the monster database with all available monsters.
     * Monsters follow a digital pixel aesthetic with diverse types and abilities.
     */
    private static void initializeMonsterDatabase() {
        // Common Monsters (Digital Pets Theme)
        addMonster(new Monster("PixelPup", Monster.Rarity.COMMON, 100, 
            Monster.EffectType.CHIP_BONUS, 50, 
            "A loyal digital companion that boosts your starting chips"));
        
        addMonster(new Monster("ByteBird", Monster.Rarity.COMMON, 80,
            Monster.EffectType.CARD_ADVANTAGE, 1,
            "Swift pixelated flyer that grants an extra card draw"));
        
        addMonster(new Monster("CodeCat", Monster.Rarity.COMMON, 90,
            Monster.EffectType.LUCK_ENHANCEMENT, 5,
            "Curious feline program that slightly improves your luck"));
        
        addMonster(new Monster("DataDog", Monster.Rarity.COMMON, 110,
            Monster.EffectType.BETTING_BOOST, 10,
            "Faithful digital hound that enhances betting effectiveness"));
        
        // Uncommon Monsters (Elemental Digital Theme)
        addMonster(new Monster("FireFox.exe", Monster.Rarity.UNCOMMON, 150,
            Monster.EffectType.CHIP_BONUS, 100,
            "Blazing browser spirit that significantly boosts starting chips"));
        
        addMonster(new Monster("AquaApp", Monster.Rarity.UNCOMMON, 140,
            Monster.EffectType.CARD_ADVANTAGE, 2,
            "Fluid application that grants multiple extra draws"));
        
        addMonster(new Monster("TechTurtle", Monster.Rarity.UNCOMMON, 180,
            Monster.EffectType.BETTING_BOOST, 20,
            "Slow but steady shell program with strong betting bonuses"));
        
        addMonster(new Monster("CloudCrawler", Monster.Rarity.UNCOMMON, 130,
            Monster.EffectType.LUCK_ENHANCEMENT, 15,
            "Floating data creature that substantially improves fortune"));
        
        // Rare Monsters (Advanced AI Theme)
        addMonster(new Monster("NeuralNinja", Monster.Rarity.RARE, 250,
            Monster.EffectType.CARD_ADVANTAGE, 3,
            "Stealthy AI warrior with superior card manipulation abilities"));
        
        addMonster(new Monster("QuantumQuokka", Monster.Rarity.RARE, 220,
            Monster.EffectType.LUCK_ENHANCEMENT, 25,
            "Quantum marsupial that bends probability in your favor"));
        
        addMonster(new Monster("CyberShark", Monster.Rarity.RARE, 280,
            Monster.EffectType.BETTING_BOOST, 40,
            "Predatory program that dominates betting rounds"));
        
        addMonster(new Monster("RoboRaven", Monster.Rarity.RARE, 240,
            Monster.EffectType.CHIP_BONUS, 200,
            "Mechanical corvid that hoards substantial digital currency"));
        
        // Epic Monsters (Legendary Programs)
        addMonster(new Monster("MegaMind.AI", Monster.Rarity.EPIC, 400,
            Monster.EffectType.CARD_ADVANTAGE, 4,
            "Supreme artificial intelligence with unparalleled card control"));
        
        addMonster(new Monster("DragonDrive", Monster.Rarity.EPIC, 450,
            Monster.EffectType.CHIP_BONUS, 350,
            "Ancient storage dragon guarding vast digital treasures"));
        
        addMonster(new Monster("PhoenixProtocol", Monster.Rarity.EPIC, 380,
            Monster.EffectType.LUCK_ENHANCEMENT, 40,
            "Self-reviving program that brings incredible fortune"));
        
        // Legendary Monsters (Ultimate Digital Entities)
        addMonster(new Monster("The Compiler", Monster.Rarity.LEGENDARY, 600,
            Monster.EffectType.CARD_ADVANTAGE, 5,
            "Legendary code transformer with ultimate card mastery"));
        
        addMonster(new Monster("Daemon.exe", Monster.Rarity.LEGENDARY, 650,
            Monster.EffectType.BETTING_BOOST, 100,
            "Mythical system process with overwhelming betting power"));
        
        addMonster(new Monster("The Algorithm", Monster.Rarity.LEGENDARY, 700,
            Monster.EffectType.LUCK_ENHANCEMENT, 75,
            "The ultimate mathematical entity that controls all probability"));
        
        // Sort monsters by rarity for easy lookup
        MONSTERS_BY_RARITY.sort(Comparator.comparing(Monster::getRarity));
    }
    
    /**
     * Add a monster to the database.
     */
    private static void addMonster(Monster monster) {
        MONSTER_DATABASE.put(monster.getName(), monster);
        MONSTERS_BY_RARITY.add(monster);
    }
    
    /**
     * Get a monster by name.
     * @param name the name of the monster
     * @return the monster, or null if not found
     */
    public static Monster getMonster(String name) {
        return MONSTER_DATABASE.get(name);
    }
    
    /**
     * Get all available monsters.
     * @return unmodifiable list of all monsters
     */
    public static List<Monster> getAllMonsters() {
        return Collections.unmodifiableList(MONSTERS_BY_RARITY);
    }
    
    /**
     * Get monsters by rarity level.
     * @param rarity the rarity to filter by
     * @return list of monsters with the specified rarity
     */
    public static List<Monster> getMonstersByRarity(Monster.Rarity rarity) {
        return MONSTERS_BY_RARITY.stream()
            .filter(monster -> monster.getRarity() == rarity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get a random monster based on rarity weights.
     * Common monsters are most likely, legendary monsters are very rare.
     * @param random random number generator to use
     * @return a randomly selected monster
     */
    public static Monster getRandomMonster(Random random) {
        // Weighted rarity selection
        double roll = random.nextDouble();
        
        if (roll < 0.50) { // 50% chance for common
            List<Monster> commons = getMonstersByRarity(Monster.Rarity.COMMON);
            return commons.get(random.nextInt(commons.size()));
        } else if (roll < 0.75) { // 25% chance for uncommon
            List<Monster> uncommons = getMonstersByRarity(Monster.Rarity.UNCOMMON);
            return uncommons.get(random.nextInt(uncommons.size()));
        } else if (roll < 0.90) { // 15% chance for rare
            List<Monster> rares = getMonstersByRarity(Monster.Rarity.RARE);
            return rares.get(random.nextInt(rares.size()));
        } else if (roll < 0.98) { // 8% chance for epic
            List<Monster> epics = getMonstersByRarity(Monster.Rarity.EPIC);
            return epics.get(random.nextInt(epics.size()));
        } else { // 2% chance for legendary
            List<Monster> legendaries = getMonstersByRarity(Monster.Rarity.LEGENDARY);
            return legendaries.get(random.nextInt(legendaries.size()));
        }
    }
    
    /**
     * Get the total number of monsters in the database.
     * @return total monster count
     */
    public static int getTotalMonsterCount() {
        return MONSTER_DATABASE.size();
    }
    
    /**
     * Get monster names that start with a specific prefix (for search/filtering).
     * @param prefix the prefix to search for
     * @return list of matching monster names
     */
    public static List<String> getMonsterNamesStartingWith(String prefix) {
        return MONSTER_DATABASE.keySet().stream()
            .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a monster exists in the database.
     * @param name the monster name to check
     * @return true if the monster exists
     */
    public static boolean hasMonster(String name) {
        return MONSTER_DATABASE.containsKey(name);
    }
}