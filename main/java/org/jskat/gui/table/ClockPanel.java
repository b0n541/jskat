/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;


/**
 * Holds the clock icon an the time<br>
 * does clock countdown too
 */
public class ClockPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel timeLabel;
	private double playerTimeInSeconds;

	/**
	 * Constructor
	 */
	public ClockPanel() {

		ImageIcon clock = new ImageIcon(JSkatGraphicRepository.instance()
				.getIconImage(Icon.CLOCK, IconSize.SMALL));
		JLabel clockLabel = new JLabel(clock);
		playerTimeInSeconds = 0.0d;
		timeLabel = new JLabel(getPlayerTimeString());

		add(clockLabel);
		add(timeLabel);
	}

	/**
	 * Sets the player time
	 * 
	 * @param newPlayerTime
	 *            Player time
	 */
	public void setPlayerTime(double newPlayerTime) {

		playerTimeInSeconds = newPlayerTime;
		timeLabel.setText(getPlayerTimeString());
	}

	private String getPlayerTimeString() {

		int minutes = (int) Math.floor(playerTimeInSeconds / 60);
		int seconds = (int) (playerTimeInSeconds - (minutes * 60));

		DecimalFormat format = ((DecimalFormat) NumberFormat.getInstance());
		format.applyPattern("00"); //$NON-NLS-1$
		return format.format(minutes) + ":" + format.format(seconds); //$NON-NLS-1$
	}
}
