# AGENTS.md

## Repo shape
- Legacy single-module Java Swing app. All application code lives in `src/*.java` with no package declarations.
- This repo does **not** use Maven or Gradle. The executable build metadata is Eclipse-style: `.classpath` and `.project`. IntelliJ metadata also exists in `WordCountPDF.iml`.
- Third-party dependencies are vendored in `libs/`. Do not replace commands with `mvn` or `gradle` guesses.
- Repo-local OpenCode config lives in `.opencode/`; `.opencode/opencode.json` enables the Serena MCP server for this repo.

## Entry points and wiring
- App entrypoint: `src/WordCount.java`. `main()` now boots the Swing UI through `SwingUtilities.invokeLater(...)`, then `launch()` creates `Model`, `View`, and `Controller`, and shows the frame.
- Runtime wiring is simple MVC:
  - `src/View.java` builds the Swing UI and exposes `addActionListener(...)`.
  - `src/Controller.java` wires button/menu actions to the model and view, and now runs PDF processing in a `SwingWorker` so the UI work stays on the EDT.
  - `src/Model.java` does PDF extraction, tokenization, stop-word loading, cleanup, counting, and runtime path validation.
- User flow is file-selection driven: in `Controller.btnChooseActionListener`, approved file selections start background processing, successful runs update the main text area and result field, and failed runs clear stale processing state before showing an error.

## Runtime assets and working-directory assumptions
- Run the app from the **repo root**. Several paths are hard-coded as relative paths:
  - `tokenizer.properties` points to `models/...`
  - `Model` reads `resources/StopWords.txt`
  - `Model` writes intermediate artifacts under `results/`
- `models/` and `tokenizer.properties` are required runtime assets for `VietTokenizer`.
- `resources/` is still a required local runtime directory. `results/` is gitignored but the app now creates it on demand during a successful run.
- `Model.runCountWord()` writes these files during processing:
  - `results/1.OriginalText.txt`
  - `results/2.TokenizedText.txt`
  - `results/3.CleanedText.txt`

## Commands
- Verified compile command from repo root:
  - `tmpdir=$(mktemp -d) && javac -cp "$(printf ':%s' libs/*.jar | cut -c2-)" -d "$tmpdir" src/*.java`
- Run command template from repo root:
  - `java -cp "out/production/WordCountPDF:$(printf ':%s' libs/*.jar | cut -c2-)" WordCount`
- Headless environments cannot fully run the UI. A smoke test with `-Djava.awt.headless=true` reaches `View` creation and then fails with `java.awt.HeadlessException`, so GUI validation requires a desktop session.

## Verification guidance
- There is still no CI workflow in this checkout, but there are now repo-native JUnit 4 regression tests under `test/`.
- For code changes, the safest verification is:
  1. compile from repo root with the vendored JAR classpath,
  2. run `java -cp "$tmpdir:$(printf ':%s' libs/*.jar | cut -c2-)" org.junit.runner.JUnitCore ModelBehaviorTest ControllerWorkerSmokeTest`,
  3. if runtime logic changed, verify with a real desktop session and a sample PDF,
  4. if filesystem/runtime assets changed, confirm `resources/StopWords.txt` exists locally.
- `out/` contains generated class files (`out/production/WordCountPDF`). Treat it as build output, not source.

## Repo-specific gotchas
- `Model` now throws `IOException` for invalid selections and missing runtime assets instead of exiting the JVM, so controller-side error handling is part of the main flow.
- `Controller.createCountWorker(...)` and `WordCount.main()` now rely on Swing thread rules. Keep long-running work in `doInBackground()` and UI mutations in `done()` / EDT code.
- `WordCountPDF.iml` contains some stale absolute JAR references from another machine in addition to repo-local `libs/...` entries. Prefer `.classpath` and the repo-local `libs/` paths as the source of truth.
- `README.md` is now a bilingual English/Vietnamese setup guide. Keep it in sync with runtime requirements and the plain `javac`/`java` workflow.
