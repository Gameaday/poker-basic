# Cross-Platform Build Testing Guide

## Overview

This document provides comprehensive testing procedures for the cross-platform build system, ensuring all build targets work correctly across Windows, Linux, macOS, and Android platforms.

## Pre-Build Testing

### 1. Environment Validation

Before attempting any builds, validate your environment:

```bash
# Run comprehensive validation
./validate-cross-platform-build.sh

# Quick Android-specific validation  
./validate-android-build.sh
```

**Expected Results:**
- ✅ All validation tests should pass
- ✅ Java 17+ detected
- ✅ Maven 3.6+ available
- ✅ All build configurations valid

### 2. Code Quality Validation

```bash
# Run full test suite (77 tests)
cd Poker-Basic
mvn clean test -B

# Verify all tests pass
echo "Exit code: $?"  # Should be 0
```

## Build System Testing

### 3. Cross-Platform JAR Build

Test the foundation for all native builds:

```bash
cd Poker-Basic

# Clean build with tests
mvn clean package -B

# Verify outputs
ls -la target/*.jar
# Expected: pokermon-0.1b.jar (~700KB) and pokermon-0.1b-fat.jar (~11MB)

# Test JAR functionality
java -jar target/pokermon-0.1b-fat.jar --help
java -jar target/pokermon-0.1b-fat.jar --version
```

**Validation Criteria:**
- ✅ Both JAR files created successfully
- ✅ Help command displays comprehensive information
- ✅ Version shows "0.1b"
- ✅ No runtime errors during launch

### 4. Platform-Specific Native Builds

#### Linux Native Package (.deb)

```bash
cd Poker-Basic

# Build Linux package
mvn clean package -Plinux-exe -DskipTests -B

# Verify output
ls -la target/jpackage/
# Expected: pokergame_0.1b-1_amd64.deb (~58MB)

# Test package metadata (if dpkg available)
dpkg --info target/jpackage/pokergame_0.1b-1_amd64.deb
```

#### Windows Native Package (.exe)

**Note:** Requires Windows environment or CI/CD

```bash
# On Windows system:
cd Poker-Basic
mvn clean package -Pwindows-exe -DskipTests -B

# Expected output: target/jpackage/PokerGame-0.1b.exe
```

#### macOS Native Package (.dmg)

**Note:** Requires macOS environment or CI/CD

```bash  
# On macOS system:
cd Poker-Basic
mvn clean package -Pmacos-exe -DskipTests -B

# Expected output: target/jpackage/PokerGame-0.1b.dmg
```

### 5. Android APK Build

**Note:** Requires internet connection for initial setup

```bash
# Verify Gradle setup
./gradlew tasks --group="build"

# Build APK (requires internet for first build)
./gradlew :android:assembleDebug --no-daemon

# Verify output
ls -la android/build/outputs/apk/debug/
# Expected: android-debug.apk (~5-10MB)
```

## Functional Testing

### 6. Application Functionality Testing

#### Console Mode Testing

```bash
cd Poker-Basic

# Test console mode functionality
timeout 30 java -jar target/pokermon-0.1b-fat.jar --basic << EOF
TestPlayer
1
500
EOF

# Expected: Game starts, accepts input, displays poker hands
```

#### Cross-Platform Compatibility

Test the same JAR on different systems:

```bash
# Test help system
java -jar pokermon-0.1b-fat.jar --help
# Should work identically on Windows, Linux, macOS

# Test version display  
java -jar pokermon-0.1b-fat.jar --version
# Should show same version across all platforms
```

## CI/CD Integration Testing

### 7. GitHub Actions Workflow Validation

The CI/CD pipeline tests all platforms automatically:

```yaml
# Workflow includes these build jobs:
- test: Ubuntu-latest (Java 17, Maven tests)
- package: Ubuntu-latest (JAR build)
- android-build: Ubuntu-latest (APK build)
- windows-native: Windows-latest (EXE build)
- linux-native: Ubuntu-latest (DEB build)  
- macos-native: macOS-latest (DMG build)
- release: Ubuntu-latest (Artifact collection)
```

**Validation Steps:**
1. Create PR → Verify all jobs pass
2. Check artifact uploads in Actions tab
3. Download and test artifacts manually

### 8. Artifact Validation

For each successful CI/CD run, verify:

```bash
# Download artifacts from GitHub Actions
# Test each platform's artifact:

# JAR artifact
java -jar pokermon-0.1b.jar --help

# Windows EXE (on Windows)
PokerGame-0.1b.exe --help

# Linux DEB (on Ubuntu/Debian)
sudo dpkg -i pokergame_0.1b-1_amd64.deb
pokergame --help

# macOS DMG (on macOS)  
# Install from DMG, test launch

# Android APK (on Android device)
# Install APK, verify app launches
```

## Build System Robustness Testing

### 9. Error Condition Testing

Test build system handles errors gracefully:

```bash
# Test with missing dependencies (should fail cleanly)
rm -rf ~/.m2/repository/org/openjfx
mvn clean package
# Should fail with clear error message

# Test with invalid Java version (if possible)
# Should fail with version requirement message

# Test network failures during Android build
# Should fail with clear network error message
```

### 10. Environment Compatibility Testing

#### Sandbox Environment Testing

```bash
# Test offline capability after initial dependency download
cd Poker-Basic
mvn clean test -B --offline
# Should work if dependencies already cached

# Test validation scripts in restricted environments
./validate-cross-platform-build.sh
# Should pass even without network access
```

#### Memory and Resource Testing

```bash
# Test builds with limited memory
export MAVEN_OPTS="-Xmx512m"
mvn clean package -B
# Should complete successfully

# Test concurrent builds
mvn clean package -B &
mvn clean test -B &
wait
# Both should complete without conflicts
```

## Performance Testing

### 11. Build Time Benchmarks

Track build performance across platforms:

```bash
# Time JAR build
time mvn clean package -DskipTests -B
# Expected: 10-15 seconds

# Time native package build  
time mvn clean package -Plinux-exe -DskipTests -B
# Expected: 45-60 seconds

# Time test suite
time mvn test -B
# Expected: 10-15 seconds for 77 tests
```

### 12. Artifact Size Validation

Verify consistent artifact sizes:

```bash
# Check JAR sizes
ls -lh Poker-Basic/target/*.jar
# pokermon-0.1b.jar: ~700KB
# pokermon-0.1b-fat.jar: ~11MB

# Check native package sizes
ls -lh Poker-Basic/target/jpackage/
# pokergame_0.1b-1_amd64.deb: ~58MB (includes JRE)

# Check APK size
ls -lh android/build/outputs/apk/debug/
# android-debug.apk: ~5-10MB
```

## Troubleshooting Common Issues

### Build Failures

1. **"jpackage not found"**
   - Verify Java 17+ JDK (not JRE) installed
   - Check JAVA_HOME points to JDK

2. **"Android SDK not found"**  
   - Requires internet for first-time setup
   - In CI/CD: Android SDK downloaded automatically

3. **"Tests failed"**
   - Run `mvn test -B` for detailed output
   - All 77 tests must pass before building

4. **"Out of memory"**
   - Increase Maven memory: `export MAVEN_OPTS="-Xmx2g"`
   - For Android: Add `org.gradle.jvmargs=-Xmx2g` to gradle.properties

### Platform-Specific Issues

1. **Windows builds on non-Windows**
   - Use CI/CD for Windows EXE generation
   - Local development: JAR testing sufficient

2. **macOS builds on non-macOS**
   - Use CI/CD for DMG generation  
   - Local development: JAR testing sufficient

3. **Android builds without internet**
   - Expected failure in sandbox environments
   - Verification: Check gradle files are present

## Test Results Documentation

### Expected Test Matrix

| Platform | Build Tool | Local Test | CI/CD Test | Manual Test |
|----------|------------|------------|------------|-------------|
| JAR | Maven | ✅ | ✅ | ✅ |
| Linux DEB | Maven + jpackage | ✅ | ✅ | ✅ |
| Windows EXE | Maven + jpackage | ⚠️* | ✅ | ✅ |
| macOS DMG | Maven + jpackage | ⚠️* | ✅ | ✅ |
| Android APK | Gradle | ⚠️** | ✅ | ✅ |

*Requires platform-specific environment  
**Requires internet connection

### Success Criteria

- ✅ All 77 unit tests pass
- ✅ All validation scripts pass  
- ✅ JAR builds and runs on all platforms
- ✅ Native packages build successfully in CI/CD
- ✅ Android APK builds successfully in CI/CD
- ✅ Help system works consistently across platforms
- ✅ Console mode works on all platforms
- ✅ No platform-specific code in shared business logic

## Conclusion

This comprehensive testing strategy ensures the cross-platform build system is robust, reliable, and ready for production use. The combination of local testing, CI/CD validation, and manual verification provides confidence that all build targets will work correctly for end users.