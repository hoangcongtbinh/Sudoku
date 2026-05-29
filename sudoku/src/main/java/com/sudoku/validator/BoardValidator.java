package com.sudoku.validator;

import java.util.List;
import com.sudoku.model.* ;

public class BoardValidator {
    boolean isValidMove(Board board, Step step) {
        // Kiểm tra bước đi có hợp lệ không
        return false;
    };

    boolean isSolved(Board board) {
        // Kiểm tra Board đã được giải xong chưa
        return false;
    };

    List<Integer> getCandidates(Board board, int row, int col) {
        // Lấy danh sách các số có thể điền vào một ô xác định
        return null;
    };

    // Thêm các hàm nếu cảm thấy cần thiết
}
