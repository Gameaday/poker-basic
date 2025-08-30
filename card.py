"""
Card class for representing individual playing cards.
"""

class Card:
    """Represents a single playing card."""
    
    SUITS = ['Hearts', 'Diamonds', 'Clubs', 'Spades']
    RANKS = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A']
    
    def __init__(self, rank, suit):
        """Initialize a card with rank and suit."""
        if rank not in self.RANKS:
            raise ValueError(f"Invalid rank: {rank}")
        if suit not in self.SUITS:
            raise ValueError(f"Invalid suit: {suit}")
            
        self.rank = rank
        self.suit = suit
    
    def __str__(self):
        """String representation of the card."""
        return f"{self.rank} of {self.suit}"
    
    def __repr__(self):
        """Official string representation of the card."""
        return f"Card('{self.rank}', '{self.suit}')"
    
    def __eq__(self, other):
        """Check equality between cards."""
        if not isinstance(other, Card):
            return False
        return self.rank == other.rank and self.suit == other.suit
    
    def get_value(self):
        """Get numerical value of the card for comparison."""
        return self.RANKS.index(self.rank)
    
    def get_display_symbol(self):
        """Get display symbol for the card suit."""
        symbols = {
            'Hearts': '♥',
            'Diamonds': '♦',
            'Clubs': '♣',
            'Spades': '♠'
        }
        return symbols[self.suit]
    
    def display_card(self):
        """Return formatted display string for the card."""
        symbol = self.get_display_symbol()
        return f"[{self.rank}{symbol}]"