package com.pokermon.ui;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.util.function.Consumer;

/**
 * Modern card view component with touch-friendly interactions and animations.
 * Handles card display, selection, and click events.
 */
public class CardView {
    
    private final StackPane cardNode;
    private final ImageView cardImage;
    private final String cardName;
    private final int cardIndex;
    private boolean selected = false;
    private Consumer<Integer> onCardClicked;
    
    public CardView(String cardName, int cardIndex) {
        this.cardName = cardName;
        this.cardIndex = cardIndex;
        this.cardNode = new StackPane();
        this.cardImage = new ImageView();
        
        setupCard();
        loadCardImage();
        setupInteractions();
    }
    
    private void setupCard() {
        cardNode.getStyleClass().add("card");
        cardNode.setPrefSize(90, 126);
        cardNode.setMaxSize(90, 126);
        cardNode.setMinSize(90, 126);
        
        // Setup image view
        cardImage.setFitWidth(88);
        cardImage.setFitHeight(124);
        cardImage.setPreserveRatio(true);
        cardImage.setSmooth(true);
        
        cardNode.getChildren().add(cardImage);
    }
    
    private void loadCardImage() {
        try {
            // Try to load the card image from resources
            String imagePath = "/Cards/TET/" + cardName + ".jpg";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            
            if (image.isError()) {
                // Fallback to text display if image not found
                createTextCard();
            } else {
                cardImage.setImage(image);
            }
        } catch (Exception e) {
            // Fallback to text display
            createTextCard();
        }
    }
    
    private void createTextCard() {
        // Create a text-based card display as fallback
        cardNode.getChildren().clear();
        
        Label cardLabel = new Label(formatCardName(cardName));
        cardLabel.getStyleClass().add("card-text");
        cardLabel.setWrapText(true);
        cardLabel.setMaxWidth(80);
        
        cardNode.getChildren().add(cardLabel);
        cardNode.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;");
    }
    
    private String formatCardName(String cardName) {
        // Convert card names like "Ace of Spades" to "A♠"
        String result = cardName;
        
        // Handle ranks
        result = result.replace("Ace", "A");
        result = result.replace("King", "K");
        result = result.replace("Queen", "Q");
        result = result.replace("Jack", "J");
        result = result.replace("Ten", "10");
        result = result.replace("Nine", "9");
        result = result.replace("Eight", "8");
        result = result.replace("Seven", "7");
        result = result.replace("Six", "6");
        result = result.replace("Five", "5");
        result = result.replace("Four", "4");
        result = result.replace("Three", "3");
        result = result.replace("Two", "2");
        
        // Handle suits
        result = result.replace(" of Spades", "♠");
        result = result.replace(" of Hearts", "♥");
        result = result.replace(" of Diamonds", "♦");
        result = result.replace(" of Clubs", "♣");
        
        return result;
    }
    
    private void setupInteractions() {
        // Mouse and touch interactions
        cardNode.setOnMouseClicked(e -> handleClick());
        cardNode.setOnTouchPressed(e -> handleClick());
        
        // Hover effects
        cardNode.setOnMouseEntered(e -> handleHover(true));
        cardNode.setOnMouseExited(e -> handleHover(false));
        
        // Touch feedback
        cardNode.setOnTouchPressed(e -> handleHover(true));
        cardNode.setOnTouchReleased(e -> handleHover(false));
    }
    
    private void handleClick() {
        if (onCardClicked != null) {
            onCardClicked.accept(cardIndex);
        }
        
        // Add click animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), cardNode);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.95);
        scaleTransition.setToY(0.95);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }
    
    private void handleHover(boolean hovering) {
        if (hovering && !selected) {
            cardNode.setStyle(cardNode.getStyle() + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);");
            
            // Subtle scale animation
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), cardNode);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        } else if (!hovering && !selected) {
            cardNode.setStyle(cardNode.getStyle().replace("; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);", ""));
            
            // Return to normal scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), cardNode);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        }
    }
    
    public void toggleSelection() {
        selected = !selected;
        updateSelectionVisual();
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectionVisual();
    }
    
    private void updateSelectionVisual() {
        if (selected) {
            cardNode.setStyle(cardNode.getStyle() + "; -fx-border-color: gold; -fx-border-width: 3; " +
                           "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 8, 0, 0, 0);");
            cardNode.setTranslateY(-10); // Lift selected card
        } else {
            cardNode.setStyle(cardNode.getStyle()
                .replace("; -fx-border-color: gold; -fx-border-width: 3; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 8, 0, 0, 0);", ""));
            cardNode.setTranslateY(0); // Return to normal position
        }
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public String getCardName() {
        return cardName;
    }
    
    public int getCardIndex() {
        return cardIndex;
    }
    
    public Node getNode() {
        return cardNode;
    }
    
    public void setOnCardClicked(Consumer<Integer> onCardClicked) {
        this.onCardClicked = onCardClicked;
    }
}