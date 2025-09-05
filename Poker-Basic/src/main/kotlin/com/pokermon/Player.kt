package com.pokermon

/**
 * Kotlin-native data class representing a poker player with their hand, chips, and game state.
 * Provides immutable state management with null safety and modern Kotlin patterns.
 * 
 * This replaces the Java Player class with a cleaner, more maintainable Kotlin implementation
 * that follows DRY principles and integrates seamlessly with the unified CardUtils.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
data class Player(
    var name: String = "",
    var chips: Int = 0,
    var isHuman: Boolean = false,
    private var _fold: Boolean = false,
    private var _lastBet: Int = 0,
    private var _bet: Int = 0,
    private var _hand: IntArray? = null,
    private var _handValue: Int = 0,
    
    // Public hand access for compatibility
    var hand: IntArray
        get() = _hand ?: IntArray(0)
        set(value) { _hand = value }
    private var _straight: Boolean = false,
    private var _aceStraight: Boolean = false,
    private var _flush: Boolean = false,
    private var _straightFlush: Boolean = false,
    private var _royalFlush: Boolean = false,
    private var _twoKind: Boolean = false,
    private var _twoPair: Boolean = false,
    private var _threeKind: Boolean = false,
    private var _fourKind: Boolean = false,
    private var _fullHouse: Boolean = false,
    
    // Converted hand representations
    private var _convertedHand: Array<String>? = null,
    private var _convertedHand2: Array<String>? = null,
    private var _handMultiples: Array<IntArray>? = null
) {
    
    // Getter properties with null safety
    val lastBet: Int get() = _lastBet
    val fold: Boolean get() = _fold
    val bet: Int get() = _bet
    val handValue: Int get() = _handValue
    
    // Hand evaluation getters
    val isStraight: Boolean get() = _straight
    val isAceStraight: Boolean get() = _aceStraight
    val isFlush: Boolean get() = _flush
    val isStraightFlush: Boolean get() = _straightFlush
    val isRoyalFlush: Boolean get() = _royalFlush
    val isTwoKind: Boolean get() = _twoKind
    val isTwoPair: Boolean get() = _twoPair
    val isThreeKind: Boolean get() = _threeKind
    val isFourKind: Boolean get() = _fourKind
    val isFullHouse: Boolean get() = _fullHouse
    
    // Safe getters that return copies to prevent external modification
    fun getHand(): IntArray? = _hand?.copyOf()
    fun getConvertedHand(): Array<String>? = _convertedHand?.copyOf()
    fun getConvertedHand2(): Array<String>? = _convertedHand2?.copyOf()
    fun getHandMultiples(): Array<IntArray>? = _handMultiples?.map { it.copyOf() }?.toTypedArray()
    
    // Setters with validation
    fun setLastBet(lastBet: Int) {
        this._lastBet = lastBet
    }
    
    fun setFold(fold: Boolean) {
        this._fold = fold
    }
    
    fun setPlayerName(playerName: String) {
        this.name = playerName
    }
    
    fun setPlayerChips(playerChips: Int) {
        this.chips = maxOf(0, playerChips) // Ensure non-negative
    }
    
    fun setHand(hand: IntArray?) {
        this._hand = hand?.copyOf()
        if (hand != null) {
            updateConvertedHands()
        }
    }
    
    fun setBet(bet: Int) {
        this._bet = maxOf(0, bet)
    }
    
    fun setHandValue(handValue: Int) {
        this._handValue = handValue
    }
    
    // Hand evaluation setters
    fun setStraight(straight: Boolean) { this._straight = straight }
    fun setAceStraight(aceStraight: Boolean) { this._aceStraight = aceStraight }
    fun setFlush(flush: Boolean) { this._flush = flush }
    fun setStraightFlush(straightFlush: Boolean) { this._straightFlush = straightFlush }
    fun setRoyalFlush(royalFlush: Boolean) { this._royalFlush = royalFlush }
    fun setTwoKind(twoKind: Boolean) { this._twoKind = twoKind }
    fun setTwoPair(twoPair: Boolean) { this._twoPair = twoPair }
    fun setThreeKind(threeKind: Boolean) { this._threeKind = threeKind }
    fun setFourKind(fourKind: Boolean) { this._fourKind = fourKind }
    fun setFullHouse(fullHouse: Boolean) { this._fullHouse = fullHouse }
    
    fun setConvertedHand(convertedHand: Array<String>?) {
        this._convertedHand = convertedHand?.copyOf()
    }
    
    fun setConvertedHand2(convertedHand2: Array<String>?) {
        this._convertedHand2 = convertedHand2?.copyOf()
    }
    
    fun setHandMultiples(handMultiples: Array<IntArray>?) {
        this._handMultiples = handMultiples?.map { it.copyOf() }?.toTypedArray()
    }
    
    /**
     * Reset betting state for a new round.
     */
    fun resetBet() {
        this._lastBet = 0
        this._bet = 0
    }
    
    /**
     * Record the last bet and reset current bet.
     */
    fun recordLastBet() {
        this._lastBet = this._bet
        this._bet = 0
    }
    
    /**
     * Update converted hand representations using unified CardUtils.
     * Follows DRY principles by using the single source of truth for card logic.
     */
    private fun updateConvertedHands() {
        _hand?.let { hand ->
            // Use unified CardUtils for consistent card conversion
            _convertedHand = hand.map { CardUtils.cardName(it) }.toTypedArray()
            
            // Convert hand multiples if available
            _handMultiples?.let { multiples ->
                _convertedHand2 = CardUtils.convertHand(multiples)
            }
        }
    }
    
    /**
     * Kotlin-native extension to get hand as card names.
     */
    fun getHandAsCardNames(): List<String> {
        return _hand?.let { CardUtils.convertCards(it) } ?: emptyList()
    }
    
    /**
     * Modern Kotlin method to place a bet with validation.
     */
    fun placeBet(amount: Int): Int {
        return if (amount <= chips && amount >= 0) {
            chips -= amount
            _bet += amount
            amount // Return the actual bet amount
        } else {
            0 // Return 0 if bet cannot be placed
        }
    }
    
    /**
     * Check if player can afford a bet.
     */
    fun canAfford(amount: Int): Boolean = amount <= chips
    
    /**
     * Go all-in with remaining chips.
     */
    fun goAllIn(): Int {
        val allInAmount = chips
        chips = 0
        _bet += allInAmount
        return allInAmount
    }
    
    /**
     * Set up player with name, chips, and initial hand.
     * Replaces the Java setupPlayer method with Kotlin implementation.
     */
    fun setupPlayer(playerName: String, playerChips: Int, deck: IntArray, handSize: Int) {
        name = playerName
        chips = playerChips
        _fold = false
        _bet = 0
        _lastBet = 0
        
        // Deal initial hand
        val newHand = IntArray(handSize)
        for (i in 0 until handSize) {
            newHand[i] = deck[i] // Simple dealing from deck
        }
        setHand(newHand)
    }
    
    /**
     * Reset fold status for a new round.
     */
    fun resetFold(): Boolean {
        _fold = false
        return fold
    }
    
    /**
     * Update hand with new cards and sort them.
     */
    fun updateHand(playerHand: IntArray?) {
        _hand = playerHand?.copyOf()
        _hand?.sort()
        updateConvertedHands()
    }
    
    /**
     * Add chips to player's total.
     */
    fun addChips(amount: Int) {
        chips += amount
    }
    
    /**
     * Remove card at specific index (set to 0).
     */
    fun removeCardAtIndex(index: Int) {
        _hand?.let { hand ->
            if (index >= 0 && index < hand.size) {
                hand[index] = 0
                updateConvertedHands()
            }
        }
    }
    
    /**
     * Get hand for direct modification (used by game logic).
     */
    fun getHandForModification(): IntArray? = _hand
    
    /**
     * Perform all hand evaluation checks.
     * Uses Main.java logic for compatibility during migration.
     */
    fun performAllChecks() {
        _hand?.let { hand ->
            // Use Main.java methods for now during migration
            try {
                // Convert hand using unified CardUtils
                convertHand()
                
                // Calculate hand value using existing logic
                _handValue = computeHandValue()
                
                // Evaluate hand types
                evaluateHandTypes()
            } catch (e: Exception) {
                println("Error performing hand checks: ${e.message}")
            }
        }
    }
    
    /**
     * Convert hand to reader-friendly format using CardUtils.
     */
    fun convertHand() {
        _hand?.let { hand ->
            _convertedHand = CardUtils.convertCards(hand).toTypedArray()
        }
    }
    
    /**
     * Calculate hand value using poker rules.
     */
    private fun computeHandValue(): Int {
        // For now, use simple high card value
        // This will be enhanced with proper poker hand evaluation
        return _hand?.maxOrNull() ?: 0
    }
    
    /**
     * Evaluate hand types (straight, flush, etc.).
     */
    private fun evaluateHandTypes() {
        _hand?.let { hand ->
            // Basic implementation for now
            // Will be enhanced with proper poker logic
            val sortedHand = hand.sorted()
            
            // Check for pairs, straights, flushes, etc.
            // This is a simplified version during migration
            val counts = hand.groupBy { it / 4 }.mapValues { it.value.size }
            
            _twoKind = counts.values.any { it == 2 }
            _threeKind = counts.values.any { it == 3 }
            _fourKind = counts.values.any { it == 4 }
            _twoPair = counts.values.count { it == 2 } == 2
            _fullHouse = _twoKind && _threeKind
        }
    }
    
    /**
     * Fold hand functionality.
     */
    fun foldHand(): Boolean {
        _fold = true
        return fold
    }
    
    // =============================================================================
    // COMPATIBILITY METHODS FOR LEGACY JAVA CODE (DRY PRINCIPLE COMPLIANCE)
    // =============================================================================
    
    /**
     * Legacy compatibility method for isFold() calls.
     * Ensures backward compatibility with existing Java code.
     */
    fun isFold(): Boolean = _fold
    
    /**
     * Legacy compatibility method for removeChips() calls.
     * Uses modern chip management while maintaining API compatibility.
     */
    fun removeChips(amount: Int): Int {
        return if (amount <= chips) {
            chips -= amount
            amount // Return actual amount removed
        } else {
            0 // Return 0 if couldn't remove chips
        }
    }
    
    /**
     * Legacy compatibility method for reportPlayer() calls.
     * Provides player status reporting for console interfaces.
     */
    fun reportPlayer(): String {
        return "Player: $name, Chips: $chips, Bet: $_bet, Folded: $_fold"
    }
    
    /**
     * Legacy compatibility constructor for simple Player creation.
     * Maintains backward compatibility while using modern Kotlin patterns.
     */
    constructor(name: String, chips: Int) : this(
        name = name,
        chips = chips,
        isHuman = false,
        _fold = false,
        _lastBet = 0,
        _bet = 0,
        _hand = null,
        _handValue = 0
    )
    
    /**
     * Legacy setupPlayer method with 3 parameters for backward compatibility.
     * Delegates to the full setupPlayer method with appropriate defaults.
     */
    fun setupPlayer(playerName: String, playerChips: Int, deck: IntArray) {
        setupPlayer(playerName, playerChips, deck, 5) // Default hand size
    }
    
    /**
     * Public calculateHandValue method for legacy compatibility.
     * Exposes hand value calculation for external use while maintaining encapsulation.
     */
    fun calculateHandValue(): Int {
        updateConvertedHands()
        _handValue = computeHandValue()
        return _handValue
    }
    
    // =============================================================================
    // END COMPATIBILITY METHODS
    // =============================================================================
    
    /**
     * Adjust chip count for returning players.
     */
    fun setChipsCurrentAgain(playerChips: Int) {
        chips = if (playerChips < 1) 200 else playerChips + 100
    }
    
    /**
     * Set chips current method for compatibility.
     */
    fun setChipsCurrent(playerChips: Int) {
        chips = playerChips
    }
    
    /**
     * Save player information for persistence (modernized).
     * Uses Kotlin-native approaches for file handling.
     */
    fun save() {
        try {
            // Modern Kotlin file operations would go here
            // For now, maintain compatibility with existing save system
            val file = java.io.File("$name.txt")
            file.appendText("Player: $name, Chips: $chips, Hand Value: $handValue\n")
        } catch (e: Exception) {
            println("Error saving player data: ${e.message}")
        }
    }
    
    // Override equals and hashCode for data class behavior with arrays
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Player
        
        return name == other.name &&
                chips == other.chips &&
                isHuman == other.isHuman &&
                _fold == other._fold &&
                _lastBet == other._lastBet &&
                _bet == other._bet &&
                _hand?.contentEquals(other._hand) == true
    }
    
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + chips
        result = 31 * result + isHuman.hashCode()
        result = 31 * result + _fold.hashCode()
        result = 31 * result + _lastBet
        result = 31 * result + _bet
        result = 31 * result + (_hand?.contentHashCode() ?: 0)
        return result
    }
    
    override fun toString(): String {
        return "Player(name='$name', chips=$chips, isHuman=$isHuman, fold=$_fold, handValue=$_handValue)"
    }
}