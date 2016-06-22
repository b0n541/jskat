/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jskat.data.GameSummary;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.SkatListMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a model for the skat list table
 */
class SkatListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SkatListTableModel.class);

	private final JSkatResourceBundle strings;

	private SkatListMode mode = SkatListMode.NORMAL;

	private int playerCount = 3;
	private final List<Player> declarers;
	private final List<List<Integer>> playerResults;
	private final List<GameSummary> gameResults;
	private final List<String> columns;
	private final List<List<Integer>> displayValues;

	/**
	 * Constructor
	 */
	public SkatListTableModel() {

		this.strings = JSkatResourceBundle.INSTANCE;

		this.declarers = new ArrayList<>();
		this.playerResults = new ArrayList<>();
		this.gameResults = new ArrayList<>();
		this.displayValues = new ArrayList<>();
		this.columns = new ArrayList<>();
		setColumns();
	}

	/**
	 * @see AbstractTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return this.columns.size();
	}

	/**
	 * @see AbstractTableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return this.declarers.size();
	}

	/**
	 * @see AbstractTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		Object result = null;

		if (this.displayValues.get(rowIndex).get(columnIndex) != null) {
			result = this.displayValues.get(rowIndex).get(columnIndex);
		} else {
			result = "-"; //$NON-NLS-1$
		}

		return result;
	}

	Integer getPlayerValue(final int playerColumn, final int gameRow) {

		Integer result = null;

		return result;
	}

	/**
	 * @see AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {

		return this.columns.get(col);
	}

	/**
	 * @see AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(final int col) {

		return Integer.class;
	}

	/**
	 * Sets the skat list mode
	 * 
	 * @param newMode
	 */
	void setSkatListMode(final SkatListMode newMode) {

		this.mode = newMode;

		calculateDisplayValues();

		fireTableDataChanged();
	}

	void calculateDisplayValues() {

		int currResult = 0;
		List<Integer> playerResultsSoFar = new ArrayList<>();
		for (int i = 0; i < this.playerCount; i++) {
			playerResultsSoFar.add(new Integer(0));
		}

		this.displayValues.clear();

		for (int game = 0; game < this.gameResults.size(); game++) {

			this.displayValues.add(new ArrayList<>());

			// add player values
			for (int player = 0; player < this.playerCount; player++) {

				int previousResult = 0;
				currResult = 0;

				if (this.declarers.get(game) != null) {

					// get previous result for player values
					previousResult = playerResultsSoFar.get(player).intValue();

					// get player results from current game
					switch (this.mode) {
					case NORMAL:
						currResult = this.playerResults.get(player).get(game);
						break;
					case TOURNAMENT:
						boolean isDeclarer = (this.playerResults.get(player).get(game) != 0);
						currResult = SkatConstants.getTournamentGameValue(isDeclarer, this.gameResults.get(game)
								.getGameValue(), this.playerCount);
						break;
					case BIERLACHS:
						// FIXME jan 31.05.2010 add bierlachs value
						break;
					}
				}

				if (currResult != 0) {

					Integer newResult = new Integer(previousResult + currResult);
					this.displayValues.get(game).add(newResult);
					playerResultsSoFar.set(player, newResult);

				} else {

					this.displayValues.get(game).add(null);
				}
			}

			// get game result
			switch (this.mode) {
			case NORMAL:
			case BIERLACHS:
				currResult = this.gameResults.get(game).getGameValue();
				break;
			case TOURNAMENT:
				currResult = SkatConstants.getTournamentGameValue(true, this.gameResults.get(game).getGameValue(),
						this.playerCount);
				break;
			}
			this.displayValues.get(game).add(currResult);
		}
	}

	/**
	 * Adds a game result to the model
	 * 
	 * @param leftOpponent
	 *            Position of the upper left opponent
	 * @param rightOpponent
	 *            Position of the upper right opponent
	 * @param user
	 *            Position of the player
	 * @param declarer
	 *            Position of the game declarer
	 * @param gameSummary
	 *            Game summary
	 */
	void addResult(final Player leftOpponent, final Player rightOpponent, final Player user, final Player declarer,
			final GameSummary gameSummary) {

		// FIXME works only on 3 player series
		// FIXME (jansch 21.03.2011) provide only one method for addResult()
		this.declarers.add(declarer);
		this.gameResults.add(gameSummary);

		int declarerColumn = getDeclarerColumn(leftOpponent, rightOpponent, user, declarer);

		if (declarer != null) {
			this.playerResults.get(declarerColumn).add(Integer.valueOf(gameSummary.getGameValue()));
			this.playerResults.get((declarerColumn + 1) % 3).add(Integer.valueOf(0));
			this.playerResults.get((declarerColumn + 2) % 3).add(Integer.valueOf(0));
		} else {
			// game was passed in
			for (int i = 0; i < this.playerCount; i++) {
				this.playerResults.get(i).add(0);
			}
		}
		calculateDisplayValues();

		fireTableDataChanged();
	}

	static int getDeclarerColumn(final Player leftOpponent, final Player rightOpponent, final Player player,
			final Player declarer) {

		int result = -1;

		if (declarer == leftOpponent) {
			result = 0;
		} else if (declarer == rightOpponent) {
			result = 1;
		} else if (declarer == player) {
			result = 2;
		}

		return result;
	}

	/**
	 * Clears the complete list
	 */
	void clearList() {

		this.declarers.clear();
		for (List<Integer> currList : this.playerResults) {

			currList.clear();
		}
		this.gameResults.clear();
		this.displayValues.clear();

		fireTableDataChanged();
	}

	public void setPlayerCount(final int newPlayerCount) {

		this.declarers.clear();
		this.gameResults.clear();

		this.playerCount = newPlayerCount;

		setColumns();

		fireTableStructureChanged();
		fireTableDataChanged();
	}

	void setColumns() {

		this.playerResults.clear();
		this.displayValues.clear();
		this.columns.clear();

		for (int i = 0; i < this.playerCount; i++) {
			// FIXME (jan 14.12.2010) get player names
			this.columns.add("P" + i);
			this.playerResults.add(new ArrayList<>());
			this.displayValues.add(new ArrayList<>());
		}
		this.columns.add(this.strings.getString("games")); //$NON-NLS-1$
		this.displayValues.add(new ArrayList<>());
	}

	void setPlayerNames(final String upperLeftPlayer, final String upperRightPlayer, final String lowerPlayer) {

		this.columns.set(0, upperLeftPlayer);
		this.columns.set(1, upperRightPlayer);
		this.columns.set(2, lowerPlayer);
		fireTableStructureChanged();
	}
}
