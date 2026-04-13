# DMNSimulator

DMNSimulator is the Java simulator of Professor Dr. Gerard Paez's academic DMN 17N78 computer architecture.

This repository preserves the original 1996 Java 1.0.1 legacy code, including the compiled `.class` files that were distributed with it.

## What Is In This Repository

The active source tree is organized around two top-level launchers and five Java packages:

- `SimDMN.java`: desktop entry point that opens the main simulator window.
- `DMNApplet.java`: applet entry point used by `DMNApplet.html` and `index.html`.
- `Assembler/`: integrated assembler for the DMN instruction set, including label resolution and error reporting.
- `GUI/`: AWT-based user interface, dialogs, menus, memory/register views, pipeline stage panels, and execution controls.
- `Simul/`: simulation core, instruction decoding to mnemonics, ALU behavior, dependency resolution, and execution flow.
- `memory/`: instruction memory, data memory, register file, and common memory abstractions.
- `binContainer/`: low-level `Word`, `Byte`, `Nibble`, and queue classes used by the simulator internals.

Additional repository contents:

- `dmnsrc.asm`: sample DMN assembly source.
- `dmnbin.bin`: sample binary program file, read as raw 16-bit words.
- `00-Doc/DMNSIM.pdf`: original project documentation kept with the legacy snapshot.
- `imag/`, `construt.gif`, `about_javalogo`, `ColPrb.html`, `index.html`, `DMNApplet.html`: historical assets and applet-era HTML pages.

The `RCS/` directories are archival revision files from the original source control workflow. They are not part of the active codebase or Make build and can be ignored.

## What The Simulator Does

The codebase implements a teaching-oriented simulator with:

- a desktop GUI written with AWT,
- an applet wrapper for browser-era deployment,
- an integrated assembler/editor,
- instruction, data, and register memory views,
- execution controls for `Play`, `Pause`, `Stop`, `Step`, and `Reset`,
- pipeline-oriented visual panels and queues,
- data-dependency highlighting,
- interrupt handling support,
- decimal, hexadecimal, binary, and mnemonic views depending on the panel.

The assembler and mnemonic conversion code show support for the following DMN instructions:

`ADD`, `SUB`, `AND`, `OR`, `NOT`, `SHT`, `ZTS`, `NTS`, `JCN`, `RTI`, `HLT`, `LDI`, `LDR`, `STR`

From the current source, the simulator uses:

- 256 instruction-memory locations,
- 256 data-memory locations,
- a 16-entry register file,
- special register labels `CN`, `HA`, `PS`, and `PC`.

## Build Layout

The project uses recursive Makefiles instead of a modern Java build tool.

- Root `Makefile`: builds `SimDMN.class` and `DMNApplet.class`, and delegates to `Simul/`, `Assembler/`, and `GUI/`.
- `Simul/Makefile`: depends on `binContainer/` and `memory/`.
- `memory/Makefile`: builds the memory abstractions and concrete memories.
- `binContainer/Makefile`: builds the binary container and queue classes.
- `Assembler/Makefile`: builds the assembler package.
- `GUI/Makefile`: builds the AWT front end.

Available root targets:

- `make`: build the project using the checked-in dependencies and package Makefiles.
- `make exec`: run the desktop simulator with `java SimDMN`.
- `make appletexec`: open the historical applet page with `appletviewer DMNApplet.html`.
- `make clean`: remove generated `.class` files from the active build directories.

## Notes On Building Today

This is legacy Java 1.0.1 code. The source uses APIs and UI patterns from the mid-1990s, especially AWT and applets.

In the current workspace:

- `make` completes because the repository already includes compiled `.class` files.
- `java` and `javac` are not installed, so a clean rebuild and runtime verification were not possible here.

There is at least one historically compatible runtime option available today: [`YujiSoftware/JDK1.0`](https://github.com/YujiSoftware/JDK1.0), whose README identifies it as a Java 1.0.2 distribution and includes the classic tools such as `java`, `javac`, and `appletviewer`.

From project usage so far:

- the checked-in legacy `.class` files can be executed with that JDK,
- recompiling the source still does not work yet,
- this repository should therefore be treated as a preserved legacy snapshot first, and as a cleanly rebuildable codebase only after additional compatibility debugging.

## Legacy Notes

- The repository intentionally keeps generated `.class` files because they are part of the preserved historical snapshot.
- Some menu items are present only as partial legacy UI features; for example, statistics-related actions are disabled in the menu.
- The HTML files and applet launcher reflect the original deployment model and are included for archival completeness.
