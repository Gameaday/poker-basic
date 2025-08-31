package com.pokermon;

/**
 * Centralized version management for the Pokermon application.
 * This class serves as the single point of truth for all version information
 * across desktop JAR, Android APK, and other build targets.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public final class Version {
    
    /**
     * The current version of the application.
     * This should be the single source of truth for version numbers.
     */
    public static final String VERSION = "1.0.0";
    
    /**
     * The application name - short for "Poker Monster" after the game features.
     */
    public static final String APP_NAME = "Pokermon";
    
    /**
     * Full application display name.
     */
    public static final String APP_DISPLAY_NAME = "Pokermon - Poker Monster Game";
    
    /**
     * Application description for use in various contexts.
     */
    public static final String APP_DESCRIPTION = "Educational poker game demonstrating cross-platform development with multiple game modes";
    
    /**
     * Version code for Android builds - increments with each release.
     * This ensures proper APK update handling on Android devices.
     */
    public static final int VERSION_CODE = 1;
    
    /**
     * Build timestamp placeholder for future use.
     */
    public static final String BUILD_TIMESTAMP = "${maven.build.timestamp}";
    
    /**
     * Copyright and attribution information.
     */
    public static final String COPYRIGHT = "© 2024 Carl Nelson (@Gameaday)";
    
    /**
     * Creator attribution for all game coding and concepts.
     */
    public static final String CREATOR = "Carl Nelson (@Gameaday)";
    
    /**
     * Get formatted version information.
     * @return Formatted version string
     */
    public static String getVersionInfo() {
        return APP_NAME + " version " + VERSION;
    }
    
    /**
     * Get full application information.
     * @return Complete app info string
     */
    public static String getFullInfo() {
        return APP_DISPLAY_NAME + " v" + VERSION + " - " + APP_DESCRIPTION;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Version() {
        throw new UnsupportedOperationException("Utility class");
    }
}