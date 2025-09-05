package com.pokermon.database

import java.io.File
import java.util.jar.JarFile

/**
 * Manages card packs for the poker game, providing dynamic discovery
 * of available card art sets and standardized access to card images.
 */
object CardPackManager {
    
    const val TEXT_SYMBOLS = "TEXT_SYMBOLS"
    const val CARDS_RESOURCE_PATH = "Cards"
    
    private val availableCardPacks: MutableMap<String, String> = linkedMapOf()
    
    init {
        discoverCardPacks()
    }
    
    /**
     * Discover available card packs from the resources directory.
     */
    private fun discoverCardPacks() {
        availableCardPacks.clear()
        
        // Always add text symbols option first
        availableCardPacks[TEXT_SYMBOLS] = "Text + Symbols (Classic)"
        
        try {
            // Try to discover card packs from resources
            val cardPackNames = getCardPacksFromResources()
            
            cardPackNames.forEach { packName ->
                // Convert folder name to display name
                val displayName = formatDisplayName(packName)
                availableCardPacks[packName] = displayName
            }
            
        } catch (e: Exception) {
            System.err.println("Warning: Could not discover card packs: ${e.message}")
            // Add TET as fallback if discovery fails
            if ("TET" !in availableCardPacks) {
                availableCardPacks["TET"] = "Eternal Tortoise Cards"
            }
        }
        
        // Always ensure CLASSIC is available as a fallback option
        if ("CLASSIC" !in availableCardPacks) {
            availableCardPacks["CLASSIC"] = "Classic"
        }
    }
    
    /**
     * Get card packs from resources directory, handling both filesystem and JAR scenarios.
     */
    private fun getCardPacksFromResources(): Set<String> {
        val packNames = mutableSetOf<String>()
        
        try {
            // First try to get resource as URL to determine if we're in a JAR
            val classLoader = javaClass.classLoader
            classLoader.getResourceAsStream(CARDS_RESOURCE_PATH)?.use { cardsStream ->
                // Try to get the actual resource path
                val resourcePath = classLoader.getResource(CARDS_RESOURCE_PATH)?.path ?: return@use
                
                if ("!" in resourcePath) {
                    // We're in a JAR file
                    packNames.addAll(getCardPacksFromJar(resourcePath))
                } else {
                    // We're in filesystem (development mode)
                    packNames.addAll(getCardPacksFromFilesystem(resourcePath))
                }
            }
        } catch (e: Exception) {
            // Fallback: assume TET is available
            packNames.add("TET")
        }
        
        return packNames
    }
    
    /**
     * Get card packs from JAR file.
     */
    private fun getCardPacksFromJar(jarResourcePath: String): Set<String> {
        val packNames = mutableSetOf<String>()
        
        try {
            // Extract JAR file path from resource path
            val jarPath = jarResourcePath.substring(5, jarResourcePath.indexOf("!"))
            
            JarFile(jarPath).use { jarFile ->
                val entries = jarFile.entries()
                
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val name = entry.name
                    
                    // Look for directories under Cards/
                    if (name.startsWith("$CARDS_RESOURCE_PATH/") && 
                        name.length > CARDS_RESOURCE_PATH.length + 1 &&
                        name.endsWith("/")) {
                        
                        val packName = name.substring(CARDS_RESOURCE_PATH.length + 1, name.length - 1)
                        if ("/" !in packName) { // Only direct subdirectories
                            packNames.add(packName)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            System.err.println("Error reading JAR: ${e.message}")
        }
        
        return packNames
    }
    
    /**
     * Get card packs from filesystem.
     */
    private fun getCardPacksFromFilesystem(resourcePath: String): Set<String> {
        val packNames = mutableSetOf<String>()
        
        try {
            val cardsDir = File(resourcePath)
            if (cardsDir.exists() && cardsDir.isDirectory) {
                cardsDir.listFiles { file -> file.isDirectory }
                    ?.forEach { subdir ->
                        packNames.add(subdir.name)
                    }
            }
        } catch (e: Exception) {
            System.err.println("Error reading filesystem: ${e.message}")
        }
        
        return packNames
    }
    
    /**
     * Format pack folder name into display name.
     */
    private fun formatDisplayName(packName: String): String {
        return when (packName.uppercase()) {
            "TET" -> "Eternal Tortoise Cards"
            "CLASSIC" -> "Classic"
            else -> {
                // Convert camelCase or snake_case to Title Case
                val result = packName
                    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
                    .replace("_", " ")
                    .lowercase()
                
                // Capitalize first letter of each word
                result.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
        }
    }
    
    /**
     * Get all available card packs as a map of pack name to display name.
     */
    fun getAvailableCardPacks(): Map<String, String> = availableCardPacks.toMap()
    
    /**
     * Get the display name for a card pack.
     */
    fun getDisplayName(packName: String): String = availableCardPacks[packName] ?: packName
    
    /**
     * Check if a card pack is available.
     */
    fun isCardPackAvailable(packName: String): Boolean = packName in availableCardPacks
    
    /**
     * Get the resource path for a card image.
     */
    fun getCardImagePath(packName: String, rankName: String, suitName: String): String? {
        if (packName == TEXT_SYMBOLS) {
            return null // Indicates to use text symbols instead
        }
        
        return "$CARDS_RESOURCE_PATH/$packName/$rankName of $suitName.jpg"
    }
    
    /**
     * Get the resource path for a card back image.
     */
    fun getCardBackImagePath(packName: String): String? {
        if (packName == TEXT_SYMBOLS) {
            return null // Indicates to use text symbols instead
        }
        
        return "$CARDS_RESOURCE_PATH/$packName/card_back.jpg"
    }
    
    /**
     * Refresh the list of available card packs.
     */
    fun refresh() {
        discoverCardPacks()
    }
}