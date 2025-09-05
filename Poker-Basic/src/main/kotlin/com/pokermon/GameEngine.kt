package com.pokermon

/**
 * Manages the core game logic and flow for poker games.
 * This class provides a centralized way to handle game operations with improved reusability.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
class GameEngine(private val gameConfig: Game) {
    internal var players: Array<Player>? = null  // Made internal for bridge access
    private var deck: IntArray = intArrayOf()
    internal var currentPot: Int = 0
    internal var currentRound: Int = 0  // Made internal for bridge access
    private var gameActive: Boolean = false
    internal var currentPlayerIndex: Int = 0  // Made internal for bridge access
    internal var currentPhase: GamePhase = GamePhase.INITIALIZATION  // Made internal for bridge access
    
    /**
     * Initializes a new game with the specified players.
     * @param playerNames array of player names
     * @return true if initialization was successful
     */
    fun initializeGame(playerNames: Array<String>?): Boolean {
        if (playerNames == null || !gameConfig.isValidPlayerCount(playerNames.size)) {
            return false
        }
        
        // Phase: PLAYER_SETUP
        currentPhase = GamePhase.PLAYER_SETUP
        players = Array(playerNames.size) { Player() }
        currentPot = 0
        currentRound = 0
        
        // Phase: DECK_CREATION
        currentPhase = GamePhase.DECK_CREATION
        deck = Main.setDeck()
        
        // Initialize all players
        playerNames.forEachIndexed { i, name ->
            players!![i].apply {
                setHuman(i == 0) // First player is human, others are AI
                setupPlayer(name, gameConfig.startingChips, deck, gameConfig.handSize)
            }
        }
        
        gameActive = true
        currentPlayerIndex = 0 // Start with first player
        currentPhase = GamePhase.ROUND_START
        return true
    }
    
    /**
     * Starts a new round by dealing new hands to all players.
     */
    fun startNewRound() {
        if (!gameActive || players == null) {
            return
        }
        
        // Phase: ROUND_START
        currentPhase = GamePhase.ROUND_START
        
        // Reset deck for new round
        deck = Main.setDeck()
        currentPot = 0
        currentRound++
        
        // Phase: HAND_DEALING
        currentPhase = GamePhase.HAND_DEALING
        
        // Deal new hands to all active players
        players!!.forEach { player ->
            if (player.chips > 0) {
                player.resetFold() // Reset fold status for all players with chips
                player.updateHand(generateHand(deck, gameConfig.handSize))
                player.performAllChecks()
            }
        }
        
        // Phase: HAND_EVALUATION
        currentPhase = GamePhase.HAND_EVALUATION
        // Hand evaluation happens automatically in performAllChecks()
        
        // Move to betting phase
        currentPhase = GamePhase.BETTING_ROUND
    }
    
    /**
     * Generates a hand of the specified size from the deck.
     * @param deck the deck to draw from
     * @param handSize the number of cards to draw
     * @return an array representing the hand
     */
    private fun generateHand(deck: IntArray, handSize: Int): IntArray {
        return IntArray(handSize) { Main.drawCard(deck) }
    }
    
    /**
     * Conducts a betting round with all active players.
     * @return the total pot after betting
     */
    fun conductBettingRound(): Int {
        if (!gameActive || players == null) {
            return currentPot
        }
        
        // Ensure we're in the right phase for betting
        if (currentPhase == GamePhase.BETTING_ROUND) {
            currentPhase = GamePhase.PLAYER_ACTIONS
        }
        
        currentPot = Main.bet(players!!, currentPot)
        
        // Move to pot management phase after betting
        currentPhase = GamePhase.POT_MANAGEMENT
        
        return currentPot
    }
    
    /**
     * Allows a player to exchange cards (for draw poker variants).
     * @param playerIndex the index of the player
     * @param cardIndices the indices of cards to replace
     */
    fun exchangeCards(playerIndex: Int, cardIndices: IntArray?) {
        if (!gameActive || players == null || playerIndex !in players!!.indices) {
            return
        }
        
        // Ensure we're in card exchange phase
        if (currentPhase != GamePhase.CARD_EXCHANGE) {
            currentPhase = GamePhase.CARD_EXCHANGE
        }
        
        val player = players!![playerIndex]
        cardIndices?.forEach { index ->
            player.removeCardAtIndex(index)
        }
        
        Main.replaceCards(player.handForModification, deck)
        player.performAllChecks()
        
        // Note: Don't auto-advance phase here since multiple players might exchange cards
    }
    
    /**
     * Determines the winner(s) of the current round.
     * @return array of winning player indices
     */
    fun determineWinners(): IntArray {
        if (!gameActive || players == null) {
            return intArrayOf()
        }
        
        // Set to winner determination phase
        currentPhase = GamePhase.WINNER_DETERMINATION
        
        return intArrayOf(Main.decideWinner(players!!))
    }
    
    /**
     * Distributes the pot to the winner(s).
     * @param winners array of winning player indices
     */
    fun distributePot(winners: IntArray) {
        if (!gameActive || players == null || winners.isEmpty()) {
            return
        }
        
        // Set to pot distribution phase
        currentPhase = GamePhase.POT_DISTRIBUTION
        
        val potShare = currentPot / winners.size
        winners.forEach { winnerIndex ->
            if (winnerIndex in players!!.indices) {
                players!![winnerIndex].addChips(potShare)
            }
        }
        
        currentPot = 0
        
        // Move to round end phase
        currentPhase = GamePhase.ROUND_END
    }
    
    /**
     * Checks if the game should continue (more than one player with chips).
     * @return true if game can continue
     */
    fun canContinue(): Boolean {
        if (!gameActive || players == null) {
            return false
        }
        
        val playersWithChips = players!!.count { it.chips > 0 }
        return playersWithChips > 1
    }
    
    /**
     * Gets the current game state summary.
     * @return string representation of current game state
     */
    fun getGameState(): String {
        if (!gameActive || players == null) {
            return "Game not active"
        }
        
        return buildString {
            append("Round $currentRound, Pot: $currentPot\n")
            append("Players:\n")
            
            players!!.forEachIndexed { i, player ->
                append("  ${i + 1}. ${player.name} - Chips: ${player.chips}, Hand Value: ${player.handValue}, Folded: ${player.isFold()}\n")
            }
        }
    }
    
    /**
     * Gets the active players in the game.
     * @return array of players
     */
    fun getPlayers(): Array<Player> {
        return players?.clone() ?: emptyArray()
    }
    
    /**
     * Gets the current pot value for backward compatibility.
     * @return current pot amount  
     */
    fun getCurrentPot(): Int = currentPot
    
    /**
     * Gets the current round number.
     * @return current round
     */
    fun getCurrentRound(): Int = currentRound
    
    /**
     * Gets the game configuration.
     * @return the game configuration
     */
    fun getGameConfig(): Game = gameConfig
    
    /**
     * Checks if the game is currently active.
     * @return true if game is active
     */
    fun isGameActive(): Boolean = gameActive
    
    /**
     * Gets the current game phase.
     * @return the current game phase
     */
    fun getCurrentPhase(): GamePhase = currentPhase
    
    /**
     * Transitions to the next game phase.
     * @return true if transition was successful
     */
    fun advancePhase(): Boolean {
        val nextPhase = currentPhase.getNextPhase()
        return if (nextPhase != null) {
            currentPhase = nextPhase
            true
        } else {
            false
        }
    }
    
    /**
     * Manually sets the game phase (for testing or special cases).
     * @param phase the phase to set
     */
    fun setPhase(phase: GamePhase) {
        currentPhase = phase
    }
    
    /**
     * Transitions to card exchange phase.
     */
    fun beginCardExchange() {
        currentPhase = GamePhase.CARD_EXCHANGE
    }
    
    /**
     * Completes card exchange and moves to hand re-evaluation.
     */
    fun completeCardExchange() {
        currentPhase = GamePhase.HAND_REEVALUATION
        // Re-evaluate all hands after card exchange
        players?.forEach { player ->
            if (!player.isFold()) {
                player.performAllChecks()
            }
        }
        currentPhase = GamePhase.FINAL_BETTING
    }
    
    /**
     * Ends the current game.
     */
    fun endGame() {
        gameActive = false
        currentPhase = GamePhase.GAME_END
    }
    
    /**
     * Gets the current highest bet amount.
     * @return current high bet
     */
    fun getCurrentHighBet(): Int {
        if (!gameActive || players == null) {
            return 0
        }
        
        return players!!.maxOfOrNull { it.bet } ?: 0
    }
    
    /**
     * Sets the current highest bet amount.
     * @param amount the new high bet amount
     */
    fun setCurrentHighBet(amount: Int) {
        // This is tracked implicitly by player bets, but we can validate
        if (amount >= 0) {
            // The high bet is maintained by checking all player bets
        }
    }
    
    /**
     * Adds amount to the current pot.
     * @param amount the amount to add
     */
    fun addToPot(amount: Int) {
        if (amount > 0) {
            currentPot += amount
        }
    }
    
    /**
     * Gets the game deck.
     * @return the current deck
     */
    fun getDeck(): IntArray = deck.clone()
    
    /**
     * Advances to the next round.
     * @return true if successfully advanced
     */
    fun nextRound(): Boolean {
        if (!gameActive) {
            return false
        }
        
        currentRound++
        currentPlayerIndex = 0 // Reset to first player for new round
        // Reset bets for next round
        players?.forEach { it.resetBet() }
        return true
    }
    
    /**
     * Gets the current player's index.
     * @return the index of the current player
     */
    fun getCurrentPlayerIndex(): Int = currentPlayerIndex
    
    /**
     * Advances to the next active player.
     */
    fun nextPlayer() {
        if (!gameActive || players == null) {
            return
        }
        
        val startIndex = currentPlayerIndex
        var checkedPlayers = 0
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players!!.size
            checkedPlayers++
        } while (checkedPlayers < players!!.size &&
                 (players!![currentPlayerIndex].isFold() || players!![currentPlayerIndex].chips <= 0))
        
        // If no valid player found after checking all, set to -1
        if (checkedPlayers == players!!.size &&
            (players!![currentPlayerIndex].isFold() || players!![currentPlayerIndex].chips <= 0)) {
            currentPlayerIndex = -1
        }
    }
    
    /**
     * Gets the current player.
     * @return the current player or null if game not active
     */
    fun getCurrentPlayer(): Player? {
        if (!gameActive || players == null || currentPlayerIndex !in players!!.indices) {
            return null
        }
        return players!![currentPlayerIndex]
    }
    
    /**
     * Checks if the current round is complete.
     * @return true if round is complete
     */
    fun isRoundComplete(): Boolean {
        // A round is complete when all players have either folded or matched the highest bet
        if (!gameActive || players == null) {
            return true
        }
        
        val highBet = getCurrentHighBet()
        var activePlayers = 0
        var playersMatchingBet = 0
        var anyPlayerHasBet = false
        
        players!!.forEach { player ->
            if (!player.isFold() && player.chips > 0) {
                activePlayers++
                if (player.bet > 0) {
                    anyPlayerHasBet = true
                }
                if (player.bet >= highBet || player.chips == 0) {
                    playersMatchingBet++
                }
            }
        }
        
        // If no one has bet yet, the round is not complete
        if (!anyPlayerHasBet) {
            return false
        }
        
        return activePlayers <= 1 || playersMatchingBet == activePlayers
    }
}