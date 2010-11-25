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

	private double cardScaleFactor;

	/**
	 * Constructor
	 * 
	 * @param jskatBitmaps
	 *            JSkat bitmaps
	 */
	TrickPanel(JSkatGraphicRepository jskatBitmaps, double newCardScaleFactor) {

		bitmaps = jskatBitmaps;
		cardScaleFactor = newCardScaleFactor;

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
		double scaleFactor = 1.0;
		double xPos = 0.0;
		double yPos = 0.0;

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		for (int i = 0; i < trick.size(); i++) {

			Card card = trick.get(i);
			Player player = positions.get(i);

			Image image = bitmaps.getCardImage(card.getSuit(), card.getRank());

			// Calculate scale factor
			scaleFactor = 1.0 / (image.getHeight(null) / (panelHeight * cardScaleFactor));

			// Calculate translation
			double xScaleSize = image.getWidth(null) * scaleFactor;
			double xAllTrickCardsSize = xScaleSize * (1 + 2.0 / 3.00);
			double xCenterTranslate = (panelWidth - xAllTrickCardsSize) / 2;
			double yScaleSize = image.getHeight(null) * scaleFactor;
			double yAllTrickCardsSize = yScaleSize * 1.5;
			double yCenterTranslate = (panelHeight - yAllTrickCardsSize) / 2;

			if (player.equals(leftOpponent)) {

				xPos = xCenterTranslate;
				yPos = (yScaleSize / 4.0) + yCenterTranslate;

			} else if (player.equals(rightOpponent)) {

				xPos = (2.0 * (xScaleSize / 3.0)) + xCenterTranslate;
				yPos = yCenterTranslate;

			} else if (player.equals(userPosition)) {

				xPos = (xScaleSize / 3.0) + xCenterTranslate;
				yPos = 2 * (yScaleSize / 4.0d) + yCenterTranslate;
			}

			AffineTransform transform = new AffineTransform();
			transform.setToIdentity();
			transform.translate(xPos, yPos);
			transform.scale(scaleFactor, scaleFactor);
			transform.rotate(cardRotations.get(i).doubleValue());

			g2D.drawImage(image, transform, this);
		}
	}

	void setUserPosition(Player newUserPosition) {

		userPosition = newUserPosition;
		leftOpponent = userPosition.getLeftNeighbor();
		rightOpponent = userPosition.getRightNeighbor();
	}
}
