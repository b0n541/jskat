/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control;

import java.util.List;
import java.util.Set;

import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.table.DuplicateTableNameInputEvent;
import org.jskat.control.event.table.EmptyTableNameInputEvent;
import org.jskat.control.event.table.TableRemovedEvent;
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
import org.jskat.util.version.VersionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

/**
 * Controls everything in JSkat
 */
// FIXME b0n541 2013-07-07: this is a god class, everything is controlled by and
// over this class. It must be split into smaller pieces with respect to SoC and
// SRP
public class JSkatMaster {

	private static Logger log = LoggerFactory.getLogger(JSkatMaster.class);

	public final static JSkatMaster INSTANCE = new JSkatMaster();

	private final JSkatOptions options;
	private final JSkatApplicationData data;
	private JSkatView view;
	private final IssController issControl;

	/**
	 * Constructor
	 */
	private JSkatMaster() {

		options = JSkatOptions.instance();
		data = JSkatApplicationData.INSTANCE;

		issControl = new IssController(this);

		JSkatEventBus.INSTANCE.register(this);
	}

	/**
	 * Checks the version of JSkat
	 *
	 * @param latestLocalVersion
	 *            Local version
	 * @param latestRemoteVersion
	 *            Remote version
	 */
	public void checkJSkatVersion(final String latestLocalVersion, final String latestRemoteVersion) {
		log.debug("Latest version web: " + latestRemoteVersion); //$NON-NLS-1$
		log.debug("Latest version local: " + latestLocalVersion); //$NON-NLS-1$
		if (VersionChecker.isHigherVersionAvailable(latestLocalVersion, latestRemoteVersion)) {
			log.debug("Newer version " + latestRemoteVersion + " is available on the JSkat website."); //$NON-NLS-1$//$NON-NLS-2$

			JSkatEventBus.INSTANCE.post(new NewJSkatVersionAvailableEvent(latestRemoteVersion));
		}
	}

	/**
	 * Creates a new skat table
	 */
	public void createTable() {

		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created

		final String tableName = view.getNewTableName(data.getLocalTablesCreated());

		if (tableName == null) {
			log.debug("Create table was cancelled..."); //$NON-NLS-1$
			return;
		}

		if (tableName.isEmpty()) {
			showEmptyInputNameMessage();
			// try again
			createTable();
			return;
		}

		if (data.isFreeTableName(tableName)) {
			createLocalTable(tableName, view.getHumanPlayerForGUI());
		} else {
			JSkatEventBus.INSTANCE.post(new DuplicateTableNameInputEvent(tableName));
			// try again
			createTable();
		}
	}

	private void createLocalTable(final String tableName, final AbstractHumanJSkatPlayer humanPlayer) {

		JSkatEventBus.INSTANCE.post(new CreateTableCommand(JSkatViewType.LOCAL_TABLE, tableName));
	}

	/**
	 * Gets the view implementation.
	 *
	 * @return The JSkat view implementation
	 *
	 * @deprecated Use only until event processing is completely implemented.
	 */
	@Deprecated
	public JSkatView getView() {
		return view;
	}

	/**
	 * Removes a table
	 *
	 * @param event
	 *            Table removed event
	 */
	@Subscribe
	public void removeTableDataOn(final TableRemovedEvent event) {
		if (JSkatViewType.LOCAL_TABLE.equals(event.tableType)) {
			data.removeLocalSkatTable(event.tableName);
		} else if (JSkatViewType.ISS_TABLE.equals(event.tableType)) {
			data.removeJoinedIssSkatTable(event.tableName);
		}
	}

	/**
	 * Invites players on ISS to the current table
	 */
	public void invitePlayer() {

		final Set<String> issPlayerNames = data.getAvailableISSPlayer();
		issPlayerNames.remove(data.getIssLoginName());

		final List<String> player = view.getPlayerForInvitation(issPlayerNames);
		for (final String currPlayer : player) {
			getIssController().invitePlayer(data.getActiveTable(), currPlayer);
		}
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
	 * @param onlyPlayRamsch
	 *            TRUE, if only Ramsch games should be played
	 * @param sleeps
	 *            Milliseconds to wait after a games ends during a series
	 */
	public void startSeries(final List<String> allPlayer, final List<String> playerNames, final int numberOfRounds,
			final boolean unlimited,
			final boolean onlyPlayRamsch, final int sleeps) {

		log.debug(data.getActiveTable());

		final SkatTable table = data.getLocalSkatTable(data.getActiveTable());

		table.removePlayers();

		int playerCount = 0;
		for (final String player : allPlayer) {
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

	public JSkatPlayer createPlayer(final String player) {
		JSkatPlayer newPlayer = null;
		try {
			newPlayer = (JSkatPlayer) Class.forName(player).newInstance();
		} catch (final InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPlayer;
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
	public synchronized boolean placePlayer(final String tableName, final JSkatPlayer player) {

		boolean result = false;

		final SkatTable table = data.getLocalSkatTable(tableName);

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
	 * Shows the error message of wrong (null) name input
	 */
	public void showEmptyInputNameMessage() {

		JSkatEventBus.INSTANCE.post(new EmptyTableNameInputEvent());
	}

	/**
	 * Triggers the human player interface to stop waiting
	 *
	 * @param event
	 *            Action event
	 */
	public void triggerHuman(final JSkatActionEvent event) {

		log.debug(event.toString());

		final String tableName = data.getActiveTable();
		final String command = event.getActionCommand();
		final Object source = event.getSource();

		if (isIssTable(tableName)) {
			handleHumanInputForISSTable(tableName, command, source);
		} else {
			data.getHumanPlayer(tableName).actionPerformed(event);
		}
	}

	private void handleHumanInputForISSTable(final String tableName, final String command, final Object source) {

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
				final CardList discardSkat = (CardList) source;
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
				final GameAnnouncement gameAnnouncement = (GameAnnouncement) source;
				issControl.sendGameAnnouncementMove(tableName, gameAnnouncement);
			} else {
				log.warn("No game announcement found for " + command); //$NON-NLS-1$
			}
		} else if (JSkatAction.PLAY_CARD.toString().equals(command) && source instanceof Card) {

			final Card nextCard = (Card) source;
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

		view.takeCardFromSkat(data.getActiveTable(), (Card) e.getSource());
	}

	/**
	 * Put a card into the skat on the active skat table
	 *
	 * @param event
	 *            Event
	 */
	public void putCardIntoSkat(final JSkatActionEvent event) {

		if (!(event.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		view.putCardIntoSkat(data.getActiveTable(), (Card) event.getSource());
	}

	/**
	 * Gets the controller for playing on the ISS.
	 *
	 * @return ISS controller
	 */
	public IssController getIssController() {

		return issControl;
	}

	/**
	 * Sets the active table.
	 *
	 * @param tableName
	 *            Table name
	 */
	public void setActiveTable(final String tableName) {
		if (data.isExistingLocalSkatTable(tableName)) {
			setActiveTable(JSkatViewType.LOCAL_TABLE, tableName);
		} else if (data.isTableJoined(tableName)) {
			setActiveTable(JSkatViewType.ISS_TABLE, tableName);
		} else {
			setActiveTable(JSkatViewType.OTHER, tableName);
		}
	}

	/**
	 * Sets the name of the active table.
	 *
	 * @param type
	 *            View type
	 * @param tableName
	 *            Table name
	 */
	public void setActiveTable(final JSkatViewType type, final String tableName) {

		data.setActiveTable(type, tableName);
		if (view != null) {
			// might not be instantiated yet
			view.setActiveView(tableName);
		}

		if (type == JSkatViewType.LOCAL_TABLE) {
			view.setGameState(tableName, data.getLocalSkatTable(tableName).getGameState());
		}
	}

	/**
	 * Sets the login name for ISS
	 *
	 * @param event
	 *            ISS connected event
	 */
	@Subscribe
	public void setLoginNameOn(final IssConnectedEvent event) {

		data.setIssLoginName(event.login);
	}

	/**
	 * Leaves a skat table
	 */
	public void leaveTable() {

		final String tableName = data.getActiveTable();

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
	public void updateISSPlayer(final String playerName, final String language, final long gamesPlayed,
			final double strength) {

		data.addAvailableISSPlayer(playerName);
		view.updateISSLobbyPlayerList(playerName, language, gamesPlayed, strength);
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
	 * Opens the ISS homepage viewin the default browser
	 */
	public void openIssHomepage() {

		openWebPage(getISSHomepageLink());
	}

	private String getISSHomepageLink() {

		String result = "http://www.skatgame.net/iss/"; //$NON-NLS-1$

		final SupportedLanguage lang = JSkatOptions.instance().getLanguage();
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

		final SupportedLanguage lang = JSkatOptions.instance().getLanguage();
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
}
