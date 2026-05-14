#!/usr/bin/env bash
# Decompiles EvenBetterChat-1.0.0.jar into src/client/java using cfr.
# Run this once after installing Java 21.
set -e

if ! java -version &>/dev/null 2>&1; then
    echo "Error: Java 21 is not installed."
    echo "Install it with: brew install --cask temurin@21"
    exit 1
fi

CFR_JAR="/tmp/cfr-0.152.jar"
if [ ! -f "$CFR_JAR" ]; then
    echo "Downloading cfr decompiler..."
    curl -sL "https://github.com/leibnitz27/cfr/releases/download/0.152/cfr-0.152.jar" -o "$CFR_JAR"
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/EvenBetterChat-1.0.0.jar"
OUT="$SCRIPT_DIR/src/client/java"

echo "Decompiling $JAR -> $OUT ..."
java -jar "$CFR_JAR" "$JAR" \
    --outputdir "$OUT" \
    --caseinsensitivefs true \
    --comments false

echo ""
echo "Done! Review decompiled files in src/client/java/"
echo "Then: ./gradlew build"
