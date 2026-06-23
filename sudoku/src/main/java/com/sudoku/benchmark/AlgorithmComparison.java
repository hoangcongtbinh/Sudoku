package com.sudoku.benchmark;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sudoku.model.Board;
import com.sudoku.solver.BacktrackingSolver;
import com.sudoku.solver.MRVSolver;
import com.sudoku.solver.SASolver;
import com.sudoku.solver.SolveResult;
import com.sudoku.solver.Solver;

public class AlgorithmComparison {
    private final Map<String, Solver> solvers;

    public AlgorithmComparison() {
        this.solvers = new LinkedHashMap<>();
        addSolver("Backtracking", new BacktrackingSolver());
        addSolver("MRV", new MRVSolver());
        addSolver("Simulated Annealing", new SASolver());
    }

    public AlgorithmComparison(Map<String, Solver> solvers) {
        if (solvers == null || solvers.isEmpty()) {
            throw new IllegalArgumentException("Solvers must not be null or empty.");
        }
        this.solvers = new LinkedHashMap<>(solvers);
    }

    public void addSolver(String name, Solver solver) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Solver name must not be blank.");
        }
        if (solver == null) {
            throw new IllegalArgumentException("Solver must not be null.");
        }
        solvers.put(name, solver);
    }

    public List<AlgorithmStats> compare(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board must not be null.");
        }

        List<AlgorithmStats> stats = new ArrayList<>();
        int initialEmptyCells = board.countEmptyCells();

        for (Map.Entry<String, Solver> entry : solvers.entrySet()) {
            stats.add(runSolver(
                    entry.getKey(),
                    entry.getValue(),
                    board.copy(),
                    initialEmptyCells
            ));
        }

        // Rank the solvers that successfully solved the board
        List<AlgorithmStats> solvedStats = new ArrayList<>();
        for (AlgorithmStats stat : stats) {
            if (stat.isSolved()) {
                solvedStats.add(stat);
            }
        }

        // Sort by elapsedTimeNanos ascending
        solvedStats.sort((s1, s2) -> Long.compare(s1.getElapsedTimeNanos(), s2.getElapsedTimeNanos()));

        if (!solvedStats.isEmpty()) {
            solvedStats.get(0).setBest(true);
            if (solvedStats.size() > 1) {
                solvedStats.get(1).setSecondBest(true);
            }
        }

        return stats;
    }

    public void printComparison(Board board) {
        System.out.println(formatAsTable(compare(board)));
    }

    public String formatAsTable(List<AlgorithmStats> stats) {
        StringBuilder table = new StringBuilder();
        table.append(String.format(
                "%-22s %-8s %12s %10s %12s %12s %10s %-20s%n",
                "Algorithm",
                "Solved",
                "Time(ms)",
                "Steps",
                "Backtracks",
                "EmptyCells",
                "Ranking",
                "Error"
        ));
        table.append("-".repeat(110)).append(System.lineSeparator());

        for (AlgorithmStats item : stats) {
            table.append(String.format(
                    "%-22s %-8s %12.3f %10d %12d %12d %10s %-20s%n",
                    item.getAlgorithmName(),
                    item.isSolved(),
                    item.getElapsedTimeMillis(),
                    item.getStepCount(),
                    item.getBacktracks(),
                    item.getInitialEmptyCells(),
                    item.isBest() ? "1" : (item.isSecondBest() ? "2" : "3"),
                    item.hasError() ? item.getErrorMessage() : "-"
            ));
        }

        return table.toString();
    }

    private AlgorithmStats runSolver(
            String name,
            Solver solver,
            Board board,
            int initialEmptyCells
    ) {
        long startTime = System.nanoTime();

        try {
            SolveResult result = solver.solve(board);
            long endTime = System.nanoTime();
            int stepCount = result.getSteps() == null ? 0 : result.getSteps().size();
            long duration = result.getDurationNanos() > 0 ? result.getDurationNanos() : (endTime - startTime);

            return new AlgorithmStats(
                    name,
                    result.isSolved(),
                    duration,
                    stepCount,
                    initialEmptyCells,
                    result.getBacktrackCount(),
                    null
            );
        } catch (RuntimeException exception) {
            long endTime = System.nanoTime();
            return new AlgorithmStats(
                    name,
                    false,
                    endTime - startTime,
                    0,
                    initialEmptyCells,
                    0,
                    exception.getClass().getSimpleName()
            );
        }
    }
}
