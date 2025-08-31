package com.pokermon;

import java.io.*;
import java.util.*;

/**
 * Console-only version of the poker game.
 * This class provides a pure text-based interface without any GUI dependencies.
 * 
 * @author Poker Game Team
 * @version 1.0.0
 */
public class ConsoleMain {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("         POKER GAME - CONSOLE MODE");
        System.out.println("=================================================");
        System.out.println();
        
        // Variables
        int workingPot = 0, topBet = 0, countup = 0;
        boolean Continue = true;
        boolean Quit = false;
        String[] players;
        Player USER = null, CPU1 = null, CPU2 = null, CPU3 = null;
        Player[] list;
        
        // Print author information
        Main.author();
        
        // Prompt user for a name
        String playerName = promptName();
        
        // Prompt for number of players to play against
        int playerCount = promptChallengers();
        
        // Prompt for starting chip quantity
        int chipsInitial = promptChips();
        
        // Set stuff up
        players = new String[playerCount + 1];
        list = new Player[players.length];
        
        // Add your name to list
        players[0] = playerName;
        
        // Decide names for the Computer players
        Main.decideNames(players);
        Main.setupList(list, USER, CPU1, CPU2, CPU3);
        
        while (Continue) {
            // Initialize the Deck
            int[] Deck = Main.setDeck();
            if (countup < 1) {
                // Initialize players with hands, names, and chips
                Main.InitializePlayers(list, players, chipsInitial, Deck);
                countup = 1;
            } else {
                Main.InitializePlayers(list, players, Deck);
            }
            
            // Show player his hand
            revealHand(list[0].getConvertedHand());
            
            // Have player bet
            workingPot = bet(list, workingPot);
            
            // Report current pot value
            System.out.println("Current Pot Value: " + workingPot);
            
            // Have player exchange cards
            Exchange(list[0], Deck);
            
            // Report new hand
            revealHand(list[0].getConvertedHand());
            
            // Have player bet again
            workingPot = bet(list, workingPot);
            
            // Report current pot value
            System.out.println("Current Pot Value: " + workingPot);
            
            // Declare results
            declareResults(list);
            
            // Divide the pot between the winner(s)
            Main.dividePot(list, workingPot);
            
            // Save updated stats to file
            Main.playersStats(list);
            
            Continue = promptEnd();
        }
        
        System.out.println("\nThank you for playing!");
        scanner.close();
    }
    
    /**
     * Console prompt for player name.
     */
    private static String promptName() {
        System.out.print("Enter a name for your player [A(n) Drew Hussie]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? "A(n) Drew Hussie" : input;
    }
    
    /**
     * Console prompt for number of opponents.
     */
    private static int promptChallengers() {
        System.out.print("How many computer players will you play against? (1-3) [2]: ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return 2;
            int count = Integer.parseInt(input);
            if (count >= 1 && count <= 3) {
                return count;
            } else {
                System.out.println("Invalid input. Using default: 2");
                return 2;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default: 2");
            return 2;
        }
    }
    
    /**
     * Console prompt for starting chips.
     */
    private static int promptChips() {
        System.out.print("Select starting chip quantity (100, 500, 1000, 2000) [500]: ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return 500;
            int chips = Integer.parseInt(input);
            int[] validChips = {100, 500, 1000, 2000};
            for (int valid : validChips) {
                if (chips == valid) return chips;
            }
            System.out.println("Invalid input. Using default: 500");
            return 500;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default: 500");
            return 500;
        }
    }
    
    /**
     * Console display of player's hand.
     */
    private static void revealHand(String[] Hand) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("YOUR HAND:");
        for (int i = 0; i < Hand.length; i++) {
            System.out.println((i + 1) + ": " + Hand[i]);
        }
        System.out.println("=".repeat(40) + "\n");
    }
    
    /**
     * Console betting interface.
     */
    private static int bet(Player[] list, int workingPot) {
        System.out.println("Your chips: " + list[0].getChips());
        System.out.print("How much will you bet? [0]: ");
        
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return workingPot;
            
            int betAmount = Integer.parseInt(input);
            if (betAmount < 0) {
                System.out.println("Invalid bet amount. Betting 0.");
                return workingPot;
            }
            
            if (betAmount > list[0].getChips()) {
                System.out.println("You don't have enough chips. Betting all your chips: " + list[0].getChips());
                betAmount = list[0].getChips();
            }
            
            list[0].placeBet(betAmount);
            return workingPot + betAmount;
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Betting 0.");
            return workingPot;
        }
    }
    
    /**
     * Console card exchange interface.
     */
    private static void Exchange(Player player, int[] Deck) {
        System.out.println("\nCard Exchange Phase");
        System.out.print("How many cards will you exchange? (0-5) [0]: ");
        
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return;
            
            int numToExchange = Integer.parseInt(input);
            if (numToExchange < 0 || numToExchange > 5) {
                System.out.println("Invalid number. No cards exchanged.");
                return;
            }
            
            if (numToExchange == 0) return;
            
            System.out.println("Enter the positions (1-5) of cards to exchange, separated by spaces:");
            String positionsInput = scanner.nextLine().trim();
            
            if (positionsInput.isEmpty()) return;
            
            String[] positions = positionsInput.split("\\s+");
            Set<Integer> exchangePositions = new HashSet<>();
            
            for (String pos : positions) {
                try {
                    int position = Integer.parseInt(pos) - 1; // Convert to 0-based index
                    if (position >= 0 && position < 5) {
                        exchangePositions.add(position);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid positions
                }
            }
            
            if (exchangePositions.size() != numToExchange) {
                System.out.println("Number of positions doesn't match requested exchange count. No cards exchanged.");
                return;
            }
            
            // Exchange cards (simplified version)
            int[] hand = player.getHandForModification();
            for (int position : exchangePositions) {
                hand[position] = Main.drawCard(Deck);
            }
            
            player.updateHand(hand);
            player.convertHand();
            
            System.out.println("Cards exchanged successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No cards exchanged.");
        }
    }
    
    /**
     * Console prompt to continue playing.
     */
    private static boolean promptEnd() {
        System.out.print("\nWould you like to play again? (y/n) [y]: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.isEmpty() || input.equals("y") || input.equals("yes");
    }
    
    /**
     * Console version of declare results without GUI dependencies.
     */
    private static boolean declareResults(Player[] list) {
        // Find the winner using existing game logic
        int winnerIndex = -1;
        int highestValue = 0;
        int winnerCount = 0;
        
        // Calculate hand values and find winner(s)
        for (int i = 0; i < list.length; i++) {
            list[i].calculateHandValue();
            int value = list[i].getHandValue();
            
            if (value > highestValue) {
                highestValue = value;
                winnerIndex = i;
                winnerCount = 1;
            } else if (value == highestValue) {
                winnerCount++;
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                GAME RESULTS");
        System.out.println("=".repeat(50));
        
        // Display all players' hands and values
        for (int i = 0; i < list.length; i++) {
            String[] hand = list[i].getConvertedHand();
            System.out.println(list[i].getName() + " (Hand Value: " + list[i].getHandValue() + "):");
            for (String card : hand) {
                System.out.println("  " + card);
            }
            System.out.println();
        }
        
        if (winnerCount == 1) {
            if (winnerIndex == 0) {
                System.out.println("🎉 CONGRATULATIONS! You won the hand! 🎉");
                return false;
            } else {
                System.out.println("😞 You lost the hand. Better luck next time!");
                System.out.println("Winner: " + list[winnerIndex].getName());
                return true;
            }
        } else {
            System.out.println("🤝 The game was a tie! Multiple players had the same hand value.");
            return false;
        }
    }
}