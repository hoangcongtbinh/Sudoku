package com.sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class    SudokuCell extends StackPane {
    private int row, col;
    private int value;
    private CellState state;
    private Label numberLabel;

    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = 0;
        this.state = CellState.EMPTY;

        numberLabel = new Label("");
        numberLabel.setFont(Font.font("Monospaced", 20));
        getChildren().add(numberLabel);
        setAlignment(Pos.CENTER);
        setPrefSize(60, 60);
        getStyleClass().add("sudoku-cell");

        updateStyle();
    }

    private void updateStyle() {
        getStyleClass().removeAll("given", "empty", "current", "trying", "backtrack", "solved");
        switch (state) {
            case GIVEN:    getStyleClass().add("given"); break;
            case EMPTY:    getStyleClass().add("empty"); break;
            case CURRENT:  getStyleClass().add("current"); break;
            case TRYING:   getStyleClass().add("trying"); break;
            case BACKTRACK:getStyleClass().add("backtrack"); break;
            case SOLVED:   getStyleClass().add("solved"); break;
        }
    }

    public void setValue(int value) {
        this.value = value;
        numberLabel.setText(value == 0 ? "" : Integer.toString(value));
    }

    public void setState(CellState state) {
        this.state = state;
        updateStyle();
    }

    // getters...
}