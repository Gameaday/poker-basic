# Cross Platform Build System Guide

## Overview

This project supports native builds for all major platforms using a comprehensive cross-platform build system:

- **Windows**: Native `.exe` executables
- **Linux**: Native `.deb` packages  
- **macOS**: Native `.dmg` disk images
- **Android**: Native `.apk` packages
- **Cross-Platform**: Universal `.jar` files

## Build System Architecture

### Technology Stack
- **Java 17**: Core runtime and compilation target
- **Maven**: Primary build system for desktop platforms
- **Gradle**: Android build system
- **jpackage**: Native packaging tool (Java 17+)
- **GitHub Actions**: Automated CI/CD pipeline

### Platform-Specific Build Tools

| Platform | Build Tool | Output Format | Runtime Bundled |
|----------|------------|---------------|-----------------|
| Windows  | jpackage   | .exe installer| Yes            |
| Linux    | jpackage   | .deb package  | Yes            |
| macOS    | jpackage   | .dmg image    | Yes            |
| Android  | Gradle     | .apk package  | N/A (Android Runtime) |
| Cross-Platform | Maven | .jar file | No (requires Java 17+) |

## Building for Specific Platforms

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Internet connection (for dependency downloads)

### Windows Executable (.exe)

```bash
cd Poker-Basic
mvn clean package -Pwindows-exe -DskipTests
```

**Output**: `target/jpackage/PokerGame-0.1b.exe`

**Features**:
- Self-contained Windows installer
- Creates Start Menu shortcuts
- Desktop shortcut option
- Add/Remove Programs integration
- No Java installation required

### Linux Package (.deb)

```bash
cd Poker-Basic
mvn clean package -Plinux-exe -DskipTests
```

**Output**: `target/jpackage/pokergame_0.1b-1_amd64.deb`

**Features**:
- Debian/Ubuntu package format
- Applications menu integration
- Automatic dependency handling
- System service integration ready
- No Java installation required

### macOS Application (.dmg)

```bash
cd Poker-Basic
mvn clean package -Pmacos-exe -DskipTests
```

**Output**: `target/jpackage/PokerGame-0.1b.dmg`

**Features**:
- macOS disk image installer
- Applications folder integration
- Launchpad integration
- Code signing ready (requires certificates)
- No Java installation required

### Android Package (.apk)

```bash
# Requires internet connection for Android SDK
./gradlew :android:assembleDebug
```

**Output**: `android/build/outputs/apk/debug/android-debug.apk`

**Features**:
- Native Android application
- Material Design 3 interface
- Touch-optimized controls
- Supports Android 5.0+ (API 21+)

### Cross-Platform JAR

```bash
cd Poker-Basic
mvn clean package -DskipTests
```

**Output**: `target/pokermon-0.1b.jar` (standard) and `target/pokermon-0.1b-fat.jar` (with dependencies)

**Features**:
- Works on any Java 17+ system
- JavaFX modern UI
- Console mode fallback
- Smallest download size

## Maven Profiles

The project uses Maven profiles for platform-specific builds:

### Available Profiles

| Profile | Activation | Purpose |
|---------|------------|---------|
| `windows-exe` | `-Pwindows-exe` | Windows executable generation |
| `linux-exe` | `-Plinux-exe` | Linux DEB package generation |
| `macos-exe` | `-Pmacos-exe` | macOS DMG generation |
| (default) | automatic | Cross-platform JAR |

### Profile Configuration

Each profile configures the jpackage Maven plugin with platform-specific settings:

- **Application metadata** (name, version, vendor, description)
- **Platform-specific options** (shortcuts, menu integration, file associations)
- **Runtime bundling** (self-contained JRE)
- **Output format and location**

## CI/CD Pipeline

### GitHub Actions Workflow

The project includes a comprehensive CI/CD pipeline that builds for all platforms:

```yaml
jobs:
  test: # Run tests once
  package: # Build JAR
  android-build: # Build APK  
  windows-native: # Build Windows EXE
  linux-native: # Build Linux DEB
  macos-native: # Build macOS DMG
  release: # Create GitHub release with all artifacts
```

### Build Matrix

| Platform | Runner | Parallel | Artifacts |
|----------|--------|----------|-----------|
| Linux | ubuntu-latest | ✓ | JAR, DEB, APK |
| Windows | windows-latest | ✓ | EXE |
| macOS | macos-latest | ✓ | DMG |

### Artifact Management

- **Pull Requests**: 14-day retention with PR-specific naming
- **Main/Master**: 90-day retention for release candidates
- **Releases**: Permanent artifacts attached to GitHub releases

## Local Development

### Quick Setup

1. **Clone repository**:
   ```bash
   git clone https://github.com/Gameaday/poker-basic.git
   cd poker-basic
   ```

2. **Verify setup**:
   ```bash
   ./gradlew verifyDualPlatformSetup
   ```

3. **Build for your platform**:
   ```bash
   # Windows (on Windows system)
   cd Poker-Basic && mvn package -Pwindows-exe -DskipTests

   # Linux (on Linux system)  
   cd Poker-Basic && mvn package -Plinux-exe -DskipTests

   # macOS (on macOS system)
   cd Poker-Basic && mvn package -Pmacos-exe -DskipTests

   # Cross-platform JAR (any system)
   cd Poker-Basic && mvn package -DskipTests

   # Android APK (any system with internet)
   ./gradlew assembleDebug
   ```

### Development Workflow

1. **Make changes** to shared source code in `Poker-Basic/src/main/java/`
2. **Test changes** with `mvn test` or `./gradlew test`
3. **Build locally** for your target platform
4. **Create PR** - all platforms build automatically
5. **Review artifacts** from CI/CD pipeline
6. **Merge to main** - release artifacts generated

## Platform-Specific Features

### Code Organization

The codebase maintains platform agnosticism while supporting platform-specific features:

```
Poker-Basic/src/main/java/com/pokermon/
├── GameLauncher.java          # Cross-platform entry point
├── ConsoleMain.java           # Console mode (all platforms)
├── Main.java                  # GUI mode (desktop platforms)
└── [other shared classes]     # Business logic (all platforms)

android/src/main/java/com/pokermon/android/
├── MainActivity.kt            # Android-specific UI
└── ui/theme/                  # Android Material Design
```

### Platform Detection

The application automatically detects the runtime platform and adjusts behavior:

- **Desktop platforms**: JavaFX UI with fallback to console
- **Android**: Native Android UI via separate APK
- **Headless environments**: Automatic console mode

### Shared Business Logic

All platforms share the same core game logic:
- Card game mechanics
- Hand evaluation
- Player management  
- Game state management
- Monster collection system

## Troubleshooting

### Common Issues

#### Build Failures

1. **"jpackage not found"**:
   - Ensure Java 17+ is installed
   - Verify `JAVA_HOME` points to JDK (not JRE)

2. **"Cannot create native package"**:
   - Install platform-specific packaging tools:
     - Windows: WiX Toolset (for .msi) or Inno Setup
     - Linux: `dpkg-dev` package
     - macOS: Xcode command line tools

3. **"Android SDK not found"**:
   - Requires internet connection for first build
   - SDK downloads automatically in CI/CD
   - For local builds: `./gradlew` handles SDK setup

#### Runtime Issues

1. **JavaFX not loading**:
   - Native packages include JavaFX runtime
   - For JAR: ensure JavaFX is on classpath

2. **Application won't start**:
   - Check system requirements match build target
   - Verify all dependencies are bundled (fat JAR vs thin JAR)

### Platform Requirements

#### Development System Requirements

- **Windows builds**: Windows 10+ with JDK 17+
- **Linux builds**: Ubuntu 18.04+ with JDK 17+ and dpkg-dev
- **macOS builds**: macOS 10.14+ with JDK 17+ and Xcode tools
- **Android builds**: Any system with internet access
- **JAR builds**: Any system with JDK 17+

#### Target System Requirements

- **Windows**: Windows 10+ (64-bit)
- **Linux**: Ubuntu 18.04+ or equivalent (amd64)
- **macOS**: macOS 10.14+ (Intel/Apple Silicon)
- **Android**: Android 5.0+ (API 21+)
- **JAR**: Java 17+ on any supported OS

## Testing

### Build System Tests

```bash
# Test all build configurations
./validate-android-build.sh

# Verify dual platform setup
./gradlew verifyDualPlatformSetup

# Test Maven builds
cd Poker-Basic && mvn clean test

# Test Gradle builds (requires internet)
./gradlew test
```

### Platform-Specific Testing

1. **Functional testing**: Each platform runs the same test suite
2. **Integration testing**: CI/CD validates builds on target platforms
3. **Manual testing**: Download artifacts from CI/CD for user testing

## Advanced Configuration

### Custom Build Properties

Modify `Poker-Basic/pom.xml` to customize build settings:

```xml
<properties>
    <app.name>CustomPokerGame</app.name>
    <app.vendor>Your Company</app.vendor>
    <app.description>Custom Description</app.description>
</properties>
```

### JVM Options

Configure runtime JVM options in the jpackage profiles:

```xml
<javaOptions>
    <option>-Xmx1024m</option>
    <option>-Dfile.encoding=UTF-8</option>
    <option>-Djava.awt.headless=false</option>
</javaOptions>
```

### File Associations

Add file type associations for native packages:

```xml
<fileAssociations>
    <fileAssociation>
        <extension>poker</extension>
        <description>Poker Game File</description>
    </fileAssociation>
</fileAssociations>
```

## Conclusion

This cross-platform build system provides comprehensive coverage for all major platforms while maintaining a single, shared codebase. The automated CI/CD pipeline ensures consistent builds and testing across all target platforms, making it easy to develop and distribute the application to users on any system.