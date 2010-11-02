/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.IJSkatPlayer;
import de.jskat.ai.PlayerType;
import de.jskat.ai.nn.AIPlayerNN;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.train.NNTrainer;
import de.jskat.control.iss.ISSController;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.JSkatApplicationData;
import de.jskat.data.JSkatOptions;
import de.jskat.gui.IJSkatView;
import de.jskat.gui.action.JSkatAction;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;

/**
 * Controls everything in JSkat
 */
public class JSkatMaster {

	private static Log log = LogFactory.getLog(JSkatMaster.class);

	private JSkatOptions options;
	private JSkatApplicationData data;
	private IJSkatView view;
	private ISSController issControl;

	/**
	 * Constructor
	 * 
	 * @param jskatOptions
	 *            JSkat options
	 */
	public JSkatMaster(JSkatOptions jskatOptions) {

		this.data = new JSkatApplicationData(jskatOptions);
		this.options = jskatOptions;

		this.issControl = new ISSController(this, this.data);
	}

	/**
	 * Creates a new skat table
	 */
	public void createTable() {

		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created

		String tableName = this.view.getNewTableName();
		if (tableName == null) {
			log.debug("Create table was cancelled..."); //$NON-NLS-1$
			return;
		}

		SkatTable table = new SkatTable(this.data.getTableOptions());
		table.setName(tableName);
		this.data.addSkatTable(table);

		this.view.createSkatTablePanel(table.getName());
		this.data.setActiveTable(table.getName());

		table.setView(this.view);
	}

	/**
	 * Invites players on ISS to the current table
	 */
	public void invitePlayer() {

		List<String> player = this.view.getPlayerForInvitation(this.data
				.getAvailableISSPlayer());
		for (String currPlayer : player) {
			getISSController().invitePlayer(this.data.getActiveTable(),
					currPlayer);
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void startSeries() {

		log.debug(this.data.getActiveTable());

		this.view.showStartSkatSeriesDialog();
	}

	/**
	 * Starts a new series with given parameters
	 * 
	 * @param allPlayer
	 *            Player names
	 * @param numberOfRounds
	 *            Number of rounds to be played
	 * @param unlimited
	 *            TRUE, if unlimited rounds should be played
	 */
	public void startSeries(ArrayList<PlayerType> allPlayer,
			int numberOfRounds, boolean unlimited) {

		log.debug(this.data.getActiveTable());

		SkatTable table = this.data.getSkatTable(this.data.getActiveTable());

		table.removePlayers();

		for (PlayerType player : allPlayer) {

			table.placePlayer(getPlayerInstance(table.getName(), player));
		}

		table.startSkatSeries(numberOfRounds);
	}

	private IJSkatPlayer getPlayerInstance(String tableName,
			PlayerType playerType) {

		IJSkatPlayer player = null;

		switch (playerType) {
		case RANDOM:
			player = getPlayerInstanceFromName("de.jskat.ai.rnd.AIPlayerRND"); //$NON-NLS-1$
			break;
		case NEURAL_NETWORK:
			player = getPlayerInstanceFromName("de.jskat.ai.nn.AIPlayerNN"); //$NON-NLS-1$
			((AIPlayerNN) player).setIsLearning(true);
			break;
		case ALGORITHMIC:
			player = getPlayerInstanceFromName("de.jskat.ai.mjl.AIPlayerMJL"); //$NON-NLS-1$
			break;
		case HUMAN:
			player = this.data.getHumanPlayer(tableName);
			break;
		}

		return player;
	}

	private IJSkatPlayer getPlayerInstanceFromName(String className) {

		IJSkatPlayer player = null;

		try {
			player = (IJSkatPlayer) Class.forName(className).newInstance();
		} catch (ClassNotFoundException ex) {
			// handle exception case
			player = getPlayerInstanceFromName("de.jskat.ai.rnd.AIPlayerRND"); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return player;
	}

	/**
	 * Pauses a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void pauseSkatSeries(String tableName) {

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.pauseSkatSeries();
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void resumeSkatSeries() {

		log.debug(this.data.getActiveTable());

		resumeSkatSeries(this.data.getActiveTable());
	}

	/**
	 * Resumes a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void resumeSkatSeries(String tableName) {

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.resumeSkatSeries();
		}
	}

	/**
	 * Pauses a skat game at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void pauseSkatGame(String tableName) {

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.pauseSkatGame();
		}
	}

	/**
	 * Resumes a skat game at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void resumeSkatGame(String tableName) {

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.resumeSkatGame();
		}
	}

	/**
	 * Checks whether a skat game is waiting
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE if the game is waiting
	 */
	public boolean isSkatGameWaiting(String tableName) {

		boolean result = false;

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			result = table.isSkatGameWaiting();
		}

		return result;
	}

	/**
	 * Checks whether a skat series is waiting
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE if the series is waiting
	 */
	public boolean isSkatSeriesWaiting(String tableName) {

		boolean result = false;

		SkatTable table = this.data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			result = table.isSkatSeriesWaiting();
		}

		return result;
	}

	/**
	 * Places a skat player on a table
	 * 
	 * @param tableName
	 *            Table ID
	 * @param player
	 *            Skat player
	 * @return TRUE if the placing was successful
	 */
	public synchronized boolean placePlayer(String tableName,
			IJSkatPlayer player) {

		boolean result = false;

		SkatTable table = this.data.getSkatTable(tableName);

		if (!table.isSeriesRunning()) {

			if (table.getPlayerCount() < table.getMaxPlayerCount()) {

				result = table.placePlayer(player);
			}
		}

		return result;
	}

	/**
	 * Sets the view (for MVC)
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(IJSkatView newView) {

		this.view = newView;
		this.issControl.setView(this.view);
	}

	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {

		if (this.view.showExitDialog() == JOptionPane.YES_OPTION) {

			this.options.saveJSkatProperties();

			System.exit(0);
		}
	}

	/**
	 * Shows the about message box
	 */
	public void showAboutMessage() {

		this.view.showAboutMessage();
	}

	/**
	 * Trains the neural networks
	 */
	public void trainNeuralNetworks() {

		NNTrainer nullTrainer = new NNTrainer();
		nullTrainer.setGameType(GameType.NULL);
		nullTrainer.start();
		NNTrainer grandTrainer = new NNTrainer();
		grandTrainer.setGameType(GameType.GRAND);
		grandTrainer.start();
		NNTrainer clubsTrainer = new NNTrainer();
		clubsTrainer.setGameType(GameType.CLUBS);
		clubsTrainer.start();
		NNTrainer spadesTrainer = new NNTrainer();
		spadesTrainer.setGameType(GameType.SPADES);
		spadesTrainer.start();
		NNTrainer heartsTrainer = new NNTrainer();
		heartsTrainer.setGameType(GameType.HEARTS);
		heartsTrainer.start();
		NNTrainer diamondsTrainer = new NNTrainer();
		diamondsTrainer.setGameType(GameType.DIAMONDS);
		diamondsTrainer.start();
	}

	/**
	 * Loads the weigths for the neural networks
	 */
	public void loadNeuralNetworks() {

		SkatNetworks
				.loadNetworks(System
						.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Saves the weigths for the neural networks
	 */
	public void saveNeuralNetworks() {

		SkatNetworks
				.saveNetworks(System
						.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Shows the help dialog
	 */
	public void showHelp() {

		this.view.showHelpDialog();
	}

	/**
	 * Shows the license dialog
	 */
	public void showLicense() {

		this.view.showLicenseDialog();
	}

	/**
	 * Triggers the human player interface to stop waiting
	 * 
	 * @param event
	 *            Action event
	 */
	public void triggerHuman(ActionEvent event) {

		log.debug(event);

		String tableName = data.getActiveTable();
		SkatTable table = data.getSkatTable(tableName);
		String command = event.getActionCommand();
		Object source = event.getSource();

		if (isIssTable(tableName)) {
			if (JSkatAction.PASS_BID.toString().equals(command)) {
				// player passed
				this.issControl.sendPassBidMove(tableName);
			} else if (JSkatAction.HOLD_BID.toString().equals(command)) {
				// player hold bid
				this.issControl.sendHoldBidMove(tableName);
			} else if (JSkatAction.LOOK_INTO_SKAT.toString().equals(command)) {
				// player wants to look into the skat
				this.issControl.sendLookIntoSkatMove(tableName);
			} else if (JSkatAction.PLAY_HAND_GAME.toString().equals(command)) {
				// player wants to play a hand game
				// FIXME (jan 02.11.2010) decision is not sent to ISS
			} else if (JSkatAction.DISCARD_CARDS.toString().equals(command)) {

				if (source instanceof CardList) {
					// player discarded cards
					CardList discardSkat = (CardList) source;

					// FIXME (jan 02.11.2010) Discarded cards are sent with the
					// game announcement to ISS

					// this.issControl.sendDiscardMove(tableName,
					// discardSkat.get(0), discardSkat.get(1));
				} else {

					log.error("Wrong source for " + command); //$NON-NLS-1$
				}
			} else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {

				if (source instanceof JButton) {
					log.debug("ONLY JBUTTON"); //$NON-NLS-1$
				} else {
					// player did game announcement
					// FIXME (jan 02.11.2010) Discarded cards are sent with the
					// game announcement to ISS
					GameAnnouncement gameAnnouncement = (GameAnnouncement) source;
					this.issControl.sendGameAnnouncementMove(tableName,
							gameAnnouncement);
				}
			} else if (JSkatAction.PLAY_CARD.toString().equals(command)
					&& source instanceof Card) {

				Card nextCard = (Card) source;
				this.issControl.sendCardMove(tableName, nextCard);
			} else {

				log.error("Unknown action event occured: " + command + " from " + source); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} else {
			this.data.getHumanPlayer(this.data.getActiveTable())
					.actionPerformed(event);
		}
	}

	private boolean isIssTable(String tableName) {

		// FIXME (jan 02.11.2010) this is not sufficient
		return tableName.startsWith("."); //$NON-NLS-1$
	}

	/**
	 * Takes a card from the skat on the active skat table
	 * 
	 * @param e
	 *            Event
	 */
	public void takeCardFromSkat(ActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		this.view.takeCardFromSkat(this.data.getActiveTable(),
				(Card) e.getSource());
	}

	/**
	 * Put a card into the skat on the active skat table
	 * 
	 * @param e
	 */
	public void putCardIntoSkat(ActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		this.view.putCardIntoSkat(this.data.getActiveTable(),
				(Card) e.getSource());
	}

	/**
	 * Loads a series
	 */
	public void loadSeries() {
		// TODO Auto-generated method stub

	}

	/**
	 * Saves a series
	 * 
	 * @param newName
	 *            TRUE, if a new name should be given to the save file
	 */
	public void saveSeries(boolean newName) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the controller for playing on the ISS
	 * 
	 * @return ISS controller
	 */
	public ISSController getISSController() {

		return this.issControl;
	}

	/**
	 * Shows the preference dialog
	 */
	public void showPreferences() {

		this.view.showPreferences();
	}

	/**
	 * Sets the name of the active table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void setActiveTable(String tableName) {

		this.data.setActiveTable(tableName);
	}

	/**
	 * Sets the login name for ISS
	 * 
	 * @param login
	 *            Login name
	 */
	public void setIssLogin(String login) {

		this.data.setIssLoginName(login);
	}

	/**
	 * Sends the table seat change signal to ISS
	 */
	public void sendTableSeatChangeSignal() {

		this.issControl.sendTableSeatChangeSignal(this.data.getActiveTable());
	}

	/**
	 * Sends the ready to play signal to ISS
	 */
	public void sendReadySignal() {

		this.issControl.sendReadySignal(this.data.getActiveTable());
	}

	/**
	 * Sends the talk enabled signal to ISS
	 */
	public void sendTalkEnabledSignal() {

		this.issControl.sendTalkEnabledSignal(this.data.getActiveTable());
	}

	/**
	 * Leaves a skat table
	 */
	public void leaveTable() {

		String tableName = this.data.getActiveTable();

		// FIXME distinguish between ISS and local skat table
		this.issControl.leaveTable(tableName);
	}

	/**
	 * Updates ISS player information
	 * 
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Language
	 * @param gamesPlayed
	 *            Games played
	 * @param strength
	 *            Playing strength
	 */
	public void updateISSPlayer(String playerName, String language,
			long gamesPlayed, double strength) {

		this.data.addAvailableISSPlayer(playerName);
		this.view.updateISSLobbyPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes an ISS player
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayer(String playerName) {

		this.data.removeAvailableISSPlayer(playerName);
		this.view.removeFromISSLobbyPlayerList(playerName);
	}

}
