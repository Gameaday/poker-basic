package com.pokermon.android.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID

/**
 * Custom serializer for Date to handle JSON serialization.
 */
@Serializable
data class SerializableDate(val timestamp: Long) {
    fun toDate(): Date = Date(timestamp)
    
    companion object {
        fun fromDate(date: Date): SerializableDate = SerializableDate(date.time)
    }
}

/**
 * Manages saved game states for continuing interrupted gameplay sessions.
 * Provides automatic save functionality and manual save slot management.
 */
class GameSaveManager private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var instance: GameSaveManager? = null

        fun getInstance(context: Context): GameSaveManager {
            return instance ?: synchronized(this) {
                instance ?: GameSaveManager(context.applicationContext).also { instance = it }
            }
        }

        private const val PREFS_NAME = "pokermon_game_saves"
        private const val KEY_AUTO_SAVE = "auto_save_game"
        private const val KEY_SAVED_GAMES = "saved_games_list"
        private const val MAX_SAVE_SLOTS = 5
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    // Reactive state for UI
    private val _savedGames = MutableStateFlow(loadSavedGames())
    val savedGames: StateFlow<List<SavedGame>> = _savedGames.asStateFlow()

    /**
     * Save the current game state automatically.
     */
    suspend fun autoSaveGame(gameState: SavedGame) {
        val jsonString = json.encodeToString(gameState)
        prefs.edit().putString(KEY_AUTO_SAVE, jsonString).apply()
    }

    /**
     * Load the auto-saved game state.
     */
    fun loadAutoSavedGame(): SavedGame? {
        val jsonString = prefs.getString(KEY_AUTO_SAVE, null) ?: return null
        return try {
            json.decodeFromString<SavedGame>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Save game to a named slot.
     */
    suspend fun saveGameToSlot(gameState: SavedGame, slotName: String) {
        val savedGameWithSlot = gameState.copy(
            id = UUID.randomUUID().toString(),
            slotName = slotName,
            savedAt = SerializableDate.fromDate(Date())
        )
        
        val currentSaves = _savedGames.value.toMutableList()
        
        // Remove existing save with same slot name
        currentSaves.removeIf { it.slotName == slotName }
        
        // Add new save
        currentSaves.add(0, savedGameWithSlot)
        
        // Keep only the most recent saves
        if (currentSaves.size > MAX_SAVE_SLOTS) {
            currentSaves.removeAt(currentSaves.size - 1)
        }
        
        // Save to preferences
        val jsonString = json.encodeToString(currentSaves)
        prefs.edit().putString(KEY_SAVED_GAMES, jsonString).apply()
        
        // Update reactive state
        _savedGames.value = currentSaves
    }

    /**
     * Load a saved game by ID.
     */
    fun loadSavedGame(id: String): SavedGame? {
        return _savedGames.value.find { it.id == id }
    }

    /**
     * Delete a saved game.
     */
    suspend fun deleteSavedGame(id: String) {
        val currentSaves = _savedGames.value.toMutableList()
        currentSaves.removeIf { it.id == id }
        
        val jsonString = json.encodeToString(currentSaves)
        prefs.edit().putString(KEY_SAVED_GAMES, jsonString).apply()
        
        _savedGames.value = currentSaves
    }

    /**
     * Clear the auto-save.
     */
    fun clearAutoSave() {
        prefs.edit().remove(KEY_AUTO_SAVE).apply()
    }

    /**
     * Load saved games from storage.
     */
    private fun loadSavedGames(): List<SavedGame> {
        val jsonString = prefs.getString(KEY_SAVED_GAMES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<SavedGame>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Check if there's an auto-saved game available.
     */
    fun hasAutoSave(): Boolean {
        return prefs.contains(KEY_AUTO_SAVE)
    }
}

/**
 * Represents a saved game state.
 */
@Serializable
data class SavedGame(
    val id: String = UUID.randomUUID().toString(),
    val slotName: String,
    val gameMode: String,
    val playerName: String,
    val currentRound: Int,
    val playerChips: Int,
    val totalPot: Int,
    val playerCards: List<String>,
    val gamePhase: String,
    val savedAt: SerializableDate = SerializableDate.fromDate(Date()),
    val playTime: Long = 0L, // Total play time in milliseconds
    val isAutoSave: Boolean = false,
    val gameProgress: Float = 0.0f, // 0.0 to 1.0 representing completion
    val modeSpecificData: Map<String, String> = emptyMap() // For adventure progress, safari captures, etc.
) {
    val formattedSaveTime: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - savedAt.timestamp
            val minutes = diff / (1000 * 60)
            
            return when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                minutes < 1440 -> "${minutes / 60}h ago"
                else -> "${minutes / 1440}d ago"
            }
        }
    
    val formattedPlayTime: String
        get() {
            val totalMinutes = playTime / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            
            return if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        }
}