package com.sudoku.generator;

import com.sudoku.model.Board;
import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;
import com.sudoku.validator.BoardValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardGenerator {

    private final Random random = new Random();

    public GameSession generateBoard(Difficulty difficulty) {

        if (difficulty == null) {
            difficulty = Difficulty.MEDIUM;
        }

        Board solutionBoard = new Board();

        fillBoard(solutionBoard);

        Board puzzleBoard = solutionBoard.copy();

        removeCells(puzzleBoard, difficulty);

        return new GameSession(
                puzzleBoard,
                solutionBoard,
                difficulty
        );
    }

    private boolean fillBoard(Board board) {

        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        List<Integer> candidates = new ArrayList<>();

        for (int value = 1; value <= Board.getSize(); value++) {
            candidates.add(value);
        }

        Collections.shuffle(candidates);

        for (int value : candidates) {

            if (BoardValidator.isValidMove(board, row, col, value)) {

                board.setCell(row, col, value);

                if (fillBoard(board)) {
                    return true;
                }

                board.setCell(
                        row,
                        col,
                        Board.getEmptyValue()
                );
            }
        }

        return false;
    }

    private void removeCells(Board board, Difficulty difficulty) {

        int targetEmptyCells = difficulty.getEmptyCells();

        int removedCells = 0;
        int attempts = 0;

        while (removedCells < targetEmptyCells && attempts < 10000) {

            attempts++;

            int row = random.nextInt(Board.getSize());
            int col = random.nextInt(Board.getSize());

            if (board.isEmptyCell(row, col)) {
                continue;
            }

            int backupValue = board.getCell(row, col);

            board.setCell(
                    row,
                    col,
                    Board.getEmptyValue()
            );

            int solutionCount = countSolutions(board.copy());

            if (solutionCount == 1) {

                removedCells++;

            } else {

                board.setCell(
                        row,
                        col,
                        backupValue
                );
            }
        }
    }

    private int countSolutions(Board board) {
        return countSolutions(board, 0);
    }

    private int countSolutions(Board board, int currentCount) {

        if (currentCount > 1) {
            return currentCount;
        }

        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            return currentCount + 1;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int value = 1; value <= Board.getSize(); value++) {

            if (BoardValidator.isValidMove(board, row, col, value)) {

                board.setCell(row, col, value);

                currentCount = countSolutions(
                        board,
                        currentCount
                );

                board.setCell(
                        row,
                        col,
                        Board.getEmptyValue()
                );

                if (currentCount > 1) {
                    return currentCount;
                }
            }
        }

        return currentCount;
    }

    private int[] findEmptyCell(Board board) {

        for (int row = 0; row < Board.getSize(); row++) {

            for (int col = 0; col < Board.getSize(); col++) {

                if (board.isEmptyCell(row, col)) {

                    return new int[] {
                            row,
                            col
                    };
                }
            }
        }

        return null;
    }
}