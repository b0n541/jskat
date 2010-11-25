/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(ToolbarButton.class);

	/**
	 * @see JButton#JButton(Action)
	 */
	public ToolbarButton(Action a) {
		
		super(a);
		setTextPosition();
		setPreferredSize(new Dimension(120, 100));
	}

    private void setTextPosition() {
		
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
	}
}
