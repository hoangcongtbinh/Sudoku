package com.sudoku;

import com.sudoku.model.Board;
import com.sudoku.solver.*;
import com.sudoku.validator.BoardValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {

    private int[][] solvedMatrix;

    @BeforeEach
    public void setUp() {
        // Một bảng Sudoku hoàn chỉnh hợp lệ
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
    }

    private Board createEasyPuzzle() {
        // Tạo một câu đố bằng cách xóa một số ô từ bảng đã giải
        Board board = new Board(solvedMatrix);
        board.setCell(0, 2, 0); // Xóa số 4
        board.setCell(0, 5, 0); // Xóa số 8
        board.setCell(1, 1, 0); // Xóa số 7
        board.setCell(2, 7, 0); // Xóa số 6
        board.setCell(4, 4, 0); // Xóa số 5
        board.setCell(5, 8, 0); // Xóa số 6
        board.setCell(7, 3, 0); // Xóa số 4
        board.setCell(8, 6, 0); // Xóa số 1
        return board;
    }

    private Board createVerySimplePuzzle() {
        // Tạo câu đố siêu đơn giản chỉ với 2 ô trống cho SASolver
        Board board = new Board(solvedMatrix);
        board.setCell(0, 2, 0); // Xóa số 4
        board.setCell(0, 3, 0); // Xóa số 6
        return board;
    }

    private Board createUnsolvablePuzzle() {
        // Tạo bảng không thể giải được (vi phạm luật Sudoku ban đầu bằng cách trùng số trong hàng)
        Board board = new Board(solvedMatrix);
        board.setCell(0, 2, 0);
        board.setCell(0, 0, 3); // Ghi đè ô (0,0) thành 3, tạo ra hai số 3 trong hàng 0
        return board;
    }

    @Test
    public void testBacktrackingSolver() {
        Solver solver = new BacktrackingSolver();
        
        // 1. Kiểm tra giải câu đố hợp lệ
        Board puzzle = createEasyPuzzle();
        SolveResult result = solver.solve(puzzle);
        
        assertTrue(result.isSolved());
        assertTrue(BoardValidator.isSolved(puzzle)); // Backtracking giải trực tiếp trên đối tượng board
        assertEquals(4, puzzle.getCell(0, 2));
        assertEquals(8, puzzle.getCell(0, 5));

        // 2. Kiểm tra với bảng không có lời giải
        Board unsolvable = createUnsolvablePuzzle();
        SolveResult failResult = solver.solve(unsolvable);
        assertFalse(failResult.isSolved());
    }

    @Test
    public void testMRVSolver() {
        Solver solver = new MRVSolver();

        // 1. Kiểm tra giải câu đố hợp lệ
        Board puzzle = createEasyPuzzle();
        SolveResult result = solver.solve(puzzle);

        assertTrue(result.isSolved());
        assertTrue(BoardValidator.isSolved(puzzle)); // MRV giải trực tiếp trên đối tượng board
        assertEquals(4, puzzle.getCell(0, 2));
        assertEquals(8, puzzle.getCell(0, 5));

        // 2. Kiểm tra với bảng không có lời giải
        Board unsolvable = createUnsolvablePuzzle();
        SolveResult failResult = solver.solve(unsolvable);
        assertFalse(failResult.isSolved());
    }

    @Test
    public void testSASolver() {
        Solver solver = new SASolver();

        // 1. Kiểm tra giải câu đố cực kỳ đơn giản (SA hiệu quả với số ô trống rất ít)
        Board puzzle = createVerySimplePuzzle();
        SolveResult result = solver.solve(puzzle);

        // Vì SA là giải thuật tối ưu hóa ngẫu nhiên (Simulated Annealing),
        // Với 2 ô trống thì xác suất giải được ngay lập tức là cực kỳ cao.
        assertTrue(result.isSolved());

        // 2. Kiểm tra với bảng ít hơn 2 ô trống (SASolver sẽ trả về kết quả dựa trên tính hợp lệ trực tiếp)
        Board alreadySolvedBoard = new Board(solvedMatrix);
        SolveResult solvedResult = solver.solve(alreadySolvedBoard);
        assertTrue(solvedResult.isSolved());
    }
}
