package com.sudoku.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class StepLogPanel extends BorderPane {
    private TextArea logArea;

    public StepLogPanel() {
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(10);
        logArea.setPrefColumnCount(30);

        // Styling matching the original cyber theme:
        // Background: #050508, Text: #88ffaa, Font: Monospaced 12px
        logArea.setStyle(
            "-fx-control-inner-background: #050508; " +
            "-fx-text-fill: #88ffaa; " +
            "-fx-font-family: 'Monospaced'; " +
            "-fx-font-size: 12px; " +
            "-fx-highlight-fill: #00ffcc; " +
            "-fx-highlight-text-fill: #050508;"
        );

        setCenter(logArea);
    }

    public void addLog(String message) {
        logArea.appendText("> " + message + "\n");
        // Scroll to bottom
        logArea.selectPositionCaret(logArea.getLength());
        logArea.deselect();
    }

    public void clear() {
        logArea.clear();
    }
}