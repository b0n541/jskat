/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

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
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Renders all cards of a trick
 */
class TrickPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Player trickForeHand;
	private JSkatGraphicRepository bitmaps;
	private List<Player> positions;
	private CardList trick;
	private Random rand = new Random();
	private Player userPosition;
	private Player rightOpponent;
	private Player leftOpponent;

	/**
	 * Constructor
	 * 
	 * @param jskatBitmaps JSkat bitmaps
	 */
	TrickPanel(JSkatGraphicRepository jskatBitmaps) {
		
		this.bitmaps = jskatBitmaps;
		
		this.trick = new CardList();
		this.positions = new ArrayList<Player>();
		
		this.setOpaque(false);
	}
	
	/**
	 * Sets the fore hand for the trick
	 * 
	 * @param player Trick fore hand
	 */
	void setTrickForeHand(Player player) {
		
		this.trickForeHand = player;
	}
	
	/**
	 * Adds a card to the trick
	 * 
	 * @param player Position of the player
	 * @param card Card
	 */
	void addCard(Player player, Card card) {
		
		if (this.trick.size() == 3) {
			// trick is full -> clear it first
			this.positions.clear();
			this.trick.clear();
		}
		this.positions.add(player);
		this.trick.add(card);
		repaint();
	}

	/**
	 * Removes the last card
	 */
	void removeCard() {
		
		this.positions.remove(this.positions.size() - 1);
		this.trick.remove(this.trick.size() - 1);
		repaint();
	}
	
	/**
	 * Removes all cards from the trick
	 */
	void clearCards() {

		this.positions.clear();
		this.trick.clear();
		repaint();
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();
		double scaleFactor = 1.0d;
		double xPos = 0.0d;
		double yPos = 0.0d;

		Graphics2D g2D = (Graphics2D) g;

		for (int i = 0; i < this.trick.size(); i++) {
			
			Card card = this.trick.get(i);
			Player player = this.positions.get(i);

			Image image = this.bitmaps.getCardImage(card.getSuit(), card.getRank());
			
			// Calculate scale factor
			// Image should have 66% of panel height
			scaleFactor = 1.0d / (image.getHeight(null) / (panelHeight * 2.0d / 3.0d));
			
			// Calculate translation
			double xScaleSize = image.getWidth(null) * scaleFactor;
			double yScaleSize = image.getHeight(null) * scaleFactor;
			double centerTranslate = (panelWidth - xScaleSize) / 2.0d - xScaleSize / 2.0d;

			if (player == this.leftOpponent) {

				xPos = 0.0d + centerTranslate;
				yPos = yScaleSize / 4.0d;
			}
			else if (player == this.rightOpponent) {

				xPos = (2 * (xScaleSize / 3.0d)) + centerTranslate;
				yPos = 0.0d;
			}
			else if (player == this.userPosition) {
				xPos = (xScaleSize / 3.0d) + centerTranslate;
				yPos = 2 * (yScaleSize / 4.0d);
			}

			AffineTransform transform = new AffineTransform();
			transform.setToIdentity();
			transform.translate(xPos, yPos);
			transform.scale(scaleFactor, scaleFactor);
//			transform.rotate(rand.nextDouble());
			
			g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			
			g2D.drawImage(image, transform, this);
		}
	}

	void setUserPosition(Player newUserPosition) {
		
		this.userPosition = newUserPosition;
		this.leftOpponent = this.userPosition.getLeftNeighbor();
		this.rightOpponent = this.userPosition.getRightNeighbor();
	}
}
