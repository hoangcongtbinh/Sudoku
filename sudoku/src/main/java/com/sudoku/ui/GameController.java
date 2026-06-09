package com.sudoku.ui;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import com.sudoku.solver.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    // -------- FXML Injected Components --------
    @FXML private SudokuBoard sudokuBoard;           // Custom component
    @FXML private AlgorithmSelector algorithmSelector; // Custom component
    @FXML private DifficultySelector difficultySelector; // Custom component
    @FXML private StatsPanel statsPanel;             // Custom component
    @FXML private StepLogPanel stepLogPanel;         // Custom component
    @FXML private ControlPanel controlPanel;         // Custom component
    @FXML private Button newGameButton;

    // -------- Backend State --------
    private Board currentBoard;
    private Solver currentSolver;
    private Timeline animationTimeline;
    private List<Step> currentSteps;
    private int currentStepIndex;
    private long startTime;
    private AtomicBoolean isPlaying;
    private int backtracks;
    private int totalSteps;

    // -------- Initialization --------
    public void initialize() {
        // Animation timer (initially stopped)
        isPlaying = new AtomicBoolean(false);
        animationTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> playNextStep()));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);

        // Setup UI components initial states
        setupUI();
        // Initialize a new game
        newGame();
        // Setup event handlers
        attachEventHandlers();
    }

    private void setupUI() {
        // Disable pause/stop initially (no animation running)
        controlPanel.getPauseButton().setDisable(true);
        controlPanel.getStopButton().setDisable(true);
    }

    private void attachEventHandlers() {
        newGameButton.setOnAction(e -> newGame());
        
        controlPanel.getStepButton().setOnAction(e -> manualStep());
        controlPanel.getPlayButton().setOnAction(e -> startAnimation());
        controlPanel.getPauseButton().setOnAction(e -> stopAnimation());
        controlPanel.getResetButton().setOnAction(e -> resetToOriginal());
        controlPanel.getStopButton().setOnAction(e -> resetToOriginal());
        controlPanel.getSpeedSlider().valueProperty().addListener((obs, old, val) -> updateAnimationSpeed());

        algorithmSelector.valueProperty().addListener((obs, old, val) -> {
            if (currentBoard != null) {
                refreshSolver();
            }
        });

        difficultySelector.valueProperty().addListener((obs, old, val) -> newGame());
    }

    // -------- Game Logic --------
    private void newGame() {
        stopAnimation();
        Difficulty diff = difficultySelector.getSelectedDifficulty();
        currentBoard = BoardGenerator.generatestaticBoard(diff);
        sudokuBoard.setBoard(currentBoard.getGrid());
        refreshSolver();
        resetStats();
        clearLog();
        appendLog("New game started - Difficulty: " + diff);
    }

    private void refreshSolver() {
        String algo = algorithmSelector.getValue();
        if (algo == null) return;
        
        if (algo.startsWith("MRV")) {
            currentSolver = new MRVSolver();
        } else if (algo.startsWith("Simulated")) {
            currentSolver = new SASolver();
        } else {
            currentSolver = new BacktrackingSolver();
        }
        
        // Use a copy to prevent solving the main board immediately
        SolveResult result = currentSolver.solve(currentBoard.copy());
        currentSteps = result.getSteps();
        currentStepIndex = 0;
        totalSteps = currentSteps.size();
        
        // Count total backtracks for the statistics panel
        backtracks = 0;
        for (Step s : currentSteps) {
            if (s.isBacktrack()) {
                backtracks++;
            }
        }
        
        updateStats();
        appendLog("Algorithm ready: " + algo + " | Total steps estimated: " + totalSteps);
    }

    private void resetToOriginal() {
        stopAnimation();
        sudokuBoard.setBoard(currentBoard.getOriginalGrid());
        currentStepIndex = 0;
        startTime = 0;
        updateStats();
        clearLog();
        appendLog("Reset to original puzzle.");
    }

    // -------- Animation Control --------
    private void startAnimation() {
        if (currentSteps == null || currentStepIndex >= currentSteps.size()) {
            appendLog("Solving already complete. Start a new game.");
            return;
        }
        isPlaying.set(true);
        controlPanel.setButtonsEnabled(true);
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        updateAnimationSpeed();
        animationTimeline.play();
        appendLog("▶️ Animation started");
    }

    private void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        isPlaying.set(false);
        controlPanel.setButtonsEnabled(false);
        appendLog("⏸️ Animation paused");
    }

    private void updateAnimationSpeed() {
        double speed = controlPanel.getSpeedSlider().getValue();
        // Convert slider value to seconds (e.g. 0.1s to 1.0s)
        Duration duration = Duration.seconds(speed);
        animationTimeline.getKeyFrames().clear();
        animationTimeline.getKeyFrames().add(new KeyFrame(duration, e -> playNextStep()));
    }

    private void playNextStep() {
        if (!isPlaying.get()) return;
        if (currentSteps == null || currentStepIndex >= currentSteps.size()) {
            stopAnimation();
            appendLog("✅ Solved! Total steps: " + currentStepIndex);
            return;
        }
        Step step = currentSteps.get(currentStepIndex);
        applyStep(step);
        currentStepIndex++;
        updateStats();
        appendLog(formatStep(step));
        
        if (currentStepIndex >= currentSteps.size()) {
            stopAnimation();
            appendLog("🎉 Solving completed.");
        }
    }

    private void manualStep() {
        if (currentSteps == null || currentStepIndex >= currentSteps.size()) {
            appendLog("No more steps. New game?");
            return;
        }
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        Step step = currentSteps.get(currentStepIndex);
        applyStep(step);
        currentStepIndex++;
        updateStats();
        appendLog(formatStep(step));
        if (currentStepIndex >= currentSteps.size()) {
            appendLog("🎉 Final step reached.");
        }
    }

    private void applyStep(Step step) {
        int row = step.getRow();
        int col = step.getCol();
        if (row == -1 || col == -1) {
            // For solvers that do not update single cells (e.g. SA)
            return;
        }
        int value = step.getValue();
        CellState state = mapStepTypeToCellState(step.getType());
        sudokuBoard.updateCell(row, col, value, state);
        
        // Highlight current cell
        sudokuBoard.highlightCell(row, col);
    }

    private CellState mapStepTypeToCellState(StepType type) {
        switch (type) {
            case TRY: return CellState.TRYING;
            case BACKTRACK: return CellState.BACKTRACK;
            case PLACE: return CellState.SOLVED;
            case SOLVER_STEP: return CellState.SOLVED;
            case UNDO: return CellState.BACKTRACK;
            default: return CellState.EMPTY;
        }
    }

    private String formatStep(Step step) {
        if (step.getRow() == -1) {
            return "Cost/Conflicts: " + step.getValue();
        }
        String typeStr = step.getType().toString();
        switch (step.getType()) {
            case TRY:
                typeStr = "Try";
                break;
            case BACKTRACK:
                typeStr = "Backtrack";
                break;
            case PLACE:
                typeStr = "Place";
                break;
            case SOLVER_STEP:
                typeStr = "Solve Step";
                break;
            case UNDO:
                typeStr = "Undo";
                break;
        }
        return String.format("%s at (%d, %d) to %d", 
            typeStr, 
            step.getRow() + 1, 
            step.getCol() + 1, 
            step.getValue());
    }

    // -------- Statistics UI Update --------
    private void resetStats() {
        currentStepIndex = 0;
        startTime = 0;
        statsPanel.reset();
    }

    private void updateStats() {
        Platform.runLater(() -> {
            statsPanel.updateSteps(currentStepIndex);
            
            // Count backtracks up to currentStepIndex
            int backtracksPlayed = 0;
            if (currentSteps != null) {
                for (int i = 0; i < currentStepIndex && i < currentSteps.size(); i++) {
                    if (currentSteps.get(i).isBacktrack()) {
                        backtracksPlayed++;
                    }
                }
            }
            statsPanel.updateBacktracks(backtracksPlayed);
            
            int filled = countFilledCells();
            statsPanel.updateFilled(filled);
            
            long elapsed = (startTime > 0 ? System.currentTimeMillis() - startTime : 0);
            statsPanel.updateTime(elapsed / 1000.0);
        });
    }

    private int countFilledCells() {
        int count = 0;
        int[][] grid = sudokuBoard.getCurrentBoard();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] != 0) count++;
            }
        }
        return count;
    }

    // -------- Logging --------
    private void clearLog() {
        stepLogPanel.clear();
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            stepLogPanel.addLog(msg);
        });
    }
}