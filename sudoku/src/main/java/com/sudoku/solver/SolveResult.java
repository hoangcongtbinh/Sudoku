package com.sudoku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sudoku.model.Step;

public class SolveResult {

    private boolean solved;
    private List<Step> steps = new ArrayList<>();
    private long timeToSolve;

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    public long getTimeToSolve() {
        return timeToSolve;
    }

    public void setTimeToSolve(long timeToSolve) {
        this.timeToSolve = timeToSolve;
    }
}