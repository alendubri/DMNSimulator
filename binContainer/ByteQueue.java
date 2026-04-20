//$id$

/*
$Log: ByteQueue.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

public class ByteQueue extends BinQueue {

	public ByteQueue(int s) {
		size = s;
		queue = new DmnByte[size];
		for(int i = 0; i < size; i++)
			queue[i] = new DmnByte();
	}

	public DmnByte seeTopQueue() {
		return (DmnByte) queue[size - 1];
	}

	public DmnByte seeElement(int pos) {
		if ((pos >= 0) && (pos < size))
			return (DmnByte) queue[pos];
		else
			return (DmnByte) queue[0];
	}

}

