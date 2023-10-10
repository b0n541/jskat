package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardList;

import java.util.Random;

/**
 * Test player throws an exception during card play.
 */
public class PlayNotAllowedCardTestPlayer extends AIPlayerRND {

    /**
     * Random generator.
     */
    private final Random random = new Random();

    @Override
    public final Card playCard() {
        final CardList notAllowedCards = new CardList(knowledge.getOwnCards());
        notAllowedCards.removeAll(getPlayableCards(knowledge.getTrickCards()));

        if (notAllowedCards.size() > 0) {
            return notAllowedCards.get(random.nextInt(notAllowedCards.size()));
        } else {
            // sometimes all cards are allowed
            return super.playCard();
        }
    }
}
