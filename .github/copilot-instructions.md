# GitHub Copilot Instructions for Poker-Basic Project

## Project Overview
This is an educational Java poker game project (version 0.08.30) that demonstrates professional code improvement and refactoring practices. The project serves as a learning example for transforming legacy code into well-structured, maintainable software.

## Architecture & Structure

### Core Components
- **Main Classes**: `Player`, `GameEngine`, `Game`, `Monster` (player AI)
- **Entry Points**: 
  - GUI: `com.pokermon.NewJFrame` 
  - Console: `com.pokermon.Main` and `com.pokermon.ConsoleMain`
- **Project Location**: All Java source is in `Poker-Basic/src/main/java/com/pokermon/`
- **Tests**: Located in `Poker-Basic/src/test/java/com/pokermon/`

### Key Design Patterns
- **Encapsulation**: All Player fields are private with public getters/setters
- **Factory Pattern**: `Game.createThreeCardPoker()` for game variants
- **Centralized Management**: `GameEngine` handles game flow and state
- **Flexible Configuration**: Support for 1-52 card hands, variable player counts

## Development Guidelines

### Code Style Conventions
- **Java 17+** target version
- **Maven** build system with standard directory structure  
- **JUnit 5** for testing (67 comprehensive tests)
- **Camel case** for methods and variables
- **Pascal case** for class names
- **Private fields** with public accessors only

### Common Patterns in Codebase
```java
// Player setup pattern
player.setupPlayer(name, chips, deck);

// Hand evaluation pattern  
player.performAllChecks();
int handValue = player.getHandValue();

// Game configuration pattern
Game game = Game.createThreeCardPoker();
GameEngine engine = new GameEngine(game);
```

### Testing Approach
- **Unit tests** for individual components (`PlayerTest`, `GameTest`)
- **Integration tests** for game logic (`GameLogicTest`, `GameEngineTest`)
- **Flexibility tests** for different configurations (`FlexibleHandTest`)
- All tests use JUnit 5 and follow naming convention: `testMethodName()`

## Build & Development

### Maven Commands
```bash
cd Poker-Basic
mvn clean compile    # Compile
mvn test            # Run tests  
mvn clean package   # Build JAR
```

### Running the Application
```bash
# GUI version (recommended)
java -jar pokermon-0.08.30.jar

# Console version
java -jar pokermon-0.08.30.jar --basic

# Development mode
mvn compile exec:java -Dexec.mainClass="com.pokermon.NewJFrame"
```

## Key Refactoring Principles Applied

### 1. Eliminated Hard-Coding
- **Before**: Separate if statements for players 0,1,2,3
- **After**: Dynamic loops supporting 1-4 players

### 2. DRY Principle Implementation
- **Before**: 70+ lines of duplicated AI betting logic
- **After**: Reusable `calculateAIBet()` method

### 3. Flexible Architecture
- Support for variable hand sizes (3-card, 5-card, 7-card poker)
- Configurable starting chips and game rules
- Multiple game variants with preset configurations

## Common Development Tasks

### Adding New Tests
- Follow existing test structure in `src/test/java/com/pokermon/`
- Use `@Test`, `@BeforeEach` annotations
- Assert with `assertEquals()`, `assertTrue()`, `assertNotNull()`

### Extending Game Variants
- Create new factory methods in `Game` class
- Configure hand size, starting chips, player limits
- Test with `GameEngineTest` patterns

### UI Modifications
- GUI components in `NewJFrame.java`
- Console interface in `Main.java` and `ConsoleMain.java`
- Both support same underlying game logic

## CI/CD Integration
- **GitHub Actions**: `.github/workflows/ci.yml`
- **Auto-testing**: All PRs trigger full test suite
- **Artifact Creation**: JAR files for releases and PR testing
- **Multi-branch Support**: Alpha, development, and production releases

## Educational Focus Areas
This project demonstrates:
- **Legacy code modernization**
- **Object-oriented design principles**
- **Code reusability and DRY principles**
- **Flexible software architecture**
- **Comprehensive testing strategies**
- **Professional documentation practices**

## Common Issues & Solutions
- **Tests failing**: Ensure Java 17+ and run `mvn clean test`
- **JAR not executable**: Check main class configuration in pom.xml
- **Cards not displaying**: GUI requires proper classpath setup
- **Player count issues**: Game supports 1-4 players dynamically