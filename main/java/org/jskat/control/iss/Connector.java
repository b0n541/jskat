/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatOptions;
import org.jskat.data.iss.ChatMessage;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;


/**
 * Connector to International Skat Server ISS
 */
class Connector {

	private static Log log = LogFactory.getLog(Connector.class);

	private static JSkatResourceBundle strings = JSkatResourceBundle.instance();
	private static JSkatOptions options = JSkatOptions.instance();

	private Socket socket;
	private PrintWriter output;
	private InputChannel issIn;
	private OutputChannel issOut;

	private String loginName;
	private String password;

	private IssController issControl;

	/**
	 * Constructor
	 * 
	 * @param controller
	 *            Controller for ISS connection
	 */
	Connector(IssController controller) {

		issControl = controller;
	}

	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName
	 *            Login name
	 * @param newPassword
	 *            Password
	 */
	void setConnectionData(String newLoginName, String newPassword) {

		loginName = newLoginName;
		password = newPassword;
	}

	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	boolean establishConnection() {

		log.debug("ISSConnector.establishConnection()"); //$NON-NLS-1$

		try {
			socket = new Socket(options.getIssAddress(), options.getIssPort()
					.intValue());
			output = new PrintWriter(socket.getOutputStream(), true);
			issOut = new OutputChannel(output);
			issIn = new InputChannel(issControl, this, socket.getInputStream());
			issIn.start();
			log.debug("Connection established..."); //$NON-NLS-1$
			issOut.send(loginName);

		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS"); //$NON-NLS-1$
			issControl.showMessage(JOptionPane.ERROR_MESSAGE,
					strings.getString("cant_connect_to_iss")); //$NON-NLS-1$
			return false;
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	void sendPassword() {

		issOut.send(password);
	}

	/**
	 * Closes the connection to ISS
	 */
	void closeConnection() {

		try {
			log.debug("closing connection"); //$NON-NLS-1$
			issIn.interrupt();
			log.debug("input channel closed"); //$NON-NLS-1$
			output.close();
			log.debug("output channel closed"); //$NON-NLS-1$
			socket.close();
			log.debug("socket closed"); //$NON-NLS-1$
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.debug("ISS connector IOException"); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether there is an open connection
	 * 
	 * @return TRUE if there is an open connection
	 */
	boolean isConnected() {

		return socket != null && !socket.isClosed();
	}

	void send(ChatMessage message) {
		// FIXME (jan 30.01.2011) refactor ChatMessage with ChatMessageType
		if ("Lobby".equals(message.getChatName())) {
			issOut.send("yell " + message.getMessage()); //$NON-NLS-1$
		} else {
			issOut.send("table " + message.getChatName() + ' ' + loginName + " tell " + message.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void requestTableCreation() {

		// TODO table creation for four player
		issOut.send("create / 3"); //$NON-NLS-1$
	}

	void joinTable(String tableName) {

		issOut.send("join " + tableName); //$NON-NLS-1$
	}

	void observeTable(String tableName) {

		issOut.send("observe " + tableName); //$NON-NLS-1$
	}

	void leaveTable(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " leave"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendReadySignal(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " ready"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendTalkEnabledSignal(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " gametalk"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendTableSeatChangeSignal(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " 34"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void invitePlayer(String tableName, String invitee) {

		issOut.send("table " + tableName + ' ' + loginName + " invite " + invitee); //$NON-NLS-1$//$NON-NLS-2$
	}

	void sendPassMove(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " play p"); //$NON-NLS-1$//$NON-NLS-2$
	}

	void sendHoldBidMove(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " play y"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void sendBidMove(String tableName, int bidValue) {

		issOut.send("table " + tableName + ' ' + loginName + " play " //$NON-NLS-1$//$NON-NLS-2$
				+ bidValue);
	}

	public void sendPickUpSkatMove(String tableName) {
		issOut.send("table " + tableName + ' ' + loginName + " play s"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void sendGameAnnouncementMove(String tableName,
			GameAnnouncement gameAnnouncement) {

		String gameAnnouncementString = getGameTypeString(gameAnnouncement.getGameType(), gameAnnouncement.isHand(),
				gameAnnouncement.isOuvert(), gameAnnouncement.isSchneider(), gameAnnouncement.isSchwarz());

		if (!gameAnnouncement.isHand()) {

			CardList skat = gameAnnouncement.getDiscardedCards();
			gameAnnouncementString += "." + getIssCardString(skat.get(0)) + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ getIssCardString(skat.get(1));
		}

		issOut.send("table " + tableName + ' ' + loginName + " play " + gameAnnouncementString); //$NON-NLS-1$//$NON-NLS-2$
	}

	private String getGameTypeString(GameType gameType, boolean hand, boolean ouvert, boolean schneider, boolean schwarz) {

		String result = getGameTypeString(gameType);

		if (hand) {
			result += "H"; //$NON-NLS-1$
		}

		if (ouvert) {
			result += "O"; //$NON-NLS-1$
		}

		if (schneider) {
			result += "S"; //$NON-NLS-1$
		}

		if (schwarz) {
			result += "Z"; //$NON-NLS-1$
		}

		return result;
	}

	private String getGameTypeString(GameType gameType) {
		switch (gameType) {
		case CLUBS:
			return "C"; //$NON-NLS-1$
		case SPADES:
			return "S"; //$NON-NLS-1$
		case HEARTS:
			return "H"; //$NON-NLS-1$
		case DIAMONDS:
			return "D"; //$NON-NLS-1$
		case NULL:
			return "N"; //$NON-NLS-1$
		case GRAND:
			return "G"; //$NON-NLS-1$
		default:
			// FIXME (jan 02.11.2010) Ramsch games are not allowed on ISS
			return null;
		}
	}

	public void sendCardMove(String tableName, Card card) {
		issOut.send("table " + tableName + ' ' + loginName + " play " + getIssCardString(card)); //$NON-NLS-1$//$NON-NLS-2$
	}

	private String getIssCardString(Card card) {
		return card.getSuit().shortString() + card.getRank().shortString();
	}

	void sendResignSignal(String tableName) {

		issOut.send("table " + tableName + ' ' + loginName + " play RE"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void sendInvitationAccepted(String tableName, String invitationTicket) {

		issOut.send("join " + tableName + " " + invitationTicket); //$NON-NLS-1$//$NON-NLS-2$
	}
}
