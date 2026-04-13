//$Id: NMETransf.java,v 1.5 1996/11/13 13:50:27 dubuc Exp $

/*
$Log: NMETransf.java,v $
Revision 1.5  1996/11/13 13:50:27  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package Simul;

import binContainer.*;
import memory.*;

public class NMETransf {

	public static String [] NMES = {
		"ADD","SUB","AND","OR ",
		"NOT","SHT","<R>","<R>",
		"ZTS","NTS","JCN","RTI",
		"HLT","LDI","LDR","STR"
	};

	public static String bin2Nme(Word w) {

		StringBuffer tmpStr = new StringBuffer();
		Byte hByte, lByte;

		hByte = BinConvert.wordToUpperByte(w);
		lByte = BinConvert.wordToLowerByte(w);

		int opcode = (BinConvert.byteToUpperNibble(hByte)).intValue();
		tmpStr = tmpStr.append(NMES[opcode]);

		if((opcode >= 0) && (opcode <= 5)){
			tmpStr = tmpStr.append(" R"+(BinConvert.byteToLowerNibble(hByte)).toHexString());
			tmpStr = tmpStr.append(",R"+(BinConvert.byteToUpperNibble(lByte)).toHexString());
			tmpStr = tmpStr.append(",R"+(BinConvert.byteToLowerNibble(lByte)).toHexString());
		}
		else if((opcode == 8) || (opcode == 9)) {
			tmpStr = tmpStr.append(" R"+(BinConvert.byteToUpperNibble(lByte)).toHexString());
			tmpStr = tmpStr.append(",R"+(BinConvert.byteToLowerNibble(lByte)).toHexString());
		}
		else if(opcode == 0x0a) {
			tmpStr = tmpStr.append(" @" + lByte.toHexString());	
		}
		else if(opcode == 0x0d) {
			Nibble n = BinConvert.byteToLowerNibble(hByte);
			if(n.intValue() == 0xe)
				tmpStr = tmpStr.append(" PS");
			else
				tmpStr = tmpStr.append(" R"+n.toHexString());
			tmpStr = tmpStr.append(",#" + lByte.toHexString());	
		}
		else if(opcode == 0x0e) {
			tmpStr = tmpStr.append(" R"+(BinConvert.byteToLowerNibble(hByte)).toHexString());
			tmpStr = tmpStr.append(",R"+(BinConvert.byteToUpperNibble(lByte)).toHexString());
		}
		else if(opcode == 0x0f) {
			tmpStr = tmpStr.append(" R"+(BinConvert.byteToUpperNibble(lByte)).toHexString());
			tmpStr = tmpStr.append(",R"+(BinConvert.byteToLowerNibble(lByte)).toHexString());
		}
		return tmpStr.toString();
	}

	public static void convMemory(InstMemory iMem, String [] nmeMem) {

		int size = iMem.getCapMemory();

		for(int i = 0; i < size; i++) 
			nmeMem[i] = bin2Nme(iMem.readMemory(i));
	}

}
