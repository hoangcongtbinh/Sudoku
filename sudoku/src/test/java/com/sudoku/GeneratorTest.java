package com.sudoku;

import com.sudoku.model.*;
import com.sudoku.generator.BoardGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTest {

    @Test
    public void testBoardGeneration() {
        // Tạo bảng với 4 độ khó khác nhau
        BoardGenerator boardGenerator = new BoardGenerator();

        GameSession gameEasy = boardGenerator.generateGameSession(Difficulty.EASY);
        GameSession gameMedium = boardGenerator.generateGameSession(Difficulty.MEDIUM);
        GameSession gameHard = boardGenerator.generateGameSession(Difficulty.HARD);
        GameSession gameExpert = boardGenerator.generateGameSession(Difficulty.EXPERT);

        // Kiểm tra bảng là câu đố thật sự
        assertFalse(gameEasy.getCurrentBoard().isFull());
        assertFalse(gameMedium.getCurrentBoard().isFull());
        assertFalse(gameHard.getCurrentBoard().isFull());
        assertFalse(gameExpert.getCurrentBoard().isFull());

        // Kiểm tra số lượng ô trống đúng với trong cài đặt
        assertEquals(Difficulty.EASY.getEmptyCells(), gameEasy.getCurrentBoard().countEmptyCells());
        assertEquals(Difficulty.MEDIUM.getEmptyCells(), gameMedium.getCurrentBoard().countEmptyCells());

        // Do đặc thù thuật toán sinh ngẫu nhiên bảo đảm độc bản lời giải (solutionCount == 1),
        // Mức Hard và Expert có thể không đạt chính xác 100% số ô trống, chỉ cần nằm trong khoảng cho phép.
        assertTrue(gameHard.getCurrentBoard().countEmptyCells() >= 48 &&
                gameHard.getCurrentBoard().countEmptyCells() <= Difficulty.HARD.getEmptyCells());
        assertTrue(gameExpert.getCurrentBoard().countEmptyCells() >= 50
                && gameExpert.getCurrentBoard().countEmptyCells() <= Difficulty.EXPERT.getEmptyCells());

        // Kiểm tra solutionBoard tồn tại
        assertNotNull(gameEasy.getSolutionBoard());
        assertNotNull(gameMedium.getSolutionBoard());
        assertNotNull(gameHard.getSolutionBoard());
        assertNotNull(gameExpert.getSolutionBoard());
    }

}
