package com.pokermon.ai;

import com.pokermon.Monster;
import com.pokermon.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PersonalityManager class and its integration functionality.
 */
public class PersonalityManagerTest {
    
    private PersonalityManager manager;
    private Player aiPlayer;
    private Monster testMonster;
    
    @BeforeEach
    public void setUp() {
        manager = PersonalityManager.getInstance();
        manager.clearAllAssignments(); // Start with clean state
        
        aiPlayer = new Player();
        aiPlayer.setName("TestAI");
        aiPlayer.setChips(1000);
        aiPlayer.setHuman(false);
        
        testMonster = new Monster("TestMonster", Monster.Rarity.COMMON, 100, 
                                Monster.EffectType.CHIP_BONUS, 10, "A test monster", Personality.HAPPY);
    }
    
    @AfterEach
    public void tearDown() {
        manager.clearAllAssignments(); // Clean up after each test
    }
    
    @Test
    public void testSingletonInstance() {
        PersonalityManager instance1 = PersonalityManager.getInstance();
        PersonalityManager instance2 = PersonalityManager.getInstance();
        assertSame(instance1, instance2, "PersonalityManager should be a singleton");
    }
    
    @Test
    public void testAssignMonsterToPlayer() {
        manager.assignMonsterToPlayer("TestAI", testMonster, Personality.BRASH);
        
        Monster assignedMonster = manager.getPlayerMonster("TestAI");
        Personality assignedPersonality = manager.getPlayerPersonality("TestAI");
        
        assertEquals(testMonster, assignedMonster);
        assertEquals(Personality.BRASH, assignedPersonality);
        assertTrue(manager.hasPlayerAssignments("TestAI"));
    }
    
    @Test
    public void testAssignMonsterWithNullPersonality() {
        // When personality is null, should use monster's personality
        manager.assignMonsterToPlayer("TestAI", testMonster, null);
        
        Personality assignedPersonality = manager.getPlayerPersonality("TestAI");
        assertEquals(Personality.HAPPY, assignedPersonality, "Should use monster's personality when null provided");
    }
    
    @Test
    public void testAssignRandomMonsterToPlayer() {
        manager.assignRandomMonsterToPlayer("TestAI");
        
        assertTrue(manager.hasPlayerAssignments("TestAI"));
        assertNotNull(manager.getPlayerMonster("TestAI"));
        assertNotNull(manager.getPlayerPersonality("TestAI"));
    }
    
    @Test
    public void testSetCustomPersonality() {
        manager.assignRandomMonsterToPlayer("TestAI");
        Personality originalPersonality = manager.getPlayerPersonality("TestAI");
        
        manager.setCustomPersonality("TestAI", Personality.CONDESCENDING);
        
        assertEquals(Personality.CONDESCENDING, manager.getPlayerPersonality("TestAI"));
        assertNotEquals(originalPersonality, manager.getPlayerPersonality("TestAI"));
    }
    
    @Test
    public void testGetPlayerPersonalityDefault() {
        // Player with no assignment should get default personality
        Personality defaultPersonality = manager.getPlayerPersonality("UnassignedPlayer");
        assertNotNull(defaultPersonality, "Should return default personality for unassigned players");
    }
    
    @Test
    public void testCalculateAdvancedAIBet() {
        manager.assignMonsterToPlayer("TestAI", testMonster, Personality.FOOLHARDY);
        
        // Set a hand value for the test
        // We can't directly set hand value, so we'll test that the method doesn't crash
        assertDoesNotThrow(() -> {
            int bet = manager.calculateAdvancedAIBet(aiPlayer, 50, 100);
            assertTrue(bet >= 0, "Bet should be non-negative");
            assertTrue(bet <= aiPlayer.getChips(), "Bet should not exceed available chips");
        });
    }
    
    @Test
    public void testCalculateAdvancedAIBetWithHumanPlayer() {
        Player humanPlayer = new Player();
        humanPlayer.setName("Human");
        humanPlayer.setHuman(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateAdvancedAIBet(humanPlayer, 50, 100);
        }, "Should throw exception for human players");
    }
    
    @Test
    public void testHasPlayerAssignments() {
        assertFalse(manager.hasPlayerAssignments("TestAI"));
        
        manager.assignRandomMonsterToPlayer("TestAI");
        assertTrue(manager.hasPlayerAssignments("TestAI"));
        
        manager.clearAllAssignments();
        assertFalse(manager.hasPlayerAssignments("TestAI"));
    }
    
    @Test
    public void testClearAllAssignments() {
        manager.assignRandomMonsterToPlayer("TestAI1");
        manager.assignRandomMonsterToPlayer("TestAI2");
        manager.setCustomPersonality("TestAI3", Personality.GULLIBLE);
        
        assertTrue(manager.hasPlayerAssignments("TestAI1"));
        assertTrue(manager.hasPlayerAssignments("TestAI2"));
        assertTrue(manager.hasPlayerAssignments("TestAI3"));
        
        manager.clearAllAssignments();
        
        assertFalse(manager.hasPlayerAssignments("TestAI1"));
        assertFalse(manager.hasPlayerAssignments("TestAI2"));
        assertFalse(manager.hasPlayerAssignments("TestAI3"));
    }
    
    @Test
    public void testGetPlayerAIInfo() {
        // Test with assigned monster
        manager.assignMonsterToPlayer("TestAI", testMonster, Personality.BRASH);
        String info = manager.getPlayerAIInfo("TestAI");
        assertTrue(info.contains("TestAI"));
        assertTrue(info.contains("TestMonster"));
        assertTrue(info.contains("Brash"));
        
        // Test with unassigned player
        String defaultInfo = manager.getPlayerAIInfo("UnassignedAI");
        assertTrue(defaultInfo.contains("UnassignedAI"));
        assertTrue(defaultInfo.contains("personality"));
    }
    
    @Test
    public void testAutoAssignMonstersToAI() {
        Player humanPlayer = new Player();
        humanPlayer.setName("Human");
        humanPlayer.setHuman(true);
        
        Player aiPlayer1 = new Player();
        aiPlayer1.setName("AI1");
        aiPlayer1.setHuman(false);
        
        Player aiPlayer2 = new Player();
        aiPlayer2.setName("AI2");
        aiPlayer2.setHuman(false);
        
        Player[] players = {humanPlayer, aiPlayer1, aiPlayer2};
        
        manager.autoAssignMonstersToAI(players);
        
        // Human player should not be assigned
        assertFalse(manager.hasPlayerAssignments("Human"));
        
        // AI players should be assigned
        assertTrue(manager.hasPlayerAssignments("AI1"));
        assertTrue(manager.hasPlayerAssignments("AI2"));
    }
    
    @Test
    public void testAutoAssignMonstersWithNullArray() {
        // Should not crash with null input
        assertDoesNotThrow(() -> {
            manager.autoAssignMonstersToAI(null);
        });
    }
    
    @Test
    public void testAutoAssignMonstersWithEmptyArray() {
        // Should not crash with empty array
        assertDoesNotThrow(() -> {
            manager.autoAssignMonstersToAI(new Player[0]);
        });
    }
    
    @Test
    public void testAutoAssignMonstersSkipsAlreadyAssigned() {
        Player aiPlayer1 = new Player();
        aiPlayer1.setName("AI1");
        aiPlayer1.setHuman(false);
        
        // Pre-assign one player
        manager.assignMonsterToPlayer("AI1", testMonster, Personality.SHY);
        Monster originalMonster = manager.getPlayerMonster("AI1");
        
        Player[] players = {aiPlayer1};
        manager.autoAssignMonstersToAI(players);
        
        // Should keep original assignment
        assertEquals(originalMonster, manager.getPlayerMonster("AI1"));
    }
    
    @Test
    public void testAssignMonsterWithNullOrEmptyName() {
        // Should handle null names gracefully
        assertDoesNotThrow(() -> {
            manager.assignMonsterToPlayer(null, testMonster, Personality.HAPPY);
            manager.assignMonsterToPlayer("", testMonster, Personality.HAPPY);
            manager.assignMonsterToPlayer("   ", testMonster, Personality.HAPPY);
        });
        
        // None should be assigned
        assertFalse(manager.hasPlayerAssignments("null"));
        assertFalse(manager.hasPlayerAssignments(""));
        assertFalse(manager.hasPlayerAssignments("   "));
    }
    
    @Test
    public void testMultiplePlayersWithDifferentPersonalities() {
        manager.assignMonsterToPlayer("Player1", null, Personality.FOOLHARDY);
        manager.assignMonsterToPlayer("Player2", null, Personality.MEEK);
        manager.assignMonsterToPlayer("Player3", null, Personality.BRAINY);
        
        assertEquals(Personality.FOOLHARDY, manager.getPlayerPersonality("Player1"));
        assertEquals(Personality.MEEK, manager.getPlayerPersonality("Player2"));
        assertEquals(Personality.BRAINY, manager.getPlayerPersonality("Player3"));
        
        // All should have different personalities
        assertNotEquals(manager.getPlayerPersonality("Player1"), 
                       manager.getPlayerPersonality("Player2"));
        assertNotEquals(manager.getPlayerPersonality("Player2"), 
                       manager.getPlayerPersonality("Player3"));
    }
}