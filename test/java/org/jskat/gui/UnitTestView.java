/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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

import java.util.ArrayList;
import java.util.List;
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
 * Helper class that represent a GUI view during unit tests
 */
public class UnitTestView implements JSkatView {

	public List<String> tables;

	/**
	 * Constructor
	 */
	public UnitTestView() {
		tables = new ArrayList<String>();
	}

	/**
	 * Resets the view
	 */
	public void reset() {
		tables.clear();
	}

	@Override
	public String getNewTableName(final int localTablesCreated) {

		return "UnitTestTable " + (localTablesCreated + 1); //$NON-NLS-1$
	}

	@Override
	public void showTable(final SkatTable table) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSeries(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showSeriesResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startBidding() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDiscarding() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPlaying() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showGameResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showISSLogin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showISSLobby() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createISSTable(final String tableName, final String loginName) {
		tables.add(tableName);
	}

	@Override
	public void createSkatTablePanel(final String name) {
		tables.add(name);
	}

	@Override
	public void closeTabPanel(final String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPlayerForInvitation(final Set<String> playerNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showAboutMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessage(final String title, final String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showErrorMessage(final String title, final String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCardNotAllowedMessage(final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPreferences() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showTrainingOverview() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCard(final String tableName, final Player player,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCards(final String tableName, final Player player,
			final CardList cards) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCard(final String tableName, final Player player,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearHand(final String tableName, final Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBid(final String tableName, final Player player,
			final int bidValue, final boolean madeBid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPass(final String tableName, final Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositions(final String tableName, final Player leftPosition,
			final Player rightPosition, final Player playerPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickCard(final String tableName, final Player position,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastTrick(final String tableName, final Trick trick) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTrickCards(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playTrickCard(final String tableName, final Player position,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameAnnouncement(final String tableName,
			final Player declarer, final GameAnnouncement ann) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameState(final String tableName, final GameState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeriesState(final String tableName, final SeriesState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGameResult(final String tableName, final GameSummary summary) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showHelpDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLicenseDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showWelcomeDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTable(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBidValueToMake(final String tableName, final int bidValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBidValueToHold(final String tableName, final int bidValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickForeHand(final String tableName,
			final Player trickForeHand) {
		// TODO Auto-generated method stub

	}

	@Override
	public void takeCardFromSkat(final String tableName, final Player player,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putCardIntoSkat(final String tableName, final Player player,
			final Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showStartSkatSeriesDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSLobbyPlayerList(final String playerName,
			final String language, final long gamesPlayed, final double strength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromISSLobbyPlayerList(final String playerName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSLobbyTableList(final String tableName,
			final int maxPlayers, final long gamesPlayed, final String player1,
			final String player2, final String player3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromISSLobbyTableList(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendISSChatMessage(final ChatMessageType messageType,
			final ChatMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSTable(final String tableName,
			final TablePanelStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSTable(final String tableName, final String loginName,
			final GameStartInformation status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSMove(final String tableName,
			final SkatGameData gameData, final MoveInformation moveInformation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResign(final String tableName, final Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePlayer(final String tableName, final Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSkat(final String tableName, final CardList skat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickNumber(final String tableName, final int trickNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showISSTableInvitation(final String invitor,
			final String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addTrainingResult(final GameType gameType, final long episodes,
			final long totalWonGames, final long episodeWonGames,
			final double avgDifference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameNumber(final String tableName, final int gameNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPlayerNames(final String tableName,
			final String upperLeftPlayerName,
			final String upperRightPlayerName, final String lowerPlayerName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeclarer(final String tableName, final Player declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeISSPanels() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDuplicateTableNameMessage(final String duplicateTableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeschoben(final String tableName, final Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNewVersionAvailableMessage(final String newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDiscardedSkat(final String tableName,
			final Player activePlayer, final CardList skatBefore,
			final CardList discardedSkat) {
		// TODO Auto-generated method stub
	}

	@Override
	public void takeCardFromSkat(final String tableName, final Card card) {
		// TODO Auto-generated method stub
	}

	@Override
	public void putCardIntoSkat(final String tableName, final Card card) {
		// TODO Auto-generated method stub
	}

	@Override
	public void openWebPage(final String link) {
		// TODO Auto-generated method stub
	}

	@Override
	public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayedTrick(final String tableName, final Trick playedTrick) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAllCards(final String tableName) {
		// TODO Auto-generated method stub
	}
}
