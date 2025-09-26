package com.pokermon.android.store

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Play Store compliance and privacy management for Pokermon.
 * Handles privacy policy, terms of service, and data protection requirements.
 * 
 * @author Pokermon Store Compliance System
 * @version 1.0.0
 */
class StoreCompliance private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: StoreCompliance? = null
        
        fun getInstance(context: Context): StoreCompliance {
            return instance ?: synchronized(this) {
                instance ?: StoreCompliance(context.applicationContext).also { instance = it }
            }
        }
        
        // App information constants
        const val APP_NAME = "Pokermon"
        const val APP_VERSION = "1.1.0"
        const val DEVELOPER_NAME = "Carl Nelson (@Gameaday)"
        const val DEVELOPER_EMAIL = "contact@pokermon.game"
        const val PRIVACY_POLICY_URL = "https://pokermon.game/privacy"
        const val TERMS_OF_SERVICE_URL = "https://pokermon.game/terms"
        const val SUPPORT_URL = "https://pokermon.game/support"
        
        // Age rating information
        const val AGE_RATING = "Teen (13+)"
        const val CONTENT_DESCRIPTION = "Simulated gambling, mild fantasy violence"
    }
    
    /**
     * App permissions and their justifications
     */
    enum class AppPermission(
        val permissionName: String,
        val justification: String,
        val isRequired: Boolean
    ) {
        INTERNET(
            "android.permission.INTERNET",
            "Required for online features and analytics",
            false
        ),
        ACCESS_NETWORK_STATE(
            "android.permission.ACCESS_NETWORK_STATE",
            "Check network connectivity for online features",
            false
        ),
        WRITE_EXTERNAL_STORAGE(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "Save game data and preferences (Android 10 and below)",
            false
        ),
        VIBRATE(
            "android.permission.VIBRATE", 
            "Haptic feedback for enhanced gameplay experience",
            false
        )
    }
    
    /**
     * Privacy compliance categories
     */
    enum class DataCategory(
        val displayName: String,
        val description: String,
        val isCollected: Boolean
    ) {
        GAME_PROGRESS(
            "Game Progress",
            "Save games, achievements, and statistics stored locally",
            true
        ),
        USER_PREFERENCES(
            "User Settings",
            "Audio settings, theme preferences, and UI customizations",
            true
        ),
        ANALYTICS(
            "Usage Analytics",
            "Anonymous gameplay statistics for app improvement",
            false
        ),
        CRASH_REPORTS(
            "Crash Reports",
            "Automated crash reports to improve app stability",
            false
        ),
        ADVERTISING_DATA(
            "Advertising Data",
            "No advertising data is collected or shared",
            false
        ),
        PERSONAL_INFO(
            "Personal Information",
            "No personal information is collected or stored",
            false
        )
    }
    
    /**
     * Get privacy policy content
     */
    fun getPrivacyPolicyContent(): PrivacyPolicyContent {
        return PrivacyPolicyContent(
            lastUpdated = "September 26, 2024",
            sections = listOf(
                PrivacySection(
                    "Information We Collect",
                    "Pokermon is designed with privacy in mind. We collect minimal data necessary for gameplay functionality."
                ),
                PrivacySection(
                    "Local Data Storage",
                    "Game progress, settings, and achievements are stored locally on your device. This data never leaves your device unless you explicitly choose to back it up."
                ),
                PrivacySection(
                    "No Personal Data Collection",
                    "We do not collect, store, or transmit any personal information, email addresses, phone numbers, or identifying information."
                ),
                PrivacySection(
                    "Third-Party Services",
                    "Pokermon may use anonymous analytics services to improve app performance. These services do not collect personal information."
                ),
                PrivacySection(
                    "Data Security",
                    "All game data is stored securely on your device using Android's built-in security features."
                ),
                PrivacySection(
                    "Children's Privacy",
                    "Pokermon is designed for users 13 years and older. We do not knowingly collect data from children under 13."
                ),
                PrivacySection(
                    "Contact Information",
                    "For privacy concerns, contact us at $DEVELOPER_EMAIL"
                )
            )
        )
    }
    
    /**
     * Get terms of service content
     */
    fun getTermsOfServiceContent(): TermsOfServiceContent {
        return TermsOfServiceContent(
            lastUpdated = "September 26, 2024",
            sections = listOf(
                TermsSection(
                    "Acceptance of Terms",
                    "By downloading and using Pokermon, you agree to these terms of service."
                ),
                TermsSection(
                    "Game Usage",
                    "Pokermon is an educational poker game for entertainment purposes only. No real money gambling is involved."
                ),
                TermsSection(
                    "Age Requirements",
                    "Users must be at least 13 years old to play Pokermon. Users under 18 should have parental supervision."
                ),
                TermsSection(
                    "Intellectual Property",
                    "All game content, including code, graphics, and audio, is owned by $DEVELOPER_NAME and protected by copyright."
                ),
                TermsSection(
                    "Prohibited Activities",
                    "Users may not reverse engineer, modify, or distribute the app without permission."
                ),
                TermsSection(
                    "Limitation of Liability",
                    "The app is provided 'as is' without warranties. We are not liable for any damages from app usage."
                ),
                TermsSection(
                    "Termination",
                    "We may terminate access to the app for users who violate these terms."
                ),
                TermsSection(
                    "Contact Information",
                    "For questions about these terms, contact us at $DEVELOPER_EMAIL"
                )
            )
        )
    }
    
    /**
     * Get app compliance information for store listing
     */
    fun getComplianceInfo(): AppComplianceInfo {
        return AppComplianceInfo(
            appName = APP_NAME,
            version = APP_VERSION,
            developer = DEVELOPER_NAME,
            ageRating = AGE_RATING,
            contentDescription = CONTENT_DESCRIPTION,
            permissions = AppPermission.values().toList(),
            dataCategories = DataCategory.values().toList(),
            isGamblingApp = false,
            hasInAppPurchases = false,
            hasAds = false,
            requiresInternet = false,
            supportedAndroidVersions = "Android ${Build.VERSION_CODES.P}+ (API 28+)",
            targetSdkVersion = 34
        )
    }
    
    /**
     * Open privacy policy in browser
     */
    fun openPrivacyPolicy() {
        openUrl(PRIVACY_POLICY_URL)
    }
    
    /**
     * Open terms of service in browser
     */
    fun openTermsOfService() {
        openUrl(TERMS_OF_SERVICE_URL)
    }
    
    /**
     * Open support page
     */
    fun openSupport() {
        openUrl(SUPPORT_URL)
    }
    
    /**
     * Open app settings
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    /**
     * Check if app meets Play Store requirements
     */
    fun validatePlayStoreCompliance(): PlayStoreComplianceResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Check required components
        if (getPrivacyPolicyContent().sections.size < 5) {
            issues.add("Privacy policy needs more comprehensive sections")
        }
        
        if (getTermsOfServiceContent().sections.size < 5) {
            issues.add("Terms of service needs more detailed sections")
        }
        
        // Check permissions
        val requestedPermissions = AppPermission.values().filter { it.isRequired }
        if (requestedPermissions.size > 3) {
            warnings.add("Consider reducing the number of required permissions")
        }
        
        // Check content rating
        if (AGE_RATING.contains("Teen")) {
            warnings.add("Ensure poker gameplay is appropriate for teen audience")
        }
        
        return PlayStoreComplianceResult(
            isCompliant = issues.isEmpty(),
            criticalIssues = issues,
            warnings = warnings,
            complianceScore = calculateComplianceScore(issues, warnings)
        )
    }
    
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    private fun calculateComplianceScore(issues: List<String>, warnings: List<String>): Float {
        val totalChecks = 10f
        val issueWeight = 2f
        val warningWeight = 0.5f
        
        val deductions = (issues.size * issueWeight) + (warnings.size * warningWeight)
        return ((totalChecks - deductions) / totalChecks * 100).coerceIn(0f, 100f)
    }
}

// Data classes for privacy and compliance information
data class PrivacyPolicyContent(
    val lastUpdated: String,
    val sections: List<PrivacySection>
)

data class PrivacySection(
    val title: String,
    val content: String
)

data class TermsOfServiceContent(
    val lastUpdated: String,
    val sections: List<TermsSection>
)

data class TermsSection(
    val title: String,
    val content: String
)

data class AppComplianceInfo(
    val appName: String,
    val version: String,
    val developer: String,
    val ageRating: String,
    val contentDescription: String,
    val permissions: List<StoreCompliance.AppPermission>,
    val dataCategories: List<StoreCompliance.DataCategory>,
    val isGamblingApp: Boolean,
    val hasInAppPurchases: Boolean,
    val hasAds: Boolean,
    val requiresInternet: Boolean,
    val supportedAndroidVersions: String,
    val targetSdkVersion: Int
)

data class PlayStoreComplianceResult(
    val isCompliant: Boolean,
    val criticalIssues: List<String>,
    val warnings: List<String>,
    val complianceScore: Float
)