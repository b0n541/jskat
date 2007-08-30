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
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 * 
 */
public class SkatTable extends Observable implements Observer {

	private static final Logger log = Logger
			.getLogger(jskat.control.SkatTable.class);

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
		SchieberRamschThread.init(dataModel);
		
		log.debug("SkatTable is ready.");
	}

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
			LastTricksDialog.getInstance().initNewSkatTable(this);

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
	 *            suit of the card that was clicked
	 * @param value
	 *            value of the card that was clicked
	 */
	public void cardPanelClicked(int panelType, int suit, int value) {

		log.debug("card panel clicked: type: " + panelType + " suit: " + suit
				+ " value: " + value);
		log.debug("SkatTable state: " + state);

		SkatGame currGame = tableData.getCurrSkatSeries().getCurrSkatGame();
		int gameState = currGame.getState();
		
		log.debug("SkatGame state: " + gameState);
		log.debug("CardHoldingPanel: " + panelType);
		
		if (gameState == SkatGame.GAMESTATE_SHOWING_SKAT) {

			if (panelType == CardHoldingPanel.PLAYER_PANEL) {

				if (!skatTableOptions.isSchieberRamschJacksInSkat()
						&& value == SkatConstants.JACK ) {

					log.info("Human player has tried to put a Jack into skat although it's not allowed!");
					
					JOptionPane.showMessageDialog(dataModel.getMainWindow(),
							jskatStrings.getString("ramsch_no_jacks"),
							jskatStrings.getString("skat_rules"),
							JOptionPane.WARNING_MESSAGE);
				}
				else {
					currGame.putCardIntoSkat(suit, value);
				}
					

			} else if (panelType == CardHoldingPanel.SKAT_PANEL) {

				currGame.takeCardFromSkat(suit, value);
			
			}

		} else if (currGame.getState() == SkatGame.GAMESTATE_WAIT_FOR_HUMAN_PLAYER_INPUT
				&& panelType == CardHoldingPanel.PLAYER_PANEL) {

			log.debug("Waiting for player input and received click");

			currGame.playTrickCard(suit, value);

		} else if (currGame.getState() == SkatGame.GAMESTATE_TRICK_COMPLETED
				&& panelType == CardHoldingPanel.TRICK_PANEL) {

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

	public void update(Observable o, Object arg) {
		
		if (o instanceof SkatSeries) {
			
			if (arg instanceof Integer) {
				
				if (((Integer) arg).intValue() == SkatSeries.SERIES_FINISHED) {
					
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
