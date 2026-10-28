//$Id: InstMemory.java,v 1.5 1996/11/13 14:09:19 dubuc Exp $

/*
$Log: InstMemory.java,v $
Revision 1.5  1996/11/13 14:09:19  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package memory;

import binContainer.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class InstMemory extends Memory {

	// Programa inicial cuando se carga el simulador, se puede modificar a gusto para probar distintos programas
	int [] inst = {0xd375,0xd405,0xd503,0x0653,0x0834,0x3756,0xf034,0xe370,0xcd00};
	public int nInst = 9;
	static final String DEFAULT_BINARY_NAME = "dmn.bin";

	public InstMemory() {
		capacity = 256;
		locs = new Word[capacity];
		accessPointer = 0;
		for(int i = 0; i < capacity; i++)
			locs[i] = new Word();
	}

	public void initialize() {
		clearMemory();
		if(!loadDefaultBinaryFile())
			loadBuiltInProgram();
	}

	private boolean loadDefaultBinaryFile() {
		File binFile = new File(DEFAULT_BINARY_NAME);
		if(!binFile.isFile())
			return false;

		DataInputStream dis;
		try {
			dis = new DataInputStream(new FileInputStream(binFile));
		}
		catch(IOException e) {
			return false;
		}

		int count = 0;
		try {
			while(count < capacity) {
				int opcode;
				try {
					opcode = dis.readUnsignedShort();
				}
				catch(EOFException e) {
					break;
				}
				locs[count].setValue(opcode);
				count++;
			}
			nInst = count;
			return true;
		}
		catch(IOException e) {
			clearMemory();
			return false;
		}
		finally {
			try {
				dis.close();
			}
			catch(IOException e) {}
		}
	}

	private void loadBuiltInProgram() {
		nInst = inst.length;
		for(int i = 0; i < nInst; i++)
			locs[i].setValue(inst[i]);
	}

	private void clearMemory() {
		for(int i = 0; i < capacity; i++)
			locs[i].clearValue();
	}

}
