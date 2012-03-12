package org.jskat.gui.table;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.gui.LayoutFactory;

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
