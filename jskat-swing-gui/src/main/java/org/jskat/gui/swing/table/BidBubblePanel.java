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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.gui.swing.LayoutFactory;

/**
 * Panel for a bid bubble
 */
class BidBubblePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image bubbleImage;
	private JLabel bidLabel;

	BidBubblePanel(Image newBubbleImage) {

		bubbleImage = newBubbleImage;

		setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

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
