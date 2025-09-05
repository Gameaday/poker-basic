package com.pokermon.ai

import com.pokermon.modern.CardUtils

/**
 * AI decision maker that implements sophisticated poker decision logic.
 * Uses personality traits and game context to make realistic AI decisions.
 * 
 * @author Pokermon AI System
 * @version 1.1.0
 */
class AIDecisionMaker(private val personality: AIPersonality) {
    
    private val opponentModels = mutableMapOf<String, OpponentModel>()
    
    /**
     * Decides the betting action based on hand strength and game context
     */
    fun decideBettingAction(
        hand: List<Int>,
        handStrength: Double,
        context: GameContext
    ): BettingAction {
        val riskTolerance = personality.calculateRiskTolerance(context)
        val aggression = personality.aggression
        
        // Base decision on hand strength and personality
        return when {
            handStrength < 0.2 && context.currentBet > 0 -> BettingAction.FOLD
            handStrength < 0.3 && riskTolerance < 0.4 -> BettingAction.FOLD
            handStrength > 0.8 || (handStrength > 0.6 && aggression > 7.0) -> {
                if (context.currentBet == 0) BettingAction.RAISE else BettingAction.RAISE
            }
            handStrength > 0.5 || context.currentBet == 0 -> {
                if (context.currentBet == 0) BettingAction.CHECK else BettingAction.CALL
            }
            handStrength > 0.3 && context.currentBet <= personality.calculateMaxCallAmount(context.pot) -> {
                BettingAction.CALL
            }
            else -> BettingAction.FOLD
        }
    }
    
    /**
     * Decides which cards to exchange based on hand analysis
     */
    fun decideCardExchange(hand: List<Int>, handStrength: Double): List<Int> {
        if (handStrength > 0.7) return emptyList() // Keep strong hands
        
        val cardsToExchange = mutableListOf<Int>()
        val ranks = hand.map { CardUtils.cardRank(it) }
        val suits = hand.map { CardUtils.cardSuit(it) }
        
        // Look for pairs, potential straights, potential flushes
        val rankCounts = ranks.groupingBy { it }.eachCount()
        val suitCounts = suits.groupingBy { it }.eachCount()
        
        // Keep pairs and better
        val keptRanks = rankCounts.filter { it.value >= 2 }.keys
        
        // Check for flush potential
        val flushSuit = suitCounts.entries.find { it.value >= 4 }?.key
        
        // Check for straight potential
        val sortedRanks = ranks.sorted()
        val hasStraightPotential = hasStraightDraw(sortedRanks)
        
        hand.forEachIndexed { index, card ->
            val rank = CardUtils.cardRank(card)
            val suit = CardUtils.cardSuit(card)
            
            val shouldKeep = when {
                rank in keptRanks -> true // Keep pairs
                flushSuit != null && suit == flushSuit -> true // Keep flush draws
                hasStraightPotential && isPartOfStraightDraw(rank, sortedRanks) -> true
                else -> false
            }
            
            if (!shouldKeep) {
                cardsToExchange.add(index)
            }
        }
        
        // Limit exchanges based on personality
        val maxExchanges = when {
            personality.courage > 7.0 -> 3 // Brave AIs exchange more
            personality.cautiousness > 7.0 -> 1 // Cautious AIs exchange less
            else -> 2
        }
        
        return cardsToExchange.take(maxExchanges)
    }
    
    /**
     * Updates the model of an opponent based on observed behavior
     */
    fun updateOpponentModel(playerName: String, action: BettingAction, amount: Int) {
        val model = opponentModels.getOrPut(playerName) { OpponentModel() }
        
        model.actions.add(Pair(action, amount))
        
        // Update aggression estimate
        when (action) {
            BettingAction.RAISE -> model.estimatedAggression += 0.1f
            BettingAction.FOLD -> model.estimatedAggression -= 0.05f
            else -> { /* No change */ }
        }
        
        // Keep aggression in bounds
        model.estimatedAggression = model.estimatedAggression.coerceIn(0.0f, 1.0f)
    }
    
    private fun hasStraightDraw(sortedRanks: List<Int>): Boolean {
        // Check for 4-card straight potential
        for (i in 0 until sortedRanks.size - 3) {
            var consecutiveCount = 1
            for (j in i + 1 until sortedRanks.size) {
                if (sortedRanks[j] == sortedRanks[j - 1] + 1) {
                    consecutiveCount++
                    if (consecutiveCount >= 4) return true
                } else if (sortedRanks[j] != sortedRanks[j - 1]) {
                    break
                }
            }
        }
        return false
    }
    
    private fun isPartOfStraightDraw(rank: Int, sortedRanks: List<Int>): Boolean {
        // Simple implementation - just check if card is part of longest sequence
        val index = sortedRanks.indexOf(rank)
        var sequenceLength = 1
        
        // Count backwards
        var i = index - 1
        while (i >= 0 && sortedRanks[i] == sortedRanks[i + 1] - 1) {
            sequenceLength++
            i--
        }
        
        // Count forwards
        i = index + 1
        while (i < sortedRanks.size && sortedRanks[i] == sortedRanks[i - 1] + 1) {
            sequenceLength++
            i++
        }
        
        return sequenceLength >= 3 // Part of potential straight
    }
}

/**
 * Model of an opponent's behavior patterns
 */
data class OpponentModel(
    val actions: MutableList<Pair<BettingAction, Int>> = mutableListOf(),
    var estimatedAggression: Float = 0.5f,
    var estimatedSkill: Float = 0.5f
)