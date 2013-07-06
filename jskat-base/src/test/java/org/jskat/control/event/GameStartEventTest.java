package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class GameStartEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private GameStartEvent event;

	@Before
	public void setUp() {
		data = new SkatGameData();
		event = new GameStartEvent(Player.FOREHAND);
	}

	@Test
	public void SkatGameDataAfterEvent() {

		event.processForward(data);

		assertThat(data.getDealer(), is(equalTo(Player.FOREHAND)));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertNull(data.getDealer());
	}
}
