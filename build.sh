#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT_DIR="$ROOT/out/production/WordCountPDF"
JAR_DIR="$ROOT/out/jar"
JAR_PATH="$JAR_DIR/WordCountPDF.jar"
MAIN_CLASS="WordCount"

usage() {
  cat <<'EOF'
Usage: ./build.sh <command>

Commands:
  compile   Compile Java sources into out/production/WordCountPDF
  run       Run the desktop app from compiled classes
  jar       Build a thin jar at out/jar/WordCountPDF.jar
  jar-run   Build the thin jar if needed, then run it with libs/*
  test      Compile and run JUnit regression tests
EOF
}

require_dir() {
  local dir="$1"
  local label="$2"
  if [[ ! -d "$dir" ]]; then
    printf 'Missing %s: %s\n' "$label" "$dir" >&2
    exit 1
  fi
}

require_file() {
  local file="$1"
  local label="$2"
  if [[ ! -f "$file" ]]; then
    printf 'Missing %s: %s\n' "$label" "$file" >&2
    exit 1
  fi
}

lib_classpath() {
  local jars=()
  while IFS= read -r jar; do
    jars+=("$jar")
  done < <(find "$ROOT/libs" -maxdepth 1 -name '*.jar' -print | sort)

  if [[ ${#jars[@]} -eq 0 ]]; then
    printf 'No dependency jars found in %s/libs\n' "$ROOT" >&2
    exit 1
  fi

  local cp=""
  local jar
  for jar in "${jars[@]}"; do
    if [[ -z "$cp" ]]; then
      cp="$jar"
    else
      cp="$cp:$jar"
    fi
  done
  printf '%s\n' "$cp"
}

manifest_classpath() {
  local jars=()
  while IFS= read -r jar; do
    jars+=("libs/$(basename "$jar")")
  done < <(find "$ROOT/libs" -maxdepth 1 -name '*.jar' -print | sort)

  printf '%s\n' "${jars[*]}"
}

compile_app() {
  require_dir "$ROOT/libs" "libs directory"
  mkdir -p "$OUT_DIR"
  javac -cp "$(lib_classpath)" -d "$OUT_DIR" "$ROOT"/src/*.java
}

run_app() {
  require_dir "$ROOT/models" "models directory"
  require_file "$ROOT/tokenizer.properties" "tokenizer.properties"
  require_file "$ROOT/resources/StopWords.txt" "stop words file"
  [[ -d "$OUT_DIR" ]] || compile_app
  java -cp "$OUT_DIR:$(lib_classpath)" "$MAIN_CLASS"
}

build_jar() {
  compile_app
  mkdir -p "$JAR_DIR"
  local manifest="$JAR_DIR/manifest.mf"
  cat > "$manifest" <<EOF
Main-Class: $MAIN_CLASS
Class-Path: $(manifest_classpath)
EOF
  jar cfm "$JAR_PATH" "$manifest" -C "$OUT_DIR" .
  printf 'Built %s\n' "$JAR_PATH"
}

run_jar() {
  require_dir "$ROOT/models" "models directory"
  require_file "$ROOT/tokenizer.properties" "tokenizer.properties"
  require_file "$ROOT/resources/StopWords.txt" "stop words file"
  [[ -f "$JAR_PATH" ]] || build_jar
  java -cp "$JAR_PATH:$(lib_classpath)" "$MAIN_CLASS"
}

run_tests() {
  require_dir "$ROOT/libs" "libs directory"
  local tmpdir
  tmpdir="$(mktemp -d)"
  javac -cp "$(lib_classpath)" -d "$tmpdir" "$ROOT"/src/*.java "$ROOT"/test/*.java
  java -cp "$tmpdir:$(lib_classpath)" org.junit.runner.JUnitCore ModelBehaviorTest ControllerWorkerSmokeTest
}

cd "$ROOT"

case "${1:-}" in
  compile)
    compile_app
    ;;
  run)
    run_app
    ;;
  jar)
    build_jar
    ;;
  jar-run)
    run_jar
    ;;
  test)
    run_tests
    ;;
  *)
    usage
    exit 1
    ;;
esac
