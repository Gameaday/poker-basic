package com.pokermon.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.pokermon.GameMode
import com.pokermon.android.data.UserProfileManager
import java.text.NumberFormat
import java.util.*

/**
 * Comprehensive statistics screen showing player progress, achievements, and game history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }
    val userProfile by userProfileManager.userProfile.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Achievements", "Game Modes")

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("📊 Statistics") },
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

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> StatisticsOverview(userProfile)
            1 -> AchievementsTab(userProfile)
            2 -> GameModesTab(userProfile)
        }
    }
}

@Composable
private fun StatisticsOverview(userProfile: com.pokermon.android.data.UserProfile) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Main Statistics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            value = userProfile.totalGamesPlayed.toString(),
                            label = "Games Played",
                            icon = "🎮"
                        )
                        StatisticItem(
                            value = userProfile.gamesWon.toString(),
                            label = "Games Won",
                            icon = "🏆"
                        )
                        StatisticItem(
                            value = "${(userProfile.winRate * 100).toInt()}%",
                            label = "Win Rate",
                            icon = "📈"
                        )
                    }
                }
            }
        }

        item {
            // Financial Statistics
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💰 Financial Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Chips Won:")
                        Text(
                            text = NumberFormat.getInstance().format(userProfile.totalChipsWon),
                            fontWeight = FontWeight.Bold,
                            color = if (userProfile.totalChipsWon > 0) Color.Green else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Best Hand:")
                        Text(
                            text = userProfile.highestHand,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Favorite Mode:")
                        Text(
                            text = GameMode.valueOf(userProfile.favoriteGameMode).displayName,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            // Monster Statistics (for monster modes)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🐲 Monster Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            value = userProfile.monstersCollected.toString(),
                            label = "Monsters\nCollected",
                            icon = "🐲"
                        )
                        StatisticItem(
                            value = userProfile.adventureProgress.toString(),
                            label = "Adventure\nProgress",
                            icon = "⚔️"
                        )
                        StatisticItem(
                            value = userProfile.safariEncounters.toString(),
                            label = "Safari\nEncounters",
                            icon = "🏞️"
                        )
                        StatisticItem(
                            value = userProfile.ironmanPulls.toString(),
                            label = "Ironman\nPulls",
                            icon = "🎰"
                        )
                    }
                }
            }
        }

        item {
            // Recent Activity
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Recent Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = "Last played: ${userProfile.lastPlayed}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (userProfile.achievements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Latest Achievement: ${userProfile.achievements.last()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AchievementsTab(userProfile: com.pokermon.android.data.UserProfile) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Achievements Unlocked",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${userProfile.achievements.size} earned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        if (userProfile.achievements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆",
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No achievements yet",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Play games to start earning achievements!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(userProfile.achievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = achievement,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getAchievementDescription(achievement),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun GameModesTab(userProfile: com.pokermon.android.data.UserProfile) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(GameMode.entries) { gameMode ->
            GameModeStatsCard(gameMode, userProfile)
        }
    }
}

@Composable
private fun GameModeStatsCard(gameMode: GameMode, userProfile: com.pokermon.android.data.UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (gameMode) {
                GameMode.CLASSIC -> MaterialTheme.colorScheme.primaryContainer
                GameMode.ADVENTURE -> MaterialTheme.colorScheme.secondaryContainer
                GameMode.SAFARI -> MaterialTheme.colorScheme.tertiaryContainer
                GameMode.IRONMAN -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = when (gameMode) {
                        GameMode.CLASSIC -> "🃏"
                        GameMode.ADVENTURE -> "⚔️"
                        GameMode.SAFARI -> "🏞️"
                        GameMode.IRONMAN -> "🎰"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Column {
                    Text(
                        text = gameMode.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (userProfile.favoriteGameMode == gameMode.name) "⭐ Favorite Mode" else "Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            // Mode-specific stats would go here
            Text(
                text = when (gameMode) {
                    GameMode.CLASSIC -> "Traditional poker experience"
                    GameMode.ADVENTURE -> "Progress: ${userProfile.adventureProgress} battles completed"
                    GameMode.SAFARI -> "Encounters: ${userProfile.safariEncounters} monsters met"
                    GameMode.IRONMAN -> "Gacha Pulls: ${userProfile.ironmanPulls} attempts"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun getAchievementDescription(achievement: String): String {
    return when (achievement) {
        "First Steps" -> "Completed your first game"
        "Winning Streak" -> "Won 10 or more games"
        "High Roller" -> "Accumulated 10,000+ chips"
        "Royal Achievement" -> "Achieved a Royal Flush"
        "Monster Collector" -> "Collected 10 or more monsters"
        else -> "Achievement unlocked"
    }
}