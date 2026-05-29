# 2526II-sudoku

## Cấu trúc dự án
```text
project/
├── pom.xml                                 <-- File cấu hình Maven (quản lý thư viện JavaFX)
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/                           <-- Nơi chứa toàn bộ mã nguồn Java
    │   │   └── com/
    │   │       └── sudoku/                 <-- Thư mục gốc của package dự án
    │   │           ├── Main.java           <-- File khởi chạy chính (kế thừa javafx.application.Application)
    │   │           ├── module-info.java    <-- Cấu hình Module cho JavaFX (bắt buộc từ Java 9+)
    │   │           │
    │   │           ├── model/
    │   │           │   ├── Board.java
    │   │           │   ├── Step.java
    │   │           │   ├── GameSession.java
    │   │           │   ├── Difficulty.java
    │   │           │   └── StepType.java
    │   │           │
    │   │           ├── validator/
    │   │           │   └── BoardValidator.java
    │   │           │
    │   │           ├── generator/
    │   │           │   └── BoardGenerator.java
    │   │           │
    │   │           ├── solver/
    │   │           │   ├── Solver.java
    │   │           │   ├── SolveResult.java
    │   │           │   ├── BacktrackingSolver.java
    │   │           │   ├── MRVSolver.java
    │   │           │   └── SASolver.java
    │   │           │
    │   │           └── ui/
    │   │               ├── GameController.java <-- Controller điều khiển giao diện
    │   │               └── ...
    │   │
    │   └── resources/                      <-- Nơi thay thế cho thư mục "assets" cũ của bạn
    │       └── com/
    │           └── sudoku/                 <-- Tạo thư mục trùng tên package để JavaFX load file dễ hơn
    │               ├── fxml/
    │               │   └── main-view.fxml     <-- File thiết kế giao diện bằng Scene Builder
    │               ├── css/
    │               │   └── styles.css         <-- File làm đẹp giao diện game
    │               └── images/
    │                   └── logo.png           <-- Icon, hình nền game (nếu có)
    └── test/                               
    └── java/                           <-- Nơi viết các hàm Test cho Solver/Validator sau này
```
