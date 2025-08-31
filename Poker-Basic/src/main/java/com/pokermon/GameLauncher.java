package com.pokermon;

/**
 * Main entry point for the Pokermon application.
 * Provides a unified launcher that defaults to GUI mode with options for console mode.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class GameLauncher {
    
    /**
     * Main entry point for the application.
     * Defaults to modern JavaFX UI unless console mode is explicitly requested.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Parse command line arguments
        if (args.length == 0) {
            // Default behavior: launch modern UI
            launchModern();
            return;
        }
        
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "-h":
                case "--help":
                    showHelp();
                    return;
                    
                case "-v":
                case "--version":
                    showVersion();
                    return;
                    
                case "-b":
                case "--basic":
                case "--console":
                    launchConsole();
                    return;
                    
                default:
                    System.err.println("Unknown argument: " + arg);
                    System.err.println("Use -h or --help for usage information.");
                    System.exit(1);
            }
        }
    }
    
    /**
     * Launch the modern JavaFX UI version of the game.
     */
    private static void launchModern() {
        System.out.println("Starting " + Version.APP_NAME + " (Modern JavaFX UI)...");
        try {
            // Use reflection to call the Kotlin JavaFX launcher
            Class<?> modernClass = Class.forName("com.pokermon.modern.ModernMainKt");
            java.lang.reflect.Method mainMethod = modernClass.getMethod("main");
            mainMethod.invoke(null);
        } catch (Exception e) {
            System.err.println("Failed to launch Modern UI: " + e.getMessage());
            System.err.println("Modern UI dependencies may not be available.");
            System.err.println("Falling back to console mode...");
            launchConsole();
        }
    }
    
    /**
     * Launch the console version of the game.
     */
    private static void launchConsole() {
        System.out.println("Starting " + Version.APP_NAME + " (Console Mode)...");
        ConsoleMain.main(new String[0]);
    }
    
    /**
     * Launch the modern JavaFX UI version of the game.
     */
    private static void launchCompose() {
        System.out.println("Starting " + Version.APP_NAME + " (Modern JavaFX UI)...");
        try {
            // Use reflection to call the Kotlin JavaFX launcher
            Class<?> modernClass = Class.forName("com.pokermon.modern.ModernMainKt");
            java.lang.reflect.Method mainMethod = modernClass.getMethod("main");
            mainMethod.invoke(null);
        } catch (Exception e) {
            System.err.println("Failed to launch Modern UI: " + e.getMessage());
            System.err.println("Modern UI dependencies may not be available.");
            System.err.println("Falling back to console mode...");
            launchConsole();
        }
    }
    
    /**
     * Display version information.
     */
    private static void showVersion() {
        System.out.println(Version.getVersionInfo());
        System.out.println("A Java-based poker game with GUI and console modes.");
        System.out.println(Version.COPYRIGHT);
    }
    
    /**
     * Display comprehensive help information.
     */
    private static void showHelp() {
        System.out.println(Version.APP_DISPLAY_NAME);
        System.out.println("Version: " + Version.VERSION);
        System.out.println(Version.COPYRIGHT);
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("  java -jar pokermon-" + Version.VERSION + ".jar [OPTIONS]");
        System.out.println();
        System.out.println("DESCRIPTION:");
        System.out.println("  " + Version.APP_DESCRIPTION + ".");
        System.out.println("  Defaults to modern JavaFX UI for the best cross-platform experience.");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("  (no arguments)     Launch modern JavaFX UI (default, recommended)");
        System.out.println("  -b, --basic        Launch console/text mode");
        System.out.println("      --console      Same as --basic");
        System.out.println("  -h, --help         Show this help message");
        System.out.println("  -v, --version      Show version information");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  java -jar pokermon-" + Version.VERSION + ".jar");
        System.out.println("    Start the game with modern JavaFX UI (default)");
        System.out.println();
        System.out.println("  java -jar pokermon-" + Version.VERSION + ".jar --basic");
        System.out.println("    Start the game in console mode");
        System.out.println();
        System.out.println("  java -jar pokermon-" + Version.VERSION + ".jar --help");
        System.out.println("    Display this help information");
        System.out.println();
        System.out.println("GAME FEATURES:");
        System.out.println("  - Multiple poker variants supported");
        System.out.println("  - AI opponents with different skill levels");
        System.out.println("  - Statistics tracking and analysis");
        System.out.println("  - Flexible hand evaluation system");
        System.out.println("  - Educational code improvement examples");
        System.out.println("  - Cross-platform UI with Kotlin/JavaFX");
        System.out.println("  - Touch, mouse, and gamepad support");
        System.out.println("  - Customizable themes and settings");
        System.out.println();
        System.out.println("CREATOR:");
        System.out.println("  All game coding and concepts by " + Version.CREATOR);
        System.out.println();
        System.out.println("For more information, visit: https://github.com/Gameaday/poker-basic");
    }
}