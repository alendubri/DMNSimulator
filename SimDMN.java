//$Id: SimDMN.java,v 1.5 1996/11/13 13:42:03 dubuc Exp $

/*
$Log: SimDMN.java,v $
Revision 1.5  1996/11/13 13:42:03  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

import GUI.*;

public class SimDMN {

	public static void main(String [] args) {
		DMNFrame f = new DMNFrame();
//		f.inAnApplet = true; //puesto para probar la apertura de archivo por red
		f.init();
		f.pack(); // Esta instruccion empaqueta los componentes visuales
		WindowUtil.centerOnScreen(f);
 		f.setVisible(true);
		f.repaint();
	}

}
