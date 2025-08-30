/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pokermon;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * Represents a poker player with their hand, chips, and game state.
 * Provides encapsulated access to player data and poker hand evaluation.
 */
class Player {
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

    // Setters

    // Setters
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
     * Sets up a player with a hand, chips, and name.
     * @param playerName the player's name
     * @param chips the starting chip count
     * @param deck the deck to draw from
     */
    public void setupPlayer(String playerName, int chips, int[] deck) {
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

public class Main {

    public static void main(String[] args) {
//Variables
        int workingPot = 0, topBet = 0, countup = 0;
        boolean Continue = true;
        boolean Quit = false;
        String[] players;
        Player USER = null, CPU1 = null, CPU2 = null, CPU3 = null;
        Player[] list;
//print my names as author
        author();

//Prompt user for a name
        String playerName = promptName();
//prompt for number of players to play against:
        int playerCount = promptChallengers();
//prompt for starting chip quantity:
        int chipsInitial = promptChips();

//Set stuff up
        players = new String[playerCount + 1];
        list = new Player[players.length];

//add your name to list
        players[0] = playerName;

//Decide names for the Computer players
        decideNames(players);
        setupList(list, USER, CPU1, CPU2, CPU3);

        while (Continue == true) {
            //initialize the Deck
            int[] Deck = setDeck();
            if (countup < 1) {
                //Initialize players with hands, names, and chips
                InitializePlayers(list, players, chipsInitial, Deck);
                countup = 1;
            } else {
                InitializePlayers(list, players, Deck);
            }
//show player his hand
            revealHand(list[0].getConvertedHand());
//have player bet
            workingPot = bet(list, workingPot);
//report current pot value
            System.out.println("Current Pot Value: " + workingPot);

//have player exchange cards
            Exchange(list[0], Deck);

//report new hand
            revealHand(list[0].getConvertedHand());

//have player bet again
            workingPot = bet(list, workingPot);
//report current pot value
            System.out.println("Current Pot Value: " + workingPot);
//playersStats(list);

//Declare results
            declareResults(list);
//divide the pot between the winner(s), note gives 100% to all winners currently
            dividePot(list, workingPot);
//save updated stats to file
            playersStats(list);
            Continue = promptEnd();
        }
    }

//methods
    private static void InitializePlayers(Player[] list, String[] players, int chipsInitial, int[] Deck) {
        for (int i = 0; i < players.length; i++) {
            if (i == 0) {
                Player USER = new Player();
                USER.setupPlayer(players[i], chipsInitial, Deck);
                list[i] = USER;
            }
            if (i == 1) {
                Player CPU1 = new Player();
                CPU1.setupPlayer(players[i], chipsInitial, Deck);
                list[i] = CPU1;
            }
            if (i == 2) {
                Player CPU2 = new Player();
                CPU2.setupPlayer(players[i], chipsInitial, Deck);
                list[i] = CPU2;
            }
            if (i == 3) {
                Player CPU3 = new Player();
                CPU3.setupPlayer(players[i], chipsInitial, Deck);
                list[i] = CPU3;
            }
            System.out.println(); //places spaces between player info, for neatness
//return null;
        }
    }

    private static void InitializePlayers(Player[] list, String[] players, int[] Deck) {
        for (int i = 0; i < players.length; i++) {

            if (i == 0) {
                Player USER = list[i];
                USER.setupPlayer(players[i], USER.getChips(), Deck);
                list[i] = USER;
            }
            if (i == 1) {
                Player CPU1 = list[i];
                CPU1.setupPlayer(players[i], CPU1.getChips(), Deck);
                list[i] = CPU1;
            }
            if (i == 2) {
                Player CPU2 = list[i];
                CPU2.setupPlayer(players[i], CPU2.getChips(), Deck);
                list[i] = CPU2;
            }
            if (i == 3) {
                Player CPU3 = list[i];
                CPU3.setupPlayer(players[i], CPU3.getChips(), Deck);
                list[i] = CPU3;
            }
            System.out.println(); //places spaces between player info, for neatness
//return null;

        }

    }

    public static int handValue(int[] hand) {
        int Value;
        int[][] Multiples = handMultiples(hand);
        Value = 13 - Multiples[0][0];
        if (isRoyalFlush(hand) == true) {
            return 100;
        }
        if (isStraightFlush(hand) == true) {
            return 99;
        }
        if (is4Kind(Multiples) == true) {
            return Value += 85;
        }
        if (isFullHouse(Multiples) == true) {
            return Value += 70;
        }
        if (isFlush(hand) == true) {
            return 65;
        }
        if (isAceStraight(hand) == true) {
            return 60;
        }
        if (isStraight(hand) == true) {
            return 55;
        }
        if (is3Kind(Multiples) == true) {
            return Value += 40;
        }
        if (is2Pair(Multiples) == true) {
            return Value += 25;
        }
        if (is2Kind(Multiples) == true) {
            return Value += 13;
        }
        return Value;

    }

    private static boolean declareResults(Player[] list) {
        int finish = decideWinner(list);
        if (finish == 0) {
            JOptionPane.showMessageDialog(null, "You have lost the hand, better luck next time", "Sorry!", finish, null);
            return true;
        }
        if (finish == 1) {
            JOptionPane.showMessageDialog(null, "You have won the hand, congratulations! ", "Winner!", finish, null);
            return true;
        }
        if (finish == 2) {
            JOptionPane.showMessageDialog(null, "No one won the hand, the game was a tie.", "TIE!", finish, null);
            return false;
        }
        return false;
    }

    static int decideWinner(Player[] list) {
        int[] winningScores = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            winningScores[i] = list[i].getHandValue();
        }
        Arrays.sort(winningScores);
        if (winningScores[list.length - 1] == list[0].getHandValue() && winningScores[list.length - 2] == list[0].getHandValue()) {
            return 2;
        } //tie
        if (winningScores[list.length - 1] == list[0].getHandValue() && winningScores[list.length - 2] != list[0].getHandValue()) {
            return 1;
        } //win
        if (winningScores[list.length - 1] != list[0].getHandValue()) {
            return 0;
        } //lose
        return 2;
    }

    static void dividePot(Player[] list, int pot) {
        int[] winningScores = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            winningScores[i] = list[i].getHandValue();
        }
        for (int i = 0; i < list.length; i++) {
            winningScores[i] = list[i].getHandValue();
            Arrays.sort(winningScores);
            if (winningScores[list.length - 1] == list[i].getHandValue()) {
                list[i].addChips(pot);
            } //this only accounts for one winner at the moment, but can be adapted,
//for instance if int 2, tie, give half to each, still not perfect but covers more siturations.
        }
    }

    public static int occurences(int[] hand, int card) {
        int count = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] == card) {
                count++;
            }
        }
        if (count >= 1) {
//System.out.print(count);
        }
        return count;
    }

    public static int[] cardMultiples(int[] hand) {
        int[] result = {0, 0};
        boolean finished;
        int i = 0;
        int cardCheck = 0, escape;
        do {
            int Card = hand[i];
            escape = hand.length;
            int found = occurences(hand, hand[i]);
            if (found > 0) {
                i++;
                if (i == 1) {
                    result[0] = Card;
                    result[1] = found;
                }
            }
            if (found > 1) {
                result[0] = Card;
                result[1] = found;
                return result;
            }
            finished = check(escape, i);
        } while (finished == false);
        return result;
    }

    public static boolean check(int input, int desired) {
        if (input == desired) {
            return true;
        } else {
            return false;
        }
    }

    public static int[][] handMultiples(int[] hand) {
        int[] adjustedHand = handRanks(hand);
        int[][] results = new int[3][2];
        boolean finished;
        int i = 0;
        do {
            int[] newHand = workingHand(adjustedHand);
            int[] found = cardMultiples(newHand);
            removeCard(adjustedHand, found[0]);
            results[i][0] = found[0];
            results[i][1] = found[1];
            i++;
            if (found[1] <= 1) {
                finished = true;
            } else {
                finished = false;
            }
        } while (finished == false);
        int[][] resultArray = shrinkArray(results);
//System.out.println(Arrays.toString(resultArray));
        return resultArray;
    }

    private static int[][] shrinkArray(int[][] array) {
        int[][] workingArray;
        workingArray = new int[usedSpace(array)][2];
        for (int i = 0; i < usedSpace(array); i++) {
            workingArray[i][0] = array[i][0];
            workingArray[i][1] = array[i][1];
        }
        return workingArray;
    }

    private static int usedSpace(int[][] Array) {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            if (Array[i][0] != 0) {
                index++;
            }
        }
        int revisedLength = index;
        return revisedLength;
    }

    public static int placeBet(int chips) {
        int bet = 0;
        boolean valid = false;
        while (valid == false) {
            Object[] chipSelection = {0, 10, 50, 100};
            Object Chips = JOptionPane.showInputDialog(null, "How much will you bet?", null,
                    JOptionPane.INFORMATION_MESSAGE, null, chipSelection, chipSelection[0]);
            bet = (int) Chips;

            if (bet <= chips) {
                valid = true;
            }
        }
        return bet;
    }

    static int bet(Player[] list, int pot) {
        int bet = 0;
        for (int i = 0; i < list.length; i++) {
            int threshold = 0;
            int lastBet = bet;
            if (i == 0 && !list[i].isFold()) {
                Player USER = list[i];
                pot += USER.placeBet(bet);
                USER.recordLastBet();
                if (lastBet != bet) {
                    recursiveBet(list, i, pot, bet);
                    break;
                }
            }
            if (i == 1 && !list[i].isFold()) {
                Player CPU1 = list[i];
                int chips = CPU1.getChips();
                if (chips > 0) {
                    if (CPU1.getHandValue() <= 38 && CPU1.getHandValue() >= 18) {
                        bet += 25;
                        threshold = bet;
                        if (chips < threshold) {
                            bet = chips / 4;
                            if (chips % 4 != 0) {
                                bet += 1;
                            }
                        } else if (CPU1.getHandValue() > 38 && CPU1.getHandValue() <= 70) {
                            bet += 50;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips / 2;
                                if (chips % 4 != 0) {
                                    bet += 1;
                                }
                            }
                        } else if (CPU1.getHandValue() > 70) {
                            bet += 100;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips;
                            }
                        } else {
                            if (bet > chips) {
                                bet = chips;
                            }
                        }
                    }
                    pot += CPU1.placeBet(bet);
                    CPU1.recordLastBet();
                    if (lastBet != bet) {
                        recursiveBet(list, i, pot, bet);
                        break;
                    }
                }
            }
            if (i == 2 && !list[i].isFold()) {
                Player CPU2 = list[i];
                int chips = CPU2.getChips();
                if (chips > 0) {
                    if (CPU2.getHandValue() <= 38 && CPU2.getHandValue() >= 18) {
                        bet += 25;
                        threshold = bet;
                        if (chips < threshold) {
                            bet = chips / 4;
                            if (chips % 4 != 0) {
                                bet += 1;
                            }
                        } else if (CPU2.getHandValue() > 38 && CPU2.getHandValue() <= 70) {
                            bet += 50;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips / 2;
                                if (chips % 4 != 0) {
                                    bet += 1;
                                }
                            }
                        } else if (CPU2.getHandValue() > 70) {
                            bet += 100;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips;
                            }
                        } else {
                            if (bet > chips) {
                                bet = chips;
                            }
                        }
                    }
                    pot += CPU2.placeBet(bet);
                    CPU2.recordLastBet();
                    if (lastBet != bet) {
                        recursiveBet(list, i, pot, bet);
                        break;
                    }
                }
            }
            if (i == 3 && !list[i].isFold()) {
                Player CPU3 = list[i];
                int chips = CPU3.getChips();
                if (chips > 0) {
                    if (CPU3.getHandValue() <= 38 && CPU3.getHandValue() >= 18) {
                        bet += 25;
                        threshold = bet;
                        if (chips < threshold) {
                            bet = chips / 4;
                            if (chips % 4 != 0) {
                                bet += 1;
                            }
                        } else if (CPU3.getHandValue() > 38 && CPU3.getHandValue() <= 70) {
                            bet += 50;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips / 2;
                                if (chips % 4 != 0) {
                                    bet += 1;
                                }
                            }
                        } else if (CPU3.getHandValue() > 70) {
                            bet += 100;
                            threshold = bet;
                            if (chips < threshold) {
                                bet = chips;
                            }
                        } else {
                            if (bet > chips) {
                                bet = chips;
                            }
                        }
                    }
                    pot += CPU3.placeBet(bet);
                    CPU3.recordLastBet();
                    if (lastBet != bet) {
                        recursiveBet(list, i, pot, bet);
                        break;
                    }
                }
            }
        }
        return pot;
    }

    static void recursiveBet(Player[] Player, int i, int pot, int bet) {
        
    }

    static void playersStats(Player[] list) {
        for (int i = 0; i < list.length; i++) {
            if (i == 0) {
                Player USER = list[i];
                USER.reportPlayer();
            }
            if (i == 1) {
                Player CPU1 = list[i];
                CPU1.reportPlayer();
            }
            if (i == 2) {
                Player CPU2 = list[i];
                CPU2.reportPlayer();
            }
            if (i == 3) {
                Player CPU3 = list[i];
                CPU3.reportPlayer();
            }
        }
    }

    public static boolean is2Kind(int[][] multiples) {
        boolean is2Kind = false;
        if (multiples[0][1] == 2) {
            is2Kind = true;
        }
        return is2Kind;
    }

    public static boolean is2Pair(int[][] multiples) {
        boolean is2Pair = false;
        if (multiples.length >= 2) {
            if (multiples[0][1] == 2 & multiples[1][1] == 2) {
                is2Pair = true;
            }
        }
        return is2Pair;
    }

    public static boolean is3Kind(int[][] multiples) {
        boolean is3Kind = false;
        if (multiples[0][1] == 3) {
            is3Kind = true;
        }
        return is3Kind;
    }

    public static boolean isFullHouse(int[][] multiples) {
        boolean hasThreeOfKind = false;
        boolean hasPair = false;
        
        for (int i = 0; i < multiples.length; i++) {
            if (multiples[i][1] == 3) {
                hasThreeOfKind = true;
            } else if (multiples[i][1] == 2) {
                hasPair = true;
            }
        }
        
        return hasThreeOfKind && hasPair;
    }

    public static boolean is4Kind(int[][] multiples) {
        boolean is4Kind = false;
        if (multiples[0][1] == 4) {
            is4Kind = true;
        }
        return is4Kind;
    }

    public static boolean isStraight(int[] hand) {
        int straight = 0, index = hand[0];
        boolean check, isStraight = false;
        for (int i = 0; i < hand.length; i++) {
            check = check(hand[i], index);
            if (i == 0 & index == 1 & hand[2] == 10) {
                index = 9;
            }
            index++;
            if (check == true) {
                straight++;
            }
        }
        if (straight == 5) {
            isStraight = true;
        }
        return isStraight;
    }

    public static boolean isAceStraight(int[] hand) {
        boolean check, aceStraight = false;
        int straight = 0, index = 1;
        for (int i = 0; i < hand.length; i++) {
            check = check(hand[i], index);
            index++;
            if (check == true) {
                straight++;
            }
        }
        if (straight == 5) {
            aceStraight = true;
        }
        return aceStraight;
    }

    public static boolean isFlush(int[] hand) {
        int Flush = 0, index = hand[0] % 4;
        boolean check, isFlush = false;
        for (int i = 0; i < hand.length; i++) {
            check = check(hand[i] % 4, index);
            if (check == true) {
                Flush++;
            }
        }
        if (Flush == 5) {
            isFlush = true;
        }
        return isFlush;
    }

    public static boolean isStraightFlush(int[] hand) {
        boolean isStraightFlush = false, straight, flush;
        straight = isStraight(hand);
        flush = isFlush(hand);
        if (straight == true & flush == true) {
            isStraightFlush = true;
        }
        return isStraightFlush;
    }

    public static boolean isRoyalFlush(int[] hand) {
        boolean check, isRoyalFlush = false, aceStraight, flush;
        flush = isFlush(hand);
        aceStraight = isAceStraight(hand);
        if (aceStraight == true & flush == true) {
            isRoyalFlush = true;
        }
        return isRoyalFlush;
    }

    public static int[] handRanks(int[] hand) {
        int[] handRanks = new int[hand.length];
        for (int i = 0; i < hand.length; i++) {
            int card = hand[i];
            int rank = card / 4;
            if (card % 4 != 0) {
                rank++;
            }
            handRanks[i] = rank;
        }
//System.out.println(Arrays.toString(handRanks));
        return handRanks;
    }

    public static int[] setDeck() {
        int[] Deck;
        // Standard deck has 52 cards (13 ranks * 4 suits)
        Deck = new int[52];
        for (int i = 0; i < Deck.length; i++) {
            Deck[i] = i + 1; // Cards numbered 1-52
        }
        return Deck;
    }

    public static int[] newHand(int[] Deck) {
        int[] hand;
        hand = new int[5];

        for (int i = 0; i < 5; i++) {
            hand[i] = drawCard(Deck);
        }
        return hand;
    }

    public static String[] convertHand(int[] hand) {
        String[] convertedHand;
        convertedHand = new String[hand.length];

        for (int i = 0; i < hand.length; i++) {
            convertedHand[i] = cardName(hand[i]);
//System.out.println("hand index: " + i);
        }

        return convertedHand;
    }

    public static String[] convertHand2(int[][] hand) {
        String[] convertedHand;
        convertedHand = new String[hand.length];
        for (int i = 0; i < hand.length; i++) {
            String Title = multicardName(hand[i][1]);
            convertedHand[i] = (Title + " " + cardRank2(hand[i][0]));
        }
        return convertedHand;
    }

    static String[] decideNames(String[] players) {
        for (int i = 1; i < players.length; i++) {
            players[i] = randomName();
        }
        return players;
    }

    static void setupList(Player[] list, Player USER, Player CPU1, Player CPU2, Player CPU3) {
        for (int i = 0; i < list.length; i++) {
            if (i == 0) {
                list[0] = USER;
            }
            if (i == 1) {
                list[1] = CPU1;
            }
            if (i == 2) {
                list[2] = CPU2;
            }
            if (i == 3) {
                list[3] = CPU3;
            }
        }
//return players;
    }

    public static boolean console() {
        System.out.println();
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        return true;
    }

    private static int randomCard(int[] workingDeck) {
//chose random card from the deck
//randomNum = minimum + (int)(Math.random()*maximum);
//randomCard = 1+(int)(Math.random()*current size of deck);
        int randomCard = 0 + (int) (Math.random() * (workingDeck.length - 1));
//System.out.println(randomCard);
        int Card = workingDeck[randomCard];
        return Card;
    }

    private static int remainingCards(int[] Cards) {
//change line below to reference another method or variable that tells deck size
        int index = 0;
        for (int i = 0; i <= Cards.length - 1; i++) {
            if (Cards[i] != 0) {
                index++;
            }
        }
        int remainingCards = index;
//System.out.println("Current size of Deck is: " + remainingCards + " Cards.");
        return remainingCards;
    }

    private static int[] workingDeck(int[] Deck) {
        int[] workingDeck;
//length of working deck = number of remaining cards
        workingDeck = new int[remainingCards(Deck)];
        int index = 0;
        for (int i = 0; i <= Deck.length - 1; i++) {
            if (Deck[i] != 0) {
                workingDeck[index] = i;
                index++;
            }
        }
        return workingDeck;
    }

    static int[] workingHand(int[] hand) {
        int[] workingHand;
//length of working deck = number of remaining cards
        workingHand = new int[remainingCards(hand)];
        int index = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != 0) {
                workingHand[index] = hand[i];
                index++;
            }
        }
        return workingHand;
    }

    private static int[] removeCard(int[] hand, int card) {
        for (int index = 0; index < hand.length; index++) {
            if (hand[index] == card) {
                hand[index] = 0;
            }
        }
        return hand;
    }

    private static int drawCard(int[] Deck) {

        int card = randomCard(workingDeck(Deck));

// "remove" card from deck
        Deck[card] = 0;
//remake working Deck
        return card;
    }

    static int[] replaceCards(int[] hand, int[] Deck) {

        for (int i = 0; i < hand.length; i++) {
            if (hand[i] == 0) {
                hand[i] = drawCard(Deck);
            }
        }
        return hand;
    }

    private static String cardRank(int card) {
        String cardRank;
        String[] Ranks = {
            "error", "Ace", "King", "Queen",
            "Jack", "Ten", "Nine", "Eight",
            "Seven", "Six", "Five", "Four",
            "Three", "Two"
        };
        int rank = card / 4;
        if (card % 4 != 0) {
            rank++;
        }
        cardRank = Ranks[rank];
//System.out.println("card rank test, Card: " + card + " rank: " + cardRank);
        return cardRank;
    }

    private static String multicardName(int quantity) {
        String title;
        String[] Titles = {
            "Error", "High", "Pair", "Three of a kind", "Four of a kind"
        };
        title = Titles[quantity];
        return title;
    }

    private static String cardSuit(int card) {
        String cardSuit;
        String[] Suits = {
            "Spades", "Hearts", "Diamonds", "Clubs"
        };
        int suit = card % 4;
        cardSuit = Suits[suit];
//System.out.println("card suit test, Card: " + card + " suit: " + suit + cardSuit);
        return cardSuit;
    }

    private static String cardName(int card) {
        String cardName = (cardRank(card) + " of " + cardSuit(card));
        return cardName;
    }

    private static String cardRank2(int rank) {
        String cardRank;
        String[] Ranks = {
            "error", "Ace", "King", "Queen",
            "Jack", "Ten", "Nine", "Eight",
            "Seven", "Six", "Five", "Four",
            "Three", "Two", "One"
        };
        cardRank = Ranks[rank];
//System.out.println("card rank test, Card: " + card + " rank: " + cardRank);
        return cardRank;
    }

    public static void author() {
        System.out.println("Made by: Carl Nelson and Anthony Elizondo"); //Creator names
        System.out.println(); //space
    }

    private static String[] possibleNames() {

        String[] names = {
            "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
            "Tony", "Jenny", "Susen", "Rory", "Melody",
            "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
            "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
            "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
            "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
            "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
            "Smallie", "Anthony", "Gwen"
        };
        return names;
    }

    private static String randomName() {
//randomNum = minimum + (int)(Math.random()*maximum);
        String[] names = possibleNames();
        int random = 0 + (int) (Math.random() * (names.length - 1));
        String name = names[random];
        return name;
    }

    private static int promptChallengers() {
        Object[] startingValue = {1, 2, 3};
        Object gameSize = JOptionPane.showInputDialog(null, "How many computer players will you play against?", "Set up 2/3",
                JOptionPane.INFORMATION_MESSAGE, null, startingValue, startingValue[0]);
        int playerCount = (int) gameSize;
        return playerCount;
    }

    private static String promptName() {
        String playerName = (String) JOptionPane.showInputDialog(null, "Enter a name for your player:", "Set up 1/3", 0, null, null, "A(n) Drew Hussie");
        return playerName;
    }

    private static void revealHand(String[] Hand) {
        JOptionPane.showMessageDialog(null, "Your hand is: " + "\n" + Arrays.toString(Hand));
    }

    private static int[] Exchange(Player current, int[] deck) {
        int e = promptExchangeNumber();
        if (e != 0) {
            for (int i = 0; i < e; i++) {
                int[] workingHand = workingHand(current.getHand());
                String[] RFV = convertHand(workingHand);
                int index = promptExchange(RFV);
                int cardIndex = findIndex(current.getHand(), workingHand[index]);
                current.removeCardAtIndex(cardIndex);
            }
        }
        replaceCards(current.getHandForModification(), deck);
        current.performAllChecks();
        return deck;
    }

    private static int promptExchange(String[] hand) {

        String[] Cards = (hand);
        Object ExchangeNumber = JOptionPane.showInputDialog(null, "Which Card will you exchange", "Pick one Card To Return",
                JOptionPane.QUESTION_MESSAGE, null, Cards, Cards[0]);
        return getChoiceIndex(ExchangeNumber, Cards);
    }

    public static int findIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int getChoiceIndex(Object choice, Object[] choices) {
        if (choice != null) {
            for (int i = 0; i < choices.length; i++) {
                if (choice.equals(choices[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int promptExchangeNumber() {
        Object[] Number = {0, 1, 2, 3, 4, 5};
        Object ExchangeNumber = JOptionPane.showInputDialog(null, "how many cards will you be exchanging?", "number of cards to return",
                JOptionPane.INFORMATION_MESSAGE, null, Number, Number[0]);
        int exchangeNumber = (int) ExchangeNumber;
        return exchangeNumber;
    }

    private static int promptChips() {
        Object[] chipSelection = {100, 500, 2500};
        Object initialChips = JOptionPane.showInputDialog(null, "Select the starting chip quantity", "Set up 3/3",
                JOptionPane.INFORMATION_MESSAGE, null, chipSelection, chipSelection[0]);
        int chipsInitial = (int) initialChips;
        return chipsInitial;
    }

    private static boolean promptEnd() {
        Object[] Continue = {true, false};
        Object End = JOptionPane.showInputDialog(null, "Would you like to play again", "Continue?",
                JOptionPane.INFORMATION_MESSAGE, null, Continue, Continue[0]);
        boolean end = (boolean) End;
        return end;
    }
}
