package com.sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class SudokuCell extends StackPane {
    private int row;
    private int col;
    private int value;
    private CellState state;
    private Label numberLabel;
    private boolean highlighted;

    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = 0;
        this.state = CellState.EMPTY;
        this.highlighted = false;

        numberLabel = new Label("");
        numberLabel.setFont(Font.font("Monospaced", 20));
        getChildren().add(numberLabel);
        setAlignment(Pos.CENTER);
        setPrefSize(60, 60);
        getStyleClass().add("sudoku-cell");

        updateStyle();
    }

    public void setValue(int value) {
        this.value = value;
        numberLabel.setText(value == 0 ? "" : Integer.toString(value));
        updateStyle();
    }

    public int getValue() {
        return value;
    }

    public void setState(CellState state) {
        this.state = state;
        updateStyle();
    }

    public CellState getState() {
        return state;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        updateStyle();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    private void updateStyle() {
        getStyleClass().removeAll("given", "empty", "current", "trying", "backtrack", "solved", "highlighted");
        switch (state) {
            case GIVEN:    getStyleClass().add("given"); break;
            case EMPTY:    getStyleClass().add("empty"); break;
            case CURRENT:  getStyleClass().add("current"); break;
            case TRYING:   getStyleClass().add("trying"); break;
            case BACKTRACK:getStyleClass().add("backtrack"); break;
            case SOLVED:   getStyleClass().add("solved"); break;
        }
        if (highlighted && state != CellState.CURRENT && state != CellState.TRYING) {
            getStyleClass().add("highlighted");
        }
    }
}