/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.nn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.util.INeuralNetwork;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;
import org.jskat.player.AbstractJSkatPlayer;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSkat player using neural network
 */
public class AIPlayerNN extends AbstractJSkatPlayer {

	private final static long MAX_SIMULATIONS = 50;

	public static double WON = 1.0d;
	public static double LOST = 0.0d;

	private Logger log = LoggerFactory.getLogger(AIPlayerNN.class);

	private DecimalFormat formatter = new DecimalFormat("0.00000000000000000");

	private final GameSimulator gameSimulator;
	private NetworkInputGenerator inputGenerator;

	private final Random rand;
	private final List<double[]> allInputs = new ArrayList<double[]>();
	private GameType bestGameTypeFromDiscarding;

	private boolean isLearning = false;
	private double lastAvgNetworkError = 0.0;

	private final List<GameType> feasibleGameTypes = new ArrayList<GameType>();

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
	public AIPlayerNN(final String newPlayerName) {

		log.debug("Constructing new AIPlayerNN"); //$NON-NLS-1$
		setPlayerName(newPlayerName);

		gameSimulator = new GameSimulator();
		inputGenerator = new SimpleNetworkInputGenerator();

		for (GameType gameType : GameType.values()) {
			if (gameType != GameType.RAMSCH && gameType != GameType.PASSED_IN) {
				feasibleGameTypes.add(gameType);
			}
		}

		rand = new Random();
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
	public int bidMore(final int nextBidValue) {

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
	public boolean holdBid(final int currBidValue) {

		return isAnyGamePossible(currBidValue);
	}

	private boolean isAnyGamePossible(final int bidValue) {

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(bidValue);

		gameSimulator.resetGameSimulator(filteredGameTypes,
				knowledge.getPlayerPosition(), knowledge.getOwnCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long
				.valueOf(MAX_SIMULATIONS / 2));

		for (Double wonRate : results.getAllWonRates()) {
			if (wonRate.doubleValue() > 0.6) {
				return true;
			}
		}
		return false;
	}

	private List<GameType> filterFeasibleGameTypes(final int bidValue) {
		// FIXME (jansch 14.09.2011) consider hand and ouvert games
		// return game announcement instead
		List<GameType> result = new ArrayList<GameType>();

		SkatGameData data = getGameDataForWonGame();

		for (GameType gameType : feasibleGameTypes) {

			GameAnnouncementFactory factory = GameAnnouncement.getFactory();
			factory.setGameType(gameType);
			data.setAnnouncement(factory.getAnnouncement());

			SkatRule skatRules = SkatRuleFactory.getSkatRules(gameType);
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
		for (Card card : knowledge.getOwnCards()) {
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
		gameSimulator.resetGameSimulator(gameTypesToCheck,
				knowledge.getPlayerPosition(), knowledge.getOwnCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long
				.valueOf(MAX_SIMULATIONS));

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

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(knowledge
				.getHighestBid(knowledge.getPlayerPosition()).intValue());

		gameSimulator.resetGameSimulator(filteredGameTypes,
				knowledge.getPlayerPosition(), knowledge.getOwnCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long
				.valueOf(MAX_SIMULATIONS));

		for (Double wonRate : results.getAllWonRates()) {

			if (wonRate.doubleValue() > 0.95) {

				result = false;
			}
		}

		return result;
	}

	/**
	 * @see JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList getCardsToDiscard() {

		CardList cards = knowledge.getOwnCards();
		CardList result = new CardList();
		double highestWonRate = 0.0;

		log.debug("Player cards before discarding: " + knowledge.getOwnCards()); //$NON-NLS-1$

		List<GameType> filteredGameTypes = filterFeasibleGameTypes(knowledge
				.getHighestBid(knowledge.getPlayerPosition()).intValue());

		// check all possible discards
		for (int i = 0; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {

				CardList simCards = new CardList();
				simCards.addAll(cards);

				CardList currSkat = new CardList();
				currSkat.add(simCards.get(i));
				currSkat.add(simCards.get(j));

				simCards.removeAll(currSkat);

				gameSimulator.resetGameSimulator(filteredGameTypes,
						knowledge.getPlayerPosition(), simCards);
				SimulationResults simulationResults = gameSimulator
						.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS / 2));

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

		if (result.size() != 2) {
			log.error("Did not found cards for discarding!!!"); //$NON-NLS-1$
			result.clear();
			result.add(cards.remove(rand.nextInt(cards.size())));
			result.add(cards.remove(rand.nextInt(cards.size())));
		}

		log.debug("Player cards after discarding: " + knowledge.getOwnCards()); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see org.jskat.player.AbstractJSkatPlayer#startGame()
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

		Map<Card, double[]> cardInputs = new HashMap<Card, double[]>();
		if (possibleCards.size() == 1) {
			// only one card is playable
			bestCardIndex = 0;
			cardInputs.put(possibleCards.get(0), inputGenerator.getNetInputs(
					knowledge, possibleCards.get(0)));
		} else {
			// find the best card by asking the network
			INeuralNetwork net = SkatNetworks.getNetwork(knowledge.getGame()
					.getGameType(), isDeclarer(), knowledge.getCurrentTrick()
					.getTrickNumberInGame());

			CardList bestCards = new CardList();
			double highestOutput = Double.NEGATIVE_INFINITY;
			for (Card card : possibleCards) {

				log.debug("Testing card " + card); //$NON-NLS-1$

				double[] inputs = inputGenerator.getNetInputs(knowledge, card);

				cardInputs.put(card, inputs);
				double currOutput = net.getPredictedOutcome(inputs);
				log.warn("net output for card " + card + ": " + formatter.format(currOutput)); //$NON-NLS-1$

				if (currOutput > highestOutput) {
					highestOutput = currOutput;
					bestCards.clear();
					bestCards.add(card);
				} else if (currOutput == highestOutput) {
					bestCards.add(card);
				}
			}

			if (bestCards.size() > 0) {
				// get random card out of the best cards
				bestCardIndex = chooseRandomCard(possibleCards, bestCards);
				log.warn("Trick " + (knowledge.getNoOfTricks() + 1) + ": " + bestCards.size() + " of " + possibleCards.size() + " are best cards. Choosing random from these."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		// store parameters for the card to play
		// for adjustment of weights after the game
		storeInputParameters(cardInputs.get(possibleCards.get(bestCardIndex)));

		log.debug("choosing card " + bestCardIndex); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(bestCardIndex)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(bestCardIndex);
	}

	private String getInputString(final double[] inputs) {
		String result = "";
		for (double input : inputs) {
			result += input + " ";
		}
		return result;
	}

	private void storeInputParameters(final double[] inputParameters) {

		allInputs.add(inputParameters);
	}

	private int chooseRandomCard(final CardList possibleCards,
			final CardList goodCards) {
		int bestCardIndex;
		Card choosenCard = goodCards.get(rand.nextInt(goodCards.size()));
		bestCardIndex = possibleCards.indexOf(choosenCard);
		return bestCardIndex;
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
	 * @see org.jskat.player.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {

		assert allInputs.size() < 11;

		if (isLearning && allInputs.size() > 0) {
			// adjust neural networks
			// from last trick to first trick
			adjustNeuralNetworks(allInputs);
		}
	}

	private void adjustNeuralNetworks(final List<double[]> inputs) {

		assert inputs.size() < 11;

		double networkErrorSum = 0.0;
		double output = 0.0d;
		if (!GameType.PASSED_IN.equals(knowledge.getGameType())) {
			if (GameType.RAMSCH.equals(knowledge.getGameType())) {
				if (isRamschGameWon(gameSummary, knowledge.getPlayerPosition())) {
					output = WON;
				} else {
					output = LOST;
				}
			} else {
				if (isDeclarer()) {
					if (gameSummary.isGameWon()) {
						output = WON;
					} else {
						output = LOST;
					}
				} else {
					if (gameSummary.isGameWon()) {
						output = LOST;
					} else {
						output = WON;
					}
				}
			}
			double[] outputs = new double[] { output };

			int index = 0;
			for (double[] inputParam : inputs) {
				INeuralNetwork net = SkatNetworks.getNetwork(knowledge
						.getGame().getGameType(), isDeclarer(), index);

				double networkError = net.adjustWeights(inputParam, outputs);
				log.warn("learning error: " + networkError);
				networkErrorSum += networkError;
				index++;
			}

			lastAvgNetworkError = networkErrorSum / inputs.size();
		}
	}

	/**
	 * Gets the last average network error
	 * 
	 * @return Last average network error
	 */
	public double getLastAvgNetworkError() {
		return lastAvgNetworkError;
	}

	// FIXME (jan 10.03.2012) code duplication with NNTrainer
	private static boolean isRamschGameWon(final GameSummary gameSummary,
			final Player currPlayer) {

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
	public void setIsLearning(final boolean newIsLearning) {

		isLearning = newIsLearning;
	}

	/**
	 * Sets a new logger for the nn player
	 * 
	 * @param newLogger
	 *            New logger
	 */
	@Override
	public void setLogger(final Logger newLogger) {
		super.setLogger(newLogger);
		log = newLogger;
	}
}
