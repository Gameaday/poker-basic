package com.pokermon.ai;

import com.pokermon.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AdvancedAIBehavior class and its decision-making algorithms.
 */
public class AdvancedAIBehaviorTest {
    
    private AdvancedAIBehavior aiBehavior;
    private Player testPlayer;
    
    @BeforeEach
    public void setUp() {
        aiBehavior = new AdvancedAIBehavior(12345L); // Fixed seed for deterministic tests
        testPlayer = new Player();
        testPlayer.setName("TestAI");
        testPlayer.setChips(1000);
        testPlayer.setHuman(false);
    }
    
    @Test
    public void testCalculateAIBetBasicFunctionality() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        // Test with different personalities and hand strengths
        int aggressiveBet = aiBehavior.calculateAIBet(testPlayer, Personality.BRASH, context, 0.8f);
        int conservativeBet = aiBehavior.calculateAIBet(testPlayer, Personality.MEEK, context, 0.8f);
        
        // Aggressive personalities should generally bet more than conservative ones with the same hand
        assertTrue(aggressiveBet >= conservativeBet, 
                "Aggressive personality should bet at least as much as conservative");
        
        // All bets should be within player's chip range
        assertTrue(aggressiveBet >= 0 && aggressiveBet <= testPlayer.getChips());
        assertTrue(conservativeBet >= 0 && conservativeBet <= testPlayer.getChips());
    }
    
    @Test
    public void testHandStrengthInfluencesBetting() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        Personality personality = Personality.HAPPY; // Balanced personality
        
        int weakHandBet = aiBehavior.calculateAIBet(testPlayer, personality, context, 0.1f);
        int strongHandBet = aiBehavior.calculateAIBet(testPlayer, personality, context, 0.9f);
        
        // Strong hands should generally result in higher bets
        assertTrue(strongHandBet >= weakHandBet, 
                "Strong hands should result in higher or equal bets compared to weak hands");
    }
    
    @Test
    public void testNoChipsPlayerHandling() {
        testPlayer.setChips(0);
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        int bet = aiBehavior.calculateAIBet(testPlayer, Personality.BRASH, context, 0.9f);
        assertEquals(50, bet, "Player with no chips should return current bet amount");
    }
    
    @Test
    public void testAssessHandStrength() {
        // Test hand strength assessment conversion
        assertEquals(0.1f, AdvancedAIBehavior.assessHandStrength(0), 0.01f);
        assertEquals(0.2f, AdvancedAIBehavior.assessHandStrength(10), 0.01f);
        assertEquals(0.4f, AdvancedAIBehavior.assessHandStrength(30), 0.01f);
        assertEquals(0.6f, AdvancedAIBehavior.assessHandStrength(50), 0.01f);
        assertEquals(0.7f, AdvancedAIBehavior.assessHandStrength(65), 0.01f);
        assertEquals(0.8f, AdvancedAIBehavior.assessHandStrength(75), 0.01f);
        assertEquals(0.9f, AdvancedAIBehavior.assessHandStrength(90), 0.01f);
        assertEquals(1.0f, AdvancedAIBehavior.assessHandStrength(100), 0.01f);
        
        // Test edge cases
        assertEquals(0.1f, AdvancedAIBehavior.assessHandStrength(-10), 0.01f, "Negative values should map to minimum");
    }
    
    @Test
    public void testCreateSimpleContext() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(100, 200);
        
        assertEquals(100, context.currentBet);
        assertEquals(200, context.potSize);
        assertEquals(2, context.playersRemaining);
        assertEquals(1, context.bettingRound);
        assertFalse(context.lastToAct);
        assertEquals(1, context.chipRatio);
    }
    
    @Test
    public void testDifferentPersonalitiesBehaveDifferently() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        float handStrength = 0.6f; // Medium hand
        
        // Test multiple personalities with same conditions
        int foolhardyBet = aiBehavior.calculateAIBet(testPlayer, Personality.FOOLHARDY, context, handStrength);
        int anxiousBet = aiBehavior.calculateAIBet(testPlayer, Personality.ANXIOUS, context, handStrength);
        int brainyBet = aiBehavior.calculateAIBet(testPlayer, Personality.BRAINY, context, handStrength);
        
        // At least some personalities should behave differently
        boolean someVariation = (foolhardyBet != anxiousBet) || 
                               (anxiousBet != brainyBet) || 
                               (foolhardyBet != brainyBet);
        assertTrue(someVariation, "Different personalities should produce different betting behavior");
    }
    
    @Test
    public void testBetWithinChipConstraints() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        // Test with limited chips
        testPlayer.setChips(75);
        int bet = aiBehavior.calculateAIBet(testPlayer, Personality.FOOLHARDY, context, 0.9f);
        assertTrue(bet <= 75, "Bet should not exceed available chips");
        
        // Test with very few chips
        testPlayer.setChips(10);
        bet = aiBehavior.calculateAIBet(testPlayer, Personality.FOOLHARDY, context, 0.9f);
        assertTrue(bet <= 10, "Bet should not exceed available chips even with strong hand");
    }
    
    @Test
    public void testConsistentBehaviorWithSameSeed() {
        // Test that behavior is deterministic with same seed
        AdvancedAIBehavior sameSeedAI = new AdvancedAIBehavior(12345L);
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        int bet1 = aiBehavior.calculateAIBet(testPlayer, Personality.HAPPY, context, 0.5f);
        int bet2 = sameSeedAI.calculateAIBet(testPlayer, Personality.HAPPY, context, 0.5f);
        
        assertEquals(bet1, bet2, "Same seed should produce same betting behavior");
    }
    
    @Test
    public void testDifferentSeedsProduceDifferentBehavior() {
        // Test that different seeds can produce different behavior
        AdvancedAIBehavior differentSeedAI = new AdvancedAIBehavior(54321L);
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        // Run multiple tests to account for randomness
        boolean foundDifference = false;
        for (int i = 0; i < 20; i++) {
            testPlayer.setChips(1000); // Reset chips
            int bet1 = aiBehavior.calculateAIBet(testPlayer, Personality.INDECISIVE, context, 0.5f);
            testPlayer.setChips(1000); // Reset chips
            int bet2 = differentSeedAI.calculateAIBet(testPlayer, Personality.INDECISIVE, context, 0.5f);
            
            if (bet1 != bet2) {
                foundDifference = true;
                break;
            }
        }
        
        assertTrue(foundDifference, "Different seeds should occasionally produce different behavior");
    }
    
    @Test
    public void testGameContextValues() {
        AdvancedAIBehavior.GameContext context = new AdvancedAIBehavior.GameContext(
                75, 300, 4, 2, true, 3);
        
        assertEquals(75, context.currentBet);
        assertEquals(300, context.potSize);
        assertEquals(4, context.playersRemaining);
        assertEquals(2, context.bettingRound);
        assertTrue(context.lastToAct);
        assertEquals(3, context.chipRatio);
    }
    
    @Test
    public void testPersonalityExtremes() {
        AdvancedAIBehavior.GameContext context = AdvancedAIBehavior.createSimpleContext(50, 100);
        
        // Test with extreme personalities
        int muscle_headedBet = aiBehavior.calculateAIBet(testPlayer, Personality.MUSCLE_HEADED, context, 0.5f);
        int shyBet = aiBehavior.calculateAIBet(testPlayer, Personality.SHY, context, 0.5f);
        
        // Both should produce valid bets
        assertTrue(muscle_headedBet >= 0 && muscle_headedBet <= testPlayer.getChips());
        assertTrue(shyBet >= 0 && shyBet <= testPlayer.getChips());
        
        // With medium hands, muscle-headed should generally be more aggressive than shy
        assertTrue(muscle_headedBet >= shyBet, 
                "Muscle-headed should be at least as aggressive as shy personality");
    }
}