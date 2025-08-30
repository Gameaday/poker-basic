package com.pokermon.ui;

import java.util.prefs.Preferences;

/**
 * Manages game settings including graphics options, gameplay preferences,
 * and user interface customizations.
 */
public class GameSettingsManager {
    
    private final Preferences prefs;
    
    // Graphics Settings
    private boolean fullScreen = false;
    private boolean enableAnimations = true;
    private boolean enableSoundEffects = true;
    private int windowWidth = 1024;
    private int windowHeight = 768;
    private String theme = "default";
    
    // Game Settings
    private int initialChips = 1000;
    private int playerCount = 3;
    private boolean showHints = true;
    private boolean enableMonsterMode = false;
    private String difficulty = "medium";
    
    // Input Settings
    private boolean enableTouch = true;
    private boolean enableGamepad = false;
    private boolean enableKeyboardShortcuts = true;
    
    public GameSettingsManager() {
        prefs = Preferences.userNodeForPackage(GameSettingsManager.class);
        loadSettings();
    }
    
    private void loadSettings() {
        // Graphics Settings
        fullScreen = prefs.getBoolean("fullScreen", false);
        enableAnimations = prefs.getBoolean("enableAnimations", true);
        enableSoundEffects = prefs.getBoolean("enableSoundEffects", true);
        windowWidth = prefs.getInt("windowWidth", 1024);
        windowHeight = prefs.getInt("windowHeight", 768);
        theme = prefs.get("theme", "default");
        
        // Game Settings
        initialChips = prefs.getInt("initialChips", 1000);
        playerCount = prefs.getInt("playerCount", 3);
        showHints = prefs.getBoolean("showHints", true);
        enableMonsterMode = prefs.getBoolean("enableMonsterMode", false);
        difficulty = prefs.get("difficulty", "medium");
        
        // Input Settings
        enableTouch = prefs.getBoolean("enableTouch", true);
        enableGamepad = prefs.getBoolean("enableGamepad", false);
        enableKeyboardShortcuts = prefs.getBoolean("enableKeyboardShortcuts", true);
    }
    
    public void saveSettings() {
        // Graphics Settings
        prefs.putBoolean("fullScreen", fullScreen);
        prefs.putBoolean("enableAnimations", enableAnimations);
        prefs.putBoolean("enableSoundEffects", enableSoundEffects);
        prefs.putInt("windowWidth", windowWidth);
        prefs.putInt("windowHeight", windowHeight);
        prefs.put("theme", theme);
        
        // Game Settings
        prefs.putInt("initialChips", initialChips);
        prefs.putInt("playerCount", playerCount);
        prefs.putBoolean("showHints", showHints);
        prefs.putBoolean("enableMonsterMode", enableMonsterMode);
        prefs.put("difficulty", difficulty);
        
        // Input Settings
        prefs.putBoolean("enableTouch", enableTouch);
        prefs.putBoolean("enableGamepad", enableGamepad);
        prefs.putBoolean("enableKeyboardShortcuts", enableKeyboardShortcuts);
    }
    
    // Graphics Settings Getters/Setters
    public boolean isFullScreen() { return fullScreen; }
    public void setFullScreen(boolean fullScreen) { this.fullScreen = fullScreen; }
    
    public boolean isAnimationsEnabled() { return enableAnimations; }
    public void setAnimationsEnabled(boolean enableAnimations) { this.enableAnimations = enableAnimations; }
    
    public boolean isSoundEffectsEnabled() { return enableSoundEffects; }
    public void setSoundEffectsEnabled(boolean enableSoundEffects) { this.enableSoundEffects = enableSoundEffects; }
    
    public int getWindowWidth() { return windowWidth; }
    public void setWindowWidth(int windowWidth) { this.windowWidth = windowWidth; }
    
    public int getWindowHeight() { return windowHeight; }
    public void setWindowHeight(int windowHeight) { this.windowHeight = windowHeight; }
    
    public String getCurrentTheme() { return theme; }
    public void setCurrentTheme(String theme) { this.theme = theme; }
    
    // Game Settings Getters/Setters
    public int getInitialChips() { return initialChips; }
    public void setInitialChips(int initialChips) { this.initialChips = initialChips; }
    
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    
    public boolean isHintsEnabled() { return showHints; }
    public void setHintsEnabled(boolean showHints) { this.showHints = showHints; }
    
    public boolean isMonsterModeEnabled() { return enableMonsterMode; }
    public void setMonsterModeEnabled(boolean enableMonsterMode) { this.enableMonsterMode = enableMonsterMode; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    // Input Settings Getters/Setters
    public boolean isTouchEnabled() { return enableTouch; }
    public void setTouchEnabled(boolean enableTouch) { this.enableTouch = enableTouch; }
    
    public boolean isGamepadEnabled() { return enableGamepad; }
    public void setGamepadEnabled(boolean enableGamepad) { this.enableGamepad = enableGamepad; }
    
    public boolean isKeyboardShortcutsEnabled() { return enableKeyboardShortcuts; }
    public void setKeyboardShortcutsEnabled(boolean enableKeyboardShortcuts) { this.enableKeyboardShortcuts = enableKeyboardShortcuts; }
}