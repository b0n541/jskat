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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.PickUpSkatEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class PickUpSkatEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private PickUpSkatEvent event;

	@Before
	public void setUp() {
		data = new SkatGameData();

		CardList skat = new CardList(Card.CJ, Card.SJ);

		data.setDealtSkatCards(skat);
		event = new PickUpSkatEvent(Player.FOREHAND);
	}

	@Test
	public void skatGameDataAfterEvent() {

		event.processForward(data);

		assertThat(data.isHand(), is(false));
		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(2));
		assertThat(data.getPlayerCards(Player.FOREHAND),
				containsInAnyOrder(Card.CJ, Card.SJ));
		assertThat(data.getSkat().size(), is(0));
		assertThat(data.isSkatPickedUp(), is(true));
	}

	@Test
	public void skatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertThat(data.isHand(), is(true));
		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getSkat().size(), is(2));
		assertThat(data.getSkat(), containsInAnyOrder(Card.CJ, Card.SJ));
		assertThat(data.isSkatPickedUp(), is(false));
	}
}
