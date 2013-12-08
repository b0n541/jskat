/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

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
	boolean resigned = false;
	ImageIcon resignedIcon = null;
	JLabel resignedLabel;

	JSkatResourceBundle strings = JSkatResourceBundle.instance();

	/**
	 * Constructor
	 */
	public IconPanel() {

		chatLabel = new JLabel(chatIcon);
		readyToPlayLabel = new JLabel(readyToPlayIcon);
		resignedLabel = new JLabel(resignedIcon);

		setIcons();

		add(resignedLabel);
		add(chatLabel);
		add(readyToPlayLabel);
	}

	/**
	 * Resets the icon panel
	 */
	public void reset() {

		resigned = false;
		setIcons();
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

	/**
	 * Sets the flag for "Resign"
	 * 
	 * @param isResign
	 *            TRUE, if the player wants to resign
	 */
	public void setResign(boolean isResign) {

		resigned = isResign;
		setIcons();
	}

	private void setIcons() {

		String tooltipText = null;
		if (resigned) {
			resignedIcon = new ImageIcon(JSkatGraphicRepository.instance()
					.getIconImage(Icon.WHITE_FLAG, IconSize.SMALL));
			tooltipText = strings.getString("iss_player_wants_to_resign"); //$NON-NLS-1$
		} else {
			resignedIcon = new ImageIcon(JSkatGraphicRepository.instance()
					.getIconImage(Icon.BLANK, IconSize.SMALL));
			tooltipText = ""; //$NON-NLS-1$
		}
		resignedLabel.setIcon(resignedIcon);
		resignedLabel.setToolTipText(tooltipText);

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
