//$Id: StagesPanel.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: StagesPanel.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;

public class StagesPanel extends Panel {

	Panel [] panels = new Panel[3];
	ClockCanvas clk;
	public IntrCanvas intrcv;
	Dimension d;
	SingleStagePanel [] stages = new SingleStagePanel[4];
	public RunCheckbox [] cbSim = new RunCheckbox[3];

	public StagesPanel(ClockCanvas c) {
		d = new Dimension(460,180);
		clk = c;
		this.setInitialPanelGeometry();
		this.setInitialComponents();
	}


	private void setInitialPanelGeometry() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0,0,0,0);
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panels[0] = addPanel(gridbag,c);
		c.gridheight = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panels[1] = addPanel(gridbag,c);
		c.gridheight = 1;
		panels[2] = addPanel(gridbag,c);
	}

	private void setInitialComponents(){

		this.setFont(new Font("Fixed",Font.BOLD,12));

		panels[0].setLayout(new GridLayout(1,6));
		CheckboxGroup cbg = new CheckboxGroup();
		panels[0].add(cbSim[0] = new RunCheckbox(MainMenu.STOP,cbg,true));
		panels[0].add(cbSim[1] = new RunCheckbox(MainMenu.PLAY,cbg,false));
		panels[0].add(cbSim[2] = new RunCheckbox(MainMenu.PAUS,cbg,false));
		panels[0].add(new Button(MainMenu.STEP));
		panels[0].add(new Button(MainMenu.REST));
		panels[0].add(intrcv = new IntrCanvas());
		

		panels[1].setLayout(new GridLayout(1,4));
		panels[1].add(stages[0] = new SingleStagePanel(new Color(0,0x3f,0x3f),Color.white,4));
		panels[1].add(stages[1] = new SingleStagePanel(new Color(0,0x7f,0x7f),Color.white,3));
		panels[1].add(stages[2] = new SingleStagePanel(new Color(0,0xbf,0xbf),Color.white,2));
		panels[1].add(stages[3] = new SingleStagePanel(new Color(0,0xff,0xff),Color.black,1));

		panels[2].setLayout(new BorderLayout());
		panels[2].add("Center",clk);
	}

	private Panel addPanel(
				GridBagLayout gridbag,
				GridBagConstraints c) {
		Panel panel = new Panel();
		gridbag.setConstraints(panel, c);
		add(panel);
		return panel;
	}

	public void setTxt(String [] st) {
		for(int i = 0 ;i < 4; i++)
			for(int j = 0;j < (4-i); j++){
				stages[i].stageLabel[j].setText(st[j+i]);
			}
	}

	public Dimension minimumSize() {
		return d;
	}

	public Dimension preferredSize() {
		return minimumSize();
	}

}

class StLabel extends Label {
	Dimension d;

	public StLabel(String st,int al){
		super(st,al);
		d = new Dimension(30,30);
	}

	public StLabel(String st){
		super(st);
		d = new Dimension(30,30);
	}

	public StLabel(){
		super();
		d = new Dimension(20,30);
	}

	public Dimension minimumSize() {
		return d;
	}

	public Dimension preferredSize() {
		return minimumSize();
	}
}

class SingleStagePanel extends Panel {

	public Label [] stageLabel;
	private StLabel [] intLabel = new StLabel[4];
	static String [] STG = {"WB","EX","RS","IF"};
	int stageActive;
	Dimension d;

	SingleStagePanel(Color bg, Color fg, int st) {
		d = new Dimension(115,120);
		stageActive = st;
		this.setFont(new Font("Fixed",Font.BOLD,12));
		this.setLayout(new GridLayout(4,1,0,0));
		this.intLabel = new StLabel[stageActive];
		this.stageLabel = new Label[stageActive];
		int start = 4 - stageActive;
		int n = 0;
		for(int i = 0; i < 4; i ++) {
			Panel p = new Panel();
			p.setLayout(new BorderLayout());
			if(i > (start - 1)) {
				intLabel[n] = new StLabel(STG[n],Label.CENTER);
				intLabel[n].setBackground(Color.gray);
				intLabel[n].setForeground(Color.white);
				stageLabel[n] = new Label();
				stageLabel[n].setBackground(bg);
				stageLabel[n].setForeground(fg);
				p.add("West",intLabel[n]);
				p.add("Center",stageLabel[n]);
				n++;
			} else {
				StLabel tmp1 = new StLabel("  ");
				tmp1.setBackground(Color.gray);
				tmp1.setForeground(Color.white);
				Label tmp2 = new Label();
				tmp2.setBackground(bg);
				tmp2.setForeground(fg);
				p.add("West",tmp1);
				p.add("Center",tmp2);
			}
			this.add(p);
		}
	}

	public Dimension minimumSize() {
		return d;
	}

	public Dimension preferredSize() {
		return minimumSize();
	}
}

class IntrCanvas extends Canvas {

	Dimension d;
	public boolean active;
	boolean enter;

	public IntrCanvas() {
		d = new Dimension(77,20);
		active = enter = false;
	}

	public void paint(Graphics g){
		g.setColor(new Color(0x00,0x8b,0x8b));
		g.fill3DRect(2,2,d.width - 2,d.height - 2,true);
		g.fill3DRect(4,4,d.width - 6,d.height - 6,false);
		if(enter) 
			g.setColor(Color.yellow);
		else 
			g.setColor(Color.white);
		g.fillRect(5,5,d.width - 9,d.height - 9);
		g.setFont(new Font("Fixed",Font.PLAIN,10));

		this.drawFlag(63,10,g);
		drawString(g,"Intr.",5,5,d.width - 21,d.height - 6);
	}

	private void drawString(Graphics g,String st,int x,int y,int w, int h) {
		FontMetrics fm = g.getFontMetrics();
		int mpv = fm.getAscent()/2 - fm.getDescent()/2;
		int mph = w/2 - fm.stringWidth(st)/2;
		g.drawString(st,x+mph,y+h/2+mpv);
	}

	private void drawFlag(int x,int y,Graphics g) {
		if(!active){
			Polygon p = defDownFlag(x,y);
			g.setColor(Color.red);
			g.fillPolygon(p);
			g.setColor(Color.black);
			g.drawPolygon(p);
			g.drawLine(x-6,y,x+6,y);
			g.drawLine(x-6,y-1,x+6,y-1);
		}
		else {
			Polygon p = defUpFlag(x,y);
			g.setColor(Color.red);
			g.fillPolygon(p);
			g.setColor(Color.black);
			g.drawPolygon(p);
			g.drawLine(x,y-6,x,y+6);
			g.drawLine(x-1,y-6,x-1,y+6);
		}
	}

	private Polygon defUpFlag(int x, int y){
		int [] xp = {x,x,x+5};
		int [] yp = {y,y-6,y-3};
		return new Polygon(xp,yp,3);
	}

	private Polygon defDownFlag(int x, int y){
		int [] xp = {x,x+6,x+3};
		int [] yp = {y,y,y+5};
		return new Polygon(xp,yp,3);
	}

	public boolean mouseEnter(Event e, int x, int y) {
		enter = true;
		this.repaint();
		return true;
	}

	public boolean mouseExit(Event e, int x, int y) {
		enter = false;
		this.repaint();
		return true;
	}

	public boolean mouseDown(Event e, int x, int y) {
		active = !active;
		this.repaint();
		return false;
	}

	public Dimension minimumSize() {
		return d;
	}

	public Dimension preferredSize() {
		return minimumSize();
	}
}


class RunCheckbox extends Checkbox {

	public RunCheckbox(String str, CheckboxGroup cbg, boolean st){
		super(str,cbg,st);
	}

	public boolean action(Event e, Object w) {
		this.setState(true);
		return false;
	}

}

