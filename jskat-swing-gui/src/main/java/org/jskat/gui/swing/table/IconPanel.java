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

	JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

	/**
	 * Constructor
	 */
	public IconPanel() {

		this.chatLabel = new JLabel(this.chatIcon);
		this.readyToPlayLabel = new JLabel(this.readyToPlayIcon);
		this.resignedLabel = new JLabel(this.resignedIcon);

		setIcons();

		add(this.resignedLabel);
		add(this.chatLabel);
		add(this.readyToPlayLabel);
	}

	/**
	 * Resets the icon panel
	 */
	public void reset() {

		this.resigned = false;
		setIcons();
	}

	/**
	 * Sets the flag for enabling chat
	 * 
	 * @param isChatEnabled
	 *            TRUE, if chatting is enabled
	 */
	public void setChatEnabled(boolean isChatEnabled) {

		this.chatEnabled = isChatEnabled;
		setIcons();
	}

	/**
	 * Sets the flag for "Ready to play"
	 * 
	 * @param isReadyToPlay
	 *            TRUE, if the player is ready to play
	 */
	public void setReadyToPlay(boolean isReadyToPlay) {

		this.readyToPlay = isReadyToPlay;
		setIcons();
	}

	/**
	 * Sets the flag for "Resign"
	 * 
	 * @param isResign
	 *            TRUE, if the player wants to resign
	 */
	public void setResign(boolean isResign) {

		this.resigned = isResign;
		setIcons();
	}

	private void setIcons() {

		String tooltipText = null;
		if (this.resigned) {
			this.resignedIcon = new ImageIcon(JSkatGraphicRepository.instance()
					.getIconImage(Icon.WHITE_FLAG, IconSize.SMALL));
			tooltipText = this.strings.getString("iss_player_wants_to_resign"); //$NON-NLS-1$
		} else {
			this.resignedIcon = new ImageIcon(JSkatGraphicRepository.instance()
					.getIconImage(Icon.BLANK, IconSize.SMALL));
			tooltipText = ""; //$NON-NLS-1$
		}
		this.resignedLabel.setIcon(this.resignedIcon);
		this.resignedLabel.setToolTipText(tooltipText);

		if (this.chatEnabled) {
			this.chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT, IconSize.SMALL));
			tooltipText = this.strings.getString("iss_chat_enabled"); //$NON-NLS-1$
		} else {
			this.chatIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CHAT_DISABLED, IconSize.SMALL));
			tooltipText = this.strings.getString("iss_chat_disabled"); //$NON-NLS-1$
		}
		this.chatLabel.setIcon(this.chatIcon);
		this.chatLabel.setToolTipText(tooltipText);

		if (this.readyToPlay) {
			this.readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.OK, IconSize.SMALL));
			tooltipText = this.strings.getString("iss_ready_to_play"); //$NON-NLS-1$
		} else {
			this.readyToPlayIcon = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.STOP, IconSize.SMALL));
			tooltipText = this.strings.getString("iss_not_ready_to_play"); //$NON-NLS-1$
		}
		this.readyToPlayLabel.setIcon(this.readyToPlayIcon);
		this.readyToPlayLabel.setToolTipText(tooltipText);
	}
}
