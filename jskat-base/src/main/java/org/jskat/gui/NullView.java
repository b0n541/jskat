/**
 * Copyright (C) 2016 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.gui;

import java.util.List;
import java.util.Set;

import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Doesn't do anything<br>
 * is needed for simulating games without gui
 */
public class NullView implements JSkatView {

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
	public void startGame(@SuppressWarnings("unused") final String tableName) {
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
	public List<String> getPlayerForInvitation(
			@SuppressWarnings("unused") final Set<String> playerNames) {
		// empty method by intent
		return null;
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
	public void setPlayerNames(@SuppressWarnings("unused") final String tableName,
			@SuppressWarnings("unused") final String upperLeftPlayerName,
			@SuppressWarnings("unused") final boolean isUpperLeftPlayerAIPlayer,
			@SuppressWarnings("unused") final String upperRightPlayerName,
			@SuppressWarnings("unused") final boolean isUpperRightPlayerAIPlayer,
			@SuppressWarnings("unused") final String lowerPlayerName,
			@SuppressWarnings("unused") final boolean isLowerPlayerAIPlayer) {
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
	public void setActiveView(final String name) {
		// empty method by intent
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAIPlayedSchwarzMessageDiscarding(final String playerName,
			final CardList discardedCards) {
		// empty method by intent
	}

	@Override
	public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {
		// empty method by intent
	}
}
