package com.sudoku.io;

import java.io.*;

public class PuzzleIOService {

    /**
     * Đọc puzzle từ file text định dạng 9 dòng, mỗi dòng 9 số (0 = ô trống)
     */
    public static int[][] readFromFile(File file) throws IOException {
        int[][] grid = new int[9][9];

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < 9) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                for (int col = 0; col < 9 && col < parts.length; col++) {
                    grid[row][col] = Integer.parseInt(parts[col].trim());
                }
                row++;
            }

            if (row < 9) {
                throw new IOException("Invalid puzzle format: expected 9 rows, found " + row);
            }
        }

        return grid;
    }

    /**
     * Ghi puzzle ra file text định dạng 9 dòng
     */
    public static void writeToFile(File file, int[][] grid) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    writer.write(String.valueOf(grid[row][col]));
                    if (col < 8) writer.write(" ");
                }
                writer.newLine();
            }
        }
    }
}