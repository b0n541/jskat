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
import org.jskat.control.event.skatgame.CardDealEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DealCardEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private CardDealEvent event;

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();

        final Map<Player, CardList> playerCards = new HashMap<Player, CardList>();
        playerCards.put(Player.FOREHAND,
                new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ)));
        playerCards.put(Player.MIDDLEHAND,
                new CardList(Arrays.asList(Card.CA, Card.SA, Card.HA, Card.DA)));
        playerCards.put(Player.REARHAND,
                new CardList(Arrays.asList(Card.CT, Card.ST, Card.HT, Card.DT)));

        final CardList skat = new CardList(Card.D7, Card.D8);

        event = new CardDealEvent(playerCards, skat);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        checkPlayerCards(Player.FOREHAND, Card.CJ, Card.SJ, Card.HJ, Card.DJ);
        checkPlayerCards(Player.MIDDLEHAND, Card.CA, Card.SA, Card.HA, Card.DA);
        checkPlayerCards(Player.REARHAND, Card.CT, Card.ST, Card.HT, Card.DT);

        checkCardList(data.getDealtSkat(), Card.D7, Card.D8);
        checkCardList(data.getSkat(), Card.D7, Card.D8);
    }

    private void checkPlayerCards(final Player player, final Card... cards) {
        final CardList cardList = data.getDealtCards().get(player);
        checkCardList(cardList, cards);
    }

    private static void checkCardList(final CardList cardList, final Card... cards) {
        assertThat(cardList).containsExactlyInAnyOrder(cards);
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        for (final Player player : Player.values()) {
            checkCardList(data.getDealtCards().get(player));
        }
        checkCardList(data.getDealtSkat());
        checkCardList(data.getSkat());
    }
}
