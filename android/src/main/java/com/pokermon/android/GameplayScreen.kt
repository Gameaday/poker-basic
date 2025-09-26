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
 * Enhanced gameplay screen with comprehensive Flow-based state management integration.
 * Features full reactive UI that responds to all game state changes and sub-states.
 * Demonstrates complete Android experience leveraging the state management system.
 */
@Composable
fun GameplayScreen(
    gameMode: GameMode,
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
    val gameEvents by gameBridge.gameEventsFlow.collectAsState(initial = emptyList<GameActions>())

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
                    }
                    is PlayingSubState.WaitingForPlayerAction -> {
                        statusMessage = "Your turn - ${subState.validActions.joinToString(", ")}"
                        awaitingPlayerAction = true
                    }
                    is PlayingSubState.ShowingResults -> {
                        statusMessage = "Round results - check winnings!"
                        awaitingPlayerAction = false
                    }
                    else -> {
                        awaitingPlayerAction = state.currentPhase == GamePhase.BETTING_ROUND ||
                            state.currentPhase == GamePhase.PLAYER_ACTIONS
                    }
                }

                // Update player data from traditional bridge for compatibility
                if (isGameInitialized) {
                    try {
                        playerChips = gameBridge.getPlayerChips()
                        playerCards = gameBridge.getPlayerHand()
                        allPlayers = gameBridge.getAllPlayers()
                        selectedCards = gameBridge.getSelectedCards()
                    } catch (e: Exception) {
                        // Handle gracefully if bridge methods aren't available
                        statusMessage = "Updating game data..."
                    }
                }
            }
            is GameState.Victory -> {
                statusMessage = "🏆 Victory! ${state.winner.name} wins with ${state.victoryType.name}!"
                achievementNotification = state.achievements.firstOrNull()
                awaitingPlayerAction = false
            }
            is GameState.GameOver -> {
                statusMessage = "Game Over - ${state.gameOverReason}"
                awaitingPlayerAction = false
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

            // Initialize mode-specific state
            when (gameMode) {
                GameMode.ADVENTURE -> {
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
                    // Classic mode - no special sub-state needed
                }
            }
        } catch (e: Exception) {
            statusMessage = "Failed to initialize game: ${e.message}"
        }
    }

    // ================================================================
    // ACTION HANDLERS WITH STATE MANAGEMENT
    // ================================================================

    fun performActionWithState(
        actionType: String,
        amount: Int = 0,
    ) {
        try {
            // Use state management system for actions
            gameBridge.performPlayerActionWithState(actionType, amount)

            // Update UI feedback
            lastActionResult =
                when (actionType.lowercase()) {
                    "call" -> "Called!"
                    "raise" -> "Raised $amount!"
                    "fold" -> "Folded"
                    "check" -> "Checked"
                    else -> "Action performed"
                }

            // Clear feedback after delay
            coroutineScope.launch {
                delay(2000)
                lastActionResult = ""
            }
        } catch (e: Exception) {
            lastActionResult = "Action failed: ${e.message}"
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
                                captureChance = 0.6 + (playerCards.size * 0.1), // Better hand = better capture chance
                            ),
                        )
                    }
                    "approach" -> {
                        gameBridge.processGameAction(
                            GameActions.SafariActions.ApproachMonster(
                                player = com.pokermon.players.Player(userProfile.username, playerChips),
                                cautious = true,
                            ),
                        )
                    }
                }
            }
            GameMode.IRONMAN -> {
                when (action) {
                    "gacha_pull" -> {
                        if (gachaPoints >= 100) {
                            gameBridge.processGameAction(
                                GameActions.IronmanActions.PerformGachaPull(
                                    player = com.pokermon.players.Player(userProfile.username, playerChips),
                                    pointsSpent = 100,
                                    pullType = "standard",
                                ),
                            )
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
    // ENHANCED UI WITH STATE MANAGEMENT INTEGRATION
    // ================================================================
    @Composable
    fun GameplayScreen(
        gameMode: GameMode,
        onBackPressed: () -> Unit,
    ) {
        val context = LocalContext.current
        val userProfileManager = remember { UserProfileManager.getInstance(context) }
        val userProfile by userProfileManager.userProfile.collectAsState()
        val gameSettings by userProfileManager.gameSettings.collectAsState()

        val gameBridge = remember { GameLogicBridge() }
        val monsterOpponentManager = remember { MonsterOpponentManager() }
        val coroutineScope = rememberCoroutineScope()

        var gameState by remember { mutableStateOf("Initializing game...") }
        var playerChips by remember { mutableIntStateOf(1000) }
        var currentPot by remember { mutableIntStateOf(0) }
        var playerCards by remember { mutableStateOf(listOf<String>()) }
        var allPlayers by remember { mutableStateOf(listOf<PlayerInfo>()) }
        var monsterOpponents by remember { mutableStateOf(listOf<MonsterOpponent>()) }
        var isGameInitialized by remember { mutableStateOf(false) }
        var awaitingPlayerAction by remember { mutableStateOf(false) }
        var lastActionResult by remember { mutableStateOf("") }
        var betAmount by remember { mutableIntStateOf(50) }
        var selectedCards by remember { mutableStateOf(setOf<Int>()) }
        var currentRound by remember { mutableIntStateOf(0) }
        var isRoundComplete by remember { mutableStateOf(false) }
        var initialChips by remember { mutableIntStateOf(1000) }

        // Back button protection
        var showExitConfirmDialog by remember { mutableStateOf(false) }

        // Game phase state
        var currentPhase by remember { mutableStateOf<GamePhase>(GamePhase.INITIALIZATION) }
        var phaseDisplayName by remember { mutableStateOf("Initializing...") }
        var phaseDescription by remember { mutableStateOf("Setting up game") }
        var shouldShowCards by remember { mutableStateOf(false) }
        var canBet by remember { mutableStateOf(false) }
        var canExchangeCards by remember { mutableStateOf(false) }
        var canProgressRound by remember { mutableStateOf(false) }

        // AI action feedback
        var aiActionMessage by remember { mutableStateOf("") }

        // Comprehensive reactive UI with state management integration
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
                                    progress = (monsterHealth / 100f).coerceIn(0f, 1f),
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
                                Text("🎲 Gacha: $gachaPoints pts", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "⚠️ Risk: ${(riskLevel * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (riskLevel > 1.5) Color.Red else MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                        else -> { /* Classic mode - no additional UI */ }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ================================================================
            // PLAYER STATS WITH COMPREHENSIVE INFO
            // ================================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Card(
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
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
                            text = "💰 $playerChips",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Your Chips",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "🎯 $currentPot",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Pot",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "🃏 ${playerCards.size}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Cards",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ================================================================
            // PLAYER HAND WITH ENHANCED CARD INTERACTION
            // ================================================================

            if (playerCards.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "🃏 Your Hand",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )

                            if (selectedCards.isNotEmpty()) {
                                Text(
                                    text = "${selectedCards.size} selected",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Enhanced card display with selection
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            playerCards.forEachIndexed { index, card ->
                                EnhancedCardDisplay(
                                    card = card,
                                    isSelected = selectedCards.contains(index),
                                    onClick = {
                                        // Enhanced card selection with state management feedback
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

            // ================================================================
            // OPPONENTS WITH MONSTER INTEGRATION
            // ================================================================

            if (allPlayers.size > 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = "👥 Opponents",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            allPlayers.drop(1).forEachIndexed { index, player ->
                                val monster = monsterOpponents.getOrNull(index)

                                Card(
                                    modifier = Modifier.size(width = 120.dp, height = 90.dp),
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                if (player.isFolded) {
                                                    MaterialTheme.colorScheme.errorContainer
                                                } else {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                },
                                        ),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(
                                            text = getMonsterEmoji(monster?.displayName ?: player.name),
                                            style = MaterialTheme.typography.titleLarge,
                                        )

                                        Text(
                                            text = (monster?.displayName ?: player.name).take(10),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                        )

                                        if (player.isFolded) {
                                            Text(
                                                text = "FOLDED",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        } else {
                                            Text(
                                                text = "💰${player.chips}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ================================================================
            // ACTION FEEDBACK AND NOTIFICATIONS
            // ================================================================

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
                                        Text("🎯 Capture")
                                    }
                                    Button(
                                        onClick = { handleModeSpecificAction("approach") },
                                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                            ),
                                    ) {
                                        Text("👀 Observe")
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
                                        enabled = gachaPoints >= 100,
                                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary,
                                            ),
                                    ) {
                                        Text("🎲 Gacha (100)")
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
                                        gameBridge.processGameAction(GameActions.RestartGame(sameSettings = true))
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
                                    onClick = {
                                        gameBridge.processGameAction(GameActions.ReturnToMainMenu)
                                        onBackPressed()
                                    },
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

        // ================================================================
        // ACHIEVEMENT NOTIFICATION HANDLER
        // ================================================================

        if (achievementNotification != null) {
            LaunchedEffect(achievementNotification) {
                delay(3000) // Show achievement for 3 seconds
                achievementNotification = null
            }
        }
    }
}

/**
 * Get monster emoji for display based on monster name.
 */
private fun getMonsterEmoji(monsterName: String): String {
    return when {
        monsterName.contains("Pup") || monsterName.contains("Dog") -> "🐕"
        monsterName.contains("Bird") -> "🐦"
        monsterName.contains("Cat") -> "🐱"
        monsterName.contains("Fox") -> "🦊"
        monsterName.contains("Turtle") -> "🐢"
        monsterName.contains("Shark") -> "🦈"
        monsterName.contains("Raven") -> "🐦‍⬛"
        monsterName.contains("Dragon") -> "🐉"
        monsterName.contains("Phoenix") -> "🔥"
        monsterName.contains("Ninja") -> "🥷"
        monsterName.contains("Quokka") -> "🐹"
        monsterName.contains("AI") || monsterName.contains("Algorithm") -> "🤖"
        monsterName.contains("Daemon") || monsterName.contains("Compiler") -> "👾"
        else -> "🎮"
    }
}

/**
 * Parse card string to extract rank, suit symbol, and color for display.
 * Handles both full format ("Ace of Spades") and poker notation ("A♠").
 */
private fun parseCardDisplay(card: String): Triple<String, String, Color> {
    // Handle full format first (e.g., "Ace of Spades")
    when {
        card.contains(" of Spades") -> {
            val rank = card.replace(" of Spades", "").trim()
            val displayRank =
                when (rank) {
                    "Ace" -> "A"
                    "King" -> "K"
                    "Queen" -> "Q"
                    "Jack" -> "J"
                    "Ten" -> "10"
                    else -> rank.firstOrNull()?.toString() ?: rank
                }
            return Triple(displayRank, "♠", Color.Black)
        }
        card.contains(" of Hearts") -> {
            val rank = card.replace(" of Hearts", "").trim()
            val displayRank =
                when (rank) {
                    "Ace" -> "A"
                    "King" -> "K"
                    "Queen" -> "Q"
                    "Jack" -> "J"
                    "Ten" -> "10"
                    else -> rank.firstOrNull()?.toString() ?: rank
                }
            return Triple(displayRank, "♥", Color.Red)
        }
        card.contains(" of Diamonds") -> {
            val rank = card.replace(" of Diamonds", "").trim()
            val displayRank =
                when (rank) {
                    "Ace" -> "A"
                    "King" -> "K"
                    "Queen" -> "Q"
                    "Jack" -> "J"
                    "Ten" -> "10"
                    else -> rank.firstOrNull()?.toString() ?: rank
                }
            return Triple(displayRank, "♦", Color.Red)
        }
        card.contains(" of Clubs") -> {
            val rank = card.replace(" of Clubs", "").trim()
            val displayRank =
                when (rank) {
                    "Ace" -> "A"
                    "King" -> "K"
                    "Queen" -> "Q"
                    "Jack" -> "J"
                    "Ten" -> "10"
                    else -> rank.firstOrNull()?.toString() ?: rank
                }
            return Triple(displayRank, "♣", Color.Black)
        }
        // Handle poker notation format (e.g., "A♠")
        card.contains("♠") -> {
            val rank = card.replace("♠", "").trim()
            return Triple(rank, "♠", Color.Black)
        }
        card.contains("♥") -> {
            val rank = card.replace("♥", "").trim()
            return Triple(rank, "♥", Color.Red)
        }
        card.contains("♦") -> {
            val rank = card.replace("♦", "").trim()
            return Triple(rank, "♦", Color.Red)
        }
        card.contains("♣") -> {
            val rank = card.replace("♣", "").trim()
            return Triple(rank, "♣", Color.Black)
        }
        else -> {
            // Fallback for unparseable cards
            return Triple(card, "", Color.Gray)
        }
    }
}
