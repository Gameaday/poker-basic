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
            assertTrue(personality.getAggressiveness() >= 0.0f && personality.getAggressiveness() <= 10.0f,
                    personality.name() + " aggressiveness out of range: " + personality.getAggressiveness());
            assertTrue(personality.getGullibility() >= 0.0f && personality.getGullibility() <= 10.0f,
                    personality.name() + " gullibility out of range: " + personality.getGullibility());
            assertTrue(personality.getBluffTendency() >= 0.0f && personality.getBluffTendency() <= 10.0f,
                    personality.name() + " bluff tendency out of range: " + personality.getBluffTendency());
            assertTrue(personality.getConfidence() >= 0.0f && personality.getConfidence() <= 10.0f,
                    personality.name() + " confidence out of range: " + personality.getConfidence());
            assertTrue(personality.getCaution() >= 0.0f && personality.getCaution() <= 10.0f,
                    personality.name() + " caution out of range: " + personality.getCaution());
            assertTrue(personality.getDeception() >= 0.0f && personality.getDeception() <= 10.0f,
                    personality.name() + " deception out of range: " + personality.getDeception());
            assertTrue(personality.getFoldTendency() >= 0.0f && personality.getFoldTendency() <= 10.0f,
                    personality.name() + " fold tendency out of range: " + personality.getFoldTendency());
        }
    }
    
    @Test
    public void testSpecificPersonalityTraits() {
        // Test that personalities have appropriate characteristics
        
        // Foolhardy should be aggressive and low caution
        assertTrue(Personality.FOOLHARDY.getAggressiveness() >= 7.0f, "Foolhardy should be aggressive");
        assertTrue(Personality.FOOLHARDY.getCaution() <= 3.0f, "Foolhardy should have low caution");
        
        // Meek should be low aggressiveness and high fold tendency
        assertTrue(Personality.MEEK.getAggressiveness() <= 3.0f, "Meek should have low aggressiveness");
        assertTrue(Personality.MEEK.getFoldTendency() >= 6.0f, "Meek should fold easily");
        
        // Brainy should be high intelligence (represented by caution) and low gullibility
        assertTrue(Personality.BRAINY.getCaution() >= 8.0f, "Brainy should be cautious/intelligent");
        assertTrue(Personality.BRAINY.getGullibility() <= 3.0f, "Brainy should have low gullibility");
        
        // Gullible should be high gullibility
        assertTrue(Personality.GULLIBLE.getGullibility() >= 7.0f, "Gullible should have high gullibility");
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
            float[] traits = {
                personality.getAggressiveness(),
                personality.getGullibility(),
                personality.getBluffTendency(),
                personality.getConfidence(),
                personality.getCaution(),
                personality.getDeception(),
                personality.getFoldTendency()
            };
            
            // Count extreme values (0-1 or 9-10)
            int extremeCount = 0;
            for (float trait : traits) {
                if (trait <= 1.0f || trait >= 9.0f) {
                    extremeCount++;
                }
            }
            
            // No personality should have more than 3 extreme traits to keep gameplay interesting
            assertTrue(extremeCount <= 3, personality.name() + " has too many extreme traits (" + extremeCount + ")");
        }
    }
}