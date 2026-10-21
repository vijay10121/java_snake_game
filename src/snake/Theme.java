package snake;

import java.awt.Color;

public enum Theme {
    CLASSIC_DARK(
        "Neon Matrix",
        new Color(15, 20, 25),       // Background
        new Color(24, 32, 42),       // Grid line / board cell tint
        new Color(46, 213, 115),     // Snake head
        new Color(38, 175, 95),      // Snake body
        new Color(255, 71, 87),      // Food
        new Color(255, 215, 0),      // Food glow / accent
        new Color(240, 246, 252)     // Text
    ),
    CYBERPUNK(
        "Cyberpunk",
        new Color(18, 11, 38),       // Deep purple
        new Color(32, 20, 64),
        new Color(255, 0, 128),      // Neon Pink
        new Color(155, 45, 235),     // Purple neon
        new Color(0, 245, 255),      // Cyan fruit
        new Color(255, 240, 0),
        new Color(255, 255, 255)
    ),
    RETRO_ARCADE(
        "Retro Green Arcade",
        new Color(12, 28, 16),       // GameBoy-like dark olive
        new Color(20, 44, 25),
        new Color(140, 220, 50),     // Bright olive green
        new Color(90, 160, 35),
        new Color(240, 180, 40),     // Amber fruit
        new Color(255, 215, 0),
        new Color(220, 245, 180)
    ),
    MIDNIGHT_BLUE(
        "Deep Ocean",
        new Color(10, 25, 47),       // Deep Navy
        new Color(17, 34, 64),
        new Color(100, 255, 218),    // Bright Aquamarine
        new Color(70, 180, 180),
        new Color(255, 110, 180),    // Coral Pink
        new Color(255, 200, 220),
        new Color(204, 214, 246)
    ),
    LIGHT_THEME(
        "Modern Light",
        new Color(245, 247, 250),    // Clean light grey-white background
        new Color(225, 230, 238),    // Soft grid lines
        new Color(22, 160, 93),      // Fresh emerald green head
        new Color(46, 204, 113),     // Mint body
        new Color(231, 76, 60),      // Crisp red apple
        new Color(211, 84, 0),       // Orange accent
        new Color(33, 37, 41)        // Dark charcoal text
    );

    public final String displayName;
    public final Color backgroundColor;
    public final Color gridColor;
    public final Color snakeHeadColor;
    public final Color snakeBodyColor;
    public final Color foodColor;
    public final Color accentColor;
    public final Color textColor;

    Theme(String displayName, Color bg, Color grid, Color head, Color body, Color food, Color accent, Color text) {
        this.displayName = displayName;
        this.backgroundColor = bg;
        this.gridColor = grid;
        this.snakeHeadColor = head;
        this.snakeBodyColor = body;
        this.foodColor = food;
        this.accentColor = accent;
        this.textColor = text;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
