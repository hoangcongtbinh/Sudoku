package com.sudoku;

import com.sudoku.model.*;
import com.sudoku.generator.*;
import com.sudoku.solver.*;

import java.util.List;

public class SolverComparisonTest {
    public static void main(String[] args) {
        // Bước 1: Tạo phiên chơi sudoku
        BoardGenerator boardGenerator = new BoardGenerator();
        GameSession mediumGame = boardGenerator.generateGameSession(Difficulty.EXPERT);

        // Hiển thị bảng
        System.out.println("Đã tạo bảng Sudoku mức độ trung bình!");
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 8 && i != 0) {
                System.out.println();
                System.out.println("---------------------");
            } else {
                System.out.println();
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 8 && j != 0) System.out.print("| ");
                System.out.print(mediumGame.getCurrentBoard().getCell(i, j) + " ");
            }
        }
        System.out.printf("\n\n");

        // Bước 2: Đưa bảng cho Solver giải
        SolveResult SA_solution = new SASolver().solve(mediumGame.getCurrentBoard());
        SolveResult MRV_solution = new MRVSolver().solve(mediumGame.getCurrentBoard());
        SolveResult Backtracking_solution = new BacktrackingSolver().solve(mediumGame.getCurrentBoard());

        // Bước 3: Đem lên so sánh
        System.out.println("Số bước:");
        System.out.println("MRV: " + MRV_solution.getSteps().size());
        System.out.println("Backtracking: " + Backtracking_solution.getSteps().size());
        System.out.println("SA: " + SA_solution.getSteps().size());

        System.out.printf("\nThời gian giải:\n");
        System.out.println("MRV: " + MRV_solution.getDurationNanos() + " ns");
        System.out.println("Backtracking: " + Backtracking_solution.getDurationNanos() + " ns");
        System.out.println("SA: " + SA_solution.getDurationNanos() + " ns");
    }
}
