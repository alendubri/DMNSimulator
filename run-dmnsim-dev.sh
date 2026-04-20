#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CLASSES_DIR="${SCRIPT_DIR}/build/classes"
MAIN_CLASS="SimDMN"

usage() {
	cat <<'EOF'
Usage: ./run-dmnsim-dev.sh [option]

Options:
  --build-only   Run the strict compile checks but do not start the simulator.
  --clean        Remove the build directory before compiling.
  -h, --help     Show this help message.

This script runs a stricter developer-oriented build before launching the
desktop simulator. The source tree is compiled with deprecation, removal,
and unchecked warnings enabled.
EOF
}

clean_first=false
build_only=false

while [[ $# -gt 0 ]]; do
	case "$1" in
		--build-only)
			build_only=true
			;;
		--clean)
			clean_first=true
			;;
		-h|--help)
			usage
			exit 0
			;;
		*)
			printf 'Unknown option: %s\n\n' "$1" >&2
			usage >&2
			exit 1
			;;
	esac
	shift
done

if ! command -v javac >/dev/null 2>&1; then
	printf '%s\n' 'Error: javac is not installed or not available in PATH.' >&2
	exit 1
fi

if ! command -v java >/dev/null 2>&1; then
	printf '%s\n' 'Error: java is not installed or not available in PATH.' >&2
	exit 1
fi

cd "${SCRIPT_DIR}"

if [[ "${clean_first}" == "true" ]]; then
	make clean
fi

mkdir -p "${CLASSES_DIR}"

javac \
	-Xlint:deprecation \
	-Xlint:removal \
	-Xlint:unchecked \
	-d "${CLASSES_DIR}" \
	$(find . -path './RCS' -prune -o -path '*/RCS' -prune -o -name '*.java' -print)

if [[ "${build_only}" == "true" ]]; then
	printf '%s\n' "Strict build completed: ${CLASSES_DIR}"
	exit 0
fi

exec java -cp "${CLASSES_DIR}" "${MAIN_CLASS}"
