package org.jskat.ai.nn.input;

import java.util.Set;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public class PlayerPartyMadeCardsAndNextCardStrategy extends
		PlayerPartyMadeCardsStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = super.getNetworkInput(knowledge, cardToPlay);

		Trick trick = null;
		try {
			trick = (Trick) knowledge.getCurrentTrick().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (trick.getFirstCard() != null && trick.getSecondCard() != null
				&& trick.getThirdCard() == null) {

			trick.setThirdCard(cardToPlay);

			SkatRule rule = SkatRuleFactory.getSkatRules(knowledge
					.getGameType());
			Set<Player> partyMembers = getPartyMembers(knowledge);

			if (partyMembers.contains(rule.calculateTrickWinner(
					knowledge.getGameType(), trick))) {
				// trick was won by player's party
				for (Card card : trick.getCardList()) {
					result[getNetworkInputIndex(card)] = 1.0;
				}
			}
		}

		return result;
	}
}
