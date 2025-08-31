package com.pokermon.bridge;

import java.util.List;

public class QuickCardTest {
    public static void main(String[] args) {
        System.out.println("Testing card display format...");
        
        GameLogicBridge bridge = new GameLogicBridge();
        boolean success = bridge.initializeGame("TestPlayer", 2, 1000);
        
        if (success) {
            List<String> cards = bridge.getPlayerHand();
            System.out.println("Sample cards: " + cards);
            System.out.println("Card format verification:");
            for (String card : cards) {
                System.out.println("  " + card + " (length: " + card.length() + ")");
            }
            
            // Test round completion
            System.out.println("\nRound completion test:");
            System.out.println("  isRoundComplete before bets: " + bridge.isRoundComplete());
            
            // Make a call
            var result = bridge.performCall();
            System.out.println("  Call result: " + result.getMessage());
            System.out.println("  isRoundComplete after call: " + bridge.isRoundComplete());
            
        } else {
            System.out.println("Failed to initialize game");
        }
    }
}