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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Panel for a bid bubble
 */
class BidBubblePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image bubbleImage;
	private JLabel bidLabel;

	BidBubblePanel(Image newBubbleImage) {

		bubbleImage = newBubbleImage;

		setLayout(new MigLayout("fill")); //$NON-NLS-1$

		bidLabel = new JLabel();
		bidLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		setBidValue(0);

		add(bidLabel, "center"); //$NON-NLS-1$
		setMinimumSize(new Dimension(75, 75));
	}

	void setBidValue(int bidValue) {

		if (bidValue > -1) {

			bidLabel.setText(String.valueOf(bidValue));

		} else {

			bidLabel.setText("X"); //$NON-NLS-1$
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		g.drawImage(bubbleImage, 0, 0, null);
	}

}
