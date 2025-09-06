# Java Configuration and Troubleshooting

This document provides comprehensive information about Java setup and troubleshooting for the Pokermon project.

## Java Version Requirements

The project is configured to use **Java 21** for optimal performance and modern language features. Java 17 is supported as a fallback.

### Build Requirements
- **Java 21** (recommended) - Full feature support
- **Java 17** (minimum) - Basic compatibility
- Gradle 8.14+ with proper toolchain configuration

### Runtime Requirements
- JAR files compiled with Java 21 require **Java 21+** to run
- JAR files compiled with Java 17 can run on **Java 17+**

## Configuration

### Gradle Properties
The `gradle.properties` file contains the primary Java configuration:

```properties
# Java version enforcement - Java 21 (Environment-aware configuration)
org.gradle.java.home=/usr/lib/jvm/temurin-21-jdk-amd64
```

### Environment Variables
The build system recognizes these environment variables in priority order:

1. `JAVA_HOME_21_X64` - Java 21 installation path
2. `JAVA_HOME_17_X64` - Java 17 installation path  
3. `JAVA_HOME` - Current Java installation path

### Automatic Detection
The build system includes intelligent Java detection that will:

1. Check for Java 21 at standard locations
2. Fall back to Java 17 if Java 21 is unavailable
3. Use environment variables as additional sources
4. Provide clear feedback about which Java version is being used

## Validation Tools

### Quick Validation
```bash
# Run comprehensive Java environment validation
./validate-java-setup.sh

# Check specific Java detection
./run-pokermon.sh detect
```

### Build Validation
```bash
# Test compilation with current Java setup
./gradlew :shared:compileKotlin --no-daemon

# Build and test JAR creation
./gradlew :shared:fatJar --no-daemon

# Run all tests
./gradlew :shared:test --no-daemon
```

## Troubleshooting

### Common Issues

#### "Invalid Java home supplied"
**Error**: `Value '/usr/lib/jvm/temurin-21-jdk-amd64' given for org.gradle.java.home Gradle property is invalid`

**Causes & Solutions**:
1. **Java 21 not installed**: Install Java 21 or update gradle.properties to use available Java version
2. **Path incorrect**: Verify the exact path with `ls -la /usr/lib/jvm/`
3. **Permissions**: Ensure the Java installation is accessible

**Fix**:
```bash
# Check available Java installations
ls -la /usr/lib/jvm/

# Update gradle.properties to use correct path
# OR let the build system auto-detect:
./validate-java-setup.sh
```

#### "UnsupportedClassVersionError"
**Error**: `java.lang.UnsupportedClassVersionError: class file version 65.0`

**Cause**: JAR compiled with Java 21 but running with older Java version

**Fix**:
```bash
# Use Java 21 to run the application
/usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar shared/build/libs/*-fat.jar --help

# OR use the smart runner script
./run-pokermon.sh --help
```

#### Gradle Daemon Issues
**Error**: Java version conflicts between Gradle runs

**Fix**:
```bash
# Stop all Gradle daemons and restart
./gradlew --stop

# Run build with fresh daemon
./gradlew :shared:fatJar --no-daemon
```

### Environment-Specific Fixes

#### GitHub Actions / CI Environments
```yaml
# In .github/workflows/ci.yml
env:
  JAVA_HOME: /usr/lib/jvm/temurin-21-jdk-amd64
  JAVA_HOME_21_X64: /usr/lib/jvm/temurin-21-jdk-amd64
```

#### Docker Environments
```dockerfile
# Install Java 21
RUN apt-get update && apt-get install -y openjdk-21-jdk

# Set environment
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV JAVA_HOME_21_X64=/usr/lib/jvm/java-21-openjdk-amd64
```

#### Local Development
```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_HOME_21_X64=/usr/lib/jvm/temurin-21-jdk-amd64
export JAVA_HOME_17_X64=/usr/lib/jvm/temurin-17-jdk-amd64

# For immediate use
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
```

## Smart Runner Script

The `run-pokermon.sh` script provides intelligent Java version detection and runtime compatibility:

```bash
# Automatic Java detection and execution
./run-pokermon.sh --help

# Force specific Java version
./run-pokermon.sh java21 --help
./run-pokermon.sh java17 --help

# Test compatibility
./run-pokermon.sh test

# Show detected Java information
./run-pokermon.sh detect
```

## Advanced Configuration

### Custom Java Installation Paths
To use a custom Java installation, update `gradle.properties`:

```properties
org.gradle.java.home=/path/to/your/java21/installation
```

### Multi-Version Support
The build system supports multiple Java versions simultaneously:

- **Development**: Use Java 21 for full feature access
- **Production**: Can deploy with Java 17 compatibility if needed
- **Testing**: Automated testing across supported Java versions

### Performance Optimization
Java 21 provides significant performance improvements:

- **G1GC improvements**: Better garbage collection
- **Virtual threads**: Enhanced concurrency (ready for future use)
- **Pattern matching**: Modern language features in use
- **Record classes**: Already utilized in the codebase

## Verification Commands

### Complete System Check
```bash
# Run comprehensive validation
./validate-java-setup.sh

# Should show:
# ✅ Java 21 (Target): /usr/lib/jvm/temurin-21-jdk-amd64
# ✅ Gradle compilation successful
# ✅ JAR build successful
# ✅ JAR runs correctly with Java 21
```

### Quick Health Check
```bash
# Should complete without errors
./gradlew verifyKotlinNativeSetup --no-daemon
./gradlew :shared:test --no-daemon
./run-pokermon.sh test
```

## Getting Help

If you encounter issues not covered in this guide:

1. Run `./validate-java-setup.sh` and share the output
2. Include the specific error message and context
3. Provide your environment details (OS, Java installations)
4. Check if the issue is environment-specific or reproducible

For the most up-to-date troubleshooting information, see the project's GitHub repository.