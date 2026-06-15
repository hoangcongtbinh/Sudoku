package com.sudoku.benchmark;

import javafx.beans.property.*;

public class AlgorithmStats {
    private final StringProperty algorithmName;
    private final IntegerProperty steps;
    private final IntegerProperty backtracks;
    private final DoubleProperty timeMs;
    private final BooleanProperty best;
    private final BooleanProperty solved;

    public AlgorithmStats(String algorithmName, int steps, int backtracks, double timeMs, boolean solved) {
        this.algorithmName = new SimpleStringProperty(algorithmName);
        this.steps = new SimpleIntegerProperty(steps);
        this.backtracks = new SimpleIntegerProperty(backtracks);
        this.timeMs = new SimpleDoubleProperty(timeMs);
        this.best = new SimpleBooleanProperty(false);
        this.solved = new SimpleBooleanProperty(solved);
    }

    public String getAlgorithmName() { return algorithmName.get(); }
    public void setAlgorithmName(String name) { algorithmName.set(name); }
    public StringProperty algorithmNameProperty() { return algorithmName; }

    public int getSteps() { return steps.get(); }
    public void setSteps(int steps) { this.steps.set(steps); }
    public IntegerProperty stepsProperty() { return steps; }

    public int getBacktracks() { return backtracks.get(); }
    public void setBacktracks(int backtracks) { this.backtracks.set(backtracks); }
    public IntegerProperty backtracksProperty() { return backtracks; }

    public double getTimeMs() { return timeMs.get(); }
    public void setTimeMs(double timeMs) { this.timeMs.set(timeMs); }
    public DoubleProperty timeMsProperty() { return timeMs; }

    public boolean isBest() { return best.get(); }
    public void setBest(boolean best) { this.best.set(best); }
    public BooleanProperty bestProperty() { return best; }

    public boolean isSolved() { return solved.get(); }
    public void setSolved(boolean solved) { this.solved.set(solved); }
    public BooleanProperty solvedProperty() { return solved; }

    @Override
    public String toString() {
        return String.format("Steps: %d, Backtracks: %d, Time: %.2f ms, Solved: %s",
                getSteps(), getBacktracks(), getTimeMs(), isSolved() ? "Yes" : "No");
    }
}