/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.jskat.gui.JSkatView;

import net.miginfocom.swing.MigLayout;

/**
 * Panel for login into International Skat Server (ISS)
 */
public class ISSLoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param newMainView Main view
	 */
	public ISSLoginPanel(JSkatView newMainView) {
		
		super();
		initPanel();
		this.mainView = newMainView;
	}
	
	private void initPanel() {
		
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
		JButton loginButton = new JButton("Connect");
		loginButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            login();  // code to execute when button is pressed
	        }
	    });
		loginButton.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyChar() == 10) {
					login();
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO implement it
			}

			public void keyTyped(KeyEvent e) {
				// TODO implement it
			}
		});
		login.add(loginButton, "span 2, align center"); //$NON-NLS-1$

		return login;
	}
	
	/**
	 * Processes the login into ISS
	 * 
	 * @return TRUE, if the login was successful
	 */
	protected boolean login() {
		
		boolean result;
		String login = this.loginField.getText();
		String password = new String(this.passwordField.getPassword());
		
		if (login.length() == 0 || password.length() == 0) {
			
			JOptionPane.showMessageDialog(this, "Please provide credentials for ISS.", "No credentials", JOptionPane.ERROR_MESSAGE);
			result = false;
		}
		else {
		
			result = this.mainView.loginToISS(login, password, 80);
			
			if (!result) {
				
				JOptionPane.showMessageDialog(this, "Login to ISS failed.", "Login failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return true;
	}
	
	private JSkatView mainView;
	
	private JTextField loginField;
	private JPasswordField passwordField;
}
