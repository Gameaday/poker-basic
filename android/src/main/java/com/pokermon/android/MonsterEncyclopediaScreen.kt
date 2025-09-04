package com.pokermon.android

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.Monster
import com.pokermon.MonsterDatabase
import com.pokermon.android.data.MonsterOpponentManager
import com.pokermon.android.data.UserProfileManager

/**
 * Monster Encyclopedia screen showing discovered monsters and their attributes.
 * Monsters are revealed progressively as players encounter them in games.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonsterEncyclopediaScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }
    val monsterOpponentManager = remember { MonsterOpponentManager() }
    
    val seenMonsters = monsterOpponentManager.getSeenMonsters()
    val defeatedMonsters = monsterOpponentManager.getDefeatedMonsters()
    val allMonsters = MonsterDatabase.getAllMonsters()
    
    var selectedRarityFilter by remember { mutableStateOf<Monster.Rarity?>(null) }
    var showOnlyDiscovered by remember { mutableStateOf(false) }
    
    val filteredMonsters = allMonsters.filter { monster ->
        val matchesRarity = selectedRarityFilter == null || monster.rarity == selectedRarityFilter
        val matchesDiscovery = !showOnlyDiscovered || seenMonsters.contains(monster.name)
        matchesRarity && matchesDiscovery
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            
            Text(
                text = "🐲 Monster Encyclopedia",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Discovery progress
        val discoveryProgress = monsterOpponentManager.getDiscoveryProgress()
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Discovery Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { discoveryProgress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("👁️ Seen: ${seenMonsters.size}/${allMonsters.size}")
                    Text("🏆 Defeated: ${defeatedMonsters.size}/${allMonsters.size}")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rarity filter
            var rarityDropdownExpanded by remember { mutableStateOf(false) }
            
            OutlinedButton(
                onClick = { rarityDropdownExpanded = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(selectedRarityFilter?.displayName ?: "All Rarities")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            
            DropdownMenu(
                expanded = rarityDropdownExpanded,
                onDismissRequest = { rarityDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Rarities") },
                    onClick = { 
                        selectedRarityFilter = null
                        rarityDropdownExpanded = false 
                    }
                )
                Monster.Rarity.entries.forEach { rarity ->
                    DropdownMenuItem(
                        text = { Text(rarity.displayName) },
                        onClick = { 
                            selectedRarityFilter = rarity
                            rarityDropdownExpanded = false 
                        }
                    )
                }
            }
            
            // Discovery filter
            FilterChip(
                onClick = { showOnlyDiscovered = !showOnlyDiscovered },
                label = { Text("Discovered Only") },
                selected = showOnlyDiscovered
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Monster list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMonsters) { monster ->
                MonsterCard(
                    monster = monster,
                    isDiscovered = seenMonsters.contains(monster.name),
                    isDefeated = defeatedMonsters.contains(monster.name)
                )
            }
        }
    }
}

@Composable
private fun MonsterCard(
    monster: Monster,
    isDiscovered: Boolean,
    isDefeated: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDiscovered) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
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
                // Monster name and status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isDiscovered) monster.name else "???",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rarity indicator
                        RarityChip(rarity = monster.rarity, isRevealed = isDiscovered)
                        
                        if (isDiscovered) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Seen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        if (isDefeated) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Defeated",
                                tint = Color(0xFFFFD700), // Gold
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                // Monster avatar/icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = getRarityColor(monster.rarity).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDiscovered) getMonsterEmoji(monster.name) else "❓",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            
            if (isDiscovered) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Monster description
                Text(
                    text = monster.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stats (only revealed when defeated)
                if (isDefeated) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatChip("❤️ HP", monster.baseHealth.toString())
                        StatChip("⚡ Power", monster.effectPower.toString())
                        StatChip("🎯 Effect", monster.effectType.name.replace("_", " "))
                    }
                } else {
                    Text(
                        text = "Defeat this monster to reveal its full stats!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Encounter this monster in battle to reveal its secrets...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RarityChip(rarity: Monster.Rarity, isRevealed: Boolean) {
    val rarityColor = getRarityColor(rarity)
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isRevealed) rarityColor.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
        modifier = Modifier.border(
            1.dp, 
            if (isRevealed) rarityColor else Color.Gray, 
            RoundedCornerShape(12.dp)
        )
    ) {
        Text(
            text = if (isRevealed) rarity.displayName else "???",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (isRevealed) rarityColor else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = "$label: $value",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getRarityColor(rarity: Monster.Rarity): Color {
    return when (rarity) {
        Monster.Rarity.COMMON -> Color(0xFF4CAF50) // Green
        Monster.Rarity.UNCOMMON -> Color(0xFF2196F3) // Blue
        Monster.Rarity.RARE -> Color(0xFF9C27B0) // Purple
        Monster.Rarity.EPIC -> Color(0xFFFF9800) // Orange
        Monster.Rarity.LEGENDARY -> Color(0xFFFFD700) // Gold
    }
}

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