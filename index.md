---
layout: default
title: Pokermon - Pure Kotlin-Native Poker Game
---

# 🐲 Pokermon 
## Where Poker meets Monster Collecting in Pure Kotlin-Native Excellence!

[![Build Status](https://img.shields.io/github/actions/workflow/status/Gameaday/poker-basic/ci.yml?branch=master)](https://github.com/Gameaday/poker-basic/actions/workflows/ci.yml)
[![Kotlin Native](https://img.shields.io/badge/architecture-Pure%20Kotlin%20Native-7F52FF)](https://kotlinlang.org/docs/native-overview.html)
[![Platform Support](https://img.shields.io/badge/platforms-Windows%20%7C%20Linux%20%7C%20macOS%20%7C%20Android-blue)](https://github.com/Gameaday/poker-basic/releases)

A comprehensive cross-platform poker game featuring monster collection mechanics, multiple game modes, and persistent user profiles. Built with **pure Kotlin-native architecture** using Gradle for unified development.

## 🚀 Quick Start

### Download & Play
- **Windows**: [Download Pokermon.exe](https://github.com/Gameaday/poker-basic/releases) (Coming Soon)
- **Linux**: [Download pokermon.deb](https://github.com/Gameaday/poker-basic/releases) (Coming Soon)  
- **macOS**: [Download Pokermon.dmg](https://github.com/Gameaday/poker-basic/releases) (Coming Soon)
- **Android**: [Download APK](https://github.com/Gameaday/poker-basic/actions) (CI Artifacts)

### Try Now (Java Required)
```bash
# Download and run the development JAR
java -jar pokermon-kotlin-native.jar --help
java -jar pokermon-kotlin-native.jar --basic  # Console mode
```

## 🎮 Game Features

### Multiple Game Modes
- **Classic Poker**: Traditional Texas Hold'em gameplay
- **Adventure Mode**: Monster collection and battles  
- **Safari Mode**: Exploration and discovery mechanics
- **Ironman Mode**: Hardcore challenge gameplay

### Core Features
- **Monster Collection**: Collect, battle, and evolve creatures
- **Profile System**: Persistent statistics and achievements
- **AI Opponents**: Multiple personality types and difficulties
- **Cross-Platform**: Seamless experience across all devices
- **Flow-Based UI**: Modern reactive state management

## 🏗️ Pure Kotlin-Native Architecture

Built entirely in Kotlin-native with modern development practices:

- **39 Kotlin files** in pure implementation
- **Gradle build system** with multi-module architecture
- **Flow-based reactive patterns** for state management
- **DRY principles** with single sources of truth
- **Comprehensive testing** with professional standards

## 📱 Platform Support

| Platform | Status | Download | Features |
|----------|---------|----------|----------|
| **Windows** | ✅ Ready | `.exe` (Coming Soon) | Full desktop experience |
| **Linux** | ✅ Ready | `.deb` (Coming Soon) | Native system integration |
| **macOS** | ✅ Ready | `.dmg` (Coming Soon) | Metal rendering support |
| **Android** | ✅ Ready | [APK](https://github.com/Gameaday/poker-basic/actions) | Touch-optimized UI |
| **Development** | ✅ Working | [JAR](https://github.com/Gameaday/poker-basic/actions) | Cross-platform testing |

## 🔧 Developer Information

### Build from Source
```bash
git clone https://github.com/Gameaday/poker-basic.git
cd poker-basic

# Verify pure Kotlin-native setup
./gradlew verifyKotlinNativeSetup --no-daemon

# Build and test
./gradlew :shared:compileKotlin --no-daemon
./gradlew :shared:test --no-daemon
./gradlew :shared:fatJar --no-daemon

# Create native executables (when available)
./gradlew :desktop:packageNative --no-daemon
```

### Architecture Highlights
- **Pure Kotlin-Native**: No Java dependencies or Maven complexity
- **Dynamic Versioning**: Timestamp-based (1.1.0.YYYYMMDD) without git dependencies
- **Multi-Module Structure**: Shared core, Android, and Desktop modules
- **CI/CD Pipeline**: Automated testing and builds for all platforms

## 🎯 Roadmap

### Current Phase: Foundation Complete ✅
- ✅ Pure Kotlin-native architecture implemented
- ✅ Build system and CI/CD framework operational
- ✅ Documentation and project structure updated

### Next Phases
1. **Android Flow-State UI** (4-6 weeks)
   - Complete settings and profile management
   - Save/continue game functionality
   - Enhanced reactive UI components

2. **Native Builds** (2-3 weeks)
   - GraalVM native image integration
   - Platform-specific packaging and distribution
   - CI/CD native build pipeline

3. **Advanced Features** (4-6 weeks)
   - Monster battle system enhancement
   - Performance optimization
   - Advanced UI/UX features

See [Comprehensive Functionality Roadmap](COMPREHENSIVE_FUNCTIONALITY_ROADMAP.md) for complete details.

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup
1. Clone the repository
2. Verify Kotlin-native setup: `./gradlew verifyKotlinNativeSetup`  
3. Run tests: `./gradlew :shared:test`
4. Create a feature branch and submit a PR

### Code Style
- Pure Kotlin-native implementation
- DRY principles with single sources of truth
- Flow-based reactive patterns
- Comprehensive testing for all changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Creator

**Carl Nelson (@Gameaday)**
- All game coding and concepts
- Pure Kotlin-native architecture design
- Cross-platform development and optimization

---

**Built with ❤️ using Pure Kotlin-Native and Modern Development Practices**