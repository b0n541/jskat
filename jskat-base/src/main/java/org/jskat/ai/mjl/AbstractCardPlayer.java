package org.jskat.ai.mjl;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the abstract card player strategy for Skat.
 * @author Markus J. Luzius <br>
 * created: 24.01.2011 18:20:09
 */
public abstract class AbstractCardPlayer implements CardPlayer {

    private static final Logger log = LoggerFactory.getLogger(AbstractCardPlayer.class);

    /**
     * Current hand available to the card selector.
     */
    protected CardList cards = null;

    /**
     * Creates a card selector for the supplied hand.
     * @param cards cards available for selection
     */
    protected AbstractCardPlayer(final CardList cards) {
        this.cards = cards;
    }

    @Override
    public void startGame(final ImmutablePlayerKnowledge knowledge) {
        log.debug("Starting game...");
        cards.sort(knowledge.getGameContract().gameType());
    }
}
