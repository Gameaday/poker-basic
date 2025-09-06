#!/bin/bash
# Pokermon Smart Java Wrapper
# Automatically selects the best available Java version for runtime

# Function to find best Java executable
find_best_java() {
    # Priority order: Java 21 > Java 17 > Java 11 > System default
    local java_paths=(
        "/usr/lib/jvm/temurin-21-jdk-amd64/bin/java"
        "$JAVA_HOME_21_X64/bin/java"
        "/usr/lib/jvm/temurin-17-jdk-amd64/bin/java"
        "$JAVA_HOME_17_X64/bin/java"
        "/usr/lib/jvm/temurin-11-jdk-amd64/bin/java"
        "$JAVA_HOME_11_X64/bin/java"
        "$JAVA_HOME/bin/java"
        "java"
    )
    
    for java_path in "${java_paths[@]}"; do
        if [ -n "$java_path" ] && [ "$java_path" != "/bin/java" ] && command -v "$java_path" >/dev/null 2>&1; then
            # Test that this Java can actually run
            if "$java_path" -version >/dev/null 2>&1; then
                echo "$java_path"
                return 0
            fi
        fi
    done
    
    # Fallback to system java
    echo "java"
}

# Function to check Java version compatibility
check_java_compatibility() {
    local java_exe="$1"
    local jar_file="$2"
    
    # Try to run the JAR and capture any version errors
    if "$java_exe" -jar "$jar_file" --version >/dev/null 2>&1; then
        return 0
    else
        # Check if it's a version compatibility issue
        local error_output=$("$java_exe" -jar "$jar_file" --version 2>&1)
        if echo "$error_output" | grep -q "UnsupportedClassVersionError"; then
            return 1
        else
            # Other error, might still be runnable for basic operations
            return 2
        fi
    fi
}

# Main execution
main() {
    local action="${1:-help}"
    local java_exe
    local jar_file
    
    # Find the JAR file
    jar_file=$(find shared/build/libs -name "*-fat.jar" 2>/dev/null | head -1)
    if [ ! -f "$jar_file" ]; then
        echo "Error: JAR file not found. Please build first with:"
        echo "  ./gradlew :shared:fatJar --no-daemon"
        exit 1
    fi
    
    case "$action" in
        "help"|"--help"|"-h")
            echo "Pokermon Smart Java Runner"
            echo "Usage: $0 [action] [arguments...]"
            echo ""
            echo "Actions:"
            echo "  help, --help, -h     Show this help"
            echo "  run [args...]        Run with best available Java"
            echo "  java21 [args...]     Force Java 21"
            echo "  java17 [args...]     Force Java 17"
            echo "  detect               Show detected Java information"
            echo "  test                 Test JAR compatibility"
            echo ""
            echo "JAR file: $jar_file"
            ;;
            
        "detect")
            java_exe=$(find_best_java)
            echo "Detected Java: $java_exe"
            "$java_exe" -version 2>&1 | head -3
            echo "JAR file: $jar_file"
            check_java_compatibility "$java_exe" "$jar_file"
            case $? in
                0) echo "✅ Compatible - JAR can run with this Java version" ;;
                1) echo "❌ Version incompatible - JAR requires newer Java" ;;
                2) echo "⚠️  Unknown compatibility - other runtime error detected" ;;
            esac
            ;;
            
        "test")
            java_exe=$(find_best_java)
            echo "Testing JAR compatibility with: $java_exe"
            check_java_compatibility "$java_exe" "$jar_file"
            case $? in
                0) 
                    echo "✅ Test passed - running help command:"
                    "$java_exe" -jar "$jar_file" --help | head -10
                    ;;
                1) 
                    echo "❌ Version incompatible - trying Java 21 specifically..."
                    if command -v "/usr/lib/jvm/temurin-21-jdk-amd64/bin/java" >/dev/null 2>&1; then
                        "/usr/lib/jvm/temurin-21-jdk-amd64/bin/java" -jar "$jar_file" --help | head -5
                    else
                        echo "Java 21 not available at expected location"
                    fi
                    ;;
                *) 
                    echo "⚠️  Unknown error occurred"
                    ;;
            esac
            ;;
            
        "java21")
            shift
            if command -v "/usr/lib/jvm/temurin-21-jdk-amd64/bin/java" >/dev/null 2>&1; then
                exec "/usr/lib/jvm/temurin-21-jdk-amd64/bin/java" -jar "$jar_file" "$@"
            else
                echo "Error: Java 21 not found at /usr/lib/jvm/temurin-21-jdk-amd64/bin/java"
                exit 1
            fi
            ;;
            
        "java17")
            shift
            if command -v "/usr/lib/jvm/temurin-17-jdk-amd64/bin/java" >/dev/null 2>&1; then
                exec "/usr/lib/jvm/temurin-17-jdk-amd64/bin/java" -jar "$jar_file" "$@"
            else
                echo "Error: Java 17 not found at /usr/lib/jvm/temurin-17-jdk-amd64/bin/java"
                exit 1
            fi
            ;;
            
        "run")
            shift
            java_exe=$(find_best_java)
            echo "Using Java: $java_exe" >&2
            exec "$java_exe" -jar "$jar_file" "$@"
            ;;
            
        *)
            # Default: try to run with arguments
            java_exe=$(find_best_java)
            exec "$java_exe" -jar "$jar_file" "$@"
            ;;
    esac
}

# Execute main function with all arguments
main "$@"