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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.table.TableCreatedEvent;
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

import com.google.common.eventbus.Subscribe;

/**
 * Helper class that represent a GUI view during unit tests
 */
public class UnitTestView implements JSkatView {

	public List<String> tables;

	/**
	 * Constructor
	 */
	public UnitTestView() {
		this.tables = new ArrayList<String>();
		JSkatEventBus.INSTANCE.register(this);
	}

	@Subscribe
	public void handle(final TableCreatedEvent event) {
		this.tables.add(event.tableName);
	}

	/**
	 * Resets the view
	 */
	public void reset() {
		this.tables.clear();
	}

	@Override
	public String getNewTableName(final int localTablesCreated) {

		return "UnitTestTable " + (localTablesCreated + 1); //$NON-NLS-1$
	}

	@Override
	public void startGame(final String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showISSLogin() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPlayerForInvitation(final Set<String> playerNames) {
		// TODO Auto-generated method stub
		return null;
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
	public void setGameState(final String tableName, final GameState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeriesState(final String tableName, final SeriesState state) {
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
	public void setSkat(final String tableName, final CardList skat) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showISSTableInvitation(final String invitor,
			final String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPlayerNames(String tableName, String upperLeftPlayerName, boolean isUpperLeftPlayerAIPlayer,
			String upperRightPlayerName, boolean isUpperRightPlayerAIPlayer, String lowerPlayerName,
			boolean isLowerPlayerAIPlayer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeclarer(final String tableName, final Player declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeschoben(final String tableName, final Player player) {
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
	public void setActiveView(final String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAIPlayedSchwarzMessageDiscarding(final String playerName,
			final CardList discardedCard) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {
		// TODO Auto-generated method stub
	}
}
