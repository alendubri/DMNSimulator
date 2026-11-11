package Simul;

import Assembler.Assembler;
import binContainer.DmnByte;
import binContainer.Word;
import memory.DataMemory;
import memory.RegFileMemory;

public final class PreservationChecks {
	private PreservationChecks() {}

	public static void main(String[] args) {
		Assembler assembler = new Assembler();
		if(assembler.assemble("ldi r1,#7f\nadd r2,r1,r0\nstr r2,r1\nhlt\n"))
			throw new AssertionError("Assembly failed");
		Simulator simulator = new Simulator();
		assembler.putOnMemory(simulator.iMemory);
		check(simulator.iMemory.peekMemory(0).intValue() == 0xD17F, "LDI encoding");
		check(simulator.iMemory.peekMemory(1).intValue() == 0x0210, "ADD encoding");
		check(simulator.iMemory.peekMemory(2).intValue() == 0xF021, "STR encoding");
		check(simulator.iMemory.peekMemory(3).intValue() == 0xCD00, "HLT encoding");
		Assembler windowsText = new Assembler();
		check(!windowsText.assemble("LDI R1,#71\r\nLDI R2,#AB"), "CRLF editor input");
		Simulator windowsProgram = new Simulator();
		windowsText.putOnMemory(windowsProgram.iMemory);
		check(windowsProgram.iMemory.peekMemory(0).intValue() == 0xD171, "first CRLF instruction");
		check(windowsProgram.iMemory.peekMemory(1).intValue() == 0xD2AB, "second CRLF instruction");

		RegFileMemory registers = new RegFileMemory();
		registers.writeMemory(new DmnByte(99), 0);
		check(registers.readMemory(0).intValue() == 0, "zero register");
		DataMemory data = new DataMemory();
		data.writeMemory(new DmnByte(255), 8);
		check(data.peekMemory(8).intValue() == 255, "data width");
		check(data.getAccessPointer() == 8, "peek leaves pointer unchanged");

		ExecutionSnapshot initial = simulator.snapshot();
		int[] registerCopy = initial.registers();
		registerCopy[1] = 99;
		check(initial.register(1) == 0, "snapshot is immutable");
		for(int count = 0; count < 4; count++) simulator.step();
		check(simulator.snapshot().cycle() == 4, "cycle count");
		simulator.dMemory.writeMemory(new DmnByte(165), 4);
		simulator.reset();
		check(simulator.snapshot().cycle() == 0, "reset cycle count");
		check(simulator.iMemory.peekMemory(0).intValue() == 0xD17F, "reset retains program");
		check(simulator.dMemory.peekMemory(4).intValue() == 165, "reset retains data");
		checkLegacyArchitecturalOutcomes();
		LoopRegression.main(new String[0]);
		System.out.println("PreservationChecks passed.");
	}

	private static Simulator runProgram(int cycles, int... words) {
		Simulator result = new Simulator();
		for(int index = 0; index < words.length; index++)
			result.iMemory.writeMemory(new Word(words[index]), index);
		for(int cycle = 0; cycle < cycles; cycle++) result.step();
		return result;
	}

	private static void checkResult(String instruction, int expected, int... words) {
		Simulator result = runProgram(5, words);
		check(result.rFile.readMemory(3).intValue() == expected,
			instruction + " legacy result in R3");
	}

	private static void checkLegacyArchitecturalOutcomes() {
		checkResult("ADD", 8, 0xD105, 0xD203, 0x0312);
		checkResult("SUB", 2, 0xD105, 0xD203, 0x1312);
		checkResult("AND", 3, 0xD10F, 0xD203, 0x2312);
		checkResult("OR", 15, 0xD10C, 0xD203, 0x3312);
		check(runProgram(5, 0xD10F, 0x4210).rFile.readMemory(2).intValue() == 16,
			"NOT legacy result");
		checkResult("SHT", 4, 0xD102, 0xD201, 0x5312);
		checkResult("SHT with FF count", 0, 0xD102, 0xD2FF, 0x5312);
		for(int reserved : new int[] {0x6300, 0x7300}) {
			Simulator candidate = new Simulator();
			candidate.rFile.writeMemory(new DmnByte(7), 3);
			candidate.iMemory.writeMemory(new Word(reserved), 0);
			for(int cycle = 0; cycle < 3; cycle++) candidate.step();
			check(candidate.rFile.readMemory(3).intValue() == 0,
				"reserved opcode clears destination in legacy");
		}
		check(runProgram(5, 0xD105, 0xD205, 0x8012)
			.rFile.readMemory(Simulator.CNDBT).intValue() == 255, "ZTS equal");
		check(runProgram(5, 0xD105, 0xD205, 0x9012)
			.rFile.readMemory(Simulator.CNDBT).intValue() == 0, "NTS equal is false in legacy");
		check(runProgram(5, 0xD101, 0xD202, 0x9012)
			.rFile.readMemory(Simulator.CNDBT).intValue() == 255, "NTS less is true");
		check(runProgram(4, 0xD104, 0xE210)
			.rFile.readMemory(2).intValue() == 0, "LDR empty data");
		Simulator load = new Simulator();
		load.dMemory.writeMemory(new DmnByte(165), 4);
		load.iMemory.writeMemory(new Word(0xD104), 0);
		load.iMemory.writeMemory(new Word(0xE210), 1);
		for(int cycle = 0; cycle < 4; cycle++) load.step();
		check(load.rFile.readMemory(2).intValue() == 165, "LDR legacy result");
		Simulator store = runProgram(5, 0xD104, 0xD2A5, 0xF012);
		check(store.dMemory.readMemory(4).intValue() == 165, "STR legacy result");
		check(runProgram(3, 0xCD00).rFile.readMemory(Simulator.HAREG).intValue() == 255,
			"HLT reaches HA on cycle 3");
		check(runProgram(3, 0xB000).pcReg.intValue() == 0,
			"RTI redirects to initial PCR on cycle 3");
		checkResult("ADD overflow", 0, 0xD17F, 0xD201, 0x0312);
		checkResult("ADD negative 128", 0, 0xD180, 0xD200, 0x0312);
		Simulator branch = runProgram(6, 0xD101, 0xD201, 0x8012, 0xAF08);
		check(branch.pcReg.intValue() == 8, "taken JCN reaches address 08 on cycle 6");
		Simulator noBranch = runProgram(6, 0xD101, 0xD202, 0x8012, 0xAF08);
		check(noBranch.pcReg.intValue() == 6, "untaken JCN advances on cycle 6");
		Simulator interrupt = new Simulator();
		int[] interruptProgram = {0xDE08, 0xD101, 0xD202, 0x0312, 0, 0, 0, 0, 0xD477, 0xB000};
		for(int index = 0; index < interruptProgram.length; index++)
			interrupt.iMemory.writeMemory(new Word(interruptProgram[index]), index);
		for(int cycle = 0; cycle < 5; cycle++) interrupt.step();
		interrupt.intrAck();
		interrupt.step();
		check(interrupt.pcReg.intValue() == 8, "interrupt vector on cycle 6");
		check(interrupt.alu.PCR.intValue() == 2, "interrupt return address");
		check(interrupt.rFile.readMemory(3).intValue() == 3, "pending writeback before interrupt");
	}

	private static void check(boolean condition, String description) {
		if(!condition) throw new AssertionError(description);
	}
}
