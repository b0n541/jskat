package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements some methods of the interface SkatRules that are the same in suit
 * and grand games.
 */
public abstract class SuitGrandRule extends SuitGrandRamschRule {

    private static final Logger log = LoggerFactory.getLogger(SuitGrandRule.class);

    /**
     * @see SkatRule#isGameWon(SkatGameData)
     */
    @Override
    public final boolean isGameWon(final SkatGameData gameData) {

        return gameData.getScore(gameData.getDeclarer()) >= getMinimumWinningScore(gameData) && !isOverbid(gameData);
    }

    private static int getMinimumWinningScore(final SkatGameData gameData) {
        int result = SkatConstants.MIN_WINNING_POINTS;

        if (gameData.isSchneiderAnnounced()) {
            result = SkatConstants.MIN_SCHNEIDER_WINNING_POINTS;
        }
        if (gameData.isSchwarzAnnounced()) {
            result = SkatConstants.MIN_SCHWARZ_WINNING_POINTS;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGameValueForWonGame(final SkatGameData gameData) {
        int multiplier = getMultiplier(gameData);

        log.debug("calcSuitResult: after Jacks and Trump: multiplier " + multiplier);

        // TODO add option: Hand game is only counted when game was not lost
        // if (gameData.isHand() && !gameData.isGameLost()) {
        if (gameData.isHand()) {
            multiplier++;
        }

        if (gameData.isOuvert()) {
            multiplier++;
        }

        if (gameData.isSchneider()) {
            multiplier++;
            if (gameData.isHand() && gameData.isSchneiderAnnounced()) {
                multiplier++;
            }
            log.debug("calcSuitResult: Schneider: multiplier " + multiplier);
        }

        if (gameData.isSchwarz()) {
            multiplier++;
            if (gameData.isHand() && gameData.isSchwarzAnnounced()) {
                multiplier++;
            }
            log.debug("calcSuitResult: Schwarz: multiplier " + multiplier);
        }

        if (gameData.isContra()) {
            multiplier *= 2;
            log.debug("calcSuitResult: Contra: multiplier " + multiplier);

            if (gameData.isRe()) {
                multiplier *= 2;
                log.debug("calcSuitResult: Re: multiplier " + multiplier);
            }
        }

        final int gameValue = SkatConstants.getGameBaseValue(gameData.getGameType(),
                gameData.isHand(), gameData.isOuvert());

        log.debug("gameValue" + gameValue);

        return gameValue * multiplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calcGameResult(final SkatGameData gameData) {

        int result = getGameValueForWonGame(gameData);

        if (gameData.isGameLost()) {

            // penalty if game lost
            result = result * -2;
        }

        return result;
    }

    /**
     * Gets the multiplier for a suit or grand game
     *
     * @param gameData Game data
     * @return Multiplier
     */
    @Override
    public int getMultiplier(final SkatGameData gameData) {

        int result = 0;

        final CardList declarerCards = getDeclarerCards(gameData);

        result = getMultiplier(declarerCards, gameData.getGameType());

        return result;
    }

    private CardList getDeclarerCards(final SkatGameData gameData) {
        final CardList declarerCards = new CardList(gameData.getDealtCards().get(
                gameData.getDeclarer()));
        declarerCards.addAll(gameData.getDealtSkat());
        return declarerCards;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayWithJacks(final SkatGameData gameData) {
        final CardList declarerCards = getDeclarerCards(gameData);

        return declarerCards.contains(Card.CJ);
    }

    /**
     * Gets the multiplier for a {@link CardList} and a {@link GameType}
     *
     * @param cards    Card list
     * @param gameType Game type
     * @return Multiplier
     */
    public abstract int getMultiplier(CardList cards, GameType gameType);

    /**
     * Checks whether a game was a schneider game<br>
     * schneider means one party made only 30 points or below
     *
     * @param gameData Game data
     * @return TRUE if the game was a schneider game
     */
    public static boolean isSchneider(final SkatGameData gameData) {

        final boolean result = gameData.getScore(Player.FOREHAND) < 31
                || gameData.getScore(Player.MIDDLEHAND) < 31
                || gameData.getScore(Player.REARHAND) < 31;

        // one player was schneider

        return result;
    }

    /**
     * Checks whether a game was a schwarz game<br>
     * schwarz means one party made no trick or a wrong card was played
     *
     * @param gameData Game data
     * @return TRUE, if the game is a schwarz game
     */
    public static boolean isSchwarz(final SkatGameData gameData) {
        return gameData.isPlayerMadeNoTrick()
                || gameData.getResult().isSchwarz();
    }
}
