# Android Debug Build Configuration

## Overview

This document explains the Android debug build configuration that resolves package conflicts when installing development builds over release builds, and ensures proper version code incrementing for seamless debug build updates.

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

## Version Code Management

### Timestamp-Based Version Incrementing

The project uses a robust timestamp-based versioning system that ensures debug builds update properly regardless of git history depth:

```gradle
versionCode getVersionCode.get() // Dynamic version code based on timestamp
versionName "1.0.${getVersionCode.get()}" // Dynamic version name with timestamp-based version
```

### Versioning Logic

The version code calculation uses days since project epoch plus current hour:

1. **Base Epoch**: June 25, 2025 (project start reference)
2. **Days Calculation**: Days elapsed since base epoch
3. **Hour Addition**: Current hour (0-23) for same-day builds
4. **Formula**: `(days_since_base * 100) + current_hour`

**Example:**
- Date: August 15, 2025, 14:00 UTC
- Days since June 25, 2025: 51 days
- Current hour: 14
- Version code = (51 * 100) + 14 = 5114

### Benefits

- **Always Incrementing**: Version codes increase with time, never conflict
- **Git-Independent**: Works regardless of repository history depth or grafting
- **Multiple Builds Per Day**: Hour component allows up to 24 builds daily
- **Predictable**: Easy to understand and troubleshoot
- **Android Compatible**: Generates reasonable version codes (typically 5-6 digits)

### Troubleshooting Version Issues

#### Problem: Version code not incrementing
**Solution**: 
- Timestamp-based versioning automatically increments with time
- If building multiple times in the same hour, version code remains the same (intended behavior)
- Wait for next hour or modify the timestamp logic if more granular versioning is needed

#### Problem: Version code too large
**Solution**: 
- Current system generates reasonable codes (typically 40000-70000 range)
- Android supports version codes up to 2.1 billion, so no practical limit

#### Problem: Inconsistent version across builds
**Solution**: 
- All builds use system time, so they should be consistent within the same hour
- Ensure system time is correct and synchronized

## References

- [Android Developer Guide: Build Types](https://developer.android.com/studio/build/build-variants#build-types)
- [Android Package Conflicts](https://developer.android.com/guide/topics/manifest/manifest-element#package)
- [Android Version Codes](https://developer.android.com/studio/publish/versioning#versioningsettings)