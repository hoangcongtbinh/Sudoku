package com.sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

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

        setAlignment(Pos.CENTER);
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

                // Viền dày giữa các khối 3x3
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
                cells[row][col].setValue(newBoard[row][col]);

                if (newBoard[row][col] != 0) {
                    cells[row][col].setState(CellState.GIVEN);
                    cells[row][col].setFixed(true);
                } else {
                    cells[row][col].setState(CellState.EMPTY);
                    cells[row][col].setFixed(false);
                }
                cells[row][col].setError(false);
            }
        }
    }

    public void updateCell(int row, int col, int value, CellState state) {
        board[row][col] = value;
        cells[row][col].setValue(value);
        cells[row][col].setState(state);

        // Flash highlight khi ô thay đổi
        if (state == CellState.SOLVED || state == CellState.USER_INPUT ||
                state == CellState.TRYING || state == CellState.BACKTRACK) {
            cells[row][col].flashChanged();
        }
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

    /**
     * Kiểm tra và đánh dấu lỗi cho tất cả các ô
     * Trả về true nếu có lỗi
     */
    public boolean validateBoard() {
        boolean hasAnyError = false;

        // Reset lỗi cũ
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c].setError(false);
            }
        }

        // Kiểm tra từng ô
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = cells[r][c].getValue();
                if (val == 0) continue;

                // Kiểm tra trùng hàng
                for (int cc = 0; cc < 9; cc++) {
                    if (cc != c && cells[r][cc].getValue() == val) {
                        cells[r][c].setError(true);
                        cells[r][cc].setError(true);
                        hasAnyError = true;
                    }
                }

                // Kiểm tra trùng cột
                for (int rr = 0; rr < 9; rr++) {
                    if (rr != r && cells[rr][c].getValue() == val) {
                        cells[r][c].setError(true);
                        cells[rr][c].setError(true);
                        hasAnyError = true;
                    }
                }

                // Kiểm tra trùng box 3x3
                int boxRow = (r / 3) * 3;
                int boxCol = (c / 3) * 3;
                for (int rr = boxRow; rr < boxRow + 3; rr++) {
                    for (int cc = boxCol; cc < boxCol + 3; cc++) {
                        if ((rr != r || cc != c) && cells[rr][cc].getValue() == val) {
                            cells[r][c].setError(true);
                            cells[rr][cc].setError(true);
                            hasAnyError = true;
                        }
                    }
                }
            }
        }

        return hasAnyError;
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

    public SudokuCell getCell(int row, int col) {
        return cells[row][col];
    }

    public boolean isFixedCell(int row, int col) {
        return cells[row][col].isFixed();
    }
}