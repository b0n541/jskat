/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Renders all cards of a trick
 */
class TrickPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JSkatGraphicRepository bitmaps;
	private List<Double> cardRotations;
	private List<Player> positions;
	private CardList trick;
	private Random rand = new Random();
	private Player userPosition;
	private Player rightOpponent;
	private Player leftOpponent;

	/**
	 * Constructor
	 * 
	 * @param jskatBitmaps
	 *            JSkat bitmaps
	 */
	TrickPanel(JSkatGraphicRepository jskatBitmaps) {

		bitmaps = jskatBitmaps;

		trick = new CardList();
		positions = new ArrayList<Player>();
		cardRotations = new ArrayList<Double>();

		setOpaque(false);
	}

	/**
	 * Adds a card to the trick
	 * 
	 * @param player
	 *            Position of the player
	 * @param card
	 *            Card
	 */
	void addCard(Player player, Card card) {

		if (trick.size() == 3) {
			// trick is full -> clear it first
			positions.clear();
			trick.clear();
			cardRotations.clear();
		}
		positions.add(player);
		trick.add(card);
		cardRotations.add(Double.valueOf(0.5 * rand.nextDouble() - 0.25));
		repaint();
	}

	/**
	 * Removes the last card
	 */
	void removeCard() {

		positions.remove(positions.size() - 1);
		trick.remove(trick.size() - 1);
		cardRotations.remove(trick.size() - 1);
		repaint();
	}

	/**
	 * Removes all cards from the trick
	 */
	void clearCards() {

		positions.clear();
		trick.clear();
		cardRotations.clear();
		repaint();
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		int panelWidth = getWidth();
		int panelHeight = getHeight();
		double scaleFactor = 1.0d;
		double xPos = 0.0d;
		double yPos = 0.0d;

		Graphics2D g2D = (Graphics2D) g;

		for (int i = 0; i < trick.size(); i++) {

			Card card = trick.get(i);
			Player player = positions.get(i);

			Image image = bitmaps.getCardImage(card.getSuit(), card.getRank());

			// Calculate scale factor
			// Image should have 66% of panel height
			scaleFactor = 0.5;// 1.0d / (image.getHeight(null) / (panelHeight *
								// 2.0d / 3.0d));

			// Calculate translation
			double xScaleSize = image.getWidth(null) * scaleFactor;
			double yScaleSize = image.getHeight(null) * scaleFactor;
			double centerTranslate = (panelWidth - xScaleSize) / 2.0d
					- xScaleSize / 2.0d;

			if (player.equals(leftOpponent)) {

				xPos = 0.0d + centerTranslate;
				yPos = yScaleSize / 4.0d;
			} else if (player.equals(rightOpponent)) {

				xPos = (2 * (xScaleSize / 3.0d)) + centerTranslate;
				yPos = 0.0d;
			} else if (player.equals(userPosition)) {
				xPos = (xScaleSize / 3.0d) + centerTranslate;
				yPos = 2 * (yScaleSize / 4.0d);
			}

			AffineTransform transform = new AffineTransform();
			transform.setToIdentity();
			transform.translate(xPos, yPos);
			transform.scale(scaleFactor, scaleFactor);
			transform.rotate(cardRotations.get(i).doubleValue());

			g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			g2D.drawImage(image, transform, this);
		}
	}

	void setUserPosition(Player newUserPosition) {

		userPosition = newUserPosition;
		leftOpponent = userPosition.getLeftNeighbor();
		rightOpponent = userPosition.getRightNeighbor();
	}
}
