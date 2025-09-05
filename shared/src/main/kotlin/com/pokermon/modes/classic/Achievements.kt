package com.pokermon.modes.classic

import com.pokermon.players.Player
import com.pokermon.database.Monster

/**
 * Classic achievements management system.
 * Tracks and rewards player accomplishments in classic mode.
 * 
 * @author Pokermon Classic Achievements
 * @version 1.1.0
 */
object Achievements {
    
    /**
     * Available achievements in classic mode
     */
    enum class Achievement(
        val id: String,
        val title: String,
        val description: String,
        val points: Int
    ) {
        ROYAL_VICTORY("royal_victory", "Royal Flush Victory", "Win a hand with a Royal Flush", 100),
        STRAIGHT_FLUSH_MASTER("straight_flush", "Straight Flush Master", "Get a Straight Flush", 75),
        QUAD_KING("quad_king", "Four of a Kind King", "Get Four of a Kind", 50),
        FULL_HOUSE_HERO("full_house", "Full House Hero", "Get a Full House", 30),
        FLUSH_MASTER("flush_master", "Flush Master", "Get a Flush", 25),
        STRAIGHT_SHOOTER("straight", "Straight Shooter", "Get a Straight", 20),
        TRIPLE_THREAT("triple_threat", "Triple Threat", "Get Three of a Kind", 15),
        PAIR_PRESSURE("pair_pressure", "Pair Pressure", "Win with Two Pair", 10),
        HIGH_ROLLER("high_roller", "High Roller", "Win 1000 chips in a single hand", 40),
        COMEBACK_KID("comeback", "Comeback Kid", "Win after being down to less than 100 chips", 60),
        MONSTER_BOND("monster_bond", "Monster Bond", "Win 10 hands with the same monster", 35),
        CLASSIC_CHAMPION("champion", "Classic Champion", "Win 100 classic mode games", 200)
    }
    
    /**
     * Player achievement progress tracking
     */
    data class PlayerProgress(
        val playerId: String,
        val unlockedAchievements: MutableSet<Achievement> = mutableSetOf(),
        val handTypeCount: MutableMap<String, Int> = mutableMapOf(),
        var gamesWon: Int = 0,
        var totalChipsWon: Int = 0,
        var biggestWin: Int = 0,
        var comebackWins: Int = 0,
        val monsterWins: MutableMap<String, Int> = mutableMapOf()
    ) {
        fun addAchievement(achievement: Achievement): Boolean {
            return unlockedAchievements.add(achievement)
        }
        
        fun hasAchievement(achievement: Achievement): Boolean {
            return achievement in unlockedAchievements
        }
        
        fun getTotalPoints(): Int {
            return unlockedAchievements.sumOf { it.points }
        }
    }
    
    private val playerProgressMap = mutableMapOf<String, PlayerProgress>()
    
    /**
     * Gets or creates player progress
     */
    fun getPlayerProgress(playerId: String): PlayerProgress {
        return playerProgressMap.getOrPut(playerId) { PlayerProgress(playerId) }
    }
    
    /**
     * Checks and awards achievements based on hand result
     */
    fun checkHandAchievements(
        player: Player,
        handType: String,
        potWon: Int,
        wasComeback: Boolean = false
    ): List<Achievement> {
        val progress = getPlayerProgress(player.name)
        val newAchievements = mutableListOf<Achievement>()
        
        // Update hand type count
        progress.handTypeCount[handType] = progress.handTypeCount.getOrDefault(handType, 0) + 1
        
        // Check hand-based achievements
        val handAchievement = when (handType.uppercase()) {
            "ROYAL_FLUSH" -> Achievement.ROYAL_VICTORY
            "STRAIGHT_FLUSH" -> Achievement.STRAIGHT_FLUSH_MASTER
            "FOUR_OF_A_KIND" -> Achievement.QUAD_KING
            "FULL_HOUSE" -> Achievement.FULL_HOUSE_HERO
            "FLUSH" -> Achievement.FLUSH_MASTER
            "STRAIGHT" -> Achievement.STRAIGHT_SHOOTER
            "THREE_OF_A_KIND" -> Achievement.TRIPLE_THREAT
            "TWO_PAIR" -> Achievement.PAIR_PRESSURE
            else -> null
        }
        
        if (handAchievement != null && progress.addAchievement(handAchievement)) {
            newAchievements.add(handAchievement)
        }
        
        // Check pot-based achievements
        if (potWon >= 1000 && progress.addAchievement(Achievement.HIGH_ROLLER)) {
            newAchievements.add(Achievement.HIGH_ROLLER)
        }
        
        // Track biggest win
        if (potWon > progress.biggestWin) {
            progress.biggestWin = potWon
        }
        
        // Check comeback achievement
        if (wasComeback && progress.addAchievement(Achievement.COMEBACK_KID)) {
            newAchievements.add(Achievement.COMEBACK_KID)
            progress.comebackWins++
        }
        
        // Track monster wins
        val monster = player.currentMonster
        if (monster != null) {
            val monsterWins = progress.monsterWins.getOrDefault(monster.name, 0) + 1
            progress.monsterWins[monster.name] = monsterWins
            
            if (monsterWins >= 10 && progress.addAchievement(Achievement.MONSTER_BOND)) {
                newAchievements.add(Achievement.MONSTER_BOND)
            }
        }
        
        return newAchievements
    }
    
    /**
     * Checks and awards game completion achievements
     */
    fun checkGameAchievements(player: Player, gameWon: Boolean): List<Achievement> {
        val progress = getPlayerProgress(player.name)
        val newAchievements = mutableListOf<Achievement>()
        
        if (gameWon) {
            progress.gamesWon++
            
            if (progress.gamesWon >= 100 && progress.addAchievement(Achievement.CLASSIC_CHAMPION)) {
                newAchievements.add(Achievement.CLASSIC_CHAMPION)
            }
        }
        
        return newAchievements
    }
    
    /**
     * Gets achievement statistics for a player
     */
    fun getAchievementStats(playerId: String): AchievementStats {
        val progress = getPlayerProgress(playerId)
        val totalAchievements = Achievement.values().size
        val unlockedCount = progress.unlockedAchievements.size
        val completionPercentage = (unlockedCount.toFloat() / totalAchievements * 100).toInt()
        
        return AchievementStats(
            totalAchievements = totalAchievements,
            unlockedAchievements = unlockedCount,
            completionPercentage = completionPercentage,
            totalPoints = progress.getTotalPoints(),
            recentAchievements = progress.unlockedAchievements.toList().takeLast(5)
        )
    }
    
    /**
     * Gets all available achievements with unlock status
     */
    fun getAllAchievements(playerId: String): List<AchievementInfo> {
        val progress = getPlayerProgress(playerId)
        
        return Achievement.values().map { achievement ->
            AchievementInfo(
                achievement = achievement,
                unlocked = progress.hasAchievement(achievement),
                progress = getAchievementProgress(achievement, progress)
            )
        }
    }
    
    private fun getAchievementProgress(achievement: Achievement, progress: PlayerProgress): Float {
        return when (achievement) {
            Achievement.CLASSIC_CHAMPION -> (progress.gamesWon.toFloat() / 100).coerceAtMost(1.0f)
            Achievement.HIGH_ROLLER -> if (progress.biggestWin >= 1000) 1.0f else progress.biggestWin / 1000f
            Achievement.MONSTER_BOND -> {
                val maxMonsterWins = progress.monsterWins.values.maxOrNull() ?: 0
                (maxMonsterWins.toFloat() / 10).coerceAtMost(1.0f)
            }
            else -> if (progress.hasAchievement(achievement)) 1.0f else 0.0f
        }
    }
}

/**
 * Achievement statistics for a player
 */
data class AchievementStats(
    val totalAchievements: Int,
    val unlockedAchievements: Int,
    val completionPercentage: Int,
    val totalPoints: Int,
    val recentAchievements: List<Achievements.Achievement>
)

/**
 * Achievement information with unlock status
 */
data class AchievementInfo(
    val achievement: Achievements.Achievement,
    val unlocked: Boolean,
    val progress: Float
)