package com.sudoku.solver;

import java.util.Collections;
import java.util.List;
import com.sudoku.model.*;

public class SolveResult {
    private boolean solved;
    private List<Step> steps;
    private long timeToSolve;

    // Có lời giải
    public SolveResult(boolean solved, List<Step> steps, long timeToSolve) {
        this.solved = solved;
        this.steps = steps == null ? null : Collections.unmodifiableList(steps);
        this.timeToSolve = timeToSolve;
    }

    // Bảng đã được giải xong
    public static SolveResult alreadySolved() {
        return new SolveResult(true, List.of(), 0);
    }

    // Không có lời giải
    public static SolveResult noSolution() {
        return new SolveResult(false, null, 0);
    }

    public boolean isSolved() {
        return solved;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public long getTimeToSolve() {
        return timeToSolve;
    }
}

