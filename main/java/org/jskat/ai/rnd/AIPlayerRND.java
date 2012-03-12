package org.jskat.ai.rnd;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Random player for testing purposes
 */
public class AIPlayerRND extends AbstractJSkatPlayer {

	private static Log log = LogFactory.getLog(AIPlayerRND.class);

	/**
	 * Random generator
	 */
	private Random rand = new Random();

	/**
	 * Creates a new instance of AIPlayerRND
	 */
	public AIPlayerRND() {

		this("unknown"); //$NON-NLS-1$
	}

	/**
	 * Creates a new instance of AIPlayerRND
	 * 
	 * @param newPlayerName
	 *            Player's name
	 */
	public AIPlayerRND(String newPlayerName) {

		log.debug("Constructing new AIPlayerRND"); //$NON-NLS-1$
		setPlayerName(newPlayerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean pickUpSkat() {

		return rand.nextBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		// select a random game type (without RAMSCH and PASSED_IN)
		factory.setGameType(GameType.values()[rand.nextInt(GameType.values().length - 2)]);
		factory.setOuvert(Boolean.valueOf(rand.nextBoolean()));

		return factory.getAnnouncement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bidMore(int nextBidValue) {
		int result = -1;

		if (rand.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean holdBid(int currBidValue) {
		return rand.nextBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Card playCard() {

		int index = -1;

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		// then choose a random one
		index = rand.nextInt(possibleCards.size());

		log.debug("choosing card " + index); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CardList discardSkat() {
		CardList cards = knowledge.getMyCards();
		CardList result = new CardList();

		log.debug("Player cards before discarding: " + cards); //$NON-NLS-1$

		// just discard two random cards
		result.add(cards.remove(rand.nextInt(cards.size())));
		result.add(cards.remove(rand.nextInt(cards.size())));

		log.debug("Player cards after discarding: " + cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparateForNewGame() {
		// nothing to do for AIPlayerRND
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalizeGame() {
		// nothing to do for AIPlayerRND
	}
}