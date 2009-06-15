/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.control.JSkatMaster;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStatus;
import de.jskat.data.iss.ISSLoginCredentials;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.gui.JSkatView;
import de.jskat.gui.action.JSkatAction;

/**
 * Controls all ISS related actions
 */
public class ISSController {

	private static Log log = LogFactory.getLog(ISSController.class);

	private JSkatMaster jskat;
	private JSkatView view;

	private Connector issConnect;

	/**
	 * Constructor
	 * 
	 * @param controller
	 *            JSkat master controller
	 * @param newOutput
	 *            Output channel to ISS
	 */
	public ISSController(JSkatMaster controller, JSkatView newView) {

		this.jskat = controller;
		this.view = newView;
	}

	/**
	 * Disconnects from ISS
	 */
	public void disconnect() {

		if (this.issConnect != null && this.issConnect.isConnected()) {

			log.debug("connection to ISS still open"); //$NON-NLS-1$

			this.issConnect.closeConnection();
		}
	}

	/**
	 * Shows the login panel for ISS
	 */
	public void showISSLoginPanel() {

		this.view.showISSLogin();
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

		if (this.issConnect == null) {

			this.issConnect = new Connector(this);
		}

		log.debug("connector created"); //$NON-NLS-1$

		Object source = e.getSource();
		String command = e.getActionCommand();

		if (JSkatAction.CONNECT_TO_ISS.toString().equals(command)) {
			if (source instanceof ISSLoginCredentials) {

				ISSLoginCredentials login = (ISSLoginCredentials) source;

				if (!this.issConnect.isConnected()) {

					this.issConnect.setConnectionData(login.getLoginName(),
							login.getPassword(), login.getPort());
					this.issConnect.establishConnection();
				}
			} else {

				log.error("Wrong source for " + command); //$NON-NLS-1$
			}
		}

		if (this.issConnect.isConnected()) {
			
			// show ISS lobby if connection was successfull
			this.view.showISSLobby();
		}

		return this.issConnect.isConnected();
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
	public void updateISSPlayerList(String playerName, String language,
			long gamesPlayed, double strength) {

		this.view.updateISSLobbyPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes a player from the ISS player list
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayerFromList(String playerName) {

		this.view.removeFromISSLobbyPlayerList(playerName);
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
	public void updateISSTableList(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3) {

		this.view.updateISSLobbyTableList(tableName, maxPlayers, gamesPlayed,
				player1, player2, player3);
	}

	/**
	 * Removes a table from the ISS table list
	 * 
	 * @param tableName Table name
	 */
	public void removeISSTableFromList(String tableName) {
		
		this.view.removeFromISSLobbyTableList(tableName);
	}
	
	public void sendChatMessage(ISSChatMessage message) {
		
		this.issConnect.send(message);
	}

	public void addChatMessage(ChatMessageType messageType, StringTokenizer token) {
		
		switch(messageType) {
		case LOBBY:
			addLobbyChatMessage(token);
			break;
		case USER:
		case TABLE:
			// TODO implement it
			break;
		}
	}
	
	public void addLobbyChatMessage(StringTokenizer token) {

		log.debug("addLobbyChatMessage");
		
		StringBuffer message = new StringBuffer(); 

		// first the sender of the message
		message.append(token.nextToken()).append(':').append(' ');
		
		while(token.hasMoreTokens()) {
			message.append(token.nextToken()).append(' ');
		}

		ISSChatMessage chatMessage = new ISSChatMessage("Lobby", message.toString());
		
		this.view.appendISSChatMessage(ChatMessageType.LOBBY, chatMessage);
	}

	public void requestTableCreation() {
		
		this.issConnect.requestTableCreation();
	}
	
	public void createTable(String tableName, String creator, int maxPlayers) {
		
		this.view.createISSTable(tableName);
	}
	
	public void joinTable(String tableName) {
		
		this.issConnect.joinTable(tableName);
	}
	
	public void observeTable(String tableName) {
		
		this.issConnect.observeTable(tableName);
	}
	
	public void leaveTable(String tableName, String playerName) {
		
		this.issConnect.leaveTable(tableName, playerName);
	}

	public void updateISSTableState(String tableName, ISSTablePanelStatus status) {
		
		this.view.updateISSTable(tableName, status);
	}

	public void startGame(String tableName, ISSGameStatus status) {
		
		this.view.updateISSTable(tableName, status);
	}
}
