package com.sudoku.benchmark;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;

public class BenchmarkMain {
    public static void main(String[] args) {
        BoardGenerator generator = new BoardGenerator();
        GameSession session = generator.generateGameSession(Difficulty.MEDIUM);

        AlgorithmComparison comparison = new AlgorithmComparison();
        comparison.printComparison(session.getCurrentBoard());
    }
}
