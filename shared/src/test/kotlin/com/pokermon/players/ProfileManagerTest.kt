package com.pokermon.players

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for ProfileManager to validate SharedFlow behavior
 */
class ProfileManagerTest {

    private lateinit var profileManager: ProfileManager

    @BeforeEach
    fun setup() {
        // Get the singleton instance
        profileManager = ProfileManager.getInstance()
    }

    @Test
    fun `createProfile should create and store profile`() {
        val profile = profileManager.createProfile("test_player1", "Test Player")
        
        assertEquals("test_player1", profile.playerId)
        assertEquals("Test Player", profile.playerName)
        assertEquals(1, profile.overallLevel)
    }

    @Test
    fun `loadProfile should emit profile to SharedFlow`() = runBlocking {
        // Create a profile
        val profile = profileManager.createProfile("test_player2", "Test Player 2")
        
        // Load the profile
        val loadedProfile = profileManager.loadProfile("test_player2")
        
        // Verify the profile was loaded
        assertNotNull(loadedProfile)
        assertEquals("test_player2", loadedProfile?.playerId)
        
        // Collect from SharedFlow (with replay=1, new subscribers get the last emission)
        val emittedProfile = withTimeout(1000) {
            profileManager.currentProfile.first()
        }
        assertEquals("test_player2", emittedProfile?.playerId)
    }

    @Test
    fun `loadProfile for non-existent player should emit null`() = runBlocking {
        val loadedProfile = profileManager.loadProfile("non_existent")
        
        // Should return null for non-existent profile
        assertNull(loadedProfile)
        
        // SharedFlow should emit null
        val emittedProfile = withTimeout(1000) {
            profileManager.currentProfile.first()
        }
        assertNull(emittedProfile)
    }

    @Test
    fun `saveProfile should emit to SharedFlow when it's the current profile`() = runBlocking {
        // Create and load a profile
        val profile = profileManager.createProfile("test_player3", "Test Player 3")
        profileManager.loadProfile("test_player3")
        
        // Update the profile
        val updatedProfile = profile.copy(overallLevel = 5)
        profileManager.saveProfile(updatedProfile)
        
        // Collect the emission
        val emittedProfile = withTimeout(1000) {
            profileManager.currentProfile.first()
        }
        assertEquals(5, emittedProfile?.overallLevel)
    }

    @Test
    fun `updateCurrentProfile should update and emit the profile`() = runBlocking {
        // Create and load a profile
        profileManager.createProfile("test_player4", "Test Player 4")
        profileManager.loadProfile("test_player4")
        
        // Update current profile
        profileManager.updateCurrentProfile { profile ->
            profile.copy(overallLevel = 15)
        }
        
        // Verify the emission
        val emittedProfile = withTimeout(1000) {
            profileManager.currentProfile.first()
        }
        assertEquals(15, emittedProfile?.overallLevel)
    }

    @Test
    fun `new subscribers should get the last emitted value due to replay=1`() = runBlocking {
        // Create, load and update a profile
        profileManager.createProfile("test_player5", "Test Player 5")
        profileManager.loadProfile("test_player5")
        profileManager.updateCurrentProfile { it.copy(overallLevel = 50) }
        
        // New subscriber should get the last value immediately
        val profile = withTimeout(1000) {
            profileManager.currentProfile.first()
        }
        assertEquals(50, profile?.overallLevel)
    }

    @Test
    fun `profiles StateFlow should maintain all profiles`() = runBlocking {
        // Create multiple profiles
        profileManager.createProfile("test_player6", "Player 6")
        profileManager.createProfile("test_player7", "Player 7")
        profileManager.createProfile("test_player8", "Player 8")
        
        // Get all profiles
        val allProfiles = withTimeout(1000) {
            profileManager.profiles.first()
        }
        
        // At least these 3 should exist (there may be more from other tests)
        assertTrue(allProfiles.containsKey("test_player6"))
        assertTrue(allProfiles.containsKey("test_player7"))
        assertTrue(allProfiles.containsKey("test_player8"))
    }

    @Test
    fun `gainExperience should level up when threshold is reached`() {
        val profile = profileManager.createProfile("test_player9", "Player 9")
        
        // Gain enough experience to level up
        val updatedProfile = profile.gainExperience(1000)
        
        assertEquals(2, updatedProfile.overallLevel)
        assertEquals(1000, updatedProfile.overallExperience)
    }

    @Test
    fun `unlockAchievement should add achievement only once`() {
        val profile = profileManager.createProfile("test_player10", "Player 10")
        
        val profile1 = profile.unlockAchievement("FirstWin")
        assertTrue(profile1.achievements.contains("FirstWin"))
        
        // Try to unlock again
        val profile2 = profile1.unlockAchievement("FirstWin")
        assertEquals(1, profile2.achievements.size) // Should still be 1
    }
}
