package com.pokermon;

/**
 * Centralized version management for the Pokermon application.
 * This class serves as the single point of truth for all version information
 * across desktop JAR, Android APK, and other build targets.
 * 
 * Version code is dynamically generated based on git commit count to ensure
 * automatic incrementation with each commit, eliminating manual version management
 * while maintaining semantic versioning for major and minor releases.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public final class Version {
    
    /**
     * The current version of the application with dynamic patch version based on commit count.
     * Format: major.minor.commitCount (e.g., "1.0.4" where 4 is the commit count)
     * This value is replaced during build process with actual git commit count.
     */
    public static final String VERSION = "1.0.2";
    
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
     * Version code for Android builds - dynamically generated from git commit count.
     * This ensures proper APK update handling on Android devices and automatic
     * incrementation with each commit without manual intervention.
     * This value is replaced during build process with actual git commit count.
     */
    public static final int VERSION_CODE = 2;
    
    /**
     * Build timestamp from Maven build process.
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
     * Get detailed version information including commit count.
     * @return Detailed version info with build details
     */
    public static String getDetailedVersionInfo() {
        return String.format("%s v%s (build %d) - %s", 
            APP_NAME, VERSION, VERSION_CODE, BUILD_TIMESTAMP);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Version() {
        throw new UnsupportedOperationException("Utility class");
    }
}