package com.pokermon.modes.safari

import com.pokermon.modes.Achievement
import com.pokermon.modes.AchievementCategory
import com.pokermon.database.Monster

/**
 * Safari Mode specific achievements.
 * Track player accomplishments in monster capture adventures.
 *
 * @author Pokermon Safari Achievement System
 * @version 1.0.0
 */
object SafariAchievements {
    
    /**
     * Safari-specific achievement definitions
     */
    enum class SafariAchievement(
        val id: String,
        val displayName: String,
        val description: String,
        val points: Int,
        val iconName: String = "safari_trophy"
    ) {
        FIRST_CAPTURE(
            "safari_first_capture",
            "First Catch",
            "Successfully capture your first monster in Safari Mode",
            10,
            "safari_ball"
        ),
        
        PERFECT_CAPTURE(
            "safari_perfect_capture", 
            "Perfect Safari",
            "Complete a safari with 100% capture success rate (minimum 5 encounters)",
            50,
            "perfect_ball"
        ),
        
        LEGENDARY_HUNTER(
            "safari_legendary_hunter",
            "Legendary Hunter", 
            "Capture a Legendary monster in Safari Mode",
            100,
            "legendary_crown"
        ),
        
        COLLECTION_MASTER(
            "safari_collection_master",
            "Collection Master",
            "Capture monsters of all 5 rarity types in a single safari",
            75,
            "collection_book"
        ),
        
        STORM_CHASER(
            "safari_storm_chaser",
            "Storm Chaser",
            "Capture a monster during a storm",
            25,
            "storm_cloud"
        ),
        
        NIGHT_STALKER(
            "safari_night_stalker",
            "Night Stalker",
            "Capture 10 monsters during nighttime",
            30,
            "moon_badge"
        ),
        
        ENVIRONMENT_EXPLORER(
            "safari_environment_explorer",
            "Environment Explorer",
            "Capture monsters in all 5 terrain types",
            40,
            "compass"
        ),
        
        EFFICIENCY_EXPERT(
            "safari_efficiency_expert",
            "Efficiency Expert", 
            "Complete a safari using 5 or fewer safari balls with at least 3 captures",
            35,
            "efficiency_star"
        ),
        
        PERSISTENCE_PAYS(
            "safari_persistence_pays",
            "Persistence Pays",
            "Capture a monster after 5 failed attempts on the same species",
            20,
            "persistent_hunter"
        ),
        
        ROYAL_SAFARI(
            "safari_royal_safari",
            "Royal Safari",
            "Capture a monster with a Royal Flush hand",
            60,
            "royal_crown"
        ),
        
        HIGH_STAKES(
            "safari_high_stakes",
            "High Stakes Hunter",
            "Capture an Epic or Legendary monster with less than 3 safari balls remaining",
            45,
            "risk_taker"
        ),
        
        BEHAVIOR_SPECIALIST(
            "safari_behavior_specialist",
            "Behavior Specialist",
            "Successfully capture monsters with all behavior types",
            55,
            "animal_whisperer"
        );
        
        fun toAchievement(): Achievement {
            return Achievement(
                id = this.id,
                name = this.displayName,
                description = this.description,
                isUnlocked = false,
                progress = 0,
                maxProgress = 1,
                category = AchievementCategory.GAMEPLAY
            )
        }
    }
    
    /**
     * Tracks safari session data for achievement checking
     */
    data class SafariSession(
        val capturesAttempted: Int = 0,
        val capturesSuccessful: Int = 0,
        val monstersEscaped: Int = 0,
        val ballsUsed: Int = 0,
        val raritiesCaptured: MutableSet<Monster.Rarity> = mutableSetOf(),
        val terrainsVisited: MutableSet<TerrainType> = mutableSetOf(),
        val behaviorsCaptured: MutableSet<MonsterBehavior> = mutableSetOf(),
        val weatherEncounters: MutableSet<WeatherCondition> = mutableSetOf(),
        val timeOfDayCaptures: MutableSet<TimeOfDay> = mutableSetOf(),
        val handTypesUsed: MutableSet<String> = mutableSetOf(),
        val failedAttempts: MutableMap<String, Int> = mutableMapOf(),
        val stormCaptures: Int = 0,
        val nightCaptures: Int = 0,
        val lowBallCaptures: Int = 0, // Captures with 3 or fewer balls remaining
        val consecutiveFailures: Int = 0,
        val maxConsecutiveFailures: Int = 0
    )
    
    /**
     * Checks and awards achievements based on safari session data
     */
    fun checkAchievements(
        session: SafariSession,
        allTimeStats: Map<String, Any> = emptyMap()
    ): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        // First Capture
        if (session.capturesSuccessful >= 1) {
            achievements.add(SafariAchievement.FIRST_CAPTURE.toAchievement())
        }
        
        // Perfect Capture (100% success rate with minimum encounters)
        if (session.capturesAttempted >= 5 && 
            session.capturesSuccessful == session.capturesAttempted) {
            achievements.add(SafariAchievement.PERFECT_CAPTURE.toAchievement())
        }
        
        // Legendary Hunter
        if (Monster.Rarity.LEGENDARY in session.raritiesCaptured) {
            achievements.add(SafariAchievement.LEGENDARY_HUNTER.toAchievement())
        }
        
        // Collection Master (all 5 rarities in one safari)
        if (session.raritiesCaptured.size >= 5) {
            achievements.add(SafariAchievement.COLLECTION_MASTER.toAchievement())
        }
        
        // Storm Chaser
        if (session.stormCaptures > 0) {
            achievements.add(SafariAchievement.STORM_CHASER.toAchievement())
        }
        
        // Night Stalker (10 nighttime captures)
        if (session.nightCaptures >= 10) {
            achievements.add(SafariAchievement.NIGHT_STALKER.toAchievement())
        }
        
        // Environment Explorer (all terrain types)
        if (session.terrainsVisited.size >= 5) {
            achievements.add(SafariAchievement.ENVIRONMENT_EXPLORER.toAchievement())
        }
        
        // Efficiency Expert (5 or fewer balls, 3+ captures)
        if (session.ballsUsed <= 5 && session.capturesSuccessful >= 3) {
            achievements.add(SafariAchievement.EFFICIENCY_EXPERT.toAchievement())
        }
        
        // Persistence Pays (5 failed attempts on same species)
        if (session.failedAttempts.any { it.value >= 5 }) {
            achievements.add(SafariAchievement.PERSISTENCE_PAYS.toAchievement())
        }
        
        // Royal Safari (Royal Flush capture)
        if ("Royal Flush" in session.handTypesUsed) {
            achievements.add(SafariAchievement.ROYAL_SAFARI.toAchievement())
        }
        
        // High Stakes (Epic/Legendary with <3 balls remaining)
        if (session.lowBallCaptures > 0 && 
            (Monster.Rarity.EPIC in session.raritiesCaptured || 
             Monster.Rarity.LEGENDARY in session.raritiesCaptured)) {
            achievements.add(SafariAchievement.HIGH_STAKES.toAchievement())
        }
        
        // Behavior Specialist (all behavior types)
        if (session.behaviorsCaptured.size >= MonsterBehavior.values().size) {
            achievements.add(SafariAchievement.BEHAVIOR_SPECIALIST.toAchievement())
        }
        
        return achievements
    }
    
    /**
     * Updates session data when a capture attempt is made
     */
    fun updateSessionOnCapture(
        session: SafariSession,
        monster: WildMonster,
        result: CaptureResult,
        weather: WeatherCondition,
        terrain: TerrainType,
        timeOfDay: TimeOfDay,
        handType: String,
        ballsRemaining: Int
    ): SafariSession {
        val updatedSession = session.copy(
            capturesAttempted = session.capturesAttempted + 1,
            ballsUsed = session.ballsUsed + 1,
            terrainsVisited = session.terrainsVisited.apply { add(terrain) },
            weatherEncounters = session.weatherEncounters.apply { add(weather) },
            handTypesUsed = session.handTypesUsed.apply { add(handType) }
        )
        
        return when (result.outcome) {
            CaptureOutcome.SUCCESS -> {
                updatedSession.copy(
                    capturesSuccessful = updatedSession.capturesSuccessful + 1,
                    raritiesCaptured = updatedSession.raritiesCaptured.apply { add(monster.rarity) },
                    behaviorsCaptured = updatedSession.behaviorsCaptured.apply { add(monster.behavior) },
                    timeOfDayCaptures = updatedSession.timeOfDayCaptures.apply { add(timeOfDay) },
                    stormCaptures = if (weather == WeatherCondition.STORM) updatedSession.stormCaptures + 1 else updatedSession.stormCaptures,
                    nightCaptures = if (timeOfDay == TimeOfDay.NIGHT) updatedSession.nightCaptures + 1 else updatedSession.nightCaptures,
                    lowBallCaptures = if (ballsRemaining <= 3) updatedSession.lowBallCaptures + 1 else updatedSession.lowBallCaptures,
                    consecutiveFailures = 0
                )
            }
            
            CaptureOutcome.ESCAPED -> {
                updatedSession.copy(
                    monstersEscaped = updatedSession.monstersEscaped + 1,
                    failedAttempts = updatedSession.failedAttempts.apply { 
                        put(monster.name, (get(monster.name) ?: 0) + 1)
                    },
                    consecutiveFailures = updatedSession.consecutiveFailures + 1,
                    maxConsecutiveFailures = maxOf(updatedSession.maxConsecutiveFailures, updatedSession.consecutiveFailures + 1)
                )
            }
            
            CaptureOutcome.FAILED -> {
                updatedSession.copy(
                    failedAttempts = updatedSession.failedAttempts.apply { 
                        put(monster.name, (get(monster.name) ?: 0) + 1)
                    },
                    consecutiveFailures = updatedSession.consecutiveFailures + 1,
                    maxConsecutiveFailures = maxOf(updatedSession.maxConsecutiveFailures, updatedSession.consecutiveFailures + 1)
                )
            }
            
            CaptureOutcome.OUT_OF_BALLS -> updatedSession
        }
    }
}