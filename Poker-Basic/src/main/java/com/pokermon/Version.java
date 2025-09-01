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
     * This value is replaced during build process with actual git commit count from main branch.
     * Fallback to "1.0.0" if replacement fails to ensure valid version string.
     */
    public static final String VERSION = "1.0.8";
    
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
     * Version code for Android builds - dynamically generated from git commit count from main branch.
     * This ensures proper APK update handling on Android devices and automatic
     * incrementation with each commit without manual intervention.
     * This value is replaced during build process with actual git commit count from main branch.
     * Fallback to 1 if replacement fails to ensure valid version code.
     */
    public static final int VERSION_CODE = 8;
    
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
     * Get formatted version information with validation.
     * @return Formatted version string with fallback if replacement failed
     */
    public static String getVersionInfo() {
        return APP_NAME + " version " + getValidatedVersion();
    }
    
    /**
     * Get validated version string with fallback for failed placeholder replacement.
     * @return Valid version string (falls back to "1.0.0" if placeholder replacement failed)
     */
    public static String getValidatedVersion() {
        // Use string concatenation to avoid Maven replacement affecting this check
        String placeholder = "@" + "git.commit.count" + "@";
        if (VERSION.contains(placeholder)) {
            // Placeholder replacement failed - use fallback version
            return "1.0.0";
        }
        return VERSION;
    }
    
    /**
     * Get validated version code with fallback for failed placeholder replacement.
     * @return Valid version code (falls back to 1 if placeholder replacement failed)
     */
    public static int getValidatedVersionCode() {
        // Use string concatenation to avoid Maven replacement affecting this check
        String placeholder = "@" + "git.commit.count" + "@";
        String versionStr = String.valueOf(VERSION_CODE);
        if (versionStr.contains(placeholder) || VERSION_CODE <= 0) {
            // Placeholder replacement failed or invalid - use fallback version code
            return 1;
        }
        return VERSION_CODE;
    }
    
    /**
     * Get full application information.
     * @return Complete app info string
     */
    public static String getFullInfo() {
        return APP_DISPLAY_NAME + " v" + getValidatedVersion() + " - " + APP_DESCRIPTION;
    }
    
    /**
     * Get detailed version information including commit count.
     * @return Detailed version info with build details
     */
    public static String getDetailedVersionInfo() {
        return String.format("%s v%s (build %d) - %s", 
            APP_NAME, getValidatedVersion(), getValidatedVersionCode(), BUILD_TIMESTAMP);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Version() {
        throw new UnsupportedOperationException("Utility class");
    }
}