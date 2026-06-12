package com.sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class SudokuBoard extends GridPane {
    private SudokuCell[][] cells;
    private int[][] board;
    private int[][] originalBoard;

    private OnCellClickedListener onCellClickedListener;

    public interface OnCellClickedListener {
        void onCellClicked(int row, int col);
    }

    public void setOnCellClicked(OnCellClickedListener listener) {
        this.onCellClickedListener = listener;
    }

    public SudokuBoard() {
        cells = new SudokuCell[9][9];
        board = new int[9][9];
        originalBoard = new int[9][9];

        // Căn giữa bảng
        setAlignment(Pos.CENTER);
        // Khoảng cách giữa các ô (tạo đường kẻ mảnh)
        setHgap(0.5);
        setVgap(0.5);
        getStyleClass().add("sudoku-board");

        initializeCells();
    }

    private void initializeCells() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = new SudokuCell(row, col);
                final int r = row;
                final int c = col;

                cell.setOnMouseClicked(event -> {
                    if (onCellClickedListener != null) {
                        onCellClickedListener.onCellClicked(r, c);
                    }
                    highlightCell(r, c);
                });

                cells[row][col] = cell;
                add(cell, col, row);

                // Thêm class CSS để tạo viền dày giữa các khối 3x3
                if ((col + 1) % 3 == 0 && col != 8) {
                    cell.getStyleClass().add("right-thick-border");
                }
                if ((row + 1) % 3 == 0 && row != 8) {
                    cell.getStyleClass().add("bottom-thick-border");
                }
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
                cells[r][c].setHighlighted(false);
            }
        }
        if (row >= 0 && row < 9 && col >= 0 && col < 9) {
            cells[row][col].setHighlighted(true);
        }
    }

    public int[][] getCurrentBoard() {
        int[][] result = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                result[r][c] = cells[r][c].getValue();
            }
        }
        return result;
    }
    public int getValueAt(int row, int col) {
        return cells[row][col].getValue();
    }
}