# Gradle & Android Gradle Plugin Upgrade Guide

This guide documents the upgrade path for keeping Gradle and Android Gradle Plugin (AGP) up to date in the Pokermon project.

## Current Versions (as of 2026-02-09)

- **Gradle**: 9.3.1
- **Android Gradle Plugin (AGP)**: 9.0.0
- **Kotlin**: 2.2.21

## Version Compatibility Matrix

| Android Gradle Plugin | Minimum Gradle Version | Recommended Gradle |
|----------------------|------------------------|-------------------|
| 8.7.x                | 8.9                    | 8.13              |
| 9.0.x                | 9.0                    | 9.3+              |
| 9.1.x (future)       | 9.1                    | 9.4+              |
| 10.0.x (future)      | 9.5+                   | TBD               |

## Upgrade Path: Gradle 8.13 → 9.3.1 (Completed)

### Changes Made

1. **Updated Gradle Wrapper** (`gradle/wrapper/gradle-wrapper.properties`):
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-9.3.1-bin.zip
   ```

2. **Restored Android Gradle Plugin** (`build.gradle`):
   ```gradle
   classpath 'com.android.tools.build:gradle:9.0.0'
   ```

3. **Removed deprecated kotlin-android plugin** (`android/build.gradle`):
   - Removed: `id 'kotlin-android'`
   - Reason: AGP 9.0+ integrates Kotlin support natively

4. **Updated Kotlin configuration** (`android/build.gradle`):
   ```gradle
   // Old (AGP 8.x):
   kotlinOptions {
       jvmTarget = '17'
   }
   
   // New (AGP 9.0+):
   kotlin {
       jvmToolchain(17)
   }
   ```

### Benefits of Upgrading

- **Future-proof**: Staying current with latest stable versions
- **New features**: Access to latest Gradle and AGP improvements
- **Better performance**: Gradle 9.x includes performance optimizations
- **Security updates**: Latest versions include security fixes
- **Configuration cache**: Gradle 9.x has improved configuration cache support

## How to Upgrade in the Future

### Step 1: Check Compatibility

Before upgrading, always check the compatibility matrix:
- [Gradle Releases](https://gradle.org/releases/)
- [AGP Release Notes](https://developer.android.com/build/releases/gradle-plugin)

### Step 2: Update Gradle Wrapper

```bash
# Check available Gradle versions
curl -s https://services.gradle.org/versions/all | grep version

# Update wrapper (replace X.Y.Z with desired version)
./gradlew wrapper --gradle-version=X.Y.Z
```

Or manually edit `gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-X.Y.Z-bin.zip
```

### Step 3: Update Android Gradle Plugin

Edit `build.gradle`:
```gradle
dependencies {
    classpath 'com.android.tools.build:gradle:X.Y.Z'
    // ... other dependencies
}
```

### Step 4: Handle Breaking Changes

Common breaking changes to watch for:

**AGP 9.0:**
- ✅ Removed `kotlin-android` plugin (integrated natively)
- ✅ Changed `kotlinOptions` to `kotlin` block with `jvmToolchain()`
- ⚠️ `android.enableJetifier` deprecated (remove from gradle.properties if not needed)

**AGP 10.0 (future):**
- Jetifier will be removed entirely
- Check release notes for other breaking changes

### Step 5: Test Thoroughly

```bash
# Verify build configuration
./gradlew verifyKotlinNativeSetup --no-daemon

# Run all tests
./gradlew :shared:test --no-daemon

# Build all artifacts
./gradlew :shared:fatJar --no-daemon

# Test Android build (if applicable)
./gradlew :android:assembleDebug --no-daemon
```

### Step 6: Update Documentation

Update this guide and PROJECT_STATUS.md with new versions.

## Troubleshooting

### Error: "Minimum supported Gradle version is X.Y.Z"

**Cause**: AGP version requires newer Gradle than currently installed.

**Solution**: Upgrade Gradle wrapper to required version or newer.

### Error: "Could not find method kotlinOptions()"

**Cause**: Using old `kotlinOptions` syntax with AGP 9.0+.

**Solution**: Replace with `kotlin { jvmToolchain(17) }` block.

### Error: "Failed to apply plugin 'kotlin-android'"

**Cause**: AGP 9.0+ has integrated Kotlin support.

**Solution**: Remove `id 'kotlin-android'` from plugins block.

## Checking for Updates

### Automated Dependency Checking

Consider using [Gradle Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html) or [Dependabot](https://docs.github.com/en/code-security/dependabot) for automated dependency updates.

### Manual Checking

```bash
# Check for Gradle updates
./gradlew wrapper --gradle-version=latest

# Check for AGP updates
# Visit: https://developer.android.com/build/releases/gradle-plugin

# Check for Kotlin updates
# Visit: https://kotlinlang.org/docs/releases.html
```

## Configuration Cache

Gradle 9.x has improved configuration cache support. To enable:

```bash
# Add to gradle.properties
org.gradle.configuration-cache=true
```

This can significantly speed up builds.

## Best Practices

1. **Stay on stable releases**: Avoid RC/milestone builds in production
2. **Test in CI/CD first**: Validate upgrades in CI before merging
3. **Read release notes**: Always review breaking changes
4. **Upgrade incrementally**: Don't skip major versions
5. **Keep dependencies aligned**: Ensure Kotlin, AGP, and Gradle are compatible
6. **Document changes**: Update this guide with each upgrade

## Resources

- [Gradle Release Notes](https://docs.gradle.org/current/release-notes.html)
- [Android Gradle Plugin Release Notes](https://developer.android.com/build/releases/gradle-plugin)
- [Kotlin Release Notes](https://kotlinlang.org/docs/releases.html)
- [Gradle Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)

---

*Last Updated: 2026-02-09*
*Gradle 9.3.1 + AGP 9.0.0 + Kotlin 2.2.21*
