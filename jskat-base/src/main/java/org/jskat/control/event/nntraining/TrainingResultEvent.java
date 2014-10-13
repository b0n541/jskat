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
