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
package org.jskat.gui.swing.iss;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jskat.util.JSkatResourceBundle;

/**
 * Provides a model for the skat list table
 */
class PlayerListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final JSkatResourceBundle strings;

	private final List<List<Object>> data;
	private final List<String> columns;

	/**
	 * Constructor
	 */
	public PlayerListTableModel() {

		strings = JSkatResourceBundle.instance();
		data = new ArrayList<List<Object>>();
		columns = new ArrayList<String>();
		columns.add(strings.getString("name"));
		columns.add(strings.getString("games"));
		columns.add(strings.getString("strength"));
		columns.add(strings.getString("language"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getColumnClass(final int columnIndex) {

		if (columnIndex == 1) {
			return Long.class;
		} else if (columnIndex == 2) {
			return Long.class;
		}
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColumnCount() {

		return columns.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {

		return data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		return data.get(rowIndex).get(columnIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(final int col) {

		return columns.get(col);
	}

	/**
	 * Updates the player table model
	 * 
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Player language
	 * @param gamesPlayed
	 *            Games played
	 * @param strength
	 *            Player strength
	 */
	public void updatePlayer(final String playerName, final String language,
			final long gamesPlayed, final double strength) {

		boolean playerFound = false;
		int index = 0;

		// first try to find a player already known
		while (!playerFound && index < data.size()) {

			List<Object> currRow = data.get(index);
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

	private void updateRow(final int index, final String language,
			final long gamesPlayed, final double strength) {

		List<Object> row = data.get(index);
		// set updated values
		row.set(1, Long.toString(gamesPlayed));
		row.set(2, Double.toString(strength));
		row.set(3, language);

		fireTableDataChanged();
	}

	private void addRow(final String playerName, final String language,
			final long gamesPlayed, final double strength) {

		ArrayList<Object> newLine = new ArrayList<Object>();
		newLine.add(playerName);
		newLine.add(Long.valueOf(gamesPlayed));
		newLine.add(Double.valueOf(strength));
		newLine.add(language);
		data.add(newLine);

		fireTableDataChanged();
	}

	public void removePlayer(final String playerName) {

		int index = 0;
		int removeIndex = 0;
		for (List<Object> currRow : data) {

			if (currRow.get(0).equals(playerName)) {

				removeIndex = index;
			}
			index++;
		}
		data.remove(removeIndex);

		fireTableDataChanged();
	}
}
