//$Id: RegFileMemory.java,v 1.5 1996/11/13 14:09:19 dubuc Exp $

/*
$Log: RegFileMemory.java,v $
Revision 1.5  1996/11/13 14:09:19  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package memory;

import binContainer.*;

public class RegFileMemory extends Memory {

	public RegFileMemory(){
		capacity = 16;
		locs = new DmnByte[capacity];
		accessPointer = 0;
		for(int i = 0; i < capacity; i++)
			locs[i] = new DmnByte();
	}

	public Word readMemory() {
		locs[0].clearValue();
		return super.readMemory();
	}

	public Word readMemory(int loc) {
		locs[0].clearValue();
		return super.readMemory(loc);
	}

	public void writeMemory(Word w) {
		if(accessPointer != 0)
			super.writeMemory(w);
	}

	public void writeMemory(Word w, int loc) {
		if(loc != 0)
			super.writeMemory(w,loc);
	}

	public void clearMemory() {
		for(int i = 0; i < capacity; i ++)
			locs[i].clearValue();
	}

}
