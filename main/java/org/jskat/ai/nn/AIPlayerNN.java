/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.util.INeuralNetwork;
import org.jskat.control.JSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * JSkat player using neural network
 */
public class AIPlayerNN extends AbstractJSkatPlayer {

	private Log log = LogFactory.getLog(AIPlayerNN.class);

	private final GameSimulator gameSimulator;

	private final Random rand;
	private final List<double[]> allInputs = new ArrayList<double[]>();
	private GameType bestGameTypeFromDiscarding;

	private boolean isLearning = false;

	private final List<GameType> feasibleGameTypes = new ArrayList<GameType>();

	private final double[] netInputs = new double[1089];
	private final double[] netOutputs = new double[1];
	private final static int PLAYER_LENGTH = 363;
	private final static long MAX_SIMULATIONS = 50;

	// 1.0 and 2.0 for tanh function
	// 2.0 and 4.0 for sigmoid function
	private static double HAS_CARD = 1.0d;
	private static double COULD_HAVE_CARD = 0.5d;
	private static double DOESNT_HAVE_CARD = 0.0d;
	private static double PLAYED_CARD = -0.5d;
	private static double PLAYED_CARD_IN_TRICK = -1.0d;

	private static double ACTIVE = 1.0d;
	private static double INACTIVE = 0.0d;

	// won game 1.0 and lost game -1.0 for tanh function
	// won game 1.0 and lost game 0.0 for sigmoid function
	private static double WON = 1.0d;
	private static double LOST = 0.0d;

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

		initInputBuffer();

		for (GameType gameType : GameType.values()) {
			if (gameType != GameType.RAMSCH && gameType != GameType.PASSED_IN) {
				feasibleGameTypes.add(gameType);
			}
		}

		rand = new Random();
		isLearning = true;
	}

	private void initInputBuffer() {
		for (int i = 0; i < 10; i++) {
			allInputs.add(new double[netInputs.length]);
		}
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

		gameSimulator.resetGameSimulator(filteredGameTypes, knowledge.getPlayerPosition(), knowledge.getOwnCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS / 2));

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
		gameSimulator.resetGameSimulator(gameTypesToCheck, knowledge.getPlayerPosition(), knowledge.getOwnCards());
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

		gameSimulator.resetGameSimulator(filteredGameTypes, knowledge.getPlayerPosition(), knowledge.getOwnCards());
		SimulationResults results = gameSimulator.simulateMaxEpisodes(Long.valueOf(MAX_SIMULATIONS));

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
			List<INeuralNetwork> networks = SkatNetworks.getNetwork(knowledge.getGame().getGameType(), isDeclarer());
			INeuralNetwork net = networks.get(knowledge.getCurrentTrick().getTrickNumberInGame());

			Map<Card, double[]> cardInputs = new HashMap<Card, double[]>();
			CardList goodCards = new CardList();
			CardList undecidedCards = new CardList();
			double highestOutput = -1000.0;
			Card cardWithHighestOutput = null;
			for (Card card : possibleCards) {

				log.debug("Testing card " + card); //$NON-NLS-1$

				double[] currInputs = getNetInputs(card);
				cardInputs.put(card, currInputs);
				double currOutput = net.getPredictedOutcome(currInputs);
				log.debug("net output: " + currOutput); //$NON-NLS-1$

				if (currOutput > highestOutput) {
					highestOutput = currOutput;
					cardWithHighestOutput = card;
				}

				if (currOutput > 0.95) {
					goodCards.add(card);
				} else if (currOutput > 0.05) {
					undecidedCards.add(card);
				}
			}

			if (goodCards.size() > 0) {
				// get random card out of the good cards
				bestCardIndex = chooseRandomCard(possibleCards, goodCards);
				log.warn("Trick " + (knowledge.getNoOfTricks() + 1) + ": " + goodCards.size() + " of " + possibleCards.size() + " are good cards. Choosing random from these."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} else if (undecidedCards.size() > 0) {
				// get random card out of the undecided cards
				bestCardIndex = chooseRandomCard(possibleCards, undecidedCards);
				log.warn("Trick " + (knowledge.getNoOfTricks() + 1) + ": " + undecidedCards.size() + " of " + possibleCards.size() + " are undecided cards. Choosing random from these."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} else {
				// card with highest output
				bestCardIndex = chooseRandomCard(possibleCards, possibleCards);
				log.warn("Trick " + (knowledge.getNoOfTricks() + 1) + ": No good card, choosing random from all."); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			// store parameters for the card to play
			// for adjustment of weights after the game
			storeInputParameters(knowledge.getCurrentTrick().getTrickNumberInGame(),
					cardInputs.get(possibleCards.get(bestCardIndex)));
		}

		log.debug("choosing card " + bestCardIndex); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(bestCardIndex)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(bestCardIndex);
	}

	private void storeInputParameters(final int trick, final double[] inputParameters) {

		double[] trickInputs = allInputs.get(trick);

		for (int i = 0; i < trickInputs.length; i++) {
			trickInputs[i] = inputParameters[i];
		}
	}

	private int chooseRandomCard(final CardList possibleCards, final CardList goodCards) {
		int bestCardIndex;
		Card choosenCard = goodCards.get(rand.nextInt(goodCards.size()));
		bestCardIndex = possibleCards.indexOf(choosenCard);
		return bestCardIndex;
	}

	/**
	 * Creates the net input attributes
	 * 
	 * @param cardToPlay
	 *            Card to be played
	 * @return Net input attributes
	 */
	double[] getNetInputs(final Card cardToPlay) {

		// set game declarer
		setDeclarerInputs(netInputs, PLAYER_LENGTH);

		// set information for all played cards
		final int TRICK_LENGTH = 33;
		final int CARD_OFFSET = 1;
		setTrickInputs(netInputs, PLAYER_LENGTH, TRICK_LENGTH, CARD_OFFSET);

		// set information for all unplayed cards
		Player leftOpponent = knowledge.getPlayerPosition().getLeftNeighbor();
		Player rightOpponent = knowledge.getPlayerPosition().getRightNeighbor();
		final int KNOWN_CARDS_OFFSET = 331;
		for (Card card : knowledge.getCompleteDeck()) {
			setKnowCards(netInputs, leftOpponent, rightOpponent, card, PLAYER_LENGTH, KNOWN_CARDS_OFFSET);
		}

		// set information of card to be played
		if (cardToPlay != null) {
			int trickStartIndex = knowledge.getCurrentTrick().getTrickNumberInGame() * TRICK_LENGTH + 1;
			setCardInputs(netInputs, PLAYER_LENGTH, knowledge.getPlayerPosition(), trickStartIndex, CARD_OFFSET,
					knowledge.getPlayerPosition(), cardToPlay);
		}

		return netInputs;
	}

	private void setTrickInputs(final double[] inputs, final int playerLength, final int trickLength,
			final int cardOffset) {

		List<Trick> trickList = new ArrayList<Trick>();
		trickList.addAll(knowledge.getCompletedTricks());
		trickList.add(knowledge.getCurrentTrick());
		for (Trick trick : trickList) {
			Player position = knowledge.getPlayerPosition();
			Player trickForeHand = trick.getForeHand();

			int trickStartIndex = trick.getTrickNumberInGame() * trickLength + 1;
			if (position.getLeftNeighbor() == trickForeHand) {
				inputs[trickStartIndex] = 1.0;
			} else if (position == trickForeHand) {
				inputs[trickStartIndex + playerLength] = 1.0;
			} else if (position.getRightNeighbor() == trickForeHand) {
				inputs[trickStartIndex + 2 * playerLength] = 1.0;
			}

			Player trickPlayer = trick.getForeHand();
			for (Card card : trick.getCardList()) {
				setCardInputs(inputs, playerLength, position, trickStartIndex, cardOffset, trickPlayer, card);
				trickPlayer = trickPlayer.getLeftNeighbor();
			}
		}
	}

	private void setCardInputs(final double[] inputs, final int playerLength, final Player position,
			final int trickStartIndex, final int cardOffset, final Player trickPlayer, final Card card) {
		int cardIndex = getNetInputIndex(knowledge.getGameType(), card);

		if (position.getLeftNeighbor() == trickPlayer) {
			inputs[cardIndex + trickStartIndex + cardOffset] = 1.0;
		} else if (position == trickPlayer) {
			inputs[cardIndex + trickStartIndex + cardOffset + playerLength] = 1.0;
		} else if (position.getRightNeighbor() == trickPlayer) {
			inputs[cardIndex + trickStartIndex + cardOffset + 2 * playerLength] = 1.0;
		}
	}

	private void setDeclarerInputs(final double[] inputs, final int NEURON_OFFSET) {
		if (!GameType.RAMSCH.equals(knowledge.getGameType())) {
			// in Ramsch games there is no declarer
			Player position = knowledge.getPlayerPosition();
			Player declarer = knowledge.getDeclarer();

			if (position.getLeftNeighbor() == declarer) {
				inputs[0] = 1.0;
			} else if (position == declarer) {
				inputs[NEURON_OFFSET] = 1.0;
			} else if (position.getRightNeighbor() == declarer) {
				inputs[2 * NEURON_OFFSET] = 1.0;
			}
		}
	}

	private void setKnowCards(final double[] inputs, final Player leftOpponent, final Player rightOpponent,
			final Card card, final int playerLength, final int knownCardsOffset) {

		GameType gameType = knowledge.getGame().getGameType();
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			if (knowledge.couldHaveCard(rightOpponent, card)) {
				inputs[netInputIndexForCard + knownCardsOffset] = COULD_HAVE_CARD;
			} else {
				inputs[netInputIndexForCard + knownCardsOffset] = HAS_CARD;
			}
		}

		// inputs for player
		if (knowledge.getOwnCards().contains(card)) {
			inputs[netInputIndexForCard + knownCardsOffset + playerLength] = HAS_CARD;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			if (knowledge.couldHaveCard(leftOpponent, card)) {
				inputs[netInputIndexForCard + knownCardsOffset + 2 * playerLength] = COULD_HAVE_CARD;
			} else {
				inputs[netInputIndexForCard + knownCardsOffset + 2 * playerLength] = HAS_CARD;
			}
		}
	}

	/**
	 * Returns the index for a card according a game type
	 * 
	 * @param gameType
	 *            Game type
	 * @return Index of card for the given game type
	 */
	static int getNetInputIndex(final GameType gameType, final Card card) {

		int result = 0;

		if (gameType == GameType.NULL) {

			result = getNetInputIndexNullGame(card);
		} else {

			result = getNetInputIndexSuitGrandRamschGame(gameType, card);
		}

		return result;
	}

	private static int getNetInputIndexSuitGrandRamschGame(final GameType gameType, final Card card) {

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

	private static int getNetInputIndexJack(final Suit jackSuit) {

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

	private static int getNetInputIndexNullGame(final Card card) {
		// TODO better order cards after frequency or points
		// normal null ordering
		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

	private static int getNetInputIndexGrandGame(final Card card) {
		// TODO better order cards after frequency or points
		// normal suit ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getSuitGrandOrder();
	}

	private static int getNetInputIndexRamschGame(final Card card) {
		// TODO better order cards after frequency or points
		// ramsch ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getRamschOrder();
	}

	private static int getNetInputIndexSuitGame(final GameType gameType, final Card card) {

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
	 * @see org.jskat.control.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		resetInputBuffer();
		bestGameTypeFromDiscarding = null;
	}

	private void resetInputBuffer() {
		for (double[] inputs : allInputs) {
			Arrays.fill(inputs, 0.0);
		}
	}

	/**
	 * @see org.jskat.control.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {

		if (isLearning && allInputs.size() > 0) {
			// adjust neural networks
			// from last trick to first trick
			adjustNeuralNetworks(allInputs);
		}
	}

	private void adjustNeuralNetworks(final List<double[]> inputs) {

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
			netOutputs[0] = output;

			List<INeuralNetwork> networks = SkatNetworks.getNetwork(knowledge.getGame().getGameType(), isDeclarer());

			int index = 0;
			for (double[] inputParam : inputs) {
				INeuralNetwork net = networks.get(index);
				net.adjustWeights(inputParam, netOutputs);
				index++;
			}
		}
	}

	// FIXME (jan 10.03.2012) code duplication with NNTrainer
	private static boolean isRamschGameWon(final GameSummary gameSummary, final Player currPlayer) {

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
	public void setLogger(final Log newLogger) {
		super.setLogger(newLogger);
		log = newLogger;
	}

}
