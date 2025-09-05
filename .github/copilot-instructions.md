# Poker Basic - GitHub Copilot Instructions

**CRITICAL: Always reference these instructions first and only fallback to search or bash commands when you encounter unexpected information that does not match the information here.**

## Repository Overview

Poker Basic is a Java 17 cross-platform poker game project featuring monster collection mechanics with multiple build targets:
- Desktop JAR (via Maven) - **ALWAYS WORKS**
- Android APK (via Gradle) - **REQUIRES INTERNET** 
- Professional codebase with 190 comprehensive tests

The project demonstrates modern software development practices using professional architecture patterns and comprehensive testing for a fun gaming experience.

## Development Principles

### DRY (Don't Repeat Yourself) Guidelines
This project follows DRY principles to create single authoritative sources of truth for APIs and methods that govern the project outside of any specific platform (agnostic) where possible:

- **Centralized Game Logic**: Core poker mechanics in `Poker-Basic/src/main/java/com/pokermon/` are platform-agnostic
- **Shared Bridge Pattern**: `GameLogicBridge.kt` provides unified API for all UI platforms (Android, Desktop, Console)
- **Single Monster Database**: `MonsterDatabase.java` serves as the authoritative source for all monster data
- **Unified Phase Management**: `GamePhase.java` enum defines game states used across all platforms
- **Centralized Card Management**: `CardPackManager.java` handles card assets for all platforms

These guidelines are educational and iterative - they can be broken if there is a good valuable reason to do so, but should be the default approach for new development.

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
- **Always run**: `mvn test -B` (190 tests must pass)
- **Always test**: Run console game scenario (see Validation section)
- **Java compatibility**: Ensure Java 17+ compatibility
- **JAR functionality**: Test `java -jar pokermon-1.0.0-fat.jar --help`

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
cd Poker-Basic && mvn test -B # Must pass all 190 tests
```

## Build Outputs and Artifacts

### Successful Build Outputs
- **JAR**: `Poker-Basic/target/pokermon-1.0.0-fat.jar` (works standalone)
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
- `mvn test`: ~12 seconds (190 tests)
- `mvn clean package`: ~15 seconds
- `./validate-android-build.sh`: <1 second
- Android build: 30+ minutes (with internet)

### Timeout Recommendations
- Maven commands: 60+ seconds minimum
- Android builds: 60+ minutes minimum
- Validation scripts: 30 seconds

## Project Structure Reference

### Key Directories
```
poker-basic/
├── Poker-Basic/           # Maven project (JAR builds)
│   ├── src/main/java/com/pokermon/  # Shared Java source
│   ├── pom.xml           # Maven configuration
│   └── target/           # Build outputs
├── android/              # Android module (APK builds)
├── .github/workflows/    # CI/CD pipeline
└── validate-android-build.sh  # Offline validation
```

### Main Classes
- `com.pokermon.GameLauncher` - Main entry point with help system
- `com.pokermon.ConsoleMain` - Console mode implementation
- `com.pokermon.NewJFrame` - GUI mode (legacy, may not work headless)

## Common Issues and Solutions

### "No address associated with hostname" (Android builds)
- **Cause**: Network restrictions in sandboxed environment
- **Solution**: Expected behavior. Document that Android builds require internet access

### "Tests failed" or compilation errors
- **Cause**: Code changes broke existing functionality
- **Solution**: Fix code to maintain existing test compatibility
- **Validation**: All 77 tests must pass

### JAR doesn't execute
- **Cause**: Missing main class or dependencies
- **Solution**: Rebuild with `mvn clean package -B`
- **Verification**: Test `java -jar *.jar --help`

## Documentation References

For detailed information, consult these existing documentation files:
- `README.md` - Comprehensive project overview and features
- `ANDROID_BUILD_GUIDE.md` - Android build setup and requirements
- `ANDROID_BUILD_TESTING.md` - Android build validation details
- `RELEASE_TESTING.md` - Alpha release and artifact management
- `IMPLEMENTATION_SUMMARY.md` - Technical implementation details

## CI/CD Integration

The repository includes comprehensive GitHub Actions workflow (`.github/workflows/ci.yml`):
- **Test job**: Validates all code changes
- **Package job**: Creates JAR artifacts
- **Android job**: Creates APK artifacts (with internet)
- **Release job**: Manages GitHub releases with artifacts

Always run local validation before pushing to ensure CI/CD success.

## Key Success Metrics

- ✅ All 77 tests pass (`mvn test -B`)
- ✅ JAR builds successfully (~700KB)
- ✅ Console mode works interactively
- ✅ Help system displays comprehensive information
- ✅ Build system validation passes (21/21 checks)
- ✅ No network-dependent operations fail gracefully