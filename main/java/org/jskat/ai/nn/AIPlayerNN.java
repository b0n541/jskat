/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.ai.JSkatPlayer;
import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.util.NeuralNetwork;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;
import org.jskat.util.Card;
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

	private Log log = LogFactory.getLog(AIPlayerNN.class);

	private GameSimulator gameSimulator;

	private Random rand;
	private List<double[]> allInputs;
	private GameType bestGameTypeFromDiscarding;

	private boolean isLearning = false;

	private List<GameType> feasibleGameTypes;

	private static long MAX_SIMULATIONS = 100;

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

		gameSimulator = new GameSimulator();

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
	 * @see JSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see JSkatPlayer#bidMore(int)
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
	 * @see JSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {

		return isAnyGamePossible(currBidValue);
	}

	private boolean isAnyGamePossible(int bidValue) {

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(bidValue);

		gameSimulator.resetGameSimulator(filteredGameTypes, knowledge.getPlayerPosition(), knowledge.getMyCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS / 2));

		for (Double wonRate : results.getAllWonRates()) {
			if (wonRate.doubleValue() > 0.6) {
				return true;
			}
		}
		return false;
	}

	private List<GameType> filterFeasibleGameTypes(int bidValue) {
		// FIXME (jansch 14.09.2011) consider hand and ouvert games
		// return game announcement instead
		List<GameType> result = new ArrayList<GameType>();

		SkatGameData data = getGameDataForWonGame();

		for (GameType gameType : feasibleGameTypes) {

			GameAnnouncementFactory factory = GameAnnouncement.getFactory();
			factory.setGameType(gameType);
			data.setAnnouncement(factory.getAnnouncement());

			BasicSkatRules skatRules = SkatRuleFactory.getSkatRules(gameType);
			int currGameResult = skatRules.calcGameResult(data);

			if (currGameResult >= bidValue) {

				result.add(gameType);
			}
		}

		return result;
	}

	private SkatGameData getGameDataForWonGame() {

		SkatGameData data = new SkatGameData();

		data.setDeclarerPickedUpSkat(true);

		SkatGameResult result = new SkatGameResult();
		result.setWon(true);
		data.setResult(result);

		// it doesn't matter which position is set for declarer
		// skat game data are only used to calculate the game value
		data.setDeclarer(Player.FOREHAND);
		for (Card card : knowledge.getMyCards()) {
			data.setDealtCard(Player.FOREHAND, card);
		}

		return data;
	}

	/**
	 * @see JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		if (bestGameTypeFromDiscarding != null) {
			// use game type from discarding
			factory.setGameType(bestGameTypeFromDiscarding);
		} else {
			// no discarding done
			factory.setGameType(getBestGameType());
			factory.setHand(Boolean.TRUE);
		}

		// FIXME (jan 17.01.2011) setting ouvert and schneider/schwarz
		// newGame.setOuvert(rand.nextBoolean());

		GameAnnouncement newGame = factory.getAnnouncement();

		log.debug("Announcing: " + newGame); //$NON-NLS-1$

		return newGame;
	}

	private GameType getBestGameType() {

		// FIXME (jan 18.01.2011) check for overbidding!
		GameType bestGameType = null;
		double highestWonRate = 0.0;

		List<GameType> gameTypesToCheck = filterFeasibleGameTypes(knowledge
				.getHighestBid(knowledge.getPlayerPosition()));
		gameSimulator.resetGameSimulator(gameTypesToCheck, knowledge.getPlayerPosition(), knowledge.getMyCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS));

		for (GameType gameType : gameTypesToCheck) {

			Double wonRate = results.getWonRate(gameType);

			if (wonRate.doubleValue() > highestWonRate) {

				log.debug("Found new highest number of won games " //$NON-NLS-1$
						+ wonRate + " for game type " + gameType); //$NON-NLS-1$

				highestWonRate = wonRate;
				bestGameType = gameType;
			}
		}

		if (bestGameType == null) {
			// FIXME (jansch 21.09.2011) find cheapest game
			log.error("No best game type found. Announcing null!!!"); //$NON-NLS-1$
			bestGameType = GameType.NULL;
		}

		return bestGameType;
	}

	/**
	 * @see JSkatPlayer#pickUpSkat()
	 */
	@Override
	public boolean pickUpSkat() {

		boolean result = true;

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(knowledge.getHighestBid(
				knowledge.getPlayerPosition()).intValue());

		gameSimulator.resetGameSimulator(filteredGameTypes, knowledge.getPlayerPosition(), knowledge.getMyCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS));

		for (Double wonRate : results.getAllWonRates()) {

			if (wonRate.doubleValue() > 0.9) {

				result = false;
			}
		}

		return result;
	}

	/**
	 * @see JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {

		CardList cards = knowledge.getMyCards();
		CardList result = new CardList();
		double highestWonRate = 0.0;

		log.debug("Player cards before discarding: " + knowledge.getMyCards()); //$NON-NLS-1$

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(knowledge.getHighestBid(
				knowledge.getPlayerPosition()).intValue());

		// check all possible discards
		for (int i = 0; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {

				CardList simCards = new CardList();
				simCards.addAll(cards);

				CardList currSkat = new CardList();
				currSkat.add(simCards.get(i));
				currSkat.add(simCards.get(j));

				simCards.removeAll(currSkat);

				gameSimulator.resetGameSimulator(filteredGameTypes, knowledge.getPlayerPosition(), simCards);
				SimulationResults simulationResults = gameSimulator.simulateMaxEpisodes(Long
						.valueOf(MAX_SIMULATIONS / 2));

				for (GameType currType : filteredGameTypes) {

					Double wonRate = simulationResults.getWonRate(currType);

					if (wonRate.doubleValue() > highestWonRate) {
						highestWonRate = wonRate.doubleValue();
						bestGameTypeFromDiscarding = currType;
						result.clear();
						result.addAll(currSkat);
					}
				}
			}
		}

		if (result.size() == 2) {

			cards.remove(result.get(0));
			cards.remove(result.get(1));
		} else {

			log.error("Did not found cards for discarding!!!"); //$NON-NLS-1$
			result.clear();
			result.add(cards.remove(rand.nextInt(cards.size())));
			result.add(cards.remove(rand.nextInt(cards.size())));
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
	 * @see JSkatPlayer#playCard()
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
			List<NeuralNetwork> networks = SkatNetworks.getNetwork(knowledge.getGame().getGameType(), isDeclarer());
			NeuralNetwork net = networks.get(knowledge.getCurrentTrick().getTrickNumberInGame());

			Map<Card, double[]> cardInputs = new HashMap<Card, double[]>();
			CardList goodCards = new CardList();
			for (Card card : possibleCards) {

				log.debug("Testing card " + card); //$NON-NLS-1$

				double[] currInputs = getNetInputs(card);
				cardInputs.put(card, currInputs);
				double currOutput = net.getPredictedOutcome(currInputs);
				log.debug("net output: " + currOutput); //$NON-NLS-1$

				if (currOutput > 0.9) {
					goodCards.add(card);
				}
			}

			if (goodCards.size() > 0) {
				// get random card out of the good cards
				Card choosenCard = goodCards.get(rand.nextInt(goodCards.size()));
				bestCardIndex = possibleCards.indexOf(choosenCard);
			} else {
				// then choose a random card
				bestCardIndex = rand.nextInt(possibleCards.size());
				log.error("RANDOM CARD CHOOSEN"); //$NON-NLS-1$
			}

			// store parameters for the card to play
			// for adjustment of weights after the game
			allInputs.add(cardInputs.get(possibleCards.get(bestCardIndex)));
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

		Player declarer = null;
		if (GameType.RAMSCH.equals(knowledge.getGameType())) {
			// there is no declarer in ramsch games, set player as declarer
			declarer = knowledge.getPlayerPosition();
		} else {
			declarer = knowledge.getDeclarer();
		}

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
				inputs[64 + getNetInputIndex(knowledge.getGame().getGameType(), cardToPlay)] = -2.0d;
			} else {
				inputs[32 + getNetInputIndex(knowledge.getGame().getGameType(), cardToPlay)] = -2.0d;
			}
		}

		return inputs;
	}

	private void setDeclarerNetInputs(double[] inputs, Player leftOpponent, Player rightOpponent, Card card) {

		GameType gameType = knowledge.getGame().getGameType();
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			if (knowledge.couldHaveCard(rightOpponent, card)) {
				inputs[netInputIndexForCard] = 0.5d;
			} else {
				inputs[netInputIndexForCard] = 2.0d;
			}
		} else if (knowledge.isCardPlayedInTrick(leftOpponent, card)) {
			inputs[netInputIndexForCard] = -2.0d;
		} else if (knowledge.isCardPlayedBy(leftOpponent, card)) {
			inputs[netInputIndexForCard] = -0.5d;
		} else {
			inputs[netInputIndexForCard] = 0.0d;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			if (knowledge.couldHaveCard(leftOpponent, card)) {
				inputs[32 + netInputIndexForCard] = 0.5d;
			} else {
				inputs[32 + netInputIndexForCard] = 2.0d;
			}
		} else if (knowledge.isCardPlayedInTrick(rightOpponent, card)) {
			inputs[32 + netInputIndexForCard] = -2.0d;
		} else if (knowledge.isCardPlayedBy(rightOpponent, card)) {
			inputs[32 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[32 + netInputIndexForCard] = 0.0d;
		}

		// inputs for player
		if (knowledge.getMyCards().contains(card)) {
			inputs[64 + netInputIndexForCard] = 2.0d;
		} else if (knowledge.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[64 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[64 + netInputIndexForCard] = 0.0d;
		}

		// // inputs for skat
		// if (knowledge.couldLieInSkat(card)) {
		// inputs[96 + netInputIndexForCard] = 0.5d;
		// } else {
		// inputs[96 + netInputIndexForCard] = 0.0d;
		// }
	}

	private void setOpponentNetInputs(double[] inputs, Player otherOpponent, Player declarer, Card card) {

		GameType gameType = knowledge.getGame().getGameType();
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for other opponent
		if (knowledge.couldHaveCard(otherOpponent, card)) {
			if (knowledge.couldHaveCard(declarer, card)) {
				inputs[netInputIndexForCard] = 0.5d;
			} else {
				inputs[netInputIndexForCard] = 2.0d;
			}
		} else if (knowledge.isCardPlayedInTrick(otherOpponent, card)) {
			inputs[netInputIndexForCard] = -2.0d;
		} else if (knowledge.isCardPlayedBy(otherOpponent, card)) {
			inputs[netInputIndexForCard] = -0.5d;
		} else {
			inputs[netInputIndexForCard] = 0.0d;
		}

		// inputs for player
		if (knowledge.getMyCards().contains(card)) {
			inputs[32 + netInputIndexForCard] = 2.0d;
		} else if (knowledge.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
			inputs[32 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[32 + netInputIndexForCard] = 0.0d;
		}

		// inputs for single player
		if (knowledge.couldHaveCard(declarer, card)) {
			if (knowledge.couldHaveCard(otherOpponent, card)) {
				inputs[64 + netInputIndexForCard] = 0.5d;
			} else {
				inputs[64 + netInputIndexForCard] = 2.0d;
			}
		} else if (knowledge.isCardPlayedInTrick(declarer, card)) {
			inputs[64 + netInputIndexForCard] = -2.0d;
		} else if (knowledge.isCardPlayedBy(declarer, card)) {
			inputs[64 + netInputIndexForCard] = -0.5d;
		} else {
			inputs[64 + netInputIndexForCard] = 0.0d;
		}

		// // inputs for skat
		// if (knowledge.couldLieInSkat(card)) {
		// inputs[96 + netInputIndexForCard] = 0.5d;
		// } else {
		// inputs[96 + netInputIndexForCard] = 0.0d;
		// }
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

	private static int getNetInputIndexSuitGrandRamschGame(GameType gameType, Card card) {

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

				result = 4 + 7 + (card.getSuit().getSuitOrder() - 1) * 7 + card.getSuitGrandOrder();
			} else {

				result = 4 + 7 + card.getSuit().getSuitOrder() * 7 + card.getSuitGrandOrder();
			}
		}

		return result;
	}

	/**
	 * @see org.jskat.ai.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		allInputs.clear();
		bestGameTypeFromDiscarding = null;
	}

	/**
	 * @see org.jskat.ai.JSkatPlayer#finalizeGame()
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

		if (GameType.RAMSCH.equals(knowledge.getGameType())) {
			if (isRamschGameWon(gameSummary, knowledge.getPlayerPosition())) {
				output = 1.0d;
			} else {
				output = -1.0d;
			}
		} else {
			if (isDeclarer()) {
				if (gameSummary.isGameWon()) {
					output = 1.0d;
				} else {
					output = -1.0d;
				}
			} else {
				if (gameSummary.isGameWon()) {
					output = -1.0d;
				} else {
					output = 1.0d;
				}
			}
		}
		double[] outputParam = { output };

		List<NeuralNetwork> networks = SkatNetworks.getNetwork(knowledge.getGame().getGameType(), isDeclarer());

		int index = 0;
		for (double[] inputParam : inputs) {
			NeuralNetwork net = networks.get(index);
			net.adjustWeights(inputParam, outputParam);
			index++;
		}
	}

	private static boolean isRamschGameWon(GameSummary gameSummary, Player currPlayer) {

		boolean ramschGameWon = false;
		int playerPoints = gameSummary.getPlayerPoints(currPlayer);
		int highestPlayerPoints = 0;
		for (Player player : Player.values()) {
			int currPlayerPoints = gameSummary.getPlayerPoints(player);

			if (currPlayerPoints > highestPlayerPoints) {
				highestPlayerPoints = currPlayerPoints;
			}
		}

		if (highestPlayerPoints > playerPoints) {
			ramschGameWon = true;
		}

		return ramschGameWon;
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

	/**
	 * Sets a new logger for the nn player
	 * 
	 * @param newLogger
	 *            New logger
	 */
	public void setLogger(Log newLogger) {
		super.setLogger(newLogger);
		log = newLogger;
	}

}
