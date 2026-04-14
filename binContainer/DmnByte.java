//$Id: DmnByte.java,v 1.5 1996/11/13 14:20:39 dubuc Exp $

/*
$Log: DmnByte.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

import java.util.BitSet;

public class DmnByte extends Word {
	public DmnByte(){
		super(0,8);
	}

	public DmnByte(int n){
		super(n,8);
	}

	public DmnByte(long l){
		super(l,8);
	}

	public Nibble toNibble() {
		Nibble n = new Nibble();
		for(int i = 0; i < n.nbits; i++)
			if(this.bits.get(i))
				n.bits.set(i);
		return n;
	}

	public DmnByte add2Complement(DmnByte sum) {
		int s1 = this.intValue2Complement();
		int s2 = sum.intValue2Complement();
		DmnByte w = new DmnByte();
		w.setValue2Complement(s1 + s2);
		return w;
	}

	public DmnByte sub2Complement(DmnByte sus) {
		int s1 = this.intValue2Complement();
		int s2 = sus.intValue2Complement();
		DmnByte w = new DmnByte();
		w.setValue2Complement(s1 - s2);
		return w;
	}
}
