#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/build/classes"
MAIN_CLASS="SimDMN"

usage() {
	cat <<'EOF'
Usage: ./run-dmnsim.sh [option]

Options:
  --build-only   Compile the project but do not start the simulator.
  --clean        Remove the build directory before compiling.
  -h, --help     Show this help message.

This script builds the project with the unified Makefile and runs the
desktop simulator entry point on the current JDK.
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

if ! command -v make >/dev/null 2>&1; then
	printf '%s\n' 'Error: make is not installed or not available in PATH.' >&2
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

make compile

if [[ "${build_only}" == "true" ]]; then
	printf '%s\n' "Build completed: ${BUILD_DIR}"
	exit 0
fi

exec java -cp "${BUILD_DIR}" "${MAIN_CLASS}"
