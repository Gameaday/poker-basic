#!/bin/bash

# Demo script for the Modern JavaFX Poker UI
# This script demonstrates the new features and capabilities

echo "==================================================="
echo "Modern JavaFX Poker UI Demo"
echo "==================================================="
echo

# Navigate to the project directory
cd "$(dirname "$0")/Poker-Basic"

echo "Building the project..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✅ Build successful!"
echo

echo "Running tests..."
mvn test -q
if [ $? -ne 0 ]; then
    echo "❌ Tests failed!"
    exit 1
fi
echo "✅ All tests passed!"
echo

echo "Creating JAR package..."
mvn package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "❌ Packaging failed!"
    exit 1
fi
echo "✅ JAR created successfully!"
echo

echo "==================================================="
echo "Demo Features Implemented:"
echo "==================================================="
echo
echo "🎮 Modern UI Components:"
echo "  ✅ JavaFX-based interface replacing Swing"
echo "  ✅ Touch-friendly controls and gestures"
echo "  ✅ Responsive design for different screen sizes"
echo "  ✅ Hardware-accelerated rendering"
echo
echo "🎨 Theming and Styling:"
echo "  ✅ CSS-based styling system"
echo "  ✅ Three built-in themes (Default, Dark, Luxury)"
echo "  ✅ Dynamic theme switching"
echo "  ✅ Smooth animations and transitions"
echo
echo "⚙️ Settings Management:"
echo "  ✅ Graphics settings (fullscreen, animations, themes)"
echo "  ✅ Game settings (chips, players, difficulty)"
echo "  ✅ Input settings (touch, gamepad, keyboard)"
echo "  ✅ Persistent storage using Java Preferences"
echo
echo "🎯 Input Support:"
echo "  ✅ Mouse and touch interactions"
echo "  ✅ Keyboard shortcuts (F=fold, C=call, R=raise, etc.)"
echo "  ✅ Gamepad simulation (expandable to native support)"
echo "  ✅ Platform-agnostic input handling"
echo
echo "🔗 Cross-Platform Compatibility:"
echo "  ✅ Windows, Linux, macOS support"
echo "  ✅ Fallback to Swing if JavaFX unavailable"
echo "  ✅ Future Android support via GluonHQ"
echo "  ✅ Backward compatibility maintained"
echo
echo "📱 Modern Features:"
echo "  ✅ Card images with text fallback"
echo "  ✅ Animated card dealing and selection"
echo "  ✅ Touch-friendly minimum 44px targets"
echo "  ✅ Accessibility considerations"
echo

echo "==================================================="
echo "How to Test the New UI:"
echo "==================================================="
echo
echo "1. 🚀 Launch with new JavaFX UI:"
echo "   java -jar target/pokermon-0.08.30.jar"
echo
echo "2. 🎮 Test different features:"
echo "   - Click 'Settings' to see the new settings system"
echo "   - Try different themes in Graphics tab"
echo "   - Start a 'New Game' to see the poker interface"
echo "   - Use keyboard shortcuts (F, C, R, S, 1-5, F11)"
echo
echo "3. 🔄 Fallback to old Swing UI:"
echo "   java -jar target/pokermon-0.08.30.jar --console"
echo
echo "4. ❓ Get help and options:"
echo "   java -jar target/pokermon-0.08.30.jar --help"
echo

echo "==================================================="
echo "Development Commands:"
echo "==================================================="
echo
echo "🔧 Compile: mvn compile"
echo "🧪 Test: mvn test"
echo "📦 Package: mvn package"
echo "🚀 Run JavaFX: mvn javafx:run"
echo "🏗️ Clean build: mvn clean compile test package"
echo

echo "==================================================="
echo "Architecture Overview:"
echo "==================================================="
echo
echo "📁 New UI Package Structure:"
echo "  src/main/java/com/pokermon/ui/"
echo "  ├── PokerFXApplication.java    # Main JavaFX app"
echo "  ├── GameView.java              # Game interface"
echo "  ├── CardView.java              # Card component"
echo "  ├── SettingsDialog.java        # Settings UI"
echo "  ├── GameSettingsManager.java   # Settings logic"
echo "  ├── ThemeManager.java          # Theme system"
echo "  └── InputHandler.java          # Input handling"
echo
echo "🎨 Resources:"
echo "  src/main/resources/"
echo "  ├── css/modern-poker.css       # Modern styling"
echo "  └── Cards/TET/                 # Card images (preserved)"
echo

if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "Java Version: $JAVA_VERSION"
    
    if [[ "$JAVA_VERSION" < "17" ]]; then
        echo "⚠️  Warning: Java 17+ recommended for optimal JavaFX support"
    else
        echo "✅ Java version compatible"
    fi
else
    echo "❌ Java not found in PATH"
fi
echo

echo "==================================================="
echo "Ready to test the modern JavaFX interface!"
echo "Run: java -jar target/pokermon-0.08.30.jar"
echo "==================================================="