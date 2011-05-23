/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.iss.ISSLoginCredentials;
import org.jskat.gui.AbstractTabPanel;
import org.jskat.gui.action.JSkatAction;


/**
 * Panel for login into International Skat Server (ISS)
 */
public class LoginPanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LoginPanel.class);

	JTextField loginField;
	private JPasswordField passwordField;

	/**
	 * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
	 */
	public LoginPanel(String newTableName, ActionMap actions) {

		super(newTableName, actions);
		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(new MigLayout("fill")); //$NON-NLS-1$

		getActionMap().get(JSkatAction.CREATE_ISS_TABLE).setEnabled(true);

		add(getLoginPanel(), "center"); //$NON-NLS-1$
	}

	private JPanel getLoginPanel() {

		JPanel login = new JPanel(new MigLayout());

		JLabel headerLabel = new JLabel(strings.getString("login_to_iss_title")); //$NON-NLS-1$
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		login.add(headerLabel, "span 2, align center, wrap"); //$NON-NLS-1$
		login.add(new JLabel(strings.getString("login"))); //$NON-NLS-1$
		this.loginField = new JTextField(10);
		login.add(this.loginField, "growx, wrap"); //$NON-NLS-1$
		login.add(new JLabel(strings.getString("password"))); //$NON-NLS-1$
		this.passwordField = new JPasswordField(10);
		login.add(this.passwordField, "growx, wrap"); //$NON-NLS-1$

		final JButton loginButton = new JButton(this.getActionMap().get(
				JSkatAction.CONNECT_TO_ISS));
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				ISSLoginCredentials loginCredentials = new ISSLoginCredentials();
				loginCredentials.setLoginName(LoginPanel.this.loginField
						.getText());
				loginCredentials.setPassword(new String(
						LoginPanel.this.passwordField.getPassword()));
				// FIXME must be setable
				loginCredentials.setPort(7000);

				e.setSource(loginCredentials);
				// fire event again
				loginButton.dispatchEvent(e);
			}
		});
		final JButton issHomepageButton = new JButton(getActionMap().get(
				JSkatAction.OPEN_ISS_HOMEPAGE));
		final JButton issRegisterButton = new JButton(getActionMap().get(
				JSkatAction.REGISTER_ON_ISS));

		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		buttonPanel.add(loginButton);
		buttonPanel.add(issHomepageButton);
		buttonPanel.add(issRegisterButton);

		login.add(buttonPanel, "span 2, align center"); //$NON-NLS-1$

		return login;
	}

	public void setFocus() {
		this.loginField.requestFocus();
	}
}
