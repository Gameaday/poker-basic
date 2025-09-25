package com.pokermon.modes.adventure

import com.pokermon.*
import com.pokermon.database.Monster
import com.pokermon.database.MonsterCollection
import com.pokermon.modern.CardUtils
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Adventure Mode implementation - Monster battles using poker combat.
 * Players battle monsters whose health equals their chip count.
 * Poker hand strength determines battle outcomes.
 *
 * @author Carl Nelson (@Gameaday)
 * @version 1.1.0 - Enhanced with Quest System
 */
class AdventureMode(
    private val playerName: String,
    private val initialChips: Int,
) {
    private val playerCollection = MonsterCollection()
    private val questSystem = QuestSystem()
    private var currentLevel = 1
    private var monstersDefeated = 0
    private var experience = 0
    private var consecutiveWins = 0

    companion object {
        private val random = Random.Default
        private const val LEVEL_UP_EXP = 200
    }

    /**
     * Starts the Adventure Mode gameplay loop.
     */
    fun startAdventure() {
        println("🏔️ $playerName's Adventure Begins! 🏔️")
        println("You start with $initialChips chips to use in battle.")
        println()
        
        // Initialize quest system
        questSystem.initialize()
        println("📋 Quest system activated! Check your objectives regularly.")
        println()

        var continueAdventure = true
        var playerChips = initialChips

        while (continueAdventure && playerChips > 0) {
            displayAdventureStatus(playerChips)
            
            val enemy = generateEnemyMonster()
            println("💀 A wild ${enemy.name} appears!")
            println("   Rarity: ${enemy.rarity.displayName}")
            println("   Health: ${enemy.effectiveHealth} HP")
            println("   Level: $currentLevel")
            
            // Show relevant quest progress
            if (questSystem.getActiveQuests().isNotEmpty()) {
                println("   🎯 Active quests: ${questSystem.getActiveQuests().size}")
            }
            println()

            val result = battleMonster(enemy, playerChips)
            
            // Create battle outcome for quest system
            val battleOutcome = BattleOutcome(
                victory = result.victory,
                handStrength = result.handStrength,
                handType = "Unknown", // Would need to track from hand evaluation
                chipsGained = if (result.victory) calculateChipReward(enemy) else 0,
                damageDealt = result.damageDealt
            )
            
            // Update quest progress
            questSystem.updateProgress(battleOutcome)

            if (result.victory) {
                println("🎉 Victory! You defeated the ${enemy.name}!")

                // Calculate rewards
                val chipReward = calculateChipReward(enemy)
                val expReward = calculateExpReward(enemy)
                
                playerChips += chipReward
                experience += expReward
                monstersDefeated++
                consecutiveWins++

                println("   Chip reward: +$chipReward chips")
                println("   Experience: +$expReward XP (Total: $experience)")
                println("   Total chips: $playerChips")

                // Check for level up
                checkLevelUp()

                // Chance to capture the monster
                if (attemptCapture(enemy, result.handStrength)) {
                    playerCollection.addMonster(enemy)
                    println("   🎁 Bonus: ${enemy.name} joined your collection!")
                }
                
                // Generate dynamic quests
                questSystem.generateDynamicQuests(currentLevel, monstersDefeated)

            } else {
                println("💀 Defeat! The ${enemy.name} was too strong.")
                val chipsLost = min(playerChips / 4, 100) // Lose 25% or max 100
                playerChips -= chipsLost
                consecutiveWins = 0
                
                println("   You lost $chipsLost chips in the retreat.")
                println("   Remaining chips: $playerChips")
            }
            
            println()

            if (playerChips <= 0) {
                println("💀 Game Over! You've run out of chips for battle.")
                break
            }

            continueAdventure = promptContinue()
        }

        showAdventureStats()
    }
    
    /**
     * Displays current adventure status
     */
    private fun displayAdventureStatus(playerChips: Int) {
        println("\n" + "=".repeat(50))
        println("🏔️ ADVENTURE STATUS - Level $currentLevel")
        println("Chips: $playerChips | XP: $experience/${getExpToNextLevel()}")
        println("Monsters defeated: $monstersDefeated | Win streak: $consecutiveWins")
        println("Collection: ${playerCollection.getMonsterCount()} monsters")
        println("=".repeat(50))
    }
    
    /**
     * Calculates experience reward
     */
    private fun calculateExpReward(enemy: Monster): Int {
        val baseExp = when (enemy.rarity) {
            Monster.Rarity.COMMON -> 20
            Monster.Rarity.UNCOMMON -> 35
            Monster.Rarity.RARE -> 60
            Monster.Rarity.EPIC -> 100
            Monster.Rarity.LEGENDARY -> 200
        }
        return baseExp + (currentLevel * 5)
    }
    
    /**
     * Gets experience needed for next level
     */
    private fun getExpToNextLevel(): Int {
        return LEVEL_UP_EXP * currentLevel
    }
    
    /**
     * Checks and handles level ups
     */
    private fun checkLevelUp() {
        val expNeeded = getExpToNextLevel()
        if (experience >= expNeeded) {
            currentLevel++
            experience -= expNeeded
            
            println("\n🆙 LEVEL UP! You are now level $currentLevel!")
            println("   New monsters and challenges await!")
            
            // Level up rewards
            val levelReward = currentLevel * 50
            println("   Level bonus: +$levelReward chips")
            
            // Restore some health (represented as extra chips)
            val healthBonus = 100
            println("   Health restored: +$healthBonus chips")
        }
    }

    /**
     * Generates an enemy monster based on current level.
     */
    private fun generateEnemyMonster(): Monster {
        val (rarity, name, baseHealth) =
            when {
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
            description = "A wild monster encountered in adventure mode",
        )
    }

    /**
     * Conducts a poker battle between player and monster.
     */
    private fun battleMonster(
        enemy: Monster,
        playerChips: Int,
    ): BattleResult {
        println("⚔️ POKER BATTLE COMMENCES! ⚔️")
        println("Draw your hand and prepare for battle!")
        println()

        // Create a simple poker hand for the player using modern game engine
        val gameEngine =
            GameEngine(
                gameConfig =
                    Game(
                        gameMode = GameMode.ADVENTURE,
                        startingChips = playerChips,
                    ),
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
    private fun calculateDamage(
        handValue: Int,
        enemy: Monster,
    ): Int {
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
    private fun attemptCapture(
        enemy: Monster,
        handStrength: Int,
    ): Boolean {
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
        println("Choose your action:")
        println("1. Continue adventure")
        println("2. View quests")
        println("3. View collection") 
        println("4. Rest (save and exit)")
        print("Select option (1-4) [1]: ")
        
        val input = readlnOrNull()?.trim() ?: "1"
        
        return when (input) {
            "2" -> {
                showQuestStatus()
                true
            }
            "3" -> {
                showCollection()
                true
            }
            "4" -> {
                println("💾 Adventure progress saved. Thanks for playing!")
                false
            }
            else -> true // Continue adventure
        }
    }
    
    /**
     * Shows current quest status
     */
    private fun showQuestStatus() {
        println("\n" + questSystem.getQuestSummary())
        print("Press Enter to continue...")
        readlnOrNull()
    }
    
    /**
     * Shows monster collection
     */
    private fun showCollection() {
        println("\n🎴 MONSTER COLLECTION")
        println("=".repeat(30))
        
        if (playerCollection.getMonsterCount() > 0) {
            val monsters = playerCollection.getOwnedMonsters()
            val rarityGroups = monsters.groupBy { it.rarity }
            
            Monster.Rarity.values().forEach { rarity ->
                val count = rarityGroups[rarity]?.size ?: 0
                if (count > 0) {
                    println("${rarity.displayName}: $count monsters")
                    rarityGroups[rarity]?.forEach { monster ->
                        println("  - ${monster.name}")
                    }
                }
            }
            
            val totalPower = monsters.sumOf { it.effectPower }
            println("\nTotal Collection Power: $totalPower")
        } else {
            println("No monsters captured yet. Keep battling!")
        }
        
        print("\nPress Enter to continue...")
        readlnOrNull()
    }

    /**
     * Displays final adventure statistics.
     */
    private fun showAdventureStats() {
        println("\n🏆 ADVENTURE COMPLETE! 🏆")
        println("=".repeat(40))
        println("Final Level: $currentLevel")
        println("Experience: $experience")
        println("Monsters defeated: $monstersDefeated")
        println("Highest level reached: $currentLevel")
        println("Monsters captured: ${playerCollection.getMonsterCount()}")
        println("Best win streak: $consecutiveWins")
        
        // Show quest completion stats
        val completedCount = questSystem.getActiveQuests().size
        println("Quests completed: $completedCount")
        
        println()

        if (playerCollection.getMonsterCount() > 0) {
            println("Your Monster Collection:")
            println("  Total monsters: ${playerCollection.getMonsterCount()}")
            val rarityGroups = playerCollection.getOwnedMonsters().groupBy { it.rarity }
            
            Monster.Rarity.values().forEach { rarity ->
                val count = rarityGroups[rarity]?.size ?: 0
                if (count > 0) {
                    println("  ${rarity.displayName}: $count")
                }
            }
            
            // Show most powerful monsters
            val topMonsters = playerCollection.getOwnedMonsters()
                .sortedByDescending { it.effectPower }
                .take(3)
                
            if (topMonsters.isNotEmpty()) {
                println("\nStrongest Companions:")
                topMonsters.forEachIndexed { index, monster ->
                    val rank = when (index) {
                        0 -> "👑"
                        1 -> "⭐"
                        2 -> "🌟"
                        else -> "  "
                    }
                    println("  $rank ${monster.name} (${monster.rarity.displayName}) - Power: ${monster.effectPower}")
                }
            }
        }
        
        // Calculate final score
        val score = calculateAdventureScore()
        println("\nFinal Adventure Score: $score")
        
        when {
            score >= 5000 -> println("🏅 RANK: LEGENDARY ADVENTURER")
            score >= 3000 -> println("🏅 RANK: MASTER ADVENTURER")
            score >= 1500 -> println("🏅 RANK: EXPERT ADVENTURER")
            score >= 800 -> println("🏅 RANK: SKILLED ADVENTURER")
            score >= 400 -> println("🏅 RANK: BRAVE ADVENTURER")
            else -> println("🏅 RANK: NOVICE ADVENTURER")
        }

        println("\nThank you for playing Adventure Mode!")
    }
    
    /**
     * Calculates final adventure score
     */
    private fun calculateAdventureScore(): Int {
        var score = 0
        score += monstersDefeated * 50
        score += currentLevel * 100
        score += experience
        score += playerCollection.getMonsterCount() * 100
        score += playerCollection.getOwnedMonsters().sumOf { it.rarity.powerMultiplier.toInt() * 50 }
        return score
    }

    /**
     * Result of a battle encounter.
     */
    private data class BattleResult(
        val victory: Boolean,
        val handStrength: Int,
        val damageDealt: Int,
    )
}
