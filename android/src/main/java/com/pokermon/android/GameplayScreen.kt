package com.pokermon.android

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.GameMode
import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.PlayerInfo

private const val BET_INCREMENT = 10

/**
 * Main gameplay screen that integrates with the shared game logic.
 */
@Composable
fun GameplayScreen(
    gameMode: GameMode,
    onBackPressed: () -> Unit
) {
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
    
    // Function to refresh all game data
    fun refreshGameData() {
        if (isGameInitialized) {
            playerChips = gameBridge.getPlayerChips()
            currentPot = gameBridge.getCurrentPot()
            playerCards = gameBridge.getPlayerHand()
            allPlayers = gameBridge.getAllPlayers()
            currentRound = gameBridge.getCurrentRound()
            isRoundComplete = gameBridge.isRoundComplete()
        }
    }
    
    // Initialize game when screen loads
    LaunchedEffect(gameMode) {
        gameBridge.setGameMode(gameMode)
        val success = gameBridge.initializeGame("Player", 3, 1000)
        if (success) {
            isGameInitialized = true
            refreshGameData()
            gameState = "Game ready! Make your move."
        } else {
            gameState = "Failed to initialize game"
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with round info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🃏 ${gameMode.displayName}",
                style = MaterialTheme.typography.headlineMedium
            )
            
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
        
        // Player hand with card selection for exchange
        if (playerCards.isNotEmpty()) {
            PlayerHandCard(
                cards = playerCards,
                selectedCards = selectedCards,
                onCardSelected = { cardIndex ->
                    selectedCards = if (selectedCards.contains(cardIndex)) {
                        selectedCards - cardIndex
                    } else {
                        selectedCards + cardIndex
                    }
                    gameBridge.toggleCardSelection(cardIndex)
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Game actions
        if (isGameInitialized) {
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
                onExchangeCards = {
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
                betAmount = betAmount,
                onBetAmountChanged = { betAmount = it },
                hasSelectedCards = selectedCards.isNotEmpty(),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Round management
        if (isGameInitialized) {
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
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Game status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = gameState,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
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
            
            if (selectedCards.isNotEmpty()) {
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
                        isSelected = selectedCards.contains(index),
                        onClick = { onCardSelected(index) }
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
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 80.dp)
            .clickable { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = card, // Show full card notation (e.g., "A♠", "K♥")
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.White else Color.Black,
            textAlign = TextAlign.Center
        )
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
            
            // Card exchange section
            if (hasSelectedCards) {
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