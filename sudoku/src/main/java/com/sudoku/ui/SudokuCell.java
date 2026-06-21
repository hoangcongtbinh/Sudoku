package com.sudoku.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        numberLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        getChildren().add(numberLabel);
        setAlignment(Pos.CENTER);
        setPrefSize(52, 52);
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
        // FIX: Chỉ update nếu thay đổi thực sự, tránh gọi updateStyle() liên tục
        if (this.hasError != error) {
            this.hasError = error;
            updateStyle();
        }
    }

    public boolean hasError() {
        return hasError;
    }

    /**
     * FIX: Flash highlight dùng background color thay vì opacity
     * Tránh conflict với CSS và giảm tải UI thread
     */
    public void flashChanged() {
        // Hủy timeline cũ nếu có
        if (changeTimeline != null) {
            changeTimeline.stop();
        }

        this.isChanged = true;
        updateStyle();

        // FIX: Dùng PauseTransition đơn giản thay vì Timeline phức tạp
        // Tắt highlight sau 500ms
        changeTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    this.isChanged = false;
                    updateStyle();
                })
        );
        changeTimeline.setCycleCount(1);
        changeTimeline.play();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    /**
     * Cập nhật style class theo priority:
     * ERROR (đỏ) > CHANGED (flash xanh) > HINT (cam) > FIXED (xám đậm)
     * GIVEN (đen đậm) vs USER_INPUT (xanh)
     */
    private void updateStyle() {
        getStyleClass().removeAll(
                "given", "empty", "current", "trying", "backtrack",
                "solved", "highlighted", "fixed", "error", "changed",
                "user-input", "hint"
        );

        // Base state
        switch (state) {
            case GIVEN:
                getStyleClass().add("given");
                break;
            case EMPTY:
                getStyleClass().add("empty");
                break;
            case CURRENT:
                getStyleClass().add("current");
                break;
            case TRYING:
                getStyleClass().add("trying");
                break;
            case BACKTRACK:
                getStyleClass().add("backtrack");
                break;
            case SOLVED:
                getStyleClass().add("solved");
                break;
            case HINT:
                getStyleClass().add("hint");
                break;
            case USER_INPUT:
                getStyleClass().add("user-input");
                break;
            case ERROR:
                getStyleClass().add("error");
                break;
            case CHANGED:
                getStyleClass().add("changed");
                break;
        }

        // Priority override: error > changed > hint > fixed
        if (hasError) {
            getStyleClass().add("error");
        } else if (isChanged) {
            getStyleClass().add("changed");
        } else if (state == CellState.HINT) {
            getStyleClass().add("hint");
        } else if (isFixed) {
            getStyleClass().add("fixed");
        }

        // Highlight border (không override màu nền của error/changed)
        if (highlighted && !hasError && !isChanged) {
            getStyleClass().add("highlighted");
        }
    }
}