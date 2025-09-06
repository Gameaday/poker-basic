package com.pokermon.database

/**
 * Manages a collection of monsters for a player.
 * This class handles monster inventory, active monster selection, and collection management.
 */
class MonsterCollection(
    private val maxActiveMonsters: Int = 1,
) {
    private val ownedMonsters: MutableList<Monster> = mutableListOf()
    private var activeMonster: Monster? = null

    init {
        require(maxActiveMonsters >= 0) { "Max active monsters cannot be negative" }
    }

    /**
     * Adds a monster to the collection.
     * @param monster the monster to add
     * @return true if the monster was added, false if it was already in the collection
     */
    fun addMonster(monster: Monster?): Boolean {
        monster ?: throw IllegalArgumentException("Monster cannot be null")

        if (monster in ownedMonsters) {
            return false // Already have this monster
        }

        ownedMonsters.add(monster)

        // If no active monster and we can have one, make this the active monster
        if (activeMonster == null && maxActiveMonsters > 0) {
            activeMonster = monster
        }

        return true
    }

    /**
     * Removes a monster from the collection.
     * @param monster the monster to remove
     * @return true if the monster was removed, false if it wasn't in the collection
     */
    fun removeMonster(monster: Monster?): Boolean {
        monster ?: return false

        val removed = ownedMonsters.remove(monster)

        // If we removed the active monster, clear it
        if (removed && monster == activeMonster) {
            activeMonster = null
        }

        return removed
    }

    /**
     * Sets the active monster for gameplay effects.
     * @param monster the monster to make active (must be in the collection)
     * @return true if the monster was set as active, false otherwise
     */
    fun setActiveMonster(monster: Monster?): Boolean {
        if (monster == null) {
            activeMonster = null
            return true
        }

        if (maxActiveMonsters == 0) {
            return false // No active monsters allowed
        }

        if (monster !in ownedMonsters) {
            return false // Don't own this monster
        }

        activeMonster = monster
        return true
    }

    /**
     * Gets the currently active monster.
     * @return the active monster, or null if none is active
     */
    fun getActiveMonster(): Monster? = activeMonster

    /**
     * Gets a list of all owned monsters.
     * @return an unmodifiable list of owned monsters
     */
    fun getOwnedMonsters(): List<Monster> = ownedMonsters.toList()

    /**
     * Gets the number of monsters in the collection.
     * @return the number of owned monsters
     */
    fun getMonsterCount(): Int = ownedMonsters.size

    /**
     * Checks if the collection contains a specific monster.
     * @param monster the monster to check for
     * @return true if the monster is in the collection
     */
    fun hasMonster(monster: Monster?): Boolean = monster != null && monster in ownedMonsters

    /**
     * Gets monsters of a specific rarity.
     * @param rarity the rarity to filter by
     * @return a list of monsters with the specified rarity
     */
    fun getMonstersByRarity(rarity: Monster.Rarity?): List<Monster> {
        rarity ?: return emptyList()

        return ownedMonsters.filter { it.rarity == rarity }
    }

    /**
     * Gets monsters with a specific effect type.
     * @param effectType the effect type to filter by
     * @return a list of monsters with the specified effect type
     */
    fun getMonstersByEffect(effectType: Monster.EffectType?): List<Monster> {
        effectType ?: return emptyList()

        return ownedMonsters.filter { it.effectType == effectType }
    }

    /**
     * Checks if there's an active monster with a specific effect type.
     * @param effectType the effect type to check for
     * @return true if the active monster has the specified effect type
     */
    fun hasActiveEffect(effectType: Monster.EffectType?): Boolean = activeMonster?.effectType == effectType

    /**
     * Gets the power of the active monster's effect.
     * @return the effective power of the active monster's effect, or 0 if no active monster
     */
    fun getActiveEffectPower(): Int = activeMonster?.effectivePower ?: 0

    /**
     * Clears all monsters from the collection.
     */
    fun clearCollection() {
        ownedMonsters.clear()
        activeMonster = null
    }

    override fun toString(): String =
        buildString {
            append("Monster Collection (${ownedMonsters.size} monsters):\n")

            activeMonster?.let { active ->
                append("Active: $active\n")
            }

            if (ownedMonsters.isNotEmpty()) {
                append("Owned Monsters:\n")
                ownedMonsters.forEach { monster ->
                    append("  - $monster")
                    if (monster == activeMonster) {
                        append(" [ACTIVE]")
                    }
                    append("\n")
                }
            } else {
                append("No monsters owned\n")
            }
        }
}
