package com.sudoku.model;

import com.sudoku.generator.BoardGenerator;

public class Board {
    private static final int SIZE = 9;
    private static final int EMPTY = 0;

    private int[][] values; // Trang thai bang hien tai
    private int[][] original; // Tranng thai ban dau de reset

    public Board() {
        this.values = new int[SIZE][SIZE];
        this.original = new int[SIZE][SIZE];
    }

    public Board(int[][] values) {
        if (!isValidMatrix(values)) {
            throw new IllegalArgumentException("Board must be a 9x9 matrix with values from 0 to 9.");
        }

        this.values = copyMatrix(values);
        this.original = copyMatrix(values);
    }

    // bang tai thoi diem choi
    public int [][] getGrid(){
        return copyMatrix(values);
    }

    //bang ban dau
    public int[][] getOriginalGrid() {
        return copyMatrix(original);
    }

    /**
     * Initializes the board with a puzzle of the given difficulty.
     * Uses BoardGenerator to generate the puzzle.
     */
    public void initBoard(Difficulty difficulty) {
        Board puzzleBoard = BoardGenerator.generatestaticBoard(difficulty); // implement BoardGenerator.generate()

        if (puzzleBoard != null) {
            int[][] puzzle = puzzleBoard.getGrid();
            if(isValidMatrix(puzzle)) {
                this.values = copyMatrix(puzzle);
                saveOriginal();
                return;
            }
        } else {
            // Fallback: empty board
            this.values = new int[SIZE][SIZE];
            this.original = new int[SIZE][SIZE];
        }
    }



    /**
     * Saves the current board state as the original (for reset).
     */
    public void saveOriginal() {
        this.original = copyMatrix(values);
    }

    /**
     * Sets the original board state.
     */
    public void setOriginalGrid(int[][] original) {
        if (isValidMatrix(original)) {
            this.original = copyMatrix(original);
        }
    }


    public int getCell(int row, int col) {
        validatePosition(row, col);
        return values[row][col];
    }

    public void setCell(int row, int col, int value) {
        validatePosition(row, col);
        validateValue(value);
        values[row][col] = value;
    }

    public boolean applyStep(Step step) {
        if (step == null) {
            return false;
        }

        int row = step.getRow();
        int col = step.getCol();
        int newValue = step.getValue();

        if (!isValidPosition(row, col) || !isValidValue(newValue)) {
            return false;
        }

        values[row][col] = newValue;
        return true;
    }

    public Board copy() {
        return new Board(values);
    }

    public int[][] getValues() {
        return copyMatrix(values);
    }

    public boolean isEmptyCell(int row, int col) {
        return getCell(row, col) == EMPTY;
    }

    public boolean isFull() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (values[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getSize() {
        return SIZE;
    }

    public static int getEmptyValue() {
        return EMPTY;
    }

    private static boolean isValidMatrix(int[][] matrix) {
        if (matrix == null || matrix.length != SIZE) {
            return false;
        }

        for (int row = 0; row < SIZE; row++) {
            if (matrix[row] == null || matrix[row].length != SIZE) {
                return false;
            }

            for (int col = 0; col < SIZE; col++) {
                if (!isValidValue(matrix[row][col])) {
                    return false;
                }
            }
        }

        return true;
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copied = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(source[row], 0, copied[row], 0, SIZE);
        }
        return copied;
    }

    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private static boolean isValidValue(int value) {
        return value >= EMPTY && value <= SIZE;
    }

    private static void validatePosition(int row, int col) {
        if (!isValidPosition(row, col)) {
            throw new IllegalArgumentException("Row and column must be from 0 to 8.");
        }
    }

    private static void validateValue(int value) {
        if (!isValidValue(value)) {
            throw new IllegalArgumentException("Cell value must be from 0 to 9.");
        }
    }

    public int[] findEmptyCell() {
        int size = Board.getSize(); // Hoặc dùng this.size nếu class có biến này
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (this.isEmptyCell(r, c)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public int countEmptyCells() {
        int count = 0;
        int size = Board.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (this.isEmptyCell(r, c)) {
                    count++;
                }
            }
        }
        return count;
    }
}
