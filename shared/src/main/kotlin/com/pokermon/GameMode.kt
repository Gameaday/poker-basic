package com.pokermon

/**
 * Defines the different game modes available in the poker game.
 * This enum supports the future expansion into monster-based gameplay.
 */
enum class GameMode(
    val displayName: String,
    val description: String,
) {
    /**
     * Traditional poker game mode - classic 5-card draw with betting.
     */
    CLASSIC(
        "Classic Poker",
        "Traditional poker gameplay with betting and card exchange",
    ),

    /**
     * Adventure mode - battle monsters whose health equals their chips.
     * Players must defeat enemies to progress and earn rewards.
     */
    ADVENTURE(
        "Adventure Mode",
        "Battle monsters in poker duels where their health equals their chips",
    ),

    /**
     * Safari mode - attempt to capture monsters through poker gameplay.
     * Success in capturing depends on poker performance and luck.
     */
    SAFARI(
        "Safari Mode",
        "Capture monsters through strategic poker gameplay",
    ),

    /**
     * Ironman mode - chips are converted to gacha currency for monster prizes.
     * Higher chip counts increase chances of rare monster rewards.
     */
    IRONMAN(
        "Ironman Mode",
        "Convert poker winnings into monster gacha pulls with rarity chances",
    ),
    ;

    /**
     * Checks if this game mode involves monster mechanics.
     * @return true if the mode uses monsters, false for classic poker
     */
    fun hasMonsters(): Boolean = this != CLASSIC
}
