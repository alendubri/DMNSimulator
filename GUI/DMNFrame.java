//DMNFrame.java,v 1.6 1996/11/14 21:32:28 dubuc Exp
//------------------------------------------------------------------------------
// Implementacion de la interface grafica en Java del Simulador del DMN V6
// Por Alberto Enrique Dubuc Brice~no (Alendubri) 
// Agosto,Septiembre,Octubre  1996
//------------------------------------------------------------------------------

/*
DMNFrame.java,v
Revision 1.6  1996/11/14 21:32:28  dubuc
quitado problema del refresco del archivo de registro.

Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

Revision 1.3  1996/10/30 01:43:21  dubuc
Cambiado el mensaje de 'About' para que siga las revisiones del RCS

Revision 1.2  1996/10/30 01:39:25  dubuc
Agregado del dialogo para el ensamblador, y el de salida de la aplicacion
Refinamiento de botones de interrupcion, mejora en el refresco del
Archivo de registros. Definidos nuevos eventos.

Revision 1.1  1996/10/27 16:23:24  dubuc
Initial revision

*/

package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import binContainer.*;
import memory.*;
import Simul.*;
import Assembler.Assembler;
import java.net.*;
import java.io.*;

//==============================================================================
// Clase DMNFrame
//==============================================================================
public class DMNFrame extends Frame {

//------------------------------------------------------------------------------
// Definicion de Constantes
//------------------------------------------------------------------------------

	final String OKDL = "Ok";

	final String ABOUT_LAB1 = "CEMISID";
	final String ABOUT_LAB2 = "Universidad de Los Andes";

	final String ABOUT_LAB3 = "Revision: 1.6";
	final String ABOUT_LAB4 = "Author: dubuc";
	final String PROJECT_HOME_URL = "https://github.com/alendubri/DMNSimulator";

	final int	DEC = 1;
	final int	HEX = 2;
	final int	BIN = 3;	
	final int	RUN_STOP = 1;
	final int	RUN_PLAY = 2;
	final int	RUN_PAUSE = 3;

//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Atributos de la Clase (Interface)
//------------------------------------------------------------------------------

	public boolean inAnApplet = false;
	public URL urlBase, nFile;

	Simulator	simul;

	public int	dRep = HEX;
	boolean nmeOn = true;
	boolean previus = true;

	MainMenu mainMenu;
	Panel [] panels    = new Panel[7];
	Dialog aboutDl;
	Button aboutOkButton;
	PCQPanel	infpan;
	RegisterPanel [] memView = new RegisterPanel[3];
	RegisterList rlist;
	DataMemList  dmlst;
	InstMemList  imlst;
	QueuePanel	[] qpan = new QueuePanel[4];
	DMNFigPanel archPan;
	StagesPanel stPan;
	public ClockCanvas clkCvn;
	public int nInst;
	DataInputStream dis;
	FNFDialog fnfdial;
	int runMode = RUN_STOP;

//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Metodos de la Clase (Implementacion)
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Constructor
//------------------------------------------------------------------------------
	public DMNFrame(){
		super("SimDMN");

		this.setSize(600,600);
		this.setBackground(new Color(0x00,0x8b,0x8b));
		this.setForeground(Color.black);
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));

		this.simul = new Simulator();
		this.simul.iMemory.initialize();
		this.nInst = this.simul.iMemory.nInst;
		this.rlist = new RegisterList(this.simul.rFile);
		this.rlist.setBackground(Color.white);
		this.rlist.setForeground(Color.black);
		this.dmlst = new DataMemList(this,this.simul.dMemory);
		this.dmlst.setBackground(Color.white);
		this.dmlst.setForeground(Color.black);
		this.simul.nmeConvMem();
		this.imlst = new InstMemList(this.simul.iMemory, this.simul.nmeMem);
		this.imlst.setBackground(Color.white);
		this.imlst.setForeground(Color.black);

		this.mainMenu = new MainMenu(this);

		this.setInitialPanelGeometry(); 
		this.setInitialComponents();
		this.setDialogs();
		this.registerListeners();
	}
//------------------------------------------------------------------------------

	private void setStages(){
		String [] stg = new String[4];
		int pc = simul.pcReg.intValue();
		if(pc == 0) {
			stg[3] = simul.nmeMem[0];
			stg[2] = simul.nmeMem[255];
			stg[1] = simul.nmeMem[254];
			stg[0] = simul.nmeMem[253];
		} else if(pc == 1) {
			stg[3] = simul.nmeMem[1];
			stg[2] = simul.nmeMem[0];
			stg[1] = simul.nmeMem[255];
			stg[0] = simul.nmeMem[254];
		} else if(pc == 2) {
			stg[3] = simul.nmeMem[2];
			stg[2] = simul.nmeMem[1];
			stg[1] = simul.nmeMem[0];
			stg[0] = simul.nmeMem[255];
		} else {
			stg[3] = simul.nmeMem[pc];
			stg[2] = simul.nmeMem[pc-1];
			stg[1] = simul.nmeMem[pc-2];
			stg[0] = simul.nmeMem[pc-3];
		}
		this.stPan.setTxt(stg);
	}


//------------------------------------------------------------------------------
// Inicializa la geometria de los componentes iniciales
//------------------------------------------------------------------------------
	private void setInitialPanelGeometry() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(2,2,2,2);
		c.gridheight = 3;
		panels[0] = addPanel(gridbag,c);
		c.gridheight = 1;
		panels[1] = addPanel(gridbag,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		panels[2] = addPanel(gridbag,c);
		panels[3] = addPanel(gridbag,c);
		c.gridwidth = 1;
		panels[4] = addPanel(gridbag,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		panels[5] = addPanel(gridbag,c);
	}
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Inicializa el contenido de los componentes iniciales
//------------------------------------------------------------------------------
	private void setInitialComponents(){

		memView[2] = new RegisterPanel("Registers",new Dimension(150,190),rlist);
		panels[0].add(archPan = new DMNFigPanel(memView[2]));

		panels[1].add(memView[0] = new RegisterPanel("Inst. Mem.",new Dimension(200,175),imlst));

		panels[2].add(memView[1] = new RegisterPanel("Data Mem.",new Dimension(200,175),dmlst));

		panels[3].setLayout(new BorderLayout());
		panels[3].add("Center",stPan = new StagesPanel(clkCvn = new ClockCanvas(this,simul)));

		panels[4].setLayout(new GridLayout(1,3,5,0));
		panels[4].add(qpan[2] = new QueuePanel("EX",3));
		qpan[2].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		panels[4].add(qpan[3] = new QueuePanel("WB",3));
		panels[4].add(qpan[0] = new QueuePanel("RSA",2,3,QueuePanel.DOWN));
		panels[4].add(qpan[1] = new QueuePanel("RSB",2,3,QueuePanel.DOWN));

		panels[5].setLayout(new BorderLayout());
		panels[5].add("Center",infpan = new PCQPanel());
	}

//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Inicializa los Dialogos por omision
//------------------------------------------------------------------------------
	private void setDialogs(){

		aboutDl = new Dialog(this,"About ...",false);
		Panel p1 = new Panel();
		p1.setLayout(new GridLayout(7,1));
		p1.add(new Label(ABOUT_LAB1,Label.CENTER));
		p1.add(new Label(ABOUT_LAB2,Label.CENTER));
		p1.add(new Panel());
		p1.add(new Label(ABOUT_LAB3,Label.CENTER));
		p1.add(new Label(ABOUT_LAB4,Label.CENTER));
		aboutDl.add("North",p1);
		aboutDl.add("South",aboutOkButton = new Button(OKDL));
		aboutDl.setSize(250,200);
		aboutDl.setResizable(false);
	}
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Crea un Panel dentro del espacio asignado en gridbag con las resticciones c
//------------------------------------------------------------------------------
	private Panel addPanel(
				GridBagLayout gridbag,
				GridBagConstraints c) {
		Panel panel = new Panel();
		gridbag.setConstraints(panel, c);
		add(panel);
		return panel;
	}
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Metodo de Inicializacion
//------------------------------------------------------------------------------
	public void init() {
		setResizable(false);
		dataMemRefresh();
		rFileRefresh();
	}
//------------------------------------------------------------------------------

	private void quitConfirm() {
		QuitDialog qDialog = new QuitDialog(this,this);
		qDialog.setSize(250,150);
		WindowUtil.centerOnScreen(qDialog);
		qDialog.setVisible(true);
	}

	private void registerListeners() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quitConfirm();
			}
		});

		aboutOkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutDl.setVisible(false);
			}
		});
		aboutDl.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				aboutDl.setVisible(false);
			}
		});

		mainMenu.quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitConfirm();
			}
		});
		mainMenu.aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WindowUtil.centerOnScreen(aboutDl);
				aboutDl.setVisible(true);
			}
		});
		mainMenu.contItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openProjectHomePage();
			}
		});
		mainMenu.osrcItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSourceDialog();
			}
		});
		mainMenu.obinItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openBinaryDialog();
			}
		});
		mainMenu.esrcItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openEditor(false);
			}
		});
		mainMenu.ecurItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openEditor(true);
			}
		});
		mainMenu.stepItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepSimulation();
			}
		});
		mainMenu.resetItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetSimulation();
			}
		});

		mainMenu.mneChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				nmeOn = mainMenu.mneChk.getState();
				refreshInstructionMemoryView();
			}
		});
		mainMenu.decChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleDataRepresentationSelection(DEC, e.getStateChange());
			}
		});
		mainMenu.hexChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleDataRepresentationSelection(HEX, e.getStateChange());
			}
		});
		mainMenu.binChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleDataRepresentationSelection(BIN, e.getStateChange());
			}
		});
		mainMenu.stChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleRunMenuSelection(RUN_STOP, e.getStateChange());
			}
		});
		mainMenu.plChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleRunMenuSelection(RUN_PLAY, e.getStateChange());
			}
		});
		mainMenu.psChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleRunMenuSelection(RUN_PAUSE, e.getStateChange());
			}
		});

		stPan.cbSim[0].addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (stPan.cbSim[0].getState()) setRunMode(RUN_STOP);
			}
		});
		stPan.cbSim[1].addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (stPan.cbSim[1].getState()) setRunMode(RUN_PLAY);
			}
		});
		stPan.cbSim[2].addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (stPan.cbSim[2].getState()) setRunMode(RUN_PAUSE);
			}
		});
		stPan.stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepSimulation();
			}
		});
		stPan.resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetSimulation();
			}
		});
		stPan.intrcv.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				simul.intrAck();
				repaint();
			}
		});
	}

	private void handleDataRepresentationSelection(int representation, int stateChange) {
		if (stateChange == ItemEvent.SELECTED) {
			setDataRepresentation(representation);
		} else {
			syncDataRepresentationMenu();
		}
	}

	private void handleRunMenuSelection(int mode, int stateChange) {
		if (stateChange == ItemEvent.SELECTED) {
			setRunMode(mode);
		} else {
			syncRunControls();
		}
	}

	private void openSourceDialog() {
		if (!inAnApplet) {
			FileDialog fd = new FileDialog(this,"Open DMN Source",FileDialog.LOAD);
			fd.pack();
			fd.setVisible(true);
			if((fd.getDirectory()!= null)&&(fd.getFile()!=null)) {
				if(createDataInputStream(fd.getDirectory(),fd.getFile()))
					openDMNSource();
				else
					showFileOpenError("Couldn't open DMN Source");
			}
		}
		else {
			NetFileDialog nfdial = new NetFileDialog(this,"Open DMN Source",false);
			nfdial.pack();
			WindowUtil.centerOnScreen(nfdial);
			nfdial.setVisible(true);
		}
	}

	private void openProjectHomePage() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop.browse(new URI(PROJECT_HOME_URL));
					return;
				}
			}
		}
		catch (java.io.IOException e) {
		}
		catch (java.net.URISyntaxException e) {
		}
		showFileOpenError("Couldn't open project web page");
	}

	private void openBinaryDialog() {
		if (!inAnApplet) {
			FileDialog fd = new FileDialog(this,"Open DMN Binary",FileDialog.LOAD);
			fd.pack();
			fd.setVisible(true);
			if((fd.getDirectory()!= null)&&(fd.getFile()!=null)) {
				if(createDataInputStream(fd.getDirectory(),fd.getFile()))
					openDMNBinary();
				else
					showFileOpenError("Couldn't open DMN Binary");
			}
		}
		else {
			NetFileDialog nfdial = new NetFileDialog(this,"Open DMN Binary",true);
			nfdial.pack();
			WindowUtil.centerOnScreen(nfdial);
			nfdial.setVisible(true);
		}
	}

	private void openEditor(boolean currentProgram) {
		EditorFrame edFrame;
		if (currentProgram) edFrame = new EditorFrame(this,true);
		else edFrame = new EditorFrame(this);
		edFrame.pack();
		WindowUtil.centerOnScreen(edFrame);
		edFrame.setVisible(true);
	}

	private void showFileOpenError(String message) {
		fnfdial = new FNFDialog(this,message);
		fnfdial.pack();
		WindowUtil.centerOnScreen(fnfdial);
		fnfdial.setVisible(true);
	}

	private void setRunMode(int mode) {
		runMode = mode;
		switch(mode) {
		case RUN_PLAY:
			this.clkCvn.start();
			this.repaint();
			break;
		case RUN_STOP:
		case RUN_PAUSE:
			this.clkCvn.stop();
			break;
		default:
			return;
		}
		syncRunControls();
	}

	private void syncRunControls() {
		mainMenu.stChk.setState(runMode == RUN_STOP);
		mainMenu.plChk.setState(runMode == RUN_PLAY);
		mainMenu.psChk.setState(runMode == RUN_PAUSE);
		stPan.cbSim[0].setState(runMode == RUN_STOP);
		stPan.cbSim[1].setState(runMode == RUN_PLAY);
		stPan.cbSim[2].setState(runMode == RUN_PAUSE);
	}

	private void stepSimulation() {
		setRunMode(RUN_STOP);
		this.clkCvn.step();
	}

	private void resetSimulation() {
		simul.reset();
		this.repaint();
		this.rFileRefresh();
	}

	private void setDataRepresentation(int representation) {
		dRep = representation;
		syncDataRepresentationMenu();
		switch(dRep) {
			case DEC:
				rlist.refreshItemsDec();
				break;
			case HEX:
				rlist.refreshItemsHex();
				break;
			case BIN:
				rlist.refreshItemsBin();
				break;
		}
		this.repaint();
		this.dataMemRefresh();
		if (!nmeOn) this.instMemRefresh();
	}

	private void syncDataRepresentationMenu() {
		mainMenu.decChk.setState(dRep == DEC);
		mainMenu.hexChk.setState(dRep == HEX);
		mainMenu.binChk.setState(dRep == BIN);
	}

	private void refreshInstructionMemoryView() {
		mainMenu.mneChk.setState(nmeOn);
		this.repaint();
		if (nmeOn) this.imlst.refreshItemsNme();
		else this.instMemRefresh();
	}
	
	public boolean createDataInputStream(String dir,String file){
		if((dir != null)&&(file != null)){
			File iFile = new File(dir,file);
			try{
				dis = new DataInputStream(new FileInputStream(iFile));
				return true;
			}
			catch(java.io.FileNotFoundException e){
				return false;
			}
		}
		else return false;
	}

	public boolean createDataInputStream(String file){
		if(file != null){
			try{

				nFile = urlBase.toURI().resolve(file).toURL();
				dis = new DataInputStream(nFile.openStream());
				return true;
			}
			catch(java.net.URISyntaxException e){
				return false;
			}
			catch(java.net.MalformedURLException e){
				return false;
			}
			catch(java.io.IOException e) {
				return false;
			}
		}
		else return false;
	}
	
	public boolean openDMNBinary(){
		try{
			int b,i = 0;

			while(true){
				try {
					b = dis.readUnsignedShort();
					Word w = new Word(b);
					if(i <= 0xff)
						simul.iMemory.writeMemory(w,i);
					i++;
				}
				catch (java.io.EOFException e) {
					break;
				}
			}
			if(i <= 0xff) { 
				nInst = i;
				for(int j = i; j <= 0xff; j++)
					simul.iMemory.writeMemory(new Word(0),j);
			}
			else nInst = 0x100;
			simul.nmeConvMem();
			if (nmeOn) imlst.refreshItemsNme();
			else instMemRefresh();

			dis.close();
			return true;
		}
		catch(java.io.IOException e){
			return false;
		}
	}

	public boolean openDMNSource(){
		StringBuffer sb = new StringBuffer();
		try{
			int i = 0;
			String inputLine;
			BufferedReader reader = new BufferedReader(new InputStreamReader(dis));

			while((inputLine = reader.readLine()) != null){
				if(i <= 0xff)
					sb.append(inputLine + "\n");
				i++;
			}

			reader.close();
		}
		catch(java.io.IOException e){
			return false;
		}
		EditorFrame edSrc = new EditorFrame(this,sb.toString());
		edSrc.pack();
		WindowUtil.centerOnScreen(edSrc);
		edSrc.setVisible(true);
		return true;
	}

	public void theEnd(){
		closeFrame();
	}

	private void closeFrame(){
		if (inAnApplet) {
			setVisible(false);
			dispose();
		} else {
			System.exit(0);
		}
	}
//------------------------------------------------------------------------------

	public void paint(Graphics g) {

		this.setStages();

		int pc = simul.pcReg.intValue();
		imlst.makeVisible(pc);
		imlst.select(pc);

		archPan.depL1(simul.depUnit.depL1);
		archPan.depL2(simul.depUnit.depL2);
		archPan.intrEn(simul.alu.intr);

		switch(dRep) {
			case DEC:
				rlist.refreshItemsDec(simul.wb4.intValue());
				infpan.tf[0].setText(simul.pcReg.toString());
				infpan.tf[4].setText(simul.alu.PCR.toString());
				archPan.putX(simul.xReg.toString());
				archPan.putA(simul.depUnit.aReg[1].toString());
				archPan.putB(simul.depUnit.bReg[1].toString());
				if(!nmeOn) this.decQueuesRefresh();
				if(simul.wmem) {
					dmlst.refreshItemsDec(simul.mempos);
					dmlst.makeVisible(simul.mempos);
					dmlst.select(simul.mempos);
				}
				break;
			case HEX:
				rlist.refreshItemsHex(simul.wb4.intValue());
				infpan.tf[0].setText(simul.pcReg.toHexString());
				infpan.tf[4].setText(simul.alu.PCR.toHexString());
				archPan.putX(simul.xReg.toHexString());
				archPan.putA(simul.depUnit.aReg[1].toHexString());
				archPan.putB(simul.depUnit.bReg[1].toHexString());
				if(!nmeOn) this.hexQueuesRefresh();
				if(simul.wmem) {
					dmlst.refreshItemsHex(simul.mempos);
					dmlst.makeVisible(simul.mempos);
					dmlst.select(simul.mempos);
				}
				break;
			case BIN:
				rlist.refreshItemsBin(simul.wb4.intValue());
				infpan.tf[0].setText(simul.pcReg.toBinaryString());
				infpan.tf[4].setText(simul.alu.PCR.toBinaryString());
				archPan.putX(simul.xReg.toBinaryString());
				archPan.putA(simul.depUnit.aReg[1].toBinaryString());
				archPan.putB(simul.depUnit.bReg[1].toBinaryString());
				if(!nmeOn) this.binQueuesRefresh();
				if(simul.wmem) {
					dmlst.refreshItemsBinary(simul.mempos);
					dmlst.makeVisible(simul.mempos);
					dmlst.select(simul.mempos);
				}
				break;
		}

		for(int i = 1; i < 4;i++)
		switch(dRep) {
			case DEC:
				infpan.tf[i].setText((simul.pcQueue.seeElement(i-1)).toString());
				break;
			case HEX:
				infpan.tf[i].setText((simul.pcQueue.seeElement(i-1)).toHexString());
				break;
			case BIN:
				infpan.tf[i].setText((simul.pcQueue.seeElement(i-1)).toBinaryString());
				break;
		}
		for(int i = 0; i < 4; i++){
			infpan.pc[i].active = simul.pcState[i];
			infpan.pc[i].repaint();
		}

		if(stPan.intrcv.active != simul.alu.intr){ 
			stPan.intrcv.active = !stPan.intrcv.active;
			stPan.intrcv.repaint();
		}

		if(nmeOn) this.nmeQueuesRefresh();

		archPan.repaint();
	}

	private void nmeQueuesRefresh() {
		Nibble a = BinConvert.byteToUpperNibble(simul.alu.rldi.seeTopQueue());
		Nibble b = BinConvert.byteToLowerNibble(simul.alu.rldi.seeTopQueue());

		if(simul.depUnit.depAL2) qpan[0].ntf[0].setBackground(Color.red);
		else qpan[0].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[0].ntf[0].setText("R"+a.toHexString());

		if(simul.depUnit.depAL1) {
			qpan[0].ntf[1].setBackground(Color.blue);
			qpan[0].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[0].ntf[1].setBackground(Color.white);
			qpan[0].ntf[1].setForeground(Color.black);
		}
		qpan[0].ntf[1].setText("R"+(simul.depUnit.sla).toHexString());
		
		if(simul.depUnit.depBL2) qpan[1].ntf[0].setBackground(Color.red);
		else qpan[1].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[1].ntf[0].setText("R"+b.toHexString());

		if(simul.depUnit.depBL1) {
			qpan[1].ntf[1].setBackground(Color.blue);
			qpan[1].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[1].ntf[1].setBackground(Color.white);
			qpan[1].ntf[1].setForeground(Color.black);
		}
		qpan[1].ntf[1].setText("R"+(simul.depUnit.slb).toHexString());

		qpan[2].ntf[0].setText(NMETransf.NMES[simul.alu.wbQ3p.intValue()]);
		qpan[2].ntf[1].setText(NMETransf.NMES[(simul.exQueue.seeTopQueue()).intValue()]);
		qpan[2].ntf[2].setText(NMETransf.NMES[(simul.exQueue.seeElement(0)).intValue()]);

		if(simul.depUnit.depL2) {
			qpan[3].ntf[0].setBackground(Color.red);
			qpan[3].ntf[0].setForeground(Color.black);
		}
		else if (simul.depUnit.depL1){
			qpan[3].ntf[0].setBackground(Color.blue);
			qpan[3].ntf[0].setForeground(Color.white);
		}
		else {
			qpan[3].ntf[0].setBackground(Color.white);
			qpan[3].ntf[0].setForeground(Color.black);
		}

		qpan[3].ntf[0].setText("R"+(simul.wbQueue.seeTopQueue()).toHexString());
		qpan[3].ntf[1].setText("R"+(simul.wbQueue.seeElement(1)).toHexString());
		qpan[3].ntf[2].setText("R"+(simul.wbQueue.seeElement(0)).toHexString());
	}

	private void decQueuesRefresh() {
		Nibble a = BinConvert.byteToUpperNibble(simul.alu.rldi.seeTopQueue());
		Nibble b = BinConvert.byteToLowerNibble(simul.alu.rldi.seeTopQueue());

		if(simul.depUnit.depAL2) qpan[0].ntf[0].setBackground(Color.red);
		else qpan[0].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[0].ntf[0].setText(a.toString());

		if(simul.depUnit.depAL1) {
			qpan[0].ntf[1].setBackground(Color.blue);
			qpan[0].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[0].ntf[1].setBackground(Color.white);
			qpan[0].ntf[1].setForeground(Color.black);
		}
		qpan[0].ntf[1].setText(simul.depUnit.sla.toString());

		if(simul.depUnit.depBL2) qpan[1].ntf[0].setBackground(Color.red);
		else qpan[1].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[1].ntf[0].setText(b.toString());

		if(simul.depUnit.depBL1) {
			qpan[1].ntf[1].setBackground(Color.blue);
			qpan[1].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[1].ntf[1].setBackground(Color.white);
			qpan[1].ntf[1].setForeground(Color.black);
		}
		qpan[1].ntf[1].setText(simul.depUnit.slb.toString());

		qpan[2].ntf[0].setText(simul.alu.wbQ3p.toString());
		qpan[2].ntf[1].setText(simul.exQueue.seeTopQueue().toString());
		qpan[2].ntf[2].setText(simul.exQueue.seeElement(0).toString());

		if(simul.depUnit.depL2) {
			qpan[3].ntf[0].setBackground(Color.red);
			qpan[3].ntf[0].setForeground(Color.black);
		}
		else if (simul.depUnit.depL1){
			qpan[3].ntf[0].setBackground(Color.blue);
			qpan[3].ntf[0].setForeground(Color.white);
		}
		else {
			qpan[3].ntf[0].setBackground(Color.white);
			qpan[3].ntf[0].setForeground(Color.black);
		}

		qpan[3].ntf[0].setText(simul.wbQueue.seeTopQueue().toString());
		qpan[3].ntf[1].setText(simul.wbQueue.seeElement(1).toString());
		qpan[3].ntf[2].setText(simul.wbQueue.seeElement(0).toString());
	}

	private void hexQueuesRefresh() {
		Nibble a = BinConvert.byteToUpperNibble(simul.alu.rldi.seeTopQueue());
		Nibble b = BinConvert.byteToLowerNibble(simul.alu.rldi.seeTopQueue());

		if(simul.depUnit.depAL2) qpan[0].ntf[0].setBackground(Color.red);
		else qpan[0].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[0].ntf[0].setText(a.toHexString());

		if(simul.depUnit.depAL1) {
			qpan[0].ntf[1].setBackground(Color.blue);
			qpan[0].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[0].ntf[1].setBackground(Color.white);
			qpan[0].ntf[1].setForeground(Color.black);
		}
		qpan[0].ntf[1].setText(simul.depUnit.sla.toHexString());

		if(simul.depUnit.depBL2) qpan[1].ntf[0].setBackground(Color.red);
		else qpan[1].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[1].ntf[0].setText(b.toHexString());

		if(simul.depUnit.depBL1) {
			qpan[1].ntf[1].setBackground(Color.blue);
			qpan[1].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[1].ntf[1].setBackground(Color.white);
			qpan[1].ntf[1].setForeground(Color.black);
		}
		qpan[1].ntf[1].setText(simul.depUnit.slb.toHexString());

		qpan[2].ntf[0].setText(simul.alu.wbQ3p.toHexString());
		qpan[2].ntf[1].setText(simul.exQueue.seeTopQueue().toHexString());
		qpan[2].ntf[2].setText(simul.exQueue.seeElement(0).toHexString());

		if(simul.depUnit.depL2) {
			qpan[3].ntf[0].setBackground(Color.red);
			qpan[3].ntf[0].setForeground(Color.black);
		}
		else if (simul.depUnit.depL1){
			qpan[3].ntf[0].setBackground(Color.blue);
			qpan[3].ntf[0].setForeground(Color.white);
		}
		else {
			qpan[3].ntf[0].setBackground(Color.white);
			qpan[3].ntf[0].setForeground(Color.black);
		}

		qpan[3].ntf[0].setText(simul.wbQueue.seeTopQueue().toHexString());
		qpan[3].ntf[1].setText(simul.wbQueue.seeElement(1).toHexString());
		qpan[3].ntf[2].setText(simul.wbQueue.seeElement(0).toHexString());
	}

	private void binQueuesRefresh() {
		Nibble a = BinConvert.byteToUpperNibble(simul.alu.rldi.seeTopQueue());
		Nibble b = BinConvert.byteToLowerNibble(simul.alu.rldi.seeTopQueue());
	
		if(simul.depUnit.depAL2) qpan[0].ntf[0].setBackground(Color.red);
		else qpan[0].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[0].ntf[0].setText(a.toBinaryString());

		if(simul.depUnit.depAL1) {
			qpan[0].ntf[1].setBackground(Color.blue);
			qpan[0].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[0].ntf[1].setBackground(Color.white);
			qpan[0].ntf[1].setForeground(Color.black);
		}
		qpan[0].ntf[1].setText(simul.depUnit.sla.toBinaryString());

		if(simul.depUnit.depBL2) qpan[1].ntf[0].setBackground(Color.red);
		else qpan[1].ntf[0].setBackground(new Color(0x00,0x8b,0x8b));
		qpan[1].ntf[0].setText(b.toBinaryString());

		if(simul.depUnit.depBL1) {
			qpan[1].ntf[1].setBackground(Color.blue);
			qpan[1].ntf[1].setForeground(Color.white);
		}
		else {
			qpan[1].ntf[1].setBackground(Color.white);
			qpan[1].ntf[1].setForeground(Color.black);
		}
		qpan[1].ntf[1].setText(simul.depUnit.slb.toBinaryString());

		qpan[2].ntf[0].setText(simul.alu.wbQ3p.toBinaryString());
		qpan[2].ntf[1].setText(simul.exQueue.seeTopQueue().toBinaryString());
		qpan[2].ntf[2].setText(simul.exQueue.seeElement(0).toBinaryString());

		if(simul.depUnit.depL2) {
			qpan[3].ntf[0].setBackground(Color.red);
			qpan[3].ntf[0].setForeground(Color.black);
		}
		else if (simul.depUnit.depL1){
			qpan[3].ntf[0].setBackground(Color.blue);
			qpan[3].ntf[0].setForeground(Color.white);
		}
		else {
			qpan[3].ntf[0].setBackground(Color.white);
			qpan[3].ntf[0].setForeground(Color.black);
		}

		qpan[3].ntf[0].setText(simul.wbQueue.seeTopQueue().toBinaryString());
		qpan[3].ntf[1].setText(simul.wbQueue.seeElement(1).toBinaryString());
		qpan[3].ntf[2].setText(simul.wbQueue.seeElement(0).toBinaryString());
	}



	private void dataMemRefresh(){
		switch(dRep) {
			case DEC:
				dmlst.refreshItemsDec();
				break;
			case HEX:
				dmlst.refreshItemsHex();
				break;
			case BIN:
				dmlst.refreshItemsBin();
				break;
		}
	}

	public void instMemRefresh(){
		switch(dRep) {
			case DEC:
				imlst.refreshItemsDec();
				break;
			case HEX:
				imlst.refreshItemsHex();
				break;
			case BIN:
				imlst.refreshItemsBin();
				break;
		}
	}

	private void rFileRefresh(){
		switch(dRep) {
			case DEC:
				rlist.refreshItemsDec();
				break;
			case HEX:
				rlist.refreshItemsHex();
				break;
			case BIN:
				rlist.refreshItemsBin();
				break;
		}
	}
//------------------------------------------------------------------------------

}


//==============================================================================
// Fin de la Clase DMNFrame
//==============================================================================

class QuitDialog extends Dialog {

	DMNFrame parent;
	Button yesButton, noButton;

	public QuitDialog(Frame fp, DMNFrame rp) {
		super(fp,"Confirm SimDMN exit", true);
		parent = rp;
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout(4,1));
		p2.setFont(new Font("Fixed",Font.BOLD,16));
		p2.add(new Panel());
		p2.add(new Label("Do you really want",Label.CENTER));
		p2.add(new Label("to Quit?",Label.CENTER));
		p2.add(new Panel());
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
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
				parent.theEnd();
			}
		});
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}

class FNFDialog extends Dialog {

	Dimension d;
	Button dismissButton;

	public FNFDialog(Frame f,String mesg){
		super(f,mesg,true);
		this.setFont(new Font("Fixed",Font.BOLD,14));
		this.setLayout(new GridLayout(5,1));
		this.add(new Panel());
		this.add(new Label("ERROR:",Label.CENTER));
		this.add(new Label("Cannot Open File",Label.CENTER));
		this.add(new Panel());
		this.add(dismissButton = new Button("Dismiss"));
		this.d = new Dimension(200,140);
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

	private void closeDialog() {
		setVisible(false);
		dispose();
	}

	public Dimension minimunSize(){
		return d;
	}

	public Dimension getPreferredSize(){
		return minimunSize();
	}

}

class NetFileDialog extends Dialog {

	Dimension d;
	TextField tf;
	public String urlSt;
	DMNFrame fSim;
	boolean bin;
	FNFDialog fnfd;
	Button openButton, dismissButton;

	public NetFileDialog(DMNFrame f,String mesg,boolean b){
		super(f,mesg,true);
		this.setBackground(f.getBackground());
		fSim = f;
		bin = b;
		this.setFont(new Font("Fixed",Font.BOLD,14));
		this.setLayout(new GridLayout(3,1));
		this.add(new Label("Open network file.",Label.CENTER));
		Panel p = new Panel();
		Panel q = new Panel();
		p.setLayout(new GridLayout(1,2));
		p.add(new Label("URL to Open:"+ fSim.urlBase.getProtocol()+ "://"+fSim.urlBase.getHost()+"/"));
		p.add(tf = new TextField());
		this.add(p);
		q.add(openButton = new Button("Open"));
		q.add(dismissButton = new Button("Dismiss"));
		this.add(q);
		this.d = new Dimension(500,160);
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
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNetworkFile();
			}
		});
	}

	private void openNetworkFile() {
		urlSt = tf.getText();
		boolean ok = fSim.createDataInputStream(urlSt);
		if(bin) {
			if(ok)
				ok = fSim.openDMNBinary();
		}
		else {
			if(ok)
				ok = fSim.openDMNSource();
		}
		if(!ok) {
			if(bin) 
				fnfd = new FNFDialog(fSim,"Couldn't open DMN Binary");
			else
				fnfd = new FNFDialog(fSim,"Couldn't open DMN Source");
			fnfd.pack();
			WindowUtil.centerOnScreen(fnfd);
			fnfd.setVisible(true);
		}
		else closeDialog();
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}

	public Dimension minimunSize(){
		return d;
	}

	public Dimension getPreferredSize(){
		return minimunSize();
	}
}
