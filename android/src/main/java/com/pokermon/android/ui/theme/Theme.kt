package com.pokermon.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Poker table theme definitions - popular casino colors
enum class PokerTableTheme(
    val displayName: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val tableColor: Color,
) {
    CLASSIC_GREEN(
        "Classic Green",
        "Traditional casino green table",
        Color(0xFF1B5E20), // Dark green
        Color(0xFF4CAF50), // Green
        Color(0xFF2E7D32), // Medium green
    ),
    ROYAL_BLUE(
        "Royal Blue",
        "Elegant blue felt table",
        Color(0xFF0D47A1), // Deep blue
        Color(0xFF2196F3), // Blue
        Color(0xFF1565C0), // Medium blue
    ),
    CRIMSON_RED(
        "Crimson Red",
        "Bold red casino style",
        Color(0xFFB71C1C), // Dark red
        Color(0xFFF44336), // Red
        Color(0xFFD32F2F), // Medium red
    ),
    MIDNIGHT_BLACK(
        "Midnight Black",
        "Sophisticated black table",
        Color(0xFF212121), // Dark gray
        Color(0xFF424242), // Gray
        Color(0xFF303030), // Medium gray
    ),
    BOURBON_BROWN(
        "Bourbon Brown",
        "Warm leather table style",
        Color(0xFF3E2723), // Dark brown
        Color(0xFF8D6E63), // Brown
        Color(0xFF5D4037), // Medium brown
    ),
}

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF1976D2),
        secondary = Color(0xFF388E3C),
        tertiary = Color(0xFFFF9800),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF2196F3),
        secondary = Color(0xFF4CAF50),
        tertiary = Color(0xFFFFC107),
    )

// Poker table themed color schemes
private fun createPokerTableColorScheme(
    theme: PokerTableTheme,
    isDark: Boolean,
) = if (isDark) {
    darkColorScheme(
        primary = theme.primaryColor,
        secondary = theme.secondaryColor,
        tertiary = theme.tableColor,
        surface = theme.tableColor.copy(alpha = 0.8f),
        background = theme.tableColor.copy(alpha = 0.9f),
    )
} else {
    lightColorScheme(
        primary = theme.primaryColor,
        secondary = theme.secondaryColor,
        tertiary = theme.tableColor,
        surface = theme.tableColor.copy(alpha = 0.1f),
        background = Color.White,
    )
}

@Composable
fun PokerGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    pokerTableTheme: PokerTableTheme? = null,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            pokerTableTheme != null -> createPokerTableColorScheme(pokerTableTheme, darkTheme)
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
