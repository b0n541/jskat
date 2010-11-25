/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

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

		bidLabel.setText(String.valueOf(bidValue));
	}

	@Override
	public void paintComponent(Graphics g) {

		g.drawImage(bubbleImage, 0, 0, null);
	}

}
