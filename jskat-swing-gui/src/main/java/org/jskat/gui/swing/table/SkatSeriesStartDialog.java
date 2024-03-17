package org.jskat.gui.swing.table;

import org.jskat.control.JSkatMaster;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.swing.AbstractI18NComboBoxRenderer;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.player.JSkatPlayerResolver;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asks for user defined options on start of a skat series
 */
public class SkatSeriesStartDialog extends JDialog implements ActionListener {


    private static final String CANCEL = "CANCEL";
    private static final String START = "START";

    private static final String PLAYER1_DEFAULT_NAME = "Jan";
    private static final String PLAYER2_DEFAULT_NAME = "Markus";
    private static final String PLAYER3_DEFAULT_NAME = System
            .getProperty("user.name");

    private final Component parent;

    private final JSkatOptions options;
    private final JSkatResourceBundle strings;

    private JTextField player1name;
    private JTextField player2name;
    private JTextField player3name;
    private JComboBox player1;
    private JComboBox player2;
    private JComboBox player3;
    private JSpinner numberOfRounds;
    private JCheckBox unlimited;
    private JCheckBox onlyPlayRamsch;

    /**
     * Constructor
     *
     * @param parent Parent component of the skat series start dialog
     */
    public SkatSeriesStartDialog(final Component parent) {

        this.parent = parent;
        strings = JSkatResourceBundle.INSTANCE;
        options = JSkatOptions.instance();

        initGUI();
    }

    private void initGUI() {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        setTitle(strings.getString("start_series"));

        final Container root = getContentPane();
        root.setLayout(LayoutFactory.getMigLayout());

        final List<String> playerTypes = new ArrayList<>();
        for (final String aiPlayer : JSkatPlayerResolver.getAllAIPlayerImplementations()) playerTypes.add(aiPlayer);
        Collections.sort(playerTypes);

        root.add(new JLabel(strings.getString("player") + " 1"));
        player1name = new JTextField(PLAYER1_DEFAULT_NAME);
        root.add(player1name, "span2, growx");
        player1 = new JComboBox(playerTypes.toArray());
        player1.setRenderer(new PlayerTypeComboBoxRenderer());
        root.add(player1, "growx, wrap");

        root.add(new JLabel(strings.getString("player") + " 2"));
        player2name = new JTextField(PLAYER2_DEFAULT_NAME);
        root.add(player2name, "span2, growx");
        player2 = new JComboBox(playerTypes.toArray());
        player2.setRenderer(new PlayerTypeComboBoxRenderer());
        root.add(player2, "growx, wrap");
        root.add(new JLabel(strings.getString("player") + " 3"));

        player3name = new JTextField(PLAYER3_DEFAULT_NAME);
        root.add(player3name, "span2, growx");
        // Human player can only be player 3
        playerTypes.add(JSkatPlayerResolver.HUMAN_PLAYER_CLASS);
        player3 = new JComboBox(playerTypes.toArray());
        player3.setRenderer(new PlayerTypeComboBoxRenderer());
        player3.setSelectedIndex(player3.getItemCount() - 1);
        root.add(player3, "span2, growx, wrap");

        root.add(new JLabel(strings.getString("number_of_rounds")));
        numberOfRounds = new JSpinner(new SpinnerNumberModel(12, 1, 48, 1));
        root.add(numberOfRounds);
        unlimited = new JCheckBox(strings.getString("unlimited"));
        unlimited.addChangeListener(e -> numberOfRounds.setEnabled(!unlimited.isSelected()));
        root.add(unlimited, "wrap");
        root.add(new JLabel(strings.getString("ramsch")));
        onlyPlayRamsch = new JCheckBox(strings.getString("only_play_ramsch"));
        root.add(onlyPlayRamsch, "wrap");

        final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout());
        final JButton start = new JButton(strings.getString("start"));
        start.setActionCommand(START);
        start.addActionListener(this);
        buttonPanel.add(start);
        final JButton cancel = new JButton(strings.getString("cancel"));
        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);
        buttonPanel.add(cancel);
        root.add(buttonPanel, "span 4, center");

        pack();
    }

    /**
     * @see JDialog#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean isVisible) {

        if (options.isPlayRamsch(true)) {
            onlyPlayRamsch.setEnabled(true);
        } else {
            onlyPlayRamsch.setSelected(false);
            onlyPlayRamsch.setEnabled(false);
        }

        if (isVisible) {
            setLocationRelativeTo(parent);
        }

        super.setVisible(isVisible);
    }

    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (CANCEL.equals(e.getActionCommand())) setVisible(false);
        else if (START.equals(e.getActionCommand())) {
            if (player1name.getText().isEmpty()
                    || player2name.getText().isEmpty()
                    || player3name.getText().isEmpty()) {
                JSkatMaster.showEmptyInputNameMessage();
                return;
            }

            final List<String> playerTypes = new ArrayList<>();
            playerTypes.add((String) player1.getSelectedItem());
            playerTypes.add((String) player2.getSelectedItem());
            playerTypes.add((String) player3.getSelectedItem());

            final List<String> playerNames = new ArrayList<>();
            playerNames.add(player1name.getText());
            playerNames.add(player2name.getText());
            playerNames.add(player3name.getText());

            setVisible(false);

            JSkatMaster.INSTANCE.startSeries(playerTypes, playerNames,
                    Integer.parseInt(numberOfRounds.getValue().toString()),
                    unlimited.isSelected(), onlyPlayRamsch.isSelected(), 100);
        }
    }

    private class PlayerTypeComboBoxRenderer extends AbstractI18NComboBoxRenderer {

        private final String AI_CLASS_NEW_ALGORITHMIC = "org.jskat.ai.newalgorithm.AlgorithmAI";
        private final String AI_CLASS_ALGORITHMIC = "org.jskat.ai.mjl.AIPlayerMJL";
        private final String AI_CLASS_RANDOM = "org.jskat.ai.rnd.AIPlayerRND";
        private final String AI_CLASS_NEURAL_NETWORK = "org.jskat.ai.nn.AIPlayerNN";
        private final String AI_CLASS_HUMAN = "org.jskat.gui.human.SwingHumanPlayer";

        PlayerTypeComboBoxRenderer() {
            super();
        }

        @Override
        public String getValueText(final Object value) {

            final String playerType = (String) value;

            return switch (playerType) {
                case AI_CLASS_NEW_ALGORITHMIC -> strings.getString("algorithmic_nextgen_player");
                case AI_CLASS_ALGORITHMIC -> strings.getString("algorithmic_player");
                case AI_CLASS_RANDOM -> strings.getString("random_player");
                case AI_CLASS_NEURAL_NETWORK -> strings.getString("neural_network_player");
                case AI_CLASS_HUMAN -> strings.getString("human_player");
                default -> playerType;
            };
        }
    }
}
