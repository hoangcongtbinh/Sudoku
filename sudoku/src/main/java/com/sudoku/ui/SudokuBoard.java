package com.sudoku.ui;

import javafx.scene.layout.GridPane;

public class SudokuBoard extends GridPane {
    private SudokuCell[][] cells;
    private int[][] board;
    private int[][] originalBoard;

    public SudokuBoard() {
        cells = new SudokuCell[9][9];
        board = new int[9][9];
        originalBoard = new int[9][9];

        setHgap(1);
        setVgap(1);
        getStyleClass().add("sudoku-board");

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = new SudokuCell(row, col);
                cells[row][col] = cell;
                add(cell, col, row);
                // thick borders for 3x3 blocks
                if ((col + 1) % 3 == 0 && col != 8)
                    GridPane.setMargin(cell, new javafx.geometry.Insets(0, 0, 0, 2));
                if ((row + 1) % 3 == 0 && row != 8)
                    GridPane.setMargin(cell, new javafx.geometry.Insets(0, 0, 2, 0));
            }
        }
    }

    public void setBoard(int[][] newBoard) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board[row][col] = newBoard[row][col];
                originalBoard[row][col] = newBoard[row][col];
                if (newBoard[row][col] != 0) {
                    cells[row][col].setValue(newBoard[row][col]);
                    cells[row][col].setState(CellState.GIVEN);
                } else {
                    cells[row][col].setValue(0);
                    cells[row][col].setState(CellState.EMPTY);
                }
            }
        }
    }

    public void updateCell(int row, int col, int value, CellState state) {
        board[row][col] = value;
        cells[row][col].setValue(value);
        cells[row][col].setState(state);
    }

    public void highlightCell(int row, int col) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (cells[r][c].getState() != CellState.GIVEN)
                    cells[r][c].setHighlighted(false);
            }
        }
        cells[row][col].setHighlighted(true);
    }

    public int[][] getCurrentBoard() {
        int[][] result = new int[9][9];
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                result[r][c] = cells[r][c].getValue();
        return result;
    }
}