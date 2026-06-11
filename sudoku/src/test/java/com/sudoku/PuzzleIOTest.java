package com.sudoku;

import com.sudoku.io.PuzzleFormatException;
import com.sudoku.io.PuzzleIOService;
import com.sudoku.io.PuzzleReader;
import com.sudoku.io.PuzzleWriter;
import com.sudoku.model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleIOTest {

    @TempDir
    Path tempDir;

    private int[][] solvedMatrix;
    private Board validBoard;
    private String validBoardString;

    @BeforeEach
    public void setUp() {
        solvedMatrix = new int[][]{
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        validBoard = new Board(solvedMatrix);
        
        validBoardString = 
            "534678912\n" +
            "672195348\n" +
            "198342567\n" +
            "859761423\n" +
            "426853791\n" +
            "713924856\n" +
            "961537284\n" +
            "287419635\n" +
            "345286179\n";
    }

    @Test
    public void testRead_ValidFile() throws IOException {
        Path filePath = tempDir.resolve("valid_sudoku.txt");
        Files.writeString(filePath, validBoardString);

        PuzzleReader reader = new PuzzleReader();
        Board board = reader.read(filePath.toString());

        assertNotNull(board);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assertEquals(solvedMatrix[r][c], board.getCell(r, c));
            }
        }
    }

    @Test
    public void testRead_NullOrEmptyPath() {
        PuzzleReader reader = new PuzzleReader();
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
        assertThrows(IllegalArgumentException.class, () -> reader.read(""));
        assertThrows(IllegalArgumentException.class, () -> reader.read("   "));
    }

    @Test
    public void testRead_NonExistentFile() {
        PuzzleReader reader = new PuzzleReader();
        Path filePath = tempDir.resolve("non_existent.txt");
        assertThrows(IOException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testRead_InvalidFormat_WrongColumnCount() throws IOException {
        // Line 2 has only 8 digits instead of 9
        String invalidContent = 
            "534678912\n" +
            "67219534\n" + // 8 chars
            "198342567\n" +
            "859761423\n" +
            "426853791\n" +
            "713924856\n" +
            "961537284\n" +
            "287419635\n" +
            "345286179\n";

        Path filePath = tempDir.resolve("wrong_col_count.txt");
        Files.writeString(filePath, invalidContent);

        PuzzleReader reader = new PuzzleReader();
        assertThrows(PuzzleFormatException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testRead_InvalidFormat_WrongRowCount() throws IOException {
        // File has only 8 rows
        String invalidContent = 
            "534678912\n" +
            "672195348\n" +
            "198342567\n" +
            "859761423\n" +
            "426853791\n" +
            "713924856\n" +
            "961537284\n" +
            "287419635\n"; // Missing row 9

        Path filePath = tempDir.resolve("wrong_row_count.txt");
        Files.writeString(filePath, invalidContent);

        PuzzleReader reader = new PuzzleReader();
        assertThrows(PuzzleFormatException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testRead_InvalidFormat_NonDigitCharacter() throws IOException {
        // Contains 'A' in the first line
        String invalidContent = 
            "53A678912\n" +
            "672195348\n" +
            "198342567\n" +
            "859761423\n" +
            "426853791\n" +
            "713924856\n" +
            "961537284\n" +
            "287419635\n" +
            "345286179\n";

        Path filePath = tempDir.resolve("non_digit.txt");
        Files.writeString(filePath, invalidContent);

        PuzzleReader reader = new PuzzleReader();
        assertThrows(PuzzleFormatException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testRead_InvalidFormat_ExtraLines() throws IOException {
        // File has 10 rows instead of 9
        String invalidContent = validBoardString + "123456789\n";

        Path filePath = tempDir.resolve("extra_lines.txt");
        Files.writeString(filePath, invalidContent);

        PuzzleReader reader = new PuzzleReader();
        assertThrows(PuzzleFormatException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testRead_ViolatesSudokuRules() throws IOException {
        // Row 1 has two 5s (cell (0,0) and cell (0,8) are both 5)
        String invalidContent = 
            "534678915\n" +
            "672195348\n" +
            "198342567\n" +
            "859761423\n" +
            "426853791\n" +
            "713924856\n" +
            "961537284\n" +
            "287419635\n" +
            "345286179\n";

        Path filePath = tempDir.resolve("violates_rules.txt");
        Files.writeString(filePath, invalidContent);

        PuzzleReader reader = new PuzzleReader();
        assertThrows(PuzzleFormatException.class, () -> reader.read(filePath.toString()));
    }

    @Test
    public void testWrite_ValidBoard() throws IOException {
        Path filePath = tempDir.resolve("written_board.txt");
        PuzzleWriter writer = new PuzzleWriter();
        
        writer.write(validBoard, filePath.toString());
        
        // Verify file exists
        assertTrue(filePath.toFile().exists());

        // Verify the exact content
        List<String> lines = Files.readAllLines(filePath);
        assertEquals(9, lines.size());
        
        String[] expectedLines = validBoardString.split("\n");
        for (int i = 0; i < 9; i++) {
            assertEquals(expectedLines[i], lines.get(i));
        }
    }

    @Test
    public void testWrite_NullBoard() {
        PuzzleWriter writer = new PuzzleWriter();
        Path filePath = tempDir.resolve("written_board.txt");
        assertThrows(IllegalArgumentException.class, () -> writer.write(null, filePath.toString()));
    }

    @Test
    public void testWrite_NullOrEmptyPath() {
        PuzzleWriter writer = new PuzzleWriter();
        assertThrows(IllegalArgumentException.class, () -> writer.write(validBoard, null));
        assertThrows(IllegalArgumentException.class, () -> writer.write(validBoard, ""));
        assertThrows(IllegalArgumentException.class, () -> writer.write(validBoard, "   "));
    }

    @Test
    public void testPuzzleIOService_Integration() throws IOException {
        Path filePath = tempDir.resolve("service_board.txt");
        PuzzleIOService service = new PuzzleIOService();

        // Save using service
        service.savePuzzle(validBoard, filePath.toString());
        assertTrue(filePath.toFile().exists());

        // Load using service
        Board loadedBoard = service.loadPuzzle(filePath.toString());
        assertNotNull(loadedBoard);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assertEquals(validBoard.getCell(r, c), loadedBoard.getCell(r, c));
            }
        }
    }
}
