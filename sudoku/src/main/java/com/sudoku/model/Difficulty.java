package com.sudoku.model;

public enum Difficulty {
    EASY(30),
    MEDIUM(40),
    HARD(50),
    EXPERT(55);

    private final int emptyCells;

    Difficulty(int emptyCells) {
        this.emptyCells = emptyCells;
    }

    public int getEmptyCells() {
        return emptyCells;
    }
}