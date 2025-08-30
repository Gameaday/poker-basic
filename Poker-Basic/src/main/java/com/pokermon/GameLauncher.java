package com.pokermon;

/**
 * Main entry point for the Poker Game application.
 * Provides a unified launcher that defaults to GUI mode with options for console mode.
 * 
 * @author Poker Game Team
 * @version 0.08.30
 */
public class GameLauncher {

    private static final String VERSION = "0.08.30";
    private static final String APP_NAME = "Poker Game";
    
    /**
     * Main entry point for the application.
     * Defaults to GUI mode unless console mode is explicitly requested.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Parse command line arguments
        if (args.length == 0) {
            // Default behavior: launch GUI
            launchGUI();
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
     * Launch the GUI version of the game.
     */
    private static void launchGUI() {
        System.out.println("Starting " + APP_NAME + " (GUI Mode)...");
        NewJFrame.main(new String[0]);
    }
    
    /**
     * Launch the console version of the game.
     */
    private static void launchConsole() {
        System.out.println("Starting " + APP_NAME + " (Console Mode)...");
        ConsoleMain.main(new String[0]);
    }
    
    /**
     * Display version information.
     */
    private static void showVersion() {
        System.out.println(APP_NAME + " version " + VERSION);
        System.out.println("A Java-based poker game with GUI and console modes.");
    }
    
    /**
     * Display comprehensive help information.
     */
    private static void showHelp() {
        System.out.println(APP_NAME + " - Educational Poker Game");
        System.out.println("Version: " + VERSION);
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("  java -jar pokermon-" + VERSION + ".jar [OPTIONS]");
        System.out.println();
        System.out.println("DESCRIPTION:");
        System.out.println("  An educational poker game supporting multiple game variants.");
        System.out.println("  Defaults to GUI mode for the best user experience.");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("  (no arguments)     Launch GUI mode (default, recommended)");
        System.out.println("  -b, --basic        Launch console/text mode");
        System.out.println("      --console      Same as --basic");
        System.out.println("  -h, --help         Show this help message");
        System.out.println("  -v, --version      Show version information");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  java -jar pokermon-" + VERSION + ".jar");
        System.out.println("    Start the game in GUI mode (default)");
        System.out.println();
        System.out.println("  java -jar pokermon-" + VERSION + ".jar --basic");
        System.out.println("    Start the game in console mode");
        System.out.println();
        System.out.println("  java -jar pokermon-" + VERSION + ".jar --help");
        System.out.println("    Display this help information");
        System.out.println();
        System.out.println("GAME FEATURES:");
        System.out.println("  - Multiple poker variants supported");
        System.out.println("  - AI opponents with different skill levels");
        System.out.println("  - Statistics tracking and analysis");
        System.out.println("  - Flexible hand evaluation system");
        System.out.println("  - Educational code improvement examples");
        System.out.println();
        System.out.println("For more information, visit: https://github.com/Gameaday/poker-basic");
    }
}