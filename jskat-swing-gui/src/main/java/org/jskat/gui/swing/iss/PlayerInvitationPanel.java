package org.jskat.gui.swing.iss;

import org.jskat.gui.swing.LayoutFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dialog panel for player invitation on ISS
 */
public class PlayerInvitationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private ButtonGroup firstPlayerGroup;
    private ButtonGroup secondPlayerGroup;

    // private ButtonGroup thirdPlayerGroup;

    /**
     * Constructor
     *
     * @param player Available player on ISS
     */
    public PlayerInvitationPanel(Set<String> player) {

        initPanel(player);
    }

    void initPanel(Set<String> player) {

        this.setLayout(LayoutFactory.getMigLayout("fill"));

        this.firstPlayerGroup = new ButtonGroup();
        this.secondPlayerGroup = new ButtonGroup();
        // this.thirdPlayerGroup = new ButtonGroup();

        for (String currPlayer : player) {

            addPlayerInvitationLabel(currPlayer);
        }
    }

    void addPlayerInvitationLabel(String playerName) {

        this.add(new JLabel(playerName));

        JRadioButton firstButton = new JRadioButton();
        firstButton.setActionCommand(playerName);
        this.firstPlayerGroup.add(firstButton);
        this.add(firstButton);

        JRadioButton secondButton = new JRadioButton();
        secondButton.setActionCommand(playerName);
        this.secondPlayerGroup.add(secondButton);
        this.add(secondButton, "wrap");

        // JRadioButton thirdButton = new JRadioButton();
        // thirdButton.setActionCommand(playerName);
        // this.thirdPlayerGroup.add(thirdButton);
        // this.add(thirdButton, "wrap"); 
    }

    /**
     * Gets the selected player names
     *
     * @return Selected player names
     */
    public List<String> getPlayer() {

        List<String> result = new ArrayList<>();

        if (this.firstPlayerGroup.getSelection() != null) {
            result.add(this.firstPlayerGroup.getSelection().getActionCommand());
        }
        if (this.secondPlayerGroup.getSelection() != null) {
            result.add(this.secondPlayerGroup.getSelection().getActionCommand());
        }
        // if (this.thirdPlayerGroup.getSelection() != null) {
        // result.add(this.thirdPlayerGroup.getSelection().getActionCommand());
        // }

        return result;
    }
}
