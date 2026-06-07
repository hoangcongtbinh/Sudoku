package com.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSession {
    private Board initBoard;
    private Board currentBoard;
    private Board solutionBoard;
    private Difficulty difficulty;
    private List<Step> history;
    private long startTime;
    private long endTime;
    private boolean finished;

    public GameSession(Board initBoard, Board solutionBoard, Difficulty difficulty) {
        if (initBoard == null) {
            throw new IllegalArgumentException("Initial board must not be null.");
        }

        this.initBoard = initBoard.copy();
        this.currentBoard = initBoard.copy();
        this.solutionBoard = solutionBoard == null ? null : solutionBoard.copy();
        this.difficulty = difficulty;
        this.history = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.finished = false;
    }

    public Board getInitBoard() {
        return initBoard.copy();
    }

    public Board getCurrentBoard() {
        return currentBoard.copy();
    }

    public Board getSolutionBoard() {
        return solutionBoard == null ? null : solutionBoard.copy();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<Step> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTime() {
        long finishTime = finished ? endTime : System.currentTimeMillis();
        return finishTime - startTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean makeMove(int row, int col, int value) {
        if (finished || !isEditableCell(row, col)) {
            return false;
        }

        int prevValue = currentBoard.getCell(row, col);
        Step step = new Step(row, col, prevValue, value, StepType.INPUT);
        if (!currentBoard.applyStep(step)) {
            return false;
        }

        history.add(step);
        return true;
    }

    public boolean clearCell(int row, int col) {
        if (finished || !isEditableCell(row, col)) {
            return false;
        }

        int prevValue = currentBoard.getCell(row, col);
        Step step = new Step(row, col, prevValue, Board.getEmptyValue(), StepType.CLEAR);
        if (!currentBoard.applyStep(step)) {
            return false;
        }

        history.add(step);
        return true;
    }

    public boolean applyStep(Step step) {
        if (finished || step == null || !isEditableCell(step.getRow(), step.getCol())) {
            return false;
        }

        if (!currentBoard.applyStep(step)) {
            return false;
        }

        history.add(step);
        return true;
    }

    public boolean undo() {
        if (finished || history.isEmpty()) {
            return false;
        }

        Step lastStep = history.remove(history.size() - 1);
        Step undoStep = new Step(
                lastStep.getRow(),
                lastStep.getCol(),
                lastStep.getValue(),
                lastStep.getPrevValue(),
                StepType.UNDO
        );

        return currentBoard.applyStep(undoStep);
    }

    public boolean isEditableCell(int row, int col) {
        return initBoard.getCell(row, col) == Board.getEmptyValue();
    }

    public void finish() {
        if (!finished) {
            this.finished = true;
            this.endTime = System.currentTimeMillis();
        }
    }

    public void restart() {
        this.currentBoard = initBoard.copy();
        this.history.clear();
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.finished = false;
    }
}
