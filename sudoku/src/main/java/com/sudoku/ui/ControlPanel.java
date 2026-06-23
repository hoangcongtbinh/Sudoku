package com.sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControlPanel extends VBox {

    private Button resetButton;
    private Button stepButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Slider speedSlider;
    private Label speedLabel;

    public ControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10, 15, 10, 15));
        setAlignment(Pos.CENTER);
        getStyleClass().add("control-panel");

        // Button row
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);

        resetButton = createButton("◄◄", "Reset to original puzzle");
        stepButton = createButton("►", "Step through one move");
        playButton = createButton("▶", "Play animation");
        pauseButton = createButton("⏸", "Pause animation");
        stopButton = createButton("●", "Stop and reset");

        buttonRow.getChildren().addAll(resetButton, stepButton, playButton, pauseButton, stopButton);

        // Speed control row
        HBox speedRow = new HBox(10);
        speedRow.setAlignment(Pos.CENTER);

        speedLabel = new Label("Speed:");
        speedLabel.getStyleClass().add("speed-label");

        speedSlider = new Slider(0.5, 3, 0.5);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setPrefWidth(200);
        speedSlider.getStyleClass().add("speed-slider");

        Label slowLabel = new Label("Slow");
        Label fastLabel = new Label("Fast");
        slowLabel.getStyleClass().add("speed-text");
        fastLabel.getStyleClass().add("speed-text");

        HBox sliderBox = new HBox(5, slowLabel, speedSlider, fastLabel);
        sliderBox.setAlignment(Pos.CENTER);

        speedRow.getChildren().addAll(speedLabel, sliderBox);

        getChildren().addAll(buttonRow, speedRow);
    }

    private Button createButton(String text, String tooltip) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().add("control-button");
        button.setPrefWidth(60);
        return button;
    }

    // Getters for event handlers in GameController
    public Button getResetButton() { return resetButton; }
    public Button getStepButton() { return stepButton; }
    public Button getPlayButton() { return playButton; }
    public Button getPauseButton() { return pauseButton; }
    public Button getStopButton() { return stopButton; }
    public Slider getSpeedSlider() { return speedSlider; }

    // Enable/disable methods
    public void setButtonsEnabled(boolean playing) {
        playButton.setDisable(playing);
        pauseButton.setDisable(!playing);
        stepButton.setDisable(playing);
        resetButton.setDisable(playing);
        stopButton.setDisable(!playing);
    }

    public void resetButtonState() {
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stepButton.setDisable(false);
        resetButton.setDisable(false);
        stopButton.setDisable(true);
    }
}