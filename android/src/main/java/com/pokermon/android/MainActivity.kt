package com.pokermon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokermon.GameMode
import com.pokermon.android.ui.theme.PokerGameTheme

/**
 * Main Android activity for the Poker Game.
 * Uses Jetpack Compose with Navigation for modern Android UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokerGameTheme {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🃏 Pokermon",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Mobile Edition v1.0.0",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                navController.navigate("game_mode_selection")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Game")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                navController.navigate("settings")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { 
                navController.navigate("about")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("About")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Android version of Pokermon - the cross-platform poker game.\nNow with full game logic integration!",
            style = MaterialTheme.typography.bodyMedium
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