package com.pokermon

/**
 * Centralized version management for the Pokermon application.
 * This object serves as the single point of truth for all version information
 * across desktop JAR, Android APK, and other build targets.
 *
 * Versioning uses different approaches per platform:
 * - JAR builds: Git commit count-based versioning for reproducible desktop builds
 * - Android builds: Timestamp-based versioning for reliable APK update ordering
 *
 * This hybrid approach ensures proper version management across all build targets
 * while maintaining semantic versioning for major and minor releases.
 *
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object Version {
    /**
     * The current version of the application with dynamic patch version for JAR builds.
     * Format: major.minor.commitCount (e.g., "1.0.4" where 4 is the git commit count)
     * This value is replaced during Maven build process with actual git commit count from main branch.
     * Android builds use timestamp-based versioning instead (see android/build.gradle).
     * Fallback to "1.0.0" if replacement fails to ensure valid version string.
     */
    const val VERSION = "1.0.@git.commit.count@"

    /**
     * The application name - short for "Poker Monster" after the game features.
     */
    const val APP_NAME = "Pokermon"

    /**
     * Full application display name.
     */
    const val APP_DISPLAY_NAME = "Pokermon - Poker Monster Game"

    /**
     * Application description for use in various contexts.
     */
    const val APP_DESCRIPTION = "Educational poker game demonstrating cross-platform development with multiple game modes"

    /**
     * Version code for JAR builds - dynamically generated from git commit count from main branch.
     * For Android builds, timestamp-based version codes are used instead (calculated in android/build.gradle).
     * This ensures proper update handling across different build targets with platform-appropriate versioning.
     * This value is replaced during Maven build process with actual git commit count from main branch.
     * Fallback to 1 if replacement fails to ensure valid version code.
     */
    const val VERSION_CODE_STR = "@git.commit.count@"

    /**
     * Build timestamp from Maven build process.
     */
    const val BUILD_TIMESTAMP = "\${maven.build.timestamp}"

    /**
     * Copyright and attribution information.
     */
    const val COPYRIGHT = "© 2024 Carl Nelson (@Gameaday)"

    /**
     * Creator attribution for all game coding and concepts.
     */
    const val CREATOR = "Carl Nelson (@Gameaday)"

    /**
     * Get formatted version information with validation.
     * @return Formatted version string with fallback if replacement failed
     */
    fun getVersionInfo(): String = "$APP_NAME version ${getValidatedVersion()}"

    /**
     * Get validated version string with fallback for failed placeholder replacement.
     * @return Valid version string (falls back to "1.0.0" if placeholder replacement failed)
     */
    fun getValidatedVersion(): String {
        // Use string concatenation to avoid Maven replacement affecting this check
        val placeholder = "@" + "git.commit.count" + "@"
        return if (VERSION.contains(placeholder)) {
            // Placeholder replacement failed - use fallback version
            "1.0.0"
        } else {
            VERSION
        }
    }

    /**
     * Get validated version code with fallback for failed placeholder replacement.
     * @return Valid version code (falls back to 1 if placeholder replacement failed)
     */
    fun getValidatedVersionCode(): Int {
        // Use string concatenation to avoid Maven replacement affecting this check
        val placeholder = "@" + "git.commit.count" + "@"
        return if (VERSION_CODE_STR.contains(placeholder)) {
            // Placeholder replacement failed - use fallback version code
            1
        } else {
            try {
                VERSION_CODE_STR.toInt()
            } catch (e: NumberFormatException) {
                1
            }
        }
    }

    /**
     * Get full application information.
     * @return Complete app info string
     */
    fun getFullInfo(): String = "$APP_DISPLAY_NAME v${getValidatedVersion()} - $APP_DESCRIPTION"

    /**
     * Get detailed version information including commit count.
     * @return Detailed version info with build details
     */
    fun getDetailedVersionInfo(): String = "$APP_NAME v${getValidatedVersion()} (build ${getValidatedVersionCode()}) - $BUILD_TIMESTAMP"
}
