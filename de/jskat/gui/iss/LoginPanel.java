/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

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

import de.jskat.data.iss.ISSLoginCredentials;
import de.jskat.gui.JSkatTabPanel;
import de.jskat.gui.action.JSkatActions;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for login into International Skat Server (ISS)
 */
public class LoginPanel extends JSkatTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LoginPanel.class);

	private JTextField loginField;
	private JPasswordField passwordField;
	
	/**
	 * @param newTableName
	 * @param jskatBitmaps
	 * @param actions
	 */
	public LoginPanel(String newTableName,
			JSkatGraphicRepository jskatBitmaps, ActionMap actions) {
		
		super(newTableName, jskatBitmaps, actions);
		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}
	
	/**
	 * @see JSkatTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {
		
		setLayout(new MigLayout("fill")); //$NON-NLS-1$
		
		add(getLoginPanel(), "center"); //$NON-NLS-1$
	}
	
	private JPanel getLoginPanel() {
		
		JPanel login = new JPanel(new MigLayout());
		
		login.add(new JLabel("Connect to International Skat Server"), "span 2, align center, wrap");
		login.add(new JLabel("Login"));
		this.loginField = new JTextField(10);
		login.add(this.loginField, "growx, wrap"); //$NON-NLS-1$
		login.add(new JLabel("Password"));
		this.passwordField = new JPasswordField(10);
		login.add(this.passwordField, "growx, wrap"); //$NON-NLS-1$
		final JButton loginButton = new JButton(this.getActionMap().get(JSkatActions.CONNECT_TO_ISS));
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ISSLoginCredentials login = new ISSLoginCredentials();
				login.setLoginName(loginField.getText());
				login.setPassword(new String(passwordField.getPassword()));
				// FIXME must be setable
				login.setPort(80);
				
				e.setSource(login);  
				// fire event again
				loginButton.dispatchEvent(e);
			}
		});

		login.add(loginButton, "span 2, align center"); //$NON-NLS-1$

		return login;
	}
}
