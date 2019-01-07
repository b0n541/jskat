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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class TrickCardPlayedEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private List<TrickCardPlayedEvent> eventList;

	@Before
	public void setUp() {
		data = new SkatGameData();
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		data.setAnnouncement(factory.getAnnouncement());
		data.addPlayerCard(Player.FOREHAND, Card.CJ);
		data.addPlayerCard(Player.FOREHAND, Card.CA);
		data.addPlayerCard(Player.FOREHAND, Card.C7);
		data.addPlayerCard(Player.MIDDLEHAND, Card.SJ);
		data.addPlayerCard(Player.MIDDLEHAND, Card.SA);
		data.addPlayerCard(Player.MIDDLEHAND, Card.C8);
		data.addPlayerCard(Player.REARHAND, Card.HJ);
		data.addPlayerCard(Player.REARHAND, Card.HA);
		data.addPlayerCard(Player.REARHAND, Card.C9);

		eventList = new ArrayList<>();
		eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.CJ));
		eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SJ));
		eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.HJ));
		eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.C7));
		eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.C8));
		eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.C9));
		eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.HA));
		eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.CA));
		eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SA));
	}

	@Test
	public void skatGameDataAfterEvent() {

		Iterator<TrickCardPlayedEvent> events = eventList.iterator();

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CJ, Card.CA,
				Card.C7);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SJ, Card.SA,
				Card.C8);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA,
				Card.C9);
		assertNull(data.getCurrentTrick());

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SJ, Card.SA,
				Card.C8);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA,
				Card.C9);
		assertTrick(data.getCurrentTrick(), Card.CJ, null, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA,
				Card.C9);
		assertTrick(data.getCurrentTrick(), Card.CJ, Card.SJ, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
		assertTrick(data.getLastCompletedTrick(), Card.CJ, Card.SJ, Card.HJ,
				Player.FOREHAND);
		assertEmptyTrick(data.getCurrentTrick());

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
		assertTrick(data.getCurrentTrick(), Card.C7, null, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
		assertTrick(data.getCurrentTrick(), Card.C7, Card.C8, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HA);
		assertTrick(data.getLastCompletedTrick(), Card.C7, Card.C8, Card.C9,
				Player.REARHAND);
		assertEmptyTrick(data.getCurrentTrick());

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertTrick(data.getCurrentTrick(), Card.HA, null, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND));
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertTrick(data.getCurrentTrick(), Card.HA, Card.CA, null, null);

		events.next().processForward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND));
		assertHand(data.getPlayerCards(Player.MIDDLEHAND));
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertTrick(data.getLastCompletedTrick(), Card.HA, Card.CA, Card.SA,
				Player.FOREHAND);
		assertEmptyTrick(data.getCurrentTrick());
	}

	private void assertEmptyTrick(Trick trick) {
		assertNull(trick.getFirstCard());
		assertNull(trick.getSecondCard());
		assertNull(trick.getThirdCard());
		assertNull(trick.getTrickWinner());
	}

	private void assertHand(CardList hand, Card... cards) {
		assertThat(hand.size(), is(cards.length));
		assertThat(hand, containsInAnyOrder(cards));
	}

	@Test
	public void skatGameDataBeforeEvent() {

		for (TrickCardPlayedEvent event : eventList) {
			event.processForward(data);
		}

		List<TrickCardPlayedEvent> reverseEventList = new ArrayList<TrickCardPlayedEvent>(
				eventList);
		Collections.reverse(reverseEventList);
		Iterator<TrickCardPlayedEvent> events = reverseEventList.iterator();

		assertHand(data.getPlayerCards(Player.FOREHAND));
		assertHand(data.getPlayerCards(Player.MIDDLEHAND));
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertEmptyTrick(data.getCurrentTrick());
		assertTrick(data.getLastCompletedTrick(), Card.HA, Card.CA, Card.SA,
				Player.FOREHAND);

		events.next().processBackward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND));
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertTrick(data.getCurrentTrick(), Card.HA, Card.CA, null, null);

		events.next().processBackward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND));
		assertTrick(data.getCurrentTrick(), Card.HA, null, null, null);

		events.next().processBackward(data);

		assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
		assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
		assertHand(data.getPlayerCards(Player.REARHAND), Card.HA);
		assertEmptyTrick(data.getCurrentTrick());
		assertTrick(data.getLastCompletedTrick(), Card.C7, Card.C8, Card.C9,
				Player.REARHAND);
	}

	private void assertTrick(Trick trick, Card firstCard, Card secondCard,
			Card thirdCard, Player trickWinner) {
		assertThat(trick.getFirstCard(), is(firstCard));
		assertThat(trick.getSecondCard(), is(secondCard));
		assertThat(trick.getThirdCard(), is(thirdCard));
		assertThat(trick.getTrickWinner(), is(trickWinner));
	}
}
