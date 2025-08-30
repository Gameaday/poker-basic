"""
Card rendering showcase - demonstrates visual card display functionality.
"""

from card import Card
from deck import Deck

def display_full_deck():
    """Display all cards in a nicely formatted layout."""
    print("Full 52-Card Deck with Visual Rendering")
    print("=" * 50)
    
    deck = Deck()
    
    for suit in Card.SUITS:
        print(f"\n{suit}:")
        suit_cards = [card for card in deck.cards if card.suit == suit]
        
        # Display cards in rows of 6
        for i in range(0, len(suit_cards), 6):
            row_cards = suit_cards[i:i+6]
            print("  " + "  ".join(card.display_card() for card in row_cards))

def display_poker_hands():
    """Display sample poker hands."""
    print("\n\nSample Poker Hands")
    print("=" * 30)
    
    hands = [
        ("Royal Flush", [Card('10', 'Hearts'), Card('J', 'Hearts'), Card('Q', 'Hearts'), Card('K', 'Hearts'), Card('A', 'Hearts')]),
        ("Four of a Kind", [Card('A', 'Hearts'), Card('A', 'Spades'), Card('A', 'Clubs'), Card('A', 'Diamonds'), Card('K', 'Hearts')]),
        ("Full House", [Card('K', 'Hearts'), Card('K', 'Spades'), Card('K', 'Clubs'), Card('Q', 'Hearts'), Card('Q', 'Spades')]),
        ("Flush", [Card('2', 'Hearts'), Card('5', 'Hearts'), Card('7', 'Hearts'), Card('9', 'Hearts'), Card('J', 'Hearts')]),
        ("Straight", [Card('5', 'Hearts'), Card('6', 'Spades'), Card('7', 'Clubs'), Card('8', 'Diamonds'), Card('9', 'Hearts')]),
        ("Two Pair", [Card('K', 'Hearts'), Card('K', 'Spades'), Card('Q', 'Hearts'), Card('Q', 'Clubs'), Card('J', 'Hearts')]),
        ("Pair", [Card('A', 'Hearts'), Card('A', 'Spades'), Card('K', 'Hearts'), Card('Q', 'Clubs'), Card('J', 'Diamonds')]),
        ("High Card", [Card('A', 'Hearts'), Card('K', 'Spades'), Card('Q', 'Hearts'), Card('J', 'Clubs'), Card('9', 'Diamonds')])
    ]
    
    for hand_name, cards in hands:
        hand_display = "  ".join(card.display_card() for card in cards)
        print(f"\n{hand_name:15}: {hand_display}")

def main():
    """Run card rendering showcase."""
    display_full_deck()
    display_poker_hands()
    
    print("\n\nCard rendering demonstration complete!")
    print("Cards are displayed with Unicode suit symbols for visual appeal.")

if __name__ == "__main__":
    main()