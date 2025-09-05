#!/bin/bash
#
# Alpha Build Generation Script for Pokermon
# Creates alpha builds for all supported platforms
#

set -e

echo "=========================================="
echo "Pokermon Alpha Build Generation"
echo "=========================================="

# Get version from Gradle
VERSION=$(cd "$(dirname "$0")" && ./gradlew properties -q | grep "^version:" | head -1 | awk '{print $2}')
echo "Building version: $VERSION"

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean --no-daemon --quiet

# Create build output directory
mkdir -p alpha-builds

echo ""
echo "Building Kotlin-native JAR (Primary Alpha Build)..."
./gradlew :shared:fatJar --no-daemon
if [ $? -eq 0 ]; then
    cp "Poker-Basic/build/libs/shared-${VERSION}-fat.jar" "alpha-builds/pokermon-alpha-${VERSION}.jar"
    echo "✅ Alpha JAR: alpha-builds/pokermon-alpha-${VERSION}.jar"
    
    # Test the JAR
    echo "Testing JAR functionality..."
    java -jar "alpha-builds/pokermon-alpha-${VERSION}.jar" --help >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "✅ JAR functionality verified"
    else
        echo "⚠️  JAR test failed"
    fi
else
    echo "❌ JAR build failed"
    exit 1
fi

echo ""
echo "Attempting native builds (may fail without proper setup)..."

# Try native builds
echo "Building native executables..."
./gradlew :desktop:packageNative --no-daemon --continue >/dev/null 2>&1 && echo "✅ Native build succeeded" || echo "⚠️  Native build failed (expected without native toolchain)"

# Try Android build (requires internet)
echo "Attempting Android build (requires internet)..."
./gradlew :android:assembleDebug --no-daemon --continue >/dev/null 2>&1 && echo "✅ Android APK build succeeded" || echo "⚠️  Android build failed (expected without internet)"

echo ""
echo "=========================================="
echo "Alpha Build Summary"
echo "=========================================="
ls -la alpha-builds/
echo ""

# Calculate JAR size
JAR_SIZE=$(du -h "alpha-builds/pokermon-alpha-${VERSION}.jar" | awk '{print $1}')
echo "Alpha JAR size: $JAR_SIZE"

echo ""
echo "Testing basic functionality..."
echo "Help system:"
java -jar "alpha-builds/pokermon-alpha-${VERSION}.jar" --help | head -5

echo ""
echo "=========================================="
echo "Alpha Build Complete!"
echo "=========================================="
echo "Primary deliverable: alpha-builds/pokermon-alpha-${VERSION}.jar"
echo "Ready for manual testing and validation."