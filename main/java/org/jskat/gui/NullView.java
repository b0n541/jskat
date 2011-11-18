/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui;

import java.util.List;
import java.util.Set;

import org.jskat.control.SkatTable;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Doesn't do anything<br />
 * is needed for simulating games without gui
 */
public class NullView implements JSkatView {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCard(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearHand(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTrickCards(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") String loginName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createSkatTablePanel(@SuppressWarnings("unused") String name) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCard(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPositions(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player leftPosition, @SuppressWarnings("unused") Player rightPosition,
			@SuppressWarnings("unused") Player playerPosition) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickCard(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player position,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAboutMessage() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showGameResults() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLogin() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showSeriesResults() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTable(@SuppressWarnings("unused") SkatTable table) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBidding() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDiscarding() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startPlaying() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startSeries(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameAnnouncement(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player declarer, @SuppressWarnings("unused") GameAnnouncement ann) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameState(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") GameState state) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGameResult(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") GameSummary summary) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showHelpDialog() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLicenseDialog() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTable(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showMessage(@SuppressWarnings("unused") int messageType, @SuppressWarnings("unused") String title,
			@SuppressWarnings("unused") String message) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBid(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") int bidValue, @SuppressWarnings("unused") boolean madeBid) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickForeHand(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player trickForeHand) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCardIntoSkat(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeCardFromSkat(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showStartSkatSeriesDialog() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyPlayerList(@SuppressWarnings("unused") String playerName,
			@SuppressWarnings("unused") String playerLanguage1, @SuppressWarnings("unused") long gamesPlayed,
			@SuppressWarnings("unused") double strength) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyPlayerList(@SuppressWarnings("unused") String playerName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLobby() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyTableList(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyTableList(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int maxPlayers, @SuppressWarnings("unused") long gamesPlayed,
			@SuppressWarnings("unused") String player1, @SuppressWarnings("unused") String player2,
			@SuppressWarnings("unused") String player3) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendISSChatMessage(@SuppressWarnings("unused") ChatMessageType messageType,
			@SuppressWarnings("unused") ChatMessage message) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") TablePanelStatus status) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") String playerName, @SuppressWarnings("unused") GameStartInformation status) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewTableName(@SuppressWarnings("unused") int localTablesCreated) {
		// empty method by indent
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSMove(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SkatGameData gameData,
			@SuppressWarnings("unused") MoveInformation moveInformation) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playTrickCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player position, @SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLastTrick(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player trickForeHand, @SuppressWarnings("unused") Card foreHandCard,
			@SuppressWarnings("unused") Card middleHandCard, @SuppressWarnings("unused") Card rearHandCard) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showPreferences() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTrainingOverview() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTabPanel(@SuppressWarnings("unused") String name) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerForInvitation(@SuppressWarnings("unused") Set<String> playerNames) {
		// empty method by indent
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCards(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") CardList cards) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivePlayer(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPass(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSeriesState(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SeriesState state) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToMake(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") int bidValue) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToHold(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") int bidValue) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSkat(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") CardList skat) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickNumber(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") int trickNumber) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showISSTableInvitation(@SuppressWarnings("unused") String invitor,
			@SuppressWarnings("unused") String tableName) {
		// empty method by indent
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameResult(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") GameSummary summary) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showCardNotAllowedMessage(@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTrainingResult(@SuppressWarnings("unused") GameType gameType,
			@SuppressWarnings("unused") long episodes, @SuppressWarnings("unused") long totalWonGames,
			@SuppressWarnings("unused") long episodeWonGames, @SuppressWarnings("unused") double avgDifference) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameNumber(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") int gameNumber) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayerNames(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") String upperLeftPlayerName,
			@SuppressWarnings("unused") String upperRightPlayerName, @SuppressWarnings("unused") String lowerPlayerName) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclarer(@SuppressWarnings("unused") String tableName, @SuppressWarnings("unused") Player declarer) {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeISSPanels() {
		// empty method by indent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameResultWithoutSkatList(String tableName, GameSummary summary) {
		// FIXME (jansch 05.04.2011) remove this dirty hack
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showDuplicateTableNameMessage(String duplicateTableName) {
		// empty method by indent
	}

	@Override
	public void setResign(String tableName, Player player) {
		// empty method by indent
	}
}
