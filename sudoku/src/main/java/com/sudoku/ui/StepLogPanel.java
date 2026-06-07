package com.sudoku.ui;

import javax.swing.*;
import java.awt.*;

public class StepLogPanel extends JPanel {
    private JTextArea logArea;
    private JScrollPane scrollPane;

    public StepLogPanel() {
        setLayout(new BorderLayout());
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(5, 5, 8));
        logArea.setForeground(new Color(136, 255, 170));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addLog(String message) {
        logArea.append("> " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clear() {
        logArea.setText("");
    }
}