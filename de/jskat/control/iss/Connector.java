/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.iss.ISSChatMessage;
import de.jskat.util.Card;
import de.jskat.util.GameType;

/**
 * Connector to International Skat Server ISS
 */
class Connector {

	private static Log log = LogFactory.getLog(Connector.class);

	private Socket socket;
	private PrintWriter output;
	private InputChannel issIn;
	private OutputChannel issOut;

	private String loginName;
	private String password;
	private int port;

	private ISSController issControl;

	/**
	 * Constructor
	 * 
	 * @param controller
	 *            Controller for ISS connection
	 */
	Connector(ISSController controller) {

		this.issControl = controller;
	}

	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName
	 *            Login name
	 * @param newPassword
	 *            Password
	 * @param newPort
	 *            Port number (80, 7000, 8000 are allowed)
	 */
	void setConnectionData(String newLoginName, String newPassword, int newPort) {

		this.loginName = newLoginName;
		this.password = newPassword;

		if (newPort == 80 || newPort == 7000 || newPort == 8000) {

			this.port = newPort;
		} else {

			throw new IllegalArgumentException(
					"Unsupported port number: " + newPort); //$NON-NLS-1$
		}
	}

	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	boolean establishConnection() {

		log.debug("ISSConnector.establishConnection()"); //$NON-NLS-1$

		try {
			// TODO make this configurable
			this.socket = new Socket("skatgame.net", this.port); //$NON-NLS-1$
			this.output = new PrintWriter(this.socket.getOutputStream(), true);
			this.issOut = new OutputChannel(this.output);
			this.issIn = new InputChannel(this.issControl, this,
					this.socket.getInputStream());
			this.issIn.start();
			log.debug("Connection established..."); //$NON-NLS-1$
			this.issOut.send(this.loginName);

		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS"); //$NON-NLS-1$
			this.issControl.showMessage(JOptionPane.ERROR_MESSAGE,
					"Can't establish connection to ISS");
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString()); //$NON-NLS-1$
		}

		return true;
	}

	void sendPassword() {

		this.issOut.send(this.password);
	}

	/**
	 * Closes the connection to ISS
	 */
	void closeConnection() {

		try {
			log.debug("closing connection"); //$NON-NLS-1$
			this.issIn.interrupt();
			log.debug("input channel closed"); //$NON-NLS-1$
			this.output.close();
			log.debug("output channel closed"); //$NON-NLS-1$
			this.socket.close();
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

		return this.socket != null && this.socket.isBound();
	}

	void send(ISSChatMessage message) {

		this.issOut.send("yell " + message.getMessage()); //$NON-NLS-1$
	}

	void requestTableCreation() {

		// TODO table creation for four player
		this.issOut.send("create / 3"); //$NON-NLS-1$
	}

	void joinTable(String tableName) {

		this.issOut.send("join " + tableName); //$NON-NLS-1$
	}

	void observeTable(String tableName) {

		this.issOut.send("observe " + tableName); //$NON-NLS-1$
	}

	void leaveTable(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " leave"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendReadySignal(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " ready"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendTalkEnabledSignal(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " gametalk"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void sendTableSeatChangeSignal(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " 34"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	void invitePlayer(String tableName, String invitee) {

		this.issOut
				.send("table " + tableName + ' ' + loginName + " invite " + invitee); //$NON-NLS-1$//$NON-NLS-2$
	}

	void sendPassMove(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " play p"); //$NON-NLS-1$//$NON-NLS-2$
	}

	void sendHoldBidMove(String tableName) {

		this.issOut.send("table " + tableName + ' ' + loginName + " play y"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void sendBidMove(String tableName, int bidValue) {

		this.issOut.send("table " + tableName + ' ' + loginName + " play " //$NON-NLS-1$//$NON-NLS-2$
				+ bidValue);
	}

	public void sendLookIntoSkatMove(String tableName) {
		this.issOut.send("table " + tableName + ' ' + loginName + " play s"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void sendGameAnnouncementMove(String tableName, GameType gameType,
			boolean hand, boolean ouvert, Card... discardedCards) {

		String gameAnnouncement = getGameTypeString(gameType, hand, ouvert);

		if (!hand) {
			// FIXME (jan 02.11.2010) don't rely on toString() for card string
			// generation
			gameAnnouncement += "." + discardedCards[0].toString() + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ discardedCards[1].toString();
		}

		this.issOut
				.send("table " + tableName + ' ' + loginName + " play " + gameAnnouncement); //$NON-NLS-1$//$NON-NLS-2$
	}

	private String getGameTypeString(GameType gameType, boolean hand,
			boolean ouvert) {

		String result = getGameTypeString(gameType);

		if (hand) {
			result += "H"; //$NON-NLS-1$
		}

		if (ouvert) {
			result += "O"; //$NON-NLS-1$
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
		// FIXME (jan 02.11.2010) don't rely on toString() for card string
		// generation
		this.issOut
				.send("table " + tableName + ' ' + loginName + " play " + card.toString()); //$NON-NLS-1$//$NON-NLS-2$
	}
}
