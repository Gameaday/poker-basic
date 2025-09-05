package com.pokermon

import java.util.*

/**
 * Console-only version of the Pokermon game.
 * This class provides a pure text-based interface without any GUI dependencies.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
object ConsoleMain {
    
    private val scanner = Scanner(System.`in`)
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("=================================================")
        println("         ${Version.APP_NAME.uppercase()} - CONSOLE MODE")
        println("=================================================")
        println()
        
        // Variables
        var workingPot = 0
        val topBet = 0
        var countup = 0
        var Continue = true
        val Quit = false
        var players: Array<String>
        var USER: Player? = null
        var CPU1: Player? = null
        var CPU2: Player? = null
        var CPU3: Player? = null
        var list: Array<Player>
        
        // Print author information
        Main.author()
        
        // Prompt for game mode selection
        val selectedMode = promptGameMode()
        
        // Prompt user for a name
        val playerName = promptName()
        
        // Prompt for number of players to play against
        val playerCount = promptChallengers()
        
        // Prompt for starting chip quantity
        val chipsInitial = promptChips()
        
        // Set stuff up
        players = Array(playerCount + 1) { "" }
        list = Array<Player>(players.size) { Player() }
        
        // Add your name to list
        players[0] = playerName
        
        // Decide names for the Computer players
        Main.decideNames(players)
        
        // Check if monster mode is selected and handle accordingly
        if (selectedMode.hasMonsters()) {
            handleMonsterMode(selectedMode, playerName, playerCount, chipsInitial)
            return
        }
        
        Main.setupList(list, USER, CPU1, CPU2, CPU3)
        
        while (Continue) {
            // Initialize the Deck
            val Deck = Main.setDeck()
            if (countup < 1) {
                // Initialize players with hands, names, and chips
                Main.InitializePlayers(list, players, chipsInitial, Deck)
                countup = 1
            } else {
                Main.InitializePlayers(list, players, Deck)
            }
            
            // Show player his hand
            revealHand(list[0].getConvertedHand() ?: emptyArray())
            
            // Have player bet
            workingPot = bet(list, workingPot)
            
            // Report current pot value
            println("Current Pot Value: $workingPot")
            
            // Have player exchange cards
            Exchange(list[0], Deck)
            
            // Report new hand
            revealHand(list[0].getConvertedHand() ?: emptyArray())
            
            // Have player bet again
            workingPot = bet(list, workingPot)
            
            // Report current pot value
            println("Current Pot Value: $workingPot")
            
            // Declare results
            declareResults(list)
            
            // Divide the pot between the winner(s)
            Main.dividePot(list, workingPot)
            
            // Save updated stats to file
            Main.playersStats(list)
            
            Continue = promptEnd()
        }
        
        println("\nThank you for playing!")
        scanner.close()
    }
    
    /**
     * Console prompt for player name.
     */
    private fun promptName(): String {
        print("Enter a name for your player [A(n) Drew Hussie]: ")
        val input = scanner.nextLine().trim()
        return if (input.isEmpty()) "A(n) Drew Hussie" else input
    }
    
    /**
     * Console prompt for number of opponents.
     */
    private fun promptChallengers(): Int {
        print("How many computer players will you play against? (1-3) [2]: ")
        return try {
            val input = scanner.nextLine().trim()
            if (input.isEmpty()) return 2
            val count = input.toInt()
            if (count in 1..3) {
                count
            } else {
                println("Invalid input. Using default: 2")
                2
            }
        } catch (e: NumberFormatException) {
            println("Invalid input. Using default: 2")
            2
        }
    }
    
    /**
     * Console prompt for starting chips.
     */
    private fun promptChips(): Int {
        print("Select starting chip quantity (100, 500, 1000, 2000) [500]: ")
        return try {
            val input = scanner.nextLine().trim()
            if (input.isEmpty()) return 500
            val chips = input.toInt()
            val validChips = intArrayOf(100, 500, 1000, 2000)
            for (valid in validChips) {
                if (chips == valid) return chips
            }
            println("Invalid input. Using default: 500")
            500
        } catch (e: NumberFormatException) {
            println("Invalid input. Using default: 500")
            500
        }
    }
    
    /**
     * Console display of player's hand.
     */
    private fun revealHand(Hand: Array<String>) {
        println("\n" + "=".repeat(40))
        println("YOUR HAND:")
        for (i in Hand.indices) {
            println("${i + 1}: ${Hand[i]}")
        }
        println("=".repeat(40) + "\n")
    }
    
    /**
     * Console betting interface.
     */
    private fun bet(list: Array<Player>, workingPot: Int): Int {
        println("Your chips: ${list[0].chips}")
        print("How much will you bet? [0]: ")
        
        return try {
            val input = scanner.nextLine().trim()
            if (input.isEmpty()) return workingPot
            
            val betAmount = input.toInt()
            if (betAmount < 0) {
                println("Invalid bet amount. Betting 0.")
                return workingPot
            }
            
            val playerChips = list[0].chips
            val actualBet = if (betAmount > playerChips) {
                println("You don't have enough chips. Betting all your chips: $playerChips")
                playerChips
            } else {
                betAmount
            }
            
            list[0].placeBet(actualBet)
            workingPot + actualBet
            
        } catch (e: NumberFormatException) {
            println("Invalid input. Betting 0.")
            workingPot
        }
    }
    
    /**
     * Console card exchange interface.
     */
    private fun Exchange(player: Player, Deck: IntArray) {
        println("\nCard Exchange Phase")
        print("How many cards will you exchange? (0-5) [0]: ")
        
        try {
            val input = scanner.nextLine().trim()
            if (input.isEmpty()) return
            
            val numToExchange = input.toInt()
            if (numToExchange < 0 || numToExchange > 5) {
                println("Invalid number. No cards exchanged.")
                return
            }
            
            if (numToExchange == 0) return
            
            println("Enter the positions (1-5) of cards to exchange, separated by spaces:")
            val positionsInput = scanner.nextLine().trim()
            
            if (positionsInput.isEmpty()) return
            
            val positions = positionsInput.split("\\s+".toRegex())
            val exchangePositions = mutableSetOf<Int>()
            
            for (pos in positions) {
                try {
                    val position = pos.toInt() - 1 // Convert to 0-based index
                    if (position in 0..4) {
                        exchangePositions.add(position)
                    }
                } catch (e: NumberFormatException) {
                    // Ignore invalid positions
                }
            }
            
            if (exchangePositions.size != numToExchange) {
                println("Number of positions doesn't match requested exchange count. No cards exchanged.")
                return
            }
            
            // Exchange cards (simplified version)
            val hand = player.getHandForModification()
            if (hand != null) {
                for (position in exchangePositions) {
                    hand[position] = Main.drawCard(Deck)
                }
                
                player.updateHand(hand)
                player.convertHand()
            }
            
            println("Cards exchanged successfully!")
            
        } catch (e: NumberFormatException) {
            println("Invalid input. No cards exchanged.")
        }
    }
    
    /**
     * Console prompt to continue playing.
     */
    private fun promptEnd(): Boolean {
        print("\nWould you like to play again? (y/n) [y]: ")
        val input = scanner.nextLine().trim().lowercase()
        return input.isEmpty() || input == "y" || input == "yes"
    }
    
    /**
     * Console version of declare results without GUI dependencies.
     */
    private fun declareResults(list: Array<Player>): Boolean {
        // Find the winner using existing game logic
        var winnerIndex = -1
        var highestValue = 0
        var winnerCount = 0
        
        // Calculate hand values and find winner(s)
        for (i in list.indices) {
            list[i].calculateHandValue()
            val value = list[i].handValue
            
            if (value > highestValue) {
                highestValue = value
                winnerIndex = i
                winnerCount = 1
            } else if (value == highestValue) {
                winnerCount++
            }
        }
        
        println("\n" + "=".repeat(50))
        println("                GAME RESULTS")
        println("=".repeat(50))
        
        // Display all players' hands and values
        for (i in list.indices) {
            val hand = list[i].getConvertedHand() ?: emptyArray()
            println("${list[i].name} (Hand Value: ${list[i].handValue}):")
            for (card in hand) {
                println("  $card")
            }
            println()
        }
        
        return if (winnerCount == 1) {
            if (winnerIndex == 0) {
                println("🎉 CONGRATULATIONS! You won the hand! 🎉")
                false
            } else {
                println("😞 You lost the hand. Better luck next time!")
                println("Winner: ${list[winnerIndex].name}")
                true
            }
        } else {
            println("🤝 The game was a tie! Multiple players had the same hand value.")
            false
        }
    }
    
    /**
     * Console prompt for game mode selection.
     */
    private fun promptGameMode(): GameMode {
        println("\n" + "=".repeat(50))
        println("                GAME MODE SELECTION")
        println("=".repeat(50))
        println("Choose your game mode:")
        println("1. Classic Poker - Traditional 5-card draw with betting")
        println("2. Adventure Mode - Battle monsters with poker combat")
        println("3. Safari Mode - Capture monsters through poker gameplay")
        println("4. Ironman Mode - Convert winnings to monster gacha pulls")
        println()
        print("Enter your choice (1-4) [1]: ")
        
        return try {
            val input = scanner.nextLine().trim()
            if (input.isEmpty()) return GameMode.CLASSIC
            
            val choice = input.toInt()
            when (choice) {
                1 -> GameMode.CLASSIC
                2 -> GameMode.ADVENTURE
                3 -> GameMode.SAFARI
                4 -> GameMode.IRONMAN
                else -> {
                    println("Invalid choice. Using default: Classic Poker")
                    GameMode.CLASSIC
                }
            }
        } catch (e: NumberFormatException) {
            println("Invalid input. Using default: Classic Poker")
            GameMode.CLASSIC
        }
    }
    
    /**
     * Handles monster-based game modes.
     */
    private fun handleMonsterMode(mode: GameMode, playerName: String, playerCount: Int, chipsInitial: Int) {
        println("\n" + "=".repeat(50))
        println("          ${mode.displayName.uppercase()}")
        println("=".repeat(50))
        println(mode.description)
        println()
        
        when (mode) {
            GameMode.ADVENTURE -> {
                println("🏔️ Welcome to Adventure Mode, $playerName!")
                println("You'll battle monsters whose health equals their poker chip count.")
                println("Use your poker skills to defeat enemies and earn rewards!")
                println()
                launchAdventureMode(playerName, playerCount, chipsInitial)
            }
            GameMode.SAFARI -> {
                println("🌿 Welcome to Safari Mode, $playerName!")
                println("Encounter wild monsters during poker games.")
                println("Better hands increase your capture probability!")
                println()
                launchSafariMode(playerName, playerCount, chipsInitial)
            }
            GameMode.IRONMAN -> {
                println("🎰 Welcome to Ironman Mode, $playerName!")
                println("Convert your poker winnings into monster gacha pulls.")
                println("Higher winnings increase your chances of rare monsters!")
                println()
                launchIronmanMode(playerName, playerCount, chipsInitial)
            }
            else -> {
                println("Unknown game mode. Returning to classic poker.")
                return
            }
        }
    }
    
    /**
     * Adventure Mode implementation - Monster battles using poker combat.
     */
    private fun launchAdventureMode(playerName: String, playerCount: Int, chipsInitial: Int) {
        println("🏔️ Welcome to Adventure Mode, $playerName!")
        println("Battle monsters using poker hand strength as your weapon!")
        println()
        
        val adventure = AdventureMode(playerName, chipsInitial)
        adventure.startAdventure()
    }
    
    /**
     * Safari Mode implementation - Monster capture during poker games.
     */
    private fun launchSafariMode(playerName: String, playerCount: Int, chipsInitial: Int) {
        println("🚧 BETA FEATURE - Safari Mode")
        println()
        println("Safari Mode is currently under development for the beta milestone.")
        println("This mode will feature:")
        println("  • Random monster encounters during poker games")
        println("  • Capture probability based on poker performance")
        println("  • Monster collection and rarity distribution")
        println("  • Habitat variety and special encounter events")
        println()
        println("Implementation Status: Encounter system design complete")
        println("Expected in: Beta Milestone 1.2")
        println()
        print("Press Enter to continue with Classic Poker for now...")
        scanner.nextLine()
        
        // For now, fall back to classic poker with a note
        println("\nFalling back to Classic Poker...")
        // Re-launch classic mode
        main(emptyArray())
    }
    
    /**
     * Ironman Mode implementation - Gacha system using poker winnings.
     */
    private fun launchIronmanMode(playerName: String, playerCount: Int, chipsInitial: Int) {
        println("🚧 BETA FEATURE - Ironman Mode")
        println()
        println("Ironman Mode is currently under development for the beta milestone.")
        println("This mode will feature:")
        println("  • Gacha system converting chips to premium currency")
        println("  • Weighted random monster acquisition")
        println("  • Leaderboards and high score tracking")
        println("  • Limited-time events and special monsters")
        println()
        println("Implementation Status: Gacha mechanics designed")
        println("Expected in: Beta Milestone 1.3")
        println()
        print("Press Enter to continue with Classic Poker for now...")
        scanner.nextLine()
        
        // For now, fall back to classic poker with a note
        println("\nFalling back to Classic Poker...")
        // Re-launch classic mode
        main(emptyArray())
    }
}