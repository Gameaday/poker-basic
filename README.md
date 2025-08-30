# Poker Game - Educational Code Improvement Project

This is a Java poker game that serves as an educational example of code improvement and refactoring over time. The project demonstrates how legacy code can be systematically improved while maintaining functionality, focusing on code reusability, proper object-oriented design, and flexible architecture.

## Version 0.08.30 - Recent Major Improvements

This version represents a significant milestone in the project's evolution, showcasing how code can be professionally refactored for better maintainability and extensibility.

### Key Features

- **Complete 5-card draw poker game logic** with comprehensive hand evaluation
- **Flexible game configurations** - Support for 3-card poker, 7-card stud, and custom variants
- **Multi-player support** - Play against 1-3 computer opponents with intelligent AI
- **Dual interfaces** - Both console and graphical user interface (GUI) with themed cards
- **Comprehensive betting system** with pot management across multiple rounds
- **Card exchange mechanics** - Select and replace cards to improve your hand
- **Player statistics** persistence between games
- **Professional code architecture** with proper encapsulation and reusability

### Recent Educational Improvements (v0.08.30)

#### 1. **Proper Object-Oriented Design**
- Refactored `Player` class with private fields and public getters/setters
- Eliminated direct field access throughout the codebase
- Added comprehensive documentation to all methods
- Implemented proper encapsulation principles

#### 2. **Code Reusability & DRY Principles**
- **Eliminated hard-coded player initialization**: Replaced explicit if statements for each player (0,1,2,3) with dynamic loops
- **Consolidated duplicated betting logic**: Removed 70+ lines of duplicated AI betting code with reusable `calculateAIBet()` method
- **Flexible player management**: Game now works with any number of players (1-4) without code changes

#### 3. **Flexible Game Architecture**
- **Variable hand sizes**: Support for 1-52 card hands (3-card poker, traditional 5-card, 7-card stud)
- **Configurable game rules**: Customizable starting chips, betting rounds, and player limits
- **Game variants**: Built-in presets for common poker variations
- **Centralized game management**: New `GameEngine` class manages game flow and state

#### 4. **Professional Testing Suite**
- **40 comprehensive tests** validating all functionality
- **Unit tests** for encapsulation and individual components
- **Integration tests** for game logic and reusability
- **Flexibility tests** for variable configurations
- **All tests pass** ensuring refactoring maintains functionality

## Quick Start

### Using the JAR (Recommended)
```bash
# Download and run the latest JAR from GitHub releases
java -jar pokermon-0.08.30.jar          # Start GUI mode (default)
java -jar pokermon-0.08.30.jar --basic  # Start console mode
java -jar pokermon-0.08.30.jar --help   # Show all options
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

This project uses Maven for building and dependency management:

```bash
cd Poker-Basic
mvn clean compile    # Compile the project
mvn test            # Run all tests
mvn clean package   # Create distributable JAR
```

### Command Line Options

The JAR supports the following command-line options:

```bash
java -jar pokermon-0.08.30.jar [OPTIONS]

OPTIONS:
  (no arguments)     Launch GUI mode (default, recommended)
  -b, --basic        Launch console/text mode
      --console      Same as --basic
  -h, --help         Show help message and usage information
  -v, --version      Show version information

EXAMPLES:
  java -jar pokermon-0.08.30.jar
    Start the game in GUI mode (default)

  java -jar pokermon-0.08.30.jar --basic
    Start the game in console mode

  java -jar pokermon-0.08.30.jar --help
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

### Monster-Enhanced Game Modes ✨

#### Adventure Mode - Monster Battles
```java
Game adventure = Game.createAdventureMode(); // Battle monsters whose health equals their chips
```
Fight through encounters where monster health determines their chip count. Defeat monsters to earn rewards and progress through increasingly challenging opponents.

#### Safari Mode - Monster Capturing  
```java
Game safari = Game.createSafariMode(); // Capture monsters through strategic poker gameplay
```
Encounter wild monsters during poker games. Your poker performance affects capture probability, with better hands increasing your chances of adding monsters to your collection.

#### Ironman Mode - Gacha Rewards
```java
Game ironman = Game.createIronmanMode(); // Convert winnings into monster gacha pulls
```
Play poker to accumulate chips, then cash out to perform gacha-style monster pulls. Higher chip counts increase chances of rare monster rewards.

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

This project continues to evolve as an educational tool with an exciting vision for monster-enhanced gameplay:

### Version 1.0 - Monster Integration
- **Monster System Foundation** ✅: Core monster classes, game modes, and collection management
- **Adventure Mode**: Battle monsters whose health equals their chips - defeat them to earn rewards
- **Safari Mode**: Capture monsters through strategic poker gameplay
- **Ironman Mode**: Convert poker winnings into gacha-style monster pulls with rarity-based rewards
- **Monster Effects**: Monsters provide gameplay advantages (chip bonuses, card advantages, betting boosts)

### Platform Evolution
- **Cross-Platform UI**: Replace Swing with JavaFX or web-based framework for Windows, Linux, and Android support
- **Responsive Design**: Adaptive layouts for mouse and touchscreen controls
- **Theme System**: Visual customization based on collected monsters

### Advanced Features
- **Monster Shop & Trading**: Buy, sell, and trade monsters with NPCs or other players
- **Achievement System**: Unlock new monsters and game modes through gameplay milestones
- **Save/Load System**: Persistent monster collections and player progress
- **Monster Breeding**: Combine monsters to create new varieties with enhanced effects

### Technical Goals
- **Version 1.0 Target**: Complete professional-grade codebase
- **CI/CD Integration**: Automated testing and deployment
- **Dependency Management**: Automated updates and security scanning
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

### Phase 1: Monster System Foundation ✅ (Current)
- [x] Core monster classes (Monster, MonsterCollection, GameMode)
- [x] Game mode enumeration and configuration
- [x] Foundation tests (27 new tests added)
- [x] Documentation updates and future vision

### Phase 2: Adventure Mode Implementation
- [ ] Enemy monster generation and battle mechanics
- [ ] Health-to-chips conversion system
- [ ] Reward distribution after defeating monsters
- [ ] Progressive difficulty scaling
- [ ] Adventure mode UI integration

### Phase 3: Safari Mode Implementation  
- [ ] Monster encounter system during poker games
- [ ] Capture probability mechanics based on performance
- [ ] Safari-specific UI elements
- [ ] Monster rarity distribution in wild encounters

### Phase 4: Ironman Mode Implementation
- [ ] Gacha system with chip-to-currency conversion
- [ ] Rarity-weighted monster pull mechanics
- [ ] Ironman leaderboards and high score tracking
- [ ] Special Ironman-only monsters and rewards

### Phase 5: Platform Migration
- [ ] Replace Swing with JavaFX for better cross-platform support
- [ ] Responsive UI design for different screen sizes
- [ ] Touch-friendly controls for mobile platforms
- [ ] Platform-specific deployment configurations

### Phase 6: Enhanced Features
- [ ] Monster shop and trading system
- [ ] Save/load functionality for monster collections
- [ ] Achievement and progression systems
- [ ] Monster breeding and evolution mechanics
- [ ] Multiplayer monster battles

### Phase 7: Advanced Integration
- [ ] AI opponent monster integration
- [ ] Online multiplayer with monster sharing
- [ ] Tournament modes with monster restrictions
- [ ] Community features and monster showcases

Each phase will maintain the project's educational value while adding engaging gameplay elements that demonstrate advanced software architecture patterns.

## Development Setup

### GitHub Copilot Integration
This project includes optimized GitHub Copilot setup for enhanced development experience:

```bash
# Quick setup with Copilot optimization
git clone https://github.com/Gameaday/poker-basic.git
cd poker-basic
code poker-basic.code-workspace  # Opens with optimized settings
```

See **[COPILOT_SETUP.md](COPILOT_SETUP.md)** for comprehensive setup instructions and tips for maximizing Copilot performance with this codebase.

### Documentation Resources
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Development guidelines and best practices
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed technical architecture documentation
- **[COPILOT_SETUP.md](COPILOT_SETUP.md)** - GitHub Copilot optimization guide

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