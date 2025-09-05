/**
 * Legacy main class for the Pokermon game.
 * Contains core game logic and card management functionality.
 * Now integrates with the advanced AI personality system.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
package com.pokermon;

import com.pokermon.ai.PersonalityManager;
import java.io.*;
import java.util.*;

public class Main {

    // Card pack constants (other constants moved to CardUtils.kt for DRY compliance)
    
    private static final String[] POSSIBLE_NAMES = {
        "Carl", "Jeff", "James", "Chris", "Fred", "Daniel",
        "Tony", "Jenny", "Susen", "Rory", "Melody",
        "Liz", "Pamela", "Diane", "Carol", "Ed", "Edward",
        "Alphonse", "Ricky", "Matt", "Waldo", "Wesley", "GLaDOS",
        "Joe", "Bob", "Alex", "Josh", "David", "Brenda", "Ann",
        "Billy", "Naomi", "Vincent", "John", "Jane", "Dave", "Dirk",
        "Rose", "Roxy", "Jade", "Jake", "Karkat", "Lord English",
        "Smallie", "Anthony", "Gwen"
    };
    
    private static final int[] VALID_CHIPS = {100, 500, 1000, 2500};
    
    // Game constants
    private static final int DECK_SIZE = 52;
    private static final int DEFAULT_HAND_SIZE = 5;
    private static final int MAX_MULTIPLES_ARRAY_SIZE = 3;

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

//Prompt user for a name - using defaults for non-interactive mode
        String playerName = promptName("A(n) Drew Hussie");
//prompt for number of players to play against:
        int playerCount = promptChallengers(2);
//prompt for starting chip quantity:
        int chipsInitial = promptChips(500);

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
//have player bet (using default bet amounts for non-interactive mode)
            workingPot = bet(list, workingPot);
//report current pot value
            System.out.println("Current Pot Value: " + workingPot);

//have player exchange cards (no exchange in default mode)
            Exchange(list[0], Deck, new int[0]);

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
            Continue = promptEnd(false); // Default to ending after one game
        }
    }

//methods
    /**
     * Initializes all players with hands, names, and chips.
     * @param list the array of players to initialize
     * @param players the array of player names
     * @param chipsInitial the initial number of chips for each player
     * @param deck the deck to draw cards from
     */
    static void InitializePlayers(Player[] list, String[] players, int chipsInitial, int[] deck) {
        for (int i = 0; i < players.length; i++) {
            Player player = new Player();
            player.setHuman(i == 0); // First player (index 0) is human
            player.setupPlayer(players[i], chipsInitial, deck);
            list[i] = player;
            System.out.println(); // places spaces between player info, for neatness
        }
    }

    /**
     * Re-initializes existing players with new hands while preserving their current chips.
     * @param list the array of existing players to re-initialize
     * @param players the array of player names
     * @param deck the deck to draw cards from
     */
    static void InitializePlayers(Player[] list, String[] players, int[] deck) {
        for (int i = 0; i < players.length; i++) {
            Player player = list[i];
            if (player == null) {
                player = new Player();
                player.setHuman(i == 0); // First player (index 0) is human
                list[i] = player;
            }
            player.setupPlayer(players[i], player.getChips(), deck);
            System.out.println(); // places spaces between player info, for neatness
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
        // Ensure hand value is always positive (minimum value of 1 for high card hands)
        return Math.max(Value, 1);

    }

    private static boolean declareResults(Player[] list) {
        int finish = decideWinner(list);
        // Return winner information instead of showing dialogs
        // This allows calling code to handle display appropriately
        if (finish == 0) {
            System.out.println("You have lost the hand, better luck next time");
            return true;
        }
        if (finish == 1) {
            System.out.println("You have won the hand, congratulations!");
            return true;
        }
        if (finish == 2) {
            System.out.println("No one won the hand, the game was a tie.");
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
        if (hand == null || hand.length == 0) {
            return new int[]{0, 0}; // Return safe default for empty hands
        }
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
        // Make results array size dynamic based on hand size
        // Maximum possible multiples is hand.length (all different cards)
        int[][] results = new int[hand.length][2];
        boolean finished;
        int i = 0;
        do {
            int[] newHand = workingHand(adjustedHand);
            int[] found = cardMultiples(newHand);
            removeCard(adjustedHand, found[0]);
            if (i < results.length) { // Add bounds check
                results[i][0] = found[0];
                results[i][1] = found[1];
            }
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
        for (int i = 0; i < MAX_MULTIPLES_ARRAY_SIZE; i++) {
            if (Array[i][0] != 0) {
                index++;
            }
        }
        int revisedLength = index;
        return revisedLength;
    }

    public static int placeBet(int chips, int requestedBet) {
        int bet = requestedBet;
        if (bet > chips) {
            bet = chips; // All-in if requested bet is more than available chips
        }
        if (bet < 0) {
            bet = 0; // No negative bets
        }
        return bet;
    }

    /**
     * Calculates the bet amount for an AI player using the advanced personality system.
     * Falls back to the legacy system if the advanced system is not available.
     * 
     * @param player the AI player
     * @param currentBet the current bet amount
     * @param potSize the current pot size (defaults to currentBet * 2 if not available)
     * @return the new bet amount
     */
    public static int calculateAdvancedAIBet(Player player, int currentBet, int potSize) {
        try {
            PersonalityManager manager = PersonalityManager.getInstance();
            
            // Ensure the player has a monster/personality assignment
            if (!manager.hasPlayerAssignments(player.getName())) {
                manager.assignRandomMonsterToPlayer(player.getName());
            }
            
            // Use the advanced AI system
            return manager.calculateAdvancedAIBet(player, currentBet, potSize);
            
        } catch (Exception e) {
            // Fallback to legacy system if anything goes wrong
            System.err.println("Warning: Advanced AI system failed, falling back to legacy AI: " + e.getMessage());
            return calculateLegacyAIBet(player, currentBet);
        }
    }

    /**
     * Calculates the bet amount for an AI player based on their hand value and available chips.
     * This method now uses the advanced AI system by default.
     * 
     * @param player the AI player
     * @param currentBet the current bet amount
     * @return the new bet amount
     */
    private static int calculateAIBet(Player player, int currentBet) {
        // Estimate pot size if not available (simple heuristic)
        int estimatedPot = Math.max(currentBet * 2, 100);
        return calculateAdvancedAIBet(player, currentBet, estimatedPot);
    }

    /**
     * Legacy AI calculation method - simple hand-value based betting.
     * Kept for backward compatibility and as a fallback.
     * 
     * @param player the AI player
     * @param currentBet the current bet amount
     * @return the new bet amount
     */
    private static int calculateLegacyAIBet(Player player, int currentBet) {
        int chips = player.getChips();
        int handValue = player.getHandValue();
        int bet = currentBet;
        
        if (chips <= 0) {
            return bet;
        }
        
        int betIncrease = 0;
        if (handValue <= 38 && handValue >= 18) {
            betIncrease = 25;
        } else if (handValue > 38 && handValue <= 70) {
            betIncrease = 50;
        } else if (handValue > 70) {
            betIncrease = 100;
        } else {
            // Weak hand - just call or fold if bet is too high
            return Math.min(bet, chips);
        }
        
        bet += betIncrease;
        
        // Adjust bet if player doesn't have enough chips
        if (chips < bet) {
            if (handValue <= 38) {
                bet = chips / 4;
                if (chips % 4 != 0) {
                    bet += 1;
                }
            } else if (handValue <= 70) {
                bet = chips / 2;
                if (chips % 4 != 0) {
                    bet += 1;
                }
            } else {
                bet = chips; // All-in for strong hands
            }
        }
        
        return bet;
    }

    static int bet(Player[] list, int pot) {
        int bet = 0;
        for (int i = 0; i < list.length; i++) {
            int lastBet = bet;
            
            if (list[i].isFold()) {
                continue; // Skip folded players
            }
            
            if (list[i].isHuman()) {
                // Human player - get bet through UI
                Player user = list[i];
                pot += user.placeBet(bet);
                user.recordLastBet();
                if (lastBet != bet) {
                    recursiveBet(list, i, pot, bet);
                    break;
                }
            } else {
                // AI player - calculate bet automatically
                Player aiPlayer = list[i];
                bet = calculateAIBet(aiPlayer, bet);
                pot += aiPlayer.placeBet(bet);
                aiPlayer.recordLastBet();
                if (lastBet != bet) {
                    recursiveBet(list, i, pot, bet);
                    break;
                }
            }
        }
        return pot;
    }

    static void recursiveBet(Player[] Player, int i, int pot, int bet) {
        
    }

    static void playersStats(Player[] list) {
        for (int i = 0; i < list.length; i++) {
            Player player = list[i];
            if (player != null) {
                player.reportPlayer();
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
        Deck = new int[DECK_SIZE];
        for (int i = 0; i < Deck.length; i++) {
            Deck[i] = i + 1; // Cards numbered 1-52
        }
        return Deck;
    }

    /**
     * Generates a hand with the default size (5 cards).
     * @param deck the deck to draw from
     * @return a hand of 5 cards
     */
    public static int[] newHand(int[] deck) {
        return newHand(deck, DEFAULT_HAND_SIZE);
    }
    
    /**
     * Generates a hand with the specified number of cards.
     * @param deck the deck to draw from
     * @param handSize the number of cards to draw
     * @return a hand of the specified size
     */
    public static int[] newHand(int[] deck, int handSize) {
        if (handSize < 1 || handSize > DECK_SIZE) {
            throw new IllegalArgumentException("Hand size must be between 1 and " + DECK_SIZE);
        }
        
        int[] hand = new int[handSize];
        for (int i = 0; i < handSize; i++) {
            hand[i] = drawCard(deck);
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
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.convertHand(hand);
    }

    static String[] decideNames(String[] players) {
        for (int i = 1; i < players.length; i++) {
            players[i] = randomName();
        }
        return players;
    }

    /**
     * Initializes the player list array dynamically.
     * This cross-platform method sets up the game state for all platforms.
     * 
     * @param list Array to hold all players in the game
     * @param USER Human player object
     * @param CPU1 First AI player (may be null if fewer than 2 total players)
     * @param CPU2 Second AI player (may be null if fewer than 3 total players)  
     * @param CPU3 Third AI player (may be null if fewer than 4 total players)
     */
    static void setupList(Player[] list, Player USER, Player CPU1, Player CPU2, Player CPU3) {
        // Array of players in order - human first, then AI players
        Player[] players = {USER, CPU1, CPU2, CPU3};
        
        // Dynamically assign players to list positions based on array length
        for (int i = 0; i < list.length && i < players.length; i++) {
            list[i] = players[i];
        }
        
        // Initialize AI players with monsters and personalities
        try {
            PersonalityManager manager = PersonalityManager.getInstance();
            manager.autoAssignMonstersToAI(list);
        } catch (Exception e) {
            // Silently fail if advanced AI system is not available
            // The game will continue with legacy AI behavior
        }
    }

    public static boolean console() {
        System.out.println();
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        return true;
    }

    /**
     * Selects a random card from the working deck.
     * This method is cross-platform compatible and uses standard Java random number generation.
     * 
     * @param workingDeck Array containing available cards in the deck
     * @return The randomly selected card value
     * @throws IllegalStateException if the deck is empty
     */
    private static int randomCard(int[] workingDeck) {
        // Validate deck is not empty before drawing
        if (workingDeck.length == 0) {
            throw new IllegalStateException("Cannot draw from empty deck");
        }
        
        // Generate random index within deck bounds: 0 to (deck.length - 1)
        int randomIndex = (int) (Math.random() * workingDeck.length);
        
        // Return the card at the randomly selected index
        int selectedCard = workingDeck[randomIndex];
        return selectedCard;
    }

    /**
     * Counts the number of remaining cards in a deck array.
     * This method supports cross-platform card game logic by providing deck state information.
     * 
     * @param Cards Array representing the deck (0 = used card, non-zero = available card)
     * @return Number of cards still available in the deck
     */
    private static int remainingCards(int[] Cards) {
        int count = 0;
        
        // Iterate through all deck positions and count non-zero (available) cards
        for (int i = 0; i < Cards.length; i++) {
            if (Cards[i] != 0) {
                count++;  // This card is still available
            }
        }
        
        return count;
    }

    /**
     * Creates a working deck containing only available cards.
     * This cross-platform method compacts the deck array by removing used cards.
     * 
     * @param Deck Full deck array (may contain zeros for used cards)
     * @return Compacted array containing only available cards
     */
    private static int[] workingDeck(int[] Deck) {
        // Create array sized for exactly the number of remaining cards
        int[] workingDeck = new int[remainingCards(Deck)];
        int index = 0;
        
        // Copy all available (non-zero) cards to the working deck
        for (int i = 0; i < Deck.length; i++) {
            if (Deck[i] != 0) {
                workingDeck[index] = i;  // Store the card index (position in original deck)
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

    /**
     * Draws a card from the deck.
     * @param deck the deck to draw from
     * @return the drawn card value
     */
    public static int drawCard(int[] deck) {
        int[] workingDeck = workingDeck(deck);
        if (workingDeck.length == 0) {
            throw new IllegalStateException("Cannot draw from empty deck");
        }
        int cardIndex = randomCard(workingDeck);
        
        // Get the card value before removing it
        int cardValue = deck[cardIndex];
        
        // "remove" card from deck
        deck[cardIndex] = 0;
        
        // Return the card value
        return cardValue;
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
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.cardRank(card);
    }

    private static String multicardName(int quantity) {
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.multicardName(quantity);
    }

    private static String cardSuit(int card) {
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.cardSuit(card);
    }

    private static String cardName(int card) {
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.cardName(card);
    }

    private static String cardRank2(int rank) {
        // Use unified CardUtils for DRY compliance - single source of truth
        return CardUtils.INSTANCE.cardRank2(rank);
    }

    public static void author() {
        System.out.println("Made by: Carl Nelson and Anthony Elizondo"); //Creator names
        System.out.println(); //space
    }

    private static String[] possibleNames() {
        return POSSIBLE_NAMES;
    }

    private static String randomName() {
//randomNum = minimum + (int)(Math.random()*maximum);
        String[] names = possibleNames();
        int random = 0 + (int) (Math.random() * (names.length - 1));
        String name = names[random];
        return name;
    }

    private static int promptChallengers(int defaultCount) {
        // Return the provided count, with validation
        if (defaultCount >= 1 && defaultCount <= 3) {
            return defaultCount;
        }
        return 2; // Default fallback
    }

    private static String promptName(String defaultName) {
        // Return the provided name, or default if empty
        return (defaultName != null && !defaultName.trim().isEmpty()) ? defaultName : "A(n) Drew Hussie";
    }

    private static void revealHand(String[] Hand) {
        System.out.println("Your hand is: " + Arrays.toString(Hand));
    }

    private static int[] Exchange(Player current, int[] deck, int[] cardsToExchange) {
        if (cardsToExchange != null && cardsToExchange.length > 0) {
            for (int cardIndex : cardsToExchange) {
                if (cardIndex >= 0 && cardIndex < current.getHand().length) {
                    current.removeCardAtIndex(cardIndex);
                }
            }
        }
        replaceCards(current.getHandForModification(), deck);
        current.performAllChecks();
        return deck;
    }

    private static int promptExchange(String[] hand, int cardIndex) {
        // Return the requested card index, with bounds checking
        if (cardIndex >= 0 && cardIndex < hand.length) {
            return cardIndex;
        }
        return 0; // Default to first card if invalid index
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

    private static int promptExchangeNumber(int defaultNumber) {
        // Return the provided number, with validation
        if (defaultNumber >= 0 && defaultNumber <= 5) {
            return defaultNumber;
        }
        return 0; // Default to no exchange
    }

    private static int promptChips(int defaultChips) {
        // Return the provided chip count, with validation
        for (int valid : VALID_CHIPS) {
            if (defaultChips == valid) {
                return defaultChips;
            }
        }
        return 500; // Default fallback
    }

    private static boolean promptEnd(boolean defaultContinue) {
        // Return the provided continue flag
        return defaultContinue;
    }
}
