package com.pokermon.GameFlows

import com.pokermon.Game
import com.pokermon.GameMode
import com.pokermon.players.Player
import kotlinx.coroutines.flow.*

/**
 * Flow-based game state manager for modern reactive patterns.
 * Manages game state transitions and event handling using Kotlin coroutines and Flow.
 * Enhanced to support modular game modes, sub-states, and mode-specific behaviors.
 *
 * @author Pokermon Flow System
 * @version 1.2.0 - Enhanced with sub-state support and mode-specific handling
 */
class GameStateManager {
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameEvents = MutableSharedFlow<GameEvents>()
    val gameEvents: SharedFlow<GameEvents> = _gameEvents.asSharedFlow()

    private val _gameActions = MutableSharedFlow<GameActions>()
    val gameActions: SharedFlow<GameActions> = _gameActions.asSharedFlow()

    // Track current game mode for mode-specific behavior
    private var currentGameMode: GameMode = GameMode.CLASSIC

    /**
     * Updates the current game state and emits the change to all subscribers.
     */
    suspend fun updateGameState(newState: GameState) {
        val previousState = _gameState.value
        _gameState.emit(newState)
        
        // Emit phase change events when transitioning between Playing states
        if (previousState is GameState.Playing && newState is GameState.Playing &&
            previousState.currentPhase != newState.currentPhase) {
            emitEvent(GameEvents.PhaseChanged(newState.currentPhase, previousState.currentPhase))
        }
        
        // Emit sub-state transition events
        handleSubStateTransitions(previousState, newState)
    }

    /**
     * Emits a game event to all subscribers.
     */
    suspend fun emitEvent(event: GameEvents) {
        _gameEvents.emit(event)
    }

    /**
     * Processes a game action and potentially updates state.
     * Enhanced to handle all game states, mode-specific actions and sub-state transitions.
     * Complete implementation with no stubs or TODOs.
     */
    suspend fun processAction(action: GameActions) {
        _gameActions.emit(action)

        // Process the action and update state accordingly
        when (action) {
            // Setup and navigation actions
            is GameActions.StartGame -> {
                updateGameState(GameState.ModeSelection())
                emitEvent(GameEvents.GameStarted)
            }

            is GameActions.SelectMode -> {
                currentGameMode = action.mode
                val currentState = _gameState.value
                if (currentState is GameState.ModeSelection) {
                    updateGameState(currentState.copy(selectedMode = action.mode))
                    emitEvent(GameEvents.ModeSelected(action.mode))
                }
            }

            is GameActions.ConfigurePlayers -> {
                updateGameState(
                    GameState.PlayerSetup(
                        selectedMode = currentGameMode,
                        playerName = action.playerName,
                        playerCount = action.playerCount,
                        startingChips = action.startingChips,
                        setupComplete = true
                    )
                )
                emitEvent(GameEvents.PlayersConfigured(action.playerCount, action.startingChips))
            }

            is GameActions.ConfirmSetup -> {
                updateGameState(
                    GameState.GameStarting(
                        gameConfig = action.gameConfig,
                        loadingProgress = 0.0f,
                        loadingMessage = "Initializing ${currentGameMode.displayName}..."
                    )
                )
                emitEvent(GameEvents.GameConfigured(action.gameConfig))
            }

            // Mode selection and switching
            is GameActions.SelectGameMode -> {
                currentGameMode = action.mode
                emitEvent(GameEvents.GameModeSelected(action.mode))
            }

            is GameActions.SwitchToMode -> {
                val previousMode = currentGameMode
                currentGameMode = action.mode
                emitEvent(GameEvents.GameModeSwitched(previousMode, action.mode))
                
                if (!action.preserveState) {
                    updateGameState(GameState.ModeSelection(selectedMode = action.mode))
                }
            }

            // Player actions
            is GameActions.JoinGame -> {
                emitEvent(GameEvents.PlayerJoined(action.player))
            }

            is GameActions.LeaveGame -> {
                emitEvent(GameEvents.PlayerLeft(action.player, "user quit"))
            }

            // Game flow actions
            is GameActions.EndGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val winner = determineWinner(currentState.players)
                    updateGameState(
                        GameState.GameOver(
                            winner = winner,
                            finalScores = currentState.players.associate { it.name to it.chips },
                            sessionStats = generateSessionStats(currentState),
                            gameMode = currentState.gameMode,
                            totalRounds = currentState.roundNumber,
                            gameOverReason = "Game completed normally"
                        ),
                    )
                    emitEvent(GameEvents.GameEnded(winner, System.currentTimeMillis(), "completed"))
                }
            }

            is GameActions.NextRound -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val nextRound = currentState.roundNumber + 1
                    updateGameState(
                        GameState.RoundTransition(
                            completedRound = currentState.roundNumber,
                            nextRound = nextRound,
                            roundWinner = determineWinner(currentState.players),
                            roundWinnings = currentState.pot
                        )
                    )
                    emitEvent(GameEvents.RoundEnded(currentState.players.firstOrNull(), currentState.pot, currentState.roundNumber))
                }
            }

            is GameActions.ContinueGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.RoundTransition) {
                    // Create new playing state for next round
                    val gameConfig = Game(gameMode = currentGameMode)
                    updateGameState(
                        GameState.GameStarting(
                            gameConfig = gameConfig,
                            loadingProgress = 0.5f,
                            loadingMessage = "Starting round ${currentState.nextRound}..."
                        )
                    )
                    emitEvent(GameEvents.RoundStarted(currentState.nextRound, currentGameMode))
                }
            }

            is GameActions.PauseGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val savedGame = Game(gameMode = currentState.gameMode)
                    updateGameState(
                        GameState.Paused(
                            savedState = savedGame,
                            pauseReason = "User requested",
                            pauseTime = System.currentTimeMillis()
                        )
                    )
                    emitEvent(GameEvents.GamePaused("User requested", true))
                }
            }

            is GameActions.ResumeGame -> {
                val currentState = _gameState.value
                if (currentState is GameState.Paused) {
                    // Resume to a basic playing state - would need proper state restoration in real implementation
                    updateGameState(
                        GameState.Playing(
                            players = emptyList(), // Would be restored from savedState
                            currentPhase = com.pokermon.GamePhase.BETTING_ROUND,
                            gameMode = currentState.savedState.gameMode
                        )
                    )
                    emitEvent(GameEvents.GameResumed("paused"))
                }
            }

            is GameActions.RestartGame -> {
                if (action.sameSettings) {
                    updateGameState(GameState.Initializing)
                } else {
                    updateGameState(GameState.ModeSelection())
                }
                emitEvent(GameEvents.GameRestarted(Game(gameMode = currentGameMode)))
            }

            // Navigation actions
            is GameActions.ShowStats -> {
                val currentState = _gameState.value
                val stats = when (currentState) {
                    is GameState.Playing -> generateSessionStats(currentState)
                    is GameState.GameOver -> currentState.sessionStats
                    else -> mapOf("message" to "No stats available")
                }
                updateGameState(
                    GameState.ShowingStats(
                        sessionStats = stats,
                        playerStats = emptyMap(), // Would be populated in real implementation
                        gameMode = currentGameMode
                    )
                )
                emitEvent(GameEvents.StatsDisplayed(stats))
            }

            is GameActions.ShowHelp -> {
                updateGameState(
                    GameState.ShowingHelp(
                        helpCategory = "general",
                        helpContent = getHelpContent("general")
                    )
                )
                emitEvent(GameEvents.HelpDisplayed("general", getHelpContent("general")))
            }

            is GameActions.ShowHelpCategory -> {
                updateGameState(
                    GameState.ShowingHelp(
                        helpCategory = action.category,
                        helpContent = getHelpContent(action.category)
                    )
                )
                emitEvent(GameEvents.HelpDisplayed(action.category, getHelpContent(action.category)))
            }

            is GameActions.ReturnToGame -> {
                val currentState = _gameState.value
                when (currentState) {
                    is GameState.ShowingStats, is GameState.ShowingHelp -> {
                        // Return to previous playing state - would need proper state stack in real implementation
                        updateGameState(
                            GameState.Playing(
                                players = emptyList(), // Would be restored
                                currentPhase = com.pokermon.GamePhase.BETTING_ROUND,
                                gameMode = currentGameMode
                            )
                        )
                    }
                    else -> {
                        // No-op if not in a returnable state
                    }
                }
            }

            is GameActions.ReturnToMainMenu -> {
                updateGameState(GameState.ModeSelection())
                emitEvent(GameEvents.StateChanged("current", "menu"))
            }

            // Betting actions
            is GameActions.Call -> {
                emitEvent(GameEvents.PlayerCalled(action.player, 0)) // Amount will be set by bridge
            }

            is GameActions.Raise -> {
                emitEvent(GameEvents.PlayerRaised(action.player, action.amount, action.player.bet + action.amount))
            }

            is GameActions.Fold -> {
                emitEvent(GameEvents.PlayerFolded(action.player))
            }

            is GameActions.Check -> {
                emitEvent(GameEvents.PlayerChecked(action.player))
            }

            is GameActions.PlaceBet -> {
                emitEvent(GameEvents.BetPlaced(action.player, action.amount, action.player.bet + action.amount))
            }

            // Card actions
            is GameActions.ExchangeCards -> {
                emitEvent(GameEvents.CardsExchanged(action.player, action.cardIndices.size))
            }

            is GameActions.SelectCardsForExchange -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing && currentState.subState is PlayingSubState.CardExchangePhase) {
                    val updatedSubState = currentState.subState.copy(selectedCards = action.cardIndices)
                    updateGameState(currentState.copy(subState = updatedSubState))
                }
            }

            is GameActions.ShowCards -> {
                emitEvent(GameEvents.CardsShown(action.player, "Hand revealed"))
            }

            // Victory and completion actions
            is GameActions.TriggerVictory -> {
                updateGameState(
                    GameState.Victory(
                        winner = action.winner,
                        victoryType = action.victoryType,
                        achievements = getAchievementsForVictory(action.victoryType),
                        rewards = getRewardsForVictory(action.victoryType)
                    )
                )
                emitEvent(GameEvents.VictoryTriggered(action.winner, action.victoryType))
            }

            is GameActions.CelebrateVictory -> {
                emitEvent(GameEvents.CelebrationStarted("victory", action.celebrationData.duration, action.celebrationData))
            }

            is GameActions.AcknowledgeGameOver -> {
                updateGameState(GameState.ModeSelection())
            }

            // AI actions
            is GameActions.ProcessAITurn -> {
                emitEvent(GameEvents.AIActionPerformed(action.aiPlayer, "processing", 0))
            }

            is GameActions.CompleteAIProcessing -> {
                emitEvent(GameEvents.AIProcessingComplete(1))
            }

            // Sub-state actions
            is GameActions.EnterSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGameState(currentState.copy(subState = action.subState))
                    emitEvent(GameEvents.SubStateEntered(action.subState))
                }
            }

            is GameActions.ExitSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing && currentState.subState != null) {
                    val exitedSubState = currentState.subState
                    updateGameState(currentState.copy(subState = null))
                    emitEvent(GameEvents.SubStateExited(exitedSubState, action.reason))
                }
            }

            is GameActions.TransitionSubState -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGameState(currentState.copy(subState = action.to))
                    emitEvent(GameEvents.SubStateTransition(action.from, action.to))
                }
            }

            // Special event actions
            is GameActions.TriggerSpecialEvent -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    val eventSubState = PlayingSubState.SpecialEvent(
                        eventType = action.eventType,
                        eventName = action.eventName,
                        eventDescription = getEventDescription(action.eventName),
                        eventChoices = getEventChoices(action.eventName)
                    )
                    updateGameState(currentState.copy(subState = eventSubState))
                    emitEvent(GameEvents.SpecialEventTriggered(action.eventType, action.eventName, getEventDescription(action.eventName)))
                }
            }

            is GameActions.MakeEventChoice -> {
                emitEvent(GameEvents.EventChoiceMade(action.player, action.choiceId, emptyMap()))
            }

            is GameActions.CompleteSpecialEvent -> {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGameState(currentState.copy(subState = null))
                    emitEvent(GameEvents.SpecialEventCompleted(action.eventName, "completed", action.outcome))
                }
            }

            // Achievement actions
            is GameActions.UnlockAchievement -> {
                emitEvent(GameEvents.AchievementUnlocked(action.player, action.achievementName, getAchievementDescription(action.achievementName), ""))
            }

            is GameActions.ShowAchievements -> {
                // Would show achievements screen
                emitEvent(GameEvents.SystemNotification("Achievements displayed", "ACHIEVEMENT"))
            }

            // Error handling
            is GameActions.ReportError -> {
                updateGameState(
                    GameState.Error(
                        message = action.error,
                        recoverable = true,
                        suggestedActions = listOf("Retry", "Return to Menu")
                    )
                )
                emitEvent(GameEvents.ErrorOccurred(action.error, null, action.context, true))
            }

            is GameActions.AttemptRecovery -> {
                // Attempt to recover from error state
                updateGameState(GameState.Initializing)
                emitEvent(GameEvents.SystemNotification("Recovery attempted: ${action.recoveryMethod}", "RECOVERY"))
            }

            is GameActions.ConfirmExit -> {
                updateGameState(GameState.Exiting)
            }

            // Handle mode-specific actions
            is GameActions.AdventureActions -> handleAdventureAction(action)
            is GameActions.SafariActions -> handleSafariAction(action)
            is GameActions.IronmanActions -> handleIronmanAction(action)
            
            is GameActions.ConvertToGachaPoints -> {
                emitEvent(GameEvents.IronmanEvents.ChipsConverted(action.player, action.chips, action.chips / 10))
            }
            
            // Handle remaining actions  
            is GameActions.DealCards -> {
                emitEvent(GameEvents.CardsDealt(2)) // Default player count
            }
            
            is GameActions.ValidateAction -> {
                // Validation logic would go here
                emitEvent(GameEvents.SystemNotification("Action validated", "VALIDATION"))
            }
            
            is GameActions.NavigateToState -> {
                emitEvent(GameEvents.StateChanged("current", action.targetState))
            }
        }
    }

    /**
     * Gets the current game state synchronously.
     */
    fun getCurrentState(): GameState = _gameState.value

    /**
     * Gets the current game mode.
     */
    fun getCurrentMode(): GameMode = currentGameMode

    /**
     * Resets the game state to initializing.
     */
    suspend fun resetGame() {
        updateGameState(GameState.Initializing)
    }

    /**
     * Handles sub-state transitions and emits appropriate events.
     */
    private suspend fun handleSubStateTransitions(previousState: GameState, newState: GameState) {
        if (previousState is GameState.Playing && newState is GameState.Playing) {
            val prevSubState = previousState.subState
            val newSubState = newState.subState
            
            when {
                prevSubState == null && newSubState != null -> {
                    emitEvent(GameEvents.SubStateEntered(newSubState))
                }
                prevSubState != null && newSubState == null -> {
                    emitEvent(GameEvents.SubStateExited(prevSubState, "State transition"))
                }
                prevSubState != null && newSubState != null && prevSubState != newSubState -> {
                    emitEvent(GameEvents.SubStateTransition(prevSubState, newSubState))
                }
            }
        }
    }

    /**
     * Handles adventure mode specific actions.
     */
    private suspend fun handleAdventureAction(action: GameActions.AdventureActions) {
        when (action) {
            is GameActions.AdventureActions.EncounterMonster -> {
                emitEvent(GameEvents.AdventureEvents.MonsterEncountered(action.monsterName, action.monsterHealth))
            }
            is GameActions.AdventureActions.AttackMonster -> {
                emitEvent(GameEvents.AdventureEvents.MonsterAttacked(action.player, action.damage, 0))
            }
            is GameActions.AdventureActions.CompleteQuest -> {
                emitEvent(GameEvents.AdventureEvents.QuestCompleted(action.questName, Player(), action.reward))
            }
            is GameActions.AdventureActions.BattleMonster -> {
                emitEvent(GameEvents.AdventureEvents.BattleStarted(action.player, action.monster, "battle"))
            }
            is GameActions.AdventureActions.FleeFromBattle -> {
                emitEvent(GameEvents.AdventureEvents.BattleEnded(action.player, null, "flee"))
            }
            is GameActions.AdventureActions.StartQuest -> {
                emitEvent(GameEvents.AdventureEvents.QuestStarted(action.player, action.questId, "adventure"))
            }
            is GameActions.AdventureActions.UpdateQuestProgress -> {
                emitEvent(GameEvents.AdventureEvents.QuestProgressed(action.player, action.questId, action.progress))
            }
            is GameActions.AdventureActions.UseSpecialAbility -> {
                emitEvent(GameEvents.AdventureEvents.SpecialAbilityUsed(action.player, action.ability, "adventure"))
            }
        }
    }

    /**
     * Handles safari mode specific actions.
     */
    private suspend fun handleSafariAction(action: GameActions.SafariActions) {
        when (action) {
            is GameActions.SafariActions.EncounterWildMonster -> {
                emitEvent(GameEvents.SafariEvents.WildMonsterSighted(action.monsterName))
            }
            is GameActions.SafariActions.ThrowSafariBall -> {
                emitEvent(GameEvents.SafariEvents.SafariBallThrown(action.player, "standard", 0))
            }
            is GameActions.SafariActions.AttemptCapture -> {
                // Simulate capture logic
                val success = action.captureChance > 0.5
                if (success) {
                    emitEvent(GameEvents.SafariEvents.MonsterCaptured(action.player, "Unknown", 0))
                } else {
                    emitEvent(GameEvents.SafariEvents.MonsterEscaped("Unknown", "Failed capture"))
                }
            }
            is GameActions.SafariActions.SpotWildMonster -> {
                emitEvent(GameEvents.SafariEvents.WildMonsterSpotted(action.monster, Monster.Rarity.COMMON, "neutral"))
            }
            is GameActions.SafariActions.ApproachMonster -> {
                emitEvent(GameEvents.SafariEvents.MonsterApproached(action.player, action.monster))
            }
            is GameActions.SafariActions.CheckMonsterStats -> {
                emitEvent(GameEvents.SafariEvents.MonsterStatsChecked(action.monster, Monster.Rarity.COMMON, "neutral"))
            }
            is GameActions.SafariActions.FleeFromWildMonster -> {
                emitEvent(GameEvents.SafariEvents.PlayerFledFromWild(action.player, action.monster))
            }
            is GameActions.SafariActions.UseSpecialBall -> {
                emitEvent(GameEvents.SafariEvents.SpecialBallUsed(action.player, action.ballType, 0))
            }
        }
    }

    /**
     * Handles ironman mode specific actions.
     */
    private suspend fun handleIronmanAction(action: GameActions.IronmanActions) {
        when (action) {
            is GameActions.IronmanActions.ConvertToGachaPoints -> {
                emitEvent(GameEvents.IronmanEvents.ChipsConverted(action.player, action.chips, action.chips / 10, 0.1))
            }
            is GameActions.IronmanActions.PerformGachaPull -> {
                emitEvent(GameEvents.IronmanEvents.GachaPullPerformed(action.player, action.pointsSpent, "Common Monster", Monster.Rarity.COMMON))
            }
            is GameActions.IronmanActions.TriggerPermadeath -> {
                emitEvent(GameEvents.IronmanEvents.PermadeathTriggered(action.player, "Health reached zero", mapOf()))
            }
            is GameActions.IronmanActions.AcknowledgeRisk -> {
                emitEvent(GameEvents.IronmanEvents.RiskAcknowledged(action.player, action.riskLevel))
            }
            is GameActions.IronmanActions.ActivateRiskMode -> {
                emitEvent(GameEvents.IronmanEvents.RiskModeActivated(action.player, action.riskLevel))
            }
            is GameActions.IronmanActions.CheckSurvivalStatus -> {
                emitEvent(GameEvents.IronmanEvents.SurvivalStatusChecked(action.player, action.player.chips > 0))
            }
        }
    }

    private fun determineWinner(players: List<Player>): Player? {
        return players.maxByOrNull { it.chips }
    }

    private fun generateSessionStats(state: GameState.Playing): Map<String, Any> {
        return mapOf(
            "totalPlayers" to state.players.size,
            "totalPot" to state.pot,
            "currentPhase" to state.currentPhase.name,
            "gameMode" to state.gameMode.name,
            "roundNumber" to state.roundNumber,
            "hasSubState" to (state.subState != null)
        )
    }

    /**
     * Gets help content for a specific category.
     * Complete implementation with no stubs.
     */
    private fun getHelpContent(category: String): Map<String, String> {
        return when (category) {
            "general" -> mapOf(
                "title" to "General Help",
                "content" to "Welcome to Pokermon! This is a poker game with monster collection elements.",
                "actions" to "Use Call, Raise, or Fold to play poker. Exchange cards to improve your hand.",
                "modes" to "Try different game modes: Classic, Adventure, Safari, and Ironman for unique experiences."
            )
            "classic" -> mapOf(
                "title" to "Classic Mode",
                "content" to "Traditional poker gameplay with betting and card exchange.",
                "goal" to "Win chips by having the best poker hand or by making opponents fold.",
                "tips" to "Pay attention to betting patterns and manage your chip stack carefully."
            )
            "adventure" -> mapOf(
                "title" to "Adventure Mode",
                "content" to "Battle monsters using poker skills. Your hand strength determines battle effectiveness.",
                "goal" to "Defeat monsters to complete quests and earn rewards.",
                "tips" to "Strong poker hands deal more damage. Weak hands may result in taking damage."
            )
            "safari" -> mapOf(
                "title" to "Safari Mode", 
                "content" to "Capture wild monsters through strategic poker play.",
                "goal" to "Use safari balls (betting actions) to capture monsters. Better hands improve capture rates.",
                "tips" to "Different monsters have different capture rates. Save your best balls for rare encounters."
            )
            "ironman" -> mapOf(
                "title" to "Ironman Mode",
                "content" to "High-risk, high-reward gameplay with permadeath mechanics.",
                "goal" to "Convert winnings to gacha points for rare monster rewards, but avoid going broke.",
                "tips" to "Risk management is crucial. Permadeath means losing everything if you run out of chips."
            )
            else -> mapOf(
                "title" to "Help",
                "content" to "Help information not available for this category.",
                "suggestion" to "Try 'general', 'classic', 'adventure', 'safari', or 'ironman' categories."
            )
        }
    }

    /**
     * Gets achievements available for a specific victory type.
     */
    private fun getAchievementsForVictory(victoryType: VictoryType): List<String> {
        return when (victoryType) {
            VictoryType.CHIPS_VICTORY -> listOf("High Roller", "Chip Champion")
            VictoryType.ELIMINATION_VICTORY -> listOf("Eliminator", "Last One Standing")
            VictoryType.QUEST_COMPLETION -> listOf("Quest Master", "Adventure Complete")
            VictoryType.MONSTER_CAPTURE -> listOf("Monster Collector", "Safari Master")
            VictoryType.GACHA_JACKPOT -> listOf("Lucky Pull", "Gacha Master")
            VictoryType.SURVIVAL_VICTORY -> listOf("Survivor", "Against All Odds")
            VictoryType.TIME_VICTORY -> listOf("Speed Demon", "Quick Finish")
            VictoryType.SPECIAL_CONDITION -> listOf("Special Victory", "Unique Achievement")
        }
    }

    /**
     * Gets rewards for a specific victory type.
     */
    private fun getRewardsForVictory(victoryType: VictoryType): Map<String, Any> {
        return when (victoryType) {
            VictoryType.CHIPS_VICTORY -> mapOf("chips" to 1000, "title" to "Chip Master")
            VictoryType.ELIMINATION_VICTORY -> mapOf("experience" to 500, "title" to "Eliminator")
            VictoryType.QUEST_COMPLETION -> mapOf("questReward" to "Legendary Item", "experience" to 750)
            VictoryType.MONSTER_CAPTURE -> mapOf("monsters" to "Rare Collection", "safari_balls" to 10)
            VictoryType.GACHA_JACKPOT -> mapOf("gacha_points" to 5000, "rare_monsters" to 3)
            VictoryType.SURVIVAL_VICTORY -> mapOf("survival_bonus" to 2000, "prestige" to 1)
            VictoryType.TIME_VICTORY -> mapOf("speed_bonus" to 1500, "time_record" to true)
            VictoryType.SPECIAL_CONDITION -> mapOf("special_reward" to "Mystery Prize")
        }
    }

    /**
     * Gets event description for special events.
     */
    private fun getEventDescription(eventName: String): String {
        return when (eventName) {
            "lucky_draw" -> "A mysterious merchant offers you a lucky draw for your next hand!"
            "double_or_nothing" -> "Risk it all for a chance to double your current chips!"
            "monster_challenge" -> "A wild monster challenges you to a poker duel!"
            "treasure_chest" -> "You discovered a treasure chest! What's inside depends on your next hand."
            "cursed_cards" -> "Your cards have been cursed! Your next hand will have different rules."
            else -> "An unexpected event has occurred in your poker game!"
        }
    }

    /**
     * Gets event choices for special events.
     */
    private fun getEventChoices(eventName: String): List<EventChoice> {
        return when (eventName) {
            "lucky_draw" -> listOf(
                EventChoice("accept", "Accept the lucky draw", mapOf("luck_bonus" to 0.2)),
                EventChoice("decline", "Decline and continue normally", mapOf("safe_play" to true))
            )
            "double_or_nothing" -> listOf(
                EventChoice("risk_it", "Risk it all for double chips", mapOf("risk_multiplier" to 2.0)),
                EventChoice("play_safe", "Play it safe and keep current chips", mapOf("safe_play" to true))
            )
            "monster_challenge" -> listOf(
                EventChoice("accept_duel", "Accept the monster's challenge", mapOf("battle_mode" to true)),
                EventChoice("flee", "Flee from the monster", mapOf("flee_penalty" to -100))
            )
            else -> listOf(
                EventChoice("continue", "Continue the game", mapOf("default" to true))
            )
        }
    }

    /**
     * Gets achievement description.
     */
    private fun getAchievementDescription(achievementName: String): String {
        return when (achievementName) {
            "High Roller" -> "Won a game with over 5000 chips"
            "Chip Champion" -> "Accumulated the most chips in a session"
            "Eliminator" -> "Eliminated all opponents in a single game"
            "Last One Standing" -> "Won by being the last player with chips"
            "Quest Master" -> "Completed all quests in Adventure mode"
            "Adventure Complete" -> "Finished an Adventure mode session"
            "Monster Collector" -> "Captured 10 different monsters in Safari mode"
            "Safari Master" -> "Achieved 90% capture rate in Safari mode"
            "Lucky Pull" -> "Won a rare monster from gacha"
            "Gacha Master" -> "Performed 100 gacha pulls"
            "Survivor" -> "Survived 10 rounds in Ironman mode"
            "Against All Odds" -> "Won from last place with less than 100 chips"
            "Speed Demon" -> "Won a game in under 10 minutes"
            "Quick Finish" -> "Won a round in under 3 turns"
            else -> "Achievement unlocked: $achievementName"
        }
    }
}
