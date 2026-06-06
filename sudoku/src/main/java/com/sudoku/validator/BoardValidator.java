package com.sudoku.validator;

import java.util.ArrayList;
import java.util.List;

import com.sudoku.model.Board;
import com.sudoku.model.Step;

public class BoardValidator {
    public static boolean isValidMove(Board board, Step step) {
        return validateMove(board, step).isValid();
    }

    public static boolean isValidMove(Board board, int row, int col, int value) {
        return validateMove(board, row, col, value).isValid();
    }

    public static ValidationResult validateMove(Board board, Step step) {
        if (step == null) {
            return invalid("Step must not be null.", -1, -1, -1, -1);
        }

        return validateMove(board, step.getRow(), step.getCol(), step.getValue());
    }

    public static ValidationResult validateMove(Board board, int row, int col, int value) {
        if (board == null) {
            return invalid("Board must not be null.", row, col, -1, -1);
        }
        if (!isValidPosition(row, col)) {
            return invalid("Row and column must be from 0 to 8.", row, col, -1, -1);
        }
        if (!isValidValue(value)) {
            return invalid("Cell value must be from 0 to 9.", row, col, -1, -1);
        }
        if (value == Board.getEmptyValue()) {
            return valid(row, col);
        }

        for (int checkedCol = 0; checkedCol < Board.getSize(); checkedCol++) {
            if (checkedCol != col && board.getCell(row, checkedCol) == value) {
                return invalid("Value already exists in the same row.",
                        row, col, row, checkedCol);
            }
        }

        for (int checkedRow = 0; checkedRow < Board.getSize(); checkedRow++) {
            if (checkedRow != row && board.getCell(checkedRow, col) == value) {
                return invalid("Value already exists in the same column.",
                        row, col, checkedRow, col);
            }
        }

        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;
        for (int checkedRow = boxStartRow; checkedRow < boxStartRow + 3; checkedRow++) {
            for (int checkedCol = boxStartCol; checkedCol < boxStartCol + 3; checkedCol++) {
                if ((checkedRow != row || checkedCol != col)
                        && board.getCell(checkedRow, checkedCol) == value) {
                    return invalid("Value already exists in the same 3x3 box.",
                            row, col, checkedRow, checkedCol);
                }
            }
        }

        return valid(row, col);
    }

    public static boolean isSolved(Board board) {
        return board != null && board.isFull() && validateBoard(board).isValid();
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
        return validateBoard(board).isValid();
    }

    public static ValidationResult validateBoard(Board board) {
        if (board == null) {
            return invalid("Board must not be null.", -1, -1, -1, -1);
        }

        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {
                int value = board.getCell(row, col);
                if (value != Board.getEmptyValue()) {
                    ValidationResult result = validateMove(board, row, col, value);
                    if (!result.isValid()) {
                        return result;
                    }
                }
            }
        }

        return valid(-1, -1);
    }

    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < Board.getSize() && col >= 0 && col < Board.getSize();
    }

    private static boolean isValidValue(int value) {
        return value >= Board.getEmptyValue() && value <= Board.getSize();
    }

    private static ValidationResult valid(int row, int col) {
        return new ValidationResult(true, null, row, col, -1, -1);
    }

    private static ValidationResult invalid(
            String message,
            int row,
            int col,
            int conflictRow,
            int conflictCol
    ) {
        return new ValidationResult(false, message, row, col, conflictRow, conflictCol);
    }
}
