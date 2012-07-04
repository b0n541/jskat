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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

import javax.swing.Action;
import javax.swing.JPanel;

import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for showing cards on a hand
 */
class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CardPanel.class);

	private final JSkatGraphicRepository bitmaps;

	double scaleFactor = 1.0;

	/**
	 * Holds the game type for the sorting order
	 */
	GameType sortGameType = GameType.GRAND;

	CardList cards;

	private boolean showBackside = true;
	private JPanel parent = null;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param parent
	 *            Parent panel
	 * @param scaleFactor
	 *            Scale factor for cards
	 * @param showBackside
	 *            TRUE if the Card should hide its face
	 */
	CardPanel(final JPanel parent, final double scaleFactor, final boolean showBackside) {

		setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.parent = parent;
		setActionMap(parent.getActionMap());
		bitmaps = JSkatGraphicRepository.instance();
		this.scaleFactor = scaleFactor;
		this.showBackside = showBackside;

		cards = new CardList();

		setOpaque(false);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				// not needed
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseReleased(final MouseEvent e) {

				cardClicked(e);
			}
		});
	}

	protected void addCard(final Card newCard) {

		cards.add(newCard);
		cards.sort(sortGameType);
		repaint();
	}

	protected void addCards(final CardList newCards) {

		cards.addAll(newCards);
		cards.sort(sortGameType);
		repaint();
	}

	protected void removeCard(final Card cardToRemove) {

		if (cards.contains(cardToRemove)) {
			cards.remove(cardToRemove);
		} else if (showBackside) {
			// card panels with hidden cards may contain unknown cards
			// remove the last one
			cards.remove(cards.size() - 1);
		}
		repaint();
	}

	protected Card get(final int index) {

		return cards.get(index);
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected synchronized void paintComponent(final Graphics g) {

		super.paintComponent(g);

		// copying cards prevents ConcurrentModificationException
		CardList cardsToPaint = new CardList(cards);

		// rendering hints
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// calculate card gap
		int panelWidth = getWidth();
		int cardWidth = bitmaps.getCardImage(Suit.CLUBS, Rank.JACK).getWidth(this);
		int cardGap = cardWidth;
		if (cards.size() * cardGap > panelWidth) {

			cardGap = (panelWidth - cardWidth) / (cards.size() - 1);
		}

		// paint all cards
		int cardNo = 0;
		for (Card card : cardsToPaint) {

			Image image = null;

			if (showBackside) {

				image = bitmaps.getCardImage(null, null);
			} else {
				if (bitmaps == null || card == null) {
					// e.g. in debug mode
					image = bitmaps.getCardImage(null, null);
				} else {
					image = bitmaps.getCardImage(card.getSuit(), card.getRank());
				}
			}

			AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);
			transform.translate(cardNo * cardGap, 0);
			g2D.drawImage(image, transform, this);

			cardNo++;
		}
	}

	/**
	 * Clears the card panel
	 */
	void clearCards() {

		cards.clear();
		repaint();
	}

	/**
	 * Flips the card
	 */
	void flipCard() {

		if (showBackside) {

			showCards();
		} else {

			hideCards();
		}
	}

	/**
	 * Shows the card
	 */
	void showCards() {

		showBackside = false;
		repaint();
	}

	/**
	 * Hides the card
	 */
	void hideCards() {

		if (!JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
			showBackside = true;
			repaint();
		}
	}

	int getCardCount() {

		return cards.size();
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	void cardClicked(final MouseEvent e) {
		// FIXME (jan 04.12.2010) refactor this method, nobody understands it
		int xPosition = e.getX();
		int yPosition = e.getY();

		//		log.debug("Card panel clicked at: " + xPosition + " x " + yPosition); //$NON-NLS-1$ //$NON-NLS-2$

		if (xPosition > -1 && xPosition < getWidth() && yPosition > -1 && yPosition < getHeight()) {

			//			log.debug("Mouse button release inside panel"); //$NON-NLS-1$

			// get card
			double cardWidth = bitmaps.getCardImage(Suit.CLUBS, Rank.JACK).getWidth(this);

			int cardIndex = -1;
			if (cards.size() > 0) {
				if (cards.size() == 1) {
					log.debug("only on card on hand"); //$NON-NLS-1$
					if (xPosition < cardWidth) {
						cardIndex = 0;
					}
				} else {
					double distanceBetweenCards = cardWidth;
					if (cards.size() > 1) {
						distanceBetweenCards = (getWidth() - cardWidth) / (cards.size() - 1.0);
					}

					if (cardWidth > distanceBetweenCards) {
						// cards without gaps
						log.debug("cards without gaps"); //$NON-NLS-1$
						cardIndex = 0;
						while (!((cardIndex * distanceBetweenCards) < xPosition && ((cardIndex + 1)
								* distanceBetweenCards > xPosition))
								&& cardIndex < (cards.size() - 1)) {
							cardIndex++;
						}
					} else {
						// cards with gaps
						log.debug("cards with gaps"); //$NON-NLS-1$
						double cardGap = 0.0;

						if ((int) ((xPosition / (cardWidth + cardGap))) == (int) ((xPosition + cardGap) / (cardWidth + cardGap))) {
							cardIndex = (int) (xPosition / (cardWidth + cardGap));
						}
					}
				}
			}

			Card card = null;
			if (cardIndex > -1 && cardIndex < cards.size()) {

				card = cards.get(cardIndex);
				//				log.debug("card index: " + cardIndex + " card: " + cards.get(cardIndex)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (card != null) {
				// send event only, if the card panel shows a card
				Action action = null;

				if (parent instanceof DiscardPanel) {
					// card panel in discard panel was clicked
					action = getActionMap().get(JSkatAction.TAKE_CARD_FROM_SKAT);
				} else if (parent instanceof JSkatUserPanel) {
					// card panel in player panel was clicked

					GameState state = ((JSkatUserPanel) parent).getGameState();

					if (state == GameState.DISCARDING || state == GameState.SCHIEBERAMSCH) {
						// discarding phase
						action = getActionMap().get(JSkatAction.PUT_CARD_INTO_SKAT);
					} else if (state == GameState.TRICK_PLAYING) {
						// trick playing phase
						action = getActionMap().get(JSkatAction.PLAY_CARD);
					}
				} else {

					log.debug("Other parent " + parent); //$NON-NLS-1$
				}

				if (action != null) {

					action.actionPerformed(new ActionEvent(Card.getCardFromString(card.getSuit().shortString()
							+ card.getRank().shortString()), ActionEvent.ACTION_PERFORMED, (String) action
							.getValue(Action.ACTION_COMMAND_KEY)));
				} else {

					log.debug("Action is null"); //$NON-NLS-1$
				}
			}
		}
	}

	void setSortType(final GameType newGameType) {

		sortGameType = newGameType;
		cards.sort(sortGameType);
		repaint();
	}
}
