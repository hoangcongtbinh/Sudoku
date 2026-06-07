package com.sudoku.ui;

public enum CellState {
    GIVEN,      // Original puzzle numbers (cyan)
    EMPTY,      // Empty cell (dark gray)
    CURRENT,    // Currently being processed (amber glow)
    TRYING,     // Trying a value (green)
    BACKTRACK,  // Just backtracked (red flash)
    SOLVED      // Correctly solved (green)
}