"""
Test module for card functionality.
"""

import pytest
from card import Card

class TestCard:
    """Test cases for Card class."""
    
    def test_card_creation(self):
        """Test creating valid cards."""
        card = Card('A', 'Hearts')
        assert card.rank == 'A'
        assert card.suit == 'Hearts'
    
    def test_invalid_rank(self):
        """Test creating card with invalid rank."""
        with pytest.raises(ValueError, match="Invalid rank"):
            Card('X', 'Hearts')
    
    def test_invalid_suit(self):
        """Test creating card with invalid suit."""
        with pytest.raises(ValueError, match="Invalid suit"):
            Card('A', 'Invalid')
    
    def test_card_string_representation(self):
        """Test string representation of card."""
        card = Card('K', 'Spades')
        assert str(card) == "K of Spades"
        assert repr(card) == "Card('K', 'Spades')"
    
    def test_card_equality(self):
        """Test card equality comparison."""
        card1 = Card('A', 'Hearts')
        card2 = Card('A', 'Hearts')
        card3 = Card('K', 'Hearts')
        
        assert card1 == card2
        assert card1 != card3
        assert card1 != "not a card"
    
    def test_card_value(self):
        """Test card value calculation."""
        card_2 = Card('2', 'Hearts')
        card_k = Card('K', 'Hearts')
        card_a = Card('A', 'Hearts')
        
        assert card_2.get_value() == 0
        assert card_k.get_value() == 11
        assert card_a.get_value() == 12
    
    def test_display_symbol(self):
        """Test card display symbols."""
        hearts = Card('A', 'Hearts')
        diamonds = Card('A', 'Diamonds')
        clubs = Card('A', 'Clubs')
        spades = Card('A', 'Spades')
        
        assert hearts.get_display_symbol() == '♥'
        assert diamonds.get_display_symbol() == '♦'
        assert clubs.get_display_symbol() == '♣'
        assert spades.get_display_symbol() == '♠'
    
    def test_display_card(self):
        """Test card display formatting."""
        card = Card('K', 'Hearts')
        assert card.display_card() == '[K♥]'
    
    def test_all_valid_combinations(self):
        """Test all valid rank and suit combinations."""
        for suit in Card.SUITS:
            for rank in Card.RANKS:
                card = Card(rank, suit)
                assert card.rank == rank
                assert card.suit == suit