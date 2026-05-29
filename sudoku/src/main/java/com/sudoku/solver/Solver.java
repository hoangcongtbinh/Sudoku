package com.sudoku.solver;

import com.sudoku.model.Board;

public interface Solver {
    SolveResult solve(Board board);
}
