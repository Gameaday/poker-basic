package com.pokermon.bridge

import com.pokermon.GameMode
import com.pokermon.database.Monster
import com.pokermon.database.MonsterBattleSystem
import com.pokermon.database.MonsterCollection
import com.pokermon.data.JsonSaveSystem
import com.pokermon.modes.adventure.AdventureMode
import com.pokermon.modes.classic.ClassicGameMode
import com.pokermon.modes.safari.SafariGameMode
import com.pokermon.modes.ironman.IronmanGameMode
import com.pokermon.players.PlayerProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * Comprehensive cross-platform interface manager that integrates the monster system
 * with all game interfaces (Android, Desktop, Console).
 * Provides unified access to monster battles, training, collection management, and saves.
 * 
 * @author Pokermon Cross-Platform System
 * @version 1.0.0
 */
class MonsterSystemBridge {
    private val saveSystem = JsonSaveSystem()
    private val battleSystem = MonsterBattleSystem()
    private val classicMode = ClassicGameMode()
    private val safariMode: SafariGameMode? = null // Will be instantiated per game
    private val ironmanMode: IronmanGameMode? = null // Will be instantiated per game
    private val adventureMode: AdventureMode? = null // Will be instantiated per game
    
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    // State management for reactive UIs
    private val _currentProfile = MutableStateFlow<PlayerProfile?>(null)
    val currentProfile: StateFlow<PlayerProfile?> = _currentProfile.asStateFlow()
    
    private val _monsterCollection = MutableStateFlow<MonsterCollection?>(null)
    val monsterCollection: StateFlow<MonsterCollection?> = _monsterCollection.asStateFlow()
    
    private val _battleStatus = MutableStateFlow<BattleStatus>(BattleStatus.Idle)
    val battleStatus: StateFlow<BattleStatus> = _battleStatus.asStateFlow()
    
    /**
     * Initialize or load player profile
     */
    suspend fun initializeProfile(playerId: String, playerName: String): ProfileResult {
        return try {
            val loadResult = saveSystem.loadPlayerProfile(playerId)
            val profile = when (loadResult) {
                is com.pokermon.data.LoadResult.Success -> {
                    loadResult.data
                }
                is com.pokermon.data.LoadResult.NotFound -> {
                    // Create new profile
                    PlayerProfile(
                        playerId = playerId,
                        playerName = playerName,
                        monsterCollection = MonsterCollection(maxActiveMonsters = 6)
                    )
                }
                is com.pokermon.data.LoadResult.Error -> {
                    return ProfileResult.Error(loadResult.message)
                }
            }
            
            _currentProfile.value = profile
            _monsterCollection.value = profile.monsterCollection
            
            ProfileResult.Success(profile)
        } catch (e: Exception) {
            ProfileResult.Error("Failed to initialize profile: ${e.message}")
        }
    }
    
    /**
     * Save current player profile
     */
    suspend fun saveCurrentProfile(): SaveProfileResult {
        val profile = _currentProfile.value ?: return SaveProfileResult.Error("No profile loaded")
        
        return try {
            val saveResult = saveSystem.savePlayerProfile(profile)
            when (saveResult) {
                is com.pokermon.data.SaveResult.Success -> SaveProfileResult.Success(saveResult.message)
                is com.pokermon.data.SaveResult.Error -> SaveProfileResult.Error(saveResult.message)
            }
        } catch (e: Exception) {
            SaveProfileResult.Error("Failed to save profile: ${e.message}")
        }
    }
    
    /**
     * Start a monster battle for any game mode
     */
    fun startMonsterBattle(gameMode: GameMode, enemyMonster: Monster, handStrength: Int): BattleResult {
        val profile = _currentProfile.value ?: return BattleResult.Error("No profile loaded")
        val playerMonster = profile.monsterCollection.getActiveMonster()
            ?: return BattleResult.Error("No active monster")
        
        _battleStatus.value = BattleStatus.InProgress(playerMonster, enemyMonster)
        
        return try {
            val result = when (gameMode) {
                GameMode.CLASSIC -> {
                    val classicResult = classicMode.triggerMonsterBattle(
                        player = com.pokermon.players.Player(profile.playerName),
                        playerProfile = profile,
                        handStrength = handStrength
                    )
                    classicResult?.let { 
                        BattleResult.Success(it.battleResult, it.chipReward, it.experienceGained)
                    } ?: BattleResult.Error("No battle triggered")
                }
                
                GameMode.ADVENTURE -> {
                    val advMode = AdventureMode(profile.playerName, 1000)
                    val advResult = advMode.triggerFullMonsterBattle(profile, enemyMonster, handStrength)
                    advResult?.let {
                        BattleResult.Success(it.battleResult, 0, it.experienceGained)
                    } ?: BattleResult.Error("No battle triggered")
                }
                
                GameMode.SAFARI -> {
                    val safariMode = SafariGameMode(profile.playerName, 1000)
                    val safariResult = safariMode.triggerSafariEncounter(profile, enemyMonster, handStrength)
                    if (safariResult.battleResult != null) {
                        BattleResult.Success(safariResult.battleResult, if (safariResult.captured) 200 else 0, 50)
                    } else {
                        BattleResult.Error("Safari encounter failed")
                    }
                }
                
                GameMode.IRONMAN -> {
                    val ironmanMode = IronmanGameMode(profile.playerName, 1000)
                    val ironmanResult = ironmanMode.triggerIronmanBattle(profile, enemyMonster, handStrength)
                    ironmanResult?.let {
                        BattleResult.Success(it.battleResult, it.gachaPointsEarned, 0)
                    } ?: BattleResult.Error("No battle triggered")
                }
            }
        } catch (e: Exception) {
            BattleResult.Error("Battle failed: ${e.message}")
        } finally {
            _battleStatus.value = BattleStatus.Idle
        }
    }
    
    /**
     * Train a monster using mode-specific training
     */
    fun trainMonster(monster: Monster, gameMode: GameMode, rounds: Int = 1): MonsterTrainingResult {
        return try {
            val trainedMonster = when (gameMode) {
                GameMode.CLASSIC -> classicMode.trainMonster(monster, rounds)
                GameMode.ADVENTURE -> {
                    val advMode = AdventureMode("trainer", 1000)
                    advMode.trainAdventureMonster(monster, rounds)
                }
                GameMode.SAFARI -> {
                    val safariMode = SafariGameMode("trainer", 1000)
                    safariMode.trainSafariMonster(monster, rounds)
                }
                GameMode.IRONMAN -> {
                    val ironmanMode = IronmanGameMode("trainer", 1000)
                    ironmanMode.trainIronmanMonster(monster, rounds)
                }
            }
            
            // Update monster in collection
            updateMonsterInCollection(trainedMonster)
            
            MonsterTrainingResult.Success(trainedMonster, rounds)
        } catch (e: Exception) {
            MonsterTrainingResult.Error("Training failed: ${e.message}")
        }
    }
    
    /**
     * Add monster to collection
     */
    fun addMonsterToCollection(monster: Monster): CollectionResult {
        val profile = _currentProfile.value ?: return CollectionResult.Error("No profile loaded")
        
        return try {
            val success = profile.monsterCollection.addMonster(monster)
            if (success) {
                _monsterCollection.value = profile.monsterCollection
                CollectionResult.Success("Monster added to collection")
            } else {
                CollectionResult.Error("Failed to add monster - collection might be full")
            }
        } catch (e: Exception) {
            CollectionResult.Error("Error adding monster: ${e.message}")
        }
    }
    
    /**
     * Set active monster for battle (updated to match actual API)
     */
    fun setActiveMonster(monster: Monster): CollectionResult {
        val profile = _currentProfile.value ?: return CollectionResult.Error("No profile loaded")
        
        return try {
            profile.monsterCollection.setActiveMonster(monster)
            _monsterCollection.value = profile.monsterCollection
            CollectionResult.Success("Active monster updated")
        } catch (e: Exception) {
            CollectionResult.Error("Error setting active monster: ${e.message}")
        }
    }
    
    /**
     * Get monster statistics and information
     */
    fun getMonsterInfo(monsterId: String): MonsterInfoResult {
        val profile = _currentProfile.value ?: return MonsterInfoResult.Error("No profile loaded")
        
        val monster = profile.monsterCollection.getOwnedMonsters().find { it.name == monsterId }
            ?: return MonsterInfoResult.Error("Monster not found")
        
        return MonsterInfoResult.Success(
            monster = monster,
            battlesWon = 0, // Would track in profile statistics
            battlesLost = 0,
            totalExperience = 0, // Monster doesn't have experience field yet
            trainingRounds = 0 // Would track in profile
        )
    }
    
    /**
     * Export monster collection for sharing
     */
    suspend fun exportMonsterCollection(fileName: String): ExportResult {
        val collection = _monsterCollection.value ?: return ExportResult.Error("No collection loaded")
        
        return try {
            val saveResult = saveSystem.exportMonsterCollection(collection, fileName)
            when (saveResult) {
                is com.pokermon.data.SaveResult.Success -> ExportResult.Success(saveResult.message)
                is com.pokermon.data.SaveResult.Error -> ExportResult.Error(saveResult.message)
            }
        } catch (e: Exception) {
            ExportResult.Error("Export failed: ${e.message}")
        }
    }
    
    /**
     * List all available save files
     */
    suspend fun listSaveFiles(): List<String> {
        return try {
            saveSystem.listSavedProfiles()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Update monster in collection after training/evolution
     */
    private fun updateMonsterInCollection(updatedMonster: Monster) {
        val profile = _currentProfile.value ?: return
        val collection = profile.monsterCollection
        
        // Replace monster in owned monsters (simplified - would need proper collection update)
        val ownedMonsters = collection.getOwnedMonsters().toMutableList()
        val index = ownedMonsters.indexOfFirst { it.name == updatedMonster.name }
        if (index >= 0) {
            ownedMonsters[index] = updatedMonster
            // Note: In actual implementation, MonsterCollection would need update methods
        }
        
        // Update active monster if it matches
        val activeMonster = collection.getActiveMonster()
        if (activeMonster?.name == updatedMonster.name) {
            collection.setActiveMonster(updatedMonster)
        }
        
        _monsterCollection.value = collection
    }
}

// Result classes for type-safe responses

sealed class ProfileResult {
    data class Success(val profile: PlayerProfile) : ProfileResult()
    data class Error(val message: String) : ProfileResult()
}

sealed class SaveProfileResult {
    data class Success(val message: String) : SaveProfileResult()
    data class Error(val message: String) : SaveProfileResult()
}

sealed class BattleResult {
    data class Success(val result: com.pokermon.database.BattleResult, val reward: Int, val experience: Int) : BattleResult()
    data class Error(val message: String) : BattleResult()
}

sealed class MonsterTrainingResult {
    data class Success(val trainedMonster: Monster, val rounds: Int) : MonsterTrainingResult()
    data class Error(val message: String) : MonsterTrainingResult()
}

sealed class CollectionResult {
    data class Success(val message: String) : CollectionResult()
    data class Error(val message: String) : CollectionResult()
}

sealed class MonsterInfoResult {
    data class Success(
        val monster: Monster,
        val battlesWon: Int,
        val battlesLost: Int,
        val totalExperience: Int,
        val trainingRounds: Int
    ) : MonsterInfoResult()
    data class Error(val message: String) : MonsterInfoResult()
}

sealed class ExportResult {
    data class Success(val message: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}

sealed class BattleStatus {
    object Idle : BattleStatus()
    data class InProgress(val playerMonster: Monster, val enemyMonster: Monster) : BattleStatus()
}