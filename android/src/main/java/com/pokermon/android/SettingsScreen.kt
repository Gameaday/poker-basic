package com.pokermon.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pokermon.android.data.UserProfileManager
import com.pokermon.android.ui.theme.PokerTableTheme
import com.pokermon.database.CardPackManager
import java.util.Locale

/**
 * Enhanced settings screen with persistent user profile integration.
 * Automatically saves all settings changes and integrates with comprehensive user profile system.
 */
@Composable
fun SettingsScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }

    // Collect settings from the profile manager
    val gameSettings by userProfileManager.gameSettings.collectAsState()
    val userProfile by userProfileManager.userProfile.collectAsState()

    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCardPackDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }

    // Get current theme enum value
    val selectedTheme =
        remember(gameSettings.selectedTheme) {
            try {
                PokerTableTheme.valueOf(gameSettings.selectedTheme)
            } catch (e: IllegalArgumentException) {
                PokerTableTheme.CLASSIC_GREEN
            }
        }

    // Get available card packs
    val availableCardPacks = remember { CardPackManager.getAvailableCardPacks() }
    val selectedCardPack = gameSettings.selectedCardPack
    val selectedCardPackDisplay = CardPackManager.getDisplayName(selectedCardPack)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header with user info
        Text(
            text = "⚙️ Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = "Welcome back, ${userProfile.username}!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        Text(
            text = "Configure your Pokermon experience",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        // User Profile Section
        SettingsSection(
            title = "👤 User Profile",
        ) {
            SettingsActionItem(
                icon = Icons.Default.Person,
                title = "Profile Information",
                description = "Games: ${userProfile.totalGamesPlayed}, Win Rate: ${String.format(
                    Locale.getDefault(),
                    "%.1f",
                    userProfile.winRate * 100,
                )}%",
                onClick = { showProfileDialog = true },
            )

            SettingsActionItem(
                icon = Icons.Default.Star,
                title = "Achievements",
                description = "${userProfile.achievements.size} unlocked",
                onClick = { showAchievementsDialog = true },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Preferences Section
        SettingsSection(
            title = "🎮 Game Preferences",
        ) {
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Sound Effects",
                description = "Enable game sounds and audio feedback",
                checked = gameSettings.soundEnabled,
                onCheckedChange = { enabled ->
                    userProfileManager.updateGameSettings(
                        gameSettings.copy(soundEnabled = enabled),
                    )
                },
            )

            SettingsToggleItem(
                icon = Icons.Default.PlayArrow,
                title = "Animations",
                description = "Enable card dealing and UI animations",
                checked = gameSettings.animationsEnabled,
                onCheckedChange = { enabled ->
                    userProfileManager.updateGameSettings(
                        gameSettings.copy(animationsEnabled = enabled),
                    )
                },
            )

            SettingsToggleItem(
                icon = Icons.Default.Star,
                title = "Auto-Save",
                description = "Automatically save game progress",
                checked = gameSettings.autoSaveEnabled,
                onCheckedChange = { enabled ->
                    userProfileManager.updateGameSettings(
                        gameSettings.copy(autoSaveEnabled = enabled),
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pokermon Theme Section
        SettingsSection(
            title = "🎨 Pokermon Table Theme",
        ) {
            SettingsActionItem(
                icon = Icons.Default.Settings,
                title = "Table Style",
                description = selectedTheme.displayName,
                onClick = { showThemeDialog = true },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card Art Selection Section
        SettingsSection(
            title = "🃏 Card Art Selection",
        ) {
            SettingsActionItem(
                icon = Icons.Default.Star,
                title = "Card Pack",
                description = selectedCardPackDisplay,
                onClick = { showCardPackDialog = true },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data Management Section (Enhanced)
        SettingsSection(
            title = "💾 Data Management",
        ) {
            SettingsActionItem(
                icon = Icons.Default.Share,
                title = "Export Profile",
                description = "Create backup of all user data and settings",
                onClick = { showBackupDialog = true },
            )

            SettingsActionItem(
                icon = Icons.Default.AccountCircle,
                title = "Import Profile",
                description = "Restore from a previous backup",
                onClick = { showRestoreDialog = true },
            )

            SettingsActionItem(
                icon = Icons.Default.Delete,
                title = "Reset All Data",
                description = "Remove all progress and settings (cannot be undone)",
                onClick = { showDeleteDialog = true },
                isDestructive = true,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Back button
        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Back to Menu")
        }
    }

    // Enhanced Dialogs with real functionality
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = {
                Text(
                    text = "💾 Export Profile Data",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    text =
                        """
                        Export your complete Pokermon profile including:
                        • User statistics and achievements
                        • Game settings and preferences
                        • Monster collection progress
                        • All unlocked content
                        
                        This creates a complete backup of your data.
                        """.trimIndent(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // Export user data with comprehensive backup functionality
                    val exportData = userProfileManager.exportUserData()

                    // Create shareable content for user data backup
                    val shareIntent =
                        android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, exportData)
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Pokermon Profile Backup")
                        }

                    try {
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Export Profile Data"))
                    } catch (e: Exception) {
                        // Fallback: Copy to clipboard or show data
                        println("Export data: $exportData") // For debugging/demo
                    }

                    showBackupDialog = false
                }) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text(
                    text = "🔄 Import Profile Data",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    text =
                        """
                        Import a previously exported Pokermon profile.
                        
                        ⚠️ This will replace all current data including:
                        • User statistics and achievements
                        • Game settings and preferences
                        • Monster collection progress
                        
                        Current progress will be lost!
                        """.trimIndent(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // In real app, this would open file picker
                    showRestoreDialog = false
                }) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "⚠️ Reset All Data",
                    color = MaterialTheme.colorScheme.error,
                )
            },
            text = {
                Text(
                    text =
                        """
                        This will permanently delete ALL your Pokermon data:
                        
                        • User profile and statistics
                        • All achievements and progress
                        • Monster collection
                        • Game settings and preferences
                        
                        This action cannot be undone!
                        """.trimIndent(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userProfileManager.clearAllUserData()
                        showDeleteDialog = false
                    },
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                ) {
                    Text("DELETE ALL")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "🎨 Choose Pokermon Table Theme",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Select your preferred poker table style:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PokerTableTheme.values().forEach { theme ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme,
                                onClick = {
                                    userProfileManager.updateGameSettings(
                                        gameSettings.copy(selectedTheme = theme.name),
                                    )
                                },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = theme.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Text(
                                    text = theme.description,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Done")
                }
            },
        )
    }

    // Card Pack Selection Dialog
    if (showCardPackDialog) {
        AlertDialog(
            onDismissRequest = { showCardPackDialog = false },
            title = { Text("🃏 Select Card Art Pack") },
            text = {
                Column {
                    Text(
                        text = "Choose your preferred card art style:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    availableCardPacks.forEach { (packName, displayName) ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedCardPack == packName,
                                onClick = {
                                    userProfileManager.updateGameSettings(
                                        gameSettings.copy(selectedCardPack = packName),
                                    )
                                },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                if (packName == CardPackManager.TEXT_SYMBOLS) {
                                    Text(
                                        text = "Classic text and symbols display",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                } else {
                                    Text(
                                        text = "Image-based card art pack",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCardPackDialog = false }) {
                    Text("Done")
                }
            },
        )
    }

    // User Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("👤 ${userProfile.username}") },
            text = {
                Column {
                    Text("🎮 Games Played: ${userProfile.totalGamesPlayed}")
                    Text("🏆 Games Won: ${userProfile.gamesWon}")
                    Text("📊 Win Rate: ${String.format(Locale.getDefault(), "%.1f", userProfile.winRate * 100)}%")
                    Text("💰 Total Chips Won: ${userProfile.totalChipsWon}")
                    Text("🃏 Best Hand: ${userProfile.highestHand}")
                    Text("🎯 Favorite Mode: ${userProfile.favoriteGameMode}")
                    Text("🏅 Achievements: ${userProfile.achievements.size}")

                    if (userProfile.monstersCollected > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🐲 Monsters Collected: ${userProfile.monstersCollected}")
                        Text("⚔️ Adventure Progress: ${userProfile.adventureProgress}")
                        Text("🌿 Safari Encounters: ${userProfile.safariEncounters}")
                        Text("🎰 Ironman Pulls: ${userProfile.ironmanPulls}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // Achievements Dialog
    if (showAchievementsDialog) {
        AlertDialog(
            onDismissRequest = { showAchievementsDialog = false },
            title = { Text("🏅 Achievements") },
            text = {
                Column {
                    if (userProfile.achievements.isEmpty()) {
                        Text("No achievements unlocked yet. Keep playing to earn them!")
                    } else {
                        Text("Unlocked Achievements:")
                        Spacer(modifier = Modifier.height(8.dp))
                        userProfile.achievements.forEach { achievement ->
                            Text("🏆 $achievement")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAchievementsDialog = false }) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            content()
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isDestructive) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp),
                tint =
                    if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (isDestructive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        if (isDestructive) {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint =
                    if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
        }
    }
}
