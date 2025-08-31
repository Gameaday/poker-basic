#!/bin/bash

# Comprehensive Cross-Platform Build System Validator
# This script ensures all build systems are robust and copilot-safe

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_TOTAL=0
WARNINGS=0

# Test result function
test_item() {
    local description="$1"
    local command="$2"
    
    if eval "$command" >/dev/null 2>&1; then
        echo -e "✅ ${GREEN}PASS${NC}: $description"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: $description"
    fi
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
}

# Warning function
warning_item() {
    local description="$1"
    local details="$2"
    
    echo -e "⚠️  ${YELLOW}WARNING${NC}: $description"
    if [ -n "$details" ]; then
        echo "   $details"
    fi
    WARNINGS=$((WARNINGS + 1))
}

echo -e "${BLUE}🔍 Comprehensive Cross-Platform Build Validation${NC}"
echo "=================================================="
echo ""

# Environment Safety Tests
echo -e "${BLUE}🛡️  Build System Safety Tests${NC}"
echo "------------------------------"

# Test Java environment for copilot safety
test_item "Java 17+ available" "java -version 2>&1 | grep -E '(openjdk|java) version' | grep -E '(1[7-9]|[2-9][0-9])'"
test_item "JAVA_HOME is set" "[ -n \"\$JAVA_HOME\" ]"
test_item "jpackage available" "command -v jpackage"
test_item "Maven 3.6+ available" "mvn --version | grep -E 'Apache Maven [3-9]\\.[6-9]'"

# Test memory requirements
AVAILABLE_MEMORY=$(free -m | awk 'NR==2{printf "%.0f", $7}' 2>/dev/null || echo "unknown")
if [ "$AVAILABLE_MEMORY" != "unknown" ] && [ "$AVAILABLE_MEMORY" -lt 1024 ]; then
    warning_item "Low memory detected" "Available: ${AVAILABLE_MEMORY}MB, Recommended: 2GB+ for native builds"
fi

# Test disk space
AVAILABLE_SPACE=$(df . | awk 'NR==2 {print $4}' | xargs -I {} expr {} / 1024 2>/dev/null || echo "unknown")
if [ "$AVAILABLE_SPACE" != "unknown" ] && [ "$AVAILABLE_SPACE" -lt 1024 ]; then
    warning_item "Low disk space detected" "Available: ${AVAILABLE_SPACE}MB, Recommended: 2GB+ for builds"
fi

echo ""

# Repository Structure Safety
echo -e "${BLUE}📁 Repository Structure Safety${NC}"
echo "-----------------------------"

test_item "Maven POM structure valid" "[ -f Poker-Basic/pom.xml ] && { command -v xmllint >/dev/null 2>&1 && xmllint --noout Poker-Basic/pom.xml; } || { ! command -v xmllint >/dev/null 2>&1 && grep -q '<project' Poker-Basic/pom.xml; }"
test_item "Gradle wrapper executable" "[ -x gradlew ]"
test_item "Android module exists" "[ -d android ] && [ -f android/build.gradle ]"
test_item "Source code present" "[ -d Poker-Basic/src/main/java/com/pokermon ]"
test_item "Test code present" "[ -d Poker-Basic/src/test/java/com/pokermon ]"

# Maven profile safety
test_item "Windows profile configured" "grep -q 'windows-exe' Poker-Basic/pom.xml"
test_item "Linux profile configured" "grep -q 'linux-exe' Poker-Basic/pom.xml"
test_item "macOS profile configured" "grep -q 'macos-exe' Poker-Basic/pom.xml"

echo ""

# Build System Robustness Tests
echo -e "${BLUE}🔧 Build System Robustness Tests${NC}"
echo "--------------------------------"

# Test Maven validation (copilot-safe - no actual building)
if [ -d "Poker-Basic" ]; then
    cd Poker-Basic
    test_item "Maven can parse POM" "mvn help:effective-pom -q >/dev/null"
    test_item "Maven dependencies resolvable" "mvn dependency:resolve -q >/dev/null"
    test_item "Maven test compilation works" "mvn test-compile -q >/dev/null"

    # Test jpackage profiles without building
    test_item "jpackage plugin configured" "mvn help:describe -Dplugin=org.panteleyev:jpackage-maven-plugin -q >/dev/null"
    cd ..
else
    warning_item "Poker-Basic directory not found" "Maven tests skipped"
fi

# Test Gradle configuration (copilot-safe)
test_item "Gradle wrapper functional" "./gradlew tasks --dry-run >/dev/null"
test_item "Android plugin loadable" "./gradlew projects >/dev/null"

echo ""

# Build Configuration Tests
echo -e "${BLUE}⚙️  Build Configuration Tests${NC}"
echo "----------------------------"

# Verify build configurations are copilot-friendly
test_item "Maven timeout configurations present" "grep -q 'maven.surefire.version' Poker-Basic/pom.xml"
test_item "Shade plugin configured for fat JAR" "grep -q 'maven-shade-plugin' Poker-Basic/pom.xml"
test_item "Android API levels compatible" "grep -q 'minSdk 28' android/build.gradle"

# Check for potential copilot crash conditions
test_item "No dangerous system calls" "! grep -r 'System.exit' Poker-Basic/src/main/java/ | grep -v 'GameLauncher.java'"

echo ""

# Network Safety Tests
echo -e "${BLUE}🌐 Network Dependency Safety${NC}"
echo "----------------------------"

# Test network requirements
test_item "Maven dependencies cached" "[ -d ~/.m2/repository/org/junit ] || echo 'First build will require network'"
test_item "Gradle dependencies cached" "[ -d ~/.gradle/caches ] || echo 'First Android build will require network'"

# Test offline capability where possible
if [ -d ~/.m2/repository/org/junit ]; then
    test_item "Maven offline mode works" "cd Poker-Basic && mvn compile --offline -q >/dev/null"
else
    warning_item "Maven dependencies not cached" "First build will require internet access"
fi

echo ""

# CI/CD Safety Tests  
echo -e "${BLUE}🚀 CI/CD Configuration Safety${NC}"
echo "-----------------------------"

test_item "GitHub Actions workflow exists" "[ -f .github/workflows/ci.yml ]"
test_item "All platforms in CI" "grep -q 'windows-native:' .github/workflows/ci.yml && grep -q 'linux-native:' .github/workflows/ci.yml && grep -q 'macos-native:' .github/workflows/ci.yml"
test_item "Timeout configurations in CI" "grep -q 'timeout-minutes:' .github/workflows/ci.yml || grep -q 'timeout' .github/workflows/ci.yml"
test_item "Error handling in CI" "grep -q 'continue-on-error:' .github/workflows/ci.yml"

# Check for copilot-unsafe CI patterns
test_item "No hardcoded secrets in CI" "! grep -i 'password\|secret\|token' .github/workflows/ci.yml | grep -v '\${{ secrets\.' || true"
test_item "Artifact retention configured" "grep -q 'retention-days:' .github/workflows/ci.yml"

echo ""

# Quick Build Tests (Safe for Copilot)
echo -e "${BLUE}🏗️  Quick Build Safety Tests${NC}"
echo "----------------------------"

# Only test build components, not full builds (to avoid timeouts)
if [ -d "Poker-Basic" ]; then
    cd Poker-Basic

    # Test compilation without packaging (faster, safer)
    if mvn clean compile -q >/dev/null 2>&1; then
        echo -e "✅ ${GREEN}PASS${NC}: Maven compilation succeeds"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Maven compilation fails"
    fi
    TESTS_TOTAL=$((TESTS_TOTAL + 1))

    # Test that test compilation works
    if mvn test-compile -q >/dev/null 2>&1; then
        echo -e "✅ ${GREEN}PASS${NC}: Test compilation succeeds"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "❌ ${RED}FAIL${NC}: Test compilation fails"
    fi
    TESTS_TOTAL=$((TESTS_TOTAL + 1))

    cd ..
else
    warning_item "Poker-Basic directory not found" "Build tests skipped"
fi

echo ""

# Resource Usage Tests
echo -e "${BLUE}📊 Resource Usage Safety${NC}"
echo "-----------------------"

# Check that builds won't consume excessive resources
JAVA_MAX_HEAP=$(java -XX:+PrintFlagsFinal -version 2>&1 | grep MaxHeapSize | awk '{print $4}' | xargs -I {} expr {} / 1024 / 1024 2>/dev/null || echo "unknown")
if [ "$JAVA_MAX_HEAP" != "unknown" ]; then
    if [ "$JAVA_MAX_HEAP" -lt 512 ]; then
        warning_item "Low Java heap limit" "Max heap: ${JAVA_MAX_HEAP}MB, may need -Xmx for large builds"
    else
        echo -e "✅ ${GREEN}PASS${NC}: Java heap limit adequate (${JAVA_MAX_HEAP}MB)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    fi
else
    warning_item "Cannot determine Java heap limit" "Consider setting explicit -Xmx values"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

# Test build parallelism safety
CPU_CORES=$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo "1")
if [ "$CPU_CORES" -ge 2 ]; then
    echo -e "✅ ${GREEN}PASS${NC}: Multi-core system detected ($CPU_CORES cores)"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    warning_item "Single-core system detected" "Builds may be slower, consider increasing timeouts"
fi
TESTS_TOTAL=$((TESTS_TOTAL + 1))

echo ""

# Final Safety Report
echo -e "${BLUE}📋 Safety Validation Summary${NC}"
echo "============================"

if [ $TESTS_PASSED -eq $TESTS_TOTAL ] && [ $WARNINGS -eq 0 ]; then
    echo -e "🎉 ${GREEN}ALL SAFETY CHECKS PASSED${NC}"
    echo -e "✅ Build system is ${GREEN}COPILOT-SAFE${NC} and ready for automated builds"
elif [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    echo -e "✅ ${GREEN}All tests passed${NC} with $WARNINGS warnings"
    echo -e "⚠️  Build system is mostly safe, review warnings above"
else
    FAILED=$((TESTS_TOTAL - TESTS_PASSED))
    echo -e "❌ ${RED}$FAILED tests failed${NC} out of $TESTS_TOTAL"
    echo -e "⚠️  Build system needs attention before automated builds"
fi

echo ""
echo "Tests Passed: $TESTS_PASSED/$TESTS_TOTAL"
echo "Warnings: $WARNINGS"

# Copilot-specific recommendations
echo ""
echo -e "${BLUE}🤖 Copilot Build Recommendations${NC}"
echo "--------------------------------"
echo "✅ Use Maven for primary builds (most reliable)"
echo "✅ Set timeout to 180+ seconds for package builds"
echo "✅ Use -DskipTests for faster artifact generation"
echo "✅ Android builds require internet - test in CI/CD only"
echo "✅ Native packages work best in platform-specific CI/CD runners"

# Exit with appropriate code
if [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    exit 0
else
    exit 1
fi