# Contributing to Pokermon

Thank you for your interest in contributing to Pokermon! This document provides guidelines and instructions for contributing to the project.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Requirements](#testing-requirements)
- [Submitting Changes](#submitting-changes)
- [Asset Contributions](#asset-contributions)

---

## Code of Conduct

This project follows professional software development practices. We expect all contributors to:

- Be respectful and constructive in discussions
- Focus on the technical merits of contributions
- Help maintain code quality and project standards
- Follow the established architectural patterns

---

## Getting Started

### Prerequisites

- **JDK 17 or higher** for JVM builds
- **Gradle** (included via wrapper)
- **Android SDK** (optional, for Android builds)
- **Git** for version control

### Initial Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Gameaday/poker-basic.git
   cd poker-basic
   ```

2. **Verify setup:**
   ```bash
   ./gradlew verifyKotlinNativeSetup --no-daemon
   ```

3. **Run tests:**
   ```bash
   ./gradlew :shared:test --no-daemon
   ```

4. **Build the project:**
   ```bash
   ./gradlew :shared:fatJar --no-daemon
   ```

---

## Development Workflow

### Branch Strategy

- `master` - Stable, production-ready code
- `copilot/*` - Feature branches created by GitHub Copilot
- Feature branches should be named descriptively (e.g., `feature/monster-evolution`)

### Making Changes

1. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the coding standards below

3. **Run tests frequently:**
   ```bash
   ./gradlew :shared:test --no-daemon
   ```

4. **Build to verify:**
   ```bash
   ./gradlew :shared:compileKotlin --no-daemon
   ```

5. **Commit with descriptive messages:**
   ```bash
   git commit -m "Add monster evolution system"
   ```

---

## Coding Standards

### Kotlin Style Guide

This project follows **Kotlin coding conventions** with the following specifics:

#### General Principles

1. **Pure Kotlin-Native**: All new code should be written in Kotlin
2. **DRY Principles**: Don't Repeat Yourself - create single sources of truth
3. **Type Safety**: Leverage Kotlin's type system and null safety
4. **Immutability**: Prefer `val` over `var`, use data classes for state

#### File Organization

```kotlin
// Package structure follows logical organization
com.pokermon/
├── GameEngine.kt           // Core logic
├── GameMode.kt            // Enums and constants
├── bridge/                // Cross-platform API
├── modes/                 // Game mode implementations
│   ├── adventure/         // Adventure mode specific
│   ├── classic/           // Classic mode specific
│   ├── safari/            // Safari mode specific
│   └── ironmon/           // Ironman mode specific
└── GameFlows/             // Reactive state management
```

#### Naming Conventions

- **Classes**: `PascalCase` (e.g., `GameEngine`, `MonsterDatabase`)
- **Functions**: `camelCase` (e.g., `evaluateHand`, `dealCards`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_PLAYERS`, `DEFAULT_CHIPS`)
- **Private properties**: prefix with underscore if needed for clarity

#### Code Style

```kotlin
// ✅ GOOD: Clear, type-safe Kotlin code
data class Monster(
    val id: String,
    val name: String,
    val type: MonsterType,
    val level: Int = 1,
    val health: Int = 100
) {
    fun takeDamage(damage: Int): Monster {
        return copy(health = maxOf(0, health - damage))
    }
}

// ✅ GOOD: Null safety with smart casts
fun processMonster(monster: Monster?) {
    monster?.let {
        println("Processing ${it.name}")
        if (it.health > 0) {
            // it is smart-cast to non-null Monster
            it.takeDamage(10)
        }
    }
}

// ✅ GOOD: Flow-based reactive state
class GameStateManager {
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
}

// ❌ AVOID: Nullable types when not needed
var monster: Monster? = null  // Only if truly optional

// ❌ AVOID: Mutable state when immutable is sufficient
var health = 100  // Prefer val with copy() for changes
```

#### Documentation

- **Public APIs**: Must have KDoc comments
- **Complex logic**: Inline comments explaining "why", not "what"
- **TODOs**: Use `TODO` comments with context, but don't commit them for production code

```kotlin
/**
 * Evaluates a poker hand and returns its strength.
 *
 * @param cards The list of cards to evaluate (must be exactly 5 cards)
 * @return HandEvaluation containing the hand type and comparison value
 * @throws IllegalArgumentException if cards.size != 5
 */
fun evaluateHand(cards: List<Card>): HandEvaluation {
    require(cards.size == 5) { "Hand must contain exactly 5 cards" }
    // Implementation...
}
```

---

## Testing Requirements

### Test Coverage

- **All new features** must include tests
- **Bug fixes** should include regression tests
- **Tests must pass** before submitting changes

### Writing Tests

```kotlin
class GameEngineTest {
    @Test
    fun `should deal correct number of cards to each player`() {
        // Arrange
        val game = Game(playerCount = 3, startingChips = 1000)
        val engine = GameEngine(game)
        
        // Act
        engine.dealCards()
        
        // Assert
        assertEquals(5, engine.getPlayerHand(0).size)
        assertEquals(5, engine.getPlayerHand(1).size)
        assertEquals(5, engine.getPlayerHand(2).size)
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew :shared:test --no-daemon

# Run specific test class
./gradlew :shared:test --tests "GameEngineTest" --no-daemon

# Run tests with detailed output
./gradlew :shared:test --no-daemon --info
```

---

## Submitting Changes

### Pull Request Process

1. **Ensure all tests pass:**
   ```bash
   ./gradlew :shared:test --no-daemon
   ```

2. **Build successfully:**
   ```bash
   ./gradlew :shared:compileKotlin --no-daemon
   ./gradlew :shared:fatJar --no-daemon
   ```

3. **Update documentation** if needed (README.md, PROJECT_STATUS.md)

4. **Create a pull request** with:
   - Clear title describing the change
   - Detailed description of what changed and why
   - Reference to any related issues
   - Test results showing all tests pass

### Pull Request Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Performance improvement
- [ ] Documentation update
- [ ] Code refactoring

## Testing
- [ ] All existing tests pass
- [ ] New tests added for new functionality
- [ ] Manual testing completed

## Checklist
- [ ] Code follows Kotlin coding conventions
- [ ] Documentation updated
- [ ] No new warnings or errors
- [ ] Commit messages are clear and descriptive
```

---

## Asset Contributions

### Asset Types Needed

1. **Monster Artwork** (50+ unique monsters)
   - Format: PNG with transparency
   - Resolution: 512x512 minimum
   - Style: Consistent across all monsters

2. **Card Designs** (54 poker cards)
   - Format: PNG
   - Resolution: 300x450 minimum
   - Style: Professional, readable

3. **UI Elements**
   - Icons, buttons, backgrounds
   - Format: PNG or SVG
   - Style: Material Design 3 compatible

4. **Audio**
   - Sound effects: OGG format
   - Music tracks: OGG format, loopable
   - Quality: 44.1kHz, stereo

### Asset Submission

1. **Follow asset guidelines** in `docs/ASSET_GUIDELINES.md` (coming soon)
2. **Submit via GitHub issue** with "Asset Contribution" label
3. **Include license information** (must be compatible with project license)
4. **Provide source files** if available (PSD, AI, etc.)

---

## Code Review Process

All submissions go through code review:

1. **Automated checks** run via GitHub Actions
2. **Manual review** by project maintainers
3. **Feedback addressed** through discussion
4. **Approval** and merge when ready

### Review Criteria

- ✅ Code quality and style
- ✅ Test coverage
- ✅ Documentation completeness
- ✅ Performance considerations
- ✅ Architectural alignment

---

## Getting Help

- **Questions?** Open a GitHub Discussion
- **Bugs?** File a GitHub Issue
- **Feature ideas?** Start with a Discussion, then create an Issue

---

## License

By contributing to Pokermon, you agree that your contributions will be licensed under the same license as the project.

---

## Recognition

Contributors will be recognized in:
- Project README.md
- Release notes
- Project documentation

Thank you for contributing to Pokermon! 🎮🐲

---

*For project status and roadmap, see [PROJECT_STATUS.md](PROJECT_STATUS.md)*
