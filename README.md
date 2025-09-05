# 🐲 Pokermon - Poker Monster Game

[![Build Status](https://img.shields.io/github/actions/workflow/status/Gameaday/poker-basic/ci.yml?branch=main&label=Build%20Status)](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)
[![GitHub Release](https://img.shields.io/github/v/release/Gameaday/poker-basic?include_prereleases&label=Latest%20Release)](https://github.com/Gameaday/poker-basic/releases/latest)
[![GitHub Downloads](https://img.shields.io/github/downloads/Gameaday/poker-basic/total?label=Downloads)](https://github.com/Gameaday/poker-basic/releases)
[![Tests](https://img.shields.io/badge/tests-254%20passing-brightgreen)](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)

**Where Poker meets Monster Collecting!**

A comprehensive cross-platform poker game featuring monster collection mechanics, multiple game modes, and persistent user profiles. Built with modern **Kotlin-native architecture** for unified development and professional practices.

## 🚀 Quick Start

### 📦 Download & Play (Recommended)
```bash
# Download latest release from GitHub
wget https://github.com/Gameaday/poker-basic/releases/latest/download/pokermon-1.0.0.jar
java -jar pokermon-1.0.0.jar           # GUI mode (recommended)
java -jar pokermon-1.0.0.jar --basic   # Console mode
java -jar pokermon-1.0.0.jar --help    # Show all options
```

### ⚡ Production Build Commands
```bash
git clone https://github.com/Gameaday/poker-basic.git && cd poker-basic
./validate-android-build.sh            # Verify build system (21 checks)
cd Poker-Basic && mvn clean package -B  # Create production JAR (~15 seconds)
java -jar target/pokermon-1.0.0-fat.jar # Run the game
```

### 🔧 Development Build Commands  
```bash
cd Poker-Basic
mvn clean compile -B    # Compile only (~10 seconds)
mvn test -B            # Run tests (~12 seconds)
mvn clean package -B   # Full build with tests (~20 seconds)
```

## 🔄 CI/CD & Build Status

### 📊 Current Build Status
- [![Latest Commit Build](https://img.shields.io/github/actions/workflow/status/Gameaday/poker-basic/ci.yml?branch=main&label=Latest%20Commit&logo=github)](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)
- [![Test Results](https://img.shields.io/badge/tests-254%20tests-brightgreen?logo=junit5)](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)
- [![Build Time](https://img.shields.io/badge/build%20time-~15min-blue?logo=githubactions)](https://github.com/Gameaday/poker-basic/actions)
- [![Platforms](https://img.shields.io/badge/platforms-5%20supported-success?logo=java)](https://github.com/Gameaday/poker-basic/releases)

### 🏗️ CI/CD Pipeline
The automated pipeline tests and builds for all platforms on every commit:
- **Test Job**: Runs 254 comprehensive tests
- **Package Job**: Creates cross-platform JAR 
- **Android Build**: Creates APK for mobile
- **Native Builds**: Windows EXE, Linux DEB, macOS DMG
- **Release Job**: Publishes artifacts to GitHub Releases

**[View Latest Build Results →](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)**

## 🚀 Build Information

### 📦 Production Builds (Ready to Use)

Download pre-built releases for immediate use:

```bash
# Download latest stable release
wget https://github.com/Gameaday/poker-basic/releases/latest/download/pokermon-1.0.0.jar
java -jar pokermon-1.0.0.jar

# Or use GitHub CLI  
gh release download --repo Gameaday/poker-basic --pattern "*.jar"
```

**Production Releases:**
- ✅ Fully tested with 190 passing tests
- ✅ Code-signed and verified
- ✅ Available for all platforms
- ✅ No build environment required

### 🔧 Development Builds (For Contributors)

Build from source for development and testing:

```bash
# Quick development build
git clone https://github.com/Gameaday/poker-basic.git && cd poker-basic
./validate-android-build.sh            # Verify system (21 checks, <1 second)
cd Poker-Basic && mvn clean package -B  # Build JAR (~15 seconds)

# Development workflow  
mvn clean compile -B    # Compile only (~10 seconds)
mvn test -B            # Run tests (~12 seconds) 
mvn clean package -B   # Full build (~20 seconds)
```

**Development Requirements:**
- Java 17+ (OpenJDK recommended)
- Maven 3.6+ for JAR builds
- Internet connection for Android builds
- 2GB+ RAM for native platform builds

## 📋 Platform Support

| Platform | Download | Requirements |
|----------|----------|--------------|
| **Cross-Platform** | `pokermon-1.0.0.jar` | Java 17+ |
| **Windows** | `PokerGame-1.0.0.exe` | Windows 10+ (64-bit) |
| **Linux** | `pokergame_1.0.0-1_amd64.deb` | Ubuntu 18.04+ |
| **macOS** | `PokerGame-1.0.0.dmg` | macOS 10.14+ |
| **Android** | `android-debug.apk` | Android 5.0+ (API 21+) |

📱 **[Download Latest Release](https://github.com/Gameaday/poker-basic/releases/latest)** - All platforms available

### 🎯 Release Management (For Maintainers)

| Release Type | Trigger | Use Case |
|--------------|---------|----------|
| **Alpha** | `alpha-*` branch | Testing new features |
| **Development** | Push to `main`/`master` | Latest stable code |
| **Official** | `v*.*.*` tag | Public releases |
| **PR Testing** | Pull Request | Code review artifacts |

## Version 1.0.0 - Complete Pokermon Experience

### 🎮 Game Features

- **🃏 Complete 5-card draw poker engine** with comprehensive hand evaluation
- **🐲 Monster-themed game modes** - Adventure, Safari, Ironman, and Classic modes  
- **👥 Multi-player adventures** - Battle against 1-3 AI opponents with intelligent strategies
- **📱 Cross-platform gaming** - Desktop JAR, Android APK, and modern web-ready architecture
- **💾 Persistent user profiles** - Statistics, achievements, and progress tracking
- **🎨 Customizable themes** - Multiple poker table styles with persistent selection
- **⚙️ Auto-save functionality** - Background saving of all user data and game progress
- **🏆 Achievement system** - Unlock rewards based on gameplay milestones

### 🏠 User Experience
- **Personalized welcome** with user statistics and progress display
- **Real-time achievement tracking** with automatic unlocking system
- **Comprehensive profile management** including games played, win rates, and monster progress
- **Persistent settings** that auto-save without user intervention

### 🐲 Monster Integration
- **Adventure Mode** - Battle monsters in poker duels (framework ready)
- **Safari Mode** - Capture monsters through strategic gameplay (UI implemented)
- **Ironman Mode** - Convert poker winnings into monster gacha pulls (system designed)
- **Monster collection tracking** - Progress counters for future monster features

### 🎨 Modern Design
- **Monster-themed UI elements** throughout the application
- **Pokermon table themes** - Classic Green, Royal Blue, Crimson Red, Midnight Black, Bourbon Brown
- **Enhanced iconography** with monster-themed emojis and styling
- **Cohesive branding** across desktop and mobile platforms

## 📱 Platform-Specific Features

### Desktop (JAR)
- Full JavaFX GUI with card graphics
- Console/text mode option (`--basic`)
- Complete keyboard/mouse controls
- Works on any Java 17+ system

### Android (APK)
- Native Material Design interface
- Touch-friendly game controls
- Optimized for phones and tablets
- Requires Android 5.0+ (API 21+)

### Native Executables
- Self-contained apps (no Java installation required)
- Native OS integration
- Platform-specific installers
- Optimized performance with bundled JRE

## 🎮 Command Line Options

```bash
java -jar pokermon-1.0.0.jar [OPTIONS]

OPTIONS:
  (no arguments)     Launch GUI mode (default)
  -b, --basic        Launch console/text mode
  -h, --help         Show help and usage information
  -v, --version      Show version information
```

## 🏗️ Kotlin-Native Architecture & Design Principles

### Unified Kotlin-First Development
Pokermon leverages Kotlin-native architecture for unified cross-platform development with modern language features:

#### 🎯 Core Kotlin-Native Components
- **`GameLogicBridge.kt`** - Unified API bridging all UI platforms with coroutines support
- **`GameEngine.kt`** - Modern Kotlin implementation with null safety and collection operators  
- **`Game.kt`** - Data class configuration with default parameters and immutable design
- **`GameMode.kt`** - Rich enum class with extension functions and type safety
- **`GamePhase.kt`** - Sealed class hierarchy for type-safe game state management
- **`Player.java`** - Legacy compatibility (migration in progress)
- **`Main.java`** - Core utilities (migration planned)

#### ⚡ Kotlin-Native Benefits
- **Native Android compatibility** - No Java-Kotlin interop complexity
- **Coroutines support** - Async programming ready for responsive UIs  
- **Null safety** - Eliminates common runtime crashes
- **Data classes** - Immutable state management with copy semantics
- **Extension functions** - Enhanced functionality without inheritance
- **Smart casts** - Type-safe operations with automatic casting

#### 🔄 Kotlin-Native Cross-Platform Architecture
```
┌─────────────────┬─────────────────┬─────────────────┐
│   Android UI    │   Desktop UI    │   Console UI    │
│   (Kotlin)      │   (JavaFX)      │   (Text Mode)   │
├─────────────────┼─────────────────┼─────────────────┤
│              GameLogicBridge.kt (Unified API)      │
├─────────────────┼─────────────────┼─────────────────┤
│        Core Kotlin-Native Game Logic               │
│  • GameEngine.kt  • Game.kt  • GameMode.kt        │
│  • GamePhase.kt  • Bridge Components              │
├─────────────────┼─────────────────┼─────────────────┤
│         Legacy Java Components                     │
│  • Player.java  • Main.java  • MonsterDatabase    │
└─────────────────────────────────────────────────────┘
```

#### 🎮 Platform-Specific Optimizations
- **Android**: Native Kotlin with coroutines for responsive async operations
- **Desktop**: JavaFX interface bridged through Kotlin-native components  
- **Console**: Text-based interface leveraging Kotlin's enhanced string handling
- **All Platforms**: Unified Kotlin business logic with null safety and modern patterns

This Kotlin-native architecture provides superior maintainability, type safety, and development velocity while ensuring feature parity across all platforms.

## 🔗 Links

- **[🏗️ GitHub Actions](https://github.com/Gameaday/poker-basic/actions)** - Build status and CI/CD
- **[📦 Releases](https://github.com/Gameaday/poker-basic/releases)** - Download latest versions
- **[📊 Workflow](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)** - CI/CD pipeline details

---

**Pokermon** combines professional software development practices with an engaging gaming experience. Built with modern **Kotlin-native architecture** and comprehensive testing (254 tests) for reliability across all platforms.