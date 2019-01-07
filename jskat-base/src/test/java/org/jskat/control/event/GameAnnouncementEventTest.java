/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class GameAnnouncementEventTest extends AbstractJSkatTest {

	private SkatGameData data;

	private GameAnnouncementEvent event;

	@Before
	public void setUp() {

		data = new SkatGameData();

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.GRAND);
		factory.setDiscardedCards(new CardList(Card.CJ, Card.SJ));

		event = new GameAnnouncementEvent(Player.FOREHAND,
				factory.getAnnouncement());
	}

	@Test
	public void skatGameDataAfterEvent() {

		assertNull(data.getAnnoucement().getGameType());
		assertThat(data.getAnnoucement().getDiscardedCards().size(), is(0));
		assertThat(data.getAnnoucement().isHand(), is(true));
		assertThat(data.getAnnoucement().isOuvert(), is(false));
		assertThat(data.getAnnoucement().isSchneider(), is(false));
		assertThat(data.getAnnoucement().isSchwarz(), is(false));

		event.processForward(data);

		assertThat(data.getAnnoucement().getGameType(), is(GameType.GRAND));
		assertThat(data.getAnnoucement().getDiscardedCards().size(), is(2));
		assertTrue(data.getAnnoucement().getDiscardedCards().contains(Card.CJ));
		assertTrue(data.getAnnoucement().getDiscardedCards().contains(Card.SJ));
		assertThat(data.getAnnoucement().isHand(), is(false));
		assertThat(data.getAnnoucement().isOuvert(), is(false));
		assertThat(data.getAnnoucement().isSchneider(), is(false));
		assertThat(data.getAnnoucement().isSchwarz(), is(false));
	}

	@Test
	public void skatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertNull(data.getAnnoucement().getGameType());
		assertThat(data.getAnnoucement().getDiscardedCards().size(), is(0));
		assertThat(data.getAnnoucement().isHand(), is(true));
		assertThat(data.getAnnoucement().isOuvert(), is(false));
		assertThat(data.getAnnoucement().isSchneider(), is(false));
		assertThat(data.getAnnoucement().isSchwarz(), is(false));
	}
}
