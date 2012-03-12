package org.jskat.ai.algorithmic;

import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 08.07.2011 17:22:49
 *
 */
public interface IAlgorithmicAIPlayer {

	/** Prompts the ai player instance to play a card
	 * @return the card to play
	 */
	Card playCard();

	/** Asks the ai player instance to discard two skat cards
	 * @param bidEvaluator
	 * @return the discarded skat cards
	 */
	CardList discardSkat(BidEvaluator bidEvaluator);

}
