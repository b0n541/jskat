/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.control.event.skatgame;

import java.util.Objects;

import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;

/**
 * Event for game start.
 */
public final class GameFinishEvent implements SkatGameEvent {

	public final GameSummary gameSummary;

	public GameFinishEvent(GameSummary gameSummary) {
		this.gameSummary = gameSummary;
	}

	@Override
	public final void processForward(SkatGameData data) {
		data.setResult(gameSummary.gameResult);
	}

	@Override
	public final void processBackward(SkatGameData data) {
		data.setResult(new SkatGameResult());
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameSummary);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;			
		}
		if (obj == null) {
			return false;			
		}
		if (getClass() != obj.getClass()) {
			return false;			
		}
		final GameFinishEvent other = (GameFinishEvent) obj;
		
		return Objects.equals(gameSummary, other.gameSummary);
	}
}
