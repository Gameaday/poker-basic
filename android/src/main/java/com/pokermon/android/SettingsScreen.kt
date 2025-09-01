package com.pokermon.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokermon.android.ui.theme.PokerTableTheme

/**
 * Settings screen for customization and save management.
 */
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit
) {
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var animationsEnabled by remember { mutableStateOf(true) }
    var autoSaveEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf<PokerTableTheme?>(null) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "⚙️ Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Customize your poker experience",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Game Preferences Section
        SettingsSection(
            title = "🎮 Game Preferences"
        ) {
            SettingsToggleItem(
                icon = if (soundEnabled) Icons.Default.Check else Icons.Default.Close,
                title = "Sound Effects",
                description = "Enable game sounds and audio feedback",
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )
            
            SettingsToggleItem(
                icon = Icons.Default.Settings,
                title = "Animations",
                description = "Enable card dealing and UI animations",
                checked = animationsEnabled,
                onCheckedChange = { animationsEnabled = it }
            )
            
            SettingsToggleItem(
                icon = Icons.Default.CheckCircle,
                title = "Auto-Save",
                description = "Automatically save game progress",
                checked = autoSaveEnabled,
                onCheckedChange = { autoSaveEnabled = it }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Table Theme Section
        SettingsSection(
            title = "🎨 Table Theme"
        ) {
            SettingsActionItem(
                icon = Icons.Default.Palette,
                title = "Poker Table Style",
                description = selectedTheme?.displayName ?: "Default (System Colors)",
                onClick = { showThemeDialog = true }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Save Management Section
        SettingsSection(
            title = "💾 Save Management"
        ) {
            SettingsActionItem(
                icon = Icons.Default.Add,
                title = "Backup Save Data",
                description = "Create a backup of your game progress",
                onClick = { showBackupDialog = true }
            )
            
            SettingsActionItem(
                icon = Icons.Default.Refresh,
                title = "Restore Save Data",
                description = "Restore from a previous backup",
                onClick = { showRestoreDialog = true }
            )
            
            SettingsActionItem(
                icon = Icons.Default.Delete,
                title = "Delete Save Data",
                description = "Remove all saved progress (cannot be undone)",
                onClick = { showDeleteDialog = true },
                isDestructive = true
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Back button
        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }
    
    // Dialogs
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("💾 Backup Save Data") },
            text = { Text("Your game progress has been backed up successfully!\n\nNote: This is a demo feature. In a real implementation, this would create a backup file or sync to cloud storage.") },
            confirmButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("🔄 Restore Save Data") },
            text = { Text("Do you want to restore from your last backup?\n\nNote: This is a demo feature. In a real implementation, this would restore from a backup file or cloud storage.") },
            confirmButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Delete Save Data") },
            text = { Text("Are you sure you want to delete all your save data?\n\nThis action cannot be undone and you will lose all progress.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Theme selection dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("🎨 Choose Table Theme") },
            text = {
                Column {
                    Text(
                        text = "Select your preferred poker table color scheme:",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Default/System option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == null,
                            onClick = { selectedTheme = null }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "System colors",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Poker table themes
                    PokerTableTheme.values().forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme,
                                onClick = { selectedTheme = theme }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = theme.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = theme.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
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
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
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
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive) 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp),
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go",
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}