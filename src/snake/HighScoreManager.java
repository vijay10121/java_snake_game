package snake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "snake_highscore.dat";
    private int highScore = 0;

    public HighScoreManager() {
        loadHighScore();
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
            return true;
        }
        return false;
    }

    private void loadHighScore() {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            highScore = 0;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
}
