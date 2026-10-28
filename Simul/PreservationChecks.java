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
		simulator.reset();
		check(simulator.snapshot().cycle() == 0, "reset cycle count");
		check(simulator.iMemory.peekMemory(0).intValue() == 0xD17F, "reset retains program");
		LoopRegression.main(new String[0]);
		System.out.println("PreservationChecks passed.");
	}

	private static void check(boolean condition, String description) {
		if(!condition) throw new AssertionError(description);
	}
}
