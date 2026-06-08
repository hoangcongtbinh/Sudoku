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

        // Rows
        for (int row = 0; row < size; row++) {

            int[] count = new int[size + 1];

            for (int col = 0; col < size; col++) {
                count[board.getCell(row, col)]++;
            }

            for (int value = 1; value <= size; value++) {
                if (count[value] > 1) {
                    conflicts += count[value] - 1;
                }
            }
        }

        // Columns
        for (int col = 0; col < size; col++) {

            int[] count = new int[size + 1];

            for (int row = 0; row < size; row++) {
                count[board.getCell(row, col)]++;
            }

            for (int value = 1; value <= size; value++) {
                if (count[value] > 1) {
                    conflicts += count[value] - 1;
                }
            }
        }

        // Boxes
        int boxSize = (int) Math.sqrt(size);

        for (int boxRow = 0; boxRow < boxSize; boxRow++) {
            for (int boxCol = 0; boxCol < boxSize; boxCol++) {

                int[] count = new int[size + 1];

                for (int row = boxRow * boxSize;
                     row < (boxRow + 1) * boxSize;
                     row++) {

                    for (int col = boxCol * boxSize;
                         col < (boxCol + 1) * boxSize;
                         col++) {

                        count[board.getCell(row, col)]++;
                    }
                }

                for (int value = 1; value <= size; value++) {
                    if (count[value] > 1) {
                        conflicts += count[value] - 1;
                    }
                }
            }
        }

        return conflicts;
    }

    private Board generateNeighbor(Board board) {

        Board neighbor = board.copy();

        if (random.nextDouble() < 0.5) {

            int row;
            int col;

            do {
                row = random.nextInt(Board.getSize());
                col = random.nextInt(Board.getSize());
            } while (fixed[row][col]);

            int oldValue = neighbor.getCell(row, col);

            int newValue;

            do {
                newValue = random.nextInt(Board.getSize()) + 1;
            } while (newValue == oldValue);

            neighbor.setCell(row, col, newValue);

        } else {

            int row1;
            int col1;
            int row2;
            int col2;

            do {
                row1 = random.nextInt(Board.getSize());
                col1 = random.nextInt(Board.getSize());
            } while (fixed[row1][col1]);

            do {
                row2 = random.nextInt(Board.getSize());
                col2 = random.nextInt(Board.getSize());
            } while (
                    fixed[row2][col2]
                            || (row1 == row2 && col1 == col2)
            );

            int temp = neighbor.getCell(row1, col1);

            neighbor.setCell(
                    row1,
                    col1,
                    neighbor.getCell(row2, col2)
            );

            neighbor.setCell(
                    row2,
                    col2,
                    temp
            );
        }

        return neighbor;
    }

    @Override
    public SolveResult solve(Board board) {

        long startTime = System.currentTimeMillis();

        initializeFixedCells(board);

        steps = new ArrayList<>();

        if (freeCellCount() == 0) {

            long endTime = System.currentTimeMillis();

            return new SolveResult(
                    BoardValidator.isSolved(board),
                    steps,
                    endTime - startTime
            );
        }

        Board current = board.copy();

        initializeBoard(current);

        int currentCost = calculateCost(current);

        double temperature = INITIAL_TEMP;

        steps.add(new Step(
                -1,
                -1,
                currentCost,
                currentCost,
                StepType.SOLVER_STEP
        ));

        while (temperature > MIN_TEMP && currentCost > 0) {

            Board neighbor = generateNeighbor(current);

            int neighborCost = calculateCost(neighbor);

            int delta = neighborCost - currentCost;

            boolean accepted = false;

            if (delta <= 0) {

                current = neighbor;
                currentCost = neighborCost;
                accepted = true;

            } else {

                double probability =
                        Math.exp(
                                -((double) delta)
                                        / temperature
                        );

                if (random.nextDouble() < probability) {

                    current = neighbor;
                    currentCost = neighborCost;
                    accepted = true;
                }
            }

            if (accepted) {

                steps.add(new Step(
                        -1,
                        -1,
                        currentCost,
                        currentCost,
                        StepType.SOLVER_STEP
                ));
            }

            temperature *= COOLING_RATE;
        }

        long endTime = System.currentTimeMillis();

        return new SolveResult(
                BoardValidator.isSolved(current),
                steps,
                endTime - startTime
        );
    }
}