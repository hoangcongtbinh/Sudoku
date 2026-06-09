package com.sudoku.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import com.sudoku.model.Difficulty;

public class DifficultySelector extends ComboBox<String> {

    public DifficultySelector() {
        // Add difficulty options
        getItems().addAll(
                "Easy",
                "Medium",
                "Hard",
                "Expert"
        );

        // Set default selection
        setValue("Medium");

        // Tooltip for user guidance
        setTooltip(new Tooltip("Select puzzle difficulty"));

        // Apply CSS style
        getStyleClass().add("difficulty-combo");

        // Make it non-editable
        setEditable(false);

        // Preferred width
        setPrefWidth(180);
    }

    // Helper method to get selected Difficulty enum
    public Difficulty getSelectedDifficulty() {
        String value = getValue();
        switch (value) {
            case "Easy": return Difficulty.EASY;
            case "Medium": return Difficulty.MEDIUM;
            case "Hard": return Difficulty.HARD;
            case "Expert": return Difficulty.EXPERT;
            default: return Difficulty.MEDIUM;
        }
    }
}