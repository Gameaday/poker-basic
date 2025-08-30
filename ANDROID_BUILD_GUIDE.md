# Android Build Setup for Poker Game

This directory contains the Android build infrastructure for creating APK files alongside the existing JAR builds.

## Quick Start

### Prerequisites
- Internet connection (required for Android SDK download)
- Java 17 (already configured in the main project)

### Building Android APK

1. **First-time setup** (requires internet):
   ```bash
   # From project root
   ./gradlew assembleDebug
   ```

2. **Build outputs**:
   - APK file: `app/build/outputs/apk/debug/app-debug.apk`
   - Installable on Android devices running API 21+ (Android 5.0+)

### Architecture

The Android build reuses the existing Java source code from `Poker-Basic/src/main/java/com/pokermon/` through Gradle's `sourceSets` configuration. This means:

- ✅ Same game logic for both JAR and APK
- ✅ No code duplication
- ✅ Consistent behavior across platforms

### Files Structure

```
poker-basic/
├── Poker-Basic/           # Maven project (JAR build)
│   ├── src/main/java/com/pokermon/  # Shared Java source
│   └── pom.xml
├── app/                   # Android module (APK build)
│   ├── src/main/
│   │   ├── java/com/pokermon/MainActivity.java  # Android UI
│   │   ├── AndroidManifest.xml
│   │   └── res/           # Android resources
│   └── build.gradle       # Android build config
├── build.gradle           # Root build config
├── settings.gradle        # Gradle settings
└── gradlew               # Gradle wrapper
```

### Platform-Specific Features

#### Desktop (JAR)
- Swing GUI (`NewJFrame.java`)
- Console mode (`ConsoleMain.java`)
- Full keyboard/mouse interaction

#### Android (APK)
- Native Android UI (`MainActivity.java`)
- Touch-friendly interface
- Material Design components
- Support for phones and tablets

### Verification

Run the verification task to check dual platform setup:
```bash
./gradlew verifyDualPlatformSetup
```

This checks that both Maven and Android configurations are properly set up.

### Network Requirements

- **Maven/JAR builds**: Work offline after initial dependency download
- **Android/APK builds**: Require internet for Android SDK components

### Troubleshooting

1. **"Plugin not found" errors**: Ensure internet connection for first Android build
2. **SDK not found**: Android SDK will be automatically downloaded on first build
3. **Build fails in CI**: Expected in environments without internet access

The Android infrastructure is fully configured and ready for development environments with network access.