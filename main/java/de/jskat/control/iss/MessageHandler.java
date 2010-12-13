/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData;

/**
 * Handles messages from ISS
 */
public class MessageHandler {

	static Log log = LogFactory.getLog(MessageHandler.class);

	Connector connect;
	IssController issControl;

	private final static int protocolVersion = 14;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            Connection to ISS
	 * @param controller
	 *            ISS controller for JSkat
	 */
	public MessageHandler(Connector conn, IssController controller) {

		connect = conn;
		issControl = controller;
	}

	void handleMessage(String message) {

		log.debug("ISS    |--> " + message); //$NON-NLS-1$

		// FIXME (jan 14.11.2010) check for NULL
		StringTokenizer tokenizer = new StringTokenizer(message); // get first
		// command
		String first = tokenizer.nextToken();
		// get all parameters
		List<String> params = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			params.add(tokenizer.nextToken());
		}

		try {

			handleMessage(first, params);

		} catch (Exception except) {
			log.error("Error in parsing ISS protocoll", except); //$NON-NLS-1$
			issControl.showMessage(JOptionPane.ERROR_MESSAGE,
					"Error in parsing ISS protocoll.");
		}

	}

	void handleMessage(String first, List<String> params) throws Exception {

		if (first.equals("password:")) { //$NON-NLS-1$

			handlePasswordMessage();

		} else if (first.equals("Welcome")) { //$NON-NLS-1$

			handleWelcomeMessage(params);

		} else if (first.equals("clients")) { //$NON-NLS-1$

			handleClientListMessage(params);

		} else if (first.equals("tables")) { //$NON-NLS-1$

			handleTableListMessage(params);

		} else if (first.equals("create")) { //$NON-NLS-1$

			handleTableCreateMessage(params);

		} else if (first.equals("table")) { //$NON-NLS-1$

			handleTableUpdateMessage(params);

		} else if (first.equals("error")) { //$NON-NLS-1$

			handleErrorMessage(params);

		} else if (first.equals("text")) { //$NON-NLS-1$

			handleTextMessage(params);

		} else if (first.equals("yell")) { //$NON-NLS-1$

			handleLobbyChatMessage(params);

		} else if (first.equals("destroy")) { //$NON-NLS-1$

			handleTableDestroyMessage(params);

		} else if (first.equals("invite")) { //$NON-NLS-1$

			handleTableInvitationMessage(params);

		} else {

			log.error("UNHANDLED MESSAGE: " + first + params.toString()); //$NON-NLS-1$ }
		}
	}

	void handleLobbyChatMessage(List<String> params) {

		issControl.addChatMessage(ChatMessageType.LOBBY, params);
	}

	void handlePasswordMessage() {

		connect.sendPassword();
	}

	void handleTextMessage(List<String> params) {
		// FIXME show it to the user
		log.error(params.toString());
	}

	void handleErrorMessage(List<String> params) {

		log.error(params.toString());
		// FIXME (jan 23.11.2010) i18n needed
		issControl.showMessage(JOptionPane.ERROR_MESSAGE,
				getErrorString(params));
	}

	private String getErrorString(List<String> params) {

		String result = new String();

		for (String token : params) {

			if (result.length() > 0) {
				result += " "; //$NON-NLS-1$
			}

			result += token;
		}

		return result;
	}

	void handleTableCreateMessage(List<String> params) {

		log.debug("table creation message"); //$NON-NLS-1$

		String tableName = params.get(0);
		String creator = params.get(1);
		int seats = Integer.parseInt(params.get(2));
		issControl.createTable(tableName, creator, seats);
	}

	void handleTableDestroyMessage(List<String> params) {

		log.debug("table destroy message"); //$NON-NLS-1$

		String tableName = params.get(0);
		issControl.destroyTable(tableName);
	}

	void handleTableInvitationMessage(List<String> params) {
		log.debug("table destroy message"); //$NON-NLS-1$

		String invitor = params.get(0);
		String tableName = params.get(1);
		String invitationTicket = params.get(2);

		issControl.handleInvitation(invitor, tableName, invitationTicket);
	}

	/**
	 * table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0
	 * 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
	 */
	void handleTableUpdateMessage(List<String> params) {

		log.debug("table update message"); //$NON-NLS-1$

		String tableName = params.get(0);
		// FIXME (jan 18.11.2010) is this the name of the creator or the
		// login name of the current player?
		String creator = params.get(1);
		String actionCommand = params.get(2);
		List<String> detailParams = params.subList(3, params.size());

		if (actionCommand.equals("error")) { //$NON-NLS-1$

			handleErrorMessage(params.subList(3, params.size()));

		} else if (actionCommand.equals("state")) { //$NON-NLS-1$

			issControl.updateISSTableState(tableName,
					MessageParser.getTableStatus(creator, detailParams));

		} else if (actionCommand.equals("start")) { //$NON-NLS-1$

			issControl.updateISSGame(tableName,
					MessageParser.getGameStartStatus(creator, detailParams));

		} else if (actionCommand.equals("go")) { //$NON-NLS-1$

			issControl.startGame(tableName);

		} else if (actionCommand.equals("play")) { //$NON-NLS-1$

			issControl.updateMove(tableName,
					MessageParser.getMoveInformation(detailParams));

		} else if (actionCommand.equals("end")) { //$NON-NLS-1$

			issControl.endGame(tableName, getGameInformation(detailParams));

		} else {

			log.debug("unhandled action command: " + actionCommand + " for table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private SkatGameData getGameInformation(List<String> params) {

		// first glue alle params back together
		String gameResult = glueParams(params);

		return MessageParser.parseGameSummary(gameResult);
	}

	private String glueParams(List<String> params) {

		String result = new String();
		Iterator<String> paramIterator = params.iterator();

		while (paramIterator.hasNext()) {

			if (result.length() > 0) {
				result += " "; //$NON-NLS-1$
			}

			result += paramIterator.next();
		}

		return result;
	}

	/**
	 * Handles a client list message
	 * 
	 * @param token
	 *            Client information
	 */
	void handleClientListMessage(List<String> params) {

		String plusMinus = params.remove(0);

		if (plusMinus.equals("+")) { //$NON-NLS-1$

			updateClientList(params);

		} else if (plusMinus.equals("-")) { //$NON-NLS-1$

			removeClientFromList(params);
		}
	}

	/**
	 * Adds or updates a client on the client list
	 * 
	 * @param token
	 *            Player information
	 */
	void updateClientList(List<String> params) {

		String playerName = params.get(0);
		String language = params.get(2);
		long gamesPlayed = Long.parseLong(params.get(3));
		double strength = Double.parseDouble(params.get(4));

		issControl.updateISSPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes a client from the client list
	 * 
	 * @param token
	 *            Player information
	 */
	void removeClientFromList(List<String> params) {

		issControl.removeISSPlayerFromList(params.get(0));
	}

	/**
	 * Handles the welcome message and checks the protocol version
	 * 
	 * @param token
	 *            Welcome information
	 */
	void handleWelcomeMessage(List<String> params) {

		double issProtocolVersion = Double
				.parseDouble(params.get(params.size() - 1));

		log.debug("iss version: " + issProtocolVersion); //$NON-NLS-1$
		log.debug("local version: " + protocolVersion); //$NON-NLS-1$

		if ((int) issProtocolVersion != protocolVersion) {
			// TODO handle this in JSkatMaster
			log.error("Wrong protocol version!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Handles a table list message
	 * 
	 * @param Table
	 *            information
	 */
	void handleTableListMessage(List<String> params) {

		String plusMinus = params.remove(0);

		if (plusMinus.equals("+")) { //$NON-NLS-1$

			updateTableList(params);

		} else if (plusMinus.equals("-")) { //$NON-NLS-1$

			removeTableFromList(params);
		}
	}

	/**
	 * Adds or updates a table on the table list
	 * 
	 * @param token
	 *            Table information
	 */
	void updateTableList(List<String> params) {

		String tableName = params.get(0);
		int maxPlayers = Integer.parseInt(params.get(1));
		long gamesPlayed = Long.parseLong(params.get(2));
		String player1 = params.get(3);
		String player2 = params.get(4);
		String player3 = params.get(5);

		issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed,
				player1, player2, player3);
	}

	/**
	 * Removes a table from the table list
	 * 
	 * @param token
	 *            Table information
	 */
	void removeTableFromList(List<String> params) {

		issControl.removeISSTableFromList(params.get(0));
	}

}
