/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.util.List;
import java.util.Set;

import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.SkatSeriesData.SeriesState;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStartInformation;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Doesn't do anything<br />
 * is needed for simulating games without gui
 */
public class NullView implements IJSkatView {

	/**
	 * @see IJSkatView#addCard(String, Player, Card)
	 */
	@Override
	public void addCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#clearHand(String, Player)
	 */
	@Override
	public void clearHand(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#clearTrickCards(String)
	 */
	@Override
	public void clearTrickCards(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#createISSTable(String, String)
	 */
	@Override
	public void createISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") String loginName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#createSkatTablePanel(String)
	 */
	@Override
	public void createSkatTablePanel(@SuppressWarnings("unused") String name) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#removeCard(String, Player, Card)
	 */
	@Override
	public void removeCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setPositions(String, Player, Player, Player)
	 */
	@Override
	public void setPositions(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player leftPosition,
			@SuppressWarnings("unused") Player rightPosition,
			@SuppressWarnings("unused") Player playerPosition) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setTrickCard(String, Player, Card)
	 */
	@Override
	public void setTrickCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player position,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showAboutMessage()
	 */
	@Override
	public void showAboutMessage() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showExitDialog()
	 */
	@Override
	public int showExitDialog() {
		return 0;
	}

	/**
	 * @see IJSkatView#showGameResults()
	 */
	@Override
	public void showGameResults() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showISSLogin()
	 */
	@Override
	public void showISSLogin() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showSeriesResults()
	 */
	@Override
	public void showSeriesResults() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showTable(SkatTable)
	 */
	@Override
	public void showTable(@SuppressWarnings("unused") SkatTable table) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#startBidding()
	 */
	@Override
	public void startBidding() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#startDiscarding()
	 */
	@Override
	public void startDiscarding() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#startGame(String)
	 */
	@Override
	public void startGame(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#startPlaying()
	 */
	@Override
	public void startPlaying() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#startSeries(String)
	 */
	@Override
	public void startSeries(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setGameAnnouncement(String, Player, GameAnnouncement)
	 */
	@Override
	public void setGameAnnouncement(
			@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player declarer,
			@SuppressWarnings("unused") GameAnnouncement ann) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setGameState(String, GameState)
	 */
	@Override
	public void setGameState(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") GameState state) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#addGameResult(String, SkatGameData)
	 */
	@Override
	public void addGameResult(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SkatGameData data) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showHelpDialog()
	 */
	@Override
	public void showHelpDialog() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showLicenseDialog()
	 */
	@Override
	public void showLicenseDialog() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#clearTable(String)
	 */
	@Override
	public void clearTable(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showMessage(int, String, String)
	 */
	@Override
	public void showMessage(@SuppressWarnings("unused") int messageType,
			@SuppressWarnings("unused") String title,
			@SuppressWarnings("unused") String message) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setBid(String, Player, int, boolean)
	 */
	@Override
	public void setBid(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") int bidValue,
			@SuppressWarnings("unused") boolean madeBid) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setTrickForeHand(String, Player)
	 */
	@Override
	public void setTrickForeHand(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player trickForeHand) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#putCardIntoSkat(String, Card)
	 */
	@Override
	public void putCardIntoSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#takeCardFromSkat(String, Card)
	 */
	@Override
	public void takeCardFromSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showStartSkatSeriesDialog()
	 */
	@Override
	public void showStartSkatSeriesDialog() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#updateISSLobbyPlayerList(String, String, long, double)
	 */
	@Override
	public void updateISSLobbyPlayerList(
			@SuppressWarnings("unused") String playerName,
			@SuppressWarnings("unused") String playerLanguage1,
			@SuppressWarnings("unused") long gamesPlayed,
			@SuppressWarnings("unused") double strength) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#removeFromISSLobbyPlayerList(String)
	 */
	@Override
	public void removeFromISSLobbyPlayerList(
			@SuppressWarnings("unused") String playerName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showISSLobby()
	 */
	@Override
	public void showISSLobby() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#removeFromISSLobbyTableList(String)
	 */
	@Override
	public void removeFromISSLobbyTableList(
			@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#updateISSLobbyTableList(String, int, long, String,
	 *      String, String)
	 */
	@Override
	public void updateISSLobbyTableList(
			@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int maxPlayers,
			@SuppressWarnings("unused") long gamesPlayed,
			@SuppressWarnings("unused") String player1,
			@SuppressWarnings("unused") String player2,
			@SuppressWarnings("unused") String player3) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#appendISSChatMessage(ChatMessageType, ISSChatMessage)
	 */
	@Override
	public void appendISSChatMessage(
			@SuppressWarnings("unused") ChatMessageType messageType,
			@SuppressWarnings("unused") ISSChatMessage message) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#updateISSTable(String, ISSTablePanelStatus)
	 */
	@Override
	public void updateISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") ISSTablePanelStatus status) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#updateISSTable(String, String, ISSGameStartInformation)
	 */
	@Override
	public void updateISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") String playerName,
			@SuppressWarnings("unused") ISSGameStartInformation status) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#getNewTableName()
	 */
	@Override
	public String getNewTableName() {
		// empty method by indent
		return null;
	}

	/**
	 * @see IJSkatView#updateISSMove(String, SkatGameData, ISSMoveInformation)
	 */
	@Override
	public void updateISSMove(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SkatGameData gameData,
			@SuppressWarnings("unused") ISSMoveInformation moveInformation) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#playTrickCard(String, Player, Card)
	 */
	@Override
	public void playTrickCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player position,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setLastTrick(String, Player, Card, Card, Card)
	 */
	@Override
	public void setLastTrick(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player trickForeHand,
			@SuppressWarnings("unused") Card foreHandCard,
			@SuppressWarnings("unused") Card middleHandCard,
			@SuppressWarnings("unused") Card hindHandCard) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showPreferences()
	 */
	@Override
	public void showPreferences() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showTrainingOverview()
	 */
	@Override
	public void showTrainingOverview() {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#closeTabPanel(java.lang.String)
	 */
	@Override
	public void closeTabPanel(@SuppressWarnings("unused") String name) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#getPlayerForInvitation(Set)
	 */
	@Override
	public List<String> getPlayerForInvitation(
			@SuppressWarnings("unused") Set<String> playerNames) {
		// empty method by indent
		return null;
	}

	/**
	 * @see IJSkatView#addCards(String, Player, CardList)
	 */
	@Override
	public void addCards(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") CardList cards) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setActivePlayer(String, Player)
	 */
	@Override
	public void setActivePlayer(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setPass(String, Player)
	 */
	@Override
	public void setPass(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setSeriesState(String, SeriesState)
	 */
	@Override
	public void setSeriesState(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SeriesState state) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setBidValueToMake(String, int)
	 */
	@Override
	public void setBidValueToMake(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int bidValue) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setBidValueToHold(String, int)
	 */
	@Override
	public void setBidValueToHold(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int bidValue) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setSkat(String, CardList)
	 */
	@Override
	public void setSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") CardList skat) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setTrickNumber(String, int)
	 */
	@Override
	public void setTrickNumber(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int trickNumber) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showISSTableInvitation(String, String)
	 */
	@Override
	public boolean showISSTableInvitation(
			@SuppressWarnings("unused") String invitor,
			@SuppressWarnings("unused") String tableName) {
		// empty method by indent
		return false;
	}

	/**
	 * @see IJSkatView#setGameResult(String, SkatGameData)
	 */
	@Override
	public void setGameResult(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SkatGameData gameData) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#showCardNotAllowedMessage(Card)
	 */
	@Override
	public void showCardNotAllowedMessage(@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#addTrainingResult(GameType, long, long, long, double,
	 *      double)
	 */
	@Override
	public void addTrainingResult(
			@SuppressWarnings("unused") GameType gameType,
			@SuppressWarnings("unused") long episodes,
			@SuppressWarnings("unused") long totalWonGames,
			@SuppressWarnings("unused") long episodeWonGames,
			@SuppressWarnings("unused") double avgDeclDiff,
			@SuppressWarnings("unused") double avgOppDiff) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setGameNumber(String, int)
	 */
	@Override
	public void setGameNumber(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int gameNumber) {
		// empty method by indent
	}

	/**
	 * @see IJSkatView#setPlayerNames(String, String, String, String)
	 */
	@Override
	public void setPlayerNames(String tableName, String upperLeftPlayerName,
			String upperRightPlayerName, String lowerPlayerName) {
		// TODO Auto-generated method stub

	}
}
