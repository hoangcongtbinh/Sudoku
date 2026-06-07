package com.sudoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuBoard extends JPanel {

    private SudokuCell[][] cells;
    private int[][] board;
    private int[][] originalBoard;

    // Colors
    private static final Color BOARD_BG = new Color(15, 15, 21);
    private static final Color GRID_LINE = new Color(0, 255, 204, 100);
    private static final Color THICK_GRID = new Color(0, 255, 204);

    public SudokuBoard() {
        cells = new SudokuCell[9][9];
        board = new int[9][9];
        originalBoard = new int[9][9];

        setLayout(new GridLayout(9, 9, 0, 0));
        setBackground(BOARD_BG);
        setPreferredSize(new Dimension(600, 600));
        setBorder(BorderFactory.createLineBorder(THICK_GRID, 2));

        initializeCells();
    }

    private void initializeCells() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = new SudokuCell(row, col);

                // Set thickness for 3x3 box borders
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == 8) ? 2 : 1;
                int right = (col == 8) ? 2 : 1;

                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, GRID_LINE));

                cells[row][col] = cell;
                add(cell);
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
        cells[row][col].repaint();
    }

    public void updateCellWithFlash(int row, int col, int value, CellState state) {
        updateCell(row, col, value, state);

        // Flash effect for backtracking
        if (state == CellState.BACKTRACK) {
            Timer flashTimer = new Timer(150, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (value != 0 && originalBoard[row][col] == 0) {
                        cells[row][col].setState(CellState.EMPTY);
                    } else if (value != 0 && originalBoard[row][col] != 0) {
                        cells[row][col].setState(CellState.GIVEN);
                    }
                    cells[row][col].repaint();
                }
            });
            flashTimer.setRepeats(false);
            flashTimer.start();
        }
    }

    public void clearHighlights() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (cells[row][col].getState() != CellState.GIVEN &&
                        cells[row][col].getState() != CellState.SOLVED) {
                    if (board[row][col] != 0) {
                        cells[row][col].setState(CellState.SOLVED);
                    } else {
                        cells[row][col].setState(CellState.EMPTY);
                    }
                }
                cells[row][col].repaint();
            }
        }
    }

    public void highlightCurrentCell(int row, int col) {
        // Reset previous current cell highlights
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (cells[r][c].getState() == CellState.CURRENT) {
                    if (board[r][c] != 0 && originalBoard[r][c] == 0) {
                        cells[r][c].setState(CellState.TRYING);
                    } else if (board[r][c] != 0 && originalBoard[r][c] != 0) {
                        cells[r][c].setState(CellState.GIVEN);
                    } else {
                        cells[r][c].setState(CellState.EMPTY);
                    }
                }
            }
        }

        // Set new current cell
        cells[row][col].setState(CellState.CURRENT);
        cells[row][col].repaint();
    }

    public void highlightRelatedCells(int row, int col) {
        // Clear previous highlights
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c].setHighlighted(false);
            }
        }

        // Highlight same row
        for (int c = 0; c < 9; c++) {
            if (c != col && board[row][c] != 0) {
                cells[row][c].setHighlighted(true);
            }
        }

        // Highlight same column
        for (int r = 0; r < 9; r++) {
            if (r != row && board[r][col] != 0) {
                cells[r][col].setHighlighted(true);
            }
        }

        // Highlight same 3x3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if ((r != row || c != col) && board[r][c] != 0) {
                    cells[r][c].setHighlighted(true);
                }
            }
        }

        repaint();
    }

    public void resetBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board[row][col] = originalBoard[row][col];
                if (originalBoard[row][col] != 0) {
                    cells[row][col].setValue(originalBoard[row][col]);
                    cells[row][col].setState(CellState.GIVEN);
                } else {
                    cells[row][col].setValue(0);
                    cells[row][col].setState(CellState.EMPTY);
                }
                cells[row][col].setHighlighted(false);
                cells[row][col].repaint();
            }
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setValueAt(int row, int col, int value) {
        board[row][col] = value;
        cells[row][col].setValue(value);
        cells[row][col].repaint();
    }

    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    public boolean isGivenCell(int row, int col) {
        return originalBoard[row][col] != 0;
    }
}
