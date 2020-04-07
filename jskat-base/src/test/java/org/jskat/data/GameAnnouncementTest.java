/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.data;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link GameAnnouncement}
 */
public class GameAnnouncementTest extends AbstractJSkatTest {

    private static final CardList VALID_DISCARDED_CARDS = new CardList(Card.CJ, Card.SJ);

    private static final Set<GameAnnouncement> validAnnouncements = new HashSet<>();

    @BeforeAll
    public static void createAllValidGameAnnouncements() {
        validAnnouncements.add(createNullWithoutDiscardedCards());
        validAnnouncements.add(createNull());
        validAnnouncements.add(createNullHand());
        validAnnouncements.add(createNullOuvert());
        validAnnouncements.add(createNullOuvertWithoutDiscardedCards());
        validAnnouncements.add(createNullHandOuvert());

        for (final GameType suitGrand : Arrays.asList(GameType.GRAND,
                GameType.CLUBS, GameType.SPADES, GameType.HEARTS,
                GameType.DIAMONDS)) {
            validAnnouncements
                    .add(createSuitGrandWithoutDiscardedCards(suitGrand));
            validAnnouncements.add(createSuitGrand(suitGrand));
            validAnnouncements.add(createSuitGrandHand(suitGrand));
            validAnnouncements.add(createSuitGrandHandSchneider(suitGrand));
            validAnnouncements
                    .add(createSuitGrandHandSchneiderSchwarz(suitGrand));
            validAnnouncements.add(createSuitGrandOuvert(suitGrand));
        }

        validAnnouncements.add(createRamsch());
        validAnnouncements.add(createPassedIn());

        assertThat(validAnnouncements.size()).isEqualTo(38);
    }

    private static GameAnnouncement createRamsch() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.RAMSCH;
        return result;
    }

    private static GameAnnouncement createPassedIn() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.PASSED_IN;
        return result;
    }

    private static GameAnnouncement createSuitGrand(final GameType gameType) {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        result.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    @Deprecated
    private static GameAnnouncement createSuitGrandWithoutDiscardedCards(
            final GameType gameType) {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        return result;
    }

    private static GameAnnouncement createSuitGrandHand(final GameType gameType) {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        result.hand = Boolean.TRUE;
        return result;
    }

    private static GameAnnouncement createSuitGrandHandSchneider(
            final GameType gameType) {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        result.hand = Boolean.TRUE;
        result.schneider = Boolean.TRUE;
        return result;
    }

    private static GameAnnouncement createSuitGrandHandSchneiderSchwarz(
            final GameType gameType) {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        result.hand = Boolean.TRUE;
        result.schneider = Boolean.TRUE;
        result.schwarz = Boolean.TRUE;
        return result;
    }

    private static GameAnnouncement createSuitGrandOuvert(
            final GameType gameType) {
        GameAnnouncement.getFactory();
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = gameType;
        result.hand = Boolean.TRUE;
        result.ouvert = Boolean.TRUE;
        result.schneider = Boolean.TRUE;
        result.schwarz = Boolean.TRUE;
        return result;
    }

    @Deprecated
    private static GameAnnouncement createNullWithoutDiscardedCards() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        return result;
    }

    private static GameAnnouncement createNull() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        result.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    private static GameAnnouncement createNullHand() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        result.hand = Boolean.TRUE;
        return result;
    }

    @Deprecated
    private static GameAnnouncement createNullOuvertWithoutDiscardedCards() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        result.ouvert = Boolean.TRUE;
        return result;
    }

    private static GameAnnouncement createNullOuvert() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        result.ouvert = Boolean.TRUE;
        result.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    private static GameAnnouncement createNullHandOuvert() {
        final GameAnnouncement result = new GameAnnouncement();
        result.gameType = GameType.NULL;
        result.hand = Boolean.TRUE;
        result.ouvert = Boolean.TRUE;
        return result;
    }

    @Test
    public void testEmptyGameType() {
        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        final GameAnnouncement announcement = factory.getAnnouncement();
        assertNull(announcement);
    }

    @Test
    public void testAllAnnouncementPermutations() {

        for (final GameType gameType : GameType.values()) {
            for (final Boolean isHand : getAllBooleanExpressions()) {
                for (final Boolean isOuvert : getAllBooleanExpressions()) {
                    for (final Boolean isSchneider : getAllBooleanExpressions()) {
                        for (final Boolean isSchwarz : getAllBooleanExpressions()) {
                            for (final CardList discardedCards : getAllDiscardedCardsExpressions()) {

                                final GameAnnouncement announcement = prepareAnnouncement(
                                        gameType, isHand, isOuvert,
                                        isSchneider, isSchwarz, discardedCards);

                                testAnnouncement(announcement, gameType,
                                        isHand, isOuvert, isSchneider,
                                        isSchwarz, discardedCards);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void testAnnouncement(final GameAnnouncement announcement,
                                         final GameType gameType, final Boolean isHand,
                                         final Boolean isOuvert, final Boolean isSchneider,
                                         final Boolean isSchwarz, final CardList discardedCards) {

        if (isValidAnnouncement(announcement)) {
            checkAnnouncement(announcement, gameType, isHand, isOuvert,
                    isSchneider, isSchwarz, discardedCards);
        } else {
            // cross check
            final GameAnnouncement ann = new GameAnnouncement();
            ann.gameType = gameType;
            ann.hand = isHand;
            ann.ouvert = isOuvert;
            ann.schneider = isSchneider;
            ann.schwarz = isSchwarz;
            ann.discardedCards = discardedCards;

            assertFalse(validAnnouncements.contains(ann));
            assertNull(announcement);
        }
    }

    private static GameAnnouncement prepareAnnouncement(final GameType gameType,
                                                        final Boolean isHand, final Boolean isOuvert,
                                                        final Boolean isSchneider, final Boolean isSchwarz,
                                                        final CardList discardedCards) {

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(gameType);
        factory.setHand(isHand);
        factory.setOuvert(isOuvert);
        factory.setSchneider(isSchneider);
        factory.setSchwarz(isSchwarz);
        factory.setDiscardedCards(discardedCards);

        final GameAnnouncement announcement = factory.getAnnouncement();
        return announcement;
    }

    private static void checkAnnouncement(final GameAnnouncement announcement,
                                          final GameType gameType, final Boolean isHand,
                                          final Boolean isOuvert, final Boolean isSchneider,
                                          final Boolean isSchwarz, final CardList discardedCards) {
        assertThat(announcement.gameType).isEqualTo(gameType);
        assertThat(announcement.hand).isEqualTo(isHand);
        assertThat(announcement.ouvert).isEqualTo(isOuvert);
        assertThat(announcement.schneider).isEqualTo(isSchneider);
        assertThat(announcement.schwarz).isEqualTo(isSchwarz);
        assertThat(announcement.discardedCards).isEqualTo(discardedCards);
    }

    private static boolean isValidAnnouncement(
            final GameAnnouncement announcement) {
        return validAnnouncements.contains(announcement);
    }

    private static List<CardList> getAllDiscardedCardsExpressions() {
        return Arrays.asList(new CardList(),
                new CardList(Arrays.asList(Card.CJ)), VALID_DISCARDED_CARDS,
                new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ)));
    }

    private static List<Boolean> getAllBooleanExpressions() {
        return Arrays.asList(Boolean.TRUE, Boolean.FALSE);
    }
}
