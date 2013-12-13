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
package org.jskat.control.iss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.control.JSkatMaster;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.Trick;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.LoginCredentials;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MoveType;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.JSkatView;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls all ISS related actions
 */
public class IssController {

	private static Logger log = LoggerFactory.getLogger(IssController.class);

	private final JSkatMaster jskat;
	private JSkatView view;
	private final JSkatApplicationData data;
	private final JSkatResourceBundle strings;
	private IssConnector issConnector;

	private String login;
	private String password;

	private MessageGenerator issMsg;
	private OutputChannel issOut;

	private final Map<String, SkatGameData> gameData;

	/**
	 * Constructor
	 */
	public IssController(final JSkatMaster newJSkat) {

		jskat = newJSkat;
		data = JSkatApplicationData.instance();
		strings = JSkatResourceBundle.instance();
		gameData = new HashMap<String, SkatGameData>();
	}

	/**
	 * Sets the view (MVC)
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(final JSkatView newView) {

		view = newView;
	}

	/**
	 * Disconnects from ISS
	 */
	public void disconnect() {

		if (issConnector != null && issConnector.isConnected()) {

			log.debug("connection to ISS still open"); //$NON-NLS-1$

			issConnector.closeConnection();
			// FIXME (jan 07.12.2010) use constant instead of string
			view.closeISSPanels();
		}
	}

	/**
	 * Shows the login panel for ISS
	 */
	public void showISSLoginPanel() {

		view.showISSLogin();
	}

	/**
	 * Connects to the ISS
	 * 
	 * @param credentials
	 *            Login credentials
	 * @return TRUE if the connection was established successfully
	 */
	public boolean connectToISS(LoginCredentials credentials) {

		log.debug("connectToISS"); //$NON-NLS-1$

		if (issConnector == null) {

			issConnector = new StreamConnector();
			// issConnector = new WebSocketConnector();
		}

		log.debug("connector created"); //$NON-NLS-1$

		login = credentials.getLoginName();
		password = credentials.getPassword();

		if (issConnector != null && !issConnector.isConnected()) {

			issConnector.setConnectionData(login, password);
			final boolean isConnected = issConnector.establishConnection(this);

			if (isConnected) {
				log.debug("Connection to ISS established: " + issConnector.isConnected()); //$NON-NLS-1$
				issMsg = new MessageGenerator(login);
				issOut = issConnector.getOutputChannel();
				sendToIss(login);
				// sendToIss(issMsg.getLoginAndPasswordMessage(password));
			}
		}

		return issConnector != null && issConnector.isConnected();
	}

	private void sendToIss(final String message) {
		issOut.sendMessage(message);
	}

	/**
	 * Shows ISS lobby when the login was successful
	 * 
	 * @param login
	 *            Login name
	 */
	void showISSLobby(final String login) {
		// show ISS lobby if connection was successfull
		// FIXME (jan 07.12.2010) use constant instead of title
		view.closeTabPanel("ISS login"); //$NON-NLS-1$
		view.showISSLobby();
		jskat.setIssLogin(login);
	}

	/**
	 * Updates ISS player list
	 * 
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Language
	 * @param gamesPlayed
	 *            Games played
	 * @param strength
	 *            Play strength
	 */
	public void updateISSPlayerList(final String playerName,
			final String language, final long gamesPlayed, final double strength) {

		jskat.updateISSPlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * Removes a player from the ISS player list
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayerFromList(final String playerName) {

		jskat.removeISSPlayer(playerName);
	}

	/**
	 * Updates ISS table list
	 * 
	 * @param tableName
	 *            Table name
	 * @param maxPlayers
	 *            Maximum number of players
	 * @param gamesPlayed
	 *            Games played
	 * @param player1
	 *            Player 1 (? for free seat)
	 * @param player2
	 *            Player 2 (? for free seat)
	 * @param player3
	 *            Player 3 (? for free seat)
	 */
	public void updateISSTableList(final String tableName,
			final int maxPlayers, final long gamesPlayed, final String player1,
			final String player2, final String player3) {

		view.updateISSLobbyTableList(tableName, maxPlayers, gamesPlayed,
				player1, player2, player3);
	}

	/**
	 * Removes a table from the ISS table list
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void removeISSTableFromList(final String tableName) {

		view.removeFromISSLobbyTableList(tableName);
	}

	/**
	 * Sends the password to the ISS<br />
	 * only used until protocol version 14
	 */
	@Deprecated
	public void sendPassword() {
		sendToIss(password);
	}

	/**
	 * Sends a chat message to the ISS
	 * 
	 * @param message
	 *            Chat message
	 */
	public void sendChatMessage(final ChatMessage message) {

		sendToIss(issMsg.getChatMessage(message));
	}

	/**
	 * Adds a chat message to a chat
	 * 
	 * @param messageType
	 *            Chat message type
	 * @param params
	 *            Chat message
	 */
	public void addChatMessage(final ChatMessageType messageType,
			final List<String> params) {

		switch (messageType) {
		case LOBBY:
			addLobbyChatMessage(params);
			break;
		case TABLE:
			addTableChatMessage(params);
			break;
		case USER:
			// TODO implement it
			break;
		}
	}

	void addLobbyChatMessage(final List<String> params) {

		log.debug("addLobbyChatMessage"); //$NON-NLS-1$

		final StringBuffer message = new StringBuffer();

		// first the sender of the message
		message.append(params.get(0)).append(": "); //$NON-NLS-1$
		// then the text
		for (int i = 1; i < params.size(); i++) {
			message.append(params.get(i)).append(' ');
		}

		final ChatMessage chatMessage = new ChatMessage("Lobby", //$NON-NLS-1$
				message.toString());

		view.appendISSChatMessage(ChatMessageType.LOBBY, chatMessage);
	}

	void addTableChatMessage(final List<String> params) {

		log.debug("addTableChatMessage");

		// first the table for the message
		final String tableName = params.get(0);

		final StringBuffer message = new StringBuffer();
		// second the sender of the message
		message.append(params.get(1)).append(": "); //$NON-NLS-1$
		// then the text
		for (int i = 2; i < params.size(); i++) {
			message.append(params.get(i)).append(' ');
		}

		final ChatMessage chatMessage = new ChatMessage(tableName,
				message.toString());

		view.appendISSChatMessage(ChatMessageType.TABLE, chatMessage);
	}

	/**
	 * Requests the creation of a new table on the ISS
	 */
	public void requestTableCreation() {

		sendToIss(issMsg.getTableCreationMessage());
	}

	/**
	 * Creates a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param creator
	 *            Table creator
	 * @param maxPlayers
	 *            Maximum number of players
	 */
	public void createTable(final String tableName, final String creator,
			final int maxPlayers) {

		view.createISSTable(tableName, creator);
		jskat.setActiveTable(JSkatViewType.ISS_TABLE, tableName);
	}

	/**
	 * Destroys a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void destroyTable(final String tableName) {

		view.closeTabPanel(tableName);
		data.removeJoinedIssSkatTable(tableName);
		jskat.removeTable(JSkatViewType.ISS_TABLE, tableName);
	}

	/**
	 * Joins a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void joinTable(final String tableName) {
		sendToIss(issMsg.getJoinTableMessage(tableName));
	}

	/**
	 * Observes a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void observeTable(final String tableName) {
		sendToIss(issMsg.getObserveTableMessage(tableName));
	}

	/**
	 * Leaves a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void leaveTable(final String tableName) {
		sendToIss(issMsg.getLeaveTableMessage(tableName));
	}

	/**
	 * Updates a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New table status
	 */
	public void updateISSTableState(final String tableName,
			final TablePanelStatus status) {
		view.updateISSTable(tableName, status);
	}

	/**
	 * Updates a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New game status
	 */
	public void updateISSGame(final String tableName,
			final GameStartInformation status) {

		view.updateISSTable(tableName, data.getIssLoginName(), status);

		gameData.put(tableName, createSkatGameData(status));
	}

	private SkatGameData createSkatGameData(final GameStartInformation status) {

		final SkatGameData result = new SkatGameData();

		result.setGameState(GameState.GAME_START);
		for (final Player player : Player.values()) {
			result.setPlayerName(player, status.getPlayerName(player));
		}

		return result;
	}

	/**
	 * Starts a game on a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void startGame(final String tableName) {

		view.startGame(tableName);
	}

	/**
	 * Updates a move on a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param moveInformation
	 *            Move information
	 */
	public void updateMove(final String tableName,
			final MoveInformation moveInformation) {

		final SkatGameData currGame = gameData.get(tableName);
		updateGameData(currGame, moveInformation);

		view.updateISSMove(tableName, currGame, moveInformation);

		// TODO (jan 19.11.2010) extract this into separate methods
		if (MoveType.BID.equals(moveInformation.getType())
				|| MoveType.PASS.equals(moveInformation.getType())) {

			if (isBiddingFinished(currGame)) {
				view.setDeclarer(tableName, currGame.getDeclarer());
				view.setGameState(tableName, GameState.PICKING_UP_SKAT);
			}

		} else if (MoveType.CARD_PLAY.equals(moveInformation.getType())) {

			// handle trick playing
			final Trick trick = currGame.getCurrentTrick();

			view.setTrickNumber(tableName, trick.getTrickNumberInGame() + 1);

			if (trick.getThirdCard() != null) {

				final Player trickWinner = SkatRuleFactory.getSkatRules(
						currGame.getGameType()).calculateTrickWinner(
						currGame.getGameType(), trick);
				trick.setTrickWinner(trickWinner);
				currGame.addTrick(new Trick(currGame.getTricks().size(), trick
						.getTrickWinner()));

				view.setActivePlayer(tableName, currGame.getCurrentTrick()
						.getForeHand());

			} else if (trick.getSecondCard() != null) {

				view.setActivePlayer(tableName, trick.getForeHand()
						.getRightNeighbor());

			} else if (trick.getFirstCard() != null) {

				view.setActivePlayer(tableName, trick.getForeHand()
						.getLeftNeighbor());
			}
		}
	}

	private boolean isBiddingFinished(final SkatGameData currGame) {

		boolean result = false;

		if (currGame.getNumberOfPasses() == 2) {
			for (final Player currPlayer : Player.values()) {
				if (!currGame.isPlayerPass(currPlayer)) {
					if (currGame.getMaxPlayerBid(currPlayer) > 0) {
						result = true;
						currGame.setDeclarer(currPlayer);
					}
				}
			}
		}

		return result;
	}

	private void updateGameData(final SkatGameData currGame,
			final MoveInformation moveInformation) {

		final Player movePlayer = moveInformation.getPlayer();

		switch (moveInformation.getType()) {
		case DEAL:
			currGame.setGameState(GameState.DEALING);
			break;
		case BID:
			currGame.setGameState(GameState.BIDDING);
			currGame.addPlayerBid(movePlayer, moveInformation.getBidValue());
			break;
		case HOLD_BID:
			currGame.setGameState(GameState.BIDDING);
			currGame.addPlayerBid(movePlayer, currGame.getMaxBidValue());
			break;
		case PASS:
			currGame.setGameState(GameState.BIDDING);
			currGame.setPlayerPass(movePlayer, true);
			break;
		case SKAT_REQUEST:
			currGame.setGameState(GameState.DISCARDING);
			break;
		case PICK_UP_SKAT:
			currGame.setGameState(GameState.DISCARDING);
			break;
		case GAME_ANNOUNCEMENT:
			currGame.setGameState(GameState.DECLARING);
			currGame.setAnnouncement(moveInformation.getGameAnnouncement());
			currGame.addTrick(new Trick(0, Player.FOREHAND));
			break;
		case CARD_PLAY:
			currGame.setGameState(GameState.TRICK_PLAYING);
			currGame.setTrickCard(movePlayer, moveInformation.getCard());
			break;
		case RESIGN:
		case TIME_OUT:
			currGame.setGameState(GameState.PRELIMINARY_GAME_END);
			break;
		}
	}

	/**
	 * Handles end of a game
	 * 
	 * @param tableName
	 *            Table name
	 * @param newGameData
	 *            Game data
	 */
	public void endGame(final String tableName, final SkatGameData newGameData) {

		view.setGameState(tableName, GameState.GAME_OVER);
		view.addGameResult(tableName, newGameData.getGameSummary());
		view.showCards(tableName, newGameData.getCardsAfterDiscard());
		gameData.put(tableName, newGameData);
	}

	/**
	 * Shows a message from ISS.
	 * 
	 * @param message
	 *            Message
	 */
	public void showMessage(final String message) {
		view.showMessage(strings.getString("iss_message"), message); //$NON-NLS-1$
	}

	/**
	 * Shows an error message from ISS.
	 * 
	 * @param message
	 *            Message
	 */
	public void showErrorMessage(final String message) {
		view.showErrorMessage(strings.getString("iss_message"), message); //$NON-NLS-1$
	}

	/**
	 * Invites a player on ISS to play at a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param invitee
	 *            Invited player
	 */
	public void invitePlayer(final String tableName, final String invitee) {
		sendToIss(issMsg.getInvitePlayerMessage(tableName, invitee));
	}

	/**
	 * Sends ready to play signal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendReadySignal(final String tableName) {
		sendToIss(issMsg.getReadyMessage(tableName));
	}

	/**
	 * Send talk enabled signal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendTalkEnabledSignal(final String tableName) {
		sendToIss(issMsg.getTalkEnabledMessage(tableName));
	}

	/**
	 * Sends a resign signal to ISS
	 * 
	 * @param tableName
	 */
	public void sendResignSignal(final String tableName) {
		sendToIss(issMsg.getResignMessage(tableName));
	}

	/**
	 * Sends a show cards signal to ISS
	 * 
	 * @param tableName
	 */
	public void sendShowCardsSignal(final String tableName) {
		sendToIss(issMsg.getShowCardsMessage(tableName));
	}

	/**
	 * Send table seat change singal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendTableSeatChangeSignal(final String tableName) {
		sendToIss(issMsg.getTableSeatChangeMessage(tableName));
	}

	/**
	 * Send pass bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendPassBidMove(final String tableName) {
		sendToIss(issMsg.getPassMoveMessage(tableName));
	}

	/**
	 * Send hold bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendHoldBidMove(final String tableName) {
		sendToIss(issMsg.getHoldBidMoveMessage(tableName));
	}

	/**
	 * Send bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendBidMove(final String tableName) {
		sendToIss(issMsg.getBidMoveMessage(tableName, SkatConstants
				.getNextBidValue(gameData.get(tableName).getMaxBidValue())));
	}

	/**
	 * Send pick up skat move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendPickUpSkatMove(final String tableName) {
		sendToIss(issMsg.getPickUpSkatMoveMessage(tableName));
	}

	/**
	 * Send game announcement to ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameAnnouncement
	 *            Game announcement
	 */
	public void sendGameAnnouncementMove(final String tableName,
			final GameAnnouncement gameAnnouncement) {
		sendToIss(issMsg.getGameAnnouncementMoveMessage(tableName,
				gameAnnouncement));
	}

	/**
	 * Send card move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param nextCard
	 *            Card
	 */
	public void sendCardMove(final String tableName, final Card nextCard) {
		sendToIss(issMsg.getCardMoveMessage(tableName, nextCard));
	}

	/**
	 * Handle an invitation from another player
	 * 
	 * @param invitor
	 *            Invitor
	 * @param tableName
	 *            Table name
	 * @param invitationTicket
	 *            Invitation ticket
	 */
	public void handleInvitation(final String invitor, final String tableName,
			final String invitationTicket) {
		if (view.showISSTableInvitation(invitor, tableName)) {
			sendToIss(issMsg.getInvitationAcceptedMessage(tableName,
					invitationTicket));
		}
	}

	/**
	 * Updates a chat message on an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param message
	 *            Chat message
	 */
	public void updateISSTableChatMessage(final String tableName,
			final ChatMessage message) {
		// FIXME (jan 30.01.2011) tableName not needed here?
		view.appendISSChatMessage(ChatMessageType.TABLE, message);
	}

	/**
	 * Closes all ISS related tab panels
	 */
	public void closeIssPanels() {
		view.closeISSPanels();
	}

	/**
	 * Checks whether a table is joined or not
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE iff the table is joined
	 */
	public boolean isTableJoined(final String tableName) {

		return data.isTableJoined(tableName);
	}
}
