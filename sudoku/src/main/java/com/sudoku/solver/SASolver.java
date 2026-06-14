package com.sudoku.solver;

import com.sudoku.model.Board;
import com.sudoku.model.Step;
import com.sudoku.model.StepType;
import com.sudoku.validator.BoardValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SASolver implements Solver {

    private static final double INITIAL_TEMP = 2.0;
    private static final double COOLING_RATE = 0.999;
    private static final double MIN_TEMP = 0.01;
    private static final int MAX_RESTARTS = 10;

    private final Random random = new Random();
    private boolean[][] fixed;

    private List<Step> steps;

    private int lastSwapRow1;
    private int lastSwapCol1;
    private int lastSwapVal1;
    private int lastSwapRow2;
    private int lastSwapCol2;
    private int lastSwapVal2;

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

    private void initializeBoard(Board board, List<Step> steps) {

        int size = Board.getSize();
        int boxSize = (int) Math.sqrt(size);

        // Duyệt qua từng khối 3x3
        for (int boxRow = 0; boxRow < boxSize; boxRow++) {
            for (int boxCol = 0; boxCol < boxSize; boxCol++) {

                boolean[] present = new boolean[size + 1];
                List<int[]> emptyCells = new ArrayList<>();

                // Duyệt qua các ô trong khối
                for (int r = boxRow * boxSize; r < (boxRow + 1) * boxSize; r++) {
                    for (int c = boxCol * boxSize; c < (boxCol + 1) * boxSize; c++) {
                        // Thu thập vị trí các ô trống trong khối
                        if (fixed[r][c]) {
                            present[board.getCell(r, c)] = true;
                        } else {
                            emptyCells.add(new int[]{r, c});
                        }
                    }
                }

                // Liệt kê các số 1-9 chưa được điền
                List<Integer> missing = new ArrayList<>();
                for (int val = 1; val <= size; val++) {
                    if (!present[val]) {
                        missing.add(val);
                    }
                }

                // Trộn các số đó và điền
                Collections.shuffle(missing, random);
                for (int i = 0; i < emptyCells.size(); i++) {
                    int[] cell = emptyCells.get(i);
                    int prevVal = board.getCell(cell[0], cell[1]);
                    int newVal = missing.get(i);
                    board.setCell(cell[0], cell[1], newVal);
                    if (steps != null) {
                        steps.add(new Step(cell[0], cell[1], prevVal, newVal, StepType.SOLVER_STEP));
                    }
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

        // Boxes are guaranteed to have 0 conflicts because of box-level initialization and swapping.
        return conflicts;
    }

    private Board generateNeighbor(Board board) {

        Board neighbor = board.copy();
        int size = Board.getSize();
        int boxSize = (int) Math.sqrt(size);

        // Duyệt tìm các cu 3x3 có 2+ ô trống
        List<Integer> candidateBoxes = new ArrayList<>();
        for (int b = 0; b < size; b++) {
            int boxRow = b / boxSize;
            int boxCol = b % boxSize;
            int freeCount = 0;
            for (int r = boxRow * boxSize; r < (boxRow + 1) * boxSize; r++) {
                for (int c = boxCol * boxSize; c < (boxCol + 1) * boxSize; c++) {
                    if (!fixed[r][c]) {
                        freeCount++;
                    }
                }
            }
            if (freeCount >= 2) {
                candidateBoxes.add(b);
            }
        }

        if (candidateBoxes.isEmpty()) {
            return neighbor;
        }

        // Chọn ngẫu nhiên 1 cụm 3x3
        int selectedBox = candidateBoxes.get(random.nextInt(candidateBoxes.size()));
        int boxRow = selectedBox / boxSize;
        int boxCol = selectedBox % boxSize;

        // Lấy danh sách các ô ko fixed
        List<int[]> freeCells = new ArrayList<>();
        for (int r = boxRow * boxSize; r < (boxRow + 1) * boxSize; r++) {
            for (int c = boxCol * boxSize; c < (boxCol + 1) * boxSize; c++) {
                if (!fixed[r][c]) {
                    freeCells.add(new int[]{r, c});
                }
            }
        }

        // Lấy ngẫu nhiên 2 ô ko fixed và tráo đổi giá trị của chúng
        int idx1 = random.nextInt(freeCells.size());
        int idx2;
        do {
            idx2 = random.nextInt(freeCells.size());
        } while (idx1 == idx2);
        int[] cell1 = freeCells.get(idx1);
        int[] cell2 = freeCells.get(idx2);

        int val1 = neighbor.getCell(cell1[0], cell1[1]);
        int val2 = neighbor.getCell(cell2[0], cell2[1]);

        neighbor.setCell(cell1[0], cell1[1], val2);
        neighbor.setCell(cell2[0], cell2[1], val1);

        this.lastSwapRow1 = cell1[0];
        this.lastSwapCol1 = cell1[1];
        this.lastSwapVal1 = val1;
        this.lastSwapRow2 = cell2[0];
        this.lastSwapCol2 = cell2[1];
        this.lastSwapVal2 = val2;

        return neighbor;
    }

    @Override
    public SolveResult solve(Board board) {

        long startTime = System.nanoTime();

        initializeFixedCells(board);

        steps = new ArrayList<>();

        if (freeCellCount() == 0) {

            long endTime = System.nanoTime();

            return new SolveResult(
                    BoardValidator.isSolved(board),
                    steps,
                    (endTime - startTime)
            );
        }

        Board currentBestBoard = null;
        int bestCost = Integer.MAX_VALUE;

        for (int restart = 0; restart < MAX_RESTARTS; restart++) {
            // Reset the board UI for the new restart in the step list
            if (restart > 0 && steps != null) {
                int size = Board.getSize();
                for (int r = 0; r < size; r++) {
                    for (int c = 0; c < size; c++) {
                        if (!fixed[r][c]) {
                            steps.add(new Step(r, c, board.getCell(r, c), Board.getEmptyValue(), StepType.BACKTRACK));
                        }
                    }
                }
            }

            Board current = board.copy();
            initializeBoard(current, steps);
            int currentCost = calculateCost(current);

            steps.add(new Step(
                    -1,
                    -1,
                    currentCost,
                    currentCost,
                    StepType.SOLVER_STEP
            ));

            double temperature = INITIAL_TEMP;

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
                            lastSwapRow1, lastSwapCol1,
                            lastSwapVal1, lastSwapVal2,
                            StepType.SOLVER_STEP
                    ));
                    steps.add(new Step(
                            lastSwapRow2, lastSwapCol2,
                            lastSwapVal2, lastSwapVal1,
                            StepType.SOLVER_STEP
                    ));
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

            if (currentCost < bestCost) {
                bestCost = currentCost;
                currentBestBoard = current.copy();
            }

            if (currentCost == 0) {
                break;
            }
        }

        if (bestCost == 0 && currentBestBoard != null) {
            for (int r = 0; r < Board.getSize(); r++) {
                for (int c = 0; c < Board.getSize(); c++) {
                    board.setCell(r, c, currentBestBoard.getCell(r, c));
                }
            }
        }

        long endTime = System.nanoTime();

        return new SolveResult(
                bestCost == 0,
                steps,
                (endTime - startTime)
        );
    }
}