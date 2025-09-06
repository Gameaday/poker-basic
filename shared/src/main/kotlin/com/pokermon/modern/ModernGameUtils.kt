package com.pokermon.modern

import com.pokermon.GameMode
import com.pokermon.GamePhase
import com.pokermon.players.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Modern Kotlin utilities demonstrating advanced language features for the Pokermon game.
 * This class showcases Kotlin's powerful capabilities without breaking existing functionality.
 *
 * Features demonstrated:
 * - Sealed classes for type-safe state management
 * - Data classes for immutable state
 * - Coroutines and Flow for async operations
 * - Scope functions and higher-order functions
 * - Type-safe builders and DSL patterns
 *
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0 - Kotlin-native architecture
 */

// Sealed Classes - Type-safe game events
sealed class GameEvent {
    data class PlayerJoined(val player: Player) : GameEvent()

    data class PlayerLeft(val playerId: String) : GameEvent()

    data class BetPlaced(val playerId: String, val amount: Int) : GameEvent()

    data class CardDealt(val playerId: String, val cardCount: Int) : GameEvent()

    data class HandEvaluated(val playerId: String, val handValue: Int) : GameEvent()

    data class RoundEnded(val winnerId: String, val potAmount: Int) : GameEvent()

    object GameStarted : GameEvent()

    object GameEnded : GameEvent()
}

// Data Classes - Immutable game state
data class GameState(
    val currentPhase: GamePhase = GamePhase.INITIALIZATION,
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val pot: Int = 0,
    val roundNumber: Int = 1,
    val gameMode: GameMode = GameMode.CLASSIC,
    val isActive: Boolean = false,
) {
    // Computed properties using Kotlin's powerful syntax
    val activePlayers: List<Player> get() = players.filter { it.isActive() }
    val totalBets: Int get() = players.sumOf { it.getBetSafely() }
    val isGameEnded: Boolean get() = activePlayers.size <= 1
    val currentPlayer: Player? get() = players.getOrNull(currentPlayerIndex)

    // Copy methods for immutable updates
    fun withPhase(newPhase: GamePhase): GameState = copy(currentPhase = newPhase)

    fun withPot(newPot: Int): GameState = copy(pot = newPot)

    fun nextPlayer(): GameState = copy(currentPlayerIndex = (currentPlayerIndex + 1) % players.size)

    fun addToPot(amount: Int): GameState = copy(pot = pot + amount)
}

// Result wrapper for operations that can fail
sealed class GameResult<out T> {
    data class Success<T>(val data: T) : GameResult<T>()

    data class Error(val message: String, val cause: Throwable? = null) : GameResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): GameResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String) -> Unit): GameResult<T> {
        if (this is Error) action(message)
        return this
    }

    inline fun <R> map(transform: (T) -> R): GameResult<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
}

/**
 * Modern Kotlin game manager with coroutines and Flow support.
 */
class ModernGameManager {
    // StateFlow for reactive state management
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // SharedFlow for broadcasting game events
    private val _gameEvents = MutableSharedFlow<GameEvent>()
    val gameEvents: SharedFlow<GameEvent> = _gameEvents.asSharedFlow()

    // Coroutine scope for async operations
    private val gameScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Starts a new game asynchronously.
     * Demonstrates coroutines for non-blocking operations.
     */
    suspend fun startGame(
        players: List<Player>,
        mode: GameMode,
    ): GameResult<GameState> =
        withContext(Dispatchers.Default) {
            try {
                val newState =
                    GameState(
                        currentPhase = GamePhase.PLAYER_SETUP,
                        players = players,
                        gameMode = mode,
                        isActive = true,
                    )

                _gameState.value = newState
                _gameEvents.emit(GameEvent.GameStarted)

                GameResult.Success(newState)
            } catch (e: Exception) {
                GameResult.Error("Failed to start game", e)
            }
        }

    /**
     * Processes a player bet with validation.
     * Demonstrates functional error handling.
     */
    fun placeBet(
        playerId: String,
        amount: Int,
    ): GameResult<GameState> {
        val currentState = _gameState.value
        val player =
            currentState.players.find { it.name == playerId }
                ?: return GameResult.Error("Player not found: $playerId")

        return when {
            !player.canAfford(amount) -> GameResult.Error("Insufficient chips")
            amount <= 0 -> GameResult.Error("Bet amount must be positive")
            !currentState.currentPhase.allowsBetting() -> GameResult.Error("Betting not allowed in current phase")
            else -> {
                player.placeBet(amount)
                val newState = currentState.addToPot(amount)
                _gameState.value = newState

                // Emit event asynchronously
                gameScope.launch {
                    _gameEvents.emit(GameEvent.BetPlaced(playerId, amount))
                }

                GameResult.Success(newState)
            }
        }
    }

    /**
     * Advances to the next game phase.
     * Demonstrates when expressions and state transitions.
     */
    suspend fun advancePhase(): GameResult<GameState> =
        withContext(Dispatchers.Default) {
            val currentState = _gameState.value
            val nextPhase =
                currentState.currentPhase.getNextPhase()
                    ?: return@withContext GameResult.Error("Game has ended")

            val newState = currentState.withPhase(nextPhase)
            _gameState.value = newState

            // Perform phase-specific logic
            when (nextPhase) {
                GamePhase.HAND_DEALING -> dealCards()
                GamePhase.WINNER_DETERMINATION -> determineWinner()
                GamePhase.GAME_END -> endGame()
                else -> { /* No special action needed */ }
            }

            GameResult.Success(newState)
        }

    /**
     * Observes game events with filtering.
     * Demonstrates Flow operators and reactive programming.
     */
    fun observeBettingEvents(): Flow<GameEvent.BetPlaced> = gameEvents.filterIsInstance<GameEvent.BetPlaced>()

    /**
     * Gets game statistics as a Flow.
     * Demonstrates Flow transformation and computation.
     */
    fun getGameStatistics(): Flow<Map<String, Any>> =
        gameState.map { state ->
            mapOf(
                "round" to state.roundNumber,
                "activePlayers" to state.activePlayers.size,
                "totalPot" to state.pot,
                "currentPhase" to state.currentPhase.displayName,
                "gameMode" to state.gameMode.displayName,
            )
        }

    // Private helper methods
    private suspend fun dealCards() {
        val state = _gameState.value
        state.players.forEach { player ->
            _gameEvents.emit(GameEvent.CardDealt(player.name ?: "Unknown", 5))
        }
    }

    private suspend fun determineWinner() {
        val state = _gameState.value
        val winner = state.activePlayers.maxByOrNull { it.handValue }
        winner?.let {
            _gameEvents.emit(GameEvent.RoundEnded(it.name ?: "Unknown", state.pot))
        }
    }

    private suspend fun endGame() {
        _gameEvents.emit(GameEvent.GameEnded)
        gameScope.cancel() // Clean up coroutines
    }

    /**
     * Cleans up resources.
     * Important for proper coroutine lifecycle management.
     */
    fun cleanup() {
        gameScope.cancel()
    }
}

/**
 * DSL for building game configurations.
 * Demonstrates Kotlin's DSL capabilities.
 */
@DslMarker
annotation class GameDsl

@GameDsl
class GameConfigBuilder {
    var mode: GameMode = GameMode.CLASSIC
    var maxPlayers: Int = 4
    var startingChips: Int = 1000
    var autoSave: Boolean = true
    private val players = mutableListOf<Player>()

    @GameDsl
    fun player(
        name: String,
        isHuman: Boolean = false,
        chips: Int = startingChips,
    ) {
        players.add(
            Player().apply {
                setPlayerName(name)
                this.isHuman = isHuman
                setChipsCurrent(chips)
            },
        )
    }

    fun build(): Pair<List<Player>, GameMode> = players.toList() to mode
}

/**
 * DSL function for creating game configurations.
 * Usage: gameConfig { mode = GameMode.ADVENTURE; player("Alice", true) }
 */
fun gameConfig(init: GameConfigBuilder.() -> Unit): Pair<List<Player>, GameMode> {
    return GameConfigBuilder().apply(init).build()
}

/**
 * Utility object for game analysis using functional programming.
 */
object GameAnalytics {
    /**
     * Analyzes player performance using functional operations.
     */
    fun analyzePlayerPerformance(events: List<GameEvent>): Map<String, Map<String, Any>> {
        return events
            .filterIsInstance<GameEvent.BetPlaced>()
            .groupBy { it.playerId }
            .mapValues { (_, bets) ->
                mapOf(
                    "totalBets" to bets.sumOf { it.amount },
                    "betCount" to bets.size,
                    "averageBet" to if (bets.isNotEmpty()) bets.sumOf { it.amount } / bets.size else 0,
                    "maxBet" to (bets.maxOfOrNull { it.amount } ?: 0),
                    "minBet" to (bets.minOfOrNull { it.amount } ?: 0),
                )
            }
    }

    /**
     * Calculates game duration from events.
     */
    fun calculateGameDuration(events: List<GameEvent>): Long {
        val start = events.find { it is GameEvent.GameStarted }
        val end = events.find { it is GameEvent.GameEnded }
        return if (start != null && end != null) {
            // In a real implementation, events would have timestamps
            System.currentTimeMillis() // Placeholder
        } else {
            0L
        }
    }

    /**
     * Finds the most active players using functional composition.
     */
    fun findMostActivePlayer(events: List<GameEvent>): String? {
        return events
            .filterIsInstance<GameEvent.BetPlaced>()
            .groupingBy { it.playerId }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
}

/**
 * Demonstration of higher-order functions for game logic.
 */
object GameFunctions {
    /**
     * Generic validation function that can be customized.
     */
    fun <T> validateWith(
        value: T,
        vararg validators: (T) -> String?,
    ): List<String> {
        return validators.mapNotNull { it(value) }
    }

    /**
     * Player validators using function composition.
     */
    val playerNameValidator: (Player) -> String? = { player ->
        when {
            player.name?.isBlank() != false -> "Player name cannot be empty"
            player.name?.length ?: 0 > 20 -> "Player name too long"
            else -> null
        }
    }

    val playerChipsValidator: (Player) -> String? = { player ->
        when {
            player.chips < 0 -> "Player chips cannot be negative"
            player.chips > 100000 -> "Player chips exceed maximum"
            else -> null
        }
    }

    /**
     * Validates a player using composed validators.
     */
    fun validatePlayer(player: Player): List<String> {
        return validateWith(player, playerNameValidator, playerChipsValidator)
    }

    /**
     * Function that returns a function (closure example).
     */
    fun createBettingStrategy(riskLevel: Double): (Player, Int) -> Boolean {
        return { player, betAmount ->
            val ratio = betAmount.toDouble() / player.chips
            ratio <= riskLevel
        }
    }
}

/**
 * Example usage and integration point for the modern Kotlin features.
 */
object ModernGameExample {
    /**
     * Demonstrates how to use the modern Kotlin utilities with existing code.
     */
    suspend fun runModernGameExample() {
        val gameManager = ModernGameManager()

        try {
            // Use DSL to configure game
            val (players, mode) =
                gameConfig {
                    mode = GameMode.ADVENTURE
                    player("Alice", isHuman = true, chips = 1000)
                    player("Bob", isHuman = false, chips = 1000)
                    player("Charlie", isHuman = false, chips = 1000)
                }

            // Start game with coroutines
            gameManager.startGame(players, mode)
                .onSuccess { state ->
                    println("Game started with ${state.players.size} players")
                }
                .onError { error ->
                    println("Failed to start game: $error")
                }

            // Observe events reactively
            gameManager.observeBettingEvents().collect { betEvent ->
                println("${betEvent.playerId} bet ${betEvent.amount} chips")
            }
        } finally {
            gameManager.cleanup()
        }
    }
}
