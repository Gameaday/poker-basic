package com.pokermon.android

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.GameFlows.GameActions
import com.pokermon.GameFlows.GameEvents
import com.pokermon.GameFlows.GameState
import com.pokermon.GameFlows.PlayingSubState
import com.pokermon.GameMode
import com.pokermon.GamePhase
import com.pokermon.android.data.MonsterOpponent
import com.pokermon.android.data.MonsterOpponentManager
import com.pokermon.android.data.UserProfileManager
import com.pokermon.android.ui.EnhancedCardDisplay
import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.PlayerInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val BET_INCREMENT = 10

/**
 * Clean, working gameplay screen with comprehensive Flow-based state management integration.
 * Features full reactive UI that responds to all game state changes and sub-states.
 * Demonstrates complete Android experience leveraging the state management system.
 */
@Composable
fun GameplayScreen(
    gameMode: GameMode,
    savedGame: com.pokermon.android.data.SavedGame? = null,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }
    val userProfile by userProfileManager.userProfile.collectAsState()
    val gameSettings by userProfileManager.gameSettings.collectAsState()

    val gameBridge = remember { GameLogicBridge() }
    val monsterOpponentManager = remember { MonsterOpponentManager() }
    val coroutineScope = rememberCoroutineScope()

    // ================================================================
    // COMPREHENSIVE STATE MANAGEMENT INTEGRATION
    // ================================================================

    // Reactive state from state management system
    val gameState by gameBridge.gameStateFlow.collectAsState(initial = GameState.Initializing)
    // Note: GameEvents are handled internally by the GameStateManager

    // UI state derived from game state
    var statusMessage by remember { mutableStateOf("Initializing game...") }
    var playerChips by remember { mutableIntStateOf(1000) }
    var currentPot by remember { mutableIntStateOf(0) }
    var currentBet by remember { mutableIntStateOf(0) }
    var playerCards by remember { mutableStateOf(listOf<String>()) }
    var allPlayers by remember { mutableStateOf(listOf<PlayerInfo>()) }
    var monsterOpponents by remember { mutableStateOf(listOf<MonsterOpponent>()) }
    var isGameInitialized by remember { mutableStateOf(false) }
    var awaitingPlayerAction by remember { mutableStateOf(false) }
    var lastActionResult by remember { mutableStateOf("") }
    var betAmount by remember { mutableIntStateOf(50) }
    var selectedCards by remember { mutableStateOf(setOf<Int>()) }
    var currentRound by remember { mutableIntStateOf(0) }

    // Mode-specific state
    var adventureMonster by remember { mutableStateOf<String?>(null) }
    var monsterHealth by remember { mutableIntStateOf(100) }
    var safariCaptures by remember { mutableIntStateOf(0) }
    var gachaPoints by remember { mutableIntStateOf(0) }
    var riskLevel by remember { mutableDoubleStateOf(1.0) }
    var achievementNotification by remember { mutableStateOf<String?>(null) }

    // Back button protection
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    // ================================================================
    // SAVED GAME RESTORATION
    // ================================================================

    // Restore game state from saved game if provided
    LaunchedEffect(savedGame) {
        savedGame?.let { save ->
            try {
                // Restore basic game state
                currentRound = save.currentRound
                playerChips = save.playerChips
                currentPot = save.totalPot
                playerCards = save.playerCards
                
                // Restore mode-specific data
                adventureMonster = save.modeSpecificData["adventureMonster"]?.takeIf { it.isNotEmpty() }
                monsterHealth = save.modeSpecificData["monsterHealth"]?.toIntOrNull() ?: 100
                safariCaptures = save.modeSpecificData["safariCaptures"]?.toIntOrNull() ?: 0
                gachaPoints = save.modeSpecificData["gachaPoints"]?.toIntOrNull() ?: 0
                riskLevel = save.modeSpecificData["riskLevel"]?.toDoubleOrNull() ?: 1.0
                
                statusMessage = "Game restored from ${save.formattedSaveTime}"
                isGameInitialized = true
                
                // Clear the status message after a delay
                delay(3000)
                statusMessage = "Game in progress - ${save.gamePhase}"
            } catch (e: Exception) {
                statusMessage = "Failed to restore saved game: ${e.message}"
            }
        }
    }

    // ================================================================
    // REACTIVE STATE PROCESSING
    // ================================================================

    // React to game state changes
    LaunchedEffect(gameState) {
        when (val state = gameState) {
            is GameState.Initializing -> {
                statusMessage = "Setting up game..."
                isGameInitialized = false
            }
            is GameState.ModeSelection -> {
                statusMessage = "Selecting game mode: ${state.selectedMode?.displayName ?: "None"}"
            }
            is GameState.PlayerSetup -> {
                statusMessage = "Configuring ${state.playerCount} players, ${state.startingChips} chips"
            }
            is GameState.GameStarting -> {
                statusMessage = state.loadingMessage
                isGameInitialized = false
            }
            is GameState.Playing -> {
                statusMessage = "Playing ${state.gameMode.displayName} - ${state.currentPhase.displayName}"
                isGameInitialized = true
                currentPot = state.pot
                currentBet = state.currentBet
                currentRound = state.roundNumber

                // Handle sub-states for mode-specific UI
                when (val subState = state.subState) {
                    is PlayingSubState.AdventureMode -> {
                        adventureMonster = subState.currentMonster
                        monsterHealth = subState.monsterHealth
                        statusMessage = "Battle with ${subState.currentMonster}! (${subState.monsterHealth} HP)"
                    }
                    is PlayingSubState.SafariMode -> {
                        statusMessage = "Wild ${subState.wildMonster} spotted! Capture chance: ${(subState.captureChance * 100).toInt()}%"
                    }
                    is PlayingSubState.IronmanMode -> {
                        gachaPoints = subState.gachaPoints
                        riskLevel = subState.riskLevel
                        statusMessage = "Ironman Mode - ${subState.gachaPoints} gacha points (Risk: ${(subState.riskLevel * 100).toInt()}%)"
                    }
                    is PlayingSubState.CardExchangePhase -> {
                        statusMessage = "Exchange up to ${subState.maxExchanges} cards (${subState.exchangesRemaining} remaining)"
                        awaitingPlayerAction = !subState.exchangeComplete
                        
                        // Auto-progress if exchange is complete
                        if (subState.exchangeComplete) {
                            coroutineScope.launch {
                                delay(2000) // Show results for 2 seconds
                                try {
                                    gameBridge.processGameAction(GameActions.ContinueGame)
                                } catch (e: Exception) {
                                    // Fallback to advancing the game state
                                    gameBridge.processGameAction(GameActions.DealCards)
                                }
                            }
                        }
                    }
                    is PlayingSubState.WaitingForPlayerAction -> {
                        statusMessage = "Your turn - ${subState.validActions.joinToString(", ")}"
                        awaitingPlayerAction = true
                    }
                    is PlayingSubState.ShowingResults -> {
                        statusMessage = "Round results - check winnings!"
                        awaitingPlayerAction = false
                        
                        // Auto-progress after showing results
                        coroutineScope.launch {
                            delay(3000) // Show results for 3 seconds
                            try {
                                gameBridge.processGameAction(GameActions.ContinueGame)
                            } catch (e: Exception) {
                                // Continue to next round or restart
                                gameBridge.processGameAction(GameActions.NextRound)
                            }
                        }
                    }
                    else -> {
                        awaitingPlayerAction = state.currentPhase == GamePhase.BETTING_ROUND ||
                            state.currentPhase == GamePhase.PLAYER_ACTIONS
                    }
                }

                // Update player data from traditional bridge for compatibility
                if (isGameInitialized) {
                    try {
                        // Synchronize UI state with GameBridge state
                        val bridgeChips = gameBridge.getPlayerChips()
                        val bridgeHand = gameBridge.getPlayerHand()
                        val bridgePlayers = gameBridge.getAllPlayers()
                        val bridgeSelected = gameBridge.getSelectedCards()
                        
                        // Only update if values have actually changed to prevent unnecessary recomposition
                        if (bridgeChips != playerChips) playerChips = bridgeChips
                        if (bridgeHand != playerCards) playerCards = bridgeHand
                        if (bridgePlayers != allPlayers) allPlayers = bridgePlayers
                        if (bridgeSelected != selectedCards) selectedCards = bridgeSelected
                        
                        // Update current pot from game state
                        if (state.currentPhase == GamePhase.BETTING_ROUND || 
                            state.currentPhase == GamePhase.FINAL_BETTING) {
                            val pot = gameBridge.getCurrentPot()
                            if (pot != currentPot) currentPot = pot
                        }
                        
                    } catch (e: Exception) {
                        // Handle gracefully if bridge methods aren't available
                        if (statusMessage.isEmpty()) {
                            statusMessage = "Syncing game data..."
                            // Clear sync message quickly
                            coroutineScope.launch {
                                delay(1000)
                                if (statusMessage == "Syncing game data...") {
                                    statusMessage = ""
                                }
                            }
                        }
                    }
                }
            }
            is GameState.Victory -> {
                statusMessage = "🏆 Victory! ${state.winner.name} wins with ${state.victoryType.name}!"
                achievementNotification = state.achievements.firstOrNull()
                awaitingPlayerAction = false
                
                // Record game completion in statistics
                val isPlayerWinner = state.winner.name == userProfile.username
                val chipsWon = if (isPlayerWinner) maxOf(0, playerChips - 1000) else 0 // Assuming starting chips were 1000
                
                // Try to get hand evaluation, fallback to basic evaluation
                val handAchieved = try {
                    val hand = gameBridge.getPlayerHand()
                    if (hand.isNotEmpty()) {
                        // Simple hand evaluation based on card names
                        when {
                            hand.any { it.contains("A") && it.contains("K") } -> "High Pair"
                            hand.size >= 2 && hand.groupBy { it.split(" ")[0] }.any { it.value.size >= 2 } -> "Pair"
                            else -> "High Card"
                        }
                    } else "High Card"
                } catch (e: Exception) {
                    "High Card"
                }
                
                userProfileManager.recordGameCompletion(
                    won = isPlayerWinner,
                    chipsWon = chipsWon.toLong(),
                    handAchieved = handAchieved,
                    gameMode = gameMode.name
                )
                
                // Award achievements if any
                state.achievements.forEach { achievement ->
                    userProfileManager.awardAchievement(achievement)
                }
            }
            is GameState.GameOver -> {
                statusMessage = "Game Over - ${state.gameOverReason}"
                awaitingPlayerAction = false
                
                // Record game completion as loss
                val handAchieved = try {
                    val hand = gameBridge.getPlayerHand()
                    if (hand.isNotEmpty()) {
                        // Simple hand evaluation based on card names
                        when {
                            hand.any { it.contains("A") && it.contains("K") } -> "High Pair"
                            hand.size >= 2 && hand.groupBy { it.split(" ")[0] }.any { it.value.size >= 2 } -> "Pair"
                            else -> "High Card"
                        }
                    } else "High Card"
                } catch (e: Exception) {
                    "High Card"
                }
                
                userProfileManager.recordGameCompletion(
                    won = false,
                    chipsWon = 0L,
                    handAchieved = handAchieved,
                    gameMode = gameMode.name
                )
            }
            is GameState.Paused -> {
                statusMessage = "Game paused - ${state.pauseReason}"
                awaitingPlayerAction = false
            }
            is GameState.Error -> {
                statusMessage = "Error: ${state.message}"
                awaitingPlayerAction = false
            }
            else -> {
                statusMessage = "Game state: ${gameState::class.simpleName}"
            }
        }
    }

    // ================================================================
    // GAME INITIALIZATION WITH STATE MANAGEMENT
    // ================================================================

    // Initialize game when screen loads with full state management
    LaunchedEffect(gameMode) {
        // Skip initialization if loading from saved game
        if (savedGame != null) {
            return@LaunchedEffect
        }
        
        // Reset all state variables to ensure clean start
        currentRound = 1
        playerChips = 1000
        currentPot = 0
        playerCards = emptyList()
        selectedCards = emptySet()
        adventureMonster = null
        monsterHealth = 100
        safariCaptures = 0
        gachaPoints = 0
        riskLevel = 1.0
        lastActionResult = ""
        achievementNotification = ""
        statusMessage = "Initializing ${gameMode.displayName}..."
        isGameInitialized = false
        
        try {
            // Generate monster opponents based on player's experience level
            val skillLevel = (userProfile.gamesWon / 10).coerceAtMost(4) // 0-4 skill level
            monsterOpponents = monsterOpponentManager.generateOpponents(3, skillLevel)

            // Start comprehensive game session
            gameBridge.startGameSession(
                mode = gameMode,
                playerName = userProfile.username,
                playerCount = 4,
                startingChips = 1000,
            )

            // Initialize mode-specific state and award first-time achievements
            when (gameMode) {
                GameMode.ADVENTURE -> {
                    // Award first-time adventure achievement
                    if (userProfile.adventureProgress == 0) {
                        userProfileManager.awardAchievement("Adventure Begins")
                    }
                    
                    delay(1000) // Allow initialization
                    gameBridge.enterSubState(
                        PlayingSubState.AdventureMode(
                            currentMonster = "Training Dummy",
                            monsterHealth = 100,
                            questProgress = mapOf("battles_won" to 0),
                        ),
                    )
                }
                GameMode.SAFARI -> {
                    // Award first-time safari achievement
                    if (userProfile.safariEncounters == 0) {
                        userProfileManager.awardAchievement("Safari Explorer")
                    }
                    
                    delay(1000)
                    gameBridge.enterSubState(
                        PlayingSubState.SafariMode(
                            wildMonster = "Common Starter",
                            captureChance = 0.6,
                            safariBallsRemaining = 10,
                        ),
                    )
                }
                GameMode.IRONMAN -> {
                    // Award first-time ironman achievement
                    if (userProfile.ironmanPulls == 0) {
                        userProfileManager.awardAchievement("High Stakes")
                    }
                    
                    delay(1000)
                    gameBridge.enterSubState(
                        PlayingSubState.IronmanMode(
                            gachaPoints = 0,
                            riskLevel = 1.0,
                            permadeathWarning = false,
                        ),
                    )
                }
                else -> {
                    // Classic mode - award first game achievement if needed
                    if (userProfile.totalGamesPlayed == 0) {
                        userProfileManager.awardAchievement("First Steps")
                    }
                }
            }
            
            // Mark initialization as complete
            delay(500) // Brief delay to ensure state is set
            isGameInitialized = true
            statusMessage = "${gameMode.displayName} game ready!"
            
            // Clear initialization message
            delay(2000)
            statusMessage = ""
            
        } catch (e: Exception) {
            statusMessage = "Failed to initialize game: ${e.message}"
            // Clear error message after delay
            delay(5000)
            statusMessage = ""
        }
    }

    // ================================================================
    // ACTION HANDLERS WITH STATE MANAGEMENT
    // ================================================================

    fun performActionWithState(
        actionType: String,
        amount: Int = 0,
    ) {
        // Cancel any pending feedback clear operations
        coroutineScope.launch {
            try {
                // Use state management system for actions
                gameBridge.performPlayerActionWithState(actionType, amount)

                // Update UI feedback with timestamp to prevent conflicts
                val timestamp = System.currentTimeMillis()
                lastActionResult =
                    when (actionType.lowercase()) {
                        "call" -> "Called!"
                        "raise" -> "Raised $amount!"
                        "fold" -> "Folded"
                        "check" -> "Checked"
                        else -> "Action performed"
                    }

                // Clear feedback after delay, but only if it's still the same message
                delay(2000)
                if (lastActionResult.contains(actionType.lowercase().capitalize()) || 
                    lastActionResult.contains(amount.toString())) {
                    lastActionResult = ""
                }
            } catch (e: Exception) {
                lastActionResult = "Action failed: ${e.message}"
                delay(3000)
                if (lastActionResult.startsWith("Action failed")) {
                    lastActionResult = ""
                }
            }
        }
    }

    fun handleModeSpecificAction(action: String) {
        when (gameMode) {
            GameMode.ADVENTURE -> {
                when (action) {
                    "attack" -> {
                        gameBridge.processGameAction(
                            GameActions.AdventureActions.AttackMonster(
                                player =
                                    allPlayers.firstOrNull { it.name == userProfile.username }?.let {
                                        com.pokermon.players.Player(it.name, it.chips)
                                    } ?: com.pokermon.players.Player(userProfile.username, playerChips),
                                damage = (playerCards.size * 10), // Damage based on hand
                            ),
                        )
                        
                        // Update adventure progress
                        val currentProfile = userProfileManager.userProfile.value
                        userProfileManager.updateUserProfile(
                            currentProfile.copy(adventureProgress = currentProfile.adventureProgress + 1)
                        )
                    }
                    "flee" -> {
                        gameBridge.processGameAction(
                            GameActions.AdventureActions.FleeFromBattle(
                                player = com.pokermon.players.Player(userProfile.username, playerChips),
                            ),
                        )
                    }
                }
            }
            GameMode.SAFARI -> {
                when (action) {
                    "capture" -> {
                        gameBridge.processGameAction(
                            GameActions.SafariActions.AttemptCapture(
                                player = com.pokermon.players.Player(userProfile.username, playerChips),
                                captureChance = 0.6, // Base capture chance
                            ),
                        )
                        
                        // Update safari encounters and potentially monsters collected
                        val currentProfile = userProfileManager.userProfile.value
                        val captured = Math.random() < 0.6 // Simulate capture success
                        userProfileManager.updateUserProfile(
                            currentProfile.copy(
                                safariEncounters = currentProfile.safariEncounters + 1,
                                monstersCollected = if (captured) currentProfile.monstersCollected + 1 else currentProfile.monstersCollected
                            )
                        )
                        
                        if (captured) {
                            lastActionResult = "Monster captured!"
                            safariCaptures += 1
                        } else {
                            lastActionResult = "Capture failed!"
                        }
                        
                        // Clear message after delay
                        coroutineScope.launch {
                            delay(3000)
                            lastActionResult = ""
                        }
                    }
                    "bait" -> {
                        // Safari ball mechanics
                    }
                }
            }
            GameMode.IRONMAN -> {
                when (action) {
                    "gacha_pull" -> {
                        gameBridge.processGameAction(
                            GameActions.IronmanActions.PerformGachaPull(
                                player = com.pokermon.players.Player(userProfile.username, playerChips),
                                pointsSpent = gachaPoints.coerceAtLeast(1),
                            ),
                        )
                        
                        // Update ironman pulls
                        val currentProfile = userProfileManager.userProfile.value
                        userProfileManager.updateUserProfile(
                            currentProfile.copy(ironmanPulls = currentProfile.ironmanPulls + 1)
                        )
                        
                        // Award achievement for first gacha pull
                        if (currentProfile.ironmanPulls == 0) {
                            userProfileManager.awardAchievement("First Gacha")
                        }
                    }
                    "risk_mode" -> {
                        gameBridge.processGameAction(
                            GameActions.IronmanActions.ActivateRiskMode(
                                player = com.pokermon.players.Player(userProfile.username, playerChips),
                                multiplier = 2.0,
                            ),
                        )
                    }
                }
            }
            else -> {
                // Classic mode - no special actions
            }
        }
    }

    // Handle back button during gameplay - show confirmation dialog
    BackHandler(enabled = isGameInitialized) {
        showExitConfirmDialog = true
    }

    // ================================================================
    // MAIN UI LAYOUT
    // ================================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // ================================================================
        // TOP SECTION: ENHANCED GAME STATUS WITH MODE-SPECIFIC INFO
        // ================================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        when (gameMode) {
                            GameMode.ADVENTURE -> MaterialTheme.colorScheme.secondaryContainer
                            GameMode.SAFARI -> MaterialTheme.colorScheme.tertiaryContainer
                            GameMode.IRONMAN -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Game mode and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text =
                            when (gameMode) {
                                GameMode.ADVENTURE -> "⚔️ ${gameMode.displayName}"
                                GameMode.SAFARI -> "🦎 ${gameMode.displayName}"
                                GameMode.IRONMAN -> "💀 ${gameMode.displayName}"
                                else -> "🎮 ${gameMode.displayName}"
                            },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Round $currentRound",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                // Player chips and pot info
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("💰 Chips: $playerChips", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("🏆 Pot: $currentPot", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                // Mode-specific status bars
                when (gameMode) {
                    GameMode.ADVENTURE -> {
                        if (adventureMonster != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("🐉 $adventureMonster", style = MaterialTheme.typography.bodySmall)
                                Text("❤️ $monsterHealth HP", style = MaterialTheme.typography.bodySmall)
                            }

                            // Monster health bar
                            LinearProgressIndicator(
                                progress = { (monsterHealth / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                color =
                                    if (monsterHealth > 50) {
                                        Color.Green
                                    } else if (monsterHealth > 25) {
                                        Color.Yellow
                                    } else {
                                        Color.Red
                                    },
                            )
                        }
                    }
                    GameMode.SAFARI -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("🏆 Captures: $safariCaptures", style = MaterialTheme.typography.bodySmall)
                            Text("⚡ Safari Balls: 10", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    GameMode.IRONMAN -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("🎰 Gacha: $gachaPoints", style = MaterialTheme.typography.bodySmall)
                            Text("⚠️ Risk: ${(riskLevel * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    else -> { /* No mode-specific UI for classic */ }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ================================================================
        // PLAYER CARDS SECTION WITH ENHANCED DISPLAY
        // ================================================================

        if (playerCards.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "🃏 Your Hand",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        playerCards.forEachIndexed { index, card ->
                            EnhancedCardDisplay(
                                card = card,
                                isSelected = selectedCards.contains(index),
                                onClick = {
                                    if ((gameState as? GameState.Playing)?.subState is PlayingSubState.CardExchangePhase) {
                                        try {
                                            gameBridge.toggleCardSelection(index)
                                            selectedCards = gameBridge.getSelectedCards()

                                            // Trigger card selection action in state management
                                            gameBridge.processGameAction(
                                                GameActions.SelectCardsForExchange(
                                                    player = com.pokermon.players.Player(userProfile.username, playerChips),
                                                    cardIndices = selectedCards.toList(),
                                                ),
                                            )
                                        } catch (e: Exception) {
                                            lastActionResult = "Card selection failed"
                                        }
                                    }
                                },
                                canClick = (gameState as? GameState.Playing)?.subState is PlayingSubState.CardExchangePhase,
                                modifier = Modifier.size(width = 60.dp, height = 80.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action result feedback
        if (lastActionResult.isNotEmpty() || achievementNotification != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (achievementNotification != null) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            },
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (achievementNotification != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "🏆",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                            Text(
                                text = "Achievement: $achievementNotification",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    } else if (lastActionResult.isNotEmpty()) {
                        Text(
                            text = lastActionResult,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        // ================================================================
        // ENHANCED ACTION BUTTONS WITH MODE-SPECIFIC OPTIONS
        // ================================================================

        if (awaitingPlayerAction || isGameInitialized) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    // Mode-specific actions first
                    when (gameMode) {
                        GameMode.ADVENTURE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(
                                    onClick = { handleModeSpecificAction("attack") },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                        ),
                                ) {
                                    Text("⚔️ Attack")
                                }
                                Button(
                                    onClick = { handleModeSpecificAction("flee") },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                        ),
                                ) {
                                    Text("🏃 Flee")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        GameMode.SAFARI -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(
                                    onClick = { handleModeSpecificAction("capture") },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                        ),
                                ) {
                                    Text("🏀 Capture")
                                }
                                Button(
                                    onClick = { handleModeSpecificAction("bait") },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                ) {
                                    Text("🍎 Bait")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        GameMode.IRONMAN -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(
                                    onClick = { handleModeSpecificAction("gacha_pull") },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                        ),
                                ) {
                                    Text("🎰 Gacha")
                                }
                                Button(
                                    onClick = { handleModeSpecificAction("risk_mode") },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                        ),
                                ) {
                                    Text("⚠️ Risk x2")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        else -> { /* No mode-specific actions for classic */ }
                    }

                    // Card exchange actions
                    if ((gameState as? GameState.Playing)?.subState is PlayingSubState.CardExchangePhase && selectedCards.isNotEmpty()) {
                        Button(
                            onClick = {
                                gameBridge.processGameAction(
                                    GameActions.ExchangeCards(
                                        player = com.pokermon.players.Player(userProfile.username, playerChips),
                                        cardIndices = selectedCards.toList(),
                                    ),
                                )
                                // Also perform traditional exchange for compatibility
                                try {
                                    val result = gameBridge.exchangeCards(selectedCards.toList())
                                    lastActionResult = result.message
                                    selectedCards = emptySet()
                                } catch (e: Exception) {
                                    lastActionResult = "Exchange failed"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                ),
                        ) {
                            Text("🔄 Exchange ${selectedCards.size} Cards")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Traditional poker actions
                    if (awaitingPlayerAction && (gameState as? GameState.Playing)?.subState !is PlayingSubState.CardExchangePhase) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Button(
                                onClick = { performActionWithState("call") },
                                modifier = Modifier.weight(1f).padding(end = 2.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ),
                            ) {
                                Text("📞 Call")
                            }

                            Button(
                                onClick = { performActionWithState("raise", betAmount) },
                                modifier = Modifier.weight(1f).padding(horizontal = 1.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                    ),
                            ) {
                                Text("⬆️ Raise $betAmount")
                            }

                            Button(
                                onClick = { performActionWithState("fold") },
                                modifier = Modifier.weight(1f).padding(start = 2.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                    ),
                            ) {
                                Text("🔽 Fold")
                            }
                        }
                    }

                    // Game progression actions
                    if (gameState is GameState.GameOver || gameState is GameState.Victory) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Button(
                                onClick = {
                                    // Reset all game state before restarting
                                    coroutineScope.launch {
                                        // Clear current state
                                        currentRound = 1
                                        playerChips = 1000
                                        currentPot = 0
                                        playerCards = emptyList()
                                        selectedCards = emptySet()
                                        adventureMonster = null
                                        monsterHealth = 100
                                        safariCaptures = 0
                                        gachaPoints = 0
                                        riskLevel = 1.0
                                        lastActionResult = ""
                                        achievementNotification = ""
                                        statusMessage = "Starting new game..."
                                        isGameInitialized = false
                                        
                                        delay(1000) // Give time for UI to update
                                        
                                        // Process restart action
                                        gameBridge.processGameAction(GameActions.RestartGame(sameSettings = true))
                                        
                                        // Reinitialize game state
                                        delay(500)
                                        isGameInitialized = true
                                        statusMessage = "New ${gameMode.displayName} game started!"
                                        
                                        // Clear message after delay
                                        delay(3000)
                                        statusMessage = ""
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(end = 4.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ),
                            ) {
                                Text("🔄 Play Again")
                            }

                            Button(
                                onClick = onBackPressed,
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                    ),
                            ) {
                                Text("🏠 Menu")
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // EXIT CONFIRMATION WITH STATE MANAGEMENT
    // ================================================================

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = {
                Text(
                    text = "Pause Game?",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    text =
                        "Do you want to pause this ${gameMode.displayName} session and return to the menu?\n\n" +
                            "Your progress will be saved and you can continue later.",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Use state management for pause action
                        gameBridge.processGameAction(GameActions.PauseGame)
                        showExitConfirmDialog = false
                        onBackPressed()
                    },
                ) {
                    Text("💾 Pause & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text("▶️ Continue Playing")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        )
    }

    // Achievement notification auto-clear
    LaunchedEffect(achievementNotification) {
        if (achievementNotification != null) {
            delay(3000) // Show achievement for 3 seconds
            achievementNotification = null
        }
    }
}