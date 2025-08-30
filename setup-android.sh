#!/bin/bash

# Android Build Setup Script for Poker Game
# This script configures the Android build when network access is available

echo "=== Android Build Setup ==="

# Check if we have internet connectivity
if curl -s --connect-timeout 5 https://dl.google.com >/dev/null 2>&1; then
    echo "✅ Internet connection available"
    
    # Enable Android plugin in app/build.gradle
    if ! grep -q "apply plugin: 'com.android.application'" app/build.gradle; then
        echo "Enabling Android plugin in app/build.gradle..."
        sed -i '1i apply plugin: '\''com.android.application'\''' app/build.gradle
    fi
    
    # Enable Android plugin dependencies in root build.gradle
    if ! grep -q "classpath 'com.android.tools.build:gradle:" build.gradle; then
        echo "Adding Android plugin to buildscript dependencies..."
        sed -i '/dependencies {/a\        classpath '\''com.android.tools.build:gradle:8.1.0'\''' build.gradle
        sed -i '/buildscript {/a\    repositories {\n        google()\n        mavenCentral()\n    }\n    dependencies {' build.gradle
    fi
    
    echo "✅ Android build configuration enabled"
    echo "You can now run: ./gradlew assembleDebug"
    
else
    echo "⚠️ No internet connection - Android build not available"
    echo "Android configuration files are present but require network access"
    echo "Run this script again when connected to the internet"
fi

echo ""
echo "=== Verification ==="
./gradlew verifyDualPlatformSetup --no-daemon