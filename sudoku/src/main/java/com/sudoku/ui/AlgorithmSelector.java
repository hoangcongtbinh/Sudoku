package com.sudoku.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;

public class AlgorithmSelector extends ComboBox<String> {

    public AlgorithmSelector() {
        // Add algorithm options
        getItems().addAll(
                "Backtracking",
                "MRV (Minimum Remaining Values)",
                "Simulated Annealing"
        );

        // Set default selection
        setValue("Backtracking");

        // Tooltip for user guidance
        setTooltip(new Tooltip("Select the algorithm to visualize"));

        // Apply CSS style
        getStyleClass().add("algorithm-combo");

        // Make it non-editable
        setEditable(false);

        // Preferred width
        setPrefWidth(220);
    }
}