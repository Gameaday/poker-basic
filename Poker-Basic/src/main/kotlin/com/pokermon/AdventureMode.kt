package com.pokermon

import kotlin.math.min
import kotlin.math.max
import kotlin.random.Random

/**
 * Adventure Mode implementation - Monster battles using poker combat.
 * Players battle monsters whose health equals their chip count.
 * Poker hand strength determines battle outcomes.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0 - Beta Implementation (Kotlin-native)
 */
class AdventureMode(
    private val playerName: String,
    private val initialChips: Int
) {
    private val playerCollection = MonsterCollection()
    private var currentLevel = 1
    private var monstersDefeated = 0
    
    companion object {
        private val random = Random.Default
    }
    
    /**
     * Starts the Adventure Mode gameplay loop.
     */
    fun startAdventure() {
        println("🏔️ ${playerName}'s Adventure Begins! 🏔️")
        println("You start with $initialChips chips to use in battle.")
        println()
        
        var continueAdventure = true
        var playerChips = initialChips
        
        while (continueAdventure && playerChips > 0) {
            val enemy = generateEnemyMonster()
            println("💀 A wild ${enemy.name} appears!")
            println("   Rarity: ${enemy.rarity.displayName}")
            println("   Health: ${enemy.effectiveHealth} HP")
            println("   Your chips: $playerChips")
            println()
            
            val result = battleMonster(enemy, playerChips)
            
            if (result.victory) {
                println("🎉 Victory! You defeated the ${enemy.name}!")
                
                // Calculate rewards
                val chipReward = calculateChipReward(enemy)
                playerChips += chipReward
                monstersDefeated++
                
                println("   Chip reward: +$chipReward chips")
                println("   Total chips: $playerChips")
                
                // Chance to capture the monster
                if (attemptCapture(enemy, result.handStrength)) {
                    playerCollection.addMonster(enemy)
                    println("   🎁 Bonus: ${enemy.name} joined your collection!")
                }
                
                println()
                currentLevel++
                
            } else {
                println("💀 Defeat! The ${enemy.name} was too strong.")
                val chipsLost = min(playerChips / 4, 100) // Lose 25% or max 100
                playerChips -= chipsLost
                println("   You lost $chipsLost chips in the retreat.")
                println("   Remaining chips: $playerChips")
                println()
            }
            
            if (playerChips <= 0) {
                println("💀 Game Over! You've run out of chips for battle.")
                break
            }
            
            continueAdventure = promptContinue()
        }
        
        showAdventureStats()
    }
    
    /**
     * Generates an enemy monster based on current level.
     */
    private fun generateEnemyMonster(): Monster {
        val (rarity, name, baseHealth) = when {
            currentLevel >= 10 && random.nextInt(100) < 5 -> {
                Triple(Monster.Rarity.LEGENDARY, "Ancient Dragon", 800)
            }
            currentLevel >= 7 && random.nextInt(100) < 15 -> {
                Triple(Monster.Rarity.EPIC, "Shadow Beast", 500)
            }
            currentLevel >= 4 && random.nextInt(100) < 30 -> {
                Triple(Monster.Rarity.RARE, "Crystal Golem", 300)
            }
            currentLevel >= 2 && random.nextInt(100) < 60 -> {
                Triple(Monster.Rarity.UNCOMMON, "Forest Guardian", 150)
            }
            else -> {
                Triple(Monster.Rarity.COMMON, "Wild Slime", 100)
            }
        }
        
        // Scale health with level
        val scaledHealth = baseHealth + (currentLevel - 1) * 50
        
        return Monster(
            name = "$name Lv.$currentLevel",
            rarity = rarity,
            baseHealth = scaledHealth,
            effectType = Monster.EffectType.CHIP_BONUS,
            effectPower = 20,
            description = "A wild monster encountered in adventure mode"
        )
    }
    
    /**
     * Conducts a poker battle between player and monster.
     */
    private fun battleMonster(enemy: Monster, playerChips: Int): BattleResult {
        println("⚔️ POKER BATTLE COMMENCES! ⚔️")
        println("Draw your hand and prepare for battle!")
        println()
        
        // Create a simple poker hand for the player using modern game engine
        val gameEngine = GameEngine(
            gameConfig = Game(
                gameMode = GameMode.ADVENTURE,
                startingChips = playerChips
            )
        )
        
        // Initialize the game with the player
        gameEngine.initializeGame(arrayOf(playerName))
        val hand = gameEngine.players!![0].hand
        
        // Evaluate hand using HandEvaluator for consistent scoring
        val handResult = HandEvaluator.evaluateHand(hand)
        val handValue = handResult.score
        
        // Display player's hand using CardUtils
        println("Your battle hand:")
        hand.forEachIndexed { index, card ->
            println("  ${index + 1}: ${CardUtils.cardName(card)}")
        }
        println()
        
        println("Hand strength: $handValue (${handResult.description})")
        
        // Calculate battle outcome
        val damageDealt = calculateDamage(handValue, enemy)
        val victory = damageDealt >= enemy.effectiveHealth
        
        println("⚡ You deal $damageDealt damage!")
        
        if (victory) {
            println("💥 Critical hit! The monster is defeated!")
        } else {
            println("💢 The monster survives with ${enemy.effectiveHealth - damageDealt} HP remaining.")
        }
        println()
        
        return BattleResult(victory, handValue, damageDealt)
    }
    
    /**
     * Calculates damage based on poker hand strength.
     */
    private fun calculateDamage(handValue: Int, enemy: Monster): Int {
        // Base damage scales with hand strength
        val baseDamage = handValue * 10
        
        // Add randomness (80-120% of base damage)
        val multiplier = 0.8 + (random.nextDouble() * 0.4)
        var finalDamage = (baseDamage * multiplier).toInt()
        
        // Rarity affects enemy toughness
        val rarityDefense = 1.0 / enemy.rarity.powerMultiplier
        finalDamage = (finalDamage * rarityDefense).toInt()
        
        return max(finalDamage, 1) // Minimum 1 damage
    }
    
    /**
     * Calculates chip rewards for defeating a monster.
     */
    private fun calculateChipReward(enemy: Monster): Int {
        val baseReward = enemy.effectiveHealth / 2 // Half the monster's health
        val rarityBonus = enemy.rarity.powerMultiplier
        return (baseReward * rarityBonus).toInt()
    }
    
    /**
     * Attempts to capture a defeated monster.
     */
    private fun attemptCapture(enemy: Monster, handStrength: Int): Boolean {
        // Higher hand strength = better capture chance
        var captureChance = min(0.8, 0.1 + (handStrength * 0.02))
        
        // Rarer monsters are harder to capture
        captureChance /= enemy.rarity.powerMultiplier
        
        return random.nextDouble() < captureChance
    }
    
    /**
     * Prompts player to continue the adventure.
     */
    private fun promptContinue(): Boolean {
        print("Continue your adventure? (y/n) [y]: ")
        val input = readlnOrNull()?.trim()?.lowercase() ?: ""
        return input.isEmpty() || input == "y" || input == "yes"
    }
    
    /**
     * Displays final adventure statistics.
     */
    private fun showAdventureStats() {
        println("🏆 ADVENTURE COMPLETE! 🏆")
        println("=".repeat(40))
        println("Monsters defeated: $monstersDefeated")
        println("Highest level reached: $currentLevel")
        println("Monsters captured: ${playerCollection.getMonsterCount()}")
        println()
        
        if (playerCollection.getMonsterCount() > 0) {
            println("Your Monster Collection:")
            println("  Total monsters: ${playerCollection.getMonsterCount()}")
            playerCollection.getOwnedMonsters().forEach { monster ->
                println("  - ${monster.name} (${monster.rarity.displayName})")
            }
        }
        
        println("Thank you for playing Adventure Mode!")
    }
    
    /**
     * Result of a battle encounter.
     */
    private data class BattleResult(
        val victory: Boolean,
        val handStrength: Int,
        val damageDealt: Int
    )
}