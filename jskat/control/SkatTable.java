/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.control;

import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;

import jskat.data.JSkatDataModel;
import jskat.data.SkatTableData;
import jskat.data.SkatTableOptions;
import jskat.gui.main.CardHoldingPanel;
import jskat.gui.main.LastTricksDialog;
import jskat.player.HumanPlayer;
import jskat.player.JSkatPlayer;
import jskat.share.SkatConstants;

import org.apache.log4j.Logger;

/**
 * SkatTable controls a SkatSeries
 */
public class SkatTable extends Observable implements Observer {

	private static final Logger log = Logger
			.getLogger(jskat.control.SkatTable.class);

	/**
	 * Constructor
	 * 
	 * @param dataModel The JSkatDataModel
	 */
	public SkatTable(JSkatDataModel dataModel) {

		// initialize all variables
		this.dataModel = dataModel;
		jskatStrings = dataModel.getResourceBundle();
		// TODO make a copy/clone of options
		// 25.05.2007 mjl: Why?
		this.skatTableOptions = dataModel.getJSkatOptions()
				.getSkatTableOptions();
		tableData = new SkatTableData();
		player = new Vector<JSkatPlayer>();
		state = NEW_SERIES;
		SchiebeRamschThread.init(dataModel);
		
		log.debug("SkatTable is ready.");
	}

	/**
	 * Gets the player objects sitting on the table
	 * 
	 * @return Players
	 */
	public JSkatPlayer[] getPlayers() {
		return players;
	}

	
	/**
	 * Adds a player to the players on the table
	 * 
	 * @param newPlayer
	 * @return FALSE, if there are already five players Otherwise TRUE
	 */
	public boolean addPlayer(JSkatPlayer newPlayer) {

		boolean result = false;

		if (isFreePlayerSeat()) {

			player.add(newPlayer);

			result = true;
		}

		return result;
	}

	/**
	 * Asks whether there is a free player seat at the table or not
	 * 
	 * @return TRUE, if there is a free seat Otherwise FALSE
	 */
	public boolean isFreePlayerSeat() {

		boolean result = false;

		if (player.size() < 5) {

			result = true;
		}

		return result;
	}

	/**
	 * Starts a new skat series at this table
	 * 
	 * @param numberOfRounds Maximal number of rounds to be played 
	 * @param playerNames Names of the players
	 * @param playerClasses Class names of the players
	 * 
	 * @return TRUE, if the new series has started successfully Otherwise, FALSE
	 */
	public boolean createNewSkatSeries(int numberOfRounds, String[] playerNames,
			String[] playerClasses) {

		boolean result = false;

		if (state != SERIES_STARTED) {

			players = new JSkatPlayer[3];
			for (int i = 0; i < 3; i++) {
				try {

					if (SkatConstants.HUMANPLAYER.equals(playerClasses[i])) {

						players[i] = new HumanPlayer(i, playerNames[i]);

					} else {

						players[i] = (JSkatPlayer) Class.forName(
								"jskat.player." + playerClasses[i] + "."
										+ playerClasses[i]).newInstance();
						players[i].setPlayerID(i);
						players[i].setPlayerName(playerNames[i]);
					}

				} catch (ClassNotFoundException e) {

					log.error(playerClasses[i] + " not found!");

				} catch (Exception e) {

					log.error("Error loading " + playerClasses[i] + ": (" + e
							+ ")");
				}
			}

			// Set randomly who is playing forehand, middlehand and hindhand
			// at the beginning
			// TODO implement this in SkatSeries
			// playerOrder = new int[3];
			// setNewForeHandPlayer((int) Math.floor(3 * Math.random()));
			// foreHandPlayerAtGameStart = playerOrder[SkatConstants.FORE_HAND];

			SkatSeries series = new SkatSeries(dataModel, players,
					numberOfRounds);
			series.addObserver(this);
			tableData.addSkatSeries(series);
			
			setChanged();
			notifyObservers(series);

			// TODO (28.05.07 mjl): This is a "dirty" initialization of LastTricksDialog!!!
			LastTricksDialog.getInstance().setSkatTable(this);

			setState(SERIES_STARTED);

			series.startPlaying();
			
			result = true;

		} else {

			log.debug("Skat series is still in progress...");
		}

		return result;
	}

	/**
	 * Sets the state for the skat table
	 * 
	 * @param newState
	 *            The new state to be set
	 */
	private void setState(int newState) {

		state = newState;

		setChanged();
		notifyObservers(new Integer(newState));
	}

	/**
	 * Responds to a click on a card panel
	 * 
	 * @param panelType
	 *            ID of the CardHoldingPanel that holds the card that was
	 *            clicked
	 * @param suit
	 *            Suit of the card that was clicked
	 * @param rank
	 *            Rank of the card that was clicked
	 */
	public void cardPanelClicked(CardHoldingPanel.PanelTypes panelType, SkatConstants.Suits suit, SkatConstants.Ranks rank) {

		log.debug("card panel clicked: type: " + panelType + " suit: " + suit
				+ " value: " + rank);
		log.debug("SkatTable state: " + state);

		SkatGame currGame = tableData.getCurrSkatSeries().getCurrSkatGame();
		SkatGame.GameState gameState = currGame.getState();
		
		log.debug("SkatGame state: " + gameState);
		log.debug("CardHoldingPanel: " + panelType);
		
		if (gameState == SkatGame.GameState.SHOWING_SKAT) {

			if (panelType == CardHoldingPanel.PanelTypes.PLAYER) {

				if (currGame.getSkatGameData().getGameType() == SkatConstants.GameTypes.RAMSCH
						&& !skatTableOptions.isSchieberRamschJacksInSkat()
						&& rank == SkatConstants.Ranks.JACK ) {

					log.info("Human player has tried to put a Jack into skat although it's not allowed!");
					
					JOptionPane.showMessageDialog(dataModel.getMainWindow(),
							jskatStrings.getString("ramsch_no_jacks"),
							jskatStrings.getString("skat_rules"),
							JOptionPane.WARNING_MESSAGE);
				}
				else {
					currGame.putCardIntoSkat(suit, rank);
				}
					

			} else if (panelType == CardHoldingPanel.PanelTypes.SKAT) {

				currGame.takeCardFromSkat(suit, rank);
			
			}

		} else if (currGame.getState() == SkatGame.GameState.WAIT_FOR_HUMAN_PLAYER_INPUT
				&& panelType == CardHoldingPanel.PanelTypes.PLAYER) {

			log.debug("Waiting for player input and received click");

			currGame.playTrickCard(suit, rank);

		} else if (currGame.getState() == SkatGame.GameState.TRICK_COMPLETED
				&& panelType == CardHoldingPanel.PanelTypes.TRICK) {

			currGame.calculateTrickWinner();
		}
	}

	/**
	 * Gets the current player order
	 * 
	 * @return The player order as Array of int
	 */
	public int[] getCurrentPlayerOrder() {

		return playerOrder;
	}

	/**
	 * Gets the table options
	 * 
	 * @return Table options
	 */
	public SkatTableOptions getSkatTableOptions() {

		return skatTableOptions;
	}

	/**
	 * Returns the skat table data
	 * 
	 * @return The skat table data
	 */
	public SkatTableData getSkatTableData() {

		return tableData;
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		
		if (o instanceof SkatSeries) {
			
			if (arg instanceof SkatSeries.SeriesStates) {
				
				if ((SkatSeries.SeriesStates) arg == SkatSeries.SeriesStates.SERIES_FINISHED) {
					
					setState(SERIES_FINISHED);
				}
			}
		}
	}

	private JSkatDataModel dataModel;

	private SkatTableData tableData;

	private SkatTableOptions skatTableOptions;

	// TODO hold players only in one collection
	/** Holds up to five skat players */
	private Vector<JSkatPlayer> player;

	/** Holds all players */
	private JSkatPlayer[] players = null;

	/** Holds the order of players */
	private int[] playerOrder;

	/** Holds the state of the JSkatTable */
	private int state;

	private static ResourceBundle jskatStrings;

	private final int NEW_SERIES = 0;
	private final int SERIES_STARTED = 1;
	private final int SERIES_FINISHED = 2;
	
	// TODO implement game score
	// TODO implement Kiebitze
}
