# 🧩 2526II-Sudoku

<div align="center">

![Java](https://img.shields.io/badge/Java-24-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.x-red?style=for-the-badge&logo=apachemaven&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows%20|%20macOS%20|%20Linux-lightgrey?style=for-the-badge)

**Hệ thống Trò chơi và Phòng thí nghiệm Thuật toán Sudoku**

*Xây dựng bằng Java + JavaFX · Nhóm sinh viên lớp 2526II*

</div>

---

## 📸 Giao diện ứng dụng

<div align="center">

**PLAY Mode**

![Play Mode](docs/screenshot-play.png)

*Chơi trực tiếp, nhập số, gợi ý*

---

**SOLVE Mode**

![Solve Mode](docs/screenshot-solve.png)

*Trực quan hóa từng bước giải*

---

**COMPARE Mode**

![Compare Mode](docs/screenshot-compare.png)

*So sánh hiệu năng 3 thuật toán*
</div>

> **Giao diện nổi bật:** Theme tối (Dark) với hiệu ứng phát sáng Neon xanh, chia 3 tab rõ ràng: **PLAY · SOLVE · COMPARE**

---

## 📑 Mục lục

1. [Thành viên dự án](#-thành-viên-dự-án)
2. [Luật chơi Sudoku](#-luật-chơi-sudoku)
3. [Tính năng nổi bật](#-tính-năng-nổi-bật)
4. [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
5. [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống-mvc--layered)
6. [Phân tích Giải thuật chuyên sâu](#-phân-tích-giải-thuật-chuyên-sâu)
7. [Hệ thống Benchmark](#-hệ-thống-benchmark)
8. [I/O Puzzle](#-io-puzzle)
9. [Môi trường và Cài đặt](#-môi-trường-và-cài-đặt)
10. [Hướng dẫn sử dụng](#-hướng-dẫn-sử-dụng)

---

## 👥 Thành viên dự án

Dự án được nghiên cứu và phát triển bởi nhóm sinh viên lớp **2526II**:

| STT | Họ và tên | MSSV |
|:---:|:---|:---:|
| 1 | **Nguyễn Công Anh** | 24022254 |
| 2 | **Nguyễn Hoàng Công** | 24022272 |
| 3 | **Nguyễn Hữu Hòa** | 24022333 |
| 4 | **Phạm Gia Hồ Huy** | 24022357 |
| 5 | **Nguyễn Minh Huy** | 24022356 |
| 6 | **Nguyễn Minh Khoa** | 24022368 |

---

## 🎮 Luật chơi Sudoku

Bàn cờ 9x9 được chia thành 9 vùng con 3x3. Người chơi cần điền các số từ **1 đến 9** vào các ô trống sao cho thỏa mãn **đồng thời** 3 ràng buộc:

| Ràng buộc | Mô tả |
|:---|:---|
| **Hàng ngang** | Mỗi hàng chứa đủ 9 số, không trùng lặp |
| **Cột dọc** | Mỗi cột chứa đủ 9 số, không trùng lặp |
| **Vùng 3x3** | Mỗi ô vuông 3x3 chứa đủ 9 số, không trùng lặp |

> Mỗi đề bài được sinh ra đều được đảm bảo **chỉ có duy nhất một lời giải đúng**.

---

## 🌟 Tính năng nổi bật

### Tab PLAY — Chơi trực tiếp

- **Sinh đề tự động** theo 4 cấp độ: `Easy` · `Medium` · `Hard` · `Expert`
- **Bộ đếm thời gian** hiển thị realtime
- **Theo dõi tiến độ:** Mistakes · Hints used · Filled (ví dụ: 41/81)
- **Cảnh báo lỗi** tức thì khi nhập số vi phạm luật
- **Hint:** Gợi ý 1 ô chính xác
- **Undo** không giới hạn số bước
- **Check Solution:** Kiểm tra đáp án
- **Import / Export** puzzle ra file `.txt`
- **High Scores:** Bảng xếp hạng điểm cao

### Tab SOLVE — Trực quan hóa giải thuật

- Chọn thuật toán: `Backtracking` · `MRV Heuristic` · `Simulated Annealing`
- **Trực quan hóa từng bước** giải trên bàn cờ
- **Step Log Panel:** Console log từng bước (Algorithm ready, Switched to SOLVE mode,...)
- **Statistics realtime:** Steps · Time (s) · Backtracks · Filled
- **Thanh tiến trình** hiển thị mức độ hoàn thành
- **Điều khiển animation:** Play / Pause / Step · Tốc độ từ Slow → Fast

### Tab COMPARE — So sánh hiệu năng

- Tick chọn thuật toán muốn so sánh (Backtracking · MRV Heuristic · Simulated Annealing)
- **Run Comparison:** Chạy tất cả thuật toán trên cùng 1 bàn cờ
- Bảng kết quả: Algorithm · Time (ms)
- **Relative Speed:** Biểu đồ tốc độ tương đối

---

## 📁 Cấu trúc thư mục

```
2526II-Sudoku/
├── pom.xml                                      <- Cấu hình Maven & JavaFX
├── README.md
├── .gitignore
└── sudoku/
    └── src/
        ├── main/
        │   ├── java/com/sudoku/
        │   │   ├── Main.java                    <- Entry point (extends Application)
        │   │   ├── SudokuApp.java               <- Khởi tạo Scene & Stage chính
        │   │   │
        │   │   ├── model/                       <- Tầng dữ liệu
        │   │   │   ├── Board.java               <- Ma trận 9x9 + logic sao chép
        │   │   │   ├── Difficulty.java          <- Enum: EASY, MEDIUM, HARD, EXPERT
        │   │   │   ├── GameSession.java         <- Trạng thái phiên chơi hiện tại
        │   │   │   ├── Step.java                <- Một bước thao tác (ô, giá trị cũ/mới)
        │   │   │   └── StepType.java            <- Enum loại bước: PLACE, ERASE...
        │   │   │
        │   │   ├── validator/                   <- Kiểm tra tính hợp lệ
        │   │   │   └── BoardValidator.java      <- Validate hàng, cột, vùng 3x3
        │   │   │
        │   │   ├── generator/                   <- Sinh đề bài
        │   │   │   └── BoardGenerator.java      <- Sinh ngẫu nhiên, đảm bảo 1 nghiệm
        │   │   │
        │   │   ├── solver/                      <- Tầng thuật toán
        │   │   │   ├── Solver.java              <- Interface chung
        │   │   │   ├── SolveResult.java         <- Đóng gói kết quả (steps, time, BT)
        │   │   │   ├── BacktrackingSolver.java  <- Thuật toán Quay lui
        │   │   │   ├── MRVSolver.java           <- Thuật toán MRV Heuristic
        │   │   │   └── SASolver.java            <- Simulated Annealing
        │   │   │
        │   │   ├── benchmark/                   <- Tầng đo lường hiệu năng
        │   │   │   ├── AlgorithmComparison.java <- Chạy & so sánh tất cả solver
        │   │   │   ├── AlgorithmStats.java      <- DTO lưu kết quả mỗi thuật toán
        │   │   │   └── BenchmarkMain.java       <- Entry point chạy benchmark CLI
        │   │   │
        │   │   ├── io/                          <- Đọc/Ghi puzzle ra file
        │   │   │   ├── PuzzleFormatException.java <- Exception tuỳ chỉnh
        │   │   │   ├── PuzzleIOService.java     <- Đọc/ghi int[][] thô
        │   │   │   ├── PuzzleReader.java        <- Đọc file -> Board (có validate)
        │   │   │   └── PuzzleWriter.java        <- Ghi Board -> file text
        │   │   │
        │   │   ├── highscore/                   <- Bảng xếp hạng
        │   │   │   └── HighScoreManager.java    <- Lưu & truy xuất điểm cao
        │   │   │
        │   │   └── ui/                          <- Tầng giao diện (Controller)
        │   │       ├── GameController.java      <- Controller trung tâm (MVC)
        │   │       ├── SudokuBoard.java         <- Component bàn cờ 9x9
        │   │       ├── SudokuCell.java          <- Component từng ô đơn lẻ
        │   │       ├── CellState.java           <- Trạng thái hiển thị của ô
        │   │       ├── ControlPanel.java        <- Panel điều khiển (New Game, Solve...)
        │   │       ├── DifficultySelector.java  <- Chọn độ khó
        │   │       ├── AlgorithmSelector.java   <- Chọn thuật toán giải
        │   │       ├── StatsPanel.java          <- Hiển thị bảng thống kê benchmark
        │   │       └── StepLogPanel.java        <- Hiển thị log từng bước giải
        │   │
        │   └── resources/com/sudoku/
        │       ├── fxml/main-view.fxml          <- Layout thiết kế bằng Scene Builder
        │       ├── css/styles.css               <- Stylesheet (Dark Neon theme)
        │       └── images/logo.png
        │
        └── test/java/                           <- Unit Tests
```

---

## 🏛 Kiến trúc hệ thống (MVC + Layered)

Dự án kết hợp mô hình **MVC** với kiến trúc **phân lớp** rõ ràng:

```
+-----------------------------------------------------+
|               TANG GIAO DIEN (View)                 |
|   SudokuBoard   SudokuCell   ControlPanel           |
|   DifficultySelector   AlgorithmSelector            |
|   StatsPanel   StepLogPanel                         |
+-----------------------------------------------------+
|             TANG DIEU PHOI (Controller)              |
|                 GameController.java                 |
+----------------------+------------------------------+
|   TANG NGHIEP VU     |      TANG GIAI THUAT        |
|  BoardValidator      |  Solver  (interface)        |
|  BoardGenerator      |  BacktrackingSolver         |
|  HighScoreManager    |  MRVSolver                  |
|  PuzzleReader/Writer |  SASolver                   |
+----------------------+------------------------------+
|                TANG DU LIEU (Model)                  |
|       Board   GameSession   Step   Difficulty       |
+-----------------------------------------------------+
|              TANG DO LUONG (Benchmark)               |
|    AlgorithmComparison   AlgorithmStats             |
+-----------------------------------------------------+
```

**Nguyên tắc thiết kế:**

- **`Solver` là Interface** — mọi thuật toán đều triển khai cùng một hợp đồng, dễ dàng thêm mới mà không ảnh hưởng hệ thống *(Open/Closed Principle)*.
- **`SolveResult`** đóng gói toàn bộ kết quả (danh sách bước đi, thời gian nano, số lần quay lui), tách biệt dữ liệu kết quả khỏi logic giải.
- **`AlgorithmComparison`** hoạt động độc lập hoàn toàn với tầng UI — có thể chạy CLI qua `BenchmarkMain`.
- **Tầng `io/`** tách biệt hoàn toàn logic đọc/ghi file, hỗ trợ cả raw `int[][]` và `Board` object có validate.

---

## 🧠 Phân tích Giải thuật chuyên sâu

### 1. Backtracking Solver — Điểm mốc cơ sở

**Cơ chế:** Duyệt theo chiều sâu (DFS). Thử lần lượt các giá trị 1–9 vào ô trống đầu tiên tìm được. Nếu vi phạm ràng buộc → cắt tỉa nhánh (Pruning) và quay lui.

```
Tim o trong dau tien (theo thu tu)
  |__ Thu gia tri 1..9
       |-- Hop le  --> Gan gia tri --> De quy tiep
       |              |__ Giai xong --> Tra ve true
       |-- Vi pham hoac de quy that bai --> Quay lui (backtrack)
```

| Chỉ số | Giá trị |
|:---|:---|
| Độ phức tạp (xấu nhất) | O(9^m) với m = số ô trống |
| Vai trò | Baseline để đối chiếu |
| Ưu điểm | Đơn giản, luôn tìm được nghiệm nếu tồn tại |
| Nhược điểm | Chậm với đề khó, khám phá nhiều nhánh thừa |

---

### 2. MRV Solver — Heuristic "Chọn ô khó nhất trước"

**Cơ chế:** Cải tiến Backtracking bằng chiến lược **Minimum Remaining Values**. Thay vì chọn ô trống theo thứ tự, luôn ưu tiên ô có **ít lựa chọn hợp lệ nhất** (domain nhỏ nhất).

```
Quet toan bo o trong --> Tinh domain (tap so hop le) cho moi o
  |__ Chon o co |domain| nho nhat
       |-- |domain| = 0 --> Be tac --> Quay lui ngay (cat tia som)
       |-- |domain| = 1 --> Dien chac chan --> Tiet kiem de quy
       |-- |domain| > 1 --> Thu tung gia tri trong domain
```

| Chỉ số | So sánh với Backtracking |
|:---|:---|
| Số nhánh cần khám phá | Giảm đáng kể |
| Phát hiện bế tắc | Sớm hơn nhiều |
| Phù hợp nhất | Đề `Hard`, `Expert` với nhiều ràng buộc chặt |

---

### 3. Simulated Annealing (SA) — Luyện kim mô phỏng

**Cơ chế:** Meta-heuristic lấy ý tưởng từ quá trình làm nguội kim loại. Chấp nhận nghiệm xấu hơn có xác suất nhất định để **thoát khỏi bẫy cực tiểu cục bộ**.

```
Khoi tao: Dien ngau nhien cac o trong trong moi vung 3x3
  |
  |__ Lap theo nhiet do T (giam dan):
       |-- Hoan doi ngau nhien 2 o trong cung vung 3x3
       |-- Tinh nang luong E = tong so vi pham (hang + cot)
       |-- Delta_E = E_moi - E_cu
       |    |-- Delta_E < 0  --> Chap nhan (tot hon)
       |    |-- Delta_E >= 0 --> Chap nhan voi xac suat P = exp(-Delta_E / T)
       |__ Giam nhiet do: T = T x alpha  (alpha ~ 0.99)
```

**Xác suất chấp nhận nghiệm xấu:**

```
P = exp( -Delta_E / T )
```

Khi T cao → P lớn → dễ chấp nhận nghiệm xấu (khám phá rộng).
Khi T thấp → P nhỏ → chọn lọc khắt khe hơn (khai thác nghiệm tốt).

| Thông số | Ý nghĩa |
|:---|:---|
| `T` — Nhiệt độ | Cao = chấp nhận xấu nhiều; Thấp = chọn lọc hơn |
| `alpha` — Cooling rate | Tốc độ giảm nhiệt, thường 0.99 – 0.999 |
| `E` — Năng lượng | Tổng lỗi vi phạm; E = 0 là bàn cờ đã giải xong |

> **Đặc điểm:** SA không đảm bảo luôn tìm được nghiệm *(non-deterministic)*, nhưng hiệu quả trên không gian tìm kiếm rộng và phù hợp nghiên cứu bài toán tối ưu hóa tổ hợp.

---

## 📊 Hệ thống Benchmark

Module `benchmark/` cho phép so sánh hiệu năng 3 thuật toán trên cùng một đề bài.

### Cách hoạt động

```java
// AlgorithmComparison chạy tuần tự từng solver trên bản sao của board
AlgorithmComparison comparison = new AlgorithmComparison();
List<AlgorithmStats> results = comparison.compare(board);
// Kết quả được xếp hạng tự động theo elapsedTimeNanos tăng dần
// -> best = #1, secondBest = #2
```

### Dữ liệu thu thập mỗi lần chạy (`AlgorithmStats`)

| Trường | Kiểu | Mô tả |
|:---|:---|:---|
| `algorithmName` | `String` | Tên thuật toán |
| `solved` | `boolean` | Có giải được không |
| `elapsedTimeNanos` | `long` | Thời gian thực thi (nano giây) |
| `stepCount` | `int` | Tổng số bước thao tác |
| `backtracks` | `int` | Số lần quay lui |
| `initialEmptyCells` | `int` | Số ô trống ban đầu |
| `best` / `secondBest` | `boolean` | Xếp hạng #1 / #2 |

### Ví dụ đầu ra (CLI)

```
Algorithm              Solved     Time(ms)     Steps   Backtracks   EmptyCells    Ranking Error
-----------------------------------------------------------------------------------------------
Backtracking           true          1.823        243           87           45          2 -
MRV                    true          0.612        198           21           45          1 -
Simulated Annealing    true         12.445       5832            0           45          3 -
```

### Chạy Benchmark độc lập (không cần giao diện)

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.sudoku.benchmark.BenchmarkMain"
```

---

## 💾 I/O Puzzle

Package `io/` cung cấp đầy đủ hỗ trợ đọc/ghi puzzle ra file văn bản.

### Định dạng file `.txt`

```
5 3 0 0 7 0 0 0 0
6 0 0 1 9 5 0 0 0
0 9 8 0 0 0 0 6 0
8 0 0 0 6 0 0 0 3
4 0 0 8 0 3 0 0 1
7 0 0 0 2 0 0 0 6
0 6 0 0 0 0 2 8 0
0 0 0 4 1 9 0 0 5
0 0 0 0 8 0 0 7 9
```

> Số `0` = ô trống. Mỗi dòng là một hàng, các số cách nhau bằng dấu cách.

### Các lớp trong package `io/`

| Lớp | Chức năng |
|:---|:---|
| `PuzzleIOService` | Đọc/ghi `int[][]` thô — nhanh, linh hoạt, không validate |
| `PuzzleReader` | Đọc file → `Board` object, **tự động validate** Sudoku rules |
| `PuzzleWriter` | Ghi `Board` object → file text chuẩn |
| `PuzzleFormatException` | Exception tùy chỉnh cho lỗi định dạng file |

```java
// Doc puzzle tu file (co validate tu dong)
PuzzleReader reader = new PuzzleReader();
Board board = reader.read("puzzle.txt");
// Nem PuzzleFormatException neu sai dinh dang hoac vi pham luat Sudoku

// Ghi puzzle ra file
PuzzleWriter writer = new PuzzleWriter();
writer.write(board, "output.txt");

// Tren giao dien: nut IMPORT va EXPORT trong tab PLAY
```

---

## ⚙️ Môi trường và Cài đặt

### Yêu cầu tiên quyết

| Công cụ | Phiên bản tối thiểu | Ghi chú |
|:---|:---|:---|
| **JDK** | 11+ | Khuyến nghị Java 24 |
| **Apache Maven** | 3.6+ | Quản lý thư viện & build |
| **JavaFX** | 21 | Tự động tải qua Maven |
| **OS** | Windows / macOS / Linux | Đa nền tảng |

### Cài đặt và chạy

```bash
# 1. Clone repository
git clone https://github.com/hoangcongtbinh/Sudoku.git
cd Sudoku

# 2. Tải thư viện và biên dịch
mvn clean compile

# 3. Mở giao diện trò chơi
mvn javafx:run

# 4. Chạy Unit Test
mvn test

# 5. Chạy Benchmark CLI (khong can giao dien)
mvn exec:java -Dexec.mainClass="com.sudoku.benchmark.BenchmarkMain"
```

---

## 🕹 Hướng dẫn sử dụng

### Tab PLAY — Chơi game

| Thao tác | Hành động |
|:---|:---|
| **NEW GAME** | Tạo đề mới, chọn độ khó từ dropdown |
| Click ô | Chọn ô muốn điền |
| Nhấn `1`–`9` hoặc nút Input | Điền số vào ô đang chọn |
| **ERASE** | Xóa số trong ô đang chọn |
| **HINT** | Gợi ý 1 ô chính xác |
| **UNDO** | Hoàn tác thao tác vừa thực hiện |
| **CHECK SOLUTION** | Kiểm tra toàn bộ đáp án |
| **IMPORT / EXPORT** | Tải puzzle từ file / Lưu puzzle ra file |
| **HIGH SCORES** | Xem bảng xếp hạng |

### Tab SOLVE — Giải tự động

1. Chọn thuật toán từ dropdown (**Backtracking / MRV Heuristic / Simulated Annealing**)
2. Nhấn **SOLVE THIS BOARD**
3. Quan sát bàn cờ được giải từng bước kèm thống kê realtime
4. Dùng nút `<<` `|>` `>` để điều khiển animation, kéo thanh **Speed** để chỉnh tốc độ

### Tab COMPARE — So sánh thuật toán

1. Tick chọn thuật toán muốn so sánh
2. Nhấn **RUN COMPARISON**
3. Xem kết quả tại bảng **RESULTS** (Algorithm · Time ms) và biểu đồ **RELATIVE SPEED**

---

## 🛠 Công nghệ sử dụng

| Công nghệ | Mục đích |
|:---|:---|
| **Java 24** | Ngôn ngữ lập trình chính |
| **JavaFX 21** | Thư viện đồ họa giao diện (Dark Neon theme) |
| **Apache Maven** | Quản lý thư viện, build & test |
| **JUnit** | Unit Testing các thuật toán & validator |
| **Scene Builder** | Thiết kế layout FXML trực quan |

---

## 📜 License

Dự án được phát hành theo giấy phép **MIT License**.

---

<div align="center">

Made with ❤️ by **Nhom 2526II** · Viện Trí Tuệ Nhân Tạo

</div>
