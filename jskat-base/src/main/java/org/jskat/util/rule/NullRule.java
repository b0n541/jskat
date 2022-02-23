package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.util.*;

/**
 * Implementation of skat rules for Null games
 */
public class NullRule extends AbstractSkatRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGameValueForWonGame(final SkatGameData gameData) {

        Integer gameValue = SkatConstants.getGameBaseValue(
                gameData.getGameType(), gameData.isHand(), gameData.isOuvert());

        if (gameData.isContra()) {
            gameValue *= 2;
            if (gameData.isRe()) {
                gameValue *= 2;
            }
        }

        return gameValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calcGameResult(final SkatGameData gameData) {

        int result = getGameValueForWonGame(gameData);

        if (gameData.isGameLost()) {

            // Lost game is always counted double
            result = result * -2;
        }

        return result;
    }

    /**
     * @see SkatRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Override
    public boolean isCardBeatsCard(
            @SuppressWarnings("unused") final GameType gameType,
            final Card cardToBeat, final Card card) {

        boolean result = false;

        if (cardToBeat.getSuit() == card.getSuit()) {

            if (cardToBeat.getNullOrder() < card.getNullOrder()) {

                result = true;
            }
        }

        return result;
    }

    /**
     * @see SkatRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Override
    public boolean isCardAllowed(final GameType gameType,
                                 final Card initialCard, final CardList hand, final Card card) {

        boolean result = false;

        if (initialCard == null) {
            // no intial card is given --> every card is allowed
            result = true;
        } else if (card.getSuit() == initialCard.getSuit()) {
            // card must serve same suit
            result = true;

        } else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
            // hand has no card of same suit --> every card is allowed
            result = true;
        }

        return result;
    }

    /**
     * @see SkatRule#isGameWon(SkatGameData)
     */
    @Override
    public boolean isGameWon(final SkatGameData gameData) {

        return gameData.isPlayerMadeNoTrick(gameData.getDeclarer()) && !isOverbid(gameData);
    }

    /**
     * @see SkatRule#hasSuit(GameType, CardList, Suit)
     */
    @Override
    public boolean hasSuit(final GameType gameType, final CardList hand,
                           final Suit suit) {

        boolean result = false;

        int index = 0;
        while (!result && index < hand.size()) {

            if (hand.get(index).getSuit() == suit) {

                result = true;
            }

            index++;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMultiplier(
            @SuppressWarnings("unused") final SkatGameData gameData) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayWithJacks(
            @SuppressWarnings("unused") final SkatGameData gameData) {
        return false;
    }
}
