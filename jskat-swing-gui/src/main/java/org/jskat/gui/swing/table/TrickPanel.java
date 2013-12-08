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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders all cards of a trick
 */
class TrickPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(TrickPanel.class);

	private static final double TRICK_SIZE_FACTOR = 1.0d + 2.0d / 3.0d;

	private static JSkatOptions options = JSkatOptions.instance();
	private final JSkatGraphicRepository bitmaps;
	private final List<Double> cardRotations;
	private final List<Player> positions;
	private final CardList trick;
	private final Random rand = new Random();
	private Player userPosition;
	private Player rightOpponent;
	private Player leftOpponent;

	private CardFace cardFace;
	private final boolean randomPlacement;
	private final double globalScale;

	/**
	 * Constructor
	 * 
	 * @param randomPlacement
	 *            Random placement of cards
	 */
	TrickPanel(final boolean randomPlacement) {
		this(1.0, randomPlacement);
	}

	/**
	 * Constructor
	 * 
	 * @param globalScale
	 *            Global scale of cards
	 * @param randomPlacement
	 *            Random placement of cards
	 */
	TrickPanel(final double globalScale, final boolean randomPlacement) {

		bitmaps = JSkatGraphicRepository.instance();

		cardFace = options.getCardSet().getCardFace();

		this.randomPlacement = randomPlacement;
		this.globalScale = globalScale;

		trick = new CardList();
		positions = new ArrayList<Player>();
		cardRotations = new ArrayList<Double>();

		setOpaque(false);

		addComponentListener(this);
	}

	/**
	 * Adds a card to the trick
	 * 
	 * @param player
	 *            Position of the player
	 * @param card
	 *            Card
	 */
	void addCard(final Player player, final Card card) {

		positions.add(player);
		trick.add(card);

		if (randomPlacement) {
			cardRotations.add(Double.valueOf(0.5 * rand.nextDouble() - 0.25));
		} else {
			cardRotations.add(Double.valueOf(0.0));
		}

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
	protected synchronized void paintComponent(final Graphics g) {

		super.paintComponent(g);

		if (isNewCardFace()) {
			cardFace = options.getCardSet().getCardFace();
		}

		final int panelWidth = getWidth();
		final int panelHeight = getHeight();

		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		final double cardScale = getCardScale() * globalScale;
		final Image image = bitmaps.getCardImage(Card.CJ);

		final double xScaleSize = image.getWidth(this);
		final double xAllTrickCardsSize = xScaleSize * TRICK_SIZE_FACTOR;
		final double xBorder = (panelWidth * (1 / cardScale) - xAllTrickCardsSize) / 2.0d;

		final double yScaleSize = image.getHeight(this);
		final double yAllTrickCardsSize = yScaleSize * TRICK_SIZE_FACTOR;
		final double yBorder = (panelHeight * (1 / cardScale) - yAllTrickCardsSize) / 2.0d;

		for (int i = 0; i < trick.size(); i++) {

			final Card card = trick.get(i);
			final Player player = positions.get(i);

			if (card != null && player != null) {
				// Calculate translation
				double posX = 0.0d;
				double posY = 0.0d;
				if (player.equals(leftOpponent)) {

					posX = xBorder;
					posY = yBorder + yScaleSize * (1.0d / 3.0d);

				} else if (player.equals(rightOpponent)) {

					posX = xBorder + xScaleSize * (2.0d / 3.0d);
					posY = yBorder;

				} else if (player.equals(userPosition)) {

					posX = xBorder + xScaleSize * (1.0d / 3.0d);
					posY = yBorder + yScaleSize * (2.0d / 3.0d);
				}

				final AffineTransform transform = new AffineTransform();
				transform.translate(posX * cardScale, posY * cardScale);
				transform.rotate(cardRotations.get(i).doubleValue());
				transform.scale(cardScale, cardScale);

				g2D.drawImage(bitmaps.getCardImage(card), transform, this);
			}
		}
	}

	private double getCardScale() {

		final Image sampleCard = bitmaps.getCardImage(Card.CJ);
		final double imageWidth = sampleCard.getWidth(this) * TRICK_SIZE_FACTOR;
		final double imageHeight = sampleCard.getHeight(this)
				* TRICK_SIZE_FACTOR;

		final double scaleX = getWidth() / imageWidth;
		final double scaleY = getHeight() / imageHeight;

		double scaleFactor = 1.0;
		if (scaleX < 1.0 || scaleY < 1.0) {
			if (scaleX < scaleY) {
				scaleFactor = scaleX;
			} else {
				scaleFactor = scaleY;
			}
		}

		return scaleFactor;
	}

	boolean isNewCardFace() {
		return !cardFace.equals(options.getCardSet().getCardFace());
	}

	void setUserPosition(final Player newUserPosition) {

		userPosition = newUserPosition;
		leftOpponent = userPosition.getLeftNeighbor();
		rightOpponent = userPosition.getRightNeighbor();
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		repaint();
	}

	@Override
	public void componentMoved(final ComponentEvent e) {
		// not needed
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		repaint();
	}

	@Override
	public void componentHidden(final ComponentEvent e) {
		// not needed
	}
}
