"""
Main poker game class that manages game flow and player interactions.
"""

from deck import Deck
from player import Player, HumanPlayer, AIPlayer

class PokerGame:
    """Main poker game class managing game flow."""
    
    def __init__(self, human_players=1, ai_players=3, starting_chips=1000):
        """Initialize poker game with specified number of players."""
        self.deck = Deck()
        self.players = []
        self.pot = 0
        self.current_bet = 0
        self.minimum_bet = 10
        self.dealer_position = 0
        
        # Create human players
        for i in range(human_players):
            name = f"Player {i + 1}" if human_players > 1 else "You"
            self.players.append(HumanPlayer(name, starting_chips))
        
        # Create AI players with varying aggression levels
        ai_names = ["Alice", "Bob", "Charlie", "Diana", "Eve"]
        for i in range(ai_players):
            name = ai_names[i % len(ai_names)]
            if i >= len(ai_names):
                name += f" {i // len(ai_names) + 1}"
            
            # Vary aggression levels for more interesting gameplay
            aggression = 0.3 + (i * 0.2) % 0.7
            self.players.append(AIPlayer(name, starting_chips, aggression))
    
    def setup_hand(self):
        """Set up a new hand - shuffle deck and reset players."""
        print("\n" + "="*50)
        print("NEW HAND")
        print("="*50)
        
        self.deck.reset()
        self.deck.shuffle()
        self.pot = 0
        self.current_bet = 0
        
        # Reset all players for new hand
        for player in self.players:
            player.reset_for_new_hand()
        
        # Remove players with no chips
        self.players = [p for p in self.players if p.chips > 0]
        
        if len(self.players) < 2:
            return False
        
        return True
    
    def deal_cards(self, cards_per_player=2):
        """Deal cards to all players."""
        print(f"\nDealing {cards_per_player} cards to each player...")
        
        for _ in range(cards_per_player):
            for player in self.players:
                if self.deck.cards_remaining() > 0:
                    player.add_card(self.deck.deal_card())
        
        # Show all players' cards (in a real game, only show to individual players)
        print("\nPlayer hands:")
        for player in self.players:
            if isinstance(player, HumanPlayer):
                print(f"{player.name}: {player.get_hand_display()}")
            else:
                print(f"{player.name}: [Hidden AI hand - {len(player.hand)} cards]")
    
    def betting_round(self):
        """Conduct a betting round with all active players."""
        print(f"\n--- Betting Round (Current bet: {self.current_bet}) ---")
        
        active_players = [p for p in self.players if not p.has_folded and not p.is_all_in]
        
        if len(active_players) <= 1:
            return
        
        # Continue betting until all active players have matched the current bet
        betting_complete = False
        last_raiser = None
        players_acted = set()  # Track which players have acted in this round
        
        while not betting_complete:
            all_called = True
            round_had_action = False
            
            for player in self.players:
                if player.has_folded or player.is_all_in:
                    continue
                
                # Player needs to act if:
                # 1. They haven't matched the current bet, OR
                # 2. They are the last raiser and others need to respond, OR
                # 3. This is the first round and no one has acted yet
                needs_to_act = (
                    player.current_bet < self.current_bet or 
                    player == last_raiser or
                    (self.current_bet == 0 and player not in players_acted)
                )
                
                if needs_to_act:
                    # Special check for last raiser - only act if others haven't matched
                    if player == last_raiser and all(
                        p.current_bet == self.current_bet or p.has_folded or p.is_all_in 
                        for p in self.players if p != player
                    ):
                        continue
                    
                    print(f"\nCurrent pot: {self.pot}")
                    decision, amount = player.make_decision(self.current_bet, self.minimum_bet)
                    players_acted.add(player)
                    round_had_action = True
                    
                    if decision == 'fold':
                        player.fold()
                        print(f"{player.name} folds.")
                    
                    elif decision == 'call':
                        if amount > 0:
                            actual_bet = player.place_bet(amount)
                            self.pot += actual_bet
                            print(f"{player.name} calls with {actual_bet} chips.")
                        else:
                            print(f"{player.name} checks.")
                    
                    elif decision == 'raise':
                        actual_bet = player.place_bet(amount)
                        self.pot += actual_bet
                        self.current_bet = player.current_bet
                        last_raiser = player
                        all_called = False
                        players_acted.clear()  # Reset acted players after a raise
                        players_acted.add(player)
                        print(f"{player.name} raises! New bet: {self.current_bet}")
                
                # Check if only one player remains
                active_players = [p for p in self.players if not p.has_folded]
                if len(active_players) <= 1:
                    return
            
            # Check if betting is complete
            active_betting_players = [p for p in self.players if not p.has_folded and not p.is_all_in]
            if len(active_betting_players) <= 1:
                betting_complete = True
            elif not round_had_action:
                # No one acted this round, betting is complete
                betting_complete = True
            else:
                betting_complete = all(
                    p.current_bet == self.current_bet or p.has_folded or p.is_all_in 
                    for p in self.players
                )
    
    def determine_winner(self):
        """Determine winner and distribute pot."""
        active_players = [p for p in self.players if not p.has_folded]
        
        if len(active_players) == 1:
            winner = active_players[0]
            print(f"\n{winner.name} wins the pot of {self.pot} chips!")
            winner.chips += self.pot
        else:
            # For simplicity, just award to player with highest card
            # In a real poker game, this would evaluate poker hands
            best_player = None
            best_value = -1
            
            print("\n--- Showdown ---")
            for player in active_players:
                print(f"{player.name}: {player.get_hand_display()}")
                max_card_value = max(card.get_value() for card in player.hand)
                if max_card_value > best_value:
                    best_value = max_card_value
                    best_player = player
            
            print(f"\n{best_player.name} wins with highest card!")
            print(f"Pot of {self.pot} chips goes to {best_player.name}")
            best_player.chips += self.pot
    
    def display_game_state(self):
        """Display current game state."""
        print(f"\n--- Game State ---")
        print(f"Pot: {self.pot} chips")
        print(f"Current bet: {self.current_bet}")
        print("Players:")
        for i, player in enumerate(self.players):
            marker = " (DEALER)" if i == self.dealer_position else ""
            print(f"  {player}{marker}")
    
    def play_hand(self):
        """Play a single hand of poker."""
        if not self.setup_hand():
            return False
        
        self.deal_cards()
        self.display_game_state()
        self.betting_round()
        
        # Check if game should continue
        active_players = [p for p in self.players if not p.has_folded]
        if len(active_players) > 0:
            self.determine_winner()
        
        # Move dealer button
        self.dealer_position = (self.dealer_position + 1) % len(self.players)
        
        return len([p for p in self.players if p.chips > 0]) > 1
    
    def play_game(self):
        """Main game loop."""
        print("Welcome to Poker Basic!")
        print(f"Players: {len(self.players)}")
        
        hand_count = 0
        while True:
            hand_count += 1
            print(f"\n{'='*20} HAND {hand_count} {'='*20}")
            
            if not self.play_hand():
                break
            
            # Check if we should continue
            remaining_players = [p for p in self.players if p.chips > 0]
            if len(remaining_players) <= 1:
                break
            
            # Ask if human player wants to continue
            try:
                if any(isinstance(p, HumanPlayer) for p in remaining_players):
                    continue_game = input("\nContinue to next hand? (y/n): ").lower().strip()
                    if continue_game != 'y':
                        break
            except KeyboardInterrupt:
                break
        
        # Game over
        print("\n" + "="*50)
        print("GAME OVER")
        print("="*50)
        
        # Show final results
        final_standings = sorted(self.players, key=lambda p: p.chips, reverse=True)
        print("\nFinal Standings:")
        for i, player in enumerate(final_standings, 1):
            print(f"{i}. {player.name}: {player.chips} chips")


if __name__ == "__main__":
    # Create and run a poker game
    game = PokerGame(human_players=1, ai_players=3)
    game.play_game()