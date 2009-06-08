package de.jskat.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Provides a model for the skat list table
 */
class SkatListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<List<String>> data;
	private List<String> columns;

	/**
	 * Constructor
	 */
	public SkatListTableModel() {

		this.data = new ArrayList<List<String>>();
		this.columns = new ArrayList<String>();
		this.columns.add("P1");
		this.columns.add("P2");
		this.columns.add("P3");
		this.columns.add("Games");
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
	 * Adds a row to the model
	 * 
	 * @param p1 Value for player 1
	 * @param p2 Value for player 2
	 * @param p3 Value for player 3
	 * @param game Game value
	 */
	void addRow(String p1, String p2, String p3, String game) {

		ArrayList<String> newLine = new ArrayList<String>();
		newLine.add(p1);
		newLine.add(p2);
		newLine.add(p3);
		newLine.add(game);
		this.data.add(newLine);

		this.fireTableRowsInserted(this.data.size() - 1, this.data.size() - 1);
	}
	
	/**
	 * Clears the complete list
	 */
	void clearList() {
		
		this.data.clear();
		
		this.fireTableDataChanged();
	}
	
	public void setPlayerCount(int playerCount) {
		
		this.data.clear();
		
		this.columns.clear();
		for (int i = 0; i < playerCount; i++) {
			
			this.columns.add("P" + i);
		}
		this.columns.add("Games");
		
		this.fireTableStructureChanged();
		this.fireTableDataChanged();
	}
}
