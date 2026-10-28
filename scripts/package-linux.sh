#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

for tool in git mvn jpackage fakeroot dpkg-deb unzip; do
    command -v "$tool" >/dev/null || { echo "Missing required tool: $tool" >&2; exit 1; }
done

tag="$(git describe --tags --exact-match --match 'v[0-9]*' HEAD 2>/dev/null || true)"
if [[ ! "$tag" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Package only a release commit tagged vX.Y.Z." >&2
    exit 1
fi
if [[ -n "$(git status --porcelain --untracked-files=normal)" ]]; then
    echo "Working-tree changes will be ignored; packaging the tagged commit $tag."
fi

version="${tag#v}"
commit="$(git rev-parse --short=7 HEAD)"
work_root="$(mktemp -d)"
trap 'rm -rf "$work_root"' EXIT
git clone --quiet --no-hardlinks "$repo_root" "$work_root/source"
cd "$work_root/source"
mvn -B -ntp clean package

jar="$work_root/source/target/dmnsimulator-$version.jar"
if [[ ! -f "$jar" ]]; then
    echo "Expected Maven artifact missing: $jar" >&2
    exit 1
fi
metadata="$(unzip -p "$jar" build-version.properties)"
if ! grep -Fxq "version=$version" <<< "$metadata" || ! grep -Fxq "commit=$commit" <<< "$metadata"; then
    echo "JAR version or commit does not match $tag at $commit." >&2
    exit 1
fi

mkdir -p "$repo_root/target" "$work_root/input"
cp "$jar" "$repo_root/target/"
cp "$jar" "$work_root/input/"
mkdir -p "$repo_root/dist" "$work_root/output"
jpackage --type deb --name DMNSimulator --linux-package-name dmnsimulator \
    --app-version "$version" --vendor "Alberto Dubuc" \
    --linux-deb-maintainer "alendubri@yahoo.com" --linux-shortcut \
    --input "$work_root/input" --main-jar "$(basename "$jar")" --main-class SimDMN \
    --dest "$work_root/output"
package="$work_root/output/dmnsimulator_${version}_amd64.deb"
if [[ ! -f "$package" ]]; then
    echo "Expected Linux package missing: $package" >&2
    exit 1
fi
temporary_package="$(mktemp "$repo_root/dist/.dmnsimulator-XXXXXX")"
cp "$package" "$temporary_package"
mv -f "$temporary_package" "$repo_root/dist/$(basename "$package")"
echo "Linux package created in $repo_root/dist"
