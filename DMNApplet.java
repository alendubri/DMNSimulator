//$Id: DMNApplet.java,v 1.5 1996/11/13 13:42:03 dubuc Exp $

/*
$Log: DMNApplet.java,v $
Revision 1.5  1996/11/13 13:42:03  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

import GUI.*;

import java.awt.Button;
import java.awt.Event;
import java.awt.Font;
import java.awt.BorderLayout;
import java.applet.Applet;
import java.net.URL;

public class DMNApplet extends Applet {
	DMNFrame dmnSim;

	URL urlBase;

	static String STARTSIM = "Start SimDMN";

	public void init() {
		setLayout(new BorderLayout(2,2));
		resize(200,50);
		Button but = new Button(STARTSIM);
		but.setFont(new Font("Helvetica", Font.BOLD, 14));
		add("Center",but);
		urlBase = this.getDocumentBase();
	}

	public boolean handleEvent(Event e) {
		if (STARTSIM.equals(e.arg)) {
			dmnSim = new DMNFrame();
			dmnSim.inAnApplet = true;
			dmnSim.urlBase = urlBase;
			dmnSim.init();
			dmnSim.pack();
			dmnSim.show();
		}
		return super.handleEvent(e);
	}
}
