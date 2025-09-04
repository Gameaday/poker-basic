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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokermon.GameMode
import com.pokermon.android.data.UserProfileManager
import com.pokermon.android.ui.theme.PokerGameTheme
import com.pokermon.android.ui.theme.PokerTableTheme

/**
 * Main Android activity for the Pokermon Game.
 * Enhanced with comprehensive user profile integration and persistent theming.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val userProfileManager = remember { UserProfileManager.getInstance(context) }
            val gameSettings by userProfileManager.gameSettings.collectAsState()
            
            // Apply user's selected theme
            val selectedTheme = remember(gameSettings.selectedTheme) {
                try {
                    PokerTableTheme.valueOf(gameSettings.selectedTheme)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            
            PokerGameTheme(pokerTableTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokerGameNavigation()
                }
            }
        }
    }
}

@Composable
fun PokerGameNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main_menu"
    ) {
        composable("main_menu") {
            MainMenuScreen(navController = navController)
        }
        composable("game_mode_selection") {
            GameModeSelectionScreen(
                onGameModeSelected = { gameMode ->
                    navController.navigate("gameplay/${gameMode.name}")
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