/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import java.util.Observer;
import java.util.Observable;
import java.util.Vector;
import java.util.ResourceBundle;

import jskat.control.JSkatMaster;
import jskat.control.SkatGame;
import jskat.control.SkatSeries;
import jskat.control.SkatTable;
import jskat.data.JSkatDataModel;
import jskat.player.JSkatPlayer;
import jskat.share.SkatConstants;

/**
 * The table model for the score table
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class ScoreTableModel extends AbstractTableModel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5600052721425524314L;

	static Logger log = Logger.getLogger(jskat.gui.main.ScoreTableModel.class);

	/**
	 * Creates a new ScoreTableDataModel
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 */
	public ScoreTableModel(JSkatDataModel dataModel) {

		data = new Vector<Vector<String>>();

		dataModel.getJSkatMaster().addObserver(this);
		jskatStrings = dataModel.getResourceBundle();

		columnNames = new String[5];

		columnNames[0] = " ";
		columnNames[1] = " ";
		columnNames[2] = " ";
		columnNames[3] = jskatStrings.getString("games");
		columnNames[4] = "B.";
	}

	/**
	 * Gets the column count
	 * 
	 * @return column count
	 */
	public int getColumnCount() {

		return columnNames.length;
	}

	/**
	 * Gets the row count
	 * 
	 * @return row count
	 */
	public int getRowCount() {

		return data.size();
	}

	/**
	 * Gets the name of a column
	 * 
	 * @param col
	 *            ID of the column
	 * @return name of the column
	 */
	public String getColumnName(int col) {

		return columnNames[col];
	}

	/**
	 * Get the value of one table cell
	 * 
	 * @param row
	 *            row of table cell
	 * @param col
	 *            column of table cell
	 * @return the value of the table cell
	 */
	public Object getValueAt(int row, int col) {

		return ((Vector) data.get(row)).get(col);
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	/**
	 * Gets the class of a column
	 * 
	 * @param c
	 *            the ID of the column
	 * @return the class of the column
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {

		if (c == 4)
			return java.lang.String.class;
		else
			return java.lang.Integer.class;
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	/**
	 * Returns whether a cell is editable or not
	 * 
	 * @param row
	 *            row of the table cell
	 * @param col
	 *            column of the table cell
	 * @return TRUE if the cell is editable
	 */
	public boolean isCellEditable(int row, int col) {

		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	/**
	 * Sets a value for a table cell
	 * 
	 * @param value
	 *            the value to be set
	 * @param row
	 *            the row of the table cell
	 * @param col
	 *            the column of the table cell
	 */
	public void setValueAt(Object value, int row, int col) {

		if (true) {

			log.debug("Setting value at " + row + "," + col + " to " + value
					+ " (an instance of " + value.getClass() + ")");
		}

		// data[row][col] = value;
		fireTableCellUpdated(row, col);

		if (true) {

			log.debug("New value of data:");
			printDebugData();
		}
	}

	private void printDebugData() {

		int numRows = getRowCount();
		int numCols = getColumnCount();
		String rowString;

		for (int i = 0; i < numRows; i++) {

			rowString = "    row " + i + ":";
			for (int j = 0; j < numCols; j++) {
				rowString = rowString + "  " + ((Vector) data.get(i)).get(j);
			}

			log.debug(rowString);
		}

		log.debug("--------------------------");
	}

	/**
	 * Implementation of the Observer pattern
	 * 
	 * @param observ
	 *            The Observable that is observed
	 * @param obj
	 *            The Object that has changed in the Observable
	 */
	public void update(Observable observ, Object obj) {

		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");
		
		if (observ instanceof JSkatMaster && obj instanceof SkatTable) {
			
			((SkatTable) obj).addObserver(this);
			
		} else if (observ instanceof SkatTable && obj instanceof SkatSeries) {
			
			((SkatSeries) obj).addObserver(this);
			
			if( ((SkatTable) observ).getSkatTableData().getCurrSkatSeries() != null) {
				JSkatPlayer[] players = ((SkatTable) observ).getPlayers();
				
				if(players!=null) {
					// Update the player names
					columnNames[0] = players[0].getPlayerName();
					columnNames[1] = players[1].getPlayerName();
					columnNames[2] = players[2].getPlayerName();
			
					this.fireTableStructureChanged();
				}
				
			}
			
			data.clear();
			
			this.fireTableDataChanged();
	
		} else if (observ instanceof SkatSeries && obj instanceof SkatGame) {
			((SkatGame) obj).addObserver(this);
		} else if (observ instanceof SkatGame) {

			SkatGame currGame = (SkatGame) observ;

			if (currGame.getState() == SkatGame.GAMESTATE_NEXT_GAME) {

				Vector<String> newRow = new Vector<String>();

				for (int i = 0; i < 5; i++) {

					if (i == currGame.getSkatGameData().getSinglePlayer()) {

						if (data.size() == 0) {

							// first game in series --> just put in the game value
							newRow.add(new Integer(currGame.getSkatGameData()
									.getGameResult()).toString());

						} else {

							// it's not the first game --> search the last
							// entry for this player and add the current game
							// value to the last score

							int oldScore = 0;
							int j = data.size();

							log.debug("j = " + j);
							log.debug("i = " + i);

							while (j > 0 && oldScore == 0) {

								if (((Vector) data.get(j - 1)).get(i)
										.toString() != "-") {

									// TODO: this works but is terrible, go and change it
									oldScore = new Integer((String) ((Vector)data.get(
											j - 1)).get(i)).intValue();
								}

								j--;
							}

							newRow.add(new Integer(oldScore
									+ currGame.getSkatGameData().getGameResult())
									.toString());
						}

					} else if (i == 3) {

						// it's the game column --> put in the game value
						newRow.add(new Integer(currGame.getSkatGameData()
								.getGameResult()).toString());

					} else if (i == 4) {

						// it's the game type column --> put in the game type
						int gameType = currGame.getSkatGameData().getGameType();
						String text = "";
						if(gameType == SkatConstants.RAMSCH || gameType == SkatConstants.GRAND) {
							text = SkatConstants.getGameType(gameType);
						}
						else {
							text = SkatConstants.getSuit(currGame.getSkatGameData().getTrump());
						}
						newRow.add(text);

					} else {

						newRow.add("-");

					}
				}

				newRow.add(" ");

				data.add(newRow);

				fireTableRowsInserted(data.size(), data.size());
				// printDebugData();

				JTable table = (JTable) getTableModelListeners()[0];
				table.scrollRectToVisible(table.getCellRect(data.size(), -1,
						true));
			}
		}
	}

	private String[] columnNames;

	private Vector<Vector<String>> data;

	private ResourceBundle jskatStrings;
}
