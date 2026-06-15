package com.sudoku.ui;

public enum CellState {
    EMPTY,      // Ô trống
    GIVEN,      // Ô cố định ban đầu
    CURRENT,    // Ô đang được xử lý (solver)
    TRYING,     // Ô đang thử (solver)
    BACKTRACK,  // Ô bị backtrack (solver)
    SOLVED,     // Ô đã giải xong (solver)
    HINT,       // Ô hint (play mode)
    USER_INPUT, // Ô người dùng nhập (play mode)
    ERROR,      // Ô lỗi (trùng hàng/cột/cụm)
    CHANGED     // Ô vừa thay đổi (highlight tạm thời)
}