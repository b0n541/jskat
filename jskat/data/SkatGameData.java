/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;
import java.util.Vector;

import jskat.player.JSkatPlayer;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

import org.apache.log4j.Logger;

/**
 * Data class for a Skat game
 */
public class SkatGameData extends Observable {

	static Logger log = Logger.getLogger(jskat.data.SkatGameData.class);

	/** 
	 * Creates a new instance of a Skat game 
	 *
	 */
	public SkatGameData() {

		result = -1;
		tricks = new Vector<Trick>();

		playerHands = new Vector<CardVector>();
		for (int i = 0; i < 3; i++) {

			playerHands.add(new CardVector());
		}

		skat = new CardVector();

		dealtCards = new Vector<CardVector>();

		for (int i = 0; i < 4; i++) {

			dealtCards.add(new CardVector());
		}

		log.debug("Game created");
	}

	/** 
	 * Returns the result of the game 
	 *
	 * @return Value of the game
	 */
	public int getGameResult() {

		if (result == 0 && gameType != SkatConstants.GameTypes.PASSED_IN) {
			
			log.warn("Game result hasn't been calculated yet!");
			calcResult();
		}

		return result;
	}

	/** 
	 * Sets the result of the game manually (for overbidded games) 
	 *
	 * @deprecated
	 * 
	 * @param result Result of the game
	 */
	public void setGameResult(int result) {

		this.result = result;
	}

	/** 
	 * Returns the single player of the game 
	 *
	 * @return Player ID of the single player
	 */
	public int getSinglePlayer() {

		return declarer;
	}

	/** 
	 * Set the single player of the game 
	 *
	 * @param singlePlayer Player ID of the single player
	 */
	public void setSinglePlayer(int singlePlayer) {

		log.debug("Current Single Player " + singlePlayer);

		this.declarer = singlePlayer;
	}

	/** 
	 * Returns the highest bid value of the game 
	 *
	 * @return Highest bid value
	 */
	public int getBidValue() {

		return highestBidValue;
	}

	/** 
	 * Sets the highest bid value of the game 
	 * 
	 * @param value Highest bid value
	 */
	public void setBidValue(int value) {

		this.highestBidValue = value;
		log.debug("setBidValue(" + value + ")");
	}

	/** 
	 * Returns game type
	 * 
	 * @return Game type
	 */
	public SkatConstants.GameTypes getGameType() {

		return gameType;
	}

	/** 
	 * Set the type of the game
	 * 
	 * @param gameType Game type
	 */
	public void setGameType(SkatConstants.GameTypes gameType) {

		this.gameType = gameType;
		
		rules = SkatRulesFactory.getSkatRules(gameType);
		
		if (gameType == SkatConstants.GameTypes.PASSED_IN) {
			// a passed in game has no value
			result = 0;
		}
	}

	/** 
	 * Returns the trump suit 
	 * 
	 * @return Trump suit 
	 */
	public SkatConstants.Suits getTrump() {

		return trump;
	}

	/** 
	 * Sets the trump suit
	 * 
	 * @param newTrump Trump suit
	 */
	public void setTrump(SkatConstants.Suits newTrump) {

		trump = newTrump;
	}

	/**
	 * Gets whether the game was lost or not
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGameLost() {

		return lost;
	}

	/**
	 * Sets whether the game was lost or not
	 * 
	 * @param gameLost
	 *            TRUE if the game was lost
	 */
	public void setGameLost(boolean gameLost) {

		this.lost = gameLost;

		log.debug("setGameLost(): Game lost = " + gameLost);
	}

	/**
	 * Checks whether the single player overbidded
	 * 
	 * @return TRUE if the single player overbidded
	 */
	public boolean getOverBidded() {

		// TODO This should not be possible when a Null or Ramsch game is played
		// maybe throw an exception instead?
		if (gameType == SkatConstants.GameTypes.RAMSCH || 
				gameType == SkatConstants.GameTypes.RAMSCHGRAND) {
			log.warn("Overbidding cannot happen in Ramsch games: gameType="+gameType);
		}
		return overBidded;
	}

	/**
	 * Sets the flag for overbidding
	 * 
	 * @param overBidded TRUE if the single player overbidded
	 */
	public void setOverBidded(boolean overBidded) {

		this.overBidded = overBidded;
	}

	/** 
	 * Checks whether the single player had the club jack 
	 *
	 * @return TRUE if the player hat the club jack
	 */
	public boolean getClubJack() {

		return clubJack;
	}

	/**
	 * Sets the flag for the club jack
	 * 
	 * @param clubJack TRUE if the single player had the club jack
	 */
	public void setClubJack(boolean clubJack) {

		this.clubJack = clubJack;
	}

	/** 
	 * Checks whether the single player had the spade jack 
	 *
	 * @return TRUE if the player hat the spade jack
	 */
	public boolean getSpadeJack() {

		return spadeJack;
	}

	/**
	 * Sets the flag for the spade jack
	 * 
	 * @param spadeJack TRUE if the single player had the spade jack
	 */
	public void setSpadeJack(boolean spadeJack) {

		this.spadeJack = spadeJack;
	}

	/** 
	 * Checks whether the single player had the heart jack 
	 *
	 * @return TRUE if the player hat the heart jack
	 */
	public boolean getHeartJack() {

		return heartJack;
	}

	/**
	 * Sets the flag for the heart jack
	 * 
	 * @param heartJack TRUE if the single player had the heart jack
	 */
	public void setHeartJack(boolean heartJack) {

		this.heartJack = heartJack;
	}

	/** 
	 * Checks whether the single player had the diamond jack 
	 *
	 * @return TRUE if the player hat the diamond jack
	 */
	public boolean getDiamondJack() {

		return diamondJack;
	}

	/**
	 * Sets the flag for the diamond jack
	 * 
	 * @param diamondJack TRUE if the single player had the diamond jack
	 */
	public void setDiamondJack(boolean diamondJack) {

		this.diamondJack = diamondJack;
	}

	/**
	 * Checks whether the single player has played a hand game
	 * 
	 * @return TRUE if the single player has played a hand game
	 */
	public boolean isHand() {

		return hand;
	}

	/**
	 * Sets the flag for hand game
	 * 
	 * @param hand
	 *            TRUE if the single player has played a hand game
	 */
	public void setHand(boolean hand) {

		this.hand = hand;
	}

	/**
	 * Checks whether the single player has played an ouvert
	 * 
	 * @return TRUE if the single player has played an ouvert game
	 */
	public boolean isOuvert() {

		return ouvert;
	}

	/**
	 * Sets the flag for ouvert game
	 * 
	 * @param ouvert
	 *            TRUE if the single player has played an ouvert game
	 */
	public void setOuvert(boolean ouvert) {

		this.ouvert = ouvert;
	}

	/**
	 * Checks whether one party played schneider
	 * 
	 * @return TRUE if the single player or the opponents played schneider
	 */
	public boolean isSchneider() {

		return schneider;
	}

	/**
	 * Sets the flag for schneider
	 * 
	 * @param schneider
	 *            TRUE if the single player or the opponents played schneider
	 */
	public void setSchneider(boolean schneider) {

		this.schneider = schneider;
	}

	/**
	 * Checks whether schneider was announced
	 * 
	 * @return TRUE if Schneider was announced
	 */
	public boolean isSchneiderAnnounced() {

		return schneiderAnnounced;
	}

	/**
	 * Sets the flag for schneider announced
	 * 
	 * @param schneiderAnnounced
	 *            TRUE if Schneider was announced
	 */
	public void setSchneiderAnnounced(boolean schneiderAnnounced) {

		this.schneiderAnnounced = schneiderAnnounced;
	}

	/**
	 * Checks whether schwarz was played
	 * 
	 * @return TRUE if the player or the opponents played schwarz
	 */
	public boolean isSchwarz() {

		return schwarz;
	}

	/**
	 * Sets the flag for schwarz
	 * 
	 * @param schwarz
	 *            TRUE if the player or the opponents played schwarz
	 */
	public void setSchwarz(boolean schwarz) {

		this.schwarz = schwarz;
	}

	/**
	 * Checks whether schwarz was announced
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarzAnnounced() {

		return schwarzAnnounced;
	}

	/**
	 * Sets the flag for schwarz announced
	 * 
	 * @param schwarzAnnounced
	 *            TRUE if schwarz was announced
	 */
	public void setSchwarzAnnounced(boolean schwarzAnnounced) {

		this.schwarzAnnounced = schwarzAnnounced;
	}

	/**
	 * Checks whether contra was announced
	 * 
	 * @return TRUE if contra was announced
	 */
	public boolean isContra() {

		return contra;
	}

	/**
	 * Sets the flag for contra announced
	 * 
	 * @param contra
	 *            TRUE if contra was announced
	 */
	public void setContra(boolean contra) {

		this.contra = contra;
	}

	/**
	 * Checks whether re was announced
	 * 
	 * @return TRUE if re was announced
	 */
	public boolean isRe() {

		return re;
	}

	/**
	 * Sets the flag for re announced
	 * 
	 * @param re
	 *            TRUE if re was announced
	 */
	public void setRe(boolean re) {

		this.re = re;
	}

	/**
	 * Checks whether bock was announced
	 * 
	 * @return TRUE if bock was announced
	 */
	public boolean isBock() {

		return bock;
	}

	/**
	 * Sets the flag for bock announced
	 * 
	 * @param bock
	 *            TRUE if bock was announced
	 */
	public void setBock(boolean bock) {

		this.bock = bock;
	}

	/**
	 * Checks whether a durchmarsch was done in a ramsch game or not
	 * 
	 * @return TRUE if someone did a durchmarsch in a ramsch game
	 */
	public boolean isDurchMarsch() {

		return durchMarsch;
	}

	/**
	 * Sets the flag for durchmarsch in a ramsch game
	 * 
	 * @param durchMarsch
	 *            TRUE if someone did a durchmarsch in a ramsch game
	 */
	public void setDurchMarsch(boolean durchMarsch) {

		this.durchMarsch = durchMarsch;
	}

	/**
	 * Checks whether someone was jungfrau in a ramsch game
	 * 
	 * @return TRUE if someone was jungfrau in a ramsch game
	 */
	public boolean isJungFrau() {

		return jungFrau;
	}

	/**
	 * Sets the flag for jungfrau in a ramsch
	 * 
	 * @param jungFrau
	 *            TRUE if someone was jungfrau in a ramsch game
	 */
	public void setJungFrau(boolean jungFrau) {

		this.jungFrau = jungFrau;
	}

	/**
	 * Adds the value of a trick to the points of a player
	 * 
	 * @param player
	 *            The ID of the player
	 * @param trickValue
	 *            The value of the trick
	 */
	public void addToPlayerPoints(int player, int trickValue) {

		if (player < 0 || player > 2) {
			
			throw new IllegalArgumentException("Illegal player ID: " + player);
		}

		playerPoints[player] += trickValue;
	}

	/**
	 * Gets the score of a player
	 * 
	 * @param player
	 *            The ID of a player
	 * @return The score of a player
	 */
	public int getScore(int player) {

		int score = 0;

		if (player > -1 || player < 3) {

			score = playerPoints[player];
		}

		return score;
	}

	/**
	 * Gets the score of the single player
	 * 
	 * @return The score of the single player
	 */
	public int getSinglePlayerScore() {

		int score = 0;

		if (declarer > -1) {

			score = getScore(declarer);
		}

		return score;
	}

	/**
	 * @return Score The score of the opponent player
	 */
	public int getOpponentScore() {

		if (declarer < 0)
			return 0;
		else
			return getScore((declarer + 1) % 3)
					+ getScore((declarer + 2) % 3);
	}

	/**
	 * Calculates the result of a game
	 */
	public void calcResult() {
		
		result = rules.getGameResult(this);
	}

	/**
	 * Calculates final results of a ramsch game 
	 */
	public void finishRamschGame() {
		
		int ramschLoser = -1;

		if (playerPoints[0] > playerPoints[1]) {
			
			if (playerPoints[0] > playerPoints[2]) {
				ramschLoser = 0;
			} else {
				ramschLoser = 2;
			}
			
		} else {
			
			if (playerPoints[1] > playerPoints[2]) {
				ramschLoser = 1;
			} else {
				ramschLoser = 2;
			}
		}
		
		setSinglePlayer(ramschLoser);
		
		if (isDurchMarsch()) {
			setGameLost(false);
		} else {
			setGameLost(true);
		}
	}
	
	/**
	 * Gets the result of a game
	 * 
	 * @return The result of a game
	 */
	public int getResult() {

		return result;
	}

	/**
	 * Adds a trick
	 * 
	 * @param foreHand
	 *            The player ID of the forehand in the trick
	 */
	public void addTrick(int foreHand) {

		tricks.add(new Trick(foreHand));
		setChanged();
		notifyObservers(tricks.lastElement());
	}

	/**
	 * Sets a trick card
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @param cardNumber
	 *            The number of the card in the trick
	 * @param card
	 *            The ID of the card that was played
	 */
	public void setTrickCard(int trickNumber, int cardNumber, Card card) {

		log.debug(this + ".setTrickCard(" + trickNumber + ", " + cardNumber
				+ ", " + card + ")");

		switch (cardNumber) {

		case 0:
			tricks.get(trickNumber).setFirstCard(card);
			break;
		case 1:
			tricks.get(trickNumber).setSecondCard(card);
			break;
		case 2:
			tricks.get(trickNumber).setThirdCard(card);
			break;
		}
		
		setChanged();
		notifyObservers(tricks.get(trickNumber));
	}

	/**
	 * Sets the trick winner
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @param winner
	 *            The player ID of the winner of the trick
	 */
	public void setTrickWinner(int trickNumber, int winner) {

		log.debug("setTrickWinner(" + trickNumber + ", " + winner + ")");
		
		tricks.get(trickNumber).setTrickWinner(winner);
	}

	/**
	 * Gets the winner of the trick
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @return The player ID of the trick winner
	 */
	public int getTrickWinner(int trickNumber) {

		return tricks.get(trickNumber).getTrickWinner();

	}

	/**
	 * Gets all tricks
	 * 
	 * @return Vector of tricks
	 */
	public Vector<Trick> getTricks() {

		return tricks;
	}

	/**
	 * Gets the number of geschoben
	 * 
	 * @return Returns the number of geschoben
	 */
	public int getGeschoben() {

		return geschoben;
	}

	/**
	 * Gets the geschoben multiplier
	 * 
	 * @return Returns the geschoben multiplier
	 */
	public int getGeschobenMultiplier() {

		log.debug("geschoben=" + geschoben + ", 2^" + geschoben + "="
				+ (1 << geschoben));

		int result = 0;

		if (geschoben < 0) {

			result = geschoben;

		} else {

			// TODO: need to know what this is doing
			result = (1 << geschoben);
		}

		return result;
	}

	/**
	 * Raises the value of geschoben by 1
	 * 
	 */
	public void geschoben() {

		geschoben++;
	}

	/**
	 * Returns the skat owner
	 * 
	 * @return The player ID of the skat owner
	 */
	public int getSkatOwner() {

		return skatOwner;
	}

	/**
	 * Sets the owner of the skat
	 * 
	 * @param skatOwner
	 *            The player ID of the skat owner to be set
	 */
	public void setSkatOwner(int skatOwner) {

		this.skatOwner = skatOwner;
	}

	/**
	 * Sets the players of the game
	 * 
	 * @param players
	 *            An array of the players
	 */
	public void setPlayers(JSkatPlayer[] players) {

		this.players = players;
	}

	/**
	 * Gets the players of the game
	 * 
	 * @return An array of the players
	 */
	public JSkatPlayer[] getPlayers() {

		return players;
	}

	/**
	 * Get the player cards
	 * 
	 * @param playerID Player ID
	 * @return Vector of Cards from the player
	 */
	public CardVector getPlayerCards(int playerID) {
		
		return playerHands.get(playerID);
	}
	
	/**
	 * Updates player cards
	 * 
	 * @param playerID Player ID
	 * @param oldSkat Old skat before the exchange of some cards
	 */
	public void updatePlayerCards(int playerID, CardVector oldSkat) {

		// "skat" is the new skat
		Card helperCard;
		helperCard = skat.getCard(0);
		if (!oldSkat.contains(helperCard)) {
			// if the card is not in the old skat, it must be from the player's hand - so remove it
			playerHands.get(playerID).remove(helperCard);
		}
		helperCard = skat.getCard(1);
		if (!oldSkat.contains(helperCard)) {
			// if the card is not in the old skat, it must be from the player's hand - so remove it
			playerHands.get(playerID).remove(helperCard);
		}
		helperCard = oldSkat.getCard(0);
		if (!playerHands.get(playerID).contains(helperCard) && !skat.contains(helperCard)) {
			// if the card from the old skat is not yet in the player's hand - put it there
			playerHands.get(playerID).add(helperCard);
		}
		helperCard = oldSkat.getCard(1);
		if (!playerHands.get(playerID).contains(helperCard) && !skat.contains(helperCard)) {
			// if the card from the old skat is not yet in the player's hand - put it there
			playerHands.get(playerID).add(helperCard);
		}

		if(playerHands.get(playerID).size() != 10 || skat.size() != 2) {
			log.error("Player hand or skat have the wrong size! playerHand:"+playerHands.get(playerID).size()+", skat:"+ skat.size());
			log.debug(""+playerHands.get(playerID));
			log.debug(""+skat);
		}
	}
	

	/**
	 * Sets the first fore hand for the game
	 * 
	 * @param firstForeHand
	 *            The player ID of the first fore hand
	 */
	public void setDealer(int firstForeHand) {

		// TODO is this correct: dealer == firstForeHand? (js:2007-06-08)
		this.dealer = firstForeHand;
	}

	/**
	 * Gets the first fore hand for the game
	 * 
	 * @return The player ID of the first fore hand
	 */
	public int getDealer() {

		return dealer;
	}

	/**
	 * Gets a reference to the skat for the game
	 * 
	 * @return skat The cards of the skat
	 */
	public CardVector getSkat() {

		return skat;
	}

	/**
	 * Clears the skat 
	 *
	 */
	public void clearSkat() {
		
		skat.clear();
	}
	
	/**
	 * Gets the dealt cards
	 * 
	 * @return The dealt cards
	 */
	public Vector<CardVector> getDealtCards() {
		
		return dealtCards;
	}
	
	/**
	 * Sets a dealt card
	 * 
	 * @param playerID Player ID of the player that got the Card
	 * @param card Card that was dealt
	 */
	public void setDealtCard(int playerID, Card card) {
		
		dealtCards.get(playerID).add(card);

		if (playerID == 0 || playerID ==1 || playerID == 2) {
			
			playerHands.get(playerID).add(card);
			
		} else if (playerID == 3) {
			
			skat.add(card);
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Overwrites the method addObserver() from the class Observable
	 * 
	 * @param newObserver
	 *            The observer class to be added
	 */
	/*
	public void addObserver(Observer newObserver) {

		if (newObserver instanceof CardHoldingPanel) {

			CardHoldingPanel panel = (CardHoldingPanel) newObserver;
			int panelType = panel.getPanelType();

			if (panelType == CardHoldingPanel.OPPONENT_PANEL
					|| panelType == CardHoldingPanel.PLAYER_PANEL) {

				playerHands.get(panel.getPlayerID()).addObserver(newObserver);

			} else if (panelType == CardHoldingPanel.SKAT_PANEL) {

				skat.addObserver(newObserver);

			} else if (panelType == CardHoldingPanel.TRICK_PANEL) {

				trick.addObserver(newObserver);
			}

			super.addObserver(newObserver);
			
		} else {

			super.addObserver(newObserver);
		}
	}
*/
	/**
	 * Sets the game announcement
	 * 
	 * @param announcement The game announcement
	 */
	public void setAnnouncement(GameAnnouncement announcement) {
		
		this.announcement = announcement;
	}
	
	/**
	 * Gets the game announcement
	 * 
	 * @return The game announcement
	 */
	public GameAnnouncement getAnnoucement() {
		
		return announcement;
	}
	
	/**
	 * Checks whether the game was played under ISPA rules or not
	 * 
	 * @return TRUE when the game was played under ISPA rules
	 */
	public boolean isIspaRules() {
		return ispaRules;
	}

	/**
	 * Sets the flag for ISPA rules
	 * 
	 * @param ispaRules TRUE when the game was played under ISPA rules
	 */
	public void setIspaRules(boolean ispaRules) {
		this.ispaRules = ispaRules;
	}

	
	/**
	 * Flag for the Skat rules
	 */
	private boolean ispaRules = true;
	
	/**
	 * Skat rules according the game type
	 */
	private SkatRules rules;
	
	/**
	 * The game announcement made by the declarer
	 */
	private GameAnnouncement announcement;
	
	/**
	 * All players
	 */
	private JSkatPlayer[] players;

	/**
	 * Declarer player
	 */
	private int declarer = -1;

	/**
	 * Dealer of the cards
	 */
	private int dealer = -1;

	/**
	 * Points the player made during the game
	 */
	private int[] playerPoints = { 0, 0, 0 };

	/**
	 * Game result
	 */
	private int result = 0;

	/**
	 * Highest bid value made during bidding
	 */
	private int highestBidValue = -1;

	/**
	 * Bids the players made during bidding
	 */
	private int[] playerBids = { 0, 0, 0 };
	
	/**
	 * Type of the game
	 */
	private SkatConstants.GameTypes gameType;

	/**
	 * Trump color in suit games
	 */
	private SkatConstants.Suits trump;

	/**
	 * Flag for lost games
	 */
	private boolean lost = false;

	/**
	 * Flag for an over bidded game
	 */
	private boolean overBidded = false;

	/**
	 * Flag for the club jack on declarers hand
	 */
	private boolean clubJack = false;

	/**
	 * Flag for the spade jack on declarers hand
	 */
	private boolean spadeJack = false;

	/**
	 * Flag for the heart jack on declarers hand
	 */
	private boolean heartJack = false;

	/**
	 * Flag for the diamond jack on declarers hand
	 */
	private boolean diamondJack = false;

	/**
	 * Flag for a hand game
	 */
	private boolean hand = false;

	/**
	 * Flag for an ouvert game
	 */
	private boolean ouvert = false;

	/**
	 * Flag for a schneider game
	 */
	private boolean schneider = false;

	/**
	 * Flag for an announced schneider game 
	 */
	private boolean schneiderAnnounced = false;

	/**
	 * Flag for a schwarz game
	 */
	private boolean schwarz = false;

	/**
	 * Flag for an announced schwarz game
	 */
	private boolean schwarzAnnounced = false;

	/**
	 * Flag for a contra announced game
	 */
	private boolean contra = false;

	/**
	 * Flag for a re announced game
	 */
	private boolean re = false;

	/**
	 * Flag for a bock announced game
	 */
	private boolean bock = false;

	/**
	 * Flag for a durchmarsch game
	 * (one player made all tricks in a ramsch game)
	 */
	private boolean durchMarsch = false;

	/**
	 * Flag for a jungfrau game
	 * (one player made no tricks in a ramsch game)
	 */
	private boolean jungFrau = false;

	/**
	 * Flag for a geschoben game
	 * (the skat was handed over from player to player at the beginning of a ramsch game)
	 */
	private int geschoben = 0;

	/**
	 * Player that owned the skat
	 */
	private int skatOwner = -1;

	/**
	 * Tricks made in the game
	 */
	private Vector<Trick> tricks;

	/**
	 * Cards on player hands
	 */
	private Vector<CardVector> playerHands;

	/**
	 * Cards in the skat
	 */
	private CardVector skat;

	/**
	 * Holds all cards dealt to the players and to the skat
	 */
	private Vector<CardVector> dealtCards = new Vector<CardVector>();
}
