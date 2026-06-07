package com.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import com.sudoku.model.Board;
import com.sudoku.model.Step;
import com.sudoku.model.StepType;
import com.sudoku.validator.BoardValidator;

public class BacktrackingSolver implements Solver {

    @Override
    public SolveResult solve(Board board) {

        if (board == null || !BoardValidator.isValidBoard(board)) {
            return SolveResult.noSolution();
        }

        List<Step> steps = new ArrayList<>();

        long start = System.nanoTime();

        boolean solved = solveRecursive(board, steps);

        long end = System.nanoTime();

        return new SolveResult(solved, steps, end - start);
    }

    private boolean solveRecursive(Board board, List<Step> steps) {

        int[] cell = findEmptyCell(board);

        if (cell == null) return true;

        int row = cell[0];
        int col = cell[1];

        int size = Board.getSize();
        int empty = Board.getEmptyValue();

        for (int val = 1; val <= size; val++) {

            if (BoardValidator.isValidMove(board, row, col, val)) {

                // TRY
                board.setCell(row, col, val);

                steps.add(new Step(
                        row,
                        col,
                        empty,
                        val,
                        StepType.TRY
                ));

                if (solveRecursive(board, steps)) {
                    return true;
                }

                // BACKTRACK
                board.setCell(row, col, empty);

                steps.add(new Step(
                        row,
                        col,
                        val,
                        empty,
                        StepType.BACKTRACK
                ));
            }
        }

        return false;
    }

    private int[] findEmptyCell(Board board) {

        int size = Board.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board.isEmptyCell(r, c)) {
                    return new int[]{r, c};
                }
            }
        }

        return null;
    }
}