package org.jskat.ai.nn.input;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public class CurrentTrickAndNextCardStrategy extends CurrentTrickStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		Trick trick = null;
		try {
			trick = (Trick) knowledge.getCurrentTrick().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (trick.getFirstCard() != null && trick.getSecondCard() != null
				&& trick.getThirdCard() == null) {
			// trick will be completed by next card
			trick.setThirdCard(cardToPlay);

			SkatRule rule = SkatRuleFactory.getSkatRules(knowledge
					.getGameType());

			result[getTrickForehand(rule.calculateTrickWinner(
					knowledge.getGameType(), trick))] = 1.0;

		} else {
			// set trick forehand position
			result[getTrickForehand(trick.getForeHand())] = 1.0;

			// set already played cards
			if (trick.getFirstCard() != null) {
				result[3 + getNetworkInputIndex(trick.getFirstCard())] = 1.0;
			}
			if (trick.getSecondCard() != null) {
				result[3 + 32 + getNetworkInputIndex(trick.getSecondCard())] = 1.0;
			}
			if (trick.getThirdCard() != null) {
				result[3 + 64 + getNetworkInputIndex(trick.getThirdCard())] = 1.0;
			}
		}

		return result;
	}
}
