//$Id: DependResolver.java,v 1.5.1.1 1996/11/14 21:21:15 dubuc Exp $

/*
$Log: DependResolver.java,v $
Revision 1.5.1.1  1996/11/14 21:21:15  dubuc
Cambio menor para evitar resolver dependencia de datos en R0.

Revision 1.5  1996/11/13 13:50:27  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package Simul;

import binContainer.*;
import memory.*;

public class DependResolver {

	RegFileMemory rFile;
	public DmnByte [] aReg = new DmnByte[2];
	public DmnByte [] bReg = new DmnByte[2];
	public Nibble sla, slb;
	ByteQueue rldi;
	NibbleQueue wbQueue;
	DmnByte xReg;
	boolean wbValid;
	public boolean depL1, depL2;
	public boolean depAL1, depAL2;
	public boolean depBL1, depBL2;

	public DependResolver() {
		sla = new Nibble();
		slb = new Nibble();
		for(int i = 0; i < 2; i++) {
			aReg[i] = new DmnByte();
			bReg[i] = new DmnByte();
		}
		depL1 = depL2 = false;
		depAL1 = depAL2 = depBL1 = depBL2 = false;
	}

	public void reset() {
		sla.clearValue();
		slb.clearValue();
		for(int i = 0; i < 2; i++) {
			aReg[i].clearValue();
			bReg[i].clearValue();
		}
		depL1 = depL2 = false;
		depAL1 = depAL2 = depBL1 = depBL2 = false;
	}

	public void fetchSelector(DmnByte b) {
		sla.setValue((BinConvert.byteToUpperNibble(b)).intValue());
		slb.setValue((BinConvert.byteToLowerNibble(b)).intValue());
	}

	public void setParameters(
		RegFileMemory rf,
		DmnByte xr,
		ByteQueue rldiq,
		NibbleQueue wbq,
		DmnByte sByte)
	{
		this.setParameters(rf,xr,rldiq,wbq,true);
		this.fetchSelector(sByte);
	}

	public void setParameters(
		RegFileMemory rf,
		DmnByte xr,
		ByteQueue rldiq,
		NibbleQueue wbq,
		boolean wbv)
	{
		this.rFile = rf;
		this.xReg = xr;
		this.rldi = rldiq;
		this.wbQueue = wbq;
		this.wbValid = wbv;
	}
	public void resolve() {
		depL1 = depL2 = false;
		depAL1 = depAL2 = depBL1 = depBL2 = false;
		aReg[1].setValue(aReg[0].intValue());
		bReg[1].setValue(bReg[0].intValue());
		aReg[0].setValue((rFile.readMemory(sla.intValue())).intValue());
		bReg[0].setValue((rFile.readMemory(slb.intValue())).intValue());

		if(!wbValid)
			return;

		int wbQtop = (wbQueue.seeTopQueue()).intValue();
		if(wbQtop == 0)
			return;

		// A[1] (Dependencia de Nivel 1 en A)
		if(wbQtop == (BinConvert.byteToUpperNibble(rldi.seeElement(0))).intValue()){
			aReg[1].setValue(xReg.intValue());
			depAL2 = true;
		}
	
		// B[1] (Dependencia de Nivel 1 en B)
		if(wbQtop == (BinConvert.byteToLowerNibble(rldi.seeElement(0))).intValue()){
			bReg[1].setValue(xReg.intValue());
			depBL2 = true;
		}

		depL2 = depAL2 || depBL2;

		// A[0] (Dependencia de Nivel 2 en A)
		if(wbQtop == sla.intValue()){
			aReg[0].setValue(xReg.intValue());
			depAL1 = true;
		}

		// B[0] (Dependencia de Nivel 2 en B)
		if(wbQtop == slb.intValue()){
			bReg[0].setValue(xReg.intValue());
			depBL1 = true;
		}

		depL1 = depAL1 || depBL1;
	}

	public void resolve(
		RegFileMemory rf,
		DmnByte xr,
		ByteQueue rldiq,
		NibbleQueue wbq,
		boolean wbv)
	{
		this.setParameters(rf,xr,rldiq,wbq,wbv);
		this.resolve();
	}
		
	public void resolve(
		RegFileMemory rf,
		DmnByte xr,
		ByteQueue rldiq,
		NibbleQueue wbq,
		boolean wbv,
		DmnByte sByte)
	{
		this.setParameters(rf,xr,rldiq,wbq,wbv);
		this.fetchSelector(sByte);
		this.resolve();
	}
}
