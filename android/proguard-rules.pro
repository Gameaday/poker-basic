# ProGuard rules for Poker Game Android APK

# CRITICAL: Exclude JavaFX and desktop classes completely from Android builds
# JavaFX is not available on Android platform
-dontwarn javafx.**
-dontwarn com.sun.javafx.**
-dontwarn sun.util.logging.PlatformLogger
-dontnote javafx.**

# Explicitly exclude desktop UI classes that use JavaFX
-dontwarn com.pokermon.modern.**
-dontwarn com.pokermon.NewJFrame**
-dontwarn com.pokermon.ConsoleMain**

# Keep Android app classes
-keep class com.pokermon.android.** { *; }

# Keep only core game logic classes (Android-compatible)
-keep class com.pokermon.Card { *; }
-keep class com.pokermon.CardPackManager { *; }
-keep class com.pokermon.GameLauncher { *; }
-keep class com.pokermon.PokerGame { *; }
-keep class com.pokermon.Player { *; }
-keep class com.pokermon.hand.** { *; }
-keep class com.pokermon.bridge.** { *; }

# Keep data classes and game state
-keep class com.pokermon.android.data.** { *; }

# Keep Kotlin Compose UI components
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }

# Keep Material Design components
-keep class androidx.compose.material3.** { *; }

# Keep Android Core and Lifecycle components  
-keep class androidx.core.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.activity.compose.** { *; }

# Keep card and game resource files
-keep class **.R$* { *; }

# Preserve enum classes for game logic
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep serialization for user profiles and game state
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Keep reflection access for JSON serialization
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Don't warn about missing platform classes
-dontwarn java.lang.invoke.StringConcatFactory

# Additional exclusions for desktop-only classes
-dontwarn javax.swing.**
-dontwarn java.awt.**
-dontwarn java.beans.**

# Optimize but don't obfuscate for educational purposes
-dontobfuscate
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*