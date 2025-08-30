# Modern JavaFX UI Implementation

This document describes the new modern JavaFX user interface that replaces the legacy Swing GUI.

## Overview

The new UI system provides a cross-platform, touch-friendly interface with modern animations, theming, and input support. It maintains compatibility with existing game logic while offering a superior user experience.

## Architecture

### Core Components

#### `PokerFXApplication` - Main Application
- Entry point for the JavaFX interface
- Manages main menu and scene transitions
- Integrates all managers and handlers

#### `GameView` - Game Interface
- Primary gameplay interface
- Displays poker table, cards, and player information
- Handles game actions and interactions

#### `CardView` - Card Component
- Modern card display with images or text fallback
- Touch-friendly interactions with animations
- Selection states with visual feedback

### Management Systems

#### `GameSettingsManager` - Settings Persistence
- Manages all user preferences
- Persistent storage using Java Preferences API
- Categories: Graphics, Game, Input

#### `ThemeManager` - Visual Themes
- Dynamic theme switching
- CSS-based styling system
- Built-in themes: Default, Dark, Luxury

#### `InputHandler` - Cross-Platform Input
- Keyboard shortcuts for accessibility
- Touch gesture support
- Gamepad compatibility layer

### User Interface Features

#### Settings System
- **Graphics Tab**: Fullscreen, animations, themes, window size
- **Game Tab**: Initial chips, player count, difficulty, monster mode
- **Input Tab**: Touch, gamepad, and keyboard options

#### Modern Styling
- CSS-based theming with hover effects
- Responsive design for different screen sizes
- Touch-friendly controls (44px minimum targets)
- Smooth animations and transitions

#### Input Methods
- **Mouse**: Standard desktop interaction
- **Touch**: Optimized for tablets and touchscreens
- **Keyboard**: Comprehensive shortcut system
- **Gamepad**: Simulated support (expandable to native)

## Usage

### Running the Application

```bash
# Default launch (tries JavaFX, falls back to Swing)
java -jar pokermon-0.08.30.jar

# Force console mode
java -jar pokermon-0.08.30.jar --console

# View help
java -jar pokermon-0.08.30.jar --help
```

### Development Build

```bash
# Compile and test
mvn compile test

# Package JAR
mvn package

# Run with JavaFX
mvn javafx:run
```

### Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `Esc` | Close dialogs/return to menu |
| `Space` | Deal cards |
| `Enter` | Confirm action |
| `F` | Fold |
| `C` | Call |
| `R` | Raise |
| `S` | Settings |
| `1-5` | Select cards |
| `F11` | Toggle fullscreen |

## Technical Details

### Dependencies
- JavaFX 21.0.2 (controls, FXML, media)
- Java 17+ required
- Maven 3.6+ for building

### Platform Support
- **Windows**: Native JavaFX support
- **Linux**: Native JavaFX support  
- **macOS**: Native JavaFX support
- **Android**: Possible with GluonHQ (future enhancement)

### Theming System
Themes are defined in `ThemeManager` with CSS styling. The system supports:
- Dynamic theme switching
- CSS file loading from resources
- Inline style overrides for themes
- Touch-specific styling classes

### Settings Persistence
Settings are stored using Java's Preferences API:
- Windows: Registry
- Linux: `~/.java/.userPrefs/`
- macOS: `~/Library/Preferences/`

## Migration from Swing

### Compatibility
- Old Swing interface remains available as fallback
- `GameLauncher` automatically detects JavaFX availability
- Existing game logic fully compatible
- All tests continue to pass

### Advantages Over Swing
1. **Modern Look**: CSS theming and animations
2. **Touch Support**: Native touch event handling
3. **Cross-Platform**: Better consistency across platforms
4. **Performance**: Hardware acceleration and efficient rendering
5. **Responsive**: Automatic scaling and layout adaptation
6. **Future-Proof**: Active development and modern standards

## Future Enhancements

### Planned Features
- Native gamepad support via external libraries
- Animation system integration with game events
- Custom theme creation tools
- Mobile packaging for Android
- Accessibility improvements (screen reader support)

### Extension Points
- Theme system easily extensible
- Input handler designed for additional input methods
- Settings system ready for new configuration options
- Card view supports custom rendering plugins

## Development Guidelines

### Adding New Settings
1. Add fields to `GameSettingsManager`
2. Update load/save methods
3. Add UI controls in `SettingsDialog`
4. Update tab layout as needed

### Creating New Themes
1. Add theme definition to `ThemeManager.initializeThemes()`
2. Define CSS styling for theme-specific elements
3. Test across different screen sizes
4. Ensure accessibility compliance

### Extending Input Support
1. Add new input methods to `InputHandler`
2. Define action mappings in setup methods
3. Implement handler methods for new inputs
4. Update settings system for configuration

This implementation provides a solid foundation for modern UI development while maintaining backward compatibility and supporting future enhancements.