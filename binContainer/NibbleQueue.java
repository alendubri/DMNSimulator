//$id$

/*
$Log: NibbleQueue.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

public class NibbleQueue extends BinQueue {

	public NibbleQueue(int s) {
		size = s;
		queue = new Nibble[size];
		for(int i = 0; i < size; i++)
			queue[i] = new Nibble();
	}

	public Nibble seeTopQueue() {
		return (Nibble) queue[size - 1];
	}

	public Nibble seeElement(int pos) {
		if ((pos >= 0) && (pos < size))
			return (Nibble) queue[pos];
		else
			return (Nibble) queue[0];
	}

}

