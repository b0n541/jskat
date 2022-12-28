package org.jskat.gui.swing.iss;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.gui.swing.table.ContextPanelType;
import org.jskat.gui.swing.table.JSkatUserPanel;
import org.jskat.gui.swing.table.OpponentPanel;
import org.jskat.gui.swing.table.SkatTablePanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

    ChatPanel chatPanel;

    // FIXME (jansch 05.04.2011) Dirty hack
    TablePanelStatus lastTableStatus;

    /**
     * Constructor
     *
     * @param tableName Table name
     * @param actions   Action map
     */
    public ISSTablePanel(final String tableName, final ActionMap actions) {
        super(tableName, actions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<JSkatAction> getGameOverActions() {
        return Arrays.asList(
                JSkatAction.INVITE_ISS_PLAYER,
                JSkatAction.READY_TO_PLAY,
                JSkatAction.TALK_ENABLED,
                JSkatAction.LEAVE_ISS_TABLE);
    }

    /**
     * @see SkatTablePanel#getPlayGroundPanel()
     */
    @Override
    protected JPanel getPlayGroundPanel() {
        final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill,insets 0", "[grow][shrink]", "fill"));
        panel.add(super.getPlayGroundPanel(), "width 80%, grow");

        // replace game start context panel
        addContextPanel(ContextPanelType.START, new StartContextPanel(getActionMap(), getGameOverActions()));
        setContextPanel(ContextPanelType.START);

        return panel;
    }

    @Override
    protected JTabbedPane getLeftPanel() {
        JTabbedPane leftPanel = super.getLeftPanel();

        this.chatPanel = getChatPanel();
        this.chatPanel.addNewChat(
                this.strings.getString("table") + " " + getName(), getName());
        leftPanel.add(this.strings.getString("chat"), this.chatPanel);

        return leftPanel;
    }

    @Override
    protected OpponentPanel getOpponentPanel() {
        return new OpponentPanel(getActionMap(), 12, true);
    }

    @Override
    protected JSkatUserPanel createPlayerPanel() {
        return new JSkatUserPanel(getActionMap(), 12, true);
    }

    private ChatPanel getChatPanel() {
        return new ChatPanel(this);
    }

    @Override
    protected JPanel getRightPanelForTrickPanel() {
        final JPanel additionalActionsPanel = new JPanel(LayoutFactory.getMigLayout());
        additionalActionsPanel.setOpaque(false);

        final JButton resignButton = new JButton(getActionMap().get(JSkatAction.RESIGN));
        additionalActionsPanel.add(resignButton, "growx, wrap");
        final JButton showCardsButton = new JButton(getActionMap().get(JSkatAction.SHOW_CARDS));
        additionalActionsPanel.add(showCardsButton, "growx");

        return additionalActionsPanel;
    }

    /**
     * Updates the panel with the new status
     *
     * @param tableStatus New table status
     */
    public void setTableStatus(final TablePanelStatus tableStatus) {

        // FIXME (jansch 05.04.2011) make 3<>4 change possible
        // setMaxPlayers(tableStatus.getMaxPlayers());

        for (final String playerName : tableStatus.getPlayerInformations().keySet()) {

            final PlayerStatus status = tableStatus.getPlayerInformation(playerName);

            if (!status.isPlayerLeft()) {
                addPlayerName(playerName);
            }
            setPlayerReadyToPlay(playerName, status.isReadyToPlay());
            setPlayerChatEnabled(playerName, status.isTalkEnabled());
            if (status.isPlayerLeft()) {
                removePlayerName(playerName);
            }
        }

        this.lastTableStatus = tableStatus;
    }

    private void addPlayerName(final String playerName) {

        if (!this.playerNamesAndPositions.containsKey(playerName)) {

            this.playerNamesAndPositions.put(playerName, null);

            if (this.userPanel.getPlayerName() == null) {

                this.userPanel.setPlayerName(playerName);

            } else if (this.leftOpponentPanel.getPlayerName() == null) {

                this.leftOpponentPanel.setPlayerName(playerName);

            } else if (this.rightOpponentPanel.getPlayerName() == null) {

                this.rightOpponentPanel.setPlayerName(playerName);
            }
        }
    }

    private void removePlayerName(final String playerName) {

        if (this.playerNamesAndPositions.containsKey(playerName)) {

            this.playerNamesAndPositions.remove(playerName);

            if (playerName.equals(this.userPanel.getPlayerName())) {

                this.userPanel.setPlayerName("");

            } else if (playerName.equals(this.leftOpponentPanel.getPlayerName())) {

                this.leftOpponentPanel.setPlayerName("");

            } else if (playerName.equals(this.rightOpponentPanel.getPlayerName())) {

                this.rightOpponentPanel.setPlayerName("");
            }
        }
    }

    /**
     * Adds a new chat message to the chat
     *
     * @param message Chat message
     */
    public void appendChatMessage(final ChatMessage message) {

        this.chatPanel.appendMessage(message);
    }
}
