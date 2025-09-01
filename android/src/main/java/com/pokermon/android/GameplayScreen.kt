package com.pokermon.android

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.PlayerInfo

private const val BET_INCREMENT = 10

/**
 * Enhanced gameplay screen with user profile integration for tracking game progress.
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
    var gameState by remember { mutableStateOf("Initializing game...") }
    var playerChips by remember { mutableIntStateOf(1000) }
    var currentPot by remember { mutableIntStateOf(0) }
    var playerCards by remember { mutableStateOf(listOf<String>()) }
    var allPlayers by remember { mutableStateOf(listOf<PlayerInfo>()) }
    var isGameInitialized by remember { mutableStateOf(false) }
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
        }
    }
    
    // Initialize game when screen loads  
    LaunchedEffect(gameMode) {
        gameBridge.setGameMode(gameMode)
        val success = gameBridge.initializeGame(userProfile.username, 3, 1000)
        if (success) {
            isGameInitialized = true
            initialChips = 1000
            refreshGameData()
            gameState = phaseDescription
        } else {
            gameState = "Failed to initialize game"
        }
    }
    
    // Handle back button during gameplay - show confirmation dialog
    BackHandler(enabled = isGameInitialized) {
        showExitConfirmDialog = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with round and phase info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🐲 ${gameMode.displayName}",
                    style = MaterialTheme.typography.headlineMedium
                )
                if (isGameInitialized) {
                    Text(
                        text = phaseDisplayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            if (isGameInitialized) {
                Text(
                    text = "Round $currentRound",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Game stats
        GameStatsCard(
            playerChips = playerChips,
            currentPot = currentPot,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // All players information
        if (allPlayers.isNotEmpty()) {
            AllPlayersCard(
                players = allPlayers,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Player hand with card selection for exchange - only show if phase allows
        if (shouldShowCards && playerCards.isNotEmpty()) {
            PlayerHandCard(
                cards = playerCards,
                selectedCards = selectedCards,
                onCardSelected = if (canExchangeCards) { cardIndex ->
                    selectedCards = if (selectedCards.contains(cardIndex)) {
                        selectedCards - cardIndex
                    } else {
                        selectedCards + cardIndex
                    }
                    gameBridge.toggleCardSelection(cardIndex)
                } else { _ -> 
                    // Card selection disabled when not in exchange phase
                },
                canSelectCards = canExchangeCards,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Game actions - only show if phase allows betting
        if (isGameInitialized && canBet) {
            GameActionsCard(
                onCall = {
                    val result = gameBridge.performCall()
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                    }
                },
                onRaise = { amount ->
                    val result = gameBridge.performRaise(amount)
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                    }
                },
                onFold = {
                    val result = gameBridge.performFold()
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                    }
                },
                onCheck = {
                    val result = gameBridge.performCheck()
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                    }
                },
                onExchangeCards = if (canExchangeCards) { {
                    if (selectedCards.isNotEmpty()) {
                        val result = gameBridge.exchangeCards(selectedCards.toList())
                        gameState = result.message
                        if (result.success) {
                            selectedCards = emptySet()
                            refreshGameData()
                        }
                    } else {
                        gameState = "Select cards to exchange first"
                    }
                } } else { {
                    gameState = "Card exchange not available in current phase"
                } },
                betAmount = betAmount,
                onBetAmountChanged = { betAmount = it },
                hasSelectedCards = selectedCards.isNotEmpty(),
                canExchangeCards = canExchangeCards,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Card exchange actions - separate section when in exchange phase
        if (isGameInitialized && canExchangeCards && !canBet) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔄 Card Exchange",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = if (selectedCards.isNotEmpty()) 
                            "${selectedCards.size} card(s) selected for exchange"
                        else "Select cards to exchange (tap cards above)",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (selectedCards.isNotEmpty()) {
                                    val result = gameBridge.exchangeCards(selectedCards.toList())
                                    gameState = result.message
                                    if (result.success) {
                                        selectedCards = emptySet()
                                        refreshGameData()
                                    }
                                } else {
                                    gameState = "Select cards to exchange first"
                                }
                            },
                            enabled = selectedCards.isNotEmpty()
                        ) {
                            Text("Exchange Selected")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                val result = gameBridge.completeCardExchange()
                                gameState = result.message
                                if (result.success) {
                                    selectedCards = emptySet()
                                    refreshGameData()
                                }
                            }
                        ) {
                            Text("Skip Exchange")
                        }
                    }
                }
            }
        }
        
        // Round management - only show if phase allows round progression  
        if (isGameInitialized && canProgressRound) {
            RoundManagementCard(
                isRoundComplete = isRoundComplete,
                onNextRound = {
                    val result = gameBridge.nextRound()
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                    }
                },
                onDetermineWinner = {
                    val result = gameBridge.determineWinner()
                    gameState = result.message
                    if (result.success) {
                        refreshGameData()
                        
                        // Track game completion in user profile if auto-save is enabled
                        if (gameSettings.autoSaveEnabled) {
                            val finalChips = gameBridge.getPlayerChips()
                            val chipsWon = finalChips - initialChips
                            val playerWon = chipsWon > 0
                            val playerInfo = allPlayers.firstOrNull { it.name == userProfile.username }
                            val handAchieved = playerInfo?.let { 
                                // Try to get the best hand achieved during the game
                                // For now, use a simplified approach
                                when {
                                    finalChips > initialChips * 2 -> "High Win"
                                    finalChips > initialChips -> "Good Hand"
                                    else -> "High Card"
                                }
                            } ?: "High Card"
                            
                            // Record the game completion
                            userProfileManager.recordGameCompletion(
                                won = playerWon,
                                chipsWon = chipsWon.toLong(),
                                handAchieved = handAchieved,
                                gameMode = gameMode.name
                            )
                            
                            // Auto-save current game state if available
                            gameBridge.saveGameState()
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Game status with phase information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isGameInitialized) {
                    Text(
                        text = phaseDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = phaseDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                Text(
                    text = gameState,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Back button
        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }
    
    // Exit confirmation dialog
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { Text("⚠️ Exit Game") },
            text = { 
                Text("Are you sure you want to exit the current game?\n\nYour progress will be lost if you haven't saved.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirmDialog = false
                        onBackPressed()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Exit Game")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text("Continue Playing")
                }
            }
        )
    }
}

@Composable
fun GameStatsCard(
    playerChips: Int,
    currentPot: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Your Chips",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "$playerChips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Current Pot",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "$currentPot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlayerHandCard(
    cards: List<String>,
    selectedCards: Set<Int>,
    onCardSelected: (Int) -> Unit,
    canSelectCards: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🃏 Your Hand",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (canSelectCards && selectedCards.isNotEmpty()) {
                Text(
                    text = "${selectedCards.size} card(s) selected for exchange",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    CardDisplay(
                        card = card,
                        isSelected = canSelectCards && selectedCards.contains(index),
                        onClick = if (canSelectCards) { { onCardSelected(index) } } else { {} },
                        canClick = canSelectCards
                    )
                }
            }
        }
    }
}

@Composable
fun CardDisplay(
    card: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    canClick: Boolean = true
) {
    // Parse card string to get rank and suit for better display
    val (rank, suitSymbol, suitColor) = parseCardDisplay(card)
    
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 80.dp)
            .clickable(enabled = canClick) { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                       else Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Add border for selected cards
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .then(
                        Modifier.background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    )
            )
        }
        
        // Card content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rank
            Text(
                text = rank,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color.White else suitColor,
                fontWeight = FontWeight.Bold
            )
            // Suit symbol
            Text(
                text = suitSymbol,
                style = MaterialTheme.typography.titleLarge,
                color = if (isSelected) Color.White else suitColor
            )
        }
    }
}

/**
 * Parse card string to extract rank, suit symbol, and color for display.
 */
private fun parseCardDisplay(card: String): Triple<String, String, Color> {
    when {
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

@Composable
fun AllPlayersCard(
    players: List<PlayerInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "👥 All Players",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(players) { player ->
                    PlayerInfoDisplay(player = player)
                }
            }
        }
    }
}

@Composable
fun PlayerInfoDisplay(player: PlayerInfo) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (player.isCurrentPlayer) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (player.isCurrentPlayer) "You" else player.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "💰 ${player.chips}",
                style = MaterialTheme.typography.bodySmall
            )
            
            if (player.isFolded) {
                Text(
                    text = "🚫 Folded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "Hand: ${player.handValue}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun GameActionsCard(
    onCall: () -> Unit,
    onRaise: (Int) -> Unit,
    onFold: () -> Unit,
    onCheck: () -> Unit,
    onExchangeCards: () -> Unit,
    betAmount: Int,
    onBetAmountChanged: (Int) -> Unit,
    hasSelectedCards: Boolean,
    canExchangeCards: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎮 Game Actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // First row: Call, Check, Fold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Call")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onCheck,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Check")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onFold,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Fold")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Raise section
            Text(
                text = "Raise Amount: $betAmount",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { if (betAmount > BET_INCREMENT) onBetAmountChanged(betAmount - BET_INCREMENT) }
                ) {
                    Text("-$BET_INCREMENT")
                }
                
                Button(
                    onClick = { onRaise(betAmount) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Raise $betAmount")
                }
                
                OutlinedButton(
                    onClick = { onBetAmountChanged(betAmount + BET_INCREMENT) }
                ) {
                    Text("+$BET_INCREMENT")
                }
            }
            
            // Card exchange section - only show if card exchange is allowed
            if (canExchangeCards && hasSelectedCards) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onExchangeCards,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text("Exchange Selected Cards")
                }
            }
        }
    }
}

@Composable
fun RoundManagementCard(
    isRoundComplete: Boolean,
    onNextRound: () -> Unit,
    onDetermineWinner: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Round Management",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (isRoundComplete) {
                Text(
                    text = "Round is complete!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = onNextRound,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Next Round")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDetermineWinner,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Show Winner")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    OutlinedButton(
                        onClick = onNextRound,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next Round")
                    }
                }
            }
        }
    }
}