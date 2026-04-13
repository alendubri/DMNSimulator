//$id$

/*
$Log: BinQueue.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

public abstract class BinQueue {

	protected int size;
	protected Word [] queue;

	public void pushQueue(Word w) {
		for(int i = 0; i < (size - 1); i++)
			queue[size - 1 - i].setValue(queue[size - 2 - i].intValue());
		queue[0].setValue(w.intValue());
	}

	public void clearQueue() {
		for(int i = 0; i < size; i++)
			queue[i].clearValue();
	}

}

