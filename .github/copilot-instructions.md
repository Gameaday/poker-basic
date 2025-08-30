# Poker Basic - GitHub Copilot Instructions

**Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

This is a poker game repository that is currently in a minimal, transitional state. The repository contains only a README.md file as the maintainer plans to rewrite the entire application in Flutter as a learning project.

## Repository State and Expectations

- **Current State**: Minimal repository with only README.md. No buildable code exists on the default branch.
- **Legacy Code**: Historical Java implementation exists on the `STALE_Eclipse_Java` branch but has compilation errors and is not maintained.
- **Planned Technology**: Flutter rewrite mentioned in README.md but not yet implemented.
- **Build Status**: No build system, tests, or runnable code currently exists.
- **CI/CD**: No GitHub Actions workflows for building or testing application code.

## Working Effectively

### Repository Setup
- Clone the repository: `git clone https://github.com/Gameaday/poker-basic.git`
- Navigate to repository: `cd poker-basic`
- Check current branch: `git branch` (defaults to `master` branch)
- **Note**: These instructions and additional files like `.gitignore` may be on development branches during active work
- Repository contains only: `README.md` and `.git/` directory (on `master` branch)

### Basic Operations
- View repository contents: `ls -la` (expect only README.md)
- Read project description: `cat README.md`
- Check git status: `git status`
- List available branches: `git branch -a` or `git ls-remote --heads origin`

### Exploring Repository History
- Fetch all branches: `git fetch origin`
- Explore legacy Java code: 
  - `git fetch origin STALE_Eclipse_Java:java-branch`
  - `git checkout java-branch`
  - `ls -la` (will show Java project structure with Maven build files)
- Return to current working branch: `git checkout master` (or working branch name)

### Available Tools and Dependencies
- **Java**: OpenJDK 17 available (`java -version`)
- **Maven**: Maven 3.9+ available (`mvn -version`)
- **Flutter/Dart**: Not installed by default (would need installation for Flutter development)
- **Git**: Standard git operations work normally

## Build and Test Status

### Current Branch (master/main)
- **Build**: No build system exists. No package.json, Makefile, or build scripts.
- **Test**: No tests exist. No test framework configured.
- **Run**: No runnable application. Only documentation available.
- **Lint**: No linting configuration. No code to lint.

### Legacy Java Branch (STALE_Eclipse_Java)
- **Build**: `cd Poker-Basic && mvn clean compile` - **FAILS** due to compilation errors (missing method recursiveBet). Build time: ~10 seconds.
- **Legacy Code**: NetBeans/Maven project with Java 17 configuration but contains compilation errors.
- **Dependencies**: Uses Maven but no external dependencies defined in pom.xml.
- **Tests**: No tests found in the legacy codebase.

## Development Guidelines

### For Current State
- Repository is intentionally minimal while awaiting Flutter rewrite
- No functional code to build, test, or run
- Documentation changes can be made to README.md
- New development should follow Flutter/Dart patterns when implemented

### For Future Flutter Development
- Install Flutter SDK: Follow official Flutter installation guide for your platform
- Verify installation: `flutter doctor`
- Create Flutter project: `flutter create poker_game` (when ready to implement)
- Common Flutter commands:
  - `flutter pub get` - Install dependencies
  - `flutter run` - Run app in development mode
  - `flutter test` - Run tests
  - `flutter build` - Build for production

### For Legacy Java Code (Reference Only)
- **DO NOT** attempt to fix compilation errors in legacy Java code
- Legacy code is marked as STALE and not maintained
- Maven build will fail: `mvn clean compile` shows missing `recursiveBet` method
- Code exists for reference but is not intended for active development

## Validation and Testing

### Current Repository
- **File Access**: Verify README.md is readable with `cat README.md`
- **Git Operations**: Verify git commands work (`git status`, `git log`)
- **Branch Exploration**: Verify ability to fetch and examine different branches
- **No Functional Testing**: No application to test since no runnable code exists

### When Flutter Code is Added
- Always run `flutter doctor` to verify development environment
- Test basic Flutter operations: `flutter pub get && flutter analyze`
- Run Flutter tests: `flutter test`
- Verify app builds: `flutter build web` (or target platform)

## Common Tasks and File Locations

### Repository Structure (Current)
```
/
├── .git/
└── README.md
```

### Repository Structure (Legacy Java Branch)
```
/
├── .git/
├── .gitignore
├── .vscode/
├── Poker-Basic/
│   ├── pom.xml
│   ├── src/
│   └── target/
├── build.xml
├── build/
├── demo/
├── dist/
└── nbproject/
```

### Key Information Commands
- Repository info: `ls -la && cat README.md`
- Git branches: `git ls-remote --heads origin`
- Legacy code exploration: `git fetch origin STALE_Eclipse_Java:java-branch && git checkout java-branch`

### Quick Reference
- **README.md**: Contains project description and rewrite plans
- **No package.json**: This is not a Node.js project
- **No Makefile**: No build automation exists
- **No Dockerfile**: No containerization setup
- **No CI files**: No automated builds or tests

## Important Notes

- **NEVER** expect builds or tests to work on current branch - no code exists
- **NEVER** try to fix legacy Java code - it's intentionally stale
- **ALWAYS** check which branch you're on with `git branch`
- **ALWAYS** read README.md first to understand current project state
- **Flutter development** would start fresh, not build on legacy code
- **Compilation of legacy Java fails** with symbol errors (missing recursiveBet method)
- **No automated workflows** - repository is in transition state

## Time Expectations

- Repository clone: ~5-10 seconds
- Branch switching: ~1-2 seconds  
- File operations: Instantaneous (only README.md exists)
- Maven build attempt (legacy): ~10 seconds (will fail)
- Git operations: Standard timing

This repository is unique in that it's intentionally minimal while awaiting a complete rewrite. Focus on documentation improvements and prepare for future Flutter development rather than attempting to build non-existent code.