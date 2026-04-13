//$Id: MainMenu.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: MainMenu.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Font;

public class MainMenu {

//      Cadenas que conforman el Menu Pull Down
	static final String FILE = "File";
	static final String OBIN = "Open DMN Binary ... ";
	static final String OSRC = "Open DMN Source ... ";
	static final String SSTA = "Save Statistics as ... ";
	static final String QUIT = "Quit";
	static final String OPTS = "Options";
	static final String STAT = "Statistics ... ";
	static final String MNEM = "Show Mnemonics";
	static final String DTAR = "Data Representation";
	static final String DDEC = "Decimal";
	static final String DHEX = "Hexadecimal";
	static final String DBIN = "Binary";
	static final String ASSM = "Assembler";
	static final String ESRC = "Edit New DMN Program ... ";
	static final String ECUR = "Edit Current DMN Program ... ";
	static final String RUN  = "Simulate";
	static final String STOP = " Stop  ";
	static final String PLAY = " Play  ";
	static final String PAUS = " Pause ";
	static final String STEP = " Step  ";
	static final String REST = " RESET ";
	static final String HELP = "Help";
	static final String CONT = "SimDMN Help Page ... ";
	static final String SRCH = "Search For Help On ... ";
	static final String ABUT = "About ... ";

	MenuBar menuBar;
	public CheckboxMenuItem mneChk, 
	       decChk, hexChk, binChk,
	       stChk, plChk, psChk;


//------------------------------------------------------------------------------
// Coloca el Menu
//------------------------------------------------------------------------------
	public MainMenu(Frame frame){

		menuBar = new MenuBar();

		mneChk  = new CheckboxMenuItem(MNEM);
		
		decChk  = new CheckboxMenuItem(DDEC);
		hexChk  = new CheckboxMenuItem(DHEX);
		binChk  = new CheckboxMenuItem(DBIN);

		stChk   = new CheckboxMenuItem(STOP);
		plChk   = new CheckboxMenuItem(PLAY);
		psChk   = new CheckboxMenuItem(PAUS);

		frame.setMenuBar(menuBar);
		menuBar.setFont(new Font("Helvetica", Font.PLAIN, 12));

		Menu m1 = new Menu(FILE);
		m1.add(new MenuItem(OBIN));
		m1.add(new MenuItem(OSRC));
		MenuItem iSSTA;
		m1.add(iSSTA = new MenuItem(SSTA));
		iSSTA.disable();
		m1.addSeparator();
		m1.add(new MenuItem(QUIT));
		Menu m2 = new Menu(OPTS);
		MenuItem iSTAT;
		m2.add(iSTAT = new MenuItem(STAT));
		iSTAT.disable();
		mneChk.setState(true);
		m2.add(mneChk);
		Menu sm1 = new Menu(DTAR);
		hexChk.setState(true);
		sm1.add(decChk);
		sm1.add(hexChk);
		sm1.add(binChk);
		m2.add(sm1);
		Menu ma = new Menu(ASSM);
		ma.add(new MenuItem(ESRC));
		ma.add(new MenuItem(ECUR));
		Menu m3 = new Menu(RUN);
		stChk.setState(true);
		m3.add(stChk);
		m3.add(plChk);
		m3.add(psChk);
		m3.addSeparator();
		m3.add(new MenuItem(STEP));
		m3.addSeparator();
		m3.add(new MenuItem(REST));
		Menu m4 = new Menu(HELP);
		m4.add(new MenuItem(CONT));
//		m4.add(new MenuItem(SRCH));
//		m4.addSeparator();
		m4.add(new MenuItem(ABUT));
		menuBar.add(m1);
		menuBar.add(m2);
		menuBar.add(ma);
		menuBar.add(m3);
		menuBar.add(m4);
	}
}
