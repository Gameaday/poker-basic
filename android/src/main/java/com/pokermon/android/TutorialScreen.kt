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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Comprehensive tutorial and help screen for new players.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(
    onBackPressed: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Quick Start", "Game Modes", "Poker Basics", "Tips")

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("📚 Tutorial & Help") },
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
            0 -> QuickStartTab()
            1 -> GameModesTab()
            2 -> PokerBasicsTab()
            3 -> TipsTab()
        }
    }
}

@Composable
private fun QuickStartTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WelcomeCard()
        }

        item {
            QuickStartStep(
                stepNumber = 1,
                title = "Choose Your Adventure",
                description = "Select from four unique game modes: Classic Poker, Adventure Mode, Safari Mode, or Ironman Mode.",
                icon = "🎮"
            )
        }

        item {
            QuickStartStep(
                stepNumber = 2,
                title = "Learn the Basics",
                description = "If you're new to poker, start with Classic Mode to learn the fundamentals of hand rankings and betting.",
                icon = "🃏"
            )
        }

        item {
            QuickStartStep(
                stepNumber = 3,
                title = "Collect Monsters",
                description = "Try Adventure, Safari, or Ironman modes to encounter and collect unique monsters while playing poker.",
                icon = "🐲"
            )
        }

        item {
            QuickStartStep(
                stepNumber = 4,
                title = "Track Progress",
                description = "View your statistics, achievements, and monster collection in the Settings and Statistics screens.",
                icon = "📊"
            )
        }
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Welcome to Pokermon!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "A unique blend of poker gameplay and monster collecting adventure. Get ready for an exciting journey!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickStartStep(
    stepNumber: Int,
    title: String,
    description: String,
    icon: String
) {
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
            // Step number circle
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Icon
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun GameModesTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GameModeHelpCard(
                title = "🃏 Classic Mode",
                description = "Traditional Texas Hold'em poker gameplay",
                features = listOf(
                    "Standard 5-card draw poker",
                    "Betting rounds with call, raise, fold",
                    "Hand rankings from High Card to Royal Flush",
                    "Perfect for learning poker basics"
                )
            )
        }

        item {
            GameModeHelpCard(
                title = "⚔️ Adventure Mode",
                description = "Battle monsters using poker skills",
                features = listOf(
                    "Monster opponents with health points",
                    "Deal damage based on hand strength",
                    "Quest progression and storylines",
                    "Unlock new areas and challenges"
                )
            )
        }

        item {
            GameModeHelpCard(
                title = "🏞️ Safari Mode",
                description = "Capture wild monsters in their natural habitat",
                features = listOf(
                    "Encounter random wild monsters",
                    "Use Safari Balls to capture them",
                    "Success based on poker performance",
                    "Build your monster collection"
                )
            )
        }

        item {
            GameModeHelpCard(
                title = "🎰 Ironman Mode",
                description = "High-risk, high-reward gacha gameplay",
                features = listOf(
                    "Convert winnings to gacha points",
                    "Pull for rare monster rewards",
                    "Permadeath adds stakes",
                    "Risk multipliers for bigger rewards"
                )
            )
        }
    }
}

@Composable
private fun GameModeHelpCard(
    title: String,
    description: String,
    features: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokerBasicsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PokerBasicsCard(
                title = "Hand Rankings",
                content = listOf(
                    "Royal Flush" to "A, K, Q, J, 10 all same suit",
                    "Straight Flush" to "Five cards in sequence, same suit",
                    "Four of a Kind" to "Four cards of same rank",
                    "Full House" to "Three of a kind + pair",
                    "Flush" to "Five cards of same suit",
                    "Straight" to "Five cards in sequence",
                    "Three of a Kind" to "Three cards of same rank",
                    "Two Pair" to "Two different pairs",
                    "Pair" to "Two cards of same rank",
                    "High Card" to "Highest card wins"
                )
            )
        }

        item {
            PokerBasicsCard(
                title = "Betting Actions",
                content = listOf(
                    "Call" to "Match the current bet",
                    "Raise" to "Increase the current bet",
                    "Fold" to "Give up your hand and chips",
                    "Check" to "Pass without betting (if no bet to call)"
                )
            )
        }

        item {
            PokerBasicsCard(
                title = "Game Flow",
                content = listOf(
                    "1. Deal" to "Each player gets cards",
                    "2. Betting Round" to "Players make their bets",
                    "3. Card Exchange" to "Replace unwanted cards",
                    "4. Final Betting" to "Last chance to bet",
                    "5. Showdown" to "Best hand wins the pot"
                )
            )
        }
    }
}

@Composable
private fun PokerBasicsCard(
    title: String,
    content: List<Pair<String, String>>
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            content.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(2f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun TipsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(getTipsList()) { tip ->
            TipCard(tip.first, tip.second, tip.third)
        }
    }
}

@Composable
private fun TipCard(
    icon: String,
    title: String,
    description: String
) {
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
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun getTipsList(): List<Triple<String, String, String>> {
    return listOf(
        Triple("🎯", "Start with Classic Mode", "Learn poker fundamentals before trying monster modes"),
        Triple("💰", "Manage Your Chips", "Don't bet everything on one hand - patience pays off"),
        Triple("🃏", "Know Your Odds", "Pairs and two-pairs are common, straights and flushes are rare"),
        Triple("👀", "Watch Your Opponents", "AI players have different personalities and strategies"),
        Triple("🎰", "Try Different Modes", "Each mode offers unique rewards and challenges"),
        Triple("💾", "Save Your Progress", "Use the pause menu to save your game anytime"),
        Triple("🏆", "Collect Achievements", "Complete challenges to unlock special rewards"),
        Triple("🐲", "Build Your Collection", "Monster modes let you collect and battle unique creatures"),
        Triple("📊", "Track Your Stats", "Check the Statistics screen to see your progress"),
        Triple("⚙️", "Customize Settings", "Adjust themes, sounds, and preferences in Settings")
    )
}