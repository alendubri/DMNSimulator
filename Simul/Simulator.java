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
	public boolean wmem;
	public int mempos;
	private ByteQueue rldi;

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

		instruction = iMemory.readMemory(pcReg.intValue());

		hInst = BinConvert.wordToUpperByte(instruction);
		lInst = BinConvert.wordToLowerByte(instruction);
	}

	private void pushQueues() {
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
		if(wbValid[wbValid.length - 1] && (topWb != 15))
			rFile.writeMemory(xReg,topWb);

		pcQueue.pushQueue(pcReg);
		pcReg.setValue(rFile.readMemory(PCREG).intValue());

		for(int i = 3; i > 0; i--)
			pcState[i] = pcState[i-1];
		pcState[0] = true;
		rFile.writeMemory(pcReg,PCREG);
	}

	public synchronized void step() {
		if(rFile.readMemory(HAREG).intValue() != 255) {
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
			if(exValid[exValid.length - 1])
				this.alu.exMux(depUnit.aReg[1],depUnit.bReg[1],rFile,exQueue.seeTopQueue(),depUnit.sla,depUnit.slb);
			else
				this.alu.bubble(rFile,depUnit.sla,depUnit.slb);
			this.writeBack();
			if(alu.flush)
				flushQueues();
		}
	}

	private void flushQueues() {
		wbQueue.clearQueue();
		exQueue.clearQueue();
		clearValidity(wbValid);
		clearValidity(exValid);
		depUnit.reset();
		alu.clearPipelineState();
		wb4.clearValue();
		resetPCState();
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

