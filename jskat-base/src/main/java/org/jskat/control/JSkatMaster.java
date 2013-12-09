/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.train.NNTrainer;
import org.jskat.control.iss.IssController;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.JSkatViewType;
import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.action.JSkatActionEvent;
import org.jskat.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.player.JSkatPlayer;
import org.jskat.player.JSkatPlayerResolver;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.version.VersionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls everything in JSkat
 */
// FIXME b0n541 2013-07-07: this is a god class, everything is controlled by and
// over this class. It must be split into smaller pieces with respect to SoC and
// SRP
public class JSkatMaster {

	private static Logger log = LoggerFactory.getLogger(JSkatMaster.class);

	private volatile static JSkatMaster instance = null;

	private final JSkatOptions options;
	private final JSkatApplicationData data;
	private JSkatView view;
	private final IssController issControl;

	private final List<NNTrainer> runningNNTrainers;

	/**
	 * Gets the instance of the JSkat master controller
	 * 
	 * @return JSkat master controller
	 */
	public static JSkatMaster instance() {

		if (instance == null) {

			instance = new JSkatMaster();
		}

		return instance;
	}

	/**
	 * Constructor
	 */
	private JSkatMaster() {

		options = JSkatOptions.instance();
		data = JSkatApplicationData.instance();

		issControl = new IssController(this);

		runningNNTrainers = new ArrayList<NNTrainer>();
	}

	/**
	 * Checks the version of JSkat
	 * 
	 * @param latestLocalVersion
	 *            Local version
	 * @param latestRemoteVersion
	 *            Remote version
	 */
	public void checkJSkatVersion(final String latestLocalVersion,
			final String latestRemoteVersion) {
		log.debug("Latest version web: " + latestRemoteVersion); //$NON-NLS-1$
		log.debug("Latest version local: " + latestLocalVersion); //$NON-NLS-1$
		if (VersionChecker.isHigherVersionAvailable(latestLocalVersion,
				latestRemoteVersion)) {
			log.debug("Newer version " + latestRemoteVersion + " is available on the JSkat website."); //$NON-NLS-1$//$NON-NLS-2$
			view.showNewVersionAvailableMessage(latestRemoteVersion);
		}
	}

	/**
	 * Creates a new skat table
	 */
	public void createTable() {

		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created

		String tableName = view.getNewTableName(data.getLocalTablesCreated());

		if (tableName == null) {
			log.debug("Create table was cancelled..."); //$NON-NLS-1$
			return;
		}

		if (data.isFreeTableName(tableName)) {
			createLocalTable(tableName, view.getHumanPlayerForGUI());
		} else {
			view.showDuplicateTableNameMessage(tableName);
			// try again
			createTable();
		}
	}

	public void createLocalTable(String tableName) {
		createLocalTable(tableName, view.getHumanPlayerForGUI());
	}

	private void createLocalTable(final String tableName,
			final AbstractHumanJSkatPlayer humanPlayer) {

		SkatTable table = new SkatTable(data.getTableOptions());
		table.setName(tableName);
		table.setView(view);
		data.addLocalSkatTable(table);
		data.registerHumanPlayerObject(table, humanPlayer);

		view.createSkatTablePanel(table.getName());

		data.setActiveView(JSkatViewType.LOCAL_TABLE, table.getName());
	}

	/**
	 * Removes a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void removeTable(JSkatViewType type, String tableName) {
		if (type == JSkatViewType.LOCAL_TABLE) {
			data.removeLocalSkatTable(tableName);
		} else if (type == JSkatViewType.ISS_TABLE) {
			// FIXME jan 23.02.2013: remove ISS table
		}
	}

	/**
	 * Invites players on ISS to the current table
	 */
	public void invitePlayer() {

		Set<String> issPlayerNames = data.getAvailableISSPlayer();
		issPlayerNames.remove(data.getIssLoginName());

		List<String> player = view.getPlayerForInvitation(issPlayerNames);
		for (String currPlayer : player) {
			getIssController().invitePlayer(data.getActiveView(), currPlayer);
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void startSeries() {

		log.debug("starting new skat series on table: " + data.getActiveView());

		view.showStartSkatSeriesDialog();
	}

	/**
	 * Starts a new series with given parameters
	 * 
	 * @param allPlayer
	 *            Player types
	 * @param playerNames
	 *            Player names
	 * @param numberOfRounds
	 *            Number of rounds to be played
	 * @param unlimited
	 *            TRUE, if unlimited rounds should be played
	 */
	public void startSeries(List<String> allPlayer, List<String> playerNames,
			int numberOfRounds, boolean unlimited, boolean onlyPlayRamsch,
			int sleeps) {

		log.debug(data.getActiveView());

		SkatTable table = data.getLocalSkatTable(data.getActiveView());

		table.removePlayers();

		int playerCount = 0;
		for (String player : allPlayer) {
			JSkatPlayer newPlayer = null;
			if (JSkatPlayerResolver.HUMAN_PLAYER_CLASS.equals(player)) {
				newPlayer = data.getHumanPlayer(table.getName());
			} else {
				newPlayer = createPlayer(player);
			}
			newPlayer.setPlayerName(playerNames.get(playerCount));
			table.placePlayer(newPlayer);
			playerCount++;
		}

		table.startSkatSeries(numberOfRounds, unlimited, onlyPlayRamsch, sleeps);
	}

	public JSkatPlayer createPlayer(String player) {
		JSkatPlayer newPlayer = null;
		try {
			newPlayer = (JSkatPlayer) Class.forName(player).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPlayer;
	}

	/**
	 * Pauses a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void pauseSkatSeries(final String tableName) {

		SkatTable table = data.getLocalSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.pauseSkatSeries();
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void resumeSkatSeries() {

		log.debug(data.getActiveView());

		resumeSkatSeries(data.getActiveView());
	}

	/**
	 * Resumes a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void resumeSkatSeries(final String tableName) {

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public void pauseSkatGame(final String tableName) {

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public void resumeSkatGame(final String tableName) {

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public boolean isSkatGameWaiting(final String tableName) {

		boolean result = false;

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public boolean isSkatSeriesWaiting(final String tableName) {

		boolean result = false;

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public synchronized boolean placePlayer(final String tableName,
			final JSkatPlayer player) {

		boolean result = false;

		SkatTable table = data.getLocalSkatTable(tableName);

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
	public void setView(final JSkatView newView) {

		view = newView;
		issControl.setView(view);
	}

	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {

		options.saveJSkatProperties();
		System.exit(0);
	}

	/**
	 * Shows the about message box
	 */
	public void showAboutMessage() {

		view.showAboutMessage();
	}

	/**
	 * Trains the neural networks
	 */
	public void trainNeuralNetworks() {

		view.showTrainingOverview();

		NNTrainer nullTrainer = new NNTrainer();
		nullTrainer.setGameType(GameType.NULL);
		nullTrainer.start();
		runningNNTrainers.add(nullTrainer);
		NNTrainer grandTrainer = new NNTrainer();
		grandTrainer.setGameType(GameType.GRAND);
		grandTrainer.start();
		runningNNTrainers.add(grandTrainer);
		NNTrainer clubsTrainer = new NNTrainer();
		clubsTrainer.setGameType(GameType.CLUBS);
		clubsTrainer.start();
		runningNNTrainers.add(clubsTrainer);
		NNTrainer spadesTrainer = new NNTrainer();
		spadesTrainer.setGameType(GameType.SPADES);
		spadesTrainer.start();
		runningNNTrainers.add(spadesTrainer);
		NNTrainer heartsTrainer = new NNTrainer();
		heartsTrainer.setGameType(GameType.HEARTS);
		heartsTrainer.start();
		runningNNTrainers.add(heartsTrainer);
		NNTrainer diamondsTrainer = new NNTrainer();
		diamondsTrainer.setGameType(GameType.DIAMONDS);
		diamondsTrainer.start();
		runningNNTrainers.add(diamondsTrainer);
		NNTrainer ramschTrainer = new NNTrainer();
		ramschTrainer.setGameType(GameType.RAMSCH);
		ramschTrainer.start();
		runningNNTrainers.add(ramschTrainer);
	}

	public void stopTrainNeuralNetworks() {
		for (NNTrainer trainer : runningNNTrainers) {
			trainer.stopTraining(true);
		}
		runningNNTrainers.clear();
	}

	/**
	 * Loads the weigths for the neural networks
	 */
	public void loadNeuralNetworks() {
		SkatNetworks.instance().loadNetworks();
	}

	/**
	 * Resets neural networks
	 */
	public void resetNeuralNetworks() {
		SkatNetworks.instance().resetNeuralNetworks();
	}

	/**
	 * Saves the weigths for the neural networks
	 */
	public void saveNeuralNetworks() {
		SkatNetworks.instance().saveNetworks(options.getSavePath());
	}

	/**
	 * Shows the help dialog
	 */
	public void showHelp() {

		view.showHelpDialog();
	}

	/**
	 * Shows the license dialog
	 */
	public void showLicense() {

		view.showLicenseDialog();
	}

	/**
	 * Triggers the human player interface to stop waiting
	 * 
	 * @param event
	 *            Action event
	 */
	public void triggerHuman(final JSkatActionEvent event) {

		log.debug(event.toString());

		String tableName = data.getActiveView();
		String command = event.getActionCommand();
		Object source = event.getSource();

		if (isIssTable(tableName)) {
			handleHumanInputForISSTable(tableName, command, source);
		} else {
			data.getHumanPlayer(tableName).actionPerformed(event);
		}
	}

	private void handleHumanInputForISSTable(final String tableName,
			final String command, final Object source) {

		if (JSkatAction.PASS_BID.toString().equals(command)) {
			// player passed
			issControl.sendPassBidMove(tableName);
		} else if (JSkatAction.MAKE_BID.toString().equals(command)) {
			// player makes bid
			issControl.sendBidMove(tableName);
		} else if (JSkatAction.HOLD_BID.toString().equals(command)) {
			// player hold bid
			issControl.sendHoldBidMove(tableName);
		} else if (JSkatAction.PICK_UP_SKAT.toString().equals(command)) {
			// player wants to pick up the skat
			issControl.sendPickUpSkatMove(tableName);
		} else if (JSkatAction.PLAY_HAND_GAME.toString().equals(command)) {
			// player wants to play a hand game
			// FIXME (jan 02.11.2010) decision is not sent to ISS
		} else if (JSkatAction.DISCARD_CARDS.toString().equals(command)) {

			if (source instanceof CardList) {
				// player discarded cards
				CardList discardSkat = (CardList) source;
				log.debug(discardSkat.toString());

				// FIXME (jan 02.11.2010) Discarded cards are sent with the
				// game announcement to ISS

				// issControl.sendDiscardMove(tableName,
				// discardSkat.get(0), discardSkat.get(1));
			} else {
				log.warn("No discarded cards found for " + command); //$NON-NLS-1$
			}
		} else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {

			if (source instanceof GameAnnouncement) {
				// player did game announcement
				// FIXME (jan 02.11.2010) Discarded cards are sent with the
				// game announcement to ISS
				GameAnnouncement gameAnnouncement = (GameAnnouncement) source;
				issControl
						.sendGameAnnouncementMove(tableName, gameAnnouncement);
			} else {
				log.warn("No game announcement found for " + command); //$NON-NLS-1$
			}
		} else if (JSkatAction.PLAY_CARD.toString().equals(command)
				&& source instanceof Card) {

			Card nextCard = (Card) source;
			issControl.sendCardMove(tableName, nextCard);
		} else {

			log.error("Unknown action event occured: " + command + " from " + source); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private boolean isIssTable(final String tableName) {

		return data.isTableJoined(tableName);
	}

	/**
	 * Takes a card from the skat on the active skat table
	 * 
	 * @param e
	 *            Event
	 */
	public void takeCardFromSkat(final JSkatActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		view.takeCardFromSkat(data.getActiveView(), (Card) e.getSource());
	}

	/**
	 * Put a card into the skat on the active skat table
	 * 
	 * @param e
	 */
	public void putCardIntoSkat(final JSkatActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		view.putCardIntoSkat(data.getActiveView(), (Card) e.getSource());
	}

	/**
	 * Loads a series
	 */
	public void loadSeries() {
		// TODO saving/loading a skat series (here: load)

	}

	/**
	 * Saves a series
	 * 
	 * @param newName
	 *            TRUE, if a new name should be given to the save file
	 */
	public void saveSeries(final boolean newName) {
		// TODO saving/loading a skat series (here: save)

	}

	/**
	 * Gets the controller for playing on the ISS
	 * 
	 * @return ISS controller
	 */
	public IssController getIssController() {

		return issControl;
	}

	/**
	 * Shows the preference dialog
	 */
	public void showPreferences() {

		view.showPreferences();
	}

	public void setActiveTable(String tableName) {
		if (data.isExistingLocalSkatTable(tableName)) {
			setActiveTable(JSkatViewType.LOCAL_TABLE, tableName);
		} else if (data.isTableJoined(tableName)) {
			setActiveTable(JSkatViewType.ISS_TABLE, tableName);
		} else {
			setActiveTable(JSkatViewType.OTHER, tableName);
		}
	}

	/**
	 * Sets the name of the active table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void setActiveTable(JSkatViewType type, String tableName) {

		data.setActiveView(type, tableName);
		if (view != null) {
			// might not be instantiated yet
			view.setActiveView(tableName);
		}

		if (type == JSkatViewType.LOCAL_TABLE) {
			view.setGameState(tableName, data.getLocalSkatTable(tableName)
					.getGameState());
		}
	}

	/**
	 * Sets the login name for ISS
	 * 
	 * @param login
	 *            Login name
	 */
	public void setIssLogin(final String login) {

		data.setIssLoginName(login);
	}

	/**
	 * Sends the table seat change signal to ISS
	 */
	public void sendTableSeatChangeSignal() {

		issControl.sendTableSeatChangeSignal(data.getActiveView());
	}

	/**
	 * Sends the ready to play signal to ISS
	 */
	public void sendReadySignal() {

		issControl.sendReadySignal(data.getActiveView());
	}

	/**
	 * Sends the talk enabled signal to ISS
	 */
	public void sendTalkEnabledSignal() {

		issControl.sendTalkEnabledSignal(data.getActiveView());
	}

	/**
	 * Sends the resign signal to ISS
	 */
	public void sendResignSignal() {

		issControl.sendResignSignal(data.getActiveView());
	}

	/**
	 * Sends the show cards signal to ISS
	 */
	public void sendShowCardsSignal() {

		issControl.sendShowCardsSignal(data.getActiveView());
	}

	/**
	 * Leaves a skat table
	 */
	public void leaveTable() {

		String tableName = data.getActiveView();

		// FIXME distinguish between ISS and local skat table
		issControl.leaveTable(tableName);
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
	public void updateISSPlayer(final String playerName, final String language,
			final long gamesPlayed, final double strength) {

		data.addAvailableISSPlayer(playerName);
		view.updateISSLobbyPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes an ISS player
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayer(final String playerName) {

		data.removeAvailableISSPlayer(playerName);
		view.removeFromISSLobbyPlayerList(playerName);
	}

	/**
	 * Opens the ISS homepage in the default browser
	 */
	public void openIssHomepage() {

		openWebPage(getISSHomepageLink());
	}

	private String getISSHomepageLink() {

		String result = "http://www.skatgame.net/iss/"; //$NON-NLS-1$

		SupportedLanguage lang = JSkatOptions.instance().getLanguage();
		switch (lang) {
		case GERMAN:
			result += "index-de.html"; //$NON-NLS-1$
			break;
		case ENGLISH:
			result += "index.html"; //$NON-NLS-1$
			break;
		}

		return result;
	}

	private void openWebPage(final String link) {
		view.openWebPage(link);
	}

	/**
	 * Opens the ISS registration form in the default browser
	 */
	public void openIssRegisterPage() {

		openWebPage(getIssRegisterLink());
	}

	private String getIssRegisterLink() {

		String result = "http://skatgame.net:7000/"; //$NON-NLS-1$

		SupportedLanguage lang = JSkatOptions.instance().getLanguage();
		switch (lang) {
		case GERMAN:
			result += "de-register"; //$NON-NLS-1$
			break;
		case ENGLISH:
			result += "en-register"; //$NON-NLS-1$
			break;
		}

		return result;
	}

	/**
	 * Adds training results
	 * 
	 * @param gameType
	 *            Game type
	 * @param episodes
	 *            Number of episodes
	 * @param totalWonGames
	 *            Total number of won games
	 * @param avgNetworkErrorDeclarer
	 *            Average difference
	 */
	public void addTrainingResult(final GameType gameType, final long episodes,
			final long totalWonGames, final double avgNetworkErrorDeclarer,
			final double avgNetworkErrorOpponents) {

		view.addTrainingResult(gameType, episodes, totalWonGames,
				avgNetworkErrorDeclarer, avgNetworkErrorOpponents);
	}

	/**
	 * Shows the welcome dialog
	 */
	public void showWelcomeDialog() {
		if (view != null) {
			view.showWelcomeDialog();
		} else {
			log.warn("no view for welcome message found");
		}
	}
}
