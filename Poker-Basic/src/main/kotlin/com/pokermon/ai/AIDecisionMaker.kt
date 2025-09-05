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
    /**
     * Represents possible AI actions in poker.
     */
    enum class AIAction {
        FOLD,
        CALL,
        RAISE_SMALL,   // 25-50% of current bet
        RAISE_MEDIUM,  // 50-100% of current bet
        RAISE_LARGE,   // 100-200% of current bet
        ALL_IN
    }

    /**
     * Represents the context of the current game situation using data class.
     */
    data class GameContext(
        val currentBet: Int,
        val potSize: Int,
        val playersRemaining: Int = 4,
        val bettingRound: Int = 1,  // 1 = pre-flop, 2 = post-flop, etc.
        val lastToAct: Boolean = false,
        val chipRatio: Int = 1    // player chips / average chips
    )

    /**
     * Calculates the bet amount for an AI player based on personality and game context.
     * This is the main entry point for the advanced AI system.
     * 
     * @param player the AI player making the decision
     * @param personality the player's personality
     * @param context the current game context
     * @param handStrength assessed hand strength (0.0-1.0)
     * @return the bet amount the AI wants to place
     */
    fun calculateAIBet(
        player: Player,
        personality: Personality,
        context: GameContext,
        handStrength: Float
    ): Int {
        require(!player.isHuman) { "Cannot calculate AI bet for human player" }
        
        // Get personality traits for more nuanced decision making
        val traits = PersonalityTraits.fromPersonality(personality)
        
        // Determine the AI action based on multiple factors
        val action = determineAIAction(traits, context, handStrength)
        
        // Convert action to bet amount
        return convertActionToBet(action, player, context)
    }

    /**
     * Determines the AI action based on personality traits, game context, and hand strength.
     */
    private fun determineAIAction(
        traits: PersonalityTraits,
        context: GameContext,
        handStrength: Float
    ): AIAction {
        // Calculate base action probability based on hand strength
        val foldThreshold = calculateFoldThreshold(traits, context)
        val raiseThreshold = calculateRaiseThreshold(traits, context)
        
        // Apply personality modifiers
        val adjustedHandStrength = adjustHandStrengthForPersonality(handStrength, traits)
        
        // Determine action based on thresholds
        return when {
            adjustedHandStrength < foldThreshold -> AIAction.FOLD
            adjustedHandStrength > raiseThreshold -> determineRaiseSize(traits, context, adjustedHandStrength)
            else -> AIAction.CALL
        }
    }

    /**
     * Calculates the fold threshold based on personality and game context.
     */
    private fun calculateFoldThreshold(traits: PersonalityTraits, context: GameContext): Float {
        var threshold = 0.3f // Base fold threshold
        
        // Brave players fold less often
        threshold -= (traits.bravery - 5.0f) * 0.02f
        
        // Confident players fold less often
        threshold -= (traits.confidence - 5.0f) * 0.015f
        
        // Adjust for pot odds (higher pot makes folding less attractive)
        val potOdds = context.potSize.toFloat() / maxOf(context.currentBet, 1).toFloat()
        threshold -= potOdds * 0.01f
        
        // Late in the game, more aggressive
        threshold -= (context.bettingRound - 1) * 0.05f
        
        return threshold.coerceIn(0.1f, 0.7f)
    }

    /**
     * Calculates the raise threshold based on personality and game context.
     */
    private fun calculateRaiseThreshold(traits: PersonalityTraits, context: GameContext): Float {
        var threshold = 0.7f // Base raise threshold
        
        // Brave and confident players raise more often
        threshold -= (traits.bravery - 5.0f) * 0.03f
        threshold -= (traits.confidence - 5.0f) * 0.025f
        
        // Intelligence affects when to raise strategically
        threshold -= (traits.intelligence - 5.0f) * 0.02f
        
        // Adjust for chip ratio (chip leaders can afford to be more aggressive)
        threshold -= (context.chipRatio - 1) * 0.1f
        
        return threshold.coerceIn(0.3f, 0.9f)
    }

    /**
     * Adjusts hand strength perception based on personality traits.
     */
    private fun adjustHandStrengthForPersonality(handStrength: Float, traits: PersonalityTraits): Float {
        var adjusted = handStrength
        
        // Overconfident players overestimate their hands
        adjusted += (traits.confidence - 5.0f) * 0.05f
        
        // Smart players are more accurate in assessment
        val accuracyFactor = 1.0f - (traits.intelligence - 5.0f) * 0.02f
        adjusted = handStrength + (adjusted - handStrength) * accuracyFactor
        
        // Add some randomness based on adaptability (less predictable)
        val randomFactor = (10.0f - traits.adaptability) * 0.01f
        adjusted += (random.nextFloat() - 0.5f) * randomFactor
        
        return adjusted.coerceIn(0.0f, 1.0f)
    }

    /**
     * Determines the size of raise based on personality and context.
     */
    private fun determineRaiseSize(
        traits: PersonalityTraits,
        context: GameContext,
        handStrength: Float
    ): AIAction {
        val aggressionScore = (traits.bravery + traits.confidence) / 2.0f
        val randomFactor = random.nextFloat()
        
        return when {
            handStrength > 0.95f && aggressionScore > 7.0f -> AIAction.ALL_IN
            handStrength > 0.85f && aggressionScore > 6.0f && randomFactor < 0.3f -> AIAction.RAISE_LARGE
            handStrength > 0.75f && aggressionScore > 5.0f -> AIAction.RAISE_MEDIUM
            else -> AIAction.RAISE_SMALL
        }
    }

    /**
     * Converts an AI action to an actual bet amount.
     */
    private fun convertActionToBet(action: AIAction, player: Player, context: GameContext): Int {
        return when (action) {
            AIAction.FOLD -> 0
            AIAction.CALL -> context.currentBet
            AIAction.RAISE_SMALL -> context.currentBet + (context.currentBet * 0.25f + random.nextFloat() * context.currentBet * 0.25f).toInt()
            AIAction.RAISE_MEDIUM -> context.currentBet + (context.currentBet * 0.5f + random.nextFloat() * context.currentBet * 0.5f).toInt()
            AIAction.RAISE_LARGE -> context.currentBet + (context.currentBet * 1.0f + random.nextFloat() * context.currentBet * 1.0f).toInt()
            AIAction.ALL_IN -> player.chips
        }.let { betAmount ->
            // Ensure bet doesn't exceed player's chips
            minOf(betAmount, player.chips)
        }
    }

    companion object {
        /**
         * Assesses hand strength from hand value using improved algorithm.
         * Converts the game's hand value system to a 0.0-1.0 strength scale.
         * 
         * @param handValue the numerical hand value from the game
         * @return hand strength from 0.0 (worst) to 1.0 (best)
         */
        fun assessHandStrength(handValue: Int): Float {
            return when {
                handValue >= 10000 -> 1.0f // Royal Flush
                handValue >= 9000 -> 0.95f + (handValue - 9000) / 1000f * 0.05f // Straight Flush
                handValue >= 8000 -> 0.85f + (handValue - 8000) / 1000f * 0.10f // Four of a Kind
                handValue >= 7000 -> 0.75f + (handValue - 7000) / 1000f * 0.10f // Full House
                handValue >= 6000 -> 0.65f + (handValue - 6000) / 1000f * 0.10f // Flush
                handValue >= 5000 -> 0.55f + (handValue - 5000) / 1000f * 0.10f // Straight
                handValue >= 4000 -> 0.45f + (handValue - 4000) / 1000f * 0.10f // Three of a Kind
                handValue >= 3000 -> 0.25f + (handValue - 3000) / 1000f * 0.20f // Two Pair
                handValue >= 2000 -> 0.15f + (handValue - 2000) / 1000f * 0.10f // Pair
                else -> (handValue / 2000f).coerceIn(0.0f, 0.15f) // High Card
            }
        }

        /**
         * Creates a simple game context for basic AI calculations.
         * This is a convenience method for simpler game modes.
         */
        fun createSimpleContext(currentBet: Int, potSize: Int): GameContext {
            return GameContext(
                currentBet = currentBet,
                potSize = potSize,
                playersRemaining = 4,
                bettingRound = 1,
                lastToAct = false,
                chipRatio = 1
            )
        }

        /**
         * Creates a detailed game context with all parameters.
         */
        fun createDetailedContext(
            currentBet: Int,
            potSize: Int,
            playersRemaining: Int,
            bettingRound: Int,
            lastToAct: Boolean,
            playerChips: Int,
            averageChips: Int
        ): GameContext {
            val chipRatio = if (averageChips > 0) playerChips / averageChips else 1
            return GameContext(
                currentBet = currentBet,
                potSize = potSize,
                playersRemaining = playersRemaining,
                bettingRound = bettingRound,
                lastToAct = lastToAct,
                chipRatio = chipRatio
            )
        }
    }
}