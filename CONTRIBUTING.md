# Contributing to Poker-Basic Project

Welcome to the Poker-Basic educational project! This guide will help you set up your development environment and work effectively with the codebase.

## Prerequisites

- **Java 17 or higher**
- **Maven 3.6+**
- **Git**
- **IDE**: VS Code (recommended) or IntelliJ IDEA

## Development Environment Setup

### 1. Repository Setup
```bash
git clone https://github.com/Gameaday/poker-basic.git
cd poker-basic
cd Poker-Basic
mvn clean compile test
```

### 2. GitHub Copilot Setup (Recommended)

This project is optimized for GitHub Copilot to provide enhanced development assistance.

#### VS Code Setup
1. **Install Extensions**:
   - GitHub Copilot
   - Extension Pack for Java
   - Maven for Java

2. **Configure Copilot**:
   - The project includes optimized VS Code settings in `.vscode/settings.json`
   - Copilot instructions are available in `.github/copilot-instructions.md`
   - These provide context about the project structure and coding conventions

3. **Optimize Copilot Performance**:
   - Keep relevant files open when working (e.g., related classes)
   - Use descriptive variable and method names
   - Add comments explaining complex business logic
   - Reference existing patterns in the codebase

#### IntelliJ IDEA Setup
1. **Install GitHub Copilot Plugin**
2. **Configure Project Settings**:
   - Set Java SDK to 17+
   - Import as Maven project
   - Enable annotation processing

## Project Structure

```
Poker-Basic/
├── src/main/java/com/pokermon/
│   ├── Main.java              # Console entry point
│   ├── ConsoleMain.java       # Alternative console interface
│   ├── NewJFrame.java         # GUI entry point
│   ├── Player.java            # Core player logic
│   ├── GameEngine.java        # Game management
│   ├── Game.java              # Game configuration
│   └── Monster.java           # AI player logic
├── src/test/java/com/pokermon/
│   ├── PlayerTest.java        # Player unit tests
│   ├── GameEngineTest.java    # Game engine tests
│   ├── GameLogicTest.java     # Integration tests
│   └── ...                    # Additional test files
└── pom.xml                    # Maven configuration
```

## Development Workflow

### 1. Making Changes
1. **Create a branch**: `git checkout -b feature/your-feature-name`
2. **Write tests first**: Follow existing test patterns
3. **Implement changes**: Use existing code patterns
4. **Run tests**: `mvn test`
5. **Test manually**: Run both GUI and console versions

### 2. Testing Strategy
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=PlayerTest

# Run with verbose output
mvn test -X

# Build and test JAR
mvn clean package
java -jar target/pokermon-0.08.30.jar --help
```

### 3. Code Quality Guidelines

#### Encapsulation
- All fields should be private
- Use public getters/setters for access
- Follow existing `Player` class pattern

```java
// Good
private int chips;
public int getChips() { return chips; }
public void setChips(int chips) { this.chips = chips; }

// Avoid
public int chips;
```

#### Code Reusability
- Avoid hard-coding player counts or configurations
- Use loops instead of repetitive if statements
- Extract common logic into methods

```java
// Good - flexible
for (int i = 0; i < players.length; i++) {
    players[i].setupPlayer(names[i], startingChips, deck);
}

// Avoid - hard-coded
if (playerCount == 1) { player1.setup(...); }
if (playerCount == 2) { player2.setup(...); }
```

#### Testing
- Write tests for all new functionality
- Follow existing naming conventions: `testMethodName()`
- Use descriptive assertions with custom messages

```java
@Test
void testPlayerInitialization() {
    Player player = new Player();
    assertEquals(0, player.getChips(), "New player should start with 0 chips");
    assertNull(player.getName(), "New player should have no name initially");
}
```

## Working with GitHub Copilot

### Getting Better Suggestions

1. **Provide Context**:
   ```java
   // Good comment for Copilot
   // Calculate AI bet amount based on hand strength and available chips
   public int calculateAIBet(int handValue, int availableChips) {
   ```

2. **Use Descriptive Names**:
   ```java
   // Good - clear intent
   int calculatedBetAmount = determineOptimalBet(handStrength, playerChips);
   
   // Less helpful
   int x = calc(y, z);
   ```

3. **Reference Existing Patterns**:
   ```java
   // Copilot will understand this pattern from existing code
   player.setupPlayer(playerName, startingChips, gameDeck);
   player.performAllChecks();
   ```

### Common Copilot Use Cases

- **Test Generation**: Comment what you want to test, let Copilot suggest the test
- **Method Implementation**: Write method signature and comment, get implementation
- **Refactoring**: Describe the pattern you want to replace
- **Documentation**: Generate JavaDoc comments for methods

## Submitting Changes

### Pull Request Guidelines
1. **Tests must pass**: All 67+ tests should pass
2. **Follow existing patterns**: Maintain consistency with current code
3. **Include documentation**: Update relevant MD files if needed
4. **Small, focused changes**: One feature/fix per PR

### PR Checklist
- [ ] Tests pass locally (`mvn test`)
- [ ] Code follows project conventions
- [ ] New functionality includes tests
- [ ] Documentation updated if needed
- [ ] Commit messages are descriptive

## Getting Help

### Project-Specific Resources
- **Architecture Overview**: See `README.md`
- **Implementation Details**: See `IMPLEMENTATION_SUMMARY.md`
- **Release Process**: See `RELEASE_TESTING.md`
- **Copilot Context**: See `.github/copilot-instructions.md`

### Common Issues
- **Build failures**: Ensure Java 17+ and latest Maven
- **Test failures**: Check that you haven't broken existing functionality
- **Copilot not working**: Verify extensions are installed and authenticated

### Best Practices for Educational Project

This project demonstrates professional development practices:

1. **Code Quality**: Write maintainable, well-documented code
2. **Testing**: Comprehensive test coverage for reliability
3. **Documentation**: Clear explanations for educational value
4. **Architecture**: Flexible, extensible design patterns
5. **Version Control**: Meaningful commits and clear PR descriptions

Remember: This is an educational project. Focus on demonstrating best practices and clear, maintainable code that others can learn from!