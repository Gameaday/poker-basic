package com.pokermon;

import java.util.*;

/**
 * Console-only version of the Pokermon game.
 * This class provides a pure text-based interface without any GUI dependencies.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class ConsoleMain {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("         " + Version.APP_NAME.toUpperCase() + " - CONSOLE MODE");
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
        
        // Prompt for game mode selection
        GameMode selectedMode = promptGameMode();
        
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
        
        // Check if monster mode is selected and handle accordingly
        if (selectedMode.hasMonsters()) {
            handleMonsterMode(selectedMode, playerName, playerCount, chipsInitial);
            return;
        }
        
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
    
    /**
     * Console prompt for game mode selection.
     */
    private static GameMode promptGameMode() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                GAME MODE SELECTION");
        System.out.println("=".repeat(50));
        System.out.println("Choose your game mode:");
        System.out.println("1. Classic Poker - Traditional 5-card draw with betting");
        System.out.println("2. Adventure Mode - Battle monsters with poker combat");
        System.out.println("3. Safari Mode - Capture monsters through poker gameplay");
        System.out.println("4. Ironman Mode - Convert winnings to monster gacha pulls");
        System.out.println();
        System.out.print("Enter your choice (1-4) [1]: ");
        
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return GameMode.CLASSIC;
            
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1: return GameMode.CLASSIC;
                case 2: return GameMode.ADVENTURE;
                case 3: return GameMode.SAFARI;
                case 4: return GameMode.IRONMAN;
                default:
                    System.out.println("Invalid choice. Using default: Classic Poker");
                    return GameMode.CLASSIC;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default: Classic Poker");
            return GameMode.CLASSIC;
        }
    }
    
    /**
     * Handles monster-based game modes.
     */
    private static void handleMonsterMode(GameMode mode, String playerName, int playerCount, int chipsInitial) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("          " + mode.getDisplayName().toUpperCase());
        System.out.println("=".repeat(50));
        System.out.println(mode.getDescription());
        System.out.println();
        
        switch (mode) {
            case ADVENTURE:
                System.out.println("🏔️ Welcome to Adventure Mode, " + playerName + "!");
                System.out.println("You'll battle monsters whose health equals their poker chip count.");
                System.out.println("Use your poker skills to defeat enemies and earn rewards!");
                System.out.println();
                launchAdventureMode(playerName, playerCount, chipsInitial);
                break;
                
            case SAFARI:
                System.out.println("🌿 Welcome to Safari Mode, " + playerName + "!");
                System.out.println("Encounter wild monsters during poker games.");
                System.out.println("Better hands increase your capture probability!");
                System.out.println();
                launchSafariMode(playerName, playerCount, chipsInitial);
                break;
                
            case IRONMAN:
                System.out.println("🎰 Welcome to Ironman Mode, " + playerName + "!");
                System.out.println("Convert your poker winnings into monster gacha pulls.");
                System.out.println("Higher winnings increase your chances of rare monsters!");
                System.out.println();
                launchIronmanMode(playerName, playerCount, chipsInitial);
                break;
                
            default:
                System.out.println("Unknown game mode. Returning to classic poker.");
                return;
        }
    }
    
    /**
     * Adventure Mode implementation - Monster battles using poker combat.
     */
    private static void launchAdventureMode(String playerName, int playerCount, int chipsInitial) {
        System.out.println("🏔️ Welcome to Adventure Mode, " + playerName + "!");
        System.out.println("Battle monsters using poker hand strength as your weapon!");
        System.out.println();
        
        AdventureMode adventure = new AdventureMode(playerName, chipsInitial);
        adventure.startAdventure();
    }
    
    /**
     * Safari Mode implementation - Monster capture during poker games.
     */
    private static void launchSafariMode(String playerName, int playerCount, int chipsInitial) {
        System.out.println("🚧 BETA FEATURE - Safari Mode");
        System.out.println();
        System.out.println("Safari Mode is currently under development for the beta milestone.");
        System.out.println("This mode will feature:");
        System.out.println("  • Random monster encounters during poker games");
        System.out.println("  • Capture probability based on poker performance");
        System.out.println("  • Monster collection and rarity distribution");
        System.out.println("  • Habitat variety and special encounter events");
        System.out.println();
        System.out.println("Implementation Status: Encounter system design complete");
        System.out.println("Expected in: Beta Milestone 1.2");
        System.out.println();
        System.out.print("Press Enter to continue with Classic Poker for now...");
        scanner.nextLine();
        
        // For now, fall back to classic poker with a note
        System.out.println("\nFalling back to Classic Poker...");
        // Re-launch classic mode
        main(new String[0]);
    }
    
    /**
     * Ironman Mode implementation - Gacha system using poker winnings.
     */
    private static void launchIronmanMode(String playerName, int playerCount, int chipsInitial) {
        System.out.println("🚧 BETA FEATURE - Ironman Mode");
        System.out.println();
        System.out.println("Ironman Mode is currently under development for the beta milestone.");
        System.out.println("This mode will feature:");
        System.out.println("  • Gacha system converting chips to premium currency");
        System.out.println("  • Weighted random monster acquisition");
        System.out.println("  • Leaderboards and high score tracking");
        System.out.println("  • Limited-time events and special monsters");
        System.out.println();
        System.out.println("Implementation Status: Gacha mechanics designed");
        System.out.println("Expected in: Beta Milestone 1.3");
        System.out.println();
        System.out.print("Press Enter to continue with Classic Poker for now...");
        scanner.nextLine();
        
        // For now, fall back to classic poker with a note
        System.out.println("\nFalling back to Classic Poker...");
        // Re-launch classic mode
        main(new String[0]);
    }
}