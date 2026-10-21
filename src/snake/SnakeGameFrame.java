package snake;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SnakeGameFrame extends JFrame {
    private final HighScoreManager highScoreManager;
    private final GamePanel gamePanel;

    // Header score labels
    private JLabel scoreLabel;
    private JLabel highScoreLabel;

    // Control buttons
    private JButton pauseResumeBtn;
    private JButton restartBtn;

    // Theme & Speed controls
    private JComboBox<Theme> themeSelector;
    private JComboBox<SpeedLevel> speedSelector;

    // Header and settings bar containers
    private JPanel headerPanel;
    private JPanel controlPanel;
    private JPanel boardContainer;
    private JLabel titleLabel;
    private JLabel themeLabel;
    private JLabel speedLabel;
    private JLabel tipsLabel;

    public SnakeGameFrame() {
        setTitle("Snake Game - PL-3 Mini Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        highScoreManager = new HighScoreManager();

        // Initialize GamePanel with callbacks to sync UI elements
        gamePanel = new GamePanel(
            highScoreManager,
            this::updateScoreLabels,
            this::updateControlStates
        );

        initUI();

        pack();
        setLocationRelativeTo(null); // Center on screen

        // Ensure key events go to GamePanel when window is focused
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 24, 33));

        // 1. Top Header: Score Board & Controls
        headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Game Area (Square Board)
        boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(new Color(12, 16, 22));
        boardContainer.setBorder(new EmptyBorder(12, 12, 12, 12));
        boardContainer.add(gamePanel);
        add(boardContainer, BorderLayout.CENTER);

        // 3. Bottom: Settings Bar (Speed, Theme, Instructions)
        controlPanel = createBottomSettingsPanel();
        add(controlPanel, BorderLayout.SOUTH);

        applyThemeStyles(gamePanel.getCurrentTheme());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left: Game title with icon-like styling
        titleLabel = new JLabel("🐍 SNAKE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        // Center: Live Score & High Score badges
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        scorePanel.setOpaque(false);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(46, 213, 115));

        highScoreLabel = new JLabel("High Score: " + highScoreManager.getHighScore());
        highScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        highScoreLabel.setForeground(new Color(255, 215, 0));

        scorePanel.add(scoreLabel);
        scorePanel.add(highScoreLabel);

        // Right: Pause & Restart buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        pauseResumeBtn = createStyledButton("Pause", new Color(225, 232, 240));
        pauseResumeBtn.addActionListener(e -> {
            gamePanel.togglePause();
            gamePanel.requestFocusInWindow();
        });

        restartBtn = createStyledButton("Restart", new Color(255, 215, 215));
        restartBtn.addActionListener(e -> {
            gamePanel.restartGame();
            gamePanel.requestFocusInWindow();
        });

        actionPanel.add(pauseResumeBtn);
        actionPanel.add(restartBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(scorePanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createBottomSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBorder(new EmptyBorder(10, 20, 12, 20));

        // Left / Center: Settings combo boxes
        JPanel settingsGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        settingsGroup.setOpaque(false);

        // Theme selector
        themeLabel = new JLabel("🎨 Theme:");
        themeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        themeSelector = new JComboBox<>(Theme.values());
        themeSelector.setSelectedItem(gamePanel.getCurrentTheme());
        styleComboBox(themeSelector);
        themeSelector.addActionListener(e -> {
            Theme selected = (Theme) themeSelector.getSelectedItem();
            if (selected != null) {
                gamePanel.setTheme(selected);
                applyThemeStyles(selected);
                gamePanel.requestFocusInWindow();
            }
        });

        // Speed selector
        speedLabel = new JLabel("⚡ Speed:");
        speedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        speedSelector = new JComboBox<>(SpeedLevel.values());
        speedSelector.setSelectedItem(gamePanel.getCurrentSpeed());
        styleComboBox(speedSelector);
        speedSelector.addActionListener(e -> {
            SpeedLevel selected = (SpeedLevel) speedSelector.getSelectedItem();
            if (selected != null) {
                gamePanel.setSpeed(selected);
                gamePanel.requestFocusInWindow();
            }
        });

        settingsGroup.add(themeLabel);
        settingsGroup.add(themeSelector);
        settingsGroup.add(speedLabel);
        settingsGroup.add(speedSelector);

        // Right: Keyboard shortcuts quick tips
        tipsLabel = new JLabel("Controls: W/A/S/D or Arrow Keys | P/Space: Pause | R: Restart");
        tipsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        panel.add(settingsGroup, BorderLayout.WEST);
        panel.add(tipsLabel, BorderLayout.EAST);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(new Color(20, 20, 20)); // High-contrast black text
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 190, 205), 1, true),
            new EmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private <T> void styleComboBox(JComboBox<T> combo) {
        combo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(25, 25, 25));
        combo.setFocusable(false);
        combo.setOpaque(true);

        // Custom renderer ensuring crisp, high-contrast black text on clean white/light background
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 8, 5, 8));
                if (isSelected) {
                    setBackground(new Color(38, 128, 235));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(20, 20, 20));
                }
                return this;
            }
        });
    }

    private void applyThemeStyles(Theme theme) {
        boolean isLight = (theme == Theme.LIGHT_THEME);

        headerPanel.setBackground(theme.backgroundColor);
        controlPanel.setBackground(theme.backgroundColor);
        getContentPane().setBackground(theme.backgroundColor);

        if (boardContainer != null) {
            boardContainer.setBackground(isLight ? new Color(230, 235, 242) : new Color(12, 16, 22));
        }

        // Header and labels text coloring
        if (titleLabel != null) {
            titleLabel.setForeground(theme.textColor);
        }
        if (themeLabel != null) {
            themeLabel.setForeground(theme.textColor);
        }
        if (speedLabel != null) {
            speedLabel.setForeground(theme.textColor);
        }
        if (tipsLabel != null) {
            tipsLabel.setForeground(isLight ? new Color(90, 105, 125) : new Color(160, 175, 195));
        }

        // Score Badges
        scoreLabel.setForeground(theme.snakeHeadColor);
        highScoreLabel.setForeground(theme.accentColor);
    }

    private void updateScoreLabels() {
        if (scoreLabel != null && gamePanel != null) {
            scoreLabel.setText("Score: " + gamePanel.getScore());
        }
        if (highScoreLabel != null && highScoreManager != null) {
            highScoreLabel.setText("High Score: " + highScoreManager.getHighScore());
        }
    }

    private void updateControlStates() {
        if (pauseResumeBtn == null || gamePanel == null) return;
        if (gamePanel.isPaused()) {
            pauseResumeBtn.setText("Resume");
            pauseResumeBtn.setBackground(new Color(185, 245, 205));
            pauseResumeBtn.setForeground(new Color(20, 20, 20));
        } else {
            pauseResumeBtn.setText("Pause");
            pauseResumeBtn.setBackground(new Color(225, 232, 240));
            pauseResumeBtn.setForeground(new Color(20, 20, 20));
        }
    }
}
