package com.pokermon.android.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Comprehensive user profile manager for persistent Pokermon user experience.
 * Handles user data, game progress, statistics, and settings persistence.
 */
class UserProfileManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: UserProfileManager? = null
        
        fun getInstance(context: Context): UserProfileManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserProfileManager(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // SharedPreferences keys
        private const val PREFS_NAME = "pokermon_user_profile"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_TOTAL_GAMES = "total_games"
        private const val KEY_GAMES_WON = "games_won"
        private const val KEY_TOTAL_CHIPS_WON = "total_chips_won"
        private const val KEY_HIGHEST_HAND = "highest_hand"
        private const val KEY_FAVORITE_GAME_MODE = "favorite_game_mode"
        private const val KEY_ACHIEVEMENTS = "achievements"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_ANIMATIONS_ENABLED = "animations_enabled"
        private const val KEY_AUTO_SAVE_ENABLED = "auto_save_enabled"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_PLAYED = "last_played"
        private const val KEY_MONSTERS_COLLECTED = "monsters_collected"
        private const val KEY_ADVENTURE_PROGRESS = "adventure_progress"
        private const val KEY_SAFARI_ENCOUNTERS = "safari_encounters"
        private const val KEY_IRONMAN_PULLS = "ironman_pulls"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Reactive state flows for UI
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _gameSettings = MutableStateFlow(loadGameSettings())
    val gameSettings: StateFlow<GameSettings> = _gameSettings.asStateFlow()
    
    init {
        // Initialize user ID if first launch
        if (isFirstLaunch()) {
            initializeNewUser()
        }
    }
    
    /**
     * Load user profile from persistent storage.
     */
    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            userId = prefs.getString(KEY_USER_ID, "") ?: "",
            username = prefs.getString(KEY_USERNAME, "Pokermon Trainer") ?: "Pokermon Trainer",
            totalGamesPlayed = prefs.getInt(KEY_TOTAL_GAMES, 0),
            gamesWon = prefs.getInt(KEY_GAMES_WON, 0),
            totalChipsWon = prefs.getLong(KEY_TOTAL_CHIPS_WON, 0L),
            highestHand = prefs.getString(KEY_HIGHEST_HAND, "High Card") ?: "High Card",
            favoriteGameMode = prefs.getString(KEY_FAVORITE_GAME_MODE, "CLASSIC") ?: "CLASSIC",
            achievements = loadAchievements(),
            lastPlayed = Date(prefs.getLong(KEY_LAST_PLAYED, System.currentTimeMillis())),
            monstersCollected = prefs.getInt(KEY_MONSTERS_COLLECTED, 0),
            adventureProgress = prefs.getInt(KEY_ADVENTURE_PROGRESS, 0),
            safariEncounters = prefs.getInt(KEY_SAFARI_ENCOUNTERS, 0),
            ironmanPulls = prefs.getInt(KEY_IRONMAN_PULLS, 0)
        )
    }
    
    /**
     * Load game settings from persistent storage.
     */
    private fun loadGameSettings(): GameSettings {
        return GameSettings(
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            animationsEnabled = prefs.getBoolean(KEY_ANIMATIONS_ENABLED, true),
            autoSaveEnabled = prefs.getBoolean(KEY_AUTO_SAVE_ENABLED, true),
            selectedTheme = prefs.getString(KEY_SELECTED_THEME, "CLASSIC_GREEN") ?: "CLASSIC_GREEN"
        )
    }
    
    /**
     * Load achievements from comma-separated string.
     */
    private fun loadAchievements(): List<String> {
        val achievementsString = prefs.getString(KEY_ACHIEVEMENTS, "") ?: ""
        return if (achievementsString.isBlank()) {
            emptyList()
        } else {
            achievementsString.split(",").filter { it.isNotBlank() }
        }
    }
    
    /**
     * Check if this is the first app launch.
     */
    private fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * Initialize a new user profile.
     */
    private fun initializeNewUser() {
        val userId = UUID.randomUUID().toString()
        
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, "Pokermon Trainer")
            putInt(KEY_TOTAL_GAMES, 0)
            putInt(KEY_GAMES_WON, 0)
            putLong(KEY_TOTAL_CHIPS_WON, 0L)
            putString(KEY_HIGHEST_HAND, "High Card")
            putString(KEY_FAVORITE_GAME_MODE, "CLASSIC")
            putString(KEY_ACHIEVEMENTS, "")
            putBoolean(KEY_SOUND_ENABLED, true)
            putBoolean(KEY_ANIMATIONS_ENABLED, true)
            putBoolean(KEY_AUTO_SAVE_ENABLED, true)
            putString(KEY_SELECTED_THEME, "CLASSIC_GREEN")
            putBoolean(KEY_FIRST_LAUNCH, false)
            putLong(KEY_LAST_PLAYED, System.currentTimeMillis())
            putInt(KEY_MONSTERS_COLLECTED, 0)
            putInt(KEY_ADVENTURE_PROGRESS, 0)
            putInt(KEY_SAFARI_ENCOUNTERS, 0)
            putInt(KEY_IRONMAN_PULLS, 0)
        }.apply()
        
        // Update reactive state
        _userProfile.value = loadUserProfile()
        _gameSettings.value = loadGameSettings()
    }
    
    /**
     * Update user profile and persist to storage.
     */
    fun updateUserProfile(profile: UserProfile) {
        prefs.edit().apply {
            putString(KEY_USERNAME, profile.username)
            putInt(KEY_TOTAL_GAMES, profile.totalGamesPlayed)
            putInt(KEY_GAMES_WON, profile.gamesWon)
            putLong(KEY_TOTAL_CHIPS_WON, profile.totalChipsWon)
            putString(KEY_HIGHEST_HAND, profile.highestHand)
            putString(KEY_FAVORITE_GAME_MODE, profile.favoriteGameMode)
            putString(KEY_ACHIEVEMENTS, profile.achievements.joinToString(","))
            putLong(KEY_LAST_PLAYED, profile.lastPlayed.time)
            putInt(KEY_MONSTERS_COLLECTED, profile.monstersCollected)
            putInt(KEY_ADVENTURE_PROGRESS, profile.adventureProgress)
            putInt(KEY_SAFARI_ENCOUNTERS, profile.safariEncounters)
            putInt(KEY_IRONMAN_PULLS, profile.ironmanPulls)
        }.apply()
        
        _userProfile.value = profile
    }
    
    /**
     * Update game settings and persist to storage.
     */
    fun updateGameSettings(settings: GameSettings) {
        prefs.edit().apply {
            putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            putBoolean(KEY_ANIMATIONS_ENABLED, settings.animationsEnabled)
            putBoolean(KEY_AUTO_SAVE_ENABLED, settings.autoSaveEnabled)
            putString(KEY_SELECTED_THEME, settings.selectedTheme)
        }.apply()
        
        _gameSettings.value = settings
    }
    
    /**
     * Record a game completion and update statistics.
     */
    fun recordGameCompletion(won: Boolean, chipsWon: Long, handAchieved: String, gameMode: String) {
        val currentProfile = _userProfile.value
        val updatedProfile = currentProfile.copy(
            totalGamesPlayed = currentProfile.totalGamesPlayed + 1,
            gamesWon = if (won) currentProfile.gamesWon + 1 else currentProfile.gamesWon,
            totalChipsWon = currentProfile.totalChipsWon + chipsWon,
            highestHand = if (isHigherHand(handAchieved, currentProfile.highestHand)) handAchieved else currentProfile.highestHand,
            favoriteGameMode = gameMode,
            lastPlayed = Date()
        )
        
        updateUserProfile(updatedProfile)
        
        // Check for new achievements
        checkAndAwardAchievements(updatedProfile)
    }
    
    /**
     * Award an achievement to the user.
     */
    fun awardAchievement(achievement: String) {
        val currentProfile = _userProfile.value
        if (!currentProfile.achievements.contains(achievement)) {
            val updatedProfile = currentProfile.copy(
                achievements = currentProfile.achievements + achievement
            )
            updateUserProfile(updatedProfile)
        }
    }
    
    /**
     * Export user profile and settings as JSON string for backup.
     */
    fun exportUserData(): String {
        val profile = _userProfile.value
        val settings = _gameSettings.value
        
        return """
        {
            "userProfile": {
                "userId": "${profile.userId}",
                "username": "${profile.username}",
                "totalGamesPlayed": ${profile.totalGamesPlayed},
                "gamesWon": ${profile.gamesWon},
                "totalChipsWon": ${profile.totalChipsWon},
                "highestHand": "${profile.highestHand}",
                "favoriteGameMode": "${profile.favoriteGameMode}",
                "achievements": [${profile.achievements.joinToString(",") { "\"$it\"" }}],
                "lastPlayed": ${profile.lastPlayed.time},
                "monstersCollected": ${profile.monstersCollected},
                "adventureProgress": ${profile.adventureProgress},
                "safariEncounters": ${profile.safariEncounters},
                "ironmanPulls": ${profile.ironmanPulls}
            },
            "gameSettings": {
                "soundEnabled": ${settings.soundEnabled},
                "animationsEnabled": ${settings.animationsEnabled},
                "autoSaveEnabled": ${settings.autoSaveEnabled},
                "selectedTheme": "${settings.selectedTheme}"
            }
        }
        """.trimIndent()
    }
    
    /**
     * Clear all user data (for delete functionality).
     */
    fun clearAllUserData() {
        prefs.edit().clear().apply()
        initializeNewUser()
    }
    
    /**
     * Check if a poker hand is higher than another.
     */
    private fun isHigherHand(newHand: String, currentHand: String): Boolean {
        val handRankings = listOf(
            "High Card", "Pair", "Two Pair", "Three of a Kind", 
            "Straight", "Flush", "Full House", "Four of a Kind", 
            "Straight Flush", "Royal Flush"
        )
        
        val newRank = handRankings.indexOf(newHand)
        val currentRank = handRankings.indexOf(currentHand)
        
        return newRank > currentRank
    }
    
    /**
     * Check for new achievements based on user progress.
     */
    private fun checkAndAwardAchievements(profile: UserProfile) {
        // First game achievement
        if (profile.totalGamesPlayed == 1) {
            awardAchievement("First Steps")
        }
        
        // Win streak achievements
        if (profile.gamesWon >= 10) {
            awardAchievement("Winning Streak")
        }
        
        // Chip accumulation achievements
        if (profile.totalChipsWon >= 10000) {
            awardAchievement("High Roller")
        }
        
        // Hand achievements
        if (profile.highestHand == "Royal Flush") {
            awardAchievement("Royal Achievement")
        }
        
        // Monster achievements (for future monster modes)
        if (profile.monstersCollected >= 10) {
            awardAchievement("Monster Collector")
        }
    }
}

/**
 * Data class representing a user's profile and game progress.
 */
data class UserProfile(
    val userId: String,
    val username: String,
    val totalGamesPlayed: Int,
    val gamesWon: Int,
    val totalChipsWon: Long,
    val highestHand: String,
    val favoriteGameMode: String,
    val achievements: List<String>,
    val lastPlayed: Date,
    val monstersCollected: Int,
    val adventureProgress: Int,
    val safariEncounters: Int,
    val ironmanPulls: Int
) {
    val winRate: Double
        get() = if (totalGamesPlayed > 0) gamesWon.toDouble() / totalGamesPlayed else 0.0
}

/**
 * Data class representing user's game settings.
 */
data class GameSettings(
    val soundEnabled: Boolean,
    val animationsEnabled: Boolean,
    val autoSaveEnabled: Boolean,
    val selectedTheme: String
)