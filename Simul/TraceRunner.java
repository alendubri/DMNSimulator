package Simul;

import Assembler.Assembler;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TraceRunner {
	private TraceRunner() {}

	public static void main(String[] args) throws Exception {
		if(args.length > 2)
			throw new IllegalArgumentException("Usage: TraceRunner [source.asm] [maxCycles]");
		int maxCycles = args.length == 2 ? Integer.parseInt(args[1]) : 256;
		if(maxCycles < 1)
			throw new IllegalArgumentException("maxCycles must be positive");
		Simulator simulator = new Simulator();
		if(args.length > 0) {
			Assembler assembler = new Assembler();
			String source = Files.readString(Path.of(args[0]))
				.replace("\r\n", "\n").replace('\r', '\n');
			if(assembler.assemble(source))
				throw new IllegalArgumentException(String.join("\n", assembler.errMsgArray()));
			assembler.putOnMemory(simulator.iMemory);
		}
		else simulator.iMemory.initialize();
		ExecutionSnapshot previous = simulator.snapshot();
		for(int count = 0; count < maxCycles && !previous.halted(); count++) {
			simulator.step();
			ExecutionSnapshot current = simulator.snapshot();
			System.out.print(current);
			for(int register = 0; register < 16; register++)
				if(current.register(register) != previous.register(register))
					System.out.print(" R" + Integer.toHexString(register).toUpperCase()
						+ "=" + String.format("%02X", current.register(register)));
			if(current.memoryWrite())
				System.out.print(" M[" + String.format("%02X", current.memoryAddress())
					+ "]=" + String.format("%02X", current.data(current.memoryAddress())));
			System.out.println();
			previous = current;
		}
	}
}
