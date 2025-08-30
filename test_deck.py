"""
Test module for deck functionality.
"""

import pytest
from deck import Deck
from card import Card

class TestDeck:
    """Test cases for Deck class."""
    
    def test_deck_creation(self):
        """Test creating a new deck."""
        deck = Deck()
        assert len(deck) == 52
        assert deck.cards_remaining() == 52
        assert not deck.is_empty()
    
    def test_deck_contains_all_cards(self):
        """Test that deck contains all expected cards."""
        deck = Deck()
        
        # Check we have all combinations
        expected_cards = []
        for suit in Card.SUITS:
            for rank in Card.RANKS:
                expected_cards.append(Card(rank, suit))
        
        assert len(deck.cards) == len(expected_cards)
        
        # Check each expected card exists in deck
        for expected_card in expected_cards:
            found = any(card == expected_card for card in deck.cards)
            assert found, f"Missing card: {expected_card}"
    
    def test_deck_shuffle(self):
        """Test deck shuffling."""
        deck1 = Deck()
        deck2 = Deck()
        
        # Store original order
        original_order = [str(card) for card in deck1.cards]
        
        # Shuffle one deck
        deck2.shuffle()
        shuffled_order = [str(card) for card in deck2.cards]
        
        # Orders should be different (very unlikely to be same after shuffle)
        assert original_order != shuffled_order
        
        # But should contain same cards
        assert sorted(original_order) == sorted(shuffled_order)
    
    def test_deal_card(self):
        """Test dealing cards from deck."""
        deck = Deck()
        initial_count = len(deck)
        
        card = deck.deal_card()
        assert isinstance(card, Card)
        assert len(deck) == initial_count - 1
        assert deck.cards_remaining() == initial_count - 1
    
    def test_deal_from_empty_deck(self):
        """Test dealing from empty deck raises error."""
        deck = Deck()
        
        # Deal all cards
        while not deck.is_empty():
            deck.deal_card()
        
        # Now deck should be empty
        assert deck.is_empty()
        assert len(deck) == 0
        
        # Dealing from empty deck should raise error
        with pytest.raises(ValueError, match="Cannot deal from empty deck"):
            deck.deal_card()
    
    def test_deck_reset(self):
        """Test resetting deck to full state."""
        deck = Deck()
        
        # Deal some cards
        for _ in range(10):
            deck.deal_card()
        
        assert len(deck) == 42
        
        # Reset deck
        deck.reset()
        assert len(deck) == 52
        assert not deck.is_empty()
    
    def test_deck_string_representation(self):
        """Test string representation of deck."""
        deck = Deck()
        assert str(deck) == "Deck with 52 cards"
        
        # Deal some cards and check string updates
        deck.deal_card()
        assert str(deck) == "Deck with 51 cards"
    
    def test_multiple_shuffles_different_orders(self):
        """Test that multiple shuffles produce different orders."""
        deck = Deck()
        orders = []
        
        for _ in range(5):
            deck.shuffle()
            orders.append([str(card) for card in deck.cards])
        
        # All orders should be different (extremely unlikely to be same)
        for i, order1 in enumerate(orders):
            for j, order2 in enumerate(orders):
                if i != j:
                    assert order1 != order2, f"Shuffle {i} and {j} produced same order"