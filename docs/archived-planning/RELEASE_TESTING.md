# Alpha Release Testing Guide

## Overview
This repository now includes automated GitHub release creation for alpha testing. The CI/CD pipeline has been enhanced to create GitHub releases with JAR artifacts for different branch types.

## Release Workflow Features

### Automatic Release Creation
The workflow creates different types of releases based on the branch:

- **Alpha Release**: Triggered by branches starting with `alpha-`
  - Creates pre-release with "Alpha Test" label
  - Tag format: `alpha-{version}-{timestamp}`
  - Example: `alpha-1.0.0-20240830-120000`

- **Development Release**: Triggered by pushes to `main` or `master`
  - Creates pre-release with "Development Build" label
  - Tag format: `dev-{version}-{timestamp}`

- **Official Release**: Triggered by version tags starting with `v`
  - Creates official release
  - Tag format: Uses the actual tag (e.g., `v1.0.0`)

### Release Assets
Each release includes:
- Executable JAR file (`pokermon-{version}.jar`)
- Comprehensive release notes with usage instructions
- Version information and build details
- Quick start guide for both GUI and console versions

## Testing the Alpha Release

### Step 1: Create Alpha Branch
```bash
git checkout -b alpha-test-release
git push origin alpha-test-release
```

### Step 2: Workflow Execution
The workflow will automatically:
1. Run tests and build the project
2. Create a distributable JAR
3. Generate a timestamped alpha release
4. Upload the JAR as a release asset

### Step 3: Verify Release
1. Check the [Releases page](https://github.com/Gameaday/poker-basic/releases)
2. Download the JAR file
3. Test the application:
   ```bash
   # GUI Version
   java -cp pokermon-1.0.0.jar com.pokermon.NewJFrame
   
   # Console Version
   java -cp pokermon-1.0.0.jar com.pokermon.Main
   ```

## Release Notes Template
Each release includes standardized information:
- Release type and version
- Build timestamp and commit details
- Quick start instructions
- System requirements (Java 17+)
- Educational project context

## Branch Strategy
- `alpha-*` branches: For alpha testing releases
- `main`/`master`: Development releases
- `v*` tags: Official releases

## Workflow Permissions
The workflow uses `GITHUB_TOKEN` with standard repository permissions to:
- Create releases
- Upload assets
- Generate release notes

This implementation ensures that alpha testing can be conducted efficiently with proper versioning and asset distribution through GitHub releases.