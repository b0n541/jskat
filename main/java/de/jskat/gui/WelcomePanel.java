/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Font;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.gui.action.JSkatAction;

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

		setLayout(new MigLayout("fill")); //$NON-NLS-1$
		add(getWelcomePanel(), "grow, center"); //$NON-NLS-1$
	}

	private JPanel getWelcomePanel() {

		JPanel welcomePanel = new JPanel(new MigLayout("fill"));

		JLabel headerLabel = new JLabel(strings.getString("welcome_to_jskat")); //$NON-NLS-1$
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		welcomePanel.add(headerLabel, "shrink, span 2, align center, wrap"); //$NON-NLS-1$

		JPanel localTablePanel = new JPanel(new MigLayout("fill"));
		final JButton localTableButton = new JButton(this.getActionMap().get(
				JSkatAction.CREATE_LOCAL_TABLE));
		localTablePanel.add(localTableButton, "center, wrap");
		localTablePanel.add(new JLabel("<html><p>"
				+ strings.getString("explain_local_table_1") + "</p><p>"
						+ strings.getString("explain_local_table_2")
						+ "</p></html>"), "center");

		JPanel issTablePanel = new JPanel(new MigLayout("fill"));
		final JButton issTableButton = new JButton(getActionMap().get(
				JSkatAction.SHOW_ISS_LOGIN));
		issTablePanel.add(issTableButton, "center, wrap");
		issTablePanel.add(new JLabel("<html><p>"
				+ strings.getString("explain_iss_table_1") + "</p><p>"
						+ strings.getString("explain_iss_table_2")
						+ "</p></html>"), "center");

		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		buttonPanel.add(localTablePanel, "width 50%");
		buttonPanel.add(issTablePanel, "width 50%");

		welcomePanel.add(buttonPanel, "grow, span 2, align center"); //$NON-NLS-1$

		return welcomePanel;
	}

	@Override
	protected void setFocus() {
		// no focus needed
	}
}
