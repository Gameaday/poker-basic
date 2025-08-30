package com.pokermon.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game view for the poker game.
 * Provides a modern, responsive interface for gameplay with animations and touch support.
 */
public class GameView {
    
    private final GameSettingsManager settingsManager;
    private final ThemeManager themeManager;
    private final BorderPane root;
    
    // Game state
    private int currentPot = 0;
    private int playerChips = 1000;
    private String[] playerHand = {"Ace of Spades", "King of Hearts", "Queen of Diamonds", "Jack of Clubs", "Ten of Spades"};
    
    // UI components
    private Label potLabel;
    private VBox playerArea;
    private HBox playerHandArea;
    private Button dealButton;
    private Button foldButton;
    private Button callButton;
    private Button raiseButton;
    private VBox actionArea;
    private List<CardView> handCards;
    
    public GameView(GameSettingsManager settingsManager, ThemeManager themeManager) {
        this.settingsManager = settingsManager;
        this.themeManager = themeManager;
        this.root = new BorderPane();
        this.handCards = new ArrayList<>();
        this.playerChips = settingsManager.getInitialChips();
        
        createUI();
        startNewGame();
    }
    
    private void createUI() {
        root.getStyleClass().add("poker-table");
        root.setPadding(new Insets(20));
        
        // Create top area with pot and game info
        createTopArea();
        
        // Create center area with opponents
        createCenterArea();
        
        // Create bottom area with player hand and controls
        createBottomArea();
        
        // Create right area with action buttons
        createRightArea();
    }
    
    private void createTopArea() {
        VBox topArea = new VBox(10);
        topArea.setAlignment(Pos.CENTER);
        topArea.getStyleClass().add("pot-area");
        topArea.setPadding(new Insets(10));
        
        Label gameTitle = new Label("Poker Game");
        gameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        potLabel = new Label("Pot: $0");
        potLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        potLabel.getStyleClass().add("pot-label");
        
        topArea.getChildren().addAll(gameTitle, potLabel);
        root.setTop(topArea);
    }
    
    private void createCenterArea() {
        GridPane centerArea = new GridPane();
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setHgap(20);
        centerArea.setVgap(20);
        centerArea.setPadding(new Insets(20));
        
        // Add opponent players in a circle around the table
        for (int i = 1; i <= settingsManager.getPlayerCount(); i++) {
            VBox opponentArea = createOpponentArea("CPU " + i, settingsManager.getInitialChips());
            
            // Position opponents around the table
            int col = (i - 1) % 3;
            int row = (i - 1) / 3;
            centerArea.add(opponentArea, col, row);
        }
        
        root.setCenter(centerArea);
    }
    
    private VBox createOpponentArea(String playerName, int chips) {
        VBox area = new VBox(5);
        area.setAlignment(Pos.CENTER);
        area.getStyleClass().add("player-area");
        area.setPadding(new Insets(10));
        area.setPrefWidth(150);
        
        Label nameLabel = new Label(playerName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label chipsLabel = new Label("Chips: $" + chips);
        chipsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        
        // Card back images for opponent's hand
        HBox opponentCards = new HBox(2);
        opponentCards.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            ImageView cardBack = createCardBack();
            cardBack.setVisible(false);
            opponentCards.getChildren().add(cardBack);
        }
        
        area.getChildren().addAll(nameLabel, chipsLabel, opponentCards);
        return area;
    }
    
    private void createBottomArea() {
        VBox bottomArea = new VBox(10);
        bottomArea.setAlignment(Pos.CENTER);
        bottomArea.setPadding(new Insets(20));
        
        // Player info
        Label playerNameLabel = new Label("Your Hand");
        playerNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label playerChipsLabel = new Label("Chips: $" + playerChips);
        playerChipsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        // Player hand area
        playerHandArea = new HBox(5);
        playerHandArea.setAlignment(Pos.CENTER);
        playerHandArea.setPrefHeight(140);
        
        bottomArea.getChildren().addAll(playerNameLabel, playerChipsLabel, playerHandArea);
        root.setBottom(bottomArea);
    }
    
    private void createRightArea() {
        actionArea = new VBox(10);
        actionArea.setAlignment(Pos.CENTER);
        actionArea.setPadding(new Insets(20));
        actionArea.setPrefWidth(120);
        
        // Game action buttons
        dealButton = createActionButton("Deal", this::dealCards);
        foldButton = createActionButton("Fold", this::fold);
        callButton = createActionButton("Call", this::call);
        raiseButton = createActionButton("Raise", this::raise);
        
        // Additional controls
        Button settingsButton = createActionButton("Settings", this::showSettings);
        Button menuButton = createActionButton("Menu", this::returnToMenu);
        
        actionArea.getChildren().addAll(
            dealButton,
            new Separator(),
            foldButton,
            callButton,
            raiseButton,
            new Separator(),
            settingsButton,
            menuButton
        );
        
        root.setRight(actionArea);
    }
    
    private Button createActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(100);
        button.setPrefHeight(40);
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        button.getStyleClass().add("action-button");
        button.setOnAction(e -> action.run());
        return button;
    }
    
    private void startNewGame() {
        // Enable deal button, disable others
        dealButton.setDisable(false);
        foldButton.setDisable(true);
        callButton.setDisable(true);
        raiseButton.setDisable(true);
        
        // Update UI
        updatePotDisplay();
        updatePlayerChips();
    }
    
    private void dealCards() {
        // Simulate dealing cards
        playerHand = new String[]{"Ace of Spades", "King of Hearts", "Queen of Diamonds", "Jack of Clubs", "Ten of Spades"};
        
        // Show player's hand
        displayPlayerHand();
        
        // Enable game buttons
        dealButton.setDisable(true);
        foldButton.setDisable(false);
        callButton.setDisable(false);
        raiseButton.setDisable(false);
        
        // Animate dealing
        animateDealing();
    }
    
    private void displayPlayerHand() {
        playerHandArea.getChildren().clear();
        handCards.clear();
        
        for (int i = 0; i < playerHand.length; i++) {
            CardView cardView = new CardView(playerHand[i], i);
            cardView.setOnCardClicked(this::onCardClicked);
            handCards.add(cardView);
            playerHandArea.getChildren().add(cardView.getNode());
        }
    }
    
    private void onCardClicked(int cardIndex) {
        if (handCards.size() > cardIndex) {
            CardView cardView = handCards.get(cardIndex);
            cardView.toggleSelection();
        }
    }
    
    private void animateDealing() {
        if (!settingsManager.isAnimationsEnabled()) {
            return;
        }
        
        // Animate cards being dealt with staggered timing
        for (int i = 0; i < handCards.size(); i++) {
            CardView cardView = handCards.get(i);
            Node cardNode = cardView.getNode();
            
            // Start with cards off-screen
            cardNode.setTranslateY(200);
            cardNode.setOpacity(0);
            
            // Create animation with delay for each card
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(i * 100), 
                    new KeyValue(cardNode.translateYProperty(), 200),
                    new KeyValue(cardNode.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(i * 100 + 500),
                    new KeyValue(cardNode.translateYProperty(), 0),
                    new KeyValue(cardNode.opacityProperty(), 1)
                )
            );
            timeline.play();
        }
    }
    
    private ImageView createCardBack() {
        // Create a placeholder card back image
        ImageView cardBack = new ImageView();
        cardBack.setFitWidth(60);
        cardBack.setFitHeight(84);
        cardBack.getStyleClass().add("card-back");
        return cardBack;
    }
    
    private void fold() {
        // Simulate fold action
        showMessage("You folded!");
        endRound();
    }
    
    private void call() {
        // Simulate call action
        int callAmount = 50; // Example call amount
        playerChips -= callAmount;
        currentPot += callAmount;
        updatePotDisplay();
        updatePlayerChips();
        showMessage("You called $" + callAmount);
    }
    
    private void raise() {
        // Show raise dialog
        TextInputDialog dialog = new TextInputDialog("100");
        dialog.setTitle("Raise");
        dialog.setHeaderText("Enter raise amount:");
        dialog.setContentText("Amount:");
        
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount > 0 && amount <= playerChips) {
                    // Simulate raise action
                    playerChips -= amount;
                    currentPot += amount;
                    updatePotDisplay();
                    updatePlayerChips();
                    showMessage("You raised $" + amount);
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount entered.");
            }
        });
    }
    
    private void endRound() {
        // Disable action buttons
        foldButton.setDisable(true);
        callButton.setDisable(true);
        raiseButton.setDisable(true);
        
        // Enable deal for next round
        dealButton.setDisable(false);
    }
    
    private void updatePotDisplay() {
        potLabel.setText("Pot: $" + currentPot);
    }
    
    private void updatePlayerChips() {
        // This would update the player chips display in the UI
        // For now, just update the internal state
    }
    
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Action");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(settingsManager, themeManager);
        settingsDialog.showAndWait();
    }
    
    private void returnToMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Return to Menu");
        alert.setHeaderText("Return to Main Menu?");
        alert.setContentText("Your current game will be lost.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // This would trigger a scene change back to main menu
                System.out.println("Returning to main menu...");
            }
        });
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Game Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public BorderPane getRoot() {
        return root;
    }
}