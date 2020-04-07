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
import org.jskat.control.event.skatgame.PickUpSkatEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PickUpSkatEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private PickUpSkatEvent event;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();

        final CardList skat = new CardList(Card.CJ, Card.SJ);

        data.setDealtSkatCards(skat);
        event = new PickUpSkatEvent(Player.FOREHAND);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        assertFalse(data.isHand());
        assertThat(data.getPlayerCards(Player.FOREHAND)).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertThat(data.getSkat()).hasSize(0);
        assertTrue(data.isSkatPickedUp());
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertTrue(data.isHand());
        assertThat(data.getPlayerCards(Player.FOREHAND)).hasSize(0);
        assertThat(data.getSkat()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertFalse(data.isSkatPickedUp());
    }
}
