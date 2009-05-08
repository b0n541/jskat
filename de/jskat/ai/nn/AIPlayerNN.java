/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.ai.JSkatPlayer;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.util.NeuralNetwork;
import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * JSkat player using neural network
 */
public class AIPlayerNN extends AbstractJSkatPlayer {

	private static Log log = LogFactory.getLog(AIPlayerNN.class);

	private Random rand;
	private List<double[]> allInputs;
	
	private boolean isLearning = false;

	/**
	 * Constructor
	 */
	public AIPlayerNN() {
		
		this("unknown"); //$NON-NLS-1$
	}
	
	/** 
	 * Creates a new instance of AIPlayerNN
	 * 
	 * @param newPlayerName Player's name
	 */
	public AIPlayerNN(String newPlayerName) {

		log.debug("Constructing new AIPlayerNN"); //$NON-NLS-1$
		setPlayerName(newPlayerName);

		this.allInputs = new ArrayList<double[]>();
		
		this.rand = new Random();
	}

	/**
	 * @see JSkatPlayer#isAIPlayer()
	 */
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see JSkatPlayer#bidMore(int)
	 */
	public int bidMore(int nextBidValue) {
		// TODO replace random bidding
		int result = -1;
		
		if (this.rand.nextBoolean()) {
			
			result = nextBidValue;
		}
		
		return result;
	}

	/**
	 * @see JSkatPlayer#holdBid(int)
	 */
	public boolean holdBid(int currBidValue) {
		// TODO replace random bidding
		return this.rand.nextBoolean();
	}
	
	private void simulateGames() {
		
		GameSimulationThread gameSimThread = new GameSimulationThread();
		gameSimThread.setPlayerPosition(this.knowledge.getPlayerPosition());
		gameSimThread.setCards(this.knowledge.getKnownCards());
		gameSimThread.start();
	}
	
	/**
	 * @see JSkatPlayer#announceGame()
	 */
	public GameAnnouncement announceGame() {
		// TODO replace random game announcing
		log.debug("position: " + this.knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + this.knowledge.getHighestBid(Player.FORE_HAND) +  //$NON-NLS-1$
					" " + this.knowledge.getHighestBid(Player.MIDDLE_HAND) +  //$NON-NLS-1$
					" " + this.knowledge.getHighestBid(Player.HIND_HAND)); //$NON-NLS-1$
		
		GameAnnouncement newGame = new GameAnnouncement();

		// select a random game type (without RAMSCH and PASSED_IN)
		newGame.setGameType(GameType.values()[this.rand.nextInt(GameType.values().length - 2)]);
		newGame.setOuvert(this.rand.nextBoolean());

		return newGame;
	}

	/**
	 * @see JSkatPlayer#lookIntoSkat()
	 */
	public boolean lookIntoSkat() {
		// TODO replace random skat looking
		return this.rand.nextBoolean();
	}

	/**
	 * @see JSkatPlayer#discardSkat()
	 */
	public CardList discardSkat() {
		// TODO replace random discarding
		CardList result = new CardList();
		
		log.debug("Player cards before discarding: " + this.cards); //$NON-NLS-1$

		// just discard two random cards
		result.add(this.cards.remove(this.rand.nextInt(this.cards.size())));
		result.add(this.cards.remove(this.rand.nextInt(this.cards.size())));

		log.debug("Player cards after discarding: " + this.cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see JSkatPlayer#playCard()
	 */
	public Card playCard() {
		
		int bestCardIndex = -1;
		
		log.debug('\n' + this.knowledge.toString());
		
		// first find all possible cards
		CardList possibleCards = getPlayableCards(this.knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards);  //$NON-NLS-1$//$NON-NLS-2$

		if (possibleCards.size() == 1) {
			
			bestCardIndex = 0;
		}
		else {
		
			SkatNetworks.getInstance();
			NeuralNetwork net = SkatNetworks.getNetwork(this.gameType, isDeclarer());
			
			double highestOutput = -2.0d;
			double[] bestInputs = new double[96];
			for (Card card : possibleCards) {
				
				log.debug("Testing card " + card); //$NON-NLS-1$
				
				double[] currInputs = getNetInputs(card);
//				net.setInputParameter(currInputs);
//				net.propagateForward();
//				log.debug("net output: " + net.getOutputParameters()); //$NON-NLS-1$
				double currOutput = net.getPredictedOutcome(currInputs);

				if (currOutput > highestOutput) {
					
					log.debug("Found higher reward: " + currOutput); //$NON-NLS-1$
					highestOutput = currOutput;
					bestCardIndex = possibleCards.getIndexOf(card);
					
					// remember best input parameters
					bestInputs = currInputs;
				}
			}
			
			// store parameters for the card to play
			// for adjustment of weights after the game
			this.allInputs.add(bestInputs);
		}
		
		if (bestCardIndex == -1) {
			// then choose a random one
			bestCardIndex  = this.rand.nextInt(possibleCards.size());
			log.error("RANDOM CARD CHOOSEN"); //$NON-NLS-1$
		}
		
		log.debug("choosing card " + bestCardIndex ); //$NON-NLS-1$
		log.debug("as player " + this.knowledge.getPlayerPosition() + ": " + possibleCards.get(bestCardIndex ));  //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(bestCardIndex);
	}
	
	/**
	 * Creates the net input attributes
	 * 
	 * @param cardToPlay Card to be played
	 * @return Net input attributes
	 */
	private double[] getNetInputs(Card cardToPlay) {
		
		double[] inputs = new double[96];
		
		Player leftOpponent = this.knowledge.getPlayerPosition().getLeftNeighbor();
		Player rightOpponent = this.knowledge.getPlayerPosition().getRightNeighbor();
		Player otherOpponent = null;
		
		if (!isDeclarer()) {
			
			if (this.singlePlayer.getLeftNeighbor() != this.knowledge.getPlayerPosition()) {
			
				otherOpponent = this.singlePlayer.getLeftNeighbor();
			}
			else {
				
				otherOpponent = this.singlePlayer.getRightNeighbor();
			}
		}
		
		// set information for all cards
		for (Card card : this.knowledge.getCompleteDeck()) {

			if (isDeclarer()) {
				
				setDeclarerNetInputs(inputs, leftOpponent, rightOpponent, card);
			}
			else {
				
				setOpponentNetInputs(inputs, otherOpponent, this.singlePlayer, card);
			}
		}
		
		// set card to play
		if (isDeclarer()) {
			
			inputs[64 + getNetInputIndex(this.gameType, cardToPlay)] = -1.0d;
		}
		else {
			
			inputs[32 + getNetInputIndex(this.gameType, cardToPlay)] = -1.0d;
		}
		
		return inputs;
	}

	private void setDeclarerNetInputs(double[] inputs, Player leftOpponent,
			Player rightOpponent, Card card) {
		
		// inputs for left opponent
		if (this.knowledge.couldHaveCard(leftOpponent, card)) {
			inputs[getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedInTrick(leftOpponent, card)) {
			inputs[getNetInputIndex(this.gameType, card)] = -1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(leftOpponent, card)) {
			inputs[getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[getNetInputIndex(this.gameType, card)] = 0.0d;
		}
		
		// inputs for right opponent
		if (this.knowledge.couldHaveCard(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedInTrick(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(this.gameType, card)] = -1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[32 + getNetInputIndex(this.gameType, card)] = 0.0d;
		}
		
		// inputs for player
		if (this.knowledge.couldHaveCard(this.knowledge.getPlayerPosition(), card)) {
			inputs[64 + getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(this.knowledge.getPlayerPosition(), card)) {
			inputs[64 + getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[64 + getNetInputIndex(this.gameType, card)] = 0.0d;
		}
	}

	private void setOpponentNetInputs(double[] inputs, Player otherOpponent,
			Player declarer, Card card) {
		
		// inputs for other opponent
		if (this.knowledge.couldHaveCard(otherOpponent, card)) {
//			inputs[card.getIndex()] = 0.5d;
			inputs[getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedInTrick(otherOpponent, card)) {
			inputs[getNetInputIndex(this.gameType, card)] = -1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(otherOpponent, card)) {
			inputs[getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[getNetInputIndex(this.gameType, card)] = 0.0d;
		}
		
		// inputs for player
		if (this.knowledge.couldHaveCard(this.knowledge.getPlayerPosition(), card)) {
			inputs[32 + getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(this.knowledge.getPlayerPosition(), card)) {
			inputs[32 + getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[32 + getNetInputIndex(this.gameType, card)] = 0.0d;
		}
		
		// inputs for single player
		if (this.knowledge.couldHaveCard(declarer, card)) {
			inputs[64 + getNetInputIndex(this.gameType, card)] = 1.0d;
		}
		else if (this.knowledge.isCardPlayedInTrick(declarer, card)) {
			inputs[64 + getNetInputIndex(this.gameType, card)] = -1.0d;
		}
		else if (this.knowledge.isCardPlayedBy(declarer, card)) {
			inputs[64 + getNetInputIndex(this.gameType, card)] = -0.5d;
		}
		else {
			inputs[64 + getNetInputIndex(this.gameType, card)] = 0.0d;
		}
	}

	/**
	 * Returns the index for a card according a game type
	 * 
	 * @param gameType Game type
	 * @return Index of card for the given game type
	 */
	private static int getNetInputIndex(GameType gameType, Card card) {
		
		int result = 0;
		
		if (gameType == GameType.NULL) {
			
			result = getNetInputIndexNullGame(card);
		}
		else {
			
			result = getNetInputIndexSuitGrandRamschGame(gameType, card);
		}
		
		return result;
	}
	
	private static int getNetInputIndexSuitGrandRamschGame(GameType gameType, Card card) {
		
		int result = -1;
		
		if (card.getRank() == Rank.JACK) {
			
			result = getNetInputIndexJack(card.getSuit());
		}
		else {
			
			if (gameType == GameType.GRAND) {
				
				result = getNetInputIndexGrandGame(card);
			}
			else if (gameType == GameType.RAMSCH){
				
				result = getNetInputIndexRamschGame(card);
			}
			else {
				
				result = getNetInputIndexSuitGame(gameType, card);
			}
		}
		
		return result;
	}
	
	private static int getNetInputIndexJack(Suit jackSuit) {
		
		int result = -1;
		
		switch(jackSuit) {
		case CLUBS:
			result = 0;
			break;
		case SPADES:
			result = 1;
			break;
		case HEARTS:
			result = 2;
			break;
		case DIAMONDS:
			result = 3;
			break;
		}
		
		return result;
	}

	private static int getNetInputIndexNullGame(Card card) {
		// TODO better order cards after frequency or points
		// normal null ordering
		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}
	
	private static int getNetInputIndexGrandGame(Card card) {
		// TODO better order cards after frequency or points
		// normal suit ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getSuitGrandOrder();
	}
	
	private static int getNetInputIndexRamschGame(Card card) {
		// TODO better order cards after frequency or points
		// ramsch ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getRamschOrder();
	}
	
	private static int getNetInputIndexSuitGame(GameType gameType, Card card) {
		
		int result = -1;
		Suit trump = gameType.getTrumpSuit();

		if (card.getSuit() == trump) {
			// trump cards after all jacks
			result = 4 + card.getSuitGrandOrder();
		}
		else {
			// TODO better order cards after frequency or points
			// normal suit ordering after all trump cards
			if (card.getSuit().getSuitOrder() > trump.getSuitOrder()) {
			
				result = 4 + 7 + (card.getSuit().getSuitOrder() - 1) * 7 + card.getSuitGrandOrder();
			}
			else {
				
				result = 4 + 7 + card.getSuit().getSuitOrder() * 7 + card.getSuitGrandOrder();
			}
		}
		
		return result;
	}

	/**
	 * @see de.jskat.ai.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		
		this.allInputs.clear();
	}

	/**
	 * @see de.jskat.ai.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		
		if (this.isLearning) {
			// adjust neural networks
			// from last trick to first trick
			for (int i = this.allInputs.size() - 1; i > -1; i--) {
				
				adjustNeuralNetworks(this.allInputs.get(i));
			}
		}
	}

	private void adjustNeuralNetworks(double[] input) {
		
		double output = 0.0d;
		if (isDeclarer()) {
			if (this.gameWon) {
				output = 1.0d;
			}
			else {
				output = -1.0d;
			}
		}
		else {
			if (this.gameWon) {
				output = -1.0d;
			}
			else {
				output = 1.0d;
			}
		}
		double[] outputParam = {output};
		
		NeuralNetwork net = SkatNetworks.getNetwork(this.gameType, isDeclarer());
		net.adjustWeights(input, outputParam);
		
		log.debug("Neural network: avg diff: " + net.getAvgDiff()); //$NON-NLS-1$
	}
	
	/**
	 * Sets the player into learning mode
	 * 
	 * @param newIsLearning TRUE if the player should learn during play
	 */
	public void setIsLearning(boolean newIsLearning) {
		
		this.isLearning = newIsLearning;
	}
}
