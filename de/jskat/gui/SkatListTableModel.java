/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

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

		for (int i = 0; i < this.gameResults.size(); i++) {

			this.displayValues.add(new ArrayList<Integer>());

			for (int j = 0; j < this.playerCount; j++) {

				int previousResult = 0;
				currResult = 0;

				if (this.declarers.get(i) != null) {

					if (i > 0) {
						// get previous result for player values
						// from second game on
						previousResult = this.displayValues.get(i - 1).get(j);
					} else {
						previousResult = 0;
					}

					// get player results from current game
					boolean declarer = this.declarers.get(i).getOrder() == j;
					switch (this.mode) {
					case NORMAL:
						if (declarer) {
							currResult = this.gameResults.get(i);
						}
						break;
					case TOURNAMENT:
						currResult = SkatConstants.getTournamentGameValue(
								declarer, this.gameResults.get(i),
								this.playerCount);
						break;
					case BIERLACHS:
						break;
					}
				}
				
				if (currResult != 0) {

					this.displayValues.get(i).add(previousResult + currResult);
				} else {

					this.displayValues.get(i).add(0);
				}
			}

			// get game result
			switch (this.mode) {
			case NORMAL:
			case BIERLACHS:
				currResult = this.gameResults.get(i);
				break;
			case TOURNAMENT:
				currResult = SkatConstants.getTournamentGameValue(true,
						this.gameResults.get(i), this.playerCount);
				break;
			}
			this.displayValues.get(i).add(currResult);
		}
	}

	void addResult(Player singlePlayer, int gameResult) {

		this.declarers.add(singlePlayer);
		this.gameResults.add(gameResult);
		if (singlePlayer != null) {
			this.playerResults.get(singlePlayer.getOrder()).add(gameResult);
			this.playerResults.get(singlePlayer.getLeftNeighbor().getOrder())
					.add(0);
			this.playerResults.get(singlePlayer.getRightNeighbor().getOrder())
					.add(0);
		} else {
			// game was passed in
			for (int i = 0; i < playerCount; i++) {
				this.playerResults.get(i).add(0);
			}
		}
		calculateDisplayValues();

		this.fireTableDataChanged();
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
