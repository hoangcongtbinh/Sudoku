package com.sudoku.model;

public class Step {
    private int row;
    private int col;
    private int prevValue;
    private int value;
    private StepType type;

    public Step(int row, int col, int prevValue, int value, StepType type) {
        this.row = row;
        this.col = col;
        this.prevValue = prevValue;
        this.value = value;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getValue() {
        return value;
    }

    public StepType getType() {
        return type;
    }

    public boolean isBacktrack() {
        return type == StepType.BACKTRACK || type == StepType.UNDO;
    }
}
