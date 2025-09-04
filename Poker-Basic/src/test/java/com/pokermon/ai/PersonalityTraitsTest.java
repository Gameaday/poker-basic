package com.pokermon.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PersonalityTraits class and its functionality.
 */
public class PersonalityTraitsTest {
    
    private PersonalityTraits testTraits;
    
    @BeforeEach
    public void setUp() {
        testTraits = new PersonalityTraits(5.0f, 6.0f, 7.0f, 8.0f, 4.0f, 3.0f, 9.0f, 2.0f);
    }
    
    @Test
    public void testConstructorAndGetters() {
        assertEquals(5.0f, testTraits.getBravery(), 0.01f);
        assertEquals(6.0f, testTraits.getTenacity(), 0.01f);
        assertEquals(7.0f, testTraits.getIntelligence(), 0.01f);
        assertEquals(8.0f, testTraits.getConfidence(), 0.01f);
        assertEquals(4.0f, testTraits.getTactfulness(), 0.01f);
        assertEquals(3.0f, testTraits.getEmpathy(), 0.01f);
        assertEquals(9.0f, testTraits.getPatience(), 0.01f);
        assertEquals(2.0f, testTraits.getAdaptability(), 0.01f);
    }
    
    @Test
    public void testTraitClamping() {
        // Test that values outside 0-10 range are clamped
        PersonalityTraits clampedTraits = new PersonalityTraits(-5.0f, 15.0f, 0.0f, 10.0f, -1.0f, 11.0f, 5.5f, 8.3f);
        
        assertEquals(0.0f, clampedTraits.getBravery(), 0.01f, "Negative values should be clamped to 0");
        assertEquals(10.0f, clampedTraits.getTenacity(), 0.01f, "Values > 10 should be clamped to 10");
        assertEquals(0.0f, clampedTraits.getIntelligence(), 0.01f, "0 should remain 0");
        assertEquals(10.0f, clampedTraits.getConfidence(), 0.01f, "10 should remain 10");
        assertEquals(0.0f, clampedTraits.getTactfulness(), 0.01f, "Negative values should be clamped to 0");
        assertEquals(10.0f, clampedTraits.getEmpathy(), 0.01f, "Values > 10 should be clamped to 10");
        assertEquals(5.5f, clampedTraits.getPatience(), 0.01f, "Valid values should remain unchanged");
        assertEquals(8.3f, clampedTraits.getAdaptability(), 0.01f, "Valid values should remain unchanged");
    }
    
    @Test
    public void testFromPersonality() {
        PersonalityTraits foolhardyTraits = PersonalityTraits.fromPersonality(Personality.FOOLHARDY);
        PersonalityTraits meekTraits = PersonalityTraits.fromPersonality(Personality.MEEK);
        PersonalityTraits brainyTraits = PersonalityTraits.fromPersonality(Personality.BRAINY);
        
        // Test that different personalities produce different trait patterns
        assertNotEquals(foolhardyTraits.getBravery(), meekTraits.getBravery(), 0.1f,
                "Different personalities should have different bravery");
        assertNotEquals(foolhardyTraits.getIntelligence(), brainyTraits.getIntelligence(), 0.1f,
                "Different personalities should have different intelligence");
        
        // Test that traits are within valid range
        assertTrue(foolhardyTraits.getBravery() >= 0.0f && foolhardyTraits.getBravery() <= 10.0f);
        assertTrue(meekTraits.getConfidence() >= 0.0f && meekTraits.getConfidence() <= 10.0f);
        assertTrue(brainyTraits.getIntelligence() >= 0.0f && brainyTraits.getIntelligence() <= 10.0f);
    }
    
    @Test
    public void testGetOverallStrength() {
        // Test overall strength calculation
        float expectedStrength = (5.0f + 6.0f + 7.0f + 8.0f + 4.0f + 3.0f + 9.0f + 2.0f) / 8.0f;
        assertEquals(expectedStrength, testTraits.getOverallStrength(), 0.01f);
        
        // Test with extreme values
        PersonalityTraits minTraits = new PersonalityTraits(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        assertEquals(0.0f, minTraits.getOverallStrength(), 0.01f);
        
        PersonalityTraits maxTraits = new PersonalityTraits(10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f);
        assertEquals(10.0f, maxTraits.getOverallStrength(), 0.01f);
    }
    
    @Test
    public void testApplyMultiplier() {
        PersonalityTraits multipliedTraits = testTraits.applyMultiplier(2.0f);
        
        // Test that multiplier is applied correctly
        assertEquals(10.0f, multipliedTraits.getBravery(), 0.01f, "5.0 * 2.0 = 10.0 (clamped)");
        assertEquals(10.0f, multipliedTraits.getTenacity(), 0.01f, "6.0 * 2.0 = 12.0 (clamped to 10.0)");
        assertEquals(10.0f, multipliedTraits.getIntelligence(), 0.01f, "7.0 * 2.0 = 14.0 (clamped to 10.0)");
        assertEquals(8.0f, multipliedTraits.getTactfulness(), 0.01f, "4.0 * 2.0 = 8.0");
        assertEquals(6.0f, multipliedTraits.getEmpathy(), 0.01f, "3.0 * 2.0 = 6.0");
        
        // Test with smaller multiplier
        PersonalityTraits reducedTraits = testTraits.applyMultiplier(0.5f);
        assertEquals(2.5f, reducedTraits.getBravery(), 0.01f, "5.0 * 0.5 = 2.5");
        assertEquals(3.0f, reducedTraits.getTenacity(), 0.01f, "6.0 * 0.5 = 3.0");
        assertEquals(1.0f, reducedTraits.getAdaptability(), 0.01f, "2.0 * 0.5 = 1.0");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        PersonalityTraits identical = new PersonalityTraits(5.0f, 6.0f, 7.0f, 8.0f, 4.0f, 3.0f, 9.0f, 2.0f);
        PersonalityTraits different = new PersonalityTraits(5.0f, 6.0f, 7.0f, 8.0f, 4.0f, 3.0f, 9.0f, 3.0f);
        
        // Test equals
        assertEquals(testTraits, identical);
        assertNotEquals(testTraits, different);
        assertNotEquals(testTraits, null);
        assertNotEquals(testTraits, "not a PersonalityTraits");
        
        // Test hashCode consistency
        assertEquals(testTraits.hashCode(), identical.hashCode());
        assertNotEquals(testTraits.hashCode(), different.hashCode());
    }
    
    @Test
    public void testToString() {
        String string = testTraits.toString();
        
        // Test that toString contains all trait values
        assertTrue(string.contains("5.0"), "Should contain bravery value");
        assertTrue(string.contains("6.0"), "Should contain tenacity value");
        assertTrue(string.contains("7.0"), "Should contain intelligence value");
        assertTrue(string.contains("8.0"), "Should contain confidence value");
        assertTrue(string.contains("4.0"), "Should contain tactfulness value");
        assertTrue(string.contains("3.0"), "Should contain empathy value");
        assertTrue(string.contains("9.0"), "Should contain patience value");
        assertTrue(string.contains("2.0"), "Should contain adaptability value");
        
        // Test that toString is formatted properly
        assertTrue(string.startsWith("PersonalityTraits{"));
        assertTrue(string.endsWith("}"));
    }
    
    @Test
    public void testFromPersonalityConsistency() {
        // Test that fromPersonality produces consistent results
        for (Personality personality : Personality.values()) {
            PersonalityTraits traits1 = PersonalityTraits.fromPersonality(personality);
            PersonalityTraits traits2 = PersonalityTraits.fromPersonality(personality);
            
            assertEquals(traits1, traits2, "fromPersonality should be deterministic for " + personality.name());
            assertTrue(traits1.getOverallStrength() >= 0.0f && traits1.getOverallStrength() <= 10.0f,
                    "Overall strength should be in valid range for " + personality.name());
        }
    }
}