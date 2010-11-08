/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.util.ArrayList;
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
	ISSController issControl;

	private final static int protocolVersion = 14;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            Connection to ISS
	 * @param controller
	 *            ISS controller for JSkat
	 */
	public MessageHandler(Connector conn, ISSController controller) {

		this.connect = conn;
		this.issControl = controller;
	}

	void handleMessage(String message) {

		log.debug("ISS    |--> " + message); //$NON-NLS-1$

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
			this.issControl.showMessage(JOptionPane.ERROR_MESSAGE,
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

		} else {

			log.error("UNHANDLED MESSAGE: " + first + params.toString()); //$NON-NLS-1$ }
		}
	}

	void handleLobbyChatMessage(List<String> params) {

		this.issControl.addChatMessage(ChatMessageType.LOBBY, params);
	}

	void handlePasswordMessage() {

		this.connect.sendPassword();
	}

	void handleTextMessage(List<String> params) {
		// FIXME show it to the user
		log.error(params.toString());
	}

	void handleErrorMessage(List<String> params) {
		// FIXME show it to the user
		log.error(params.toString());
	}

	void handleTableCreateMessage(List<String> params) {

		log.debug("table creation message"); //$NON-NLS-1$

		String tableName = params.get(0);
		String creator = params.get(1);
		int seats = Integer.parseInt(params.get(2));
		this.issControl.createTable(tableName, creator, seats);
	}

	void handleTableDestroyMessage(List<String> params) {

		log.debug("table destroy message"); //$NON-NLS-1$

		String tableName = params.get(0);
		this.issControl.destroyTable(tableName);
	}

	/**
	 * table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0
	 * 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
	 */
	void handleTableUpdateMessage(List<String> params) {

		log.debug("table update message"); //$NON-NLS-1$

		String tableName = params.get(0);
		String creator = params.get(1);
		String actionCommand = params.get(2);
		List<String> detailParams = params.subList(3, params.size());

		if (actionCommand.equals("state")) { //$NON-NLS-1$

			this.issControl.updateISSTableState(tableName,
					MessageParser.getTableStatus(creator, detailParams));

		} else if (actionCommand.equals("start")) { //$NON-NLS-1$

			this.issControl.updateISSGame(tableName,
					MessageParser.getGameStartStatus(detailParams));

		} else if (actionCommand.equals("go")) { //$NON-NLS-1$

			this.issControl.startGame(tableName);

		} else if (actionCommand.equals("play")) { //$NON-NLS-1$

			this.issControl.updateMove(tableName,
					MessageParser.getMoveInformation(detailParams));

		} else if (actionCommand.equals("end")) { //$NON-NLS-1$
			this.issControl
					.endGame(tableName, getGameInformation(detailParams));
		} else {

			log.debug("unhandled action command: " + actionCommand + " for table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private SkatGameData getGameInformation(List<String> params) {

		return new SkatGameData();
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

		this.issControl.updateISSPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes a client from the client list
	 * 
	 * @param token
	 *            Player information
	 */
	void removeClientFromList(List<String> params) {

		this.issControl.removeISSPlayerFromList(params.get(0));
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

		this.issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed,
				player1, player2, player3);
	}

	/**
	 * Removes a table from the table list
	 * 
	 * @param token
	 *            Table information
	 */
	void removeTableFromList(List<String> params) {

		this.issControl.removeISSTableFromList(params.get(0));
	}

}
