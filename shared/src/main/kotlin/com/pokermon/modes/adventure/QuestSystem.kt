package com.pokermon.modes.adventure

import com.pokermon.database.Monster
import kotlin.random.Random

/**
 * Quest system for Adventure Mode.
 * Provides objectives and progression through the adventure.
 *
 * @author Pokermon Adventure System
 * @version 1.0.0
 */
class QuestSystem {
    private val activeQuests = mutableListOf<Quest>()
    private val completedQuests = mutableSetOf<String>()
    private val questProgress = mutableMapOf<String, Int>()
    private val random = Random.Default
    
    /**
     * Initialize the quest system with starter quests
     */
    fun initialize() {
        // Add initial quests
        val starterQuests = listOf(
            Quest(
                id = "first_victory",
                name = "First Victory",
                description = "Defeat your first monster",
                type = QuestType.DEFEAT_MONSTERS,
                target = 1,
                rewards = listOf(QuestReward("chips", 100), QuestReward("experience", 50))
            ),
            Quest(
                id = "monster_hunter",
                name = "Monster Hunter",
                description = "Defeat 5 monsters",
                type = QuestType.DEFEAT_MONSTERS,
                target = 5,
                rewards = listOf(QuestReward("chips", 500), QuestReward("monster", "Common Battle Companion"))
            ),
            Quest(
                id = "hand_master",
                name = "Hand Master",
                description = "Win battles with 3 different hand types",
                type = QuestType.HAND_VARIETY,
                target = 3,
                rewards = listOf(QuestReward("chips", 300), QuestReward("experience", 100))
            )
        )
        
        activeQuests.addAll(starterQuests)
    }
    
    /**
     * Updates quest progress based on battle results
     */
    fun updateProgress(battleResult: BattleOutcome) {
        activeQuests.forEach { quest ->
            when (quest.type) {
                QuestType.DEFEAT_MONSTERS -> {
                    if (battleResult.victory) {
                        incrementProgress(quest.id)
                    }
                }
                QuestType.DEFEAT_RARE -> {
                    if (battleResult.victory && battleResult.handStrength > 700) { // Simplified rare check
                        incrementProgress(quest.id)
                    }
                }
                QuestType.DEFEAT_BOSS -> {
                    if (battleResult.victory && battleResult.handStrength > 800) { // Simplified boss check
                        incrementProgress(quest.id)
                    }
                }
                QuestType.HAND_VARIETY -> {
                    if (battleResult.victory) {
                        // Track unique hand types used
                        val handKey = "hand_types_${quest.id}"
                        val usedHandTypes = questProgress.getOrPut(handKey) { 0 }
                        // This would need actual hand type tracking in battle result
                        if (battleResult.handStrength > 500) { // Simplified check
                            questProgress[handKey] = usedHandTypes + 1
                            incrementProgress(quest.id, questProgress[handKey]!!)
                        }
                    }
                }
                QuestType.PERFECT_HAND -> {
                    if (battleResult.victory && battleResult.handStrength > 900) { // Simplified perfect hand check
                        incrementProgress(quest.id)
                    }
                }
                QuestType.COLLECT_MONSTERS -> {
                    if (battleResult.monsterCaptured != null) {
                        incrementProgress(quest.id)
                    }
                }
                QuestType.SURVIVE_BATTLES -> {
                    // Track survival without regard to victory
                    incrementProgress(quest.id)
                }
                QuestType.WIN_STREAK -> {
                    if (battleResult.victory) {
                        incrementProgress(quest.id)
                    } else {
                        // Reset streak progress
                        questProgress[quest.id] = 0
                    }
                }
                QuestType.CHIP_ACCUMULATION -> {
                    if (battleResult.victory) {
                        incrementProgress(quest.id, battleResult.chipsGained)
                    }
                }
            }
        }
        
        // Check for completions
        checkQuestCompletions()
    }
    
    /**
     * Increments progress for a quest
     */
    private fun incrementProgress(questId: String, amount: Int = 1) {
        val currentProgress = questProgress.getOrPut(questId) { 0 }
        questProgress[questId] = currentProgress + amount
    }
    
    /**
     * Checks and handles quest completions
     */
    private fun checkQuestCompletions(): List<Quest> {
        val completed = mutableListOf<Quest>()
        
        activeQuests.removeAll { quest ->
            val progress = questProgress[quest.id] ?: 0
            if (progress >= quest.target) {
                completedQuests.add(quest.id)
                completed.add(quest)
                println("\n🎯 QUEST COMPLETED: ${quest.name}")
                println("   ${quest.description}")
                quest.rewards.forEach { reward ->
                    println("   Reward: ${reward.type} - ${reward.value}")
                }
                
                // Add follow-up quests
                addFollowUpQuests(quest)
                true
            } else {
                false
            }
        }
        
        return completed
    }
    
    /**
     * Adds follow-up quests based on completed quests
     */
    private fun addFollowUpQuests(completedQuest: Quest) {
        when (completedQuest.id) {
            "first_victory" -> {
                addQuestIfNotExists(
                    Quest(
                        id = "rare_hunter",
                        name = "Rare Hunter",
                        description = "Defeat a Rare or higher rarity monster",
                        type = QuestType.DEFEAT_RARE,
                        target = 1,
                        rewards = listOf(QuestReward("monster", "Rare Battle Companion"))
                    )
                )
            }
            "monster_hunter" -> {
                addQuestIfNotExists(
                    Quest(
                        id = "veteran_hunter",
                        name = "Veteran Hunter",
                        description = "Defeat 15 monsters",
                        type = QuestType.DEFEAT_MONSTERS,
                        target = 15,
                        rewards = listOf(QuestReward("chips", 1500), QuestReward("title", "Monster Veteran"))
                    )
                )
            }
        }
    }
    
    /**
     * Adds a quest if it doesn't already exist
     */
    private fun addQuestIfNotExists(quest: Quest) {
        if (!completedQuests.contains(quest.id) && activeQuests.none { it.id == quest.id }) {
            activeQuests.add(quest)
            println("📋 New Quest Available: ${quest.name}")
        }
    }
    
    /**
     * Generates dynamic quests based on player progress
     */
    fun generateDynamicQuests(level: Int, monstersDefeated: Int) {
        // Level-based quests
        if (level % 5 == 0 && level > 5) {
            val questId = "boss_challenge_$level"
            if (!completedQuests.contains(questId)) {
                addQuestIfNotExists(
                    Quest(
                        id = questId,
                        name = "Boss Challenge $level",
                        description = "Defeat the level $level boss monster",
                        type = QuestType.DEFEAT_BOSS,
                        target = 1,
                        rewards = listOf(
                            QuestReward("chips", level * 200),
                            QuestReward("monster", "Epic Boss Companion")
                        )
                    )
                )
            }
        }
        
        // Random special quests
        if (random.nextInt(100) < 10) { // 10% chance per check
            generateSpecialQuest()
        }
    }
    
    /**
     * Generates special limited-time quests
     */
    private fun generateSpecialQuest() {
        val specialQuests = listOf(
            Quest(
                id = "perfect_victory_${System.currentTimeMillis()}",
                name = "Perfect Victory",
                description = "Win a battle with a Royal Flush",
                type = QuestType.PERFECT_HAND,
                target = 1,
                rewards = listOf(QuestReward("chips", 2000), QuestReward("title", "Perfect Duelist"))
            ),
            Quest(
                id = "lucky_streak_${System.currentTimeMillis()}",
                name = "Lucky Streak",
                description = "Win 3 battles in a row",
                type = QuestType.WIN_STREAK,
                target = 3,
                rewards = listOf(QuestReward("monster", "Lucky Charm Companion"))
            )
        )
        
        val quest = specialQuests.random()
        if (activeQuests.none { it.type == quest.type }) {
            activeQuests.add(quest)
            println("✨ Special Quest Appeared: ${quest.name}")
        }
    }
    
    /**
     * Gets current active quests
     */
    fun getActiveQuests(): List<Quest> = activeQuests.toList()
    
    /**
     * Gets quest progress summary
     */
    fun getQuestSummary(): String {
        val sb = StringBuilder()
        sb.appendLine("📋 ACTIVE QUESTS:")
        sb.appendLine("-".repeat(30))
        
        activeQuests.forEach { quest ->
            val progress = questProgress[quest.id] ?: 0
            val percentage = ((progress.toDouble() / quest.target) * 100).toInt()
            sb.appendLine("${quest.name}: $progress/${quest.target} ($percentage%)")
            sb.appendLine("  ${quest.description}")
        }
        
        sb.appendLine("\n🏆 Completed Quests: ${completedQuests.size}")
        
        return sb.toString()
    }
}

/**
 * Represents a quest with objectives and rewards
 */
data class Quest(
    val id: String,
    val name: String,
    val description: String,
    val type: QuestType,
    val target: Int,
    val rewards: List<QuestReward>
)

/**
 * Types of quest objectives
 */
enum class QuestType {
    DEFEAT_MONSTERS,     // Defeat X monsters
    DEFEAT_RARE,         // Defeat rare monsters
    DEFEAT_BOSS,         // Defeat boss monsters
    HAND_VARIETY,        // Use different hand types
    PERFECT_HAND,        // Get specific hand types
    COLLECT_MONSTERS,    // Capture monsters
    SURVIVE_BATTLES,     // Survive X battles
    WIN_STREAK,          // Win consecutive battles
    CHIP_ACCUMULATION    // Accumulate chips
}

/**
 * Quest rewards
 */
data class QuestReward(
    val type: String,    // "chips", "experience", "monster", "title"
    val value: Any       // Amount or name
)

/**
 * Battle outcome for quest tracking
 */
data class BattleOutcome(
    val victory: Boolean,
    val handStrength: Int,
    val handType: String,
    val chipsGained: Int,
    val damageDealt: Int,
    val monsterCaptured: Monster? = null
)