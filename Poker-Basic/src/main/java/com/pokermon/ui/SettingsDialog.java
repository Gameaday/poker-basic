package com.pokermon.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.util.StringConverter;

/**
 * Settings dialog for configuring graphics and game options.
 * Provides a user-friendly interface for customizing the game experience.
 */
public class SettingsDialog extends Dialog<ButtonType> {
    
    private final GameSettingsManager settingsManager;
    private final ThemeManager themeManager;
    
    // Graphics controls
    private CheckBox fullScreenCheckBox;
    private CheckBox animationsCheckBox;
    private CheckBox soundEffectsCheckBox;
    private ComboBox<String> themeComboBox;
    private Slider windowWidthSlider;
    private Slider windowHeightSlider;
    
    // Game controls
    private Spinner<Integer> initialChipsSpinner;
    private Spinner<Integer> playerCountSpinner;
    private CheckBox hintsCheckBox;
    private CheckBox monsterModeCheckBox;
    private ComboBox<String> difficultyComboBox;
    
    // Input controls
    private CheckBox touchCheckBox;
    private CheckBox gamepadCheckBox;
    private CheckBox keyboardShortcutsCheckBox;
    
    public SettingsDialog(GameSettingsManager settingsManager, ThemeManager themeManager) {
        this.settingsManager = settingsManager;
        this.themeManager = themeManager;
        
        initializeDialog();
        createContent();
        loadCurrentSettings();
    }
    
    private void initializeDialog() {
        setTitle("Settings");
        setHeaderText("Game Configuration");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        
        // Add OK and Cancel buttons
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Handle OK button action
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setOnAction(e -> saveSettings());
    }
    
    private void createContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Create tabs
        Tab graphicsTab = createGraphicsTab();
        Tab gameTab = createGameTab();
        Tab inputTab = createInputTab();
        
        tabPane.getTabs().addAll(graphicsTab, gameTab, inputTab);
        
        // Set the content
        getDialogPane().setContent(tabPane);
        getDialogPane().setPrefSize(500, 400);
    }
    
    private Tab createGraphicsTab() {
        Tab tab = new Tab("Graphics");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Full screen option
        fullScreenCheckBox = new CheckBox("Full Screen Mode");
        
        // Animations option
        animationsCheckBox = new CheckBox("Enable Animations");
        
        // Sound effects option
        soundEffectsCheckBox = new CheckBox("Enable Sound Effects");
        
        // Theme selection
        Label themeLabel = new Label("Theme:");
        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(themeManager.getAvailableThemes());
        themeComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String theme) {
                return theme.substring(0, 1).toUpperCase() + theme.substring(1);
            }
            
            @Override
            public String fromString(String string) {
                return string.toLowerCase();
            }
        });
        
        // Window size controls
        Label sizeLabel = new Label("Window Size:");
        
        Label widthLabel = new Label("Width:");
        windowWidthSlider = new Slider(800, 1920, 1024);
        windowWidthSlider.setShowTickLabels(true);
        windowWidthSlider.setShowTickMarks(true);
        windowWidthSlider.setMajorTickUnit(200);
        Label widthValueLabel = new Label();
        windowWidthSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            widthValueLabel.setText(String.valueOf(newVal.intValue())));
        
        Label heightLabel = new Label("Height:");
        windowHeightSlider = new Slider(600, 1080, 768);
        windowHeightSlider.setShowTickLabels(true);
        windowHeightSlider.setShowTickMarks(true);
        windowHeightSlider.setMajorTickUnit(100);
        Label heightValueLabel = new Label();
        windowHeightSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            heightValueLabel.setText(String.valueOf(newVal.intValue())));
        
        HBox widthBox = new HBox(10, widthLabel, windowWidthSlider, widthValueLabel);
        widthBox.setAlignment(Pos.CENTER_LEFT);
        HBox heightBox = new HBox(10, heightLabel, windowHeightSlider, heightValueLabel);
        heightBox.setAlignment(Pos.CENTER_LEFT);
        
        content.getChildren().addAll(
            fullScreenCheckBox,
            animationsCheckBox,
            soundEffectsCheckBox,
            new Separator(),
            themeLabel,
            themeComboBox,
            new Separator(),
            sizeLabel,
            widthBox,
            heightBox
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private Tab createGameTab() {
        Tab tab = new Tab("Game");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Initial chips
        Label chipsLabel = new Label("Initial Chips:");
        initialChipsSpinner = new Spinner<>(100, 10000, 1000, 100);
        initialChipsSpinner.setEditable(true);
        HBox chipsBox = new HBox(10, chipsLabel, initialChipsSpinner);
        chipsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Player count
        Label playersLabel = new Label("Number of Players:");
        playerCountSpinner = new Spinner<>(1, 4, 3, 1);
        playerCountSpinner.setEditable(true);
        HBox playersBox = new HBox(10, playersLabel, playerCountSpinner);
        playersBox.setAlignment(Pos.CENTER_LEFT);
        
        // Hints option
        hintsCheckBox = new CheckBox("Show Hints");
        
        // Monster mode option
        monsterModeCheckBox = new CheckBox("Enable Monster Mode");
        
        // Difficulty selection
        Label difficultyLabel = new Label("Difficulty:");
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("easy", "medium", "hard", "expert");
        difficultyComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String difficulty) {
                return difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1);
            }
            
            @Override
            public String fromString(String string) {
                return string.toLowerCase();
            }
        });
        
        content.getChildren().addAll(
            chipsBox,
            playersBox,
            new Separator(),
            hintsCheckBox,
            monsterModeCheckBox,
            new Separator(),
            difficultyLabel,
            difficultyComboBox
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private Tab createInputTab() {
        Tab tab = new Tab("Input");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Touch controls
        touchCheckBox = new CheckBox("Enable Touch Controls");
        
        // Gamepad controls
        gamepadCheckBox = new CheckBox("Enable Gamepad Support");
        
        // Keyboard shortcuts
        keyboardShortcutsCheckBox = new CheckBox("Enable Keyboard Shortcuts");
        
        // Info labels
        Label touchInfo = new Label("Touch controls optimize the interface for touchscreen devices.");
        touchInfo.setWrapText(true);
        touchInfo.getStyleClass().add("info-text");
        
        Label gamepadInfo = new Label("Gamepad support allows navigation using Xbox/PlayStation controllers.");
        gamepadInfo.setWrapText(true);
        gamepadInfo.getStyleClass().add("info-text");
        
        Label keyboardInfo = new Label("Keyboard shortcuts provide quick access to common actions.");
        keyboardInfo.setWrapText(true);
        keyboardInfo.getStyleClass().add("info-text");
        
        content.getChildren().addAll(
            touchCheckBox,
            touchInfo,
            new Separator(),
            gamepadCheckBox,
            gamepadInfo,
            new Separator(),
            keyboardShortcutsCheckBox,
            keyboardInfo
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private void loadCurrentSettings() {
        // Graphics settings
        fullScreenCheckBox.setSelected(settingsManager.isFullScreen());
        animationsCheckBox.setSelected(settingsManager.isAnimationsEnabled());
        soundEffectsCheckBox.setSelected(settingsManager.isSoundEffectsEnabled());
        themeComboBox.setValue(settingsManager.getCurrentTheme());
        windowWidthSlider.setValue(settingsManager.getWindowWidth());
        windowHeightSlider.setValue(settingsManager.getWindowHeight());
        
        // Game settings
        initialChipsSpinner.getValueFactory().setValue(settingsManager.getInitialChips());
        playerCountSpinner.getValueFactory().setValue(settingsManager.getPlayerCount());
        hintsCheckBox.setSelected(settingsManager.isHintsEnabled());
        monsterModeCheckBox.setSelected(settingsManager.isMonsterModeEnabled());
        difficultyComboBox.setValue(settingsManager.getDifficulty());
        
        // Input settings
        touchCheckBox.setSelected(settingsManager.isTouchEnabled());
        gamepadCheckBox.setSelected(settingsManager.isGamepadEnabled());
        keyboardShortcutsCheckBox.setSelected(settingsManager.isKeyboardShortcutsEnabled());
    }
    
    private void saveSettings() {
        // Graphics settings
        settingsManager.setFullScreen(fullScreenCheckBox.isSelected());
        settingsManager.setAnimationsEnabled(animationsCheckBox.isSelected());
        settingsManager.setSoundEffectsEnabled(soundEffectsCheckBox.isSelected());
        settingsManager.setCurrentTheme(themeComboBox.getValue());
        settingsManager.setWindowWidth((int) windowWidthSlider.getValue());
        settingsManager.setWindowHeight((int) windowHeightSlider.getValue());
        
        // Game settings
        settingsManager.setInitialChips(initialChipsSpinner.getValue());
        settingsManager.setPlayerCount(playerCountSpinner.getValue());
        settingsManager.setHintsEnabled(hintsCheckBox.isSelected());
        settingsManager.setMonsterModeEnabled(monsterModeCheckBox.isSelected());
        settingsManager.setDifficulty(difficultyComboBox.getValue());
        
        // Input settings
        settingsManager.setTouchEnabled(touchCheckBox.isSelected());
        settingsManager.setGamepadEnabled(gamepadCheckBox.isSelected());
        settingsManager.setKeyboardShortcutsEnabled(keyboardShortcutsCheckBox.isSelected());
        
        // Save to preferences
        settingsManager.saveSettings();
    }
}