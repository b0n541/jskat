package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
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
	public void SkatGameDataAfterEvent() {

		event.processForward(data);

		assertThat(data.isHand(), is(false));
		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(2));
		assertThat(data.getPlayerCards(Player.FOREHAND),
				hasItems(Card.CJ, Card.SJ));
		assertThat(data.getSkat().size(), is(0));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertThat(data.isHand(), is(true));
		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getSkat().size(), is(2));
		assertThat(data.getSkat(), hasItems(Card.CJ, Card.SJ));
	}
}
