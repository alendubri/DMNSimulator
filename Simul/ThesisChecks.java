package Simul;

import binContainer.DmnByte;
import binContainer.Word;

/** Focused checks for the rules stated in DMNSIM.pdf, pages 17-26. */
public final class ThesisChecks {
	private ThesisChecks() {}

	public static void main(String[] args) {
		Simulator logic = program(0xD10F, 0x4210, 0xD381, 0xD401,
			0x5534, 0xD4FF, 0x5634, 0xCD00);
		runUntilHalt(logic);
		check(logic.snapshot().register(2) == 0xF0, "NOT complements all eight bits");
		check(logic.snapshot().register(5) == 0x40, "positive count shifts right");
		check(logic.snapshot().register(6) == 0x02, "negative count shifts left");

		Simulator condition = program(0xD105, 0x9011, 0xAF05, 0xD2AA,
			0xD2AA, 0xCD00);
		runUntilHalt(condition);
		check(condition.snapshot().register(Simulator.CNDBT) == 0xFF,
			"NTS equality is true and JCN preserves CN");
		check(condition.snapshot().register(2) == 0,
			"taken JCN flushes younger instructions");
		Simulator signedCondition = program(0xD1FF, 0xD201, 0x9012, 0xCD00);
		runUntilHalt(signedCondition);
		check(signedCondition.snapshot().register(Simulator.CNDBT) == 0xFF,
			"NTS compares signed operands");

		Simulator boundary = program(0xD180, 0xD200, 0x0312, 0xCD00);
		runUntilHalt(boundary);
		check(boundary.snapshot().register(3) == 0x80,
			"-128 is a representable signed result");

		Simulator illegal = program(0x6300, 0xD1AA);
		installHandler(illegal, 0x20);
		runUntilHalt(illegal);
		check(illegal.alu.PCR.intValue() == 0, "illegal opcode return address");
		check(illegal.snapshot().register(3) == 0, "illegal opcode has no writeback");
		check(illegal.snapshot().register(1) == 0, "younger instruction was flushed");

		Simulator youngerStore = program(0x6300, 0xF012);
		youngerStore.rFile.writeMemory(new DmnByte(4), 1);
		youngerStore.rFile.writeMemory(new DmnByte(0xA5), 2);
		installHandler(youngerStore, 0x20);
		runUntilHalt(youngerStore);
		check(youngerStore.snapshot().data(4) == 0,
			"younger STR does not write memory before fault redirect");

		Simulator illegalRegister = program(0xDC01, 0xD1AA);
		installHandler(illegalRegister, 0x20);
		runUntilHalt(illegalRegister);
		check(illegalRegister.snapshot().register(Simulator.CNDBT) == 0,
			"ordinary instruction cannot write CN");
		check(illegalRegister.alu.PCR.intValue() == 0,
			"illegal register return address");
		Simulator illegalPcSource = program(0x01F0);
		installHandler(illegalPcSource, 0x20);
		runUntilHalt(illegalPcSource);
		check(illegalPcSource.alu.PCR.intValue() == 0,
			"PC cannot be used as an ordinary source register");

		Simulator overflow = program(0xD17F, 0xD201, 0x0312, 0xD4AA);
		installHandler(overflow, 0x20);
		runUntilHalt(overflow);
		check(overflow.alu.PCR.intValue() == 2, "overflow return address");
		check(overflow.snapshot().register(3) == 0, "overflowing ADD has no writeback");
		check(overflow.snapshot().register(4) == 0, "younger instruction was flushed");
		Simulator negativeOverflow = program(0xD180, 0xD201, 0x1312);
		installHandler(negativeOverflow, 0x20);
		runUntilHalt(negativeOverflow);
		check(negativeOverflow.alu.PCR.intValue() == 2,
			"negative SUB overflow is interrupted");
		check(negativeOverflow.snapshot().register(3) == 0,
			"negative overflow does not write back");

		Simulator memory = program(0xD104, 0xD2AA, 0xF012, 0xE310, 0xCD00);
		runUntilHalt(memory);
		check(memory.snapshot().data(4) == 0xAA, "STR still writes data memory");
		check(memory.snapshot().register(3) == 0xAA, "LDR still reads data memory");

		Simulator returnFromInterrupt = program(0xD101, 0xCD00);
		returnFromInterrupt.rFile.writeMemory(new DmnByte(0x20), Simulator.PSREG);
		returnFromInterrupt.iMemory.writeMemory(new Word(0xB000), 0x20);
		returnFromInterrupt.intrAck();
		returnFromInterrupt.step();
		check(returnFromInterrupt.snapshot().pc() == 0x20,
			"external interrupt redirects to PS");
		returnFromInterrupt.intrAck();
		check(!returnFromInterrupt.alu.intr,
			"interrupt requests are masked during service");
		runUntilHalt(returnFromInterrupt);
		check(returnFromInterrupt.snapshot().register(1) == 1,
			"RTI resumes the interrupted program");

		Simulator haltDiagram = program(0xC000);
		runUntilHalt(haltDiagram);
		check(haltDiagram.snapshot().register(Simulator.HAREG) == 0xFF,
			"the thesis HLT encoding reaches HA");

		System.out.println("ThesisChecks passed.");
	}

	private static Simulator program(int... words) {
		Simulator simulator = new Simulator();
		for(int i = 0; i < words.length; i++)
			simulator.iMemory.writeMemory(new Word(words[i]), i);
		return simulator;
	}

	private static void installHandler(Simulator simulator, int address) {
		simulator.rFile.writeMemory(new DmnByte(address), Simulator.PSREG);
		simulator.iMemory.writeMemory(new Word(0xCD00), address);
	}

	private static void runUntilHalt(Simulator simulator) {
		for(int cycle = 0; cycle < 64; cycle++) {
			simulator.step();
			if(simulator.snapshot().halted())
				return;
		}
		throw new AssertionError("Program did not halt within 64 cycles");
	}

	private static void check(boolean condition, String description) {
		if(!condition)
			throw new AssertionError(description);
	}
}
