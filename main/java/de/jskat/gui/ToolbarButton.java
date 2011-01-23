/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.gui.img.JSkatGraphicRepository.IconSize;

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
		// setTextPosition();
		// setPreferredSize(new Dimension(120, 100));
		setIconSize(IconSize.SMALL);
	}

	private void setTextPosition() {

		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}

	/**
	 * Sets the icon size of a toolbar button
	 * 
	 * @param iconSize
	 *            IconSize to set
	 */
	public void setIconSize(IconSize iconSize) {

		ImageIcon icon = null;
		switch (iconSize) {
		case SMALL:
			icon = (ImageIcon) getAction().getValue(Action.SMALL_ICON);
			break;
		case BIG:
			icon = (ImageIcon) getAction().getValue(Action.LARGE_ICON_KEY);
			break;
		}

		if (icon != null) {
			setIcon(icon);
		}
	}
}
