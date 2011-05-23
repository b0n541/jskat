/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jskat.gui.table;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;


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
