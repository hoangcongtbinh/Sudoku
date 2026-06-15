package com.sudoku.solver;

import com.sudoku.model.Board;
import com.sudoku.model.Step;

import java.util.List;

public class SolveResult {
    private boolean solved;
    private List<Step> steps;
    private long durationNanos;
    private Board finalBoard;
    private int backtrackCount;

    public SolveResult(boolean solved, List<Step> steps, long durationNanos) {
        this.solved = solved;
        this.steps = steps;
        this.durationNanos = durationNanos;
        this.backtrackCount = 0;
        for (Step s : steps) {
            if (s.isBacktrack()) backtrackCount++;
        }
    }

    public SolveResult(boolean solved, List<Step> steps, long durationNanos, Board finalBoard) {
        this(solved, steps, durationNanos);
        this.finalBoard = finalBoard;
    }

    public static SolveResult noSolution() {
        return new SolveResult(false, java.util.Collections.emptyList(), 0);
    }

    public boolean isSolved() { return solved; }
    public List<Step> getSteps() { return steps; }
    public long getDurationNanos() { return durationNanos; }
    public Board getFinalBoard() { return finalBoard; }
    public int getBacktrackCount() { return backtrackCount; }
}