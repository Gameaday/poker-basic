package com.pokermon

/**
 * Common Main functionality for native builds
 * Provides simplified deck setup without JVM dependencies
 */
object CommonMain {
    
    /**
     * Creates a standard 52-card deck for native builds
     * @return IntArray representing a shuffled deck
     */
    fun setDeck(): IntArray {
        // Create standard 52-card deck (1-52)
        val deck = IntArray(52) { it + 1 }
        
        // Simple shuffle using native-compatible randomization
        for (i in deck.indices) {
            val randomIndex = (0..i).random()
            val temp = deck[i]
            deck[i] = deck[randomIndex]
            deck[randomIndex] = temp
        }
        
        return deck
    }
    
    /**
     * Convert card number to readable string for native builds
     * @param cardNumber the card number (1-52)
     * @return String representation of the card
     */
    fun cardToString(cardNumber: Int): String {
        if (cardNumber < 1 || cardNumber > 52) return "Invalid Card"
        
        val suits = arrayOf("♠", "♥", "♦", "♣")
        val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        
        val suitIndex = (cardNumber - 1) / 13
        val rankIndex = (cardNumber - 1) % 13
        
        return "${ranks[rankIndex]}${suits[suitIndex]}"
    }
    
    /**
     * Get card rank for hand evaluation (native compatible)
     * @param cardNumber the card number (1-52)
     * @return rank value (1-13, where Ace=1)
     */
    fun getCardRank(cardNumber: Int): Int {
        if (cardNumber < 1 || cardNumber > 52) return 0
        return ((cardNumber - 1) % 13) + 1
    }
    
    /**
     * Get card suit for hand evaluation (native compatible)
     * @param cardNumber the card number (1-52)
     * @return suit index (0-3)
     */
    fun getCardSuit(cardNumber: Int): Int {
        if (cardNumber < 1 || cardNumber > 52) return -1
        return (cardNumber - 1) / 13
    }
}