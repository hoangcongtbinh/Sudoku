package com.sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class StatsPanel extends VBox {

    private Label stepsLabel;
    private Label timeLabel;
    private Label backtracksLabel;
    private Label filledLabel;
    private ProgressBar filledProgressBar;

    public StatsPanel() {
        setSpacing(10);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("stats-panel");

        // Title
        Label title = new Label("STATISTICS");
        title.setFont(Font.font("Monospaced", 14));
        title.getStyleClass().add("stats-title");

        // Statistics labels
        stepsLabel = createStatLabel("Steps: 0");
        timeLabel = createStatLabel("Time: 0.00 s");
        backtracksLabel = createStatLabel("Backtracks: 0");
        filledLabel = createStatLabel("Filled: 0 / 81");

        // Progress bar for filled cells
        filledProgressBar = new ProgressBar(0);
        filledProgressBar.setPrefWidth(180);
        filledProgressBar.getStyleClass().add("filled-progress");

        getChildren().addAll(title, stepsLabel, timeLabel, backtracksLabel, filledLabel, filledProgressBar);
    }

    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospaced", 12));
        label.getStyleClass().add("stat-label");
        return label;
    }

    // Update methods called from GameController
    public void updateSteps(long steps) {
        stepsLabel.setText("Steps: " + steps);
    }

    public void updateTime(double seconds) {
        timeLabel.setText(String.format("Time: %.2f s", seconds));
    }

    public void updateBacktracks(int backtracks) {
        backtracksLabel.setText("Backtracks: " + backtracks);
    }

    public void updateFilled(int filled) {
        filledLabel.setText("Filled: " + filled + " / 81");
        filledProgressBar.setProgress(filled / 81.0);
    }

    public void reset() {
        updateSteps(0);
        updateTime(0);
        updateBacktracks(0);
        updateFilled(0);
    }
}