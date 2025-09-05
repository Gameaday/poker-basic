# Poker Basic - GitHub Copilot Instructions

**CRITICAL: Always reference these instructions first and only fallback to search or bash commands when you encounter unexpected information that does not match the information here.**

## Repository Overview

Poker Basic is a **Kotlin-native** cross-platform poker game project featuring monster collection mechanics with multiple build targets:
- Desktop JAR (via Maven) - **ALWAYS WORKS**
- Android APK (via Gradle) - **REQUIRES INTERNET** 
- Professional codebase with 254 comprehensive tests

The project demonstrates modern Kotlin-first development practices using native Android architecture patterns and comprehensive testing for a fun gaming experience.

## Development Philosophy: Kotlin-Native First

This project has migrated to a **Kotlin-native architecture** for unified cross-platform development:

### Core Benefits
- **Native Android compatibility** with Kotlin-first design
- **Unified codebase** reducing Java-Kotlin compilation conflicts  
- **Modern null safety** with Kotlin's type system
- **Coroutines support** for async programming
- **Data classes** for immutable game state management
- **Sealed classes** for type-safe game phases and modes

### Architectural Principles
- **Kotlin classes are primary** - Java classes exist only for legacy compatibility
- **Bridge pattern** connects UI platforms through `GameLogicBridge.kt`
- **Enum classes** provide rich functionality (GameMode.kt, GamePhase.kt)
- **Type safety first** with null-safe operators and smart casts

## Quick Start - Essential Commands

### Bootstrap and Validate Repository
```bash
cd /home/runner/work/poker-basic/poker-basic
./validate-android-build.sh  # Validates build system (works offline, <1 second)
```

### Build and Test - NEVER CANCEL these commands
```bash
cd Poker-Basic
mvn clean compile -B    # Build: ~10 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
mvn test -B            # Test: ~12 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
mvn clean package -B   # Package: ~15 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
```

### Run the Application
```bash
cd Poker-Basic
java -jar target/pokermon-1.0.0-fat.jar --help    # Show comprehensive help
java -jar target/pokermon-1.0.0-fat.jar --basic   # Console mode (interactive)
java -jar target/pokermon-1.0.0-fat.jar          # GUI mode (requires display)
```

### Android Build (Network Required)
```bash
# From repository root
./gradlew :android:assembleDebug --no-daemon  # APK: 30+ minutes. NEVER CANCEL. Set timeout to 60+ minutes.
```
**WARNING**: Android builds require internet access and will fail in sandboxed environments with "dl.google.com: No address associated with hostname". This is expected behavior.

## Working Effectively

### NEVER CANCEL BUILD COMMANDS
- **Maven builds**: Take 10-15 seconds but set timeouts to 60+ seconds
- **Android builds**: Take 30+ minutes with internet access. NEVER CANCEL. Set timeout to 60+ minutes
- **Tests**: Take ~12 seconds but set timeouts to 60+ seconds

### Development Workflow
1. **Always validate first**: `./validate-android-build.sh`
2. **Build and test**: `cd Poker-Basic && mvn clean test -B`
3. **Create JAR**: `mvn clean package -B`
4. **Test functionality**: Run console mode and complete a game scenario
5. **Android build**: Only attempt if internet access is available

### Required Validation After Changes
- **Always run**: `mvn test -B` (254 tests must pass)
- **Always test**: Run console game scenario (see Validation section)
- **Kotlin compatibility**: Ensure Kotlin-first compilation
- **JAR functionality**: Test `java -jar pokermon-1.0.0-fat.jar --help`

## Kotlin-Native Architecture

### Project Structure
```
poker-basic/
├── Poker-Basic/                    # Maven project (JAR builds) - Kotlin-first
│   ├── src/main/kotlin/com/pokermon/   # Primary Kotlin source (PRIORITY)
│   │   ├── GameEngine.kt           # Core game logic (migrated)
│   │   ├── Game.kt                 # Game configuration (migrated)
│   │   ├── GameMode.kt             # Game modes enum (rich Kotlin)
│   │   ├── GamePhase.kt            # Game phases enum (rich Kotlin)
│   │   └── bridge/GameLogicBridge.kt   # UI platform bridge
│   ├── src/main/java/com/pokermon/     # Legacy Java (for compatibility)
│   ├── pom.xml                     # Maven configuration (Kotlin-optimized)
│   └── target/                     # Build outputs
├── android/                        # Android module (APK builds)
├── .github/workflows/              # CI/CD pipeline
└── validate-android-build.sh      # Offline validation
```

### Primary Classes (Kotlin)
- `com.pokermon.GameEngine` - Core game logic with Kotlin null safety
- `com.pokermon.Game` - Configuration data class with default parameters
- `com.pokermon.GameMode` - Rich enum with extension functions
- `com.pokermon.GamePhase` - Type-safe game state management
- `com.pokermon.bridge.GameLogicBridge` - Unified API for all UI platforms

### Legacy Classes (Java - for compatibility)
- `com.pokermon.Player` - Player management (will be migrated)
- `com.pokermon.Main` - Core utilities (will be migrated)
- `com.pokermon.MonsterDatabase` - Monster data source
- Console and UI classes - maintained for compatibility

## Build System: Kotlin-First

### Maven Configuration Highlights
- **Kotlin compilation priority**: Kotlin source compiled before Java
- **Enhanced compiler options**: JSR305 strict mode, JVM annotations
- **Coroutines support**: Ready for async Android development
- **Kotlin 1.9.22**: Latest stable version with modern features
- **All-open plugin**: Easier Java interop when needed

### Key Dependencies
```xml
<kotlin.version>1.9.22</kotlin.version>
<kotlinx.coroutines.version>1.7.3</kotlinx.coroutines.version>
```

## Android Integration: Native Kotlin

### Advantages of Kotlin-Native Approach
- **Direct compilation**: No Java-Kotlin bridge complexity
- **Coroutines**: Native async support for UI responsiveness
- **Null safety**: Eliminates common Android crashes
- **Data classes**: Perfect for Android Parcelable and state management
- **Sealed classes**: Type-safe navigation and game state

### Android-Specific Enhancements
```kotlin
// Example: Kotlin-native Android integration
enum class GameMode(val displayName: String, val isMultiplayer: Boolean) {
    CLASSIC("Classic Poker", true),
    ADVENTURE("Adventure Mode", false) {
        fun hasMonsters(): Boolean = true
        fun getMonsterTypes(): List<String> = listOf("Dragon", "Wizard", "Beast")
    }
}
```

## Network and Environment Limitations

### Works Offline (After Initial Maven Dependencies)
- Maven compilation, testing, and packaging
- JAR execution (console and GUI modes)
- Validation scripts
- All documentation and code analysis

### Requires Internet Access
- **Android builds**: Downloads Android SDK and dependencies
- **Initial Maven setup**: First-time dependency downloads
- **CI/CD**: Full functionality requires network access

### Sandbox Environment Behavior
- Android builds fail with "No address associated with hostname" (expected)
- JAR builds work perfectly after initial dependency download
- All validation scripts work offline

## Manual Validation Scenarios

### CRITICAL: Always Test Complete User Scenarios

#### Console Mode Validation
```bash
cd Poker-Basic
java -jar target/pokermon-1.0.0-fat.jar --basic
# 1. Enter player name (e.g., "TestPlayer")
# 2. Choose number of AI opponents (1-3) 
# 3. Choose starting chips (500 recommended)
# 4. Verify game displays cards and hand evaluation
# 5. Place a bet and verify betting system works
# 6. Exit gracefully (timeout will occur)
```

#### JAR Functionality Validation
```bash
cd Poker-Basic
java -jar target/pokermon-1.0.0-fat.jar --help     # Must show comprehensive help
java -jar target/pokermon-1.0.0-fat.jar --version  # Must show version 1.0.0
```

#### Build System Validation
```bash
cd /home/runner/work/poker-basic/poker-basic
./validate-android-build.sh  # Must pass all 21 checks
cd Poker-Basic && mvn test -B # Must pass all 254 tests
```

## Build Outputs and Artifacts

### Successful Build Outputs
- **JAR**: `Poker-Basic/target/pokermon-1.0.0-fat.jar` (works standalone, ~700KB)
- **APK**: `android/build/outputs/apk/debug/android-debug.apk` (requires internet to build)
- **Test Reports**: `Poker-Basic/target/surefire-reports/` (XML format)

### Build Verification Commands
```bash
cd Poker-Basic
ls -la target/*.jar                        # Verify JAR exists (~700KB)
java -jar target/pokermon-1.0.0-fat.jar --help  # Verify JAR is executable
```

## Time Expectations

### Typical Command Times (Add 50% buffer for timeouts)
- `mvn clean compile`: ~10 seconds
- `mvn test`: ~12 seconds (254 tests)
- `mvn clean package`: ~15 seconds
- `./validate-android-build.sh`: <1 second
- Android build: 30+ minutes (with internet)

### Timeout Recommendations
- Maven commands: 60+ seconds minimum
- Android builds: 60+ minutes minimum
- Validation scripts: 30 seconds

## Development Best Practices

### Kotlin-First Guidelines
1. **New features**: Always implement in Kotlin first
2. **Null safety**: Use safe call operators (?.) and Elvis operators (?:)
3. **Data classes**: For immutable state management
4. **Sealed classes**: For type-safe game state transitions
5. **Coroutines**: For async operations (especially Android)
6. **Extension functions**: To enhance existing Java classes

### Java Compatibility
- Maintain existing Java classes until migration is complete
- Use `@JvmStatic`, `@JvmOverloads` for Java interoperability
- Preserve method signatures for backward compatibility

## Success Metrics

- ✅ All 254 tests pass (`mvn test -B`)
- ✅ JAR builds successfully (~700KB)
- ✅ Console mode works interactively with Kotlin logic
- ✅ Help system displays comprehensive information
- ✅ Build system validation passes (21/21 checks)
- ✅ Kotlin compilation prioritized over Java
- ✅ No Kotlin-Java interop conflicts
- ✅ Android builds succeed with internet access

## Migration Progress

### Completed ✅
- Core game classes migrated to Kotlin (Game.kt, GameEngine.kt)
- Enhanced enum classes with rich Kotlin functionality
- Null-safe bridge pattern implementation
- Kotlin-first build system configuration
- Comprehensive test compatibility (254 tests passing)

### In Progress 🔄
- Player class migration to Kotlin
- Hand evaluation logic modernization
- Coroutines integration for async operations

### Planned 📋
- Complete Java-to-Kotlin migration
- Enhanced Android UI with Compose integration
- Advanced null safety patterns
- Performance optimizations with Kotlin idioms

This Kotlin-native approach provides a solid foundation for modern cross-platform development while maintaining backward compatibility and ensuring robust testing coverage.