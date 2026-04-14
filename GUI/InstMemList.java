//$Id: InstMemList.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: InstMemList.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;
import memory.*;
import binContainer.*;

public class InstMemList extends List {

	InstMemory iMem;
	String [] nmeMem;
	int size;
	long tact, tini;

	public InstMemList(InstMemory im,String [] nme) {
		super();
		iMem = im;
		nmeMem = nme;
		size = iMem.getCapMemory();
		for(int i = 0; i < size; i++){
			DmnByte d = new DmnByte(i);
			this.add(d.toHexString() + ":" + nmeMem[i]);
		}
	}

	public void refreshItemsDec() {
		for(int i = 0; i < size; i++){
			Word b = iMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toString(),i);
		}
	}

	public void refreshItemsHex() {
		for(int i = 0; i < size; i++){
			Word b = iMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toHexString(),i);
		}
	}

	public void refreshItemsBin() {
		for(int i = 0; i < size; i++){
			Word b = iMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toBinaryString(),i);
		}
	}

	public void refreshItemsNme() {
		for(int i = 0; i < size; i++){
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + nmeMem[i],i);
		}
	}

}
