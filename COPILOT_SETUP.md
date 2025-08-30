# GitHub Copilot Setup Guide

This document provides setup instructions for optimizing GitHub Copilot performance with the Poker-Basic project.

## Quick Setup

### 1. Clone and Setup
```bash
git clone https://github.com/Gameaday/poker-basic.git
cd poker-basic
```

### 2. Open in VS Code (Recommended)
```bash
code poker-basic.code-workspace
```

### 3. Install Required Extensions
VS Code will prompt to install recommended extensions:
- GitHub Copilot
- GitHub Copilot Chat
- Extension Pack for Java
- Maven for Java

### 4. Verify Setup
```bash
cd Poker-Basic
mvn clean test
```

All 67 tests should pass.

## Copilot Optimization Features

### 📋 Context Files Added
- **`.github/copilot-instructions.md`** - Comprehensive project context for Copilot
- **`ARCHITECTURE.md`** - Detailed technical architecture documentation
- **`CONTRIBUTING.md`** - Development guidelines and Copilot best practices
- **`poker-basic.code-workspace`** - Optimized VS Code workspace configuration

### ⚙️ VS Code Configuration
Enhanced `.vscode/settings.json` with:
- Copilot enabled for all relevant file types
- Optimized suggestion parameters
- Java development settings
- Maven integration

### 🚀 Workspace Features
The workspace includes:
- **Quick Tasks**: Run tests, build, start GUI/console
- **Optimized File Organization**: Easy navigation
- **Copilot Integration**: Pre-configured for best performance

## How This Improves Copilot Performance

### 1. Rich Context
- **Project Overview**: Copilot understands this is an educational poker game
- **Architecture Details**: Clear class hierarchy and design patterns
- **Code Conventions**: Naming standards and best practices
- **Common Patterns**: Frequent code patterns and usage examples

### 2. Better Suggestions
With context files, Copilot provides:
- **Accurate method signatures** based on existing patterns
- **Consistent naming** following project conventions  
- **Appropriate test structures** matching existing test patterns
- **Documentation** that matches project style

### 3. Development Efficiency
- **Faster coding**: Better suggestions reduce typing
- **Fewer errors**: Context-aware suggestions are more likely correct
- **Consistent style**: Maintains project conventions automatically
- **Learning assistance**: Great for understanding the codebase

## Using Copilot Effectively

### 💡 Best Practices

#### 1. Leverage Existing Patterns
```java
// Copilot recognizes this pattern from context
Player player = new Player();
player.setupPlayer("PlayerName", 1000, Main.setDeck());
player.performAllChecks();
```

#### 2. Use Descriptive Comments
```java
// Calculate optimal AI bet based on hand strength and available chips
public int calculateOptimalBet(int handStrength, int availableChips) {
    // Copilot will provide context-aware implementation
}
```

#### 3. Follow Test Patterns
```java
@Test
void testNewFeature() {
    // Arrange - Copilot knows the setup patterns
    Player player = new Player();
    
    // Act - Copilot suggests appropriate method calls
    
    // Assert - Copilot provides meaningful assertions
    assertEquals(expected, actual, "Clear error message");
}
```

### 🔧 Common Use Cases

#### Test Generation
1. Write test method name and comment describing what to test
2. Copilot suggests complete test implementation
3. Follows existing project test patterns

#### Method Implementation
1. Write method signature with descriptive JavaDoc
2. Copilot provides implementation following project patterns
3. Maintains consistent error handling and validation

#### Refactoring Assistance
1. Describe desired changes in comments
2. Copilot suggests code modifications
3. Maintains project architecture and conventions

## Troubleshooting

### Copilot Not Working?
1. **Check Extensions**: Ensure GitHub Copilot is installed and authenticated
2. **Restart VS Code**: Sometimes needed after installing extensions
3. **Check Settings**: Verify `github.copilot.enable` is true in workspace settings

### Getting Poor Suggestions?
1. **Add Context**: Include relevant files in your workspace
2. **Use Comments**: Describe what you're trying to accomplish
3. **Follow Patterns**: Reference existing code patterns in comments

### Build Issues?
1. **Java Version**: Ensure Java 17+ is installed
2. **Maven**: Run `mvn clean compile` to verify setup
3. **Dependencies**: Check internet connection for Maven downloads

## Project Structure Quick Reference

```
poker-basic/
├── .github/
│   └── copilot-instructions.md    # Main Copilot context
├── .vscode/
│   └── settings.json              # VS Code + Copilot settings
├── Poker-Basic/                   # Main Java project
│   ├── src/main/java/com/pokermon/
│   │   ├── Main.java              # Entry point + Player class
│   │   ├── GameEngine.java        # Game management
│   │   ├── NewJFrame.java         # GUI interface
│   │   └── ...                    # Other classes
│   └── src/test/java/com/pokermon/
│       ├── PlayerTest.java        # Unit tests
│       ├── GameEngineTest.java    # Integration tests
│       └── ...                    # Other tests
├── ARCHITECTURE.md                # Technical documentation
├── CONTRIBUTING.md                # Development guide
├── poker-basic.code-workspace     # VS Code workspace
└── README.md                      # Project overview
```

## Next Steps

1. **Open the workspace**: `code poker-basic.code-workspace`
2. **Install extensions** when prompted
3. **Run tests**: Use Command Palette → "Tasks: Run Task" → "Maven: Clean and Test"
4. **Start coding**: Copilot will provide enhanced suggestions based on project context

## Support

- **Project Documentation**: See `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`
- **Copilot Help**: Check GitHub Copilot documentation
- **Issues**: Create GitHub issues for project-specific problems

---

**Happy coding with enhanced GitHub Copilot assistance!** 🎯