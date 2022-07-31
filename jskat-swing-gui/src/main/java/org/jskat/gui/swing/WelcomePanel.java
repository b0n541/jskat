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
    private static final Logger log = LoggerFactory.getLogger(WelcomePanel.class);

    /**
     * @param newTableName Table name
     * @param actions      Actions
     * @see AbstractTabPanel#AbstractTabPanel(String, ActionMap)
     */
    public WelcomePanel(final String newTableName, final ActionMap actions) {

        super(newTableName, actions);
        log.debug("SkatTablePanel: name: " + newTableName);
    }

    /**
     * @see AbstractTabPanel#initPanel()
     */
    @Override
    protected void initPanel() {

        setLayout(LayoutFactory.getMigLayout("fill"));
        add(getWelcomePanel(), "grow, center");
    }

    private JScrollPane getWelcomePanel() {

        final JPanel welcomePanel = new JPanel(LayoutFactory.getMigLayout(
                "fill", "[]", "[shrink][grow]"));

        welcomePanel.add(createHeaderPanel(), "center, wrap");
        welcomePanel.add(createButtonPanel(), "center");

        return new JScrollPane(welcomePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JPanel createHeaderPanel() {

        final JPanel headerPanel = new JPanel(
                LayoutFactory.getMigLayout("fill"));
        final JLabel logoLabel = new JLabel(new ImageIcon(
                this.bitmaps.getJSkatLogoImage()));
        headerPanel.add(logoLabel);
        final JLabel headerLabel = new JLabel(
                this.strings.getString("welcome_to_jskat"));
        headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
        headerPanel.add(headerLabel, "center");
        return headerPanel;
    }

    private JPanel createButtonPanel() {

        final JButton issTableButton = new JButton(getActionMap().get(
                JSkatAction.SHOW_ISS_LOGIN));
        final JLabel issTableDescription = new JLabel("<html>"
                + this.strings.getString("explain_iss_table_1") + "<br>"
                + this.strings.getString("explain_iss_table_2"));

        final JButton localTableButton = new JButton(this.getActionMap().get(
                JSkatAction.CREATE_LOCAL_TABLE));
        final JLabel localTableDescription = new JLabel("<html>"
                + this.strings.getString("explain_local_table_1") + "<br>"
                + this.strings.getString("explain_local_table_2"));

        final JButton optionsButton = new JButton(getActionMap().get(
                JSkatAction.PREFERENCES));
        final JLabel optionsDescription = new JLabel(
                this.strings.getString("explain_options_1"));

        final JButton quitButton = new JButton(getActionMap().get(
                JSkatAction.EXIT_JSKAT));
        final JLabel quitDescription = new JLabel(
                this.strings.getString("explain_exit"));

        final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout(
                "", "[][]", "[center][center][center][center]"));
        buttonPanel.add(issTableButton, "growx, shrinky");
        buttonPanel.add(issTableDescription, "wrap");
        buttonPanel.add(localTableButton, "growx, shrinky");
        buttonPanel.add(localTableDescription, "wrap");
        buttonPanel.add(optionsButton, "growx, shrinky, gapy 1cm");
        buttonPanel.add(optionsDescription, "gapy 1cm, wrap");
        buttonPanel.add(quitButton, "growx, shrinky, gapy 1cm");
        buttonPanel.add(quitDescription, "gapy 1cm, wrap");

        return buttonPanel;
    }

    @Override
    protected void setFocus() {
        // no focus needed
    }
}
