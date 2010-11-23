/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.iss.ISSLoginCredentials;
import de.jskat.gui.AbstractTabPanel;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for login into International Skat Server (ISS)
 */
public class LoginPanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LoginPanel.class);

	JTextField loginField;
	private JPasswordField passwordField;

	/**
	 * @param newTableName
	 * @param jskatBitmaps
	 * @param actions
	 * @param strings
	 *            i18n strings
	 */
	public LoginPanel(String newTableName, JSkatGraphicRepository jskatBitmaps,
			ActionMap actions, ResourceBundle strings) {

		super(newTableName, jskatBitmaps, actions, strings);
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

		login.add(
				new JLabel("Connect to International Skat Server"), "span 2, align center, wrap"); //$NON-NLS-2$
		login.add(new JLabel("Login"));
		this.loginField = new JTextField(10);
		login.add(this.loginField, "growx, wrap"); //$NON-NLS-1$
		login.add(new JLabel("Password"));
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
		login.add(loginButton, "span 2, align center, wrap"); //$NON-NLS-1$

		final JButton issHomepageButton = new JButton(getActionMap().get(
				JSkatAction.OPEN_ISS_HOMEPAGE));
		login.add(issHomepageButton, "span 2, align center, wrap"); //$NON-NLS-1$

		final JButton issRegisterButton = new JButton(getActionMap().get(
				JSkatAction.REGISTER_ON_ISS));
		login.add(issRegisterButton, "span 2, align center"); //$NON-NLS-1$

		return login;
	}

	public void setFocus() {
		this.loginField.requestFocus();
	}
}
