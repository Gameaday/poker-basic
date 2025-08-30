"""
Test module for poker game functionality.
"""

import pytest
from unittest.mock import patch, MagicMock
from poker_game import PokerGame
from player import HumanPlayer, AIPlayer
from card import Card

class TestPokerGame:
    """Test cases for PokerGame class."""
    
    def test_game_initialization_default(self):
        """Test game initialization with default parameters."""
        game = PokerGame()
        
        assert len(game.players) == 4  # 1 human + 3 AI by default
        assert isinstance(game.players[0], HumanPlayer)
        assert all(isinstance(p, AIPlayer) for p in game.players[1:])
        assert game.pot == 0
        assert game.current_bet == 0
        assert game.minimum_bet == 10
        assert game.dealer_position == 0
    
    def test_game_initialization_custom(self):
        """Test game initialization with custom parameters."""
        game = PokerGame(human_players=2, ai_players=2, starting_chips=500)
        
        assert len(game.players) == 4
        assert sum(1 for p in game.players if isinstance(p, HumanPlayer)) == 2
        assert sum(1 for p in game.players if isinstance(p, AIPlayer)) == 2
        assert all(p.chips == 500 for p in game.players)
    
    def test_game_initialization_ai_names(self):
        """Test AI players get proper names."""
        game = PokerGame(human_players=0, ai_players=3)
        
        ai_players = [p for p in game.players if isinstance(p, AIPlayer)]
        names = [p.name for p in ai_players]
        
        assert "Alice" in names
        assert "Bob" in names
        assert "Charlie" in names
    
    def test_game_initialization_many_ai_players(self):
        """Test game with more AI players than predefined names."""
        game = PokerGame(human_players=0, ai_players=7)
        
        assert len(game.players) == 7
        ai_players = [p for p in game.players if isinstance(p, AIPlayer)]
        names = [p.name for p in ai_players]
        
        # Should have numbered versions for excess players
        assert any("2" in name for name in names)
    
    def test_setup_hand(self):
        """Test setting up a new hand."""
        game = PokerGame(human_players=0, ai_players=2)
        
        # Give players some previous state
        game.pot = 100
        game.current_bet = 50
        game.players[0].add_card(Card('A', 'Hearts'))
        game.players[0].current_bet = 25
        
        result = game.setup_hand()
        
        assert result is True
        assert game.pot == 0
        assert game.current_bet == 0
        assert len(game.deck) == 52
        assert all(len(p.hand) == 0 for p in game.players)
        assert all(p.current_bet == 0 for p in game.players)
    
    def test_setup_hand_insufficient_players(self):
        """Test setup hand with insufficient players (chips = 0)."""
        game = PokerGame(human_players=0, ai_players=2)
        
        # Set all players to 0 chips
        for player in game.players:
            player.chips = 0
        
        result = game.setup_hand()
        assert result is False
    
    def test_deal_cards(self):
        """Test dealing cards to players."""
        game = PokerGame(human_players=0, ai_players=3)
        game.setup_hand()
        
        initial_deck_size = len(game.deck)
        game.deal_cards(2)
        
        # Each player should have 2 cards
        assert all(len(p.hand) == 2 for p in game.players)
        
        # Deck should have 6 fewer cards (3 players * 2 cards)
        assert len(game.deck) == initial_deck_size - 6
    
    def test_deal_cards_custom_amount(self):
        """Test dealing custom number of cards."""
        game = PokerGame(human_players=0, ai_players=2)
        game.setup_hand()
        
        game.deal_cards(3)
        
        assert all(len(p.hand) == 3 for p in game.players)
    
    def test_betting_round_all_fold_except_one(self):
        """Test betting round where all but one player folds."""
        game = PokerGame(human_players=0, ai_players=3)
        game.setup_hand()
        
        # Make all but first player fold
        for player in game.players[1:]:
            player.fold()
        
        initial_pot = game.pot
        game.betting_round()
        
        # Should exit early when only one active player
        assert game.pot == initial_pot  # No new bets placed
    
    def test_betting_round_all_players_all_in(self):
        """Test betting round with all players all-in."""
        game = PokerGame(human_players=0, ai_players=2)
        game.setup_hand()
        
        # Make all players all-in
        for player in game.players:
            player.is_all_in = True
        
        game.betting_round()
        # Should handle all-in scenario without issues
    
    def test_determine_winner_single_player(self):
        """Test winner determination with single active player."""
        game = PokerGame(human_players=0, ai_players=3)
        game.setup_hand()
        game.pot = 100
        
        # Make all but one player fold
        for player in game.players[1:]:
            player.fold()
        
        winner_chips_before = game.players[0].chips
        game.determine_winner()
        
        assert game.players[0].chips == winner_chips_before + 100
    
    def test_determine_winner_showdown(self):
        """Test winner determination in showdown."""
        game = PokerGame(human_players=0, ai_players=2)
        game.setup_hand()
        game.pot = 100
        
        # Give players cards
        game.players[0].add_card(Card('A', 'Hearts'))  # High card
        game.players[0].add_card(Card('K', 'Spades'))
        
        game.players[1].add_card(Card('2', 'Clubs'))   # Low card
        game.players[1].add_card(Card('3', 'Diamonds'))
        
        winner_chips_before = game.players[0].chips
        game.determine_winner()
        
        # Player with Ace should win
        assert game.players[0].chips == winner_chips_before + 100
    
    def test_play_hand_normal_flow(self):
        """Test playing a complete hand."""
        game = PokerGame(human_players=0, ai_players=2)
        
        # Mock AI decisions to avoid infinite loops
        with patch.object(AIPlayer, 'make_decision', return_value=('fold', 0)):
            result = game.play_hand()
        
        # Should return True if game can continue
        assert isinstance(result, bool)
    
    def test_play_hand_insufficient_players(self):
        """Test play hand with insufficient players."""
        game = PokerGame(human_players=0, ai_players=1)
        
        # Set the only player to 0 chips
        game.players[0].chips = 0
        
        result = game.play_hand()
        assert result is False
    
    def test_display_game_state(self):
        """Test display game state functionality."""
        game = PokerGame(human_players=0, ai_players=2)
        game.pot = 150
        game.current_bet = 50
        
        # This should run without errors
        game.display_game_state()
    
    def test_dealer_position_rotation(self):
        """Test dealer position rotates properly."""
        game = PokerGame(human_players=0, ai_players=3)
        initial_dealer = game.dealer_position
        
        # Mock the game flow to test dealer rotation
        with patch.object(game, 'setup_hand', return_value=True), \
             patch.object(game, 'deal_cards'), \
             patch.object(game, 'display_game_state'), \
             patch.object(game, 'betting_round'), \
             patch.object(game, 'determine_winner'):
            
            game.play_hand()
        
        # Dealer position should have moved
        assert game.dealer_position == (initial_dealer + 1) % len(game.players)
    
    @patch('builtins.input')
    def test_play_game_quit_early(self, mock_input):
        """Test quitting game early."""
        mock_input.return_value = 'n'  # Don't continue
        
        game = PokerGame(human_players=1, ai_players=1)
        
        # Mock game methods to avoid actual game logic
        with patch.object(game, 'play_hand', return_value=True):
            game.play_game()
        
        # Should exit without errors
    
    def test_game_with_no_human_players(self):
        """Test game with only AI players."""
        game = PokerGame(human_players=0, ai_players=3)
        
        assert len(game.players) == 3
        assert all(isinstance(p, AIPlayer) for p in game.players)
        
        # Should be able to set up hand
        result = game.setup_hand()
        assert result is True
    
    def test_game_with_only_human_players(self):
        """Test game with only human players."""
        game = PokerGame(human_players=3, ai_players=0)
        
        assert len(game.players) == 3
        assert all(isinstance(p, HumanPlayer) for p in game.players)
    
    def test_betting_round_with_raises(self):
        """Test betting round with AI raises."""
        game = PokerGame(human_players=0, ai_players=2)
        game.setup_hand()
        
        # Mock AI players to participate in betting
        decision_count = 0
        def mock_decision(current_bet, minimum_bet):
            nonlocal decision_count
            decision_count += 1
            if decision_count == 1:
                return ('raise', 50)
            else:
                return ('call', 50)
        
        with patch.object(AIPlayer, 'make_decision', side_effect=mock_decision):
            game.betting_round()
        
        # Should have had some betting activity
        assert decision_count > 0