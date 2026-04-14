@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
set "CLASSES_DIR=%SCRIPT_DIR%\build\classes"
set "MAIN_CLASS=SimDMN"
set "CLEAN_FIRST=false"
set "BUILD_ONLY=false"

:parse_args
if "%~1"=="" goto args_done
if /I "%~1"=="--build-only" (
	set "BUILD_ONLY=true"
	shift
	goto parse_args
)
if /I "%~1"=="--clean" (
	set "CLEAN_FIRST=true"
	shift
	goto parse_args
)
if /I "%~1"=="-h" goto usage
if /I "%~1"=="--help" goto usage
echo Unknown option: %~1
echo.
goto usage_error

:usage
echo Usage: run-dmnsim-dev.bat [option]
echo.
echo Options:
echo   --build-only   Run the strict compile checks but do not start the simulator.
echo   --clean        Remove the build directory before compiling.
echo   -h, --help     Show this help message.
echo.
echo This script runs a stricter developer-oriented build before launching
echo the desktop simulator. The source tree is compiled with deprecation,
echo removal, and unchecked warnings enabled.
exit /b 0

:usage_error
call :usage >nul
echo Usage: run-dmnsim-dev.bat [option]
echo.
echo Options:
echo   --build-only   Run the strict compile checks but do not start the simulator.
echo   --clean        Remove the build directory before compiling.
echo   -h, --help     Show this help message.
echo.
echo This script runs a stricter developer-oriented build before launching
echo the desktop simulator. The source tree is compiled with deprecation,
echo removal, and unchecked warnings enabled.
exit /b 1

:args_done
where javac >nul 2>nul
if errorlevel 1 (
	echo Error: javac is not installed or not available in PATH.
	exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
	echo Error: java is not installed or not available in PATH.
	exit /b 1
)

cd /d "%SCRIPT_DIR%"

if /I "%CLEAN_FIRST%"=="true" (
	if exist "%SCRIPT_DIR%\build" rmdir /s /q "%SCRIPT_DIR%\build"
)

if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

set "SOURCES_FILE=%TEMP%\dmnsim-sources-%RANDOM%%RANDOM%.txt"
if exist "%SOURCES_FILE%" del /q "%SOURCES_FILE%"

for /r "%SCRIPT_DIR%" %%F in (*.java) do (
	echo %%~fF | findstr /I /C:"\RCS\" >nul
	if errorlevel 1 (
		>>"%SOURCES_FILE%" echo %%~fF
	)
)

javac -Xlint:deprecation -Xlint:removal -Xlint:unchecked -d "%CLASSES_DIR%" @"%SOURCES_FILE%"
if errorlevel 1 goto cleanup_error

if /I "%BUILD_ONLY%"=="true" (
	echo Strict build completed: %CLASSES_DIR%
	goto cleanup_ok
)

java -cp "%CLASSES_DIR%" "%MAIN_CLASS%"
set "EXIT_CODE=%ERRORLEVEL%"
goto cleanup_exit

:cleanup_error
set "EXIT_CODE=1"
goto cleanup_exit

:cleanup_ok
set "EXIT_CODE=0"

:cleanup_exit
if exist "%SOURCES_FILE%" del /q "%SOURCES_FILE%"
exit /b %EXIT_CODE%
