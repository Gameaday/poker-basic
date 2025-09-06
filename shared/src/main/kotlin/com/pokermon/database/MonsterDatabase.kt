package com.pokermon.database

/**
 * Kotlin-native monster database serving as the single authoritative source for all monster data.
 *
 * This object provides a centralized, immutable collection of monsters with their attributes,
 * following DRY principles as the single source of truth for monster information across
 * all game modes and platforms.
 *
 * Features modern Kotlin patterns including data classes, sealed classes, and null safety
 * while maintaining full compatibility with existing game systems.
 *
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object MonsterDatabase {
    // Immutable collections for thread safety and DRY compliance
    private val monsterDatabase: Map<String, Monster>
    private val monstersByRarity: List<Monster>

    init {
        // Initialize with immutable collections
        val tempDatabase = mutableMapOf<String, Monster>()
        initializeMonsterDatabase(tempDatabase)

        monsterDatabase = tempDatabase.toMap()
        monstersByRarity = monsterDatabase.values.sortedBy { it.rarity.ordinal }
    }

    /**
     * Initialize the monster database with all available monsters.
     * Monsters follow a digital pixel aesthetic with diverse types and abilities.
     */
    private fun initializeMonsterDatabase(database: MutableMap<String, Monster>) {
        // Common Monsters (Digital Pets Theme)
        database.addMonster(
            Monster(
                name = "PixelPup",
                rarity = Monster.Rarity.COMMON,
                baseHealth = 100,
                effectType = Monster.EffectType.CHIP_BONUS,
                effectPower = 50,
                description = "A loyal digital companion that boosts your starting chips",
            ),
        )

        database.addMonster(
            Monster(
                name = "ByteBird",
                rarity = Monster.Rarity.COMMON,
                baseHealth = 80,
                effectType = Monster.EffectType.CARD_ADVANTAGE,
                effectPower = 1,
                description = "Swift pixelated flyer that grants an extra card draw",
            ),
        )

        database.addMonster(
            Monster(
                name = "CodeCat",
                rarity = Monster.Rarity.COMMON,
                baseHealth = 90,
                effectType = Monster.EffectType.LUCK_ENHANCEMENT,
                effectPower = 5,
                description = "Curious feline program that slightly improves your luck",
            ),
        )

        database.addMonster(
            Monster(
                name = "DataDog",
                rarity = Monster.Rarity.COMMON,
                baseHealth = 110,
                effectType = Monster.EffectType.BETTING_BOOST,
                effectPower = 10,
                description = "Faithful digital hound that enhances betting effectiveness",
            ),
        )

        // Uncommon Monsters (Elemental Digital Theme)
        database.addMonster(
            Monster(
                name = "FireFox.exe",
                rarity = Monster.Rarity.UNCOMMON,
                baseHealth = 150,
                effectType = Monster.EffectType.CHIP_BONUS,
                effectPower = 100,
                description = "Blazing browser spirit that significantly boosts starting chips",
            ),
        )

        database.addMonster(
            Monster(
                name = "AquaApp",
                rarity = Monster.Rarity.UNCOMMON,
                baseHealth = 140,
                effectType = Monster.EffectType.CARD_ADVANTAGE,
                effectPower = 2,
                description = "Fluid application that grants multiple extra draws",
            ),
        )

        database.addMonster(
            Monster(
                name = "TerraTerminal",
                rarity = Monster.Rarity.UNCOMMON,
                baseHealth = 160,
                effectType = Monster.EffectType.DEFENSIVE_SHIELD,
                effectPower = 20,
                description = "Grounded command line that provides protective barriers",
            ),
        )

        database.addMonster(
            Monster(
                name = "AirScript",
                rarity = Monster.Rarity.UNCOMMON,
                baseHealth = 130,
                effectType = Monster.EffectType.LUCK_ENHANCEMENT,
                effectPower = 10,
                description = "Ethereal code snippet that enhances fortune",
            ),
        )

        // Rare Monsters (Advanced Digital Entities)
        database.addMonster(
            Monster(
                name = "CyberDragon",
                rarity = Monster.Rarity.RARE,
                baseHealth = 200,
                effectType = Monster.EffectType.CHIP_BONUS,
                effectPower = 200,
                description = "Majestic digital dragon that grants substantial chip bonuses",
            ),
        )

        database.addMonster(
            Monster(
                name = "QuantumQuester",
                rarity = Monster.Rarity.RARE,
                baseHealth = 180,
                effectType = Monster.EffectType.CARD_ADVANTAGE,
                effectPower = 3,
                description = "Quantum-powered explorer that provides significant card advantages",
            ),
        )

        database.addMonster(
            Monster(
                name = "NeuralNet",
                rarity = Monster.Rarity.RARE,
                baseHealth = 170,
                effectType = Monster.EffectType.AI_ENHANCEMENT,
                effectPower = 15,
                description = "Self-learning network that improves decision-making algorithms",
            ),
        )

        // Epic Monsters (Legendary Digital Beings)
        database.addMonster(
            Monster(
                name = "CloudKeeper",
                rarity = Monster.Rarity.EPIC,
                baseHealth = 250,
                effectType = Monster.EffectType.CHIP_BONUS,
                effectPower = 300,
                description = "Guardian of the digital realm with immense power",
            ),
        )

        database.addMonster(
            Monster(
                name = "MatrixMaster",
                rarity = Monster.Rarity.EPIC,
                baseHealth = 230,
                effectType = Monster.EffectType.LUCK_ENHANCEMENT,
                effectPower = 25,
                description = "Master of the code matrix with reality-bending abilities",
            ),
        )

        // Legendary Monsters (Ultimate Digital Entities)
        database.addMonster(
            Monster(
                name = "AlgorithmicAncient",
                rarity = Monster.Rarity.LEGENDARY,
                baseHealth = 300,
                effectType = Monster.EffectType.ULTIMATE_POWER,
                effectPower = 50,
                description = "Ancient algorithmic entity with ultimate digital mastery",
            ),
        )
    }

    /**
     * Extension function for clean monster addition to database.
     */
    private fun MutableMap<String, Monster>.addMonster(monster: Monster) {
        this[monster.name] = monster
    }

    /**
     * Get a monster by name (null-safe).
     */
    fun getMonster(name: String?): Monster? {
        return if (name != null) monsterDatabase[name] else null
    }

    /**
     * Get all monsters as immutable list.
     */
    fun getAllMonsters(): List<Monster> = monsterDatabase.values.toList()

    /**
     * Get monsters by rarity.
     */
    fun getMonstersByRarity(rarity: Monster.Rarity): List<Monster> {
        return monstersByRarity.filter { it.rarity == rarity }
    }

    /**
     * Get random monster by rarity with Kotlin-native random.
     */
    fun getRandomMonsterByRarity(rarity: Monster.Rarity): Monster? {
        val monsters = getMonstersByRarity(rarity)
        return if (monsters.isNotEmpty()) monsters.random() else null
    }

    /**
     * Get all monster names for UI purposes.
     */
    fun getAllMonsterNames(): List<String> = monsterDatabase.keys.sorted()

    /**
     * Check if monster exists in database.
     */
    fun containsMonster(name: String?): Boolean {
        return name != null && monsterDatabase.containsKey(name)
    }

    /**
     * Get monsters by effect type for strategic selection.
     */
    fun getMonstersByEffectType(effectType: Monster.EffectType): List<Monster> {
        return monsterDatabase.values.filter { it.effectType == effectType }
    }

    /**
     * Get total number of monsters in database.
     */
    fun getMonsterCount(): Int = monsterDatabase.size

    /**
     * Get database statistics for UI display.
     */
    fun getDatabaseStats(): DatabaseStats {
        return DatabaseStats(
            totalMonsters = monsterDatabase.size,
            commonCount = getMonstersByRarity(Monster.Rarity.COMMON).size,
            uncommonCount = getMonstersByRarity(Monster.Rarity.UNCOMMON).size,
            rareCount = getMonstersByRarity(Monster.Rarity.RARE).size,
            epicCount = getMonstersByRarity(Monster.Rarity.EPIC).size,
            legendaryCount = getMonstersByRarity(Monster.Rarity.LEGENDARY).size,
            effectTypes = Monster.EffectType.values().toList(),
        )
    }

    /**
     * Java compatibility methods for legacy code during migration.
     */
    @JvmStatic
    fun getMonsterStatic(name: String?): Monster? = getMonster(name)

    @JvmStatic
    fun getAllMonstersStatic(): List<Monster> = getAllMonsters()

    @JvmStatic
    fun getRandomMonsterByRarityStatic(rarity: Monster.Rarity): Monster? = getRandomMonsterByRarity(rarity)

    // =============================================================================
    // ADDITIONAL METHODS FOR TEST COMPATIBILITY (DRY PRINCIPLE COMPLIANCE)
    // =============================================================================

    /**
     * Get total monster count for test validation.
     */
    fun getTotalMonsterCount(): Int = getMonsterCount()

    /**
     * Check if a monster exists by name.
     */
    fun hasMonster(name: String?): Boolean = getMonster(name) != null

    /**
     * Get random monster using provided Random instance.
     */
    fun getRandomMonster(random: java.util.Random): Monster? {
        val monsters = getAllMonsters()
        return if (monsters.isNotEmpty()) {
            monsters[random.nextInt(monsters.size)]
        } else {
            null
        }
    }

    /**
     * Get monster names starting with specified prefix.
     */
    fun getMonsterNamesStartingWith(prefix: String): List<String> {
        return monsterDatabase.keys.filter { it.startsWith(prefix, ignoreCase = true) }.sorted()
    }
}

/**
 * Data class for database statistics.
 */
data class DatabaseStats(
    val totalMonsters: Int,
    val commonCount: Int,
    val uncommonCount: Int,
    val rareCount: Int,
    val epicCount: Int,
    val legendaryCount: Int,
    val effectTypes: List<Monster.EffectType>,
)
