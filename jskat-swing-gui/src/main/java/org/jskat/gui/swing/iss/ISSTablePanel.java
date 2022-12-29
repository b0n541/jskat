package org.jskat.gui.swing.iss;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.event.iss.IssTableGameStartedEvent;
import org.jskat.control.event.iss.IssTableStateChangedEvent;
import org.jskat.control.event.skatgame.GameStartEvent;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.gui.swing.table.ContextPanelType;
import org.jskat.gui.swing.table.JSkatUserPanel;
import org.jskat.gui.swing.table.OpponentPanel;
import org.jskat.gui.swing.table.SkatTablePanel;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

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
     * {@inheritDoc}
     */
    @Subscribe
    public void clearTableOn(IssTableGameStartedEvent event) {

        if (event.gameStart.loginName().equals(event.gameStart.playerNames().get(Player.FOREHAND))) {
            clearTable(event.tableName, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND, event.gameStart);
        } else if (event.gameStart.loginName().equals(event.gameStart.playerNames().get(Player.MIDDLEHAND))) {
            clearTable(event.tableName, Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND, event.gameStart);
        } else if (event.gameStart.loginName().equals(event.gameStart.playerNames().get(Player.REARHAND))) {
            clearTable(event.tableName, Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND, event.gameStart);
        }
    }

    private void clearTable(String tableName, final Player leftOpponent, final Player rightOpponent,
                            final Player player, final GameStartInformation gameStart) {

        // FIXME: should have been sent via the event bus, event is missing in history
        resetTableOn(new GameStartEvent(gameStart.gameNo(), GameVariant.STANDARD, leftOpponent, rightOpponent, player));

        setPlayerName(leftOpponent, gameStart.playerNames().get(leftOpponent));
        setPlayerTime(leftOpponent, gameStart.playerTimes().get(leftOpponent));
        setPlayerName(rightOpponent, gameStart.playerNames().get(rightOpponent));
        setPlayerTime(rightOpponent, gameStart.playerTimes().get(rightOpponent));
        setPlayerName(player, gameStart.playerNames().get(player));
        setPlayerTime(player, gameStart.playerTimes().get(player));
    }

    /**
     * Updates the panel with the new status
     *
     * @param event Table status changed event
     */
    @Subscribe
    public void updateTableStatusOn(IssTableStateChangedEvent event) {

        TablePanelStatus tableStatus = event.status;

        // FIXME (jansch 05.04.2011) make 3<>4 change possible
        // setMaxPlayers(tableStatus.getMaxPlayers());

        for (final String playerName : tableStatus.getPlayerInformation().keySet()) {

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
