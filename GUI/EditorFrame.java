//$Id: EditorFrame.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: EditorFrame.java,v $
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
import Assembler.*;

public class EditorFrame extends Dialog {

	TextArea editArea;
	List errLst, binLst;
	Assembler assem;
	DMNFrame fSim;
	AssembHelp hlp;
	Button assembleButton, clearButton, closeButton, helpButton;

	public EditorFrame(DMNFrame pr, boolean editOldProgram){
		this(pr);
		if(editOldProgram){
			int nInst = fSim.nInst;
			for(int i = 0; i < nInst; i++) {
				editArea.append(fSim.simul.nmeMem[i] + "\n");
			}
		}
	}

	public EditorFrame(DMNFrame pr, String editSrc) {
		this(pr);
		editArea.append(editSrc);
	}

	public EditorFrame(DMNFrame pr){
		super(pr,"Edit and Assemble DMN Source",true);
		fSim = pr;
		this.setBackground(new Color(0x00,0x8b,0x8b));
		editArea = new TextArea(18,50);
		Panel o = new Panel();
		Panel p = new Panel();
		Panel q = new Panel();
		o.setLayout(new BorderLayout());
		o.add("East",binLst = new List(18,false));
		o.add("Center",editArea);
		q.setLayout(new BorderLayout());
		q.add("North",new Label("Assembler Messages",Label.CENTER));
		q.add("Center",errLst = new List(4,false));
		p.setLayout(new GridLayout(1,4,20,20));
		p.add(assembleButton = new Button("Assemble"));
		p.add(clearButton = new Button("Clear"));
		p.add(closeButton = new Button("Close"));
		p.add(helpButton = new Button("Help"));
		q.add("South",p);
		this.add("Center",o);
		this.add("South",q);
		assem = new Assembler();
		registerListeners();
	}

	private void registerListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		assembleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				assem.assemble(editArea.getText());
				if(assem.assembOk) {
					if (errLst.getItemCount() != 0) errLst.removeAll();
					if (binLst.getItemCount() != 0) binLst.removeAll();
					errLst.add("Successfully Assembly!!");
					String [] assSt = assem.binArray(fSim.dRep);
					for(int i = 0; i < assem.numOp; i++)
						binLst.add(assSt[i]);
					ConfirmAssembDialog cdial = new ConfirmAssembDialog(fSim,assem);
					cdial.pack();
					WindowUtil.centerOnScreen(cdial);
					cdial.setVisible(true);
				}
				else {
					if (errLst.getItemCount() != 0) errLst.removeAll();
					String errMsg = Integer.toString(assem.numErr) + " Error(s) on Assembly.";
					errLst.add(errMsg);
					String [] errSt = assem.errMsgArray();
					for(int i = 0 ; i < assem.numErr; i++)
						errLst.add(errSt[i]);
				}
			}
		});
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hlp = new AssembHelp(fSim);
				hlp.pack();
				WindowUtil.centerOnScreen(hlp);
				hlp.setVisible(true);
			}
		});
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editArea.setText("");
			}
		});
	}

	private void closeDialog() {
		if (hlp != null) hlp.killMe();
		setVisible(false);
		dispose();
	}
}

class AssembHelp extends Dialog {

	static String [] hlpTxt = {
		"DMN instruction set architecture Help.\n\n",
		"Arithmetic and Logical Instructions:\n",
		"ADD Rx,Rx,Rx\n",
		"SUB Rx,Rx,Rx\n",
		"AND Rx,Rx,Rx\n",
		"OR  Rx,Rx,Rx\n",
		"NOT Rx,Rx\n",
		"SHT Rx,Rx,Rx\n\n",
		"Test and Control Instrucctions:\n",
		"ZTS Rx,Rx\n",
		"NTS Rx,Rx\n",
		"JCN @HNUM\n",
		"RTI\n",
		"HLT\n\n",
		"Register Load and Register Store Instructions:\n",
		"LDI Rx,#HNUM\n",
		"LDR Rx,Rx\n",
		"STR Rx,Rx\n\n",
		"Notes:\nRx means R0 to RE (R0 to R14 in hexadecimal) or PS.\n",
		"HNUM is an hexadecimal number with values between 00h and FFh.\n",
		"JCN instruction can use labels as directions.\n",
		"Example:\n",
		"\tLOOP:\tSUB R2,R2,R3\n",
		"\t\tNTS R0,R2\n",
		"\t\tJCN LOOP\n",
		"\t\tHLT\n\n",
		"This assembler is case insensitive.\n",
		"LDI, ldi or Ldi means the same to the assembler."
	};

	Button dismissButton;


	public AssembHelp(Frame f){
		super(f,"Help On DMN Architecture",false);
		TextArea ta = new TextArea(10,50);
		ta.setEditable(false);
		ta.setFont(new Font("Roman",Font.PLAIN,14));
		for(int i = 0; i < 28; i++)
			ta.append(hlpTxt[i]);
		this.add("Center",ta);
		this.add("South",dismissButton = new Button("Dismiss"));
		registerListeners();
	}

	private void registerListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		dismissButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
	}

	public void killMe(){
		closeDialog();
	}
	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}

class ConfirmAssembDialog extends Dialog {

	DMNFrame fSim;
	Assembler assem;
	Button yesButton, noButton;

	public ConfirmAssembDialog(DMNFrame pr, Assembler as){
		super(pr,"Save on Memory",true);
		fSim = pr;
		assem = as;
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout(3,1));
		p2.setFont(new Font("Fixed",Font.BOLD,14));
		p2.add(new Label("Successfully Assembly.",Label.CENTER));
		p2.add(new Label("Do you want to save",Label.CENTER));
		p2.add(new Label("on Instruction Memory?",Label.CENTER));
		Panel p3 = new Panel();
		p3.add(yesButton = new Button("Yes"));
		p3.add(noButton = new Button("No"));
		this.add("Center",p2);
		this.add("South",p3);
		registerListeners();
	}

	private void registerListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					assem.putOnMemory(fSim.simul.iMemory);
					fSim.simul.nmeConvMem();
					if (fSim.nmeOn) fSim.imlst.refreshItemsNme();
					else fSim.instMemRefresh();
					fSim.nInst = assem.numOp;
					closeDialog();
			}
		});
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}
