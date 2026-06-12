package com.sudoku.ui;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


import com.sudoku.solver.*;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    // -------- FXML Injected Components --------
    @FXML private SudokuBoard sudokuBoard;
    @FXML private AlgorithmSelector algorithmSelector;
    @FXML private DifficultySelector difficultySelector;
    @FXML private StatsPanel statsPanel;
    @FXML private StepLogPanel stepLogPanel;
    @FXML private ControlPanel controlPanel;
    @FXML private Button newGameButton;

    // Additional FXML components for Play Mode
    @FXML private VBox playPanel;
    @FXML private VBox solvePanel;
    @FXML private VBox comparePanel;
    @FXML private HBox playBottomBar;
    @FXML private Label timerLabel;
    @FXML private Label mistakesLabel;
    @FXML private Label hintsLabel;
    @FXML private Label filledLabel;
    @FXML private Label playStatusLabel;
    @FXML private Button hintButton;
    @FXML private Button undoButton;
    @FXML private Button checkButton;
    @FXML private Button solveButton;
    @FXML private Button resetButton;
    private Stack<PlayMove> moveHistory;

    // Compare mode components
    @FXML private CheckBox cbBacktracking;
    @FXML private CheckBox cbMRV;
    @FXML private CheckBox cbSA;
    @FXML private TableView<?> compareTable;
    @FXML private VBox compareBars;

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
    private int hintsUsed = 0;
    private int mistakes = 0;

    // -------- Cell Selection --------
    private int selectedRow = -1;
    private int selectedCol = -1;

    // -------- Initialization --------
    @FXML
    public void initialize() {
        // Animation timer
        isPlaying = new AtomicBoolean(false);
        animationTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> playNextStep()));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);

        moveHistory = new Stack<>();
        setupUI();
        setupCellSelection();
        newGame();
        attachEventHandlers();

        // Default to Play mode
        showPlayMode();
    }

    private void setupUI() {
        if (controlPanel != null) {
            controlPanel.getPauseButton().setDisable(true);
            controlPanel.getStopButton().setDisable(true);
        }
    }

    private void setupCellSelection() {
        if (sudokuBoard != null) {
            sudokuBoard.setOnCellClicked((row, col) -> {
                selectedRow = row;
                selectedCol = col;
                sudokuBoard.highlightCell(row, col);
                appendLog("Selected cell: (" + (row + 1) + ", " + (col + 1) + ")");
            });
        }
    }

    // Inner class để lưu thông tin một nước đi
    private static class PlayMove {
        int row, col, oldValue, newValue;
        PlayMove(int row, int col, int oldValue, int newValue) {
            this.row = row; this.col = col;
            this.oldValue = oldValue; this.newValue = newValue;
        }
    }

    private void attachEventHandlers() {
        if (newGameButton != null) {
            newGameButton.setOnAction(e -> onNewGame());
        }

        if (controlPanel != null) {
            controlPanel.getStepButton().setOnAction(e -> manualStep());
            controlPanel.getPlayButton().setOnAction(e -> startAnimation());
            controlPanel.getPauseButton().setOnAction(e -> stopAnimation());
            controlPanel.getResetButton().setOnAction(e -> resetToOriginal());
            controlPanel.getStopButton().setOnAction(e -> resetToOriginal());
            controlPanel.getSpeedSlider().valueProperty().addListener((obs, old, val) -> updateAnimationSpeed());
        }

        if (algorithmSelector != null) {
            algorithmSelector.valueProperty().addListener((obs, old, val) -> {
                if (currentBoard != null) refreshSolver();
            });
        }

        if (difficultySelector != null) {
            difficultySelector.valueProperty().addListener((obs, old, val) -> onNewGame());
        }
    }

    // ==================== MODE SWITCHING METHODS ====================

    @FXML
    private void onModePlay() {
        showPlayMode();
    }

    @FXML
    private void onModeSolve() {
        showSolveMode();
    }

    @FXML
    private void onModeCompare() {
        showCompareMode();
    }

    private void showPlayMode() {
        if (playPanel != null) {
            playPanel.setVisible(true);
            playPanel.setManaged(true);
        }
        if (solvePanel != null) {
            solvePanel.setVisible(false);
            solvePanel.setManaged(false);
        }
        if (comparePanel != null) {
            comparePanel.setVisible(false);
            comparePanel.setManaged(false);
        }
        if (playBottomBar != null) {
            playBottomBar.setVisible(true);
            playBottomBar.setManaged(true);
        }
        if (controlPanel != null) {
            controlPanel.setVisible(false);
            controlPanel.setManaged(false);
        }
        appendLog("Switched to PLAY mode");
    }

    private void showSolveMode() {
        if (playPanel != null) {
            playPanel.setVisible(false);
            playPanel.setManaged(false);
        }
        if (solvePanel != null) {
            solvePanel.setVisible(true);
            solvePanel.setManaged(true);
        }
        if (comparePanel != null) {
            comparePanel.setVisible(false);
            comparePanel.setManaged(false);
        }
        if (playBottomBar != null) {
            playBottomBar.setVisible(false);
            playBottomBar.setManaged(false);
        }
        if (controlPanel != null) {
            controlPanel.setVisible(true);
            controlPanel.setManaged(true);
        }
        appendLog("Switched to SOLVE mode");
    }

    private void showCompareMode() {
        if (playPanel != null) {
            playPanel.setVisible(false);
            playPanel.setManaged(false);
        }
        if (solvePanel != null) {
            solvePanel.setVisible(false);
            solvePanel.setManaged(false);
        }
        if (comparePanel != null) {
            comparePanel.setVisible(true);
            comparePanel.setManaged(true);
        }
        if (playBottomBar != null) {
            playBottomBar.setVisible(false);
            playBottomBar.setManaged(false);
        }
        if (controlPanel != null) {
            controlPanel.setVisible(false);
            controlPanel.setManaged(false);
        }
        appendLog("Switched to COMPARE mode");
    }

    // ==================== GAME LOGIC ====================

    @FXML
    private void onNewGame() {
        stopAnimation();
        Difficulty diff = difficultySelector.getSelectedDifficulty();
        currentBoard = BoardGenerator.generatestaticBoard(diff);
        sudokuBoard.setBoard(currentBoard.getGrid());
        refreshSolver();
        resetStats();
        clearLog();
        mistakes = 0;
        hintsUsed = 0;
        updatePlayStats();
        appendLog("New game started - Difficulty: " + diff);
    }

    // Keep original newGame method for compatibility
    private void newGame() {
        onNewGame();
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

        SolveResult result = currentSolver.solve(currentBoard.copy());
        currentSteps = result.getSteps();
        currentStepIndex = 0;
        totalSteps = currentSteps.size();

        backtracks = 0;
        for (Step s : currentSteps) {
            if (s.isBacktrack()) backtracks++;
        }

        updateStats();
        appendLog("Algorithm ready: " + algo + " | Total steps: " + totalSteps);
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

    // ==================== PLAY MODE METHODS ====================

    @FXML
    private void onNum1() { setValueOnSelectedCell(1); }
    @FXML
    private void onNum2() { setValueOnSelectedCell(2); }
    @FXML
    private void onNum3() { setValueOnSelectedCell(3); }
    @FXML
    private void onNum4() { setValueOnSelectedCell(4); }
    @FXML
    private void onNum5() { setValueOnSelectedCell(5); }
    @FXML
    private void onNum6() { setValueOnSelectedCell(6); }
    @FXML
    private void onNum7() { setValueOnSelectedCell(7); }
    @FXML
    private void onNum8() { setValueOnSelectedCell(8); }
    @FXML
    private void onNum9() { setValueOnSelectedCell(9); }

    @FXML
    private void onErase() {
        if (selectedRow >= 0 && selectedCol >= 0 && isEditableCell(selectedRow, selectedCol)) {
            sudokuBoard.updateCell(selectedRow, selectedCol, 0, CellState.EMPTY);
            updatePlayStats();
            appendLog("Erased cell (" + (selectedRow + 1) + ", " + (selectedCol + 1) + ")");
        } else {
            appendLog("Please select an editable cell first");
        }
    }

    @FXML
    private void onHint() {
        if (selectedRow >= 0 && selectedCol >= 0 && isEditableCell(selectedRow, selectedCol)) {
            hintsUsed++;
            updatePlayStats();
            appendLog("Hint requested for cell (" + (selectedRow + 1) + ", " + (selectedCol + 1) + ")");
        } else {
            appendLog("Please select an empty cell first");
        }
    }

    @FXML
    private void onUndo() {
        if (moveHistory.isEmpty()) {
            appendLog("Nothing to undo");
            return;
        }
        PlayMove lastMove = moveHistory.pop();
        // Khôi phục giá trị cũ
        sudokuBoard.updateCell(lastMove.row, lastMove.col, lastMove.oldValue, CellState.EMPTY);
        updatePlayStats();
        appendLog("Undo: restored (" + (lastMove.row+1) + "," + (lastMove.col+1) + ") to " + lastMove.oldValue);
        // Cập nhật lại selected cell (tuỳ chọn)
        selectedRow = lastMove.row;
        selectedCol = lastMove.col;
        sudokuBoard.highlightCell(selectedRow, selectedCol);
    }

    @FXML
    private void onCheck() {
        boolean isComplete = checkBoardComplete();
        if (isComplete) {
            if (playStatusLabel != null) {
                playStatusLabel.setText("✓ SOLVED!");
                playStatusLabel.setStyle("-fx-text-fill: #00ff44;");
            }
            appendLog("✓ Puzzle solved correctly!");
        } else {
            if (playStatusLabel != null) {
                playStatusLabel.setText("✗ INCOMPLETE");
                playStatusLabel.setStyle("-fx-text-fill: #ff3300;");
            }
            appendLog("✗ Board not yet complete.");
        }
    }

    private void setValueOnSelectedCell(int value) {
        if (selectedRow >= 0 && selectedCol >= 0 && isEditableCell(selectedRow, selectedCol)) {
            int oldValue = sudokuBoard.getValueAt(selectedRow, selectedCol);
            // Lưu vào history trước khi thay đổi
            if (oldValue != value) {
                moveHistory.push(new PlayMove(selectedRow, selectedCol, oldValue, value));
            }
            sudokuBoard.updateCell(selectedRow, selectedCol, value, CellState.SOLVED);
            updatePlayStats();
            appendLog("Set cell (" + (selectedRow + 1) + ", " + (selectedCol + 1) + ") to " + value);

            // Kiểm tra nếu sai (dựa vào solution có sẵn? Hoặc đơn giản là tăng mistakes)
            // Nếu muốn kiểm tra lỗi, cần có solutionBoard. Ở đây tạm thời bỏ qua.
        } else {
            appendLog("Please select an empty cell first");
        }
    }

    private boolean isEditableCell(int row, int col) {
        if (currentBoard == null) return true;
        int[][] original = currentBoard.getOriginalGrid();
        return original[row][col] == 0;
    }

    private void updatePlayStats() {
        int filled = countFilledCells();
        if (filledLabel != null) filledLabel.setText(filled + " / 81");
        if (hintsLabel != null) hintsLabel.setText(String.valueOf(hintsUsed));
        if (mistakesLabel != null) mistakesLabel.setText(mistakes + " / 3");
    }

    private boolean checkBoardComplete() {
        int[][] grid = sudokuBoard.getCurrentBoard();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) return false;
            }
        }
        return true;
    }

    // ==================== SOLVE MODE METHODS ====================

    @FXML
    private void onSolve() {
        if (currentSteps == null || currentSteps.isEmpty()) {
            refreshSolver();
        }
        startAnimation();
    }

    @FXML
    private void onReset() {
        resetToOriginal();
    }

    // ==================== COMPARE MODE METHODS ====================

    @FXML
    private void onRunComparison() {
        appendLog("Running algorithm comparison...");
        appendLog("Backtracking: " + runSolverAndGetStats(new BacktrackingSolver(), currentBoard.copy()));
        appendLog("MRV: " + runSolverAndGetStats(new MRVSolver(), currentBoard.copy()));
        appendLog("Simulated Annealing: " + runSolverAndGetStats(new SASolver(), currentBoard.copy()));
        appendLog("Comparison complete.");
    }

    private String runSolverAndGetStats(Solver solver, Board board) {
        long start = System.nanoTime();
        SolveResult result = solver.solve(board);
        long end = System.nanoTime();
        long timeMicros = (end - start) / 1000;
        return String.format("Steps: %d, Backtracks: %d, Time: %d μs",
                result.getSteps().size(), result.getBacktrackCount(), timeMicros);
    }

    // ==================== ANIMATION CONTROL ====================

    private void startAnimation() {
        if (currentSteps == null || currentStepIndex >= currentSteps.size()) {
            appendLog("Solving already complete. Start a new game.");
            return;
        }
        isPlaying.set(true);
        if (controlPanel != null) controlPanel.setButtonsEnabled(true);
        if (startTime == 0) startTime = System.currentTimeMillis();
        updateAnimationSpeed();
        animationTimeline.play();
        appendLog("▶️ Animation started");
    }

    private void stopAnimation() {
        if (animationTimeline != null) animationTimeline.stop();
        isPlaying.set(false);
        if (controlPanel != null) controlPanel.setButtonsEnabled(false);
        appendLog("⏸️ Animation paused");
    }

    private void updateAnimationSpeed() {
        if (controlPanel == null) return;
        double speed = controlPanel.getSpeedSlider().getValue();
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
        if (startTime == 0) startTime = System.currentTimeMillis();
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
        if (row == -1 || col == -1) return;
        int value = step.getValue();
        CellState state = mapStepTypeToCellState(step.getType());
        sudokuBoard.updateCell(row, col, value, state);
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

    private String formatStep(Step step) {
        if (step.getRow() == -1) return "Cost/Conflicts: " + step.getValue();
        return String.format("%s at (%d, %d) to %d",
                step.getType().toString(),
                step.getRow() + 1,
                step.getCol() + 1,
                step.getValue());
    }

    // ==================== STATISTICS ====================

    private void resetStats() {
        currentStepIndex = 0;
        startTime = 0;
        if (statsPanel != null) statsPanel.reset();
    }

    private void updateStats() {
        Platform.runLater(() -> {
            if (statsPanel == null) return;
            statsPanel.updateSteps(currentStepIndex);
            int backtracksPlayed = 0;
            if (currentSteps != null) {
                for (int i = 0; i < currentStepIndex && i < currentSteps.size(); i++) {
                    if (currentSteps.get(i).isBacktrack()) backtracksPlayed++;
                }
            }
            statsPanel.updateBacktracks(backtracksPlayed);
            statsPanel.updateFilled(countFilledCells());
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

    // ==================== LOGGING ====================

    private void clearLog() {
        if (stepLogPanel != null) stepLogPanel.clear();
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            if (stepLogPanel != null) stepLogPanel.addLog(msg);
        });
    }
}