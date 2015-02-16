/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.control;

import java.util.ArrayList;
import java.util.List;

import org.jskat.control.event.skatgame.SkatGameEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.data.SkatGameData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for replaying skat games
 */
public class SkatGameReplayer {

	private final static Logger LOG = LoggerFactory
			.getLogger(SkatGameReplayer.class);

	private final String tableName;
	private SkatGameData data;
	private final List<SkatGameEvent> gameMoves = new ArrayList<>();
	private int currentMove = 0;

	public SkatGameReplayer(String tableName, List<SkatGameEvent> gameMoves) {
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

	private void oneStepForward() {
		SkatGameEvent event = gameMoves.get(currentMove);
		event.processForward(data);
		JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, event));
		currentMove++;
	}
}
