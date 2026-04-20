//$Id: DataMemList.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: DataMemList.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
			DmnByte b = (DmnByte)dMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.add(d.toHexString() + ":" + b.toString());
		}
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSelectedLocation();
			}
		});
	}

	public void refreshItemsDec() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)dMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toString(),i);
		}
	}

	public void refreshItemsDec(int pos) {
		DmnByte b = (DmnByte)dMem.readMemory(pos);
		DmnByte d = new DmnByte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toString(),pos);
	}

	public void refreshItemsHex() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)dMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toHexString(),i);
		}
	}

	public void refreshItemsHex(int pos) {
		DmnByte b = (DmnByte)dMem.readMemory(pos);
		DmnByte d = new DmnByte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toHexString(),pos);
	}

	public void refreshItemsBin() {
		for(int i = 0; i < size; i++){
			DmnByte b = (DmnByte)dMem.readMemory(i);
			DmnByte d = new DmnByte(i);
			this.replaceItem(d.toHexString() + ":" + b.toBinaryString(),i);
		}
	}

	public void refreshItemsBinary(int pos) {
		DmnByte b = (DmnByte)dMem.readMemory(pos);
		DmnByte d = new DmnByte(pos);
		this.replaceItem(d.toHexString() + ":" + b.toBinaryString(),pos);
	}

	private void openSelectedLocation() {
		int selectedIndex = getSelectedIndex();
		if ((selectedIndex >= 0) && isIndexSelected(selectedIndex)) {
			ChangeDMemDialog cd = new ChangeDMemDialog(fSim,selectedIndex);
			cd.pack();
			WindowUtil.centerOnScreen(cd);
			cd.setVisible(true);
		}
	}


}

class ChangeDMemDialog extends Dialog {

	final int   DEC = 1;
	final int   HEX = 2;
	final int   BIN = 3;

	DMNFrame fSim;
	TextField bVal;
	int mLoc;
	Button changeButton, cancelButton;

	public ChangeDMemDialog(DMNFrame f,int loc) {
		super(f,"Change Data Memory Location",true);
		fSim = f;
		mLoc = loc;
		this.setLayout(new GridLayout(4,1));
		String locSt = "Change Location at address :" + (new DmnByte(loc)).toHexString();
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
		q.add(changeButton = new Button("Change"));
		q.add(cancelButton = new Button("Cancel"));
		this.setFont(new Font("Fixed",Font.BOLD,14));
		this.add(new Label(locSt,Label.CENTER));
		this.add(new Label(repSt + " representation.",Label.CENTER));
		this.add(p);
		this.add(q);
		registerListeners();
	}

	private void closeDialog(){
		setVisible(false);
		dispose();
	}

	private void registerListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeValue();
			}
		});
	}

	private void changeValue() {
		try{
			int val;
			switch(fSim.dRep){
			case DEC: 
				val = Integer.parseInt(bVal.getText()); 
				if((val < 0) || (val > 0xff))
					putNumber();
				else {
					fSim.simul.dMemory.writeMemory(new DmnByte(val),mLoc);
					fSim.dmlst.refreshItemsDec(mLoc);
					closeDialog();
				}
				break;
			case HEX: 
				val = Integer.parseInt(bVal.getText(),16); 
				if((val < 0) || (val > 0xff))
					putNumber();
				else {
					fSim.simul.dMemory.writeMemory(new DmnByte(val),mLoc);
					fSim.dmlst.refreshItemsHex(mLoc);
					closeDialog();
				}
				break;
			case BIN: 
				val = Integer.parseInt(bVal.getText(),2); 
				if((val < 0) || (val > 0xff))
					putNumber();
				else {
					fSim.simul.dMemory.writeMemory(new DmnByte(val),mLoc);
					fSim.dmlst.refreshItemsBinary(mLoc);
					closeDialog();
				}
				break;
			}
		}
		catch(java.lang.NumberFormatException excp){
			putNumber();
		}
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
