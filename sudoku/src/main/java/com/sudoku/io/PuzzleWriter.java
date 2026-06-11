package com.sudoku.io;

import com.sudoku.model.Board;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;

public class PuzzleWriter {

    public void write(Board board,
                      String path)
            throws IOException {

        if (board == null) {
            throw new IllegalArgumentException(
                    "Board must not be null"
            );
        }

        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(
                    "Path must not be empty"
            );
        }

        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             Path.of(path))) {

            for (int row = 0;
                 row < Board.getSize();
                 row++) {

                for (int col = 0;
                     col < Board.getSize();
                     col++) {

                    writer.write(
                            String.valueOf(
                                    board.getCell(row, col)
                            )
                    );
                }

                writer.newLine();
            }
        }
    }
}