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
package org.jskat.gui.swing.nn;

import java.awt.Container;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.GameType;

/**
 * Overview dialog for training of neural networks
 */
public class NeuralNetworkTrainingOverview extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JFrame parent;

	JTable overviewTable;

	/**
	 * Constructor
	 */
	public NeuralNetworkTrainingOverview(final JFrame mainFrame) {

		parent = mainFrame;
		initGUI();
	}

	private void initGUI() {

		setMinimumSize(new Dimension(600, 480));

		setTitle("Training of neural networks");

		Container root = getContentPane();
		root.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill"));

		JPanel rootPanel = new JPanel(LayoutFactory.getMigLayout("fill",
				"fill", "fill"));

		overviewTable = new JTable(new TrainingOverviewTableModel());
		overviewTable.getColumnModel().getColumn(3)
				.setCellRenderer(new DoubleRenderer(5));
		overviewTable.getColumnModel().getColumn(4)
				.setCellRenderer(new DoubleRenderer(10));
		overviewTable.getColumnModel().getColumn(5)
				.setCellRenderer(new DoubleRenderer(10));
		overviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		JScrollPane scrollPane = new JScrollPane(overviewTable);
		rootPanel.add(scrollPane, "grow, center");

		root.add(rootPanel, "center, grow");
	}

	private class DoubleRenderer extends DefaultTableCellRenderer {
		private DecimalFormat formatter;
		private int fractionDigits;

		public DoubleRenderer(int fractionDigits) {
			super();
			setHorizontalAlignment(SwingConstants.RIGHT);
			this.fractionDigits = fractionDigits;
		}

		@Override
		public void setValue(Object value) {
			if (formatter == null) {
				formatter = new DecimalFormat("###,###,###.##");
				formatter.setMinimumFractionDigits(fractionDigits);
				formatter.setMaximumFractionDigits(fractionDigits);
				DecimalFormatSymbols dfs = formatter.getDecimalFormatSymbols();
				dfs.setGroupingSeparator(' ');
				dfs.setDecimalSeparator(',');
				formatter.setDecimalFormatSymbols(dfs);
			}
			setText((value == null) ? "" : formatter.format(value));
		}
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
	 * @param avgNetworkErrorDeclarer
	 *            Average difference of declarer network
	 * @param avgNetworkErrorOpponents
	 *            Average difference of opponents networks
	 */
	public void addTrainingResult(GameType gameType, Long episodes,
			Long totalWonGames, Double avgNetworkErrorDeclarer,
			Double avgNetworkErrorOpponents) {

		((TrainingOverviewTableModel) overviewTable.getModel())
				.addTrainingResult(gameType, episodes, totalWonGames,
						avgNetworkErrorDeclarer, avgNetworkErrorOpponents);
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
			header.add("Percent");
			header.add("Network error declarer");
			header.add("Network error opponents");

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
		 * @param avgNetworkErrorDeclarer
		 *            Average error of declarer network
		 * @param avgNetworkErrorOpponents
		 *            Average error of opponents networks
		 */
		public void addTrainingResult(GameType gameType, Long episodes,
				Long totalWonGames, Double avgNetworkErrorDeclarer,
				Double avgNetworkErrorOpponents) {

			TableModel tableModel = overviewTable.getModel();

			tableModel.setValueAt(gameType, gameType.ordinal(), 0);
			tableModel.setValueAt(episodes, gameType.ordinal(), 1);
			tableModel.setValueAt(totalWonGames, gameType.ordinal(), 2);
			tableModel.setValueAt(totalWonGames * 100.0d / episodes,
					gameType.ordinal(), 3);
			tableModel.setValueAt(avgNetworkErrorDeclarer, gameType.ordinal(),
					4);
			tableModel.setValueAt(avgNetworkErrorOpponents, gameType.ordinal(),
					5);

			fireTableDataChanged();
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {

			data.get(GameType.values()[rowIndex]).set(columnIndex, value);
		}
	}

	/**
	 * @see JDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean isVisible) {

		if (isVisible) {
			setLocationRelativeTo(parent);
		}

		super.setVisible(isVisible);
	}
}
