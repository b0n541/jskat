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
package org.jskat.control.iss;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.control.JSkatMaster;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatApplicationData;
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
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Controls all ISS related actions
 */
public class IssController {

	private static Log log = LogFactory.getLog(IssController.class);

	private final JSkatMaster jskat;
	private JSkatView view;
	private final JSkatApplicationData data;
	private final JSkatResourceBundle strings;
	private Connector issConnect;

	private final Map<String, SkatGameData> gameData;

	/**
	 * Constructor
	 */
	public IssController(JSkatMaster newJSkat) {

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
	public void setView(JSkatView newView) {

		view = newView;
	}

	/**
	 * Disconnects from ISS
	 */
	public void disconnect() {

		if (issConnect != null && issConnect.isConnected()) {

			log.debug("connection to ISS still open"); //$NON-NLS-1$

			issConnect.closeConnection();
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
	 * @param e
	 *            Login credentials
	 * @return TRUE if the connection was established successfully
	 */
	public boolean connectToISS(ActionEvent e) {

		log.debug("connectToISS"); //$NON-NLS-1$

		if (issConnect == null) {

			issConnect = new Connector(this);
		}

		log.debug("connector created"); //$NON-NLS-1$

		Object source = e.getSource();
		String command = e.getActionCommand();
		String login = null;
		String password = null;

		if (JSkatAction.CONNECT_TO_ISS.toString().equals(command)) {
			if (source instanceof LoginCredentials) {

				LoginCredentials loginCredentials = (LoginCredentials) source;
				login = loginCredentials.getLoginName();
				password = loginCredentials.getPassword();

				if (!issConnect.isConnected()) {

					issConnect.setConnectionData(login, password);
					issConnect.establishConnection();
				}
			} else {

				log.error("Wrong source for " + command); //$NON-NLS-1$
			}
		}

		log.debug("Connection to ISS established: " + issConnect.isConnected()); //$NON-NLS-1$
		return issConnect.isConnected();
	}

	/**
	 * Shows ISS lobby when the login was successful
	 * 
	 * @param login
	 *            Login name
	 */
	void showISSLobby(String login) {
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
	public void updateISSPlayerList(String playerName, String language, long gamesPlayed, double strength) {

		jskat.updateISSPlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * Removes a player from the ISS player list
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayerFromList(String playerName) {

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
	public void updateISSTableList(String tableName, int maxPlayers, long gamesPlayed, String player1, String player2,
			String player3) {

		view.updateISSLobbyTableList(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
	}

	/**
	 * Removes a table from the ISS table list
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void removeISSTableFromList(String tableName) {

		view.removeFromISSLobbyTableList(tableName);
	}

	/**
	 * Sends a chat message to the ISS
	 * 
	 * @param message
	 *            Chat message
	 */
	public void sendChatMessage(ChatMessage message) {

		issConnect.send(message);
	}

	/**
	 * Adds a chat message to a chat
	 * 
	 * @param messageType
	 *            Chat message type
	 * @param params
	 *            Chat message
	 */
	public void addChatMessage(ChatMessageType messageType, List<String> params) {

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

	void addLobbyChatMessage(List<String> params) {

		log.debug("addLobbyChatMessage"); //$NON-NLS-1$

		StringBuffer message = new StringBuffer();

		// first the sender of the message
		message.append(params.get(0)).append(": "); //$NON-NLS-1$
		// then the text
		for (int i = 1; i < params.size(); i++) {
			message.append(params.get(i)).append(' ');
		}

		ChatMessage chatMessage = new ChatMessage("Lobby", //$NON-NLS-1$
				message.toString());

		view.appendISSChatMessage(ChatMessageType.LOBBY, chatMessage);
	}

	void addTableChatMessage(List<String> params) {

		log.debug("addTableChatMessage");

		// first the table for the message
		String tableName = params.get(0);

		StringBuffer message = new StringBuffer();
		// second the sender of the message
		message.append(params.get(1)).append(": "); //$NON-NLS-1$
		// then the text
		for (int i = 2; i < params.size(); i++) {
			message.append(params.get(i)).append(' ');
		}

		ChatMessage chatMessage = new ChatMessage(tableName, message.toString());

		view.appendISSChatMessage(ChatMessageType.TABLE, chatMessage);
	}

	/**
	 * Requests the creation of a new table on the ISS
	 */
	public void requestTableCreation() {

		issConnect.requestTableCreation();
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
	public void createTable(String tableName, String creator, int maxPlayers) {

		view.createISSTable(tableName, creator);
		jskat.setActiveTable(tableName);
	}

	/**
	 * Destroys a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void destroyTable(String tableName) {

		view.closeTabPanel(tableName);
		data.removeJoinedIssSkatTable(tableName);
		// TODO set to next table
		jskat.setActiveTable(null);
	}

	/**
	 * Joins a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void joinTable(String tableName) {

		issConnect.joinTable(tableName);
	}

	/**
	 * Observes a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void observeTable(String tableName) {

		issConnect.observeTable(tableName);
	}

	/**
	 * Leaves a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void leaveTable(String tableName) {
		issConnect.leaveTable(tableName);
	}

	/**
	 * Updates a local representation of an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New table status
	 */
	public void updateISSTableState(String tableName, TablePanelStatus status) {

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
	public void updateISSGame(String tableName, GameStartInformation status) {

		view.updateISSTable(tableName, data.getIssLoginName(), status);

		gameData.put(tableName, createSkatGameData(status));
	}

	private SkatGameData createSkatGameData(GameStartInformation status) {

		SkatGameData result = new SkatGameData();

		result.setGameState(GameState.GAME_START);
		for (Player player : Player.values()) {
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
	public void startGame(String tableName) {

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
	public void updateMove(String tableName, MoveInformation moveInformation) {

		SkatGameData currGame = gameData.get(tableName);
		updateGameData(currGame, moveInformation);

		view.updateISSMove(tableName, currGame, moveInformation);

		// TODO (jan 19.11.2010) extract this into separate method
		if (MoveType.BID.equals(moveInformation.getType()) || MoveType.PASS.equals(moveInformation.getType())) {

			if (isBiddingFinished(currGame)) {
				view.setDeclarer(tableName, currGame.getDeclarer());
				view.setGameState(tableName, GameState.PICKING_UP_SKAT);
			}

		} else if (MoveType.CARD_PLAY.equals(moveInformation.getType())) {

			// handle trick playing
			Trick trick = currGame.getCurrentTrick();

			view.setTrickNumber(tableName, trick.getTrickNumberInGame() + 1);

			if (trick.getThirdCard() != null) {

				Player trickWinner = SkatRuleFactory.getSkatRules(currGame.getGameType()).calculateTrickWinner(
						currGame.getGameType(), trick);
				trick.setTrickWinner(trickWinner);
				currGame.addTrick(new Trick(currGame.getTricks().size(), trick.getTrickWinner()));

				view.setActivePlayer(tableName, currGame.getCurrentTrick().getForeHand());

			} else if (trick.getSecondCard() != null) {

				view.setActivePlayer(tableName, trick.getForeHand().getRightNeighbor());

			} else if (trick.getFirstCard() != null) {

				view.setActivePlayer(tableName, trick.getForeHand().getLeftNeighbor());
			}
		}
	}

	private boolean isBiddingFinished(SkatGameData currGame) {

		boolean result = false;

		if (currGame.getNumberOfPasses() == 2) {
			for (Player currPlayer : Player.values()) {
				if (!currGame.isPlayerPass(currPlayer)) {
					if (currGame.getPlayerBid(currPlayer) > 0) {
						result = true;
						currGame.setDeclarer(currPlayer);
					}
				}
			}
		}

		return result;
	}

	private void updateGameData(SkatGameData currGame, MoveInformation moveInformation) {

		Player movePlayer = moveInformation.getPlayer();

		switch (moveInformation.getType()) {
		case DEAL:
			currGame.setGameState(GameState.DEALING);
			break;
		case BID:
			currGame.setGameState(GameState.BIDDING);
			currGame.setBidValue(moveInformation.getBidValue());
			currGame.setPlayerBid(movePlayer, moveInformation.getBidValue());
			break;
		case HOLD_BID:
			currGame.setGameState(GameState.BIDDING);
			currGame.setPlayerBid(movePlayer, currGame.getBidValue());
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
	public void endGame(String tableName, SkatGameData newGameData) {

		view.setGameState(tableName, GameState.GAME_OVER);
		view.addGameResult(tableName, newGameData.getGameSummary());
		gameData.put(tableName, newGameData);
	}

	/**
	 * Shows a message from ISS
	 * 
	 * @param messageType
	 * @param message
	 */
	public void showMessage(int messageType, String message) {
		view.showMessage(messageType, strings.getString("iss_message"), message); //$NON-NLS-1$
	}

	/**
	 * Invites a player on ISS to play at a table on ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param invitee
	 *            Invited player
	 */
	public void invitePlayer(String tableName, String invitee) {
		issConnect.invitePlayer(tableName, invitee);
	}

	/**
	 * Sends ready to play signal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendReadySignal(String tableName) {
		issConnect.sendReadySignal(tableName);
	}

	/**
	 * Send talk enabled signal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendTalkEnabledSignal(String tableName) {
		issConnect.sendTalkEnabledSignal(tableName);
	}

	/**
	 * Sends a resign signal to ISS
	 * 
	 * @param tableName
	 */
	public void sendResignSignal(String tableName) {

		issConnect.sendResignSignal(tableName);
	}

	/**
	 * Send table seat change singal to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendTableSeatChangeSignal(String tableName) {
		issConnect.sendTableSeatChangeSignal(tableName);
	}

	/**
	 * Send pass bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendPassBidMove(String tableName) {
		issConnect.sendPassMove(tableName);
	}

	/**
	 * Send hold bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendHoldBidMove(String tableName) {
		issConnect.sendHoldBidMove(tableName);
	}

	/**
	 * Send bid move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendBidMove(String tableName) {

		issConnect.sendBidMove(tableName, SkatConstants.getNextBidValue(gameData.get(tableName).getBidValue()));
	}

	/**
	 * Send pick up skat move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void sendPickUpSkatMove(String tableName) {
		issConnect.sendPickUpSkatMove(tableName);
	}

	/**
	 * Send game announcement to ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameAnnouncement
	 *            Game announcement
	 */
	public void sendGameAnnouncementMove(String tableName, GameAnnouncement gameAnnouncement) {

		issConnect.sendGameAnnouncementMove(tableName, gameAnnouncement);

	}

	/**
	 * Send card move to ISS
	 * 
	 * @param tableName
	 *            Table name
	 * @param nextCard
	 *            Card
	 */
	public void sendCardMove(String tableName, Card nextCard) {
		issConnect.sendCardMove(tableName, nextCard);
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
	public void handleInvitation(String invitor, String tableName, String invitationTicket) {

		if (view.showISSTableInvitation(invitor, tableName)) {

			issConnect.sendInvitationAccepted(tableName, invitationTicket);
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
	public void updateISSTableChatMessage(String tableName, ChatMessage message) {
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
	public boolean isTableJoined(String tableName) {

		return data.isTableJoined(tableName);
	}
}
