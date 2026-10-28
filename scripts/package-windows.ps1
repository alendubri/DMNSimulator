$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Push-Location $repoRoot
try {
    if (-not (Get-Command git.exe -ErrorAction SilentlyContinue)) {
        throw 'Git for Windows must be available on PATH.'
    }
    $tags = @(& git.exe tag --points-at HEAD --list 'v*' | Where-Object { $_ -match '^v\d+\.\d+\.\d+$' })
    if ($LASTEXITCODE -ne 0 -or $tags.Count -ne 1) {
        throw 'Package only a release commit tagged vX.Y.Z.'
    }
    $tag = $tags[0]
    if (& git.exe status --porcelain --untracked-files=normal) {
        Write-Host "Working-tree changes will be ignored; packaging the tagged commit $tag."
    }
    $version = $tag.Substring(1)
    $commit = (& git.exe rev-parse --short=7 HEAD).Trim()
    $jarName = "dmnsimulator-$version.jar"
    $jarPath = Join-Path $repoRoot "target\$jarName"
    if (-not (Test-Path $jarPath -PathType Leaf)) {
        throw "Missing $jarName. Run scripts/package-linux.sh in WSL first."
    }

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::OpenRead($jarPath)
    try {
        $entry = $archive.GetEntry('build-version.properties')
        if ($null -eq $entry) { throw 'JAR has no build-version.properties.' }
        $reader = [System.IO.StreamReader]::new($entry.Open())
        try { $metadata = $reader.ReadToEnd() } finally { $reader.Dispose() }
    } finally { $archive.Dispose() }
    if ($metadata -notmatch "(?m)^version=$([regex]::Escape($version))\r?$" -or
        $metadata -notmatch "(?m)^commit=$([regex]::Escape($commit))\r?$") {
        throw "JAR version or commit does not match $tag at $commit."
    }

    $knownJpackage = 'C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin\jpackage.exe'
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\jpackage.exe'))) {
        $jpackage = Join-Path $env:JAVA_HOME 'bin\jpackage.exe'
    } elseif (Test-Path $knownJpackage) {
        $jpackage = $knownJpackage
    } else {
        $jpackageCommand = Get-Command jpackage.exe -ErrorAction SilentlyContinue
        if (-not $jpackageCommand) { throw 'Windows JDK 21+ with jpackage is required.' }
        $jpackage = $jpackageCommand.Source
    }
    $javaMajor = [int]((& $jpackage --version).Trim().Split('.')[0])
    if ($javaMajor -lt 21) { throw 'Use Windows JDK 21 or newer to package this Java 21 application.' }

    $wixBin = 'C:\Program Files (x86)\WiX Toolset v3.11\bin'
    if (-not (Get-Command candle.exe -ErrorAction SilentlyContinue) -and (Test-Path (Join-Path $wixBin 'candle.exe'))) {
        $env:Path = "$wixBin;$env:Path"
    }
    if (-not (Get-Command candle.exe -ErrorAction SilentlyContinue) -or
        -not (Get-Command light.exe -ErrorAction SilentlyContinue)) {
        throw 'WiX Toolset is required to create the Windows EXE installer.'
    }

    $stage = Join-Path $env:TEMP ("dmnsimulator-jpackage-" + [guid]::NewGuid().ToString('N'))
    New-Item -ItemType Directory -Path $stage | Out-Null
    try {
        $inputDir = Join-Path $stage 'input'
        $outputDir = Join-Path $stage 'output'
        New-Item -ItemType Directory -Path $inputDir, $outputDir | Out-Null
        Copy-Item $jarPath (Join-Path $inputDir $jarName)
        $dist = Join-Path $repoRoot 'dist'
        New-Item -ItemType Directory -Path $dist -Force | Out-Null
        $packageArgs = @('--type', 'exe', '--name', 'DMNSimulator', '--app-version', $version,
            '--vendor', 'Alberto Dubuc', '--win-per-user-install', '--win-menu', '--win-shortcut',
            '--input', $inputDir, '--main-jar', $jarName, '--main-class', 'SimDMN', '--dest', $outputDir)
        & $jpackage @packageArgs
        if ($LASTEXITCODE -ne 0) { throw "jpackage failed with exit code $LASTEXITCODE." }
        $installerName = "DMNSimulator-$version.exe"
        $installer = Join-Path $outputDir $installerName
        if (-not (Test-Path $installer -PathType Leaf)) { throw "Expected installer missing: $installer" }
        $temporaryInstaller = Join-Path $dist ('.' + [guid]::NewGuid().ToString('N') + '.exe')
        Copy-Item $installer $temporaryInstaller
        Move-Item $temporaryInstaller (Join-Path $dist $installerName) -Force
        Write-Host "Windows installer created in $dist"
    } finally {
        Remove-Item $stage -Recurse -Force
    }
} finally {
    Pop-Location
}
