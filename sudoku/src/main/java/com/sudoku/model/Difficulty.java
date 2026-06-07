package com.sudoku.model;

public enum Difficulty {
    EASY(35),
    MEDIUM(45),
    HARD(55),
    EXPERT(60);

    private final int emptyCells;

    Difficulty(int emptyCells) {
        this.emptyCells = emptyCells;
    }

    public int getEmptyCells() {
        return emptyCells;
    }
}