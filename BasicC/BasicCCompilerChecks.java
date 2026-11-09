package BasicC;

import Assembler.Assembler;
import Simul.ExecutionSnapshot;
import Simul.Simulator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Runs generated assembly through the historical assembler and simulator. */
public final class BasicCCompilerChecks {
	private BasicCCompilerChecks() { }

	public static void main(String[] args) throws Exception {
		checkProgram("int main(void) { unsigned char x = 7; unsigned char y = 5;"
			+ " unsigned char z; z = (x + y) & 15; return z; }", 12, 2);
		checkProgram("int main() { unsigned char x = 3; unsigned char y = 0;"
			+ " while (x != 0) { y = y + x; x = x - 1; } return y; }", 6, 1);
		checkProgram("int main() { unsigned char x = 2; unsigned char y = 0;"
			+ " if (x == 2) { y = 9; } else { y = 1; } return y; }", 9, 1);
		checkProgram("int main() { unsigned char x = 3; unsigned char y = 0;"
			+ " if (x == 2) { y = 9; } else { y = 1; } return y; }", 1, 1);
		checkProgram("int main() { unsigned char x = 0; unsigned char y = 2;"
			+ " if (x) { y = 9; } return y; }", 2, 1);
		checkProgram("int main() { unsigned char x = 1; unsigned char y = 2;"
			+ " if (x) { y = 9; } return y; }", 9, 1);
		checkProgram("int main() { unsigned char x = 2; unsigned char y = 3;"
			+ " unsigned char z = 4; unsigned char result;"
			+ " result = (x + y) + (z & 7); return result; }", 9, 3);
		checkProgram("int main() { unsigned char x = 0xAB; unsigned char y;"
			+ " y = x & 0x0F; return y; }", 11, 1);
		checkProgram("int main() { unsigned char _x = 0; unsigned char y;"
			+ " /* signed result -1 is representable */ y = _x - 1;"
			+ " if (y != 0) { y = y | 0; } return y; }", 255, 1);
		ExecutionSnapshot fibonacci = checkProgram(
			Files.readString(Path.of("BasicC/examples/fibonacci_checksum.c")), 54, 6);
		checkData(fibonacci, 0, 0);   // loop count
		checkData(fibonacci, 1, 21);  // previous term
		checkData(fibonacci, 2, 34);  // next current term
		checkData(fibonacci, 4, 10);  // even terms
		checkData(fibonacci, 5, 44);  // odd terms
		ExecutionSnapshot nested = checkProgram(
			Files.readString(Path.of("BasicC/examples/nested_loops.c")), 6, 2);
		checkData(nested, 0, 0);  // outer counter
		checkData(nested, 1, 0);  // inner counter
		checkEmbeddedSource();
		checkFailure("int main() { unsigned char x = 256; }", "Literal must fit one byte");
		checkFailure("int main() { x = 1; }", "Unknown variable");
		checkFailure("int main() { unsigned char x; unsigned char x; }", "Duplicate variable");
		checkFailure("int main() { unsigned char x = x; }", "Unknown variable");
		checkFailure("int main() { unsigned char x; x = 1 * 2; }", "Unsupported character");
		checkFailure("int main() { unsigned char x; return x; x = 1; }",
			"return must be the final statement");
		checkFailure("int main() { unsigned char x; if (x) { return x; } }",
			"return is supported only as the final statement in main");
		StringBuilder large = new StringBuilder("int main() { unsigned char x;");
		for(int index = 0; index < 100; index++) large.append(" x = x + 1;");
		large.append(" }");
		try {
			new BasicCCompiler().compile(large.toString());
			throw new AssertionError("Expected instruction-memory limit");
		} catch(IllegalArgumentException exception) {
			if(!exception.getMessage().contains("256 instruction words"))
				throw new AssertionError(exception);
		}
		System.out.println("BasicCCompilerChecks passed.");
	}

	private static ExecutionSnapshot checkProgram(String source, int expectedReturn, int variableAddress) {
		String assembly = new BasicCCompiler().compile(source);
		Assembler assembler = new Assembler();
		if(assembler.assemble(assembly))
			throw new AssertionError("Assembly failed: " + String.join("; ", assembler.errMsgArray()));
		Simulator simulator = new Simulator();
		assembler.putOnMemory(simulator.iMemory);
		for(int cycle = 0; cycle < 2048 && !simulator.snapshot().halted(); cycle++) simulator.step();
		ExecutionSnapshot snapshot = simulator.snapshot();
		if(!snapshot.halted()) throw new AssertionError("Program failed to halt:\n" + assembly);
		if(snapshot.register(1) != expectedReturn)
			throw new AssertionError("Expected R1=" + expectedReturn + " got " + snapshot.register(1)
				+ " after " + snapshot.cycle() + " cycles:\n" + assembly);
		if(snapshot.data(variableAddress) != expectedReturn)
			throw new AssertionError("Expected M[" + variableAddress + "]=" + expectedReturn
				+ " got " + snapshot.data(variableAddress) + ":\n" + assembly);
		return snapshot;
	}

	private static void checkData(ExecutionSnapshot snapshot, int address, int expected) {
		if(snapshot.data(address) != expected)
			throw new AssertionError("Expected M[" + address + "]=" + expected
				+ " got " + snapshot.data(address));
	}

	private static void checkEmbeddedSource() {
		String source = "// lead\r\nint main(void) {\r\n\r\n"
			+ "    unsigned char x = 7;\r\n    // gap\r\n"
			+ "    return x;\r\n}\r\n";
		String plain = new BasicCCompiler().compile(source);
		String embedded = new BasicCCompiler().compile(source, true);
		if(!embedded.contains("; C line 3: \n; C line 4:     unsigned char x = 7;\n"))
			throw new AssertionError("Embedded C source did not preserve blank lines");
		if(!embedded.contains("; C line 1: // lead\n; C line 2: int main(void) {\n"))
			throw new AssertionError("Embedded C source did not preserve leading comments");
		if(embedded.indexOf("; C line 6:     return x;") < embedded.indexOf("STR R3,R1"))
			throw new AssertionError("Return source appeared before declaration assembly");
		if(embedded.indexOf("; C line 7: }") > embedded.indexOf("HLT"))
			throw new AssertionError("Function closing line appeared after HLT");
		Assembler plainAssembler = new Assembler();
		Assembler embeddedAssembler = new Assembler();
		if(plainAssembler.assemble(plain) || embeddedAssembler.assemble(embedded))
			throw new AssertionError("Embedded source must assemble successfully");
		if(plainAssembler.numOp != embeddedAssembler.numOp
				|| !Arrays.equals(plainAssembler.binArray(2), embeddedAssembler.binArray(2)))
			throw new AssertionError("Embedded source changed the assembled instructions");
		String complex = "int main() {\n  unsigned char x = 1;\n"
			+ "  unsigned char y = 0;\n  if (x) {\n    y = 2;\n"
			+ "  } else {\n    y = 3;\n  }\n  return y;\n}\n";
		String annotatedComplex = new BasicCCompiler().compile(complex, true);
		for(int line = 1; line <= complex.split("\n", -1).length; line++)
			if(!annotatedComplex.contains("; C line " + line + ":"))
				throw new AssertionError("Missing C source line " + line);
		Assembler complexAssembler = new Assembler();
		Assembler annotatedAssembler = new Assembler();
		if(complexAssembler.assemble(new BasicCCompiler().compile(complex))
				|| annotatedAssembler.assemble(annotatedComplex)
				|| !Arrays.equals(complexAssembler.binArray(2), annotatedAssembler.binArray(2)))
			throw new AssertionError("Interleaved if/else source changed the machine code");
	}

	private static void checkFailure(String source, String message) {
		try {
			new BasicCCompiler().compile(source);
			throw new AssertionError("Expected compiler failure: " + source);
		} catch(BasicCCompiler.CompileException exception) {
			if(!exception.getMessage().contains(message)) throw new AssertionError(exception);
		}
	}
}
