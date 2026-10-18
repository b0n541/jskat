package org.jskat.ai.newalgorithm;

import org.jskat.ai.newalgorithm.exception.IllegalMethodException;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Defines the algorithm opponent null type.
 */
public class AlgorithmOpponentNull extends AbstractAlgorithmAI {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmOpponentNull.class);

    AlgorithmOpponentNull(final AlgorithmAI p, final GameType pGameType) {
        super(p, pGameType);

        log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
                + this.getClass().getName());
    }

    @Override
    protected Card startGame() {
        log.debug("OpponentNull starts Game: " + knowledge.getPlayerPosition());

        return playStartGameCard(knowledge.getOwnCards(),
                knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
                oSituation, knowledge.getDeclarer());
    }

    @Override
    protected Card playForehandCard() {
        log.debug("OpponentNull plays Forehand-Card: "
                + knowledge.getPlayerPosition());

        return playForehandCard(knowledge.getOwnCards(),
                knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
                oSituation, knowledge.getDeclarer());
    }

    @Override
    protected Card playMiddlehandCard() {
        log.debug("OpponentNull plays Middlehand-Card: "
                + knowledge.getPlayerPosition());

        return playMiddlehandCard(
                myPlayer.getPlayableCards(knowledge.getTrickCards()),
                knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
                oSituation, knowledge.getDeclarer());
    }

    @Override
    protected Card playRearhandCard() {
        log.debug("OpponentNull plays Rearhand-Card: "
                + knowledge.getPlayerPosition());

        return playRearhandCard(
                myPlayer.getPlayableCards(knowledge.getTrickCards()),
                knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
                oSituation, knowledge.getDeclarer());
    }

    @Override
    public CardList discardSkat(final BidEvaluator bidEvaluator) {
        throw new IllegalMethodException(
                "AlgorithmOpponentNull has nothing to discard!");
    /**
     * Provides this API member.
     */
    }

    // static methods for creating JUnit-tests and test cardplaybehavior
    /**
     * Play start game card.
     * @param pCards p cards value.
     * @param pTrickCards p trick cards value.
     * @param pPlayedCards p played cards value.
     * @param pNotOpponentCards p not opponent cards value.
     * @param pSituation p situation value.
     * @param pDeclarer p declarer value.
     * @return play start game card result.
     */
    public static Card playStartGameCard(final CardList pCards, final CardList pTrickCards,
                                         final CardList pPlayedCards, final CardList pNotOpponentCards,
                                         final Situation pSituation, final Player pDeclarer) {
        pCards.sort(pSituation.getGameType());
        final boolean tDeclarerInMiddle = Player.FOREHAND.getLeftNeighbor() == pDeclarer;

        final CardList tPossibleHighCard = new CardList();
        final CardList tPossibleLowCard = new CardList();

        return getRandomAllowedCard(pCards, null, pSituation.getGameType());
    /**
     * Provides this API member.
     */
    }

    /**
     * Play forehand card.
     * @param pCards p cards value.
     * @param pTrickCards p trick cards value.
     * @param pPlayedCards p played cards value.
     * @param pNotOpponentCards p not opponent cards value.
     * @param pSituation p situation value.
     * @param pDeclarer p declarer value.
     * @return play forehand card result.
     */
    public static Card playForehandCard(final CardList pCards, final CardList pTrickCards,
                                        final CardList pPlayedCards, final CardList pNotOpponentCards,
                                        final Situation pSituation, final Player pDeclarer) {
        pCards.sort(pSituation.getGameType());
        final boolean tDeclarerInMiddle = Player.FOREHAND.getLeftNeighbor() == pDeclarer;

        final CardList possibleCards = new CardList();

        /**
         * Returns or indicates random allowed card.
         * @param pCards p cards value.
         * @param null null value.
         * @return get random allowed card result.
         */
        return getRandomAllowedCard(pCards, null, pSituation.getGameType());
    }

    /**
     * Play middlehand card.
     * @param pCards p cards value.
     * @param pTrickCards p trick cards value.
     * @param pPlayedCards p played cards value.
     * @param pNotOpponentCards p not opponent cards value.
     * @param pSituation p situation value.
     * @param pDeclarer p declarer value.
     * @return play middlehand card result.
     */
    public static Card playMiddlehandCard(final CardList pCards,
                                          final CardList pTrickCards, final CardList pPlayedCards,
                                          final CardList pNotOpponentCards, final Situation pSituation, final Player pDeclarer) {
        pCards.sort(pSituation.getGameType());
        final boolean tDeclarerInForhand = Player.MIDDLEHAND.getRightNeighbor() == pDeclarer;
        final Card tForehandCard = pTrickCards.get(0);
        final Suit tSuit = tForehandCard.getSuit();
        ArrayList<Suit> tDeclarerBlankSuits = pSituation
                .getLeftPlayerBlankSuits();
        if (Player.MIDDLEHAND.getRightNeighbor() == pDeclarer) {
            tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
        }

        final CardList possibleCards = new CardList();

        return getRandomAllowedCard(pCards, tForehandCard,
                pSituation.getGameType());
    }

    /**
     * Play rearhand card.
     * @param pCards p cards value.
     * @param pTrickCards p trick cards value.
     * @param pPlayedCards p played cards value.
     * @param pNotOpponentCards p not opponent cards value.
     * @param pSituation p situation value.
     * @param pDeclarer p declarer value.
     * @return play rearhand card result.
     */
    public static Card playRearhandCard(final CardList pCards, final CardList pTrickCards,
                                        final CardList pPlayedCards, final CardList pNotOpponentCards,
                                        final Situation pSituation, final Player pDeclarer) {
        pCards.sort(pSituation.getGameType());
        final Card tForehandCard = pTrickCards.get(0);
        final Card tMiddlehandCard = pTrickCards.get(1);
        final CardList possibleCards = new CardList();

        Card tCardToBeat = tForehandCard;
        if (tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat)) {
            tCardToBeat = tMiddlehandCard;
        }
        final Suit tSuit = tCardToBeat.getSuit();

        ArrayList<Suit> tDeclarerBlankSuits = pSituation
                .getLeftPlayerBlankSuits();
        if (Player.REARHAND.getRightNeighbor() == pDeclarer) {
            tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
        }

        return getRandomAllowedCard(pCards, tForehandCard,
                pSituation.getGameType());
    }
}
