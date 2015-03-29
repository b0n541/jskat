package org.jskat.gui.swing;

import java.awt.Toolkit;

/**
 * Determines the screen resolution
 */
public class ScreenResolution {
	public final static boolean isBigScreen() {
		return Toolkit.getDefaultToolkit().getScreenSize().height > 800;
	}

	public final static boolean isVeryBigScreen() {
		return Toolkit.getDefaultToolkit().getScreenSize().height > 1300;
	}
}
