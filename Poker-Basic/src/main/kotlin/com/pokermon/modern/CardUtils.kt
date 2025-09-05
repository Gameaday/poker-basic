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
    private val CARD_RANKS = arrayOf(
        "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
        "Nine", "Ten", "Jack", "Queen", "King", "Ace"
    )
    
    private val CARD_SUITS = arrayOf("Clubs", "Diamonds", "Hearts", "Spades")
    
    // Short rank names for compact display
    private val SHORT_RANKS = arrayOf(
        "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
    )
    
    // Unicode suit symbols for enhanced display
    private val SUIT_SYMBOLS = mapOf(
        "Clubs" to "♣",
        "Diamonds" to "♦", 
        "Hearts" to "♥",
        "Spades" to "♠"
    )
    
    /**
     * Get card rank from encoded card value.
     * Uses the same logic as original Main.cardRank for compatibility.
     */
    fun cardRank(card: Int): Int {
        return card % 13
    }
    
    /**
     * Get card suit from encoded card value.
     * Uses the same logic as original Main.cardSuit for compatibility.
     */
    fun cardSuit(card: Int): Int {
        return card / 13
    }
    
    /**
     * Get rank name from card value.
     */
    fun rankName(card: Int): String {
        val rank = cardRank(card)
        return if (rank in CARD_RANKS.indices) CARD_RANKS[rank] else "Unknown"
    }
    
    /**
     * Get suit name from card value.
     */
    fun suitName(card: Int): String {
        val suit = cardSuit(card)
        return if (suit in CARD_SUITS.indices) CARD_SUITS[suit] else "Unknown"
    }
    
    /**
     * Get short rank name for compact display.
     */
    fun shortRankName(card: Int): String {
        val rank = cardRank(card)
        return if (rank in SHORT_RANKS.indices) SHORT_RANKS[rank] else "?"
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
     * Validate card value is within valid range.
     */
    fun isValidCard(card: Int): Boolean {
        return card in 0..51
    }
    
    /**
     * Compare two cards by rank (for sorting).
     */
    fun compareByRank(card1: Int, card2: Int): Int {
        return cardRank(card1).compareTo(cardRank(card2))
    }
    
    /**
     * Compare two cards by suit (for sorting).
     */
    fun compareBySuit(card1: Int, card2: Int): Int {
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
    fun numericRank(card: Int, aceHigh: Boolean): Int {
        val rank = cardRank(card)
        return if (aceHigh && rank == 12) 13 else rank // Ace = 12 normally, 13 if ace high
    }
    
    /**
     * Check if two cards have the same rank.
     */
    fun sameRank(card1: Int, card2: Int): Boolean {
        return cardRank(card1) == cardRank(card2)
    }
    
    /**
     * Check if two cards have the same suit.
     */
    fun sameSuit(card1: Int, card2: Int): Boolean {
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
    fun formatHand(hand: IntArray, compact: Boolean = false): String {
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
    fun countRank(hand: IntArray, rank: Int): Int {
        return hand.count { cardRank(it) == rank }
    }
    
    /**
     * Count cards of specific suit in hand.
     */
    fun countSuit(hand: IntArray, suit: Int): Int {
        return hand.count { cardSuit(it) == suit }
    }
    
    // =================================================================
    // JAVA COMPATIBILITY METHODS FOR MIGRATION PERIOD
    // =================================================================
    
    /**
     * Java-compatible static access for cardRank.
     */
    @JvmStatic
    fun getCardRank(card: Int): Int = cardRank(card)
    
    /**
     * Java-compatible static access for cardSuit.
     */
    @JvmStatic
    fun getCardSuit(card: Int): Int = cardSuit(card)
    
    /**
     * Java-compatible static access for card name.
     */
    @JvmStatic
    fun getCardName(card: Int): String = cardName(card)
}