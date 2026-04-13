//$Id: RegisterPanel.java,v 1.5 1996/11/13 12:48:25 dubuc Exp $

/*
$Log: RegisterPanel.java,v $
Revision 1.5  1996/11/13 12:48:25  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.*;

public class RegisterPanel extends Panel {
	public List regList;
	public Button [] typeBut = new Button[4];
	private Dimension dim;

	public RegisterPanel(String st,Dimension dm) {
		this(st,dm,new List(5,false));
	}

	public RegisterPanel(String st,Dimension dm,List lst) {
		dim = dm;
		Label regLabel		= new Label(st,Label.CENTER);

		regLabel.setBackground(Color.darkGray);
		regLabel.setForeground(Color.white);
		this.setLayout(new BorderLayout());
		this.add("North",regLabel);
		this.add("Center",lst);
	}

	public Dimension minimumSize() {
		return(dim);
	}

	public Dimension preferredSize() {
		return(minimumSize());
	}
}

