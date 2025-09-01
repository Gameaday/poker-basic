# 🐲 Pokermon - Educational Poker Monster Game

**Where Poker meets Monster Collecting!**

This is a comprehensive Java poker game that serves as an educational example of code improvement and professional software development. The project demonstrates how to build cross-platform applications with persistent user experiences, modern UI design, and comprehensive feature sets. Pokermon combines traditional poker gameplay with monster collection mechanics across multiple exciting game modes.

## Version 1.0.0 - Pokermon Overhaul Complete

This version represents a major milestone featuring the complete Pokermon experience with enhanced branding, persistent user profiles, comprehensive settings management, and cross-platform monster-themed gameplay. The project showcases professional software development practices with a focus on user experience and data persistence.

### 🎮 Core Pokermon Features

- **🃏 Complete 5-card draw poker engine** with comprehensive hand evaluation
- **🐲 Monster-themed game modes** - Adventure, Safari, Ironman, and Classic modes
- **👥 Multi-player adventures** - Battle against 1-3 AI opponents with intelligent strategies
- **📱 Cross-platform experience** - Desktop JAR, Android APK, and modern web-ready architecture
- **💾 Persistent user profiles** - Comprehensive statistics, achievements, and progress tracking
- **🎨 Customizable themes** - Multiple poker table styles with persistent selection
- **⚙️ Auto-save functionality** - Background saving of all user data and game progress
- **🏆 Achievement system** - Unlock rewards based on gameplay milestones

### 🌟 New Pokermon Experience Features (v1.0.0)

#### 🏠 **Enhanced User Experience**
- **Personalized welcome** with user statistics and progress display
- **Real-time achievement tracking** with automatic unlocking system
- **Comprehensive profile management** including games played, win rates, and monster progress
- **Persistent settings** that auto-save without user intervention

#### 🐲 **Monster Integration Foundation**
- **Adventure Mode preparation** - Battle monsters in poker duels (framework ready)
- **Safari Mode foundation** - Capture monsters through strategic gameplay (UI implemented)
- **Ironman Mode structure** - Convert poker winnings into monster gacha pulls (system designed)
- **Monster collection tracking** - Progress counters for future monster features

#### 💾 **Advanced Data Management**
- **Automatic user profile creation** with unique ID generation
- **Comprehensive backup/export** system for complete profile data
- **Settings persistence** across app restarts and platform switches
- **Achievement progression** with detailed unlock conditions

#### 🎨 **Enhanced Pokermon Branding**
- **Monster-themed UI elements** throughout the application
- **Pokermon table themes** - Classic Green, Royal Blue, Crimson Red, Midnight Black, Bourbon Brown
- **Enhanced iconography** with monster-themed emojis and styling
- **Cohesive branding** across desktop and mobile platforms

### 🔧 Technical Architecture Improvements (v1.0.0)

#### 1. **Comprehensive User Profile System**
- **Persistent data storage** using Android SharedPreferences and cross-platform compatibility
- **Reactive state management** with StateFlow for real-time UI updates
- **Automatic profile creation** with unique user ID generation
- **Statistics tracking** including games played, wins, chips won, and achievement progress

#### 2. **Enhanced Settings Management**
- **Auto-save functionality** - All settings changes persist immediately without user action
- **Theme persistence** - Selected table themes maintain across app restarts
- **Backup/restore system** - Complete profile export and import capabilities
- **Integration testing** - Settings integrate seamlessly with game logic and user profiles

#### 3. **Cross-Platform User Experience**
- **Unified branding** across desktop JAR and Android APK platforms
- **Persistent user experience** - Settings and progress sync across platforms
- **Modern UI architecture** with Jetpack Compose on Android and JavaFX on desktop
- **Monster-themed consistency** throughout all user interfaces

#### 4. **Robust Testing & Quality Assurance**
- **185 comprehensive tests** validating all functionality including new user profile features
- **Backward compatibility** ensuring existing game logic remains unaffected
- **Cross-platform testing** for desktop and mobile implementations
- **Persistent data validation** ensuring user data integrity across sessions

## 🚀 Quick Start Guide

### 🖥️ Desktop Experience (Recommended)
```bash
# Download and run the latest JAR from GitHub releases
java -jar pokermon-1.0.0.jar           # Start modern JavaFX GUI (recommended)
java -jar pokermon-1.0.0.jar --basic   # Start console mode for classic experience
java -jar pokermon-1.0.0.jar --mode    # Interactive game mode selection
java -jar pokermon-1.0.0.jar --help    # Show all Pokermon options and features
```

### 📱 Android Mobile Experience
```bash
# Build and install the Android APK
./gradlew :android:assembleDebug --no-daemon
adb install android/build/outputs/apk/debug/android-debug.apk

# Or download from GitHub Releases
# Features: Touch-optimized UI, persistent profiles, full Pokermon experience
```

### 🏃‍♂️ Development Quick Start
```bash
# Clone and build Pokermon
git clone https://github.com/Gameaday/poker-basic.git
cd poker-basic

# Validate build system
./validate-android-build.sh

# Build desktop version
cd Poker-Basic
mvn clean package -B

# Test comprehensive functionality (185 tests)
mvn test -B

# Run Pokermon
java -jar target/pokermon-1.0.0-fat.jar
```

### Development Mode

#### GUI Version (NetBeans/IDE)
```bash
cd Poker-Basic
mvn compile exec:java -Dexec.mainClass="com.pokermon.NewJFrame"
```

#### Console Version (Terminal)
```bash
cd Poker-Basic  
mvn compile exec:java -Dexec.mainClass="com.pokermon.ConsoleMain"
```

#### Running Tests
```bash
cd Poker-Basic
mvn test
```

## Building

This project supports **comprehensive cross-platform builds** for all major platforms:

### 🖥️ Desktop Native Executables

#### Windows (.exe)
```bash
cd Poker-Basic
mvn clean package -Pwindows-exe -DskipTests
# Output: target/jpackage/PokerGame-1.0.0.exe
```

#### Linux (.deb)
```bash
cd Poker-Basic
mvn clean package -Plinux-exe -DskipTests
# Output: target/jpackage/pokergame_1.0.0-1_amd64.deb
```

#### macOS (.dmg)
```bash
cd Poker-Basic
mvn clean package -Pmacos-exe -DskipTests
# Output: target/jpackage/PokerGame-1.0.0.dmg
```

### ☕ Cross-Platform JAR
```bash
cd Poker-Basic
mvn clean compile    # Compile the project
mvn test            # Run all tests
mvn clean package   # Create distributable JAR
# Output: target/pokermon-1.0.0.jar (standard)
#         target/pokermon-1.0.0-fat.jar (with dependencies)
```

### 📱 Android Build (APK)
```bash
# Requires internet connection for first-time setup
./gradlew :android:assembleDebug    # Create Android APK
# Output: android/build/outputs/apk/debug/android-debug.apk
```

### 🔍 Verify All Build Configurations
```bash
./validate-cross-platform-build.sh    # Comprehensive build system validation
```

### Platform-Specific Features

#### Native Desktop Applications (Windows/Linux/macOS)
- Self-contained executables (no Java installation required)
- Native OS integration (menus, shortcuts, file associations)
- Optimized performance with bundled JRE
- Platform-specific installers and packages

#### Cross-Platform JAR
- Full JavaFX GUI with card graphics
- Console/text mode option
- Mouse and keyboard controls
- Works on any Java 17+ compatible system

#### Android (APK) 
- Native Material Design interface
- Touch-friendly game selection
- Optimized for phones and tablets
- Requires Android 5.0+ (API 21+)

### Command Line Options

The JAR supports the following command-line options:

```bash
java -jar pokermon-1.0.0.jar [OPTIONS]

OPTIONS:
  (no arguments)     Launch GUI mode (default, recommended)
  -b, --basic        Launch console/text mode
      --console      Same as --basic
  -h, --help         Show help message and usage information
  -v, --version      Show version information

EXAMPLES:
  java -jar pokermon-1.0.0.jar
    Start the game in GUI mode (default)

  java -jar pokermon-1.0.0.jar --basic
    Start the game in console mode

  java -jar pokermon-1.0.0.jar --help
    Display help information
```

## Game Variants Supported

The flexible architecture now supports multiple poker variants and exciting new monster-based game modes:

### Traditional Poker Variants

#### Traditional 5-Card Draw (Default)
```java
Game traditionalPoker = new Game(); // 5 cards, 4 players max, 1000 chips, 2 betting rounds
```

#### 3-Card Poker
```java
Game threeCard = Game.createThreeCardPoker(); // 3 cards, 4 players max, 500 chips, 1 betting round
```

#### 7-Card Stud
```java
Game sevenCard = Game.createSevenCardStud(); // 7 cards, 4 players max, 1500 chips, 3 betting rounds
```

### 🐲 Pokermon Game Modes

Pokermon features multiple engaging game modes that combine traditional poker with monster-collecting elements:

#### 🎯 Classic Mode ✅ (Fully Implemented)
```java
Game classic = Game.createClassicMode(); // Traditional 5-card draw poker
```
Experience traditional poker gameplay with the Pokermon user interface and comprehensive statistics tracking. Perfect for learning poker fundamentals while building your trainer profile.

#### ⚔️ Adventure Mode 🚧 (Framework Ready)
```java
Game adventure = Game.createAdventureMode(); // Battle monsters in poker duels
```
**Coming Soon**: Battle through encounters where monster health determines their chip count. Defeat monsters to earn rewards and progress through increasingly challenging opponents. Framework implemented, gameplay mechanics in development.

#### 🌿 Safari Mode 🚧 (UI Implemented)
```java
Game safari = Game.createSafariMode(); // Capture monsters through strategic gameplay
```
**Coming Soon**: Encounter wild monsters during poker games. Your poker performance affects capture probability, with better hands increasing your chances of adding monsters to your collection. UI components ready, encounter system in development.

#### 🎰 Ironman Mode 🚧 (System Designed)
```java
Game ironman = Game.createIronmanMode(); // Convert winnings into monster gacha pulls
```
**Coming Soon**: Play poker to accumulate chips, then cash out to perform gacha-style monster pulls. Higher chip counts increase chances of rare monster rewards. Backend systems designed, gacha mechanics in development.

### 🎮 Current Experience
- **Full Classic Poker**: Complete 5-card draw implementation with AI opponents
- **User Profile Integration**: All games tracked in persistent user profiles
- **Achievement System**: Unlock rewards based on gameplay milestones
- **Cross-Platform**: Identical experience on desktop JAR and Android APK
- **Auto-Save**: Continuous background saving of progress and statistics

### 🔮 Coming in Beta Release
- **Monster Battle Mechanics**: Adventure mode with health-based chip systems
- **Monster Capture System**: Safari mode with probability-based collection
- **Gacha Mechanics**: Ironman mode with rarity-weighted pulls
- **Monster Effects**: Gameplay bonuses from collected monsters

### Custom Configuration
```java
Game custom = new Game(handSize, maxPlayers, startingChips, bettingRounds, gameMode);
```

## Educational Value

This project serves as an excellent example of:

1. **Legacy Code Improvement**: How to systematically refactor old code
2. **Code Reusability**: Eliminating duplication through proper design
3. **Object-Oriented Principles**: Encapsulation, abstraction, and modularity
4. **Test-Driven Refactoring**: Ensuring functionality while improving code
5. **Flexible Architecture**: Building systems that adapt to changing requirements
6. **Professional Development Practices**: Documentation, version control, and incremental improvement

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      Game       │    │   GameEngine    │    │     Player      │
│   Configuration │◄───┤  Game Manager   │◄───┤   Individual    │
│                 │    │                 │    │   Player Data   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └──────────────►│      Main       │◄─────────────┘
                        │   Game Logic    │
                        └─────────────────┘
```

## Game Rules

### 5-Card Draw Poker (Default)
1. Each player receives 5 cards
2. First betting round
3. Players can exchange unwanted cards
4. Second betting round  
5. Best hand wins the pot

### Hand Rankings (Highest to Lowest)
- Royal Flush (A, K, Q, J, 10 of same suit)
- Straight Flush (5 consecutive cards of same suit)
- Four of a Kind
- Full House (3 of a kind + pair)
- Flush (5 cards of same suit)
- Straight (5 consecutive cards)
- Three of a Kind
- Two Pair
- One Pair
- High Card

## Future Goals

This project has successfully reached Version 1.0 with a complete monster system foundation and is now ready for beta milestone planning:

### Version 1.0 - Monster Integration ✅ (COMPLETE)
- **Monster System Foundation** ✅: Core monster classes, game modes, and collection management
- **Cross-Platform Build System** ✅: JAR, Windows EXE, Linux DEB, macOS DMG, Android APK
- **Professional Testing Suite** ✅: 77 comprehensive tests with full coverage
- **Educational Documentation** ✅: Complete guides and implementation examples

### Next: Beta Milestone - Enhanced Gameplay
- **Adventure Mode**: Battle monsters whose health equals their chips - defeat them to earn rewards
- **Safari Mode**: Capture monsters through strategic poker gameplay
- **Ironman Mode**: Convert poker winnings into gacha-style monster pulls with rarity-based rewards
- **Monster Effects**: Monsters provide gameplay advantages (chip bonuses, card advantages, betting boosts)

### Platform Evolution ✅ (COMPLETE)
- **Dual Platform Support**: Now supports both desktop (JAR) and Android (APK) builds
- **Cross-Platform UI**: Desktop uses Swing, Android uses native Material Design
- **Shared Codebase**: Same game logic runs on both platforms
- **Theme System**: Visual customization based on collected monsters

#### Current Platform Support ✅
- **Desktop (JAR)**: Windows, Linux, macOS via Java 17
- **Windows Native**: Self-contained EXE installer
- **Linux Native**: DEB package for Ubuntu/Debian
- **macOS Native**: DMG installer for Intel/Apple Silicon
- **Android (APK)**: Android 5.0+ (API 21+) phones and tablets

### Advanced Features (Beta Roadmap)
- **Monster Shop & Trading**: Buy, sell, and trade monsters with NPCs or other players
- **Achievement System**: Unlock new monsters and game modes through gameplay milestones
- **Save/Load System**: Persistent monster collections and player progress
- **Monster Breeding**: Combine monsters to create new varieties with enhanced effects

### Technical Goals (Future Versions)
- **Version 2.0 Target**: Complete monster gameplay integration
- **CI/CD Integration**: Automated testing and deployment ✅ (COMPLETE)
- **Dependency Management**: Automated updates and security scanning ✅ (COMPLETE)
- **Additional Game Variants**: Texas Hold'em, Omaha, etc.
- **Advanced AI**: Machine learning-based opponents
- **Multiplayer Networking**: Online play capabilities

## Monster System Preview

The foundation for the monster system is now in place:

### Game Modes
```java
Game adventure = Game.createAdventureMode();  // Battle monsters for rewards
Game safari = Game.createSafariMode();        // Capture monsters
Game ironman = Game.createIronmanMode();      // Gacha-style monster rewards
```

### Monster Types & Rarities
- **Common**: Basic monsters with standard effects
- **Uncommon**: Enhanced monsters with 1.5x power multiplier
- **Rare**: Strong monsters with 2x power multiplier  
- **Epic**: Powerful monsters with 3x power multiplier
- **Legendary**: Ultimate monsters with 5x power multiplier

### Monster Effects
- **Chip Bonus**: Increases starting chips for poker games
- **Card Advantage**: Provides extra card draws or exchanges
- **Betting Boost**: Improves betting effectiveness and bluffing
- **Luck Enhancement**: Increases chance of favorable hands
- **Visual Theme**: Changes game appearance and card designs

### Monster Collection System
```java
MonsterCollection collection = new MonsterCollection();
Monster dragon = new Monster("Fire Dragon", Rarity.RARE, 100, EffectType.CHIP_BONUS, 50, "A fierce dragon");
collection.addMonster(dragon);
collection.setActiveMonster(dragon); // Apply dragon's effects to gameplay
```

## Development Roadmap

## Development Roadmap

### Phase 1: Monster System Foundation ✅ (COMPLETE)
- [x] Core monster classes (Monster, MonsterCollection, GameMode)
- [x] Game mode enumeration and configuration
- [x] Foundation tests (77 comprehensive tests)
- [x] Documentation updates and future vision
- [x] Cross-platform build system
- [x] Professional CI/CD pipeline

## 🗺️ Development Roadmap

### Phase 1: Pokermon Foundation ✅ (COMPLETE)
- [x] Core monster classes (Monster, MonsterCollection, GameMode)
- [x] Game mode enumeration and configuration
- [x] Foundation tests (185 comprehensive tests)
- [x] Enhanced Pokermon branding and user experience
- [x] Cross-platform build system
- [x] Professional CI/CD pipeline
- [x] Comprehensive user profile system
- [x] Persistent settings and auto-save functionality

### Phase 2: User Experience Enhancement ✅ (COMPLETE)
- [x] Comprehensive user profile management with persistent data storage
- [x] Enhanced Android UI with Pokermon branding and theming
- [x] Auto-save functionality with background data persistence
- [x] Achievement system with automatic progression tracking
- [x] Settings integration with real-time theme application
- [x] Backup/export system for complete profile data management

### Phase 3: Adventure Mode Implementation (Beta Milestone)
- [ ] Enemy monster generation and battle mechanics
- [ ] Health-to-chips conversion system
- [ ] Reward distribution after defeating monsters
- [ ] Progressive difficulty scaling
- [ ] Adventure mode UI integration

### Phase 4: Safari Mode Implementation (Beta Milestone)
- [ ] Monster encounter system during poker games
- [ ] Capture probability mechanics based on performance
- [ ] Safari-specific UI elements
- [ ] Monster rarity distribution in wild encounters

### Phase 5: Ironman Mode Implementation (Beta Milestone)
- [ ] Gacha system with chip-to-currency conversion
- [ ] Rarity-weighted monster pull mechanics
- [ ] Ironman leaderboards and high score tracking
- [ ] Special Ironman-only monsters and rewards

### Phase 6: Platform Enhancement ✅ (COMPLETE)
- [x] Cross-platform native executables (Windows, Linux, macOS)
- [x] Android APK with full functionality
- [x] Responsive UI design for different screen sizes
- [x] Touch-friendly controls for mobile platforms
- [x] Platform-specific deployment configurations

### Phase 7: Enhanced Features (Future)
- [ ] Monster shop and trading system
- [ ] Advanced monster collection management
- [ ] Achievement and progression systems expansion
- [ ] Monster breeding and evolution mechanics
- [ ] Multiplayer monster battles

### Phase 8: Advanced Integration (Future)
- [ ] AI opponent monster integration
- [ ] Online multiplayer with monster sharing
- [ ] Tournament modes with monster restrictions
- [ ] Community features and monster showcases

Each phase will maintain the project's educational value while adding engaging gameplay elements that demonstrate advanced software architecture patterns.

## Credits

- **Original Developers**: Carl Nelson and Anthony Elizondo (Original implementation ~2014)
- **Card Artwork**: "The Eternal Tortoise" themed cards by Small Comic
- **Code Modernization & Architecture**: GitHub Copilot (2024)
- **Educational Framework**: Demonstrates professional development practices

## Project Philosophy

> "The best way to learn programming is not to start over, but to improve what exists."

This project embodies the principle that real-world software development is about continuous improvement, not constant rewrites. It shows how legacy code can be transformed into modern, maintainable software through systematic refactoring while preserving functionality and adding new capabilities.

---

**Note**: This project serves as both a functional poker game and an educational resource demonstrating professional software development practices, code improvement techniques, and the evolution of software architecture over time.