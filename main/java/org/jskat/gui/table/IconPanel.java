/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-20
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
/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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
import org.jskat.util.JSkatResourceBundle;


/**
 * Holds information icons for the player
 */
public class IconPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	boolean chatEnabled = false;
	ImageIcon chatIcon = null;
	JLabel chatLabel;
	boolean readyToPlay = false;
	ImageIcon readyToPlayIcon = null;
	JLabel readyToPlayLabel;

	JSkatResourceBundle strings = JSkatResourceBundle.instance();

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

		String tooltipText = null;
		if (chatEnabled) {
			chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT, IconSize.SMALL));
			tooltipText = strings.getString("iss_chat_enabled"); //$NON-NLS-1$
		} else {
			chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT_DISABLED, IconSize.SMALL));
			tooltipText = strings.getString("iss_chat_disabled"); //$NON-NLS-1$
		}
		chatLabel.setIcon(chatIcon);
		chatLabel.setToolTipText(tooltipText);

		if (readyToPlay) {
			readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.OK, IconSize.SMALL));
			tooltipText = strings.getString("iss_ready_to_play"); //$NON-NLS-1$
		} else {
			readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.STOP, IconSize.SMALL));
			tooltipText = strings.getString("iss_not_ready_to_play"); //$NON-NLS-1$
		}
		readyToPlayLabel.setIcon(readyToPlayIcon);
		readyToPlayLabel.setToolTipText(tooltipText);
	}
}
