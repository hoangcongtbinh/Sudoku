package com.sudoku.ui;

import javafx.geometry.Insets;
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
        setHgap(1);
        setVgap(1);
        setPadding(new Insets(2));
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
                    cell.requestFocus();
                });

                cells[row][col] = cell;
                add(cell, col, row);

                // Viền dày phân cách các khối 3x3
                boolean isRightBlockEdge = (col + 1) % 3 == 0 && col != 8;
                boolean isBottomBlockEdge = (row + 1) % 3 == 0 && row != 8;

                if (isRightBlockEdge && isBottomBlockEdge) {
                    cell.getStyleClass().add("border-right-bottom");
                } else if (isRightBlockEdge) {
                    cell.getStyleClass().add("border-right");
                } else if (isBottomBlockEdge) {
                    cell.getStyleClass().add("border-bottom");
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

    /**
     * FIX: Chỉ flash khi giá trị THỰC SỰ thay đổi
     * Tránh gọi animation khi validateBoard reset lỗi
     */
    public void updateCell(int row, int col, int value, CellState state) {
        SudokuCell cell = cells[row][col];
        int oldValue = cell.getValue();
        CellState oldState = cell.getState();

        board[row][col] = value;
        cell.setValue(value);
        cell.setState(state);

        // FIX: Chỉ flash khi giá trị hoặc state thực sự thay đổi
        // VÀ không phải là EMPTY (tránh flash khi erase)
        boolean valueChanged = oldValue != value;
        boolean stateChanged = oldState != state;
        boolean isSignificant = state == CellState.SOLVED ||
                state == CellState.USER_INPUT ||
                state == CellState.HINT ||
                state == CellState.TRYING ||
                state == CellState.BACKTRACK;

        if ((valueChanged || stateChanged) && isSignificant) {
            cell.flashChanged();
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
     * Kiểm tra và đánh dấu lỗi cho tất cả các ô.
     * Trả về true nếu có ít nhất một lỗi.
     * FIX: Tối ưu - chỉ gọi setError khi thực sự cần
     */
    public boolean validateBoard() {
        boolean hasAnyError = false;

        // Reset lỗi cũ - dùng mảng tạm để tránh gọi updateStyle nhiều lần
        boolean[][] newErrors = new boolean[9][9];

        // Kiểm tra từng ô
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = cells[r][c].getValue();
                if (val == 0) continue;

                // Kiểm tra trùng hàng
                for (int cc = 0; cc < 9; cc++) {
                    if (cc != c && cells[r][cc].getValue() == val) {
                        newErrors[r][c] = true;
                        newErrors[r][cc] = true;
                        hasAnyError = true;
                    }
                }

                // Kiểm tra trùng cột
                for (int rr = 0; rr < 9; rr++) {
                    if (rr != r && cells[rr][c].getValue() == val) {
                        newErrors[r][c] = true;
                        newErrors[rr][c] = true;
                        hasAnyError = true;
                    }
                }

                // Kiểm tra trùng box 3x3
                int boxRow = (r / 3) * 3;
                int boxCol = (c / 3) * 3;
                for (int rr = boxRow; rr < boxRow + 3; rr++) {
                    for (int cc = boxCol; cc < boxCol + 3; cc++) {
                        if ((rr != r || cc != c) && cells[rr][cc].getValue() == val) {
                            newErrors[r][c] = true;
                            newErrors[rr][cc] = true;
                            hasAnyError = true;
                        }
                    }
                }
            }
        }

        // Áp dụng lỗi một lần
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c].setError(newErrors[r][c]);
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

    public int[][] getOriginalGrid() {
        int[][] result = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(originalBoard[r], 0, result[r], 0, 9);
        }
        return result;
    }
}