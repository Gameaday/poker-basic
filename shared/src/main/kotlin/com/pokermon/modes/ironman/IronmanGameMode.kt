package com.pokermon.modes.ironman

import com.pokermon.*
import com.pokermon.database.Monster
import com.pokermon.database.MonsterCollection
import com.pokermon.modern.CardUtils
import com.pokermon.modes.Achievement
import com.pokermon.modes.GameContext
import com.pokermon.modes.MonsterEffect
import com.pokermon.modes.PlayerHandResult
import com.pokermon.modes.RoundResult
import com.pokermon.players.Player
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Ironman Mode implementation - High-risk poker with gacha mechanics and permadeath.
 * Players convert poker winnings into gacha pulls for rare monster rewards.
 * Features permadeath system where losing all chips ends the run permanently.
 *
 * @author Pokermon Ironman Mode System  
 * @version 1.0.0
 */
class IronmanGameMode(
    private val playerName: String,
    private val initialChips: Int,
    private val difficultyLevel: Int = 3 // 1=Easy, 2=Normal, 3=Hard, 4=Nightmare
) {
    private val playerCollection = MonsterCollection()
    private val random = Random.Default
    
    // Ironman progression tracking
    private var currentLevel = 1
    private var gachaPoints = 0
    private var totalWinnings = 0
    private var totalLosses = 0
    private var roundsSurvived = 0
    private var deathCount = 0
    private var survivedDeaths = 0
    private var currentStreak = 0
    private var bestStreak = 0
    
    // Risk management
    private var riskLevel = 1.0
    private var permadeathThreshold = 0
    private var hasPermadeath = true
    
    // Gacha system
    private var guaranteeCounter = 0
    private var pityTimer = 0
    
    companion object {
        // Difficulty multipliers
        private val DIFFICULTY_MULTIPLIERS = mapOf(
            1 to 0.7,  // Easy - 30% less risk
            2 to 1.0,  // Normal - Standard risk
            3 to 1.3,  // Hard - 30% more risk  
            4 to 1.6   // Nightmare - 60% more risk
        )
        
        // Gacha rates (base rates)
        private val GACHA_RATES = mapOf(
            Monster.Rarity.COMMON to 0.60,
            Monster.Rarity.UNCOMMON to 0.25,
            Monster.Rarity.RARE to 0.10,
            Monster.Rarity.EPIC to 0.04,
            Monster.Rarity.LEGENDARY to 0.01
        )
        
        private const val PITY_TIMER_MAX = 50 // Guaranteed rare after 50 pulls
        private const val GUARANTEE_THRESHOLD = 100 // Guaranteed legendary after 100 pulls
    }

    /**
     * Starts the Ironman Mode gameplay loop.
     */
    fun startIronman() {
        println("⚡ IRONMAN MODE - HIGH STAKES SURVIVAL ⚡")
        println("Difficulty: ${getDifficultyName(difficultyLevel)}")
        println("Permadeath: ${if (hasPermadeath) "ENABLED" else "DISABLED"}")
        println("Starting chips: $initialChips")
        println()
        
        displayIronmanRules()
        
        var continueRun = true
        var playerChips = initialChips
        
        while (continueRun && playerChips > 0) {
            println("\n" + "=".repeat(50))
            println("           IRONMAN RUN - LEVEL $currentLevel")
            println("Chips: $playerChips | Gacha Points: $gachaPoints | Risk: ${String.format("%.1fx", riskLevel)}")
            println("Streak: $currentStreak | Best: $bestStreak | Deaths Survived: $survivedDeaths")
            println("=".repeat(50))
            
            val roundResult = playPokerRound(playerChips)
            
            when (roundResult.outcome) {
                RoundOutcome.VICTORY -> {
                    val winnings = roundResult.chipsGained
                    playerChips += winnings
                    totalWinnings += winnings
                    currentStreak++
                    bestStreak = maxOf(bestStreak, currentStreak)
                    roundsSurvived++
                    
                    // Convert winnings to gacha points
                    val pointsEarned = calculateGachaPoints(winnings)
                    gachaPoints += pointsEarned
                    
                    println("🎉 Victory! +$winnings chips, +$pointsEarned gacha points")
                    
                    // Level up and increase risk
                    if (roundsSurvived % 5 == 0) {
                        levelUp()
                    }
                }
                
                RoundOutcome.DEFEAT -> {
                    val losses = roundResult.chipsLost
                    playerChips = maxOf(0, playerChips - losses)
                    totalLosses += losses
                    currentStreak = 0
                    
                    println("💀 Defeat! -$losses chips")
                    
                    // Check for permadeath
                    if (playerChips <= 0 && hasPermadeath) {
                        if (!attemptRevive()) {
                            println("⚰️ PERMADEATH! Your ironman run has ended.")
                            deathCount++
                            break
                        } else {
                            playerChips = (initialChips * 0.25).toInt() // Revive with 25% chips
                            survivedDeaths++
                            println("💊 Revived with $playerChips chips!")
                        }
                    }
                }
                
                RoundOutcome.DRAW -> {
                    println("🤝 Draw - no chips gained or lost")
                }
            }
            
            // Post-round options
            if (playerChips > 0) {
                continueRun = handlePostRoundOptions(playerChips)
            }
        }
        
        showIronmanStats()
    }
    
    /**
     * Displays the Ironman mode rules and mechanics
     */
    private fun displayIronmanRules() {
        println("📋 IRONMAN MODE RULES:")
        println("• High stakes poker with escalating difficulty")
        println("• Convert winnings to gacha points for monster rewards")
        println("• Permadeath: Losing all chips ends your run permanently")
        println("• Risk multiplier increases as you progress")
        println("• Rare monsters provide powerful abilities")
        println("• Use revival items wisely - they're limited!")
        println()
        
        print("Ready to begin your ironman run? (y/n) [y]: ")
        val input = readlnOrNull()?.trim()?.lowercase() ?: ""
        if (input == "n" || input == "no") {
            println("Maybe next time. Good luck!")
            return
        }
    }
    
    /**
     * Plays a single poker round with ironman mechanics
     */
    private fun playPokerRound(playerChips: Int): IronmanRoundResult {
        println("\n⚔️ IRONMAN POKER ROUND ⚔️")
        println("Risk level: ${String.format("%.1fx", riskLevel)}")
        
        // Create enhanced AI opponents based on level
        val aiOpponentCount = min(3, 1 + (currentLevel / 3))
        val difficultyMultiplier = DIFFICULTY_MULTIPLIERS[difficultyLevel] ?: 1.0
        
        // Create game engine with ironman settings
        val gameEngine = GameEngine(
            gameConfig = Game(
                gameMode = GameMode.IRONMAN,
                startingChips = playerChips,
                difficultyLevel = (difficultyLevel * difficultyMultiplier).toInt(),
            )
        )
        
        // Initialize with player and AI
        val players = arrayOf(playerName) + Array(aiOpponentCount) { "IronmanAI-${it + 1}" }
        gameEngine.initializeGame(players)
        
        val hand = gameEngine.players!![0].hand
        val handResult = HandEvaluator.evaluateHand(hand)
        
        println("Your ironman hand:")
        hand.forEachIndexed { index, card ->
            println("  ${index + 1}: ${CardUtils.cardName(card)}")
        }
        println("Hand strength: ${handResult.score} (${handResult.description})")
        
        // Calculate stakes based on risk level and hand strength  
        val baseStake = (playerChips * 0.1 * riskLevel).toInt()
        val adjustedStake = adjustStakeByHand(baseStake, handResult)
        
        println("Stakes: $adjustedStake chips (${String.format("%.1f%%", (adjustedStake.toDouble() / playerChips) * 100)} of total)")
        
        // Simulate ironman poker round
        val opponentStrength = generateOpponentStrength()
        val victory = handResult.score > opponentStrength
        
        val chipsChange = if (victory) {
            (adjustedStake * (1.5 + (riskLevel - 1.0))).toInt()
        } else {
            -adjustedStake
        }
        
        return IronmanRoundResult(
            outcome = when {
                victory -> RoundOutcome.VICTORY
                chipsChange < 0 -> RoundOutcome.DEFEAT  
                else -> RoundOutcome.DRAW
            },
            chipsGained = maxOf(0, chipsChange),
            chipsLost = maxOf(0, -chipsChange),
            handStrength = handResult.score,
            opponentStrength = opponentStrength
        )
    }
    
    /**
     * Adjusts stake based on hand strength (risk management)
     */
    private fun adjustStakeByHand(baseStake: Int, handResult: HandEvaluator.HandResult): Int {
        val handStrengthFactor = when (handResult.handType) {
            HandEvaluator.HandType.HIGH_CARD -> 0.5
            HandEvaluator.HandType.ONE_PAIR -> 0.7
            HandEvaluator.HandType.TWO_PAIR -> 1.0
            HandEvaluator.HandType.THREE_OF_A_KIND -> 1.3
            HandEvaluator.HandType.STRAIGHT -> 1.5
            HandEvaluator.HandType.FLUSH -> 1.7
            HandEvaluator.HandType.FULL_HOUSE -> 2.0
            HandEvaluator.HandType.FOUR_OF_A_KIND -> 2.5
            HandEvaluator.HandType.STRAIGHT_FLUSH -> 3.0
            HandEvaluator.HandType.ROYAL_FLUSH -> 4.0
        }
        
        return (baseStake * handStrengthFactor).toInt()
    }
    
    /**
     * Generates opponent strength based on current level and difficulty
     */
    private fun generateOpponentStrength(): Int {
        val baseStrength = 300 + (currentLevel * 50)
        val difficultyBonus = (difficultyLevel - 1) * 100
        val randomness = random.nextInt(-100, 201) // ±100 randomness
        
        return baseStrength + difficultyBonus + randomness
    }
    
    /**
     * Levels up and increases difficulty/risk
     */
    private fun levelUp() {
        currentLevel++
        val oldRisk = riskLevel
        riskLevel = min(3.0, 1.0 + (currentLevel * 0.1))
        
        println("\n🆙 LEVEL UP! Level $currentLevel")
        println("Risk multiplier: ${String.format("%.1fx", oldRisk)} → ${String.format("%.1fx", riskLevel)}")
        
        // Bonus gacha points for leveling up
        val levelBonus = currentLevel * 10
        gachaPoints += levelBonus
        println("Level bonus: +$levelBonus gacha points")
    }
    
    /**
     * Calculates gacha points earned from winnings
     */
    private fun calculateGachaPoints(winnings: Int): Int {
        // Better conversion rate at higher levels
        val conversionRate = 0.1 + (currentLevel * 0.01)
        return (winnings * conversionRate).toInt()
    }
    
    /**
     * Attempts to revive using gacha points or rare items
     */
    private fun attemptRevive(): Boolean {
        println("\n💀 PERMADEATH IMMINENT!")
        println("Options:")
        println("1. Spend ${500 + (deathCount * 200)} gacha points for revival")
        println("2. Use Phoenix Feather (if owned)")
        println("3. Accept death and end run")
        
        val reviveCost = 500 + (deathCount * 200)
        
        print("Choose option (1-3): ")
        val choice = readlnOrNull()?.trim() ?: "3"
        
        return when (choice) {
            "1" -> {
                if (gachaPoints >= reviveCost) {
                    gachaPoints -= reviveCost
                    println("💚 Revival successful! Gacha points: $gachaPoints")
                    true
                } else {
                    println("❌ Insufficient gacha points (need $reviveCost, have $gachaPoints)")
                    false
                }
            }
            "2" -> {
                // Check for Phoenix Feather in collection
                val hasFeather = playerCollection.getOwnedMonsters().any { 
                    it.name.contains("Phoenix") || it.effectType == Monster.EffectType.DEFENSIVE_SHIELD 
                }
                if (hasFeather) {
                    println("🔥 Phoenix Feather used! Rising from the ashes!")
                    // Remove one phoenix from collection
                    true
                } else {
                    println("❌ No Phoenix Feather found in collection")
                    false
                }
            }
            else -> false
        }
    }
    
    /**
     * Handles post-round player options
     */
    private fun handlePostRoundOptions(playerChips: Int): Boolean {
        println("\n🎯 POST-ROUND OPTIONS:")
        println("1. Continue ironman run")
        println("2. Gacha pull ($100 points per pull)")
        println("3. View collection and stats")
        println("4. Retire (save progress and exit)")
        
        print("Choose option (1-4) [1]: ")
        val choice = readlnOrNull()?.trim() ?: "1"
        
        return when (choice) {
            "2" -> {
                performGachaPull()
                true
            }
            "3" -> {
                displayCollectionAndStats()
                true  
            }
            "4" -> {
                println("💾 Progress saved. Thanks for playing Ironman Mode!")
                false
            }
            else -> true // Continue run
        }
    }
    
    /**
     * Performs a gacha pull for monster rewards
     */
    private fun performGachaPull() {
        val pullCost = 100
        val maxPulls = gachaPoints / pullCost
        
        if (maxPulls <= 0) {
            println("❌ Insufficient gacha points! Need $pullCost points per pull.")
            return
        }
        
        println("\n🎰 GACHA PULL SYSTEM 🎰")
        println("Available pulls: $maxPulls")
        println("Pity timer: $pityTimer/$PITY_TIMER_MAX")
        println("Guarantee counter: $guaranteeCounter/$GUARANTEE_THRESHOLD")
        
        print("How many pulls? (1-$maxPulls) [1]: ")
        val pullCount = try {
            readlnOrNull()?.trim()?.toIntOrNull()?.coerceIn(1, maxPulls) ?: 1
        } catch (e: Exception) {
            1
        }
        
        println("\n🎯 Performing $pullCount gacha pulls...")
        gachaPoints -= pullCount * pullCost
        
        repeat(pullCount) { pullIndex ->
            Thread.sleep(1000) // Build suspense
            val monster = performSinglePull()
            println("Pull ${pullIndex + 1}: ${monster.name} (${monster.rarity.displayName})!")
            playerCollection.addMonster(monster)
        }
        
        println("\nRemaining gacha points: $gachaPoints")
    }
    
    /**
     * Performs a single gacha pull with pity system
     */
    private fun performSinglePull(): Monster {
        pityTimer++
        guaranteeCounter++
        
        // Guarantee system
        val guaranteedRarity = when {
            guaranteeCounter >= GUARANTEE_THRESHOLD -> {
                guaranteeCounter = 0
                Monster.Rarity.LEGENDARY
            }
            pityTimer >= PITY_TIMER_MAX -> {
                pityTimer = 0
                Monster.Rarity.RARE
            }
            else -> null
        }
        
        val rarity = guaranteedRarity ?: rollRarity()
        
        return generateGachaMonster(rarity)
    }
    
    /**
     * Rolls for monster rarity based on gacha rates
     */
    private fun rollRarity(): Monster.Rarity {
        val roll = random.nextDouble()
        var cumulative = 0.0
        
        for ((rarity, rate) in GACHA_RATES) {
            cumulative += rate
            if (roll <= cumulative) {
                if (rarity in listOf(Monster.Rarity.RARE, Monster.Rarity.EPIC, Monster.Rarity.LEGENDARY)) {
                    pityTimer = 0 // Reset pity on rare+ pull
                }
                return rarity
            }
        }
        
        return Monster.Rarity.COMMON
    }
    
    /**
     * Generates a monster from gacha with ironman-specific abilities
     */
    private fun generateGachaMonster(rarity: Monster.Rarity): Monster {
        val names = when (rarity) {
            Monster.Rarity.COMMON -> listOf("Iron Golem", "Steel Slime", "Metal Moth")
            Monster.Rarity.UNCOMMON -> listOf("Chrome Warrior", "Silver Sage", "Copper Dragon")
            Monster.Rarity.RARE -> listOf("Golden Phoenix", "Platinum Tiger", "Diamond Wolf")
            Monster.Rarity.EPIC -> listOf("Mythril Titan", "Orichalcum Beast", "Adamant Sphinx")
            Monster.Rarity.LEGENDARY -> listOf("Legendary Iron Kaiser", "Supreme Steel Emperor", "Ultimate Metal God")
        }
        
        val effectTypes = when (rarity) {
            Monster.Rarity.COMMON -> listOf(Monster.EffectType.CHIP_BONUS)
            Monster.Rarity.UNCOMMON -> listOf(Monster.EffectType.BETTING_BOOST, Monster.EffectType.CARD_ADVANTAGE)
            Monster.Rarity.RARE -> listOf(Monster.EffectType.DEFENSIVE_SHIELD, Monster.EffectType.LUCK_ENHANCEMENT)
            Monster.Rarity.EPIC -> listOf(Monster.EffectType.AI_ENHANCEMENT, Monster.EffectType.VISUAL_THEME)
            Monster.Rarity.LEGENDARY -> listOf(Monster.EffectType.ULTIMATE_POWER)
        }
        
        return Monster(
            name = names.random(),
            rarity = rarity,
            baseHealth = (100 * rarity.powerMultiplier).toInt(),
            effectType = effectTypes.random(),
            effectPower = (rarity.powerMultiplier * 25).toInt(),
            description = "An ironman gacha monster with enhanced abilities"
        )
    }
    
    /**
     * Displays collection and current stats
     */
    private fun displayCollectionAndStats() {
        println("\n📊 IRONMAN STATISTICS")
        println("=".repeat(30))
        println("Level: $currentLevel")
        println("Rounds survived: $roundsSurvived") 
        println("Current streak: $currentStreak")
        println("Best streak: $bestStreak")
        println("Deaths survived: $survivedDeaths")
        println("Total deaths: $deathCount")
        println("Total winnings: $totalWinnings chips")
        println("Total losses: $totalLosses chips")
        println("Gacha points: $gachaPoints")
        println("Risk level: ${String.format("%.1fx", riskLevel)}")
        
        val winRate = if (roundsSurvived > 0) (roundsSurvived * 100.0) / (roundsSurvived + deathCount) else 0.0
        println("Survival rate: ${String.format("%.1f%%", winRate)}")
        
        println("\n🎴 MONSTER COLLECTION (${playerCollection.getMonsterCount()} total)")
        val monstersByRarity = playerCollection.getOwnedMonsters().groupBy { it.rarity }
        
        Monster.Rarity.values().forEach { rarity ->
            val count = monstersByRarity[rarity]?.size ?: 0
            println("${rarity.displayName}: $count")
        }
        
        print("\nPress Enter to continue...")
        readlnOrNull()
    }
    
    /**
     * Gets difficulty name for display
     */
    private fun getDifficultyName(level: Int): String {
        return when (level) {
            1 -> "Easy"
            2 -> "Normal" 
            3 -> "Hard"
            4 -> "Nightmare"
            else -> "Unknown"
        }
    }
    
    /**
     * Shows final ironman statistics
     */
    private fun showIronmanStats() {
        println("\n🏆 IRONMAN RUN COMPLETE! 🏆")
        println("=".repeat(40))
        println("Final level reached: $currentLevel")
        println("Rounds survived: $roundsSurvived")
        println("Best streak: $bestStreak") 
        println("Deaths survived: $survivedDeaths")
        println("Total deaths: $deathCount")
        println("Final gacha points: $gachaPoints")
        println("Difficulty: ${getDifficultyName(difficultyLevel)}")
        
        val score = calculateFinalScore()
        println("Final score: $score")
        
        when {
            score >= 10000 -> println("🏅 RANK: IRONMAN LEGEND")
            score >= 5000 -> println("🏅 RANK: IRONMAN MASTER") 
            score >= 2000 -> println("🏅 RANK: IRONMAN EXPERT")
            score >= 1000 -> println("🏅 RANK: IRONMAN VETERAN")
            score >= 500 -> println("🏅 RANK: IRONMAN SURVIVOR")
            else -> println("🏅 RANK: IRONMAN ROOKIE")
        }
        
        println("\nMonsters collected: ${playerCollection.getMonsterCount()}")
        if (playerCollection.getMonsterCount() > 0) {
            val legendaryCount = playerCollection.getOwnedMonsters().count { it.rarity == Monster.Rarity.LEGENDARY }
            if (legendaryCount > 0) {
                println("🌟 Legendary monsters: $legendaryCount")
            }
        }
        
        println("\nThank you for playing Ironman Mode!")
    }
    
    /**
     * Calculates final score based on achievements
     */
    private fun calculateFinalScore(): Int {
        var score = 0
        score += roundsSurvived * 100
        score += bestStreak * 50
        score += survivedDeaths * 200
        score += gachaPoints
        score += playerCollection.getOwnedMonsters().sumOf { it.rarity.powerMultiplier.toInt() * 100 }
        score = (score * DIFFICULTY_MULTIPLIERS[difficultyLevel]!!).toInt()
        return score
    }
}

/**
 * Result of a poker round in Ironman mode
 */
data class IronmanRoundResult(
    val outcome: RoundOutcome,
    val chipsGained: Int,
    val chipsLost: Int,
    val handStrength: Int,
    val opponentStrength: Int
)

/**
 * Possible outcomes of an Ironman poker round
 */
enum class RoundOutcome {
    VICTORY,    // Won the round
    DEFEAT,     // Lost the round
    DRAW        // Tied
}