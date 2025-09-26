package com.pokermon.players

import com.pokermon.GameMode
import com.pokermon.database.Monster
import com.pokermon.database.MonsterCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/**
 * Comprehensive player profile system supporting cross-game progression,
 * monster collection, and persistent save data.
 *
 * @author Pokermon Profile System
 * @version 1.0.0
 */
data class PlayerProfile(
    val playerId: String,
    val playerName: String,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val lastPlayedDate: LocalDateTime = LocalDateTime.now(),
    val totalPlayTime: Long = 0, // in seconds
    val overallLevel: Int = 1,
    val overallExperience: Int = 0,
    val monsterCollection: MonsterCollection = MonsterCollection(maxActiveMonsters = 6),
    val gameProgress: Map<GameMode, GameModeProgress> = emptyMap(),
    val achievements: Set<String> = emptySet(),
    val statistics: PlayerStatistics = PlayerStatistics(),
    val settings: PlayerSettings = PlayerSettings(),
    val saveData: Map<String, Any> = emptyMap(),
) {
    companion object {
        private const val EXP_TO_NEXT_LEVEL = 1000
        private const val MAX_OVERALL_LEVEL = 200
    }

    val expToNextLevel: Int get() = EXP_TO_NEXT_LEVEL * overallLevel
    val canLevelUp: Boolean get() = overallLevel < MAX_OVERALL_LEVEL && overallExperience >= expToNextLevel

    /**
     * Gain overall experience across all game modes
     */
    fun gainExperience(exp: Int): PlayerProfile {
        val newExp = overallExperience + exp
        var newProfile = copy(overallExperience = newExp, lastPlayedDate = LocalDateTime.now())

        while (newProfile.canLevelUp) {
            newProfile = newProfile.copy(overallLevel = newProfile.overallLevel + 1)
        }

        return newProfile
    }

    /**
     * Add a monster to the collection
     */
    fun addMonster(monster: Monster): PlayerProfile {
        val updatedCollection =
            MonsterCollection(monsterCollection.maxActiveMonsters).apply {
                monsterCollection.getOwnedMonsters().forEach { addMonster(it) }
                addMonster(monster)
            }
        return copy(monsterCollection = updatedCollection)
    }

    /**
     * Unlock an achievement
     */
    fun unlockAchievement(achievementId: String): PlayerProfile {
        if (achievementId in achievements) return this
        return copy(achievements = achievements + achievementId)
    }

    /**
     * Update game mode progress
     */
    fun updateGameProgress(
        mode: GameMode,
        progress: GameModeProgress,
    ): PlayerProfile {
        val updatedProgress = gameProgress + (mode to progress)
        return copy(gameProgress = updatedProgress)
    }

    /**
     * Update player statistics
     */
    fun updateStatistics(update: (PlayerStatistics) -> PlayerStatistics): PlayerProfile {
        return copy(statistics = update(statistics))
    }

    /**
     * Save custom data
     */
    fun saveData(
        key: String,
        value: Any,
    ): PlayerProfile {
        return copy(saveData = saveData + (key to value))
    }
}

/**
 * Progress tracking for individual game modes
 */
data class GameModeProgress(
    val mode: GameMode,
    val level: Int = 1,
    val experience: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalChipsWon: Int = 0,
    val totalChipsLost: Int = 0,
    val averageGameLength: Double = 0.0,
    val lastPlayed: LocalDateTime = LocalDateTime.now(),
    val modeSpecificData: Map<String, Any> = emptyMap(),
) {
    val winRate: Double get() = if (gamesPlayed > 0) gamesWon.toDouble() / gamesPlayed else 0.0
    val netChips: Int get() = totalChipsWon - totalChipsLost

    fun recordGame(
        won: Boolean,
        chipsChange: Int,
        gameLength: Long,
    ): GameModeProgress {
        val newGamesPlayed = gamesPlayed + 1
        val newGamesWon = if (won) gamesWon + 1 else gamesWon
        val newStreak = if (won) currentStreak + 1 else 0
        val newBestStreak = if (newStreak > bestStreak) newStreak else bestStreak
        val newTotalWon = if (chipsChange > 0) totalChipsWon + chipsChange else totalChipsWon
        val newTotalLost = if (chipsChange < 0) totalChipsLost + kotlin.math.abs(chipsChange) else totalChipsLost
        val newAvgLength = ((averageGameLength * gamesPlayed) + gameLength) / newGamesPlayed

        return copy(
            gamesPlayed = newGamesPlayed,
            gamesWon = newGamesWon,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            totalChipsWon = newTotalWon,
            totalChipsLost = newTotalLost,
            averageGameLength = newAvgLength,
            lastPlayed = LocalDateTime.now(),
        )
    }
}

/**
 * Comprehensive player statistics across all game modes
 */
data class PlayerStatistics(
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val totalPlayTimeSeconds: Long = 0,
    val totalChipsWon: Int = 0,
    val totalChipsLost: Int = 0,
    val monstersCollected: Int = 0,
    val monstersEvolved: Int = 0,
    val battlesFought: Int = 0,
    val battlesWon: Int = 0,
    val questsCompleted: Int = 0,
    val achievementsUnlocked: Int = 0,
    val handCounts: Map<String, Int> = emptyMap(), // Track poker hands achieved
    val favoriteGameMode: GameMode? = null,
    val longestSession: Long = 0, // in seconds
    val firstGameDate: LocalDateTime? = null,
    val milestones: Map<String, LocalDateTime> = emptyMap(),
) {
    val overallWinRate: Double get() = if (totalGamesPlayed > 0) totalGamesWon.toDouble() / totalGamesPlayed else 0.0
    val battleWinRate: Double get() = if (battlesFought > 0) battlesWon.toDouble() / battlesFought else 0.0
    val netChips: Int get() = totalChipsWon - totalChipsLost
    val averageSessionLength: Double get() = if (totalGamesPlayed > 0) totalPlayTimeSeconds.toDouble() / totalGamesPlayed else 0.0
}

/**
 * Player preferences and settings
 */
data class PlayerSettings(
    val autoSave: Boolean = true,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val autoSkipAI: Boolean = false,
    val difficultyPreference: String = "Normal",
    val preferredCardPack: String = "TET",
    val interfaceTheme: String = "Default",
    val languagePreference: String = "English",
    val tutorialCompleted: Map<GameMode, Boolean> = emptyMap(),
    val notificationSettings: Map<String, Boolean> =
        mapOf(
            "achievements" to true,
            "levelUp" to true,
            "evolution" to true,
            "newMonster" to true,
        ),
)

/**
 * Profile manager handles save/load and live profile updates
 */
class ProfileManager private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: ProfileManager? = null

        fun getInstance(): ProfileManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileManager().also { INSTANCE = it }
            }
        }
    }

    private val _currentProfile = MutableStateFlow<PlayerProfile?>(null)
    val currentProfile: StateFlow<PlayerProfile?> = _currentProfile.asStateFlow()

    private val _profiles = MutableStateFlow<Map<String, PlayerProfile>>(emptyMap())
    val profiles: StateFlow<Map<String, PlayerProfile>> = _profiles.asStateFlow()

    /**
     * Create a new player profile
     */
    fun createProfile(
        playerId: String,
        playerName: String,
    ): PlayerProfile {
        val profile = PlayerProfile(playerId = playerId, playerName = playerName)
        _profiles.value = _profiles.value + (playerId to profile)
        return profile
    }

    /**
     * Load an existing profile
     */
    fun loadProfile(playerId: String): PlayerProfile? {
        val profile = _profiles.value[playerId]
        _currentProfile.value = profile
        return profile
    }

    /**
     * Save profile changes
     */
    fun saveProfile(profile: PlayerProfile) {
        _profiles.value = _profiles.value + (profile.playerId to profile)
        if (_currentProfile.value?.playerId == profile.playerId) {
            _currentProfile.value = profile
        }
    }

    /**
     * Update current profile
     */
    fun updateCurrentProfile(update: (PlayerProfile) -> PlayerProfile) {
        _currentProfile.value?.let { current ->
            val updated = update(current)
            saveProfile(updated)
        }
    }
}
