# Android Debug Build Configuration

## Overview

This document explains the Android debug build configuration that resolves package conflicts when installing development builds over release builds.

## Problem Statement

When developing Android applications, it's common to encounter the error:
```
App not installed as package conflicts with an existing package
```

This occurs when:
1. A release build is installed on a device
2. A developer tries to install a debug build over it (or vice versa)
3. Android sees the same package name but different signing certificates
4. The installation fails due to package conflict

## Solution

The project uses a **debug applicationId suffix** to distinguish debug builds from release builds:

### Build Type Configuration

In `android/build.gradle`:

```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
    debug {
        // Faster debug builds
        minifyEnabled false
        debuggable true
        // Use debug suffix to avoid package conflicts with release builds
        applicationIdSuffix ".debug"
    }
}
```

### Resulting Package Names

- **Release builds**: `com.pokermon.android`
- **Debug builds**: `com.pokermon.android.debug`

## Benefits

1. **No Installation Conflicts**: Debug and release builds can be installed simultaneously
2. **Standard Practice**: Follows Android development best practices
3. **Zero Code Changes**: No changes needed to application code or functionality
4. **CI/CD Compatibility**: Works seamlessly with existing build pipeline

## Developer Workflow

### Installing Both Versions

Developers can now:
1. Install release APK from GitHub releases: `com.pokermon.android`
2. Install debug APK from local development: `com.pokermon.android.debug`
3. Test both versions side-by-side without conflicts

### Identifying Builds

On Android devices, both apps will appear as:
- **Release**: "Pokermon" (package: com.pokermon.android)
- **Debug**: "Pokermon" (package: com.pokermon.android.debug)

The debug version can be identified by checking the package name in app settings.

## Technical Details

### Version Management

Both build types use the same dynamic versioning:
- `versionCode`: Git commit count (ensures proper update ordering)
- `versionName`: "1.0.${commitCount}" (semantic versioning with auto-increment)

### Build Artifacts

- **Debug APK**: `android/build/outputs/apk/debug/android-debug.apk`
- **Release APK**: `android/build/outputs/apk/release/android-release.apk`

### CI/CD Pipeline

The GitHub Actions workflow builds both variants:
1. Debug builds for development testing
2. Release builds for production distribution

## Related Files

- `android/build.gradle` - Build configuration
- `.github/workflows/ci.yml` - CI/CD pipeline
- `ANDROID_BUILD_GUIDE.md` - General Android build instructions

## References

- [Android Developer Guide: Build Types](https://developer.android.com/studio/build/build-variants#build-types)
- [Android Package Conflicts](https://developer.android.com/guide/topics/manifest/manifest-element#package)