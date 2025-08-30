# Project Architecture Documentation

## Overview
This document provides detailed architectural information to help developers (and AI assistants like GitHub Copilot) understand the codebase structure, design patterns, and implementation details.

## Core Architecture

### Design Philosophy
The project follows these key principles:
- **Encapsulation**: Private fields with public accessors
- **Flexibility**: Support for variable game configurations
- **Reusability**: Eliminate code duplication through common methods
- **Testability**: Comprehensive test coverage with clear interfaces

### Class Hierarchy

```
Game Logic Hierarchy:
├── Main/ConsoleMain (Entry Points)
├── NewJFrame (GUI Entry Point)
├── GameEngine (Central Game Manager)
│   ├── Game (Configuration)
│   ├── Player[] (Player Management)
│   └── Monster (AI Logic)
└── Utility Classes (Card handling, etc.)
```

## Core Classes Detail

### 1. Player Class (`Player.java`)
**Purpose**: Represents a poker player with encapsulated state

**Key Methods**:
```java
// Setup and initialization
public void setupPlayer(String name, int chips, int[] deck)

// State management  
public void setName(String name)
public void setChips(int chips)
public void setFold(boolean fold)

// Game mechanics
public void performAllChecks()  // Evaluates hand strength
public int getHandValue()       // Returns calculated hand value
```

**Design Patterns**:
- **Encapsulation**: All fields private, public getters/setters only
- **State Management**: Tracks chips, cards, betting status
- **Immutable Setup**: `setupPlayer()` initializes all necessary state

### 2. GameEngine Class (`GameEngine.java`)
**Purpose**: Central coordinator for game flow and state management

**Key Responsibilities**:
- Player initialization and management
- Game state tracking (rounds, pot, active status)
- Game flow coordination
- Multi-player support (1-4 players)

**Common Usage Pattern**:
```java
Game config = new Game();  // or Game.createThreeCardPoker()
GameEngine engine = new GameEngine(config);
engine.initializeGame(playerNames);
// Game runs...
engine.endGame();
```

### 3. Game Class (`Game.java`)
**Purpose**: Game configuration and rules management

**Factory Methods**:
```java
public static Game createThreeCardPoker()  // 3-card variant
public static Game createTexasHoldem()     // Future variant
```

**Configuration Properties**:
- Hand size (3, 5, 7+ cards supported)
- Starting chip amounts
- Player limits
- Betting rules

### 4. Monster Class (`Monster.java`)
**Purpose**: AI player logic and decision making

**Key Features**:
- Hand strength evaluation
- Betting amount calculation
- Bluffing and strategy logic
- Difficulty level management

## Data Flow Patterns

### 1. Player Initialization Flow
```java
// Standard pattern used throughout codebase
Player player = new Player();
player.setupPlayer(name, startingChips, sharedDeck);
player.performAllChecks();  // Calculate initial hand value
```

### 2. Game Loop Pattern
```java
GameEngine engine = new GameEngine(gameConfig);
engine.initializeGame(playerNames);

while (engine.canContinue()) {
    // Betting round
    int pot = Main.bet(players, currentPot);
    
    // Card exchange (if applicable)
    // Hand evaluation
    // Winner determination
    
    engine.nextRound();
}
```

### 3. Flexible Player Management
```java
// Supports 1-4 players dynamically
for (int i = 0; i < playerCount; i++) {
    players[i] = new Player();
    players[i].setupPlayer(names[i], chips, deck);
}
```

## Testing Architecture

### Test Categories

#### 1. Unit Tests
- **PlayerTest**: Individual player functionality
- **GameTest**: Game configuration and setup
- **MonsterTest**: AI logic testing

#### 2. Integration Tests  
- **GameEngineTest**: Multi-component interaction
- **GameLogicTest**: End-to-end game flow

#### 3. Flexibility Tests
- **FlexibleHandTest**: Variable hand sizes
- **Different player counts and configurations

### Test Patterns
```java
@BeforeEach
void setUp() {
    // Initialize test objects
    player = new Player();
    testDeck = Main.setDeck();
}

@Test
void testMethodName() {
    // Arrange
    // Act  
    // Assert with descriptive messages
    assertEquals(expected, actual, "Clear error message");
}
```

## File Organization

### Source Structure
```
src/main/java/com/pokermon/
├── Main.java              # Console game entry point
├── ConsoleMain.java       # Alternative console interface  
├── NewJFrame.java         # GUI entry point (Swing)
├── Player.java            # Core player logic
├── GameEngine.java        # Game state management
├── Game.java              # Configuration and variants
├── Monster.java           # AI player implementation
└── [Utility classes]      # Supporting functionality
```

### Test Structure
```
src/test/java/com/pokermon/
├── PlayerTest.java        # Player class unit tests
├── GameEngineTest.java    # Game engine integration tests
├── GameLogicTest.java     # Game flow integration tests
├── FlexibleHandTest.java  # Variable configuration tests
├── MonsterTest.java       # AI logic tests
└── [Additional tests]     # Feature-specific tests
```

## Common Development Patterns

### 1. Error Handling
```java
// Graceful degradation pattern
try {
    player.performAllChecks();
} catch (Exception e) {
    // Log error, set safe defaults
    player.setHandValue(0);
}
```

### 2. Configuration Pattern
```java
// Flexible configuration through Game class
Game customGame = new Game();
customGame.setHandSize(3);           // 3-card poker
customGame.setStartingChips(500);    // Custom starting amount
customGame.setMaxPlayers(4);         // Up to 4 players
```

### 3. State Validation
```java
// Validate before operations
if (player.getChips() >= betAmount && !player.isFold()) {
    // Process bet
}
```

## Extension Points

### Adding New Game Variants
1. Create factory method in `Game` class
2. Configure hand size, rules, starting conditions
3. Add tests in `GameTest` or create new test file
4. Update documentation

### Adding New AI Strategies
1. Extend `Monster` class or create new AI class
2. Implement decision-making logic
3. Add difficulty levels or strategies
4. Test with `MonsterTest` patterns

### UI Enhancements
- **GUI**: Modify `NewJFrame.java` (Swing-based)
- **Console**: Update `Main.java` or `ConsoleMain.java`
- Both interfaces use same underlying game logic

## Performance Considerations

### Memory Management
- Deck arrays are shared between players when possible
- Player objects reused between rounds
- Game state cleared appropriately

### Scalability
- Dynamic player arrays (not hard-coded)
- Configurable game parameters
- Efficient hand evaluation algorithms

## Dependencies and Build

### Key Dependencies
- **JUnit 5**: Testing framework
- **Java 17+**: Target runtime
- **Maven**: Build and dependency management

### Build Artifacts
- **JAR file**: Self-contained executable
- **Test reports**: Comprehensive coverage reporting
- **GitHub releases**: Automated artifact distribution

## Code Quality Standards

### Naming Conventions
- **Classes**: PascalCase (`GameEngine`, `Player`)
- **Methods**: camelCase (`setupPlayer`, `getHandValue`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_PLAYERS`)
- **Variables**: camelCase (`playerCount`, `handValue`)

### Documentation Standards
- **JavaDoc**: All public methods documented
- **Inline comments**: Complex logic explained
- **README files**: Architecture and usage documented
- **Test descriptions**: Clear test intent and expected outcomes

This architecture documentation provides comprehensive context for understanding and extending the poker game codebase.