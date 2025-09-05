package com.pokermon.modern

import com.pokermon.Player
import com.pokermon.GameMode
import com.pokermon.GamePhase

/**
 * Kotlin extension functions to enhance existing Java classes with modern Kotlin idioms.
 * These extensions provide additional functionality without modifying the original classes,
 * demonstrating Kotlin's powerful extension mechanism.
 * 
 * @author Carl Nelson (@Gameaday) 
 * @version 1.0.0 - Kotlin-native enhancements
 */

// Player Extensions - Add Kotlin-style functionality to Java Player class
/**
 * Checks if the player can afford a specific bet amount.
 * @param amount the bet amount to check
 * @return true if the player has sufficient chips
 */
fun Player.canAfford(amount: Int): Boolean = this.chips >= amount

/**
 * Checks if the player is active (not folded and has chips).
 * @return true if the player can continue playing
 */
fun Player.isActive(): Boolean = !this.isFold() && this.chips > 0

/**
 * Gets the player's status as a descriptive string.
 * @return formatted status string
 */
fun Player.getStatus(): String = when {
    this.isFold() -> "Folded"
    this.chips <= 0 -> "Out of chips"
    this.isHuman() -> "Human Player"
    else -> "AI Player"
}

/**
 * Safely gets the player's current bet with null safety.
 * @return the bet amount or 0 if unavailable
 */
fun Player.getBetSafely(): Int = try { this.bet } catch (e: Exception) { 0 }

/**
 * Gets a formatted display of the player's hand information.
 * @return formatted hand display string
 */
fun Player.getHandDisplay(): String = buildString {
    appendLine("Player: ${name}")
    appendLine("Chips: ${chips}")
    hand?.let { 
        appendLine("Cards: ${it.contentToString()}")
    }
    convertedHand?.let {
        appendLine("Hand: ${it.contentToString()}")
    }
    if (handValue > 0) {
        appendLine("Hand Value: ${handValue}")
    }
}

// Array<Player> Extensions - Enhance player collections
/**
 * Gets all active players (not folded, have chips).
 * @return list of active players
 */
fun Array<Player>.getActivePlayers(): List<Player> = 
    this.filter { it.isActive() }

/**
 * Gets the player with the highest hand value.
 * @return player with best hand, or null if none found
 */
fun Array<Player>.getBestHand(): Player? = 
    this.filter { !it.isFold() }.maxByOrNull { it.handValue }

/**
 * Gets the total pot from all player bets.
 * @return sum of all player bets
 */
fun Array<Player>.getTotalPot(): Int = 
    this.sumOf { it.getBetSafely() }

/**
 * Checks if only one player remains active.
 * @return true if exactly one player is still active
 */
fun Array<Player>.hasWinner(): Boolean = 
    this.count { it.isActive() } == 1

// String Extensions - Enhanced string handling for game data
/**
 * Converts a string to title case for display.
 * @return title-cased string
 */
fun String.toTitleCase(): String = 
    this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.titlecase() }
    }

/**
 * Safely converts string to integer with default value.
 * @param defaultValue value to return if conversion fails
 * @return parsed integer or default value
 */
fun String.toIntSafely(defaultValue: Int = 0): Int = 
    this.toIntOrNull() ?: defaultValue

// IntArray Extensions - Enhanced array operations for game logic
/**
 * Checks if the hand array represents a valid poker hand.
 * @return true if hand has exactly 5 cards
 */
fun IntArray.isValidHand(): Boolean = this.size == 5 && this.all { it > 0 }

/**
 * Gets the sorted hand for consistent comparison.
 * @return new sorted array
 */
fun IntArray.getSorted(): IntArray = this.sortedArray()

/**
 * Checks if the hand contains a specific card value.
 * @param cardValue the card value to search for
 * @return true if card is found
 */
fun IntArray.containsCard(cardValue: Int): Boolean = this.contains(cardValue)

// Collection Extensions - General utility functions
/**
 * Safely gets an element at index with null safety.
 * @param index the index to access
 * @return element at index or null if out of bounds
 */
fun <T> List<T>.getOrNull(index: Int): T? = 
    if (index in 0 until size) this[index] else null

/**
 * Chunks a list into smaller sublists of specified size.
 * @param size the maximum size of each chunk
 * @return list of sublists
 */
fun <T> List<T>.chunked(size: Int): List<List<T>> {
    if (size <= 0) throw IllegalArgumentException("Chunk size must be positive")
    return this.windowed(size, size, true)
}

// GameMode Extensions - Enhanced enum functionality
/**
 * Checks if the game mode requires special UI elements.
 * @return true if mode needs additional UI components
 */
fun GameMode.requiresSpecialUI(): Boolean = this.hasMonsters()

/**
 * Gets the recommended number of AI opponents for this mode.
 * @return optimal number of AI players
 */
fun GameMode.getRecommendedAICount(): Int = when (this) {
    GameMode.CLASSIC -> 3
    GameMode.ADVENTURE -> 1
    GameMode.SAFARI -> 2
    GameMode.IRONMAN -> 3
}

// GamePhase Extensions - Enhanced phase management
/**
 * Checks if this phase allows player interaction.
 * @return true if player can interact during this phase
 */
fun GamePhase.allowsInteraction(): Boolean = 
    this.allowsBetting() || this.allowsCardExchange() || this.allowsRoundProgression()

/**
 * Gets the estimated duration for this phase in seconds.
 * @return estimated time in seconds
 */
fun GamePhase.getEstimatedDuration(): Int = when (this) {
    GamePhase.INITIALIZATION, GamePhase.DECK_CREATION -> 1
    GamePhase.PLAYER_SETUP -> 5
    GamePhase.HAND_DEALING, GamePhase.HAND_EVALUATION -> 2
    GamePhase.BETTING_ROUND, GamePhase.FINAL_BETTING -> 30
    GamePhase.PLAYER_ACTIONS -> 60
    GamePhase.CARD_EXCHANGE -> 45
    GamePhase.WINNER_DETERMINATION, GamePhase.POT_DISTRIBUTION -> 3
    GamePhase.ROUND_END -> 10
    GamePhase.GAME_END -> 5
    else -> 10
}

// Utility Extensions - General purpose helpers
/**
 * Safely executes a block and returns result or default value on exception.
 * @param defaultValue value to return if block throws exception
 * @param block the code block to execute
 * @return result of block or default value
 */
inline fun <T> safeExecute(defaultValue: T, block: () -> T): T = try {
    block()
} catch (e: Exception) {
    defaultValue
}

/**
 * Measures execution time of a block in milliseconds.
 * @param block the code block to time
 * @return pair of (result, execution time in ms)
 */
inline fun <T> measureTimeMillis(block: () -> T): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result = block()
    val endTime = System.currentTimeMillis()
    return result to (endTime - startTime)
}

/**
 * Retries a block of code up to maxAttempts times.
 * @param maxAttempts maximum number of retry attempts
 * @param delayMs delay between attempts in milliseconds
 * @param block the code block to retry
 * @return result of successful execution
 * @throws Exception if all attempts fail
 */
inline fun <T> retry(maxAttempts: Int = 3, delayMs: Long = 1000, block: () -> T): T {
    repeat(maxAttempts - 1) { _ ->
        try {
            return block()
        } catch (e: Exception) {
            Thread.sleep(delayMs)
        }
    }
    return block() // Final attempt without catching exception
}