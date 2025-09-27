package com.pokermon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokermon.GameMode
import com.pokermon.android.data.UserProfileManager
import com.pokermon.android.ui.theme.PokerGameTheme
import com.pokermon.android.ui.theme.PokerTableTheme
import kotlinx.coroutines.launch

/**
 * Temporary storage for saved game data during navigation.
 * This is a simple solution for passing complex objects through navigation.
 */
object GameplayNavigation {
    @Volatile
    var savedGameToLoad: com.pokermon.android.data.SavedGame? = null
}

/**
 * Main Android activity for the Pokermon Game.
 * Enhanced with Kotlin-native features: coroutines, flow, and modern state management.
 *
 * Features Kotlin-native enhancements:
 * - Coroutines for async operations
 * - StateFlow for reactive UI updates
 * - Lifecycle-aware state management
 * - Type-safe navigation with sealed classes
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use Kotlin coroutines for async initialization
        lifecycleScope.launch {
            initializeUserData()
        }

        setContent {
            val context = LocalContext.current
            val userProfileManager = remember { UserProfileManager.getInstance(context) }

            // Kotlin Flow integration for reactive UI
            val gameSettings by userProfileManager.gameSettings.collectAsState()

            // Type-safe theme selection with Kotlin when expression
            val selectedTheme: PokerTableTheme =
                remember(gameSettings.selectedTheme) {
                    when (gameSettings.selectedTheme) {
                        PokerTableTheme.CLASSIC_GREEN.name -> PokerTableTheme.CLASSIC_GREEN
                        PokerTableTheme.ROYAL_BLUE.name -> PokerTableTheme.ROYAL_BLUE
                        PokerTableTheme.CRIMSON_RED.name -> PokerTableTheme.CRIMSON_RED
                        PokerTableTheme.MIDNIGHT_BLACK.name -> PokerTableTheme.MIDNIGHT_BLACK
                        PokerTableTheme.BOURBON_BROWN.name -> PokerTableTheme.BOURBON_BROWN
                        else -> PokerTableTheme.CLASSIC_GREEN // Safe default
                    }
                }

            PokerGameTheme(pokerTableTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PokerGameNavigation(userProfileManager)
                }
            }
        }
    }

    /**
     * Kotlin coroutine function for async user data initialization.
     * Demonstrates Kotlin-native async patterns.
     */
    private suspend fun initializeUserData() {
        try {
            val userProfileManager = UserProfileManager.getInstance(this)

            // Use StateFlow.value for immediate access to current state
            val settings = userProfileManager.gameSettings.value

            // Kotlin-native null safety and smart casts
            settings.let { gameSettings ->
                val userProfile = userProfileManager.userProfile.value
                if (userProfile.username.isBlank()) {
                    // Initialize default settings if needed
                    val updatedProfile = userProfile.copy(username = "New Player")
                    userProfileManager.updateUserProfile(updatedProfile)
                }
            }
        } catch (e: Exception) {
            // Kotlin-style exception handling
            // Log error but don't crash the app
            android.util.Log.w("MainActivity", "Failed to initialize user data", e)
        }
    }
}

/**
 * Enhanced navigation with Kotlin-native sealed classes for type safety.
 * Demonstrates modern Kotlin patterns for Android navigation.
 */
sealed class NavigationRoute(val route: String) {
    object MainMenu : NavigationRoute("main_menu")

    object GameModeSelection : NavigationRoute("game_mode_selection")

    object Settings : NavigationRoute("settings")

    object About : NavigationRoute("about")

    object MonsterEncyclopedia : NavigationRoute("monster_encyclopedia")
    
    object Statistics : NavigationRoute("statistics")
    
    object SavedGames : NavigationRoute("saved_games")
    
    object Tutorial : NavigationRoute("tutorial")

    data class Gameplay(val gameMode: GameMode) : NavigationRoute("gameplay/${gameMode.name}")

    companion object {
        fun fromGameMode(gameMode: GameMode): Gameplay = Gameplay(gameMode)
    }
}

@Composable
fun PokerGameNavigation(userProfileManager: UserProfileManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.MainMenu.route,
    ) {
        composable(NavigationRoute.MainMenu.route) {
            MainMenuScreen(navController = navController)
        }
        composable(NavigationRoute.GameModeSelection.route) {
            GameModeSelectionScreen(
                onGameModeSelected = { gameMode ->
                    // Type-safe navigation using sealed classes
                    navController.navigate(NavigationRoute.fromGameMode(gameMode).route)
                },
                onBackPressed = { navController.popBackStack() },
            )
        }
        composable("gameplay/{gameMode}") { backStackEntry ->
            val gameModeString = backStackEntry.arguments?.getString("gameMode") ?: "CLASSIC"
            val gameMode =
                try {
                    GameMode.valueOf(gameModeString)
                } catch (e: IllegalArgumentException) {
                    GameMode.CLASSIC
                }

            GameplayScreen(
                gameMode = gameMode,
                savedGame = GameplayNavigation.savedGameToLoad,
                onBackPressed = {
                    // Clear the saved game data when exiting
                    GameplayNavigation.savedGameToLoad = null
                    navController.popBackStack("main_menu", false)
                },
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }
        composable("encyclopedia") {
            MonsterEncyclopediaScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }
        composable("about") {
            aboutScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }
        composable(NavigationRoute.Statistics.route) {
            StatisticsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }
        composable(NavigationRoute.SavedGames.route) {
            SavedGamesScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onLoadGame = { savedGame ->
                    // Store the saved game data for gameplay restoration
                    GameplayNavigation.savedGameToLoad = savedGame
                    // Navigate to gameplay with the saved game
                    try {
                        val gameMode = GameMode.valueOf(savedGame.gameMode)
                        navController.navigate(NavigationRoute.fromGameMode(gameMode).route)
                    } catch (e: Exception) {
                        navController.navigate(NavigationRoute.fromGameMode(GameMode.CLASSIC).route)
                    }
                }
            )
        }
        composable(NavigationRoute.Tutorial.route) {
            TutorialScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }
    val gameSaveManager = remember { com.pokermon.android.data.GameSaveManager.getInstance(context) }
    val userProfile by userProfileManager.userProfile.collectAsState()
    val savedGames by gameSaveManager.savedGames.collectAsState()
    
    val hasAutoSave = gameSaveManager.hasAutoSave()
    val hasSavedGames = savedGames.isNotEmpty() || hasAutoSave

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🐲 Pokermon",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Poker Monster Adventure",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome back message with user stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Welcome back, ${userProfile.username}!",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("🎮 Games: ${userProfile.totalGamesPlayed}")
                    Text("🏆 Wins: ${userProfile.gamesWon}")
                    Text("🏅 Achievements: ${userProfile.achievements.size}")
                }
                if (userProfile.monstersCollected > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🐲 Monsters: ${userProfile.monstersCollected}")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Game button (if saved games available)
        if (hasSavedGames) {
            Button(
                onClick = {
                    // Try to load the most recent game (auto-save first, then manual saves)
                    val autoSave = gameSaveManager.loadAutoSavedGame()
                    val mostRecentSave = autoSave ?: savedGames.firstOrNull()
                    
                    mostRecentSave?.let { save ->
                        GameplayNavigation.savedGameToLoad = save
                        try {
                            val gameMode = GameMode.valueOf(save.gameMode)
                            navController.navigate(NavigationRoute.fromGameMode(gameMode).route)
                        } catch (e: Exception) {
                            navController.navigate(NavigationRoute.fromGameMode(GameMode.CLASSIC).route)
                        }
                    } ?: run {
                        // Fallback to saved games screen if no direct save found
                        navController.navigate(NavigationRoute.SavedGames.route)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                val autoSave = gameSaveManager.loadAutoSavedGame()
                val buttonText = if (autoSave != null) {
                    "▶️ Continue Game (Auto-Save)"
                } else {
                    "▶️ Continue Game"
                }
                Text(buttonText)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                navController.navigate("game_mode_selection")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("🎮 New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Two-column layout for secondary buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate(NavigationRoute.Statistics.route)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("📊 Stats")
            }

            Button(
                onClick = {
                    navController.navigate(NavigationRoute.Tutorial.route)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("📚 Tutorial")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate("settings")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("⚙️ Settings")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("encyclopedia")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("🐲 Monster Encyclopedia")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("about")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("ℹ️ About")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    PokerGameTheme {
        MainMenuScreen(rememberNavController())
    }
}
