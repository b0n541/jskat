package org.jskat.ai.mjl;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <br>
 * created: 24.01.2011 18:20:09
 */
public abstract class AbstractCardPlayer implements CardPlayer {

    private static final Logger log = LoggerFactory.getLogger(AbstractCardPlayer.class);

    protected CardList cards = null;

    protected AbstractCardPlayer(final CardList cards) {
        this.cards = cards;
    }

    @Override
    public void startGame(final ImmutablePlayerKnowledge knowledge) {
        log.debug("Starting game...");
        cards.sort(knowledge.getGameContract().gameType());
    }
}
