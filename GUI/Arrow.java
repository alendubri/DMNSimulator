//$Id: Arrow.java,v 1.5 1996/11/13 13:10:57 dubuc Exp $

/*
$Log: Arrow.java,v $
Revision 1.5  1996/11/13 13:10:57  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package GUI;

import java.awt.Polygon;

public class Arrow {

	final static int UP		= 1;
	final static int DOWN	= 2;
	final static int LEFT	= 3;
	final static int RIGTH	= 4;

	public static Polygon def(int x, int y,int dir) {

		switch(dir) {
			case UP:
				int [] xu = {x,x-5,x+5};
				int [] yu = {y,y+5,y+5};
				return (new Polygon(xu,yu,3));
			case DOWN:
				int [] xd = {x,x-5,x+5};
				int [] yd = {y,y-5,y-5};
				return (new Polygon(xd,yd,3));
			case LEFT:
				int [] xl = {x,x+5,x+5};
				int [] yl = {y,y+5,y-5};
				return (new Polygon(xl,yl,3));
			case RIGTH:
				int [] xr = {x,x-5,x-5};
				int [] yr = {y,y+5,y-5};
				return (new Polygon(xr,yr,3));
			default:
				return null;
		}
	}
}
