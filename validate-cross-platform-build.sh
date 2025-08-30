#!/bin/bash
# Cross Platform Build System Validation Script
# Tests all build configurations and verifies cross-platform setup

echo "🔍 Cross Platform Build System Validation"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_TOTAL=0

# Helper function to run tests
test_item() {
    local description="$1"
    local test_command="$2"
    
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
    
    if eval "$test_command" >/dev/null 2>&1; then
        echo -e "✅ ${GREEN}PASS${NC}: $description"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: $description"
    fi
}

echo ""
echo "📁 Build System Structure Tests"
echo "-------------------------------"

# Maven configuration tests
test_item "Maven POM exists" "[ -f Poker-Basic/pom.xml ]"
test_item "Maven has jpackage plugin" "grep -q 'jpackage-maven-plugin' Poker-Basic/pom.xml"
test_item "Maven has shade plugin" "grep -q 'maven-shade-plugin' Poker-Basic/pom.xml"
test_item "Maven has Windows profile" "grep -q 'windows-exe' Poker-Basic/pom.xml"
test_item "Maven has Linux profile" "grep -q 'linux-exe' Poker-Basic/pom.xml"
test_item "Maven has macOS profile" "grep -q 'macos-exe' Poker-Basic/pom.xml"

# Gradle configuration tests
test_item "Root Gradle build exists" "[ -f build.gradle ]"
test_item "Gradle settings exist" "[ -f settings.gradle ]"
test_item "Android module included" "grep -q ':android' settings.gradle"
test_item "Gradle wrapper exists" "[ -f gradlew ]"

# CI/CD configuration tests
test_item "GitHub Actions workflow exists" "[ -f .github/workflows/ci.yml ]"
test_item "CI has Windows build job" "grep -q 'windows-native:' .github/workflows/ci.yml"
test_item "CI has Linux build job" "grep -q 'linux-native:' .github/workflows/ci.yml"
test_item "CI has macOS build job" "grep -q 'macos-native:' .github/workflows/ci.yml"
test_item "CI has Android build job" "grep -q 'android-build:' .github/workflows/ci.yml"

echo ""
echo "🔧 Java & Maven Environment Tests"
echo "---------------------------------"

# Check Java version
if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "✅ ${GREEN}PASS${NC}: Java $JAVA_VERSION detected (17+ required)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Java $JAVA_VERSION detected (17+ required)"
    fi
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Java not found"
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
fi

# Check Maven
if command -v mvn >/dev/null 2>&1; then
    MAVEN_VERSION=$(mvn -version 2>/dev/null | head -n 1)
    echo -e "✅ ${GREEN}PASS${NC}: Maven detected - $MAVEN_VERSION"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Maven not found"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Check jpackage availability
if command -v jpackage >/dev/null 2>&1; then
    JPACKAGE_VERSION=$(jpackage --version 2>/dev/null)
    echo -e "✅ ${GREEN}PASS${NC}: jpackage available - version $JPACKAGE_VERSION"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: jpackage not found (required for native packaging)"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

echo ""
echo "📦 Build Configuration Analysis"
echo "------------------------------"

# Check Maven profiles
if [ -f Poker-Basic/pom.xml ]; then
    MAVEN_PROFILES=$(grep -o '<id>[^<]*</id>' Poker-Basic/pom.xml | sed 's/<[^>]*>//g' | tr '\n' ', ')
    echo "📋 Maven Profiles: $MAVEN_PROFILES"
    
    # Check version consistency
    MAVEN_VERSION=$(grep -o '<version>[^<]*</version>' Poker-Basic/pom.xml | head -n 1 | sed 's/<[^>]*>//g')
    echo "📋 Maven Version: $MAVEN_VERSION"
fi

# Check Gradle configuration
if [ -f android/build.gradle ]; then
    ANDROID_VERSION=$(grep "versionName" android/build.gradle | sed -n 's/.*versionName "\([^"]*\)".*/\1/p')
    echo "📋 Android Version: $ANDROID_VERSION"
    
    COMPILE_SDK=$(grep "compileSdk" android/build.gradle | sed -n 's/.*compileSdk \([0-9]*\).*/\1/p')
    MIN_SDK=$(grep "minSdk" android/build.gradle | sed -n 's/.*minSdk \([0-9]*\).*/\1/p')
    echo "📋 Android SDK: Compile $COMPILE_SDK, Min $MIN_SDK"
fi

echo ""
echo "🚀 Platform Build Tests"
echo "-----------------------"

# Test Maven JAR build
echo "Testing Maven JAR build..."
if cd Poker-Basic && mvn clean compile -q >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}PASS${NC}: Maven compilation successful"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Maven compilation failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))
cd ..

# Test fat JAR creation
echo "Testing fat JAR creation..."
if cd Poker-Basic && mvn package -DskipTests -q >/dev/null 2>&1; then
    if [ -f target/pokermon-*-fat.jar ]; then
        FAT_JAR_SIZE=$(ls -lh target/pokermon-*-fat.jar | awk '{print $5}')
        echo -e "✅ ${GREEN}PASS${NC}: Fat JAR created successfully ($FAT_JAR_SIZE)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Fat JAR not found"
    fi
else
    echo -e "❌ ${RED}FAIL${NC}: Fat JAR build failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))
cd ..

# Test platform-specific profiles (configuration only, not actual build)
echo "Testing platform profile configurations..."

if cd Poker-Basic && mvn help:all-profiles -q >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}PASS${NC}: Maven profiles are valid"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Maven profile validation failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))
cd ..

echo ""
echo "🔍 Source Code Organization Tests"
echo "--------------------------------"

# Check shared source code structure
test_item "GameLauncher main class exists" "[ -f Poker-Basic/src/main/java/com/pokermon/GameLauncher.java ]"
test_item "Console main class exists" "[ -f Poker-Basic/src/main/java/com/pokermon/ConsoleMain.java ]"
test_item "Main class exists" "[ -f Poker-Basic/src/main/java/com/pokermon/Main.java ]"
test_item "Game logic classes exist" "[ -f Poker-Basic/src/main/java/com/pokermon/Game.java ]"

# Check Android-specific source
test_item "Android MainActivity exists" "[ -f android/src/main/java/com/pokermon/android/MainActivity.kt ]"
test_item "Android manifest exists" "[ -f android/src/main/AndroidManifest.xml ]"

# Check test coverage
test_item "Maven tests exist" "[ -d Poker-Basic/src/test/java ]"
test_item "Test files present" "find Poker-Basic/src/test/java -name '*Test.java' | grep -q Test"

echo ""
echo "🌐 Network Connectivity Test"
echo "----------------------------"

# Test network connectivity for builds that require internet
if ping -c 1 google.com >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}Network access available${NC} - All builds should work"
    NETWORK_AVAILABLE=true
    
    # Quick Gradle test if network is available
    if command -v ./gradlew >/dev/null 2>&1; then
        echo "Testing Gradle wrapper..."
        if ./gradlew tasks --no-daemon >/dev/null 2>&1; then
            echo -e "✅ ${GREEN}PASS${NC}: Gradle wrapper functional"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            echo -e "⚠️  ${YELLOW}WARN${NC}: Gradle execution limited (expected in sandbox)"
        fi
        TESTS_TOTAL=$((TESTS_TOTAL + 1))
    fi
else
    echo -e "⚠️  ${YELLOW}Network limited (sandbox environment)${NC}"
    echo "   Expected behavior:"
    echo "   - JAR builds: ✅ Work offline after dependencies downloaded"
    echo "   - Native packaging: ✅ Work offline with JDK 17+"
    echo "   - Android builds: ❌ Require internet for SDK/dependencies"
    echo "   - CI/CD: ✅ Has full internet access"
    NETWORK_AVAILABLE=false
fi

echo ""
echo "🎯 Expected Build Outputs"
echo "-------------------------"
echo "📦 Cross-Platform JAR:    Poker-Basic/target/pokermon-0.1b.jar"
echo "📦 Fat JAR (self-contained): Poker-Basic/target/pokermon-0.1b-fat.jar"
echo "🪟 Windows EXE:           Poker-Basic/target/jpackage/PokerGame-0.1b.exe"
echo "🐧 Linux DEB:             Poker-Basic/target/jpackage/pokergame_0.1b-1_amd64.deb"
echo "🍎 macOS DMG:             Poker-Basic/target/jpackage/PokerGame-0.1b.dmg"
echo "📱 Android APK:           android/build/outputs/apk/debug/android-debug.apk"

echo ""
echo "📊 Test Results Summary"
echo "======================"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}/$TESTS_TOTAL"

if [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    echo -e "🎉 ${GREEN}ALL TESTS PASSED${NC}"
    echo -e "✅ ${GREEN}Cross-platform build system is properly configured${NC}"
    echo -e "✅ ${GREEN}Ready for all platform builds${NC}"
    if [ "$NETWORK_AVAILABLE" = true ]; then
        echo -e "✅ ${GREEN}Network available - all builds should work${NC}"
    else
        echo -e "⚠️  ${YELLOW}Limited network - Android builds will work in CI/CD${NC}"
    fi
    exit 0
else
    FAILED=$((TESTS_TOTAL - TESTS_PASSED))
    echo -e "❌ ${RED}$FAILED tests failed${NC}"
    echo -e "⚠️  ${YELLOW}Some build configurations may not work correctly${NC}"
    exit 1
fi