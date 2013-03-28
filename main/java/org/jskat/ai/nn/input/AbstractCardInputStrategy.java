package org.jskat.ai.nn.input;

import org.jskat.util.Card;

/**
 * Abstract card related strategy for network inputs
 */
public abstract class AbstractCardInputStrategy extends AbstractInputStrategy {

	/**
	 * Gets the index for a card for network inputs
	 * 
	 * @param card
	 *            Card
	 * @return Index of card in network input
	 */
	protected static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

	@Override
	public final int getNeuronCount() {
		return 32;
	}
}
