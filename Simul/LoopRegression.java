package Simul;

import Assembler.Assembler;

public class LoopRegression {

	private static final String PROGRAM =
		"ldi r1,#1\n" +
		"ldi r2,#a\n" +
		"loop: sub r2,r2,r1\n" +
		"or r3,r2,r3\n" +
		"sub r4,r2,r3\n" +
		"nts r0,r2\n" +
		"jcn loop\n" +
		"hlt\n";

	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		Assembler assembler = new Assembler();
		boolean trace = (args.length > 0) && "--trace".equals(args[0]);

		if(assembler.assemble(PROGRAM) || !assembler.assembOk)
			throw new IllegalStateException("Assembly failed for regression program.");

		assembler.putOnMemory(simulator.iMemory);
		simulator.nmeConvMem();

		int previousR2 = simulator.rFile.readMemory(2).intValue();
		boolean sawFirstSubWrite = false;
		boolean checkedSecondSubWrite = false;

		for(int cycle = 1; cycle <= 64; cycle++) {
			simulator.step();
			int currentR2 = simulator.rFile.readMemory(2).intValue();
			if(trace) {
				System.out.println(
					"cycle=" + cycle +
					" pc=" + simulator.pcReg.toHexString() +
					" r2=" + Integer.toHexString(currentR2) +
					" xReg=" + simulator.xReg.toHexString() +
					" xQ0=" + simulator.alu.xQueue.seeElement(0).toHexString() +
					" wbTop=" + simulator.wbQueue.seeTopQueue().toHexString() +
					" exTop=" + simulator.exQueue.seeTopQueue().toHexString() +
					" cond=" + simulator.rFile.readMemory(Simulator.CNDBT).toHexString()
				);
			}

			if(currentR2 != previousR2) {
				if(!sawFirstSubWrite && currentR2 == 9) {
					sawFirstSubWrite = true;
				}
				else if(sawFirstSubWrite && !checkedSecondSubWrite) {
					if(currentR2 != 8)
						throw new IllegalStateException(
							"Second SUB writeback regression: expected R2=8 but found R2=" +
							Integer.toHexString(currentR2) + " on cycle " + cycle
						);
					checkedSecondSubWrite = true;
					break;
				}
				previousR2 = currentR2;
			}
		}

		if(!sawFirstSubWrite)
			throw new IllegalStateException("Regression program never produced the first SUB writeback.");
		if(!checkedSecondSubWrite)
			throw new IllegalStateException("Regression program never reached the second SUB writeback.");

		System.out.println("LoopRegression passed.");
	}
}
