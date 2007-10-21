/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import java.util.Observer;
import java.util.Observable;

import jskat.control.JSkatMaster;
import jskat.control.SkatGame;
import jskat.control.SkatSeries;
import jskat.control.SkatTable;
import jskat.data.JSkatDataModel;
import jskat.data.SkatGameData;
import jskat.data.SkatSeriesData;
import jskat.data.SkatTableData;
import jskat.gui.JSkatGraphicRepository;
import jskat.player.JSkatPlayer;

/**
 * The JSkatPlayArea that holds all CardHoldingPanels and the BiddingPanel
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class JSkatPlayArea extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final Logger log = Logger
			.getLogger(jskat.gui.main.JSkatPlayArea.class);

	private static final long serialVersionUID = -5438519797373764725L;

	/**
	 * Creates a new instance of JSkatPlayArea
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param jskatBitmaps
	 *            The JSkatGraphicRepository the holds all images used in JSkat
	 */
	public JSkatPlayArea(JSkatDataModel dataModel,
			JSkatGraphicRepository jskatBitmaps) {

		this.dataModel = dataModel;
		dataModel.addObserver(this);
		this.jskatBitmaps = jskatBitmaps;
		initComponents();

		log.debug("JSkatPlayArea is ready.");
	}

	private void initComponents() {

		setLayout(new GridLayout(3, 1));
		setPreferredSize(new Dimension(800, 500));

		opponentPanel = new JPanel();
		opponentPanel.setLayout(new GridLayout(1, 2));
		opponentPanel.setOpaque(false);
		opponentPanel.add(new CardHoldingPanel(dataModel, 0,
				CardHoldingPanel.OPPONENT_PANEL, 10, jskatBitmaps));
		opponentPanel.add(new CardHoldingPanel(dataModel, 1,
				CardHoldingPanel.OPPONENT_PANEL, 10, jskatBitmaps));
		add(opponentPanel);

		skatTrickHoldingPanel = new JPanel();
		skatTrickHoldingPanel.setOpaque(false);
		skatTrickHoldingPanel.setLayout(new CardLayout());
		trickPanel = new JPanel();
		trickPanel.setOpaque(false);
		trickPanel.add(new CardHoldingPanel(dataModel, 3,
				CardHoldingPanel.TRICK_PANEL, 3, jskatBitmaps));
		skatTrickHoldingPanel.add(trickPanel, "trick");
		skatPanel = new JPanel();
		skatPanel.setOpaque(false);
		skatPanel.add(new CardHoldingPanel(dataModel, 4,
				CardHoldingPanel.SKAT_PANEL, 4, jskatBitmaps));
		skatTrickHoldingPanel.add(skatPanel, "skat");
		bidPanel = new BiddingPanel(dataModel);
		skatTrickHoldingPanel.add(bidPanel, "bidding");
		add(skatTrickHoldingPanel);

		playerPanel = new JPanel();
		playerPanel.setLayout(new GridLayout(1, 1));
		playerPanel.setOpaque(false);
		playerPanel.add(new CardHoldingPanel(dataModel, 2,
				CardHoldingPanel.PLAYER_PANEL, 10, jskatBitmaps));
		add(playerPanel);
	}

	/**
	 * Overwrites the paintComponent method of JPanel draws the wooden table in
	 * the background
	 * 
	 * @param g
	 *            The Graphics environment
	 */
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.drawImage(jskatBitmaps.getSkatTableImage(), 0, 0, this);
	}

	/**
	 * Implementation of the Observer pattern
	 * 
	 * @param observ
	 *            The Observable that is observed
	 * @param obj
	 *            The Object that has changed in the Observable
	 */
	public void update(Observable observ, Object obj) {

		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");

		if (observ instanceof JSkatMaster && obj instanceof SkatTable) {

			// Observ skat table
			skatTable = ((SkatTable) obj);
			skatTable.addObserver(this);
			skatTable.getSkatTableData().addObserver(this);
			// give the reference to the skat table to
			// the card holding panel at the bottom of the JSkatPlayArea
			// the skat panel
			// and the trick panel
			// --> clicks on the cards are recognized from now on
			getCardHoldingPanel(PLAYER_TWO).setSkatTable(skatTable);
			getSkatPanel().setSkatTable(skatTable);
			getTrickPanel().setSkatTable(skatTable);

		} else if (observ instanceof SkatTableData && obj instanceof SkatSeries) {

			// Observ skat series
			SkatSeries series = ((SkatSeries) obj);
			series.addObserver(this);
			series.getSkatSeriesData().addObserver(this);

			// get player names
			setPlayerData(series.getSkatSeriesData().getPlayers());

		} else if (observ instanceof SkatSeriesData && obj instanceof SkatGame) {

			SkatGame game = ((SkatGame) obj);
			game.addObserver(this);
			game.addObserver(getCardHoldingPanel(PLAYER_ZERO));
			game.addObserver(getCardHoldingPanel(PLAYER_ONE));
			game.addObserver(getCardHoldingPanel(PLAYER_TWO));
			SkatGameData gameData = game.getSkatGameData();
			gameData.addObserver(this);
			gameData.getPlayerCards(0).addObserver(
					getCardHoldingPanel(PLAYER_ZERO));
			gameData.getPlayerCards(1).addObserver(
					getCardHoldingPanel(PLAYER_ONE));
			gameData.getPlayerCards(2).addObserver(
					getCardHoldingPanel(PLAYER_TWO));
			gameData.addObserver(getTrickPanel());
			gameData.getSkat().addObserver(getSkatPanel());

		
		} else if (observ instanceof SkatSeries && obj instanceof Integer) {

			// state of current series has changed
			Integer state = (Integer) obj;

			log.debug("Skat series state changed to " + state);

			switch (state.intValue()) {

			case SkatSeries.SERIES_FINISHED:
				getCardHoldingPanel(0).clearPanel();
				getCardHoldingPanel(1).clearPanel();
				getCardHoldingPanel(2).clearPanel();
				getTrickPanel().clearPanel();
				((CardLayout) skatTrickHoldingPanel.getLayout()).show(
						skatTrickHoldingPanel, "trick");
				break;
			}

		} else if (observ instanceof SkatGame && obj instanceof Integer) {

			// state of current game has changed
			SkatGame game = (SkatGame) observ;
			Integer state = (Integer) obj;

			switch (state.intValue()) {

			case BIDDING:
				game.getBidStatus().addObserver(bidPanel);
				((CardLayout) skatTrickHoldingPanel.getLayout()).show(
						skatTrickHoldingPanel, "bidding");
				break;

			case SkatGame.GAMESTATE_DEALING:
			case SkatGame.GAMESTATE_PLAYING:
				((CardLayout) skatTrickHoldingPanel.getLayout()).show(
						skatTrickHoldingPanel, "trick");
				break;

			case SkatGame.GAMESTATE_SHOWING_SKAT:
				((CardLayout) skatTrickHoldingPanel.getLayout()).show(
						skatTrickHoldingPanel, "skat");
				break;
			}
		}
	}

	/**
	 * Flips all cards on the JSkatPlayArea
	 */
	public void flipCards() {

		getCardHoldingPanel(PLAYER_ZERO).flipCards();
		getCardHoldingPanel(PLAYER_ONE).flipCards();
	}

	/**
	 * Sets all player data
	 * 
	 * @param players
	 *            Array of all players
	 */
	private void setPlayerData(JSkatPlayer players[]) {

		getCardHoldingPanel(PLAYER_ZERO).setPlayerName(
				players[0].getPlayerName());
		getCardHoldingPanel(PLAYER_ONE).setPlayerName(
				players[1].getPlayerName());
		getCardHoldingPanel(PLAYER_TWO).setPlayerName(
				players[2].getPlayerName());
	}

	/**
	 * Returns the CardHoldingPanel for a player
	 * 
	 * @param panelID
	 *            The panel type
	 * @return The CardHoldingPanel
	 */
	public CardHoldingPanel getCardHoldingPanel(int panelID) {

		CardHoldingPanel panel;

		switch (panelID) {

		case PLAYER_ZERO:

			panel = (CardHoldingPanel) opponentPanel.getComponent(0);
			break;

		case PLAYER_ONE:

			panel = (CardHoldingPanel) opponentPanel.getComponent(1);
			break;

		case PLAYER_TWO:

			panel = (CardHoldingPanel) playerPanel.getComponent(0);
			break;

		case SKAT:

			// skat panel
			panel = getSkatPanel();
			break;

		case TRICK:

			// trick panel
			panel = getTrickPanel();
			break;

		default:
			panel = (CardHoldingPanel) new JPanel();
			break;
		}

		return panel;
	}

	public BiddingPanel getBiddingPanel() {

		return bidPanel;
	}

	public CardHoldingPanel getTrickPanel() {

		return (CardHoldingPanel) trickPanel.getComponent(0);
	}

	public CardHoldingPanel getSkatPanel() {

		return (CardHoldingPanel) skatPanel.getComponent(0);
	}

	private JSkatDataModel dataModel;

	private SkatTable skatTable;

	private JSkatGraphicRepository jskatBitmaps;

	private JPanel opponentPanel;

	private JPanel skatTrickHoldingPanel;

	private JPanel skatPanel;

	private JPanel trickPanel;

	private BiddingPanel bidPanel;

	private JPanel playerPanel;

	private final int PLAYER_ZERO = 0;
	private final int PLAYER_ONE = 1;
	private final int PLAYER_TWO = 2;
	private final int TRICK = 3;
	private final int BIDDING = 4;
	private final int SKAT = 5;
	
}
