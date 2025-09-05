package com.pokermon

/**
 * Represents the overall game state and configuration for a poker game.
 * This class manages game settings that can be customized for different variations.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
data class Game @JvmOverloads constructor(
    val handSize: Int = DEFAULT_HAND_SIZE,
    val maxPlayers: Int = MAX_PLAYERS,
    val startingChips: Int = DEFAULT_STARTING_CHIPS,
    val maxBettingRounds: Int = 2,
    val gameMode: GameMode = GameMode.CLASSIC,
    val enableMonsters: Boolean = false,
    val difficultyLevel: Int = 1
) {
    companion object {
        private const val DEFAULT_HAND_SIZE = 5
        private const val DEFAULT_STARTING_CHIPS = 1000
        private const val MAX_PLAYERS = 4
        private const val MIN_PLAYERS = 1
        
        /**
         * Creates a game configuration for 3-card poker.
         * @return a Game instance configured for 3-card poker
         */
        @JvmStatic
        fun createThreeCardPoker(): Game {
            return Game(handSize = 3, maxPlayers = 4, startingChips = 500, maxBettingRounds = 1)
        }
        
        /**
         * Creates a game configuration for 7-card stud.
         * @return a Game instance configured for 7-card stud
         */
        @JvmStatic
        fun createSevenCardStud(): Game {
            return Game(handSize = 7, maxPlayers = 4, startingChips = 1500, maxBettingRounds = 3)
        }
        
        /**
         * Creates a game configuration for heads-up (2-player) poker.
         * @return a Game instance configured for heads-up play
         */
        @JvmStatic
        fun createHeadsUp(): Game {
            return Game(handSize = 5, maxPlayers = 2, startingChips = 1000, maxBettingRounds = 2)
        }
        
        /**
         * Creates a game configuration for Adventure mode.
         * @return a Game instance configured for monster battles
         */
        @JvmStatic
        fun createAdventureMode(): Game {
            return Game(handSize = 5, maxPlayers = 4, startingChips = 1000, maxBettingRounds = 2, gameMode = GameMode.ADVENTURE)
        }
        
        /**
         * Creates a game configuration for Safari mode.
         * @return a Game instance configured for monster capturing
         */
        @JvmStatic
        fun createSafariMode(): Game {
            return Game(handSize = 5, maxPlayers = 4, startingChips = 1000, maxBettingRounds = 2, gameMode = GameMode.SAFARI)
        }
        
        /**
         * Creates a game configuration for Ironman mode.
         * @return a Game instance configured for gacha-style monster rewards
         */
        @JvmStatic
        fun createIronmanMode(): Game {
            return Game(handSize = 5, maxPlayers = 4, startingChips = 1000, maxBettingRounds = 2, gameMode = GameMode.IRONMAN)
        }
    }
    
    init {
        require(handSize in 1..10) { "Hand size must be between 1 and 10" }
        require(maxPlayers in MIN_PLAYERS..MAX_PLAYERS) { "Player count must be between $MIN_PLAYERS and $MAX_PLAYERS" }
        require(startingChips > 0) { "Starting chips must be positive" }
        require(maxBettingRounds > 0) { "Must have at least one betting round" }
    }
    
    /**
     * Validates if a player count is valid for this game.
     * @param playerCount the number of players to validate
     * @return true if valid, false otherwise
     */
    fun isValidPlayerCount(playerCount: Int): Boolean {
        return playerCount in MIN_PLAYERS..maxPlayers
    }
    
    override fun toString(): String {
        return "Game[mode=${gameMode.displayName}, handSize=$handSize, maxPlayers=$maxPlayers, startingChips=$startingChips, maxBettingRounds=$maxBettingRounds]"
    }
}