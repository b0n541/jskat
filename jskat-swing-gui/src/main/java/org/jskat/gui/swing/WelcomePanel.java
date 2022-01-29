package org.jskat.gui.swing;

import org.jskat.control.gui.action.JSkatAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for welcome message and options for playing local or online games
 */
public class WelcomePanel extends AbstractTabPanel {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(WelcomePanel.class);

    /**
     * @param newTableName Table name
     * @param actions      Actions
     * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
     */
    public WelcomePanel(final String newTableName, final ActionMap actions) {

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

    private JScrollPane getWelcomePanel() {

        final JPanel welcomePanel = new JPanel(LayoutFactory.getMigLayout(
                "fill", "[]", "[shrink][grow]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        welcomePanel.add(createHeaderPanel(), "center, wrap"); //$NON-NLS-1$
        welcomePanel.add(createButtonPanel(), "center"); //$NON-NLS-1$

        return new JScrollPane(welcomePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JPanel createHeaderPanel() {

        final JPanel headerPanel = new JPanel(
                LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
        final JLabel logoLabel = new JLabel(new ImageIcon(
                this.bitmaps.getJSkatLogoImage()));
        headerPanel.add(logoLabel);
        final JLabel headerLabel = new JLabel(
                this.strings.getString("welcome_to_jskat")); //$NON-NLS-1$
        headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
        headerPanel.add(headerLabel, "center"); //$NON-NLS-1$
        return headerPanel;
    }

    private JPanel createButtonPanel() {

        final JButton issTableButton = new JButton(getActionMap().get(
                JSkatAction.SHOW_ISS_LOGIN));
        final JLabel issTableDescription = new JLabel("<html>"
                + this.strings.getString("explain_iss_table_1") + "<br>" //$NON-NLS-1$ //$NON-NLS-2$
                + this.strings.getString("explain_iss_table_2")); //$NON-NLS-1$//$NON-NLS-2$

        final JButton localTableButton = new JButton(this.getActionMap().get(
                JSkatAction.CREATE_LOCAL_TABLE));
        final JLabel localTableDescription = new JLabel("<html>" //$NON-NLS-1$
                + this.strings.getString("explain_local_table_1") + "<br>" //$NON-NLS-1$ //$NON-NLS-2$
                + this.strings.getString("explain_local_table_2")); //$NON-NLS-1$//$NON-NLS-2$

        final JButton optionsButton = new JButton(getActionMap().get(
                JSkatAction.PREFERENCES));
        final JLabel optionsDescription = new JLabel(
                this.strings.getString("explain_options_1")); //$NON-NLS-1$//$NON-NLS-2$

        final JButton quitButton = new JButton(getActionMap().get(
                JSkatAction.EXIT_JSKAT));
        final JLabel quitDescription = new JLabel(
                this.strings.getString("explain_exit")); //$NON-NLS-1$//$NON-NLS-2$

        final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout(
                "", "[][]", "[center][center][center][center]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buttonPanel.add(issTableButton, "growx, shrinky"); //$NON-NLS-1$
        buttonPanel.add(issTableDescription, "wrap"); //$NON-NLS-1$
        buttonPanel.add(localTableButton, "growx, shrinky"); //$NON-NLS-1$
        buttonPanel.add(localTableDescription, "wrap"); //$NON-NLS-1$
        buttonPanel.add(optionsButton, "growx, shrinky, gapy 1cm"); //$NON-NLS-1$
        buttonPanel.add(optionsDescription, "gapy 1cm, wrap"); //$NON-NLS-1$
        buttonPanel.add(quitButton, "growx, shrinky, gapy 1cm"); //$NON-NLS-1$
        buttonPanel.add(quitDescription, "gapy 1cm, wrap"); //$NON-NLS-1$

        return buttonPanel;
    }

    @Override
    protected void setFocus() {
        // no focus needed
    }
}
