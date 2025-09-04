package com.pokermon.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Enhanced card graphics manager for the Android Pokermon app.
 * Provides proper integration with TET card pack and improved visual design.
 */
object CardGraphicsManager {
    
    /**
     * Map card string names to Android drawable resource IDs.
     */
    private val cardResourceMap = mapOf(
        // Spades
        "Ace of Spades" to "ace_of_spades",
        "King of Spades" to "king_of_spades", 
        "Queen of Spades" to "queen_of_spades",
        "Jack of Spades" to "jack_of_spades",
        "Ten of Spades" to "ten_of_spades",
        "Nine of Spades" to "nine_of_spades",
        "Eight of Spades" to "eight_of_spades",
        "Seven of Spades" to "seven_of_spades",
        "Six of Spades" to "six_of_spades",
        "Five of Spades" to "five_of_spades",
        "Four of Spades" to "four_of_spades",
        "Three of Spades" to "three_of_spades",
        "Two of Spades" to "two_of_spades",
        
        // Hearts
        "Ace of Hearts" to "ace_of_hearts",
        "King of Hearts" to "king_of_hearts",
        "Queen of Hearts" to "queen_of_hearts", 
        "Jack of Hearts" to "jack_of_hearts",
        "Ten of Hearts" to "ten_of_hearts",
        "Nine of Hearts" to "nine_of_hearts",
        "Eight of Hearts" to "eight_of_hearts",
        "Seven of Hearts" to "seven_of_hearts",
        "Six of Hearts" to "six_of_hearts",
        "Five of Hearts" to "five_of_hearts",
        "Four of Hearts" to "four_of_hearts",
        "Three of Hearts" to "three_of_hearts",
        "Two of Hearts" to "two_of_hearts",
        
        // Diamonds
        "Ace of Diamonds" to "ace_of_diamonds",
        "King of Diamonds" to "king_of_diamonds",
        "Queen of Diamonds" to "queen_of_diamonds",
        "Jack of Diamonds" to "jack_of_diamonds", 
        "Ten of Diamonds" to "ten_of_diamonds",
        "Nine of Diamonds" to "nine_of_diamonds",
        "Eight of Diamonds" to "eight_of_diamonds",
        "Seven of Diamonds" to "seven_of_diamonds",
        "Six of Diamonds" to "six_of_diamonds",
        "Five of Diamonds" to "five_of_diamonds",
        "Four of Diamonds" to "four_of_diamonds",
        "Three of Diamonds" to "three_of_diamonds",
        "Two of Diamonds" to "two_of_diamonds",
        
        // Clubs
        "Ace of Clubs" to "ace_of_clubs",
        "King of Clubs" to "king_of_clubs",
        "Queen of Clubs" to "queen_of_clubs",
        "Jack of Clubs" to "jack_of_clubs",
        "Ten of Clubs" to "ten_of_clubs",
        "Nine of Clubs" to "nine_of_clubs",
        "Eight of Clubs" to "eight_of_clubs",
        "Seven of Clubs" to "seven_of_clubs",
        "Six of Clubs" to "six_of_clubs",
        "Five of Clubs" to "five_of_clubs",
        "Four of Clubs" to "four_of_clubs",
        "Three of Clubs" to "three_of_clubs",
        "Two of Clubs" to "two_of_clubs"
    )
    
    /**
     * Convert card name to drawable resource name.
     */
    fun getCardResourceName(cardName: String): String {
        return cardResourceMap[cardName] ?: "card_back"
    }
    
    /**
     * Get drawable resource ID for a card name.
     */
    fun getCardResourceId(context: android.content.Context, cardName: String): Int {
        val resourceName = getCardResourceName(cardName)
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}

/**
 * Enhanced card display component using actual TET card graphics.
 */
@Composable
fun EnhancedCardDisplay(
    card: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    canClick: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cardResourceId = remember(card) {
        CardGraphicsManager.getCardResourceId(context, card)
    }
    
    Card(
        modifier = modifier
            .size(width = 60.dp, height = 84.dp)
            .clickable(enabled = canClick) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (cardResourceId != 0) {
                Image(
                    painter = painterResource(id = cardResourceId),
                    contentDescription = card,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback to text display if image not found
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val (rank, suitSymbol, suitColor) = parseCardDisplay(card)
                    Text(
                        text = rank,
                        style = MaterialTheme.typography.titleMedium,
                        color = suitColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = suitSymbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = suitColor
                    )
                }
            }
            
            // Selection overlay
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    }
}

/**
 * Parse card string to extract rank, suit symbol, and color for fallback display.
 */
private fun parseCardDisplay(card: String): Triple<String, String, Color> {
    when {
        card.contains("♠") || card.contains("Spades") -> {
            val rank = card.replace("♠", "").replace(" of Spades", "").trim()
            return Triple(rank, "♠", Color.Black)
        }
        card.contains("♥") || card.contains("Hearts") -> {
            val rank = card.replace("♥", "").replace(" of Hearts", "").trim()
            return Triple(rank, "♥", Color.Red)
        }
        card.contains("♦") || card.contains("Diamonds") -> {
            val rank = card.replace("♦", "").replace(" of Diamonds", "").trim()
            return Triple(rank, "♦", Color.Red)
        }
        card.contains("♣") || card.contains("Clubs") -> {
            val rank = card.replace("♣", "").replace(" of Clubs", "").trim()
            return Triple(rank, "♣", Color.Black)
        }
        else -> {
            return Triple(card.take(1), "?", Color.Gray)
        }
    }
}

/**
 * Card back display for hidden cards.
 */
@Composable
fun CardBackDisplay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cardBackResourceId = remember {
        context.resources.getIdentifier("card_back", "drawable", context.packageName)
    }
    
    Card(
        modifier = modifier.size(width = 60.dp, height = 84.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (cardBackResourceId != 0) {
                Image(
                    painter = painterResource(id = cardBackResourceId),
                    contentDescription = "Card back",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🐲",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}