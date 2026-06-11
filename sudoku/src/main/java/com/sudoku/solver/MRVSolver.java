package com.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import com.sudoku.model.*;
import com.sudoku.validator.BoardValidator;

public class MRVSolver implements Solver {

    @Override
    public SolveResult solve(Board board) {
        List<Step> steps = new ArrayList<>();

        long startTime = System.nanoTime();
        boolean success = backtrackMRV(board, steps);

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime);

        return new SolveResult(success, steps, durationMs);
    }

    // Hàm đệ quy backtracking với MRV
    private boolean backtrackMRV(Board board, List<Step> steps) {
        int[] nextCell = findBestCellMRV(board);

        if (nextCell == null){
            return BoardValidator.isSolved(board);
        }

        int row = nextCell[0];
        int col = nextCell[1];

        // Lấy giá trị cũ của ô trước khi ghi đè (phục vụ biến prevValue trong Step)
        int prevValue = board.getCell(row, col);

        int size = Board.getSize();

        for (int num = 1; num <= size; num++) {
            if (BoardValidator.isValidMove(board, row, col, num)) { 
                board.setCell(row, col, num);

                steps.add(new Step(row, col, prevValue, num, StepType.SOLVER_STEP)); 

                // Đệ quy tiến về phía trước
                if (backtrackMRV(board, steps)) {
                    return true;
                }

                // BACKTRACK 
                board.setCell(row, col, Board.getEmptyValue());
                steps.add(new Step(row, col, num, Board.getEmptyValue(), StepType.BACKTRACK));
            }
        }

        return false; 
    }
    // Hàm tìm ô trống MRV
    public static int[] findBestCellMRV(Board board) {
        int[] bestCell = null;
        int minRemaining = Integer.MAX_VALUE;

        int size = Board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.isEmptyCell(row, col)) {
                    int currentOptions = countValidOptions(board, row, col);
                    if (currentOptions == 0) {
                        return new int[]{row, col}; 
                    }

                    // Tìm ô có ít đáp án nhất
                    if (currentOptions < minRemaining) {
                        minRemaining = currentOptions;
                        bestCell = new int[]{row, col};
                    }
                }
            }
        }
        return bestCell;
    }

    // Hàm đếm số lượng giá trị hợp lệ từ 1-9 có thể điền vào ô
    public static int countValidOptions(Board board, int row, int col) {
        return BoardValidator.getCandidates(board, row, col).size();
    }
}
