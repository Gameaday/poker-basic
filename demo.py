"""
Demo script to showcase card rendering and game functionality.
"""

from card import Card
from deck import Deck
from player import AIPlayer, HumanPlayer
from poker_game import PokerGame

def demo_card_display():
    """Demonstrate card display functionality."""
    print("=== Card Display Demo ===")
    print()
    
    # Create some sample cards
    cards = [
        Card('A', 'Hearts'),
        Card('K', 'Spades'),
        Card('Q', 'Diamonds'),
        Card('J', 'Clubs'),
        Card('10', 'Hearts')
    ]
    
    print("Sample cards with symbols:")
    for card in cards:
        print(f"  {card} -> {card.display_card()}")
    
    print()
    print("Full deck preview (first 13 cards):")
    deck = Deck()
    for i, card in enumerate(deck.cards[:13]):
        print(f"  {card.display_card()}", end="")
        if (i + 1) % 6 == 0:
            print()  # New line every 6 cards
    print("\n")

def demo_player_classes():
    """Demonstrate player class hierarchy and functionality."""
    print("=== Player Classes Demo ===")
    print()
    
    # Create different types of players
    human = HumanPlayer("Alice", chips=1000)
    ai_aggressive = AIPlayer("Bob", chips=1000, aggression=0.8)
    ai_conservative = AIPlayer("Charlie", chips=1000, aggression=0.3)
    
    players = [human, ai_aggressive, ai_conservative]
    
    print("Created players:")
    for player in players:
        print(f"  {player}")
        print(f"    Type: {type(player).__name__}")
        if isinstance(player, AIPlayer):
            print(f"    Aggression: {player.aggression}")
    
    print()
    
    # Give them some cards
    cards = [
        Card('A', 'Hearts'),
        Card('K', 'Spades'),
        Card('Q', 'Diamonds'),
        Card('J', 'Clubs'),
        Card('10', 'Hearts'),
        Card('9', 'Spades')
    ]
    
    for i, player in enumerate(players):
        player.add_card(cards[i * 2])
        player.add_card(cards[i * 2 + 1])
        print(f"{player.name}'s hand: {player.get_hand_display()}")
    
    print()

def demo_game_setup():
    """Demonstrate proper game setup with iteration through players."""
    print("=== Game Setup Demo ===")
    print()
    
    # Create a game with mixed player types
    print("Creating poker game with 1 human and 3 AI players...")
    game = PokerGame(human_players=1, ai_players=3, starting_chips=500)
    
    print(f"Game created with {len(game.players)} players:")
    for i, player in enumerate(game.players):
        marker = " (DEALER)" if i == game.dealer_position else ""
        print(f"  {i+1}. {player.name} ({type(player).__name__}) - {player.chips} chips{marker}")
    
    print()
    print("Setting up a hand...")
    game.setup_hand()
    
    print("Dealing cards...")
    game.deal_cards(2)
    
    print("\nPlayer hands after dealing:")
    for player in game.players:
        if isinstance(player, HumanPlayer):
            print(f"  {player.name}: {player.get_hand_display()}")
        else:
            print(f"  {player.name}: [Hidden AI hand - {len(player.hand)} cards]")
    
    print()
    game.display_game_state()

def demo_inheritance():
    """Demonstrate inheritance structure."""
    print("=== Inheritance Demo ===")
    print()
    
    human = HumanPlayer("Human")
    ai = AIPlayer("AI")
    
    print("Class hierarchy:")
    print(f"  HumanPlayer -> {HumanPlayer.__bases__}")
    print(f"  AIPlayer -> {AIPlayer.__bases__}")
    
    print()
    print("Polymorphism demonstration:")
    players = [human, ai]
    
    for player in players:
        print(f"  {player.name} ({type(player).__name__}):")
        print(f"    Can bet: {player.can_bet()}")
        print(f"    Hand display: {player.get_hand_display()}")
        
        # Add a card to show polymorphic behavior
        player.add_card(Card('A', 'Hearts'))
        print(f"    After adding card: {player.get_hand_display()}")
        player.reset_for_new_hand()

def main():
    """Run all demos."""
    print("Poker Basic - OOP Implementation Demo")
    print("=" * 50)
    print()
    
    demo_card_display()
    print()
    
    demo_player_classes()
    print()
    
    demo_inheritance()
    print()
    
    demo_game_setup()
    print()
    
    print("Demo complete!")
    print()
    print("To play an interactive game, run:")
    print("  python poker_game.py")

if __name__ == "__main__":
    main()