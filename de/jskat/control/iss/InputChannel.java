/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.iss.ISSGameStatus;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSPlayerStatus;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.data.iss.MovePlayer;
import de.jskat.data.iss.MoveType;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Reads data from ISS until an interrupt signal occures
 * 
 * Idea was taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel extends Thread {

	static Log log = LogFactory.getLog(InputChannel.class);

	ISSController issControl;

	private Connector connect;
	private final static int protocolVersion = 14;

	Object lock = new Object();
	InputStream stream;
	BufferedReader reader;
	boolean done = false;

	/**
	 * Constructor
	 * 
	 * @param controller
	 * @param conn
	 * @param is
	 *            Input stream
	 */
	InputChannel(ISSController controller, Connector conn, InputStream is) {

		this.issControl = controller;
		this.connect = conn;
		this.stream = is;
		this.reader = new BufferedReader(new InputStreamReader(this.stream));
	}

	/**
	 * Helper class for reading incoming information
	 */
	class ReaderClass extends Thread {

		@Override
		public void run() {

			String line;
			while (!InputChannel.this.done) {
				try {
					line = InputChannel.this.reader.readLine();
					try {
						handleMessage(line);
					} catch (Exception except) {
						log.error("Error in parsing ISS protocoll", except); //$NON-NLS-1$
						InputChannel.this.issControl.showMessage(
								JOptionPane.ERROR_MESSAGE,
								"Error in parsing ISS protocoll.");
					}
				} catch (IOException ioe) {
					log.debug("IO exception", ioe); //$NON-NLS-1$
					InputChannel.this.done = true;
				}
			}

			synchronized (InputChannel.this.lock) {
				InputChannel.this.lock.notify();
			}
		}
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		ReaderClass rc = new ReaderClass();

		synchronized (this.lock) {

			rc.start();

			while (!this.done) {
				try {
					this.lock.wait();
				} catch (InterruptedException ie) {
					this.done = true;
					rc.interrupt();
					try {
						this.stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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

			log.error("UNHANDLED MESSAGE: " + message); //$NON-NLS-1$ }
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
					getTableStatus(detailParams));

		} else if (actionCommand.equals("start")) { //$NON-NLS-1$

			this.issControl.updateISSGame(tableName,
					getGameStartStatus(detailParams));

		} else if (actionCommand.equals("go")) { //$NON-NLS-1$

			this.issControl.startGame(tableName);

		} else if (actionCommand.equals("play")) { //$NON-NLS-1$

			this.issControl.updateMove(tableName,
					getMoveInformation(detailParams));

		} else if (actionCommand.equals("end")) { //$NON-NLS-1$
			// TODO implement it
			// this.issControl.endGame(tableName, getGameInformation(token));
		} else {

			log
					.debug("unhandled action command: " + actionCommand + " for table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * table .1 bar state --> was cut away before <br>
	 * --> params: <br>
	 * 3 <br>
	 * bar xskat xskat:2 . <br>
	 * bar . 0 0 0 0 0 0 1 0 <br>
	 * xskat $ 0 0 0 0 0 0 1 1 <br>
	 * xskat:2 $ 0 0 0 0 0 0 1 1 <br>
	 * . . 0 0 0 0 0 0 0 0 false <br>
	 */
	ISSTablePanelStatus getTableStatus(List<String> params) {

		ISSTablePanelStatus status = new ISSTablePanelStatus();

		status.setMaxPlayers(Integer.parseInt(params.get(0)));

		// get player status
		for (int i = 0; i < status.getMaxPlayers(); i++) {
			// parse only non empty seats
			if (!(".".equals(params.get(i * 10 + 5)))) { //$NON-NLS-1$
				// there is a player
				status.addPlayer(parsePlayerStatus(params.subList(i * 10 + 5,
						i * 10 + 16)));
			}
		}

		return status;
	}

	/**
	 * table .1 bar state <br>
	 * 3 <br>
	 * bar xskat xskat:2 . <br>
	 * bar . 0 0 0 0 0 0 1 0 <br>
	 * --> params:<br>
	 * xskat $ 0 0 0 0 0 0 1 1 <br>
	 * xskat:2 $ 0 0 0 0 0 0 1 1 <br>
	 * . . 0 0 0 0 0 0 0 0 false <br>
	 */
	ISSPlayerStatus parsePlayerStatus(List<String> params) {

		ISSPlayerStatus status = new ISSPlayerStatus();

		status.setName(params.get(0));
		status.setIP(params.get(1));
		status.setGamesPlayed(Integer.parseInt(params.get(2)));
		status.setGamesWon(Integer.parseInt(params.get(3)));
		status.setLastGameResult(Integer.parseInt(params.get(4)));
		status.setTotalPoints(Integer.parseInt(params.get(5)));
		status.setSwitch34(Integer.parseInt(params.get(6)) == 1);
		// ignore next information, unknown purpose
		status.setTalkEnabled(Integer.parseInt(params.get(8)) == 1);
		status.setReadyToPlay(Integer.parseInt(params.get(9)) == 1);

		return status;
	}

	ISSGameStatus getGameStartStatus(List<String> params) {

		log.debug("game start parameter: " + params); //$NON-NLS-1$

		ISSGameStatus status = new ISSGameStatus();

		status.setGameNo(Integer.parseInt(params.get(0)));
		status.putPlayerName(Player.FORE_HAND, params.get(1));
		status.putPlayerTime(Player.FORE_HAND, new Double(params.get(2)));
		status.putPlayerName(Player.MIDDLE_HAND, params.get(3));
		status.putPlayerTime(Player.MIDDLE_HAND, new Double(params.get(4)));
		status.putPlayerName(Player.HIND_HAND, params.get(5));
		status.putPlayerTime(Player.HIND_HAND, new Double(params.get(6)));

		return status;
	}

	ISSMoveInformation getMoveInformation(List<String> params) {

		ISSMoveInformation info = new ISSMoveInformation();

		String movePlayer = params.get(0);
		log.debug("Move player: " + movePlayer); //$NON-NLS-1$
		if ("w".equals(movePlayer)) { //$NON-NLS-1$
			// world move
			info.setMovePlayer(MovePlayer.WORLD);
		} else if ("0".equals(movePlayer)) { //$NON-NLS-1$
			// fore hand move
			info.setMovePlayer(MovePlayer.FORE_HAND);
		} else if ("1".equals(movePlayer)) { //$NON-NLS-1$
			// middle hand move
			info.setMovePlayer(MovePlayer.MIDDLE_HAND);
		} else if ("2".equals(movePlayer)) { //$NON-NLS-1$
			// hind hand move
			info.setMovePlayer(MovePlayer.HIND_HAND);
		}

		// FIXME Unhandled moves
		String move = params.get(1);
		log.debug("Move: " + move); //$NON-NLS-1$
		if ("y".equals(move)) { //$NON-NLS-1$
			// holding bid move
			info.setType(MoveType.BID);
		} else if ("p".equals(move)) { //$NON-NLS-1$
			// pass move
			info.setType(MoveType.PASS);
		} else if ("s".equals(move)) { //$NON-NLS-1$
			// skat request move
			info.setType(MoveType.SKAT_REQUEST);
		} else if ("??.??".equals(move)) { //$NON-NLS-1$
			// hidden skat given to a player
			info.setType(MoveType.SKAT_LOOKING);
		} else if (move.startsWith("TI.")) { //$NON-NLS-1$
			// time out for player
			info.setType(MoveType.TIME_OUT);
			info.setTimeOutPlayer(parseTimeOut(move));
		} else {
			// extensive parsing needed

			// test card move
			Card card = Card.getCardFromString(move);
			if (card != null) {
				// card play move
				info.setType(MoveType.CARD_PLAY);
				info.setCard(card);
			} else {
				// card parsing failed

				// test bidding
				int bid = -1;
				try {

					bid = Integer.parseInt(move);

				} catch (NumberFormatException e) {

					bid = -1;
				}
				if (bid > -1) {
					// bidding
					info.setType(MoveType.BID);
					info.setBidValue(Integer.parseInt(move));
				} else {

					if (move.length() == 95) {
						// card dealing
						info.setType(MoveType.DEAL);
						info.setDealCards(parseCardDeal(move));
					} else if (move.length() == 5) {
						// open skat given to a player
						info.setType(MoveType.SKAT_LOOKING);
						// TODO parse cards
					} else {
						// game announcement
						info.setType(MoveType.GAME_ANNOUNCEMENT);
						parseGameAnnoucement(info, move);
					}
				}
			}
		}

		// parse player times
		info.putPlayerTime(Player.FORE_HAND, new Double(params.get(params
				.size() - 3)));
		info.putPlayerTime(Player.MIDDLE_HAND, new Double(params.get(params
				.size() - 2)));
		info.putPlayerTime(Player.HIND_HAND, new Double(params.get(params
				.size() - 1)));

		return info;
	}

	/**
	 * <game-type> :: (G | C | S | H | D | N) (type Grand .. Null) [O] (ouvert)
	 * [H] (hand, not given if O + trump game) [S] (schneider announced, only in
	 * H games, not if O or Z) [Z] (schwarz announced, only in H games)
	 */
	void parseGameAnnoucement(ISSMoveInformation info, String move) {

		StringTokenizer annToken = new StringTokenizer(move, "."); //$NON-NLS-1$
		String gameType = annToken.nextToken();

		GameAnnouncement ann = new GameAnnouncement();

		// at first the game type
		if (gameType.startsWith("G")) { //$NON-NLS-1$

			ann.setGameType(GameType.GRAND);

		} else if (gameType.startsWith("C")) { //$NON-NLS-1$

			ann.setGameType(GameType.CLUBS);

		} else if (gameType.startsWith("S")) { //$NON-NLS-1$

			ann.setGameType(GameType.SPADES);

		} else if (gameType.startsWith("H")) { //$NON-NLS-1$

			ann.setGameType(GameType.HEARTS);

		} else if (gameType.startsWith("D")) { //$NON-NLS-1$

			ann.setGameType(GameType.DIAMONDS);

		} else if (gameType.startsWith("N")) { //$NON-NLS-1$

			ann.setGameType(GameType.NULL);
		}

		// parse other game modifiers
		for (int i = 1; i < gameType.length(); i++) {

			char mod = gameType.charAt(i);

			if (mod == 'O') {

				ann.setOuvert(true);
			} else if (mod == 'H') {

				ann.setHand(true);
			} else if (mod == 'S') {

				ann.setSchneider(true);
			} else if (mod == 'Z') {

				ann.setSchwarz(true);
			}
		}
		info.setGameAnnouncement(ann);

		Card skatCard0 = Card.getCardFromString(annToken.nextToken());
		Card skatCard1 = Card.getCardFromString(annToken.nextToken());

		info.setSkatCards(skatCard0, skatCard1);

		while (annToken.hasMoreTokens()
				&& info.getGameAnnouncement().isOuvert()) {
			// player has shown the cards
			// ouvert game

			// FIXME show cards
		}
	}

	/**
	 * ??.??.??.??.??.??.??.??.??.??|D9.S9.ST.S8.C9.DT.DQ.CJ.SA.HA|??.??.??.??.?
	 * ?.??.??.??.??.??|??.??
	 * 
	 * ?? - hidden card fore hand cards|middle hand cards|hind hand cards|skat
	 */
	List<CardList> parseCardDeal(String move) {

		StringTokenizer handTokens = new StringTokenizer(move, "|"); //$NON-NLS-1$
		List<CardList> result = new ArrayList<CardList>();

		while (handTokens.hasMoreTokens()) {
			result.add(parseHand(handTokens.nextToken()));
		}

		return result;
	}

	CardList parseHand(String hand) {

		StringTokenizer cardTokens = new StringTokenizer(hand, "."); //$NON-NLS-1$
		CardList result = new CardList();

		while (cardTokens.hasMoreTokens()) {
			result.add(Card.getCardFromString(cardTokens.nextToken()));
		}

		return result;
	}

	/**
	 * TI.0
	 */
	Player parseTimeOut(String timeOut) {

		Player result = null;

		switch (timeOut.charAt(3)) {
		case '0':
			result = Player.FORE_HAND;
			break;
		case '1':
			result = Player.MIDDLE_HAND;
			break;
		case '2':
			result = Player.HIND_HAND;
			break;
		}

		return result;
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

	/**
	 * Handles a client list message
	 * 
	 * @param token
	 *            Client information
	 */
	void handleClientListMessage(List<String> params) {

		String plusMinus = params.remove(0);

		if (plusMinus.equals("+")) { //$NON-NLS-1$

			updatePlayerList(params);

		} else if (plusMinus.equals("-")) { //$NON-NLS-1$

			removeClientFromList(params);
		}
	}

	/**
	 * Adds or updates a player on the client list
	 * 
	 * @param token
	 *            Player information
	 */
	void updatePlayerList(List<String> params) {

		String playerName = params.get(0);
		String language = params.get(2);
		long gamesPlayed = Long.parseLong(params.get(3));
		double strength = Double.parseDouble(params.get(4));

		this.issControl.updateISSPlayerList(playerName, language, gamesPlayed,
				strength);
	}

	/**
	 * Removes a player from the client list
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

		double issProtocolVersion = Double.parseDouble(params
				.get(params.size() - 1));

		log.debug("iss version: " + issProtocolVersion); //$NON-NLS-1$
		log.debug("local version: " + InputChannel.protocolVersion); //$NON-NLS-1$

		if ((int) issProtocolVersion != InputChannel.protocolVersion) {
			// TODO handle this in JSkatMaster
			log.error("Wrong protocol version!!!"); //$NON-NLS-1$
		}
	}
}