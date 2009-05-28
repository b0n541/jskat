package de.jskat.gui.iss;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Provides a model for the skat list table
 */
class TableListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<List<String>> data;
	private List<String> columns;

	/**
	 * Constructor
	 */
	public TableListTableModel() {

		this.data = new ArrayList<List<String>>();
		this.columns = new ArrayList<String>();
		this.columns.add("Name"); //$NON-NLS-1$
		this.columns.add("Type"); //$NON-NLS-1$
		this.columns.add("Games"); //$NON-NLS-1$
		this.columns.add("Player 1"); //$NON-NLS-1$
		this.columns.add("Player 2"); //$NON-NLS-1$
		this.columns.add("Player 3"); //$NON-NLS-1$
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
	 * Updates the skat table table model
	 * 
	 * @param tableName
	 *            Table name
	 * @param maxPlayers
	 *            Table type (3 or 4 players)
	 * @param gamesPlayed
	 *            Games played
	 * @param player1
	 *            Player 1
	 * @param player2
	 *            Player 2
	 * @param player3
	 *            Player 3
	 */
	public void updatePlayer(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3) {

		boolean tableFound = false;
		int index = 0;

		// first try to find a player already known
		while (!tableFound && index < this.data.size()) {

			List<String> currRow = this.data.get(index);
			// check player name
			if (currRow.get(0).equals(tableName)) {
				// player found
				updateRow(index, maxPlayers, gamesPlayed, player1, player2,
						player3);
				tableFound = true;
			}
			index++;
		}

		if (!tableFound) {
			// table not found --> new table
			// add this table
			addRow(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
		}
	}

	private void updateRow(int index, int maxPlayers, long gamesPlayed,
			String player1, String player2, String player3) {

		List<String> row = this.data.get(index);
		// set updated values
		row.set(1, Integer.toString(maxPlayers));
		row.set(2, Long.toString(gamesPlayed));
		row.set(3, player1);
		row.set(4, player2);
		row.set(5, player3);

		this.fireTableDataChanged();
	}

	private void addRow(String tableName, int maxPlayers, long gamesPlayed,
			String player1, String player2, String player3) {

		ArrayList<String> newLine = new ArrayList<String>();
		newLine.add(tableName);
		newLine.add(Integer.toString(maxPlayers));
		newLine.add(Long.toString(gamesPlayed));
		newLine.add(player1);
		newLine.add(player2);
		newLine.add(player3);
		this.data.add(newLine);

		this.fireTableDataChanged();
	}

	public void removeTable(String tableName) {

		int index = 0;
		int removeIndex = 0;
		for (List<String> currRow : this.data) {

			if (currRow.get(0).equals(tableName)) {

				removeIndex = index;
			}
			index++;
		}
		this.data.remove(removeIndex);

		this.fireTableDataChanged();
	}
}
