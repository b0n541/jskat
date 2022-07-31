package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of skat rules
 */
public abstract class AbstractSkatRule implements SkatRule {

    private static final Logger log = LoggerFactory.getLogger(AbstractSkatRule.class);

    /**
     * Checks whether a game was overbid
     *
     * @param gameData Game data
     * @return TRUE if the game was overbid
     */
    @Override
    public boolean isOverbid(final SkatGameData gameData) {
        return gameData.getMaxBidValue() > getGameValueForWonGame(gameData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player calculateTrickWinner(final GameType gameType,
                                       final Trick trick) {

        Player trickWinner = null;
        Card first = trick.getFirstCard();
        Card second = trick.getSecondCard();
        Card third = trick.getThirdCard();
        Player trickForeHand = trick.getForeHand();

        if (isCardBeatsCard(gameType, first, second)) {

            if (isCardBeatsCard(gameType, second, third)) {
                // trick winner is rear hand
                trickWinner = trickForeHand.getRightNeighbor();
            } else {
                // trick winner is middle hand
                trickWinner = trickForeHand.getLeftNeighbor();
            }
        } else {

            if (isCardBeatsCard(gameType, first, third)) {
                // trick winner is rear hand
                trickWinner = trickForeHand.getRightNeighbor();
            } else {
                // trick winner is fore hand
                trickWinner = trickForeHand;
            }
        }

        log.debug("Trick fore hand: " + trickForeHand);
        log.debug("Trick winner: " + trickWinner);

        return trickWinner;
    }

    @Override
    public int calcOverbidGameResult(SkatGameData gameData) {
        int declarerBidValue = gameData.getMaxBidValue();
        int gameBaseValue = SkatConstants.getGameBaseValue(gameData.getAnnoucement().getGameType(), false, false);

        int overbidMultiplier = 0;
        while (overbidMultiplier * gameBaseValue < declarerBidValue) {
            overbidMultiplier++;
        }

        return overbidMultiplier * gameBaseValue * -2;
    }
}
