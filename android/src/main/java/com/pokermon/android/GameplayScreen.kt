package com.pokermon.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
    var isGameInitialized by remember { mutableStateOf(false) }
    var betAmount by remember { mutableIntStateOf(50) }
    
    // Initialize game when screen loads
    LaunchedEffect(gameMode) {
        val success = gameBridge.initializeGame("Player", 3, 1000)
        if (success) {
            isGameInitialized = true
            playerChips = gameBridge.getPlayerChips()
            currentPot = gameBridge.getCurrentPot()
            playerCards = gameBridge.getPlayerHand()
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
        // Header
        Text(
            text = "🃏 ${gameMode.displayName}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Game stats
        GameStatsCard(
            playerChips = playerChips,
            currentPot = currentPot,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Player hand
        if (playerCards.isNotEmpty()) {
            PlayerHandCard(
                cards = playerCards,
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
                        playerChips = gameBridge.getPlayerChips()
                        currentPot = gameBridge.getCurrentPot()
                    }
                },
                onRaise = { amount ->
                    val result = gameBridge.performRaise(amount)
                    gameState = result.message
                    if (result.success) {
                        playerChips = gameBridge.getPlayerChips()
                        currentPot = gameBridge.getCurrentPot()
                    }
                },
                onFold = {
                    val result = gameBridge.performFold()
                    gameState = result.message
                },
                betAmount = betAmount,
                onBetAmountChanged = { betAmount = it },
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
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    CardDisplay(card = card)
                }
            }
        }
    }
}

@Composable
fun CardDisplay(card: String) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 80.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = card.take(3), // Show first 3 characters
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GameActionsCard(
    onCall: () -> Unit,
    onRaise: (Int) -> Unit,
    onFold: () -> Unit,
    betAmount: Int,
    onBetAmountChanged: (Int) -> Unit,
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
                    onClick = { if (betAmount > 10) onBetAmountChanged(betAmount - 10) }
                ) {
                    Text("-10")
                }
                
                Button(
                    onClick = { onRaise(betAmount) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Raise $betAmount")
                }
                
                OutlinedButton(
                    onClick = { onBetAmountChanged(betAmount + 10) }
                ) {
                    Text("+10")
                }
            }
        }
    }
}