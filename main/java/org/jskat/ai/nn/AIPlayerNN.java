/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jskat.ai.nn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.ai.IJSkatPlayer;
import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.util.NeuralNetwork;
import org.jskat.control.SkatGame;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.NullView;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.BasicSkatRules;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * JSkat player using neural network
 */
public class AIPlayerNN extends AbstractJSkatPlayer {

	private static Log log = LogFactory.getLog(AIPlayerNN.class);

	private Random rand;
	private List<double[]> allInputs;

	private boolean isLearning = false;

	private List<GameType> feasibleGameTypes;

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

		feasibleGameTypes = new ArrayList<GameType>();
		for (GameType gameType : GameType.values()) {
			if (gameType != GameType.RAMSCH && gameType != GameType.PASSED_IN) {
				feasibleGameTypes.add(gameType);
			}
		}

		rand = new Random();
		isLearning = true;
	}

	/**
	 * @see IJSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see IJSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {

		int result = -1;

		if (isAnyGamePossible(nextBidValue)) {
			result = nextBidValue;
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {

		return isAnyGamePossible(currBidValue);
	}

	private boolean isAnyGamePossible(int bidValue) {

		boolean result = false;

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(bidValue);

		for (GameType gameType : filteredGameTypes) {

			int gamesToSimulate = 20;
			int wonGames = simulateGames(cards, gameType, gamesToSimulate);
			double wonRate = (double) wonGames / (double) gamesToSimulate;

			if (wonRate > 0.5) {

				result = true;
			}
		}
		return result;
	}

	private List<GameType> filterFeasibleGameTypes(int bidValue) {

		List<GameType> result = new ArrayList<GameType>();

		SkatGameData data = getSimulatedGameData();

		for (GameType gameType : feasibleGameTypes) {

			GameAnnouncement gameAnnouncement = new GameAnnouncement();
			gameAnnouncement.setGameType(gameType);
			data.setAnnouncement(gameAnnouncement);

			BasicSkatRules skatRules = SkatRuleFactory.getSkatRules(gameType);
			int gameResult = skatRules.calcGameResult(data);

			if (gameResult >= bidValue) {

				result.add(gameType);
			}
		}

		return result;
	}

	private SkatGameData getSimulatedGameData() {

		SkatGameData data = new SkatGameData();

		data.setGameWon(true);

		if (cards.contains(Card.CJ)) {
			data.setClubJack(true);
		}
		if (cards.contains(Card.SJ)) {
			data.setClubJack(true);
		}
		if (cards.contains(Card.HJ)) {
			data.setClubJack(true);
		}
		if (cards.contains(Card.DJ)) {
			data.setClubJack(true);
		}

		return data;
	}

	/**
	 * @see IJSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		GameAnnouncement newGame = new GameAnnouncement();
		newGame.setGameType(getBestGameType());

		// FIXME (jan 17.01.2011) setting ouvert and schneider/schwarz
		// newGame.setOuvert(rand.nextBoolean());

		log.debug("Announcing: " + newGame); //$NON-NLS-1$

		return newGame;
	}

	private GameType getBestGameType() {

		// FIXME (jan 18.01.2011) check for overbidding!
		SimulationResults simulationResults = simulateGames(cards, 20);
		GameType bestGameType = null;
		int highestWonGames = -1;

		for (GameType gameType : feasibleGameTypes) {

			int currWonGames = simulationResults.getWonGames(gameType)
					.intValue();

			if (currWonGames > highestWonGames) {

				log.debug("Found new highest number of won games " //$NON-NLS-1$
						+ currWonGames + " for game type " + gameType); //$NON-NLS-1$

				highestWonGames = currWonGames;
				bestGameType = gameType;
			}

			if (bestGameType == null) {

				log.error("No best game type found. Announcing grand!!!"); //$NON-NLS-1$
				bestGameType = GameType.GRAND;

			}
		}

		return bestGameType;
	}

	private SimulationResults simulateGames(CardList playerCards,
			int numberOfSimulations) {

		// FIXME (jan 18.01.2011) make number of simulated games time dependent
		SimulationResults result = new SimulationResults();

		// FIXME (jan 18.01.2011) parallelize this process
		for (GameType gameType : feasibleGameTypes) {

			log.debug("Getting number of won games for game type: " //$NON-NLS-1$
					+ gameType);

			int currWonGames = simulateGames(playerCards, gameType,
					numberOfSimulations);
			result.setWonGames(gameType, Integer.valueOf(currWonGames));

			log.debug("Number of won games is: " + currWonGames); //$NON-NLS-1$
		}

		return result;
	}

	private int simulateGames(CardList playerCards, GameType gameType,
			int episodes) {

		int wonGames = 0;

		for (int i = 0; i < episodes; i++) {

			if (simulateGame(playerCards, gameType)) {
				wonGames++;
			}
		}

		return wonGames;
	}

	private boolean simulateGame(CardList playerCards, GameType gameType) {

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
				knowledge.getPlayerPosition(), playerCards);
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
	 * @see IJSkatPlayer#pickUpSkat()
	 */
	@Override
	public boolean pickUpSkat() {

		boolean result = true;

		int gamesToSimulate = 20;
		SimulationResults simResult = simulateGames(cards, gamesToSimulate);

		for (GameType gameType : feasibleGameTypes) {

			int wonGames = simResult.getWonGames(gameType).intValue();
			double wonRate = (double) wonGames / (double) gamesToSimulate;

			if (wonRate > 0.9) {

				result = false;
			}
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {

		CardList result = new CardList();
		int highestNumberOfWonGames = 0;

		log.debug("Player cards before discarding: " + cards); //$NON-NLS-1$

		// check all possible discards
		for (int i = 0; i < cards.size(); i++) {

			for (int j = 0; j < cards.size() - 1; j++) {

				CardList simCards = new CardList();
				simCards.addAll(cards);

				CardList currSkat = new CardList();
				currSkat.add(simCards.remove(i));
				currSkat.add(simCards.remove(j));

				for (GameType gameType : filterFeasibleGameTypes(knowledge
						.getHighestBid(knowledge.getPlayerPosition()))) {

					int wonGames = simulateGames(simCards, gameType, 3);

					if (wonGames > highestNumberOfWonGames) {

						highestNumberOfWonGames = wonGames;
						result.clear();
						result.addAll(currSkat);
					}
				}
			}
		}

		if (result.size() != 2) {

			log.error("Did not found cards for discarding!!!"); //$NON-NLS-1$
			result.clear();
			result.add(cards.remove(rand.nextInt(cards.size())));
			result.add(cards.remove(rand.nextInt(cards.size())));
		} else {
			cards.remove(result.get(0));
			cards.remove(result.get(1));
		}

		log.debug("Player cards after discarding: " + cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see org.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// CHECK Auto-generated method stub

	}

	/**
	 * @see IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {

		int bestCardIndex = -1;

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		if (possibleCards.size() == 1) {

			bestCardIndex = 0;
		} else {
			// FIXME (jan 22.12.2010) looks wrong, neural networks only say yes
			// or no, output parameter value of 0.99 shows not a better card
			// than than 0.95
			// better solution: simulate games from this point on
			SkatNetworks.instance();
			NeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGame()
					.getGameType(), isDeclarer());

			double highestOutput = -2.0d;
			double[] bestInputs = new double[96];
			for (Card card : possibleCards) {

				log.debug("Testing card " + card); //$NON-NLS-1$

				double[] currInputs = getNetInputs(card);
				double currOutput = net.getPredictedOutcome(currInputs);
				log.debug("net output: " + currOutput); //$NON-NLS-1$

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
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			inputs[netInputIndexForCard] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(leftOpponent, card)) {
			inputs[netInputIndexForCard] = -1.0d;
		} else if (knowledge.isCardPlayedBy(leftOpponent, card)) {
			inputs[netInputIndexForCard] = -0.5d;
		} else {
			inputs[netInputIndexForCard] = 0.0d;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			inputs[32 + netInputIndexForCard] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(rightOpponent, card)) {
			inputs[32 + netInputIndexForCard] = -1.0d;
		} else if (knowledge.isCardPlayedBy(rightOpponent, card)) {
			inputs[32 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[32 + netInputIndexForCard] = 0.0d;
		}

		// inputs for player
		if (knowledge.couldHaveCard(knowledge.getPlayerPosition(), card)) {
			inputs[64 + netInputIndexForCard] = 1.0d;
		} else if (knowledge
				.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[64 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[64 + netInputIndexForCard] = 0.0d;
		}
	}

	private void setOpponentNetInputs(double[] inputs, Player otherOpponent,
			Player declarer, Card card) {

		GameType gameType = knowledge.getGame().getGameType();
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for other opponent
		if (knowledge.couldHaveCard(otherOpponent, card)) {
			inputs[netInputIndexForCard] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(otherOpponent, card)) {
			inputs[netInputIndexForCard] = -1.0d;
		} else if (knowledge.isCardPlayedBy(otherOpponent, card)) {
			inputs[netInputIndexForCard] = -0.5d;
		} else {
			inputs[netInputIndexForCard] = 0.0d;
		}

		// inputs for player
		if (knowledge.couldHaveCard(knowledge.getPlayerPosition(), card)) {
			inputs[32 + netInputIndexForCard] = 1.0d;
		} else if (knowledge
				.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[32 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[32 + netInputIndexForCard] = 0.0d;
		}

		// inputs for single player
		if (knowledge.couldHaveCard(declarer, card)) {
			inputs[64 + netInputIndexForCard] = 1.0d;
		} else if (knowledge.isCardPlayedInTrick(declarer, card)) {
			inputs[64 + netInputIndexForCard] = -1.0d;
		} else if (knowledge.isCardPlayedBy(declarer, card)) {
			inputs[64 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[64 + netInputIndexForCard] = 0.0d;
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
	 * @see org.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		allInputs.clear();
	}

	/**
	 * @see org.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {

		if (isLearning && allInputs.size() > 0) {
			// adjust neural networks
			// from last trick to first trick
			adjustNeuralNetworks(allInputs);
		}
	}

	private void adjustNeuralNetworks(List<double[]> inputs) {

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

		for (double[] inputParam : inputs) {
			net.adjustWeights(inputParam, outputParam);
		}
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
