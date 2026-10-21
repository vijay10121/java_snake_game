# Snake Game (Java Swing) - PL-3 Mini Project

A feature-rich, object-oriented Snake Game built using Java and Swing.

## Features Implemented
1. **Dynamic Snake Growth**: The snake grows by 1 segment each time it consumes a fruit.
2. **Random Fruit Generation**: New fruit is randomly spawned in unoccupied grid squares.
3. **Square Arena**: Spacious 26x26 grid (650x650 px) allowing ample room for the snake to grow.
4. **Collision Detection**:
   - **Boundary Collision**: Game ends if the snake hits the outer square walls.
   - **Self-Collision**: Game ends if the snake runs into any part of its own body.
5. **Live Score & High Score Tracking**:
   - Live score is updated immediately upon eating fruit.
   - High score is tracked and saved to `snake_highscore.dat` persistently across sessions.
6. **Pause & Resume**:
   - Dedicated UI button (`Pause` / `Resume`).
   - Keyboard shortcuts: `P` or `Spacebar`.
7. **Settings Options**:
   - **Themes**: Multiple themes including *Neon Matrix*, *Cyberpunk*, *Retro Green Arcade*, and *Deep Ocean*.
   - **Speed Control**: Adjust difficulty with *Very Slow*, *Slow*, *Medium*, *Fast*, and *Insane*.

## Project Structure
```
mini project/
├── src/
│   └── snake/
│       ├── Main.java              # Entry point
│       ├── SnakeGameFrame.java    # Top-level window and control panels
│       ├── GamePanel.java         # Game loop, rendering, mechanics, collisions
│       ├── Theme.java             # Theme definitions and color palettes
│       ├── SpeedLevel.java        # Speed levels and tick delays
│       └── HighScoreManager.java  # Persistent high score tracker
├── bin/                           # Compiled .class files
├── run.bat                        # One-click Windows runner
└── README.md
```

## How to Run

### Option 1: Double-click or run the batch script
Run `run.bat` in your terminal or double-click it in Windows Explorer:
```powershell
.\run.bat
```

### Option 2: Run via Terminal
```powershell
javac -d bin src/snake/*.java
java -cp bin snake.Main
```

## Controls
- **Movement**: Arrow Keys (`Up`, `Down`, `Left`, `Right`) or `W`, `A`, `S`, `D`
- **Pause / Resume**: `Spacebar` or `P` (or click Pause button)
- **Restart**: `R` (or click Restart button)
