package com.pokermon.modes.ironman

import com.pokermon.database.Monster
import com.pokermon.modes.Achievement
import com.pokermon.modes.AchievementCategory

/**
 * Ironman Mode specific achievements.
 * Track player accomplishments in high-stakes survival gameplay.
 *
 * @author Pokermon Ironman Achievement System
 * @version 1.0.0
 */
object IronmanAchievements {
    /**
     * Ironman-specific achievement definitions
     */
    enum class IronmanAchievement(
        val id: String,
        val displayName: String,
        val description: String,
        val points: Int,
        val iconName: String = "ironman_trophy",
    ) {
        FIRST_SURVIVAL(
            "ironman_first_survival",
            "Ironman Initiate",
            "Survive your first Ironman round",
            10,
            "ironman_shield",
        ),

        SURVIVOR_STREAK(
            "ironman_survivor_streak",
            "Survivor Streak",
            "Achieve a 10-round survival streak",
            30,
            "streak_flame",
        ),

        DEATH_DEFIER(
            "ironman_death_defier",
            "Death Defier",
            "Survive permadeath using a revival method",
            50,
            "phoenix_feather",
        ),

        GACHA_ADDICT(
            "ironman_gacha_addict",
            "Gacha Addict",
            "Perform 100 gacha pulls in a single run",
            40,
            "gacha_machine",
        ),

        LEGENDARY_COLLECTOR(
            "ironman_legendary_collector",
            "Legendary Collector",
            "Collect 5 Legendary monsters through gacha",
            75,
            "legendary_collection",
        ),

        PITY_BREAKER(
            "ironman_pity_breaker",
            "Pity Breaker",
            "Get a Legendary monster before hitting pity timer",
            35,
            "lucky_star",
        ),

        HIGH_ROLLER(
            "ironman_high_roller",
            "High Roller",
            "Win a round with 90% of your chips at stake",
            45,
            "dice_crown",
        ),

        RISK_MASTER(
            "ironman_risk_master",
            "Risk Master",
            "Reach 3x risk multiplier and survive 5 more rounds",
            60,
            "risk_crown",
        ),

        NIGHTMARE_SURVIVOR(
            "ironman_nightmare_survivor",
            "Nightmare Survivor",
            "Survive 25 rounds on Nightmare difficulty",
            100,
            "nightmare_skull",
        ),

        PHOENIX_KING(
            "ironman_phoenix_king",
            "Phoenix King",
            "Use phoenix revival 3 times in a single run and still survive",
            80,
            "phoenix_crown",
        ),

        JACKPOT_WINNER(
            "ironman_jackpot_winner",
            "Jackpot Winner",
            "Win over 10,000 chips in a single round",
            55,
            "jackpot_coins",
        ),

        COLLECTION_MASTER(
            "ironman_collection_master",
            "Collection Master",
            "Own at least 1 monster of each rarity through gacha",
            65,
            "master_collection",
        ),

        ULTIMATE_SURVIVOR(
            "ironman_ultimate_survivor",
            "Ultimate Survivor",
            "Reach level 20 in Ironman mode",
            90,
            "ultimate_crown",
        ),

        PERFECT_RUN(
            "ironman_perfect_run",
            "Perfect Run",
            "Complete an Ironman run with 0 deaths",
            85,
            "perfect_gem",
        ),

        GACHA_LEGEND(
            "ironman_gacha_legend",
            "Gacha Legend",
            "Pull 3 Legendary monsters in consecutive pulls",
            95,
            "triple_legendary",
        ),
        ;

        fun toAchievement(): Achievement {
            return Achievement(
                id = this.id,
                name = this.displayName,
                description = this.description,
                isUnlocked = false,
                progress = 0,
                maxProgress = 1,
                category = AchievementCategory.GAMEPLAY,
            )
        }
    }

    /**
     * Tracks ironman session data for achievement checking
     */
    data class IronmanSession(
        val currentLevel: Int = 1,
        val roundsSurvived: Int = 0,
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val deathCount: Int = 0,
        val survivedDeaths: Int = 0,
        val totalWinnings: Int = 0,
        val maxSingleWin: Int = 0,
        val gachaPulls: Int = 0,
        val legendaryPulls: Int = 0,
        val pitySaves: Int = 0,
        val phoenixUses: Int = 0,
        val riskLevel: Double = 1.0,
        val maxRiskSurvived: Double = 1.0,
        val difficultyLevel: Int = 1,
        val raritiesOwned: MutableSet<Monster.Rarity> = mutableSetOf(),
        val consecutiveLegendaryPulls: Int = 0,
        val maxConsecutiveLegendaryPulls: Int = 0,
        val highStakeWins: Int = 0,
        val perfectRun: Boolean = true,
    )

    /**
     * Checks and awards achievements based on ironman session data
     */
    fun checkAchievements(
        session: IronmanSession,
        allTimeStats: Map<String, Any> = emptyMap(),
    ): List<Achievement> {
        val achievements = mutableListOf<Achievement>()

        // First Survival
        if (session.roundsSurvived >= 1) {
            achievements.add(IronmanAchievement.FIRST_SURVIVAL.toAchievement())
        }

        // Survivor Streak
        if (session.bestStreak >= 10) {
            achievements.add(IronmanAchievement.SURVIVOR_STREAK.toAchievement())
        }

        // Death Defier
        if (session.survivedDeaths >= 1) {
            achievements.add(IronmanAchievement.DEATH_DEFIER.toAchievement())
        }

        // Gacha Addict
        if (session.gachaPulls >= 100) {
            achievements.add(IronmanAchievement.GACHA_ADDICT.toAchievement())
        }

        // Legendary Collector
        if (session.legendaryPulls >= 5) {
            achievements.add(IronmanAchievement.LEGENDARY_COLLECTOR.toAchievement())
        }

        // Pity Breaker
        if (session.pitySaves >= 1) {
            achievements.add(IronmanAchievement.PITY_BREAKER.toAchievement())
        }

        // High Roller
        if (session.highStakeWins >= 1) {
            achievements.add(IronmanAchievement.HIGH_ROLLER.toAchievement())
        }

        // Risk Master
        if (session.maxRiskSurvived >= 3.0 && session.roundsSurvived >= 5) {
            achievements.add(IronmanAchievement.RISK_MASTER.toAchievement())
        }

        // Nightmare Survivor
        if (session.difficultyLevel >= 4 && session.roundsSurvived >= 25) {
            achievements.add(IronmanAchievement.NIGHTMARE_SURVIVOR.toAchievement())
        }

        // Phoenix King
        if (session.phoenixUses >= 3 && session.roundsSurvived >= 10) {
            achievements.add(IronmanAchievement.PHOENIX_KING.toAchievement())
        }

        // Jackpot Winner
        if (session.maxSingleWin >= 10000) {
            achievements.add(IronmanAchievement.JACKPOT_WINNER.toAchievement())
        }

        // Collection Master
        if (session.raritiesOwned.size >= Monster.Rarity.values().size) {
            achievements.add(IronmanAchievement.COLLECTION_MASTER.toAchievement())
        }

        // Ultimate Survivor
        if (session.currentLevel >= 20) {
            achievements.add(IronmanAchievement.ULTIMATE_SURVIVOR.toAchievement())
        }

        // Perfect Run
        if (session.perfectRun && session.roundsSurvived >= 15) {
            achievements.add(IronmanAchievement.PERFECT_RUN.toAchievement())
        }

        // Gacha Legend
        if (session.maxConsecutiveLegendaryPulls >= 3) {
            achievements.add(IronmanAchievement.GACHA_LEGEND.toAchievement())
        }

        return achievements
    }

    /**
     * Updates session data when a round completes
     */
    fun updateSessionOnRound(
        session: IronmanSession,
        result: IronmanRoundResult,
        riskLevel: Double,
    ): IronmanSession {
        return when (result.outcome) {
            RoundOutcome.VICTORY ->
                session.copy(
                    roundsSurvived = session.roundsSurvived + 1,
                    currentStreak = session.currentStreak + 1,
                    bestStreak = maxOf(session.bestStreak, session.currentStreak + 1),
                    totalWinnings = session.totalWinnings + result.chipsGained,
                    maxSingleWin = maxOf(session.maxSingleWin, result.chipsGained),
                    riskLevel = riskLevel,
                    maxRiskSurvived = maxOf(session.maxRiskSurvived, riskLevel),
                    highStakeWins = if (isHighStakeWin(result)) session.highStakeWins + 1 else session.highStakeWins,
                )

            RoundOutcome.DEFEAT ->
                session.copy(
                    currentStreak = 0,
                    deathCount = session.deathCount + 1,
                    perfectRun = false,
                    riskLevel = riskLevel,
                )

            RoundOutcome.DRAW ->
                session.copy(
                    riskLevel = riskLevel,
                )
        }
    }

    /**
     * Updates session data when a gacha pull is performed
     */
    fun updateSessionOnGacha(
        session: IronmanSession,
        monster: Monster,
        isPityBreaker: Boolean,
    ): IronmanSession {
        val newRaritiesOwned = session.raritiesOwned.toMutableSet().apply { add(monster.rarity) }

        val legendaryPulls =
            if (monster.rarity == Monster.Rarity.LEGENDARY) {
                session.legendaryPulls + 1
            } else {
                session.legendaryPulls
            }

        val consecutiveLegendaryPulls =
            if (monster.rarity == Monster.Rarity.LEGENDARY) {
                session.consecutiveLegendaryPulls + 1
            } else {
                0
            }

        val pitySaves =
            if (isPityBreaker && monster.rarity == Monster.Rarity.LEGENDARY) {
                session.pitySaves + 1
            } else {
                session.pitySaves
            }

        return session.copy(
            gachaPulls = session.gachaPulls + 1,
            legendaryPulls = legendaryPulls,
            raritiesOwned = newRaritiesOwned,
            consecutiveLegendaryPulls = consecutiveLegendaryPulls,
            maxConsecutiveLegendaryPulls =
                maxOf(
                    session.maxConsecutiveLegendaryPulls,
                    consecutiveLegendaryPulls,
                ),
            pitySaves = pitySaves,
        )
    }

    /**
     * Updates session data when a death is survived
     */
    fun updateSessionOnRevival(
        session: IronmanSession,
        method: String,
    ): IronmanSession {
        val phoenixUses = if (method == "phoenix") session.phoenixUses + 1 else session.phoenixUses

        return session.copy(
            survivedDeaths = session.survivedDeaths + 1,
            phoenixUses = phoenixUses,
            deathCount = session.deathCount + 1,
        )
    }

    /**
     * Determines if a win qualifies as high stakes (90%+ of chips)
     */
    private fun isHighStakeWin(result: IronmanRoundResult): Boolean {
        // This would need to be determined based on the actual stake vs total chips
        // For now, use a heuristic based on chips gained
        return result.chipsGained >= 1000 // High threshold for high stakes
    }

    /**
     * Updates session data when leveling up
     */
    fun updateSessionOnLevelUp(
        session: IronmanSession,
        newLevel: Int,
    ): IronmanSession {
        return session.copy(
            currentLevel = newLevel,
        )
    }

    /**
     * Gets achievement progress for UI display
     */
    fun getAchievementProgress(
        session: IronmanSession,
        achievement: IronmanAchievement,
    ): Pair<Int, Int> {
        return when (achievement) {
            IronmanAchievement.FIRST_SURVIVAL -> Pair(session.roundsSurvived, 1)
            IronmanAchievement.SURVIVOR_STREAK -> Pair(session.bestStreak, 10)
            IronmanAchievement.DEATH_DEFIER -> Pair(session.survivedDeaths, 1)
            IronmanAchievement.GACHA_ADDICT -> Pair(session.gachaPulls, 100)
            IronmanAchievement.LEGENDARY_COLLECTOR -> Pair(session.legendaryPulls, 5)
            IronmanAchievement.PITY_BREAKER -> Pair(session.pitySaves, 1)
            IronmanAchievement.HIGH_ROLLER -> Pair(session.highStakeWins, 1)
            IronmanAchievement.RISK_MASTER -> Pair(if (session.maxRiskSurvived >= 3.0) session.roundsSurvived else 0, 5)
            IronmanAchievement.NIGHTMARE_SURVIVOR -> Pair(if (session.difficultyLevel >= 4) session.roundsSurvived else 0, 25)
            IronmanAchievement.PHOENIX_KING -> Pair(if (session.phoenixUses >= 3) session.roundsSurvived else 0, 10)
            IronmanAchievement.JACKPOT_WINNER -> Pair(session.maxSingleWin / 1000, 10)
            IronmanAchievement.COLLECTION_MASTER -> Pair(session.raritiesOwned.size, Monster.Rarity.values().size)
            IronmanAchievement.ULTIMATE_SURVIVOR -> Pair(session.currentLevel, 20)
            IronmanAchievement.PERFECT_RUN -> Pair(if (session.perfectRun) session.roundsSurvived else 0, 15)
            IronmanAchievement.GACHA_LEGEND -> Pair(session.maxConsecutiveLegendaryPulls, 3)
        }
    }
}
