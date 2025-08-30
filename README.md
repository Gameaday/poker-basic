Poker Game
===========

This is a Java poker game featuring both console and GUI gameplay.

## Features

- **Complete poker game logic** with hand evaluation (pairs, straights, flushes, full house, etc.)
- **Graphical user interface** with themed card graphics (The Eternal Tortoise theme)
- **Multi-player support** - Play against 1-3 computer opponents
- **Betting system** with pot management across rounds
- **Card exchange** - Select and replace cards to improve your hand
- **Player statistics** persistence between games

## How to Play

### GUI Version (Recommended)
```bash
cd Poker-Basic
mvn compile exec:java -Dexec.mainClass="com.pokermon.NewJFrame"
```

### Console Version
```bash
cd Poker-Basic  
mvn compile exec:java -Dexec.mainClass="com.pokermon.Main"
```

## Building

This project uses Maven for building:

```bash
cd Poker-Basic
mvn clean compile
```

## Game Rules

This is a 5-card draw poker game:
1. Each player gets 5 cards
2. First betting round
3. Exchange unwanted cards
4. Second betting round  
5. Best hand wins the pot

## Credits

- **Original developers**: Carl Nelson and Anthony Elizondo
- **Card artwork**: The Eternal Tortoise themed cards by Small Comic
- **Cleanup and bug fixes**: GitHub Copilot (2024)

## Recent Improvements (2024)

- Fixed critical bugs in game logic (deck size, flush detection, full house evaluation)
- Cleaned up build system (migrated to Maven)  
- Corrected package structure and naming
- Removed obsolete code and build artifacts
- Fixed UI bugs in betting system
- Improved code organization and documentation

---

*Note: This project was originally developed a decade ago and has been recently cleaned up and modernized while preserving all original functionality.*