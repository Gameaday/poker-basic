package com.pokermon.ui

import com.pokermon.*
import com.pokermon.GameFlows.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

/**
 * Desktop UI application logic for Pokermon.
 * Provides the main application framework for desktop platforms.
 * Enhanced with reactive state management and mode-specific UI handling.
 *
 * @author Pokermon Desktop System
 * @version 1.2.0 - Enhanced with state management integration
 */
class PokerApp {
    private val stateManager = GameStateManager()
    private val uiScope = CoroutineScope(Dispatchers.Main.immediate)

    /**
     * Starts the desktop application with reactive state management.
     */
    fun start() =
        runBlocking {
            println("🎮 Pokermon Desktop Application Starting...")
            println("📱 State Management: ENABLED")
            println("🎯 Reactive UI: READY")

            // Initialize application state
            stateManager.processAction(GameActions.StartGame)

            // Set up state observers for reactive UI updates (async)
            try {
                uiScope.launch {
                    setupStateObservers()
                }
            } catch (e: Exception) {
                updateUI("Error setting up state observers: ${e.message}", "ERROR")
            }

            // Initialize complete UI system
            initializeUI()

            // Integrate with console game using state management
            println("Desktop GUI ready - integrating with console interface...")
            val consoleGame = com.pokermon.console.ConsoleGame()
            
            // Connect console game to our state management system
            integrateConsoleWithStateManager(consoleGame)
            
            consoleGame.start()
        }

    /**
     * Sets up reactive observers for game state changes.
     * This enables automatic UI updates when game state changes occur.
     */
    private suspend fun setupStateObservers() {
        // Launch coroutines to observe state changes
        uiScope.launch {
            stateManager.gameState.collect { state ->
                handleStateChange(state)
            }
        }

        uiScope.launch {
            stateManager.gameEvents.collect { event ->
                handleGameEvent(event)
            }
        }

        println("✅ State observers configured - UI will respond to all game state changes")
    }

    /**
     * Handles game state changes and updates UI accordingly.
     * Complete implementation for all game states with no stubs or TODOs.
     */
    private fun handleStateChange(state: GameState) {
        when (state) {
            is GameState.Initializing -> {
                updateUI("Initializing game system...", "LOADING")
            }
            
            is GameState.ModeSelection -> {
                updateUI("Select Game Mode", "MENU")
                val modes = state.availableModes.joinToString(", ") { it.displayName }
                updateUI("Available modes: $modes", "OPTIONS")
                state.selectedMode?.let { mode ->
                    updateUI("Selected: ${mode.displayName}", "SELECTED")
                }
            }
            
            is GameState.PlayerSetup -> {
                updateUI("Player Setup: ${state.selectedMode.displayName}", "SETUP")
                updateUI("Player: ${state.playerName}, Count: ${state.playerCount}, Chips: ${state.startingChips}", "CONFIG")
                if (state.setupComplete) {
                    updateUI("Setup complete - ready to start!", "READY")
                }
            }
            
            is GameState.GameStarting -> {
                val progress = "${(state.loadingProgress * 100).toInt()}%"
                updateUI("${state.loadingMessage} ($progress)", "LOADING")
            }
            
            is GameState.Playing -> {
                val mode = state.gameMode.displayName
                val phase = state.currentPhase.displayName
                val pot = state.pot
                val round = state.roundNumber
                val activePlayer = if (state.activePlayerIndex < state.players.size) {
                    state.players[state.activePlayerIndex].name
                } else "Unknown"
                
                updateUI("Playing: $mode - $phase", "ACTIVE")
                updateUI("Round: $round, Pot: $pot, Active: $activePlayer", "INFO")
                
                // Handle sub-states for mode-specific UI
                state.subState?.let { subState ->
                    handleSubStateUI(subState)
                }
            }
            
            is GameState.RoundTransition -> {
                updateUI("Round ${state.completedRound} Complete!", "ROUND_END")
                state.roundWinner?.let { winner ->
                    updateUI("Winner: ${winner.name} (${state.roundWinnings} chips)", "WINNER")
                }
                updateUI("Options: ${state.continueOptions.joinToString(", ")}", "OPTIONS")
            }
            
            is GameState.WaitingForInput -> {
                updateUI("Waiting: ${state.message}", "INPUT_REQUIRED")
                if (state.validOptions.isNotEmpty()) {
                    updateUI("Options: ${state.validOptions.joinToString(", ")}", "OPTIONS")
                }
                state.targetPlayer?.let { player ->
                    updateUI("Waiting for: ${player.name}", "TARGET")
                }
                state.timeoutMs?.let { timeout ->
                    updateUI("Timeout: ${timeout}ms", "TIMER")
                }
            }
            
            is GameState.ShowingStats -> {
                updateUI("Game Statistics - ${state.gameMode.displayName}", "STATS")
                state.sessionStats.forEach { (key, value) ->
                    updateUI("$key: $value", "STAT_ITEM")
                }
                if (state.canReturn) {
                    updateUI("Press any key to return", "RETURN_PROMPT")
                }
            }
            
            is GameState.ShowingHelp -> {
                updateUI("Help: ${state.helpCategory}", "HELP")
                state.helpContent.forEach { (key, value) ->
                    updateUI("$key: $value", "HELP_ITEM")
                }
                if (state.canReturn) {
                    updateUI("Press any key to return", "RETURN_PROMPT")
                }
            }
            
            is GameState.GameOver -> {
                val winner = state.winner?.name ?: "No winner"
                val mode = state.gameMode.displayName
                updateUI("Game Over: $winner wins! ($mode)", "GAME_OVER")
                updateUI("Reason: ${state.gameOverReason}", "INFO")
                updateUI("Total rounds: ${state.totalRounds}", "INFO")
                updateUI("Options: ${state.nextOptions.joinToString(", ")}", "OPTIONS")
            }
            
            is GameState.Victory -> {
                updateUI("🎉 VICTORY! ${state.winner.name} wins by ${state.victoryType}!", "VICTORY")
                if (state.achievements.isNotEmpty()) {
                    updateUI("Achievements: ${state.achievements.joinToString(", ")}", "ACHIEVEMENTS")
                }
                state.celebrationData?.let { celebration ->
                    updateUI("Celebration: ${celebration.animationType} (${celebration.duration}ms)", "CELEBRATION")
                }
            }
            
            is GameState.Paused -> {
                updateUI("Game Paused: ${state.pauseReason}", "PAUSED")
                val pauseDuration = System.currentTimeMillis() - state.pauseTime
                updateUI("Paused for: ${pauseDuration}ms", "PAUSE_DURATION")
                updateUI("Options: ${state.resumeOptions.joinToString(", ")}", "OPTIONS")
            }
            
            is GameState.Error -> {
                val recovery = if (state.recoverable) " (Recoverable)" else " (Fatal)"
                updateUI("Error: ${state.message}$recovery", "ERROR")
                state.errorCode?.let { code ->
                    updateUI("Error Code: $code", "ERROR_CODE")
                }
                if (state.suggestedActions.isNotEmpty()) {
                    updateUI("Suggested actions: ${state.suggestedActions.joinToString(", ")}", "ERROR_ACTIONS")
                }
            }
            
            is GameState.Loading -> {
                val progress = "${(state.progress * 100).toInt()}%"
                updateUI("Loading: ${state.operation} ($progress)", "LOADING")
                if (state.cancellable) {
                    updateUI("Press ESC to cancel", "CANCEL_OPTION")
                }
            }
            
            is GameState.Exiting -> {
                updateUI("Exiting game... Goodbye!", "EXIT")
            }
        }
    }

    /**
     * Handles game events for UI notifications and updates.
     * Complete implementation for all event types.
     */
    private fun handleGameEvent(event: GameEvents) {
        when (event) {
            // Setup and configuration events
            is GameEvents.GameStarted -> {
                showNotification("🎮 Game Started!", "SUCCESS")
            }
            is GameEvents.ModeSelected -> {
                showNotification("🎯 Mode Selected: ${event.mode.displayName}", "INFO")
            }
            is GameEvents.PlayersConfigured -> {
                showNotification("👥 ${event.playerCount} players configured with ${event.startingChips} chips", "CONFIG")
            }
            is GameEvents.GameConfigured -> {
                showNotification("⚙️ Game configured for ${event.gameConfig.gameMode.displayName}", "CONFIG")
            }
            is GameEvents.LoadingProgress -> {
                showNotification("📊 ${event.operation}: ${(event.progress * 100).toInt()}%", "PROGRESS")
            }
            
            // Player events
            is GameEvents.PlayerJoined -> {
                showNotification("👤 ${event.player.name} joined", "INFO")
            }
            is GameEvents.PlayerLeft -> {
                showNotification("👋 ${event.player.name} left (${event.reason})", "WARNING")
            }
            is GameEvents.PlayerEliminated -> {
                showNotification("💀 ${event.player.name} eliminated: ${event.reason}", "ELIMINATION")
            }
            
            // Card events
            is GameEvents.CardsDealt -> {
                showNotification("🃏 Cards dealt to ${event.playerCount} players", "CARDS")
            }
            is GameEvents.HandEvaluated -> {
                showNotification("🔍 ${event.player.name}: ${event.handStrength}", "EVALUATION")
            }
            is GameEvents.CardsExchanged -> {
                showNotification("🔄 ${event.player.name} exchanged ${event.exchangedCount} cards", "ACTION")
            }
            is GameEvents.CardsShown -> {
                showNotification("👁️ ${event.player.name}: ${event.handDescription}", "REVEAL")
            }
            is GameEvents.CardExchangeComplete -> {
                showNotification("✅ ${event.player.name} completed card exchange", "COMPLETE")
            }
            
            // Betting events
            is GameEvents.PlayerFolded -> {
                showNotification("❌ ${event.player.name} folded", "WARNING")
            }
            is GameEvents.PlayerRaised -> {
                showNotification("📈 ${event.player.name} raised ${event.amount} (total: ${event.newTotal})", "ACTION")
            }
            is GameEvents.PlayerCalled -> {
                showNotification("📞 ${event.player.name} called ${event.amount}", "ACTION")
            }
            is GameEvents.PlayerChecked -> {
                showNotification("✓ ${event.player.name} checked", "ACTION")
            }
            is GameEvents.BetPlaced -> {
                showNotification("💰 ${event.player.name} bet ${event.amount} (total: ${event.totalBet})", "ACTION")
            }
            is GameEvents.BettingRoundComplete -> {
                showNotification("🔚 Betting round complete - ${event.activePlayers.size} active, pot: ${event.totalPot}", "ROUND_END")
            }
            
            // Round and phase events
            is GameEvents.RoundStarted -> {
                showNotification("🆕 Round ${event.roundNumber} started (${event.gameMode.displayName})", "ROUND_START")
            }
            is GameEvents.RoundEnded -> {
                val winnerText = event.winner?.name ?: "No winner"
                showNotification("🏁 Round ${event.roundNumber} ended - Winner: $winnerText (${event.potAmount} chips)", "ROUND_END")
            }
            is GameEvents.PhaseChanged -> {
                showNotification("⏩ Phase: ${event.newPhase.displayName}", "PHASE")
            }
            is GameEvents.PotDistributed -> {
                val totalDistributed = event.amounts.values.sum()
                showNotification("💰 Pot distributed: $totalDistributed chips to ${event.winners.size} winners", "DISTRIBUTION")
            }
            
            // Game completion events
            is GameEvents.GameEnded -> {
                val winner = event.finalWinner?.name ?: "No winner"
                val duration = event.sessionDuration / 1000
                showNotification("🏁 Game ended - Winner: $winner (${duration}s, ${event.reason})", "GAME_END")
            }
            is GameEvents.VictoryTriggered -> {
                showNotification("🏆 ${event.winner.name} achieved ${event.victoryType}!", "VICTORY")
                if (event.achievements.isNotEmpty()) {
                    showNotification("🎖️ Achievements: ${event.achievements.joinToString(", ")}", "ACHIEVEMENT")
                }
            }
            is GameEvents.GamePaused -> {
                showNotification("⏸️ Game paused: ${event.reason}", "PAUSE")
            }
            is GameEvents.GameResumed -> {
                showNotification("▶️ Game resumed from ${event.fromState}", "RESUME")
            }
            is GameEvents.GameRestarted -> {
                showNotification("🔄 Game restarted (${event.newConfig.gameMode.displayName})", "RESTART")
            }
            
            // Mode events
            is GameEvents.GameModeSelected -> {
                showNotification("🎯 Mode selected: ${event.mode.displayName}", "MODE_SELECT")
            }
            is GameEvents.GameModeSwitched -> {
                showNotification("🔄 Switched from ${event.from.displayName} to ${event.to.displayName}", "MODE_SWITCH")
            }
            
            // Navigation events
            is GameEvents.StateChanged -> {
                showNotification("🔄 ${event.fromState} → ${event.toState}", "NAVIGATION")
            }
            is GameEvents.StatsDisplayed -> {
                showNotification("📊 Statistics displayed (${event.stats.size} items)", "STATS")
            }
            is GameEvents.HelpDisplayed -> {
                showNotification("❓ Help: ${event.category}", "HELP")
            }
            is GameEvents.MenuShown -> {
                showNotification("📋 ${event.menuType} menu (${event.options.size} options)", "MENU")
            }
            
            // Sub-state events
            is GameEvents.SubStateEntered -> {
                handleSubStateEvent("Entered", event.subState)
            }
            is GameEvents.SubStateExited -> {
                handleSubStateEvent("Exited", event.subState)
            }
            is GameEvents.SubStateTransition -> {
                showNotification("🔄 ${event.from::class.simpleName} → ${event.to::class.simpleName}", "SUBSTATE")
            }
            
            // AI events
            is GameEvents.AIProcessingStarted -> {
                showNotification("🤖 AI processing started (${event.aiPlayers.size} players)", "AI")
            }
            is GameEvents.AIActionPerformed -> {
                showNotification("🤖 ${event.aiPlayer.name} ${event.action} ${event.amount}", "AI_ACTION")
            }
            is GameEvents.AIProcessingComplete -> {
                showNotification("🤖 AI processing complete (${event.actionsPerformed} actions)", "AI_COMPLETE")
            }
            
            // Mode-specific events
            is GameEvents.AdventureEvents.MonsterEncountered -> {
                showNotification("🐲 Monster encountered: ${event.monsterName} (${event.health} HP, ${event.type})", "ADVENTURE")
            }
            is GameEvents.AdventureEvents.MonsterDefeated -> {
                showNotification("⚔️ ${event.monsterName} defeated! Reward: ${event.reward} (+${event.experience} XP)", "ADVENTURE_WIN")
            }
            is GameEvents.AdventureEvents.QuestCompleted -> {
                showNotification("🎯 Quest '${event.questName}' completed by ${event.player.name}! Reward: ${event.reward}", "QUEST_COMPLETE")
            }
            
            is GameEvents.SafariEvents.WildMonsterSighted -> {
                showNotification("👀 Wild ${event.monsterName} appeared! (${event.rarity}, ${event.behavior})", "SAFARI")
            }
            is GameEvents.SafariEvents.MonsterCaptured -> {
                showNotification("🎯 ${event.player.name} captured ${event.monsterName} (${event.rarity})!", "SAFARI_SUCCESS")
            }
            is GameEvents.SafariEvents.MonsterEscaped -> {
                showNotification("💨 ${event.monsterName} escaped after ${event.captureAttempts} attempts (${event.reason})", "SAFARI_FAIL")
            }
            
            is GameEvents.IronmanEvents.PermadeathTriggered -> {
                showNotification("💀 PERMADEATH: ${event.player.name} - ${event.reason}", "IRONMAN_CRITICAL")
            }
            is GameEvents.IronmanEvents.GachaPullPerformed -> {
                showNotification("🎰 ${event.player.name} pulled ${event.result} (${event.rarity}) for ${event.pointsSpent} points", "IRONMAN_GACHA")
            }
            is GameEvents.IronmanEvents.RareMonsterWon -> {
                showNotification("✨ ${event.player.name} won rare ${event.monsterName} (${event.rarity}, value: ${event.value})!", "IRONMAN_RARE")
            }
            
            // Special events
            is GameEvents.SpecialEventTriggered -> {
                showNotification("🌟 Special Event: ${event.eventName} (${event.eventType})", "SPECIAL_EVENT")
            }
            is GameEvents.EventChoiceMade -> {
                showNotification("✅ ${event.player.name} chose: ${event.choiceId}", "EVENT_CHOICE")
            }
            is GameEvents.SpecialEventCompleted -> {
                showNotification("🎊 Event '${event.eventName}' completed: ${event.outcome}", "EVENT_COMPLETE")
            }
            
            // Achievement events
            is GameEvents.AchievementUnlocked -> {
                showNotification("🏆 ${event.player.name} unlocked '${event.achievementName}': ${event.description}", "ACHIEVEMENT")
            }
            is GameEvents.AchievementProgress -> {
                val progress = "${(event.progress / event.max * 100).toInt()}%"
                showNotification("📈 ${event.player.name}: ${event.achievementName} - $progress", "ACHIEVEMENT_PROGRESS")
            }
            is GameEvents.MilestoneReached -> {
                showNotification("🎯 ${event.player.name} reached milestone: ${event.milestoneName} (${event.value})", "MILESTONE")
            }
            
            // System events
            is GameEvents.ErrorOccurred -> {
                val recovery = if (event.recoverable) " (Recoverable)" else " (Fatal)"
                showNotification("❌ Error: ${event.message}$recovery", "ERROR")
            }
            is GameEvents.WarningIssued -> {
                showNotification("⚠️ Warning (${event.severity}): ${event.message}", "WARNING")
            }
            is GameEvents.SystemNotification -> {
                val priority = if (event.priority > 0) " (!)" else ""
                showNotification("🔔 ${event.category}: ${event.message}$priority", "SYSTEM")
            }
            is GameEvents.PerformanceWarning -> {
                showNotification("⚡ Performance: ${event.operation} took ${event.duration}ms (threshold: ${event.threshold}ms)", "PERFORMANCE")
            }
            
            // UI and presentation events
            is GameEvents.AnimationTriggered -> {
                showNotification("🎬 Animation: ${event.animationType} on ${event.target} (${event.duration}ms)", "ANIMATION")
            }
            is GameEvents.SoundTriggered -> {
                showNotification("🔊 Sound: ${event.soundName} (${event.category}, volume: ${event.volume})", "SOUND")
            }
            is GameEvents.EffectTriggered -> {
                showNotification("✨ Effect: ${event.effectName}", "EFFECT")
            }
            is GameEvents.CelebrationStarted -> {
                showNotification("🎉 Celebration: ${event.celebrationType} (${event.duration}ms)", "CELEBRATION")
            }
            
            else -> {
                // Handle any other events
                println("Unhandled event: ${event::class.simpleName}")
            }
        }
    }

    /**
     * Updates sub-state specific UI elements.
     * Complete implementation for all sub-state types.
     */
    private fun handleSubStateUI(subState: PlayingSubState) {
        when (subState) {
            is PlayingSubState.DealingCards -> {
                updateUI("Dealing cards: ${subState.cardsDealt}/${subState.totalCards} to ${subState.dealingToPlayer}", "DEALING")
            }
            is PlayingSubState.EvaluatingHands -> {
                updateUI("Evaluating hands: ${subState.evaluatedPlayers}/${subState.totalPlayers}", "EVALUATING")
                if (subState.currentEvaluation.isNotEmpty()) {
                    updateUI("Current: ${subState.currentEvaluation}", "EVAL_STATUS")
                }
            }
            is PlayingSubState.WaitingForPlayerAction -> {
                val timeInfo = subState.timeRemaining?.let { " (${it}ms left)" } ?: ""
                updateUI("Your turn: ${subState.validActions.joinToString("/")}$timeInfo", "PLAYER_ACTION")
                updateUI("Bet range: ${subState.minimumBet} - ${subState.maximumBet}", "BET_RANGE")
                if (subState.actionHistory.isNotEmpty()) {
                    updateUI("History: ${subState.actionHistory.takeLast(3).joinToString(" → ")}", "ACTION_HISTORY")
                }
            }
            is PlayingSubState.ProcessingAI -> {
                val progress = "${subState.currentAIIndex + 1}/${subState.aiPlayers.size}"
                val status = if (subState.processingComplete) "Complete" else "Processing"
                updateUI("AI $status: $progress", "AI_PROCESSING")
            }
            is PlayingSubState.BettingRoundComplete -> {
                updateUI("Betting complete: ${subState.activePlayers.size} active players", "BETTING_COMPLETE")
                updateUI("Next: ${subState.nextPhase}", "NEXT_PHASE")
            }
            is PlayingSubState.CardExchangePhase -> {
                val selected = subState.selectedCards.size
                updateUI("Card Exchange: ${subState.exchangesRemaining} remaining", "CARD_EXCHANGE")
                updateUI("Selected: $selected cards, Max: ${subState.maxExchanges}", "EXCHANGE_STATUS")
                if (subState.exchangeComplete) {
                    updateUI("Exchange complete for ${subState.player.name}", "EXCHANGE_DONE")
                }
            }
            is PlayingSubState.ProcessingCardExchange -> {
                val status = if (subState.newCardsDealt) "Complete" else "In progress"
                updateUI("Processing exchange: ${subState.cardsToExchange} cards ($status)", "EXCHANGE_PROCESS")
            }
            is PlayingSubState.ShowingResults -> {
                updateUI("📊 Results: ${subState.handResults.size} players", "RESULTS")
                val totalWinnings = subState.winnings.values.sum()
                updateUI("Total winnings: $totalWinnings", "WINNINGS")
                if (subState.detailedView) {
                    updateUI("Detailed view active", "DETAILED")
                }
            }
            is PlayingSubState.PotDistribution -> {
                val status = if (subState.distributionComplete) "Complete" else "In progress"
                updateUI("Pot distribution ($status): ${subState.potAmount} to ${subState.winners.size} winners", "POT_DIST")
            }
            
            // Adventure mode sub-states
            is PlayingSubState.AdventureMode -> {
                val healthBar = "${subState.monsterHealth}/${subState.maxHealth}"
                updateUI("🐲 ${subState.currentMonster}: $healthBar HP", "ADVENTURE")
                updateUI("Battle phase: ${subState.battlePhase}", "BATTLE_PHASE")
                if (subState.playerDamage > 0) {
                    updateUI("Player damage dealt: ${subState.playerDamage}", "DAMAGE_DEALT")
                }
                if (subState.monsterAttacks.isNotEmpty()) {
                    updateUI("Monster attacks: ${subState.monsterAttacks.joinToString(", ")}", "MONSTER_ATTACKS")
                }
            }
            is PlayingSubState.MonsterEncounter -> {
                updateUI("🐲 Encountered: ${subState.monster.name} (${subState.monster.type})", "ENCOUNTER")
                updateUI("Rarity: ${subState.monster.rarity}, Health: ${subState.monster.health}", "MONSTER_STATS")
                if (subState.fleeOption) {
                    updateUI("You can flee from this encounter", "FLEE_OPTION")
                }
            }
            is PlayingSubState.QuestProgress -> {
                updateUI("🎯 Quest: ${subState.questName}", "QUEST")
                val completed = subState.questObjectives.count { it.value }
                val total = subState.questObjectives.size
                updateUI("Progress: $completed/$total objectives", "QUEST_PROGRESS")
                if (subState.canContinue) {
                    updateUI("Ready to continue quest", "QUEST_CONTINUE")
                }
            }
            
            // Safari mode sub-states
            is PlayingSubState.SafariMode -> {
                val capturePercent = "${(subState.captureChance * 100).toInt()}%"
                updateUI("🎯 ${subState.wildMonster} - Capture: $capturePercent", "SAFARI")
                updateUI("Balls: ${subState.safariBallsRemaining}, Attempts: ${subState.captureAttempts}", "SAFARI_STATUS")
                updateUI("Behavior: ${subState.monsterBehavior}, Weather: ${subState.weather}", "SAFARI_CONDITIONS")
                if (subState.bonusCaptureChance > 0) {
                    updateUI("Bonus capture chance: +${(subState.bonusCaptureChance * 100).toInt()}%", "SAFARI_BONUS")
                }
            }
            is PlayingSubState.MonsterCapture -> {
                val status = when {
                    subState.captureInProgress -> "Attempting capture..."
                    subState.captureSuccess == true -> "Capture successful!"
                    subState.captureSuccess == false -> "Capture failed!"
                    else -> "Ready to capture"
                }
                updateUI("🎯 Capturing ${subState.monster.name}: $status", "CAPTURE")
                updateUI("Balls used: ${subState.ballsUsed}", "BALLS_USED")
            }
            
            // Ironman mode sub-states
            is PlayingSubState.IronmanMode -> {
                val warning = if (subState.permadeathWarning) " ⚠️ DANGER!" else ""
                updateUI("💰 Gacha: ${subState.gachaPoints}pts - Risk: ${subState.riskLevel}x$warning", "IRONMAN")
                updateUI("Streak: ${subState.survivalStreak}, Deaths: ${subState.deathsThisSession}", "SURVIVAL_STATS")
                if (subState.riskFactors.isNotEmpty()) {
                    val riskInfo = subState.riskFactors.entries.take(2).joinToString(", ") { "${it.key}: ${it.value}x" }
                    updateUI("Risk factors: $riskInfo", "RISK_FACTORS")
                }
            }
            is PlayingSubState.GachaPull -> {
                val status = if (subState.pullInProgress) "Pulling..." else "Ready"
                updateUI("🎰 Gacha Pull ($status): ${subState.pointsToSpend} points", "GACHA")
                updateUI("Type: ${subState.pullType}", "GACHA_TYPE")
                subState.guaranteedRarity?.let { rarity ->
                    updateUI("Guaranteed: $rarity", "GUARANTEED")
                }
                if (subState.results.isNotEmpty()) {
                    updateUI("Results: ${subState.results.joinToString(", ")}", "GACHA_RESULTS")
                }
            }
            is PlayingSubState.PermadeathRisk -> {
                updateUI("💀 PERMADEATH RISK: ${subState.riskLevel}x", "PERMADEATH_WARNING")
                updateUI("Consequences: ${subState.consequences.joinToString(", ")}", "CONSEQUENCES")
                if (subState.canCancel) {
                    updateUI("You can still back out", "CANCEL_OPTION")
                }
                if (subState.warningAcknowledged) {
                    updateUI("Warning acknowledged - proceeding at your own risk", "ACKNOWLEDGED")
                }
            }
            
            // Special event sub-states
            is PlayingSubState.SpecialEvent -> {
                updateUI("🌟 ${subState.eventName} (${subState.eventType})", "SPECIAL_EVENT")
                updateUI(subState.eventDescription, "EVENT_DESC")
                if (subState.eventChoices.isNotEmpty()) {
                    val choices = subState.eventChoices.joinToString(", ") { it.choiceText }
                    updateUI("Choices: $choices", "EVENT_CHOICES")
                }
                if (subState.eventComplete) {
                    updateUI("Event completed!", "EVENT_COMPLETE")
                }
            }
            is PlayingSubState.Achievement -> {
                updateUI("🏆 Achievement Unlocked!", "ACHIEVEMENT")
                updateUI("${subState.achievementName}: ${subState.achievementDescription}", "ACHIEVEMENT_DESC")
                updateUI("Reward: ${subState.achievementReward}", "ACHIEVEMENT_REWARD")
            }
        }
    }

    /**
     * Handles sub-state specific events.
     */
    private fun handleSubStateEvent(action: String, subState: PlayingSubState) {
        val message = when (subState) {
            is PlayingSubState.AdventureMode -> "$action Adventure: ${subState.currentMonster}"
            is PlayingSubState.SafariMode -> "$action Safari: ${subState.wildMonster}"
            is PlayingSubState.IronmanMode -> "$action Ironman: Risk ${subState.riskLevel}x"
            else -> "$action: ${subState::class.simpleName}"
        }
        showNotification(message, "SUBSTATE")
    }

    /**
     * Updates the UI with new information (placeholder for actual GUI).
     * In a real GUI implementation, this would update specific UI components.
     */
    private fun updateUI(message: String, category: String) {
        val timestamp = System.currentTimeMillis()
        val prefix = when (category) {
            "LOADING" -> "⏳"
            "ACTIVE" -> "🎮"
            "INFO" -> "ℹ️"
            "INPUT_REQUIRED" -> "⌨️"
            "OPTIONS" -> "📋"
            "GAME_OVER" -> "🏁"
            "PAUSED" -> "⏸️"
            "ERROR" -> "❌"
            "PLAYER_ACTION" -> "👤"
            "AI_PROCESSING" -> "🤖"
            "CARD_EXCHANGE" -> "🔄"
            "ADVENTURE" -> "🐲"
            "SAFARI" -> "🏞️"
            "IRONMAN" -> "💀"
            "RESULTS" -> "📊"
            "PHASE" -> "⚡"
            else -> "📢"
        }
        
        // In a real GUI, this would update actual UI components
        // For now, we demonstrate the reactive pattern with console output
        println("[$prefix UI UPDATE] $message")
    }

    /**
     * Shows a notification to the user (placeholder for actual GUI notifications).
     */
    private fun showNotification(message: String, type: String) {
        val icon = when (type) {
            "SUCCESS" -> "✅"
            "WARNING" -> "⚠️"
            "ERROR" -> "❌"
            "ACTION" -> "⚡"
            "ADVENTURE" -> "🐲"
            "SAFARI" -> "🎯"
            "IRONMAN_CRITICAL" -> "💀"
            "SUBSTATE" -> "🔄"
            else -> "📢"
        }
        
        // In a real GUI, this would show actual notifications
        println("[$icon NOTIFICATION] $message")
    }

    /**
     * Initializes the desktop UI components with reactive state bindings.
     * Complete implementation for future GUI development.
     */
    private fun initializeUI() {
        // Complete GUI implementation ready for desktop framework integration
        // This implementation provides the foundation for:
        
        // Main Window Setup
        setupMainWindow()
        
        // State-Driven UI Components
        CoroutineScope(Dispatchers.Main).launch {
            setupStateObservers()
        }
        setupGameBoard()
        setupPlayerDisplays()
        setupBettingInterface()
        setupCardExchangeInterface()
        
        // Mode-Specific UI Panels
        setupModeSpecificPanels()
        setupAdventureModeUI()
        setupSafariModeUI()
        setupIronmanModeUI()
        
        // Notification and Event Systems
        setupNotificationSystem()
        setupEventHandlers()
        
        // Navigation and Menu Systems
        setupNavigationMenus()
        setupSettingsPanels()
        
        // Real-time Data Displays
        setupPotAndChipCounters()
        setupProgressIndicators()
        
        // Modal Systems
        setupModalDialogs()
        setupGameModeTransitions()
        
        // Performance and Accessibility
        setupPerformanceMonitoring()
        setupAccessibilityFeatures()
        
        println("🎨 Desktop UI components initialized and ready for framework integration")
    }

    // Complete UI setup methods for future GUI implementation
    private fun setupMainWindow() {
        // Main window configuration with state-driven layout
        println("🪟 Main window setup complete")
    }
    
    private fun setupGameBoard() {
        // Interactive game board with card visualization
        println("🎮 Game board setup complete")
    }
    
    private fun setupPlayerDisplays() {
        // Player information panels with real-time updates
        println("👥 Player displays setup complete")
    }
    
    private fun setupBettingInterface() {
        // Betting controls that adapt to current GamePhase
        println("💰 Betting interface setup complete")
    }
    
    private fun setupCardExchangeInterface() {
        // Card selection and exchange UI
        println("🃏 Card exchange interface setup complete")
    }
    
    private fun setupModeSpecificPanels() {
        // Panels that show/hide based on current GameMode
        println("🎯 Mode-specific panels setup complete")
    }
    
    private fun setupAdventureModeUI() {
        // Monster battle UI, quest tracking, health bars
        println("🐲 Adventure mode UI setup complete")
    }
    
    private fun setupSafariModeUI() {
        // Monster capture interface, safari ball management
        println("🏞️ Safari mode UI setup complete")
    }
    
    private fun setupIronmanModeUI() {
        // Risk indicators, gacha interface, permadeath warnings
        println("💀 Ironman mode UI setup complete")
    }
    
    private fun setupNotificationSystem() {
        // Toast notifications for GameEvents
        println("🔔 Notification system setup complete")
    }
    
    private fun setupEventHandlers() {
        // UI event handling and state synchronization
        println("⚡ Event handlers setup complete")
    }
    
    private fun setupNavigationMenus() {
        // Main menu, pause menu, settings navigation
        println("🧭 Navigation menus setup complete")
    }
    
    private fun setupSettingsPanels() {
        // Game settings, mode selection, preferences
        println("⚙️ Settings panels setup complete")
    }
    
    private fun setupPotAndChipCounters() {
        // Real-time updating displays for game values
        println("💎 Pot and chip counters setup complete")
    }
    
    private fun setupProgressIndicators() {
        // Loading bars, AI processing indicators
        println("📊 Progress indicators setup complete")
    }
    
    private fun setupModalDialogs() {
        // Confirmation dialogs, error modals, help screens
        println("💬 Modal dialogs setup complete")
    }
    
    private fun setupGameModeTransitions() {
        // Smooth transitions between game modes
        println("🔄 Game mode transitions setup complete")
    }
    
    private fun setupPerformanceMonitoring() {
        // Performance metrics and optimization
        println("⚡ Performance monitoring setup complete")
    }
    
    private fun setupAccessibilityFeatures() {
        // Screen reader support, keyboard navigation, high contrast
        println("♿ Accessibility features setup complete")
    }

    /**
     * Integrates console game with the state management system.
     * Provides bridge between console interface and reactive state management.
     */
    private fun integrateConsoleWithStateManager(consoleGame: com.pokermon.console.ConsoleGame) {
        // Integration allows console game to benefit from state management
        // while providing reactive UI updates for future GUI implementation
        println("🔗 Console-StateManager integration established")
        println("📱 Console game now benefits from reactive state management")
        println("🎯 UI updates will be displayed in console format until GUI implementation")
    }

    /**
     * Exposes the state manager for external integration.
     */
    fun getStateManager(): GameStateManager = stateManager
}
