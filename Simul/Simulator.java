//$Id: Simulator.java,v 1.6 1996/11/13 14:07:26 dubuc Exp $

/*
$Log: Simulator.java,v $
Revision 1.6  1996/11/13 14:07:26  dubuc
Peque~no Cambio

Revision 1.5  1996/11/13 13:50:27  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

Revision 1.1  1996/10/30 00:45:14  dubuc
Initial revision

*/

package Simul;

import memory.*;
import binContainer.*;

public class Simulator {

	public static int PCREG = 15;
	public static int PSREG = 14;
	public static int HAREG = 13;
	public static int CNDBT = 12;

	public String [] nmeMem;

	Word instruction;
	DmnByte lInst, hInst;
	public DmnByte xReg;
	public DmnByte pcReg;
	public NibbleQueue wbQueue, exQueue;
	public Nibble wb4;
	public RegFileMemory rFile;
	public InstMemory iMemory;
	public DataMemory dMemory;
	public ALLU alu;
	public DependResolver depUnit;
	public ByteQueue pcQueue;
	public boolean [] pcState = new boolean[4];
	private boolean [] exValid = new boolean[2];
	private boolean [] wbValid = new boolean[3];
	// Decode faults travel with the instruction to EX and WB.
	private boolean [] illegalEx = new boolean[2];
	private boolean [] faultWb = new boolean[3];
	// Exact fetch addresses avoid ambiguity after a branch or pipeline flush.
	private int [] wbAddresses = new int[3];
	private int [] wbOpcodes = new int[3];
	private boolean interruptActive;
	public boolean wmem;
	public int mempos;
	private ByteQueue rldi;
	private long cycle;
	private int lastFetchAddress;
	private int lastInstruction;
	private boolean lastCycleFlush;

	public Simulator() {
		instruction = new Word();
		lInst = new DmnByte();
		hInst = new DmnByte();
		wbQueue = new NibbleQueue(3);
		exQueue = new NibbleQueue(2);
		wb4 = new Nibble();
		pcQueue = new ByteQueue(3);
		rFile = new RegFileMemory();
		iMemory = new InstMemory();
		alu = new ALLU();
		depUnit = new DependResolver();
		xReg = alu.xReg;
		dMemory = alu.dMemory;
		rldi = alu.rldi;
		pcReg = new DmnByte();
		nmeMem = new String[iMemory.getCapMemory()];
		pcState[0] = true;
		wmem = false;
	}

	public synchronized void intrAck(){
		if(interruptActive)
			return;
		interruptActive = true;
		boolean flag = true;
		alu.intr = true;
		int i = 4;
		do{
			i--;
			if(pcState[i]) {
				alu.PCR.setValue(pcQueue.seeElement(i-1).intValue());
				flag = false;
			}
		}while((i > 0) && (!pcState[i]));
		if (flag)
			alu.PCR.setValue(pcReg.intValue());
	}

	public synchronized void reset() {
		cycle = 0;
		lastFetchAddress = 0;
		lastInstruction = 0;
		lastCycleFlush = false;
		interruptActive = false;
		lInst.clearValue();
		hInst.clearValue();
		pcQueue.clearQueue();
		pcReg.clearValue();
		rFile.clearMemory();
		flushQueues();
		alu.reset();
		wb4.clearValue();
		wmem = false;
	}

	public void nmeConvMem() {
		NMETransf.convMemory(iMemory,nmeMem);
	}

	private void fetchInstruction() {

		lastFetchAddress = pcReg.intValue();
		instruction = iMemory.readMemory(pcReg.intValue());
		lastInstruction = instruction.intValue();

		hInst = BinConvert.wordToUpperByte(instruction);
		lInst = BinConvert.wordToLowerByte(instruction);
	}

	private void pushQueues() {
		boolean illegal = illegalInstruction(lastInstruction);
		pushValidity(illegalEx, illegal);
		pushValidity(faultWb, illegal);
		for(int i = 2; i > 0; i--) {
			wbAddresses[i] = wbAddresses[i-1];
			wbOpcodes[i] = wbOpcodes[i-1];
		}
		wbAddresses[0] = lastFetchAddress;
		wbOpcodes[0] = lastInstruction >>> 12;
		if(wbValid[wbValid.length - 1])
			wb4.setValue(wbQueue.seeTopQueue().intValue());
		else
			wb4.clearValue();
		pushValidity(exValid,true);
		exQueue.pushQueue(BinConvert.byteToUpperNibble(hInst));
		pushValidity(wbValid,true);
		wbQueue.pushQueue(BinConvert.byteToLowerNibble(hInst));
	}

	private void writeBack() {
		int topWb = wbQueue.seeTopQueue().intValue();
		int opcode = wbOpcodes[wbOpcodes.length - 1];
		if(wbValid[wbValid.length - 1] && !faultWb[faultWb.length - 1]) {
			if(opcode == 0x0c)
				rFile.writeMemory(xReg,HAREG);
			else if((opcode <= 5 || opcode == 0x0d || opcode == 0x0e) && topWb != PCREG)
				rFile.writeMemory(xReg,topWb);
		}

		pcQueue.pushQueue(pcReg);
		pcReg.setValue(rFile.readMemory(PCREG).intValue());

		for(int i = 3; i > 0; i--)
			pcState[i] = pcState[i-1];
		pcState[0] = true;
		rFile.writeMemory(pcReg,PCREG);
	}

	public synchronized void step() {
		if(rFile.readMemory(HAREG).intValue() != 255) {
			cycle++;
			lastCycleFlush = false;
			mempos = alu.mempos;
			wmem = alu.wmem;
			this.fetchInstruction();
			this.pushQueues();
			this.depUnit.resolve(
				rFile,
				alu.xQueue.seeElement(0),
				rldi,
				wbQueue,
				wbValid[wbValid.length - 1],
				lInst
			);
			boolean faultAtWriteback = wbValid[wbValid.length - 1] && faultWb[faultWb.length - 1];
			if(exValid[exValid.length - 1] && !illegalEx[illegalEx.length - 1] && !faultAtWriteback)
				this.alu.exMux(depUnit.aReg[1],depUnit.bReg[1],rFile,exQueue.seeTopQueue(),depUnit.sla,depUnit.slb);
			else
				this.alu.bubble(rFile,depUnit.sla,depUnit.slb);
			if(alu.overflow)
				faultWb[1] = true;
			if(alu.returnedFromInterrupt)
				interruptActive = false;
			this.writeBack();
			if(faultAtWriteback && !interruptActive)
				takeFaultInterrupt(wbAddresses[wbAddresses.length - 1]);
			else if(alu.flush) {
				lastCycleFlush = true;
				flushQueues();
			}
		}
	}

	public synchronized ExecutionSnapshot snapshot() {
		int[] registers = new int[rFile.getCapMemory()];
		for(int index = 0; index < registers.length; index++)
			registers[index] = index == 0 ? 0 : rFile.peekMemory(index).intValue();
		int[] data = new int[dMemory.getCapMemory()];
		for(int index = 0; index < data.length; index++)
			data[index] = dMemory.peekMemory(index).intValue();
		int[] execute = new int[2];
		int[] writeback = new int[3];
		for(int index = 0; index < execute.length; index++)
			execute[index] = exQueue.seeElement(index).intValue();
		for(int index = 0; index < writeback.length; index++)
			writeback[index] = wbQueue.seeElement(index).intValue();
		return new ExecutionSnapshot(cycle, lastFetchAddress, lastInstruction,
			pcReg.intValue(), registers, data, execute, writeback,
			exValid, wbValid, depUnit.depAL1, depUnit.depBL1,
			depUnit.depAL2, depUnit.depBL2, lastCycleFlush, wmem, mempos);
	}

	private void flushQueues() {
		wbQueue.clearQueue();
		exQueue.clearQueue();
		clearValidity(wbValid);
		clearValidity(exValid);
		clearValidity(illegalEx);
		clearValidity(faultWb);
		depUnit.reset();
		alu.clearPipelineState();
		wb4.clearValue();
		resetPCState();
	}

	private void takeFaultInterrupt(int address) {
		// Called only after the faulting instruction reaches WB.
		alu.PCR.setValue(address);
		int serviceAddress = rFile.readMemory(PSREG).intValue();
		pcReg.setValue(serviceAddress);
		rFile.writeMemory(pcReg,PCREG);
		interruptActive = true;
		lastCycleFlush = true;
		flushQueues();
	}

	private boolean illegalInstruction(int word) {
		// CN may be written only by tests; PC and CN are not ordinary operands.
		int opcode = word >>> 12;
		int destination = (word >>> 8) & 0x0f;
		int sourceA = (word >>> 4) & 0x0f;
		int sourceB = word & 0x0f;
		if(opcode == 6 || opcode == 7)
			return true;
		if(opcode <= 5 || opcode == 0x0d || opcode == 0x0e) {
			if(destination == PCREG || destination == CNDBT)
				return true;
		}
		if(opcode <= 5 && (sourceA == PCREG || sourceA == CNDBT))
			return true;
		if((opcode <= 3 || opcode == 5 || opcode == 8 || opcode == 9 || opcode == 0x0f)
			&& (sourceB == PCREG || sourceB == CNDBT))
			return true;
		if((opcode == 8 || opcode == 9 || opcode == 0x0e || opcode == 0x0f)
			&& (sourceA == PCREG || sourceA == CNDBT))
			return true;
		return false;
	}

	private void pushValidity(boolean[] validQueue, boolean validEntry) {
		for(int i = validQueue.length - 1; i > 0; i--)
			validQueue[i] = validQueue[i - 1];
		validQueue[0] = validEntry;
	}

	private void clearValidity(boolean[] validQueue) {
		for(int i = 0; i < validQueue.length; i++)
			validQueue[i] = false;
	}

	private void resetPCState() {
		for(int i = 1; i < 4; i++) 
			pcState[i] = false;
	}
}

