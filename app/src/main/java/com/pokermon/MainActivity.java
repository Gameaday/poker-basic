package com.pokermon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Android Activity for the Poker Game application.
 * Provides a mobile interface equivalent to the Swing GUI NewJFrame.
 * 
 * @author Poker Game Team
 * @version 0.08.30
 */
public class MainActivity extends Activity {
    
    private TextView statusText;
    private Game currentGame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupClickListeners();
    }
    
    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
    }
    
    private void setupClickListeners() {
        Button traditionalPokerBtn = findViewById(R.id.traditionalPokerBtn);
        Button threeCardPokerBtn = findViewById(R.id.threeCardPokerBtn);
        Button sevenCardStudBtn = findViewById(R.id.sevenCardStudBtn);
        Button adventureModeBtn = findViewById(R.id.adventureModeBtn);
        Button safariModeBtn = findViewById(R.id.safariModeBtn);
        Button ironmanModeBtn = findViewById(R.id.ironmanModeBtn);
        
        traditionalPokerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.TRADITIONAL);
            }
        });
        
        threeCardPokerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.THREE_CARD);
            }
        });
        
        sevenCardStudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.SEVEN_CARD);
            }
        });
        
        adventureModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.ADVENTURE);
            }
        });
        
        safariModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.SAFARI);
            }
        });
        
        ironmanModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.IRONMAN);
            }
        });
    }
    
    private void startGame(GameMode mode) {
        try {
            // Create game based on selected mode
            switch (mode) {
                case TRADITIONAL:
                    currentGame = new Game(); // Default 5-card poker
                    break;
                case THREE_CARD:
                    currentGame = Game.createThreeCardPoker();
                    break;
                case SEVEN_CARD:
                    currentGame = Game.createSevenCardStud();
                    break;
                case ADVENTURE:
                    currentGame = Game.createAdventureMode();
                    break;
                case SAFARI:
                    currentGame = Game.createSafariMode();
                    break;
                case IRONMAN:
                    currentGame = Game.createIronmanMode();
                    break;
                default:
                    currentGame = new Game();
            }
            
            // Update status
            statusText.setText("Starting " + mode.toString().toLowerCase().replace('_', ' ') + " poker...");
            
            // Show confirmation
            String message = "Started " + mode.toString().toLowerCase().replace('_', ' ') + " poker game!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            // TODO: Launch actual game interface
            // For now, just demonstrate that the core game logic works
            
        } catch (Exception e) {
            String errorMsg = "Failed to start game: " + e.getMessage();
            statusText.setText(errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }
}