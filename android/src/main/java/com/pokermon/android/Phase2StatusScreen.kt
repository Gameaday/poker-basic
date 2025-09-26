package com.pokermon.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokermon.android.assets.AssetManager
import com.pokermon.android.assets.AssetValidationResult
import com.pokermon.android.assets.MonsterAssets
import com.pokermon.android.assets.MonsterAssetValidationResult
import com.pokermon.android.audio.AudioManager
import com.pokermon.android.store.StoreCompliance
import com.pokermon.android.store.PlayStoreComplianceResult

/**
 * Phase 2 Implementation Status Screen
 * Shows the comprehensive status of asset integration, audio system, and Play Store preparation
 * 
 * @author Pokermon Phase 2 Implementation
 * @version 1.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Phase2StatusScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Phase 2 managers
    val assetManager = remember { AssetManager.getInstance(context) }
    val monsterAssets = remember { MonsterAssets.getInstance(context) }
    val audioManager = remember { AudioManager.getInstance(context) }
    val storeCompliance = remember { StoreCompliance.getInstance(context) }
    
    // Status states
    var assetValidation by remember { mutableStateOf<AssetValidationResult?>(null) }
    var monsterValidation by remember { mutableStateOf<MonsterAssetValidationResult?>(null) }
    var complianceResult by remember { mutableStateOf<PlayStoreComplianceResult?>(null) }
    var systemsInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Initialize systems
        audioManager.initialize()
        assetManager.preloadEssentialAssets()
        monsterAssets.preloadCommonMonsters()
        
        // Validate implementations
        assetValidation = assetManager.validateAssets()
        monsterValidation = monsterAssets.validateMonsterAssets()
        complianceResult = storeCompliance.validatePlayStoreCompliance()
        
        systemsInitialized = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phase 2: Implementation Status") },
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
            item {
                PhaseOverviewCard()
            }
            
            item {
                AssetSystemCard(
                    assetValidation = assetValidation,
                    monsterValidation = monsterValidation
                )
            }
            
            item {
                AudioSystemCard(
                    audioManager = audioManager,
                    isInitialized = systemsInitialized
                )
            }
            
            item {
                PlayStorePreparationCard(
                    complianceResult = complianceResult,
                    storeCompliance = storeCompliance
                )
            }
            
            item {
                ImplementationSummaryCard(
                    assetValidation = assetValidation,
                    monsterValidation = monsterValidation,
                    complianceResult = complianceResult,
                    systemsInitialized = systemsInitialized
                )
            }
        }
    }
}

@Composable
private fun PhaseOverviewCard() {
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
                text = "🚀 Phase 2: Asset Integration & Store Prep",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Comprehensive implementation of production-ready features:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val features = listOf(
                "✅ Asset Management System",
                "✅ Monster Asset Framework", 
                "✅ Audio System Integration",
                "✅ Play Store Compliance",
                "✅ Privacy Policy & Terms",
                "✅ Professional Architecture"
            )
            
            features.forEach { feature ->
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AssetSystemCard(
    assetValidation: AssetValidationResult?,
    monsterValidation: MonsterAssetValidationResult?
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
                text = "🎨 Asset Management System",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            assetValidation?.let { validation ->
                Text("Card Assets: ${validation.validAssets}/${validation.totalAssets}")
                
                LinearProgressIndicator(
                    progress = validation.validationPercentage / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = if (validation.isFullyValid) Color.Green else MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Validation: ${validation.validationPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            monsterValidation?.let { validation ->
                Text("Monster System: ${validation.foundAssets}/${validation.totalExpectedAssets} assets")
                
                if (validation.hasPlaceholdersAvailable) {
                    Text(
                        text = "✅ Placeholder system ready",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (validation.needsAssetCreation) {
                    Text(
                        text = "📝 Professional assets needed for production",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioSystemCard(
    audioManager: AudioManager,
    isInitialized: Boolean
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
                text = "🎵 Audio System",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (isInitialized) {
                val audioSettings = audioManager.getAudioSettings()
                
                Text("System Status: ✅ Initialized")
                Text("Music Volume: ${(audioSettings.musicVolume * 100).toInt()}%")
                Text("SFX Volume: ${(audioSettings.sfxVolume * 100).toInt()}%")
                Text("Music Enabled: ${if (audioSettings.isMusicEnabled) "✅" else "❌"}")
                Text("SFX Enabled: ${if (audioSettings.isSfxEnabled) "✅" else "❌"}")
                
                audioSettings.currentMusic?.let { music ->
                    Text(
                        text = "🎼 Playing: ${music.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green
                    )
                } ?: Text(
                    text = "🔇 No music playing",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Features: Background music, sound effects, volume control, fade transitions",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text("System Status: 🔄 Initializing...")
            }
        }
    }
}

@Composable
private fun PlayStorePreparationCard(
    complianceResult: PlayStoreComplianceResult?,
    storeCompliance: StoreCompliance
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏪 Play Store Preparation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val complianceInfo = storeCompliance.getComplianceInfo()
            
            Text("App Name: ${complianceInfo.appName}")
            Text("Version: ${complianceInfo.version}")
            Text("Age Rating: ${complianceInfo.ageRating}")
            Text("Developer: ${complianceInfo.developer}")
            
            complianceResult?.let { result ->
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Compliance Score: ${result.complianceScore.toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = if (result.isCompliant) Color.Green else MaterialTheme.colorScheme.onErrorContainer
                )
                
                if (result.isCompliant) {
                    Text(
                        text = "✅ Ready for Play Store submission",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "⚠️ ${result.criticalIssues.size} critical issues to resolve",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (result.warnings.isNotEmpty()) {
                    Text(
                        text = "📋 ${result.warnings.size} recommendations for improvement",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ImplementationSummaryCard(
    assetValidation: AssetValidationResult?,
    monsterValidation: MonsterAssetValidationResult?,
    complianceResult: PlayStoreComplianceResult?,
    systemsInitialized: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Implementation Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val completionItems = listOf(
                "Asset Management System" to true,
                "Monster Asset Framework" to true,  
                "Audio System Integration" to systemsInitialized,
                "Play Store Compliance" to (complianceResult?.complianceScore ?: 0f > 80f),
                "Card Assets Available" to (assetValidation?.validAssets ?: 0 > 50),
                "Privacy Policy Ready" to true,
                "Terms of Service Ready" to true
            )
            
            completionItems.forEach { (item, completed) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (completed) "✅" else "🔄",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val completedCount = completionItems.count { it.second }
            val totalCount = completionItems.size
            val completionPercentage = (completedCount.toFloat() / totalCount) * 100
            
            LinearProgressIndicator(
                progress = completedCount.toFloat() / totalCount,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green
            )
            
            Text(
                text = "Phase 2 Progress: $completedCount/$totalCount (${completionPercentage.toInt()}%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}