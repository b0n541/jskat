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
import jskat.share.SkatRules;

import org.apache.log4j.Logger;

/**
 * A skat game
 * 
 * @author Jan Schäfer <jan.schaefer@b0n514.net>
 */
public class SkatGameData extends Observable {

	static Logger log = Logger.getLogger(jskat.data.SkatGameData.class);

	/** Creates a new instance of a new game */
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

	/** Returns the result of the game */
	public int getGameResult() {

		/*
		 * if (result < 0) { throw new IllegalStateException("Cannot return
		 * result - call calcResult() first."); }
		 */

		if (result == 0) {
			log.warn("Game result hasn't been calculated yet!");
			result = SkatRules.getResult(this);
		}

		return result;
	}

	/** Sets the result of the game manually (for overbidded games) */
	public void setGameResult(int result) {

		this.result = result;
	}

	/** Returns the single player of the game */
	public int getSinglePlayer() {

		return singlePlayer;
	}

	/** Set the single player of the game */
	public void setSinglePlayer(int singlePlayer) {

		log.debug("Current Single Player " + singlePlayer);

		this.singlePlayer = singlePlayer;
	}

	/** Returns the bid value of the game */
	public int getBidValue() {

		return bidValue;
	}

	/** Set the bid value of the game */
	public void setBidValue(int value) {

		this.bidValue = value;
		log.debug("setBidValue(" + value + ")");
	}

	/** Returns the type of the game */
	public int getGameType() {

		return gameType;
	}

	/** Set the type of the game */
	public void setGameType(int gameType) {

		this.gameType = gameType;
	}

	/** Returns the type of the game */
	public int getTrump() {

		return trump;
	}

	/** Set the type of the game */
	public void setTrump(int trump) {

		this.trump = trump;
	}

	/**
	 * Gets whether the game was lost or not
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGameLost() {

		return gameLost;
	}

	/**
	 * Sets whether the game was lost or not
	 * 
	 * @param gameLost
	 *            TRUE if the game was lost
	 */
	public void setGameLost(boolean gameLost) {

		this.gameLost = gameLost;

		log.debug("setGameLost(): Game lost = " + gameLost);
	}

	/**
	 * Returns TRUE if the player has overbidded
	 */
	public boolean getOverBidded() {

		// TODO This should not be possible when a Null or Ramsch game is played
		// maybe throw an exception instead?
		if (gameType == SkatConstants.RAMSCH || gameType == SkatConstants.RAMSCHGRAND) {
			log.warn("Overbidding cannot happen in Ramsch games: gameType="+gameType);
		}
		return overBidded;
	}

	/**
	 * Set TRUE if the player has overbidded
	 */
	public void setOverBidded(boolean overBidded) {

		this.overBidded = overBidded;
	}

	/** Returns TRUE if the player had the club jack */
	public boolean getClubJack() {

		return clubJack;
	}

	/** Set TRUE if the player had the club jack */
	public void setClubJack(boolean clubJack) {

		this.clubJack = clubJack;
	}

	/** Returns TRUE if the player had the spade jack */
	public boolean getSpadeJack() {

		return spadeJack;
	}

	/** Set TRUE if the player had the spade jack */
	public void setSpadeJack(boolean spadeJack) {

		this.spadeJack = spadeJack;
	}

	/** Returns TRUE if the player had the heart jack */
	public boolean getHeartJack() {

		return heartJack;
	}

	/** Set TRUE if the player had the heart jack */
	public void setHeartJack(boolean heartJack) {

		this.heartJack = heartJack;
	}

	/** Returns TRUE if the player had the diamond jack */
	public boolean getDiamondJack() {

		return diamondJack;
	}

	/** Set TRUE if the player had the diamond jack */
	public void setDiamondJack(boolean diamondJack) {

		this.diamondJack = diamondJack;
	}

	/**
	 * Gets whether the single player has played a hand game or not
	 * 
	 * @return TRUE if the single player has played a hand game
	 */
	public boolean isHand() {

		return hand;
	}

	/**
	 * Set TRUE if the single player has played a hand game
	 * 
	 * @param hand
	 *            TRUE if the single player has played a hand game
	 */
	public void setHand(boolean hand) {

		this.hand = hand;
	}

	/**
	 * Gets whether the single player has played an ouvert game or not
	 * 
	 * @return TRUE if the single player has played an ouvert game
	 */
	public boolean isOuvert() {

		return ouvert;
	}

	/**
	 * Sets whether the single player has played an ouvert game or not
	 * 
	 * @param ouvert
	 *            TRUE if the single player has played an ouvert game
	 */
	public void setOuvert(boolean ouvert) {

		this.ouvert = ouvert;
	}

	/**
	 * Gets whether one party played schneider or not
	 * 
	 * @return TRUE if the single player or the opponents played schneider
	 */
	public boolean isSchneider() {

		return schneider;
	}

	/**
	 * Sets whether one party played schneider or not
	 * 
	 * @param schneider
	 *            TRUE if the single player or the opponents played schneider
	 */
	public void setSchneider(boolean schneider) {

		this.schneider = schneider;
	}

	/**
	 * Gets whether schneider was announced or not
	 * 
	 * @return TRUE if Schneider was announced
	 */
	public boolean isSchneiderAnnounced() {

		return schneiderAnnounced;
	}

	/**
	 * Sets whether schneider was announced or not
	 * 
	 * @param schneiderAnnounced
	 *            TRUE if Schneider was announced
	 */
	public void setSchneiderAnnounced(boolean schneiderAnnounced) {

		this.schneiderAnnounced = schneiderAnnounced;
	}

	/**
	 * Gets whether schwarz was played or not
	 * 
	 * @return TRUE if the player or the opponents played schwarz
	 */
	public boolean isSchwarz() {

		return schwarz;
	}

	/**
	 * Sets whether schwarz was played or not
	 * 
	 * @param schwarz
	 *            TRUE if the player or the opponents played schwarz
	 */
	public void setSchwarz(boolean schwarz) {

		this.schwarz = schwarz;
	}

	/**
	 * Gets whether schwarz was announced or not
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarzAnnounced() {

		return schwarzAnnounced;
	}

	/**
	 * Sets whether schwarz was announced or not
	 * 
	 * @param schwarzAnnounced
	 *            TRUE if schwarz was announced
	 */
	public void setSchwarzAnnounced(boolean schwarzAnnounced) {

		this.schwarzAnnounced = schwarzAnnounced;
	}

	/**
	 * Gets whether contra was announced or not
	 * 
	 * @return TRUE if contra was announced
	 */
	public boolean isContra() {

		return contra;
	}

	/**
	 * Sets whether contra was announced or not
	 * 
	 * @param contra
	 *            TRUE if contra was announced
	 */
	public void setContra(boolean contra) {

		this.contra = contra;
	}

	/**
	 * Gets whether re was announced or not
	 * 
	 * @return TRUE if re was announced
	 */
	public boolean isRe() {

		return re;
	}

	/**
	 * Sets whether re was announced or not
	 * 
	 * @param re
	 *            TRUE if re was announced
	 */
	public void setRe(boolean re) {

		this.re = re;
	}

	/**
	 * Gets whether bock was announced or not
	 * 
	 * @return TRUE if bock was announced
	 */
	public boolean isBock() {

		return bock;
	}

	/**
	 * Sets whether bock was announced or not
	 * 
	 * @param bock
	 *            TRUE if bock was announced
	 */
	public void setBock(boolean bock) {

		this.bock = bock;
	}

	/**
	 * Gets whether a durchmarsch was done in a ramsch game or not
	 * 
	 * @return TRUE if someone did a durchmarsch in a ramsch game
	 */
	public boolean isDurchMarsch() {

		return durchMarsch;
	}

	/**
	 * Sets a durchmarsch was done in a ramsch game or not
	 * 
	 * @param durchMarsch
	 *            TRUE if someone did a durchmarsch in a ramsch game
	 */
	public void setDurchMarsch(boolean durchMarsch) {

		this.durchMarsch = durchMarsch;
	}

	/**
	 * Gets whether someone was jungfrau in a ramsch game or not
	 * 
	 * @return TRUE if someone was jungfrau in a ramsch game
	 */
	public boolean isJungFrau() {

		return jungFrau;
	}

	/**
	 * Sets whether someone was jungfrau in a ramsch game or not
	 * 
	 * @param jungFrau
	 *            TRUE if someone was jungfrau in a ramsch game
	 */
	public void setJungFrau(boolean jungFrau) {

		this.jungFrau = jungFrau;
	}

	/**
	 * Adds the value of a trick to the score of a player
	 * 
	 * @param player
	 *            The ID of the player
	 * @param trickValue
	 *            The value of the trick
	 */
	public void addToScore(int player, int trickValue) {

		if (player < 0 || player > 2)
			return;

		playerScores[player] = playerScores[player] + trickValue;
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

			score = playerScores[player];
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

		if (singlePlayer > -1) {

			score = getScore(singlePlayer);
		}

		return score;
	}

	/**
	 * @return Score The score of the opponent player
	 */
	public int getOpponentScore() {

		if (singlePlayer < 0)
			return 0;
		else
			return getScore((singlePlayer + 1) % 3)
					+ getScore((singlePlayer + 2) % 3);
	}

	public void calcResult() {
		result = SkatRules.getResult(this);
	}

	public void finishRamschGame() {
		int ramschLoser = -1;

		if (playerScores[0] > playerScores[1]) {
			if (playerScores[0] > playerScores[2]) {
				ramschLoser = 0;
			} else {
				ramschLoser = 2;
			}
		} else {
			if (playerScores[1] > playerScores[2]) {
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
	 * 
	 */
	public CardVector getPlayerCards(int playerID) {
		
		return playerHands.get(playerID);
	}
	
	/**
	 * 
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
	 * Sets the first forehand for the game
	 * 
	 * @param firstForeHand
	 *            The player ID of the first forehand
	 */
	public void setDealer(int firstForeHand) {

		// TODO is this correct: dealer == firstForeHand? (js:2007-06-08)
		this.dealer = firstForeHand;
	}

	/**
	 * Gets the first forehand for the game
	 * 
	 * @return The player ID of the first forehand
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
	 * 
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
	 * Sets the game annoucement
	 * 
	 * @param announcement The game annoucement
	 */
	public void setAnnouncement(GameAnnouncement announcement) {
		
		this.announcement = announcement;
	}
	
	/**
	 * Gets the game annoucement
	 * 
	 * @return The game annoucement
	 */
	public GameAnnouncement getAnnoucement() {
		
		return announcement;
	}
	
	private GameAnnouncement announcement;
	
	private JSkatPlayer[] players;

	private int singlePlayer = -1;

	private int dealer = -1;

	private int[] playerScores = { 0, 0, 0 };

	private int result = 0;

	private int bidValue = -1;

	private int gameType = -1;

	private int trump = -1;

	private boolean gameLost = false;

	private boolean overBidded = false;

	private boolean clubJack = false;

	private boolean spadeJack = false;

	private boolean heartJack = false;

	private boolean diamondJack = false;

	private boolean hand = false;

	private boolean ouvert = false;

	private boolean schneider = false;

	private boolean schneiderAnnounced = false;

	private boolean schwarz = false;

	private boolean schwarzAnnounced = false;

	private boolean contra = false;

	private boolean re = false;

	private boolean bock = false;

	private boolean durchMarsch = false;

	private boolean jungFrau = false;

	private int geschoben = 0;

	private int skatOwner = -1;

	private Vector<Trick> tricks;

	private Vector<CardVector> playerHands;

	private CardVector skat;

	/** Holds all cards dealt to the skat players */
	private Vector<CardVector> dealtCards = new Vector<CardVector>();
}
