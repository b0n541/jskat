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
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class GameAnnouncementEventTest extends AbstractJSkatTest {

    private SkatGameData data;

    private GameAnnouncementEvent event;

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.GRAND);
        factory.setDiscardedCards(new CardList(Card.CJ, Card.SJ));

        event = new GameAnnouncementEvent(Player.FOREHAND,
                factory.getAnnouncement());
    }

    @Test
    public void skatGameDataAfterEvent() {

        assertNull(data.getAnnoucement().getGameType());
        assertThat(data.getAnnoucement().getDiscardedCards()).hasSize(0);
        assertTrue(data.getAnnoucement().isHand());
        assertFalse(data.getAnnoucement().isOuvert());
        assertFalse(data.getAnnoucement().isSchneider());
        assertFalse(data.getAnnoucement().isSchwarz());

        event.processForward(data);

        assertThat(data.getAnnoucement().getGameType()).isEqualTo(GameType.GRAND);
        assertThat(data.getAnnoucement().getDiscardedCards()).hasSize(2);
        assertThat(data.getAnnoucement().getDiscardedCards()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertFalse(data.getAnnoucement().isHand());
        assertFalse(data.getAnnoucement().isOuvert());
        assertFalse(data.getAnnoucement().isSchneider());
        assertFalse(data.getAnnoucement().isSchwarz());
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertNull(data.getAnnoucement().getGameType());
        assertThat(data.getAnnoucement().getDiscardedCards()).hasSize(0);
        assertTrue(data.getAnnoucement().isHand());
        assertFalse(data.getAnnoucement().isOuvert());
        assertFalse(data.getAnnoucement().isSchneider());
        assertFalse(data.getAnnoucement().isSchwarz());
    }
}
