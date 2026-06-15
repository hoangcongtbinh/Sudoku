package com.sudoku.benchmark;

import com.sudoku.model.Board;
import com.sudoku.solver.Solver;
import com.sudoku.solver.SolveResult;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmComparison {
    private List<SolverEntry> solvers = new ArrayList<>();

    private static class SolverEntry {
        String name;
        Solver solver;
        SolverEntry(String name, Solver solver) {
            this.name = name;
            this.solver = solver;
        }
    }

    public void addAlgorithm(String name, Solver solver) {
        solvers.add(new SolverEntry(name, solver));
    }

    public List<AlgorithmStats> runComparison(Board board) {
        List<AlgorithmStats> results = new ArrayList<>();

        for (SolverEntry entry : solvers) {
            Board copy = board.copy();
            long start = System.nanoTime();
            SolveResult result = entry.solver.solve(copy);
            long end = System.nanoTime();

            double timeMs = (end - start) / 1_000_000.0;
            int steps = result.getSteps().size();
            int backtracks = result.getBacktrackCount();

            AlgorithmStats stats = new AlgorithmStats(
                    entry.name, steps, backtracks, timeMs, result.isSolved()
            );
            results.add(stats);
        }

        // Đánh dấu algorithm nhanh nhất (có solved)
        double bestTime = results.stream()
                .filter(AlgorithmStats::isSolved)
                .mapToDouble(AlgorithmStats::getTimeMs)
                .min().orElse(Double.MAX_VALUE);

        for (AlgorithmStats stat : results) {
            if (stat.isSolved() && stat.getTimeMs() == bestTime) {
                stat.setBest(true);
            }
        }

        return results;
    }
}