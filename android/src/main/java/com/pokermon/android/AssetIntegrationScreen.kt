package com.pokermon.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pokermon.android.assets.AssetManager
import com.pokermon.android.assets.AssetMemoryStats
import com.pokermon.android.assets.AssetValidationResult
import com.pokermon.android.assets.MonsterAssets
import com.pokermon.android.assets.MonsterAssetValidationResult
import com.pokermon.android.audio.AudioManager
import com.pokermon.android.audio.AudioSettings
import kotlinx.coroutines.launch

/**
 * Asset and Audio Integration Demo Screen
 * Showcases the new Phase 2 implementations for asset management and audio systems.
 * 
 * @author Pokermon Phase 2 Implementation
 * @version 1.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetIntegrationScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Initialize managers
    val assetManager = remember { AssetManager.getInstance(context) }
    val monsterAssets = remember { MonsterAssets.getInstance(context) }
    val audioManager = remember { AudioManager.getInstance(context) }
    
    // State for asset validation
    var assetValidation by remember { mutableStateOf<AssetValidationResult?>(null) }
    var monsterValidation by remember { mutableStateOf<MonsterAssetValidationResult?>(null) }
    var memoryStats by remember { mutableStateOf<AssetMemoryStats?>(null) }
    var audioSettings by remember { mutableStateOf<AudioSettings?>(null) }
    
    // Initialize audio system and validate assets
    LaunchedEffect(Unit) {
        audioManager.initialize()
        
        // Validate assets
        assetValidation = assetManager.validateAssets()
        monsterValidation = monsterAssets.validateMonsterAssets()
        memoryStats = assetManager.getMemoryUsage()
        audioSettings = audioManager.getAudioSettings()
        
        // Preload essential assets
        assetManager.preloadEssentialAssets()
        monsterAssets.preloadCommonMonsters()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset & Audio Integration") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Asset Management Section
            item {
                AssetManagementSection(
                    assetValidation = assetValidation,
                    memoryStats = memoryStats
                )
            }
            
            // Monster Assets Section  
            item {
                MonsterAssetsSection(
                    monsterValidation = monsterValidation,
                    monsterAssets = monsterAssets
                )
            }
            
            // Audio System Section
            item {
                AudioSystemSection(
                    audioSettings = audioSettings,
                    audioManager = audioManager,
                    onVolumeChange = { musicVolume, sfxVolume ->
                        scope.launch {
                            audioManager.setMusicVolume(musicVolume)
                            audioManager.setSfxVolume(sfxVolume)
                            audioSettings = audioManager.getAudioSettings()
                        }
                    },
                    onPlayMusic = { music ->
                        audioManager.playBackgroundMusic(music, fadeIn = true)
                    },
                    onStopMusic = {
                        audioManager.stopBackgroundMusic(fadeOut = true)
                    },
                    onPlaySfx = { effect ->
                        audioManager.playSoundEffect(effect)
                    }
                )
            }
            
            // Card Assets Preview
            item {
                CardAssetsPreview(assetManager = assetManager)
            }
        }
    }
}

@Composable
private fun AssetManagementSection(
    assetValidation: AssetValidationResult?,
    memoryStats: AssetMemoryStats?
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
                text = "Asset Management System",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            assetValidation?.let { validation ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Assets Found:")
                    Text("${validation.validAssets}/${validation.totalAssets}")
                }
                
                LinearProgressIndicator(
                    progress = validation.validationPercentage / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = if (validation.isFullyValid) Color.Green else MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Validation: ${validation.validationPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            memoryStats?.let { stats ->
                Text(
                    text = "Memory Usage: ${stats.estimatedMemoryKB}KB",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Cached Assets: ${stats.cachedDrawables}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun MonsterAssetsSection(
    monsterValidation: MonsterAssetValidationResult?,
    monsterAssets: MonsterAssets
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
            Text(
                text = "Monster Asset System",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            monsterValidation?.let { validation ->
                Text("Expected Assets: ${validation.totalExpectedAssets}")
                Text("Found Assets: ${validation.foundAssets}")
                
                if (validation.needsAssetCreation) {
                    Text(
                        text = "⚠️ Asset creation needed",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (validation.hasPlaceholdersAvailable) {
                    Text(
                        text = "✅ Placeholder system ready",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Text(
                text = "Available Monsters: ${monsterAssets.getAvailableMonsters().size}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AudioSystemSection(
    audioSettings: AudioSettings?,
    audioManager: AudioManager,
    onVolumeChange: (Float, Float) -> Unit,
    onPlayMusic: (AudioManager.BackgroundMusic) -> Unit,
    onStopMusic: () -> Unit,
    onPlaySfx: (AudioManager.SoundEffect) -> Unit
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
            Text(
                text = "Audio System",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            audioSettings?.let { settings ->
                // Music Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Music Volume:")
                    Slider(
                        value = settings.musicVolume,
                        onValueChange = { onVolumeChange(it, settings.sfxVolume) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // SFX Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SFX Volume:")
                    Slider(
                        value = settings.sfxVolume,
                        onValueChange = { onVolumeChange(settings.musicVolume, it) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Music Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { onPlayMusic(AudioManager.BackgroundMusic.MAIN_THEME) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Music"
                        )
                    }
                }
                
                settings.currentMusic?.let { music ->
                    Text(
                        text = "Playing: ${music.description}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CardAssetsPreview(
    assetManager: AssetManager
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Card Assets Preview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Show a few sample cards
                listOf("ace_of_spades", "king_of_hearts", "queen_of_diamonds", "jack_of_clubs").forEach { cardName ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("android.resource://${LocalContext.current.packageName}/drawable/$cardName")
                            .build(),
                        contentDescription = cardName,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            
            Text(
                text = "${assetManager.getAvailableCardAssets().size} card assets available",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}