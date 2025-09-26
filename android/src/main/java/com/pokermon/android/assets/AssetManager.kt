package com.pokermon.android.assets

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Centralized asset management system for Pokermon.
 * Handles loading, caching, and efficient management of game assets.
 * 
 * @author Pokermon Asset System
 * @version 1.0.0
 */
class AssetManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: AssetManager? = null
        
        fun getInstance(context: Context): AssetManager {
            return instance ?: synchronized(this) {
                instance ?: AssetManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    // Asset caches for efficient memory management
    private val drawableCache = ConcurrentHashMap<String, Drawable>()
    private val assetValidationCache = ConcurrentHashMap<String, Boolean>()
    
    /**
     * Asset categories for organized management
     */
    enum class AssetCategory(val prefix: String) {
        CARDS("card_"),
        MONSTERS("monster_"),
        UI_ICONS("icon_"),
        BACKGROUNDS("bg_"),
        GAME_MODES("mode_"),
        ACHIEVEMENTS("achievement_")
    }
    
    /**
     * Load a drawable asset with caching
     */
    suspend fun loadDrawable(resourceName: String, category: AssetCategory? = null): Drawable? {
        return withContext(Dispatchers.IO) {
            val fullName = category?.prefix + resourceName
            
            // Check cache first
            drawableCache[fullName]?.let { return@withContext it }
            
            try {
                val resourceId = getResourceId(fullName, "drawable")
                if (resourceId != 0) {
                    val drawable = ContextCompat.getDrawable(context, resourceId)
                    drawable?.let { drawableCache[fullName] = it }
                    drawable
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Pre-load essential assets for faster access
     */
    suspend fun preloadEssentialAssets() {
        withContext(Dispatchers.IO) {
            // Preload card back and common UI elements
            loadDrawable("back", AssetCategory.CARDS)
            loadDrawable("launcher", AssetCategory.UI_ICONS)
            
            // Preload game mode icons
            listOf("classic", "adventure", "safari", "ironman").forEach { mode ->
                loadDrawable(mode, AssetCategory.GAME_MODES)
            }
        }
    }
    
    /**
     * Get all available card assets
     */
    fun getAvailableCardAssets(): List<String> {
        return listOf(
            // Spades
            "ace_of_spades", "two_of_spades", "three_of_spades", "four_of_spades",
            "five_of_spades", "six_of_spades", "seven_of_spades", "eight_of_spades",
            "nine_of_spades", "ten_of_spades", "jack_of_spades", "queen_of_spades", "king_of_spades",
            
            // Hearts
            "ace_of_hearts", "two_of_hearts", "three_of_hearts", "four_of_hearts",
            "five_of_hearts", "six_of_hearts", "seven_of_hearts", "eight_of_hearts",
            "nine_of_hearts", "ten_of_hearts", "jack_of_hearts", "queen_of_hearts", "king_of_hearts",
            
            // Diamonds
            "ace_of_diamonds", "two_of_diamonds", "three_of_diamonds", "four_of_diamonds",
            "five_of_diamonds", "six_of_diamonds", "seven_of_diamonds", "eight_of_diamonds",
            "nine_of_diamonds", "ten_of_diamonds", "jack_of_diamonds", "queen_of_diamonds", "king_of_diamonds",
            
            // Clubs
            "ace_of_clubs", "two_of_clubs", "three_of_clubs", "four_of_clubs",
            "five_of_clubs", "six_of_clubs", "seven_of_clubs", "eight_of_clubs",
            "nine_of_clubs", "ten_of_clubs", "jack_of_clubs", "queen_of_clubs", "king_of_clubs",
            
            // Special cards
            "card_back"
        )
    }
    
    /**
     * Validate that all required assets are present
     */
    suspend fun validateAssets(): AssetValidationResult {
        return withContext(Dispatchers.IO) {
            val missingAssets = mutableListOf<String>()
            val totalAssets = getAvailableCardAssets().size
            var validAssets = 0
            
            // Validate card assets
            for (cardAsset in getAvailableCardAssets()) {
                val resourceId = getResourceId(cardAsset, "drawable")
                if (resourceId != 0) {
                    assetValidationCache[cardAsset] = true
                    validAssets++
                } else {
                    assetValidationCache[cardAsset] = false
                    missingAssets.add(cardAsset)
                }
            }
            
            AssetValidationResult(
                totalAssets = totalAssets,
                validAssets = validAssets,
                missingAssets = missingAssets,
                validationPercentage = (validAssets.toFloat() / totalAssets) * 100
            )
        }
    }
    
    /**
     * Clear asset cache to free memory
     */
    fun clearCache() {
        drawableCache.clear()
        assetValidationCache.clear()
    }
    
    /**
     * Get memory usage statistics
     */
    fun getMemoryUsage(): AssetMemoryStats {
        return AssetMemoryStats(
            cachedDrawables = drawableCache.size,
            validationEntries = assetValidationCache.size,
            estimatedMemoryKB = drawableCache.size * 50 // Rough estimate
        )
    }
    
    /**
     * Helper method to get resource ID by name
     */
    private fun getResourceId(name: String, type: String): Int {
        return context.resources.getIdentifier(name, type, context.packageName)
    }
}

/**
 * Result of asset validation process
 */
data class AssetValidationResult(
    val totalAssets: Int,
    val validAssets: Int,
    val missingAssets: List<String>,
    val validationPercentage: Float
) {
    val isFullyValid: Boolean get() = missingAssets.isEmpty()
    val hasWarnings: Boolean get() = validationPercentage < 100f
}

/**
 * Memory usage statistics for asset management
 */
data class AssetMemoryStats(
    val cachedDrawables: Int,
    val validationEntries: Int,
    val estimatedMemoryKB: Int
)