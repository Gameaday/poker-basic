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
import com.pokermon.android.ui.theme.PokerGameTheme

/**
 * Main Android activity for the Poker Game.
 * Uses Jetpack Compose for modern Android UI.
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
                    PokerGameScreen()
                }
            }
        }
    }
}

@Composable
fun PokerGameScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🃏 Poker Game",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Mobile Edition v0.1b",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                // TODO: Start new game
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Game")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                // TODO: Show settings
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { 
                // TODO: Show about
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("About")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Android version of the cross-platform poker game.\nFull game logic integration coming soon!",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokerGameScreenPreview() {
    PokerGameTheme {
        PokerGameScreen()
    }
}