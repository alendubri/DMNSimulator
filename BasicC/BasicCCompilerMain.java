package BasicC;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Command-line entry point for compiling a C-subset source file to DMN assembly. */
public final class BasicCCompilerMain {
	private BasicCCompilerMain() { }

	public static void main(String[] args) throws Exception {
		List<String> paths = new ArrayList<>();
		boolean embedSource = false;
		for(String arg : args) {
			if("-embedSource".equals(arg)) {
				if(embedSource) throw new IllegalArgumentException("-embedSource was specified twice");
				embedSource = true;
			} else if(arg.startsWith("-")) {
				throw new IllegalArgumentException("Unknown option: " + arg);
			} else paths.add(arg);
		}
		if(paths.size() < 1 || paths.size() > 2)
			throw new IllegalArgumentException(
				"Usage: BasicCCompilerMain [-embedSource] input.c [output.asm]");
		String assembly;
		try {
			String source = Files.readString(Path.of(paths.get(0)));
			assembly = new BasicCCompiler().compile(source, embedSource);
		} catch(IllegalArgumentException exception) {
			System.err.println("Compilation failed: " + exception.getMessage());
			System.exit(1);
			return;
		}
		if(paths.size() == 2) Files.writeString(Path.of(paths.get(1)), assembly);
		else System.out.print(assembly);
	}
}
