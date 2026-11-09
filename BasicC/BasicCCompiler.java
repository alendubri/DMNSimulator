package BasicC;

import Assembler.Assembler;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/** Compiles a documented, small C-like subset into the existing DMN assembler syntax. */
public final class BasicCCompiler {
	public String compile(String source) {
		return compile(source, false);
	}

	public String compile(String source, boolean embedSource) {
		if(source == null) throw new IllegalArgumentException("source must not be null");
		Parser parser = new Parser(source);
		Program program = parser.parse();
		Generator generator = new Generator(program.variables.size(),
			embedSource ? program.sourceLines : null);
		generator.source(program.header);
		for(Statement statement : program.statements) statement.emit(generator);
		generator.source(program.close);
		generator.instruction("HLT");
		String assembly = generator.result();
		Assembler assembler = new Assembler();
		if(assembler.assemble(assembly))
			throw new IllegalStateException("Compiler emitted invalid assembly: "
				+ String.join("; ", assembler.errMsgArray()));
		return assembly;
	}

	public static final class CompileException extends IllegalArgumentException {
		private static final long serialVersionUID = 1L;

		public CompileException(String message, int line, int column) {
			super("line " + line + ", column " + column + ": " + message);
		}
	}

	private static final class Program {
		final Map<String, Integer> variables;
		final List<Statement> statements;
		final String[] sourceLines;
		final SourceMarker header, close;
		Program(Map<String, Integer> variables, List<Statement> statements,
				String[] sourceLines, SourceMarker header, SourceMarker close) {
			this.variables = variables;
			this.statements = statements;
			this.sourceLines = sourceLines;
			this.header = header;
			this.close = close;
		}
	}

	private static final class SourceMarker {
		final int firstLine, lastLine;
		final SortedSet<Integer> lines = new TreeSet<>();
		SourceMarker(int firstLine, int lastLine) {
			this.firstLine = firstLine;
			this.lastLine = lastLine;
			for(int line = firstLine; line <= lastLine; line++) lines.add(line);
		}
	}

	private interface Statement { void emit(Generator g); }
	private interface Expression { void emit(Generator g, int depth); }

	private static final class SourceStatement implements Statement {
		final SourceMarker source;
		final Statement statement;
		SourceStatement(SourceMarker source, Statement statement) {
			this.source = source;
			this.statement = statement;
		}
		public void emit(Generator g) {
			g.source(source);
			statement.emit(g);
		}
	}

	private static final class Literal implements Expression {
		final int value;
		Literal(int value) { this.value = value; }
		public void emit(Generator g, int depth) { g.load(1, value); }
	}

	private static final class Variable implements Expression {
		final int address;
		Variable(int address) { this.address = address; }
		public void emit(Generator g, int depth) { g.read(1, address); }
	}

	private static final class Binary implements Expression {
		final String operator;
		final Expression left, right;
		Binary(String operator, Expression left, Expression right) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}
		public void emit(Generator g, int depth) {
			int temp = g.temp(depth);
			left.emit(g, depth + 1);
			g.write(1, temp);
			right.emit(g, depth + 1);
			g.instruction("OR R2,R1,R0");
			g.read(1, temp);
			String opcode = switch(operator) {
				case "+" -> "ADD";
				case "-" -> "SUB";
				case "&" -> "AND";
				case "|" -> "OR";
				default -> throw new IllegalStateException("Unexpected operator: " + operator);
			};
			g.instruction(opcode + " R1,R1,R2");
		}
	}

	private static final class Assignment implements Statement {
		final int address;
		final Expression value;
		Assignment(int address, Expression value) {
			this.address = address;
			this.value = value;
		}
		public void emit(Generator g) {
			value.emit(g, 0);
			g.write(1, address);
		}
	}

	private static final class Return implements Statement {
		final Expression value;
		Return(Expression value) { this.value = value; }
		public void emit(Generator g) {
			value.emit(g, 0);
		}
	}

	private static final class Block {
		final List<Statement> statements;
		final SourceMarker close;
		Block(List<Statement> statements, SourceMarker close) {
			this.statements = statements;
			this.close = close;
		}
	}

	private static final class Condition {
		final Expression left, right;
		final String comparison;
		Condition(Expression left, String comparison, Expression right) {
			this.left = left;
			this.comparison = comparison;
			this.right = right;
		}
		void jumpIfTrue(Generator g, String target) {
			if(right == null) {
				left.emit(g, 0);
				g.instruction("ZTS R1,R0");
			} else {
				int temp = g.temp(0);
				left.emit(g, 1);
				g.write(1, temp);
				right.emit(g, 1);
				g.instruction("OR R2,R1,R0");
				g.read(1, temp);
				g.instruction("ZTS R1,R2");
			}
			if("==".equals(comparison)) {
				g.branch(target);
			} else {
				String skip = g.labelName();
				g.branch(skip);
				g.jump(target);
				g.label(skip);
			}
		}
	}

	private static final class If implements Statement {
		final Condition condition;
		final Block thenBody, elseBody;
		final SourceMarker header, elseHeader;
		If(Condition condition, Block thenBody, Block elseBody,
				SourceMarker header, SourceMarker elseHeader) {
			this.condition = condition;
			this.thenBody = thenBody;
			this.elseBody = elseBody;
			this.header = header;
			this.elseHeader = elseHeader;
		}
		public void emit(Generator g) {
			String yes = g.labelName();
			String done = g.labelName();
			g.source(header);
			condition.jumpIfTrue(g, yes);
			if(elseBody != null) {
				g.source(elseHeader);
				for(Statement statement : elseBody.statements) statement.emit(g);
				g.source(elseBody.close);
			}
			g.jump(done);
			g.label(yes);
			for(Statement statement : thenBody.statements) statement.emit(g);
			g.source(thenBody.close);
			g.label(done);
		}
	}

	private static final class While implements Statement {
		final Condition condition;
		final Block body;
		final SourceMarker header;
		While(Condition condition, Block body, SourceMarker header) {
			this.condition = condition;
			this.body = body;
			this.header = header;
		}
		public void emit(Generator g) {
			String test = g.labelName();
			String yes = g.labelName();
			String done = g.labelName();
			g.source(header);
			g.label(test);
			condition.jumpIfTrue(g, yes);
			g.jump(done);
			g.label(yes);
			for(Statement statement : body.statements) statement.emit(g);
			g.source(body.close);
			g.jump(test);
			g.label(done);
		}
	}

	private static final class Generator {
		final StringBuilder assembly = new StringBuilder();
		final int firstTemp;
		final String[] sourceLines;
		int instructionCount, nextLabel;
		Generator(int firstTemp, String[] sourceLines) {
			this.firstTemp = firstTemp;
			this.sourceLines = sourceLines;
		}
		void source(SourceMarker marker) {
			if(sourceLines != null)
				for(int line : marker.lines)
					assembly.append("; C line ").append(line).append(": ")
						.append(sourceLines[line - 1]).append('\n');
		}
		int temp(int depth) {
			int address = firstTemp + depth;
			if(address >= 256) throw new IllegalArgumentException("Program needs more than 256 data bytes");
			return address;
		}
		String labelName() { return "C" + nextLabel++; }
		void label(String name) { assembly.append(name).append(":\n"); }
		void instruction(String text) {
			if(++instructionCount > 256)
				throw new IllegalArgumentException("Program needs more than 256 instruction words");
			assembly.append(text).append('\n');
		}
		void load(int register, int value) {
			instruction(String.format("LDI R%d,#%02X", register, value));
		}
		void read(int register, int address) {
			load(3, address);
			instruction("LDR R" + register + ",R3");
		}
		void write(int register, int address) {
			load(3, address);
			instruction("STR R3,R" + register);
		}
		void jump(String label) {
			instruction("ZTS R0,R0");
			branch(label);
		}
		void branch(String label) {
			instruction("JCN " + label);
			// The simulator resolves JCN one execute cycle later using the live CN flag.
			// A following ZTS could change that flag before the branch is resolved.
			instruction("OR R0,R0,R0");
		}
		String result() { return assembly.toString(); }
	}

	private record Token(String text, int line, int column) { }

	private static final class Lexer {
		final String source;
		int index, line = 1, column = 1;
		Lexer(String source) { this.source = source; }
		Token next() {
			while(index < source.length()) {
				char c = source.charAt(index);
				if(Character.isWhitespace(c)) { advance(); continue; }
				if(starts("//")) {
					while(index < source.length() && source.charAt(index) != '\n'
							&& source.charAt(index) != '\r') advance();
					continue;
				}
				if(starts("/*")) {
					int startLine = line, startColumn = column;
					advance(); advance();
					while(index < source.length() && !starts("*/")) advance();
					if(index == source.length())
						throw new CompileException("Unterminated comment", startLine, startColumn);
					advance(); advance();
					continue;
				}
				int start = index, startLine = line, startColumn = column;
				if(Character.isLetter(c) || c == '_') {
					do { advance(); } while(index < source.length()
						&& (Character.isLetterOrDigit(source.charAt(index)) || source.charAt(index) == '_'));
				} else if(Character.isDigit(c)) {
					do { advance(); } while(index < source.length()
						&& (Character.isLetterOrDigit(source.charAt(index)) || source.charAt(index) == '_'));
				} else if(starts("==") || starts("!=")) {
					advance(); advance();
				} else if("{}();=+-&|".indexOf(c) >= 0) {
					advance();
				} else throw new CompileException("Unsupported character '" + c + "'", line, column);
				return new Token(source.substring(start, index), startLine, startColumn);
			}
			return new Token("<end>", line, column);
		}
		boolean starts(String text) { return source.startsWith(text, index); }
		void advance() {
			char c = source.charAt(index++);
			if(c == '\r') { line++; column = 1; }
			else if(c == '\n') {
				if(index < 2 || source.charAt(index - 2) != '\r') line++;
				column = 1;
			}
			else column++;
		}
	}

	private static final class Parser {
		final Lexer lexer;
		final String[] sourceLines;
		final Map<String, Integer> variables = new LinkedHashMap<>();
		final List<SourceMarker> markers = new ArrayList<>();
		Token token;
		Parser(String source) {
			lexer = new Lexer(source);
			sourceLines = source.split("\\r\\n|\\r|\\n", -1);
			token = lexer.next();
		}
		Program parse() {
			int mainLine = token.line();
			expect("int"); expect("main"); expect("(");
			accept("void");
			expect(")");
			SourceMarker header = marker(mainLine, token.line());
			expect("{");
			List<Statement> statements = new ArrayList<>();
			while(peek("unsigned")) {
				int firstLine = token.line();
				expect("unsigned");
				expect("char");
				Token name = identifier();
				if(variables.containsKey(name.text()))
					throw error(name, "Duplicate variable '" + name.text() + "'");
				if(variables.size() >= 256) throw error(name, "Too many variables");
				Expression initial = accept("=") ? expression() : new Literal(0);
				int lastLine = token.line();
				expect(";");
				int address = variables.size();
				variables.put(name.text(), address);
				statements.add(new SourceStatement(marker(firstLine, lastLine),
					new Assignment(address, initial)));
			}
			while(!peek("}") && !peek("<end>")) {
				if(peek("return")) {
					int firstLine = token.line();
					expect("return");
					Expression value = expression();
					int lastLine = token.line();
					expect(";");
					statements.add(new SourceStatement(marker(firstLine, lastLine),
						new Return(value)));
					if(!peek("}")) throw error(token, "return must be the final statement in main");
				} else statements.add(statement());
			}
			SourceMarker close = marker(token.line(), token.line());
			expect("}"); expect("<end>");
			attachUnmatchedLines();
			return new Program(variables, statements, sourceLines, header, close);
		}
		Statement statement() {
			if(peek("return"))
				throw error(token, "return is supported only as the final statement in main");
			int firstLine = token.line();
			if(accept("if")) {
				expect("("); Condition condition = condition(); expect(")");
				SourceMarker header = marker(firstLine, token.line());
				Block yes = block();
				SourceMarker elseHeader = null;
				Block no = null;
				if(peek("else")) {
					int elseLine = token.line();
					expect("else");
					elseHeader = marker(elseLine, token.line());
					no = block();
				}
				return new If(condition, yes, no, header, elseHeader);
			}
			if(accept("while")) {
				expect("("); Condition condition = condition(); expect(")");
				SourceMarker header = marker(firstLine, token.line());
				return new While(condition, block(), header);
			}
			Token name = identifier();
			Integer address = variables.get(name.text());
			if(address == null) throw error(name, "Unknown variable '" + name.text() + "'");
			expect("="); Expression value = expression();
			int lastLine = token.line();
			expect(";");
			return new SourceStatement(marker(firstLine, lastLine),
				new Assignment(address, value));
		}
		Block block() {
			expect("{");
			List<Statement> statements = new ArrayList<>();
			while(!peek("}") && !peek("<end>")) statements.add(statement());
			SourceMarker close = marker(token.line(), token.line());
			expect("}");
			return new Block(statements, close);
		}
		SourceMarker marker(int firstLine, int lastLine) {
			SourceMarker marker = new SourceMarker(firstLine, lastLine);
			markers.add(marker);
			return marker;
		}
		void attachUnmatchedLines() {
			boolean[] covered = new boolean[sourceLines.length + 1];
			for(SourceMarker marker : markers)
				for(int line = marker.firstLine; line <= marker.lastLine; line++)
					covered[line] = true;
			for(int line = 1; line <= sourceLines.length; line++) {
				if(covered[line]) continue;
				SourceMarker recipient = markers.get(markers.size() - 1);
				for(SourceMarker marker : markers)
					if(marker.firstLine > line) { recipient = marker; break; }
				recipient.lines.add(line);
			}
		}
		Condition condition() {
			Expression left = expression();
			if(peek("==") || peek("!=")) {
				String comparison = token.text(); next();
				return new Condition(left, comparison, expression());
			}
			return new Condition(left, "!=", null);
		}
		Expression expression() {
			Expression left = bitwiseAnd();
			while(accept("|")) left = new Binary("|", left, bitwiseAnd());
			return left;
		}
		Expression bitwiseAnd() {
			Expression left = additive();
			while(accept("&")) left = new Binary("&", left, additive());
			return left;
		}
		Expression additive() {
			Expression left = primary();
			while(peek("+") || peek("-")) {
				String operator = token.text(); next();
				left = new Binary(operator, left, primary());
			}
			return left;
		}
		Expression primary() {
			if(accept("(")) {
				Expression value = expression(); expect(")"); return value;
			}
			Token item = token;
			if(Character.isDigit(item.text().charAt(0))) {
				next();
				try {
					int value = item.text().startsWith("0x") || item.text().startsWith("0X")
						? Integer.parseInt(item.text().substring(2), 16)
						: Integer.parseInt(item.text());
					if(value < 0 || value > 255) throw error(item, "Literal must fit one byte");
					return new Literal(value);
				} catch(NumberFormatException ex) {
					throw error(item, "Invalid integer literal '" + item.text() + "'");
				}
			}
			Token name = identifier();
			Integer address = variables.get(name.text());
			if(address == null) throw error(name, "Unknown variable '" + name.text() + "'");
			return new Variable(address);
		}
		Token identifier() {
			Token item = token;
			if(item.text().equals("<end>")
					|| !(Character.isLetter(item.text().charAt(0)) || item.text().charAt(0) == '_'))
				throw error(item, "Expected identifier");
			if(item.text().equals("int") || item.text().equals("unsigned")
					|| item.text().equals("char") || item.text().equals("if")
					|| item.text().equals("else") || item.text().equals("while")
					|| item.text().equals("return") || item.text().equals("main")
					|| item.text().equals("void"))
				throw error(item, "Reserved word cannot be a variable");
			next(); return item;
		}
		boolean peek(String text) { return token.text().equals(text); }
		boolean accept(String text) { if(!peek(text)) return false; next(); return true; }
		void expect(String text) {
			if(!accept(text)) throw error(token, "Expected '" + text + "', found '" + token.text() + "'");
		}
		void next() { token = lexer.next(); }
		CompileException error(Token item, String message) {
			return new CompileException(message, item.line(), item.column());
		}
	}
}
