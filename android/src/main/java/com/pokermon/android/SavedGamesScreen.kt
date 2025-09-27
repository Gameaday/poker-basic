package com.pokermon.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.GameMode
import com.pokermon.android.data.GameSaveManager
import com.pokermon.android.data.SavedGame
import kotlinx.coroutines.launch

/**
 * Screen for managing saved games - loading, deleting, and viewing save details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGamesScreen(
    onBackPressed: () -> Unit,
    onLoadGame: (SavedGame) -> Unit,
) {
    val context = LocalContext.current
    val gameSaveManager = remember { GameSaveManager.getInstance(context) }
    val savedGames by gameSaveManager.savedGames.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    var showDeleteDialog by remember { mutableStateOf<SavedGame?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("💾 Saved Games") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        if (savedGames.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "💾",
                    style = MaterialTheme.typography.displayLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "No saved games",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your saved games will appear here. You can save your progress during gameplay by using the pause menu.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Check for auto-save
            val autoSave = gameSaveManager.loadAutoSavedGame()
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Auto-save item (if available)
                autoSave?.let { save ->
                    item {
                        AutoSaveCard(
                            savedGame = save,
                            onLoad = { onLoadGame(save) },
                            onDelete = { gameSaveManager.clearAutoSave() }
                        )
                    }
                }

                // Manual saves
                items(savedGames) { savedGame ->
                    SavedGameCard(
                        savedGame = savedGame,
                        onLoad = { onLoadGame(savedGame) },
                        onDelete = { showDeleteDialog = savedGame }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { gameToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text("Delete Saved Game?")
            },
            text = {
                Text("Are you sure you want to delete \"${gameToDelete.slotName}\"? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Delete the saved game using coroutine scope
                        coroutineScope.launch {
                            gameSaveManager.deleteSavedGame(gameToDelete.id)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AutoSaveCard(
    savedGame: SavedGame,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔄",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = "Auto-Save",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Latest session",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onLoad) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Load Game")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Auto-Save")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SavedGameDetails(savedGame)
        }
    }
}

@Composable
private fun SavedGameCard(
    savedGame: SavedGame,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getGameModeIcon(savedGame.gameMode),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = savedGame.slotName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saved ${savedGame.formattedSaveTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onLoad) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Load Game")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Save")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SavedGameDetails(savedGame)
        }
    }
}

@Composable
private fun SavedGameDetails(savedGame: SavedGame) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mode:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = try { 
                    GameMode.valueOf(savedGame.gameMode).displayName 
                } catch (e: Exception) { 
                    savedGame.gameMode 
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Player:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = savedGame.playerName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Round:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${savedGame.currentRound}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Chips:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${savedGame.playerChips}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        if (savedGame.playTime > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Play Time:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = savedGame.formattedPlayTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        if (savedGame.gameProgress > 0) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(savedGame.gameProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = { savedGame.gameProgress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun getGameModeIcon(gameMode: String): String {
    return try {
        when (GameMode.valueOf(gameMode)) {
            GameMode.CLASSIC -> "🃏"
            GameMode.ADVENTURE -> "⚔️"
            GameMode.SAFARI -> "🏞️"
            GameMode.IRONMAN -> "🎰"
        }
    } catch (e: Exception) {
        "🎮"
    }
}