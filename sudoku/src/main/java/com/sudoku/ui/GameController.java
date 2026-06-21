package com.sudoku.ui;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.highscore.HighScoreManager;
import com.sudoku.io.PuzzleIOService;
import com.sudoku.model.*;
import com.sudoku.validator.BoardValidator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import com.sudoku.solver.*;
import com.sudoku.benchmark.AlgorithmComparison;
import com.sudoku.benchmark.AlgorithmStats;

import java.io.File;
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

    // Play Mode
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
    @FXML private Button importButton;
    @FXML private Button exportButton;
    @FXML private Button highScoreButton;
    private Stack<PlayMove> moveHistory;

    // Compare mode components
    @FXML private CheckBox cbBacktracking;
    @FXML private CheckBox cbMRV;
    @FXML private CheckBox cbSA;
    @FXML private TableView<AlgorithmStats> compareTable;
    @FXML private VBox compareBars;
    @FXML private Button runCompareButton;

    // Mode tabs
    @FXML private Button tabPlay;
    @FXML private Button tabSolve;
    @FXML private Button tabCompare;

    // -------- Backend State --------
    private Board currentBoard;
    private Board solutionBoard;  // Lưu solution để kiểm tra đúng/sai
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
    private static final int MAX_MISTAKES = 3;
    private boolean gameOver = false;  // FIX: Theo dõi trạng thái game over

    // Play mode timer
    private Timeline playTimer;
    private long playStartTime;
    private long playElapsedSeconds;

    // High score
    private HighScoreManager highScoreManager;

    // -------- Cell Selection --------
    private int selectedRow = -1;
    private int selectedCol = -1;

    // -------- Initialization --------
    @FXML
    public void initialize() {
        isPlaying = new AtomicBoolean(false);
        animationTimeline = new Timeline();
        animationTimeline.setCycleCount(Timeline.INDEFINITE);

        moveHistory = new Stack<>();
        highScoreManager = new HighScoreManager();

        setupUI();
        setupCellSelection();
        setupKeyboardInput();
        setupCompareTable();  // FIX: Khởi tạo bảng comparison
        newGame();
        attachEventHandlers();

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

    /**
     * Bắt phím số từ bàn phím
     */
    private void setupKeyboardInput() {
        if (sudokuBoard != null) {
            sudokuBoard.setOnKeyPressed(event -> {
                String text = event.getText();
                if (text != null && text.matches("[1-9]")) {
                    int num = Integer.parseInt(text);
                    setValueOnSelectedCell(num);
                } else if (event.getCode().toString().equals("BACK_SPACE") ||
                        event.getCode().toString().equals("DELETE")) {
                    onErase();
                } else if (event.getCode().toString().equals("Z") && event.isControlDown()) {
                    onUndo();
                }
            });
            sudokuBoard.setFocusTraversable(true);
        }
    }

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

            // FIX: Speed slider - recreate timeline hoàn toàn với speed mới
            controlPanel.getSpeedSlider().valueProperty().addListener((obs, old, val) -> {
                updateAnimationSpeed(val.doubleValue());
            });
        }

        if (algorithmSelector != null) {
            algorithmSelector.valueProperty().addListener((obs, old, val) -> {
                if (currentBoard != null) refreshSolver();
            });
        }

        if (difficultySelector != null) {
            difficultySelector.valueProperty().addListener((obs, old, val) -> onNewGame());
        }

        // Import/Export buttons
        if (importButton != null) {
            importButton.setOnAction(e -> onImport());
        }
        if (exportButton != null) {
            exportButton.setOnAction(e -> onExport());
        }

        // High score button
        if (highScoreButton != null) {
            highScoreButton.setOnAction(e -> showHighScores());
        }

        // Compare button
        if (runCompareButton != null) {
            runCompareButton.setOnAction(e -> onRunComparison());
        }

        // Tab buttons
        if (tabPlay != null) {
            tabPlay.setOnAction(e -> { onModePlay(); updateTabStyles(tabPlay); });
        }
        if (tabSolve != null) {
            tabSolve.setOnAction(e -> { onModeSolve(); updateTabStyles(tabSolve); });
        }
        if (tabCompare != null) {
            tabCompare.setOnAction(e -> { onModeCompare(); updateTabStyles(tabCompare); });
        }
    }

    // ==================== MODE SWITCHING ====================

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

    /**
     * FIX: Tab focus - đảm bảo chỉ 1 tab active, style rõ ràng
     */
    private void updateTabStyles(Button activeTab) {
        String activeClass = "mode-tab-active";
        String inactiveClass = "mode-tab-inactive";

        Button[] tabs = {tabPlay, tabSolve, tabCompare};
        for (Button tab : tabs) {
            if (tab == null) continue;
            tab.getStyleClass().removeAll(activeClass, inactiveClass);
            if (tab == activeTab) {
                tab.getStyleClass().add(activeClass);
            } else {
                tab.getStyleClass().add(inactiveClass);
            }
        }
    }

    private void showPlayMode() {
        stopAnimation();
        stopPlayTimer();

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
        updateTabStyles(tabPlay);
        appendLog("Switched to PLAY mode");
    }

    private void showSolveMode() {
        stopPlayTimer();

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
        updateTabStyles(tabSolve);
        appendLog("Switched to SOLVE mode");
    }

    private void showCompareMode() {
        stopAnimation();
        stopPlayTimer();

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
        updateTabStyles(tabCompare);
        appendLog("Switched to COMPARE mode");
    }

    // ==================== GAME LOGIC ====================

    @FXML
    private void onNewGame() {
        stopAnimation();
        stopPlayTimer();
        gameOver = false;
        mistakes = 0;
        hintsUsed = 0;
        if (sudokuBoard != null) {
            sudokuBoard.setDisable(false);
        }
        if (playStatusLabel != null) {
            playStatusLabel.setText("READY");
            playStatusLabel.setStyle("");
        }

        Difficulty diff = difficultySelector.getSelectedDifficulty();
        currentBoard = BoardGenerator.generatestaticBoard(diff);
        sudokuBoard.setBoard(currentBoard.getGrid());

        // Tạo solution board để kiểm tra
        solutionBoard = currentBoard.copy();
        Solver solver = new BacktrackingSolver();
        SolveResult result = solver.solve(solutionBoard);
        if (result.isSolved()) {
            solutionBoard = result.getFinalBoard() != null ? result.getFinalBoard() : solutionBoard;
        }

        refreshSolver();
        resetStats();
        clearLog();
        mistakes = 0;
        hintsUsed = 0;
        moveHistory.clear();
        updatePlayStats();
        startPlayTimer();
        appendLog("New game started - Difficulty: " + diff);
    }

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
        // FIX: Re-enable board if disabled (allow erase after game over to restart)
        if (sudokuBoard.isDisabled()) {
            sudokuBoard.setDisable(false);
            gameOver = false;
            mistakes = 0;
            updatePlayStats();
            appendLog("Board re-enabled. Mistakes reset.");
        }

        if (selectedRow >= 0 && selectedCol >= 0 && isEditableCell(selectedRow, selectedCol)) {
            int oldValue = sudokuBoard.getValueAt(selectedRow, selectedCol);
            if (oldValue != 0) {
                moveHistory.push(new PlayMove(selectedRow, selectedCol, oldValue, 0));
            }
            sudokuBoard.updateCell(selectedRow, selectedCol, 0, CellState.EMPTY);
            updatePlayStats();
            appendLog("Erased cell (" + (selectedRow + 1) + ", " + (selectedCol + 1) + ")");
        } else {
            appendLog("Please select an editable cell first");
        }
    }

    /**
     * FIX: Hint - tìm ô trống đầu tiên nếu chưa chọn, điền đáp án đúng
     */
    @FXML
    private void onHint() {
        // FIX: Re-enable board if disabled (allow hint after game over)
        if (sudokuBoard.isDisabled()) {
            sudokuBoard.setDisable(false);
            gameOver = false;
            appendLog("Board re-enabled via hint");
        }

        if (gameOver) {
            appendLog("Game is over. Start a new game.");
            return;
        }

        int row = selectedRow;
        int col = selectedCol;

        // Nếu không có ô được chọn hoặc ô không thể sửa, tìm ô trống đầu tiên
        if (row == -1 || col == -1 || !isEditableCell(row, col)) {
            int[][] grid = sudokuBoard.getCurrentBoard();
            boolean found = false;
            for (int r = 0; r < 9 && !found; r++) {
                for (int c = 0; c < 9 && !found; c++) {
                    if (grid[r][c] == 0 && isEditableCell(r, c)) {
                        row = r; col = c;
                        found = true;
                    }
                }
            }
            if (!found) {
                appendLog("No empty cells to hint");
                return;
            }
        }

        // FIX: Kiểm tra solutionBoard tồn tại TRƯỚC khi dùng
        if (solutionBoard == null) {
            appendLog("❌ No solution available for hint");
            return;
        }

        int hintValue = solutionBoard.getCell(row, col);
        if (hintValue <= 0) {
            appendLog("❌ Invalid hint value");
            return;
        }

        // Lưu move history
        int oldValue = sudokuBoard.getValueAt(row, col);
        moveHistory.push(new PlayMove(row, col, oldValue, hintValue));

        // Điền hint với state HINT (màu cam)
        sudokuBoard.updateCell(row, col, hintValue, CellState.HINT);
        sudokuBoard.validateBoard();

        hintsUsed++;
        updatePlayStats();
        appendLog("💡 Hint: (" + (row+1) + "," + (col+1) + ") → " + hintValue);

        // Sau 2 giây chuyển thành user-input (màu xanh)
        final int hintRow = row;
        final int hintCol = col;
        final int finalValue = hintValue;
        Timeline hintTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (sudokuBoard.getValueAt(hintRow, hintCol) == finalValue) {
                sudokuBoard.updateCell(hintRow, hintCol, finalValue, CellState.USER_INPUT);
            }
        }));
        hintTimeline.play();

        checkWinCondition();
    }

    @FXML
    private void onUndo() {
        if (moveHistory.isEmpty()) {
            appendLog("Nothing to undo");
            return;
        }

        // FIX: Re-enable board if it was disabled (e.g., after game over)
        if (sudokuBoard.isDisabled()) {
            sudokuBoard.setDisable(false);
            gameOver = false;
            appendLog("Board re-enabled via undo");
        }

        PlayMove lastMove = moveHistory.pop();
        sudokuBoard.updateCell(lastMove.row, lastMove.col, lastMove.oldValue,
                lastMove.oldValue == 0 ? CellState.EMPTY : CellState.USER_INPUT);
        sudokuBoard.validateBoard();
        updatePlayStats();
        appendLog("Undo: restored (" + (lastMove.row+1) + "," + (lastMove.col+1) + ") to " + lastMove.oldValue);

        // FIX: Restore selection and ensure cell is editable
        selectedRow = lastMove.row;
        selectedCol = lastMove.col;
        sudokuBoard.highlightCell(selectedRow, selectedCol);

        // FIX: Request focus on the board to ensure keyboard input works
        sudokuBoard.requestFocus();
    }

    @FXML
    private void onCheck() {
        boolean isComplete = checkBoardComplete();
        boolean noErrors = !sudokuBoard.validateBoard();

        if (isComplete && noErrors) {
            handleWin();  // FIX: Tách riêng handleWin
        } else {
            if (playStatusLabel != null) {
                playStatusLabel.setText("✗ INCOMPLETE OR ERRORS");
                playStatusLabel.setStyle("-fx-text-fill: #ff3300;");
            }
            appendLog("✗ Board has errors or is incomplete.");
        }
    }

    /**
     * FIX: Tách riêng logic win để tái sử dụng
     */
    private void handleWin() {
        stopPlayTimer();
        gameOver = true;  // FIX: Đánh dấu game over

        if (playStatusLabel != null) {
            playStatusLabel.setText("✓ SOLVED!");
            playStatusLabel.setStyle("-fx-text-fill: #00ff44; -fx-font-weight: bold;");
        }

        // Lưu high score
        GameSession session = new GameSession(
                difficultySelector.getSelectedDifficulty(),
                playElapsedSeconds * 1000,
                mistakes,
                hintsUsed
        );
        session.setFinished(true);
        boolean isNewRecord = highScoreManager.updateHighScore(session);

        String msg = "✓ Puzzle solved correctly!";
        if (isNewRecord) {
            msg += " 🏆 NEW RECORD!";
        }
        msg += " Time: " + formatTime(playElapsedSeconds);
        appendLog(msg);

        showHighScores();
    }

    /**
     * FIX: Kiểm tra lỗi + đổi màu ngay khi nhập
     * Chỉ tăng mistakes nếu nhập SAI (so với solution)
     */
    private void setValueOnSelectedCell(int value) {
        // FIX: Kiểm tra game over
        if (gameOver || sudokuBoard.isDisabled()) {
            appendLog("Game is over. Start a new game.");
            return;
        }

        if (selectedRow < 0 || selectedCol < 0 || !isEditableCell(selectedRow, selectedCol)) {
            appendLog("Please select an empty cell first");
            return;
        }

        int oldValue = sudokuBoard.getValueAt(selectedRow, selectedCol);

        // Kiểm tra đúng/sai so với solution
        boolean isCorrect = true;
        if (solutionBoard != null) {
            isCorrect = solutionBoard.getCell(selectedRow, selectedCol) == value;
        }

        if (oldValue != value) {
            moveHistory.push(new PlayMove(selectedRow, selectedCol, oldValue, value));
        }

        sudokuBoard.updateCell(selectedRow, selectedCol, value, CellState.USER_INPUT);

        // Validate ngay lập tức - đổi màu các ô trùng
        boolean hasConflict = sudokuBoard.validateBoard();

        // Chỉ tăng mistakes nếu nhập SAI (không phải chỉ trùng conflict)
        if (!isCorrect && oldValue != value) {
            mistakes++;
            appendLog("❌ Wrong number at (" + (selectedRow+1) + "," + (selectedCol+1) + ")");

            if (mistakes >= MAX_MISTAKES) {
                gameOver = true;  // FIX: Đánh dấu game over
                appendLog("💀 Game Over! Too many mistakes (" + MAX_MISTAKES + "/" + MAX_MISTAKES + ")");
                stopPlayTimer();
                if (playStatusLabel != null) {
                    playStatusLabel.setText("GAME OVER");
                    playStatusLabel.setStyle("-fx-text-fill: #ff3355; -fx-font-weight: bold;");
                }
                sudokuBoard.setDisable(true);  // FIX: Vô hiệu hóa board
            }
        } else {
            appendLog("✓ (" + (selectedRow+1) + "," + (selectedCol+1) + ") = " + value);
        }

        updatePlayStats();
        checkWinCondition();
    }

    /**
     * FIX: Kiểm tra win condition - chỉ gọi validateBoard 1 lần
     * Tránh gọi validateBoard nhiều lần gây freeze
     */
    private void checkWinCondition() {
        if (!checkBoardComplete()) return;

        // Chỉ kiểm tra lỗi 1 lần, không cần validate lại
        boolean hasErrors = sudokuBoard.validateBoard();
        if (!hasErrors) {
            handleWin();
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
        if (mistakesLabel != null) mistakesLabel.setText(mistakes + " / " + MAX_MISTAKES);
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

    // ==================== TIMER ====================

    private void startPlayTimer() {
        playStartTime = System.currentTimeMillis();
        playElapsedSeconds = 0;
        playTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            playElapsedSeconds = (System.currentTimeMillis() - playStartTime) / 1000;
            if (timerLabel != null) {
                timerLabel.setText(formatTime(playElapsedSeconds));
            }
        }));
        playTimer.setCycleCount(Timeline.INDEFINITE);
        playTimer.play();
    }

    private void stopPlayTimer() {
        if (playTimer != null) {
            playTimer.stop();
        }
    }

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ==================== IMPORT/EXPORT ====================

    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Sudoku Puzzle");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Sudoku Files", "*.sudoku", "*.txt"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(sudokuBoard.getScene().getWindow());
        if (file == null) return;

        try {
            int[][] grid = PuzzleIOService.readFromFile(file);
            currentBoard = new Board(grid);
            sudokuBoard.setBoard(grid);

            // Tạo lại solution
            solutionBoard = currentBoard.copy();
            Solver solver = new BacktrackingSolver();
            SolveResult result = solver.solve(solutionBoard);
            if (result.isSolved()) {
                solutionBoard = result.getFinalBoard() != null ? result.getFinalBoard() : solutionBoard;
            }

            refreshSolver();
            resetStats();
            mistakes = 0;
            hintsUsed = 0;
            moveHistory.clear();
            gameOver = false;  // FIX: Reset game over
            sudokuBoard.setDisable(false);  // FIX: Re-enable board
            updatePlayStats();
            startPlayTimer();
            appendLog("Puzzle imported from: " + file.getName());
        } catch (Exception ex) {
            appendLog("❌ Failed to import: " + ex.getMessage());
            showAlert("Import Error", "Failed to import puzzle: " + ex.getMessage());
        }
    }

    @FXML
    private void onExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Sudoku Puzzle");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Sudoku Files", "*.sudoku")
        );
        fileChooser.setInitialFileName("puzzle.sudoku");

        File file = fileChooser.showSaveDialog(sudokuBoard.getScene().getWindow());
        if (file == null) return;

        try {
            PuzzleIOService.writeToFile(file, sudokuBoard.getCurrentBoard());
            appendLog("Puzzle exported to: " + file.getName());
        } catch (Exception ex) {
            appendLog("❌ Failed to export: " + ex.getMessage());
            showAlert("Export Error", "Failed to export puzzle: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== HIGH SCORES ====================

    @FXML
    private void showHighScores() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 HIGH SCORES - Best Times 🏆\n\n");
        for (Difficulty diff : Difficulty.values()) {
            sb.append(diff.name()).append(": ")
                    .append(highScoreManager.getHighScoreDisplay(diff))
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("High Scores");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.showAndWait();
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

    /**
     * FIX: Setup TableView columns cho comparison
     */
    private void setupCompareTable() {
        if (compareTable == null) return;

        compareTable.getColumns().clear();
        compareTable.setPlaceholder(new Label("Run comparison to see results"));

        TableColumn<AlgorithmStats, String> nameCol = new TableColumn<>("Algorithm");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        nameCol.setPrefWidth(140);

        TableColumn<AlgorithmStats, Number> timeCol = new TableColumn<>("Time (ms)");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeMs"));
        timeCol.setPrefWidth(90);
        timeCol.setCellFactory(col -> new TableCell<AlgorithmStats, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item.doubleValue()));
                }
            }
        });

        TableColumn<AlgorithmStats, Number> stepsCol = new TableColumn<>("Steps");
        stepsCol.setCellValueFactory(new PropertyValueFactory<>("totalSteps"));
        stepsCol.setPrefWidth(70);

        TableColumn<AlgorithmStats, Number> backCol = new TableColumn<>("Backtracks");
        backCol.setCellValueFactory(new PropertyValueFactory<>("backtracks"));
        backCol.setPrefWidth(80);

        TableColumn<AlgorithmStats, Boolean> bestCol = new TableColumn<>("Best?");
        bestCol.setCellValueFactory(new PropertyValueFactory<>("best"));
        bestCol.setPrefWidth(60);
        bestCol.setCellFactory(col -> new TableCell<AlgorithmStats, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || !item) {
                    setText("");
                } else {
                    setText("★");
                    setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-alignment: center;");
                }
            }
        });

        compareTable.getColumns().addAll(nameCol, timeCol, stepsCol, backCol, bestCol);
    }

    @FXML
    private void onRunComparison() {
        appendLog("Running algorithm comparison...");

        boolean runBacktracking = cbBacktracking != null && cbBacktracking.isSelected();
        boolean runMRV = cbMRV != null && cbMRV.isSelected();
        boolean runSA = cbSA != null && cbSA.isSelected();

        if (!runBacktracking && !runMRV && !runSA) {
            appendLog("Please select at least one algorithm to compare.");
            return;
        }

        AlgorithmComparison comparison = new AlgorithmComparison();
        if (runBacktracking) {
            comparison.addAlgorithm("Backtracking", new BacktrackingSolver());
        }
        if (runMRV) {
            comparison.addAlgorithm("MRV", new MRVSolver());
        }
        if (runSA) {
            comparison.addAlgorithm("Simulated Annealing", new SASolver());
        }

        List<AlgorithmStats> results = comparison.runComparison(currentBoard.copy());

        // FIX: Hiển thị kết quả lên table
        if (compareTable != null) {
            compareTable.getItems().clear();
            compareTable.getItems().addAll(results);
        }

        // FIX: Hiển thị progress bars
        if (compareBars != null) {
            compareBars.getChildren().clear();
            double maxTime = results.stream()
                    .mapToDouble(AlgorithmStats::getTimeMs)
                    .max().orElse(1.0);

            for (AlgorithmStats stat : results) {
                HBox barRow = new HBox(10);
                barRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                barRow.getStyleClass().add("compare-bar-row");

                Label nameLabel = new Label(stat.getAlgorithmName());
                nameLabel.getStyleClass().add("compare-bar-label");
                nameLabel.setMinWidth(140);
                nameLabel.setMaxWidth(140);

                double ratio = maxTime > 0 ? stat.getTimeMs() / maxTime : 0;
                ProgressBar bar = new ProgressBar(ratio);
                bar.setPrefWidth(250);
                bar.setPrefHeight(20);
                bar.getStyleClass().add("compare-bar");
                if (stat.isBest()) {
                    bar.getStyleClass().add("compare-bar-best");
                }

                Label timeLabel = new Label(String.format("%.2f ms", stat.getTimeMs()));
                timeLabel.getStyleClass().add("stats-value");
                timeLabel.setMinWidth(80);

                barRow.getChildren().addAll(nameLabel, bar, timeLabel);
                compareBars.getChildren().add(barRow);
            }
        }

        for (AlgorithmStats stat : results) {
            appendLog(stat.getAlgorithmName() + ": " + stat.toString());
        }
        appendLog("Comparison complete.");
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

        double speed = controlPanel != null ? controlPanel.getSpeedSlider().getValue() : 0.5;
        updateAnimationSpeed(speed);

        animationTimeline.play();
        appendLog("▶️ Animation started");
    }

    private void stopAnimation() {
        if (animationTimeline != null) animationTimeline.stop();
        isPlaying.set(false);
        if (controlPanel != null) controlPanel.setButtonsEnabled(false);
        appendLog("⏸️ Animation paused");
    }

    /**
     * FIX: Speed slider - recreate timeline hoàn toàn với duration mới
     * Không dùng clear() vì có thể gây lỗi khi timeline đang chạy
     */
    private void updateAnimationSpeed(double speed) {
        // speed: 0.01 (chậm nhất) -> 2.0 (nhanh nhất)
        // Chuyển thành duration: 2.0s -> 0.01s
        Duration duration = Duration.seconds(Math.max(0.01, 2.1 - speed));

        boolean wasPlaying = isPlaying.get();

        if (animationTimeline != null) {
            animationTimeline.stop();
        }

        // Tạo timeline mới hoàn toàn
        animationTimeline = new Timeline();
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.getKeyFrames().add(
                new KeyFrame(duration, e -> playNextStep())
        );

        if (wasPlaying) {
            isPlaying.set(true);
            animationTimeline.play();
            if (controlPanel != null) controlPanel.setButtonsEnabled(true);
        }

        appendLog("Speed: " + String.format("%.2f", speed) + " → " +
                String.format("%.2f", duration.toSeconds()) + "s/step");
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
            case SOLVER_STEP: return CellState.SOLVED;
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