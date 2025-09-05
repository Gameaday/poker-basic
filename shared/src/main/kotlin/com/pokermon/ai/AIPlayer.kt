package com.pokermon.ai

import com.pokermon.players.Player
import com.pokermon.HandEvaluator
import com.pokermon.GamePhase

/**
 * AI player implementation with personality-driven decision making.
 * Integrates with the personality system to create unique AI behaviors.
 * 
 * @author Pokermon AI System
 * @version 1.1.0
 */
class AIPlayer(
    name: String,
    chips: Int,
    val personality: AIPersonality
) : Player(name, chips, isAI = true) {
    
    private val decisionMaker = AIDecisionMaker(personality)
    
    /**
     * Makes a betting decision based on personality and game state
     */
    fun makeBettingDecision(
        currentBet: Int,
        pot: Int,
        gamePhase: GamePhase,
        opponents: List<Player>
    ): BettingAction {
        val handStrength = evaluateHandStrength()
        val gameContext = GameContext(currentBet, pot, gamePhase, opponents.size)
        
        return decisionMaker.decideBettingAction(hand.toList(), handStrength, gameContext)
    }
    
    /**
     * Decides which cards to exchange
     */
    fun makeExchangeDecision(): List<Int> {
        val handStrength = evaluateHandStrength()
        return decisionMaker.decideCardExchange(hand.toList(), handStrength)
    }
    
    /**
     * Evaluates the current hand strength (0.0 to 1.0)
     */
    private fun evaluateHandStrength(): Double {
        val handResult = HandEvaluator.evaluateHand(hand)
        return handResult.score / 999.0 // Normalize to 0-1 range
    }
    
    /**
     * Updates AI state based on observed actions
     */
    fun observeAction(player: Player, action: BettingAction, amount: Int) {
        decisionMaker.updateOpponentModel(player.name, action, amount)
    }
    
    /**
     * Gets the AI's current confidence level
     */
    fun getConfidenceLevel(): Double {
        val handStrength = evaluateHandStrength()
        return personality.calculateConfidence(handStrength, chips)
    }
}

/**
 * Betting actions that an AI can take
 */
enum class BettingAction {
    FOLD,
    CHECK,
    CALL,
    RAISE,
    ALL_IN
}

/**
 * Game context information for AI decision making
 */
data class GameContext(
    val currentBet: Int,
    val pot: Int,
    val phase: GamePhase,
    val opponentCount: Int,
    val isHeadsUp: Boolean = opponentCount == 1
)