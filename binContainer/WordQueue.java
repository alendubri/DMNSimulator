//$id$

/*
$Log: WordQueue.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

public class WordQueue extends BinQueue {

	public WordQueue(int s) {
		size = s;
		queue = new Word[size];
		for(int i = 0; i < size; i++)
			queue[i] = new Word();
	}

	public Word seeTopQueue() {
		return queue[size - 1];
	}

	public Word seeElement(int pos) {
		if ((pos >= 0) && (pos < size))
			return queue[pos];
		else
			return queue[0];
	}

}

