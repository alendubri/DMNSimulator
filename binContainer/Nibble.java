//$Id: Nibble.java,v 1.5 1996/11/13 14:20:39 dubuc Exp $

/*
$Log: Nibble.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

import java.util.BitSet;

public class Nibble extends Word {

	public Nibble() {
		super(0,4);
	}

	public Nibble(int n) {
		super(n,4);
	}

	public Nibble(long n) {
		super(n,4);
	}

	public Byte toByte() {
		Byte b = new Byte();
		b.nbits = 8;
		b.bits = (BitSet)this.bits.clone();
		return b;
	}

	public Nibble add2Complement(Nibble sum) {
		int s1 = this.intValue2Complement();
		int s2 = sum.intValue2Complement();
		Nibble w = new Nibble();
		w.setValue2Complement(s1 + s2);
		return w;
	}

	public Nibble sub2Complement(Nibble sus) {
		int s1 = this.intValue2Complement();
		int s2 = sus.intValue2Complement();
		Nibble w = new Nibble();
		w.setValue2Complement(s1 - s2);
		return w;
	}
}

