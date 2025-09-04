package com.pokermon.core;

import java.io.*;
import java.util.*;

/**
 * Represents a poker player with their hand, chips, and game state.
 * Provides encapsulated access to player data and poker hand evaluation.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class Player {
    // Private fields for better encapsulation
    private int lastBet;
    private boolean fold;
    private String name;
    private int[] hand;
    private int chips;
    private int bet;
    private String[] convertedHand, convertedHand2;
    private int[][] handMultiples;
    private boolean straight, aceStraight, flush, straightFlush, royalFlush;
    private boolean twoKind, twoPair, threeKind, fourKind, fullHouse;
    private int handValue;
    private boolean isHuman;

    
    /**
     * Default constructor for a Player.
     */
    public Player() {
        // Initialize with default values
        this.lastBet = 0;
        this.fold = false;
        this.name = "";
        this.hand = new int[5]; // Default hand size
        this.chips = 0;
        this.bet = 0;
        this.convertedHand = new String[5];
        this.convertedHand2 = new String[5];
        this.handMultiples = new int[4][3]; // [type][card, number, highest index]
        this.handValue = 0;
        this.isHuman = false;
        
        // Initialize hand evaluation flags
        this.straight = false;
        this.aceStraight = false;
        this.flush = false;
        this.straightFlush = false;
        this.royalFlush = false;
        this.twoKind = false;
        this.twoPair = false;
        this.threeKind = false;
        this.fourKind = false;
        this.fullHouse = false;
    }

    /**
     * Constructor with name initialization.
     * @param name the player's name
     */
    public Player(String name) {
        this();
        this.name = name;
    }

    /**
     * Constructor with name and chips initialization.
     * @param name the player's name
     * @param chips the initial number of chips
     */
    public Player(String name, int chips) {
        this(name);
        this.chips = chips;
    }
    
    // Getter methods
    
    /**
     * Gets the player's last bet amount.
     * @return the last bet amount
     */
    public int getLastBet() {
        return lastBet;
    }
    
    /**
     * Checks if the player has folded.
     * @return true if the player has folded
     */
    public boolean isFold() {
        return fold;
    }
    
    /**
     * Gets the player's name.
     * @return the player's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the player's hand as integer array.
     * @return the hand array
     */
    public int[] getHand() {
        return hand;
    }
    
    /**
     * Gets the player's chip count.
     * @return the number of chips
     */
    public int getChips() {
        return chips;
    }
    
    /**
     * Gets the player's current bet amount.
     * @return the current bet amount
     */
    public int getBet() {
        return bet;
    }
    
    /**
     * Gets the player's hand as converted string array.
     * @return the converted hand array
     */
    public String[] getConvertedHand() {
        return convertedHand;
    }
    
    /**
     * Gets the player's hand value for comparison.
     * @return the hand value
     */
    public int getHandValue() {
        return handValue;
    }
    
    /**
     * Checks if the player is human.
     * @return true if the player is human
     */
    public boolean isHuman() {
        return isHuman;
    }
    
    // Setter methods
    
    /**
     * Sets the player's last bet amount.
     * @param lastBet the last bet amount
     */
    public void setLastBet(int lastBet) {
        this.lastBet = lastBet;
    }
    
    /**
     * Sets the player's fold status.
     * @param fold true if the player folds
     */
    public void setFold(boolean fold) {
        this.fold = fold;
    }
    
    /**
     * Sets the player's name.
     * @param name the player's name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the player's hand.
     * @param hand the hand array
     */
    public void setHand(int[] hand) {
        this.hand = hand.clone();
    }
    
    /**
     * Sets the player's chip count.
     * @param chips the number of chips
     */
    public void setChips(int chips) {
        this.chips = Math.max(0, chips); // Ensure non-negative
    }
    
    /**
     * Sets the player's current bet amount.
     * @param bet the current bet amount
     */
    public void setBet(int bet) {
        this.bet = Math.max(0, bet); // Ensure non-negative
    }
    
    /**
     * Sets whether the player is human.
     * @param isHuman true if the player is human
     */
    public void setHuman(boolean isHuman) {
        this.isHuman = isHuman;
    }
    
    // Game logic methods
    
    /**
     * Adds chips to the player's count.
     * @param amount the amount to add
     */
    public void addChips(int amount) {
        this.chips += Math.max(0, amount);
    }
    
    /**
     * Removes chips from the player's count.
     * @param amount the amount to remove
     * @return the actual amount removed (may be less if insufficient chips)
     */
    public int removeChips(int amount) {
        int actualAmount = Math.min(amount, this.chips);
        this.chips -= actualAmount;
        return actualAmount;
    }
    
    /**
     * Sets up the player with name, chips, and initial hand.
     * @param name the player's name
     * @param chips the initial chip count
     * @param deck the deck to draw cards from
     */
    public void setupPlayer(String name, int chips, int[] deck) {
        this.name = name;
        this.chips = chips;
        this.hand = drawCards(deck, 5);
        this.convertedHand = convertCards(this.hand);
        this.fold = false;
        this.bet = 0;
        performAllChecks();
    }
    
    /**
     * Draws cards from the deck.
     * @param deck the deck to draw from
     * @param count the number of cards to draw
     * @return array of drawn cards
     */
    private int[] drawCards(int[] deck, int count) {
        int[] cards = new int[count];
        Random random = new Random();
        boolean[] used = new boolean[deck.length];
        
        for (int i = 0; i < count; i++) {
            int index;
            do {
                index = random.nextInt(deck.length);
            } while (used[index]);
            
            used[index] = true;
            cards[i] = deck[index];
        }
        
        return cards;
    }
    
    /**
     * Converts integer cards to string representation.
     * @param cards the cards to convert
     * @return string array of card names
     */
    private String[] convertCards(int[] cards) {
        String[] converted = new String[cards.length];
        String[] ranks = {"error", "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"};
        String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
        
        for (int i = 0; i < cards.length; i++) {
            int rank = cards[i] % 13 + 1;
            int suit = cards[i] / 13;
            converted[i] = ranks[rank] + " of " + suits[suit];
        }
        
        return converted;
    }
    
    /**
     * Performs all hand evaluation checks and calculates hand value.
     */
    public void performAllChecks() {
        resetFlags();
        checkForMultiples();
        checkForStraight();
        checkForFlush();
        checkForSpecialHands();
        calculateHandValue();
    }
    
    /**
     * Resets all hand evaluation flags.
     */
    private void resetFlags() {
        straight = false;
        aceStraight = false;
        flush = false;
        straightFlush = false;
        royalFlush = false;
        twoKind = false;
        twoPair = false;
        threeKind = false;
        fourKind = false;
        fullHouse = false;
    }
    
    /**
     * Checks for card multiples (pairs, three of a kind, etc.).
     */
    private void checkForMultiples() {
        // Reset multiples array
        for (int[] multiple : handMultiples) {
            Arrays.fill(multiple, 0);
        }
        
        // Count card ranks
        int[] rankCounts = new int[14]; // Index 0 unused, 1-13 for ranks
        for (int card : hand) {
            int rank = card % 13 + 1;
            rankCounts[rank]++;
        }
        
        // Find multiples
        int multipleIndex = 0;
        for (int rank = 1; rank < rankCounts.length && multipleIndex < handMultiples.length; rank++) {
            if (rankCounts[rank] >= 2) {
                handMultiples[multipleIndex][0] = rank; // Card rank
                handMultiples[multipleIndex][1] = rankCounts[rank]; // Number of this rank
                handMultiples[multipleIndex][2] = rank; // Highest rank (same as rank for multiples)
                multipleIndex++;
                
                // Set flags
                if (rankCounts[rank] == 2) {
                    if (twoKind) twoPair = true;
                    twoKind = true;
                } else if (rankCounts[rank] == 3) {
                    threeKind = true;
                } else if (rankCounts[rank] == 4) {
                    fourKind = true;
                }
            }
        }
        
        // Check for full house
        if (threeKind && twoKind) {
            fullHouse = true;
        }
    }
    
    /**
     * Checks for straight hands.
     */
    private void checkForStraight() {
        int[] sortedRanks = new int[hand.length];
        for (int i = 0; i < hand.length; i++) {
            sortedRanks[i] = hand[i] % 13 + 1;
        }
        Arrays.sort(sortedRanks);
        
        // Check for regular straight
        boolean isStraight = true;
        for (int i = 1; i < sortedRanks.length; i++) {
            if (sortedRanks[i] != sortedRanks[i-1] + 1) {
                isStraight = false;
                break;
            }
        }
        
        if (isStraight) {
            straight = true;
            return;
        }
        
        // Check for ace-low straight (A,2,3,4,5)
        if (sortedRanks[0] == 1 && sortedRanks[1] == 2 && sortedRanks[2] == 3 && sortedRanks[3] == 4 && sortedRanks[4] == 5) {
            straight = true;
            aceStraight = true;
        }
    }
    
    /**
     * Checks for flush hands.
     */
    private void checkForFlush() {
        int suit = hand[0] / 13;
        for (int card : hand) {
            if (card / 13 != suit) {
                return;
            }
        }
        flush = true;
    }
    
    /**
     * Checks for special hands (straight flush, royal flush).
     */
    private void checkForSpecialHands() {
        if (straight && flush) {
            straightFlush = true;
            
            // Check for royal flush (10, J, Q, K, A of same suit)
            int[] sortedRanks = new int[hand.length];
            for (int i = 0; i < hand.length; i++) {
                sortedRanks[i] = hand[i] % 13 + 1;
            }
            Arrays.sort(sortedRanks);
            
            if (sortedRanks[0] == 1 && sortedRanks[1] == 10 && sortedRanks[2] == 11 && sortedRanks[3] == 12 && sortedRanks[4] == 13) {
                royalFlush = true;
            }
        }
    }
    
    /**
     * Calculates the numerical hand value for comparison.
     */
    private void calculateHandValue() {
        if (royalFlush) {
            handValue = 10000;
        } else if (straightFlush) {
            handValue = 9000;
        } else if (fourKind) {
            handValue = 8000;
        } else if (fullHouse) {
            handValue = 7000;
        } else if (flush) {
            handValue = 6000;
        } else if (straight) {
            handValue = 5000;
        } else if (threeKind) {
            handValue = 4000;
        } else if (twoPair) {
            handValue = 3000;
        } else if (twoKind) {
            handValue = 2000;
        } else {
            handValue = 1000; // High card
        }
        
        // Add high card value for tie-breaking
        int highCard = 0;
        for (int card : hand) {
            int rank = card % 13 + 1;
            if (rank == 1) rank = 14; // Ace high
            highCard = Math.max(highCard, rank);
        }
        handValue += highCard;
    }
    
    /**
     * Removes a card at the specified index.
     * @param index the index of the card to remove
     */
    public void removeCardAtIndex(int index) {
        if (index >= 0 && index < hand.length) {
            // Replace with 0 or -1 to indicate empty slot
            hand[index] = -1;
        }
    }
    
    /**
     * Adds a card to the first available slot.
     * @param card the card to add
     */
    public void addCard(int card) {
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] == -1) {
                hand[i] = card;
                break;
            }
        }
        convertedHand = convertCards(hand);
        performAllChecks();
    }
    
    /**
     * Reports the player's current status.
     */
    public void reportPlayer() {
        System.out.println("name : " + name);
        System.out.println("Chips: " + chips);
        System.out.println("Cards: " + Arrays.toString(hand));
        System.out.println("Hand : " + Arrays.toString(convertedHand));
        System.out.println("Card multiples: " + getHandDescription());
        System.out.println("Hand Value: " + handValue);
    }
    
    /**
     * Gets a description of the hand type.
     * @return string description of the hand
     */
    private String getHandDescription() {
        if (royalFlush) return "Royal Flush";
        if (straightFlush) return "Straight Flush";
        if (fourKind) return "Four of a Kind";
        if (fullHouse) return "Full House";
        if (flush) return "Flush";
        if (straight) return "Straight";
        if (threeKind) return "Three of a Kind";
        if (twoPair) return "Two Pair";
        if (twoKind) return "Pair";
        
        // Find high card
        int highCard = 0;
        for (int card : hand) {
            int rank = card % 13 + 1;
            highCard = Math.max(highCard, rank);
        }
        
        String[] ranks = {"error", "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"};
        return "[High " + ranks[highCard] + "]";
    }
}