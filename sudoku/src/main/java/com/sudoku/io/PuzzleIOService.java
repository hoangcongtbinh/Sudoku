package com.sudoku.io;

import com.sudoku.model.Board;

import java.io.IOException;

public class PuzzleIOService {

    private final PuzzleReader reader =
            new PuzzleReader();

    private final PuzzleWriter writer =
            new PuzzleWriter();

    public Board loadPuzzle(String path)
            throws IOException {

        return reader.read(path);
    }

    public void savePuzzle(
            Board board,
            String path)
            throws IOException {

        writer.write(board, path);
    }
}