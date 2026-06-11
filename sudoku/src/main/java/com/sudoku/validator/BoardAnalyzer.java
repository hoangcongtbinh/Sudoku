package com.sudoku.validator;

import com.sudoku.model.Board;
import com.sudoku.model.Step;
import com.sudoku.model.StepType;
import com.sudoku.solver.MRVSolver;

import java.util.List;

public class BoardAnalyzer {
    // Hàm gợi ý 1 ô tiếp theo dùng MRV
    public static Step getHintCell(Board board) {
        if (board == null || BoardValidator.isSolved(board)) {
            return null;
        }

        MRVSolver solverInstance = new MRVSolver();
        int[] bestCellPos = solverInstance.findBestCellMRV(board);

        if (bestCellPos == null) {
            return null;
        }

        int row = bestCellPos[0];
        int col = bestCellPos[1];

        List<Integer> candidates = BoardValidator.getCandidates(board, row, col);

        if (candidates.isEmpty()) {
            return null;
        }

        int hintValue = candidates.get(0);
        int prevValue = board.getCell(row, col);

        return new Step(row, col, prevValue, hintValue, StepType.HINT);
    }
}
