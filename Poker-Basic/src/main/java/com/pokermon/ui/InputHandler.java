package com.pokermon.ui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles input events for mouse, touch, and gamepad controls.
 * Provides platform-agnostic input handling for better accessibility.
 */
public class InputHandler {
    
    private final GameSettingsManager settingsManager;
    private Scene scene;
    
    public InputHandler(GameSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
    
    /**
     * Attach input handlers to a scene.
     * @param scene the scene to attach handlers to
     */
    public void attachToScene(Scene scene) {
        this.scene = scene;
        
        if (settingsManager.isKeyboardShortcutsEnabled()) {
            setupKeyboardShortcuts();
        }
        
        if (settingsManager.isTouchEnabled()) {
            setupTouchHandling();
        }
        
        if (settingsManager.isGamepadEnabled()) {
            setupGamepadHandling();
        }
    }
    
    private void setupKeyboardShortcuts() {
        scene.setOnKeyPressed(this::handleKeyPressed);
    }
    
    private void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        
        switch (code) {
            case ESCAPE:
                // Close dialogs or return to menu
                handleEscape();
                break;
                
            case SPACE:
                // Deal cards or confirm action
                handleSpace();
                break;
                
            case ENTER:
                // Confirm current action
                handleEnter();
                break;
                
            case F:
                // Fold (if in game)
                handleFold();
                break;
                
            case C:
                // Call (if in game)
                handleCall();
                break;
                
            case R:
                // Raise (if in game)
                handleRaise();
                break;
                
            case S:
                // Settings
                handleSettings();
                break;
                
            case DIGIT1, DIGIT2, DIGIT3, DIGIT4, DIGIT5:
                // Select cards 1-5
                int cardIndex = code.ordinal() - KeyCode.DIGIT1.ordinal();
                handleCardSelection(cardIndex);
                break;
                
            case F11:
                // Toggle fullscreen
                handleFullscreenToggle();
                break;
                
            default:
                // Ignore other keys
                break;
        }
        
        event.consume();
    }
    
    private void setupTouchHandling() {
        // Add touch-specific event handlers
        scene.getRoot().setOnTouchPressed(event -> {
            // Handle touch press events
            // This provides feedback for touch interactions
            double x = event.getTouchPoint().getX();
            double y = event.getTouchPoint().getY();
            handleTouchPress(x, y);
        });
        
        scene.getRoot().setOnTouchReleased(event -> {
            // Handle touch release events
            double x = event.getTouchPoint().getX();
            double y = event.getTouchPoint().getY();
            handleTouchRelease(x, y);
        });
    }
    
    private void setupGamepadHandling() {
        // Note: JavaFX doesn't have built-in gamepad support
        // This would typically require additional libraries like JInput or SDL
        // For now, we'll simulate gamepad support with keyboard mapping
        
        scene.setOnKeyPressed(event -> {
            if (settingsManager.isGamepadEnabled()) {
                handleGamepadInput(event);
            }
        });
    }
    
    private void handleGamepadInput(KeyEvent event) {
        // Map common gamepad inputs to keyboard equivalents
        KeyCode code = event.getCode();
        
        switch (code) {
            case UP, W:
                // Navigate up
                handleNavigateUp();
                break;
                
            case DOWN, S:
                // Navigate down
                handleNavigateDown();
                break;
                
            case LEFT, A:
                // Navigate left
                handleNavigateLeft();
                break;
                
            case RIGHT, D:
                // Navigate right
                handleNavigateRight();
                break;
                
            // Xbox controller equivalents
            case J: // A button
                handleGamepadA();
                break;
                
            case K: // B button
                handleGamepadB();
                break;
                
            case L: // X button
                handleGamepadX();
                break;
                
            case SEMICOLON: // Y button
                handleGamepadY();
                break;
                
            default:
                break;
        }
    }
    
    // Event handler methods (to be implemented by specific views)
    protected void handleEscape() {
        System.out.println("Escape pressed");
    }
    
    protected void handleSpace() {
        System.out.println("Space pressed");
    }
    
    protected void handleEnter() {
        System.out.println("Enter pressed");
    }
    
    protected void handleFold() {
        System.out.println("Fold shortcut pressed");
    }
    
    protected void handleCall() {
        System.out.println("Call shortcut pressed");
    }
    
    protected void handleRaise() {
        System.out.println("Raise shortcut pressed");
    }
    
    protected void handleSettings() {
        System.out.println("Settings shortcut pressed");
    }
    
    protected void handleCardSelection(int cardIndex) {
        System.out.println("Card " + (cardIndex + 1) + " selected");
    }
    
    protected void handleFullscreenToggle() {
        System.out.println("Fullscreen toggle pressed");
    }
    
    protected void handleTouchPress(double x, double y) {
        System.out.println("Touch press at: " + x + ", " + y);
    }
    
    protected void handleTouchRelease(double x, double y) {
        System.out.println("Touch release at: " + x + ", " + y);
    }
    
    protected void handleNavigateUp() {
        System.out.println("Navigate up");
    }
    
    protected void handleNavigateDown() {
        System.out.println("Navigate down");
    }
    
    protected void handleNavigateLeft() {
        System.out.println("Navigate left");
    }
    
    protected void handleNavigateRight() {
        System.out.println("Navigate right");
    }
    
    protected void handleGamepadA() {
        System.out.println("Gamepad A pressed");
    }
    
    protected void handleGamepadB() {
        System.out.println("Gamepad B pressed");
    }
    
    protected void handleGamepadX() {
        System.out.println("Gamepad X pressed");
    }
    
    protected void handleGamepadY() {
        System.out.println("Gamepad Y pressed");
    }
}