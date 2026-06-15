package com.sudoku.highscore;

import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;

import java.io.*;
import java.util.Properties;

public class    HighScoreManager {

    private static final String DEFAULT_FILE_PATH =
            System.getProperty("user.home") + File.separator + "sudoku_highscores.properties";

    private final String filePath;
    private final Properties properties = new Properties();

    public HighScoreManager() {
        this(DEFAULT_FILE_PATH);
    }

    public HighScoreManager(String filePath) {
        this.filePath = filePath;
        loadHighScores();
    }

    private synchronized void loadHighScores() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error reading highscore file: " + e.getMessage());
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
        long currentTimeInSeconds = session.getElapsedTime() / 1000;

        String previousValue = properties.getProperty(difficulty.name());
        long bestTimeInSeconds = Long.MAX_VALUE;
        if (previousValue != null) {
            try {
                bestTimeInSeconds = Long.parseLong(previousValue);
            } catch (NumberFormatException ignored) {}
        }

        if (currentTimeInSeconds < bestTimeInSeconds) {
            properties.setProperty(difficulty.name(), String.valueOf(currentTimeInSeconds));
            try (OutputStream output = new FileOutputStream(filePath)) {
                properties.store(output, "Sudoku High Scores - Best Times (Seconds)");
                return true;
            } catch (IOException e) {
                System.err.println("Error writing highscore file: " + e.getMessage());
                if (previousValue == null) {
                    properties.remove(difficulty.name());
                } else {
                    properties.setProperty(difficulty.name(), previousValue);
                }
            }
        }

        return false;
    }

    public synchronized boolean clearHighScore(Difficulty difficulty) {
        if (properties.getProperty(difficulty.name()) == null) return false;

        properties.remove(difficulty.name());
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "Sudoku High Scores - Best Times (Seconds)");
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing highscore: " + e.getMessage());
        }
        return false;
    }

    public synchronized boolean clearAllHighScores() {
        properties.clear();
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "Sudoku High Scores - Best Times (Seconds)");
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing all highscores: " + e.getMessage());
        }
        return false;
    }

    public String formatTime(long totalSeconds) {
        if (totalSeconds == Long.MAX_VALUE) return "--:--";
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getHighScoreDisplay(Difficulty difficulty) {
        return difficulty.name() + ": " + formatTime(getBestTimeInSeconds(difficulty));
    }
}