/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jskat.gui.iss;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jskat.util.JSkatResourceBundle;


/**
 * Provides a model for the skat list table
 */
class PlayerListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private JSkatResourceBundle strings;

	private List<List<String>> data;
	private List<String> columns;

	/**
	 * Constructor
	 */
	public PlayerListTableModel() {

		strings = JSkatResourceBundle.instance();
		this.data = new ArrayList<List<String>>();
		this.columns = new ArrayList<String>();
		this.columns.add(strings.getString("name"));
		this.columns.add(strings.getString("language"));
		this.columns.add(strings.getString("games"));
		this.columns.add(strings.getString("strength"));
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
		
		this.fireTableDataChanged();
	}
	
	private void addRow(String playerName, String language,
			long gamesPlayed, double strength) {

		ArrayList<String> newLine = new ArrayList<String>();
		newLine.add(playerName);
		newLine.add(language);
		newLine.add(Long.toString(gamesPlayed));
		newLine.add(Double.toString(strength));
		this.data.add(newLine);

		this.fireTableDataChanged();
	}

	public void removePlayer(String playerName) {

		int index = 0;
		int removeIndex = 0;
		for (List<String> currRow : this.data) {
			
			if (currRow.get(0).equals(playerName)) {
				
				removeIndex = index;
			}
			index++;
		}
		this.data.remove(removeIndex);
		
		this.fireTableDataChanged();
	}
}
