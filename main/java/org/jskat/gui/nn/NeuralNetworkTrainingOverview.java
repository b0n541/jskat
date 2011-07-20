/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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
package org.jskat.gui.nn;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jskat.util.GameType;

import net.miginfocom.swing.MigLayout;

/**
 * Overview dialog for training of neural networks
 */
public class NeuralNetworkTrainingOverview extends JDialog {

	JTable overviewTable;

	public NeuralNetworkTrainingOverview() {

		initGUI();
	}

	private void initGUI() {

		setMinimumSize(new Dimension(400, 300));

		setTitle("Training of neural networks");

		Container root = getContentPane();
		root.setLayout(new MigLayout());

		JPanel rootPanel = new JPanel(new MigLayout("fill", "fill", "fill"));

		overviewTable = new JTable(new TrainingOverviewTableModel());
		overviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		JScrollPane scrollPane = new JScrollPane(overviewTable);
		rootPanel.add(scrollPane, "grow, center");

		root.add(rootPanel, "center, grow");
	}

	/**
	 * Adds training result
	 * 
	 * @param gameType
	 *            Game type of neural net
	 * @param episodes
	 *            Number of episodes
	 * @param totalWonGames
	 *            Total Number of won games
	 * @param episodeWonGames
	 *            Number of won games in last episode
	 * @param totalDeclarerNetError
	 *            Total error in declarer net
	 * @param totalOpponentNetError
	 *            Total error in opponent net
	 */
	public void addTrainingResult(GameType gameType, Long episodes, Long totalWonGames, Long episodeWonGames,
			Double totalDeclarerNetError, Double totalOpponentNetError) {

		((TrainingOverviewTableModel) overviewTable.getModel()).addTrainingResult(gameType, episodes, totalWonGames,
				episodeWonGames, totalDeclarerNetError, totalOpponentNetError);
	}

	private class TrainingOverviewTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<String> header;
		private HashMap<GameType, List<Object>> data;

		protected TrainingOverviewTableModel() {

			header = new ArrayList<String>();
			header.add("Game type");
			header.add("Episodes");
			header.add("Total won games");
			header.add("Episode won games");
			header.add("Average declarer difference");
			header.add("Average opponent difference");

			data = new HashMap<GameType, List<Object>>();

			for (GameType currGameType : GameType.values()) {

				List<Object> list = new ArrayList<Object>();
				list.add(currGameType);
				for (int i = 1; i < getColumnCount(); i++) {
					list.add(0);
				}
				data.put(currGameType, list);
			}
		}

		@Override
		public int getRowCount() {

			return GameType.values().length;
		}

		@Override
		public int getColumnCount() {

			return 6;
		}

		@Override
		public String getColumnName(int column) {

			return header.get(column);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			GameType gameType = GameType.values()[rowIndex];

			return data.get(gameType).get(columnIndex);
		}

		/**
		 * Adds training result
		 * 
		 * @param gameType
		 *            Game type of neural net
		 * @param episodes
		 *            Number of episodes
		 * @param totalWonGames
		 *            Total number of won games
		 * @param episodeWonGames
		 *            Number of won games in last episode
		 * @param totalDeclarerNetError
		 *            Total error in declarer net
		 * @param totalOpponentNetError
		 *            Total error in opponent net
		 */
		public void addTrainingResult(GameType gameType, Long episodes, Long totalWonGames, Long episodeWonGames,
				Double totalDeclarerNetError, Double totalOpponentNetError) {

			TableModel tableModel = overviewTable.getModel();

			tableModel.setValueAt(gameType, gameType.ordinal(), 0);
			tableModel.setValueAt(episodes, gameType.ordinal(), 1);
			tableModel.setValueAt(totalWonGames, gameType.ordinal(), 2);
			tableModel.setValueAt(episodeWonGames, gameType.ordinal(), 3);
			tableModel.setValueAt(totalDeclarerNetError, gameType.ordinal(), 4);
			tableModel.setValueAt(totalOpponentNetError, gameType.ordinal(), 5);

			fireTableDataChanged();
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {

			data.get(GameType.values()[rowIndex]).set(columnIndex, value);
		}
	}
}
