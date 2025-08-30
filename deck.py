"""
Deck class for managing a collection of playing cards.
"""

import random
from card import Card

class Deck:
    """Represents a deck of playing cards."""
    
    def __init__(self):
        """Initialize a standard 52-card deck."""
        self.cards = []
        self.reset()
    
    def reset(self):
        """Reset the deck to a full 52-card standard deck."""
        self.cards = []
        for suit in Card.SUITS:
            for rank in Card.RANKS:
                self.cards.append(Card(rank, suit))
    
    def shuffle(self):
        """Shuffle the deck randomly."""
        random.shuffle(self.cards)
    
    def deal_card(self):
        """Deal one card from the top of the deck."""
        if not self.cards:
            raise ValueError("Cannot deal from empty deck")
        return self.cards.pop()
    
    def cards_remaining(self):
        """Return number of cards remaining in deck."""
        return len(self.cards)
    
    def is_empty(self):
        """Check if deck is empty."""
        return len(self.cards) == 0
    
    def __len__(self):
        """Return number of cards in deck."""
        return len(self.cards)
    
    def __str__(self):
        """String representation of deck."""
        return f"Deck with {len(self.cards)} cards"