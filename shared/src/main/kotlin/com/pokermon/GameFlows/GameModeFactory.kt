package com.pokermon.GameFlows

import com.pokermon.GameMode
import com.pokermon.players.Player

/**
 * Factory for creating mode-specific configurations and behaviors.
 * Enables modular game mode implementation and easy extensibility.
 *
 * @author Pokermon Flow System
 * @version 1.0.0
 */
object GameModeFactory {
    /**
     * Configuration for a game mode including sub-states and behaviors.
     */
    data class GameModeConfig(
        val mode: GameMode,
        val initialSubState: PlayingSubState? = null,
        val supportedSubStates: List<Class<out PlayingSubState>> = emptyList(),
        val customActions: List<Class<out GameActions>> = emptyList(),
        val customEvents: List<Class<out GameEvents>> = emptyList(),
        val modeSpecificLogic: GameModeLogic? = null,
    )

    /**
     * Interface for mode-specific game logic.
     */
    interface GameModeLogic {
        fun onGameStart(players: List<Player>): PlayingSubState?

        fun onRoundStart(
            roundNumber: Int,
            players: List<Player>,
        ): PlayingSubState?

        fun onPlayerAction(
            action: GameActions,
            currentState: GameState.Playing,
        ): PlayingSubState?

        fun calculateWinCondition(players: List<Player>): Player?

        fun processCustomAction(action: GameActions): GameEvents?
    }

    /**
     * Gets the configuration for a specific game mode.
     */
    fun getModeConfig(mode: GameMode): GameModeConfig {
        return when (mode) {
            GameMode.CLASSIC -> createClassicConfig()
            GameMode.ADVENTURE -> createAdventureConfig()
            GameMode.SAFARI -> createSafariConfig()
            GameMode.IRONMAN -> createIronmanConfig()
        }
    }

    /**
     * Creates a sub-state for a given mode and context.
     */
    fun createModeSubState(
        mode: GameMode,
        context: String,
        players: List<Player>,
    ): PlayingSubState? {
        return when (mode) {
            GameMode.CLASSIC -> null // Classic mode typically doesn't use sub-states
            GameMode.ADVENTURE -> createAdventureSubState(context, players)
            GameMode.SAFARI -> createSafariSubState(context, players)
            GameMode.IRONMAN -> createIronmanSubState(context, players)
        }
    }

    private fun createClassicConfig(): GameModeConfig {
        return GameModeConfig(
            mode = GameMode.CLASSIC,
            supportedSubStates =
                listOf(
                    PlayingSubState.WaitingForPlayerAction::class.java,
                    PlayingSubState.ProcessingAI::class.java,
                    PlayingSubState.CardExchangePhase::class.java,
                    PlayingSubState.ShowingResults::class.java,
                ),
            modeSpecificLogic = ClassicModeLogic(),
        )
    }

    private fun createAdventureConfig(): GameModeConfig {
        return GameModeConfig(
            mode = GameMode.ADVENTURE,
            initialSubState = PlayingSubState.AdventureMode("Forest Dragon", 100),
            supportedSubStates =
                listOf(
                    PlayingSubState.AdventureMode::class.java,
                    PlayingSubState.WaitingForPlayerAction::class.java,
                    PlayingSubState.ShowingResults::class.java,
                ),
            customActions = listOf(GameActions.AdventureActions::class.java),
            customEvents = listOf(GameEvents.AdventureEvents::class.java),
            modeSpecificLogic = AdventureModeLogic(),
        )
    }

    private fun createSafariConfig(): GameModeConfig {
        return GameModeConfig(
            mode = GameMode.SAFARI,
            initialSubState = PlayingSubState.SafariMode("Wild Pikachu", 0.3, 30),
            supportedSubStates =
                listOf(
                    PlayingSubState.SafariMode::class.java,
                    PlayingSubState.WaitingForPlayerAction::class.java,
                    PlayingSubState.ShowingResults::class.java,
                ),
            customActions = listOf(GameActions.SafariActions::class.java),
            customEvents = listOf(GameEvents.SafariEvents::class.java),
            modeSpecificLogic = SafariModeLogic(),
        )
    }

    private fun createIronmanConfig(): GameModeConfig {
        return GameModeConfig(
            mode = GameMode.IRONMAN,
            initialSubState = PlayingSubState.IronmanMode(0, 1.0),
            supportedSubStates =
                listOf(
                    PlayingSubState.IronmanMode::class.java,
                    PlayingSubState.WaitingForPlayerAction::class.java,
                    PlayingSubState.ShowingResults::class.java,
                ),
            customActions = listOf(GameActions.IronmanActions::class.java),
            customEvents = listOf(GameEvents.IronmanEvents::class.java),
            modeSpecificLogic = IronmanModeLogic(),
        )
    }

    private fun createAdventureSubState(
        context: String,
        players: List<Player>,
    ): PlayingSubState {
        return when (context) {
            "monster_encounter" -> PlayingSubState.AdventureMode("Forest Dragon", 100)
            "quest_start" -> PlayingSubState.AdventureMode("Cave Beast", 150)
            else -> PlayingSubState.AdventureMode("Unknown Monster", 50)
        }
    }

    private fun createSafariSubState(
        context: String,
        players: List<Player>,
    ): PlayingSubState {
        return when (context) {
            "wild_encounter" -> PlayingSubState.SafariMode("Wild Pokemon", 0.4, 25)
            "rare_encounter" -> PlayingSubState.SafariMode("Shiny Pokemon", 0.1, 25)
            else -> PlayingSubState.SafariMode("Common Pokemon", 0.6, 30)
        }
    }

    private fun createIronmanSubState(
        context: String,
        players: List<Player>,
    ): PlayingSubState {
        return when (context) {
            "gacha_ready" -> PlayingSubState.IronmanMode(1000, 1.5)
            "high_risk" -> PlayingSubState.IronmanMode(500, 2.0, true)
            else -> PlayingSubState.IronmanMode(0, 1.0)
        }
    }
}

// Mode-specific logic implementations

class ClassicModeLogic : GameModeFactory.GameModeLogic {
    override fun onGameStart(players: List<Player>): PlayingSubState? = null

    override fun onRoundStart(
        roundNumber: Int,
        players: List<Player>,
    ): PlayingSubState? = null

    override fun onPlayerAction(
        action: GameActions,
        currentState: GameState.Playing,
    ): PlayingSubState? = null

    override fun calculateWinCondition(players: List<Player>): Player? = players.maxByOrNull { it.chips }

    override fun processCustomAction(action: GameActions): GameEvents? = null
}

class AdventureModeLogic : GameModeFactory.GameModeLogic {
    override fun onGameStart(players: List<Player>): PlayingSubState = PlayingSubState.AdventureMode("Forest Dragon", 100)

    override fun onRoundStart(
        roundNumber: Int,
        players: List<Player>,
    ): PlayingSubState = PlayingSubState.AdventureMode("Level $roundNumber Boss", 100 + (roundNumber * 50))

    override fun onPlayerAction(
        action: GameActions,
        currentState: GameState.Playing,
    ): PlayingSubState? {
        return when (action) {
            is GameActions.Call, is GameActions.Raise -> {
                // Successful poker action damages the monster
                val currentSub = currentState.subState
                if (currentSub is PlayingSubState.AdventureMode) {
                    val newHealth = (currentSub.monsterHealth - 25).coerceAtLeast(0)
                    if (newHealth > 0) {
                        currentSub.copy(monsterHealth = newHealth)
                    } else {
                        null // Monster defeated
                    }
                } else {
                    null
                }
            }
            else -> null
        }
    }

    override fun calculateWinCondition(players: List<Player>): Player? {
        // In adventure mode, winner is determined by quest completion
        return players.firstOrNull { it.chips > 0 && !it.fold }
    }

    override fun processCustomAction(action: GameActions): GameEvents? {
        return when (action) {
            is GameActions.AdventureActions.AttackMonster ->
                GameEvents.AdventureEvents.MonsterAttacked(action.player, action.damage, 0)
            else -> null
        }
    }
}

class SafariModeLogic : GameModeFactory.GameModeLogic {
    override fun onGameStart(players: List<Player>): PlayingSubState = PlayingSubState.SafariMode("Wild Starter", 0.5, 30)

    override fun onRoundStart(
        roundNumber: Int,
        players: List<Player>,
    ): PlayingSubState {
        val rarity =
            when (roundNumber) {
                1, 2 -> 0.6 // Common
                3, 4 -> 0.4 // Uncommon
                else -> 0.2 // Rare
            }
        return PlayingSubState.SafariMode("Wild Pokemon R$roundNumber", rarity, 30 - (roundNumber * 2))
    }

    override fun onPlayerAction(
        action: GameActions,
        currentState: GameState.Playing,
    ): PlayingSubState? {
        return when (action) {
            is GameActions.Call -> {
                // Calling uses a safari ball
                val currentSub = currentState.subState
                if (currentSub is PlayingSubState.SafariMode) {
                    val newBalls = (currentSub.safariBallsRemaining - 1).coerceAtLeast(0)
                    currentSub.copy(safariBallsRemaining = newBalls)
                } else {
                    null
                }
            }
            else -> null
        }
    }

    override fun calculateWinCondition(players: List<Player>): Player? {
        // Winner has captured the most monsters (simulated via successful calls)
        return players.maxByOrNull { player ->
            // Count successful actions as captures
            if (player.fold) 0 else player.chips / 100 // Rough estimation
        }
    }

    override fun processCustomAction(action: GameActions): GameEvents? {
        return when (action) {
            is GameActions.SafariActions.ThrowSafariBall ->
                GameEvents.SafariEvents.SafariBallThrown(action.player, "standard", 0)
            else -> null
        }
    }
}

class IronmanModeLogic : GameModeFactory.GameModeLogic {
    override fun onGameStart(players: List<Player>): PlayingSubState = PlayingSubState.IronmanMode(0, 1.0)

    override fun onRoundStart(
        roundNumber: Int,
        players: List<Player>,
    ): PlayingSubState {
        val riskLevel = 1.0 + (roundNumber * 0.2)
        return PlayingSubState.IronmanMode(roundNumber * 100, riskLevel, riskLevel > 2.0)
    }

    override fun onPlayerAction(
        action: GameActions,
        currentState: GameState.Playing,
    ): PlayingSubState? {
        return when (action) {
            is GameActions.Raise -> {
                // Aggressive play increases risk level
                val currentSub = currentState.subState
                if (currentSub is PlayingSubState.IronmanMode) {
                    currentSub.copy(
                        riskLevel = currentSub.riskLevel * 1.1,
                        permadeathWarning = currentSub.riskLevel > 1.8,
                    )
                } else {
                    null
                }
            }
            is GameActions.Fold -> {
                // Folding reduces risk
                val currentSub = currentState.subState
                if (currentSub is PlayingSubState.IronmanMode) {
                    currentSub.copy(
                        riskLevel = (currentSub.riskLevel * 0.9).coerceAtLeast(1.0),
                        permadeathWarning = false,
                    )
                } else {
                    null
                }
            }
            else -> null
        }
    }

    override fun calculateWinCondition(players: List<Player>): Player? {
        // In ironman mode, any player reaching 0 chips triggers permadeath
        val alivePlayers = players.filter { it.chips > 0 }
        return when {
            alivePlayers.isEmpty() -> null // Everyone died
            alivePlayers.size == 1 -> alivePlayers.first() // Last survivor
            else -> alivePlayers.maxByOrNull { it.chips } // Richest survivor
        }
    }

    override fun processCustomAction(action: GameActions): GameEvents? {
        return when (action) {
            is GameActions.IronmanActions.PerformGachaPull ->
                GameEvents.IronmanEvents.GachaPullPerformed(action.player, action.pointsSpent, "Mystery Prize", "COMMON")
            else -> null
        }
    }
}
