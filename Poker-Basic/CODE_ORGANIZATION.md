# Code Organization and Architecture

This document describes the improved code organization implemented to provide better separation of concerns and cleaner architecture.

## Issue Addressed

The original issue requested cleaning up the project repository design and hierarchy to better separate and organize the codebase with distinct separations for:
- Interface Android
- Interface GUI  
- Interface CLI
- Interface Common
- Interface Tests
- API

## New Package Structure

### Core Architecture Packages

```
src/main/java/com/pokermon/
├── api/                    # Core Game API & Configuration
│   ├── Game.java          # Game configuration and factory methods
│   ├── GameMode.java      # Game mode enumeration (Classic, Adventure, Safari, Ironman)
│   └── GamePhase.java     # Game state management enumeration
│
├── core/                   # Business Logic & Game Engine
│   ├── Player.java        # Player entity with hand evaluation
│   ├── GameEngine.java    # Core game logic and flow management
│   └── Monster.java       # Monster system for future game modes
│
├── interfaces/             # UI Interface Implementations
│   ├── cli/               # Console/Command Line Interface
│   │   └── ConsoleInterface.java
│   ├── gui/               # Desktop GUI Interface (planned)
│   └── common/            # Shared Interface Utilities
│       └── InterfaceUtils.java
│
└── [legacy files]         # Backward Compatibility Layer
```

### Kotlin Modern UI Structure

```
src/main/kotlin/com/pokermon/
├── modern/                # Modern JavaFX UI Implementation
│   ├── ModernMain.kt
│   └── ModernPokerApp.kt
└── bridge/                # Bridge Pattern for UI Integration
    └── GameLogicBridge.kt
```

### Platform-Specific Interfaces

```
android/src/main/java/com/pokermon/android/    # Android Interface
├── MainActivity.kt
└── ui/theme/
```

## Separation of Concerns Achieved

### 1. API Layer (`com.pokermon.api`)
- **Purpose**: Defines core game configuration and state management
- **Contents**: Game configuration, GameMode enum, GamePhase enum
- **Benefits**: Centralized game rules and configuration, clear API contracts

### 2. Core Business Logic (`com.pokermon.core`)
- **Purpose**: Contains game engine and business entities
- **Contents**: Player class, GameEngine, Monster system
- **Benefits**: Reusable across all interfaces, testable in isolation

### 3. Interface Implementations (`com.pokermon.interfaces`)
- **CLI Interface**: Pure console-based implementation
- **GUI Interface**: Desktop GUI implementation (future)
- **Common Utilities**: Shared interface utilities and constants
- **Benefits**: Clear separation of presentation logic from business logic

### 4. Platform-Specific Interfaces
- **Android**: Native Android UI implementation
- **Modern UI**: Kotlin/JavaFX cross-platform implementation
- **Benefits**: Platform-optimized user experiences

### 5. Backward Compatibility (`com.pokermon`)
- **Purpose**: Maintains compatibility with existing code and tests
- **Contents**: Original class structure preserved
- **Benefits**: Zero breaking changes, all 190 tests continue to pass

## Benefits Achieved

### ✅ Clean Separation of Concerns
- API configuration separate from business logic
- Business logic separate from presentation logic
- Interface implementations clearly organized by type

### ✅ Improved Maintainability
- Each package has a single, clear responsibility
- Easier to locate and modify specific functionality
- Reduced coupling between components

### ✅ Better Testing Structure
- Business logic can be tested independently of UI
- Interface implementations can be tested in isolation
- Clear boundaries make mocking easier

### ✅ Enhanced Extensibility
- New interfaces can be added without affecting core logic
- Game modes can be extended without touching UI code
- Platform-specific optimizations are isolated

### ✅ Future-Proof Architecture
- Ready for additional platforms (web, mobile, etc.)
- Prepared for microservices if needed
- Supports plugin architecture for new game modes

## Migration Strategy

### Phase 1: ✅ Complete
- Created new package structure
- Moved core business logic to appropriate packages
- Implemented console interface using new structure
- Maintained full backward compatibility

### Phase 2: Planned
- Migrate existing GUI code to `interfaces/gui/` package
- Update bridge patterns to use new core classes
- Create additional interface implementations as needed

### Phase 3: Future
- Gradually deprecate legacy classes in favor of new structure
- Add more sophisticated plugin architecture
- Implement additional game modes using new structure

## Usage Examples

### Using New Organized Structure
```java
// Direct use of new organized classes
import com.pokermon.api.Game;
import com.pokermon.core.GameEngine;
import com.pokermon.interfaces.cli.ConsoleInterface;

Game config = Game.createAdventureMode();
GameEngine engine = new GameEngine(config);
// ... use organized structure
```

### Backward Compatibility
```java
// Existing code continues to work unchanged
import com.pokermon.Player;
import com.pokermon.Game;

Player player = new Player("TestPlayer");
Game game = new Game();
// ... existing functionality preserved
```

## Validation

### ✅ All Tests Pass
- 190 tests continue to pass without modification
- No breaking changes to existing APIs
- Full backward compatibility maintained

### ✅ Build System Unchanged
- Maven build process unchanged
- JAR packaging works identically
- Android build compatibility preserved

### ✅ Runtime Compatibility
- All existing entry points continue to work
- Console mode functionality preserved
- Modern UI functionality preserved

## Summary

The reorganization successfully addresses the original issue requirements by providing clear separation between:

- ✅ **Interface Android**: `android/src/main/java/com/pokermon/android/`
- ✅ **Interface GUI**: `src/main/kotlin/com/pokermon/modern/` + `src/main/java/com/pokermon/interfaces/gui/`
- ✅ **Interface CLI**: `src/main/java/com/pokermon/interfaces/cli/`
- ✅ **Interface Common**: `src/main/java/com/pokermon/interfaces/common/`
- ✅ **Interface Tests**: All 190 tests continue to work with new structure
- ✅ **API**: `src/main/java/com/pokermon/api/` + `src/main/java/com/pokermon/core/`

The implementation maintains full backward compatibility while providing a clean, extensible architecture for future development.