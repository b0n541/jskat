/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.jskat.util.Player;
import de.jskat.util.SkatConstants;
import de.jskat.util.SkatListMode;

/**
 * Provides a model for the skat list table
 */
class SkatListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private SkatListMode mode = SkatListMode.NORMAL;

	private int playerCount = 3;
	private List<Player> declarers;
	private List<List<Integer>> playerResults;
	private List<Integer> gameResults;
	private List<String> columns;
	private List<List<Integer>> displayValues;

	/**
	 * Constructor
	 */
	public SkatListTableModel() {

		this.declarers = new ArrayList<Player>();
		this.playerResults = new ArrayList<List<Integer>>();
		this.gameResults = new ArrayList<Integer>();
		this.displayValues = new ArrayList<List<Integer>>();
		this.columns = new ArrayList<String>();
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
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object result = null;

		if (this.displayValues.get(rowIndex).get(columnIndex).intValue() != 0) {
			result = this.displayValues.get(rowIndex).get(columnIndex);
		} else {
			result = "-";
		}

		return result;
	}

	Integer getPlayerValue(int playerColumn, int gameRow) {

		Integer result = null;

		return result;
	}

	/**
	 * @see AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {

		return this.columns.get(col);
	}

	/**
	 * Sets the skat list mode
	 * 
	 * @param newMode
	 */
	void setSkatListMode(SkatListMode newMode) {

		this.mode = newMode;

		calculateDisplayValues();

		this.fireTableDataChanged();
	}

	void calculateDisplayValues() {

		int currResult = 0;

		this.displayValues.clear();

		for (int game = 0; game < this.gameResults.size(); game++) {

			this.displayValues.add(new ArrayList<Integer>());

			// add player values
			for (int player = 0; player < this.playerCount; player++) {

				int previousResult = 0;
				currResult = 0;

				if (this.declarers.get(game) != null) {

					if (game > 0) {
						// get previous result for player values
						// from second game on
						previousResult = this.displayValues.get(game - 1).get(
								player);
					} else {
						previousResult = 0;
					}

					// get player results from current game
					switch (this.mode) {
					case NORMAL:
						currResult = this.playerResults.get(player).get(game);
						break;
					case TOURNAMENT:
						boolean isDeclarer = (this.playerResults.get(player)
								.get(game) != 0);
						currResult = SkatConstants.getTournamentGameValue(
								isDeclarer, this.gameResults.get(game),
								this.playerCount);
						break;
					case BIERLACHS:
						break;
					}
				}

				if (currResult != 0) {

					this.displayValues.get(game).add(
							new Integer(previousResult + currResult));
				} else {

					this.displayValues.get(game).add(0);
				}
			}

			// get game result
			switch (this.mode) {
			case NORMAL:
			case BIERLACHS:
				currResult = this.gameResults.get(game);
				break;
			case TOURNAMENT:
				currResult = SkatConstants.getTournamentGameValue(true,
						this.gameResults.get(game), this.playerCount);
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
	 * @param player
	 *            Position of the player
	 * @param declarer
	 *            Position of the game declarer
	 * @param gameResult
	 *            Game result
	 */
	void addResult(Player leftOpponent, Player rightOpponent, Player player,
			Player declarer, int gameResult) {

		// FIXME works only on 3 player series
		this.declarers.add(declarer);
		this.gameResults.add(new Integer(gameResult));

		int declarerColumn = getDeclarerColumn(leftOpponent, rightOpponent,
				player, declarer);

		if (declarer != null) {
			this.playerResults.get(declarerColumn).add(new Integer(gameResult));
			this.playerResults.get((declarerColumn + 1) % 3)
					.add(new Integer(0));
			this.playerResults.get((declarerColumn + 2) % 3)
					.add(new Integer(0));
		} else {
			// game was passed in
			for (int i = 0; i < this.playerCount; i++) {
				this.playerResults.get(i).add(0);
			}
		}
		calculateDisplayValues();

		this.fireTableDataChanged();
	}

	int getDeclarerColumn(Player leftOpponent, Player rightOpponent,
			Player player, Player declarer) {

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
		for (List<Integer> currList : playerResults) {

			currList.clear();
		}
		this.gameResults.clear();
		this.displayValues.clear();

		this.fireTableDataChanged();
	}

	public void setPlayerCount(int newPlayerCount) {

		this.declarers.clear();
		this.gameResults.clear();

		this.playerCount = newPlayerCount;

		setColumns();

		this.fireTableStructureChanged();
		this.fireTableDataChanged();
	}

	void setColumns() {

		this.playerResults.clear();
		this.displayValues.clear();
		this.columns.clear();

		for (int i = 0; i < this.playerCount; i++) {

			this.columns.add("P" + i);
			this.playerResults.add(new ArrayList<Integer>());
			this.displayValues.add(new ArrayList<Integer>());
		}
		this.columns.add("Games");
		this.displayValues.add(new ArrayList<Integer>());
	}
}
