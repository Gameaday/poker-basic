package com.pokermon.modern

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.scene.Node
import com.pokermon.bridge.GameLogicBridge
import com.pokermon.bridge.GameActionResult
import com.pokermon.GameMode

/**
 * Modern JavaFX-based UI for the poker game.
 * Provides a cross-platform interface with touch and mouse support.
 * 
 * @author Poker Game Team
 * @version 0.1b
 */
class ModernPokerApp : Application() {
    
    private val gameBridge = GameLogicBridge()
    private var primaryStage: Stage? = null
    
    // UI state
    private val potLabel = Label("Pot: $0")
    private val chipsLabel = Label("Chips: $1000")
    private val statusLabel = Label("Welcome to Modern Poker!")
    
    // Game setup controls
    private val playerNameField = TextField("Player")
    private val playerCountSlider = Slider(2.0, 4.0, 4.0).apply { 
        isShowTickLabels = true
        isShowTickMarks = true
        majorTickUnit = 1.0
        isSnapToTicks = true
    }
    private val startingChipsSlider = Slider(500.0, 5000.0, 1000.0).apply {
        isShowTickLabels = true
        majorTickUnit = 500.0
    }
    
    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        primaryStage.title = "Modern Poker Game - Cross-Platform Edition"
        
        // Create main scene
        val mainScene = createMainMenuScene()
        primaryStage.scene = mainScene
        primaryStage.width = 1000.0
        primaryStage.height = 700.0
        primaryStage.show()
    }
    
    /**
     * Creates the main menu scene.
     */
    private fun createMainMenuScene(): Scene {
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(50.0)
            style = "-fx-background-color: #0f4132;"
        }
        
        // Title
        val title = Label("🃏 Modern Poker Game").apply {
            style = "-fx-font-size: 36px; -fx-text-fill: white; -fx-font-weight: bold;"
        }
        
        val subtitle = Label("Cross-Platform Kotlin Edition").apply {
            style = "-fx-font-size: 16px; -fx-text-fill: #cccccc;"
        }
        
        // Menu buttons
        val newGameBtn = Button("🎮 New Game").apply {
            prefWidth = 200.0
            prefHeight = 50.0
            style = "-fx-font-size: 14px; -fx-background-color: #198754; -fx-text-fill: white;"
            setOnAction { showGameSetup() }
        }
        
        val settingsBtn = Button("⚙️ Settings").apply {
            prefWidth = 200.0
            prefHeight = 50.0
            style = "-fx-font-size: 14px; -fx-background-color: #6c757d; -fx-text-fill: white;"
            setOnAction { showSettings() }
        }
        
        val exitBtn = Button("❌ Exit").apply {
            prefWidth = 200.0
            prefHeight = 50.0
            style = "-fx-font-size: 14px; -fx-background-color: #dc3545; -fx-text-fill: white;"
            setOnAction { primaryStage?.close() }
        }
        
        // Version info
        val versionLabel = Label("Version 0.1b - Modern UI Edition").apply {
            style = "-fx-font-size: 12px; -fx-text-fill: #999999;"
        }
        
        root.children.addAll(
            title, subtitle,
            Region().apply { prefHeight = 30.0 }, // Spacer
            newGameBtn, settingsBtn, exitBtn,
            Region().apply { prefHeight = 30.0 }, // Spacer
            versionLabel
        )
        
        return Scene(root, 1000.0, 700.0)
    }
    
    /**
     * Shows the game setup screen.
     */
    private fun showGameSetup() {
        val root = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(30.0)
            style = "-fx-background-color: #0f4132;"
        }
        
        // Title
        val title = Label("🎯 Game Setup").apply {
            style = "-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;"
        }
        
        // Setup form
        val form = VBox(15.0).apply {
            alignment = Pos.CENTER
            maxWidth = 400.0
        }
        
        // Player name
        form.children.add(Label("Your Name:").apply {
            style = "-fx-text-fill: white; -fx-font-size: 14px;"
        })
        playerNameField.style = "-fx-font-size: 14px;"
        form.children.add(playerNameField)
        
        // Game Mode Selection
        form.children.add(Label("Game Mode:").apply {
            style = "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
        })
        
        val gameModeCombo = ComboBox<GameMode>().apply {
            items.addAll(GameMode.values())
            value = GameMode.CLASSIC
            style = "-fx-font-size: 14px;"
            setOnAction { 
                gameBridge.setGameMode(value)
            }
        }
        form.children.add(gameModeCombo)
        
        // Game mode description
        val modeDescription = Label(GameMode.CLASSIC.description).apply {
            style = "-fx-text-fill: #cccccc; -fx-font-size: 12px; -fx-wrap-text: true;"
            maxWidth = 350.0
        }
        gameModeCombo.setOnAction {
            modeDescription.text = gameModeCombo.value.description
            // Show additional setup for monster modes
            if (gameModeCombo.value.hasMonsters()) {
                statusLabel.text = "Monster mode selected! Enhanced gameplay with creature collection features."
            } else {
                statusLabel.text = "Classic poker mode selected."
            }
        }
        form.children.add(modeDescription)
        
        // Player count
        form.children.add(Label("Number of Players: ${playerCountSlider.value.toInt()}").apply {
            style = "-fx-text-fill: white; -fx-font-size: 14px;"
        })
        val playerCountLabel = form.children.last() as Label
        playerCountSlider.valueProperty().addListener { _, _, newValue ->
            playerCountLabel.text = "Number of Players: ${newValue.toInt()}"
        }
        form.children.add(playerCountSlider)
        
        // Starting chips
        form.children.add(Label("Starting Chips: ${startingChipsSlider.value.toInt()}").apply {
            style = "-fx-text-fill: white; -fx-font-size: 14px;"
        })
        val chipsLabel = form.children.last() as Label
        startingChipsSlider.valueProperty().addListener { _, _, newValue ->
            chipsLabel.text = "Starting Chips: ${newValue.toInt()}"
        }
        form.children.add(startingChipsSlider)
        
        // Buttons
        val buttonBox = HBox(10.0).apply {
            alignment = Pos.CENTER
        }
        
        val backBtn = Button("← Back").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #6c757d; -fx-text-fill: white;"
            setOnAction { primaryStage?.scene = createMainMenuScene() }
        }
        
        val startBtn = Button("Start Game →").apply {
            prefWidth = 120.0
            style = "-fx-font-size: 14px; -fx-background-color: #198754; -fx-text-fill: white;"
            setOnAction { startGame() }
        }
        
        buttonBox.children.addAll(backBtn, startBtn)
        
        root.children.addAll(
            title,
            Region().apply { prefHeight = 20.0 },
            form,
            Region().apply { prefHeight = 20.0 },
            buttonBox
        )
        
        primaryStage?.scene = Scene(root, 1000.0, 700.0)
    }
    
    /**
     * Starts the game with the configured settings.
     */
    private fun startGame() {
        val success = gameBridge.initializeGame(
            playerNameField.text.ifEmpty { "Player" },
            playerCountSlider.value.toInt(),
            startingChipsSlider.value.toInt()
        )
        
        if (success) {
            showGamePlay()
        } else {
            showAlert("Error", "Failed to initialize game!")
        }
    }
    
    /**
     * Shows the main gameplay screen.
     */
    private fun showGamePlay() {
        val root = BorderPane().apply {
            style = "-fx-background-color: #0f4132;"
        }
        
        // Top status bar
        val statusBar = HBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0)
            style = "-fx-background-color: #198754;"
        }
        
        potLabel.text = "Pot: $${gameBridge.getCurrentPot()}"
        chipsLabel.text = "Chips: $${gameBridge.getPlayerChips()}"
        
        potLabel.style = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"
        chipsLabel.style = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"
        
        statusBar.children.addAll(potLabel, chipsLabel)
        root.top = statusBar
        
        // Center game area
        val gameArea = VBox(20.0).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0)
        }
        
        // Player hand area
        val handLabel = Label("Your Hand:").apply {
            style = "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
        }
        
        val handArea = HBox(10.0).apply {
            alignment = Pos.CENTER
        }
        
        // Display cards
        val handCards = gameBridge.getPlayerHand()
        val selectedCards = gameBridge.getSelectedCards()
        
        handCards.forEachIndexed { index, card ->
            val cardBtn = Button(card).apply {
                prefWidth = 80.0
                prefHeight = 120.0
                val isSelected = selectedCards.contains(index)
                style = if (isSelected) {
                    "-fx-font-size: 14px; -fx-background-color: #ffd700; -fx-border-color: #ff6600; -fx-border-width: 3px; -fx-text-fill: black;"
                } else {
                    "-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: black; -fx-text-fill: black;"
                }
                setOnAction { 
                    // Toggle card selection
                    val nowSelected = gameBridge.toggleCardSelection(index)
                    style = if (nowSelected) {
                        "-fx-font-size: 14px; -fx-background-color: #ffd700; -fx-border-color: #ff6600; -fx-border-width: 3px; -fx-text-fill: black;"
                    } else {
                        "-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: black; -fx-text-fill: black;"
                    }
                }
            }
            handArea.children.add(cardBtn)
        }
        
        gameArea.children.addAll(handLabel, handArea)
        root.center = gameArea
        
        // Bottom control panel
        val controls = createGameControls()
        root.bottom = controls
        
        primaryStage?.scene = Scene(root, 1000.0, 700.0)
    }
    
    /**
     * Creates game control buttons.
     */
    private fun createGameControls(): Node {
        val controls = VBox(10.0).apply {
            padding = Insets(20.0)
            style = "-fx-background-color: #2d5016;"
        }
        
        // Action buttons
        val actionRow = HBox(10.0).apply {
            alignment = Pos.CENTER
        }
        
        val callBtn = Button("📞 Call").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #198754; -fx-text-fill: white;"
            setOnAction { performAction("call") }
        }
        
        val raiseBtn = Button("⬆️ Raise").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #fd7e14; -fx-text-fill: white;"
            setOnAction { performAction("raise") }
        }
        
        val checkBtn = Button("✅ Check").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #20c997; -fx-text-fill: white;"
            setOnAction { performAction("check") }
        }
        
        val foldBtn = Button("❌ Fold").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #dc3545; -fx-text-fill: white;"
            setOnAction { performAction("fold") }
        }
        
        actionRow.children.addAll(callBtn, raiseBtn, checkBtn, foldBtn)
        
        // Secondary buttons
        val secondRow = HBox(10.0).apply {
            alignment = Pos.CENTER
        }
        
        val exchangeBtn = Button("🔄 Exchange Cards").apply {
            prefWidth = 150.0
            style = "-fx-font-size: 14px; -fx-background-color: #6f42c1; -fx-text-fill: white;"
            setOnAction { performAction("exchange") }
        }
        
        val menuBtn = Button("🏠 Menu").apply {
            prefWidth = 100.0
            style = "-fx-font-size: 14px; -fx-background-color: #6c757d; -fx-text-fill: white;"
            setOnAction { primaryStage?.scene = createMainMenuScene() }
        }
        
        secondRow.children.addAll(exchangeBtn, menuBtn)
        
        // Status message
        statusLabel.style = "-fx-text-fill: white; -fx-font-size: 14px;"
        statusLabel.alignment = Pos.CENTER
        
        controls.children.addAll(actionRow, secondRow, statusLabel)
        return controls
    }
    
    /**
     * Performs a game action.
     */
    private fun performAction(action: String) {
        val result = when (action) {
            "call" -> gameBridge.performCall()
            "raise" -> showRaiseDialog()
            "check" -> gameBridge.performCheck()
            "fold" -> gameBridge.performFold()
            "exchange" -> {
                val selectedCards = gameBridge.getSelectedCards().toList()
                if (selectedCards.isEmpty()) {
                    return showAlert("No Selection", "Please select cards to exchange first.")
                }
                gameBridge.exchangeCards(selectedCards)
            }
            else -> return
        }
        
        statusLabel.text = result.message
        
        // Update UI state
        updateGameDisplay()
        
        // Refresh the game display to show updated cards
        if (action == "exchange") {
            showGamePlay()
        }
    }
    
    /**
     * Shows a dialog for raise amount input.
     */
    private fun showRaiseDialog(): GameActionResult {
        val dialog = TextInputDialog("100").apply {
            title = "Raise Amount"
            headerText = "Enter raise amount:"
            contentText = "Amount:"
        }
        
        val result = dialog.showAndWait()
        return if (result.isPresent) {
            try {
                val amount = result.get().toInt()
                if (amount > 0) {
                    gameBridge.performRaise(amount)
                } else {
                    GameActionResult(false, "Invalid raise amount")
                }
            } catch (e: NumberFormatException) {
                GameActionResult(false, "Invalid number format")
            }
        } else {
            GameActionResult(false, "Raise cancelled")
        }
    }
    
    /**
     * Updates the game display with current state.
     */
    private fun updateGameDisplay() {
        potLabel.text = "Pot: $${gameBridge.getCurrentPot()}"
        chipsLabel.text = "Chips: $${gameBridge.getPlayerChips()}"
    }
    
    /**
     * Shows the settings screen.
     */
    private fun showSettings() {
        val gameMode = gameBridge.getGameMode()
        val modeInfo = when (gameMode) {
            GameMode.CLASSIC -> "Standard poker gameplay"
            GameMode.ADVENTURE -> "Battle monsters in poker duels"
            GameMode.SAFARI -> "Capture monsters through poker"
            GameMode.IRONMAN -> "Convert winnings to monster gacha"
        }
        
        val alert = Alert(Alert.AlertType.INFORMATION).apply {
            title = "Settings"
            headerText = "Game Settings"
            contentText = """
                Graphics Settings:
                ✓ Modern UI enabled
                ✓ Touch/mouse support active
                ✓ Cross-platform compatibility
                
                Game Settings:
                ✓ Current Mode: ${gameMode.displayName}
                ✓ Mode Description: $modeInfo
                ✓ Auto-save enabled
                ✓ Hints enabled
                ✓ ${if (gameMode.hasMonsters()) "Monster system active" else "Standard poker rules"}
                
                Available Game Modes:
                • Classic Poker: Traditional gameplay
                • Adventure Mode: Battle monsters (chips = monster health)
                • Safari Mode: Capture monsters through poker success
                • Ironman Mode: Gacha system with rarity chances
                
                Platform: JavaFX/Kotlin
                Version: 0.1b
            """.trimIndent()
        }
        alert.showAndWait()
    }
    
    /**
     * Shows an alert dialog.
     */
    private fun showAlert(title: String, message: String) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            this.title = title
            headerText = null
            contentText = message
        }
        alert.showAndWait()
    }
    
    companion object {
        @JvmStatic
        fun launch() {
            Application.launch(ModernPokerApp::class.java)
        }
    }
}