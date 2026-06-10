package com.sudoku.io;

import com.sudoku.model.Board;
import com.sudoku.validator.BoardValidator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.IOException;

public class PuzzleReader {

    public Board read(String path) throws IOException {

        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(
                    "Path must not be empty"
            );
        }

        int size = Board.getSize();
        int[][] values = new int[size][size];

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             Path.of(path))) {

            for (int row = 0; row < size; row++) {

                String line = reader.readLine();

                if (line == null || line.length() != size) {
                    throw new PuzzleFormatException(
                            "Invalid puzzle format at row " + row
                    );
                }

                for (int col = 0; col < size; col++) {

                    char ch = line.charAt(col);

                    if (!Character.isDigit(ch)) {
                        throw new PuzzleFormatException(
                                "Invalid character at row "
                                        + row
                                        + ", col "
                                        + col
                        );
                    }

                    values[row][col] = ch - '0';
                }
            }

            if (reader.readLine() != null) {
                throw new PuzzleFormatException(
                        "Puzzle contains extra rows"
                );
            }
        }

        Board board = new Board(values);

        if (!BoardValidator.isValidBoard(board)) {
            throw new PuzzleFormatException(
                    "Puzzle violates Sudoku rules"
            );
        }

        return board;
    }
}