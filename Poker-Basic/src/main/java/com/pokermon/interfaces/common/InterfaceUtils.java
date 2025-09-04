package com.pokermon.interfaces.common;

/**
 * Common utility methods and constants shared across all UI interfaces.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class InterfaceUtils {
    
    // Card mapping constants
    public static final String[] CARD_RANKS = {
        "error", "Ace", "King", "Queen",
        "Jack", "Ten", "Nine", "Eight",
        "Seven", "Six", "Five", "Four",
        "Three", "Two"
    };
    
    public static final String[] CARD_RANKS_EXTENDED = {
        "error", "Ace", "King", "Queen",
        "Jack", "Ten", "Nine", "Eight",
        "Seven", "Six", "Five", "Four",
        "Three", "Two", "One"
    };
    
    public static final String[] CARD_SUITS = {
        "Spades", "Hearts", "Diamonds", "Clubs"
    };
    
    public static final String[] MULTICARD_NAMES = {
        "Error", "High", "Pair", "Three of a kind", "Four of a kind"
    };
    
    public static final String[] POSSIBLE_NAMES = {
        "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
        "Tony", "Jenny", "Susen", "Rory", "Melody",
        "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
        "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
        "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
        "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
        "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
        "Smallie", "Anthony", "Gwen"
    };
    
    public static final int[] VALID_CHIPS = {100, 500, 1000, 2500};
    
    // Game constants
    public static final int DECK_SIZE = 52;
    public static final int DEFAULT_HAND_SIZE = 5;
    public static final int MAX_MULTIPLES_ARRAY_SIZE = 3;
    
    /**
     * Converts a card integer value to its string representation.
     * @param card the card integer (0-51)
     * @return the card name (e.g., "Ace of Spades")
     */
    public static String convertCard(int card) {
        if (card < 0 || card >= DECK_SIZE) {
            return "Invalid Card";
        }
        
        int rank = card % 13 + 1;
        int suit = card / 13;
        
        if (rank >= CARD_RANKS.length || suit >= CARD_SUITS.length) {
            return "Invalid Card";
        }
        
        return CARD_RANKS[rank] + " of " + CARD_SUITS[suit];
    }
    
    /**
     * Converts an array of card integers to string representations.
     * @param cards the card integers
     * @return array of card names
     */
    public static String[] convertCards(int[] cards) {
        if (cards == null) {
            return new String[0];
        }
        
        String[] converted = new String[cards.length];
        for (int i = 0; i < cards.length; i++) {
            converted[i] = convertCard(cards[i]);
        }
        return converted;
    }
    
    /**
     * Creates a standard 52-card deck.
     * @return array representing the deck
     */
    public static int[] createDeck() {
        int[] deck = new int[DECK_SIZE];
        for (int i = 0; i < DECK_SIZE; i++) {
            deck[i] = i;
        }
        return deck;
    }
    
    /**
     * Shuffles a deck using Fisher-Yates algorithm.
     * @param deck the deck to shuffle
     */
    public static void shuffleDeck(int[] deck) {
        if (deck == null) return;
        
        java.util.Random random = new java.util.Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
    }
    
    /**
     * Validates if a chip amount is in the list of valid amounts.
     * @param chips the chip amount to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidChipAmount(int chips) {
        for (int validAmount : VALID_CHIPS) {
            if (chips == validAmount) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the closest valid chip amount to the given amount.
     * @param chips the desired chip amount
     * @return the closest valid chip amount
     */
    public static int getClosestValidChipAmount(int chips) {
        int closest = VALID_CHIPS[0];
        int minDiff = Math.abs(chips - closest);
        
        for (int validAmount : VALID_CHIPS) {
            int diff = Math.abs(chips - validAmount);
            if (diff < minDiff) {
                minDiff = diff;
                closest = validAmount;
            }
        }
        
        return closest;
    }
    
    /**
     * Generates a random AI player name that's not already in use.
     * @param usedNames set of names already in use
     * @return a unique AI player name
     */
    public static String generateAIPlayerName(java.util.Set<String> usedNames) {
        java.util.Random random = new java.util.Random();
        String name;
        
        do {
            name = POSSIBLE_NAMES[random.nextInt(POSSIBLE_NAMES.length)];
        } while (usedNames.contains(name));
        
        return name;
    }
    
    /**
     * Formats a chip amount for display.
     * @param chips the chip amount
     * @return formatted string
     */
    public static String formatChips(int chips) {
        if (chips >= 1000000) {
            return String.format("%.1fM", chips / 1000000.0);
        } else if (chips >= 1000) {
            return String.format("%.1fK", chips / 1000.0);
        } else {
            return String.valueOf(chips);
        }
    }
    
    /**
     * Gets a description of a hand type based on hand value.
     * @param handValue the numerical hand value
     * @return description of the hand type
     */
    public static String getHandDescription(int handValue) {
        if (handValue >= 10000) return "Royal Flush";
        if (handValue >= 9000) return "Straight Flush";
        if (handValue >= 8000) return "Four of a Kind";
        if (handValue >= 7000) return "Full House";
        if (handValue >= 6000) return "Flush";
        if (handValue >= 5000) return "Straight";
        if (handValue >= 4000) return "Three of a Kind";
        if (handValue >= 3000) return "Two Pair";
        if (handValue >= 2000) return "Pair";
        return "High Card";
    }
}