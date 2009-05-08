package de.jskat.gui;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;

	/**
	 * @see JButton#JButton(Action)
	 */
	public ToolbarButton(Action a) {
		
		super(a);
		
		setTextPosition();
	}

    private void setTextPosition() {
		
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
	}
}
