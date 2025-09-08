# GitHub Actions Composite Actions

This directory contains reusable composite actions that follow DRY (Don't Repeat Yourself) principles to eliminate redundancy in the CI/CD pipeline.

## Available Actions

### 1. setup-java-gradle
**Purpose**: Sets up Java and Gradle environment with caching
**Location**: `.github/actions/setup-java-gradle/`

**Inputs**:
- `java-version` (optional): Java version to install (default: '17')
- `java-distribution` (optional): Java distribution (default: 'temurin')

**Features**:
- Installs specified Java version
- Sets up Gradle wrapper with caching
- Makes gradlew executable
- Verifies installation

### 2. determine-build-context
**Purpose**: Determines build context, execution conditions, and artifact management
**Location**: `.github/actions/determine-build-context/`

**Outputs**:
- `should-run`: Whether the job should execute based on event and actor conditions
- `retention-days`: Number of days to retain artifacts (14 for PRs, 90 for main)
- `artifact-suffix`: Suffix for artifact names (e.g., "-pr-123")
- `is-pr`: Boolean indicating if this is a pull request build
- `pr-number`: Pull request number (if applicable)

**Features**:
- Unified conditional logic for all jobs
- Handles Copilot bot execution permissions
- Standardized artifact retention policies
- Consistent naming conventions

### 3. setup-android-sdk
**Purpose**: Sets up Android SDK with proper caching and license handling
**Location**: `.github/actions/setup-android-sdk/`

**Inputs**:
- `api-level` (optional): Android API level (default: '34')
- `build-tools-version` (optional): Build tools version (default: '34.0.0')
- `ndk-version` (optional): NDK version (default: '25.2.9519653')

**Features**:
- Downloads and installs Android SDK components
- Pre-accepts all Android licenses
- Implements intelligent caching
- Verifies installation completeness

### 4. upload-build-artifacts
**Purpose**: Standardized artifact upload with consistent naming and retention
**Location**: `.github/actions/upload-build-artifacts/`

**Inputs**:
- `artifact-name` (required): Base name for the artifact
- `artifact-path` (required): Path to files to upload
- `retention-days` (optional): Retention period (default: '30')
- `artifact-suffix` (optional): Suffix for artifact names (default: '')

**Features**:
- Consistent artifact naming
- Standardized retention policies
- Upload status reporting

## Usage Examples

### Basic Java/Gradle Setup
```yaml
- name: Setup Java and Gradle
  uses: ./.github/actions/setup-java-gradle
  with:
    java-version: '17'
    java-distribution: 'temurin'
```

### Build Context Determination
```yaml
- name: Determine build context
  uses: ./.github/actions/determine-build-context
  id: context

- name: Skip if conditions not met
  if: steps.context.outputs.should-run != 'true'
  run: echo "Skipping job"

- name: Upload with context
  if: steps.context.outputs.should-run == 'true'
  uses: ./.github/actions/upload-build-artifacts
  with:
    artifact-name: my-artifact
    artifact-path: build/libs/*.jar
    retention-days: ${{ steps.context.outputs.retention-days }}
    artifact-suffix: ${{ steps.context.outputs.artifact-suffix }}
```

### Android SDK Setup
```yaml
- name: Setup Android SDK
  uses: ./.github/actions/setup-android-sdk
  with:
    api-level: '34'
    build-tools-version: '34.0.0'
```

## Benefits of DRY Implementation

### Before (Redundant Code)
- 4 jobs × 30 lines of Java/Gradle setup = 120 lines
- 4 jobs × 15 lines of condition logic = 60 lines  
- 4 jobs × 20 lines of artifact logic = 80 lines
- **Total**: ~260 lines of redundant code

### After (DRY Implementation)
- 4 composite actions × 50 lines average = 200 lines
- 4 jobs × 5 lines of action calls = 20 lines
- **Total**: ~220 lines with better maintainability

### Improvements
- **Maintainability**: Changes to setup logic require updates in only one place
- **Consistency**: All jobs use identical setup procedures
- **Readability**: Main workflow focuses on business logic, not boilerplate
- **Testability**: Composite actions can be tested independently
- **Reusability**: Actions can be used in other workflows

## Workflow Environment Variables

The main CI workflow now uses environment variables following DRY principles:

```yaml
env:
  JAVA_VERSION: '17'
  JAVA_DISTRIBUTION: 'temurin'
  ANDROID_API_LEVEL: '34'
  ANDROID_BUILD_TOOLS: '34.0.0'
  # Timeout values based on job complexity
  LINT_TIMEOUT: 15
  TEST_TIMEOUT: 20
  BUILD_TIMEOUT: 30
  ANDROID_TIMEOUT: 60
```

This ensures consistent configuration across all jobs and makes updates simple.