package com.sudoku.model;

public class Board {
    int[][] values; // Trạng thái bảng hiện tại

    int getCell(int row, int col) {
        // Lấy giá trị của cell bất kỳ
        return 0;
    };

    boolean applyStep(Step step) {
        // Thực hiện thao tác với bảng
        // Trả T/F để xác định có thể thực hiện được thao tác đó không
        return false;
    };

    Board copy() {
        // Nhớ copy giá trị, không copy tham chiếu
        return null;
    };

    // Để các thuộc tính Private, viết thêm hàm get/set và các hàm khác nếu cần thiết
}