package com.pokermon.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Modern JavaFX-based poker game application.
 * Replaces the old Swing-based GUI with a cross-platform, touch-friendly interface.
 */
public class PokerFXApplication extends Application {
    
    private Stage primaryStage;
    private Scene mainScene;
    private GameSettingsManager settingsManager;
    private ThemeManager themeManager;
    private InputHandler inputHandler;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize managers
        settingsManager = new GameSettingsManager();
        themeManager = new ThemeManager();
        inputHandler = new InputHandler(settingsManager);
        
        // Setup primary stage
        primaryStage.setTitle("Poker Game - Modern Edition");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Create main menu
        VBox mainMenu = createMainMenu();
        mainScene = new Scene(mainMenu, 1024, 768);
        
        // Apply default theme
        themeManager.applyTheme(mainScene, "default");
        
        // Setup input handling
        inputHandler.attachToScene(mainScene);
        
        primaryStage.setScene(mainScene);
        primaryStage.show();
        
        // Handle close request
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
    
    private VBox createMainMenu() {
        VBox mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(50));
        
        // Title
        Label title = new Label("Poker Game");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.getStyleClass().add("title");
        
        // Subtitle
        Label subtitle = new Label("Modern Cross-Platform Edition");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.getStyleClass().add("subtitle");
        
        // Menu buttons
        Button newGameButton = createMenuButton("New Game", this::startNewGame);
        Button settingsButton = createMenuButton("Settings", this::showSettings);
        Button aboutButton = createMenuButton("About", this::showAbout);
        Button exitButton = createMenuButton("Exit", this::exitGame);
        
        // Layout
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(newGameButton, settingsButton, aboutButton, exitButton);
        
        mainMenu.getChildren().addAll(title, subtitle, buttonBox);
        
        return mainMenu;
    }
    
    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        button.getStyleClass().add("menu-button");
        button.setOnAction(e -> action.run());
        return button;
    }
    
    private void startNewGame() {
        // Launch the game view
        GameView gameView = new GameView(settingsManager, themeManager);
        Scene gameScene = new Scene(gameView.getRoot(), 1024, 768);
        themeManager.applyTheme(gameScene, settingsManager.getCurrentTheme());
        
        // Setup input handling for game scene
        inputHandler.attachToScene(gameScene);
        
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Poker Game - In Progress");
    }
    
    private void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(settingsManager, themeManager);
        settingsDialog.showAndWait().ifPresent(result -> {
            // Apply any changed settings
            themeManager.applyTheme(mainScene, settingsManager.getCurrentTheme());
        });
    }
    
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Poker Game");
        alert.setHeaderText("Poker Game - Modern Edition");
        alert.setContentText("A modern, cross-platform poker game with touch support,\n" +
                           "animations, and customizable themes.\n\n" +
                           "Built with JavaFX for Windows, Linux, and Android support.\n" +
                           "Features mouse, touch, and gamepad controls.");
        alert.showAndWait();
    }
    
    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Launch the JavaFX application.
     */
    public static void launchApp(String[] args) {
        launch(args);
    }
}