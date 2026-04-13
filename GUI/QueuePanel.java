//$Id: QueuePanel.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: QueuePanel.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;

public class QueuePanel extends Panel {

	public static boolean UP = true;
	public static boolean DOWN = false;

	public TextField [] ntf;
	Label	queueLab;
	int levels;

	public QueuePanel(String stLab,int l) {
		levels = l;
		ntf = new TextField[levels];
		this.setLayout(new GridLayout(levels+1,1,0,3));
		this.add(queueLab = new Label(stLab,Label.CENTER));
		for(int i = 0; i < levels; i++) {
			ntf[i] = new TextField(3);
			ntf[i].setEditable(false);
			ntf[i].setBackground(Color.white);
			ntf[i].setForeground(Color.black);
			this.add(ntf[i]);
		}
	}

	public QueuePanel(String stLab,int l,int npan) {
		this(stLab,l,npan,UP);
	}

	public QueuePanel(String stLab,int l,int npan, boolean uptodown) {
		levels = l;
		if(npan > levels) {
			ntf = new TextField[levels];
			this.setLayout(new GridLayout(npan + 1,1,0,3));
			this.add(queueLab = new Label(stLab,Label.CENTER));
			if (uptodown) {
				for(int i = 0; i < levels; i++) {
					ntf[i] = new TextField(3);
					ntf[i].setEditable(false);
					ntf[i].setBackground(Color.white);
					ntf[i].setForeground(Color.black);
					this.add(ntf[i]);
				}
				for(int i = 0; i < (npan-levels); i++)
					this.add(new Panel());
			}
			else {
				for(int i = 0; i < (npan-levels); i++)
					this.add(new Panel());
				for(int i = 0; i < levels; i++) {
					ntf[i] = new TextField(3);
					ntf[i].setEditable(false);
					ntf[i].setBackground(Color.white);
					ntf[i].setForeground(Color.black);
					this.add(ntf[i]);
				}
			}
		}
	}
}
