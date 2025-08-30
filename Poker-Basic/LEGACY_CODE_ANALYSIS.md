# Legacy Code Analysis

This document identifies legacy code that is outside of current useful implementation and could be considered for refactoring or removal.

## Recently Removed

### ✅ NewJFrame.java (REMOVED)
- **Status**: Removed as part of default UI modernization
- **Reason**: Legacy Swing GUI implementation replaced by modern JavaFX UI
- **Size**: 1,005 lines
- **Impact**: High - was main GUI but now replaced

## Current Legacy Code Issues

### 1. Main.java - Mixed Swing Dependencies
- **Location**: `src/main/java/com/pokermon/Main.java`
- **Issue**: Uses JOptionPane (Swing) for user interaction in what should be console-only code
- **Lines**: 10, 565, 567, 569, 714, 1234, 1241, 1247, 1270, 1296, 1305, 1313
- **Problem**: 
  - Mixes GUI (Swing) code in business logic
  - Makes code non-portable and confuses separation of concerns
  - Should use console-only input/output
- **Recommendation**: Refactor to use Scanner-based console input like ConsoleMain.java

### 2. Monster System - Unused Game Feature
- **Location**: `src/main/java/com/pokermon/Monster.java` (165 lines)
- **Location**: `src/main/java/com/pokermon/MonsterCollection.java` (219 lines)
- **Issue**: Complete monster battle system that appears unused in current gameplay
- **Features Defined But Unused**:
  - Monster rarity system (Common, Uncommon, Rare, Epic, Legendary)
  - Monster stats (HP, Attack, Defense, Speed)
  - Monster evolution mechanics
  - Monster collection management
  - Monster types and abilities
- **Current Usage**: 
  - GameMode references monsters but actual gameplay doesn't use them
  - Only referenced in tests and GameMode.hasMonsters()
- **Recommendation**: Either integrate monster functionality or document as future feature

### 3. Game Architecture - Partially Implemented
- **Location**: `src/main/java/com/pokermon/Game.java` (158 lines)
- **Location**: `src/main/java/com/pokermon/GameEngine.java` (243 lines)
- **Location**: `src/main/java/com/pokermon/GameMode.java` (62 lines)
- **Issue**: Sophisticated game configuration system that is defined but not actively used
- **Features Defined**:
  - Multiple game modes (Classic, Adventure, Safari, Ironman)
  - Configurable hand sizes, player counts, chip amounts
  - Betting round configuration
  - Game state management
- **Current Usage**:
  - Classes exist and have factory methods
  - Not integrated with main gameplay in Main.java or ConsoleMain.java
  - Not used by Modern UI (uses GameLogicBridge instead)
- **Recommendation**: Either integrate with UIs or document as architectural planning

### 4. Dual Player Systems
- **Location**: `src/main/java/com/pokermon/Main.java` (Player class embedded)
- **Issue**: Player class is embedded within Main.java instead of being separate
- **Problem**:
  - Player class (lines 16-420 in Main.java) should be its own file
  - Violates single responsibility principle
  - Makes code harder to test and maintain
- **Recommendation**: Extract Player class to separate file

## Code Quality Issues

### Missing Integration Points
1. **Modern UI Bridge**: GameLogicBridge.kt uses mock data instead of connecting to Game/GameEngine
2. **Console vs GUI Split**: Main.java and ConsoleMain.java duplicate logic instead of sharing business logic
3. **Settings System**: Modern UI has settings dialogs but no persistence or integration with game logic

### Architectural Inconsistencies
1. **Three UI Systems**: 
   - Main.java (business logic + Swing dialogs)
   - ConsoleMain.java (pure console)
   - Modern UI (JavaFX with mock bridge)
2. **No Shared Business Logic**: Each UI implements its own game flow instead of using common classes

## Recommendations

### Short Term (Clean Up Legacy)
1. **Remove JOptionPane from Main.java** - Replace with console input/output
2. **Extract Player class** from Main.java to separate file
3. **Document Monster System** as future feature or remove if not planned

### Medium Term (Architecture Improvement)
1. **Integrate Game/GameEngine** with UIs or document as planning artifacts
2. **Create Shared Business Logic** that all UIs can use
3. **Connect Modern UI Bridge** to actual game logic instead of mock data

### Long Term (System Design)
1. **Single Source of Truth** for game rules and logic
2. **Plugin Architecture** for different UI implementations
3. **Persistent Settings** system across all UIs

## Files by Priority for Cleanup

### High Priority (Active Issues)
- `Main.java` - Remove Swing dependencies, extract Player class
- `GameLogicBridge.kt` - Connect to real game logic

### Medium Priority (Unused But Valid)
- `Monster.java` - Document as future feature or integrate
- `MonsterCollection.java` - Document as future feature or integrate
- `Game.java` - Integrate with UIs or document purpose
- `GameEngine.java` - Integrate with UIs or document purpose

### Low Priority (Working As Intended)
- `GameMode.java` - Well-designed enum, just needs integration
- `ConsoleMain.java` - Clean implementation, good separation

## Testing Coverage
- All legacy classes have corresponding test files
- Tests should be maintained even if classes are marked as future features
- Tests help document intended functionality

## Conclusion
The codebase shows signs of active development with multiple architectural approaches being explored. The main legacy issue is the mixed Swing/console approach in Main.java and the unintegrated monster/game engine systems. These represent either incomplete features or architectural experiments that should be clearly documented.