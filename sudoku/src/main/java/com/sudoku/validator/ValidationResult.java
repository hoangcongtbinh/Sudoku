package com.sudoku.validator;

public class ValidationResult {
    private final boolean valid;
    private final String message;
    private final int row;
    private final int col;
    private final int conflictRow;
    private final int conflictCol;

    public ValidationResult(
            boolean valid,
            String message,
            int row,
            int col,
            int conflictRow,
            int conflictCol
    ) {
        this.valid = valid;
        this.message = message;
        this.row = row;
        this.col = col;
        this.conflictRow = conflictRow;
        this.conflictCol = conflictCol;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getConflictRow() {
        return conflictRow;
    }

    public int getConflictCol() {
        return conflictCol;
    }

    public boolean hasConflictCell() {
        return conflictRow >= 0 && conflictCol >= 0;
    }
}
