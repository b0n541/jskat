package de.jskat.gui.iss;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Provides a model for the skat list table
 */
class PlayerListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<List<String>> data;
	private List<String> columns;

	/**
	 * Constructor
	 */
	public PlayerListTableModel() {

		this.data = new ArrayList<List<String>>();
		this.columns = new ArrayList<String>();
		this.columns.add("Name");
		this.columns.add("Language");
		this.columns.add("Games");
		this.columns.add("Strength");
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

		return this.data.size();
	}

	/**
	 * @see AbstractTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		return this.data.get(rowIndex).get(columnIndex);
	}

	/**
	 * @see AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
		
		return this.columns.get(col);
	}

	/**
	 * Updates the player table model
	 * 
	 * @param playerName Player name
	 * @param language Player language
	 * @param gamesPlayed Games played
	 * @param strength Player strength
	 */
	public void updatePlayer(String playerName, String language,
			long gamesPlayed, double strength) {
		
		boolean playerFound = false;
		int index = 0;
		
		// first try to find a player already known
		while (!playerFound && index < this.data.size()) {
			
			List<String> currRow = this.data.get(index);
			// check player name
			if (currRow.get(0).equals(playerName)) {
				// player found
				updateRow(0, language, gamesPlayed, strength);
				playerFound = true;
			}
			index++;
		}
		
		if (!playerFound) {
			// player not found --> new player
			// add this player
			addRow(playerName, language, gamesPlayed, strength);
		}
	}
	
	private void updateRow(int index, String language,
			long gamesPlayed, double strength) {
		
		List<String> row = this.data.get(index);
		// set updated values
		row.set(1, language);
		row.set(2, Long.toString(gamesPlayed));
		row.set(3, Double.toString(strength));
		
		this.fireTableRowsUpdated(index, index);
	}
	
	private void addRow(String playerName, String language,
			long gamesPlayed, double strength) {

		ArrayList<String> newLine = new ArrayList<String>();
		newLine.add(playerName);
		newLine.add(language);
		newLine.add(Long.toString(gamesPlayed));
		newLine.add(Double.toString(strength));
		this.data.add(newLine);

		this.fireTableRowsInserted(this.data.size() - 2, this.data.size() - 1);
	}
}
