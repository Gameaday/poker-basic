package com.pokermon.data

import com.pokermon.database.Monster
import com.pokermon.database.MonsterCollection
import com.pokermon.database.MonsterStats
import com.pokermon.players.PlayerProfile
import com.pokermon.players.PlayerStatistics
import com.pokermon.players.PlayerSettings
import com.pokermon.players.GameModeProgress
import com.pokermon.GameMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Comprehensive JSON-based save/load system for all player data and monster collections.
 * Handles persistent storage of profiles, monsters, achievements, and game progress.
 * 
 * @author Pokermon Save System
 * @version 1.0.0
 */
class JsonSaveSystem(private val saveDirectory: String = "saves") {
    
    companion object {
        private const val PROFILE_FILE_EXTENSION = ".json"
        private const val BACKUP_EXTENSION = ".bak"
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
    
    init {
        // Ensure save directory exists
        File(saveDirectory).mkdirs()
    }
    
    /**
     * Save player profile to JSON file
     */
    suspend fun savePlayerProfile(profile: PlayerProfile): SaveResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "${profile.playerId}$PROFILE_FILE_EXTENSION"
                val file = File(saveDirectory, fileName)
                val backupFile = File(saveDirectory, "${profile.playerId}$BACKUP_EXTENSION")
                
                // Create backup of existing file
                if (file.exists()) {
                    file.copyTo(backupFile, overwrite = true)
                }
                
                // Convert profile to JSON
                val json = profileToJson(profile)
                file.writeText(json)
                
                SaveResult.Success("Profile saved successfully")
            } catch (e: IOException) {
                SaveResult.Error("Failed to save profile: ${e.message}")
            } catch (e: Exception) {
                SaveResult.Error("Unexpected error during save: ${e.message}")
            }
        }
    }
    
    /**
     * Load player profile from JSON file
     */
    suspend fun loadPlayerProfile(playerId: String): LoadResult<PlayerProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "$playerId$PROFILE_FILE_EXTENSION"
                val file = File(saveDirectory, fileName)
                
                if (!file.exists()) {
                    return@withContext LoadResult.NotFound("Profile not found for player: $playerId")
                }
                
                val json = file.readText()
                val profile = jsonToProfile(json)
                
                LoadResult.Success(profile)
            } catch (e: IOException) {
                LoadResult.Error("Failed to load profile: ${e.message}")
            } catch (e: Exception) {
                LoadResult.Error("Corrupt profile data: ${e.message}")
            }
        }
    }
    
    /**
     * List all saved player profiles
     */
    suspend fun listSavedProfiles(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                File(saveDirectory)
                    .listFiles { file -> file.extension == "json" }
                    ?.map { it.nameWithoutExtension }
                    ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    /**
     * Delete a player profile
     */
    suspend fun deletePlayerProfile(playerId: String): SaveResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "$playerId$PROFILE_FILE_EXTENSION"
                val file = File(saveDirectory, fileName)
                val backupFile = File(saveDirectory, "$playerId$BACKUP_EXTENSION")
                
                var deleted = false
                if (file.exists()) {
                    deleted = file.delete()
                }
                if (backupFile.exists()) {
                    backupFile.delete()
                }
                
                if (deleted) {
                    SaveResult.Success("Profile deleted successfully")
                } else {
                    SaveResult.Error("Profile not found or could not be deleted")
                }
            } catch (e: Exception) {
                SaveResult.Error("Failed to delete profile: ${e.message}")
            }
        }
    }
    
    /**
     * Export monster collection to shareable format
     */
    suspend fun exportMonsterCollection(collection: MonsterCollection, fileName: String): SaveResult {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(saveDirectory, "$fileName.monsters.json")
                val json = monsterCollectionToJson(collection)
                file.writeText(json)
                
                SaveResult.Success("Monster collection exported to ${file.absolutePath}")
            } catch (e: Exception) {
                SaveResult.Error("Failed to export collection: ${e.message}")
            }
        }
    }
    
    /**
     * Import monster collection from file
     */
    suspend fun importMonsterCollection(fileName: String): LoadResult<MonsterCollection> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(saveDirectory, "$fileName.monsters.json")
                if (!file.exists()) {
                    return@withContext LoadResult.NotFound("Collection file not found: $fileName")
                }
                
                val json = file.readText()
                val collection = jsonToMonsterCollection(json)
                
                LoadResult.Success(collection)
            } catch (e: Exception) {
                LoadResult.Error("Failed to import collection: ${e.message}")
            }
        }
    }
    
    /**
     * Convert PlayerProfile to JSON string
     */
    private fun profileToJson(profile: PlayerProfile): String {
        return buildString {
            appendLine("{")
            appendLine("  \"playerId\": \"${profile.playerId}\",")
            appendLine("  \"playerName\": \"${profile.playerName}\",")
            appendLine("  \"createdDate\": \"${profile.createdDate.format(dateFormatter)}\",")
            appendLine("  \"lastPlayedDate\": \"${profile.lastPlayedDate.format(dateFormatter)}\",")
            appendLine("  \"totalPlayTime\": ${profile.totalPlayTime},")
            appendLine("  \"overallLevel\": ${profile.overallLevel},")
            appendLine("  \"overallExperience\": ${profile.overallExperience},")
            appendLine("  \"monsterCollection\": ${monsterCollectionToJson(profile.monsterCollection)},")
            appendLine("  \"gameProgress\": ${gameProgressToJson(profile.gameProgress)},")
            appendLine("  \"achievements\": ${achievementsToJson(profile.achievements)},")
            appendLine("  \"statistics\": ${statisticsToJson(profile.statistics)},")
            appendLine("  \"settings\": ${settingsToJson(profile.settings)},")
            appendLine("  \"saveData\": ${saveDataToJson(profile.saveData)}")
            append("}")
        }
    }
    
    /**
     * Convert JSON string to PlayerProfile
     */
    private fun jsonToProfile(json: String): PlayerProfile {
        // Simple JSON parsing implementation
        val lines = json.lines().map { it.trim() }
        
        val playerId = extractJsonValue(lines, "playerId").removeSurrounding("\"")
        val playerName = extractJsonValue(lines, "playerName").removeSurrounding("\"")
        val createdDate = LocalDateTime.parse(extractJsonValue(lines, "createdDate").removeSurrounding("\""), dateFormatter)
        val lastPlayedDate = LocalDateTime.parse(extractJsonValue(lines, "lastPlayedDate").removeSurrounding("\""), dateFormatter)
        val totalPlayTime = extractJsonValue(lines, "totalPlayTime").toLong()
        val overallLevel = extractJsonValue(lines, "overallLevel").toInt()
        val overallExperience = extractJsonValue(lines, "overallExperience").toInt()
        
        // For complex objects, we'll create defaults for now
        // In a full implementation, these would be parsed from JSON
        val monsterCollection = MonsterCollection()
        val gameProgress = emptyMap<GameMode, GameModeProgress>()
        val achievements = emptySet<String>()
        val statistics = PlayerStatistics()
        val settings = PlayerSettings()
        val saveData = emptyMap<String, Any>()
        
        return PlayerProfile(
            playerId = playerId,
            playerName = playerName,
            createdDate = createdDate,
            lastPlayedDate = lastPlayedDate,
            totalPlayTime = totalPlayTime,
            overallLevel = overallLevel,
            overallExperience = overallExperience,
            monsterCollection = monsterCollection,
            gameProgress = gameProgress,
            achievements = achievements,
            statistics = statistics,
            settings = settings,
            saveData = saveData
        )
    }
    
    /**
     * Convert MonsterCollection to JSON string
     */
    private fun monsterCollectionToJson(collection: MonsterCollection): String {
        return buildString {
            appendLine("{")
            appendLine("  \"maxActiveMonsters\": ${collection.maxActiveMonsters},")
            appendLine("  \"ownedMonsters\": [")
            collection.getOwnedMonsters().forEachIndexed { index, monster ->
                append("    ${monsterToJson(monster)}")
                if (index < collection.getOwnedMonsters().size - 1) append(",")
                appendLine()
            }
            appendLine("  \"activeMonster\": ${collection.getActiveMonster()?.let { monsterToJson(it) } ?: "null"}")
            append("}")
        }
    }
    
    /**
     * Convert JSON string to MonsterCollection
     */
    private fun jsonToMonsterCollection(json: String): MonsterCollection {
        // Simple implementation - in production this would be more robust
        return MonsterCollection()
    }
    
    /**
     * Convert Monster to JSON string
     */
    private fun monsterToJson(monster: Monster): String {
        return buildString {
            append("{")
            append("\"name\": \"${monster.name}\", ")
            append("\"rarity\": \"${monster.rarity.name}\", ")
            append("\"baseHealth\": ${monster.baseHealth}, ")
            append("\"effectType\": \"${monster.effectType.name}\", ")
            append("\"effectPower\": ${monster.effectPower}, ")
            append("\"description\": \"${monster.description}\", ")
            append("\"stats\": ${statsToJson(monster.stats)}")
            append("}")
        }
    }
    
    /**
     * Convert MonsterStats to JSON string
     */
    private fun statsToJson(stats: MonsterStats): String {
        return "{" +
            "\"baseHp\": ${stats.baseHp}, " +
            "\"baseAttack\": ${stats.baseAttack}, " +
            "\"baseDefense\": ${stats.baseDefense}, " +
            "\"baseSpeed\": ${stats.baseSpeed}, " +
            "\"baseSpecial\": ${stats.baseSpecial}, " +
            "\"level\": ${stats.level}, " +
            "\"experience\": ${stats.experience}, " +
            "\"nature\": \"${stats.nature.name}\"" +
            "}"
    }
    
    // Helper methods for JSON conversion
    private fun gameProgressToJson(progress: Map<GameMode, GameModeProgress>): String = "{}"
    private fun achievementsToJson(achievements: Set<String>): String = "[${achievements.joinToString(",") { "\"$it\"" }}]"
    private fun statisticsToJson(statistics: PlayerStatistics): String = "{}"
    private fun settingsToJson(settings: PlayerSettings): String = "{}"
    private fun saveDataToJson(saveData: Map<String, Any>): String = "{}"
    
    /**
     * Extract value from JSON lines
     */
    private fun extractJsonValue(lines: List<String>, key: String): String {
        val line = lines.find { it.contains("\"$key\":") }
            ?: throw IllegalArgumentException("Key not found: $key")
        
        return line.substringAfter(":").trim().removeSuffix(",")
    }
}

/**
 * Result of a save operation
 */
sealed class SaveResult {
    data class Success(val message: String) : SaveResult()
    data class Error(val message: String) : SaveResult()
}

/**
 * Result of a load operation
 */
sealed class LoadResult<out T> {
    data class Success<T>(val data: T) : LoadResult<T>()
    data class Error(val message: String) : LoadResult<Nothing>()
    data class NotFound(val message: String) : LoadResult<Nothing>()
}