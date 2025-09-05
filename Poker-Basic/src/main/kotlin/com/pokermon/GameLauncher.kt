package com.pokermon

/**
 * Main entry point for the Pokermon application.
 * Provides a unified launcher that defaults to GUI mode with options for console mode.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object GameLauncher {
    
    /**
     * Main entry point for the application.
     * Defaults to modern JavaFX UI unless console mode is explicitly requested.
     * 
     * @param args Command line arguments
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Parse command line arguments
        if (args.isEmpty()) {
            // Default behavior: launch modern UI
            launchModern()
            return
        }
        
        for (arg in args) {
            when (arg.lowercase()) {
                "-h", "--help" -> {
                    showHelp()
                    return
                }
                "-v", "--version" -> {
                    showVersion()
                    return
                }
                "-b", "--basic", "--console" -> {
                    launchConsole()
                    return
                }
                "-m", "--mode" -> {
                    launchModeSelection()
                    return
                }
                else -> {
                    System.err.println("Unknown argument: $arg")
                    System.err.println("Use -h or --help for usage information.")
                    kotlin.system.exitProcess(1)
                }
            }
        }
    }
    
    /**
     * Launch the modern JavaFX UI version of the game.
     */
    private fun launchModern() {
        println("Starting ${Version.APP_NAME} (Modern JavaFX UI)...")
        try {
            // Use reflection to call the Kotlin JavaFX launcher
            val modernClass = Class.forName("com.pokermon.modern.ModernMainKt")
            val mainMethod = modernClass.getMethod("main")
            mainMethod.invoke(null)
        } catch (e: Exception) {
            System.err.println("Failed to launch Modern UI: ${e.message}")
            System.err.println("Modern UI dependencies may not be available.")
            System.err.println("Falling back to console mode...")
            launchConsole()
        }
    }
    
    /**
     * Launch the console version of the game.
     */
    private fun launchConsole() {
        println("Starting ${Version.APP_NAME} (Console Mode)...")
        ConsoleMain.main(emptyArray())
    }
    
    /**
     * Launch the console version with interactive mode selection.
     */
    private fun launchModeSelection() {
        println("Starting ${Version.APP_NAME} (Console Mode - Interactive Selection)...")
        ConsoleMain.main(emptyArray())
    }
    
    /**
     * Launch the modern JavaFX UI version of the game.
     */
    private fun launchCompose() {
        println("Starting ${Version.APP_NAME} (Modern JavaFX UI)...")
        try {
            // Use reflection to call the Kotlin JavaFX launcher
            val modernClass = Class.forName("com.pokermon.modern.ModernMainKt")
            val mainMethod = modernClass.getMethod("main")
            mainMethod.invoke(null)
        } catch (e: Exception) {
            System.err.println("Failed to launch Modern UI: ${e.message}")
            System.err.println("Modern UI dependencies may not be available.")
            System.err.println("Falling back to console mode...")
            launchConsole()
        }
    }
    
    /**
     * Display version information.
     */
    private fun showVersion() {
        println(Version.getVersionInfo())
        println("A Java-based poker game with GUI and console modes.")
        println(Version.COPYRIGHT)
    }
    
    /**
     * Display comprehensive help information.
     */
    private fun showHelp() {
        println(Version.APP_DISPLAY_NAME)
        println("Version: ${Version.getValidatedVersion()}")
        println(Version.COPYRIGHT)
        println()
        println("USAGE:")
        println("  java -jar pokermon-${Version.getValidatedVersion()}.jar [OPTIONS]")
        println()
        println("DESCRIPTION:")
        println("  ${Version.APP_DESCRIPTION}.")
        println("  Defaults to modern JavaFX UI for the best cross-platform experience.")
        println()
        println("OPTIONS:")
        println("  (no arguments)     Launch modern JavaFX UI (default, recommended)")
        println("  -b, --basic        Launch console/text mode")
        println("      --console      Same as --basic")
        println("  -m, --mode         Launch console mode with interactive game mode selection")
        println("  -h, --help         Show this help message")
        println("  -v, --version      Show version information")
        println()
        println("ARCHITECTURE:")
        println("  The codebase has been reorganized for better separation of concerns:")
        println("  📦 com.pokermon.api/           - Core game configuration & enums")
        println("  📦 com.pokermon.core/          - Business logic & game engine")
        println("  📦 com.pokermon.interfaces/    - UI interface implementations")
        println("  📦 com.pokermon/               - Legacy compatibility layer")
        println()
        println("EXAMPLES:")
        println("  java -jar pokermon-${Version.getValidatedVersion()}.jar")
        println("    Start the game with modern JavaFX UI (default)")
        println()
        println("  java -jar pokermon-${Version.getValidatedVersion()}.jar --basic")
        println("    Start the game in console mode")
        println()
        println("  java -jar pokermon-${Version.getValidatedVersion()}.jar --mode")
        println("    Start with interactive game mode selection (Classic, Adventure, Safari, Ironman)")
        println()
        println("  java -jar pokermon-${Version.getValidatedVersion()}.jar --help")
        println("    Display this help information")
        println()
        println("GAME FEATURES:")
        println("  - Multiple poker variants supported")
        println("  - Monster gameplay modes (Adventure, Safari, Ironman)")
        println("  - AI opponents with different skill levels")
        println("  - Statistics tracking and analysis")
        println("  - Flexible hand evaluation system")
        println("  - Educational code improvement examples")
        println("  - Cross-platform UI with Kotlin/JavaFX")
        println("  - Touch, mouse, and gamepad support")
        println("  - Customizable themes and settings")
        println()
        println("CREATOR:")
        println("  All game coding and concepts by ${Version.CREATOR}")
        println()
        println("For more information, visit: https://github.com/Gameaday/poker-basic")
    }
}