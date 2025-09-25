package com.pokermon

/**
 * Centralized version management for the Pokermon application.
 * This object serves as the single point of truth for all version information
 * across desktop JAR, Android APK, and other build targets.
 *
 * Versioning uses different approaches per platform:
 * - JAR builds: Dynamic versioning from Gradle project version
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
     * The current version of the application with dynamic versioning for JAR builds.
     * This is read from the JAR manifest at runtime for accurate version information.
     * Format: major.minor.patch.timestamp (e.g., "1.1.0.20250907")
     * Android builds use timestamp-based versioning as defined in build.gradle.
     * Fallback to "1.0.0" if manifest reading fails.
     */
    private const val FALLBACK_VERSION = "1.0.0"

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
     * Copyright and attribution information.
     */
    const val COPYRIGHT = "© 2024 Carl Nelson (@Gameaday)"

    /**
     * Creator attribution for all game coding and concepts.
     */
    const val CREATOR = "Carl Nelson (@Gameaday)"

    /**
     * Get the application version from the JAR manifest at runtime.
     * This ensures the version is always accurate and matches the build.
     * @return Valid version string from manifest or fallback version
     */
    private fun getVersionFromManifest(): String {
        return try {
            // Try to read version from the JAR manifest
            val clazz = Version::class.java
            val packageName = clazz.`package`?.implementationVersion
            if (packageName != null && packageName.isNotBlank()) {
                packageName
            } else {
                // Fallback: try to read from resources
                val resourceUrl = clazz.getResource("${clazz.simpleName}.class")
                if (resourceUrl != null && resourceUrl.toString().contains(".jar")) {
                    // Extract version from JAR file name if possible
                    val jarPath = resourceUrl.toString()
                    val versionRegex = """pokermon-([0-9]+\.[0-9]+\.[0-9]+\.[0-9]+)-fat\.jar""".toRegex()
                    versionRegex.find(jarPath)?.groupValues?.get(1) ?: FALLBACK_VERSION
                } else {
                    FALLBACK_VERSION
                }
            }
        } catch (e: Exception) {
            // Fallback version if any error occurs
            FALLBACK_VERSION
        }
    }

    /**
     * Get formatted version information with validation.
     * @return Formatted version string with fallback if reading failed
     */
    fun getVersionInfo(): String = "$APP_NAME version ${getValidatedVersion()}"

    /**
     * Get validated version string with runtime manifest reading.
     * @return Valid version string from manifest or fallback version
     */
    fun getValidatedVersion(): String = getVersionFromManifest()

    /**
     * Get validated version code for compatibility.
     * @return Version code derived from version string
     */
    fun getValidatedVersionCode(): Int {
        return try {
            val version = getValidatedVersion()
            // Convert version string to integer code (remove dots and take first 8 digits)
            version.replace(".", "").take(8).toIntOrNull() ?: 1
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Get full application information.
     * @return Complete app info string
     */
    fun getFullInfo(): String = "$APP_DISPLAY_NAME v${getValidatedVersion()} - $APP_DESCRIPTION"

    /**
     * Get detailed version information including build details.
     * @return Detailed version info with build details
     */
    fun getDetailedVersionInfo(): String {
        val buildTime =
            try {
                val clazz = Version::class.java
                clazz.`package`?.implementationTitle ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        return "$APP_NAME v${getValidatedVersion()} - $buildTime"
    }
}
