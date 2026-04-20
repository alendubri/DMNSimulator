package GUI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public final class WindowUtil {

	private WindowUtil() {
	}

	public static void centerOnScreen(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		int x = (screenSize.width - windowSize.width) / 2;
		int y = (screenSize.height - windowSize.height) / 2;
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		window.setLocation(x,y);
	}
}
