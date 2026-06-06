package com.sudoku.validator;

import java.util.ArrayList;
import java.util.List;

import com.sudoku.model.Board;
import com.sudoku.model.Step;

public class BoardValidator {
    public static boolean isValidMove(Board board, Step step) {
        if (board == null || step == null) {
            return false;
        }

        return isValidMove(board, step.getRow(), step.getCol(), step.getValue());
    }

    public static boolean isValidMove(Board board, int row, int col, int value) {
        if (board == null || !isValidPosition(row, col) || !isValidValue(value)) {
            return false;
        }

        if (value == Board.getEmptyValue()) {
            return true;
        }

        return isValidInRow(board, row, col, value)
                && isValidInColumn(board, row, col, value)
                && isValidInBox(board, row, col, value);
    }

    public static boolean isSolved(Board board) {
        if (board == null || !board.isFull()) {
            return false;
        }

        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {
                int value = board.getCell(row, col);
                if (!isValidMove(board, row, col, value)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static List<Integer> getCandidates(Board board, int row, int col) {
        List<Integer> candidates = new ArrayList<>();
        if (board == null || !isValidPosition(row, col) || !board.isEmptyCell(row, col)) {
            return candidates;
        }

        for (int value = 1; value <= Board.getSize(); value++) {
            if (isValidMove(board, row, col, value)) {
                candidates.add(value);
            }
        }

        return candidates;
    }

    public static boolean isValidBoard(Board board) {
        if (board == null) {
            return false;
        }

        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {
                int value = board.getCell(row, col);
                if (value != Board.getEmptyValue() && !isValidMove(board, row, col, value)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidInRow(Board board, int row, int currentCol, int value) {
        for (int col = 0; col < Board.getSize(); col++) {
            if (col != currentCol && board.getCell(row, col) == value) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidInColumn(Board board, int currentRow, int col, int value) {
        for (int row = 0; row < Board.getSize(); row++) {
            if (row != currentRow && board.getCell(row, col) == value) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidInBox(Board board, int currentRow, int currentCol, int value) {
        int boxStartRow = currentRow - currentRow % 3;
        int boxStartCol = currentCol - currentCol % 3;

        for (int row = boxStartRow; row < boxStartRow + 3; row++) {
            for (int col = boxStartCol; col < boxStartCol + 3; col++) {
                if ((row != currentRow || col != currentCol) && board.getCell(row, col) == value) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < Board.getSize() && col >= 0 && col < Board.getSize();
    }

    private static boolean isValidValue(int value) {
        return value >= Board.getEmptyValue() && value <= Board.getSize();
    }
}
