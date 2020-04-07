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
package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.DiscardSkatEvent;
import org.jskat.control.event.skatgame.PickUpSkatEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscardSkatEventTest extends AbstractJSkatTest {

    private SkatGameData data;

    private PickUpSkatEvent pickUpSkatEvent;
    private DiscardSkatEvent event;

    private CardList dealtPlayerCards;
    private CardList dealtSkatCards;
    private CardList discardedCards;
    private CardList newPlayerCards;

    @BeforeEach
    public void setUp() {

        dealtPlayerCards = new CardList(Card.DA, Card.DT, Card.DK, Card.DQ, Card.DJ, Card.D9, Card.D8, Card.D7, Card.HA, Card.HT);
        dealtSkatCards = new CardList(Card.CJ, Card.SJ);
        discardedCards = new CardList(Card.DA, Card.HA);
        newPlayerCards = new CardList(Card.DT, Card.DK, Card.DQ, Card.DJ, Card.D9, Card.D8, Card.D7, Card.HT, Card.CJ, Card.SJ);

        data = new SkatGameData();
        data.setDealtSkatCards(dealtSkatCards);
        data.addDealtCards(Player.FOREHAND, dealtPlayerCards);

        pickUpSkatEvent = new PickUpSkatEvent(Player.FOREHAND);
        pickUpSkatEvent.processForward(data);

        event = new DiscardSkatEvent(Player.FOREHAND, discardedCards);
    }

    @Test
    public void skatGameDataAfterEvent() {

        assertThat(data.getPlayerCards(Player.FOREHAND)).hasSize(12);
        assertThat(data.getSkat()).hasSize(0);

        event.processForward(data);

        assertThat(data.getPlayerCards(Player.FOREHAND)).hasSize(10);
        for (final Card card : newPlayerCards) {
            assertThat(data.getPlayerCards(Player.FOREHAND)).contains(card);
        }
        assertThat(data.getSkat()).containsExactlyInAnyOrder(Card.DA, Card.HA);
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertThat(data.getPlayerCards(Player.FOREHAND)).hasSize(12);
        for (final Card card : dealtPlayerCards) {
            assertTrue(data.getPlayerCards(Player.FOREHAND).contains(card));
        }
        assertThat(data.getSkat()).hasSize(0);
    }
}
