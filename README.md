poker game
========

This is a poker game built with proper object-oriented design principles.

## Features

- **Object-Oriented Design**: Proper class hierarchy with inheritance
- **Player Classes**: Abstract base class with HumanPlayer and AIPlayer subclasses
- **Card Rendering**: Visual card display with suit symbols (♥ ♦ ♣ ♠)
- **AI Players**: Intelligent AI with configurable aggression levels
- **Game Flow**: Proper player iteration and betting logic
- **Comprehensive Tests**: Full test suite with 63+ tests

## Installation

```bash
pip install -r requirements.txt
```

## Usage

### Run Interactive Game
```bash
python poker_game.py
```

### Run Demo
```bash
python demo.py
```

### Run Tests
```bash
python -m pytest -v
```

## Architecture

The game follows proper OOP principles with the following structure:

- **Card**: Represents individual playing cards with display capabilities
- **Deck**: Manages collection of cards with shuffling and dealing
- **Player** (Abstract): Base class for all players
  - **HumanPlayer**: Interactive human player
  - **AIPlayer**: Intelligent AI with configurable strategy
- **PokerGame**: Main game controller managing game flow

## Key OOP Features

1. **Inheritance**: Player base class with specialized subclasses
2. **Polymorphism**: Both player types can be used interchangeably
3. **Encapsulation**: Proper data hiding and method organization
4. **Abstraction**: Abstract base class defining player interface

## Game Features

- Proper player iteration (no hardcoded logic)
- AI players with varying aggression levels
- Visual card rendering with Unicode symbols
- Betting rounds with call/raise/fold mechanics
- Chip management and all-in scenarios
- Winner determination

This rewrite addresses the original issues by:
- Eliminating hardcoded player logic
- Implementing proper class-based player management
- Using inheritance for code reuse
- Adding comprehensive tests
- Including visual card display functionality 