# Pokermon - GitHub Copilot Instructions

**CRITICAL: Always reference these instructions first and only fallback to search or bash commands when you encounter unexpected information that does not match the information here.**

## Repository Overview

Pokermon is a **pure Kotlin-native** cross-platform poker game project featuring monster collection mechanics with multiple build targets:
- Native executables (Windows .exe, Linux .deb, macOS .dmg) - **PRIMARY DELIVERABLES**
- Android APK (via Gradle) - **REQUIRES INTERNET** 
- Desktop JAR (via Gradle) - **DEVELOPMENT ONLY**
- Professional codebase with 39 Kotlin files and comprehensive test coverage

The project demonstrates modern Kotlin-native development practices using pure Gradle build system, flow-based reactive architecture, and comprehensive cross-platform support for an engaging gaming experience.

## Development Philosophy: Kotlin-Native with DRY Principles

This project follows a **unified Kotlin-native architecture** guided by core design philosophies:

### DRY (Don't Repeat Yourself) Guidelines

This project follows DRY principles to create single authoritative sources of truth for APIs and methods:

- **Separate Game Logic from UI Implementation**: Core game logic is isolated from UI-specific code, enabling unified API for all platforms
- **Shared Common Code**: Unified API serves all UI platforms (Android, Desktop, Console) through bridge patterns
- **Single Monster Database**: The Monster Database serves as the authoritative source for all monster data across game modes
- **Consistent Project Hierarchy**: The project maintains logical hierarchies in both code organization and package structure
- **Flow-based Reactive State Management**: Modern reactive patterns for state synchronization across components
- **Logical Code Organization**: Code specific to game modes is organized under that mode, but shared functionality moves to common areas
- **Continuous Organization**: Code locations are managed and consolidated as features are added or changed
- **Professional Code Preservation**: Functional code is not deleted for convenience unless truly unnecessary - no stubbing or "TODO later" for current requirements
- **Future-Friendly Design**: Code is designed for expansion and reuse, following DRY principles for sustainable development

**Guidelines are Iterative**: These principles can be broken when there is valuable reason, but should be the default approach for new development.

### Core Kotlin-Native Benefits
- **Native Android compatibility** with Kotlin-native design
- **Unified codebase** with pure Kotlin implementation  
- **Modern null safety** with Kotlin's type system
- **Coroutines support** for async programming and reactive patterns
- **Data classes** for immutable game state management
- **Sealed classes** for type-safe game phases and modes

### Architectural Principles
- **Pure Kotlin-native classes** throughout the entire codebase
- **Bridge pattern** provides unified API for all platforms through `GameLogicBridge.kt`
- **Rich enum classes** with extension functions (GameMode.kt, GamePhase.kt)
- **Type safety first** with null-safe operators and smart casts
- **Modern utilities** in dedicated packages for enhanced functionality

## Quick Start - Essential Commands

### Bootstrap and Validate Repository
```bash
cd /home/runner/work/poker-basic/poker-basic
./gradlew verifyKotlinNativeSetup --no-daemon  # Validates pure Kotlin-native setup (works offline, ~15 seconds)
```

### Build and Test - NEVER CANCEL these commands
```bash
./gradlew :shared:compileKotlin --no-daemon  # Build: ~15 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
./gradlew :shared:test --no-daemon          # Test: ~20 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
./gradlew :shared:fatJar --no-daemon        # Package: ~25 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
```

### Run the Application
```bash
java -jar Poker-Basic/build/libs/Poker-Basic-*-fat.jar --help    # Show comprehensive help
java -jar Poker-Basic/build/libs/Poker-Basic-*-fat.jar --basic   # Console mode (interactive)
./gradlew :shared:runConsole --no-daemon                         # Direct console execution
```

### Native Builds (Primary Deliverables)
```bash
./gradlew :desktop:packageNative --no-daemon     # Native executable for current platform
./gradlew :desktop:packageWindows --no-daemon    # Windows .exe (requires Windows or cross-compilation)
./gradlew :desktop:packageLinux --no-daemon      # Linux .deb package
./gradlew :desktop:packageMacOS --no-daemon      # macOS .dmg package
```

### Android Build (Network Required)
```bash
./gradlew :android:assembleDebug --no-daemon  # APK: 30+ minutes. NEVER CANCEL. Set timeout to 60+ minutes.
```
**WARNING**: Android builds require internet access and will fail in sandboxed environments with "dl.google.com: No address associated with hostname". This is expected behavior.

## Working Effectively

### NEVER CANCEL BUILD COMMANDS
- **Gradle builds**: Take 15-25 seconds but set timeouts to 60+ seconds
- **Android builds**: Take 30+ minutes with internet access. NEVER CANCEL. Set timeout to 60+ minutes
- **Tests**: Take ~20 seconds but set timeouts to 60+ seconds
- **Native builds**: Take 5-10 minutes but set timeouts to 15+ minutes

### Development Workflow
1. **Always validate first**: `./gradlew verifyKotlinNativeSetup --no-daemon`
2. **Build and test**: `./gradlew :shared:test --no-daemon`
3. **Create JAR**: `./gradlew :shared:fatJar --no-daemon`
4. **Test functionality**: Run console mode and complete a game scenario
5. **Native builds**: `./gradlew :desktop:packageNative --no-daemon` for current platform
6. **Android build**: Only attempt if internet access is available

### Required Validation After Changes
- **Always run**: `./gradlew :shared:test --no-daemon` (all tests must pass)
- **Always test**: Run console game scenario (see Validation section)
- **Kotlin-native compatibility**: Ensure pure Kotlin compilation
- **JAR functionality**: Test `java -jar Poker-Basic/build/libs/*-fat.jar --help`

## Pure Kotlin-Native Project Structure

### Organized Hierarchy (Following DRY Principles)
```
poker-basic/
├── Poker-Basic/                    # Gradle shared module - Pure Kotlin-native
│   ├── src/main/kotlin/com/pokermon/   # PRIMARY Kotlin source (Kotlin-native only)
│   │   ├── GameEngine.kt           # Core game logic (pure Kotlin-native)
│   │   ├── Game.kt                 # Game configuration data class
│   │   ├── GameMode.kt             # Rich game modes enum (single source)
│   │   ├── GamePhase.kt            # Type-safe game phases (single source)
│   │   ├── bridge/GameLogicBridge.kt   # Unified API for all platforms
│   │   ├── modern/                 # Modern Kotlin utilities and enhancements
│   │   │   ├── KotlinExtensions.kt # Extension functions for existing classes
│   │   │   ├── ModernGameUtils.kt  # Kotlin-specific game utilities
│   │   │   ├── ModernMain.kt       # Modern entry points
│   │   │   ├── ModernPokerApp.kt   # Enhanced application logic
│   │   │   └── CardUtils.kt        # Unified card logic (DRY compliance)
│   │   └── [Additional Kotlin files]
│   ├── build.gradle                # Gradle: Pure Kotlin-native compilation
│   └── build/                      # Build outputs (JAR, reports, native)
├── android/                        # Android module with native Kotlin support
│   ├── src/main/kotlin/           # Android Kotlin source
│   └── build.gradle               # Android build configuration
├── desktop/                        # Native desktop builds
│   ├── src/main/kotlin/           # Desktop-specific Kotlin code
│   └── build.gradle               # Native executable configuration
├── .github/workflows/             # CI/CD for pure Kotlin-native builds
├── build.gradle                   # Root build configuration
└── gradlew                        # Gradle wrapper (primary build tool)
```

### Primary Kotlin Classes (Single Source of Truth)
- `com.pokermon.GameEngine` - **Core game logic** with null safety and modern collections
- `com.pokermon.Game` - **Configuration data class** with default parameters and validation
- `com.pokermon.GameMode` - **Rich enum** with extension functions for game modes
- `com.pokermon.GamePhase` - **Type-safe state management** for game phases
- `com.pokermon.bridge.GameLogicBridge` - **Unified API** serving all UI platforms
- `com.pokermon.modern.CardUtils` - **Unified card logic** following DRY principles
- `com.pokermon.HandEvaluator` - **Enhanced hand evaluation** with scoring and modifiers
- `com.pokermon.MonsterDatabase` - **Authoritative monster data** (pure Kotlin-native)
- `com.pokermon.Player` - **Player management** with modern Kotlin patterns
- `com.pokermon.modern.*` - **Modern utilities** with Kotlin-specific enhancements

### Test Organization (Comprehensive Coverage)
Tests are organized in `src/test/kotlin/` for comprehensive validation:
- **Core game tests**: Basic functionality validation
- **Bridge tests**: Cross-platform API validation  
- **AI tests**: Personality and behavior testing
- **Integration tests**: End-to-end game flow validation
- **Edge case tests**: Boundary condition handling
- **Hand evaluation tests**: Poker logic verification

## Build System: Pure Kotlin-Native Optimized

### Gradle Configuration for Pure Kotlin-Native Development
- **Kotlin compilation priority**: Pure Kotlin-native source with no Java dependencies
- **Enhanced compiler options**: JSR305 strict mode, JVM target annotations, coroutines support
- **Multi-module architecture**: Shared core, Android, and Desktop native modules
- **Kotlin 1.9.22**: Latest stable with modern language features
- **Dynamic versioning**: Timestamp-based versioning without git dependencies (1.1.0.YYYYMMDD)
- **Modern test framework**: JUnit 5 integration with Kotlin test extensions

### Key Dependencies (Pure Kotlin-Native Stack)
```gradle
kotlin.version=1.9.22
kotlinx.coroutines.version=1.7.3
kotlin.test.junit5=5.13.4
```

### DRY Build Principles
- **Single compilation target**: Pure Kotlin-native eliminates dual-language complexity
- **Unified dependency management**: Kotlin-native dependency resolution
- **Shared resource management**: Common resources across all platforms
- **Consistent build outputs**: Standardized artifacts for all target platforms

## Android Integration: Kotlin-Native Excellence

### Native Kotlin Advantages for Android Development
- **Direct compilation**: No Java-Kotlin bridge complexity, pure Kotlin compilation
- **Coroutines and Flow**: Native async support with reactive state management
- **Null safety**: Eliminates common Android crashes through compile-time checking
- **Data classes**: Perfect for Android Parcelable, Bundle handling, and state management
- **Sealed classes**: Type-safe navigation, game state transitions, and result handling
- **Extension functions**: Enhanced Android SDK classes without inheritance complexity

### Modern Android Patterns Implemented
```kotlin
// Example: Kotlin-native reactive game state
sealed class GameState {
    object Initializing : GameState()
    data class Playing(val players: List<Player>, val currentPhase: GamePhase) : GameState()
    data class Paused(val savedState: Game) : GameState()
    object GameOver : GameState()
}

// Flow-based state management
class GameStateManager {
    private val _gameState = MutableStateFlow<GameState>(GameState.Initializing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    suspend fun updateGameState(newState: GameState) {
        _gameState.emit(newState)
    }
}

// Rich enums with behavior
enum class GameMode(val displayName: String, val isMultiplayer: Boolean) {
    CLASSIC("Classic Poker", true) {
        override fun createGameEngine(): GameEngine = GameEngine.createClassic()
    },
    ADVENTURE("Adventure Mode", false) {
        override fun createGameEngine(): GameEngine = GameEngine.createAdventure()
        fun hasMonsters(): Boolean = true
        fun getMonsterTypes(): List<String> = listOf("Dragon", "Wizard", "Beast")
    };
    
    abstract fun createGameEngine(): GameEngine
}
```

### DRY Implementation in Android Context
- **Single game logic**: Same Kotlin code serves Android UI and desktop/console
- **Unified state management**: Flow-based reactive patterns across all Android components
- **Shared monster database**: Single authoritative source accessed by all Android activities
- **Common UI patterns**: Reusable Kotlin extensions for Android views and components

## Network and Environment Limitations

### Works Offline (After Initial Gradle Dependencies)
- Gradle compilation, testing, and packaging
- JAR execution (console and GUI modes)
- Validation scripts
- All documentation and code analysis

### Requires Internet Access
- **Android builds**: Downloads Android SDK and dependencies
- **Initial Gradle setup**: First-time dependency downloads
- **CI/CD**: Full functionality requires network access

### Sandbox Environment Behavior
- Android builds fail with "No address associated with hostname" (expected)
- JAR builds work perfectly after initial dependency download
- All validation scripts work offline

## Manual Validation Scenarios

### CRITICAL: Always Test Complete User Scenarios

#### Console Mode Validation
```bash
cd Poker-Basic
java -jar target/pokermon-1.0.0-fat.jar --basic
# 1. Enter player name (e.g., "TestPlayer")
# 2. Choose number of AI opponents (1-3) 
# 3. Choose starting chips (500 recommended)
# 4. Verify game displays cards and hand evaluation
# 5. Place a bet and verify betting system works
# 6. Exit gracefully (timeout will occur)
```

#### JAR Functionality Validation
```bash
cd Poker-Basic
java -jar target/pokermon-1.0.0-fat.jar --help     # Must show comprehensive help
java -jar target/pokermon-1.0.0-fat.jar --version  # Must show version 1.0.0
```

#### Build System Validation
```bash
cd /home/runner/work/poker-basic/poker-basic
./validate-android-build.sh  # Must pass all 21 checks
cd Poker-Basic && mvn test -B # Must pass all 254 tests
```

## Build Outputs and Artifacts

### Successful Build Outputs
- **JAR**: `Poker-Basic/target/pokermon-1.0.0-fat.jar` (works standalone, ~700KB)
- **APK**: `android/build/outputs/apk/debug/android-debug.apk` (requires internet to build)
- **Test Reports**: `Poker-Basic/target/surefire-reports/` (XML format)

### Build Verification Commands
```bash
cd Poker-Basic
ls -la target/*.jar                        # Verify JAR exists (~700KB)
java -jar target/pokermon-1.0.0-fat.jar --help  # Verify JAR is executable
```

## Time Expectations

### Typical Command Times (Add 50% buffer for timeouts)
- `mvn clean compile`: ~10 seconds
- `mvn test`: ~12 seconds (254 tests)
- `mvn clean package`: ~15 seconds
- `./validate-android-build.sh`: <1 second
- Android build: 30+ minutes (with internet)

### Timeout Recommendations
- Maven commands: 60+ seconds minimum
- Android builds: 60+ minutes minimum
- Validation scripts: 30 seconds

## Development Best Practices: Kotlin-Native with DRY

### Kotlin-First Development Guidelines
1. **New features**: Always implement in Kotlin first, leverage modern language features
2. **Null safety**: Use safe call operators (?.) and Elvis operators (?:) consistently
3. **Data classes**: For immutable state management and value objects
4. **Sealed classes**: For type-safe game state transitions and result handling
5. **Coroutines and Flow**: For async operations and reactive state management
6. **Extension functions**: To enhance existing Java classes without inheritance
7. **Modern collections**: Use Kotlin collection operators (map, filter, reduce)
8. **Smart casts**: Leverage Kotlin's type inference and smart casting

### DRY Implementation Guidelines
1. **Single Source of Truth**: Avoid duplicate implementations across Java/Kotlin
2. **Logical Organization**: 
   - Game mode specific code → organize under that mode's package
   - Shared functionality → move to common areas as usage expands
   - Tests → keep separate in dedicated test hierarchy
3. **Continuous Refactoring**: Consolidate code as features grow to maintain DRY principles
4. **Professional Standards**: 
   - Don't delete functional code for convenience
   - Complete features properly, avoid stubbing or "TODO later"
   - Design for future expansion and reuse
5. **Bridge Pattern Usage**: Use `GameLogicBridge.kt` as unified API for all UI platforms

### Code Organization Principles
```kotlin
// GOOD: Organized by functionality with shared components
com.pokermon/
├── GameEngine.kt              // Core logic (shared)
├── Game.kt                    // Configuration (shared)
├── modern/                    // Modern utilities (shared)
├── bridge/                    // Cross-platform API (shared)
└── adventure/                 // Adventure mode specific code
    ├── AdventureGameMode.kt
    ├── MonsterBattle.kt
    └── QuestSystem.kt

// AVOID: Duplicated logic or scattered functionality
com.pokermon/
├── GameEngineClassic.kt       // Duplicate logic
├── GameEngineAdventure.kt     // Should extend/configure base
└── scattered_utilities/       // Should be organized by function
```

### Migration Strategy (Java → Kotlin)
- **Phase 1**: Core classes migrated (✅ Complete - Game, GameEngine, enums)
- **Phase 2**: Utility classes (🔄 In Progress - Player, Main, MonsterDatabase)
- **Phase 3**: UI and specialized classes (📋 Planned)
- **Maintain compatibility**: Use `@JvmStatic`, `@JvmOverloads` during transition
- **Preserve method signatures**: Ensure existing Java code continues to work

## Success Metrics: Kotlin-Native Excellence

### Build and Test Validation
- ✅ All 254 tests pass (`mvn test -B`) - **Critical requirement**
- ✅ JAR builds successfully (~700KB) - **Functional deliverable**
- ✅ Console mode works interactively with Kotlin logic - **User experience**
- ✅ Help system displays comprehensive information - **Documentation**
- ✅ Build system validation passes (21/21 checks) - **Infrastructure**
- ✅ Kotlin compilation prioritized over Java - **Architecture compliance**
- ✅ No Kotlin-Java interop conflicts - **Clean compilation**
- ✅ Android builds succeed with internet access - **Cross-platform support**

### DRY Implementation Metrics
- ✅ Single source enums (GameMode.kt, GamePhase.kt) - **No duplication**
- ✅ Unified game logic through GameEngine.kt - **Core DRY compliance**
- ✅ Bridge pattern provides single API for all platforms - **Interface unification**
- ✅ Monster database serves as authoritative source - **Data consistency**
- ✅ Tests organized separately from functional code - **Clean separation**
- ✅ Logical package organization by functionality - **Maintainable structure**

### Modern Kotlin Features Active
- ✅ Data classes for immutable state (Game.kt)
- ✅ Sealed classes for type safety (planned for GameState)
- ✅ Extension functions enhancing existing classes (KotlinExtensions.kt)
- ✅ Coroutines infrastructure ready (ModernGameUtils.kt)
- ✅ Flow-based reactive patterns (ModernPokerApp.kt)
- ✅ Rich enums with behavior (GameMode.kt, GamePhase.kt)
- ✅ Null safety throughout Kotlin codebase

## Migration Progress: Kotlin-Native Transformation

### Phase 1: Core Architecture ✅ **COMPLETE**
- **Game logic migration**: Game.java → Game.kt with data class benefits
- **Engine modernization**: GameEngine.java → GameEngine.kt with null safety
- **Enum enhancement**: Rich Kotlin enums (GameMode.kt, GamePhase.kt)
- **Build system optimization**: Kotlin-first compilation with Maven
- **Bridge pattern implementation**: Unified API via GameLogicBridge.kt
- **Modern utilities**: KotlinExtensions.kt, ModernGameUtils.kt, ModernPokerApp.kt
- **Test compatibility**: All 254 tests passing with Kotlin integration

### Phase 2: Legacy Migration 🔄 **IN PROGRESS**
- **Player system**: Java Player → Kotlin Player with data classes
- **Main utilities**: Legacy Main.java → ModernMain.kt
- **Monster database**: Enhance with Kotlin patterns while preserving authoritative role
- **AI system**: Personality management with Kotlin-native patterns
- **Console interface**: Modernize with Kotlin extension functions

### Phase 3: Advanced Integration 📋 **PLANNED**
- **Complete Java elimination**: Remove all duplicate .java.original files
- **Android Compose**: Native UI framework integration
- **Advanced coroutines**: Full reactive programming implementation
- **Kotlin DSL**: Configuration and game setup with domain-specific language
- **Performance optimization**: Kotlin-specific performance patterns

### DRY Compliance Status
- ✅ **Single source enums**: Eliminated duplicate Java/Kotlin implementations
- ✅ **Unified game logic**: GameEngine.kt serves all platforms  
- ✅ **Bridge pattern**: Single API endpoint for cross-platform access
- ✅ **Test separation**: Tests isolated from functional code
- 🔄 **Code consolidation**: Ongoing as features expand and requirements change
- 📋 **Monster database**: Planned Kotlin enhancement while maintaining authoritative role

### Technical Debt Elimination
- ✅ **Removed duplicate classes**: Eliminated compilation conflicts
- ✅ **Unified package structure**: Logical organization by functionality
- ✅ **Modern dependency management**: Kotlin-native stack
- 🔄 **Legacy code cleanup**: Systematic removal of obsolete .original files
- 📋 **Documentation updates**: Comprehensive guide updates for new architecture

This Kotlin-native migration provides a sustainable foundation for cross-platform development while adhering to DRY principles and maintaining professional code standards. The transformation enables modern Android development patterns while preserving full backward compatibility and comprehensive test coverage.