package com.pokermon;

import java.util.Random;
import java.util.Scanner;

/**
 * Adventure Mode implementation - Monster battles using poker combat.
 * Players battle monsters whose health equals their chip count.
 * Poker hand strength determines battle outcomes.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0 - Beta Implementation
 */
public class AdventureMode {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    
    private final String playerName;
    private final int initialChips;
    private MonsterCollection playerCollection;
    private int currentLevel;
    private int monstersDefeated;
    
    /**
     * Creates a new Adventure Mode game session.
     */
    public AdventureMode(String playerName, int initialChips) {
        this.playerName = playerName;
        this.initialChips = initialChips;
        this.playerCollection = new MonsterCollection();
        this.currentLevel = 1;
        this.monstersDefeated = 0;
    }
    
    /**
     * Starts the Adventure Mode gameplay loop.
     */
    public void startAdventure() {
        System.out.println("🏔️ " + playerName + "'s Adventure Begins! 🏔️");
        System.out.println("You start with " + initialChips + " chips to use in battle.");
        System.out.println();
        
        boolean continueAdventure = true;
        int playerChips = initialChips;
        
        while (continueAdventure && playerChips > 0) {
            Monster enemy = generateEnemyMonster();
            System.out.println("💀 A wild " + enemy.getName() + " appears!");
            System.out.println("   Rarity: " + enemy.getRarity().getDisplayName());
            System.out.println("   Health: " + enemy.getEffectiveHealth() + " HP");
            System.out.println("   Your chips: " + playerChips);
            System.out.println();
            
            BattleResult result = battleMonster(enemy, playerChips);
            
            if (result.victory) {
                System.out.println("🎉 Victory! You defeated the " + enemy.getName() + "!");
                
                // Calculate rewards
                int chipReward = calculateChipReward(enemy);
                playerChips += chipReward;
                monstersDefeated++;
                
                System.out.println("   Chip reward: +" + chipReward + " chips");
                System.out.println("   Total chips: " + playerChips);
                
                // Chance to capture the monster
                if (attemptCapture(enemy, result.handStrength)) {
                    playerCollection.addMonster(enemy);
                    System.out.println("   🎁 Bonus: " + enemy.getName() + " joined your collection!");
                }
                
                System.out.println();
                currentLevel++;
                
            } else {
                System.out.println("💀 Defeat! The " + enemy.getName() + " was too strong.");
                int chipsLost = Math.min(playerChips / 4, 100); // Lose 25% or max 100
                playerChips -= chipsLost;
                System.out.println("   You lost " + chipsLost + " chips in the retreat.");
                System.out.println("   Remaining chips: " + playerChips);
                System.out.println();
            }
            
            if (playerChips <= 0) {
                System.out.println("💀 Game Over! You've run out of chips for battle.");
                break;
            }
            
            continueAdventure = promptContinue();
        }
        
        showAdventureStats();
    }
    
    /**
     * Generates an enemy monster based on current level.
     */
    private Monster generateEnemyMonster() {
        Monster.Rarity rarity;
        String name;
        int baseHealth;
        
        // Determine rarity based on level (higher level = rarer monsters)
        int rarityRoll = random.nextInt(100);
        if (currentLevel >= 10 && rarityRoll < 5) {
            rarity = Monster.Rarity.LEGENDARY;
            name = "Ancient Dragon";
            baseHealth = 800;
        } else if (currentLevel >= 7 && rarityRoll < 15) {
            rarity = Monster.Rarity.EPIC;
            name = "Shadow Beast";
            baseHealth = 500;
        } else if (currentLevel >= 4 && rarityRoll < 30) {
            rarity = Monster.Rarity.RARE;
            name = "Crystal Golem";
            baseHealth = 300;
        } else if (currentLevel >= 2 && rarityRoll < 60) {
            rarity = Monster.Rarity.UNCOMMON;
            name = "Forest Guardian";
            baseHealth = 150;
        } else {
            rarity = Monster.Rarity.COMMON;
            name = "Wild Slime";
            baseHealth = 100;
        }
        
        // Scale health with level
        int scaledHealth = baseHealth + (currentLevel - 1) * 50;
        
        return new Monster(
            name + " Lv." + currentLevel,
            rarity,
            scaledHealth, // baseHealth 
            Monster.EffectType.CHIP_BONUS,
            20, // effectPower
            "A wild monster encountered in adventure mode"
        );
    }
    
    /**
     * Conducts a poker battle between player and monster.
     */
    private BattleResult battleMonster(Monster enemy, int playerChips) {
        System.out.println("⚔️ POKER BATTLE COMMENCES! ⚔️");
        System.out.println("Draw your hand and prepare for battle!");
        System.out.println();
        
        // Create a simple poker hand for the player
        Player tempPlayer = new Player();
        tempPlayer.setName(playerName);
        tempPlayer.setChipsCurrent(playerChips);
        int[] deck = Main.setDeck();
        
        // Initialize player with a poker hand
        int[] hand = Main.newHand(deck);
        tempPlayer.updateHand(hand);
        tempPlayer.convertHand();
        tempPlayer.calculateHandValue();
        
        // Display player's hand
        String[] playerHand = tempPlayer.getConvertedHand();
        System.out.println("Your battle hand:");
        for (int i = 0; i < playerHand.length; i++) {
            System.out.println("  " + (i + 1) + ": " + playerHand[i]);
        }
        System.out.println();
        
        int handValue = tempPlayer.getHandValue();
        System.out.println("Hand strength: " + handValue + " (" + getHandDescription(handValue) + ")");
        
        // Calculate battle outcome
        int damageDealt = calculateDamage(handValue, enemy);
        boolean victory = damageDealt >= enemy.getEffectiveHealth();
        
        System.out.println("⚡ You deal " + damageDealt + " damage!");
        
        if (victory) {
            System.out.println("💥 Critical hit! The monster is defeated!");
        } else {
            System.out.println("💢 The monster survives with " + (enemy.getEffectiveHealth() - damageDealt) + " HP remaining.");
        }
        System.out.println();
        
        return new BattleResult(victory, handValue, damageDealt);
    }
    
    /**
     * Calculates damage based on poker hand strength.
     */
    private int calculateDamage(int handValue, Monster enemy) {
        // Base damage scales with hand strength
        int baseDamage = handValue * 10;
        
        // Add randomness (80-120% of base damage)
        double multiplier = 0.8 + (random.nextDouble() * 0.4);
        int finalDamage = (int) (baseDamage * multiplier);
        
        // Rarity affects enemy toughness
        double rarityDefense = 1.0 / enemy.getRarity().getPowerMultiplier();
        finalDamage = (int) (finalDamage * rarityDefense);
        
        return Math.max(finalDamage, 1); // Minimum 1 damage
    }
    
    /**
     * Calculates chip rewards for defeating a monster.
     */
    private int calculateChipReward(Monster enemy) {
        int baseReward = enemy.getEffectiveHealth() / 2; // Half the monster's health
        double rarityBonus = enemy.getRarity().getPowerMultiplier();
        return (int) (baseReward * rarityBonus);
    }
    
    /**
     * Attempts to capture a defeated monster.
     */
    private boolean attemptCapture(Monster enemy, int handStrength) {
        // Higher hand strength = better capture chance
        double captureChance = Math.min(0.8, 0.1 + (handStrength * 0.02));
        
        // Rarer monsters are harder to capture
        captureChance /= enemy.getRarity().getPowerMultiplier();
        
        return random.nextDouble() < captureChance;
    }
    
    /**
     * Gets a description of the poker hand strength.
     */
    private String getHandDescription(int handValue) {
        if (handValue >= 80) return "Royal Flush Territory";
        if (handValue >= 60) return "Four of a Kind/Straight Flush";
        if (handValue >= 40) return "Full House/Flush";
        if (handValue >= 30) return "Straight/Three of a Kind";
        if (handValue >= 20) return "Two Pair";
        if (handValue >= 10) return "One Pair";
        return "High Card";
    }
    
    /**
     * Prompts player to continue the adventure.
     */
    private boolean promptContinue() {
        System.out.print("Continue your adventure? (y/n) [y]: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.isEmpty() || input.equals("y") || input.equals("yes");
    }
    
    /**
     * Displays final adventure statistics.
     */
    private void showAdventureStats() {
        System.out.println("🏆 ADVENTURE COMPLETE! 🏆");
        System.out.println("=".repeat(40));
        System.out.println("Monsters defeated: " + monstersDefeated);
        System.out.println("Highest level reached: " + currentLevel);
        System.out.println("Monsters captured: " + playerCollection.getMonsterCount());
        System.out.println();
        
        if (playerCollection.getMonsterCount() > 0) {
            System.out.println("Your Monster Collection:");
            // Note: MonsterCollection would need a method to list monsters
            // For now, just show count
            System.out.println("  Total monsters: " + playerCollection.getMonsterCount());
        }
        
        System.out.println("Thank you for playing Adventure Mode!");
    }
    
    /**
     * Result of a battle encounter.
     */
    private static class BattleResult {
        final boolean victory;
        final int handStrength;
        final int damageDealt;
        
        BattleResult(boolean victory, int handStrength, int damageDealt) {
            this.victory = victory;
            this.handStrength = handStrength;
            this.damageDealt = damageDealt;
        }
    }
}