package com.sudoku.solver;

import com.sudoku.model.Board;
import com.sudoku.model.Step;
import com.sudoku.model.StepType;
import com.sudoku.validator.BoardValidator;

public class BacktrackingSolver implements Solver {

    @Override
    public SolveResult solve(Board board) {
        SolveResult result = new SolveResult();

        if (board == null || !BoardValidator.isValidBoard(board)) {
            result.setSolved(false);
            result.setTimeToSolve(0);
            return result;
        }

        long startTime = System.nanoTime();

        boolean solved = solveRecursive(board, result);

        long endTime = System.nanoTime();

        result.setSolved(solved);
        result.setTimeToSolve(endTime - startTime);

        return result;
    }

    private boolean solveRecursive(Board board, SolveResult result) {

        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int value = 1; value <= Board.getSize(); value++) {

            if (BoardValidator.isValidMove(board, row, col, value)) {

                board.setCell(row, col, value);

                result.addStep(
                        new Step(
                                row,
                                col,
                                Board.getEmptyValue(),
                                value,
                                StepType.SOLVER_STEP
                        )
                );

                if (solveRecursive(board, result)) {
                    return true;
                }

                board.setCell(row, col, Board.getEmptyValue());

                result.addStep(
                        new Step(
                                row,
                                col,
                                value,
                                Board.getEmptyValue(),
                                StepType.SOLVER_STEP
                        )
                );
            }
        }

        return false;
    }

    private int[] findEmptyCell(Board board) {

        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {

                if (board.isEmptyCell(row, col)) {
                    return new int[]{row, col};
                }

            }
        }

        return null;
    }
}