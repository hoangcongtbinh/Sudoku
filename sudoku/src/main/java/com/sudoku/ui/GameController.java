package com.sudoku.ui;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import com.sudoku.solver.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    // -------- FXML Injected Components (if using FXML) --------
    @FXML private BorderPane rootPane;
    @FXML private SudokuBoard sudokuBoard;           // Custom component
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private ComboBox<String> difficultySelector;
    @FXML private Button newGameButton;
    @FXML private Button stepButton;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;
    @FXML private Label stepsLabel;
    @FXML private Label timeLabel;
    @FXML private Label backtracksLabel;
    @FXML private Label filledLabel;
    @FXML private TextArea stepLogArea;

    // -------- Non-FXML Components (if you build UI programmatically) --------
    // You can also instantiate these manually and add to layout

    // -------- Backend State --------
    private Board currentBoard;
    private Solver currentSolver;
    private Timeline animationTimeline;
    private List<Step> currentSteps;
    private int currentStepIndex;
    private long startTime;
    private AtomicBoolean isPlaying;
    private int backtracks;
    private long totalSteps;

    // -------- Initialization --------
    @FXML
    public void initialize() {
        // Setup UI components
        setupUI();
        // Initialize a new game
        newGame();
        // Setup event handlers
        attachEventHandlers();
        // Animation timer (initially stopped)
        isPlaying = new AtomicBoolean(false);
        animationTimeline = new Timeline(new KeyFrame(Duration.millis(250), e -> playNextStep()));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void setupUI() {
        // Algorithm selector items
        algorithmSelector.getItems().addAll("Backtracking", "MRV", "Simulated Annealing");
        algorithmSelector.setValue("Backtracking");

        // Difficulty selector items
        difficultySelector.getItems().addAll("Easy", "Medium", "Hard", "Expert");
        difficultySelector.setValue("Medium");

        // Speed slider (range 0.1s to 1.0s per step)
        speedSlider.setMin(0.1);
        speedSlider.setMax(1.0);
        speedSlider.setValue(0.3);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.3);
        speedSlider.setBlockIncrement(0.1);

        // Disable pause initially (no animation running)
        pauseButton.setDisable(true);
    }

    private void attachEventHandlers() {
        newGameButton.setOnAction(e -> newGame());
        stepButton.setOnAction(e -> manualStep());
        playButton.setOnAction(e -> startAnimation());
        pauseButton.setOnAction(e -> stopAnimation());
        resetButton.setOnAction(e -> resetToOriginal());
        speedSlider.valueProperty().addListener((obs, old, val) -> updateAnimationSpeed());

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
        Difficulty diff = Difficulty.valueOf(difficultySelector.getValue().toUpperCase());
        currentBoard = BoardGenerator.generatestaticBoard(diff);  // Implement this in your model
        sudokuBoard.setBoard(currentBoard.getGrid());
        refreshSolver();
        resetStats();
        clearLog();
        appendLog("New game started - Difficulty: " + difficultySelector.getValue());
    }

    private void refreshSolver() {
        String algo = algorithmSelector.getValue();
        if (algo == null) return;
        switch (algo) {
            case "Backtracking":
                currentSolver = new BacktrackingSolver();
                break;
            case "MRV":
                currentSolver = new MRVSolver();
                break;
            case "Simulated Annealing":
                currentSolver = new SASolver();
                break;
        }
        // Pre-compute all steps (or compute on the fly).
        // For simplicity, we compute the whole solution path at once.
        SolveResult result = currentSolver.solveWithSteps(currentBoard);
        currentSteps = result.getSteps();
        currentStepIndex = 0;
        totalSteps = currentSteps.size();
        backtracks = result.getBacktrackCount();
        updateStats();
        appendLog("Algorithm ready: " + algo + " | Total steps estimated: " + totalSteps);
    }

    private void resetToOriginal() {
        stopAnimation();
        sudokuBoard.setBoard(currentBoard.getOriginalGrid());
        currentStepIndex = 0;
        updateStats();
        appendLog("Reset to original puzzle.");
    }

    // -------- Animation Control --------
    private void startAnimation() {
        if (currentStepIndex >= totalSteps) {
            appendLog("Solving already complete. Start a new game.");
            return;
        }
        isPlaying.set(true);
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        stepButton.setDisable(true);
        startTime = System.currentTimeMillis();
        updateAnimationSpeed();
        animationTimeline.play();
        appendLog("▶️ Animation started");
    }

    private void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        isPlaying.set(false);
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stepButton.setDisable(false);
        appendLog("⏸️ Animation paused");
    }

    private void updateAnimationSpeed() {
        double speed = speedSlider.getValue();
        Duration duration = Duration.seconds(speed);
        animationTimeline.getKeyFrames().clear();
        animationTimeline.getKeyFrames().add(new KeyFrame(duration, e -> playNextStep()));
    }

    private void playNextStep() {
        if (!isPlaying.get()) return;
        if (currentStepIndex >= totalSteps) {
            // Finished
            stopAnimation();
            appendLog("✅ Solved! Total steps: " + totalSteps);
            return;
        }
        Step step = currentSteps.get(currentStepIndex);
        // Apply step to board
        applyStep(step);
        currentStepIndex++;
        // Update stats
        totalSteps++;
        updateStats();
        // Log step
        appendLog(step.toString());
        // Check completion
        if (currentStepIndex >= totalSteps) {
            stopAnimation();
            appendLog("🎉 Solving completed.");
        }
    }

    private void manualStep() {
        if (currentStepIndex >= totalSteps) {
            appendLog("No more steps. New game?");
            return;
        }
        Step step = currentSteps.get(currentStepIndex);
        applyStep(step);
        currentStepIndex++;
        updateStats();
        appendLog(step.toString());
        if (currentStepIndex >= totalSteps) {
            appendLog("🎉 Final step reached.");
        }
    }

    private void applyStep(Step step) {
        int row = step.getRow();
        int col = step.getCol();
        int value = step.getValue();
        CellState state = mapStepTypeToCellState(step.getType());
        sudokuBoard.updateCell(row, col, value, state);
        if (step.isBacktrack()) {
            backtracks++;
            updateStats();
        }
        // Highlight current cell
        sudokuBoard.highlightCell(row, col);
    }

    private CellState mapStepTypeToCellState(StepType type) {
        switch (type) {
            case TRY: return CellState.TRYING;
            case BACKTRACK: return CellState.BACKTRACK;
            case PLACE: return CellState.SOLVED;
            default: return CellState.EMPTY;
        }
    }

    // -------- Statistics UI Update --------
    private void resetStats() {
        totalSteps = 0;
        backtracks = 0;
        currentStepIndex = 0;
        updateStats();
    }

    private void updateStats() {
        Platform.runLater(() -> {
            stepsLabel.setText("Steps: " + totalSteps);
            backtracksLabel.setText("Backtracks: " + backtracks);
            int filled = countFilledCells();
            filledLabel.setText("Filled: " + filled + "/81");
            long elapsed = (startTime > 0 ? System.currentTimeMillis() - startTime : 0);
            timeLabel.setText(String.format("Time: %.2f s", elapsed / 1000.0));
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
        stepLogArea.clear();
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            stepLogArea.appendText("> " + msg + "\n");
            stepLogArea.setScrollTop(Double.MAX_VALUE);
        });
    }
}