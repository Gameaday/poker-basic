package com.pokermon.ui

import com.pokermon.players.Player
import com.pokermon.modern.CardUtils

/**
 * Shared UI components for cross-platform compatibility.
 * These components can be used by both Android and Desktop implementations.
 * 
 * @author Pokermon UI System
 * @version 1.1.0
 */
object UIComponents {
    
    /**
     * Formats a player's hand for display
     */
    fun formatPlayerHand(player: Player, showCards: Boolean = true): String {
        if (!showCards) {
            return "${player.name}: [Hidden Hand] - ${player.chips} chips"
        }
        
        val handString = player.hand.joinToString(", ") { card ->
            CardUtils.cardName(card)
        }
        
        return "${player.name}: [$handString] - ${player.chips} chips"
    }
    
    /**
     * Formats betting information
     */
    fun formatBettingInfo(pot: Int, currentBet: Int, minimumBet: Int): String {
        return "Pot: $$pot | Current Bet: $$currentBet | Min Bet: $$minimumBet"
    }
    
    /**
     * Formats game status
     */
    fun formatGameStatus(activePlayers: Int, totalPlayers: Int, round: Int): String {
        return "Round $round | Players: $activePlayers/$totalPlayers active"
    }
    
    /**
     * Creates a card display string with suit symbols
     */
    fun formatCardWithSymbols(card: Int): String {
        val rank = CardUtils.rankName(CardUtils.cardRank(card))
        val suit = CardUtils.cardSuit(card)
        
        val suitSymbol = when (suit) {
            1 -> "♠" // Spades
            2 -> "♥" // Hearts
            3 -> "♦" // Diamonds
            4 -> "♣" // Clubs
            else -> "?"
        }
        
        return "$rank$suitSymbol"
    }
    
    /**
     * Creates a full hand display with symbols
     */
    fun formatHandWithSymbols(hand: List<Int>): String {
        return hand.joinToString(" ") { formatCardWithSymbols(it) }
    }
    
    /**
     * Formats player status for leaderboard display
     */
    fun formatLeaderboard(players: List<Player>): List<String> {
        return players
            .sortedByDescending { it.chips }
            .mapIndexed { index, player ->
                val rank = index + 1
                val medal = when (rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#$rank"
                }
                "$medal ${player.name}: $${player.chips}"
            }
    }
    
    /**
     * Creates action button labels based on game state
     */
    fun getActionButtons(currentBet: Int, playerChips: Int): List<String> {
        val actions = mutableListOf<String>()
        
        if (currentBet == 0) {
            actions.add("Check")
        } else {
            actions.add("Call ($$currentBet)")
        }
        
        if (playerChips > currentBet) {
            actions.add("Raise")
        }
        
        actions.add("Fold")
        
        return actions
    }
    
    /**
     * Validates and formats bet amount
     */
    fun validateBetAmount(amount: String, playerChips: Int, minimumBet: Int): Pair<Boolean, Int> {
        return try {
            val betAmount = amount.toInt()
            when {
                betAmount < minimumBet -> Pair(false, minimumBet)
                betAmount > playerChips -> Pair(false, playerChips)
                else -> Pair(true, betAmount)
            }
        } catch (e: NumberFormatException) {
            Pair(false, minimumBet)
        }
    }
    
    /**
     * Creates a progress indicator string
     */
    fun createProgressBar(current: Int, total: Int, width: Int = 20): String {
        val progress = (current.toDouble() / total * width).toInt()
        val filled = "█".repeat(progress)
        val empty = "░".repeat(width - progress)
        return "[$filled$empty] $current/$total"
    }
    
    /**
     * Formats time duration for display
     */
    fun formatDuration(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
}