package com.pokermon.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.GameMode

/**
 * Screen for selecting game mode before starting a new game.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameModeSelectionScreen(
    onGameModeSelected: (GameMode) -> Unit,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "🃏 Select Game Mode",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Choose your poker adventure",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Game mode cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(GameMode.values()) { gameMode ->
                GameModeCard(
                    gameMode = gameMode,
                    onSelected = { onGameModeSelected(gameMode) }
                )
            }
        }
        
        // Back button
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameModeCard(
    gameMode: GameMode,
    onSelected: () -> Unit
) {
    val isImplemented = gameMode == GameMode.CLASSIC
    
    Card(
        onClick = if (isImplemented) onSelected else { },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isImplemented) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gameMode.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                
                if (!isImplemented) {
                    Text(
                        text = "Coming Soon",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = gameMode.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            
            if (isImplemented) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Available Now",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}