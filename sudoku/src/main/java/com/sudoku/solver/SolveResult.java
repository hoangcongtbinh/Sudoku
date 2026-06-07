package com.sudoku.solver;

import java.util.Collections;
import java.util.List;
import com.sudoku.model.*;

public class SolveResult {
    private boolean solved;
    private List<Step> steps;
    private long timeToSolve;

    // Constructor chung
    public SolveResult(boolean solved, List<Step> steps, long timeToSolve) {
        this.solved = solved;
        this.steps = steps == null ? List.of() : List.copyOf(steps);
        this.timeToSolve = timeToSolve;
    }

    // Tìm ra được lời giải
    public static SolveResult solved(List<Step> steps, long timeToSolve) {
        // Kiểm tra liệu có tồn tại bước giải không
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Solved result must contain steps");
        }
        return new SolveResult(true, steps, timeToSolve);
    }

    // Bảng đã được giải thành công trước đó
    public static SolveResult alreadySolved() {
        return new SolveResult(true, null, 0);
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

