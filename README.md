# WordCountPDF

PDF word counter for Vietnamese documents, built as a small Java Swing desktop app with PDFBox and VietTokenizer.

## English

### What this project does
- Opens a PDF file from the desktop UI
- Extracts text with PDFBox
- Tokenizes Vietnamese text with VietTokenizer
- Removes stop words from `resources/StopWords.txt`
- Shows the original text, tokenized text, stop words found in the PDF, and the final word count

### Project layout
- `src/` - Java source files, no package declarations
- `libs/` - vendored JAR dependencies
- `models/` - VietTokenizer model files
- `resources/StopWords.txt` - required local stop-word file
- `results/` - generated intermediate text files after a successful run

### Requirements
- Java installed and available on `PATH`
- Run everything from the **repository root**
- This repo does **not** use Maven or Gradle. Build and test with plain `javac` / `java` commands.
- Required runtime assets:
  - `tokenizer.properties`
  - `models/`
  - `resources/StopWords.txt`

### Run the app
Compile:

```bash
tmpdir=$(mktemp -d) && javac -cp "$(printf ':%s' libs/*.jar | cut -c2-)" -d "$tmpdir" src/*.java
```

Run with the existing output layout:

```bash
java -cp "out/production/WordCountPDF:$(printf ':%s' libs/*.jar | cut -c2-)" WordCount
```

Shortcut script:

```bash
./build.sh compile
./build.sh run
```

Build a thin jar and run it:

```bash
./build.sh jar
./build.sh jar-run
```

### Run tests

```bash
tmpdir=$(mktemp -d) && javac -cp "$(printf ':%s' libs/*.jar | cut -c2-)" -d "$tmpdir" src/*.java test/*.java && java -cp "$tmpdir:$(printf ':%s' libs/*.jar | cut -c2-)" org.junit.runner.JUnitCore ModelBehaviorTest ControllerWorkerSmokeTest
```

Or use:

```bash
./build.sh test
```

### Runtime notes
- The app creates `results/` on the first successful run if it does not exist.
- The app must be launched from the repo root because it uses relative paths.
- GUI verification needs a real desktop session. In a headless environment, Swing startup fails with `java.awt.HeadlessException`.

## Tiếng Việt

### Dự án này dùng để làm gì
- Mở file PDF từ giao diện desktop
- Trích xuất văn bản bằng PDFBox
- Tách từ tiếng Việt bằng VietTokenizer
- Loại bỏ từ dừng từ `resources/StopWords.txt`
- Hiển thị văn bản gốc, văn bản đã tách từ, danh sách từ dừng xuất hiện trong PDF và kết quả đếm từ

### Cấu trúc thư mục
- `src/` - mã nguồn Java, không dùng package
- `libs/` - các thư viện JAR được vendored sẵn
- `models/` - file model của VietTokenizer
- `resources/StopWords.txt` - file từ dừng bắt buộc phải có
- `results/` - các file trung gian được tạo ra sau khi chạy thành công

### Yêu cầu
- Máy có Java và chạy được từ `PATH`
- Luôn chạy từ **thư mục gốc của repo**
- Repo này **không** dùng Maven hoặc Gradle. Hãy build và test bằng các lệnh `javac` / `java` trực tiếp.
- Các tài nguyên runtime bắt buộc:
  - `tokenizer.properties`
  - `models/`
  - `resources/StopWords.txt`

### Chạy ứng dụng
Biên dịch:

```bash
tmpdir=$(mktemp -d) && javac -cp "$(printf ':%s' libs/*.jar | cut -c2-)" -d "$tmpdir" src/*.java
```

Chạy ứng dụng theo layout output hiện tại:

```bash
java -cp "out/production/WordCountPDF:$(printf ':%s' libs/*.jar | cut -c2-)" WordCount
```

Script rút gọn:

```bash
./build.sh compile
./build.sh run
```

Build jar mỏng rồi chạy:

```bash
./build.sh jar
./build.sh jar-run
```

### Chạy test

```bash
tmpdir=$(mktemp -d) && javac -cp "$(printf ':%s' libs/*.jar | cut -c2-)" -d "$tmpdir" src/*.java test/*.java && java -cp "$tmpdir:$(printf ':%s' libs/*.jar | cut -c2-)" org.junit.runner.JUnitCore ModelBehaviorTest ControllerWorkerSmokeTest
```

Hoặc dùng:

```bash
./build.sh test
```

### Ghi chú runtime
- Ứng dụng sẽ tự tạo thư mục `results/` sau lần chạy thành công đầu tiên nếu thư mục chưa tồn tại.
- Ứng dụng phải được chạy từ thư mục gốc của repo vì đang dùng đường dẫn tương đối.
- Muốn kiểm tra GUI thật thì cần môi trường desktop. Nếu chạy ở môi trường headless, Swing sẽ lỗi `java.awt.HeadlessException`.
