package org.jskat.gui.swing.iss;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.iss.LoginCredentials;
import org.jskat.gui.swing.AbstractTabPanel;
import org.jskat.gui.swing.LayoutFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for login into International Skat Server (ISS)
 */
@Deprecated
public class LoginPanel extends AbstractTabPanel {


    private static final Logger log = LoggerFactory.getLogger(LoginPanel.class);

    JTextField loginField;
    JPasswordField passwordField;

    /**
     * @param tableName Table name
     * @param actions   Actions
     * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
     */
    public LoginPanel(final String tableName, final ActionMap actions) {

        super(tableName, actions);
        log.debug("SkatTablePanel: name: " + tableName);
    }

    /**
     * @see AbstractTabPanel#initPanel()
     */
    @Override
    protected void initPanel() {

        setLayout(LayoutFactory.getMigLayout("fill"));

        getActionMap().get(JSkatAction.CREATE_ISS_TABLE).setEnabled(true);

        add(getLoginPanel(), "center");
    }

    private JPanel getLoginPanel() {

        final JPanel login = new JPanel(LayoutFactory.getMigLayout());

        final JLabel headerLabel = new JLabel(this.strings.getString("login_to_iss_title"));
        headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
        login.add(headerLabel, "span 2, align center, wrap");
        login.add(new JLabel(this.strings.getString("login")));
        this.loginField = new JTextField(10);
        login.add(this.loginField, "growx, wrap");
        login.add(new JLabel(this.strings.getString("password")));
        this.passwordField = new JPasswordField(10);
        login.add(this.passwordField, "growx, wrap");

        final JButton loginButton = new JButton(this.getActionMap().get(JSkatAction.CONNECT_TO_ISS));
        loginButton.addActionListener(e -> {
            final LoginCredentials loginCredentials =
                    new LoginCredentials(
                            LoginPanel.this.loginField.getText(),
                            new String(LoginPanel.this.passwordField.getPassword()));

            e.setSource(loginCredentials);
            // fire event again
            loginButton.dispatchEvent(e);
        });
        final JButton issHomepageButton = new JButton(getActionMap().get(JSkatAction.OPEN_ISS_HOMEPAGE));
        final JButton issRegisterButton = new JButton(getActionMap().get(JSkatAction.REGISTER_ON_ISS));

        final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("fill"));

        buttonPanel.add(loginButton);
        buttonPanel.add(issHomepageButton);
        buttonPanel.add(issRegisterButton);

        login.add(buttonPanel, "span 2, align center");

        return login;
    }

    @Override
    public void setFocus() {
        this.loginField.requestFocus();
    }
}
