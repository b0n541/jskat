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
package org.jskat.control;

import java.util.ArrayList;
import java.util.List;

import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.control.event.skatgame.GameFinishEvent;
import org.jskat.control.event.skatgame.GameStartEvent;
import org.jskat.control.event.skatgame.SkatGameEvent;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.event.table.TrickCompletedEvent;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.JSkatView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for replaying skat games
 */
public class SkatGameReplayer {

	private final static Logger LOG = LoggerFactory.getLogger(SkatGameReplayer.class);

	private final JSkatView view;
	private final String tableName;
	private SkatGameData data;
	private final List<SkatGameEvent> gameMoves = new ArrayList<>();
	private int currentMove = 0;

	public SkatGameReplayer(final JSkatView view, final String tableName,
			final List<SkatGameEvent> gameMoves) throws InterruptedException {
		this.view = view;
		this.tableName = tableName;
		this.gameMoves.addAll(gameMoves);
		resetReplay();
	}

	public void toStart() {
		resetReplay();
	}

	public void oneMoveBackward() {
		if (currentMove > 1) {
			currentMove--;
			gameMoves.get(currentMove).processBackward(data);
		}
	}

	public void oneMoveForward() {
		if (hasMoreMoves()) {
			oneStepForward();
		}
	}

	public void toEnd() {
		while (hasMoreMoves()) {
			oneStepForward();
		}
	}

	private void resetReplay() {
		currentMove = 0;
		data = new SkatGameData();
		// game start
		oneStepForward();
		// dealing
		oneStepForward();
	}

	private boolean hasMoreMoves() {
		return currentMove < gameMoves.size();
	}

	private void setGameState(final SkatGameEvent event) {
		if (event instanceof GameStartEvent) {
			view.setGameState(tableName, GameState.BIDDING);
		} else if (event instanceof BidEvent) {
			view.setGameState(tableName, GameState.BIDDING);
		} else if (event instanceof GameAnnouncementEvent) {
			view.setGameState(tableName, GameState.DECLARING);
		} else if (event instanceof TrickCardPlayedEvent) {
			view.setGameState(tableName, GameState.TRICK_PLAYING);
		} else if (event instanceof GameFinishEvent) {
			view.setGameState(tableName, GameState.GAME_OVER);
		}
	}

	private void oneStepForward() {
		final SkatGameEvent event = gameMoves.get(currentMove);

		setGameState(event);

		// TODO: code duplication with SkatGame.playCard()
		if (event instanceof TrickCardPlayedEvent
				&& data.getCurrentTrick() != null
				&& data.getCurrentTrick().getFirstCard() == null) {
			JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(
					new TrickCompletedEvent(data.getLastCompletedTrick()));
		} else if (event instanceof GameFinishEvent) {
			JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, data
					.getCardsAfterDiscard()));
		}

		event.processForward(data);

		JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, event));

		currentMove++;
	}
}
