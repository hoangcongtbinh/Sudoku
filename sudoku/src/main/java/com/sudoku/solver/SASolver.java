package com.sudoku.solver;

import com.sudoku.model.Board;
import com.sudoku.model.Step;
import com.sudoku.model.StepType;
import com.sudoku.validator.BoardValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SASolver implements Solver {

    private static final double INITIAL_TEMP = 1000.0;
    private static final double COOLING_RATE = 0.995;
    private static final double MIN_TEMP = 0.01;

    private final Random random = new Random();
    private boolean[][] fixed;

    private List<Step> steps;

    private void initializeFixedCells(Board board) {

        int size = Board.getSize();
        fixed = new boolean[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                fixed[row][col] =
                        board.getCell(row, col) != Board.getEmptyValue();
            }
        }
    }

    private int freeCellCount() {

        int count = 0;

        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {
                if (!fixed[row][col]) {
                    count++;
                }
            }
        }

        return count;
    }

    private void initializeBoard(Board board) {

        int size = Board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (!fixed[row][col]) {
                    board.setCell(row, col, random.nextInt(size) + 1);
                }
            }
        }
    }

    private int calculateCost(Board board) {

        int conflicts = 0;
        int size = Board.getSize();

        for (int row = 0; row < size; row++) {
            int[] count = new int[size + 1];

            for (int col = 0; col < size; col++) {
                count[board.getCell(row, col)]++;
            }

            for (int v = 1; v <= size; v++) {
                if (count[v] > 1) conflicts += count[v] - 1;
            }
        }

        for (int col = 0; col < size; col++) {
            int[] count = new int[size + 1];

            for (int row = 0; row < size; row++) {
                count[board.getCell(row, col)]++;
            }

            for (int v = 1; v <= size; v++) {
                if (count[v] > 1) conflicts += count[v] - 1;
            }
        }

        int boxSize = (int) Math.sqrt(size);

        for (int boxRow = 0; boxRow < boxSize; boxRow++) {
            for (int boxCol = 0; boxCol < boxSize; boxCol++) {

                int[] count = new int[size + 1];

                for (int r = boxRow * boxSize; r < (boxRow + 1) * boxSize; r++) {
                    for (int c = boxCol * boxSize; c < (boxCol + 1) * boxSize; c++) {
                        count[board.getCell(r, c)]++;
                    }
                }

                for (int v = 1; v <= size; v++) {
                    if (count[v] > 1) conflicts += count[v] - 1;
                }
            }
        }

        return conflicts;
    }

    private Board generateNeighbor(Board board) {

        Board neighbor = board.copy();

        int r1, c1, r2, c2;

        do {
            r1 = random.nextInt(Board.getSize());
            c1 = random.nextInt(Board.getSize());
        } while (fixed[r1][c1]);

        do {
            r2 = random.nextInt(Board.getSize());
            c2 = random.nextInt(Board.getSize());
        } while (fixed[r2][c2] || (r1 == r2 && c1 == c2));

        int temp = neighbor.getCell(r1, c1);
        neighbor.setCell(r1, c1, neighbor.getCell(r2, c2));
        neighbor.setCell(r2, c2, temp);

        return neighbor;
    }

    @Override
    public SolveResult solve(Board board) {

        long startTime = System.currentTimeMillis();

        initializeFixedCells(board);

        steps = new ArrayList<>();

        if (freeCellCount() < 2) {
            return BoardValidator.isSolved(board) ? SolveResult.alreadySolved() : SolveResult.noSolution();
        }

        Board current = board.copy();
        initializeBoard(current);

        int currentCost = calculateCost(current);
        double temperature = INITIAL_TEMP;

        steps.add(new Step(
                -1, -1,
                currentCost,
                currentCost,
                StepType.SOLVER_STEP
        ));

        while (temperature > MIN_TEMP && currentCost > 0) {

            Board neighbor = generateNeighbor(current);
            int neighborCost = calculateCost(neighbor);

            int delta = neighborCost - currentCost;

            boolean accepted = false;

            if (delta < 0) {
                current = neighbor;
                currentCost = neighborCost;
                accepted = true;
            } else {
                double probability = Math.exp(-((double) delta) / temperature);

                if (random.nextDouble() < probability) {
                    current = neighbor;
                    currentCost = neighborCost;
                    accepted = true;
                }
            }

            if (accepted) {
                steps.add(new Step(
                        -1, -1,
                        currentCost,
                        currentCost,
                        StepType.SOLVER_STEP
                ));
            }

            temperature *= COOLING_RATE;
        }

        long endTime = System.currentTimeMillis();

        boolean solved = BoardValidator.isSolved(current);
        long timeToSolve = endTime - startTime;

        return new SolveResult(solved, steps, timeToSolve);
    }
}