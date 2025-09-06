package com.pokermon

/**
 * Defines the specific phases of a poker game for state-driven UI control.
 * This enum tracks the detailed game flow to enable precise UI behavior.
 * 
 * Game Flow:
 * INITIALIZATION → PLAYER_SETUP → DECK_CREATION → ROUND_START → 
 * HAND_DEALING → HAND_EVALUATION → BETTING_ROUND → PLAYER_ACTIONS → 
 * POT_MANAGEMENT → CARD_EXCHANGE → HAND_REEVALUATION → FINAL_BETTING → 
 * WINNER_DETERMINATION → POT_DISTRIBUTION → ROUND_END
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
enum class GamePhase(
    val displayName: String,
    val description: String,
    val showCards: Boolean,
    val allowsBetting: Boolean,
    val allowsCardExchange: Boolean,
    val allowsRoundProgression: Boolean
) {
    /**
     * Game is being initialized, no players yet set up.
     */
    INITIALIZATION(
        "Initializing game...", "Setting up game configuration", 
        false, false, false, false
    ),
    
    /**
     * Players are being set up with names and starting chips.
     */
    PLAYER_SETUP(
        "Setting up players...", "Configuring player information", 
        false, false, false, false
    ),
    
    /**
     * Deck is being created and shuffled.
     */
    DECK_CREATION(
        "Preparing deck...", "Shuffling cards", 
        false, false, false, false
    ),
    
    /**
     * New round is starting.
     */
    ROUND_START(
        "Starting new round", "Beginning round %d", 
        false, false, false, false
    ),
    
    /**
     * Cards are being dealt to all players.
     */
    HAND_DEALING(
        "Dealing cards...", "Distributing cards to players", 
        false, false, false, false
    ),
    
    /**
     * Initial hand evaluation is taking place.
     */
    HAND_EVALUATION(
        "Evaluating hands...", "Analyzing card values", 
        true, false, false, false
    ),
    
    /**
     * Betting round is active, players can make betting decisions.
     */
    BETTING_ROUND(
        "Betting round", "Place your bet", 
        true, true, false, false
    ),
    
    /**
     * Waiting for player actions during betting.
     */
    PLAYER_ACTIONS(
        "Your turn", "Choose your action: call, raise, or fold", 
        true, true, false, false
    ),
    
    /**
     * Managing pot after betting actions.
     */
    POT_MANAGEMENT(
        "Processing bets...", "Updating pot and player chips", 
        true, false, false, false
    ),
    
    /**
     * Card exchange phase - players can select cards to discard.
     */
    CARD_EXCHANGE(
        "Card exchange", "Select cards to exchange", 
        true, false, true, false
    ),
    
    /**
     * Re-evaluating hands after card exchange.
     */
    HAND_REEVALUATION(
        "Re-evaluating hands...", "Analyzing new card values", 
        true, false, false, false
    ),
    
    /**
     * Final betting round after card exchange.
     */
    FINAL_BETTING(
        "Final betting", "Final chance to bet", 
        true, true, false, false
    ),
    
    /**
     * Determining the winner of the round.
     */
    WINNER_DETERMINATION(
        "Determining winner...", "Comparing final hands", 
        true, false, false, false
    ),
    
    /**
     * Distributing the pot to the winner(s).
     */
    POT_DISTRIBUTION(
        "Distributing pot...", "Awarding chips to winner", 
        true, false, false, false
    ),
    
    /**
     * Round has ended, preparing for next round or game end.
     */
    ROUND_END(
        "Round complete", "Round finished. Continue or end game?", 
        true, false, false, true
    ),
    
    /**
     * Game has ended completely.
     */
    GAME_END(
        "Game over", "Thank you for playing!", 
        true, false, false, false
    );

    /**
     * Checks if cards should be visible to the player in this phase.
     * @return true if cards should be shown
     */
    fun shouldShowCards(): Boolean = showCards
    
    /**
     * Checks if betting actions should be available in this phase.
     * @return true if betting is allowed
     */
    fun allowsBetting(): Boolean = allowsBetting
    
    /**
     * Checks if card exchange should be available in this phase.
     * @return true if card exchange is allowed
     */
    fun allowsCardExchange(): Boolean = allowsCardExchange
    
    /**
     * Checks if round progression controls should be available in this phase.
     * @return true if round progression is allowed
     */
    fun allowsRoundProgression(): Boolean = allowsRoundProgression
    
    /**
     * Gets the next logical phase in the game flow.
     * @return the next phase, or null if this is the final phase
     */
    fun getNextPhase(): GamePhase? = when (this) {
        INITIALIZATION -> PLAYER_SETUP
        PLAYER_SETUP -> DECK_CREATION
        DECK_CREATION -> ROUND_START
        ROUND_START -> HAND_DEALING
        HAND_DEALING -> HAND_EVALUATION
        HAND_EVALUATION -> BETTING_ROUND
        BETTING_ROUND -> PLAYER_ACTIONS
        PLAYER_ACTIONS -> POT_MANAGEMENT
        POT_MANAGEMENT -> CARD_EXCHANGE
        CARD_EXCHANGE -> HAND_REEVALUATION
        HAND_REEVALUATION -> FINAL_BETTING
        FINAL_BETTING -> WINNER_DETERMINATION
        WINNER_DETERMINATION -> POT_DISTRIBUTION
        POT_DISTRIBUTION -> ROUND_END
        ROUND_END -> ROUND_START // or GAME_END
        GAME_END -> null
    }
    
    /**
     * Checks if this phase represents an active game state where players can interact.
     * @return true if the game is in an active state
     */
    fun isActivePhase(): Boolean = 
        this != INITIALIZATION && this != PLAYER_SETUP && 
        this != DECK_CREATION && this != GAME_END
    
    /**
     * Checks if this phase is part of the setup process.
     * @return true if this is a setup phase
     */
    fun isSetupPhase(): Boolean = 
        this == INITIALIZATION || this == PLAYER_SETUP || this == DECK_CREATION
    
    /**
     * Checks if this phase involves player decision making.
     * @return true if player input is expected
     */
    fun requiresPlayerInput(): Boolean = 
        this == BETTING_ROUND || this == PLAYER_ACTIONS || 
        this == CARD_EXCHANGE || this == ROUND_END
}