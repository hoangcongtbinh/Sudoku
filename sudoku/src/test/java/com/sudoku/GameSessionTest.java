package com.sudoku;

import com.sudoku.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest {

    private Board initBoard;
    private Board solutionBoard;
    private GameSession gameSession;

    @BeforeEach
    public void setUp() {
        // Tạo một bảng 9x9 đơn giản để test.
        // Chỉ điền một số ô để các ô còn lại là trống (mặc định là 0).
        initBoard = new Board();
        initBoard.setCell(0, 0, 5); // Ô không thể sửa (đã điền sẵn)
        initBoard.setCell(0, 1, 3); // Ô không thể sửa
        
        solutionBoard = new Board();
        solutionBoard.setCell(0, 0, 5);
        solutionBoard.setCell(0, 1, 3);
        solutionBoard.setCell(0, 2, 4); // Điền thêm giải pháp mẫu

        gameSession = new GameSession(initBoard, solutionBoard, Difficulty.EASY);
    }

    @Test
    public void testInitialization() {
        assertNotNull(gameSession);
        assertEquals(Difficulty.EASY, gameSession.getDifficulty());
        assertFalse(gameSession.isFinished());
        assertTrue(gameSession.getHistory().isEmpty());
        
        // Kiểm tra initBoard và currentBoard ban đầu giống nhau
        assertEquals(5, gameSession.getInitBoard().getCell(0, 0));
        assertEquals(5, gameSession.getCurrentBoard().getCell(0, 0));
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 2)); // Ô trống
    }

    @Test
    public void testMakeMove() {
        // 1. Điền vào ô trống (hợp lệ)
        // Ô (0, 2) ban đầu trống (giá trị 0), là ô sửa được
        assertTrue(gameSession.isEditableCell(0, 2));
        assertTrue(gameSession.makeMove(0, 2, 4));
        
        // Kiểm tra bàn cờ hiện tại đã cập nhật
        assertEquals(4, gameSession.getCurrentBoard().getCell(0, 2));
        // Kiểm tra lịch sử lưu nước đi
        assertEquals(1, gameSession.getHistory().size());
        Step lastStep = gameSession.getHistory().get(0);
        assertEquals(0, lastStep.getRow());
        assertEquals(2, lastStep.getCol());
        assertEquals(0, lastStep.getPrevValue());
        assertEquals(4, lastStep.getValue());
        assertEquals(StepType.INPUT, lastStep.getType());

        // 2. Điền đè giá trị mới lên ô vừa điền
        assertTrue(gameSession.makeMove(0, 2, 9));
        assertEquals(9, gameSession.getCurrentBoard().getCell(0, 2));
        assertEquals(2, gameSession.getHistory().size());

        // 3. Điền vào ô không thể sửa (không hợp lệ)
        // Ô (0, 0) ban đầu có giá trị 5, không thể sửa
        assertFalse(gameSession.isEditableCell(0, 0));
        assertFalse(gameSession.makeMove(0, 0, 9));
        // Giá trị ô đó vẫn giữ nguyên là 5
        assertEquals(5, gameSession.getCurrentBoard().getCell(0, 0));
        // Lịch sử không tăng thêm
        assertEquals(2, gameSession.getHistory().size());
    }

    @Test
    public void testClearCell() {
        // Điền vào ô trống trước
        gameSession.makeMove(0, 2, 4);
        assertEquals(4, gameSession.getCurrentBoard().getCell(0, 2));

        // 1. Xoá ô vừa điền (hợp lệ)
        assertTrue(gameSession.clearCell(0, 2));
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 2)); // Trở lại giá trị trống 0
        
        // Lịch sử lưu lại sự kiện xoá
        assertEquals(2, gameSession.getHistory().size());
        Step lastStep = gameSession.getHistory().get(1);
        assertEquals(StepType.CLEAR, lastStep.getType());
        assertEquals(4, lastStep.getPrevValue());
        assertEquals(0, lastStep.getValue());

        // 2. Xoá ô ban đầu của đề bài (không hợp lệ)
        assertFalse(gameSession.clearCell(0, 0));
        assertEquals(5, gameSession.getCurrentBoard().getCell(0, 0)); // Giá trị vẫn là 5
    }

    @Test
    public void testUndo() {
        // Khi lịch sử trống, undo phải trả về false
        assertFalse(gameSession.undo());

        // Thực hiện 2 nước đi
        gameSession.makeMove(0, 2, 4); // Nước đi 1
        gameSession.makeMove(0, 3, 9); // Nước đi 2

        assertEquals(4, gameSession.getCurrentBoard().getCell(0, 2));
        assertEquals(9, gameSession.getCurrentBoard().getCell(0, 3));
        assertEquals(2, gameSession.getHistory().size());

        // 1. Undo nước đi thứ 2 (ô 0,3)
        assertTrue(gameSession.undo());
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 3)); // Trở về 0
        assertEquals(4, gameSession.getCurrentBoard().getCell(0, 2)); // Ô 0,2 vẫn giữ nguyên 4
        assertEquals(1, gameSession.getHistory().size());

        // 2. Undo nước đi thứ 1 (ô 0,2)
        assertTrue(gameSession.undo());
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 2)); // Trở về 0
        assertTrue(gameSession.getHistory().isEmpty());

        // 3. Undo tiếp khi lịch sử đã hết -> false
        assertFalse(gameSession.undo());
    }

    @Test
    public void testFinish() {
        gameSession.makeMove(0, 2, 4);
        assertFalse(gameSession.isFinished());

        // Kết thúc game
        gameSession.finish();
        assertTrue(gameSession.isFinished());

        // Sau khi finish, mọi hành động thay đổi trạng thái đều bị từ chối
        assertFalse(gameSession.makeMove(0, 3, 9));
        assertFalse(gameSession.clearCell(0, 2));
        assertFalse(gameSession.undo());

        // Bảng và lịch sử không thay đổi
        assertEquals(4, gameSession.getCurrentBoard().getCell(0, 2));
        assertEquals(1, gameSession.getHistory().size());
    }

    @Test
    public void testRestart() {
        // Thực hiện nước đi và kết thúc game
        gameSession.makeMove(0, 2, 4);
        gameSession.makeMove(0, 3, 9);
        gameSession.finish();

        assertTrue(gameSession.isFinished());
        assertEquals(2, gameSession.getHistory().size());

        // Chơi lại
        gameSession.restart();

        // Kiểm tra mọi trạng thái được reset về ban đầu
        assertFalse(gameSession.isFinished());
        assertTrue(gameSession.getHistory().isEmpty());
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 2));
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 3));
        assertEquals(5, gameSession.getCurrentBoard().getCell(0, 0)); // Ô gốc vẫn giữ nguyên
    }

    @Test
    public void testInvalidPosition() {
        // Hàng hoặc cột ngoài khoảng 0-8 phải ném ra IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            gameSession.makeMove(-1, 0, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameSession.makeMove(0, 9, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameSession.clearCell(9, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameSession.isEditableCell(0, -5);
        });
    }

    @Test
    public void testInvalidValue() {
        // Giá trị ngoài khoảng 0-9 trong makeMove phải trả về false (không được áp dụng vào bảng)
        assertFalse(gameSession.makeMove(0, 2, -1));
        assertFalse(gameSession.makeMove(0, 2, 10));

        // Kiểm tra ô đó vẫn giữ nguyên giá trị trống 0 và không có nước đi nào được lưu
        assertEquals(0, gameSession.getCurrentBoard().getCell(0, 2));
        assertTrue(gameSession.getHistory().isEmpty());
    }
}
