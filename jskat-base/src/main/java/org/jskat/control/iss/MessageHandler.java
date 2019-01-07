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
package org.jskat.control.iss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.iss.IssDisconnectedEvent;
import org.jskat.control.event.table.TableRemovedEvent;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.MoveInformation;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles messages from ISS
 */
public class MessageHandler extends Thread {

	private static Logger log = LoggerFactory.getLogger(MessageHandler.class);

	private StreamConnector connect;
	private final IssController issControl;

	private final JSkatResourceBundle strings;

	private final List<String> messageList;

	private JSkatEventBus eventBus = JSkatEventBus.INSTANCE;

	private final static int protocolVersion = 14;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            Connection to ISS
	 * @param controller
	 *            ISS controller for JSkat
	 */
	public MessageHandler(final StreamConnector conn,
			final IssController controller) {

		this.connect = conn;
		this.issControl = controller;

		this.strings = JSkatResourceBundle.INSTANCE;

		this.messageList = new ArrayList<String>();
	}

	public MessageHandler(final IssController controller) {
		this.issControl = controller;
		this.strings = JSkatResourceBundle.INSTANCE;

		this.messageList = new ArrayList<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (true) {
			if (this.messageList.size() > 0) {

				final String message = getNextMessage();
				handleMessage(message);
			} else {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					log.warn("Thread.sleep() was interrupted"); //$NON-NLS-1$
				}
			}
		}
	}

	synchronized void addMessage(final String newMessage) {

		this.messageList.add(newMessage);
	}

	private synchronized String getNextMessage() {

		return this.messageList.remove(0);
	}

	void handleMessage(final String message) {

		if (message == null) {
			this.eventBus.post(new IssDisconnectedEvent());
		} else {

			final StringTokenizer tokenizer = new StringTokenizer(message); // get
			// first
			// command
			final String first = tokenizer.nextToken();
			// get all parameters
			final List<String> params = new ArrayList<String>();
			while (tokenizer.hasMoreTokens()) {
				params.add(tokenizer.nextToken());
			}

			try {

				handleMessage(first, params);

			} catch (final Exception except) {
				log.error("Error in parsing ISS protocoll", except); //$NON-NLS-1$
				this.issControl.showErrorMessage(this.strings
						.getString("iss_error_parsing_iss_protocol")); //$NON-NLS-1$
			}
		}
	}

	void handleMessage(final String first, final List<String> params) {

		final MessageType type = MessageType.getByString(first);

		if (MessageType.UNKNOWN.equals(type)) {

			log.error("UNHANDLED MESSAGE: " + first + params.toString()); //$NON-NLS-1$ }
		} else {
			// FIXME (jansch 30.05.2011) put message into a queue
			try {
				handleMessageObsolete(type, params);
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void handleMessageObsolete(final MessageType type, final List<String> params)
			throws Exception {

		switch (type) {
		case PASSWORD:
			handlePasswordMessage();
			break;
		case WELCOME:
			handleWelcomeMessage(params);
			break;
		case VERSION:
			handleVersionMessage(params);
			break;
		case CLIENTS:
			handleClientListMessage(params);
			break;
		case TABLES:
			handleTableListMessage(params);
			break;
		case CREATE:
			handleTableCreateMessage(params);
			break;
		case INVITE:
			handleTableInvitationMessage(params);
			break;
		case TABLE:
			handleTableUpdateMessage(params);
			break;
		case DESTROY:
			handleTableDestroyMessage(params);
			break;
		case ERROR:
			handleErrorMessage(params);
			break;
		case TEXT:
			handleTextMessage(params);
			break;
		case YELL:
			handleLobbyChatMessage(params);
			break;
		}
	}

	void handleLobbyChatMessage(final List<String> params) {

		this.issControl.addChatMessage(ChatMessageType.LOBBY, params);
	}

	void handlePasswordMessage() {
		this.issControl.sendPassword();
	}

	void handleTextMessage(final List<String> params) {
		// FIXME show it to the user
		log.error(params.toString());
	}

	void handleErrorMessage(final List<String> params) {

		log.error(params.toString());
		this.issControl.showErrorMessage(getI18ErrorString(getErrorString(params)));
	}

	private String getErrorString(final List<String> params) {

		for (final String param : params) {
			if (param.startsWith("_")) { //$NON-NLS-1$
				return param;
			}
		}

		return params.toString();
	}

	private String getI18ErrorString(final String errorString) {

		if ("_id_pw_mismatch".equals(errorString)) { //$NON-NLS-1$
			return this.strings.getString("iss_login_password_wrong"); //$NON-NLS-1$
		} else if ("_not_your_turn".equals(errorString)) { //$NON-NLS-1$
			return this.strings.getString("iss_not_your_turn"); //$NON-NLS-1$
		} else if ("_invalid_move_colon".equals(errorString)) { //$NON-NLS-1$
			return this.strings.getString("iss_invalid_move_colon"); //$NON-NLS-1$
		}

		return errorString;
	}

	void handleTableCreateMessage(final List<String> params) {

		log.debug("table creation message"); //$NON-NLS-1$

		final String tableName = params.get(0);
		final String creator = params.get(1);
		final int seats = Integer.parseInt(params.get(2));
		this.issControl.createTable(tableName, creator, seats);
	}

	void handleTableDestroyMessage(final List<String> params) {

		log.debug("table destroy message"); //$NON-NLS-1$

		final String tableName = params.get(0);
		
		eventBus.post(new TableRemovedEvent(tableName, JSkatViewType.ISS_TABLE));
	}

	void handleTableInvitationMessage(final List<String> params) {
		log.debug("table destroy message"); //$NON-NLS-1$

		final String invitor = params.get(0);
		final String tableName = params.get(1);
		final String invitationTicket = params.get(2);

		this.issControl.handleInvitation(invitor, tableName, invitationTicket);
	}

	/**
	 * table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0
	 * 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
	 */
	void handleTableUpdateMessage(final List<String> params) {

		log.debug("table update message"); //$NON-NLS-1$

		final String tableName = params.get(0);

		if (this.issControl.isTableJoined(tableName)) {

			// FIXME (jan 18.11.2010) is this the name of the creator or the
			// login name of the current player?
			final String creator = params.get(1);
			final String actionCommand = params.get(2);
			final List<String> detailParams = params.subList(3, params.size());

			if (actionCommand.equals("error")) { //$NON-NLS-1$

				handleErrorMessage(params.subList(3, params.size()));

			} else if (actionCommand.equals("state")) { //$NON-NLS-1$

				this.issControl.updateISSTableState(tableName,
						MessageParser.getTableStatus(creator, detailParams));

			} else if (actionCommand.equals("start")) { //$NON-NLS-1$

				this.issControl
						.updateISSGame(tableName, MessageParser
								.getGameStartStatus(creator, detailParams));

			} else if (actionCommand.equals("go")) { //$NON-NLS-1$

				this.issControl.startGame(tableName);

			} else if (actionCommand.equals("play")) { //$NON-NLS-1$

				final MoveInformation moveInfo = MessageParser
						.getMoveInformation(detailParams);
				MessageParser.parsePlayerTimes(detailParams, moveInfo);
				this.issControl.updateMove(tableName, moveInfo);

			} else if (actionCommand.equals("tell")) { //$NON-NLS-1$

				this.issControl.updateISSTableChatMessage(tableName, MessageParser
						.getTableChatMessage(tableName, detailParams));

			} else if (actionCommand.equals("end")) { //$NON-NLS-1$

				this.issControl.endGame(tableName, getGameInformation(detailParams));

			} else {

				log.debug("unhandled action command: " + actionCommand + " for table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private SkatGameData getGameInformation(final List<String> params) {

		// first glue alle params back together
		final String gameResult = glueParams(params);

		return MessageParser.parseGameSummary(gameResult);
	}

	private String glueParams(final List<String> params) {

		String result = new String();
		final Iterator<String> paramIterator = params.iterator();

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
	 * @param params
	 *            parameters
	 */
	void handleClientListMessage(final List<String> params) {

		final String plusMinus = params.remove(0);

		if (plusMinus.equals("+")) { //$NON-NLS-1$

			updateClientList(params);

		} else if (plusMinus.equals("-")) { //$NON-NLS-1$

			removeClientFromList(params);
		}
	}

	/**
	 * Adds or updates a client on the client list
	 * 
	 * @param params
	 *            Player information
	 */
	void updateClientList(final List<String> params) {

		final String playerName = params.get(0);
		final String language = params.get(2);
		final long gamesPlayed = Long.parseLong(params.get(3));
		final double strength = Double.parseDouble(params.get(4));

		this.issControl.updateISSPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes a client from the client list
	 * 
	 * @param params
	 *            Player information
	 */
	void removeClientFromList(final List<String> params) {

		this.issControl.removeISSPlayerFromList(params.get(0));
	}

	/**
	 * Handles the welcome message and checks the protocol version
	 * 
	 * @param params
	 *            Welcome information
	 */
	void handleWelcomeMessage(final List<String> params) {

		final String login = params.get(0);
		final double issProtocolVersion = Double.parseDouble(params.get(params
				.size() - 1));

		log.debug("ISS version: " + issProtocolVersion); //$NON-NLS-1$
		log.debug("local version: " + protocolVersion); //$NON-NLS-1$

		if ((int) issProtocolVersion != protocolVersion) {
			// TODO handle this in JSkatMaster
			log.error("Wrong protocol version!!!"); //$NON-NLS-1$
			log.error("iss version: " + issProtocolVersion); //$NON-NLS-1$
			log.error("local version: " + protocolVersion); //$NON-NLS-1$
		}

		eventBus.post(new IssConnectedEvent(login));
	}

	/**
	 * Handles the version message and checks the protocol version
	 * 
	 * @param params
	 *            Welcome information
	 */
	void handleVersionMessage(final List<String> params) {
		log.debug("ISS version: " + params.get(0));
	}

	/**
	 * Handles a table list message
	 * 
	 * @param params
	 *            Table information
	 */
	void handleTableListMessage(final List<String> params) {

		final String plusMinus = params.remove(0);

		if (plusMinus.equals("+")) { //$NON-NLS-1$

			updateTableList(params);

		} else if (plusMinus.equals("-")) { //$NON-NLS-1$

			removeTableFromList(params);
		}
	}

	/**
	 * Adds or updates a table on the table list
	 * 
	 * @param params
	 *            Table information
	 */
	void updateTableList(final List<String> params) {

		final String tableName = params.get(0);
		final int maxPlayers = Integer.parseInt(params.get(1));
		final long gamesPlayed = Long.parseLong(params.get(2));
		final String player1 = params.get(3);
		final String player2 = params.get(4);
		final String player3 = params.get(5);

		this.issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed,
				player1, player2, player3);
	}

	/**
	 * Removes a table from the table list
	 * 
	 * @param params
	 *            Table information
	 */
	void removeTableFromList(final List<String> params) {

		this.issControl.removeISSTableFromList(params.get(0));
	}

}
