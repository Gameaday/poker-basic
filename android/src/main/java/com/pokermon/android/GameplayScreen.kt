package com.pokermon.android

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.GameMode
import com.pokermon.GamePhase
import com.pokermon.android.data.UserProfileManager
import com.pokermon.android.data.MonsterOpponentManager
import com.pokermon.android.data.MonsterOpponent
import com.pokermon.android.ui.EnhancedCardDisplay
import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.PlayerInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val BET_INCREMENT = 10

/**
 * Enhanced gameplay screen with compact layout that fits on one screen without scrolling.
 * Addresses gameplay flow issues and integrates monster system for engaging opponents.
 */
@Composable
fun GameplayScreen(
    gameMode: GameMode,
    onBackPressed: () -> Unit
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
    
    // Function to refresh all game data including phase information
    fun refreshGameData() {
        if (isGameInitialized) {
            playerChips = gameBridge.getPlayerChips()
            currentPot = gameBridge.getCurrentPot()
            playerCards = gameBridge.getPlayerHand()
            allPlayers = gameBridge.getAllPlayers()
            currentRound = gameBridge.getCurrentRound()
            isRoundComplete = gameBridge.isRoundComplete()
            
            // Update phase-specific state
            currentPhase = gameBridge.getCurrentPhase()
            phaseDisplayName = gameBridge.getPhaseDisplayName()
            phaseDescription = gameBridge.getPhaseDescription()
            shouldShowCards = gameBridge.shouldShowCards()
            canBet = gameBridge.canBet()
            canExchangeCards = gameBridge.canExchangeCards()
            canProgressRound = gameBridge.canProgressRound()
            
            // Update selected cards from bridge
            selectedCards = gameBridge.getSelectedCards()
            
            // Update waiting for player action
            awaitingPlayerAction = canBet || canExchangeCards
            
            // Check for AI action feedback
            gameBridge.getLastAIAction()?.let { aiAction ->
                aiActionMessage = aiAction.message
                coroutineScope.launch {
                    delay(2000) // Show for 2 seconds
                    gameBridge.clearLastAIAction()
                    aiActionMessage = ""
                }
            }
        }
    }
    
    // Initialize game when screen loads  
    LaunchedEffect(gameMode) {
        gameBridge.setGameMode(gameMode)
        
        // Enable automatic AI processing for Android UI
        gameBridge.setAutoAIEnabled(true)
        
        // Generate monster opponents based on player's experience level
        val skillLevel = (userProfile.gamesWon / 10).coerceAtMost(4) // 0-4 skill level
        monsterOpponents = monsterOpponentManager.generateOpponents(3, skillLevel)
        
        val success = gameBridge.initializeGame(userProfile.username, 3, 1000)
        if (success) {
            isGameInitialized = true
            initialChips = 1000
            refreshGameData()
            gameState = phaseDescription
            awaitingPlayerAction = canBet || canExchangeCards
        } else {
            gameState = "Failed to initialize game"
        }
    }
    
    // Handle back button during gameplay - show confirmation dialog
    BackHandler(enabled = isGameInitialized) {
        showExitConfirmDialog = true
    }
    
    // Compact layout that fits on one screen without scrolling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: Game stats and phase info (compact)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Compact game stats
            Card(
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💰 $playerChips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your Chips",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎯 $currentPot",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pot",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "R$currentRound",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Round",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        
        // Player hand section (compact)
        if (shouldShowCards && playerCards.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🃏 Your Hand",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    if (canExchangeCards && selectedCards.isNotEmpty()) {
                        Text(
                            text = "${selectedCards.size} selected for exchange",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    // Compact card display
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        playerCards.forEachIndexed { index, card ->
                            EnhancedCardDisplay(
                                card = card,
                                isSelected = canExchangeCards && selectedCards.contains(index),
                                onClick = if (canExchangeCards) { 
                                    { 
                                        // Use bridge method for consistent card selection logic
                                        gameBridge.toggleCardSelection(index)
                                        // Update local state to reflect bridge state
                                        selectedCards = gameBridge.getSelectedCards()
                                    } 
                                } else { {} },
                                canClick = canExchangeCards,
                                modifier = Modifier.size(width = 48.dp, height = 64.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Game phase and status (compact)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = phaseDisplayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = gameState,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (lastActionResult.isNotEmpty()) {
                    Text(
                        text = lastActionResult,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
        
        // AI feedback area (when available)
        if (aiActionMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = aiActionMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
        
        // Bottom section: Game actions (compact)
        if (awaitingPlayerAction || canProgressRound || isRoundComplete) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Exchange cards section (if applicable)
                    if (canExchangeCards && selectedCards.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val result = gameBridge.exchangeCards(selectedCards.toList())
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                        selectedCards = emptySet()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Exchange ${selectedCards.size} Cards", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    
                    // Betting actions (if applicable)  
                    if (canBet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val result = gameBridge.performCall()
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(end = 2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Call", style = MaterialTheme.typography.labelSmall)
                            }
                            
                            Button(
                                onClick = {
                                    val result = gameBridge.performRaise(betAmount)
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(horizontal = 1.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Raise $betAmount", style = MaterialTheme.typography.labelSmall)
                            }
                            
                            Button(
                                onClick = {
                                    val result = gameBridge.performFold()
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(start = 2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Fold", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    
                    // Round progression actions (if applicable)
                    if (canProgressRound && isRoundComplete) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val result = gameBridge.determineWinner()
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(end = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Show Winner", style = MaterialTheme.typography.labelSmall)
                            }
                            
                            Button(
                                onClick = {
                                    val result = gameBridge.nextRound()
                                    lastActionResult = result.message
                                    if (result.success) {
                                        refreshGameData()
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Next Round", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Exit confirmation dialog with improved opacity
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { 
                Text(
                    text = "Pause Game?", 
                    color = MaterialTheme.colorScheme.onSurface
                ) 
            },
            text = { 
                Text(
                    text = "Do you want to pause this round and return to the menu?\n\n" +
                          "Your game session will be preserved and you can continue later.",
                    color = MaterialTheme.colorScheme.onSurface
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Save game state before exiting
                        if (gameSettings.autoSaveEnabled) {
                            // Game state is automatically preserved in the bridge
                            // The user can return to continue the same session
                        }
                        showExitConfirmDialog = false
                        onBackPressed()
                    }
                ) {
                    Text("Pause & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text("Continue Playing")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f) // More opaque background
        )
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
            val displayRank = when (rank) {
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
            val displayRank = when (rank) {
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
            val displayRank = when (rank) {
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
            val displayRank = when (rank) {
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