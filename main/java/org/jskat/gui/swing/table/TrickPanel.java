/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
package org.jskat.gui.swing.table;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders all cards of a trick
 */
class TrickPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(TrickPanel.class);

	private static JSkatOptions options = JSkatOptions.instance();
	private final JSkatGraphicRepository bitmaps;
	private final Map<Card, Image> scaledCardImages;
	private final List<Double> cardRotations;
	private final List<Player> positions;
	private final CardList trick;
	private final Random rand = new Random();
	private Player userPosition;
	private Player rightOpponent;
	private Player leftOpponent;

	private CardFace cardFace;
	private final double cardScaleFactor;
	private final boolean randomPlacement;

	/**
	 * Constructor
	 * 
	 * @param newCardScaleFactor
	 * @param newRandomPlacement
	 */
	TrickPanel(final double newCardScaleFactor, final boolean newRandomPlacement) {

		bitmaps = JSkatGraphicRepository.instance();

		cardFace = options.getCardFace();

		scaledCardImages = new HashMap<Card, Image>();
		cardScaleFactor = newCardScaleFactor;
		randomPlacement = newRandomPlacement;

		for (final Card card : Card.values()) {
			scaledCardImages.put(card,
					bitmaps.getCardImage(card.getSuit(), card.getRank()));
		}

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
			cardFace = options.getCardFace();
			scaleImages();
		}

		final int panelWidth = getWidth();
		final int panelHeight = getHeight();
		double xPos = 0.0;
		double yPos = 0.0;

		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		for (int i = 0; i < trick.size(); i++) {

			final Card card = trick.get(i);
			final Player player = positions.get(i);

			final Image image = scaledCardImages.get(card);
			assert image != null;

			// Calculate translation
			final double xScaleSize = image.getWidth(this);
			final double xAllTrickCardsSize = xScaleSize * (1 + 2.0 / 3.00);
			final double xCenterTranslate = (panelWidth - xAllTrickCardsSize) / 2;
			final double yScaleSize = image.getHeight(this);
			final double yAllTrickCardsSize = yScaleSize * 1.5;
			final double yCenterTranslate = (panelHeight - yAllTrickCardsSize) / 2;

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

			final AffineTransform transform = new AffineTransform();
			transform.setToIdentity();
			transform.translate(xPos, yPos);
			transform.rotate(cardRotations.get(i).doubleValue());

			g2D.drawImage(image, transform, this);
		}
	}

	boolean isNewCardFace() {
		return !cardFace.equals(options.getCardFace());
	}

	void setUserPosition(final Player newUserPosition) {

		userPosition = newUserPosition;
		leftOpponent = userPosition.getLeftNeighbor();
		rightOpponent = userPosition.getRightNeighbor();
	}

	private void scaleImages() {

		final int panelWidth = getWidth();
		final int panelHeight = getHeight();

		final Image sampleCard = bitmaps.getCardImage(Suit.CLUBS, Rank.JACK);
		final int imageWidth = sampleCard.getWidth(this);
		final int imageHeight = sampleCard.getHeight(this);

		final double scaleX = ((100.0 * panelWidth) / (imageWidth * 1.6)) / 100.0;
		final double scaleY = ((100.0 * panelHeight) / (imageHeight * 1.6)) / 100.0;

		double scaleFactor = 1.0;
		if (scaleX < 1.0 || scaleY < 1.0) {
			if (scaleX < scaleY) {
				scaleFactor *= scaleX;
			} else {
				scaleFactor *= scaleY;
			}
		}
		scaleFactor *= cardScaleFactor;

		for (final Card card : Card.values()) {

			final Image cardImage = bitmaps.getCardImage(card.getSuit(),
					card.getRank());

			final int scaledWidth = (int) (imageWidth * scaleFactor);
			final int scaledHeight = (int) (imageHeight * scaleFactor);

			final BufferedImage scaledImage = new BufferedImage(scaledWidth,
					scaledHeight, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g2 = scaledImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.drawImage(cardImage, 0, 0, scaledWidth, scaledHeight, null);
			g2.dispose();

			scaledCardImages.put(card, scaledImage);
		}
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		scaleImages();
		repaint();
	}

	@Override
	public void componentMoved(final ComponentEvent e) {
		// not needed
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		scaleImages();
		repaint();
	}

	@Override
	public void componentHidden(final ComponentEvent e) {
		// not needed
	}
}
