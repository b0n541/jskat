package org.jskat.ai.nn.input;

import java.util.HashSet;
import java.util.Set;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Player;

public class PlayerPartyMadeCardsStrategy extends AbstractInputStrategy
		implements InputStrategy {

	@Override
	public int getNeuronCount() {

		return 32;
	}

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		Set<Player> partyMembers = getPartyMembers(knowledge);

		for (Trick trick : knowledge.getCompletedTricks()) {
			if (partyMembers.contains(trick.getTrickWinner())) {
				// trick was won by player's party
				for (Card card : trick.getCardList()) {
					result[getNetworkInputIndex(card)] = 1.0;
				}
			}
		}

		return result;
	}

	private Set<Player> getPartyMembers(PlayerKnowledge knowledge) {

		Set<Player> result = new HashSet<Player>();
		if (knowledge.getDeclarer().equals(knowledge.getPlayerPosition())) {
			// player is declarer
			result.add(knowledge.getPlayerPosition());
		} else {
			// player is opponent
			result.add(knowledge.getDeclarer().getLeftNeighbor());
			result.add(knowledge.getDeclarer().getRightNeighbor());
		}
		return result;
	}

	private static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}
}
