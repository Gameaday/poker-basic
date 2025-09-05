package com.pokermon

import java.util.*

/**
 * Console-only version of the Pokermon game - Pure Kotlin-native implementation.
 * This class provides a text-based interface using the new unified Kotlin architecture.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version Dynamic (Kotlin-native implementation)
 */
object ConsoleMain {
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("=================================================")
        println("         POKERMON - CONSOLE MODE")
        println("         Pure Kotlin-Native Implementation")
        println("=================================================")
        println()
        
        try {
            // Use the new Kotlin-native Main class
            Main.main(args)
        } catch (e: Exception) {
            println("Console game error: ${e.message}")
            e.printStackTrace()
        }
    }
}