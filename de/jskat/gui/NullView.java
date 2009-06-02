package de.jskat.gui;

import java.util.StringTokenizer;

import de.jskat.control.SkatGame;
import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Doesn't do anything
 */
public class NullView implements JSkatView {

	private String language;

	/**
	 * @see JSkatView#addCard(String, Player, Card)
	 */
	@Override
	public void addCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#clearHand(String, Player)
	 */
	@Override
	public void clearHand(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#clearTrickCards(String)
	 */
	@Override
	public void clearTrickCards(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#createISSTable(String)
	 */
	@Override
	public void createISSTable(@SuppressWarnings("unused") String name) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#createSkatTablePanel(String)
	 */
	@Override
	public SkatTablePanel createSkatTablePanel(
			@SuppressWarnings("unused") String name) {
		return null;
	}

	/**
	 * @see JSkatView#removeCard(String, Player, Card)
	 */
	@Override
	public void removeCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setPositions(String, Player, Player, Player)
	 */
	@Override
	public void setPositions(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player leftPosition,
			@SuppressWarnings("unused") Player rightPosition,
			@SuppressWarnings("unused") Player playerPosition) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setTrickCard(String, Player, Card)
	 */
	@Override
	public void setTrickCard(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player position,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showAboutMessage()
	 */
	@Override
	public void showAboutMessage() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showExitDialog()
	 */
	@Override
	public int showExitDialog() {
		return 0;
	}

	/**
	 * @see JSkatView#showGameResults()
	 */
	@Override
	public void showGameResults() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showISSLogin()
	 */
	@Override
	public void showISSLogin() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showSeriesResults()
	 */
	@Override
	public void showSeriesResults() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showTable(SkatTable)
	 */
	@Override
	public void showTable(@SuppressWarnings("unused") SkatTable table) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#startBidding()
	 */
	@Override
	public void startBidding() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#startDiscarding()
	 */
	@Override
	public void startDiscarding() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#startGame(SkatGame)
	 */
	@Override
	public void startGame(@SuppressWarnings("unused") SkatGame game) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#startPlaying()
	 */
	@Override
	public void startPlaying() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#startSeries(String)
	 */
	@Override
	public void startSeries(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setGameAnnouncement(String, GameAnnouncement, boolean)
	 */
	@Override
	public void setGameAnnouncement(
			@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") GameAnnouncement ann,
			@SuppressWarnings("unused") boolean hand) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setGameState(String, GameStates)
	 */
	@Override
	public void setGameState(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") GameStates state) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#addGameResult(String, SkatGameData)
	 */
	@Override
	public void addGameResult(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") SkatGameData data) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showHelpDialog()
	 */
	@Override
	public void showHelpDialog() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#clearTable(String)
	 */
	@Override
	public void clearTable(@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setNextBidValue(String, int)
	 */
	@Override
	public void setNextBidValue(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") int nextBidValue) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showMessage(int, String)
	 */
	@Override
	public void showMessage(@SuppressWarnings("unused") int messageType,
			@SuppressWarnings("unused") String message) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setBid(String, Player, int)
	 */
	@Override
	public void setBid(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") int bidValue) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setTrickForeHand(String, Player)
	 */
	@Override
	public void setTrickForeHand(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Player trickForeHand) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#setSkat(String, CardList)
	 */
	@Override
	public void setSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") CardList skat) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#putCardIntoSkat(String, Card)
	 */
	@Override
	public void putCardIntoSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#takeCardFromSkat(String, Card)
	 */
	@Override
	public void takeCardFromSkat(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") Card card) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showStartSkatSeriesDialog()
	 */
	@Override
	public void showStartSkatSeriesDialog() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#updateISSLobbyPlayerList(String, String, long, double)
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
	 * @see JSkatView#removeFromISSLobbyPlayerList(String)
	 */
	@Override
	public void removeFromISSLobbyPlayerList(
			@SuppressWarnings("unused") String playerName) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#showISSLobby()
	 */
	@Override
	public void showISSLobby() {
		// empty method by indent
	}

	/**
	 * @see JSkatView#removeFromISSLobbyTableList(String)
	 */
	@Override
	public void removeFromISSLobbyTableList(
			@SuppressWarnings("unused") String tableName) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#updateISSLobbyTableList(String, int, long, String, String,
	 *      String)
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
	 * @see JSkatView#appendISSChatMessage(ChatMessageType, ISSChatMessage)
	 */
	@Override
	public void appendISSChatMessage(
			@SuppressWarnings("unused") ChatMessageType messageType,
			@SuppressWarnings("unused") ISSChatMessage message) {
		// empty method by indent
	}

	/**
	 * @see JSkatView#updateISSTable(String, StringTokenizer)
	 */
	@Override
	public void updateISSTable(@SuppressWarnings("unused") String tableName,
			@SuppressWarnings("unused") StringTokenizer token) {
		// empty method by indent
	}
}
