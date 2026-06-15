package com.sudoku.benchmark;

import com.sudoku.generator.BoardGenerator;
import com.sudoku.model.Difficulty;
import com.sudoku.model.GameSession;
import com.sudoku.solver.BacktrackingSolver;
import com.sudoku.solver.MRVSolver;
import com.sudoku.solver.SASolver;

import java.util.List;

public class BenchmarkMain {
    public static void main(String[] args) {
        BoardGenerator generator = new BoardGenerator();
        GameSession session = generator.generateGameSession(Difficulty.MEDIUM);

        AlgorithmComparison comparison = new AlgorithmComparison();
        comparison.addAlgorithm("Backtracking", new BacktrackingSolver());
        comparison.addAlgorithm("MRV", new MRVSolver());
        comparison.addAlgorithm("Simulated Annealing", new SASolver());

        List<AlgorithmStats> results = comparison.runComparison(session.getCurrentBoard());

        for (AlgorithmStats stats : results) {
            String marker = stats.isBest() ? " *BEST*" : "";
            System.out.println(stats.getAlgorithmName() + " -> " + stats + marker);
        }
    }
}