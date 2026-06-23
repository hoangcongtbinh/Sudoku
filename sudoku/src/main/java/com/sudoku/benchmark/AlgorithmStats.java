package com.sudoku.benchmark;

public class AlgorithmStats {
    private final String algorithmName;
    private final boolean solved;
    private final long elapsedTimeNanos;
    private final int stepCount;
    private final int initialEmptyCells;
    private final String errorMessage;
    private final int backtracks;
    private boolean best;
    private boolean secondBest;

    public AlgorithmStats(
            String algorithmName,
            boolean solved,
            long elapsedTimeNanos,
            int stepCount,
            int initialEmptyCells,
            int backtracks,
            String errorMessage
    ) {
        this.algorithmName = algorithmName;
        this.solved = solved;
        this.elapsedTimeNanos = elapsedTimeNanos;
        this.stepCount = stepCount;
        this.initialEmptyCells = initialEmptyCells;
        this.backtracks = backtracks;
        this.errorMessage = errorMessage;
        this.best = false;
        this.secondBest = false;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public boolean isSolved() {
        return solved;
    }

    public long getElapsedTimeNanos() {
        return elapsedTimeNanos;
    }

    public double getElapsedTimeMillis() {
        return elapsedTimeNanos / 1_000_000.0;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getInitialEmptyCells() {
        return initialEmptyCells;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public int getBacktracks() {
        return backtracks;
    }

    public boolean isBest() {
        return best;
    }

    public void setBest(boolean best) {
        this.best = best;
    }

    public boolean isSecondBest() {
        return secondBest;
    }

    public void setSecondBest(boolean secondBest) {
        this.secondBest = secondBest;
    }

    // Alias getters for JavaFX PropertyValueFactory
    public double getTimeMs() {
        return getElapsedTimeMillis();
    }

    public int getTotalSteps() {
        return getStepCount();
    }

    @Override
    public String toString() {
        if (!solved) {
            return hasError() ? "Failed (" + errorMessage + ")" : "Unsolved";
        }
        return String.format("Solved in %.2f ms (Steps: %d, BT: %d)", getElapsedTimeMillis(), stepCount, backtracks);
    }
}
