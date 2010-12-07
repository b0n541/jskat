/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.Font;
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

import de.jskat.data.JSkatOptions;
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
	 * @see AbstractTabPanel#AbstractTabPanel(String, JSkatGraphicRepository,
	 *      ActionMap, ResourceBundle, JSkatOptions)
	 */
	public LoginPanel(String newTableName, JSkatGraphicRepository jskatBitmaps,
			ActionMap actions, ResourceBundle jskatStrings,
			JSkatOptions jskatOptions) {

		super(newTableName, jskatBitmaps, actions, jskatStrings, jskatOptions);
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
