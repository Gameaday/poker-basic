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
    cp "shared/build/libs/pokermon-${VERSION}-fat.jar" "alpha-builds/pokermon-alpha-${VERSION}.jar"
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

# Try native builds for all platforms and collect artifacts
echo "Building native executables for all platforms..."
echo "  Building Windows executable..."
./gradlew :desktop:packagewindows --no-daemon --continue >/dev/null 2>&1 && echo "✅ Windows build completed" || echo "⚠️  Windows build failed"

echo "  Building Linux executable..."
./gradlew :desktop:packagelinux --no-daemon --continue >/dev/null 2>&1 && echo "✅ Linux build completed" || echo "⚠️  Linux build failed"

echo "  Building macOS executable..."
./gradlew :desktop:packagemacos --no-daemon --continue >/dev/null 2>&1 && echo "✅ macOS build completed" || echo "⚠️  macOS build failed"

# Copy native artifacts to alpha-builds directory
echo ""
echo "Collecting native artifacts..."
if [ -d "desktop/build/distributions" ]; then
    # Copy all native artifacts
    cp desktop/build/distributions/*.deb alpha-builds/ 2>/dev/null && echo "✅ Linux .deb copied to alpha-builds/"
    cp desktop/build/distributions/*.exe alpha-builds/ 2>/dev/null && echo "✅ Windows .exe copied to alpha-builds/"
    cp desktop/build/distributions/*.dmg alpha-builds/ 2>/dev/null && echo "✅ macOS .dmg copied to alpha-builds/"
    cp desktop/build/distributions/*.msi alpha-builds/ 2>/dev/null && echo "✅ Windows .msi copied to alpha-builds/"
    
    # Copy fallback launchers
    cp desktop/build/distributions/*.bat alpha-builds/ 2>/dev/null && echo "✅ Windows launcher (.bat) copied to alpha-builds/"
    cp desktop/build/distributions/*.sh alpha-builds/ 2>/dev/null && echo "✅ Linux launcher (.sh) copied to alpha-builds/"
    cp desktop/build/distributions/*.command alpha-builds/ 2>/dev/null && echo "✅ macOS launcher (.command) copied to alpha-builds/"
fi

# Try Android build (requires internet)
echo ""
echo "Attempting Android build (requires internet)..."
./gradlew :android:assembleDebug --no-daemon --continue >/dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Android APK build succeeded"
    # Copy APK to alpha-builds
    if [ -f "android/build/outputs/apk/debug/android-debug.apk" ]; then
        cp android/build/outputs/apk/debug/android-debug.apk alpha-builds/pokermon-alpha-${VERSION}.apk
        echo "✅ Android APK copied to alpha-builds/"
    fi
else
    echo "⚠️  Android build failed (expected without internet)"
fi

echo ""
echo "=========================================="
echo "Alpha Build Summary"
echo "=========================================="
ls -la alpha-builds/
echo ""

# Count and summarize artifacts
ARTIFACT_COUNT=$(ls -1 alpha-builds/ | wc -l)
echo "Total artifacts generated: $ARTIFACT_COUNT"

# Calculate JAR size
if [ -f "alpha-builds/pokermon-alpha-${VERSION}.jar" ]; then
    JAR_SIZE=$(du -h "alpha-builds/pokermon-alpha-${VERSION}.jar" | awk '{print $1}')
    echo "Primary JAR size: $JAR_SIZE"
fi

# List artifact types
echo ""
echo "Artifact breakdown:"
[ -f alpha-builds/*.jar ] && echo "  ✅ JAR executable (cross-platform)"
[ -f alpha-builds/*.deb ] && echo "  ✅ Linux .deb package (native)"
[ -f alpha-builds/*.exe ] && echo "  ✅ Windows .exe executable (native)"
[ -f alpha-builds/*.dmg ] && echo "  ✅ macOS .dmg package (native)"
[ -f alpha-builds/*.msi ] && echo "  ✅ Windows .msi installer (native)"
[ -f alpha-builds/*.bat ] && echo "  ✅ Windows launcher script (fallback)"
[ -f alpha-builds/*.sh ] && echo "  ✅ Linux launcher script (fallback)"
[ -f alpha-builds/*.command ] && echo "  ✅ macOS launcher script (fallback)"
[ -f alpha-builds/*.apk ] && echo "  ✅ Android APK (mobile)"

echo ""
echo "Testing basic functionality..."
echo "Help system:"
java -jar "alpha-builds/pokermon-alpha-${VERSION}.jar" --help | head -5

echo ""
echo "=========================================="
echo "Alpha Build Complete!"
echo "=========================================="
echo "Primary deliverable: alpha-builds/pokermon-alpha-${VERSION}.jar"
echo "All platform artifacts collected in alpha-builds/ directory"
echo "Ready for manual testing and validation."