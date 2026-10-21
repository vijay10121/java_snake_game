package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Grid settings
    public static final int TILE_SIZE = 25;
    public static final int GRID_COLS = 26; // 650px
    public static final int GRID_ROWS = 26; // 650px
    public static final int BOARD_WIDTH = GRID_COLS * TILE_SIZE;
    public static final int BOARD_HEIGHT = GRID_ROWS * TILE_SIZE;

    // Snake representation
    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food = new Point(0, 0);

    // Direction variables
    private char direction = 'R'; // 'U', 'D', 'L', 'R'
    private char nextDirection = 'R';

    // Game state
    private boolean running = false;
    private boolean paused = false;
    private boolean gameOver = false;

    // Scores
    private int score = 0;
    private final HighScoreManager highScoreManager;
    private boolean newHighScoreAchieved = false;

    // Animation & Timers
    private Timer timer;
    private Theme currentTheme = Theme.CLASSIC_DARK;
    private SpeedLevel currentSpeed = SpeedLevel.MEDIUM;

    private final Random random = new Random();

    // Callbacks to UI container if needed
    private final Runnable onScoreUpdate;
    private final Runnable onStateChange;

    public GamePanel(HighScoreManager highScoreManager, Runnable onScoreUpdate, Runnable onStateChange) {
        this.highScoreManager = highScoreManager;
        this.onScoreUpdate = onScoreUpdate;
        this.onStateChange = onStateChange;

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(currentTheme.backgroundColor);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        initGame();
    }

    public void initGame() {
        snake.clear();
        // Start snake at center with initial length 4
        int startX = GRID_COLS / 4;
        int startY = GRID_ROWS / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));
        snake.add(new Point(startX - 3, startY));

        direction = 'R';
        nextDirection = 'R';
        score = 0;
        newHighScoreAchieved = false;
        gameOver = false;
        paused = false;

        spawnFood();

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(currentSpeed.delayMs, this);
        running = true;
        timer.start();

        if (onScoreUpdate != null) onScoreUpdate.run();
        if (onStateChange != null) onStateChange.run();
        repaint();
    }

    public void restartGame() {
        initGame();
        requestFocusInWindow();
    }

    public void togglePause() {
        if (!running || gameOver) return;

        paused = !paused;
        if (paused) {
            timer.stop();
        } else {
            timer.start();
        }
        if (onStateChange != null) onStateChange.run();
        repaint();
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isRunning() {
        return running;
    }

    public int getScore() {
        return score;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        setBackground(theme.backgroundColor);
        repaint();
    }

    public SpeedLevel getCurrentSpeed() {
        return currentSpeed;
    }

    public void setSpeed(SpeedLevel speed) {
        this.currentSpeed = speed;
        if (timer != null) {
            timer.setDelay(speed.delayMs);
        }
    }

    private void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_P) {
            togglePause();
            return;
        }

        if (keyCode == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        if (paused || gameOver) return;

        // Prevent 180-degree immediate reversal
        if ((keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) && direction != 'D') {
            nextDirection = 'U';
        } else if ((keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) && direction != 'U') {
            nextDirection = 'D';
        } else if ((keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) && direction != 'R') {
            nextDirection = 'L';
        } else if ((keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) && direction != 'L') {
            nextDirection = 'R';
        }
    }

    private void spawnFood() {
        int maxAttempts = 1000;
        int attempts = 0;
        while (attempts < maxAttempts) {
            int fx = random.nextInt(GRID_COLS);
            int fy = random.nextInt(GRID_ROWS);
            Point p = new Point(fx, fy);
            if (!snake.contains(p)) {
                food = p;
                return;
            }
            attempts++;
        }
        // Fallback: search sequentially
        for (int x = 0; x < GRID_COLS; x++) {
            for (int y = 0; y < GRID_ROWS; y++) {
                Point p = new Point(x, y);
                if (!snake.contains(p)) {
                    food = p;
                    return;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused && !gameOver) {
            move();
            repaint();
        }
    }

    private void move() {
        direction = nextDirection;
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case 'U': newHead.y--; break;
            case 'D': newHead.y++; break;
            case 'L': newHead.x--; break;
            case 'R': newHead.x++; break;
        }

        // Wall collision check (strictly dies if it touches the boundary of the square area)
        if (newHead.x < 0 || newHead.x >= GRID_COLS || newHead.y < 0 || newHead.y >= GRID_ROWS) {
            triggerGameOver("Hit the wall!");
            return;
        }

        // Self-collision check (excluding the tail if tail will be removed, but if eating it grows)
        // Checking against current segments
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            // If it's the tail and we don't eat food, the tail will move away, but touching body is game over
            if (i < snake.size() - 1 && segment.equals(newHead)) {
                triggerGameOver("Ran into self!");
                return;
            }
        }

        snake.addFirst(newHead);

        // Check if food was eaten
        if (newHead.equals(food)) {
            score += 10;
            if (highScoreManager.updateHighScore(score)) {
                newHighScoreAchieved = true;
            }
            if (onScoreUpdate != null) onScoreUpdate.run();
            spawnFood();
        } else {
            // Remove tail so length stays unchanged unless food was eaten
            snake.removeLast();
        }
    }

    private void triggerGameOver(String reason) {
        gameOver = true;
        running = false;
        if (timer != null) timer.stop();
        if (highScoreManager.updateHighScore(score)) {
            newHighScoreAchieved = true;
        }
        if (onScoreUpdate != null) onScoreUpdate.run();
        if (onStateChange != null) onStateChange.run();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g2.setColor(currentTheme.backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw stylish subtle grid pattern
        drawGrid(g2);

        // Draw food with glowing effect
        drawFood(g2);

        // Draw snake
        drawSnake(g2);

        // Draw overlays (Paused, Game Over)
        if (paused) {
            drawPauseOverlay(g2);
        } else if (gameOver) {
            drawGameOverOverlay(g2);
        }

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(currentTheme.gridColor);
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x <= BOARD_WIDTH; x += TILE_SIZE) {
            g2.drawLine(x, 0, x, BOARD_HEIGHT);
        }
        for (int y = 0; y <= BOARD_HEIGHT; y += TILE_SIZE) {
            g2.drawLine(0, y, BOARD_WIDTH, y);
        }

        // Draw bright border around the playable arena
        g2.setColor(new Color(currentTheme.foodColor.getRed(), currentTheme.foodColor.getGreen(), currentTheme.foodColor.getBlue(), 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(1, 1, BOARD_WIDTH - 2, BOARD_HEIGHT - 2);
    }

    private void drawFood(Graphics2D g2) {
        int fx = food.x * TILE_SIZE;
        int fy = food.y * TILE_SIZE;

        // Subtle outer pulse glow
        g2.setColor(new Color(currentTheme.foodColor.getRed(), currentTheme.foodColor.getGreen(), currentTheme.foodColor.getBlue(), 70));
        g2.fillOval(fx - 3, fy - 3, TILE_SIZE + 6, TILE_SIZE + 6);

        // Food core
        g2.setColor(currentTheme.foodColor);
        g2.fillOval(fx + 2, fy + 2, TILE_SIZE - 4, TILE_SIZE - 4);

        // Highlight
        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillOval(fx + 6, fy + 6, (TILE_SIZE - 4) / 3, (TILE_SIZE - 4) / 3);
    }

    private void drawSnake(Graphics2D g2) {
        int size = snake.size();
        for (int i = 0; i < size; i++) {
            Point p = snake.get(i);
            int x = p.x * TILE_SIZE;
            int y = p.y * TILE_SIZE;

            if (i == 0) {
                // Head
                g2.setColor(currentTheme.snakeHeadColor);
                RoundRectangle2D headRect = new RoundRectangle2D.Float(
                    x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 10, 10
                );
                g2.fill(headRect);

                // Snake Eyes
                drawEyes(g2, x, y);
            } else {
                // Body segments with subtle gradient shading from head to tail
                float ratio = (float) i / Math.max(1, size);
                Color bodyColor = interpolateColor(currentTheme.snakeHeadColor, currentTheme.snakeBodyColor, ratio);
                g2.setColor(bodyColor);
                RoundRectangle2D bodyRect = new RoundRectangle2D.Float(
                    x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8
                );
                g2.fill(bodyRect);
            }
        }
    }

    private void drawEyes(Graphics2D g2, int x, int y) {
        g2.setColor(Color.WHITE);
        int eyeSize = 5;
        int pupilSize = 2;
        int eye1X = 0, eye1Y = 0, eye2X = 0, eye2Y = 0;

        switch (direction) {
            case 'U':
                eye1X = x + 4; eye1Y = y + 4;
                eye2X = x + TILE_SIZE - 9; eye2Y = y + 4;
                break;
            case 'D':
                eye1X = x + 4; eye1Y = y + TILE_SIZE - 9;
                eye2X = x + TILE_SIZE - 9; eye2Y = y + TILE_SIZE - 9;
                break;
            case 'L':
                eye1X = x + 4; eye1Y = y + 4;
                eye2X = x + 4; eye2Y = y + TILE_SIZE - 9;
                break;
            case 'R':
                eye1X = x + TILE_SIZE - 9; eye1Y = y + 4;
                eye2X = x + TILE_SIZE - 9; eye2Y = y + TILE_SIZE - 9;
                break;
        }

        g2.fillOval(eye1X, eye1Y, eyeSize, eyeSize);
        g2.fillOval(eye2X, eye2Y, eyeSize, eyeSize);

        g2.setColor(Color.BLACK);
        g2.fillOval(eye1X + 1, eye1Y + 1, pupilSize, pupilSize);
        g2.fillOval(eye2X + 1, eye2Y + 1, pupilSize, pupilSize);
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        ratio = Math.min(1f, Math.max(0f, ratio));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
        String text = "GAME PAUSED";
        FontMetrics fm = g2.getFontMetrics();
        int x = (BOARD_WIDTH - fm.stringWidth(text)) / 2;
        int y = BOARD_HEIGHT / 2 - 20;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        String sub = "Press 'P', 'Space', or Resume button to continue";
        fm = g2.getFontMetrics();
        x = (BOARD_WIDTH - fm.stringWidth(sub)) / 2;
        g2.drawString(sub, x, y + 45);
    }

    private void drawGameOverOverlay(Graphics2D g2) {
        g2.setColor(new Color(15, 10, 20, 210));
        g2.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Banner box
        int boxW = 420;
        int boxH = 240;
        int boxX = (BOARD_WIDTH - boxW) / 2;
        int boxY = (BOARD_HEIGHT - boxH) / 2;

        g2.setColor(new Color(30, 35, 48));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g2.setColor(currentTheme.foodColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Game Over text
        g2.setColor(new Color(255, 75, 75));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
        FontMetrics fm = g2.getFontMetrics();
        String title = "GAME OVER";
        g2.drawString(title, (BOARD_WIDTH - fm.stringWidth(title)) / 2, boxY + 60);

        // Scores
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        fm = g2.getFontMetrics();
        String scoreMsg = "Final Score: " + score;
        g2.drawString(scoreMsg, (BOARD_WIDTH - fm.stringWidth(scoreMsg)) / 2, boxY + 115);

        if (newHighScoreAchieved) {
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            fm = g2.getFontMetrics();
            String recordMsg = "★ NEW HIGH SCORE! ★";
            g2.drawString(recordMsg, (BOARD_WIDTH - fm.stringWidth(recordMsg)) / 2, boxY + 150);
        } else {
            g2.setColor(new Color(180, 195, 210));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            fm = g2.getFontMetrics();
            String hiMsg = "Best Score: " + highScoreManager.getHighScore();
            g2.drawString(hiMsg, (BOARD_WIDTH - fm.stringWidth(hiMsg)) / 2, boxY + 150);
        }

        // Restart tip
        g2.setColor(currentTheme.snakeHeadColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        fm = g2.getFontMetrics();
        String restartMsg = "Press [R] or Click Restart to Play Again";
        g2.drawString(restartMsg, (BOARD_WIDTH - fm.stringWidth(restartMsg)) / 2, boxY + 200);
    }
}
