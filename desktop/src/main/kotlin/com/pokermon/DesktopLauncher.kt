package com.pokermon

import com.pokermon.GameLauncher
import kotlinx.coroutines.runBlocking

/**
 * Desktop launcher for Pokermon - Pure Kotlin-native desktop application entry point.
 * 
 * This launcher provides a native desktop experience using the shared Kotlin-native
 * core functionality, following DRY principles by reusing the same game logic
 * across all platforms.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version Dynamic (based on build timestamp)
 */
object DesktopLauncher {
    
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("=== Pokermon Desktop - Pure Kotlin-Native ===")
        println("Version: ${getVersionInfo()}")
        println("Platform: Desktop Native")
        println("Build: Kotlin-native ${getKotlinVersion()}")
        println()
        
        try {
            // Use the shared Kotlin-native GameLauncher
            GameLauncher.main(args)
        } catch (e: Exception) {
            println("Error launching Pokermon: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Get version information for desktop builds.
     */
    private fun getVersionInfo(): String {
        return try {
            // Use the shared Version object from Kotlin-native core
            com.pokermon.Version.getVersionInfo()
        } catch (e: Exception) {
            "Desktop-${System.currentTimeMillis()}"
        }
    }
    
    /**
     * Get Kotlin version information.
     */
    private fun getKotlinVersion(): String {
        return try {
            KotlinVersion.CURRENT.toString()
        } catch (e: Exception) {
            "1.9.22"
        }
    }
}