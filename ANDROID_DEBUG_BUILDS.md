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

### Automatic Version Incrementing

The project uses a sophisticated git-based versioning system that ensures debug builds update properly:

```gradle
versionCode getGitCommitCount.get() // Dynamic version code based on git commit count
versionName "1.0.${getGitCommitCount.get()}" // Dynamic version name with commit count
```

### Versioning Logic

The version code calculation ensures consistent and predictable versioning:

1. **Baseline**: Uses the master/main branch commit count as the foundation
2. **Feature Branch Addition**: Adds any additional commits from the current branch
3. **Consistent Updates**: Ensures version codes always increase, preventing installation conflicts

**Example:**
- Master branch has 15 commits
- Feature branch has 3 additional commits  
- Version code = 15 + 3 = 18

### Benefits

- **Seamless Updates**: New debug builds always have higher version codes
- **No Installation Conflicts**: Android recognizes new builds as updates
- **Consistent Baseline**: All builds reference the same master branch foundation
- **Branch Independence**: Feature branches get proper version increments

### Troubleshooting Version Issues

If debug builds aren't updating properly:

1. **Check git fetch**: Ensure `git fetch origin` has been run to get latest master/main references
2. **Verify version code**: The version should be master commits + branch commits
3. **Confirm branch base**: Feature branches should be based on latest master/main

## References

- [Android Developer Guide: Build Types](https://developer.android.com/studio/build/build-variants#build-types)
- [Android Package Conflicts](https://developer.android.com/guide/topics/manifest/manifest-element#package)
- [Android Version Codes](https://developer.android.com/studio/publish/versioning#versioningsettings)