package com.pokermon.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
 * Enhanced card display component that uses TET card graphics when available.
 * Falls back to text-based display for unsupported cards.
 */
@Composable
fun EnhancedCardDisplay(
    card: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cardResourceId = CardGraphicsManager.getCardResourceId(context, card)

    Card(
        modifier =
            modifier
                .size(width = 60.dp, height = 84.dp)
                .clickable { onClick() }
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                    } else {
                        Modifier
                    },
                ),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (cardResourceId != null) {
                // Use actual card image
                Image(
                    painter = painterResource(id = cardResourceId),
                    contentDescription = card,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            } else {
                // Fallback to text display
                TextCardDisplay(
                    card = card,
                    isSelected = isSelected,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

/**
 * Fallback text-based card display for when graphics are not available.
 */
@Composable
private fun TextCardDisplay(
    card: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val (rank, suit, color) = parseCardDisplay(card)

    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = rank,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center,
        )
        Text(
            text = suit,
            fontSize = 16.sp,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Parse card string to get rank, suit emoji, and color.
 */
private fun parseCardDisplay(card: String): Triple<String, String, Color> {
    val parts = card.split(" of ")
    if (parts.size != 2) {
        return Triple(card, "", Color.Black)
    }

    val rank =
        when (parts[0]) {
            "Ace" -> "A"
            "Jack" -> "J"
            "Queen" -> "Q"
            "King" -> "K"
            else -> parts[0]
        }

    val (suitEmoji, suitColor) =
        when (parts[1]) {
            "Hearts" -> "♥" to Color.Red
            "Diamonds" -> "♦" to Color.Red
            "Clubs" -> "♣" to Color.Black
            "Spades" -> "♠" to Color.Black
            else -> parts[1] to Color.Black
        }

    return Triple(rank, suitEmoji, suitColor)
}
