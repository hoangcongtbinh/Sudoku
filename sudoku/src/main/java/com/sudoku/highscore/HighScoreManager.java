package com.sudoku.highscore;

import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;
import java.io.*;
import java.util.Properties;

public class HighScoreManager {
    private static final String FILE_PATH = "sudoku_highscores.properties";
    private final Properties properties = new Properties();

    public HighScoreManager() {
        loadHighScores();
    }

    private synchronized void loadHighScores() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error reading highscore: " + e.getMessage());
        }
    }

    public synchronized long getBestTimeInSeconds(Difficulty difficulty) {
        String timeStr = properties.getProperty(difficulty.name());
        if (timeStr == null) return Long.MAX_VALUE;
        try {
            return Long.parseLong(timeStr);
        } catch (NumberFormatException e) {
            return Long.MAX_VALUE;
        }
    }

    public synchronized boolean updateHighScore(GameSession session) {
        if (session == null || !session.isFinished()) return false;
        Difficulty difficulty = session.getDifficulty();
        long currentTime = session.getElapsedTime() / 1000;
        long bestTime = getBestTimeInSeconds(difficulty);

        if (currentTime < bestTime) {
            properties.setProperty(difficulty.name(), String.valueOf(currentTime));
            try (OutputStream output = new FileOutputStream(FILE_PATH)) {
                properties.store(output, "Best Times");
                return true;
            } catch (IOException e) {
                System.err.println("Error saving highscore: " + e.getMessage());
            }
        }
        return false;
    }
}