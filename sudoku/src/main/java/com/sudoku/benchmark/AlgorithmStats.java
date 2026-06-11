package com.sudoku.benchmark;

public class AlgorithmStats {
    private final String algorithmName;
    private final boolean solved;
    private final long elapsedTimeNanos;
    private final int stepCount;
    private final int initialEmptyCells;
    private final String errorMessage;

    public AlgorithmStats(
            String algorithmName,
            boolean solved,
            long elapsedTimeNanos,
            int stepCount,
            int initialEmptyCells,
            String errorMessage
    ) {
        this.algorithmName = algorithmName;
        this.solved = solved;
        this.elapsedTimeNanos = elapsedTimeNanos;
        this.stepCount = stepCount;
        this.initialEmptyCells = initialEmptyCells;
        this.errorMessage = errorMessage;
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
}
