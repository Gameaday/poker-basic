package com.pokermon

/**
 * Unified card logic helper using DRY principles.
 * Consolidates all card-related operations into a single authoritative source.
 * 
 * This replaces the scattered card logic from Main.java with a clean Kotlin implementation
 * that serves as the single source of truth for card operations across all platforms.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object CardUtils {
    
    // Single source of truth for card data - follows DRY principles
    private val CARD_RANKS = arrayOf(
        "error", "Ace", "King", "Queen",
        "Jack", "Ten", "Nine", "Eight", 
        "Seven", "Six", "Five", "Four",
        "Three", "Two"
    )
    
    private val CARD_RANKS_EXTENDED = arrayOf(
        "error", "Ace", "King", "Queen",
        "Jack", "Ten", "Nine", "Eight",
        "Seven", "Six", "Five", "Four", 
        "Three", "Two", "One"
    )
    
    private val CARD_SUITS = arrayOf(
        "Spades", "Hearts", "Diamonds", "Clubs"
    )
    
    private val MULTICARD_NAMES = arrayOf(
        "Error", "High", "Pair", "Three of a kind", "Four of a kind"
    )

    /**
     * Convert card number to rank name.
     * Unified implementation that matches the original Main.cardRank logic.
     */
    fun cardRank(card: Int): String {
        var rank = card / 4
        if (card % 4 != 0) {
            rank++
        }
        return if (rank in CARD_RANKS.indices) CARD_RANKS[rank] else "error"
    }

    /**
     * Convert rank number directly to rank name.
     * Uses extended rank array for additional flexibility.
     */
    fun cardRank2(rank: Int): String {
        return if (rank in CARD_RANKS_EXTENDED.indices) CARD_RANKS_EXTENDED[rank] else "error"
    }

    /**
     * Convert card number to suit name.
     */
    fun cardSuit(card: Int): String {
        val suit = card % 4
        return if (suit in CARD_SUITS.indices) CARD_SUITS[suit] else "error"
    }

    /**
     * Get full card name in format "Rank of Suit".
     * This is the standard format used throughout the game.
     */
    fun cardName(card: Int): String {
        return "${cardRank(card)} of ${cardSuit(card)}"
    }

    /**
     * Get multicard name (for hand descriptions).
     */
    fun multicardName(quantity: Int): String {
        return if (quantity in MULTICARD_NAMES.indices) MULTICARD_NAMES[quantity] else "Error"
    }

    /**
     * Convert hand array to string array using unified card logic.
     * Replaces Main.convertHand2 with DRY implementation.
     */
    fun convertHand(hand: Array<IntArray>): Array<String> {
        return Array(hand.size) { i ->
            val title = multicardName(hand[i][1])
            "$title ${cardRank2(hand[i][0])}"
        }
    }

    /**
     * Convert simple card array to full card names.
     * Utility for converting integer card representations to human-readable format.
     */
    fun convertCards(cards: IntArray): List<String> {
        return cards.map { cardName(it) }
    }

    /**
     * Kotlin extension function to enhance card-related collections.
     * Demonstrates modern Kotlin patterns for card operations.
     */
    fun List<Int>.toCardNames(): List<String> = map { cardName(it) }

    /**
     * Validate card number is within valid range.
     */
    fun isValidCard(card: Int): Boolean {
        return card >= 1 && card <= 52 // Standard deck size
    }

    /**
     * Get all available rank names (for validation or UI).
     */
    fun getAllRanks(): Array<String> = CARD_RANKS.copyOf()

    /**
     * Get all available suit names (for validation or UI).
     */
    fun getAllSuits(): Array<String> = CARD_SUITS.copyOf()

    /**
     * Get all multicard names (for validation or UI).
     */
    fun getAllMulticardNames(): Array<String> = MULTICARD_NAMES.copyOf()
}