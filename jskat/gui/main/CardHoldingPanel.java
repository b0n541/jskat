/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import java.util.Observer;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.Iterator;

import jskat.control.SkatGame;
import jskat.control.SkatTable;
import jskat.data.JSkatDataModel;
import jskat.data.SkatGameData;
import jskat.data.Trick;
import jskat.gui.JSkatGraphicRepository;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * A JPanel that holds the CardPanels
 * 
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class CardHoldingPanel extends JPanel implements Observer {

	private static final Logger log = Logger
			.getLogger(jskat.gui.main.CardHoldingPanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2913589400367875369L;

	/**
	 * Creates a new instance of CardHoldingPanel
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param player
	 *            The player ID
	 * @param panelType
	 *            The type of the CardHoldingPanel
	 * @param maxCardCount
	 *            Maximal number of cards the panel can hold
	 * @param jskatBitmaps
	 *            The JSkatGraphicsRepository that holds all images that are
	 *            used in JSkat
	 */
	public CardHoldingPanel(JSkatDataModel dataModel, int player,
			int panelType, int maxCardCount, JSkatGraphicRepository jskatBitmaps) {

		this.player = player;
		this.maxCardCount = maxCardCount;
		this.cardCount = 0;

		this.jskatBitmaps = jskatBitmaps;
		jskatBitmaps.addObserver(this);
		this.jskatStrings = dataModel.getResourceBundle();
		this.mainWindow = dataModel.getMainWindow();

		this.panelType = panelType;
		cardPanels = new Vector<CardPanel>();

		initComponents();

		log.debug("CardHoldingPanel is ready.");
	}

	private void initComponents() {

		// all CardHoldingPanels have BorderLayout
		setLayout(new BorderLayout());
		removeAll();
		// These panels are only placeholders for the BorderLayout
		JPanel northPanel = new JPanel();
		JPanel eastPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JPanel westPanel = new JPanel();
		// the cards lie on the center panel
		JPanel centerPanel = new JPanel();

		// let the skat table shining through
		setOpaque(false);

		switch (panelType) {
		case CardHoldingPanel.OPPONENT_PANEL:
			// no gab between the cards of the opponent panels
			((FlowLayout) centerPanel.getLayout()).setHgap(0);
			break;
		case CardHoldingPanel.PLAYER_PANEL:
			break;
		case CardHoldingPanel.SKAT_PANEL:
			break;
		case CardHoldingPanel.TRICK_PANEL:
			break;
		}

		CardPanelMouseAdapter mouseAdapter = new CardPanelMouseAdapter();

		// create the card panels
		for (int i = 0; i < maxCardCount; i++) {

			CardPanel panel;

			// create a card
			if (panelType == CardHoldingPanel.OPPONENT_PANEL) {

				// show the back of the opponent cards
				panel = new CardPanel(this, jskatBitmaps, true);
			} else {

				// show the face of all the other cards
				panel = new CardPanel(this, jskatBitmaps, false);
			}

			// different sizes for card panels
			if (i != (maxCardCount - 1)
					&& panelType == CardHoldingPanel.OPPONENT_PANEL) {

				// some card panels for the opponent cards are smaller
				panel.setPreferredSize(new Dimension(25, 97));
			} else {

				// all the other card panels have the normal size
				panel.setPreferredSize(new Dimension(73, 97));
			}

			// let the skat table shining through if there is no card shown
			panel.setOpaque(false);

			if (panelType == CardHoldingPanel.PLAYER_PANEL
					|| panelType == CardHoldingPanel.SKAT_PANEL
					|| panelType == CardHoldingPanel.TRICK_PANEL) {

				// the mouse adapter listens to user clicks
				panel.addMouseListener(mouseAdapter);
			}

			// add it to the Vector that holds all panels
			cardPanels.add(panel);

			// add the card to the centerPanel
			centerPanel.add(panel);
		}

		if (panelType == CardHoldingPanel.PLAYER_PANEL
				|| panelType == CardHoldingPanel.OPPONENT_PANEL) {

			// Show player name on the player panels
			JPanel playerNamePanel = new JPanel();
			playerName = new JLabel(" ");
			playerNamePanel.add(playerName);
			playerNamePanel.setPreferredSize(new Dimension(150,
					(int) playerNamePanel.getPreferredSize().getHeight()));

			if (panelType == CardHoldingPanel.PLAYER_PANEL) {

				southPanel.add(playerNamePanel);
			} else if (panelType == CardHoldingPanel.OPPONENT_PANEL) {

				northPanel.add(playerNamePanel);
			}

		} else if (panelType == CardHoldingPanel.SKAT_PANEL) {

			// OK-Button for starting game after looking into the skat
			JButton okButton = new JButton(jskatStrings.getString("ok"));

			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent evt) {

					if (cardCount != 2) {

						log.debug("CardCount of skat: " + cardCount);

						JOptionPane.showMessageDialog(mainWindow, jskatStrings
								.getString("two_cards_to_discard"),
								jskatStrings.getString("hint"),
								JOptionPane.INFORMATION_MESSAGE);
					} else {

						skatTable.getSkatTableData().getCurrSkatGame()
								.skatProcessed();
					}
				}
			});

			southPanel.add(okButton);
		}

		// put all panels together
		centerPanel.setOpaque(false);
		add(centerPanel, BorderLayout.CENTER);
		northPanel.setOpaque(false);
		add(northPanel, BorderLayout.NORTH);
		eastPanel.setOpaque(false);
		add(eastPanel, BorderLayout.EAST);
		southPanel.setOpaque(false);
		add(southPanel, BorderLayout.SOUTH);
		westPanel.setOpaque(false);
		add(westPanel, BorderLayout.WEST);
	}

	/**
	 * Implementation of the Observer pattern
	 * 
	 * @param observ
	 *            The Observable that is observed
	 * @param obj
	 *            Object that has changed in the Observable
	 */
	public void update(Observable observ, Object obj) {

		// log.debug("UPDATE panel #" + panelType + ", player " + player + " " + observ + ": " + obj + " has changed...");

		if (observ instanceof JSkatGraphicRepository) {

			repaint();
			
		} else if (observ instanceof SkatGame && obj instanceof Integer) {
			if(((Integer) obj).intValue()==SkatGame.GAMESTATE_PLAYING) {

				// first, make a CardVector out of the card panels
				CardVector cv = new CardVector();
				for(int i=0;i<cardPanels.size();i++) {
					if( cardPanels.get(i).getSuit()>=0) {
						cv
							.add(new Card(
								cardPanels.get(i).getSuit(),
								cardPanels.get(i).getValue() ));
					}
				}

				// then sort the CardVector
				int gameType = ((SkatGame) observ).getSkatGameData().getGameType();
				int trump = ((SkatGame) observ).getSkatGameData().getTrump();
				log.debug("GameType:"+gameType+", Trump:"+trump+", Cards: "+cv);
				cv.sort(gameType, trump);
				
				// and now reorganize the cards to reflect the new sorting order
				reorganizeCards(cv);
				repaint();
				
			}
		} else if (observ instanceof CardVector) {

			CardVector obsCardVector = ((CardVector) observ);

			// if the cards have changed
			if (obsCardVector.size() != cardCount) {

				if (obsCardVector.size() > cardCount) {

					// new card was added to the CardVector
					addCard(obsCardVector);
					obsCardVector.sort(SkatConstants.SUIT);
					
				} else {

					// card was removed from the CardVector
					removeCard(obsCardVector);
				}

			} else {

				// no change in the card count, rearrange the cards
				reorganizeCards(obsCardVector);
			}

			repaint();

		} else if (observ instanceof SkatGameData && obj instanceof Trick) {

			if (panelType == 4) {

				// only the trick panel should change
				Trick trick = (Trick) obj;
				Card card = trick.getCard(0);
				cardPanels.get(0).setCard(card.getSuit(), card
						.getValue());
				card = trick.getCard(1);
				cardPanels.get(1).setCard(card.getSuit(), card
						.getValue());
				card = trick.getCard(2);
				cardPanels.get(2).setCard(card.getSuit(), card
						.getValue());
			}

			repaint();
		}
		/*
		 * if (observ instanceof JSkatDataModel && obj instanceof JSkatMaster) {
		 * 
		 * skatTable = ((JSkatMaster)obj).getCurrentSkatTable(); } else if
		 * (observ instanceof SkatGameData && obj instanceof JSkatPlayer[]) {
		 * 
		 * if (panelType < 3) { // only player hand panels have a player name
		 * label playerName.setText(((JSkatPlayer[]) obj)[player]
		 * .getPlayerName()); } } else if (observ instanceof SkatGameData && obj
		 * instanceof Trick) {
		 * 
		 * if (panelType == 4) { // only the trick panel should change Trick
		 * trick = (Trick) obj; Card card = trick.getCard(0);
		 * cardPanels.get(0).setCard(card.getSuit(), card.getValue()); card =
		 * trick.getCard(1); cardPanels.get(1).setCard(card.getSuit(),
		 * card.getValue()); card = trick.getCard(2);
		 * cardPanels.get(2).setCard(card.getSuit(), card.getValue()); } } else
		 * if (observ instanceof CardVector) {
		 * 
		 * CardVector obsCardVector = ((CardVector) observ); // if the cards
		 * have changed if (obsCardVector.size() != cardCount) {
		 * 
		 * if (obsCardVector.size() > cardCount) { // new card was added to the
		 * CardVector addCard(obsCardVector); } else { // card was removed from
		 * the CardVector removeCard(obsCardVector); } } else { // no change in
		 * the card count, rearange the cards reorganizeCards(obsCardVector); } }
		 * 
		 * repaint();
		 */
	}

	private void addCard(CardVector updCardVector) {

		log.debug("addCard(): "+updCardVector);

		int i = 0;

		while (i < updCardVector.size()) {

			cardPanels.get(i).setCard(updCardVector.getCard(i)
					.getSuit(), updCardVector.getCard(i).getValue());

			i++;
		}

		while (i < maxCardCount) {

			cardPanels.get(i).setCard(-1, -1);
			i++;
		}

		cardCount++;
	}

	private void removeCard(CardVector updCardVector) {

		log.debug("removeCard(): "+updCardVector);

		CardPanel currCardPanel;

		for (int i = 0; i < cardPanels.size(); i++) {

			currCardPanel = cardPanels.get(i);

			if (!updCardVector.contains(currCardPanel.getSuit(), currCardPanel
					.getValue())) {

				currCardPanel.setCard(-1, -1);
			}
		}

		if (panelType == OPPONENT_PANEL) {

			reorganizeCards(updCardVector);
		}

		cardCount--;
	}

	/**
	 * Puts the cards in a new order
	 * 
	 * @param updCardVector
	 *            A card vector with the new order
	 */
	private void reorganizeCards(CardVector updCardVector) {

		// log.debug("Reorganizing the cards... "+updCardVector);
		
		int i = 0;

		while (i < maxCardCount - updCardVector.size()) {

			cardPanels.get(i).setCard(-1, -1);
			i++;
		}

		for (Iterator iterator = updCardVector.iterator(); iterator.hasNext();) {

			Card newCard = (Card) iterator.next();

			if (newCard.getSuit() != cardPanels.get(i).getSuit()
					|| newCard.getValue() != cardPanels.get(i).getValue()) {

				cardPanels.get(i).setCard(newCard.getSuit(),
						newCard.getValue());
			}
			i++;
		}
	}

	/**
	 * Get the type of the CardHoldingPanel
	 * 
	 * @return The type of the CardHoldingPanel
	 */
	public int getPanelType() {

		return panelType;
	}

	/**
	 * Get the player ID
	 * 
	 * @return The player ID
	 */
	public int getPlayerID() {

		return player;
	}

	/**
	 * Flips all cards
	 */
	public void flipCards() {

		for (int i = 0; i < cardPanels.size(); i++) {

			cardPanels.get(i).flipCard();
		}

		repaint();
	}

	/**
	 * Sets the reference to the skat table
	 * 
	 * @param newTable
	 *            The new skat table
	 */
	public void setSkatTable(SkatTable newTable) {

		skatTable = newTable;
	}

	/**
	 * Is executed when a card panel is clicked
	 * 
	 * @param suit
	 *            The suit of the clicked card panel
	 * @param value
	 *            The value of the clicked card panel
	 */
	public void cardPanelClicked(int suit, int value) {

		skatTable.cardPanelClicked(panelType, suit, value);
	}

	public void setPlayerName(String newName) {

		playerName.setText(newName);
	}

	/**
	 * Clears the card holding panel
	 */
	public void clearPanel() {
		
		Iterator<CardPanel> panelIterator = cardPanels.iterator();
		
		while (panelIterator.hasNext()) {
		
			panelIterator.next().setCard(-1, -1);
		}
		
		repaint();
	}
	
	/**
	 * An empty CardHoldingPanel
	 */
	public static final int EMPTY_PANEL = 0;

	/**
	 * CardHoldingPanel for the opponent players
	 */
	public static final int OPPONENT_PANEL = 1;

	/**
	 * CardHoldingPanel for the Human player
	 */
	public static final int PLAYER_PANEL = 2;

	/**
	 * CardHoldingPanel for the Skat
	 */
	public static final int SKAT_PANEL = 3;

	/**
	 * CardHoldingPanel for the tricks
	 */
	public static final int TRICK_PANEL = 4;

	private JSkatGraphicRepository jskatBitmaps;

	private ResourceBundle jskatStrings;

	private JFrame mainWindow;

	private SkatTable skatTable;

	private int player;

	private int panelType;

	private int maxCardCount;

	private int cardCount;

	private Vector<CardPanel> cardPanels;

	private JLabel playerName;
}
