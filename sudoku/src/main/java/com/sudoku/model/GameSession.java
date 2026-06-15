package com.sudoku.model;

public class GameSession {
    private Board puzzleBoard;
    private Board solutionBoard;

    private Difficulty difficulty;
    private long elapsedTime; // milliseconds
    private int mistakes;
    private int hintsUsed;
    private boolean finished;

    // Constructor cũ - giữ nguyên cho các chỗ đang dùng (stats only)
    public GameSession(Difficulty difficulty, long elapsedTime, int mistakes, int hintsUsed) {
        this.difficulty = difficulty;
        this.elapsedTime = elapsedTime;
        this.mistakes = mistakes;
        this.hintsUsed = hintsUsed;
        this.finished = false;
    }

    // Constructor mới - dùng trong BoardGenerator
    public GameSession(Board puzzleBoard, Board solutionBoard, Difficulty difficulty) {
        this.puzzleBoard = puzzleBoard;
        this.solutionBoard = solutionBoard;
        this.difficulty = difficulty;
        this.elapsedTime = 0;
        this.mistakes = 0;
        this.hintsUsed = 0;
        this.finished = false;
    }

    public Board getInitBoard() { return puzzleBoard; }
    public void setInitBoard(Board puzzleBoard) { this.puzzleBoard = puzzleBoard; }

    public Board getCurrentBoard() { return puzzleBoard; }

    public Board getSolutionBoard() { return solutionBoard; }
    public void setSolutionBoard(Board solutionBoard) { this.solutionBoard = solutionBoard; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

    public int getMistakes() { return mistakes; }
    public void setMistakes(int mistakes) { this.mistakes = mistakes; }

    public int getHintsUsed() { return hintsUsed; }
    public void setHintsUsed(int hintsUsed) { this.hintsUsed = hintsUsed; }

    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }




}