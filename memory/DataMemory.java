//$Id: DataMemory.java,v 1.5 1996/11/13 14:09:19 dubuc Exp $

/*
$Log
*/

package memory;

import binContainer.*;

public class DataMemory extends Memory {

	public DataMemory(){
		capacity = 256;
		locs = new Byte[capacity];
		accessPointer = 0;
		for(int i = 0; i < capacity; i++)
			locs[i] = new Byte();
	}

}
