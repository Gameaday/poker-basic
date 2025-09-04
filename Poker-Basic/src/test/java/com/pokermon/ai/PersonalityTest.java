package com.pokermon.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Personality enum and its behavior.
 */
public class PersonalityTest {
    
    @Test
    public void testPersonalityValues() {
        // Test that all 24 personalities are defined
        Personality[] personalities = Personality.values();
        assertEquals(24, personalities.length, "Should have exactly 24 personalities");
        
        // Test specific personalities exist
        assertNotNull(Personality.FOOLHARDY);
        assertNotNull(Personality.GULLIBLE);
        assertNotNull(Personality.BRASH);
        assertNotNull(Personality.HUMBLE);
        assertNotNull(Personality.CONDESCENDING);
    }
    
    @Test
    public void testPersonalityTraitRanges() {
        // All personality traits should be in the 0.0-10.0 range
        for (Personality personality : Personality.values()) {
            // Test core generalized traits
            assertTrue(personality.getCourage() >= 0.0f && personality.getCourage() <= 10.0f,
                    personality.name() + " courage out of range: " + personality.getCourage());
            assertTrue(personality.getGullibility() >= 0.0f && personality.getGullibility() <= 10.0f,
                    personality.name() + " gullibility out of range: " + personality.getGullibility());
            assertTrue(personality.getGuile() >= 0.0f && personality.getGuile() <= 10.0f,
                    personality.name() + " guile out of range: " + personality.getGuile());
            assertTrue(personality.getConfidence() >= 0.0f && personality.getConfidence() <= 10.0f,
                    personality.name() + " confidence out of range: " + personality.getConfidence());
            assertTrue(personality.getCaution() >= 0.0f && personality.getCaution() <= 10.0f,
                    personality.name() + " caution out of range: " + personality.getCaution());
            assertTrue(personality.getEmpathy() >= 0.0f && personality.getEmpathy() <= 10.0f,
                    personality.name() + " empathy out of range: " + personality.getEmpathy());
            assertTrue(personality.getTimidness() >= 0.0f && personality.getTimidness() <= 10.0f,
                    personality.name() + " timidness out of range: " + personality.getTimidness());
            assertTrue(personality.getPatience() >= 0.0f && personality.getPatience() <= 10.0f,
                    personality.name() + " patience out of range: " + personality.getPatience());
            assertTrue(personality.getAmbition() >= 0.0f && personality.getAmbition() <= 10.0f,
                    personality.name() + " ambition out of range: " + personality.getAmbition());
            assertTrue(personality.getIntelligence() >= 0.0f && personality.getIntelligence() <= 10.0f,
                    personality.name() + " intelligence out of range: " + personality.getIntelligence());

            // Test calculated poker-specific traits
            assertTrue(personality.getAggressiveness() >= 0.0f && personality.getAggressiveness() <= 10.0f,
                    personality.name() + " aggressiveness out of range: " + personality.getAggressiveness());
            assertTrue(personality.getBluffTendency() >= 0.0f && personality.getBluffTendency() <= 10.0f,
                    personality.name() + " bluff tendency out of range: " + personality.getBluffTendency());
            assertTrue(personality.getDeception() >= 0.0f && personality.getDeception() <= 10.0f,
                    personality.name() + " deception out of range: " + personality.getDeception());
            assertTrue(personality.getFoldTendency() >= 0.0f && personality.getFoldTendency() <= 10.0f,
                    personality.name() + " fold tendency out of range: " + personality.getFoldTendency());
        }
    }
    
    @Test
    public void testSpecificPersonalityTraits() {
        // Test that personalities have appropriate characteristics based on their core traits
        
        // Foolhardy should be courageous and low timidness
        assertTrue(Personality.FOOLHARDY.getCourage() >= 8.0f, "Foolhardy should be courageous");
        assertTrue(Personality.FOOLHARDY.getTimidness() <= 2.0f, "Foolhardy should have low timidness");
        
        // Meek should be high timidness and low courage
        assertTrue(Personality.MEEK.getTimidness() >= 7.0f, "Meek should be timid");
        assertTrue(Personality.MEEK.getCourage() <= 3.0f, "Meek should have low courage");
        
        // Brainy should be high intelligence and low gullibility
        assertTrue(Personality.BRAINY.getIntelligence() >= 8.0f, "Brainy should be intelligent");
        assertTrue(Personality.BRAINY.getGullibility() <= 3.0f, "Brainy should have low gullibility");
        
        // Gullible should be high gullibility
        assertTrue(Personality.GULLIBLE.getGullibility() >= 7.0f, "Gullible should have high gullibility");
        
        // Test that calculated poker traits make sense
        // Foolhardy should have high calculated aggressiveness
        assertTrue(Personality.FOOLHARDY.getAggressiveness() >= 7.0f, "Foolhardy should be aggressive");
        
        // Meek should have high calculated fold tendency
        assertTrue(Personality.MEEK.getFoldTendency() >= 6.0f, "Meek should fold easily");
    }
    
    @Test
    public void testGetRandomPersonality() {
        // Test that random personality returns valid results
        for (int i = 0; i < 100; i++) {
            Personality randomPersonality = Personality.getRandomPersonality();
            assertNotNull(randomPersonality, "Random personality should not be null");
            assertTrue(randomPersonality.getDisplayName().length() > 0, "Random personality should have a name");
        }
    }
    
    @Test
    public void testGetByName() {
        // Test finding personalities by name
        assertEquals(Personality.FOOLHARDY, Personality.getByName("Foolhardy"));
        assertEquals(Personality.FOOLHARDY, Personality.getByName("FOOLHARDY"));
        assertEquals(Personality.HUMBLE, Personality.getByName("humble"));
        
        // Test enum name lookup
        assertEquals(Personality.MUSCLE_HEADED, Personality.getByName("MUSCLE_HEADED"));
        
        // Test null and invalid names
        assertNull(Personality.getByName(null));
        assertNull(Personality.getByName("NonexistentPersonality"));
        assertNull(Personality.getByName(""));
    }
    
    @Test
    public void testDisplayNames() {
        // Test that all personalities have reasonable display names
        for (Personality personality : Personality.values()) {
            String displayName = personality.getDisplayName();
            assertNotNull(displayName, personality.name() + " should have a display name");
            assertFalse(displayName.trim().isEmpty(), personality.name() + " display name should not be empty");
            assertTrue(displayName.length() >= 3, personality.name() + " display name too short: " + displayName);
        }
    }
    
    @Test
    public void testToString() {
        // Test that toString returns the display name
        for (Personality personality : Personality.values()) {
            assertEquals(personality.getDisplayName(), personality.toString(),
                    "toString should return display name for " + personality.name());
        }
    }
    
    @Test
    public void testPersonalityBalance() {
        // Test that personalities are reasonably balanced (no extreme values everywhere)
        for (Personality personality : Personality.values()) {
            float[] coreTraits = {
                personality.getCourage(),
                personality.getGullibility(),
                personality.getGuile(),
                personality.getConfidence(),
                personality.getCaution(),
                personality.getEmpathy(),
                personality.getTimidness(),
                personality.getPatience(),
                personality.getAmbition(),
                personality.getIntelligence()
            };
            
            // Count extreme values (0-1.5 or 8.5-10)
            int extremeCount = 0;
            for (float trait : coreTraits) {
                if (trait <= 1.5f || trait >= 8.5f) {
                    extremeCount++;
                }
            }
            
            // No personality should have more than 4 extreme traits to keep gameplay interesting
            assertTrue(extremeCount <= 4, personality.name() + " has too many extreme traits (" + extremeCount + ")");
        }
    }

    @Test
    public void testTraitCombinations() {
        // Test that calculated poker traits are properly derived from core traits
        for (Personality personality : Personality.values()) {
            // Aggressiveness should be influenced by courage, ambition, and confidence
            float expectedAggression = (personality.getCourage() * 0.4f + 
                                      personality.getAmbition() * 0.3f + 
                                      personality.getConfidence() * 0.3f);
            expectedAggression = Math.max(0.0f, Math.min(10.0f, expectedAggression));
            
            assertEquals(expectedAggression, personality.getAggressiveness(), 0.01f,
                    personality.name() + " aggressiveness calculation mismatch");
            
            // Fold tendency should be influenced by timidness, caution, and (lack of) confidence
            float expectedFold = (personality.getTimidness() * 0.4f + 
                                personality.getCaution() * 0.3f + 
                                (10.0f - personality.getConfidence()) * 0.3f);
            expectedFold = Math.max(0.0f, Math.min(10.0f, expectedFold));
            
            assertEquals(expectedFold, personality.getFoldTendency(), 0.01f,
                    personality.name() + " fold tendency calculation mismatch");
        }
    }
}