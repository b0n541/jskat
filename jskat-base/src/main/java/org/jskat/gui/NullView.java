/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
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
import java.util.Map;
import java.util.Set;

import org.jskat.control.SkatTable;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.Trick;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.human.AbstractHumanJSkatPlayer;
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
	public void addCard(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearHand(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTrickCards(
			@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createISSTable(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final String loginName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createSkatTablePanel(
			@SuppressWarnings("unused") final String name) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCard(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPositions(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player leftPosition,
			@SuppressWarnings("unused") final Player rightPosition,
			@SuppressWarnings("unused") final Player playerPosition) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickCard(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player position,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAboutMessage() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showGameResults() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLogin() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showSeriesResults() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTable(@SuppressWarnings("unused") final SkatTable table) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBidding() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDiscarding() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame(@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startPlaying() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startSeries(@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameAnnouncement(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player declarer,
			@SuppressWarnings("unused") final GameAnnouncement ann) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameState(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final GameState state) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGameResult(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final GameSummary summary) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showHelpDialog() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLicenseDialog() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showWelcomeDialog() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTable(@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showMessage(@SuppressWarnings("unused") final String title,
			@SuppressWarnings("unused") final String message) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showErrorMessage(
			@SuppressWarnings("unused") final String title,
			@SuppressWarnings("unused") final String message) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBid(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final int bidValue,
			@SuppressWarnings("unused") final boolean madeBid) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickForeHand(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player trickForeHand) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCardIntoSkat(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeCardFromSkat(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showStartSkatSeriesDialog() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyPlayerList(
			@SuppressWarnings("unused") final String playerName,
			@SuppressWarnings("unused") final String playerLanguage1,
			@SuppressWarnings("unused") final long gamesPlayed,
			@SuppressWarnings("unused") final double strength) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyPlayerList(
			@SuppressWarnings("unused") final String playerName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLobby() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyTableList(
			@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyTableList(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final int maxPlayers,
			@SuppressWarnings("unused") final long gamesPlayed,
			@SuppressWarnings("unused") final String player1,
			@SuppressWarnings("unused") final String player2,
			@SuppressWarnings("unused") final String player3) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendISSChatMessage(
			@SuppressWarnings("unused") final ChatMessageType messageType,
			@SuppressWarnings("unused") final ChatMessage message) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final TablePanelStatus status) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final String playerName,
			@SuppressWarnings("unused") final GameStartInformation status) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewTableName(
			@SuppressWarnings("unused") final int localTablesCreated) {
		// empty method by intent
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSMove(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final SkatGameData gameData,
			@SuppressWarnings("unused") final MoveInformation moveInformation) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playTrickCard(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player position,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLastTrick(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Trick trick) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showPreferences() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTrainingOverview() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTabPanel(@SuppressWarnings("unused") final String name) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerForInvitation(
			@SuppressWarnings("unused") final Set<String> playerNames) {
		// empty method by intent
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCards(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player,
			@SuppressWarnings("unused") final CardList cards) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivePlayer(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPass(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSeriesState(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final SeriesState state) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToMake(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final int bidValue) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToHold(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final int bidValue) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSkat(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final CardList skat) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickNumber(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final int trickNumber) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showISSTableInvitation(
			@SuppressWarnings("unused") final String invitor,
			@SuppressWarnings("unused") final String tableName) {
		// empty method by intent
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showCardNotAllowedMessage(
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTrainingResult(
			@SuppressWarnings("unused") final GameType gameType,
			@SuppressWarnings("unused") final long episodes,
			@SuppressWarnings("unused") final long totalWonGames,
			@SuppressWarnings("unused") final double avgNetworkErrorDeclarer,
			@SuppressWarnings("unused") final double avgNetworkErrorOpponents) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameNumber(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final int gameNumber) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayerNames(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final String upperLeftPlayerName,
			@SuppressWarnings("unused") final String upperRightPlayerName,
			@SuppressWarnings("unused") final String lowerPlayerName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclarer(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player declarer) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeISSPanels() {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showDuplicateTableNameMessage(
			@SuppressWarnings("unused") final String duplicateTableName) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResign(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGeschoben(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player player) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showNewVersionAvailableMessage(
			@SuppressWarnings("unused") final String newVersion) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDiscardedSkat(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Player activePlayer,
			@SuppressWarnings("unused") final CardList skatBefore,
			@SuppressWarnings("unused") final CardList discardedSkat) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeCardFromSkat(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCardIntoSkat(
			@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final Card card) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openWebPage(@SuppressWarnings("unused") final String link) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
		// empty method by intent
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayedTrick(final String tableName, final Trick playedTrick) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showCards(final String tableName,
			final Map<Player, CardList> cards) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActiveView(String name) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAIPlayedSchwarzMessage(String playerName, Card card) {
		// empty method by intent
	}
}
