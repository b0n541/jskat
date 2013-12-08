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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;

/**
 * Holds the clock icon and the time<br>
 * does clock countdown too
 */
public class ClockPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JLabel timeLabel;
	double playerTimeInSeconds;
	CountDownThread countDownThread;

	/**
	 * Constructor
	 */
	public ClockPanel() {

		ImageIcon clock = new ImageIcon(JSkatGraphicRepository.instance().getIconImage(Icon.CLOCK, IconSize.SMALL));
		JLabel clockLabel = new JLabel(clock);
		playerTimeInSeconds = 0.0d;
		timeLabel = new JLabel(getPlayerTimeString());

		add(clockLabel);
		add(timeLabel);
	}

	/**
	 * Sets the {@link ClockPanel} to active state
	 */
	public void setActive() {
		if (countDownThread != null) {
			// there is an old count down thread running
			// stop it at first
			countDownThread.stopCountDown();
		}
		countDownThread = new CountDownThread();
		countDownThread.start();
	}

	/**
	 * Sets the {@link ClockPanel} to inactive state
	 */
	public void setInactive() {
		if (countDownThread != null) {
			countDownThread.stopCountDown();
		}
	}

	/**
	 * Sets the player time
	 * 
	 * @param newPlayerTime
	 *            Player time
	 */
	public void setPlayerTime(final double newPlayerTime) {

		playerTimeInSeconds = newPlayerTime;
		refreshTimeLabel();
	}

	void refreshTimeLabel() {
		timeLabel.setText(getPlayerTimeString());
	}

	private String getPlayerTimeString() {

		int minutes = (int) Math.floor(playerTimeInSeconds / 60);
		int seconds = (int) (playerTimeInSeconds - (minutes * 60));

		DecimalFormat format = ((DecimalFormat) NumberFormat.getInstance());
		format.applyPattern("00"); //$NON-NLS-1$
		return format.format(minutes) + ":" + format.format(seconds); //$NON-NLS-1$
	}

	private class CountDownThread extends Thread {

		private volatile boolean isActive = true;

		/**
		 * Constructor
		 */
		CountDownThread() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			while (isActive) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (isActive) {
					// thread could be inactive meanwhile
					if (playerTimeInSeconds < 1.0) {
						setPlayerTime(0.0);
					} else {
						setPlayerTime(playerTimeInSeconds - 1.0);
					}
				}
			}
		}

		void stopCountDown() {
			isActive = false;
		}
	}
}
