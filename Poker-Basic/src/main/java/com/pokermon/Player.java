package com.pokermon;

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

    
    /**
     * Default constructor for a Player.
     */
    public Player() {
        this.fold = false;
        this.lastBet = 0;
        this.bet = 0;
        this.chips = 0;
        this.handValue = 0;
    }

    // Getters
    public int getLastBet() { return lastBet; }
    public boolean isFold() { return fold; }
    public String getName() { return name; }
    public int[] getHand() { return hand != null ? hand.clone() : null; }
    public int getChips() { return chips; }
    public int getBet() { return bet; }
    public String[] getConvertedHand() { return convertedHand != null ? convertedHand.clone() : null; }
    public String[] getConvertedHand2() { return convertedHand2 != null ? convertedHand2.clone() : null; }
    public int getHandValue() { return handValue; }
    public boolean isStraight() { return straight; }
    public boolean isAceStraight() { return aceStraight; }
    public boolean isFlush() { return flush; }
    public boolean isStraightFlush() { return straightFlush; }
    public boolean isRoyalFlush() { return royalFlush; }
    public boolean isTwoKind() { return twoKind; }
    public boolean isTwoPair() { return twoPair; }
    public boolean isThreeKind() { return threeKind; }
    public boolean isFourKind() { return fourKind; }
    public boolean isFullHouse() { return fullHouse; }

    public void setLastBet(int lastBet) { this.lastBet = lastBet; }
    public void setFold(boolean fold) { this.fold = fold; }
    
    /**
     * Sets the player's name.
     * @param playerName the name to set
     */
    public void setName(String playerName) {
        this.name = playerName;
    }

    /**
     * Sets the player's current chip count.
     * @param playerChips the number of chips to set
     */
    public void setChips(int playerChips) {
        this.chips = playerChips;
    }

    /**
     * Resets the player's betting state for a new round.
     */
    public void resetBet() {
        this.lastBet = 0;
        this.bet = 0;
    }

    /**
     * Records the last bet and resets current bet.
     */
    public void recordLastBet() {
        this.lastBet = this.bet;
        this.bet = 0;
    }

    /**
     * Saves player information to a file for persistence.
     */
    public void save() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(name + ".txt", true)));
        } catch (IOException e1) {
            System.out.println("Error during reading/writing");
        }
    }

    /**
     * Sets the player's chip count with validation.
     * @param playerChips the number of chips to set
     */
    public void setChipsCurrent(int playerChips) {
        this.chips = playerChips;
    }

    /**
     * Adjusts chip count for returning players.
     * @param playerChips current chip count
     */
    public void setChipsCurrentAgain(int playerChips) {
        if (playerChips < 1) {
            this.chips = 200;
        } else {
            this.chips = playerChips + 100;
        }
    }

    /**
     * Places a bet, deducting from chips.
     * @param betAmount the amount to bet
     * @return the actual bet amount
     */
    public int placeBet(int betAmount) {
        this.bet = betAmount;
        this.chips -= betAmount;
        return betAmount;
    }

    /**
     * Makes the player fold their hand.
     * @return true (always folds)
     */
    public boolean foldHand() {
        this.fold = true;
        return fold;
    }

    /**
     * Adds chips to the player's total.
     * @param amount the amount to add
     */
    public void addChips(int amount) {
        this.chips += amount;
    }

    /**
     * Sets a specific card in the hand to 0 (removed).
     * @param index the index of the card to remove
     */
    public void removeCardAtIndex(int index) {
        if (this.hand != null && index >= 0 && index < this.hand.length) {
            this.hand[index] = 0;
        }
    }

    /**
     * Gets the hand array for direct modification (used internally by game logic).
     * @return the hand array reference
     */
    public int[] getHandForModification() {
        return this.hand;
    }

    /**
     * Resets the fold status for a new round.
     * @return false (unfolds the player)
     */
    public boolean resetFold() {
        this.fold = false;
        return fold;
    }
    public void updateHand(int[] playerHand) {
        this.hand = playerHand != null ? playerHand.clone() : null;
        if (this.hand != null) {
            Arrays.sort(this.hand);
        }
    }

    /**
     * Converts hand to reader-friendly format.
     */
    public void convertHand() {
        this.convertedHand = Main.convertHand(hand);
    }

    /**
     * Converts card multiples to reader-friendly format.
     */
    public void convertHand2() {
        this.convertedHand2 = Main.convertHand2(handMultiples);
    }

    /**
     * Finds card multiples in the hand.
     */
    public void findMultiples() {
        this.handMultiples = Main.handMultiples(hand);
    }

    /**
     * Checks if hand is a straight.
     */
    public void checkStraight() {
        this.straight = Main.isStraight(hand);
    }

    /**
     * Checks if hand is an ace-high straight.
     */
    public void checkAceStraight() {
        this.aceStraight = Main.isAceStraight(hand);
    }

    /**
     * Checks if hand is a flush.
     */
    public void checkFlush() {
        this.flush = Main.isFlush(hand);
    }

    /**
     * Checks if hand is a straight flush.
     */
    public void checkStraightFlush() {
        this.straightFlush = Main.isStraightFlush(hand);
    }

    /**
     * Checks if hand is a royal flush.
     */
    public void checkRoyalFlush() {
        this.royalFlush = Main.isRoyalFlush(hand);
    }

    /**
     * Checks if hand has two of a kind.
     */
    public void check2Kind() {
        this.twoKind = Main.is2Kind(handMultiples);
    }

    /**
     * Checks if hand has two pairs.
     */
    public void check2Pair() {
        this.twoPair = Main.is2Pair(handMultiples);
    }

    /**
     * Checks if hand has three of a kind.
     */
    public void check3Kind() {
        this.threeKind = Main.is3Kind(handMultiples);
    }

    /**
     * Checks if hand is a full house.
     */
    public void checkFullHouse() {
        this.fullHouse = Main.isFullHouse(handMultiples);
    }

    /**
     * Checks if hand has four of a kind.
     */
    public void check4Kind() {
        this.fourKind = Main.is4Kind(handMultiples);
    }

    /**
     * Performs all hand checks and evaluations.
     */
    public void performAllChecks() {
        updateHand(hand);
        findMultiples();
        convertHand();
        convertHand2();
        calculateHandValue();
        // Perform all hand type checks
        check2Kind();
        check2Pair();
        check3Kind();
        checkFullHouse();
        check4Kind();
        checkStraight();
        checkAceStraight();
        checkFlush();
        checkStraightFlush();
        checkRoyalFlush();
    }

    /**
     * Calculates and sets the hand value.
     */
    public void calculateHandValue() {
        this.handValue = Main.handValue(hand);
    }

    /**
     * Sets up a player with a hand, chips, and name using default hand size.
     * @param playerName the player's name
     * @param chips the starting chip count
     * @param deck the deck to draw from
     */
    public void setupPlayer(String playerName, int chips, int[] deck) {
        setupPlayer(playerName, chips, deck, 5); // Default to 5-card hand
    }

    /**
     * Sets up a player with a hand, chips, and name using custom hand size.
     * @param playerName the player's name
     * @param chips the starting chip count
     * @param deck the deck to draw from
     * @param handSize the number of cards in the hand
     */
    public void setupPlayer(String playerName, int chips, int[] deck, int handSize) {
        setName(playerName);
        setChipsCurrent(chips);
        resetFold();
        updateHand(Main.newHand(deck, handSize));
        convertHand();
        printPlayer();
        printHandStats();
        reportPlayer();
    }

    /**
     * Sets up a player again with existing chips plus bonus.
     * @param playerName the player's name
     * @param deck the deck to draw from
     * @param chips the current chip count
     */
    public void setupPlayerAgain(String playerName, int[] deck, int chips) {
        setName(playerName);
        setChipsCurrent(chips);
        resetFold();
        updateHand(Main.newHand(deck));
        convertHand();
        printPlayer();
        printHandStats();
        reportPlayer();
    }

    /**
     * Prints basic player information.
     */
    public void printPlayer() {
        System.out.println("name : " + name);
        System.out.println("Chips: " + chips);
    }

    /**
     * Displays what the hand contains (only true conditions).
     */
    public void displayHandContents() {
        if (twoKind && !twoPair && !fullHouse) {
            System.out.println("Two of a Kind: " + twoKind);
        }
        if (twoPair) {
            System.out.println("Two Pairs: " + twoPair);
        }
        if (threeKind && !fullHouse) {
            System.out.println("Three of a Kind: " + threeKind);
        }
        if (fullHouse) {
            System.out.println("Full House: " + fullHouse);
        }
        if (fourKind) {
            System.out.println("Four of a Kind: " + fourKind);
        }
        if (straight && !aceStraight && !royalFlush) {
            System.out.println("Straight: " + straight);
        }
        if (aceStraight && !royalFlush) {
            System.out.println("Ace High Straight: " + aceStraight);
        }
        if (flush && !straightFlush && !royalFlush) {
            System.out.println("Flush: " + flush);
        }
        if (straightFlush && !royalFlush) {
            System.out.println("Straight Flush: " + straightFlush);
        }
        if (royalFlush) {
            System.out.println("Royal Flush: " + royalFlush);
        }
    }

    /**
     * Prints all current hand statistics.
     */
    public void printHandStats() {
        System.out.println("Cards: " + Arrays.toString(hand));
        System.out.println("Hand : " + Arrays.toString(convertedHand));
        performAllChecks();
        System.out.println("Card multiples: " + Arrays.toString(convertedHand2));
        displayHandContents();
        System.out.println("Hand Value: " + handValue);
    }

    /**
     * Saves player information to file.
     */
    public void reportPlayer() {
        save();
        printPlayer();
        printHandStats();
        Main.console();
    }
}