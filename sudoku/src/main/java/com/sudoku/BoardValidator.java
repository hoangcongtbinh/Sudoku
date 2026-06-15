package com.sudoku;

import com.sudoku.model.Board;

import java.util.ArrayList;
import java.util.List;

public class BoardValidator {

    public static boolean isValidBoard(Board board) {
        if (board == null) return false;
        int size = Board.getSize();
        
        // Kiểm tra hàng
        for (int row = 0; row < size; row++) {
            boolean[] seen = new boolean[size + 1];
            for (int col = 0; col < size; col++) {
                int val = board.getCell(row, col);
                if (val != 0) {
                    if (seen[val]) return false;
                    seen[val] = true;
                }
            }
        }
        
        // Kiểm tra cột
        for (int col = 0; col < size; col++) {
            boolean[] seen = new boolean[size + 1];
            for (int row = 0; row < size; row++) {
                int val = board.getCell(row, col);
                if (val != 0) {
                    if (seen[val]) return false;
                    seen[val] = true;
                }
            }
        }
        
        // Kiểm tra box 3x3
        int boxSize = (int) Math.sqrt(size);
        for (int boxRow = 0; boxRow < boxSize; boxRow++) {
            for (int boxCol = 0; boxCol < boxSize; boxCol++) {
                boolean[] seen = new boolean[size + 1];
                for (int r = boxRow * boxSize; r < (boxRow + 1) * boxSize; r++) {
                    for (int c = boxCol * boxSize; c < (boxCol + 1) * boxSize; c++) {
                        int val = board.getCell(r, c);
                        if (val != 0) {
                            if (seen[val]) return false;
                            seen[val] = true;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    public static boolean isValidMove(Board board, int row, int col, int value) {
        if (value == 0) return true;
        int size = Board.getSize();
        
        // Kiểm tra hàng
        for (int c = 0; c < size; c++) {
            if (c != col && board.getCell(row, c) == value) return false;
        }
        
        // Kiểm tra cột
        for (int r = 0; r < size; r++) {
            if (r != row && board.getCell(r, col) == value) return false;
        }
        
        // Kiểm tra box 3x3
        int boxSize = (int) Math.sqrt(size);
        int boxRow = (row / boxSize) * boxSize;
        int boxCol = (col / boxSize) * boxSize;
        for (int r = boxRow; r < boxRow + boxSize; r++) {
            for (int c = boxCol; c < boxCol + boxSize; c++) {
                if ((r != row || c != col) && board.getCell(r, c) == value) return false;
            }
        }
        
        return true;
    }

    public static boolean isSolved(Board board) {
        if (!isValidBoard(board)) return false;
        int size = Board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) == 0) return false;
            }
        }
        return true;
    }

    public static List<Integer> getCandidates(Board board, int row, int col) {
        List<Integer> candidates = new ArrayList<>();
        if (!board.isEmptyCell(row, col)) return candidates;
        
        for (int val = 1; val <= Board.getSize(); val++) {
            if (isValidMove(board, row, col, val)) {
                candidates.add(val);
            }
        }
        return candidates;
    }
}