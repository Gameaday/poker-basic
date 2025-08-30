"""
Player classes for poker game - base Player class and subclasses.
"""

from abc import ABC, abstractmethod
import random
from card import Card

class Player(ABC):
    """Abstract base class for all players."""
    
    def __init__(self, name, chips=1000):
        """Initialize player with name and starting chips."""
        self.name = name
        self.chips = chips
        self.hand = []
        self.current_bet = 0
        self.has_folded = False
        self.is_all_in = False
    
    def reset_for_new_hand(self):
        """Reset player state for a new hand."""
        self.hand = []
        self.current_bet = 0
        self.has_folded = False
        self.is_all_in = False
    
    def add_card(self, card):
        """Add a card to the player's hand."""
        self.hand.append(card)
    
    def get_hand_display(self):
        """Get formatted display of player's hand."""
        if not self.hand:
            return "No cards"
        return " ".join([card.display_card() for card in self.hand])
    
    def place_bet(self, amount):
        """Place a bet, handling insufficient chips."""
        if amount >= self.chips:
            # All-in
            bet_amount = self.chips
            self.is_all_in = True
            self.chips = 0
        else:
            bet_amount = amount
            self.chips -= amount
        
        self.current_bet += bet_amount
        return bet_amount
    
    def fold(self):
        """Player folds their hand."""
        self.has_folded = True
    
    def can_bet(self):
        """Check if player can place a bet."""
        return self.chips > 0 and not self.has_folded and not self.is_all_in
    
    @abstractmethod
    def make_decision(self, current_bet, minimum_bet):
        """Abstract method for making betting decisions."""
        pass
    
    def __str__(self):
        """String representation of player."""
        status = []
        if self.has_folded:
            status.append("FOLDED")
        if self.is_all_in:
            status.append("ALL-IN")
        
        status_str = f" ({', '.join(status)})" if status else ""
        return f"{self.name}: {self.chips} chips, bet: {self.current_bet}{status_str}"


class HumanPlayer(Player):
    """Human player class - requires user input for decisions."""
    
    def make_decision(self, current_bet, minimum_bet):
        """Get decision from human player via input."""
        print(f"\n{self.name}'s turn:")
        print(f"Your hand: {self.get_hand_display()}")
        print(f"Your chips: {self.chips}")
        print(f"Current bet to call: {current_bet}")
        print(f"Your current bet: {self.current_bet}")
        
        if current_bet > self.current_bet:
            call_amount = current_bet - self.current_bet
            print(f"Amount to call: {call_amount}")
        else:
            call_amount = 0
        
        while True:
            try:
                action = input("Choose action (f)old, (c)all, (r)aise: ").lower().strip()
                
                if action == 'f':
                    return ('fold', 0)
                elif action == 'c':
                    return ('call', call_amount)
                elif action == 'r':
                    if call_amount >= self.chips:
                        print("You don't have enough chips to call, let alone raise!")
                        continue
                    
                    max_raise = self.chips - call_amount
                    raise_amount = int(input(f"Raise amount (max {max_raise}): "))
                    if raise_amount < minimum_bet:
                        print(f"Minimum raise is {minimum_bet}")
                        continue
                    if raise_amount > max_raise:
                        print(f"Maximum raise is {max_raise}")
                        continue
                    
                    return ('raise', call_amount + raise_amount)
                else:
                    print("Invalid action. Please choose 'f', 'c', or 'r'.")
            except (ValueError, KeyboardInterrupt):
                print("Invalid input. Please try again.")


class AIPlayer(Player):
    """AI player class with basic poker strategy."""
    
    def __init__(self, name, chips=1000, aggression=0.5):
        """Initialize AI player with aggression level (0-1)."""
        super().__init__(name, chips)
        self.aggression = max(0.0, min(1.0, aggression))  # Clamp between 0 and 1
    
    def make_decision(self, current_bet, minimum_bet):
        """AI decision making based on simple strategy."""
        if current_bet > self.current_bet:
            call_amount = current_bet - self.current_bet
        else:
            call_amount = 0
        
        # Simple AI strategy based on hand strength and aggression
        hand_strength = self._evaluate_hand_strength()
        
        # Adjust decision based on chips and bet size
        bet_ratio = call_amount / max(self.chips, 1)
        
        # Decision thresholds
        fold_threshold = 0.2 + (bet_ratio * 0.3)
        raise_threshold = 0.6 + (0.4 - self.aggression * 0.4)
        
        print(f"\n{self.name} (AI) is thinking...")
        print(f"Hand: {self.get_hand_display()}")
        
        if hand_strength < fold_threshold and call_amount > 0:
            print(f"{self.name} folds.")
            return ('fold', 0)
        elif hand_strength > raise_threshold and self.chips > call_amount + minimum_bet:
            # Calculate raise amount based on aggression and hand strength
            max_raise = self.chips - call_amount
            raise_multiplier = self.aggression * hand_strength
            raise_amount = min(int(minimum_bet * (1 + raise_multiplier * 3)), max_raise)
            raise_amount = max(raise_amount, minimum_bet)
            
            print(f"{self.name} raises by {raise_amount}.")
            return ('raise', call_amount + raise_amount)
        else:
            print(f"{self.name} calls.")
            return ('call', call_amount)
    
    def _evaluate_hand_strength(self):
        """Evaluate hand strength (simplified for basic poker)."""
        if len(self.hand) < 2:
            return 0.5
        
        # Very basic evaluation - just check for pairs and high cards
        values = [card.get_value() for card in self.hand]
        values.sort(reverse=True)
        
        # Check for pair
        if len(values) >= 2 and values[0] == values[1]:
            return 0.7 + (values[0] / len(Card.RANKS)) * 0.3
        
        # High card evaluation
        high_card_strength = values[0] / len(Card.RANKS)
        return 0.3 + high_card_strength * 0.4