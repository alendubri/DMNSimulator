//$Id: Byte.java,v 1.5 1996/11/13 14:20:39 dubuc Exp $

/*
$Log: Byte.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

import java.util.BitSet;

public class Byte extends Word {
	public Byte(){
		super(0,8);
	}

	public Byte(int n){
		super(n,8);
	}

	public Byte(long l){
		super(l,8);
	}

	public Nibble toNibble() {
		Nibble n = new Nibble();
		for(int i = 0; i < n.nbits; i++)
			if(this.bits.get(i))
				n.bits.set(i);
		return n;
	}

	public Byte add2Complement(Byte sum) {
		int s1 = this.intValue2Complement();
		int s2 = sum.intValue2Complement();
		Byte w = new Byte();
		w.setValue2Complement(s1 + s2);
		return w;
	}

	public Byte sub2Complement(Byte sus) {
		int s1 = this.intValue2Complement();
		int s2 = sus.intValue2Complement();
		Byte w = new Byte();
		w.setValue2Complement(s1 - s2);
		return w;
	}
}
