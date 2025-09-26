package com.pokermon.android.assets

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Monster asset management system for Pokermon.
 * Handles monster images, animations, and visual representations.
 * 
 * @author Pokermon Monster Asset System
 * @version 1.0.0
 */
class MonsterAssets private constructor(
    private val context: Context,
    private val assetManager: AssetManager
) {
    
    companion object {
        @Volatile
        private var instance: MonsterAssets? = null
        
        fun getInstance(context: Context): MonsterAssets {
            return instance ?: synchronized(this) {
                val assetManager = AssetManager.getInstance(context)
                instance ?: MonsterAssets(context.applicationContext, assetManager).also { 
                    instance = it 
                }
            }
        }
        
        // Monster types with their asset names
        private val MONSTER_ASSET_MAPPING = mapOf(
            // Fire type monsters
            "dragon_fire" to "monster_dragon_fire",
            "phoenix_flame" to "monster_phoenix_flame", 
            "salamander_ember" to "monster_salamander_ember",
            "volcano_beast" to "monster_volcano_beast",
            
            // Water type monsters  
            "sea_serpent" to "monster_sea_serpent",
            "kraken_deep" to "monster_kraken_deep",
            "frost_whale" to "monster_frost_whale", 
            "tsunami_rider" to "monster_tsunami_rider",
            
            // Earth type monsters
            "stone_golem" to "monster_stone_golem",
            "mountain_troll" to "monster_mountain_troll",
            "crystal_giant" to "monster_crystal_giant",
            "earthquake_beast" to "monster_earthquake_beast",
            
            // Air type monsters
            "wind_spirit" to "monster_wind_spirit",
            "thunder_eagle" to "monster_thunder_eagle",
            "storm_dragon" to "monster_storm_dragon",
            "cyclone_elemental" to "monster_cyclone_elemental",
            
            // Legendary monsters
            "cosmic_phoenix" to "monster_cosmic_phoenix",
            "void_dragon" to "monster_void_dragon", 
            "time_guardian" to "monster_time_guardian",
            "space_wanderer" to "monster_space_wanderer"
        )
    }
    
    /**
     * Monster rarity levels for asset organization
     */
    enum class MonsterRarity(val displayName: String, val assetSuffix: String) {
        COMMON("Common", "_common"),
        UNCOMMON("Uncommon", "_uncommon"),
        RARE("Rare", "_rare"),
        EPIC("Epic", "_epic"),
        LEGENDARY("Legendary", "_legendary")
    }
    
    /**
     * Monster animation states
     */
    enum class MonsterAnimation(val assetSuffix: String) {
        IDLE("_idle"),
        ATTACK("_attack"),
        DEFEND("_defend"),
        VICTORY("_victory"),
        DEFEAT("_defeat")
    }
    
    /**
     * Load monster image asset
     */
    suspend fun loadMonsterImage(monsterId: String, rarity: MonsterRarity = MonsterRarity.COMMON): Drawable? {
        return withContext(Dispatchers.IO) {
            val assetName = MONSTER_ASSET_MAPPING[monsterId] + rarity.assetSuffix
            assetManager.loadDrawable(assetName, AssetManager.AssetCategory.MONSTERS)
                ?: loadPlaceholderMonsterImage(rarity)
        }
    }
    
    /**
     * Load monster animation frame
     */
    suspend fun loadMonsterAnimation(
        monsterId: String, 
        animation: MonsterAnimation,
        rarity: MonsterRarity = MonsterRarity.COMMON
    ): Drawable? {
        return withContext(Dispatchers.IO) {
            val assetName = MONSTER_ASSET_MAPPING[monsterId] + rarity.assetSuffix + animation.assetSuffix
            assetManager.loadDrawable(assetName, AssetManager.AssetCategory.MONSTERS)
                ?: loadPlaceholderMonsterImage(rarity) // Fallback to static image
        }
    }
    
    /**
     * Load placeholder monster image for missing assets
     */
    suspend fun loadPlaceholderMonsterImage(rarity: MonsterRarity): Drawable? {
        return withContext(Dispatchers.IO) {
            val placeholderName = "placeholder" + rarity.assetSuffix
            assetManager.loadDrawable(placeholderName, AssetManager.AssetCategory.MONSTERS)
                ?: createDefaultPlaceholder()
        }
    }
    
    /**
     * Get all available monster IDs
     */
    fun getAvailableMonsters(): List<String> {
        return MONSTER_ASSET_MAPPING.keys.toList()
    }
    
    /**
     * Get monsters by type (inferred from name)
     */
    fun getMonstersByType(type: String): List<String> {
        return MONSTER_ASSET_MAPPING.keys.filter { 
            it.contains(type.lowercase()) 
        }
    }
    
    /**
     * Validate monster assets
     */
    suspend fun validateMonsterAssets(): MonsterAssetValidationResult {
        return withContext(Dispatchers.IO) {
            val totalMonsters = MONSTER_ASSET_MAPPING.size
            val rarityCount = MonsterRarity.values().size
            val totalExpectedAssets = totalMonsters * rarityCount
            
            var foundAssets = 0
            val missingAssets = mutableListOf<String>()
            
            for ((monsterId, assetName) in MONSTER_ASSET_MAPPING) {
                for (rarity in MonsterRarity.values()) {
                    val fullAssetName = assetName + rarity.assetSuffix
                    val resourceId = context.resources.getIdentifier(
                        fullAssetName, "drawable", context.packageName
                    )
                    
                    if (resourceId != 0) {
                        foundAssets++
                    } else {
                        missingAssets.add(fullAssetName)
                    }
                }
            }
            
            MonsterAssetValidationResult(
                totalExpectedAssets = totalExpectedAssets,
                foundAssets = foundAssets,
                missingAssets = missingAssets,
                validationPercentage = (foundAssets.toFloat() / totalExpectedAssets) * 100
            )
        }
    }
    
    /**
     * Create a simple default placeholder when no assets are available
     */
    private fun createDefaultPlaceholder(): Drawable? {
        return try {
            // Use a simple drawable resource as fallback
            ContextCompat.getDrawable(context, android.R.drawable.ic_menu_gallery)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Pre-load common monster assets for better performance
     */
    suspend fun preloadCommonMonsters() {
        withContext(Dispatchers.IO) {
            // Load the most commonly used monsters
            val commonMonsters = listOf(
                "dragon_fire", "sea_serpent", "stone_golem", "wind_spirit"
            )
            
            for (monsterId in commonMonsters) {
                loadMonsterImage(monsterId, MonsterRarity.COMMON)
            }
        }
    }
}

/**
 * Result of monster asset validation
 */
data class MonsterAssetValidationResult(
    val totalExpectedAssets: Int,
    val foundAssets: Int,
    val missingAssets: List<String>,
    val validationPercentage: Float
) {
    val hasPlaceholdersAvailable: Boolean get() = foundAssets > 0
    val needsAssetCreation: Boolean get() = validationPercentage < 50f
    
    fun getAssetCreationPriority(): List<String> {
        // Return monsters that need assets the most
        return missingAssets.take(10)
    }
}