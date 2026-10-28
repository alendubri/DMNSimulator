package Simul;

import java.util.Arrays;

public final class ExecutionSnapshot {
	private final long cycle;
	private final int fetchAddress, instruction, pc;
	private final int[] registers, data, execute, writeback;
	private final boolean[] executeValid, writebackValid;
	private final boolean forwardA1, forwardB1, forwardA2, forwardB2;
	private final boolean flush, memoryWrite;
	private final int memoryAddress;

	ExecutionSnapshot(long cycle, int fetchAddress, int instruction, int pc,
		int[] registers, int[] data, int[] execute, int[] writeback,
		boolean[] executeValid, boolean[] writebackValid,
		boolean forwardA1, boolean forwardB1, boolean forwardA2, boolean forwardB2,
		boolean flush, boolean memoryWrite, int memoryAddress) {
		this.cycle = cycle;
		this.fetchAddress = fetchAddress;
		this.instruction = instruction;
		this.pc = pc;
		this.registers = registers.clone();
		this.data = data.clone();
		this.execute = execute.clone();
		this.writeback = writeback.clone();
		this.executeValid = executeValid.clone();
		this.writebackValid = writebackValid.clone();
		this.forwardA1 = forwardA1;
		this.forwardB1 = forwardB1;
		this.forwardA2 = forwardA2;
		this.forwardB2 = forwardB2;
		this.flush = flush;
		this.memoryWrite = memoryWrite;
		this.memoryAddress = memoryAddress;
	}

	public long cycle() { return cycle; }
	public int fetchAddress() { return fetchAddress; }
	public int instruction() { return instruction; }
	public int pc() { return pc; }
	public int register(int index) { return registers[index]; }
	public int data(int address) { return data[address]; }
	public int[] registers() { return registers.clone(); }
	public int[] data() { return data.clone(); }
	public int[] execute() { return execute.clone(); }
	public int[] writeback() { return writeback.clone(); }
	public boolean[] executeValid() { return executeValid.clone(); }
	public boolean[] writebackValid() { return writebackValid.clone(); }
	public boolean forwardA1() { return forwardA1; }
	public boolean forwardB1() { return forwardB1; }
	public boolean forwardA2() { return forwardA2; }
	public boolean forwardB2() { return forwardB2; }
	public boolean flush() { return flush; }
	public boolean memoryWrite() { return memoryWrite; }
	public int memoryAddress() { return memoryAddress; }
	public boolean halted() { return registers[Simulator.HAREG] == 255; }

	public String toString() {
		return "cycle=" + cycle + " fetch=" + String.format("%02X", fetchAddress)
			+ " word=" + String.format("%04X", instruction)
			+ " pc=" + String.format("%02X", pc)
			+ " ex=" + Arrays.toString(execute)
			+ " exValid=" + Arrays.toString(executeValid)
			+ " wb=" + Arrays.toString(writeback)
			+ " wbValid=" + Arrays.toString(writebackValid)
			+ " forwarding=[A1=" + forwardA1 + ",B1=" + forwardB1
			+ ",A2=" + forwardA2 + ",B2=" + forwardB2 + "]"
			+ " flush=" + flush + " halted=" + halted();
	}
}
