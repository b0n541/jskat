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
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Reads data from ISS until an interrupt signal occures
 * 
 * Idea was taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel extends Thread {

	private static Log log = LogFactory.getLog(InputChannel.class);
	
	private ISSController issControl;
	
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
     * @param is Input stream
     */
    InputChannel(ISSController controller, Connector conn, InputStream is) {

    	this.issControl = controller;
    	this.connect = conn;
    	this.stream = is;
        this.reader = new BufferedReader(new InputStreamReader(this.stream));
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        ReaderClass rc = new ReaderClass( );

        synchronized(this.lock) {

            rc.start( );

            while (!this.done) {
                try {
                    this.lock.wait( );
                } catch (InterruptedException ie) {
                    this.done = true;
                    rc.interrupt( );
                    try {
                        this.stream.close( );
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
    }

	void handleMessage(String message) {
		
		log.debug("ISS: " + message); //$NON-NLS-1$
		
		StringTokenizer token = new StringTokenizer(message);
		String first = token.nextToken();
		
		if (first.equals("password:")) { //$NON-NLS-1$
			
			handlePasswordMessage();
		}
		else if (first.equals("Welcome")) { //$NON-NLS-1$
			
			handleWelcomeMessage(token);
		}
		else if (first.equals("clients")) { //$NON-NLS-1$
			
			handleClientListMessage(token);
		}
		else if (first.equals("tables")) { //$NON-NLS-1$
			
			handleTableListMessage(token);
		}
		else if (first.equals("create")) { //$NON-NLS-1$
			
			handleTableCreateMessage(token);
		}
		else if (first.equals("table")) { //$NON-NLS-1$
			
			handleTableUpdateMessage(token);
		}
		else if (first.equals("error")) { //$NON-NLS-1$
			
			handleErrorMessage(token);
		}
		else if (first.equals("text")) { //$NON-NLS-1$
			
			handleTextMessage(token);
		}
		else if (first.equals("yell")) { //$NON-NLS-1$
			
			handleLobbyChatMessage(token);
		}
		else {
			
			log.error("UNHANDLED MESSAGE: " + message); //$NON-NLS-1$
		}
	}
	
	private void handleLobbyChatMessage(StringTokenizer token) {
		
		this.issControl.addChatMessage(ChatMessageType.LOBBY, token);
	}

	private void handlePasswordMessage() {
		
		this.connect.sendPassword();
	}

	private void handleTextMessage(StringTokenizer token) {
		
		StringBuffer textMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			textMessage.append(token.nextToken()).append(' ');
		}
		
		log.error(textMessage);
	}

	private void handleErrorMessage(StringTokenizer token) {
		
		StringBuffer errorMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			errorMessage.append(token.nextToken()).append(' ');
		}
		
		log.error(errorMessage);
	}

	private void handleTableCreateMessage(StringTokenizer token) {

		log.debug("table creation message");
		
		String tableName = token.nextToken();
		String creator = token.nextToken();
		this.issControl.createTable(tableName, creator, Integer.parseInt(token.nextToken()));
		
		// FIXME remove it
		this.issControl.invitePlayer(tableName, creator, "xskat");
		this.issControl.invitePlayer(tableName, creator, "xskat");
		this.issControl.startGame(tableName);
	}
	
	private void handleTableUpdateMessage(StringTokenizer token) {
		
		log.debug("table update message");
		
		// table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
		
		String tableName = token.nextToken();
		String creator = token.nextToken();
		String actionCommand = token.nextToken();
		
		if (actionCommand.equals("state")) {
		
			this.issControl.updateISSTableState(tableName, getTableStatus(token));
		}
		else if (actionCommand.equals("start")) {
			
			this.issControl.startGame(tableName, getGameStartStatus(token));
		}
		else if (actionCommand.equals("go")) {
			
			this.issControl.startGame(tableName);
		}
		else if (actionCommand.equals("play")) {
			
			this.issControl.updateMove(tableName, getMoveInformation(token));
		}
		else if (actionCommand.equals("end")) {
			
			//this.issControl.endGame(tableName, getGameInformation(token));
		}
		else {
			
			log.debug("unhandled action command: " + actionCommand + " for table " + tableName);
		}
	}
	
	private ISSTablePanelStatus getTableStatus(StringTokenizer token) {
		
		ISSTablePanelStatus status = new ISSTablePanelStatus();
		
		status.setMaxPlayers(Integer.parseInt(token.nextToken()));
		
		List<String> playerNames = new ArrayList<String>(); 
		for (int i = 0; i < 4; i++) {
		
			playerNames.add(token.nextToken());
		}

		for (int i = 0; i < status.getMaxPlayers(); i++) {
			
			if (!playerNames.get(i).equals(".")) {
			
				status.addPlayer(playerNames.get(i), parsePlayerStatus(token));
			}
			else {
				
				skipTokens(token, 9);
			}
		}
		
		return status;
	}
	
	private ISSGameStatus getGameStartStatus(StringTokenizer token) {

		ISSGameStatus status = new ISSGameStatus();
		
		status.setGameNo(Integer.parseInt(token.nextToken()));
		status.putPlayerName(Player.FORE_HAND, token.nextToken());
		status.putPlayerTime(Player.FORE_HAND, new Double(token.nextToken()));
		status.putPlayerName(Player.MIDDLE_HAND, token.nextToken());
		status.putPlayerTime(Player.MIDDLE_HAND, new Double(token.nextToken()));
		status.putPlayerName(Player.HIND_HAND, token.nextToken());
		status.putPlayerTime(Player.HIND_HAND, new Double(token.nextToken()));
		
		return status;
	}
	
	private ISSMoveInformation getMoveInformation(StringTokenizer token) {
		
		ISSMoveInformation info = new ISSMoveInformation();
		
		String movePlayer = token.nextToken();
		log.debug("Move player: " + movePlayer);
		if (movePlayer.equals("w")) {
			// world move
			info.setPosition(MovePlayer.WORLD);
		}
		else if (movePlayer.equals("0")) {
			// fore hand move
			info.setPosition(MovePlayer.FORE_HAND);
		}
		else if (movePlayer.equals("1")) {
			// middle hand move
			info.setPosition(MovePlayer.MIDDLE_HAND);
		}
		else if (movePlayer.equals("2")) {
			// hind hand move
			info.setPosition(MovePlayer.HIND_HAND);
		}
		
		String move = token.nextToken();
		log.debug("Move: " + move);
		if (move.equals("y")) {
			// holding bid move
			info.setType(MoveType.BID);
		}
		else if (move.equals("p")) {
			// pass move
			info.setType(MoveType.PASS);
		}
		else if (move.equals("s")) {
			// skat request move
			info.setType(MoveType.SKAT_REQUEST);
		}
		else if (move.equals("??.??")) {
			// hidden skat given to a player
			info.setType(MoveType.SKAT_LOOKING);
		}
		else {
			// extensive parsing needed
			
			// test card move
			Card card = Card.getCardFromString(move);
			if (card != null) {
				// card play move
				info.setType(MoveType.CARD_PLAY);
				info.setCard(card);
			}
			else {
				// card parsing failed
				if (move.length() == 96) {
					// card dealing
					info.setType(MoveType.DEAL);
					// TODO parse cards
				}
				else if (move.length() == 5) {
					// open skat given to a player
					info.setType(MoveType.SKAT_LOOKING);
					// TODO parse cards
				}
				else if (move.length() < 5) {
					// bidding
					info.setType(MoveType.BID);
					info.setBidValue(Integer.parseInt(move));
				}
				else {
					// game announcement
					info.setType(MoveType.GAME_ANNOUNCEMENT);
					/*
					<game-type> :: (G | C | S | H | D | N)  (type Grand .. Null)
		               [O]        (ouvert)
		               [H]        (hand, not given if O + trump game)
		               [S]        (schneider announced, only in H games, not if O or Z)
		               [Z]        (schwarz announced, only in H games)
					*/
					StringTokenizer annToken = new StringTokenizer(move, ".");
					String gameType = annToken.nextToken();
					
					GameAnnouncement ann = new GameAnnouncement();
					
					if (gameType.startsWith("G")) {
						
						ann.setGameType(GameType.GRAND);
					}
					else if (gameType.startsWith("C")) {
						
						ann.setGameType(GameType.CLUBS);
					}
					else if (gameType.startsWith("S")) {
						
						ann.setGameType(GameType.SPADES);
					}
					else if (gameType.startsWith("H")) {
						
						ann.setGameType(GameType.HEARTS);
					}
					else if (gameType.startsWith("D")) {
						
						ann.setGameType(GameType.DIAMONDS);
					}
					else if (gameType.startsWith("N")) {
						
						ann.setGameType(GameType.NULL);
					}
					
					for (int i = 1; i < gameType.length(); i++) {
						// parse other game modifiers
						char mod = gameType.charAt(i);
						
						if (mod == 'O') {
							
							ann.setOuvert(true);
						}
						else if (mod == 'H') {
							
							// FIXME hand not setable in game announcement
						}
						else if (mod == 'S') {
							
							ann.setSchneider(true);
						}
						else if (mod == 'Z') {
							
							ann.setSchwarz(true);
						}
					}
					info.setGameAnnouncement(ann);
					
					Card skatCard0 = Card.getCardFromString(annToken.nextToken());
					Card skatCard1 = Card.getCardFromString(annToken.nextToken());
					
					info.setSkatCards(skatCard0, skatCard1);
					
					while(annToken.hasMoreTokens()) {
						// ouvert cards
						info.addOuvertCard(Card.getCardFromString(annToken.nextToken()));
					}
				}
			}
		}
		
		// parse player times
		info.putPlayerTime(Player.FORE_HAND, new Double(token.nextToken()));
		info.putPlayerTime(Player.MIDDLE_HAND, new Double(token.nextToken()));
		info.putPlayerTime(Player.HIND_HAND, new Double(token.nextToken()));
		
		return info;
	}
	
	private ISSPlayerStatus parsePlayerStatus(StringTokenizer token) {
		
		ISSPlayerStatus status = new ISSPlayerStatus();
		
		// skip player name
		token.nextToken();
		status.setIP(token.nextToken());
		status.setGamesPlayed(Integer.parseInt(token.nextToken()));
		status.setGamesWon(Integer.parseInt(token.nextToken()));
		status.setLastGameResult(Integer.parseInt(token.nextToken()));
		status.setTotalPoints(Integer.parseInt(token.nextToken()));
		status.setSwitch34(Integer.parseInt(token.nextToken()) == 1);
		token.nextToken(); // ignore next information, unknown purpose
		status.setTalkEnabled(Integer.parseInt(token.nextToken()) == 1);
		status.setReadyToPlay(Integer.parseInt(token.nextToken()) == 1);
		
		return status;
	}
	
	private void skipTokens(StringTokenizer token, int skipCount) {
		
		for (int i = 0; i < skipCount; i++) {
			
			token.nextToken();
		}
	}

	/**
	 * Handles a table list message
	 * 
	 * @param Table information
	 */
	private void handleTableListMessage(StringTokenizer token) {

		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) { //$NON-NLS-1$
			
			updateTableList(token);
		}
		else if (plusMinus.equals("-")) { //$NON-NLS-1$
			
			removeTableFromList(token);
		}
	}

	/**
	 * Adds or updates a table on the table list
	 * 
	 * @param token Table information
	 */
	private void updateTableList(StringTokenizer token) {
		
		String tableName = token.nextToken();
		int maxPlayers = Integer.parseInt(token.nextToken());
		long gamesPlayed = Long.parseLong(token.nextToken());
		String player1 = token.nextToken();
		String player2 = token.nextToken();
		String player3 = token.nextToken();
		
		this.issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
	}
	
	/**
	 * Removes a table from the table list
	 * 
	 * @param token Table information
	 */
	private void removeTableFromList(StringTokenizer token) {
		
		this.issControl.removeISSTableFromList(token.nextToken());
	}
	
	/**
	 * Handles a client list message
	 * 
	 * @param token Client information
	 */
	private void handleClientListMessage(StringTokenizer token) {
		
		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) { //$NON-NLS-1$
			
			updatePlayerList(token);
		}
		else if (plusMinus.equals("-")) { //$NON-NLS-1$
			
			removeClientFromList(token);
		}
	}

	/**
	 * Adds or updates a player on the client list
	 * 
	 * @param token Player information
	 */
	private void updatePlayerList(StringTokenizer token) {
		
		String playerName = token.nextToken();
		// ignore next token
		token.nextToken();
		String language = token.nextToken();
		long gamesPlayed = Long.parseLong(token.nextToken());
		double strength = Double.parseDouble(token.nextToken());
		// ignore rest of the tokens
		
		this.issControl.updateISSPlayerList(playerName, language, gamesPlayed, strength);
	}
	
	/**
	 * Removes a player from the client list
	 * 
	 * @param token Player information
	 */
	private void removeClientFromList(StringTokenizer token) {
		
		this.issControl.removeISSPlayerFromList(token.nextToken());
	}
	
	/**
	 * Handles the welcome message and checks the protocol version
	 * 
	 * @param token Welcome information
	 */
	private void handleWelcomeMessage(StringTokenizer token) {
		
		while(!token.nextToken().equals("version")) {
			// search for token "version"
		}
		
		double issProtocolVersion = Double.parseDouble(token.nextToken());
		
		log.debug("iss version: " + issProtocolVersion); //$NON-NLS-1$
		log.debug("local version: " + InputChannel.protocolVersion); //$NON-NLS-1$
		
		if ((int)issProtocolVersion != InputChannel.protocolVersion) {
			// TODO handle this in JSkatMaster
			log.error("Wrong protocol version!!!"); //$NON-NLS-1$
		}
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
                	handleMessage(line);
                } catch (IOException ioe) {
                    InputChannel.this.done = true;
                }
            }

            synchronized(InputChannel.this.lock) {
                InputChannel.this.lock.notify( );
            }
        }
    }
}