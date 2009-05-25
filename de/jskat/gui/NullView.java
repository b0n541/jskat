package de.jskat.gui;

import de.jskat.control.SkatGame;
import de.jskat.control.SkatTable;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Doesn't do anything
 */
public class NullView implements JSkatView {

	@Override
	public void addCard(String tableName, Player player, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearHand(String tableName, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTrickCards(String tableName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createISSTablePanel(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public SkatTablePanel createSkatTablePanel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCard(String tableName, Player player, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositions(String tableName, Player leftPosition,
			Player rightPosition, Player playerPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrickCard(String tableName, Player position, Card card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAboutMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public int showExitDialog() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void showGameResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showISSLoginPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showSeriesResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showTable(SkatTable table) {
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
	public void startGame(SkatGame game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPlaying() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSeries() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameAnnouncement(String tableName, GameAnnouncement ann, boolean hand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGameState(String tableName, GameStates state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addGameResult(String tableName, SkatGameData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHelpDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearTable(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNextBidValue(String tableName, int nextBidValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessage(int messageType, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBid(String tableName, Player player, int bidValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTrickForeHand(String tableName, Player trickForeHand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSkat(String tableName, CardList skat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putCardIntoSkat(String tableName, Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeCardFromSkat(String tableName, Card card) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see JSkatView#showStartSkatSeriesDialog()
	 */
	@Override
	public void showStartSkatSeriesDialog() {
		// empty method
	}
}
