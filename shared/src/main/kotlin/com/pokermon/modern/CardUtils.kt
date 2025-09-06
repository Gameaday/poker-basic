package com.pokermon.modern

/**
 * CardUtils - Single authoritative source for all card logic operations.
 *
 * Consolidates card-related functionality to follow DRY principles, providing
 * a unified API for card operations across all game modes and platforms.
 *
 * This object serves as the single source of truth for:
 * - Card encoding/decoding (rank and suit extraction)
 * - Card display and formatting
 * - Card validation and comparison
 * - Hand analysis support functions
 *
 * Replaces scattered card logic throughout the codebase with a centralized,
 * null-safe, and Kotlin-native implementation.
 *
 * @author Carl Nelson (@Gameaday)
 * @version Dynamic (Kotlin-native implementation)
 */
object CardUtils {
    // Card constants - single source of truth
    private val CARD_RANKS =
        arrayOf(
            "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
            "Nine", "Ten", "Jack", "Queen", "King", "Ace",
        )

    private val CARD_SUITS = arrayOf("Clubs", "Diamonds", "Hearts", "Spades")

    // Game constants
    const val DECK_SIZE = 52
    const val DEFAULT_HAND_SIZE = 5
    const val MAX_MULTIPLES_ARRAY_SIZE = 3

    // Chip amounts and player names
    val VALID_CHIPS = intArrayOf(100, 500, 1000, 2500)

    val POSSIBLE_NAMES =
        arrayOf(
            "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
            "Tony", "Jenny", "Susen", "Rory", "Melody",
            "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
            "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
            "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
            "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
            "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
            "Smallie", "Anthony", "Gwen",
        )

    val HAND_TYPE_NAMES =
        arrayOf(
            "Error",
            "High",
            "Pair",
            "Three of a kind",
            "Four of a kind",
        )

    // Short rank names for compact display
    private val SHORT_RANKS =
        arrayOf(
            "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A",
        )

    // Unicode suit symbols for enhanced display
    private val SUIT_SYMBOLS =
        mapOf(
            "Clubs" to "♣",
            "Diamonds" to "♦",
            "Hearts" to "♥",
            "Spades" to "♠",
        )

    /**
     * Get card rank from encoded card value (1-52 encoding).
     * Uses the same logic as legacy CardUtils for compatibility.
     */
    fun cardRank(card: Int): Int {
        if (card < 1 || card > 52) {
            throw IllegalArgumentException("Invalid card value: $card (must be 1-52)")
        }
        return ((card - 1) / 4) + 1 // Ace=1, 2-10=face, J=11, Q=12, K=13
    }

    /**
     * Get card suit from encoded card value (1-52 encoding).
     * Uses the same logic as legacy CardUtils for compatibility.
     */
    fun cardSuit(card: Int): Int {
        if (card < 1 || card > 52) {
            throw IllegalArgumentException("Invalid card value: $card (must be 1-52)")
        }
        return (card - 1) % 4 // Spades=0, Hearts=1, Diamonds=2, Clubs=3
    }

    /**
     * Get rank name from card value (1-52 encoding).
     */
    fun rankName(card: Int): String {
        val rank = cardRank(card) // Will throw if invalid
        return CARD_RANKS[rank - 1] // Adjust for 1-based rank
    }

    /**
     * Get suit name from card value (1-52 encoding).
     */
    fun suitName(card: Int): String {
        val suit = cardSuit(card) // Will throw if invalid
        return CARD_SUITS[suit]
    }

    /**
     * Get short rank name for compact display (1-52 encoding).
     */
    fun shortRankName(card: Int): String {
        val rank = cardRank(card) // Will throw if invalid
        return SHORT_RANKS[rank - 1] // Adjust for 1-based rank
    }

    /**
     * Get suit symbol for enhanced display.
     */
    fun suitSymbol(card: Int): String {
        val suitName = suitName(card)
        return SUIT_SYMBOLS[suitName] ?: suitName
    }

    /**
     * Get full card name (e.g., "Ace of Spades").
     */
    fun cardName(card: Int): String {
        return "${rankName(card)} of ${suitName(card)}"
    }

    /**
     * Get compact card representation (e.g., "A♠").
     */
    fun compactCardName(card: Int): String {
        return "${shortRankName(card)}${suitSymbol(card)}"
    }

    /**
     * Validate card value is within valid range (1-52 encoding).
     */
    fun isValidCard(card: Int): Boolean {
        return card in 1..52
    }

    /**
     * Compare two cards by rank (for sorting).
     */
    fun compareByRank(
        card1: Int,
        card2: Int,
    ): Int {
        return cardRank(card1).compareTo(cardRank(card2))
    }

    /**
     * Compare two cards by suit (for sorting).
     */
    fun compareBySuit(
        card1: Int,
        card2: Int,
    ): Int {
        return cardSuit(card1).compareTo(cardSuit(card2))
    }

    /**
     * Get numeric rank value for calculations (Ace high = 12).
     */
    fun numericRank(card: Int): Int {
        return cardRank(card)
    }

    /**
     * Get numeric rank value with Ace high option.
     */
    fun numericRank(
        card: Int,
        aceHigh: Boolean,
    ): Int {
        val rank = cardRank(card)
        return if (aceHigh && rank == 12) 13 else rank // Ace = 12 normally, 13 if ace high
    }

    /**
     * Check if two cards have the same rank.
     */
    fun sameRank(
        card1: Int,
        card2: Int,
    ): Boolean {
        return cardRank(card1) == cardRank(card2)
    }

    /**
     * Check if two cards have the same suit.
     */
    fun sameSuit(
        card1: Int,
        card2: Int,
    ): Boolean {
        return cardSuit(card1) == cardSuit(card2)
    }

    /**
     * Get all possible ranks as list.
     */
    fun getAllRanks(): List<String> = CARD_RANKS.toList()

    /**
     * Get all possible suits as list.
     */
    fun getAllSuits(): List<String> = CARD_SUITS.toList()

    /**
     * Format hand for display with various options.
     */
    fun formatHand(
        hand: IntArray,
        compact: Boolean = false,
    ): String {
        return hand.joinToString(", ") { card ->
            if (compact) compactCardName(card) else cardName(card)
        }
    }

    /**
     * Sort hand by rank.
     */
    fun sortByRank(hand: IntArray): IntArray {
        return hand.sortedWith { a, b -> compareByRank(a, b) }.toIntArray()
    }

    /**
     * Sort hand by suit.
     */
    fun sortBySuit(hand: IntArray): IntArray {
        return hand.sortedWith { a, b -> compareBySuit(a, b) }.toIntArray()
    }

    /**
     * Get unique ranks in hand.
     */
    fun getUniqueRanks(hand: IntArray): Set<Int> {
        return hand.map { cardRank(it) }.toSet()
    }

    /**
     * Get unique suits in hand.
     */
    fun getUniqueSuits(hand: IntArray): Set<Int> {
        return hand.map { cardSuit(it) }.toSet()
    }

    /**
     * Count cards of specific rank in hand.
     */
    fun countRank(
        hand: IntArray,
        rank: Int,
    ): Int {
        return hand.count { cardRank(it) == rank }
    }

    /**
     * Count cards of specific suit in hand.
     */
    fun countSuit(
        hand: IntArray,
        suit: Int,
    ): Int {
        return hand.count { cardSuit(it) == suit }
    }

    // =================================================================
    // DECK AND HAND OPERATIONS
    // =================================================================

    /**
     * Convert hand multiples to string array format.
     * Used by Player for converting hand analysis results.
     */
    fun convertHand(multiples: Array<IntArray>): Array<String> {
        return multiples.map { (rank, count) ->
            "${CARD_RANKS.getOrElse(rank) { "Unknown" }} ($count)"
        }.toTypedArray()
    }

    /**
     * Creates a standard 52-card deck (1-52 encoding).
     * @return array representing the deck
     */
    fun createDeck(): IntArray {
        return IntArray(52) { it + 1 }
    }

    /**
     * Create a new shuffled deck of 52 cards (1-52 encoding for legacy compatibility).
     */
    fun createShuffledDeck(): IntArray {
        val deck = IntArray(52) { it + 1 }
        deck.shuffle()
        return deck
    }

    /**
     * Deal specified number of cards from deck.
     * Returns the dealt cards and removes them from the deck.
     */
    fun dealCards(
        deck: MutableList<Int>,
        count: Int,
    ): IntArray {
        require(deck.size >= count) { "Not enough cards in deck" }

        val dealt = IntArray(count)
        repeat(count) { i ->
            dealt[i] = deck.removeAt(0)
        }
        return dealt
    }

    /**
     * Format hand with symbols for visual appeal (legacy compatibility).
     */
    fun formatHandSymbols(hand: IntArray): String {
        return hand.joinToString(" ") { compactCardName(it) }
    }

    /**
     * Shuffles a deck using Fisher-Yates algorithm.
     * @param deck the deck to shuffle
     */
    fun shuffleDeck(deck: IntArray) {
        for (i in deck.size - 1 downTo 1) {
            val j = kotlin.random.Random.nextInt(i + 1)
            val temp = deck[i]
            deck[i] = deck[j]
            deck[j] = temp
        }
    }

    /**
     * Validates if a chip amount is in the list of valid amounts.
     * @param chips the chip amount to validate
     * @return true if valid, false otherwise
     */
    fun isValidChipAmount(chips: Int): Boolean {
        return chips in VALID_CHIPS
    }

    /**
     * Gets the closest valid chip amount to the given amount.
     * @param chips the desired chip amount
     * @return the closest valid chip amount
     */
    fun getClosestValidChipAmount(chips: Int): Int {
        return VALID_CHIPS.minByOrNull { kotlin.math.abs(chips - it) } ?: VALID_CHIPS[0]
    }

    /**
     * Generates a random AI player name that's not already in use.
     * @param usedNames set of names already in use
     * @return a unique AI player name
     */
    fun generateAIPlayerName(usedNames: Set<String>): String {
        val availableNames = POSSIBLE_NAMES.filter { it !in usedNames }
        return if (availableNames.isNotEmpty()) {
            availableNames.random()
        } else {
            // Fallback to numbered names if all are taken
            "AI Player ${usedNames.size + 1}"
        }
    }

    /**
     * Formats a chip amount for display.
     * @param chips the chip amount
     * @return formatted string
     */
    fun formatChips(chips: Int): String {
        return when {
            chips >= 1_000_000 -> String.format("%.1fM", chips / 1_000_000.0)
            chips >= 1_000 -> String.format("%.1fK", chips / 1_000.0)
            else -> chips.toString()
        }
    }

    /**
     * Gets a description of a hand type based on hand value.
     * @param handValue the numerical hand value
     * @return description of the hand type
     */
    fun getHandDescription(handValue: Int): String {
        return when {
            handValue >= 10000 -> "Royal Flush"
            handValue >= 9000 -> "Straight Flush"
            handValue >= 8000 -> "Four of a Kind"
            handValue >= 7000 -> "Full House"
            handValue >= 6000 -> "Flush"
            handValue >= 5000 -> "Straight"
            handValue >= 4000 -> "Three of a Kind"
            handValue >= 3000 -> "Two Pair"
            handValue >= 2000 -> "Pair"
            else -> "High Card"
        }
    }

    /**
     * Enhanced hand analysis that provides both score and description.
     * Returns a pair of (handValue, handDescription) for efficiency.
     */
    fun analyzeHand(handValue: Int): Pair<Int, String> {
        return handValue to getHandDescription(handValue)
    }
}
