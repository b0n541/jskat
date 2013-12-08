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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jskat.data.iss.LoginCredentials;
import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.AbstractTabPanel;
import org.jskat.gui.swing.LayoutFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for login into International Skat Server (ISS)
 */
public class LoginPanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LoginPanel.class);

	JTextField loginField;
	JPasswordField passwordField;

	/**
	 * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
	 */
	public LoginPanel(final JSkatView view, final String newTableName,
			final ActionMap actions) {

		super(view, newTableName, actions);
		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		getActionMap().get(JSkatAction.CREATE_ISS_TABLE).setEnabled(true);

		add(getLoginPanel(), "center"); //$NON-NLS-1$
	}

	private JPanel getLoginPanel() {

		final JPanel login = new JPanel(LayoutFactory.getMigLayout());

		final JLabel headerLabel = new JLabel(
				strings.getString("login_to_iss_title")); //$NON-NLS-1$
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
			public void actionPerformed(final ActionEvent e) {

				final LoginCredentials loginCredentials = new LoginCredentials();
				loginCredentials.setLoginName(LoginPanel.this.loginField
						.getText());
				loginCredentials.setPassword(new String(passwordField
						.getPassword()));

				e.setSource(loginCredentials);
				// fire event again
				loginButton.dispatchEvent(e);
			}
		});
		final JButton issHomepageButton = new JButton(getActionMap().get(
				JSkatAction.OPEN_ISS_HOMEPAGE));
		final JButton issRegisterButton = new JButton(getActionMap().get(
				JSkatAction.REGISTER_ON_ISS));

		final JPanel buttonPanel = new JPanel(
				LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		buttonPanel.add(loginButton);
		buttonPanel.add(issHomepageButton);
		buttonPanel.add(issRegisterButton);

		login.add(buttonPanel, "span 2, align center"); //$NON-NLS-1$

		return login;
	}

	@Override
	public void setFocus() {
		this.loginField.requestFocus();
	}
}
