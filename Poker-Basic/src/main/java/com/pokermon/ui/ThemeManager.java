package com.pokermon.ui;

import javafx.scene.Scene;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages visual themes for the poker game including CSS styling,
 * colors, and visual effects.
 */
public class ThemeManager {
    
    private final Map<String, String> themes;
    
    public ThemeManager() {
        themes = new HashMap<>();
        initializeThemes();
    }
    
    private void initializeThemes() {
        // Default theme CSS
        themes.put("default", """
            .root {
                -fx-background-color: linear-gradient(to bottom, #2e7d32, #1b5e20);
                -fx-font-family: 'Arial';
            }
            
            .title {
                -fx-text-fill: gold;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);
            }
            
            .subtitle {
                -fx-text-fill: lightgray;
            }
            
            .menu-button {
                -fx-background-color: linear-gradient(to bottom, #4caf50, #388e3c);
                -fx-text-fill: white;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #2e7d32;
                -fx-border-width: 2;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 1, 1);
            }
            
            .menu-button:hover {
                -fx-background-color: linear-gradient(to bottom, #66bb6a, #4caf50);
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);
            }
            
            .menu-button:pressed {
                -fx-background-color: linear-gradient(to bottom, #388e3c, #2e7d32);
            }
            
            .card {
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #333;
                -fx-border-width: 1;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 1, 1);
            }
            
            .card:hover {
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
            }
            
            .poker-table {
                -fx-background-color: radial-gradient(center 50% 50%, radius 70%, #2e7d32, #1b5e20);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: #8d6e63;
                -fx-border-width: 5;
            }
            
            .player-area {
                -fx-background-color: rgba(76, 175, 80, 0.3);
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #4caf50;
                -fx-border-width: 2;
            }
            
            .pot-area {
                -fx-background-color: rgba(255, 193, 7, 0.8);
                -fx-background-radius: 15;
                -fx-border-radius: 15;
                -fx-border-color: #ff8f00;
                -fx-border-width: 3;
            }
            """);
        
        // Dark theme
        themes.put("dark", """
            .root {
                -fx-background-color: linear-gradient(to bottom, #263238, #37474f);
                -fx-font-family: 'Arial';
            }
            
            .title {
                -fx-text-fill: #81c784;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);
            }
            
            .subtitle {
                -fx-text-fill: #b0bec5;
            }
            
            .menu-button {
                -fx-background-color: linear-gradient(to bottom, #455a64, #37474f);
                -fx-text-fill: white;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #546e7a;
                -fx-border-width: 2;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 1, 1);
            }
            
            .menu-button:hover {
                -fx-background-color: linear-gradient(to bottom, #546e7a, #455a64);
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 5, 0, 2, 2);
            }
            
            .poker-table {
                -fx-background-color: radial-gradient(center 50% 50%, radius 70%, #37474f, #263238);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: #546e7a;
                -fx-border-width: 5;
            }
            """);
        
        // Luxury theme
        themes.put("luxury", """
            .root {
                -fx-background-color: linear-gradient(to bottom, #3e2723, #1c0a00);
                -fx-font-family: 'Arial';
            }
            
            .title {
                -fx-text-fill: gold;
                -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 8, 0, 3, 3);
            }
            
            .subtitle {
                -fx-text-fill: #d7ccc8;
            }
            
            .menu-button {
                -fx-background-color: linear-gradient(to bottom, #8d6e63, #5d4037);
                -fx-text-fill: gold;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: gold;
                -fx-border-width: 2;
                -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.3), 5, 0, 2, 2);
            }
            
            .menu-button:hover {
                -fx-background-color: linear-gradient(to bottom, #a1887f, #8d6e63);
                -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.6), 8, 0, 3, 3);
            }
            
            .poker-table {
                -fx-background-color: radial-gradient(center 50% 50%, radius 70%, #5d4037, #3e2723);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: gold;
                -fx-border-width: 5;
            }
            """);
    }
    
    /**
     * Apply a theme to a scene.
     * @param scene the scene to apply the theme to
     * @param themeName the name of the theme to apply
     */
    public void applyTheme(Scene scene, String themeName) {
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Add the main CSS file
        String cssPath = getClass().getResource("/css/modern-poker.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        
        // Apply theme-specific inline styles
        String css = themes.get(themeName);
        if (css != null) {
            scene.getRoot().setStyle(css);
        }
    }
    
    /**
     * Get all available theme names.
     * @return array of theme names
     */
    public String[] getAvailableThemes() {
        return themes.keySet().toArray(new String[0]);
    }
    
    /**
     * Check if a theme exists.
     * @param themeName the theme name to check
     * @return true if the theme exists
     */
    public boolean hasTheme(String themeName) {
        return themes.containsKey(themeName);
    }
}