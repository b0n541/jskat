/**
 * Copyright (C) 2016 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.ai.nn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.input.GenericNetworkInputGenerator;
import org.jskat.ai.nn.input.NetworkInputGenerator;
import org.jskat.ai.nn.util.INeuralNetwork;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;

/**
 * JSkat player using neural network
 */
public class AIPlayerNN extends AbstractAIPlayer {

	private final static Long MAX_SIMULATIONS_DISCARDING = 2000L;
	private final static Long MAX_TIME_DISCARDING = 5000L;
	private final static Long MAX_SIMULATIONS_BIDDING = 100L;
	private final static Long MAX_SIMULATIONS_HAND_GAME = 500L;
	private final static Double MIN_WON_RATE_FOR_BIDDING = 0.6;
	private final static Double MIN_WON_RATE_FOR_DISCARDING = 0.75;
	private final static Double MIN_WON_RATE_FOR_HAND_GAME = 0.95;

	public final static Double IDEAL_WON = 1.0;
	public final static Double IDEAL_LOST = 0.0;
	public final static Double EPSILON = 0.2;

	// FIXME (jan 10.03.2012) code duplication with NNTrainer
	private static boolean isRamschGameWon(final GameSummary gameSummary, final Player currPlayer) {
		boolean ramschGameWon = false;
		final int playerPoints = gameSummary.getPlayerPoints(currPlayer);
		int highestPlayerPoints = 0;
		for (final Player player : Player.values()) {
			final int currPlayerPoints = gameSummary.getPlayerPoints(player);

			if (currPlayerPoints > highestPlayerPoints) {
				highestPlayerPoints = currPlayerPoints;
			}
		}

		if (highestPlayerPoints > playerPoints) {
			ramschGameWon = true;
		}

		return ramschGameWon;
	}

	private final DecimalFormat formatter = new DecimalFormat("0.00000000000000000"); //$NON-NLS-1$
	private final GameSimulator2 gameSimulator2;

	private final NetworkInputGenerator inputGenerator;
	private final static Random RANDOM = new Random();
	private final List<double[]> allInputs = new ArrayList<>();

	private GameType bestGameTypeFromDiscarding;
	private boolean isLearning = false;

	private double lastAvgNetworkError = 0.0;

	private final List<GameType> feasibleGameTypes = new ArrayList<GameType>();

	/**
	 * Constructor
	 */
	public AIPlayerNN() {
		this("unknown", null); //$NON-NLS-1$
	}

	/**
	 * Constructor
	 *
	 * @param logger
	 *            Logger to be used, allows to set NOPLogger from outside
	 */
	public AIPlayerNN(final Logger logger) {
		this("unknown", logger);
	}

	/**
	 * Creates a new instance of AIPlayerNN
	 *
	 * @param newPlayerName
	 *            Player's name
	 * @param logger
	 *            Logger to be used, allows to set NOPLogger from outside
	 */
	public AIPlayerNN(final String newPlayerName, final Logger logger) {
		log.debug("Constructing new AIPlayerNN"); //$NON-NLS-1$
		setPlayerName(newPlayerName);

		if (logger != null) {
			log = logger;
		}

		inputGenerator = new GenericNetworkInputGenerator();
		gameSimulator2 = new GameSimulator2();

		for (final GameType gameType : GameType.values()) {
			if (gameType != GameType.RAMSCH && gameType != GameType.PASSED_IN) {
				feasibleGameTypes.add(gameType);
			}
		}
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

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		if (bestGameTypeFromDiscarding != null) {
			// use game type from discarding
			factory.setGameType(bestGameTypeFromDiscarding);
		}

		factory.setHand(knowledge.isHandGame());

		// FIXME (jan 17.01.2011) setting ouvert and schneider/schwarz
		// newGame.setOuvert(rand.nextBoolean());

		final GameAnnouncement newGame = factory.getAnnouncement();

		log.debug("Announcing: " + newGame); //$NON-NLS-1$

		return newGame;
	}

	/**
	 * @see JSkatPlayer#bidMore(int)
	 */
	@Override
	public Integer bidMore(final int nextBidValue) {
		int result = -1;

		if (isAnyGamePossible(nextBidValue)) {
			result = nextBidValue;
		}

		return result;
	}

	@Override
	public Boolean callContra() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean callRe() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.jskat.player.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {

		if (isLearning && allInputs.size() > 0) {
			// adjust neural networks
			// from last trick to first trick
			adjustNeuralNetworks(allInputs);
		}
	}

	/**
	 * @see JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList getCardsToDiscard() {
		final CardList cards = knowledge.getOwnCards();

		log.debug("Player cards before discarding: " + knowledge.getOwnCards()); //$NON-NLS-1$

		final List<GameType> filteredGameTypes = filterFeasibleGameTypes(
				knowledge.getHighestBid(knowledge.getPlayerPosition()).intValue());

		gameSimulator2.reset();

		// create all possible discards
		int simCount = 0;
		for (int i = 0; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {
				simCount++;
				final CardList simCards = new CardList(cards);

				final CardList currSkat = new CardList(simCards.get(i), simCards.get(j));

				simCards.removeAll(currSkat);

				log.debug("Discard simulation no. " + simCount + ": skat " + currSkat);

				for (final GameType gameType : filteredGameTypes) {

					gameSimulator2.add(new GameSimulation(gameType, knowledge.getPlayerPosition(), simCards, currSkat));
				}
			}
		}

		final GameSimulation bestSimulation = gameSimulator2.simulateMaxEpisodes(1000L);
		bestGameTypeFromDiscarding = bestSimulation.getGameType();

		log.warn("Simulated " + bestSimulation.getEpisodes() + " episodes with highest won rate of "
				+ bestSimulation.getWonRate() + " discarded cards " + bestSimulation.getSkatCards());

		return bestSimulation.getSkatCards();
	}

	private CardList getRandomEntry(final List<CardList> possibleSkats) {
		return possibleSkats.get(RANDOM.nextInt(possibleSkats.size()));
	}

	/**
	 * Gets the last average network error
	 *
	 * @return Last average network error
	 */
	public double getLastAvgNetworkError() {
		return lastAvgNetworkError;
	}

	/**
	 * @see JSkatPlayer#holdBid(int)
	 */
	@Override
	public Boolean holdBid(final int currBidValue) {
		return isAnyGamePossible(currBidValue);
	}

	/**
	 * @see JSkatPlayer#pickUpSkat()
	 */
	@Override
	public Boolean pickUpSkat() {

		log.warn("Check hand game or pick up skat...");

		gameSimulator2.reset();

		final List<GameType> filteredGameTypes = filterFeasibleGameTypes(
				knowledge.getHighestBid(knowledge.getPlayerPosition()).intValue());
		for (final GameType gameType : filteredGameTypes) {
			gameSimulator2.add(new GameSimulation(gameType, knowledge.getPlayerPosition(), knowledge.getOwnCards()));
		}

		final GameSimulation bestSimulation = gameSimulator2.simulateMaxEpisodes(MAX_SIMULATIONS_HAND_GAME);

		log.warn("Simulated " + bestSimulation.getEpisodes() + " episodes with best won rate of "
				+ bestSimulation.getWonRate());

		if (bestSimulation.getWonRate() >= MIN_WON_RATE_FOR_HAND_GAME) {
			log.warn("Min won rate reached. Playing hand...");
			bestGameTypeFromDiscarding = bestSimulation.getGameType();
			return false;
		}

		log.warn("Min won rate not reached. Picking up skat...");
		return true;
	}

	/**
	 * @see JSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		int bestCardIndex = -1;

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		final CardList possibleCards = getPlayableCards(knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " //$NON-NLS-1$//$NON-NLS-2$
				+ possibleCards);

		final Map<Card, double[]> cardInputs = new HashMap<Card, double[]>();

		final INeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGameAnnouncement().getGameType(), isDeclarer(),
				knowledge.getCurrentTrick().getTrickNumberInGame());

		final CardList bestCards = new CardList();
		final CardList highestOutputCards = new CardList();
		Double highestOutput = Double.NEGATIVE_INFINITY;
		for (final Card card : possibleCards) {
			log.debug("Testing card " + card); //$NON-NLS-1$

			final double[] inputs = inputGenerator.getNetInputs(knowledge, card);

			cardInputs.put(card, inputs);
			final Double currOutput = net.getPredictedOutcome(inputs);
			log.warn("net output for card " + card + ": " //$NON-NLS-1$
					+ formatter.format(currOutput));

			if (currOutput > (IDEAL_WON - EPSILON)) {
				bestCards.add(card);
			}
			if (currOutput > highestOutput && !formatter.format(currOutput).equals(formatter.format(highestOutput))) {
				highestOutput = currOutput;
				highestOutputCards.clear();
				highestOutputCards.add(card);
			} else if (currOutput == highestOutput
					|| formatter.format(currOutput).equals(formatter.format(highestOutput))) {
				highestOutputCards.add(card);
			}
		}

		if (bestCards.size() > 0) {
			// get random card out of the best cards
			bestCardIndex = chooseRandomCard(possibleCards, bestCards);
			log.warn("Trick " + (knowledge.getNoOfTricks() + 1) //$NON-NLS-1$
					+ ": Found best cards. Choosing random from " //$NON-NLS-1$
					+ bestCards.size() + " out of " + possibleCards.size() + ": " //$NON-NLS-1$ //$NON-NLS-2$
					+ possibleCards.get(bestCardIndex));
		} else {
			// no best card, get card with best output
			bestCardIndex = chooseRandomCard(possibleCards, highestOutputCards);
			log.warn("Trick " + (knowledge.getNoOfTricks() + 1) //$NON-NLS-1$
					+ ": Found no best cards. Choosing card from " //$NON-NLS-1$
					+ highestOutputCards.size() + " out of " + possibleCards.size() + " with highest output: "
					+ possibleCards.get(bestCardIndex));
			// no best card, get random card out of all cards
			// bestCardIndex = chooseRandomCard(possibleCards, possibleCards);
		}

		// store parameters for the card to play
		// for adjustment of weights after the game
		storeInputParameters(cardInputs.get(possibleCards.get(bestCardIndex)));

		log.debug("choosing card " + bestCardIndex); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " //$NON-NLS-1$//$NON-NLS-2$
				+ possibleCards.get(bestCardIndex));

		return possibleCards.get(bestCardIndex);
	}

	@Override
	public Boolean playGrandHand() {
		return Boolean.FALSE;
	}

	/**
	 * @see org.jskat.player.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		bestGameTypeFromDiscarding = null;
		allInputs.clear();
	}

	/**
	 * Sets the player into learning mode
	 *
	 * @param newIsLearning
	 *            TRUE if the player should learn during play
	 */
	public void setIsLearning(final boolean newIsLearning) {
		isLearning = newIsLearning;
	}

	/**
	 * @see org.jskat.player.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// CHECK Auto-generated method stub
	}

	private void adjustNeuralNetworks(final List<double[]> inputs) {

		double output = 0.0d;
		if (!GameType.PASSED_IN.equals(knowledge.getGameType())) {
			if (GameType.RAMSCH.equals(knowledge.getGameType())) {
				if (isRamschGameWon(gameSummary, knowledge.getPlayerPosition())) {
					output = IDEAL_WON;
				} else {
					output = IDEAL_LOST;
				}
			} else {
				if (isDeclarer()) {
					if (gameSummary.isGameWon()) {
						output = IDEAL_WON;
					} else {
						output = IDEAL_LOST;
					}
				} else {
					if (gameSummary.isGameWon()) {
						output = IDEAL_LOST;
					} else {
						output = IDEAL_WON;
					}
				}
			}

			log.warn("Learning output: " + output);

			final double[][] inputsArray = new double[inputs.size()][];
			final double[][] outputsArray = new double[inputs.size()][];
			for (int i = 0; i < inputs.size(); i++) {
				inputsArray[i] = inputs.get(i);
				outputsArray[i] = new double[] { output };
			}

			final INeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGameAnnouncement().getGameType(), isDeclarer(),
					0);
			final double networkError = net.adjustWeightsBatch(inputsArray, outputsArray);

			log.warn("learning error: " + networkError);
			lastAvgNetworkError = networkError;
		}
	}

	private static int chooseRandomCard(final CardList possibleCards, final CardList goodCards) {
		int bestCardIndex;
		final Card choosenCard = goodCards.get(RANDOM.nextInt(goodCards.size()));
		bestCardIndex = possibleCards.indexOf(choosenCard);
		return bestCardIndex;
	}

	private List<GameType> filterFeasibleGameTypes(final int bidValue) {
		// FIXME (jansch 14.09.2011) consider hand and ouvert games
		// return game announcement instead
		final List<GameType> result = new ArrayList<GameType>();

		final SkatGameData data = getGameDataForWonGame();

		for (final GameType gameType : feasibleGameTypes) {

			final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
			factory.setGameType(gameType);
			data.setAnnouncement(factory.getAnnouncement());

			final SkatRule skatRules = SkatRuleFactory.getSkatRules(gameType);
			final int currGameResult = skatRules.calcGameResult(data);

			if (currGameResult >= bidValue) {

				result.add(gameType);
			}
		}

		return result;
	}

	private SkatGameData getGameDataForWonGame() {
		final SkatGameData data = new SkatGameData();

		// it doesn't matter which position is set for declarer
		// skat game data are only used to calculate the game value
		data.setDeclarer(Player.FOREHAND);
		data.addDealtCards(Player.FOREHAND, knowledge.getOwnCards());
		data.addSkatToPlayer(Player.FOREHAND);

		final SkatGameResult result = new SkatGameResult();
		result.setWon(true);
		data.setResult(result);

		return data;
	}

	private boolean isAnyGamePossible(final int bidValue) {

		final List<GameType> filteredGameTypes = filterFeasibleGameTypes(bidValue);

		log.warn("Game simulation on bidding: bid value " + bidValue);
		log.warn("Player position: " + knowledge.getPlayerPosition() + " cards: " + knowledge.getOwnCards());

		gameSimulator2.reset();

		for (final GameType gameType : filteredGameTypes) {
			gameSimulator2.add(new GameSimulation(gameType, knowledge.getPlayerPosition(), knowledge.getOwnCards()));
		}

		final GameSimulation bestSimulation = gameSimulator2.simulateMaxEpisodes(MAX_SIMULATIONS_BIDDING);

		log.warn("Simulated " + bestSimulation.getEpisodes() + " episodes with highest won rate of "
				+ bestSimulation.getWonRate());

		if (bestSimulation.getWonRate() >= MIN_WON_RATE_FOR_BIDDING) {
			log.warn("Min won rate reached. Bidding...");
			return true;
		}

		log.warn("Min won rate not reached. Passing...");

		return false;
	}

	private void storeInputParameters(final double[] inputParameters) {
		allInputs.add(inputParameters);
	}
}
