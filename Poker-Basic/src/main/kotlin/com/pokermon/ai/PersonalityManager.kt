package com.pokermon.ai

import com.pokermon.Monster
import com.pokermon.MonsterDatabase
import com.pokermon.Player
import kotlin.random.Random

/**
 * Manages personality assignments for AI players and provides advanced AI
 * behavior integration using modern Kotlin patterns. This class serves as the 
 * bridge between the monster system and the AI personality system.
 * 
 * @author Pokermon AI System
 * @version 1.0.0
 */
object PersonalityManager {
    
    private val playerMonsters = mutableMapOf<String, Monster>()
    private val playerPersonalities = mutableMapOf<String, Personality>()
    private val customPersonalities = mutableMapOf<String, Personality>()
    private val aiBehavior = AdvancedAIBehavior()
    private val random = Random.Default
    
    // Available monsters for random selection
    private val availableMonsters = listOf(
        "PixelPup", "ByteBird", "CodeCat", "DataDog", "FireFox.exe",
        "AquaApp", "TechTurtle", "CloudCrawler", "NeuralNinja", "QuantumQuokka"
    )
    
    /**
     * Assigns a monster and personality to an AI player.
     * This should be called during game setup for each AI player.
     * 
     * @param playerName the name of the AI player
     * @param monster the monster to assign (null for random selection)
     * @param personality the personality to assign (null for monster's personality)
     */
    fun assignMonsterToPlayer(playerName: String, monster: Monster? = null, personality: Personality? = null) {
        if (playerName.isBlank()) return
        
        // Select a random monster if none provided
        val selectedMonster = monster ?: selectRandomMonster()
        
        // Use monster's personality if none provided
        val selectedPersonality = personality ?: selectedMonster.personality
        
        playerMonsters[playerName] = selectedMonster
        playerPersonalities[playerName] = selectedPersonality
    }
    
    /**
     * Assigns a random monster to an AI player.
     * @param playerName the name of the AI player
     */
    fun assignRandomMonsterToPlayer(playerName: String) {
        assignMonsterToPlayer(playerName, null, null)
    }
    
    /**
     * Sets a custom personality for a specific player (boss monsters, special encounters).
     * This overrides the monster's default personality.
     * 
     * @param playerName the name of the player
     * @param personality the custom personality to assign
     */
    fun setCustomPersonality(playerName: String, personality: Personality) {
        customPersonalities[playerName] = personality
        playerPersonalities[playerName] = personality
    }
    
    /**
     * Gets the monster assigned to a player.
     * @param playerName the player's name
     * @return the assigned monster, or null if none assigned
     */
    fun getPlayerMonster(playerName: String): Monster? = playerMonsters[playerName]
    
    /**
     * Gets the personality assigned to a player.
     * @param playerName the player's name
     * @return the assigned personality, or a default personality if none assigned
     */
    fun getPlayerPersonality(playerName: String): Personality {
        return playerPersonalities[playerName] ?: defaultPersonality
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
    fun calculateAdvancedAIBet(player: Player, currentBet: Int, potSize: Int): Int {
        require(!player.isHuman) { "Cannot calculate AI bet for human player" }
        
        // Get the player's personality
        val personality = getPlayerPersonality(player.name)
        
        // Assess hand strength using the existing hand value system
        val handStrength = AdvancedAIBehavior.assessHandStrength(player.handValue)
        
        // Create a simple game context
        val context = AdvancedAIBehavior.createSimpleContext(currentBet, potSize)
        
        // Use the advanced AI system to calculate the bet
        return aiBehavior.calculateAIBet(player, personality, context, handStrength)
    }
    
    /**
     * Enhanced AI bet calculation with detailed game context.
     * Provides more sophisticated decision making for advanced game modes.
     */
    fun calculateAdvancedAIBetWithContext(
        player: Player,
        currentBet: Int,
        potSize: Int,
        playersRemaining: Int,
        bettingRound: Int,
        lastToAct: Boolean,
        averageChips: Int
    ): Int {
        require(!player.isHuman) { "Cannot calculate AI bet for human player" }
        
        val personality = getPlayerPersonality(player.name)
        val handStrength = AdvancedAIBehavior.assessHandStrength(player.handValue)
        
        val context = AdvancedAIBehavior.createDetailedContext(
            currentBet, potSize, playersRemaining, bettingRound, 
            lastToAct, player.chips, averageChips
        )
        
        return aiBehavior.calculateAIBet(player, personality, context, handStrength)
    }
    
    /**
     * Checks if a player has been assigned a monster/personality.
     * @param playerName the player's name
     * @return true if the player has assignments, false otherwise
     */
    fun hasPlayerAssignments(playerName: String): Boolean = playerPersonalities.containsKey(playerName)
    
    /**
     * Clears all player assignments. Useful for starting a new game.
     */
    fun clearAllAssignments() {
        playerMonsters.clear()
        playerPersonalities.clear()
        customPersonalities.clear()
    }
    
    /**
     * Gets information about a player's AI setup for debugging/display.
     * @param playerName the player's name
     * @return a descriptive string about the player's AI setup
     */
    fun getPlayerAIInfo(playerName: String): String {
        val monster = getPlayerMonster(playerName)
        val personality = getPlayerPersonality(playerName)
        
        return if (monster != null) {
            "$playerName (${monster.name}, ${personality.displayName})"
        } else {
            "$playerName (${personality.displayName} personality)"
        }
    }
    
    /**
     * Auto-assigns monsters to all AI players in a player list.
     * This is a convenience method for game setup using modern Kotlin patterns.
     * 
     * @param players list of players
     */
    fun autoAssignMonstersToAI(players: List<Player>) {
        players.filter { !it.isHuman && !hasPlayerAssignments(it.name) }
               .forEach { assignRandomMonsterToPlayer(it.name) }
    }
    
    /**
     * Auto-assigns monsters to all AI players in a player array (for compatibility).
     */
    fun autoAssignMonstersToAI(players: Array<Player>) {
        players.filterNotNull()
               .filter { !it.isHuman && !hasPlayerAssignments(it.name) }
               .forEach { assignRandomMonsterToPlayer(it.name) }
    }
    
    /**
     * Gets all assigned monsters for analysis or display purposes.
     */
    fun getAllAssignedMonsters(): Map<String, Monster> = playerMonsters.toMap()
    
    /**
     * Gets all assigned personalities for analysis or display purposes.
     */
    fun getAllAssignedPersonalities(): Map<String, Personality> = playerPersonalities.toMap()
    
    /**
     * Creates a summary of all AI player assignments.
     */
    fun getAssignmentSummary(): String {
        return if (playerPersonalities.isEmpty()) {
            "No AI players assigned yet."
        } else {
            playerPersonalities.keys.joinToString("\n") { getPlayerAIInfo(it) }
        }
    }
    
    /**
     * Selects a random monster from the database.
     * @return a random monster
     */
    private fun selectRandomMonster(): Monster {
        // Try to get a random monster from the predefined list
        val selectedName = availableMonsters.random(random)
        var monster = MonsterDatabase.getMonster(selectedName)
        
        // Fallback to PixelPup if the selected monster doesn't exist
        if (monster == null) {
            monster = MonsterDatabase.getMonster("PixelPup")
        }
        
        // If still null, create a basic monster
        if (monster == null) {
            monster = Monster(
                name = selectedName,
                rarity = Monster.Rarity.COMMON,
                baseHealth = 100,
                effectType = Monster.EffectType.CHIP_BONUS,
                effectPower = 10,
                description = "A digital companion"
            )
        }
        
        return monster
    }
    
    /**
     * Gets a default personality for players without assignments.
     * @return a default personality
     */
    private val defaultPersonality: Personality
        get() = Personality.HAPPY // A balanced, neutral personality
}