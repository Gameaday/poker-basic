package com.pokermon.modes.safari

import com.pokermon.Game
import com.pokermon.GameEngine
import com.pokermon.GameMode
import com.pokermon.HandEvaluator
import com.pokermon.database.Monster
import com.pokermon.database.MonsterBattleSystem
import com.pokermon.database.MonsterCollection
import com.pokermon.database.MonsterStats
import com.pokermon.modern.CardUtils
import com.pokermon.players.PlayerProfile
import kotlin.random.Random

/**
 * Safari Mode implementation - Capture wild monsters through poker gameplay.
 * Players must use skill and luck to catch rare monsters in the wild.
 * Success depends on poker hand strength, safari balls, and environmental factors.
 *
 * @author Pokermon Safari Mode System
 * @version 1.0.0
 */
class SafariGameMode(
    private val playerName: String,
    private val initialChips: Int,
    private val safariBalls: Int = 30,
) {
    private val playerCollection = MonsterCollection()
    private val battleSystem = MonsterBattleSystem()
    private val random = Random.Default
    private var ballsRemaining = safariBalls
    private var encounterCount = 0
    private var capturesSuccessful = 0
    private var escapedMonsters = 0

    // Environmental factors
    private var currentWeather = WeatherCondition.CLEAR
    private var timeOfDay = TimeOfDay.DAY
    private var terrainType = TerrainType.GRASSLAND

    /**
     * Starts the Safari Mode gameplay loop.
     */
    fun startSafari() {
        println("🏕️ Welcome to Safari Mode! 🏕️")
        println("You have $ballsRemaining safari balls to catch monsters.")
        println("Use poker skills and strategy to improve capture chances!")
        println()

        var continueAdventure = true
        var playerChips = initialChips

        while (continueAdventure && ballsRemaining > 0) {
            // Generate random environment
            updateEnvironment()

            val wildMonster = generateWildMonster()

            println("🌿 In the ${terrainType.displayName} (${currentWeather.displayName}, ${timeOfDay.displayName})")
            println("🐾 A wild ${wildMonster.name} appears!")
            println("   Rarity: ${wildMonster.rarity.displayName}")
            println("   Base Capture Rate: ${String.format("%.1f%%", wildMonster.baseCaptureRate * 100)}")
            println("   Balls remaining: $ballsRemaining")
            println()

            val encounterResult = attemptCapture(wildMonster, playerChips)

            when (encounterResult.outcome) {
                CaptureOutcome.SUCCESS -> {
                    println("🎉 Success! You caught ${wildMonster.name}!")
                    playerCollection.addMonster(wildMonster.toMonster())
                    capturesSuccessful++

                    // Bonus chips for rare captures
                    val bonusChips = calculateCaptureReward(wildMonster)
                    if (bonusChips > 0) {
                        playerChips += bonusChips
                        println("   Bonus: +$bonusChips chips for rare capture!")
                    }
                }
                CaptureOutcome.ESCAPED -> {
                    println("💨 The ${wildMonster.name} escaped!")
                    escapedMonsters++
                }
                CaptureOutcome.FAILED -> {
                    println("❌ The capture failed but ${wildMonster.name} didn't escape.")
                }
                CaptureOutcome.OUT_OF_BALLS -> {
                    println("😞 Out of safari balls! Safari ended.")
                    break
                }
            }

            ballsRemaining--
            encounterCount++

            if (ballsRemaining <= 0) {
                println("⚠️ No more safari balls remaining!")
                break
            }

            continueAdventure = promptContinue()
        }

        showSafariStats()
    }

    /**
     * Updates environmental conditions that affect capture rates
     */
    private fun updateEnvironment() {
        // Change weather occasionally
        if (random.nextInt(100) < 20) {
            currentWeather = WeatherCondition.values().random()
        }

        // Change time of day
        if (random.nextInt(100) < 30) {
            timeOfDay = if (timeOfDay == TimeOfDay.DAY) TimeOfDay.NIGHT else TimeOfDay.DAY
        }

        // Change terrain occasionally
        if (random.nextInt(100) < 15) {
            terrainType = TerrainType.values().random()
        }
    }

    /**
     * Generates a wild monster based on environmental factors
     */
    private fun generateWildMonster(): WildMonster {
        val (rarity, baseName, captureRate) =
            when {
                // Rare spawns in specific conditions
                currentWeather == WeatherCondition.STORM && timeOfDay == TimeOfDay.NIGHT && random.nextInt(100) < 5 -> {
                    Triple(Monster.Rarity.LEGENDARY, "Storm Dragon", 0.05)
                }
                terrainType == TerrainType.CAVE && random.nextInt(100) < 10 -> {
                    Triple(Monster.Rarity.EPIC, "Crystal Bat", 0.15)
                }
                timeOfDay == TimeOfDay.NIGHT && random.nextInt(100) < 25 -> {
                    Triple(Monster.Rarity.RARE, "Night Prowler", 0.25)
                }
                currentWeather == WeatherCondition.RAIN && random.nextInt(100) < 40 -> {
                    Triple(Monster.Rarity.UNCOMMON, "Rain Sprite", 0.40)
                }
                else -> {
                    Triple(Monster.Rarity.COMMON, "Wild Creature", 0.60)
                }
            }

        val environmentName =
            when (terrainType) {
                TerrainType.GRASSLAND -> "Meadow $baseName"
                TerrainType.FOREST -> "Forest $baseName"
                TerrainType.MOUNTAIN -> "Mountain $baseName"
                TerrainType.CAVE -> "Cave $baseName"
                TerrainType.WATER -> "River $baseName"
            }

        return WildMonster(
            name = environmentName,
            rarity = rarity,
            baseCaptureRate = captureRate,
            preferredWeather = currentWeather,
            preferredTerrain = terrainType,
            behavior = determineBehavior(rarity),
        )
    }

    /**
     * Determines monster behavior based on rarity
     */
    private fun determineBehavior(rarity: Monster.Rarity): MonsterBehavior {
        return when (rarity) {
            Monster.Rarity.COMMON -> if (random.nextBoolean()) MonsterBehavior.DOCILE else MonsterBehavior.NEUTRAL
            Monster.Rarity.UNCOMMON -> MonsterBehavior.NEUTRAL
            Monster.Rarity.RARE -> if (random.nextBoolean()) MonsterBehavior.NEUTRAL else MonsterBehavior.AGGRESSIVE
            Monster.Rarity.EPIC -> MonsterBehavior.AGGRESSIVE
            Monster.Rarity.LEGENDARY -> MonsterBehavior.LEGENDARY
        }
    }

    /**
     * Attempts to capture a wild monster through poker gameplay
     */
    private fun attemptCapture(
        monster: WildMonster,
        playerChips: Int,
    ): CaptureResult {
        if (ballsRemaining <= 0) {
            return CaptureResult(CaptureOutcome.OUT_OF_BALLS, 0)
        }

        println("⚔️ SAFARI CAPTURE ATTEMPT! ⚔️")
        println("Draw your hand to determine capture success!")
        println()

        // Create a poker hand using the game engine
        val gameEngine =
            GameEngine(
                gameConfig =
                    Game(
                        gameMode = GameMode.SAFARI,
                        startingChips = playerChips,
                    ),
            )

        gameEngine.initializeGame(arrayOf(playerName))
        val hand = gameEngine.players!![0].hand

        // Evaluate hand for capture power
        val handResult = HandEvaluator.evaluateHand(hand)
        val captureStrength = handResult.score

        // Display capture hand
        println("Your safari hand:")
        hand.forEachIndexed { index, card ->
            println("  ${index + 1}: ${CardUtils.cardName(card)}")
        }
        println()
        println("Capture strength: $captureStrength (${handResult.description})")

        // Calculate final capture chance
        val finalCaptureChance = calculateCaptureChance(monster, captureStrength)

        println("Final capture chance: ${String.format("%.1f%%", finalCaptureChance * 100)}")
        println("🎯 Throwing Safari Ball...")
        Thread.sleep(1500) // Build suspense

        val success = random.nextDouble() < finalCaptureChance

        return if (success) {
            CaptureResult(CaptureOutcome.SUCCESS, captureStrength)
        } else {
            // Determine if monster escapes
            val escapeChance =
                when (monster.behavior) {
                    MonsterBehavior.DOCILE -> 0.1
                    MonsterBehavior.NEUTRAL -> 0.3
                    MonsterBehavior.AGGRESSIVE -> 0.5
                    MonsterBehavior.LEGENDARY -> 0.7
                    MonsterBehavior.RARE -> 0.4
                }

            val escaped = random.nextDouble() < escapeChance
            val outcome = if (escaped) CaptureOutcome.ESCAPED else CaptureOutcome.FAILED

            CaptureResult(outcome, captureStrength)
        }
    }

    /**
     * Calculates the final capture chance based on multiple factors
     */
    private fun calculateCaptureChance(
        monster: WildMonster,
        handStrength: Int,
    ): Double {
        var captureChance = monster.baseCaptureRate

        // Hand strength bonus (stronger hands improve chances)
        val handBonus = (handStrength / 999.0) * 0.3 // Up to 30% bonus
        captureChance += handBonus

        // Environmental bonuses
        if (currentWeather == monster.preferredWeather) {
            captureChance += 0.1 // 10% bonus for preferred weather
        }

        if (terrainType == monster.preferredTerrain) {
            captureChance += 0.1 // 10% bonus for preferred terrain
        }

        // Time of day effects
        when (timeOfDay) {
            TimeOfDay.DAY -> if (monster.rarity == Monster.Rarity.COMMON) captureChance += 0.05
            TimeOfDay.NIGHT -> if (monster.rarity in listOf(Monster.Rarity.RARE, Monster.Rarity.EPIC)) captureChance += 0.05
        }

        // Weather effects
        when (currentWeather) {
            WeatherCondition.CLEAR -> captureChance += 0.02
            WeatherCondition.RAIN -> captureChance -= 0.05 // Harder to see
            WeatherCondition.STORM -> captureChance -= 0.1 // Much harder
            WeatherCondition.FOG -> captureChance -= 0.07 // Visibility issues
        }

        // Behavior effects
        when (monster.behavior) {
            MonsterBehavior.DOCILE -> captureChance += 0.15
            MonsterBehavior.NEUTRAL -> { /* no change */ }
            MonsterBehavior.AGGRESSIVE -> captureChance -= 0.1
            MonsterBehavior.LEGENDARY -> captureChance -= 0.2
            MonsterBehavior.RARE -> captureChance -= 0.05
        }

        // Ensure capture chance stays within reasonable bounds
        return captureChance.coerceIn(0.01, 0.95)
    }

    // Overload for Monster type (simplified calculation)
    private fun calculateCaptureChance(
        monster: Monster,
        handStrength: Int,
    ): Double {
        // Base capture rate based on rarity
        var captureChance =
            when (monster.rarity) {
                Monster.Rarity.COMMON -> 0.7
                Monster.Rarity.UNCOMMON -> 0.5
                Monster.Rarity.RARE -> 0.3
                Monster.Rarity.EPIC -> 0.2
                Monster.Rarity.LEGENDARY -> 0.1
            }

        // Hand strength bonus
        val handBonus = (handStrength / 999.0) * 0.3
        captureChance += handBonus

        return captureChance.coerceIn(0.05, 0.95)
    }

    /**
     * Calculates chip reward for successful captures
     */
    private fun calculateCaptureReward(monster: WildMonster): Int {
        return when (monster.rarity) {
            Monster.Rarity.COMMON -> 0
            Monster.Rarity.UNCOMMON -> 25
            Monster.Rarity.RARE -> 100
            Monster.Rarity.EPIC -> 300
            Monster.Rarity.LEGENDARY -> 1000
        }
    }

    /**
     * Prompts player to continue safari
     */
    private fun promptContinue(): Boolean {
        if (ballsRemaining <= 0) return false

        print("Continue safari? (y/n) [y]: ")
        val input = readlnOrNull()?.trim()?.lowercase() ?: ""
        return input.isEmpty() || input == "y" || input == "yes"
    }

    /**
     * Shows final safari statistics
     */
    private fun showSafariStats() {
        println("\n🏆 SAFARI COMPLETE! 🏆")
        println("=".repeat(40))
        println("Total encounters: $encounterCount")
        println("Successful captures: $capturesSuccessful")
        println("Monsters escaped: $escapedMonsters")
        println("Safari balls used: ${safariBalls - ballsRemaining}")

        val successRate = if (encounterCount > 0) (capturesSuccessful * 100.0) / encounterCount else 0.0
        println("Success rate: ${String.format("%.1f%%", successRate)}")
        println()

        if (playerCollection.getMonsterCount() > 0) {
            println("Captured Monsters:")
            playerCollection.getOwnedMonsters().forEach { monster ->
                println("  - ${monster.name} (${monster.rarity.displayName})")
            }
        } else {
            println("No monsters captured this safari. Better luck next time!")
        }

        println("\nThank you for playing Safari Mode!")
    }

    /**
     * Integrates with comprehensive monster battle system for Safari encounters
     */
    fun triggerSafariEncounter(
        playerProfile: PlayerProfile,
        wildMonster: Monster,
        handStrength: Int,
    ): SafariBattleResult {
        val playerMonster = playerProfile.monsterCollection.getActiveMonster()

        if (playerMonster != null) {
            // Safari mode uses brief battles to weaken monsters before capture
            val battleResult =
                battleSystem.executeBattle(
                    playerMonster,
                    wildMonster.copy(stats = wildMonster.stats.copy(baseHp = wildMonster.stats.effectiveHp / 2)), // Weaken for capture
                    handStrength,
                )

            // Calculate capture bonus based on battle performance
            val captureBonus = if (battleResult.winner == playerMonster) 0.3f else 0.0f
            val baseCaptureRate = calculateCaptureChance(wildMonster, handStrength)
            val finalCaptureRate = (baseCaptureRate + captureBonus).coerceAtMost(0.9)

            val captured = random.nextFloat() < finalCaptureRate

            return SafariBattleResult(
                battleResult = battleResult,
                captured = captured,
                captureRate = finalCaptureRate.toFloat(),
                wildMonster = wildMonster,
            )
        } else {
            // No monster to battle with - use traditional capture mechanics
            val baseCaptureRate = calculateCaptureChance(wildMonster, handStrength)
            val captured = random.nextFloat() < baseCaptureRate

            return SafariBattleResult(
                battleResult = null,
                captured = captured,
                captureRate = baseCaptureRate.toFloat(),
                wildMonster = wildMonster,
            )
        }
    }

    /**
     * Train safari monster for improved stats
     */
    fun trainSafariMonster(
        monster: Monster,
        rounds: Int = 3,
    ): Monster {
        var trainedMonster = monster
        repeat(rounds) {
            trainedMonster =
                trainedMonster.copy(
                    stats =
                        trainedMonster.stats.copy(
                            baseSpeed = trainedMonster.stats.baseSpeed + 4, // Tracking and agility
                            baseDefense = trainedMonster.stats.baseDefense + 3, // Environmental resistance
                            baseSpecial = trainedMonster.stats.baseSpecial + 2, // Monster sensing
                            baseAttack = trainedMonster.stats.baseAttack + 1, // Controlled force
                        ),
                )
        }
        return trainedMonster
    }
}

/**
 * Environmental conditions that affect monster spawns and capture rates
 */
enum class WeatherCondition(val displayName: String) {
    CLEAR("Clear"),
    RAIN("Rain"),
    STORM("Storm"),
    FOG("Fog"),
}

enum class TimeOfDay(val displayName: String) {
    DAY("Day"),
    NIGHT("Night"),
}

enum class TerrainType(val displayName: String) {
    GRASSLAND("Grassland"),
    FOREST("Forest"),
    MOUNTAIN("Mountain"),
    CAVE("Cave"),
    WATER("Water"),
}

/**
 * Monster behavior patterns affecting capture difficulty
 */
enum class MonsterBehavior {
    DOCILE, // Easy to capture
    NEUTRAL, // Standard behavior
    AGGRESSIVE, // Hard to capture, likely to escape
    RARE, // Special rare behavior
    LEGENDARY, // Extremely difficult legendary behavior
}

/**
 * Wild monster with safari-specific properties
 */
data class WildMonster(
    val name: String,
    val rarity: Monster.Rarity,
    val baseCaptureRate: Double,
    val preferredWeather: WeatherCondition,
    val preferredTerrain: TerrainType,
    val behavior: MonsterBehavior,
) {
    fun toMonster(): Monster {
        val stats =
            MonsterStats(
                baseHp = 80 + (rarity.ordinal * 20),
                baseAttack = 50 + (rarity.ordinal * 10),
                baseDefense = 45 + (rarity.ordinal * 8),
                baseSpeed = 60 + (rarity.ordinal * 15),
                baseSpecial = 55 + (rarity.ordinal * 12),
            )

        return Monster(
            name = name,
            rarity = rarity,
            baseHealth = stats.baseHp,
            effectType = Monster.EffectType.CARD_ADVANTAGE,
            effectPower = rarity.powerMultiplier.toInt() * 10,
            description = "A wild monster captured in safari mode",
            stats = stats,
        )
    }
}

/**
 * Safari battle result with comprehensive capture mechanics
 */
data class SafariBattleResult(
    val battleResult: com.pokermon.database.BattleResult?,
    val captured: Boolean,
    val captureRate: Float,
    val wildMonster: Monster,
)

/**
 * Possible outcomes of a capture attempt
 */
enum class CaptureOutcome {
    SUCCESS, // Monster captured successfully
    FAILED, // Capture failed but monster didn't escape
    ESCAPED, // Monster escaped
    OUT_OF_BALLS, // No more safari balls
}

/**
 * Result of a capture attempt
 */
data class CaptureResult(
    val outcome: CaptureOutcome,
    val captureStrength: Int,
)
