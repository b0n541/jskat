/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
package org.jskat.gui;

import java.awt.Font;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.gui.action.JSkatAction;

/**
 * Panel for welcome message and options for playing local or online games
 */
public class WelcomePanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WelcomePanel.class);

	/**
	 * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
	 */
	public WelcomePanel(String newTableName, ActionMap actions) {

		super(newTableName, actions);
		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		add(getWelcomePanel(), "grow, center"); //$NON-NLS-1$
	}

	private JPanel getWelcomePanel() {

		JPanel welcomePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill",
				"[shrink][grow]"));

		JPanel headerPanel = new JPanel(LayoutFactory.getMigLayout("fill"));
		JLabel headerLabel = new JLabel(strings.getString("welcome_to_jskat")); //$NON-NLS-1$
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerPanel.add(headerLabel, "center");
		welcomePanel.add(headerPanel, "shrink, wrap"); //$NON-NLS-1$

		JPanel localTablePanel = new JPanel(LayoutFactory.getMigLayout("fill"));
		final JButton localTableButton = new JButton(this.getActionMap().get(
				JSkatAction.CREATE_LOCAL_TABLE));
		localTablePanel.add(localTableButton, "center, wrap");
		localTablePanel.add(new JLabel("<html><p>"
				+ strings.getString("explain_local_table_1") + "</p><p>"
						+ strings.getString("explain_local_table_2")
						+ "</p></html>"), "center");

		JPanel issTablePanel = new JPanel(LayoutFactory.getMigLayout("fill"));
		final JButton issTableButton = new JButton(getActionMap().get(
				JSkatAction.SHOW_ISS_LOGIN));
		issTablePanel.add(issTableButton, "center, wrap");
		issTablePanel.add(new JLabel("<html><p>"
				+ strings.getString("explain_iss_table_1") + "</p><p>"
						+ strings.getString("explain_iss_table_2")
						+ "</p></html>"), "center");

		JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		buttonPanel.add(localTablePanel, "width 50%");
		buttonPanel.add(issTablePanel, "width 50%");

		welcomePanel.add(buttonPanel, "align center"); //$NON-NLS-1$

		return welcomePanel;
	}

	@Override
	protected void setFocus() {
		// no focus needed
	}
}
