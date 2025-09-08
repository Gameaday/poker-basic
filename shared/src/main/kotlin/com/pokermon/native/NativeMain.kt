/**
 * Native entry point for Kotlin/Native compilation
 * Provides a minimal console-based game interface without Java dependencies
 */

// Simple card representation for native builds
data class SimpleCard(val rank: String, val suit: String)

// Basic game state for native builds
class SimplePokerGame {
    private val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
    private val suits = listOf("Hearts", "Diamonds", "Clubs", "Spades")
    
    fun createDeck(): List<SimpleCard> {
        return suits.flatMap { suit ->
            ranks.map { rank ->
                SimpleCard(rank, suit)
            }
        }
    }
    
    fun dealHand(deck: List<SimpleCard>, handSize: Int = 5): List<SimpleCard> {
        return deck.shuffled().take(handSize)
    }
    
    fun formatHand(hand: List<SimpleCard>): String {
        return hand.joinToString(", ") { "${it.rank} of ${it.suit}" }
    }
    
    fun runGame() {
        println("=== Pokermon Native - Kotlin/Native Build ===")
        println("This is a demonstration of true native compilation!")
        println()
        
        val deck = createDeck()
        println("Created deck with ${deck.size} cards")
        
        val playerHand = dealHand(deck)
        println("Your hand: ${formatHand(playerHand)}")
        
        val dealerHand = dealHand(deck.drop(5))
        println("Dealer hand: ${formatHand(dealerHand)}")
        
        println()
        println("✅ Native executable running without Java!")
        println("Kotlin version: 1.9.22")
        println("Build: Native compilation with kotlinc-native")
    }
}

fun main() {
    val game = SimplePokerGame()
    game.runGame()
}