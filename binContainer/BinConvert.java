//$Id: BinConvert.java,v 1.5 1996/11/13 14:20:39 dubuc Exp $

/*
$Log: BinConvert.java,v $
Revision 1.5  1996/11/13 14:20:39  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package binContainer;

public class BinConvert {

	public static Byte wordToUpperByte(Word w) {
		Byte b = new Byte();
		for(int i = 0; i < 8; i++)
			if(w.bits.get(i + 8))
				b.bits.set(i);
		return b;
	}

	public static Byte wordToLowerByte(Word w) {
		Byte b = new Byte();
		for(int i = 0; i < 8; i++)
			if(w.bits.get(i))
				b.bits.set(i);
		return b;
	}

	public static Nibble byteToUpperNibble(Byte b) {
		Nibble n = new Nibble();
		for(int i = 0; i < 4; i++)
			if(b.bits.get(i + 4))
				n.bits.set(i);
		return n;
	}

	public static Nibble byteToLowerNibble(Byte b) {
		Nibble n = new Nibble();
		for(int i = 0; i < 4; i++)
			if(b.bits.get(i))
				n.bits.set(i);
		return n;
	}
}
