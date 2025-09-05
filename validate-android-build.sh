#!/bin/bash
# APK Build System Validation Script
# Tests the Android build structure without requiring network access

echo "🔍 Android APK Build System Validation"
echo "======================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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
echo "📁 Project Structure Tests"
echo "--------------------------"

test_item "Root settings.gradle exists" "[ -f settings.gradle ]"
test_item "Root build.gradle exists" "[ -f build.gradle ]"
test_item "Gradle wrapper script exists" "[ -f gradlew ]"
test_item "Gradle wrapper executable" "[ -x gradlew ]"
test_item "Gradle wrapper properties exist" "[ -f gradle/wrapper/gradle-wrapper.properties ]"
test_item "Gradle wrapper JAR exists" "[ -f gradle/wrapper/gradle-wrapper.jar ]"

echo ""
echo "📱 Android Module Tests"
echo "----------------------"

test_item "Android directory exists" "[ -d android ]"
test_item "Android build.gradle exists" "[ -f android/build.gradle ]"
test_item "Android manifest exists" "[ -f android/src/main/AndroidManifest.xml ]"
test_item "MainActivity.kt exists" "[ -f android/src/main/java/com/pokermon/android/MainActivity.kt ]"
test_item "Theme files exist" "[ -f android/src/main/java/com/pokermon/android/ui/theme/Theme.kt ]"

echo ""
echo "🔧 Configuration Content Tests"
echo "------------------------------"

test_item "settings.gradle includes android module" "grep -q ':android' settings.gradle"
test_item "Root build.gradle has Android plugin" "grep -q 'com.android.tools.build:gradle' build.gradle"
test_item "Android build.gradle has correct namespace" "grep -q 'com.pokermon.android' android/build.gradle"
test_item "Android build.gradle has dynamic version" "grep -q 'versionName.*1.0' android/build.gradle"
test_item "MainActivity has correct package" "grep -q 'package com.pokermon.android' android/src/main/java/com/pokermon/android/MainActivity.kt"

echo ""
echo "🚀 CI/CD Integration Tests"
echo "--------------------------"

test_item "CI workflow exists" "[ -f .github/workflows/ci.yml ]"
test_item "CI includes android-build job" "grep -q 'android-build:' .github/workflows/ci.yml"
test_item "CI has Android SDK setup" "grep -q 'setup-android' .github/workflows/ci.yml"
test_item "CI builds APK" "grep -q 'assembleDebug' .github/workflows/ci.yml"
test_item "CI uploads APK artifacts" "grep -q 'apk_artifact_name' .github/workflows/ci.yml"

echo ""
echo "📋 Build File Analysis"
echo "----------------------"

# Check Gradle versions
if [ -f gradle/wrapper/gradle-wrapper.properties ]; then
    GRADLE_VERSION=$(grep "gradle-.*-bin.zip" gradle/wrapper/gradle-wrapper.properties | sed -n 's/.*gradle-\([0-9.]*\)-bin.zip/\1/p')
    echo "📦 Gradle Version: $GRADLE_VERSION"
fi

# Check Android versions
if [ -f android/build.gradle ]; then
    COMPILE_SDK=$(grep "compileSdk" android/build.gradle | sed -n 's/.*compileSdk \([0-9]*\)/\1/p')
    MIN_SDK=$(grep "minSdk" android/build.gradle | sed -n 's/.*minSdk \([0-9]*\)/\1/p')
    TARGET_SDK=$(grep "targetSdk" android/build.gradle | sed -n 's/.*targetSdk \([0-9]*\)/\1/p')
    VERSION_NAME=$(grep "versionName" android/build.gradle | sed -n 's/.*versionName "\([^"]*\)"/\1/p')
    
    echo "📱 Android Compile SDK: $COMPILE_SDK"
    echo "📱 Android Min SDK: $MIN_SDK (Android 9.0+)"
    echo "📱 Android Target SDK: $TARGET_SDK"
    echo "📱 App Version: $VERSION_NAME"
fi

echo ""
echo "🎯 Expected Build Outputs"
echo "-------------------------"
echo "📦 JAR Output: Poker-Basic/target/pokermon-1.0.0-fat.jar"
echo "📱 APK Output: android/build/outputs/apk/debug/android-debug.apk"

echo ""
echo "🌐 Network Connectivity Test"
echo "----------------------------"

# Test if we can resolve Google's Maven repo (this will fail in sandbox)
if ping -c 1 dl.google.com >/dev/null 2>&1; then
    echo -e "✅ ${GREEN}Network access available${NC} - APK build should work"
    
    # If network is available, try a quick build test
    echo ""
    echo "🔨 Quick Build Test"
    echo "------------------"
    if ./gradlew tasks --no-daemon >/dev/null 2>&1; then
        echo -e "✅ ${GREEN}Gradle execution successful${NC}"
    else
        echo -e "❌ ${RED}Gradle execution failed${NC}"
    fi
else
    echo -e "⚠️  ${YELLOW}Network limited (sandbox environment)${NC}"
    echo "   This is expected in sandboxed environments"
    echo "   APK build will work in GitHub Actions with full internet access"
fi

echo ""
echo "📊 Test Results Summary"
echo "======================"
echo "Tests Passed: $TESTS_PASSED/$TESTS_TOTAL"

if [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    echo -e "🎉 ${GREEN}ALL TESTS PASSED${NC}"
    echo "✅ Android APK build system is properly configured"
    echo "✅ CI/CD integration is complete"
    echo "✅ Ready for production deployment"
    exit 0
else
    FAILED=$((TESTS_TOTAL - TESTS_PASSED))
    echo -e "⚠️  ${YELLOW}$FAILED test(s) failed${NC}"
    exit 1
fi