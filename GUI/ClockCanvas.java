//$Id: ClockCanvas.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: ClockCanvas.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;
import Simul.*;

public class ClockCanvas extends Canvas implements Runnable {

	Dimension d;
	boolean tick, steping = false;
	int up,down;
	Thread tr = null;
	Simulator simul;
	Component parent;
	volatile int halfTickDelayMs = 500;

	public ClockCanvas(Component c,Simulator s) {
		this.tick = true;
		this.setBackground(Color.white);
		d = new Dimension(464,34);
		parent = c;
		simul = s;
	}

	public void start() {
		if(tr == null) {
			tr = new Thread(this);
			tr.start();
		}
	}

	public void run() {
		if(!steping) 
			while (Thread.currentThread() == tr) {
				tick = !tick;
				this.repaint();
				if (tick){
					this.simul.step();
					this.parent.repaint();
				}
				try {
					Thread.sleep(halfTickDelayMs);
				}catch (InterruptedException e){}
			}
		else {
			for(int i=0;i<2;i++) 
				if (Thread.currentThread() == tr) {
					tick = !tick;
					repaint();
					try {
						Thread.sleep(halfTickDelayMs);
					}catch (InterruptedException e){}
				}
			this.simul.step();
			this.parent.repaint();
			this.stop();
		}
	}

	public void stop() {
		steping = false;
		tr = null;
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(0, 0, d.width - 1, d.height - 1);
		if (tick) {
			up = 4;
			down = 29;
		} else {
			up = 29;
			down = 4;
		}
		for(int i = 1;i < 5; i++) {
			int pos = i * 115;
			g.setColor(Color.blue);
			g.drawLine(pos,0,pos,34);
			pos = (i - 1)*115;
			g.setColor(Color.red);
			g.drawLine(pos + 3,up,pos + 56,up);
			g.drawLine(pos + 56,up,pos + 60,down);
			g.drawLine(pos + 60,down,pos + 113,down);
			g.drawLine(pos + 113,down,pos + 118,up);
		}
	}

	public void step(){
		steping = true;
		this.start();
	}

	public void setStepDelay(int msPerStep) {
		if(msPerStep > 0)
			halfTickDelayMs = Math.max(1,msPerStep / 2);
	}

	public Dimension getMinimumSize() {
		return d;
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}
