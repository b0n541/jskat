/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.ai.IJSkatPlayer;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.util.NeuralNetwork;
import de.jskat.control.SkatGame;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.NullView;
import de.jskat.util.Card;
import de.jskat.util.CardDeck;
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
	 * @param newPlayerName
	 *            Player's name
	 */
	public AIPlayerNN(String newPlayerName) {

		log.debug("Constructing new AIPlayerNN"); //$NON-NLS-1$
		setPlayerName(newPlayerName);

		allInputs = new ArrayList<double[]>();

		rand = new Random();
	}

	/**
	 * @see IJSkatPlayer#isAIPlayer()
	 */
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see IJSkatPlayer#bidMore(int)
	 */
	public int bidMore(int nextBidValue) {
		// TODO replace random bidding
		int result = -1;

		if (rand.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#holdBid(int)
	 */
	public boolean holdBid(int currBidValue) {
		// TODO replace random bidding
		return rand.nextBoolean();
	}

	/**
	 * @see IJSkatPlayer#announceGame()
	 */
	public GameAnnouncement announceGame() {
		// TODO replace random game announcing
		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FORE_HAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLE_HAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.HIND_HAND)); //$NON-NLS-1$

		GameAnnouncement newGame = new GameAnnouncement();

		newGame.setGameType(getBestGameType());

		// FIXME (jan 17.01.2011) setting ouvert and schneider/schwarz
		// newGame.setOuvert(rand.nextBoolean());

		log.info("Announcing: " + newGame);

		return newGame;
	}

	private GameType getBestGameType() {

		Map<GameType, Integer> wonGames = new HashMap<GameType, Integer>();
		GameType bestGameType = null;
		int highestWonGames = -1;

		for (GameType gameType : GameType.values()) {

			if (gameType != GameType.RAMSCH && gameType != GameType.PASSED_IN) {

				log.debug("Getting number of won games for game type: "
						+ gameType);

				int currWonGames = simulateGames(gameType, 100);
				wonGames.put(gameType, currWonGames);

				log.debug("Number of won games is: " + currWonGames);

				if (currWonGames > highestWonGames) {

					log.debug("Found new highest number of won games "
							+ currWonGames + " for game type " + gameType);

					highestWonGames = currWonGames;
					bestGameType = gameType;
				}
			}
		}

		if (bestGameType == null) {

			log.error("No best game type found. Announcing grand!!!");
			bestGameType = GameType.GRAND;
		} else {
			log.info(wonGames);
		}

		return bestGameType;
	}

	private int simulateGames(GameType gameType, int episodes) {

		int wonGames = 0;

		for (int i = 0; i < episodes; i++) {

			if (simulateGame(gameType)) {
				wonGames++;
			}
		}

		return wonGames;
	}

	private boolean simulateGame(GameType gameType) {

		AIPlayerNN nnPlayer1 = new AIPlayerNN();
		nnPlayer1.setIsLearning(false);
		AIPlayerNN nnPlayer2 = new AIPlayerNN();
		nnPlayer2.setIsLearning(false);
		AIPlayerNN nnPlayer3 = new AIPlayerNN();
		nnPlayer3.setIsLearning(false);

		SkatGame game = new SkatGame(null, nnPlayer1, nnPlayer2, nnPlayer3);
		game.setView(new NullView());
		game.setMaxSleep(0);

		CardDeck deck = CardDeckSimulator.simulateUnknownCards(
				knowledge.getPlayerPosition(), cards);
		log.debug("Card deck: " + deck); //$NON-NLS-1$
		game.setCardDeck(deck);
		game.dealCards();

		game.setSinglePlayer(knowledge.getPlayerPosition());

		GameAnnouncement ann = new GameAnnouncement();
		ann.setGameType(gameType);
		game.setGameAnnouncement(ann);

		game.setGameState(GameState.TRICK_PLAYING);

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return game.isGameWon();
	}

	/**
	 * @see IJSkatPlayer#lookIntoSkat()
	 */
	public boolean lookIntoSkat() {
		// TODO replace random skat looking
		return rand.nextBoolean();
	}

	/**
	 * @see IJSkatPlayer#discardSkat()
	 */
	public CardList discardSkat() {
		// TODO replace random discarding
		CardList result = new CardList();

		log.debug("Player cards before discarding: " + cards); //$NON-NLS-1$

		// just discard two random cards
		result.add(cards.remove(rand.nextInt(cards.size())));
		result.add(cards.remove(rand.nextInt(cards.size())));

		log.debug("Player cards after discarding: " + cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see de.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// CHECK Auto-generated method stub

	}

	/**
	 * @see IJSkatPlayer#playCard()
	 */
	public Card playCard() {

		int bestCardIndex = -1;

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		if (possibleCards.size() == 1) {

			bestCardIndex = 0;
		} else {
			// FIXME (jan 22.12.2010) looks wrong
			SkatNetworks.instance();
			NeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGame()
					.getGameType(), isDeclarer());

			double highestOutput = -2.0d;
			double[] bestInputs = new double[96];
			for (Card card : possibleCards) {

				log.debug("Testing card " + card); //$NON-NLS-1$

				double[] currInputs = getNetInputs(card);
				// net.setInputParameter(currInputs);
				// net.propagateForward();
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
			allInputs.add(bestInputs);
		}

		if (bestCardIndex == -1) {
			// then choose a random one
			bestCardIndex = rand.nextInt(possibleCards.size());
			log.error("RANDOM CARD CHOOSEN"); //$NON-NLS-1$
		}

		log.debug("choosing card " + bestCardIndex); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(bestCardIndex)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(bestCardIndex);
	}

	/**
	 * Creates the net input attributes
	 * 
	 * @param cardToPlay
	 *            Card to be played
	 * @return Net input attributes
	 */
	private double[] getNetInputs(Card cardToPlay) {

		double[] inputs = new double[96];

		Player leftOpponent = knowledge.getPlayerPosition().getLeftNeighbor();
		Player rightOpponent = knowledge.getPlayerPosition().getRightNeighbor();
		Player otherOpponent = null;

		Player declarer = knowledge.getDeclarer();

		if (!isDeclarer()) {

			if (declarer.getLeftNeighbor() != knowledge.getPlayerPosition()) {

				otherOpponent = declarer.getLeftNeighbor();
			} else {

				otherOpponent = declarer.getRightNeighbor();
			}
		}

		// set information for all cards
		for (Card card : knowledge.getCompleteDeck()) {

			if (isDeclarer()) {

				setDeclarerNetInputs(inputs, leftOpponent, rightOpponent, card);
			} else {

				setOpponentNetInputs(inputs, otherOpponent, declarer, card);
			}
		}

		if (cardToPlay != null) {
			// set card to play
			if (isDeclarer()) {

				inputs[64 + getNetInputIndex(knowledge.getGame().getGameType(),
						cardToPlay)] = -1.0d;
			} else {

				inputs[32 + getNetInputIndex(knowledge.getGame().getGameType(),
						cardToPlay)] = -1.0d;
			}
		}

		return inputs;
	}

	private void setDeclarerNetInputs(double[] inputs, Player leftOpponent,
			Player rightOpponent, Card card) {

		GameType gameType = knowledge.getGame().getGameType();

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			inputs[getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(leftOpponent, card)) {
			inputs[getNetInputIndex(gameType, card)] = -1.0d;
		} else if (knowledge.isCardPlayedBy(leftOpponent, card)) {
			inputs[getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[getNetInputIndex(gameType, card)] = 0.0d;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(gameType, card)] = -1.0d;
		} else if (knowledge.isCardPlayedBy(rightOpponent, card)) {
			inputs[32 + getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[32 + getNetInputIndex(gameType, card)] = 0.0d;
		}

		// inputs for player
		if (knowledge.couldHaveCard(knowledge.getPlayerPosition(), card)) {
			inputs[64 + getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge
				.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[64 + getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[64 + getNetInputIndex(gameType, card)] = 0.0d;
		}
	}

	private void setOpponentNetInputs(double[] inputs, Player otherOpponent,
			Player declarer, Card card) {

		GameType gameType = knowledge.getGame().getGameType();

		// inputs for other opponent
		if (knowledge.couldHaveCard(otherOpponent, card)) {
			// inputs[card.getIndex()] = 0.5d;
			inputs[getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(otherOpponent, card)) {
			inputs[getNetInputIndex(gameType, card)] = -1.0d;
		} else if (knowledge.isCardPlayedBy(otherOpponent, card)) {
			inputs[getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[getNetInputIndex(gameType, card)] = 0.0d;
		}

		// inputs for player
		if (knowledge.couldHaveCard(knowledge.getPlayerPosition(), card)) {
			inputs[32 + getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge
				.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[32 + getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[32 + getNetInputIndex(gameType, card)] = 0.0d;
		}

		// inputs for single player
		if (knowledge.couldHaveCard(declarer, card)) {
			inputs[64 + getNetInputIndex(gameType, card)] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(declarer, card)) {
			inputs[64 + getNetInputIndex(gameType, card)] = -1.0d;
		} else if (knowledge.isCardPlayedBy(declarer, card)) {
			inputs[64 + getNetInputIndex(gameType, card)] = -0.5d;
		} else {
			inputs[64 + getNetInputIndex(gameType, card)] = 0.0d;
		}
	}

	/**
	 * Returns the index for a card according a game type
	 * 
	 * @param gameType
	 *            Game type
	 * @return Index of card for the given game type
	 */
	private static int getNetInputIndex(GameType gameType, Card card) {

		int result = 0;

		if (gameType == GameType.NULL) {

			result = getNetInputIndexNullGame(card);
		} else {

			result = getNetInputIndexSuitGrandRamschGame(gameType, card);
		}

		return result;
	}

	private static int getNetInputIndexSuitGrandRamschGame(GameType gameType,
			Card card) {

		int result = -1;

		if (card.getRank() == Rank.JACK) {

			result = getNetInputIndexJack(card.getSuit());
		} else {

			if (gameType == GameType.GRAND) {

				result = getNetInputIndexGrandGame(card);
			} else if (gameType == GameType.RAMSCH) {

				result = getNetInputIndexRamschGame(card);
			} else {

				result = getNetInputIndexSuitGame(gameType, card);
			}
		}

		return result;
	}

	private static int getNetInputIndexJack(Suit jackSuit) {

		int result = -1;

		switch (jackSuit) {
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
		} else {
			// TODO better order cards after frequency or points
			// normal suit ordering after all trump cards
			if (card.getSuit().getSuitOrder() > trump.getSuitOrder()) {

				result = 4 + 7 + (card.getSuit().getSuitOrder() - 1) * 7
						+ card.getSuitGrandOrder();
			} else {

				result = 4 + 7 + card.getSuit().getSuitOrder() * 7
						+ card.getSuitGrandOrder();
			}
		}

		return result;
	}

	/**
	 * @see de.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		allInputs.clear();
	}

	/**
	 * @see de.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {

		if (isLearning) {
			// adjust neural networks
			// from last trick to first trick
			for (int i = allInputs.size() - 1; i > -1; i--) {

				adjustNeuralNetworks(allInputs.get(i));
			}
		}
	}

	private void adjustNeuralNetworks(double[] input) {

		double output = 0.0d;
		if (isDeclarer()) {
			if (gameWon) {
				output = 1.0d;
			} else {
				output = -1.0d;
			}
		} else {
			if (gameWon) {
				output = -1.0d;
			} else {
				output = 1.0d;
			}
		}
		double[] outputParam = { output };

		NeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGame()
				.getGameType(), isDeclarer());
		net.adjustWeights(input, outputParam);

		log.debug("Neural network: avg diff: " + net.getAvgDiff()); //$NON-NLS-1$
	}

	/**
	 * Sets the player into learning mode
	 * 
	 * @param newIsLearning
	 *            TRUE if the player should learn during play
	 */
	public void setIsLearning(boolean newIsLearning) {

		isLearning = newIsLearning;
	}

}
