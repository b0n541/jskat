package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;

import java.util.Random;

/**
 * Test player throws an exception during card play.
 */
public class PlayNonPossessingCardTestPlayer extends AIPlayerRND {

    /**
     * Random generator.
     */
    private final Random random = new Random();

    @Override
    public final Card playCard() {
        final CardDeck unpossessedCards = new CardDeck();
        unpossessedCards.removeAll(knowledge.getOwnCards());

        return unpossessedCards.get(random.nextInt(unpossessedCards.size()));
    }
}
