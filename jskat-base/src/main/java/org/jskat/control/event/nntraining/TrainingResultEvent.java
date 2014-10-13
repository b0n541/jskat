/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.event.nntraining;

import org.jskat.util.GameType;

/**
 * This event is created when new training results should be added.
 */
public class TrainingResultEvent {

	public final GameType gameType;
	public final Long episodes;
	public final Long totalGamesWon;
	public final Double avgNetworkErrorDeclarer;
	public final Double avgNetworkErrorOpponents;

	public TrainingResultEvent(final GameType gameType, final Long episodes,
			final Long totalGamesWon, final Double avgNetworkErrorDeclarer,
			final Double avgNetworkErrorOpponents) {
		this.gameType = gameType;
		this.episodes = episodes;
		this.totalGamesWon = totalGamesWon;
		this.avgNetworkErrorDeclarer = avgNetworkErrorDeclarer;
		this.avgNetworkErrorOpponents = avgNetworkErrorOpponents;
	}
}
