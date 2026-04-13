//$Id: DataMemList.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: DataMemList.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;
import memory.*;
import binContainer.*;

public class DataMemList extends List {

	DataMemory dMem;
	DMNFrame fSim;
	int size;

	public DataMemList(DMNFrame f,DataMemory dm) {
		super();
		fSim = f;
		dMem = dm;
		size = dMem.getCapMemory();
		for(int i = 0; i < size; i++){
			Byte b = (Byte)dMem.readMemory(i);
			Byte d = new Byte(i);
			this.addItem(d.toHexString() + ":" + b.toString());
		}
	}

	public void refreshItemsDec() {
		for(int i = 0; i < size; i++){
			Byte b = (Byte)dMem.readMemory(i);
			Byte d = new Byte(i);
			this.replaceItem(d.toHexString() + ":" + b.toString(),i);
		}
	}

	public void refreshItemsDec(int pos) {
		Byte b = (Byte)dMem.readMemory(pos);
		Byte d = new Byte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toString(),pos);
	}

	public void refreshItemsHex() {
		for(int i = 0; i < size; i++){
			Byte b = (Byte)dMem.readMemory(i);
			Byte d = new Byte(i);
			this.replaceItem(d.toHexString() + ":" + b.toHexString(),i);
		}
	}

	public void refreshItemsHex(int pos) {
		Byte b = (Byte)dMem.readMemory(pos);
		Byte d = new Byte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toHexString(),pos);
	}

	public void refreshItemsBin() {
		for(int i = 0; i < size; i++){
			Byte b = (Byte)dMem.readMemory(i);
			Byte d = new Byte(i);
			this.replaceItem(d.toHexString() + ":" + b.toBinaryString(),i);
		}
	}

	public void refreshItemsBinary(int pos) {
		Byte b = (Byte)dMem.readMemory(pos);
		Byte d = new Byte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toBinaryString(),pos);
	}

	public boolean handleEvent(Event event) {
		switch(event.id) {
		case Event.LIST_SELECT:{
		}break;
		case Event.LIST_DESELECT:{
		}break;
		case Event.ACTION_EVENT:{
			if(event.target instanceof List) {
				if(isSelected(getSelectedIndex())){
					ChangeDMemDialog cd = new ChangeDMemDialog(fSim,getSelectedIndex());
					cd.pack();
					cd.show();
				}
			}
		}break;
		default:
		}
		return super.handleEvent(event);
	}    


}

class ChangeDMemDialog extends Dialog {

	final int   DEC = 1;
	final int   HEX = 2;
	final int   BIN = 3;

	DMNFrame fSim;
	TextField bVal;
	int mLoc;

	public ChangeDMemDialog(DMNFrame f,int loc) {
		super(f,"Change Data Memory Location",true);
		fSim = f;
		mLoc = loc;
		this.setLayout(new GridLayout(4,1));
		String locSt = "Change Location at address :" + (new Byte(loc)).toHexString();
		String repSt = new String();
		bVal = new TextField();
		switch(fSim.dRep) {
			case DEC: {
				repSt = "Decimal";
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toString());
			}break;
			case HEX:{
				repSt = "Hexadecimal";
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toHexString());
			}break;
			case BIN:{
				repSt = "Binary";
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toBinaryString());
			}break;
		}
		bVal.selectAll();
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,2));
		Panel q = new Panel();
		p.add(new Label("Enter New Value:"));
		p.add(bVal);
		q.add(new Button("Change"));
		q.add(new Button("Cancel"));
		this.setFont(new Font("Fixed",Font.BOLD,14));
		this.add(new Label(locSt,Label.CENTER));
		this.add(new Label(repSt + " representation.",Label.CENTER));
		this.add(p);
		this.add(q);
	}

	protected void finalize(){
		this.hide();
	}

	public boolean action(Event e, Object w){
		if("Cancel".equals(e.arg)){
			this.finalize();
		}
		else if("Change".equals(e.arg)){
			try{
				int val;
				switch(fSim.dRep){
				case DEC: 
					val = Integer.parseInt(bVal.getText()); 
					if((val < 0) || (val > 0xff))
						putNumber();
					else {
						fSim.simul.dMemory.writeMemory(new Byte(val),mLoc);
						fSim.dmlst.refreshItemsDec(mLoc);
						this.finalize();
					}
					break;
				case HEX: 
					val = Integer.parseInt(bVal.getText(),16); 
					if((val < 0) || (val > 0xff))
						putNumber();
					else {
						fSim.simul.dMemory.writeMemory(new Byte(val),mLoc);
						fSim.dmlst.refreshItemsHex(mLoc);
						this.finalize();
					}
					break;
				case BIN: 
					val = Integer.parseInt(bVal.getText(),2); 
					if((val < 0) || (val > 0xff))
						putNumber();
					else {
						fSim.simul.dMemory.writeMemory(new Byte(val),mLoc);
						fSim.dmlst.refreshItemsBinary(mLoc);
						this.finalize();
					}
					break;
				}
			}
			catch(java.lang.NumberFormatException excp){
				putNumber();
			}
		}
		return true;
	}

	private void putNumber() {
		switch(fSim.dRep) {
			case DEC: {
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toString());
			}break;
			case HEX:{
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toHexString());
			}break;
			case BIN:{
				bVal.setText(fSim.simul.dMemory.readMemory(mLoc).toBinaryString());
			}break;
		}
		bVal.selectAll();
	}
}
