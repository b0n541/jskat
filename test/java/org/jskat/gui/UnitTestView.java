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
	public String getNewTableName(int localTablesCreated) {

		return "UnitTestTable " + (localTablesCreated + 1); //$NON-NLS-1$
	}

	@Override
	public void showTable(SkatTable table) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSeries(String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showSeriesResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame(String tableName) {
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
	public void createISSTable(String tableName, String loginName) {
		tables.add(tableName);
	}

	@Override
	public void createSkatTablePanel(String name) {
		tables.add(name);
	}

	@Override
	public void closeTabPanel(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPlayerForInvitation(Set<String> playerNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showAboutMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessage(int messageType, String title, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCardNotAllowedMessage(Card card) {
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
	public void addCard(String tableName, Player player, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCards(String tableName, Player player, CardList cards) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCard(String tableName, Player player, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearHand(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBid(String tableName, Player player, int bidValue, boolean madeBid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPass(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositions(String tableName, Player leftPosition, Player rightPosition, Player playerPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickCard(String tableName, Player position, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastTrick(String tableName, Trick trick) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTrickCards(String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playTrickCard(String tableName, Player position, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameAnnouncement(String tableName, Player declarer, GameAnnouncement ann) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameState(String tableName, GameState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeriesState(String tableName, SeriesState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGameResult(String tableName, GameSummary summary) {
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
	public void clearTable(String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBidValueToMake(String tableName, int bidValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBidValueToHold(String tableName, int bidValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickForeHand(String tableName, Player trickForeHand) {
		// TODO Auto-generated method stub

	}

	@Override
	public void takeCardFromSkat(String tableName, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putCardIntoSkat(String tableName, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showStartSkatSeriesDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSLobbyPlayerList(String playerName, String language, long gamesPlayed, double strength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromISSLobbyPlayerList(String playerName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSLobbyTableList(String tableName, int maxPlayers, long gamesPlayed, String player1,
			String player2, String player3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromISSLobbyTableList(String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendISSChatMessage(ChatMessageType messageType, ChatMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSTable(String tableName, TablePanelStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSTable(String tableName, String loginName, GameStartInformation status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateISSMove(String tableName, SkatGameData gameData, MoveInformation moveInformation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResign(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePlayer(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSkat(String tableName, CardList skat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickNumber(String tableName, int trickNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showISSTableInvitation(String invitor, String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGameResult(String tableName, GameSummary summary) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameResultWithoutSkatList(String tableName, GameSummary summary) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTrainingResult(GameType gameType, long episodes, long totalWonGames, long episodeWonGames,
			double avgDifference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameNumber(String tableName, int gameNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPlayerNames(String tableName, String upperLeftPlayerName, String upperRightPlayerName,
			String lowerPlayerName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeclarer(String tableName, Player declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeISSPanels() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDuplicateTableNameMessage(String duplicateTableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeschoben(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNewVersionAvailableMessage(String newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDiscardedSkat(String tableName, Player activePlayer, CardList skatBefore, CardList discardedSkat) {
		// TODO Auto-generated method stub
	}
}
