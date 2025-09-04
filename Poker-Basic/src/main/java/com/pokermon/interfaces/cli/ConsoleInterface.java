package com.pokermon.interfaces.cli;

import com.pokermon.core.Player;
import com.pokermon.Version;
import java.util.*;

/**
 * Console-only version of the Pokermon game.
 * This class provides a pure text-based interface without any GUI dependencies.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class ConsoleInterface {
    
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
        author();

        // Prompt user for a name
        String playerName = promptName();
        // Prompt for number of players to play against:
        int playerCount = promptChallengers();
        // Prompt for starting chip quantity:
        int chipsInitial = promptChips();

        // Set stuff up
        players = new String[playerCount + 1];
        list = new Player[players.length];

        // Add your name to list
        players[0] = playerName;

        // Decide names for the Computer players
        decideNames(players);
        setupList(list, USER, CPU1, CPU2, CPU3);

        while (Continue == true) {
            // Initialize the Deck
            int[] Deck = setDeck();
            if (countup < 1) {
                // Initialize players with hands, names, and chips
                InitializePlayers(list, players, chipsInitial, Deck);
                countup = 1;
            } else {
                InitializePlayers(list, players, Deck);
            }
            // Show player his hand
            revealHand(list[0].getConvertedHand());
            // Have player bet
            workingPot = bet(list, workingPot);
            // Report current pot value
            System.out.println("Current Pot Value: " + workingPot);

            // Card exchange phase
            System.out.println("\n--- Card Exchange Phase ---");
            int[] cardsToExchange = promptCardExchange(list[0]);
            if (cardsToExchange.length > 0) {
                Exchange(list[0], Deck, cardsToExchange);
                list[0].performAllChecks();
                System.out.println("\nYour new hand:");
                revealHand(list[0].getConvertedHand());
            }

            // Second betting round
            System.out.println("\n--- Final Betting Round ---");
            workingPot = bet(list, workingPot);
            System.out.println("Final Pot Value: " + workingPot);

            // Show all hands and determine winner
            System.out.println("\n--- Showdown ---");
            playersStats(list);
            boolean playerWon = declareResults(list);
            
            if (playerWon) {
                dividePot(list, workingPot);
                workingPot = 0;
            } else {
                dividePot(list, workingPot);
                workingPot = 0;
            }

            // Check if any players are out of chips
            boolean gameOver = false;
            for (Player player : list) {
                if (player != null && player.getChips() <= 0) {
                    System.out.println(player.getName() + " is out of chips!");
                    if (player.isHuman()) {
                        System.out.println("Game Over! You're out of chips.");
                        gameOver = true;
                    }
                }
            }
            
            if (gameOver) {
                Continue = false;
            } else {
                Continue = promptContinue();
            }
        }
        
        System.out.println("\nThanks for playing " + Version.APP_NAME + "!");
        System.out.println("Final Results:");
        for (Player player : list) {
            if (player != null) {
                System.out.println(player.getName() + ": " + player.getChips() + " chips");
            }
        }
    }

    private static void author() {
        System.out.println("Created by: Carl Nelson (@Gameaday)");
        System.out.println("Version: " + Version.getValidatedVersion());
        System.out.println();
    }

    private static String promptName() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        return name.isEmpty() ? "Player" : name;
    }

    private static int promptChallengers() {
        System.out.print("How many opponents would you like to play against? (1-3): ");
        try {
            int opponents = Integer.parseInt(scanner.nextLine().trim());
            if (opponents >= 1 && opponents <= 3) {
                return opponents;
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }
        System.out.println("Invalid input. Using default: 2 opponents");
        return 2;
    }

    private static int promptChips() {
        System.out.print("How many chips would you like to start with? (100, 500, 1000, 2500): ");
        try {
            int chips = Integer.parseInt(scanner.nextLine().trim());
            int[] validChips = {100, 500, 1000, 2500};
            for (int valid : validChips) {
                if (chips == valid) {
                    return chips;
                }
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }
        System.out.println("Invalid input. Using default: 500 chips");
        return 500;
    }

    private static int[] promptCardExchange(Player player) {
        System.out.println("\nSelect cards to exchange (0-4), or press Enter to keep all cards:");
        System.out.println("Your current hand:");
        String[] hand = player.getConvertedHand();
        for (int i = 0; i < hand.length; i++) {
            System.out.println((i + 1) + ": " + hand[i]);
        }
        
        System.out.print("Enter card numbers to exchange (e.g., '1 3 5' or just Enter): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return new int[0]; // No cards to exchange
        }
        
        try {
            String[] parts = input.split("\\s+");
            List<Integer> exchanges = new ArrayList<>();
            
            for (String part : parts) {
                int cardNum = Integer.parseInt(part);
                if (cardNum >= 1 && cardNum <= 5) {
                    exchanges.add(cardNum - 1); // Convert to 0-based index
                }
            }
            
            return exchanges.stream().mapToInt(i -> i).toArray();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Keeping all cards.");
            return new int[0];
        }
    }

    private static boolean promptContinue() {
        System.out.print("\nWould you like to play another round? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.startsWith("y");
    }

    // Game utility methods (adapted from original Main class)
    
    private static void decideNames(String[] players) {
        String[] possibleNames = {
            "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
            "Tony", "Jenny", "Susen", "Rory", "Melody",
            "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
            "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
            "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
            "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
            "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
            "Smallie", "Anthony", "Gwen"
        };
        
        Random random = new Random();
        Set<String> usedNames = new HashSet<>();
        usedNames.add(players[0]); // Add human player name
        
        for (int i = 1; i < players.length; i++) {
            String name;
            do {
                name = possibleNames[random.nextInt(possibleNames.length)];
            } while (usedNames.contains(name));
            
            players[i] = name;
            usedNames.add(name);
        }
    }

    private static void setupList(Player[] list, Player USER, Player CPU1, Player CPU2, Player CPU3) {
        // Initialize all list positions to null first
        for (int i = 0; i < list.length; i++) {
            list[i] = null;
        }
    }

    private static int[] setDeck() {
        int[] deck = new int[52];
        for (int i = 0; i < 52; i++) {
            deck[i] = i;
        }
        return deck;
    }

    private static void InitializePlayers(Player[] list, String[] players, int chipsInitial, int[] deck) {
        for (int i = 0; i < players.length; i++) {
            Player player = new Player();
            player.setHuman(i == 0); // First player (index 0) is human
            player.setupPlayer(players[i], chipsInitial, deck);
            list[i] = player;
        }
    }

    private static void InitializePlayers(Player[] list, String[] players, int[] deck) {
        for (int i = 0; i < players.length; i++) {
            Player player = list[i];
            if (player == null) {
                player = new Player();
                player.setHuman(i == 0); // First player (index 0) is human
                list[i] = player;
            }
            // Keep existing chips, just re-deal cards
            int currentChips = player.getChips();
            player.setupPlayer(players[i], currentChips, deck);
        }
    }

    private static void revealHand(String[] Hand) {
        System.out.println("Your hand is: " + Arrays.toString(Hand));
    }

    private static int[] Exchange(Player current, int[] deck, int[] cardsToExchange) {
        if (cardsToExchange != null && cardsToExchange.length > 0) {
            Random random = new Random();
            boolean[] usedCards = new boolean[deck.length];
            
            // Mark cards in all players' hands as used
            // (This is simplified - in a real game you'd track all dealt cards)
            
            for (int cardIndex : cardsToExchange) {
                if (cardIndex >= 0 && cardIndex < current.getHand().length) {
                    // Find a new card from the deck
                    int newCard;
                    do {
                        newCard = deck[random.nextInt(deck.length)];
                    } while (usedCards[newCard]);
                    
                    // Replace the card
                    current.getHand()[cardIndex] = newCard;
                    usedCards[newCard] = true;
                }
            }
            
            // Update converted hand
            current.performAllChecks();
        }
        return deck;
    }

    private static boolean declareResults(Player[] list) {
        int finish = decideWinner(list);
        if (finish == 0) {
            System.out.println("You have lost the hand, better luck next time");
            return false;
        } else if (finish == 1) {
            System.out.println("You have won the hand! Congratulations!");
            return true;
        } else {
            System.out.println("It's a tie!");
            return false;
        }
    }

    private static int decideWinner(Player[] list) {
        int[] winningScores = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            winningScores[i] = list[i].getHandValue();
        }
        
        int maxScore = Arrays.stream(winningScores).max().orElse(0);
        
        if (winningScores[0] == maxScore) {
            // Check if human player tied for best
            long winners = Arrays.stream(winningScores).filter(score -> score == maxScore).count();
            return winners == 1 ? 1 : 2; // 1 = win, 2 = tie
        }
        return 0; // loss
    }

    private static void dividePot(Player[] list, int pot) {
        int[] winningScores = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            winningScores[i] = list[i].getHandValue();
        }
        
        int maxScore = Arrays.stream(winningScores).max().orElse(0);
        List<Player> winners = new ArrayList<>();
        
        for (int i = 0; i < list.length; i++) {
            if (winningScores[i] == maxScore) {
                winners.add(list[i]);
            }
        }
        
        int winningsPerPlayer = pot / winners.size();
        int remainder = pot % winners.size();
        
        for (int i = 0; i < winners.size(); i++) {
            int winnings = winningsPerPlayer;
            if (i < remainder) {
                winnings++; // Distribute remainder
            }
            winners.get(i).addChips(winnings);
        }
    }

    private static int bet(Player[] list, int pot) {
        int currentBet = 0;
        
        for (int i = 0; i < list.length; i++) {
            Player player = list[i];
            
            if (player.isFold()) {
                continue;
            }
            
            if (player.isHuman()) {
                currentBet = promptHumanBet(player, currentBet);
            } else {
                currentBet = calculateAIBet(player, currentBet);
            }
            
            if (currentBet > 0) {
                int actualBet = player.removeChips(currentBet);
                pot += actualBet;
                System.out.println(player.getName() + " bets " + actualBet + " chips");
            } else if (player.isFold()) {
                System.out.println(player.getName() + " folds");
            }
        }
        
        return pot;
    }

    private static int promptHumanBet(Player player, int currentBet) {
        System.out.println("\nYour chips: " + player.getChips());
        System.out.println("Current bet to call: " + currentBet);
        System.out.print("Enter your bet amount (0 to fold): ");
        
        try {
            int bet = Integer.parseInt(scanner.nextLine().trim());
            if (bet < 0) {
                bet = 0;
            }
            if (bet > player.getChips()) {
                bet = player.getChips(); // All-in
                System.out.println("All-in for " + bet + " chips!");
            }
            if (bet == 0) {
                player.setFold(true);
            }
            return bet;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Folding.");
            player.setFold(true);
            return 0;
        }
    }

    private static int calculateAIBet(Player player, int currentBet) {
        int chips = player.getChips();
        int handValue = player.getHandValue();
        int bet = currentBet;
        
        if (chips <= 0) {
            player.setFold(true);
            return 0;
        }
        
        // Simple AI betting logic based on hand strength
        if (handValue >= 7000) { // Full house or better
            bet = Math.min(chips, currentBet + (chips / 4));
        } else if (handValue >= 4000) { // Three of a kind or better
            bet = Math.min(chips, currentBet + (chips / 8));
        } else if (handValue >= 2000) { // Pair or better
            bet = Math.min(chips, currentBet);
        } else { // High card
            if (currentBet > chips / 4) {
                player.setFold(true);
                return 0;
            }
            bet = currentBet;
        }
        
        return Math.min(bet, chips);
    }

    private static void playersStats(Player[] list) {
        System.out.println("\n--- Player Statistics ---");
        for (int i = 0; i < list.length; i++) {
            Player player = list[i];
            if (player != null && !player.isFold()) {
                System.out.println("\n" + player.getName() + ":");
                System.out.println("  Hand: " + Arrays.toString(player.getConvertedHand()));
                System.out.println("  Hand Value: " + player.getHandValue());
                System.out.println("  Chips: " + player.getChips());
            } else if (player != null) {
                System.out.println("\n" + player.getName() + ": FOLDED");
            }
        }
    }
}