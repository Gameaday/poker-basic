package com.pokermon

/**
 * Enhanced card logic system following DRY principles - Single source of truth for all card operations.
 * 
 * This object consolidates ALL card-related functionality into one authoritative source,
 * eliminating duplication and providing consistent behavior across the entire application.
 * Used by HandEvaluator, Main, and all other components for unified card handling.
 * 
 * Key Features:
 * - Consistent card number to rank/suit conversion
 * - Unified card display formatting
 * - Deck generation and shuffling
 * - Hand sorting and comparison utilities
 * - Extension functions for modern Kotlin patterns
 * 
 * Card Encoding:
 * - Cards 1-52 representing standard deck
 * - Rank calculation: (card-1) / 4 + 1 (Ace=1, 2-10=face, J=11, Q=12, K=13)
 * - Suit calculation: (card-1) % 4 (Spades=0, Hearts=1, Diamonds=2, Clubs=3)
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 2.0.0 (Enhanced Kotlin-native implementation)
 */
object CardUtils {
    
    // =================================================================
    // CONSTANTS - Single source of truth for card data
    // =================================================================
    
    private val RANK_NAMES = arrayOf(
        "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", 
        "Nine", "Ten", "Jack", "Queen", "King", "Ace"
    )
    
    private val RANK_NAMES_SHORT = arrayOf(
        "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
    )
    
    private val SUIT_NAMES = arrayOf(
        "Spades", "Hearts", "Diamonds", "Clubs"
    )
    
    private val SUIT_SYMBOLS = arrayOf(
        "♠", "♥", "♦", "♣"
    )
    
    private val SUIT_NAMES_SHORT = arrayOf(
        "S", "H", "D", "C"
    )
    
    // Hand type descriptions for legacy compatibility
    private val MULTICARD_NAMES = arrayOf(
        "Error", "High", "Pair", "Three of a kind", "Four of a kind"
    )
    
    // =================================================================
    // CORE CARD CONVERSION FUNCTIONS
    // =================================================================

    
    /**
     * Convert card number (1-52) to rank number (1-13).
     * Ace=1, 2-10=face value, Jack=11, Queen=12, King=13
     * This is the authoritative card-to-rank conversion used throughout the system.
     */
    fun cardToRank(card: Int): Int {
        require(isValidCard(card)) { "Invalid card number: $card" }
        return ((card - 1) / 4) + 1
    }
    
    /**
     * Convert card number (1-52) to suit number (0-3).
     * Spades=0, Hearts=1, Diamonds=2, Clubs=3
     */
    fun cardToSuit(card: Int): Int {
        require(isValidCard(card)) { "Invalid card number: $card" }
        return (card - 1) % 4
    }
    
    /**
     * Convert card number to rank name (e.g., "Ace", "King", "Two").
     * Uses the consistent cardToRank conversion.
     */
    fun cardRank(card: Int): String {
        val rank = cardToRank(card)
        return RANK_NAMES.getOrElse(rank - 1) { "Error" }
    }
    
    /**
     * Convert card number to suit name (e.g., "Spades", "Hearts").
     */
    fun cardSuit(card: Int): String {
        val suit = cardToSuit(card)
        return SUIT_NAMES.getOrElse(suit) { "Error" }
    }
    
    /**
     * Convert rank number directly to rank name.
     * For backward compatibility with existing code.
     */
    fun cardRank2(rank: Int): String {
        return RANK_NAMES.getOrElse(rank - 1) { "Error" }
    }
    
    // =================================================================
    // CARD DISPLAY AND FORMATTING
    // =================================================================
    
    /**
     * Get full card name in format "Rank of Suit" (e.g., "Ace of Spades").
     */
    fun cardName(card: Int): String {
        return "${cardRank(card)} of ${cardSuit(card)}"
    }
    
    /**
     * Get short card name in format "RS" (e.g., "AS" for Ace of Spades).
     */
    fun cardNameShort(card: Int): String {
        val rank = cardToRank(card)
        val suit = cardToSuit(card)
        return "${RANK_NAMES_SHORT[rank - 1]}${SUIT_NAMES_SHORT[suit]}"
    }
    
    /**
     * Get card name with symbol in format "Rank Symbol" (e.g., "A♠").
     */
    fun cardNameSymbol(card: Int): String {
        val rank = cardToRank(card)
        val suit = cardToSuit(card)
        return "${RANK_NAMES_SHORT[rank - 1]}${SUIT_SYMBOLS[suit]}"
    }
    
    /**
     * Format hand as readable string with card names.
     */
    fun formatHand(hand: IntArray): String {
        return hand.joinToString(", ") { cardName(it) }
    }
    
    /**
     * Format hand as short string for compact display.
     */
    fun formatHandShort(hand: IntArray): String {
        return hand.joinToString(" ") { cardNameShort(it) }
    }
    
    /**
     * Format hand with symbols for visual appeal.
     */
    fun formatHandSymbols(hand: IntArray): String {
        return hand.joinToString(" ") { cardNameSymbol(it) }
    }
    
    // =================================================================
    // DECK OPERATIONS
    // =================================================================
    
    /**
     * Create a new shuffled deck of 52 cards.
     */
    fun createShuffledDeck(): IntArray {
        val deck = IntArray(52) { it + 1 }
        deck.shuffle()
        return deck
    }
    
    /**
     * Create an ordered deck (useful for testing).
     */
    fun createOrderedDeck(): IntArray {
        return IntArray(52) { it + 1 }
    }
    
    /**
     * Deal specified number of cards from deck.
     * Returns the dealt cards and removes them from the deck.
     */
    fun dealCards(deck: MutableList<Int>, count: Int): IntArray {
        require(deck.size >= count) { "Not enough cards in deck" }
        
        val dealt = IntArray(count)
        repeat(count) { i ->
            dealt[i] = deck.removeAt(0)
        }
        return dealt
    }
    
    // =================================================================
    // HAND ANALYSIS UTILITIES
    // =================================================================
    
    /**
     * Sort hand by rank (ascending).
     */
    fun sortHand(hand: IntArray): IntArray {
        return hand.sortedBy { cardToRank(it) }.toIntArray()
    }
    
    /**
     * Sort hand by rank (descending).
     */
    fun sortHandDescending(hand: IntArray): IntArray {
        return hand.sortedByDescending { cardToRank(it) }.toIntArray()
    }
    
    /**
     * Get ranks from hand as sorted list.
     */
    fun getHandRanks(hand: IntArray): List<Int> {
        return hand.map { cardToRank(it) }.sorted()
    }
    
    /**
     * Get suits from hand as list.
     */
    fun getHandSuits(hand: IntArray): List<Int> {
        return hand.map { cardToSuit(it) }
    }
    
    /**
     * Check if all cards in hand are same suit.
     */
    fun isFlush(hand: IntArray): Boolean {
        val suits = getHandSuits(hand)
        return suits.all { it == suits[0] }
    }
    
    /**
     * Check if hand contains consecutive ranks.
     */
    fun isStraight(hand: IntArray): Boolean {
        val ranks = getHandRanks(hand).distinct()
        if (ranks.size != 5) return false
        
        // Check regular straight
        val consecutive = (0 until 4).all { ranks[it + 1] - ranks[it] == 1 }
        if (consecutive) return true
        
        // Check ace-low straight (A, 2, 3, 4, 5)
        return ranks == listOf(1, 2, 3, 4, 5)
    }
    
    /**
     * Count occurrences of each rank in hand.
     */
    fun getRankCounts(hand: IntArray): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        hand.forEach { card ->
            val rank = cardToRank(card)
            counts[rank] = counts.getOrDefault(rank, 0) + 1
        }
        return counts
    }
    
    // =================================================================
    // LEGACY COMPATIBILITY FUNCTIONS
    // =================================================================

    
    /**
     * Get multicard name (for hand descriptions) - legacy compatibility.
     */
    fun multicardName(quantity: Int): String {
        return MULTICARD_NAMES.getOrElse(quantity) { "Error" }
    }

    /**
     * Convert hand array to string array using unified card logic - legacy compatibility.
     */
    fun convertHand(hand: Array<IntArray>): Array<String> {
        return Array(hand.size) { i ->
            val title = multicardName(hand[i][1])
            "$title ${cardRank2(hand[i][0])}"
        }
    }

    /**
     * Convert simple card array to full card names - legacy compatibility.
     */
    fun convertCards(cards: IntArray): List<String> {
        return cards.map { cardName(it) }
    }
    
    // =================================================================
    // VALIDATION AND UTILITY FUNCTIONS
    // =================================================================

    /**
     * Validate card number is within valid range (1-52).
     */
    fun isValidCard(card: Int): Boolean {
        return card in 1..52
    }
    
    /**
     * Validate hand contains exactly 5 valid cards.
     */
    fun isValidHand(hand: IntArray): Boolean {
        return hand.size == 5 && hand.all { isValidCard(it) } && hand.distinct().size == 5
    }

    /**
     * Get all available rank names.
     */
    fun getAllRanks(): Array<String> = RANK_NAMES.copyOf()

    /**
     * Get all available suit names.
     */
    fun getAllSuits(): Array<String> = SUIT_NAMES.copyOf()

    /**
     * Get all multicard names.
     */
    fun getAllMulticardNames(): Array<String> = MULTICARD_NAMES.copyOf()
    
    // =================================================================
    // KOTLIN EXTENSION FUNCTIONS - Modern patterns
    // =================================================================

    /**
     * Extension function to enhance card-related collections.
     */
    fun List<Int>.toCardNames(): List<String> = map { cardName(it) }
    
    /**
     * Extension function to get short card names.
     */
    fun List<Int>.toCardNamesShort(): List<String> = map { cardNameShort(it) }
    
    /**
     * Extension function to get symbolic card names.
     */
    fun List<Int>.toCardNamesSymbol(): List<String> = map { cardNameSymbol(it) }
    
    /**
     * Extension function to sort cards by rank.
     */
    fun IntArray.sortByRank(): IntArray = sortHand(this)
}