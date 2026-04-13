//$Id: Memory.java,v 1.5 1996/11/13 14:09:19 dubuc Exp $

/*
$Log: Memory.java,v $
Revision 1.5  1996/11/13 14:09:19  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package memory;

import binContainer.*;

public abstract class Memory {

	protected Word [] locs;
	protected int accessPointer, capacity;

	public Word readMemory() {
		return locs[accessPointer];
	}

	public Word readMemory(int loc) {
		if(loc < capacity) {
			accessPointer = loc;
			return locs[accessPointer];
		}
		else return locs[capacity - 1];
	}

	public void writeMemory(Word w) {
		locs[accessPointer] = (Word)w.clone();
	}
	public void writeMemory(Word w, int loc) {
		if(loc < capacity) {
			accessPointer = loc;
			locs[accessPointer] = (Word)w.clone();
		}
	}

	public void setAccessPointer(int loc) {
		if(loc < capacity)
			accessPointer = loc;
	}

	public int getAccessPointer() {
		return accessPointer;
	}

	public int getCapMemory() {
		return capacity;
	}
}
