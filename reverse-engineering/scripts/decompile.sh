#!/bin/sh
set -eu

# Ghidra 11.3.2 PUBLIC and a full JDK 21 are required; never a JRE alone.
: "${GHIDRA_HOME:?Set GHIDRA_HOME to the Ghidra 11.3.2 PUBLIC directory}"
: "${JAVA_HOME:?Set JAVA_HOME to a full JDK 21 directory}"
test -x "$JAVA_HOME/bin/javac"
export PATH="$JAVA_HOME/bin:$PATH"
script_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
repo_dir=$(CDPATH= cd -- "$script_dir/../.." && pwd)
python3 "$script_dir/binary_layout.py"
project_dir=$(mktemp -d "${TMPDIR:-/tmp}/pilots-ghidra.XXXXXXXX")
trap 'rm -rf "$project_dir"' EXIT HUP INT TERM

for binary in PILOTS.EXE AUTORUN.EXE GMW.DLL; do
    touch "$project_dir/run-start"
    "$GHIDRA_HOME/support/analyzeHeadless" "$project_dir" pilots-original \
        -import "$repo_dir/$binary" \
        -scriptPath "$script_dir" \
        -postScript ExportDecompilation.py "$repo_dir/reverse-engineering/generated" \
        -analysisTimeoutPerFile 600 -max-cpu 4 -deleteProject
    # Some headless failures are logged without a failing process exit status.
    test -s "$repo_dir/reverse-engineering/generated/$binary/analysis.json"
    test "$repo_dir/reverse-engineering/generated/$binary/analysis.json" -nt "$project_dir/run-start"
done
python3 "$script_dir/validate_exports.py"
