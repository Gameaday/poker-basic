"""
Test module for player functionality.
"""

import pytest
from unittest.mock import patch, MagicMock
from player import Player, HumanPlayer, AIPlayer
from card import Card

class TestPlayer:
    """Test cases for base Player class."""
    
    def test_player_initialization(self):
        """Test player initialization - can't test directly due to abstract class."""
        # Player is abstract, so we'll test through subclasses
        ai_player = AIPlayer("TestAI")
        assert ai_player.name == "TestAI"
        assert ai_player.chips == 1000
        assert ai_player.hand == []
        assert ai_player.current_bet == 0
        assert not ai_player.has_folded
        assert not ai_player.is_all_in
    
    def test_player_initialization_with_chips(self):
        """Test player initialization with custom chips."""
        player = AIPlayer("TestAI", chips=500)
        assert player.chips == 500
    
    def test_reset_for_new_hand(self):
        """Test resetting player for new hand."""
        player = AIPlayer("TestAI")
        
        # Set up some state
        player.add_card(Card('A', 'Hearts'))
        player.current_bet = 50
        player.has_folded = True
        player.is_all_in = True
        
        # Reset
        player.reset_for_new_hand()
        
        assert player.hand == []
        assert player.current_bet == 0
        assert not player.has_folded
        assert not player.is_all_in
    
    def test_add_card(self):
        """Test adding cards to player hand."""
        player = AIPlayer("TestAI")
        card = Card('K', 'Spades')
        
        player.add_card(card)
        assert len(player.hand) == 1
        assert player.hand[0] == card
    
    def test_get_hand_display_empty(self):
        """Test hand display with no cards."""
        player = AIPlayer("TestAI")
        assert player.get_hand_display() == "No cards"
    
    def test_get_hand_display_with_cards(self):
        """Test hand display with cards."""
        player = AIPlayer("TestAI")
        player.add_card(Card('A', 'Hearts'))
        player.add_card(Card('K', 'Spades'))
        
        display = player.get_hand_display()
        assert '[A♥]' in display
        assert '[K♠]' in display
    
    def test_place_bet_normal(self):
        """Test placing a normal bet."""
        player = AIPlayer("TestAI", chips=1000)
        
        bet_amount = player.place_bet(100)
        assert bet_amount == 100
        assert player.chips == 900
        assert player.current_bet == 100
        assert not player.is_all_in
    
    def test_place_bet_all_in(self):
        """Test placing bet that results in all-in."""
        player = AIPlayer("TestAI", chips=50)
        
        bet_amount = player.place_bet(100)  # Trying to bet more than available
        assert bet_amount == 50  # Should only bet what's available
        assert player.chips == 0
        assert player.current_bet == 50
        assert player.is_all_in
    
    def test_place_bet_exact_all_in(self):
        """Test placing bet for exact chip amount."""
        player = AIPlayer("TestAI", chips=100)
        
        bet_amount = player.place_bet(100)
        assert bet_amount == 100
        assert player.chips == 0
        assert player.current_bet == 100
        assert player.is_all_in
    
    def test_fold(self):
        """Test player folding."""
        player = AIPlayer("TestAI")
        
        player.fold()
        assert player.has_folded
    
    def test_can_bet_normal(self):
        """Test can_bet under normal conditions."""
        player = AIPlayer("TestAI", chips=100)
        assert player.can_bet()
    
    def test_can_bet_no_chips(self):
        """Test can_bet with no chips."""
        player = AIPlayer("TestAI", chips=0)
        assert not player.can_bet()
    
    def test_can_bet_folded(self):
        """Test can_bet when folded."""
        player = AIPlayer("TestAI")
        player.fold()
        assert not player.can_bet()
    
    def test_can_bet_all_in(self):
        """Test can_bet when all-in."""
        player = AIPlayer("TestAI")
        player.is_all_in = True
        assert not player.can_bet()
    
    def test_str_representation(self):
        """Test string representation of player."""
        player = AIPlayer("TestAI", chips=500)
        player.current_bet = 50
        
        result = str(player)
        assert "TestAI" in result
        assert "500 chips" in result
        assert "bet: 50" in result
    
    def test_str_representation_with_status(self):
        """Test string representation with folded/all-in status."""
        player = AIPlayer("TestAI")
        player.fold()
        player.is_all_in = True
        
        result = str(player)
        assert "FOLDED" in result
        assert "ALL-IN" in result


class TestAIPlayer:
    """Test cases for AIPlayer class."""
    
    def test_ai_player_initialization(self):
        """Test AI player initialization."""
        player = AIPlayer("AI1")
        assert player.name == "AI1"
        assert player.aggression == 0.5  # Default aggression
    
    def test_ai_player_initialization_with_aggression(self):
        """Test AI player initialization with custom aggression."""
        player = AIPlayer("AI1", aggression=0.8)
        assert player.aggression == 0.8
    
    def test_ai_player_aggression_clamping(self):
        """Test aggression is clamped between 0 and 1."""
        player1 = AIPlayer("AI1", aggression=-0.5)
        assert player1.aggression == 0.0
        
        player2 = AIPlayer("AI2", aggression=1.5)
        assert player2.aggression == 1.0
    
    def test_ai_decision_making(self):
        """Test AI decision making."""
        player = AIPlayer("AI1", chips=1000)
        player.add_card(Card('A', 'Hearts'))
        player.add_card(Card('K', 'Spades'))
        
        # Test with no current bet (should check or raise)
        decision, amount = player.make_decision(0, 10)
        assert decision in ['call', 'raise']
        
        # Test with small bet
        decision, amount = player.make_decision(10, 10)
        assert decision in ['call', 'raise', 'fold']
    
    def test_ai_hand_strength_evaluation(self):
        """Test AI hand strength evaluation."""
        player = AIPlayer("AI1")
        
        # Test with no cards
        strength = player._evaluate_hand_strength()
        assert strength == 0.5
        
        # Test with one card
        player.add_card(Card('A', 'Hearts'))
        strength = player._evaluate_hand_strength()
        assert 0.0 <= strength <= 1.0
        
        # Test with pair
        player.add_card(Card('A', 'Spades'))
        strength = player._evaluate_hand_strength()
        assert strength > 0.7  # Pair should have high strength


class TestHumanPlayer:
    """Test cases for HumanPlayer class."""
    
    def test_human_player_initialization(self):
        """Test human player initialization."""
        player = HumanPlayer("Human1")
        assert player.name == "Human1"
        assert player.chips == 1000
    
    @patch('builtins.input')
    def test_human_decision_fold(self, mock_input):
        """Test human player decision to fold."""
        mock_input.return_value = 'f'
        
        player = HumanPlayer("Human1")
        player.add_card(Card('2', 'Hearts'))
        player.add_card(Card('3', 'Clubs'))
        
        decision, amount = player.make_decision(50, 10)
        assert decision == 'fold'
        assert amount == 0
    
    @patch('builtins.input')
    def test_human_decision_call(self, mock_input):
        """Test human player decision to call."""
        mock_input.return_value = 'c'
        
        player = HumanPlayer("Human1", chips=1000)
        player.current_bet = 10
        
        decision, amount = player.make_decision(50, 10)
        assert decision == 'call'
        assert amount == 40  # 50 - 10 = 40 to call
    
    @patch('builtins.input')
    def test_human_decision_raise(self, mock_input):
        """Test human player decision to raise."""
        mock_input.side_effect = ['r', '20']
        
        player = HumanPlayer("Human1", chips=1000)
        player.current_bet = 10
        
        decision, amount = player.make_decision(50, 10)
        assert decision == 'raise'
        assert amount == 60  # 40 to call + 20 raise
    
    @patch('builtins.input')
    def test_human_decision_invalid_then_valid(self, mock_input):
        """Test human player with invalid input then valid."""
        mock_input.side_effect = ['x', 'invalid', 'f']
        
        player = HumanPlayer("Human1")
        
        decision, amount = player.make_decision(50, 10)
        assert decision == 'fold'
        assert amount == 0