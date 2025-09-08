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

# Gradle configuration tests
test_item "Shared module build exists" "[ -f shared/build.gradle ]"
test_item "Gradle has application plugin" "grep -q 'application' shared/build.gradle"
test_item "Gradle has fat JAR task" "grep -q 'fatJar' shared/build.gradle"
test_item "Desktop module exists" "[ -f desktop/build.gradle ]"
test_item "Desktop has native packaging" "grep -q 'packageNative' desktop/build.gradle"
test_item "Gradle has Kotlin JVM plugin" "grep -q 'kotlin.jvm' shared/build.gradle"

# Gradle configuration tests
test_item "Root Gradle build exists" "[ -f build.gradle ]"
test_item "Gradle settings exist" "[ -f settings.gradle ]"
test_item "Android module included" "grep -q ':android' settings.gradle"
test_item "Gradle wrapper exists" "[ -f gradlew ]"

# CI/CD configuration tests
test_item "GitHub Actions workflow exists" "[ -f .github/workflows/ci.yml ]"
test_item "CI has native builds job" "grep -q 'native-builds:' .github/workflows/ci.yml"
test_item "CI has multiple platforms" "grep -q 'matrix:' .github/workflows/ci.yml && grep -q 'windows-latest' .github/workflows/ci.yml"
test_item "CI has platform strategy" "grep -q 'ubuntu-latest' .github/workflows/ci.yml && grep -q 'macos-latest' .github/workflows/ci.yml"
test_item "CI has Android build job" "grep -q 'android-build:' .github/workflows/ci.yml"

echo ""
echo "🔧 Java & Gradle Environment Tests"
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

# Check Gradle
if command -v ./gradlew >/dev/null 2>&1; then
    GRADLE_VERSION=$(./gradlew --version 2>/dev/null | grep Gradle | head -n 1)
    echo -e "✅ ${GREEN}PASS${NC}: Gradle detected - $GRADLE_VERSION"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Gradle wrapper not found"
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

# Check Gradle configuration
if [ -f shared/build.gradle ]; then
    GRADLE_VERSION_CONFIGURED=$(grep -o 'kotlin_version.*=' build.gradle | sed 's/.*= *//' | tr -d "'" | head -n 1)
    SHARED_VERSION=$(grep "version.*=" shared/build.gradle | head -n 1 | sed 's/.*= *//' | tr -d "'" || echo "dynamic")
    echo "📋 Gradle Kotlin Version: $GRADLE_VERSION_CONFIGURED"
    echo "📋 Shared Module Version: $SHARED_VERSION"
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

# Test Gradle JAR build
echo "Testing Gradle JAR build..."
if ./gradlew :shared:compileKotlin --no-daemon -q >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}PASS${NC}: Gradle compilation successful"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Gradle compilation failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test fat JAR creation
echo "Testing fat JAR creation..."
if ./gradlew :shared:fatJar --no-daemon -q >/dev/null 2>&1; then
    if [ -f shared/build/libs/pokermon-*-fat.jar ]; then
        FAT_JAR_SIZE=$(ls -lh shared/build/libs/pokermon-*-fat.jar | awk '{print $5}')
        echo -e "✅ ${GREEN}PASS${NC}: Fat JAR created successfully ($FAT_JAR_SIZE)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Fat JAR not found"
    fi
else
    echo -e "❌ ${RED}FAIL${NC}: Fat JAR build failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test platform-specific modules (configuration only, not actual build)
echo "Testing platform module configurations..."

if ./gradlew projects --no-daemon -q >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}PASS${NC}: Gradle modules are valid"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Gradle module validation failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

echo ""
echo "🔍 Source Code Organization Tests"
echo "--------------------------------"

# Check shared source code structure
test_item "GameLauncher main class exists" "[ -f shared/src/main/kotlin/com/pokermon/GameLauncher.kt ] || [ -f shared/src/main/java/com/pokermon/GameLauncher.java ]"
test_item "Console main class exists" "[ -f shared/src/main/kotlin/com/pokermon/console/ConsoleMain.kt ] || [ -f shared/src/main/java/com/pokermon/ConsoleMain.java ]"
test_item "Game logic classes exist" "[ -f shared/src/main/kotlin/com/pokermon/Game.kt ] || [ -f shared/src/main/java/com/pokermon/Game.java ]"

# Check Android-specific source
test_item "Android MainActivity exists" "[ -f android/src/main/java/com/pokermon/android/MainActivity.kt ]"
test_item "Android manifest exists" "[ -f android/src/main/AndroidManifest.xml ]"

# Check test coverage
test_item "Gradle tests exist" "[ -d shared/src/test/kotlin ] || [ -d shared/src/test/java ]"
test_item "Test files present" "find shared/src/test -name '*Test.kt' -o -name '*Test.java' | grep -q Test"

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
echo "📦 Cross-Platform JAR:    shared/build/libs/pokermon-*-fat.jar"
echo "📦 Executable JAR:        shared/build/libs/shared-*.jar"
echo "🪟 Windows EXE:           desktop/build/distributions/*-windows.exe"
echo "🐧 Linux DEB:             desktop/build/distributions/*-linux.deb"
echo "🍎 macOS DMG:             desktop/build/distributions/*-macos.dmg"
echo "📱 Android APK:           android/build/outputs/apk/debug/android-debug.apk"

echo ""
echo "🔥 Kotlin/Native Build Tests"
echo "----------------------------"

# Test native build capability
echo "Testing Kotlin/Native compiler availability..."
if [ -f kotlin-native-linux-x86_64-1.9.22/bin/kotlinc-native ]; then
    echo -e "✅ ${GREEN}PASS${NC}: Kotlin/Native compiler available"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Kotlin/Native compiler not found"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test native source structure
echo "Testing native source structure..."
if [ -f shared/src/main/kotlin/com/pokermon/native/NativeMain.kt ]; then
    echo -e "✅ ${GREEN}PASS${NC}: Native entry point exists"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "❌ ${RED}FAIL${NC}: Native entry point not found"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test Linux native build
echo "Testing Linux native build..."
if ./gradlew :shared:buildNativeLinux --no-daemon -q >/dev/null 2>&1; then
    if [ -f shared/build/native/linux/pokermon-linux.kexe ]; then
        NATIVE_SIZE=$(ls -lh shared/build/native/linux/pokermon-linux.kexe | awk '{print $5}')
        echo -e "✅ ${GREEN}PASS${NC}: Native Linux executable built successfully ($NATIVE_SIZE)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        
        # Test that it's a true native executable
        if file shared/build/native/linux/pokermon-linux.kexe | grep -q "ELF.*executable"; then
            echo -e "✅ ${GREEN}PASS${NC}: Executable is true native ELF binary"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            echo -e "❌ ${RED}FAIL${NC}: Not a native ELF executable"
        fi
        TESTS_TOTAL=$((TESTS_TOTAL + 1))
        
        # Test that it runs without Java
        echo "Testing native executable execution..."
        if shared/build/native/linux/pokermon-linux.kexe >/dev/null 2>&1; then
            echo -e "✅ ${GREEN}PASS${NC}: Native executable runs successfully"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            echo -e "❌ ${RED}FAIL${NC}: Native executable failed to run"
        fi
        TESTS_TOTAL=$((TESTS_TOTAL + 1))
        
        # Test dependencies (should not include Java)
        echo "Testing native executable dependencies..."
        if ldd shared/build/native/linux/pokermon-linux.kexe | grep -v "java" | grep -q "libc"; then
            echo -e "✅ ${GREEN}PASS${NC}: Only system dependencies (no Java required)"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            echo -e "❌ ${RED}FAIL${NC}: Unexpected dependencies found"
        fi
        TESTS_TOTAL=$((TESTS_TOTAL + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Native Linux executable not created"
    fi
else
    echo -e "❌ ${RED}FAIL${NC}: Native Linux build failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test Windows native cross-compilation
echo "Testing Windows native cross-compilation..."
if ./gradlew :shared:buildNativeWindows --no-daemon -q >/dev/null 2>&1; then
    if [ -f shared/build/native/windows/pokermon-windows.exe ]; then
        WINDOWS_SIZE=$(ls -lh shared/build/native/windows/pokermon-windows.exe | awk '{print $5}')
        echo -e "✅ ${GREEN}PASS${NC}: Native Windows executable built successfully ($WINDOWS_SIZE)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        
        # Test that it's a Windows PE executable
        if file shared/build/native/windows/pokermon-windows.exe | grep -q "PE32.*executable"; then
            echo -e "✅ ${GREEN}PASS${NC}: Executable is true Windows PE binary"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            echo -e "❌ ${RED}FAIL${NC}: Not a Windows PE executable"
        fi
        TESTS_TOTAL=$((TESTS_TOTAL + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Native Windows executable not created"
    fi
else
    echo -e "❌ ${RED}FAIL${NC}: Native Windows build failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test desktop native packaging
echo "Testing desktop native packaging..."
if ./gradlew :desktop:packageNativeLinux --no-daemon -q >/dev/null 2>&1; then
    if [ -f desktop/build/distributions/pokermon-linux-native ]; then
        PACKAGED_SIZE=$(ls -lh desktop/build/distributions/pokermon-linux-native | awk '{print $5}')
        echo -e "✅ ${GREEN}PASS${NC}: Desktop native packaging successful ($PACKAGED_SIZE)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Desktop native package not created"
    fi
else
    echo -e "❌ ${RED}FAIL${NC}: Desktop native packaging failed"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

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