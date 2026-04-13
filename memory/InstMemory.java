//$Id: InstMemory.java,v 1.5 1996/11/13 14:09:19 dubuc Exp $

/*
$Log: InstMemory.java,v $
Revision 1.5  1996/11/13 14:09:19  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package memory;

import binContainer.*;

public class InstMemory extends Memory {

	int [] inst = {0xd375,0xd405,0xd503,0x0653,0x0834,0x3756,0xf034,0xe370,0xcd00};
	public int nInst = 9;

	public InstMemory() {
		capacity = 256;
		locs = new Word[capacity];
		accessPointer = 0;
		for(int i = 0; i < capacity; i++)
			locs[i] = new Word();
	}

	public void initialize() {
		for(int i = 0; i < nInst; i++)
			locs[i].setValue(inst[i]);
	}

}
