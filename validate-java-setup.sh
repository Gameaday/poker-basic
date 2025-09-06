#!/bin/bash
# Pokermon Java Environment Validation and Setup Script
# Ensures Java 21 compatibility for build and runtime

echo "=== Pokermon Java Environment Validation ==="
echo

# Function to check if a directory exists and contains java executable
check_java_home() {
    local java_path="$1"
    local description="$2"
    
    if [ -d "$java_path" ] && [ -x "$java_path/bin/java" ]; then
        local version=$("$java_path/bin/java" -version 2>&1 | head -n 1)
        echo "✅ $description: $java_path"
        echo "   Version: $version"
        return 0
    else
        echo "❌ $description: $java_path (NOT FOUND)"
        return 1
    fi
}

# Check environment variables
echo "Environment Variables:"
echo "JAVA_HOME = ${JAVA_HOME:-'(not set)'}"
echo "JAVA_HOME_21_X64 = ${JAVA_HOME_21_X64:-'(not set)'}"
echo "JAVA_HOME_17_X64 = ${JAVA_HOME_17_X64:-'(not set)'}"
echo

# Check standard Java installations
echo "Java Installation Check:"
check_java_home "/usr/lib/jvm/temurin-21-jdk-amd64" "Java 21 (Target)"
java21_available=$?

check_java_home "/usr/lib/jvm/temurin-17-jdk-amd64" "Java 17 (Fallback)"
java17_available=$?

check_java_home "/usr/lib/jvm/temurin-11-jdk-amd64" "Java 11"
echo

# Check current gradle.properties configuration
echo "Gradle Configuration:"
if [ -f "gradle.properties" ]; then
    grep_result=$(grep "org.gradle.java.home" gradle.properties 2>/dev/null || echo "NOT SET")
    echo "gradle.properties: $grep_result"
else
    echo "❌ gradle.properties file not found"
fi
echo

# Test current system Java
echo "System Java Test:"
java_version=$(java -version 2>&1 | head -n 1)
echo "Default java command: $java_version"
echo "Default java location: $(which java)"
echo

# Test Gradle with current configuration
echo "Gradle Java Configuration Test:"
echo "Testing build with current configuration..."

# Test compilation
if ./gradlew :shared:compileKotlin --no-daemon --quiet 2>/dev/null; then
    echo "✅ Gradle compilation successful"
    
    # Check what Java was actually used by Gradle
    gradle_java_info=$(./gradlew :shared:compileKotlin --no-daemon --info 2>&1 | grep -i "Received JVM installation metadata" | head -1)
    if [ -n "$gradle_java_info" ]; then
        echo "   Gradle used: $gradle_java_info"
    fi
    
    # Test JAR creation
    if ./gradlew :shared:fatJar --no-daemon --quiet 2>/dev/null; then
        echo "✅ JAR build successful"
        
        # Test JAR runtime compatibility
        jar_file=$(find shared/build/libs -name "*-fat.jar" 2>/dev/null | head -1)
        if [ -f "$jar_file" ]; then
            echo "   JAR location: $jar_file"
            
            # Test with Java 21 (if available)
            if [ $java21_available -eq 0 ]; then
                if /usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar "$jar_file" --version >/dev/null 2>&1; then
                    echo "✅ JAR runs correctly with Java 21"
                else
                    echo "❌ JAR fails with Java 21"
                fi
            fi
            
            # Test with system default Java
            if java -jar "$jar_file" --version >/dev/null 2>&1; then
                echo "✅ JAR runs correctly with system default Java"
            else
                echo "⚠️  JAR requires Java 21+ (compiled with newer Java version)"
                echo "   Use: /usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar $jar_file"
            fi
        fi
    else
        echo "❌ JAR build failed"
    fi
else
    echo "❌ Gradle compilation failed"
    echo
    echo "Error details:"
    ./gradlew :shared:compileKotlin --no-daemon 2>&1 | tail -10
fi

echo
echo "=== Recommendations ==="

# Provide recommendations based on findings
if [ $java21_available -eq 0 ]; then
    echo "✅ Java 21 is available and properly configured"
    echo "   For runtime: /usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar [jarfile]"
else
    echo "⚠️  Java 21 not found - checking alternatives..."
    if [ $java17_available -eq 0 ]; then
        echo "   Consider updating gradle.properties to use Java 17:"
        echo "   org.gradle.java.home=/usr/lib/jvm/temurin-17-jdk-amd64"
    fi
fi

echo
echo "=== Usage Instructions ==="
echo "Build commands:"
echo "  ./gradlew :shared:fatJar --no-daemon"
echo "  ./gradlew :shared:test --no-daemon"
echo
echo "Run commands (Java 21):"
echo "  /usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar shared/build/libs/*-fat.jar --help"
echo "  /usr/lib/jvm/temurin-21-jdk-amd64/bin/java -jar shared/build/libs/*-fat.jar --basic"
echo
echo "Validation complete."