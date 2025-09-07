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
     * Convert card name to drawable resource name.
     * Simplified approach: convert to lowercase and replace spaces with underscores.
     */
    fun getCardResourceName(cardName: String): String {
        return cardName.lowercase().replace(" ", "_")
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