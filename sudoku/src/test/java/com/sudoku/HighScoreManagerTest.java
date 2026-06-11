package com.sudoku;

import com.sudoku.highscore.HighScoreManager;
import com.sudoku.model.Board;
import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    private String tempFilePath;
    private HighScoreManager manager;

    @BeforeEach
    public void setUp() {
        tempFilePath = tempDir.resolve("test_highscores.properties").toString();
        manager = new HighScoreManager(tempFilePath);
    }

    private GameSession createFinishedSession(Difficulty difficulty, long elapsedMs) throws Exception {
        Board board = new Board();
        GameSession session = new GameSession(board, board, difficulty);
        
        // Use reflection to set elapsed time and finished state precisely without sleeping
        long now = System.currentTimeMillis();
        long startTime = now - elapsedMs;
        
        java.lang.reflect.Field startField = GameSession.class.getDeclaredField("startTime");
        startField.setAccessible(true);
        startField.set(session, startTime);

        java.lang.reflect.Field endField = GameSession.class.getDeclaredField("endTime");
        endField.setAccessible(true);
        endField.set(session, now);

        java.lang.reflect.Field finishedField = GameSession.class.getDeclaredField("finished");
        finishedField.setAccessible(true);
        finishedField.set(session, true);

        return session;
    }

    private GameSession createUnfinishedSession(Difficulty difficulty) {
        Board board = new Board();
        return new GameSession(board, board, difficulty);
    }

    @Test
    public void testDefaultInitialization() {
        assertNotNull(manager);
        // Verify default best times are Long.MAX_VALUE when file is empty or non-existent
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.EASY));
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.MEDIUM));
        
        // Verify format display defaults to "--:--"
        assertEquals("EASY: --:--", manager.getHighScoreDisplay(Difficulty.EASY));
    }

    @Test
    public void testUpdateHighScore_NullOrUnfinishedSession() {
        // Null session should not update high scores
        assertFalse(manager.updateHighScore(null));
        
        // Unfinished session should not update high scores
        GameSession unfinished = createUnfinishedSession(Difficulty.EASY);
        assertFalse(manager.updateHighScore(unfinished));
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.EASY));
    }

    @Test
    public void testUpdateHighScore_Sequence() throws Exception {
        // 1. First time recording a high score
        GameSession session1 = createFinishedSession(Difficulty.EASY, 10000); // 10 seconds
        assertTrue(manager.updateHighScore(session1));
        assertEquals(10, manager.getBestTimeInSeconds(Difficulty.EASY));
        assertEquals("EASY: 00:10", manager.getHighScoreDisplay(Difficulty.EASY));

        // Verify that the properties file was created
        File file = new File(tempFilePath);
        assertTrue(file.exists());

        // 2. Try updating with a worse time (15 seconds)
        GameSession session2 = createFinishedSession(Difficulty.EASY, 15000); // 15 seconds
        assertFalse(manager.updateHighScore(session2));
        assertEquals(10, manager.getBestTimeInSeconds(Difficulty.EASY)); // Should remain 10 seconds

        // 3. Update with a better time (5 seconds)
        GameSession session3 = createFinishedSession(Difficulty.EASY, 5000); // 5 seconds
        assertTrue(manager.updateHighScore(session3));
        assertEquals(5, manager.getBestTimeInSeconds(Difficulty.EASY)); // Should update to 5 seconds
        assertEquals("EASY: 00:05", manager.getHighScoreDisplay(Difficulty.EASY));
    }

    @Test
    public void testClearHighScore() throws Exception {
        // Add scores
        GameSession easySession = createFinishedSession(Difficulty.EASY, 12000); // 12s
        GameSession mediumSession = createFinishedSession(Difficulty.MEDIUM, 45000); // 45s
        assertTrue(manager.updateHighScore(easySession));
        assertTrue(manager.updateHighScore(mediumSession));

        // Clear EASY high score
        assertTrue(manager.clearHighScore(Difficulty.EASY));
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.EASY));
        assertEquals(45, manager.getBestTimeInSeconds(Difficulty.MEDIUM)); // Medium should remain

        // Clear EASY again should return false because it's not set
        assertFalse(manager.clearHighScore(Difficulty.EASY));
    }

    @Test
    public void testClearAllHighScores() throws Exception {
        // Add scores
        GameSession easySession = createFinishedSession(Difficulty.EASY, 12000);
        GameSession mediumSession = createFinishedSession(Difficulty.MEDIUM, 45000);
        manager.updateHighScore(easySession);
        manager.updateHighScore(mediumSession);

        // Clear all
        assertTrue(manager.clearAllHighScores());
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.EASY));
        assertEquals(Long.MAX_VALUE, manager.getBestTimeInSeconds(Difficulty.MEDIUM));
    }

    @Test
    public void testFormatTime() {
        assertEquals("--:--", manager.formatTime(Long.MAX_VALUE));
        assertEquals("00:00", manager.formatTime(0));
        assertEquals("00:05", manager.formatTime(5));
        assertEquals("00:59", manager.formatTime(59));
        assertEquals("01:00", manager.formatTime(60));
        assertEquals("01:05", manager.formatTime(65));
        assertEquals("59:59", manager.formatTime(3599));
        assertEquals("60:00", manager.formatTime(3600));
    }

    @Test
    public void testPersistenceAcrossInstances() throws Exception {
        // Add score using first manager instance
        GameSession session = createFinishedSession(Difficulty.HARD, 20000); // 20s
        assertTrue(manager.updateHighScore(session));

        // Construct a new manager pointing to the same file path
        HighScoreManager newManager = new HighScoreManager(tempFilePath);
        assertEquals(20, newManager.getBestTimeInSeconds(Difficulty.HARD));
    }
}
