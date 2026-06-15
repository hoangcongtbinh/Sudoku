package com.sudoku.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SudokuCell extends StackPane {
    private int row;
    private int col;
    private int value;
    private CellState state;
    private Label numberLabel;
    private boolean highlighted;
    private boolean isFixed;      // Ô cố định ban đầu
    private boolean hasError;     // Ô đang bị lỗi (trùng)
    private boolean isChanged;    // Ô vừa thay đổi (highlight tạm)
    private Timeline changeTimeline; // Timer để tắt highlight changed

    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = 0;
        this.state = CellState.EMPTY;
        this.highlighted = false;
        this.isFixed = false;
        this.hasError = false;
        this.isChanged = false;

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

    public void setFixed(boolean fixed) {
        this.isFixed = fixed;
        updateStyle();
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setError(boolean error) {
        this.hasError = error;
        updateStyle();
    }

    public boolean hasError() {
        return hasError;
    }

    /**
     * Đánh dấu ô vừa thay đổi - highlight tạm thời rồi tự tắt
     */
    public void flashChanged() {
        this.isChanged = true;
        updateStyle();

        // Hủy timeline cũ nếu có
        if (changeTimeline != null) {
            changeTimeline.stop();
        }

        // Tạo timeline mới: highlight trong 500ms rồi fade out trong 500ms
        changeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(500), e -> {
                    this.isChanged = false;
                    updateStyle();
                }),
                new KeyFrame(Duration.millis(1000), new KeyValue(opacityProperty(), 1.0))
        );
        changeTimeline.setOnFinished(e -> {
            this.isChanged = false;
            updateStyle();
        });
        changeTimeline.play();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    private void updateStyle() {
        getStyleClass().removeAll(
                "given", "empty", "current", "trying", "backtrack",
                "solved", "highlighted", "fixed", "error", "changed", "user-input", "hint"
        );

        switch (state) {
            case GIVEN:    getStyleClass().add("given"); break;
            case EMPTY:    getStyleClass().add("empty"); break;
            case CURRENT:  getStyleClass().add("current"); break;
            case TRYING:   getStyleClass().add("trying"); break;
            case BACKTRACK:getStyleClass().add("backtrack"); break;
            case SOLVED:   getStyleClass().add("solved"); break;
            case HINT:     getStyleClass().add("hint"); break;
            case USER_INPUT: getStyleClass().add("user-input"); break;
            case ERROR:    getStyleClass().add("error"); break;
            case CHANGED:  getStyleClass().add("changed"); break;
        }

        // Ưu tiên: error > changed > fixed > highlighted
        if (hasError) {
            getStyleClass().add("error");
        } else if (isChanged) {
            getStyleClass().add("changed");
        } else if (isFixed && state != CellState.GIVEN) {
            getStyleClass().add("fixed");
        }

        if (highlighted && !hasError && !isChanged) {
            getStyleClass().add("highlighted");
        }
    }
}