# Changelog

## 2026-04-27

- Updated the `Help -> SimDMN Help Page ...` action to open the project GitHub page instead of the obsolete external page.
- Fixed a simulator pipeline/control-flow bug that affected loop execution after `NTS`/`JCN`.
  - `DependResolver` now always performs the base register reads for operands, and only suppresses forwarding hazards from `R0` instead of skipping resolution entirely.
  - the simulator now tracks valid EX/WB pipeline slots explicitly so flushed slots behave like bubbles instead of accidental opcode `ADD`
  - branch flush now clears the transient ALU-side pipeline state that could survive into the next loop iteration
  - the delayed selector/immediate queue used by `LDI` and `JCN` is still advanced correctly through bubble cycles
- Added `Simul/LoopRegression.java` to validate the loop regression case that previously corrupted `R2`.
- Expanded the `Simulate` menu with configurable execution speed options.
- Adjusted the speed options to the current slower-playback set:
  - `500 ms / step`
  - `1000 ms / step`
  - `2000 ms / step`
  - `3000 ms / step`
- Added binary export support to the assembler, using the same 16-bit word format already accepted by `Open DMN Binary ...`.
- Expanded the post-assembly confirmation dialog to allow:
  - save to instruction memory
  - save assembled binary to file
  - save both
- Changed instruction-memory startup initialization so the simulator loads `dmn.bin` automatically if it exists, and falls back to the built-in default program otherwise.
- Improved binary export naming so that when a source file is opened with `Open DMN Source ...`, the default output name uses the same base filename with `.bin` extension.
