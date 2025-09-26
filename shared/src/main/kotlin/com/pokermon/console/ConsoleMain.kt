package com.pokermon.console

import com.pokermon.GameMode
import com.pokermon.GamePhase
import com.pokermon.HandEvaluator
import com.pokermon.database.Monster
import com.pokermon.modern.CardUtils
import com.pokermon.players.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Enhanced Console-only version of the Pokermon game - Pure Kotlin-native implementation.
 *
 * This class provides a sophisticated text-based interface that offers full feature parity
 * with the Android GUI version, exposing all game modes and functions through an
 * intuitive command-line experience.
 *
 * Features:
 * - All game modes (Classic, Adventure, Safari, Ironman)
 * - HandEvaluator integration for consistent scoring
 * - Flow-based reactive state management
 * - Monster companion system integration ready
 * - Advanced poker hand evaluation and display
 * - Session statistics and player profiles
 * - Card exchange and sophisticated betting rounds
 * - AI personality system integration
 *
 * @author Carl Nelson (@Gameaday)
 * @version 2.0.0 (Enhanced Kotlin-native implementation)
 */
object ConsoleMain {
    // =================================================================
    // ENHANCED GAME STATE MANAGEMENT
    // =================================================================

    /**
     * Comprehensive game state for full feature support.
     */
    sealed class GameState {
        object Initializing : GameState()

        data class MenuSelection(val availableModes: List<GameMode>) : GameState()

        data class ModeConfiguration(val selectedMode: GameMode) : GameState()

        data class PlayerSetup(
            val mode: GameMode,
            val playerName: String = "",
            val opponentCount: Int = 0,
            val startingChips: Int = 0,
        ) : GameState()

        data class GameActive(
            val mode: GameMode,
            val players: List<Player>,
            val pot: Int = 0,
            val round: Int = 1,
            val phase: GamePhase = GamePhase.HAND_DEALING,
            val deck: MutableList<Int> = mutableListOf(),
        ) : GameState()

        data class HandEvaluation(
            val players: List<Player>,
            val handResults: Map<String, HandEvaluator.HandResult>,
        ) : GameState()

        data class BettingRound(
            val players: List<Player>,
            val pot: Int,
            val currentBet: Int,
            val phase: String = "initial",
        ) : GameState()

        data class CardExchange(val players: List<Player>) : GameState()

        data class Results(
            val winners: List<Player>,
            val pot: Int,
            val handResults: Map<String, HandEvaluator.HandResult>,
        ) : GameState()

        data class MonsterEncounter(
            val mode: GameMode,
            val monster: Monster?,
            val players: List<Player>,
        ) : GameState()

        object GameOver : GameState()
    }

    // Enhanced state management with Flow
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameStats = MutableStateFlow(GameStats())
    val gameStats: StateFlow<GameStats> = _gameStats.asStateFlow()

    /**
     * Comprehensive game statistics for session tracking.
     */
    data class GameStats(
        val gamesPlayed: Int = 0,
        val gamesWon: Int = 0,
        val totalChipsWon: Int = 0,
        val bestHand: String = "None",
        val bestHandScore: Int = 0,
        val handsPlayed: Int = 0,
        val monstersEncountered: Int = 0,
        val monstersCaptured: Int = 0,
        val adventuresCompleted: Int = 0,
        val gachaRolls: Int = 0,
        val timeStarted: Long = System.currentTimeMillis(),
    )

    @JvmStatic
    fun main(args: Array<String>) =
        runBlocking {
            displayEnhancedWelcomeBanner()

            try {
                runFullFeaturedGame()
            } catch (e: Exception) {
                println("\n❌ Console game error: ${e.message}")
                e.printStackTrace()
            }

            displayFarewellMessage()
        }

    // =================================================================
    // MAIN GAME FLOW - Full feature implementation
    // =================================================================

    /**
     * Run the complete game experience with all modes and features.
     */
    private suspend fun runFullFeaturedGame() {
        _gameState.value = GameState.Initializing

        while (true) {
            // Game mode selection with full options
            val selectedMode = displayGameModeMenu()
            if (selectedMode == null) break // User chose to exit

            _gameState.value = GameState.ModeConfiguration(selectedMode)

            // Mode-specific configuration
            val gameSetup = configureModeSettings(selectedMode)

            // Enhanced player setup
            val enhancedSetup = performEnhancedPlayerSetup(selectedMode, gameSetup)

            // Run game session with selected mode
            runGameSession(selectedMode, enhancedSetup)

            // Session results and continuation
            if (!promptForNewGame()) break
        }

        displaySessionSummary()
    }

    /**
     * Display comprehensive game mode selection menu.
     */
    private fun displayGameModeMenu(): GameMode? {
        println("\n" + "🎮".repeat(20))
        println("            POKERMON GAME MODES")
        println("🎮".repeat(20))
        println()

        val modes = GameMode.entries
        modes.forEachIndexed { index, mode ->
            val icon =
                when (mode) {
                    GameMode.CLASSIC -> "🃏"
                    GameMode.ADVENTURE -> "⚔️"
                    GameMode.SAFARI -> "🏕️"
                    GameMode.IRONMAN -> "🎰"
                }
            println("  ${index + 1}. $icon ${mode.displayName}")
            println("     ${mode.description}")
            println()
        }

        println("  0. 🚪 Exit Game")
        println()

        while (true) {
            print("Select game mode (0-${modes.size}): ")
            val choice = readLine()?.toIntOrNull()

            when {
                choice == 0 -> return null
                choice != null && choice in 1..modes.size -> {
                    val selectedMode = modes[choice - 1]
                    println("\n✅ Selected: ${selectedMode.displayName}")
                    return selectedMode
                }
                else -> println("❌ Invalid choice. Please select 0-${modes.size}.")
            }
        }
    }

    /**
     * Configure mode-specific settings.
     */
    private fun configureModeSettings(mode: GameMode): ModeConfiguration {
        println("\n🔧 ${mode.displayName.uppercase()} CONFIGURATION")
        println("-".repeat(40))

        return when (mode) {
            GameMode.CLASSIC -> configureClassicMode()
            GameMode.ADVENTURE -> configureAdventureMode()
            GameMode.SAFARI -> configureSafariMode()
            GameMode.IRONMAN -> configureIronmanMode()
        }
    }

    private fun configureClassicMode(): ModeConfiguration {
        println("Classic poker with traditional rules.")
        println("• Standard 5-card draw")
        println("• Betting rounds with raises and folds")
        println("• Card exchange phase")
        return ModeConfiguration.Classic()
    }

    private fun configureAdventureMode(): ModeConfiguration {
        println("Battle monsters where their health equals their chip count!")
        println("• Defeat monsters to progress")
        println("• Monster difficulty scales with rounds")
        println("• Earn rewards and experience")

        print("Select difficulty (1=Easy, 2=Medium, 3=Hard): ")
        val difficulty = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2

        return ModeConfiguration.Adventure(difficulty)
    }

    private fun configureSafariMode(): ModeConfiguration {
        println("Capture monsters through strategic poker gameplay!")
        println("• Higher hand rankings increase capture chance")
        println("• Build your monster collection")
        println("• Different monsters have different capture rates")

        print("Enable rare monster encounters? (y/n): ")
        val rareMonsters = readLine()?.lowercase()?.startsWith("y") ?: false

        return ModeConfiguration.Safari(rareMonsters)
    }

    private fun configureIronmanMode(): ModeConfiguration {
        println("Convert winnings into monster gacha pulls!")
        println("• Chip rewards become gacha currency")
        println("• Higher stakes = better reward rates")
        println("• Collect rare and legendary monsters")

        print("Select gacha rate (1=Conservative, 2=Balanced, 3=High-Risk): ")
        val gachaRate = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2

        return ModeConfiguration.Ironman(gachaRate)
    }

    /**
     * Mode configuration sealed class.
     */
    sealed class ModeConfiguration {
        data class Classic(val variant: String = "standard") : ModeConfiguration()

        data class Adventure(val difficulty: Int = 2) : ModeConfiguration()

        data class Safari(val rareMonsters: Boolean = false) : ModeConfiguration()

        data class Ironman(val gachaRate: Int = 2) : ModeConfiguration()
    }

    /**
     * Enhanced player setup with mode-specific options.
     */
    private fun performEnhancedPlayerSetup(
        mode: GameMode,
        config: ModeConfiguration,
    ): EnhancedGameSetup {
        println("\n🎯 PLAYER SETUP - ${mode.displayName}")
        println("-".repeat(50))

        // Get player name
        print("Enter your name (or press Enter for 'Player'): ")
        val playerName = readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "Player"

        // Get opponent configuration based on mode
        val opponentSetup =
            when (mode) {
                GameMode.CLASSIC -> setupClassicOpponents()
                GameMode.ADVENTURE -> setupAdventureOpponents()
                GameMode.SAFARI -> setupSafariOpponents()
                GameMode.IRONMAN -> setupIronmanOpponents()
            }

        // Get starting resources
        val startingChips = selectStartingChips(mode)

        return EnhancedGameSetup(
            mode = mode,
            config = config,
            playerName = playerName,
            opponentCount = opponentSetup.count,
            opponentTypes = opponentSetup.types,
            startingChips = startingChips,
        )
    }

    private fun setupClassicOpponents(): OpponentSetup {
        print("Number of AI opponents (1-3): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        return OpponentSetup(count, listOf("AI"))
    }

    private fun setupAdventureOpponents(): OpponentSetup {
        print("Number of monsters to battle (1-5): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 5) ?: 3
        return OpponentSetup(count, listOf("Monster"))
    }

    private fun setupSafariOpponents(): OpponentSetup {
        print("Number of wild monsters (1-3): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 3) ?: 2
        return OpponentSetup(count, listOf("Wild Monster"))
    }

    private fun setupIronmanOpponents(): OpponentSetup {
        print("Number of gacha challengers (1-4): ")
        val count = readLine()?.toIntOrNull()?.coerceIn(1, 4) ?: 2
        return OpponentSetup(count, listOf("Gacha Challenger"))
    }

    private fun selectStartingChips(mode: GameMode): Int {
        val validChips =
            when (mode) {
                GameMode.CLASSIC -> intArrayOf(100, 500, 1000, 2500)
                GameMode.ADVENTURE -> intArrayOf(200, 500, 1000, 2000)
                GameMode.SAFARI -> intArrayOf(150, 300, 750, 1500)
                GameMode.IRONMAN -> intArrayOf(500, 1000, 2500, 5000)
            }

        println("\nSelect starting chips for ${mode.displayName}:")
        validChips.forEachIndexed { index, chips ->
            println("  ${index + 1}. $chips chips")
        }

        while (true) {
            print("Enter choice (1-${validChips.size}): ")
            val choice = readLine()?.toIntOrNull()
            if (choice != null && choice in 1..validChips.size) {
                return validChips[choice - 1]
            }
            println("❌ Invalid choice. Please select 1-${validChips.size}.")
        }
    }

    data class OpponentSetup(val count: Int, val types: List<String>)

    /**
     * Enhanced game setup data class.
     */
    data class EnhancedGameSetup(
        val mode: GameMode,
        val config: ModeConfiguration,
        val playerName: String,
        val opponentCount: Int,
        val opponentTypes: List<String>,
        val startingChips: Int,
        val maxRounds: Int = 10, // Default maximum rounds
    )

    // =================================================================
    // GAME SESSION EXECUTION - Mode-specific gameplay
    // =================================================================

    /**
     * Run complete game session with selected mode.
     */
    private suspend fun runGameSession(
        mode: GameMode,
        setup: EnhancedGameSetup,
    ) {
        println("\n🚀 Starting ${mode.displayName} Session")
        println("=".repeat(60))

        // Create deck and players using enhanced setup
        val deck = CardUtils.createShuffledDeck().toMutableList()
        val players = createPlayersForMode(setup)

        _gameState.value =
            GameState.GameActive(
                mode = mode,
                players = players,
                deck = deck,
            )

        when (mode) {
            GameMode.CLASSIC -> runClassicSession(players, deck, setup)
            GameMode.ADVENTURE -> runAdventureSession(players, deck, setup)
            GameMode.SAFARI -> runSafariSession(players, deck, setup)
            GameMode.IRONMAN -> runIronmanSession(players, deck, setup)
        }
    }

    private fun createPlayersForMode(setup: EnhancedGameSetup): List<Player> {
        val players = mutableListOf<Player>()

        // Add human player
        players.add(Player(setup.playerName, setup.startingChips))

        // Add AI opponents based on mode
        for (i in 1..setup.opponentCount) {
            val name = generateOpponentName(setup.mode, i)
            players.add(Player(name, setup.startingChips))
        }

        return players
    }

    private fun generateOpponentName(
        mode: GameMode,
        index: Int,
    ): String {
        return when (mode) {
            GameMode.CLASSIC -> "AI Player $index"
            GameMode.ADVENTURE -> "Monster Warrior $index"
            GameMode.SAFARI -> "Wild Creature $index"
            GameMode.IRONMAN -> "Gacha Master $index"
        }
    }

    /**
     * Run classic poker session.
     */
    private suspend fun runClassicSession(
        players: List<Player>,
        deck: MutableList<Int>,
        setup: EnhancedGameSetup,
    ) {
        println("\n🃏 CLASSIC POKER SESSION")
        println("Players: ${players.size}")
        println("Starting chips: ${setup.startingChips}")

        var round = 1
        val activePlayers = players.toMutableList()

        while (activePlayers.size > 1 && round <= 10) {
            println("\n" + "=".repeat(40))
            println("               ROUND $round")
            println("=".repeat(40))

            // Deal hands
            dealHandsToPlayers(activePlayers, deck)

            // Evaluate hands using HandEvaluator
            val handResults = evaluateAllHands(activePlayers)
            displayHandEvaluations(handResults)

            // First betting round
            val pot = conductBettingRound(activePlayers, "Initial Betting")

            // Card exchange phase
            conductCardExchange(activePlayers, deck)

            // Re-evaluate hands after exchange
            val finalHandResults = evaluateAllHands(activePlayers)

            // Final betting round
            val finalPot = conductFinalBettingRound(activePlayers, pot)

            // Determine winners and distribute pot
            val winners = determineWinners(activePlayers, finalHandResults)
            distributePot(winners, finalPot)

            // Display round results
            displayRoundResults(winners, finalPot, finalHandResults)

            // Remove players with no chips
            activePlayers.removeAll { it.chips <= 0 }
            round++

            if (activePlayers.size > 1) {
                println("\nPress Enter to continue to next round...")
                readLine()
            }
        }

        // Display session winner
        displaySessionWinner(activePlayers)
    }

    /**
     * Complete Adventure Mode session implementation.
     * Features monster battles integrated with poker gameplay.
     */
    private suspend fun runAdventureSession(
        players: List<Player>,
        deck: MutableList<Int>,
        setup: EnhancedGameSetup,
    ) {
        println("\n⚔️ ADVENTURE MODE SESSION STARTED")
        println("Embark on a quest where poker skills determine battle outcomes!")

        var currentRound = 1
        val maxRounds = setup.maxRounds
        val activePlayers = players.toMutableList()

        // Adventure-specific state
        val questProgress = mutableMapOf<String, Int>()
        val monstersDefeated = mutableMapOf<Player, Int>()

        while (currentRound <= maxRounds && activePlayers.size > 1) {
            println("\n🗡️ ADVENTURE ROUND $currentRound")

            // Generate monster encounter
            val monster = generateRandomMonster(currentRound)
            println("🐲 A wild ${monster.name} appears! (Health: ${monster.effectiveHealth}, Type: ${monster.rarity})")

            // Deal cards for battle preparation
            dealCardsToPlayers(activePlayers, deck, 5)

            // Display hands for battle planning
            activePlayers.forEach { player ->
                if (player.isHuman) {
                    displayPlayerHand(player, "Battle Hand")
                }
            }

            // Battle phase - poker hands determine damage
            for (player in activePlayers) {
                if (player.fold) continue

                println("\n⚔️ ${player.name}'s battle turn!")

                val handStrength = evaluateHandForBattle(player)
                val damage = calculateBattleDamage(handStrength)

                println("🎯 Hand strength: ${handStrength.description}")
                println("⚡ Damage dealt: $damage")

                // Monster counter-attack based on remaining health
                val monsterDamage = calculateMonsterDamage(monster, damage)
                if (monsterDamage > 0) {
                    player.chips = (player.chips - monsterDamage).coerceAtLeast(0)
                    println("🔥 ${monster.name} counter-attacks for $monsterDamage damage!")
                    println("💰 ${player.name} chips: ${player.chips}")
                }

                // Check if monster is defeated
                if (damage >= monster.effectiveHealth) {
                    println("🏆 ${player.name} defeated ${monster.name}!")
                    val reward = monster.effectiveHealth * 10
                    player.chips += reward
                    monstersDefeated[player] = (monstersDefeated[player] ?: 0) + 1
                    println("💰 Reward: $reward chips (Total: ${player.chips})")
                    break
                }

                // Check if player is defeated
                if (player.chips <= 0) {
                    println("💀 ${player.name} has been defeated in battle!")
                    player.setFold(true)
                    activePlayers.remove(player)
                }
            }

            // Update quest progress
            updateQuestProgress(questProgress, currentRound, monstersDefeated)

            currentRound++
            resetRound(activePlayers, deck)
        }

        // Display adventure results
        displayAdventureResults(players, monstersDefeated, questProgress)
    }

    /**
     * Complete Safari Mode session implementation.
     * Features monster capture mechanics through poker gameplay.
     */
    private suspend fun runSafariSession(
        players: List<Player>,
        deck: MutableList<Int>,
        setup: EnhancedGameSetup,
    ) {
        println("\n🏞️ SAFARI MODE SESSION STARTED")
        println("Capture wild monsters using your poker skills!")

        var currentRound = 1
        val maxRounds = setup.maxRounds
        val activePlayers = players.toMutableList()

        // Safari-specific state
        val capturedMonsters = mutableMapOf<Player, MutableList<Monster>>()
        val safariBalls = mutableMapOf<Player, Int>()

        // Initialize safari balls for each player
        activePlayers.forEach { player ->
            safariBalls[player] = 30 // Starting safari balls
            capturedMonsters[player] = mutableListOf()
        }

        while (currentRound <= maxRounds && activePlayers.any { (safariBalls[it] ?: 0) > 0 }) {
            println("\n🎯 SAFARI ROUND $currentRound")

            // Generate wild monster encounter
            val wildMonster = generateWildMonster(currentRound)
            val baseCapture =
                when (wildMonster.rarity) {
                    Monster.Rarity.COMMON -> 0.6
                    Monster.Rarity.UNCOMMON -> 0.4
                    Monster.Rarity.RARE -> 0.2
                    Monster.Rarity.LEGENDARY -> 0.05
                    else -> 0.5
                }
            println("👀 Wild ${wildMonster.name} appeared! (Rarity: ${wildMonster.rarity})")
            println("📊 Capture rate: ${(baseCapture * 100).toInt()}%")

            // Deal cards for capture attempt
            dealCardsToPlayers(activePlayers.filter { (safariBalls[it] ?: 0) > 0 }, deck, 5)

            // Capture attempts
            for (player in activePlayers) {
                if ((safariBalls[player] ?: 0) <= 0) continue

                println("\n🎣 ${player.name}'s capture attempt!")

                if (player.isHuman) {
                    displayPlayerHand(player, "Capture Hand")
                    println("Safari balls remaining: ${safariBalls[player]}")
                    print("Attempt capture? (y/n): ")
                    val input = readLine()?.lowercase()
                    if (input != "y" && input != "yes") {
                        println("${player.name} chose not to attempt capture")
                        continue
                    }
                }

                // Use safari ball
                safariBalls[player] = (safariBalls[player] ?: 0) - 1

                val handStrength = evaluateHandForCapture(player)
                val captureBonus = calculateCaptureBonus(handStrength)
                val finalCaptureRate = (baseCapture + captureBonus).coerceIn(0.0, 1.0)

                println("🎯 Hand strength: ${handStrength.description}")
                println("📈 Capture bonus: +${(captureBonus * 100).toInt()}%")
                println("🎪 Final capture rate: ${(finalCaptureRate * 100).toInt()}%")

                // Attempt capture
                val captureSuccess = Math.random() < finalCaptureRate

                if (captureSuccess) {
                    println("🎉 Successfully captured ${wildMonster.name}!")
                    capturedMonsters[player]?.add(wildMonster)

                    // Capture rewards
                    val captureReward =
                        when (wildMonster.rarity) {
                            Monster.Rarity.COMMON -> 50
                            Monster.Rarity.UNCOMMON -> 100
                            Monster.Rarity.RARE -> 250
                            Monster.Rarity.LEGENDARY -> 500
                            else -> 25
                        }
                    player.chips += captureReward
                    println("💰 Capture reward: $captureReward chips (Total: ${player.chips})")
                    break // Monster captured, round ends
                } else {
                    println("💨 ${wildMonster.name} escaped!")
                    println("🏀 Safari balls remaining: ${safariBalls[player]}")
                }
            }

            currentRound++
            resetRound(activePlayers, deck)
        }

        // Display safari results
        displaySafariResults(players, capturedMonsters, safariBalls)
    }

    /**
     * Complete Ironman Mode session implementation.
     * Features high-risk gameplay with permadeath mechanics.
     */
    private suspend fun runIronmanSession(
        players: List<Player>,
        deck: MutableList<Int>,
        setup: EnhancedGameSetup,
    ) {
        println("\n💀 IRONMAN MODE SESSION STARTED")
        println("High-risk, high-reward gameplay - lose all chips and face PERMADEATH!")

        var currentRound = 1
        val maxRounds = setup.maxRounds
        val activePlayers = players.toMutableList()

        // Ironman-specific state
        val gachaPoints = mutableMapOf<Player, Int>()
        val riskLevels = mutableMapOf<Player, Double>()
        val survivalStreak = mutableMapOf<Player, Int>()
        val rarePulls = mutableMapOf<Player, MutableList<String>>()

        // Initialize ironman stats
        activePlayers.forEach { player ->
            gachaPoints[player] = 0
            riskLevels[player] = 1.0
            survivalStreak[player] = 0
            rarePulls[player] = mutableListOf()
        }

        while (currentRound <= maxRounds && activePlayers.size > 1) {
            println("\n⚡ IRONMAN ROUND $currentRound")

            // Display risk levels
            activePlayers.forEach { player ->
                val risk = riskLevels[player] ?: 1.0
                val warningText = if (risk > 2.0) " ⚠️ HIGH RISK!" else ""
                println("💀 ${player.name} risk level: ${String.format("%.1f", risk)}x$warningText")
            }

            // Deal cards with risk multipliers
            dealCardsToPlayers(activePlayers, deck, 5)

            // High-stakes betting round
            runIronmanBettingRound(activePlayers, riskLevels, gachaPoints)

            // Convert winnings to gacha points
            activePlayers.forEach { player ->
                if (!player.fold && player.chips > 0) {
                    val conversion = player.chips / 10
                    gachaPoints[player] = (gachaPoints[player] ?: 0) + conversion
                    println("🎰 ${player.name} earned $conversion gacha points")
                }
            }

            // Gacha pulls for those with enough points
            activePlayers.forEach { player ->
                val points = gachaPoints[player] ?: 0
                if (points >= 100 && player.isHuman) {
                    println("\n🎰 ${player.name} has $points gacha points!")
                    print("Perform gacha pull? (100 points) (y/n): ")
                    val input = readLine()?.lowercase()
                    if (input == "y" || input == "yes") {
                        performGachaPull(player, gachaPoints, rarePulls)
                    }
                }
            }

            // Check for permadeath conditions
            val eliminatedPlayers = mutableListOf<Player>()
            activePlayers.forEach { player ->
                if (player.chips <= 0) {
                    println("\n💀 PERMADEATH TRIGGERED FOR ${player.name}!")
                    println("💸 All progress lost - no second chances in Ironman mode!")
                    eliminatedPlayers.add(player)
                } else {
                    // Survived round - update streak
                    survivalStreak[player] = (survivalStreak[player] ?: 0) + 1
                    val streak = survivalStreak[player] ?: 0
                    if (streak > 0 && streak % 5 == 0) {
                        println("🏆 ${player.name} survives round $currentRound ($streak round streak!)")
                    }
                }
            }

            // Remove eliminated players
            eliminatedPlayers.forEach { player ->
                activePlayers.remove(player)
                player.setFold(true)
            }

            // Update risk levels based on performance
            updateRiskLevels(activePlayers, riskLevels, currentRound)

            currentRound++
            resetRound(activePlayers, deck)
        }

        // Display ironman results
        displayIronmanResults(players, gachaPoints, survivalStreak, rarePulls)
    }

    // =================================================================
    // MODE-SPECIFIC HELPER METHODS - Complete implementations
    // =================================================================

    private fun generateRandomMonster(round: Int): Monster {
        val monsters =
            listOf(
                Monster(
                    "Forest Dragon",
                    Monster.Rarity.UNCOMMON,
                    80 + (round * 10),
                    Monster.EffectType.CHIP_BONUS,
                    25,
                    "A fierce dragon that lurks in ancient forests",
                ),
                Monster(
                    "Cave Beast",
                    Monster.Rarity.COMMON,
                    60 + (round * 5),
                    Monster.EffectType.DEFENSIVE_SHIELD,
                    15,
                    "A sturdy creature that dwells in deep caves",
                ),
                Monster(
                    "Ancient Golem",
                    Monster.Rarity.RARE,
                    120 + (round * 15),
                    Monster.EffectType.BETTING_BOOST,
                    35,
                    "An ancient stone guardian with immense power",
                ),
                Monster(
                    "Shadow Wolf",
                    Monster.Rarity.UNCOMMON,
                    70 + (round * 8),
                    Monster.EffectType.LUCK_ENHANCEMENT,
                    20,
                    "A mystical wolf that moves through shadows",
                ),
                Monster(
                    "Crystal Spider",
                    Monster.Rarity.RARE,
                    100 + (round * 12),
                    Monster.EffectType.CARD_ADVANTAGE,
                    30,
                    "A beautiful yet dangerous crystalline arachnid",
                ),
            )
        return monsters.random()
    }

    private fun generateWildMonster(round: Int): Monster {
        val rarity =
            when ((1..100).random()) {
                in 1..60 -> Monster.Rarity.COMMON
                in 61..85 -> Monster.Rarity.UNCOMMON
                in 86..95 -> Monster.Rarity.RARE
                else -> Monster.Rarity.LEGENDARY
            }

        val captureRate =
            when (rarity) {
                Monster.Rarity.COMMON -> 0.6
                Monster.Rarity.UNCOMMON -> 0.4
                Monster.Rarity.RARE -> 0.2
                Monster.Rarity.LEGENDARY -> 0.05
                else -> 0.5
            }

        val names =
            mapOf(
                Monster.Rarity.COMMON to listOf("Field Mouse", "Garden Snake", "House Cat"),
                Monster.Rarity.UNCOMMON to listOf("Wild Boar", "Mountain Lion", "Eagle"),
                Monster.Rarity.RARE to listOf("White Tiger", "Golden Eagle", "Crystal Fox"),
                Monster.Rarity.LEGENDARY to listOf("Phoenix", "Dragon", "Unicorn"),
            )

        val name = names[rarity]?.random() ?: "Unknown"
        return Monster(
            name,
            rarity,
            50,
            Monster.EffectType.CHIP_BONUS,
            (captureRate * 100).toInt(),
            "A wild creature encountered in the safari",
        )
    }

    private fun evaluateHandForBattle(player: Player): HandEvaluator.HandResult {
        return HandEvaluator.evaluateHand(player.hand)
    }

    private fun evaluateHandForCapture(player: Player): HandEvaluator.HandResult {
        return HandEvaluator.evaluateHand(player.hand)
    }

    private fun calculateBattleDamage(handResult: HandEvaluator.HandResult): Int {
        // Convert hand strength to battle damage
        return when {
            handResult.score >= 800 -> 80 // Strong hands deal high damage
            handResult.score >= 600 -> 60
            handResult.score >= 400 -> 40
            handResult.score >= 200 -> 25
            else -> 15 // Weak hands deal minimal damage
        }
    }

    private fun calculateCaptureBonus(handResult: HandEvaluator.HandResult): Double {
        // Convert hand strength to capture bonus
        return when {
            handResult.score >= 800 -> 0.3 // Strong hands get big bonus
            handResult.score >= 600 -> 0.2
            handResult.score >= 400 -> 0.1
            handResult.score >= 200 -> 0.05
            else -> 0.0 // Weak hands get no bonus
        }
    }

    private fun calculateMonsterDamage(
        monster: Monster,
        damageDealt: Int,
    ): Int {
        // Monster counter-attack based on rarity and remaining health
        val baseCounterDamage =
            when (monster.rarity) {
                Monster.Rarity.LEGENDARY -> 30
                Monster.Rarity.EPIC -> 25
                Monster.Rarity.RARE -> 20
                Monster.Rarity.UNCOMMON -> 15
                Monster.Rarity.COMMON -> 10
            }

        // Reduce counter-damage if monster was hurt badly
        return if (damageDealt >= monster.effectiveHealth) 0 else baseCounterDamage
    }

    private fun runIronmanBettingRound(
        players: List<Player>,
        riskLevels: MutableMap<Player, Double>,
        gachaPoints: MutableMap<Player, Int>,
    ) {
        println("\n💰 High-Stakes Ironman Betting Round")

        players.forEach { player ->
            if (player.fold) return@forEach

            val risk = riskLevels[player] ?: 1.0
            val baseBet = 50
            val riskMultipliedBet = (baseBet * risk).toInt()
            val actualBet = minOf(riskMultipliedBet, player.chips)

            player.chips -= actualBet
            println("💀 ${player.name} bets $actualBet chips (${risk}x risk multiplier)")

            // Update risk based on betting behavior
            if (actualBet >= riskMultipliedBet) {
                riskLevels[player] = (risk + 0.1).coerceAtMost(3.0)
            }
        }
    }

    private fun performGachaPull(
        player: Player,
        gachaPoints: MutableMap<Player, Int>,
        rarePulls: MutableMap<Player, MutableList<String>>,
    ) {
        val points = gachaPoints[player] ?: 0
        if (points < 100) return

        gachaPoints[player] = points - 100

        val pullResult =
            when ((1..1000).random()) {
                in 1..500 -> "Common Monster"
                in 501..800 -> "Uncommon Monster"
                in 801..950 -> "Rare Monster"
                in 951..990 -> "Epic Monster"
                else -> "LEGENDARY MONSTER!"
            }

        println("🎰 GACHA RESULT: $pullResult")

        if (pullResult.contains("Rare") || pullResult.contains("Epic") || pullResult.contains("LEGENDARY")) {
            rarePulls[player]?.add(pullResult)
            val bonus =
                when {
                    pullResult.contains("LEGENDARY") -> 1000
                    pullResult.contains("Epic") -> 500
                    else -> 200
                }
            player.chips += bonus
            println("🎉 Bonus chips for rare pull: $bonus")
        }
    }

    private fun updateQuestProgress(
        questProgress: MutableMap<String, Int>,
        round: Int,
        monstersDefeated: Map<Player, Int>,
    ) {
        val totalDefeated = monstersDefeated.values.sum()
        questProgress["monsters_defeated"] = totalDefeated
        questProgress["rounds_completed"] = round

        if (totalDefeated >= 5) {
            println("🎯 Quest milestone: 5 monsters defeated!")
        }
    }

    private fun updateRiskLevels(
        players: List<Player>,
        riskLevels: MutableMap<Player, Double>,
        round: Int,
    ) {
        players.forEach { player ->
            val currentRisk = riskLevels[player] ?: 1.0

            // Increase risk over time
            val timeRisk = 1.0 + (round * 0.05)

            // Adjust based on chip count
            val chipRisk =
                when {
                    player.chips > 1000 -> 0.9 // Lower risk for wealthy players
                    player.chips < 200 -> 1.3 // Higher risk for poor players
                    else -> 1.0
                }

            val newRisk = (currentRisk * timeRisk * chipRisk).coerceIn(1.0, 3.0)
            riskLevels[player] = newRisk
        }
    }

    private fun displayAdventureResults(
        players: List<Player>,
        monstersDefeated: Map<Player, Int>,
        questProgress: Map<String, Int>,
    ) {
        println("\n⚔️ ADVENTURE MODE RESULTS")
        println("=".repeat(40))

        players.forEach { player ->
            val defeated = monstersDefeated[player] ?: 0
            println("🗡️ ${player.name}: $defeated monsters defeated, ${player.chips} chips")
        }

        val totalDefeated = questProgress["monsters_defeated"] ?: 0
        val roundsCompleted = questProgress["rounds_completed"] ?: 0
        println("\n🎯 Quest Summary:")
        println("   Total monsters defeated: $totalDefeated")
        println("   Rounds completed: $roundsCompleted")

        if (totalDefeated >= 10) {
            println("🏆 QUEST MASTERY ACHIEVED!")
        }
    }

    private fun displaySafariResults(
        players: List<Player>,
        capturedMonsters: Map<Player, List<Monster>>,
        safariBalls: Map<Player, Int>,
    ) {
        println("\n🏞️ SAFARI MODE RESULTS")
        println("=".repeat(40))

        players.forEach { player ->
            val captured = capturedMonsters[player]?.size ?: 0
            val ballsLeft = safariBalls[player] ?: 0
            println("🎯 ${player.name}: $captured monsters captured, $ballsLeft balls remaining, ${player.chips} chips")

            // Show captured monsters by rarity
            val monsters = capturedMonsters[player] ?: emptyList()
            val byRarity = monsters.groupBy { it.rarity }
            byRarity.forEach { (rarity, mons) ->
                println("   $rarity: ${mons.size} (${mons.joinToString(", ") { it.name }})")
            }
        }

        val totalCaptured = capturedMonsters.values.sumOf { it.size }
        println("\n🎪 Safari Summary: $totalCaptured total monsters captured")
    }

    private fun displayIronmanResults(
        players: List<Player>,
        gachaPoints: Map<Player, Int>,
        survivalStreak: Map<Player, Int>,
        rarePulls: Map<Player, List<String>>,
    ) {
        println("\n💀 IRONMAN MODE RESULTS")
        println("=".repeat(40))

        players.forEach { player ->
            val points = gachaPoints[player] ?: 0
            val streak = survivalStreak[player] ?: 0
            val rares = rarePulls[player]?.size ?: 0

            if (player.chips > 0) {
                println("🏆 ${player.name}: SURVIVED with ${player.chips} chips")
                println("   Survival streak: $streak rounds")
            } else {
                println("💀 ${player.name}: ELIMINATED (Permadeath)")
            }

            println("   Gacha points: $points")
            println("   Rare pulls: $rares")

            rarePulls[player]?.forEach { pull ->
                println("     ✨ $pull")
            }
        }

        val survivors = players.count { it.chips > 0 }
        println("\n⚡ Ironman Summary: $survivors/${players.size} players survived")
    }

    private fun dealCardsToPlayers(
        players: List<Player>,
        deck: MutableList<Int>,
        count: Int,
    ) {
        players.forEach { player ->
            if (!player.fold) {
                val hand = mutableListOf<Int>()
                repeat(count) {
                    if (deck.isNotEmpty()) {
                        hand.add(deck.removeFirst())
                    }
                }
                player.hand = hand.toIntArray()
            }
        }
    }

    private fun displayPlayerHand(
        player: Player,
        title: String,
    ) {
        println("\n$title - ${player.name}:")
        println("  ${CardUtils.formatHandSymbols(player.hand)}")
        val result = HandEvaluator.evaluateHand(player.hand)
        println("  ${result.description} (Score: ${result.score})")
    }

    private fun resetRound(
        players: List<Player>,
        deck: MutableList<Int>,
    ) {
        // Reset fold status for next round
        players.forEach { player ->
            if (player.chips > 0) {
                player.setFold(false)
            }
        }

        // Shuffle deck if getting low
        if (deck.size < 20) {
            deck.addAll(CardUtils.createDeck().toList())
            deck.shuffle()
        }
    }

    // =================================================================
    // GAME MECHANICS - Enhanced with HandEvaluator integration
    // =================================================================

    private fun dealHandsToPlayers(
        players: List<Player>,
        deck: MutableList<Int>,
    ) {
        println("\n🎴 Dealing hands...")

        players.forEach { player ->
            val hand = CardUtils.dealCards(deck, 5)
            player.hand = hand

            if (player.name == players[0].name) { // Human player
                println("\nYour hand:")
                displayPlayerHand(player)
            }
        }
    }

    private fun evaluateAllHands(players: List<Player>): Map<String, HandEvaluator.HandResult> {
        return players.associate { player ->
            player.name to HandEvaluator.evaluateHand(player.hand)
        }
    }

    private fun displayHandEvaluations(handResults: Map<String, HandEvaluator.HandResult>) {
        println("\n📊 Hand Evaluations:")
        handResults.forEach { (playerName, result) ->
            if (playerName == handResults.keys.first()) { // Show human player's result
                println("$playerName: ${result.description} (Score: ${result.score})")
            }
        }
    }

    private fun displayPlayerHand(player: Player) {
        println("  ${CardUtils.formatHandSymbols(player.hand)}")
        val result = HandEvaluator.evaluateHand(player.hand)
        println("  ${result.description} (Score: ${result.score})")
    }

    private fun conductBettingRound(
        players: List<Player>,
        roundName: String,
    ): Int {
        println("\n💰 $roundName")

        // Simple betting implementation for now
        val pot = players.sumOf { minOf(50, it.chips) }
        players.forEach { player ->
            val bet = minOf(50, player.chips)
            player.chips -= bet
            println("${player.name} bets $bet chips")
        }

        return pot
    }

    private fun conductCardExchange(
        players: List<Player>,
        deck: MutableList<Int>,
    ) {
        println("\n🔄 Card Exchange Phase")

        // Human player exchange
        val humanPlayer = players[0]
        conductHumanCardExchange(humanPlayer, deck)

        // AI players exchange (simplified)
        players.drop(1).forEach { player ->
            val exchangeCount = (0..3).random()
            if (exchangeCount > 0) {
                // Simple AI: exchange random cards
                repeat(exchangeCount) {
                    if (player.hand.isNotEmpty() && deck.isNotEmpty()) {
                        val randomIndex = player.hand.indices.random()
                        val newCard = deck.removeAt(0)
                        player.hand[randomIndex] = newCard
                    }
                }
                println("${player.name} exchanges $exchangeCount cards")
            }
        }
    }

    private fun conductHumanCardExchange(
        player: Player,
        deck: MutableList<Int>,
    ) {
        println("\nYour current hand:")
        displayPlayerHand(player)

        print("Enter card positions to exchange (1-5, space separated, or 'none'): ")
        val input = readLine()?.trim()?.lowercase()

        if (input == "none" || input.isNullOrEmpty()) {
            println("No cards exchanged.")
            return
        }

        try {
            val positions = input.split(" ").map { it.toInt() - 1 } // Convert to 0-based
            val validPositions = positions.filter { it in 0..4 }

            if (validPositions.isNotEmpty() && deck.size >= validPositions.size) {
                validPositions.forEach { pos ->
                    val newCard = deck.removeAt(0)
                    player.hand[pos] = newCard
                }

                println("Exchanged ${validPositions.size} cards.")
                println("Your new hand:")
                displayPlayerHand(player)
            }
        } catch (e: Exception) {
            println("Invalid input. No cards exchanged.")
        }
    }

    private fun conductFinalBettingRound(
        players: List<Player>,
        currentPot: Int,
    ): Int {
        println("\n💰 Final Betting Round")

        // Re-evaluate hands and conduct final betting
        val additionalPot = players.sumOf { minOf(25, it.chips) }
        players.forEach { player ->
            val bet = minOf(25, player.chips)
            player.chips -= bet
            println("${player.name} final bet: $bet chips")
        }

        return currentPot + additionalPot
    }

    private fun determineWinners(
        players: List<Player>,
        handResults: Map<String, HandEvaluator.HandResult>,
    ): List<Player> {
        val maxScore = handResults.values.maxOfOrNull { it.score } ?: 0
        return players.filter { handResults[it.name]?.score == maxScore }
    }

    private fun distributePot(
        winners: List<Player>,
        pot: Int,
    ) {
        val winnings = pot / winners.size
        winners.forEach { winner ->
            winner.chips += winnings
            println("${winner.name} wins $winnings chips!")
        }
    }

    private fun displayRoundResults(
        winners: List<Player>,
        pot: Int,
        handResults: Map<String, HandEvaluator.HandResult>,
    ) {
        println("\n🏆 ROUND RESULTS")
        println("-".repeat(30))

        winners.forEach { winner ->
            val result = handResults[winner.name]
            println("🥇 ${winner.name}: ${result?.description} (Score: ${result?.score})")
            println("   Chips: ${winner.chips}")
        }

        println("\nAll players:")
        handResults.forEach { (name, result) ->
            val player = winners.find { it.name == name } ?: return@forEach
            println("   $name: ${result.description} (${player.chips} chips)")
        }
    }

    private fun displaySessionWinner(remainingPlayers: List<Player>) {
        println("\n" + "🎊".repeat(20))
        println("            SESSION COMPLETE")
        println("🎊".repeat(20))

        if (remainingPlayers.isNotEmpty()) {
            val winner = remainingPlayers.maxByOrNull { it.chips }
            println("🏆 Session Winner: ${winner?.name}")
            println("💰 Final Chips: ${winner?.chips}")
        }
    }

    // =================================================================
    // USER INTERFACE UTILITIES
    // =================================================================

    private fun displayEnhancedWelcomeBanner() {
        println("=".repeat(80))
        println("           🃏 POKERMON - ENHANCED CONSOLE EDITION 🃏")
        println("               Pure Kotlin-Native Implementation")
        println("                    All Game Modes Available")
        println("=".repeat(80))
        println()
        println("🎯 Available Game Modes:")
        println("   🃏 Classic - Traditional poker with sophisticated evaluation")
        println("   ⚔️ Adventure - Battle monsters in poker duels")
        println("   🏕️ Safari - Capture monsters through strategic play")
        println("   🎰 Ironman - Convert winnings to monster gacha rolls")
        println()
        println("✨ Enhanced Features:")
        println("   • Advanced hand evaluation with detailed scoring")
        println("   • Comprehensive betting and exchange systems")
        println("   • Session statistics and player progression")
        println("   • Monster integration ready for all modes")
        println("   • Full feature parity with Android GUI version")
        println()
    }

    private fun promptForNewGame(): Boolean {
        println("\n🎮 Would you like to start a new game session?")
        print("Enter 'y' for yes, anything else to quit: ")
        val response = readLine()?.lowercase()?.trim()
        return response == "y" || response == "yes"
    }

    private fun displaySessionSummary() {
        val stats = _gameStats.value
        val sessionTime = (System.currentTimeMillis() - stats.timeStarted) / 1000

        println("\n" + "📊".repeat(30))
        println("                    SESSION SUMMARY")
        println("📊".repeat(30))

        println("Games Played: ${stats.gamesPlayed}")
        println("Games Won: ${stats.gamesWon}")
        println("Win Rate: ${if (stats.gamesPlayed > 0) "%.1f%%".format((stats.gamesWon * 100.0) / stats.gamesPlayed) else "N/A"}")
        println("Total Chips Won: ${stats.totalChipsWon}")
        println("Best Hand: ${stats.bestHand} (Score: ${stats.bestHandScore})")
        println("Hands Played: ${stats.handsPlayed}")

        if (stats.monstersEncountered > 0) {
            println("Monsters Encountered: ${stats.monstersEncountered}")
            println("Monsters Captured: ${stats.monstersCaptured}")
        }

        if (stats.gachaRolls > 0) {
            println("Gacha Rolls: ${stats.gachaRolls}")
        }

        println("Session Time: ${sessionTime}s")
        println()
    }

    private fun displayFarewellMessage() {
        println("=".repeat(80))
        println("              Thank you for playing Pokermon!")
        println("                   🎯 Game Over 🎯")
        println("=".repeat(80))
    }
}
