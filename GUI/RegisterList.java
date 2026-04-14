//$Id: RegisterList.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: RegisterList.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;
import memory.*;
import binContainer.*;

public class RegisterList extends List {

	RegFileMemory rFile;
	int size;
	private DmnByte [] old = new DmnByte[4];

	public RegisterList(RegFileMemory rf) {
		super();
		for(int i = 0 ; i < 4; i++) old[i] = new DmnByte();
		rFile = rf;
		size = rFile.getCapMemory();
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)rFile.readMemory(i);
			switch(i){
			case 12:
				this.add("CN:" + b.toString());
				break;
			case 13:
				this.add("HA:" + b.toString());
				break;
			case 14:
				this.add("PS:" + b.toString());
				break;
			case 15:
				this.add("PC:" + b.toString());
				break;
			default:
				Nibble d = new Nibble(i);
				this.add("R" + d.toHexString() + ":" + b.toString());
			}
		}
	}
	
	public void refreshItemsDec(int pos) {
		DmnByte b = (DmnByte)rFile.readMemory(pos);
		Nibble d = new Nibble(pos);
		this.replaceItem("R" + d.toHexString() + ":" + b.toString(),pos);
		this.makeVisible(pos);
		this.select(pos);
		for(int i = 12; i < 16; i++){
			b = old[i-12];
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toString(),i);
				break;
			case 15:
				b = (DmnByte)rFile.readMemory(i);
				this.replaceItem("PC:" + b.toString(),i);
				break;
			}
			old[i-12] = (DmnByte)rFile.readMemory(i);
		}
	}


	public void refreshItemsDec() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)rFile.readMemory(i);
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toString(),i);
				break;
			case 15:
				this.replaceItem("PC:" + b.toString(),i);
				break;
			default:
				Nibble d = new Nibble(i);
				this.replaceItem("R" + d.toHexString() + ":" + b.toString(),i);
			}
		}
	}

	public void refreshItemsHex(int pos) {
		DmnByte b = (DmnByte)rFile.readMemory(pos);
		Nibble d = new Nibble(pos);
		this.replaceItem("R" + d.toHexString() + ":" + b.toHexString(),pos);
		this.makeVisible(pos);
		this.select(pos);
		for(int i = 12; i < 16; i++){
			b = old[i-12];
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toHexString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toHexString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toHexString(),i);
				break;
			case 15:
				b = (DmnByte)rFile.readMemory(i);
				this.replaceItem("PC:" + b.toHexString(),i);
				break;
			}
			old[i-12] = (DmnByte)rFile.readMemory(i);
		}
	}

	public void refreshItemsHex() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)rFile.readMemory(i);
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toHexString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toHexString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toHexString(),i);
				break;
			case 15:
				this.replaceItem("PC:" + b.toHexString(),i);
				break;
			default:
				Nibble d = new Nibble(i);
				this.replaceItem("R" + d.toHexString() + ":" + b.toHexString(),i);
			}
		}
	}

	public void refreshItemsBin(int pos) {
		DmnByte b = (DmnByte)rFile.readMemory(pos);
		Nibble d = new Nibble(pos);
		this.replaceItem("R" + d.toHexString() + ":" + b.toBinaryString(),pos);
		this.makeVisible(pos);
		this.select(pos);
		for(int i = 12; i < 16; i++){
			b = old[i-12];
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toBinaryString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toBinaryString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toBinaryString(),i);
				break;
			case 15:
				b = (DmnByte)rFile.readMemory(i);
				this.replaceItem("PC:" + b.toBinaryString(),i);
				break;
			}
			old[i-12] = (DmnByte)rFile.readMemory(i);
		}
	}

	public void refreshItemsBin() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)rFile.readMemory(i);
			switch(i){
			case 12:
				this.replaceItem("CN:" + b.toBinaryString(),i);
				break;
			case 13:
				this.replaceItem("HA:" + b.toBinaryString(),i);
				break;
			case 14:
				this.replaceItem("PS:" + b.toBinaryString(),i);
				break;
			case 15:
				this.replaceItem("PC:" + b.toBinaryString(),i);
				break;
			default:
				Nibble d = new Nibble(i);
				this.replaceItem("R" + d.toHexString() + ":" + b.toBinaryString(),i);
			}
		}
	}
}
