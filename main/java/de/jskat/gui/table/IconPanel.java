/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;
import de.jskat.gui.img.JSkatGraphicRepository.IconSize;

/**
 * Holds information icons for the player
 */
public class IconPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	boolean chatEnabled = false;
	ImageIcon chatIcon;
	JLabel chatLabel;
	boolean readyToPlay = false;
	ImageIcon readyToPlayIcon;
	JLabel readyToPlayLabel;

	/**
	 * Constructor
	 */
	public IconPanel() {

		chatLabel = new JLabel(chatIcon);
		readyToPlayLabel = new JLabel(readyToPlayIcon);

		setIcons();

		add(chatLabel);
		add(readyToPlayLabel);
	}

	/**
	 * Sets the flag for enabling chat
	 * 
	 * @param isChatEnabled
	 *            TRUE, if chatting is enabled
	 */
	public void setChatEnabled(boolean isChatEnabled) {

		chatEnabled = isChatEnabled;
		setIcons();
	}

	/**
	 * Sets the flag for "Ready to play"
	 * 
	 * @param isReadyToPlay
	 *            TRUE, if the player is ready to play
	 */
	public void setReadyToPlay(boolean isReadyToPlay) {

		readyToPlay = isReadyToPlay;
		setIcons();
	}

	private void setIcons() {

		if (chatEnabled) {
			chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT, IconSize.SMALL));
		} else {
			chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT_DISABLED, IconSize.SMALL));
		}
		chatLabel.setIcon(chatIcon);

		if (readyToPlay) {
			readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.OK, IconSize.SMALL));
		} else {
			readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.STOP, IconSize.SMALL));
		}
		readyToPlayLabel.setIcon(readyToPlayIcon);
	}
}
