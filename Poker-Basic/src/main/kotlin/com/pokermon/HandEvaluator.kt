package com.pokermon

/**
 * Advanced hand evaluation system following DRY principles and designed for extensibility.
 * 
 * This object serves as the single authoritative source for poker hand evaluation,
 * providing consistent scoring that enables direct hand comparison and easy integration
 * with future monster modifier systems.
 * 
 * Key Features:
 * - Unique numerical scores for direct comparison
 * - Descriptive hand names for UI display
 * - Extensible design for monster modifiers
 * - Consistent card logic using CardUtils
 * - Modern Kotlin patterns with data classes
 * 
 * Scoring System:
 * - Royal Flush: 900-999 (highest possible)
 * - Straight Flush: 800-899 
 * - Four of a Kind: 700-799
 * - Full House: 600-699
 * - Flush: 500-599
 * - Straight: 400-499
 * - Three of a Kind: 300-399
 * - Two Pair: 200-299
 * - One Pair: 100-199
 * - High Card: 1-99
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0 (Kotlin-native implementation)
 */
object HandEvaluator {
    
    /**
     * Comprehensive hand evaluation result containing score and metadata.
     * Designed to be extensible for future monster modifier integration.
     */
    data class HandResult(
        val score: Int,
        val handType: HandType,
        val description: String,
        val primaryRank: Int = 0,     // For comparing hands of same type
        val secondaryRank: Int = 0,   // For tie-breaking (e.g., kicker cards)
        val ranks: List<Int> = emptyList(),  // All ranks for detailed comparison
        
        // Extension points for future monster modifiers
        val baseScore: Int = score,   // Original score before modifiers
        val modifiers: List<HandModifier> = emptyList()
    ) {
        /**
         * Apply monster modifiers to the base hand score.
         * This method provides the hook for future monster companion integration.
         */
        fun applyModifiers(modifiers: List<HandModifier>): HandResult {
            var modifiedScore = baseScore
            val appliedModifiers = mutableListOf<HandModifier>()
            
            modifiers.forEach { modifier ->
                if (modifier.appliesTo(this)) {
                    modifiedScore = modifier.modifyScore(modifiedScore, this)
                    appliedModifiers.add(modifier)
                }
            }
            
            return copy(
                score = modifiedScore,
                modifiers = appliedModifiers
            )
        }
        
        /**
         * Check if this hand beats another hand directly by score comparison.
         */
        fun beats(other: HandResult): Boolean = score > other.score
        
        /**
         * Get detailed comparison for tie-breaking.
         */
        fun compareDetailed(other: HandResult): Int = when {
            score != other.score -> score.compareTo(other.score)
            primaryRank != other.primaryRank -> primaryRank.compareTo(other.primaryRank)
            secondaryRank != other.secondaryRank -> secondaryRank.compareTo(other.secondaryRank)
            else -> compareKickers(other)
        }
        
        private fun compareKickers(other: HandResult): Int {
            val thisKickers = ranks.sortedDescending()
            val otherKickers = other.ranks.sortedDescending()
            
            for (i in 0 until minOf(thisKickers.size, otherKickers.size)) {
                val comparison = thisKickers[i].compareTo(otherKickers[i])
                if (comparison != 0) return comparison
            }
            return 0 // Perfect tie
        }
    }
    
    /**
     * Hand types with base score ranges.
     */
    enum class HandType(val baseScore: Int, val displayName: String) {
        HIGH_CARD(1, "High Card"),
        ONE_PAIR(100, "One Pair"),
        TWO_PAIR(200, "Two Pair"),
        THREE_OF_A_KIND(300, "Three of a Kind"),
        STRAIGHT(400, "Straight"),
        FLUSH(500, "Flush"),
        FULL_HOUSE(600, "Full House"),
        FOUR_OF_A_KIND(700, "Four of a Kind"),
        STRAIGHT_FLUSH(800, "Straight Flush"),
        ROYAL_FLUSH(900, "Royal Flush");
        
        fun isFlush(): Boolean = this == FLUSH || this == STRAIGHT_FLUSH || this == ROYAL_FLUSH
        fun isStraight(): Boolean = this == STRAIGHT || this == STRAIGHT_FLUSH || this == ROYAL_FLUSH
        fun hasMultiples(): Boolean = this != HIGH_CARD && this != STRAIGHT && this != FLUSH
    }
    
    /**
     * Interface for future monster modifiers.
     * Allows monsters to influence hand scoring without changing core logic.
     */
    interface HandModifier {
        val name: String
        val description: String
        
        fun appliesTo(hand: HandResult): Boolean
        fun modifyScore(baseScore: Int, hand: HandResult): Int
    }
    
    /**
     * Primary hand evaluation function - the single source of truth for hand analysis.
     * Uses modern Kotlin patterns and CardUtils for consistent card handling.
     */
    fun evaluateHand(hand: IntArray): HandResult {
        require(hand.size == 5) { "Hand must contain exactly 5 cards" }
        require(hand.all { CardUtils.isValidCard(it) }) { "All cards must be valid" }
        
        val rankCounts = generateRankCounts(hand)
        val suitCounts = generateSuitCounts(hand)
        val sortedRanks = hand.map { cardToRank(it) }.sorted()
        
        return when {
            isRoyalFlush(hand, rankCounts, suitCounts) -> 
                createRoyalFlushResult(sortedRanks)
            isStraightFlush(hand, rankCounts, suitCounts) -> 
                createStraightFlushResult(sortedRanks)
            isFourOfAKind(rankCounts) -> 
                createFourOfAKindResult(rankCounts, sortedRanks)
            isFullHouse(rankCounts) -> 
                createFullHouseResult(rankCounts, sortedRanks)
            isFlush(suitCounts) -> 
                createFlushResult(sortedRanks)
            isStraight(sortedRanks) -> 
                createStraightResult(sortedRanks)
            isThreeOfAKind(rankCounts) -> 
                createThreeOfAKindResult(rankCounts, sortedRanks)
            isTwoPair(rankCounts) -> 
                createTwoPairResult(rankCounts, sortedRanks)
            isOnePair(rankCounts) -> 
                createOnePairResult(rankCounts, sortedRanks)
            else -> 
                createHighCardResult(sortedRanks)
        }
    }
    
    // =================================================================
    // HAND DETECTION FUNCTIONS - Clean and optimized
    // =================================================================
    
    private fun isRoyalFlush(hand: IntArray, rankCounts: Map<Int, Int>, suitCounts: Map<Int, Int>): Boolean {
        if (!isFlush(suitCounts)) return false
        val ranks = hand.map { cardToRank(it) }.toSet()
        return ranks == setOf(10, 11, 12, 13, 1) // 10, J, Q, K, A
    }
    
    private fun isStraightFlush(hand: IntArray, rankCounts: Map<Int, Int>, suitCounts: Map<Int, Int>): Boolean {
        return isFlush(suitCounts) && isStraight(hand.map { cardToRank(it) }.sorted())
    }
    
    private fun isFourOfAKind(rankCounts: Map<Int, Int>): Boolean {
        return rankCounts.values.any { it == 4 }
    }
    
    private fun isFullHouse(rankCounts: Map<Int, Int>): Boolean {
        val counts = rankCounts.values.sorted()
        return counts == listOf(2, 3)
    }
    
    private fun isFlush(suitCounts: Map<Int, Int>): Boolean {
        return suitCounts.values.any { it == 5 }
    }
    
    private fun isStraight(sortedRanks: List<Int>): Boolean {
        // Handle regular straight
        val consecutive = (0 until 4).all { sortedRanks[it + 1] - sortedRanks[it] == 1 }
        if (consecutive) return true
        
        // Handle ace-low straight (A, 2, 3, 4, 5)
        return sortedRanks == listOf(1, 2, 3, 4, 5)
    }
    
    private fun isThreeOfAKind(rankCounts: Map<Int, Int>): Boolean {
        return rankCounts.values.any { it == 3 } && !isFullHouse(rankCounts)
    }
    
    private fun isTwoPair(rankCounts: Map<Int, Int>): Boolean {
        return rankCounts.values.count { it == 2 } == 2
    }
    
    private fun isOnePair(rankCounts: Map<Int, Int>): Boolean {
        return rankCounts.values.count { it == 2 } == 1
    }
    
    // =================================================================
    // RESULT CREATION FUNCTIONS - Detailed scoring with tie-breaking
    // =================================================================
    
    private fun createRoyalFlushResult(sortedRanks: List<Int>): HandResult {
        return HandResult(
            score = HandType.ROYAL_FLUSH.baseScore + 99, // Always maximum
            handType = HandType.ROYAL_FLUSH,
            description = "Royal Flush",
            primaryRank = 14, // Ace high
            ranks = sortedRanks
        )
    }
    
    private fun createStraightFlushResult(sortedRanks: List<Int>): HandResult {
        val highCard = if (sortedRanks == listOf(1, 2, 3, 4, 5)) 5 else sortedRanks.last()
        return HandResult(
            score = HandType.STRAIGHT_FLUSH.baseScore + highCard,
            handType = HandType.STRAIGHT_FLUSH,
            description = "Straight Flush",
            primaryRank = highCard,
            ranks = sortedRanks
        )
    }
    
    private fun createFourOfAKindResult(rankCounts: Map<Int, Int>, sortedRanks: List<Int>): HandResult {
        val quadRank = rankCounts.entries.first { it.value == 4 }.key
        val kicker = rankCounts.entries.first { it.value == 1 }.key
        
        return HandResult(
            score = HandType.FOUR_OF_A_KIND.baseScore + quadRank,
            handType = HandType.FOUR_OF_A_KIND,
            description = "Four of a Kind",
            primaryRank = quadRank,
            secondaryRank = kicker,
            ranks = sortedRanks
        )
    }
    
    private fun createFullHouseResult(rankCounts: Map<Int, Int>, sortedRanks: List<Int>): HandResult {
        val tripRank = rankCounts.entries.first { it.value == 3 }.key
        val pairRank = rankCounts.entries.first { it.value == 2 }.key
        
        return HandResult(
            score = HandType.FULL_HOUSE.baseScore + tripRank,
            handType = HandType.FULL_HOUSE,
            description = "Full House",
            primaryRank = tripRank,
            secondaryRank = pairRank,
            ranks = sortedRanks
        )
    }
    
    private fun createFlushResult(sortedRanks: List<Int>): HandResult {
        val highCard = sortedRanks.maxOrNull() ?: 0
        
        return HandResult(
            score = HandType.FLUSH.baseScore + highCard,
            handType = HandType.FLUSH,
            description = "Flush",
            primaryRank = highCard,
            ranks = sortedRanks.sortedDescending()
        )
    }
    
    private fun createStraightResult(sortedRanks: List<Int>): HandResult {
        val highCard = if (sortedRanks == listOf(1, 2, 3, 4, 5)) 5 else sortedRanks.last()
        
        return HandResult(
            score = HandType.STRAIGHT.baseScore + highCard,
            handType = HandType.STRAIGHT,
            description = "Straight",
            primaryRank = highCard,
            ranks = sortedRanks
        )
    }
    
    private fun createThreeOfAKindResult(rankCounts: Map<Int, Int>, sortedRanks: List<Int>): HandResult {
        val tripRank = rankCounts.entries.first { it.value == 3 }.key
        val kickers = rankCounts.entries.filter { it.value == 1 }.map { it.key }.sortedDescending()
        
        return HandResult(
            score = HandType.THREE_OF_A_KIND.baseScore + tripRank,
            handType = HandType.THREE_OF_A_KIND,
            description = "Three of a Kind",
            primaryRank = tripRank,
            secondaryRank = kickers.firstOrNull() ?: 0,
            ranks = sortedRanks
        )
    }
    
    private fun createTwoPairResult(rankCounts: Map<Int, Int>, sortedRanks: List<Int>): HandResult {
        val pairs = rankCounts.entries.filter { it.value == 2 }.map { it.key }.sortedDescending()
        val kicker = rankCounts.entries.first { it.value == 1 }.key
        
        return HandResult(
            score = HandType.TWO_PAIR.baseScore + pairs.first(),
            handType = HandType.TWO_PAIR,
            description = "Two Pair",
            primaryRank = pairs.first(),
            secondaryRank = pairs.getOrNull(1) ?: 0,
            ranks = sortedRanks
        )
    }
    
    private fun createOnePairResult(rankCounts: Map<Int, Int>, sortedRanks: List<Int>): HandResult {
        val pairRank = rankCounts.entries.first { it.value == 2 }.key
        val kickers = rankCounts.entries.filter { it.value == 1 }.map { it.key }.sortedDescending()
        
        return HandResult(
            score = HandType.ONE_PAIR.baseScore + pairRank,
            handType = HandType.ONE_PAIR,
            description = "One Pair",
            primaryRank = pairRank,
            secondaryRank = kickers.firstOrNull() ?: 0,
            ranks = sortedRanks
        )
    }
    
    private fun createHighCardResult(sortedRanks: List<Int>): HandResult {
        val highCard = sortedRanks.maxOrNull() ?: 0
        
        return HandResult(
            score = HandType.HIGH_CARD.baseScore + highCard,
            handType = HandType.HIGH_CARD,
            description = "High Card",
            primaryRank = highCard,
            ranks = sortedRanks.sortedDescending()
        )
    }
    
    // =================================================================
    // UTILITY FUNCTIONS - Using CardUtils for consistency
    // =================================================================
    
    private fun generateRankCounts(hand: IntArray): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        hand.forEach { card ->
            val rank = cardToRank(card)
            counts[rank] = counts.getOrDefault(rank, 0) + 1
        }
        return counts
    }
    
    private fun generateSuitCounts(hand: IntArray): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        hand.forEach { card ->
            val suit = cardToSuit(card)
            counts[suit] = counts.getOrDefault(suit, 0) + 1
        }
        return counts
    }
    
    /**
     * Convert card number to rank using consistent logic.
     * Ace = 1, 2-10 = face value, Jack = 11, Queen = 12, King = 13
     */
    private fun cardToRank(card: Int): Int {
        var rank = card / 4
        if (card % 4 != 0) rank++
        return if (rank == 13) 1 else rank // Convert 13 to 1 for Ace
    }
    
    /**
     * Convert card number to suit (0-3).
     */
    private fun cardToSuit(card: Int): Int {
        return (card - 1) % 4
    }
    
    /**
     * Convenience function for quick hand comparison.
     * Returns positive if hand1 wins, negative if hand2 wins, 0 for tie.
     */
    fun compareHands(hand1: IntArray, hand2: IntArray): Int {
        val result1 = evaluateHand(hand1)
        val result2 = evaluateHand(hand2)
        return result1.compareDetailed(result2)
    }
    
    /**
     * Get list of winners from multiple hands.
     */
    fun findWinners(hands: List<IntArray>): List<Int> {
        val results = hands.map { evaluateHand(it) }
        val maxScore = results.maxOfOrNull { it.score } ?: return emptyList()
        
        return results.mapIndexedNotNull { index, result ->
            if (result.score == maxScore) index else null
        }
    }
    
    /**
     * Extension function for Player class integration.
     */
    fun Player.evaluateHand(): HandResult = evaluateHand(this.hand)
}