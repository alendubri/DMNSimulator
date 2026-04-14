# DMNSimulator

DMNSimulator is the Java simulator of Professor Dr. Gerard Paez's academic DMN 17N78 computer architecture.

This branch is now aimed at building and testing the code on a modern JDK while preserving the historical structure of the project. The original preserved legacy state belongs to the `LegacyCode` branch of this repository.

## Current Status

This branch compiles on OpenJDK 25 in both Linux and Windows environments.

The current modernization work completed so far includes:

- repository-wide rename of the custom `binContainer.Byte` type to `binContainer.DmnByte`,
- fix for the invalid `new MainMenu(super)` constructor call in `GUI/DMNFrame.java`,
- cleanup of several removed or heavily deprecated Java APIs,
- first-phase suppression of the remaining legacy AWT event-model deprecations,
- unification of the build into a single root Makefile that works with the installed modern JDK.

The result is a codebase that can be compiled for testing on a current JDK without preserving the exact historical build mechanics in this branch.

## Repository Layout

The active source tree is organized around two top-level launchers and five Java packages:

- `SimDMN.java`: desktop entry point that opens the main simulator window.
- `DMNApplet.java`: historical applet entry point kept for source compatibility.
- `Assembler/`: integrated assembler for the DMN instruction set, including label resolution and error reporting.
- `GUI/`: AWT-based user interface, dialogs, menus, memory/register views, pipeline stage panels, and execution controls.
- `Simul/`: simulation core, instruction decoding to mnemonics, ALU behavior, dependency resolution, and execution flow.
- `memory/`: instruction memory, data memory, register file, and common memory abstractions.
- `binContainer/`: low-level `Word`, `DmnByte`, `Nibble`, and queue classes used by the simulator internals.

Additional repository contents:

- `dmnsrc.asm`: sample DMN assembly source.
- `dmnbin.bin`: sample binary program file, read as raw 16-bit words.
- `00-Doc/DMNSIM.pdf`: original project documentation.
- `imag/`, `construt.gif`, `about_javalogo`, `ColPrb.html`, `index.html`, `DMNApplet.html`: historical assets and applet-era HTML pages.

The `RCS/` directories are archival revision files from the original source control workflow. They are not part of the active codebase or the modern Make build.

## What The Simulator Does

The codebase implements a teaching-oriented simulator with:

- a desktop GUI written with AWT,
- an integrated assembler/editor,
- instruction, data, and register memory views,
- execution controls for `Play`, `Pause`, `Stop`, `Step`, and `Reset`,
- pipeline-oriented visual panels and queues,
- data-dependency highlighting,
- interrupt handling support,
- decimal, hexadecimal, binary, and mnemonic views depending on the panel.

The assembler and mnemonic conversion code support the following DMN instructions:

`ADD`, `SUB`, `AND`, `OR`, `NOT`, `SHT`, `ZTS`, `NTS`, `JCN`, `RTI`, `HLT`, `LDI`, `LDR`, `STR`

From the current source, the simulator uses:

- 256 instruction-memory locations,
- 256 data-memory locations,
- a 16-entry register file,
- special register labels `CN`, `HA`, `PS`, and `PC`.

## Modern Build

The project now uses a unified root Makefile for the modern JDK workflow.

- Root `Makefile`: the real build entry point. It compiles the full source tree from the repository root into `build/classes`.
- `GUI/Makefile`, `Simul/Makefile`, `Assembler/Makefile`, `memory/Makefile`, `binContainer/Makefile`: compatibility shims that delegate to the root Makefile.

Available root targets:

- `make`: compile the full project into `build/classes`.
- `make exec`: run the desktop simulator with `java -cp build/classes SimDMN`.
- `make clean`: remove the generated `build/` directory.
- `make sources`: print the Java source file list used by the build.
- `make legacy-clean`: remove checked-in and generated `.class` files from the working tree.

Helper script:

- `bash ./run-dmnsim.sh`: compile and run the desktop simulator.
- `bash ./run-dmnsim.sh --build-only`: compile only.
- `bash ./run-dmnsim.sh --clean`: clean first, then compile and run.

Windows helper script:

- `run-dmnsim.bat`: compile and run the desktop simulator from `cmd.exe` or PowerShell.
- `run-dmnsim.bat --build-only`: compile only.
- `run-dmnsim.bat --clean`: clean first, then compile and run.
- these Windows launchers require `java` and `javac` to be installed on the Windows side and available in the Windows `PATH`; a JDK installed only inside WSL is not enough for `cmd.exe` or PowerShell.

Developer helper script:

- `bash ./run-dmnsim-dev.sh`: run a stricter JDK 25 compile with lint warnings enabled, then start the desktop simulator.
- `bash ./run-dmnsim-dev.sh --build-only`: run the strict compile only.
- `bash ./run-dmnsim-dev.sh --clean`: clean first, then run the strict compile and launch.

Windows developer helper script:

- `run-dmnsim-dev.bat`: run a stricter JDK 25 compile with lint warnings enabled, then start the desktop simulator from `cmd.exe` or PowerShell.
- `run-dmnsim-dev.bat --build-only`: run the strict compile only.
- `run-dmnsim-dev.bat --clean`: clean first, then run the strict compile and launch.

Subdirectory usage remains available for convenience:

- `make -C GUI`
- `make -C Simul`
- `make -C Assembler`
- `make -C memory`
- `make -C binContainer`

All of those delegate to the unified root build.

## JDK 25 Notes

This branch has been tested with OpenJDK 25 on Linux and Windows-oriented launch workflows.

Important behavior changes relative to the historical branch:

- the default build no longer writes `.class` files into the source directories,
- the default build no longer depends on the old recursive package compilation order,
- the current source compiles cleanly under `javac` with strict warning flags after the first-phase cleanup,
- the applet source still compiles, but modern OpenJDK no longer includes `appletviewer`.

The `make appletexec` target is therefore intentionally replaced with an informational message rather than a runnable applet command.

Current limitation:

- parts of the GUI still rely on the legacy AWT 1.0 event model (`handleEvent`, `action`, and old mouse callbacks),
- in this first modernization phase those deprecations are handled with targeted `@SuppressWarnings("deprecation")` annotations so behavior remains unchanged for testing,
- a later refactor is expected to replace that logic with listener-based event handling (`ActionListener`, `WindowListener`, `MouseListener`, and related APIs).

## Legacy Reference

The preserved Java 1.0-era state of the project should be considered part of the `LegacyCode` branch.

That legacy branch is the place to look for:

- the original Java 1.0 / Java 1.0.1 codebase as preserved,
- the historical recursive Makefiles,
- the checked-in `.class` files as part of the original distribution model,
- the browser/applet deployment assumptions,
- the original build and runtime expectations.

Historical Java reference:

- the original code dates from 1996 and targets Java 1.0.1-era APIs,
- a historically compatible reference JDK is [`YujiSoftware/JDK1.0`](https://github.com/YujiSoftware/JDK1.0), whose README identifies it as Java 1.0.2,
- that legacy JDK may be useful for historical runtime comparisons, but it is not the target of the modernized build in this branch.
