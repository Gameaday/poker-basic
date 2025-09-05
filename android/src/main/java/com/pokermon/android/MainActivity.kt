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
import kotlinx.coroutines.flow.first

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
            val selectedTheme = remember(gameSettings.selectedTheme) {
                when (gameSettings.selectedTheme) {
                    PokerTableTheme.CLASSIC.name -> PokerTableTheme.CLASSIC
                    PokerTableTheme.NEON.name -> PokerTableTheme.NEON
                    PokerTableTheme.DARK.name -> PokerTableTheme.DARK
                    PokerTableTheme.FOREST.name -> PokerTableTheme.FOREST
                    else -> PokerTableTheme.CLASSIC // Safe default
                }
            }
            
            PokerGameTheme(pokerTableTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
            
            // Use Kotlin Flow operators for data processing
            val settings = userProfileManager.gameSettings.first()
            
            // Kotlin-native null safety and smart casts
            settings.let { gameSettings ->
                if (gameSettings.playerName.isBlank()) {
                    // Initialize default settings if needed
                    userProfileManager.updatePlayerName("New Player")
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
        startDestination = NavigationRoute.MainMenu.route
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
                onBack = { navController.popBackStack() }
            )
        }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("gameplay/{gameMode}") { backStackEntry ->
            val gameModeString = backStackEntry.arguments?.getString("gameMode") ?: "CLASSIC"
            val gameMode = try {
                GameMode.valueOf(gameModeString)
            } catch (e: IllegalArgumentException) {
                GameMode.CLASSIC
            }
            
            GameplayScreen(
                gameMode = gameMode,
                onBackPressed = {
                    navController.popBackStack("main_menu", false)
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("encyclopedia") {
            MonsterEncyclopediaScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("about") {
            AboutScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userProfileManager = remember { UserProfileManager.getInstance(context) }
    val userProfile by userProfileManager.userProfile.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🐲 Pokermon",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Poker Monster Adventure",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Mobile Edition v1.0.0",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Welcome back message with user stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome back, ${userProfile.username}!",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
        
        Button(
            onClick = { 
                navController.navigate("game_mode_selection")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎮 New Game")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                navController.navigate("settings")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⚙️ Settings")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                navController.navigate("encyclopedia")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🐲 Monster Encyclopedia")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { 
                navController.navigate("about")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ℹ️ About")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Android edition of Pokermon - where poker meets monster collecting! Battle through Adventure mode, discover creatures in Safari mode, and test your luck in Ironman gacha mode.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    PokerGameTheme {
        MainMenuScreen(rememberNavController())
    }
}