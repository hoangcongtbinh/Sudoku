package com.sudoku.model;

import java.util.List;

public class GameSession {
    Board initBoard;
    Board currentBoard;
    Board solutionBoard;

    Difficulty difficulty;
    List<Step> history; // Lịch sử
    long startTime; // Timestamp tại thời điểm bắt đầu game

    // Để các thuộc tính Private, viết thêm hàm get/set và các hàm khác nếu cần thiết
}