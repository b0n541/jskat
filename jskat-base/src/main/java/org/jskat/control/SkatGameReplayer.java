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
