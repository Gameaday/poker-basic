// Native entry point for Kotlin/Native executables
// This provides the main function for native compilation

package com.pokermon

import kotlinx.coroutines.runBlocking

/**
 * Native main function for Kotlin/Native compilation
 * This entry point is used when building native executables
 */
fun main(args: Array<String>) {
    println("=== Pokermon Native Build ===")
    println("Version: ${getVersion()}")
    println("Platform: Native Executable")
    println()
    
    // Parse command line arguments
    val options = parseCommandLineArgs(args)
    
    when {
        options.contains("--help") || options.contains("-h") -> {
            showHelp()
        }
        options.contains("--version") || options.contains("-v") -> {
            showVersion()
        }
        options.contains("--basic") -> {
            runBasicGame()
        }
        options.contains("--console") -> {
            runConsoleGame()
        }
        else -> {
            showHelp()
            println("\nStarting default console mode...")
            runConsoleGame()
        }
    }
}

/**
 * Parse command line arguments into a set of options
 */
private fun parseCommandLineArgs(args: Array<String>): Set<String> {
    return args.toSet()
}

/**
 * Display help information
 */
private fun showHelp() {
    println("Pokermon - Kotlin Native Poker Game")
    println("Usage: pokermon [options]")
    println()
    println("Options:")
    println("  --help, -h      Show this help message")
    println("  --version, -v   Show version information")
    println("  --basic         Run basic poker game")
    println("  --console       Run console interface")
    println()
    println("Native executable - no JVM required!")
}

/**
 * Show version information
 */
private fun showVersion() {
    println("Pokermon Native v${getVersion()}")
    println("Built with Kotlin/Native")
    println("Platform: ${getPlatform()}")
}

/**
 * Get the current version
 */
private fun getVersion(): String {
    return "1.1.0" // TODO: Make this dynamic from build
}

/**
 * Get the current platform
 */
private fun getPlatform(): String {
    return when {
        // We'll determine this at compile time
        else -> "Unknown"
    }
}

/**
 * Run the basic poker game
 */
private fun runBasicGame() {
    println("Starting Basic Poker Game...")
    
    try {
        // Create a basic game instance using constructor
        val game = Game(
            handSize = 5,
            maxPlayers = 3,
            startingChips = 1000,
            gameMode = GameMode.CLASSIC
        )
            
        val engine = GameEngine(game)
        
        println("Game created successfully!")
        println("Max players: ${game.maxPlayers}")
        println("Starting chips: ${game.startingChips}")
        println("Game mode: ${game.gameMode}")
        
        // Initialize and start basic game loop with player names
        val playerNames = arrayOf("Player1", "AI-Bot1", "AI-Bot2")
        val success = engine.initializeGame(playerNames)
        
        if (success) {
            runBasicGameLoop(engine)
        } else {
            println("Failed to initialize game")
        }
        
    } catch (e: Exception) {
        println("Error running game: ${e.message}")
    }
}

/**
 * Run the console game interface
 */
private fun runConsoleGame() {
    println("Starting Console Game Interface...")
    
    try {
        // For now, delegate to basic game
        // TODO: Implement full console UI
        runBasicGame()
    } catch (e: Exception) {
        println("Error running console game: ${e.message}")
    }
}

/**
 * Basic game loop for native execution
 */
private fun runBasicGameLoop(engine: GameEngine) {
    println("\n=== Game Started ===")
    
    // Simple demonstration game loop
    var round = 1
    while (round <= 3) {
        println("\n--- Round $round ---")
        
        try {
            // Deal cards (simplified)
            println("Dealing cards...")
            
            // Simulate some game actions
            println("Players making decisions...")
            
            // Show basic game state
            println("Round $round completed")
            
        } catch (e: Exception) {
            println("Error in round $round: ${e.message}")
            break
        }
        
        round++
    }
    
    println("\n=== Game Finished ===")
    println("Thanks for playing Pokermon!")
}