//$Id: ALLU.java,v 1.5 1996/11/13 13:50:27 dubuc Exp $

/*
$Log: ALLU.java,v $
Revision 1.5  1996/11/13 13:50:27  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package Simul;

import memory.*;
import binContainer.*;

public class ALLU {

	Byte	
		rA, rB;

	RegFileMemory
		rFile;

	public Byte
		xReg, PCR;

	public ByteQueue
		rldi, xQueue;

	Nibble
		exSelector, wbQ3,
		selectA, selectB;

	public Nibble
		wbQ3p;

	public DataMemory dMemory;

	public boolean intr, flush, wmem;
	public int mempos;

	ALLU() {
		dMemory = new DataMemory();
		xReg = new Byte();
		PCR = new Byte();
		rldi = new ByteQueue(2);
		xQueue = new ByteQueue(2);
		wmem = intr = flush = false;
		mempos = 0;
		wbQ3 = new Nibble();
		wbQ3p = new Nibble();
	}

	public void reset() {
		wbQ3.clearValue();
		wbQ3p.clearValue();
		PCR.clearValue();
		xReg.clearValue();
		rldi.clearQueue();
		xQueue.clearQueue();
	}

	public void setParameters(
		Byte ra,
		Byte rb,
		RegFileMemory rf,
		Nibble exs,
		Nibble sa,
		Nibble sb) 
	{
		rA = ra;
		rB = rb;
		rFile = rf;
		exSelector = exs;
		selectA = sa;
		selectB = sb;
	}
	public void exMux(
		Byte ra,
		Byte rb,
		RegFileMemory rf,
		Nibble exs,
		Nibble sa,
		Nibble sb) 
	{
		this.setParameters(ra,rb,rf,exs,sa,sb);
		this.exMux();
	}

	public void exMux() {
		if((rA != null)&&(rB != null)&&(rFile != null)&&(exSelector != null)&&(selectA != null)&&(selectB != null)) {
			wmem = flush = false;
			rldi.pushQueue(new Byte(selectA.intValue()*16 + selectB.intValue()));
			int ex = exSelector.intValue();
			switch (ex) {
			case 0x00 : { // ADD
			                 xReg.setValue((rA.add2Complement(rB)).intValue());
			            }
							break;
			case 0x01 : { // SUB
			                 xReg.setValue((rA.sub2Complement(rB)).intValue());
			            }
							break;
			case 0x02 : { // AND
			                 xReg.setValue(rA.intValue() & rB.intValue());
			            }
							break;
			case 0x03 : { // OR
			                 xReg.setValue(rA.intValue() | rB.intValue());
			            }
							break;
			case 0x04 : { // NOT
			                 xReg.setValue(Math.abs(~rA.intValue()));
			            }
							break;
			case 0x05 : { // SHT
								  int tmp = rA.intValue();
								  int desp = rA.intValue2Complement();
								  if (desp < 0) 
			                 	xReg.setValue(tmp << Math.abs(desp));
								  else
			                 	xReg.setValue(tmp >> Math.abs(desp));

			                 xReg.setValue(rA.intValue() << rB.intValue());
			            }
							break;
			case 0x06 : { // <Reservado>
			                 xReg.setValue(0);
			            }
							break;
			case 0x07 : { // <Reservado>
			                 xReg.setValue(0);
			            }
							break;
			case 0x08 : { // ZTST
								  if(rA.intValue() == rB.intValue())
								  	rFile.writeMemory(new Byte(255),Simulator.CNDBT);
								  else
								  	rFile.writeMemory(new Byte(0),Simulator.CNDBT);
								  xReg.setValue(0);
			            }
							break;
			case 0x09 : { // NTST
								  if(rA.lt2Complement(rB))
								  	rFile.writeMemory(new Byte(255),Simulator.CNDBT);
								  else
								  	rFile.writeMemory(new Byte(0),Simulator.CNDBT);
								  xReg.setValue(0);
			            }
							break;
			case 0x0a : { // JCND
								  if(rFile.readMemory(Simulator.CNDBT).intValue() != 0)
								  	xReg.setValue((rldi.seeTopQueue()).intValue());
			            }
							break;
			case 0x0b : { // RTI
								  xReg.setValue(PCR.intValue());
								  PCR.clearValue();
			            }
							break;
			case 0x0c : { // HLT
								  xReg.setValue(255);
			            }
							break;
			case 0x0d : { // LDI
								  xReg.setValue((rldi.seeTopQueue()).intValue());
			            }
							break;
			case 0x0e : { // LDR
								  Byte b = (Byte) dMemory.readMemory(rA.intValue());
								  xReg.setValue(b.intValue());
			            }
							break;
			case 0x0f : { // STR
								  int wbq = wbQ3.intValue();
								  if ((wbq != 0x0a)&&(wbq != 0x0c)&&(wbq != 0x0b)){
									  wmem = true;
									  mempos = rA.intValue();
									  dMemory.writeMemory(rB,rA.intValue());
								  }
								  xReg.setValue(0);
			            }
							break;
			}
			xQueue.pushQueue(xReg);
			xReg.setValue((xQueue.seeTopQueue()).intValue());
			this.incrPC();
			wbQ3p = (Nibble) wbQ3.clone();
			wbQ3.setValue(exSelector.intValue());
		}
	}

	private void incrPC() {
		Byte pc = (Byte) rFile.readMemory(Simulator.PCREG);
		int wbq = wbQ3.intValue();
		if (intr) {
			pc.setValue(rFile.readMemory(Simulator.PSREG).intValue());
			intr = false;
			flush = true;
		}
		else if ((wbq == 0x0b) || ((wbq == 0x0a)&&(rFile.readMemory(Simulator.CNDBT).intValue() != 0))) {
			pc.setValue(xReg.intValue());
			rFile.readMemory(Simulator.CNDBT).clearValue();
			flush = true;
		}
		else {
			if(pc.intValue() > 255)
				pc.setValue(0);
			else
				pc.setValue(pc.intValue() + 1);
		}
	}
}

