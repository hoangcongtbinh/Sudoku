package com.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSession {
    public Board initBoard;
    private Board currentBoard;
    private Board solutionBoard;
    private Difficulty difficulty;
    private List<Step> history;
    private long startTime;
    private long endTime;
    private boolean finished;

    private int mistakes;
    private int hintsUsed;
    private long elapsedTime; // milliseconds

    // Constructor cũ - giữ nguyên cho các chỗ đang dùng (stats only)
    public GameSession(Difficulty difficulty, long elapsedTime, int mistakes, int hintsUsed) {
        this.difficulty = difficulty;
        this.elapsedTime = elapsedTime;
        this.mistakes = mistakes;
        this.hintsUsed = hintsUsed;
        this.finished = false;
        this.startTime = 0;
        this.endTime = 0;
        this.history = new ArrayList<>();
    }

    // Constructor mới - dùng trong BoardGenerator và test
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
        this.mistakes = 0;
        this.hintsUsed = 0;
        this.elapsedTime = 0;
    }

    public Board getInitBoard() {
        return initBoard != null ? initBoard.copy() : null;
    }

    public void setInitBoard(Board initBoard) {
        this.initBoard = initBoard != null ? initBoard.copy() : null;
    }

    public Board getCurrentBoard() {
        return currentBoard != null ? currentBoard.copy() : null;
    }

    public Board getSolutionBoard() {
        return solutionBoard != null ? solutionBoard.copy() : null;
    }

    public void setSolutionBoard(Board solutionBoard) {
        this.solutionBoard = solutionBoard != null ? solutionBoard.copy() : null;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public long getElapsedTime() {
        if (startTime == 0) {
            return elapsedTime;
        }
        long finishTime = finished ? endTime : System.currentTimeMillis();
        return finishTime - startTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        if (startTime != 0) {
            this.startTime = (finished ? endTime : System.currentTimeMillis()) - elapsedTime;
        }
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished && this.endTime == 0) {
            this.endTime = System.currentTimeMillis();
        }
    }

    public List<Step> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public long getStartTime() {
        return startTime;
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

        int lastIndex = history.size() - 1;
        Step lastStep = history.get(lastIndex);
        Step undoStep = new Step(
                lastStep.getRow(),
                lastStep.getCol(),
                lastStep.getValue(),
                lastStep.getPrevValue(),
                StepType.UNDO
        );

        if (!currentBoard.applyStep(undoStep)) {
            return false;
        }

        history.remove(lastIndex);
        return true;
    }

    public boolean isEditableCell(int row, int col) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            throw new IllegalArgumentException("Row and column must be from 0 to 8.");
        }
        if (initBoard == null) {
            return false;
        }
        return initBoard.getCell(row, col) == Board.getEmptyValue();
    }

    public void finish() {
        if (!finished) {
            this.finished = true;
            this.endTime = System.currentTimeMillis();
        }
    }

    public void restart() {
        if (initBoard != null) {
            this.currentBoard = initBoard.copy();
        }
        this.history.clear();
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.finished = false;
    }

    public static Board initializeNewBoard(Difficulty difficulty) {
        Board board = new Board();
        board.initBoard(difficulty);
        board.saveOriginal();
        return board;
    }
}