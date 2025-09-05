package com.pokermon.ai;

import com.pokermon.Monster;
import com.pokermon.MonsterDatabase;
import com.pokermon.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages personality assignments for AI players and provides advanced AI
 * behavior integration. This class serves as the bridge between the monster
 * system and the AI personality system.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
public class PersonalityManager {
    
    private static PersonalityManager instance;
    private final Map<String, Monster> playerMonsters;
    private final Map<String, Personality> playerPersonalities;
    private final Map<String, Personality> customPersonalities;
    private final AdvancedAIBehavior aiBehavior;
    private final Random random;
    
    /**
     * Private constructor for singleton pattern.
     */
    private PersonalityManager() {
        this.playerMonsters = new HashMap<>();
        this.playerPersonalities = new HashMap<>();
        this.customPersonalities = new HashMap<>();
        this.aiBehavior = new AdvancedAIBehavior();
        this.random = new Random();
    }
    
    /**
     * Gets the singleton instance of the PersonalityManager.
     * @return the PersonalityManager instance
     */
    public static synchronized PersonalityManager getInstance() {
        if (instance == null) {
            instance = new PersonalityManager();
        }
        return instance;
    }
    
    /**
     * Assigns a monster and personality to an AI player.
     * This should be called during game setup for each AI player.
     * 
     * @param playerName the name of the AI player
     * @param monster the monster to assign (null for random selection)
     * @param personality the personality to assign (null for monster's personality)
     */
    public void assignMonsterToPlayer(String playerName, Monster monster, Personality personality) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return;
        }
        
        // Select a random monster if none provided
        if (monster == null) {
            monster = selectRandomMonster();
        }
        
        // Use monster's personality if none provided
        if (personality == null) {
            personality = monster.getPersonality();
        }
        
        playerMonsters.put(playerName, monster);
        playerPersonalities.put(playerName, personality);
    }
    
    /**
     * Assigns a random monster to an AI player.
     * @param playerName the name of the AI player
     */
    public void assignRandomMonsterToPlayer(String playerName) {
        assignMonsterToPlayer(playerName, null, null);
    }
    
    /**
     * Sets a custom personality for a specific player (boss monsters, special encounters).
     * This overrides the monster's default personality.
     * 
     * @param playerName the name of the player
     * @param personality the custom personality to assign
     */
    public void setCustomPersonality(String playerName, Personality personality) {
        if (playerName != null && personality != null) {
            customPersonalities.put(playerName, personality);
            playerPersonalities.put(playerName, personality);
        }
    }
    
    /**
     * Gets the monster assigned to a player.
     * @param playerName the player's name
     * @return the assigned monster, or null if none assigned
     */
    public Monster getPlayerMonster(String playerName) {
        return playerMonsters.get(playerName);
    }
    
    /**
     * Gets the personality assigned to a player.
     * @param playerName the player's name
     * @return the assigned personality, or a default personality if none assigned
     */
    public Personality getPlayerPersonality(String playerName) {
        return playerPersonalities.getOrDefault(playerName, getDefaultPersonality());
    }
    
    /**
     * Calculates an AI player's bet using the advanced personality system.
     * This is the main entry point for the new AI behavior.
     * 
     * @param player the AI player making the decision
     * @param currentBet the current bet amount in the game
     * @param potSize the current pot size
     * @return the bet amount the AI wants to place
     */
    public int calculateAdvancedAIBet(Player player, int currentBet, int potSize) {
        if (player.isHuman()) {
            throw new IllegalArgumentException("Cannot calculate AI bet for human player");
        }
        
        // Get the player's personality
        Personality personality = getPlayerPersonality(player.getName());
        
        // Assess hand strength using the existing hand value system
        float handStrength = AdvancedAIBehavior.assessHandStrength(player.getHandValue());
        
        // Create a simple game context
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(currentBet, potSize);
        
        // Use the advanced AI system to calculate the bet
        return aiBehavior.calculateAIBet(player, personality, context, handStrength);
    }
    
    /**
     * Checks if a player has been assigned a monster/personality.
     * @param playerName the player's name
     * @return true if the player has assignments, false otherwise
     */
    public boolean hasPlayerAssignments(String playerName) {
        return playerPersonalities.containsKey(playerName);
    }
    
    /**
     * Clears all player assignments. Useful for starting a new game.
     */
    public void clearAllAssignments() {
        playerMonsters.clear();
        playerPersonalities.clear();
        customPersonalities.clear();
    }
    
    /**
     * Gets information about a player's AI setup for debugging/display.
     * @param playerName the player's name
     * @return a descriptive string about the player's AI setup
     */
    public String getPlayerAIInfo(String playerName) {
        Monster monster = getPlayerMonster(playerName);
        Personality personality = getPlayerPersonality(playerName);
        
        if (monster != null) {
            return String.format("%s (%s, %s)", playerName, monster.getName(), personality.getDisplayName());
        } else {
            return String.format("%s (%s personality)", playerName, personality.getDisplayName());
        }
    }
    
    /**
     * Selects a random monster from the database.
     * @return a random monster
     */
    private Monster selectRandomMonster() {
        // Get a random monster from the database
        // This is a simplified approach - in practice, you might want to consider
        // difficulty levels, player progress, etc.
        String[] monsterNames = {
            "PixelPup", "ByteBird", "CodeCat", "DataDog", "FireFox.exe",
            "AquaApp", "TechTurtle", "CloudCrawler", "NeuralNinja", "QuantumQuokka"
        };
        
        String selectedName = monsterNames[random.nextInt(monsterNames.length)];
        Monster monster = MonsterDatabase.INSTANCE.getMonster(selectedName);
        
        // Fallback to PixelPup if the selected monster doesn't exist
        if (monster == null) {
            monster = MonsterDatabase.INSTANCE.getMonster("PixelPup");
        }
        
        // If still null, create a basic monster
        if (monster == null) {
            monster = new Monster(selectedName, Monster.Rarity.COMMON, 100, 
                                Monster.EffectType.CHIP_BONUS, 10, "A digital companion");
        }
        
        return monster;
    }
    
    /**
     * Gets a default personality for players without assignments.
     * @return a default personality
     */
    private Personality getDefaultPersonality() {
        return Personality.HAPPY; // A balanced, neutral personality
    }
    
    /**
     * Auto-assigns monsters to all AI players in a player array.
     * This is a convenience method for game setup.
     * 
     * @param players array of players
     */
    public void autoAssignMonstersToAI(Player[] players) {
        if (players == null) return;
        
        for (Player player : players) {
            if (player != null && !player.isHuman() && !hasPlayerAssignments(player.getName())) {
                assignRandomMonsterToPlayer(player.getName());
            }
        }
    }
}