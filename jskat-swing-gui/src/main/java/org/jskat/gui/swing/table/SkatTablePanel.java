package org.jskat.gui.swing.table;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.*;
import org.jskat.control.event.table.*;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.jskat.gui.swing.AbstractTabPanel;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Panel for a skat table
 */
public class SkatTablePanel extends AbstractTabPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SkatTablePanel.class);
    // FIXME (jan 14.11.2010) looks wrong to me, was made static to avoid
    // NullPointerException during ISS table creation
    protected static Map<Player, Boolean> playerPassed = new HashMap<>();
    protected Map<String, Player> playerNamesAndPositions;
    // declarer player on the table
    protected Player declarer;

    protected AbstractHandPanel foreHand;
    protected AbstractHandPanel middleHand;
    protected AbstractHandPanel rearHand;
    protected OpponentPanel leftOpponentPanel;
    protected OpponentPanel rightOpponentPanel;
    protected JSkatUserPanel userPanel;
    protected GameInformationPanel gameInfoPanel;
    protected JPanel gameContextPanel;
    protected Map<ContextPanelType, JPanel> contextPanels;
    protected TrickPanel trickPanel;
    protected TrickPanel lastTrickPanel;
    protected GameOverPanel gameOverPanel;
    /**
     * Table model for skat list
     */
    protected SkatListTableModel skatListTableModel;
    protected JTable scoreListTable;
    protected JScrollPane scoreListScrollPane;
    protected BiddingContextPanel biddingPanel;
    protected DeclaringContextPanel declaringPanel;
    protected SchieberamschContextPanel schieberamschPanel;

    protected boolean ramsch = false;

    protected boolean replay = false;

    /**
     * Panel for a skat table.
     *
     * @param tableName Table name
     * @param actions   Action
     */
    public SkatTablePanel(String tableName, ActionMap actions) {

        super(tableName, actions);

        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).register(this);

        log.debug("SkatTablePanel: name: " + tableName); //$NON-NLS-1$
    }

    private static GameInformationPanel getGameInfoPanel() {

        return new GameInformationPanel();
    }

    /**
     * Returns the actions for the game over context.
     *
     * @return List of actions for the game over context
     */
    protected List<JSkatAction> getGameOverActions() {
        return Arrays.asList(JSkatAction.CONTINUE_LOCAL_SERIES,
                JSkatAction.REPLAY_GAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initPanel() {

        setLayout(LayoutFactory.getMigLayout("fill,insets 0", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        playerNamesAndPositions = new HashMap<>();

        contextPanels = new HashMap<>();

        getActionMap().get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(true);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, getLeftPanel(),
                getPlayGroundPanel());
        add(splitPane, "grow"); //$NON-NLS-1$
    }

    protected JTabbedPane getLeftPanel() {

        JTabbedPane leftPanel = new JTabbedPane();

        leftPanel.addTab(strings.getString("score_sheet"),
                getScoreListPanel());

        return leftPanel;
    }

    private JPanel getScoreListPanel() {

        JPanel panel = new JPanel(LayoutFactory.getMigLayout(
                "fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        skatListTableModel = new SkatListTableModel();
        scoreListTable = new JTable(skatListTableModel);

        for (int i = 0; i < scoreListTable.getColumnModel()
                .getColumnCount(); i++) {

            if (i == 3) {

                // game colum is bigger
                scoreListTable.getColumnModel().getColumn(i)
                        .setPreferredWidth(40);
            } else {

                scoreListTable.getColumnModel().getColumn(i)
                        .setPreferredWidth(20);
            }
        }

        scoreListTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        scoreListScrollPane = new JScrollPane(scoreListTable);
        scoreListScrollPane.setMinimumSize(new Dimension(150, 100));
        scoreListScrollPane.setPreferredSize(new Dimension(300, 100));
        scoreListScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scoreListScrollPane, "growy"); //$NON-NLS-1$

        return panel;
    }

    /**
     * Builds the play ground panel
     *
     * @return Play ground panel
     */
    protected JPanel getPlayGroundPanel() {

        gameInfoPanel = getGameInfoPanel();
        leftOpponentPanel = getOpponentPanel();
        rightOpponentPanel = getOpponentPanel();
        userPanel = createPlayerPanel();
        createGameContextPanel();

        return new PlayGroundPanel(gameInfoPanel, leftOpponentPanel,
                rightOpponentPanel, gameContextPanel, userPanel);
    }

    protected OpponentPanel getOpponentPanel() {

        return new OpponentPanel(getActionMap(), 12, false);
    }

    protected JSkatUserPanel createPlayerPanel() {

        return new JSkatUserPanel(getActionMap(), 12, false);
    }

    protected void addContextPanel(ContextPanelType panelType,
                                   JPanel panel) {

        if (contextPanels.containsKey(panelType)) {
            // remove existing panel first
            gameContextPanel.remove(contextPanels.get(panelType));
            contextPanels.remove(panelType);
        }

        contextPanels.put(panelType, panel);
        gameContextPanel.add(panel, panelType.toString());
    }

    private void createGameContextPanel() {

        gameContextPanel = new JPanel();
        gameContextPanel.setOpaque(false);
        gameContextPanel.setLayout(new CardLayout());

        addContextPanel(ContextPanelType.START,
                new StartContextPanel((StartSkatSeriesAction) getActionMap()
                        .get(JSkatAction.START_LOCAL_SERIES)));

        biddingPanel = new BiddingContextPanel(getActionMap(),
                bitmaps, userPanel);
        addContextPanel(ContextPanelType.BIDDING, biddingPanel);

        declaringPanel = new DeclaringContextPanel(getActionMap(),
                userPanel);
        addContextPanel(ContextPanelType.DECLARING, declaringPanel);

        schieberamschPanel = new SchieberamschContextPanel(getActionMap(),
                userPanel, 4);
        addContextPanel(ContextPanelType.SCHIEBERAMSCH, schieberamschPanel);

        addContextPanel(ContextPanelType.RE_AFTER_CONTRA,
                createCallReAfterContraPanel(getActionMap()));

        JPanel trickHoldingPanel = new JPanel(LayoutFactory.getMigLayout(
                "fill", "[shrink][grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
                "fill")); //$NON-NLS-1$
        lastTrickPanel = new TrickPanel(0.6, false);
        trickHoldingPanel.add(lastTrickPanel, "width 25%"); //$NON-NLS-1$
        trickPanel = new TrickPanel(0.8, true);
        trickHoldingPanel.add(trickPanel, "grow"); //$NON-NLS-1$

        trickHoldingPanel.add(getRightPanelForTrickPanel(), "width 25%"); //$NON-NLS-1$
        trickHoldingPanel.setOpaque(false);
        addContextPanel(ContextPanelType.TRICK_PLAYING, trickHoldingPanel);

        gameOverPanel = new GameOverPanel(getActionMap(),
                getGameOverActions());
        addContextPanel(ContextPanelType.GAME_OVER, gameOverPanel);
    }

    // FIXME: same code can be found in class SchieberamschContextPanel
    private JPanel createCallReAfterContraPanel(ActionMap actions) {
        JPanel result = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

        JPanel question = new JPanel();
        JLabel questionIconLabel = new JLabel(new ImageIcon(
                JSkatGraphicRepository.INSTANCE.getUserBidBubble()));
        question.add(questionIconLabel);
        JLabel questionLabel = new JLabel(
                strings.getString("want_call_re_after_contra")); //$NON-NLS-1$
        questionLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        question.add(questionLabel);
        result.add(question, "center, growx, span 2, wrap"); //$NON-NLS-1$

        JButton callReButton = new JButton(
                actions.get(JSkatAction.CALL_RE));
        callReButton.setIcon(new ImageIcon(bitmaps.getIconImage(Icon.OK,
                IconSize.BIG)));
        callReButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(Boolean.TRUE);
                callReButton.dispatchEvent(e);
            }
        });

        JButton noReAfterContraButton = new JButton(
                actions.get(JSkatAction.CALL_RE));
        noReAfterContraButton.setText(strings.getString("no"));
        noReAfterContraButton.setIcon(new ImageIcon(bitmaps.getIconImage(
                Icon.STOP, IconSize.BIG)));
        noReAfterContraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(Boolean.FALSE);
                noReAfterContraButton.dispatchEvent(e);
            }
        });

        JPanel grandHandPanel = new JPanel();
        grandHandPanel.add(callReButton);
        grandHandPanel.setOpaque(false);
        result.add(grandHandPanel, "width 50%"); //$NON-NLS-1$

        JPanel schieberamschPanel = new JPanel();
        schieberamschPanel.add(noReAfterContraButton);
        schieberamschPanel.setOpaque(false);
        result.add(schieberamschPanel, "width 50%"); //$NON-NLS-1$

        result.setOpaque(false);

        return result;
    }

    private AbstractHandPanel getPlayerPanel(Player player) {

        AbstractHandPanel result = getHandPanel(player);

        return result;
    }

    protected JPanel getRightPanelForTrickPanel() {
        JPanel additionalActionsPanel = new JPanel(
                LayoutFactory.getMigLayout());
        additionalActionsPanel.setOpaque(false);

        JButton resignButton = new JButton(getActionMap().get(
                JSkatAction.CALL_CONTRA));
        additionalActionsPanel.add(resignButton, "growx, wrap"); //$NON-NLS-1$

        return additionalActionsPanel;
    }

    @Subscribe
    public void setReplayModeOn(SkatGameReplayStartedEvent event) {
        replay = true;
    }

    @Subscribe
    public void setReplayModeOn(SkatGameReplayFinishedEvent event) {
        replay = false;
    }

    /**
     * Sets player positions
     *
     * @param event Game start event
     */
    @Subscribe
    public void resetTableOn(GameStartEvent event) {

        gameInfoPanel.setGameState(GameState.GAME_START);
        gameInfoPanel.setGameNumber(event.gameNo);

        leftOpponentPanel.setPosition(event.leftPlayerPosition);
        rightOpponentPanel.setPosition(event.rightPlayerPosition);
        userPanel.setPosition(event.userPosition);

        biddingPanel.setUserPosition(event.userPosition);
        trickPanel.setUserPosition(event.userPosition);
        lastTrickPanel.setUserPosition(event.userPosition);
        gameOverPanel.setUserPosition(event.userPosition);

        // FIXME (jansch 09.11.2010) code duplication with
        // BiddingPanel.setPlayerPositions()
        switch (event.userPosition) {
            case FOREHAND:
                foreHand = userPanel;
                middleHand = leftOpponentPanel;
                rearHand = rightOpponentPanel;
                break;
            case MIDDLEHAND:
                foreHand = rightOpponentPanel;
                middleHand = userPanel;
                rearHand = leftOpponentPanel;
                break;
            case REARHAND:
                foreHand = leftOpponentPanel;
                middleHand = rightOpponentPanel;
                rearHand = userPanel;
                break;
        }

        clearTable();
    }

    /**
     * Adds cards to a player
     *
     * @param event Card deal event
     */
    @Subscribe
    public void setDealtCardsOn(CardDealEvent event) {
        setCardsForPlayers(event.playerCards);
    }

    /**
     * Sets a card played in a trick
     *
     * @param player Player position
     * @param card   Card
     */
    public void setTrickCard(Player player, Card card) {

        trickPanel.addCard(player, card);
    }

    /**
     * Clears trick cards.
     *
     * @param event Trick completed event
     */
    @Subscribe
    public void clearTrickCardsAndSetLastTrickOn(TrickCompletedEvent event) {

        clearTrickCards();
        clearLastTrickCards();
        Player trickForeHand = event.trick.getForeHand();
        lastTrickPanel.addCard(trickForeHand, event.trick.getFirstCard());
        lastTrickPanel.addCard(trickForeHand.getLeftNeighbor(),
                event.trick.getSecondCard());
        lastTrickPanel.addCard(trickForeHand.getRightNeighbor(),
                event.trick.getThirdCard());

        // set trick number of next trick
        setTrickNumber(event.trick.getTrickNumberInGame() + 2);
    }

    /**
     * Clears trick cards
     */
    private void clearTrickCards() {

        trickPanel.clearCards();
    }

    /**
     * Clears last trick cards
     */
    private void clearLastTrickCards() {

        lastTrickPanel.clearCards();
    }

    @Subscribe
    public void setTrickCardOn(TrickCardPlayedEvent event) {
        removeCard(event.player, event.card);
        setTrickCard(event.player, event.card);
    }

    private void removeCard(Player player, Card card) {

        switch (player) {
            case FOREHAND:
                foreHand.removeCard(card);
                break;
            case MIDDLEHAND:
                middleHand.removeCard(card);
                break;
            case REARHAND:
                rearHand.removeCard(card);
                break;
        }
    }

    /**
     * Removes all cards from a player
     *
     * @param player Player
     */
    public void removeAllCards(Player player) {
        switch (player) {
            case FOREHAND:
                foreHand.removeAllCards();
                break;
            case MIDDLEHAND:
                middleHand.removeAllCards();
                break;
            case REARHAND:
                rearHand.removeAllCards();
                break;
        }
    }

    /**
     * Clears the hand of a player
     *
     * @param player Player
     */
    public void clearHand(Player player) {

        getPlayerPanel(player).clearHandPanel();
    }

    /**
     * Sets the game announcement
     *
     * @param event Game announcement event
     */
    @Subscribe
    public void setGameAnnouncementOn(GameAnnouncementEvent event) {

        if (event.announcement.getGameType() == GameType.RAMSCH) {
            ramsch = true;
        }

        gameInfoPanel.setGameAnnouncement(event.announcement);

        leftOpponentPanel
                .setSortGameType(event.announcement.getGameType());
        rightOpponentPanel.setSortGameType(event.announcement
                .getGameType());
        userPanel.setSortGameType(event.announcement.getGameType());

        if (event.announcement.getGameType() != GameType.PASSED_IN
                && event.announcement.getGameType() != GameType.RAMSCH) {
            getPlayerPanel(event.player).setDeclarer(true);
        }

        if (event.announcement.isOuvert()) {
            getPlayerPanel(event.player).showCards();
        }
    }

    /**
     * Sets the game state
     *
     * @param state Game state
     */
    public void setGameState(GameState state) {

        log.debug(".setGameState(" + state + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        gameInfoPanel.setGameState(state);

        switch (state) {
            case GAME_START:
                setContextPanel(ContextPanelType.START);
                resetGameData();
                break;
            case DEALING:
                setContextPanel(ContextPanelType.START);
                break;
            case BIDDING:
                setContextPanel(ContextPanelType.BIDDING);
                break;
            case RAMSCH_GRAND_HAND_ANNOUNCING:
                setContextPanel(ContextPanelType.SCHIEBERAMSCH);
                userPanel.setGameState(state);
                break;
            case SCHIEBERAMSCH:
                setContextPanel(ContextPanelType.SCHIEBERAMSCH);
                userPanel.setGameState(state);
                break;
            case PICKING_UP_SKAT:
                if (userPanel.getPosition().equals(declarer)) {
                    setContextPanel(ContextPanelType.DECLARING);
                    userPanel.setGameState(state);
                }
                break;
            case DISCARDING:
                if (userPanel.getPosition().equals(declarer)) {
                    userPanel.setGameState(state);
                }
                break;
            case DECLARING:
                if (userPanel.getPosition().equals(declarer)) {
                    setContextPanel(ContextPanelType.DECLARING);
                }
                break;
            case RE:
                setContextPanel(ContextPanelType.RE_AFTER_CONTRA);
                break;
            case TRICK_PLAYING:
                setContextPanel(ContextPanelType.TRICK_PLAYING);
                userPanel.setGameState(state);
                break;
            case CALCULATING_GAME_VALUE:
            case PRELIMINARY_GAME_END:
            case GAME_OVER:
                setContextPanel(ContextPanelType.GAME_OVER);
                foreHand.setActivePlayer(false);
                middleHand.setActivePlayer(false);
                rearHand.setActivePlayer(false);
                break;
        }

        validate();
    }

    private void resetGameData() {

        playerPassed.put(Player.FOREHAND, Boolean.FALSE);
        playerPassed.put(Player.MIDDLEHAND, Boolean.FALSE);
        playerPassed.put(Player.REARHAND, Boolean.FALSE);
        ramsch = false;
        declarer = null;
    }

    /**
     * Sets the context panel
     *
     * @param panelType Panel type
     */
    void setContextPanel(ContextPanelType panelType) {

        ((CardLayout) gameContextPanel.getLayout()).show(
                gameContextPanel, panelType.toString());
        gameContextPanel.validate();
    }

    /**
     * Adds a new game result
     *
     * @param event Game finish event
     */
    @Subscribe
    public void addGameResultOn(GameFinishEvent event) {

        gameOverPanel.setGameSummary(event.gameSummary);

        if (!replay) {
            skatListTableModel.addResult(
                    leftOpponentPanel.getPosition(),
                    rightOpponentPanel.getPosition(),
                    userPanel.getPosition(),
                    event.gameSummary.getDeclarer(), event.gameSummary);
            scrollSkatListToTheEnd();
        }

        gameInfoPanel.setGameSummary(event.gameSummary);
    }

    private void scrollSkatListToTheEnd() {
        // scroll skat list if the new result is out of scope
        Rectangle bounds = scoreListTable.getCellRect(
                skatListTableModel.getRowCount() - 1, 0, true);
        Point loc = bounds.getLocation();
        loc.move(loc.x, loc.y + bounds.height);
        scoreListScrollPane.getViewport().setViewPosition(loc);
    }

    Player getHumanPosition() {

        return userPanel.getPosition();
    }

    /**
     * Clears the skat table
     */
    public void clearTable() {

        gameInfoPanel.clear();
        biddingPanel.resetPanel();
        declaringPanel.resetPanel();
        gameOverPanel.resetPanel();
        schieberamschPanel.resetPanel();
        clearHand(Player.FOREHAND);
        clearHand(Player.MIDDLEHAND);
        clearHand(Player.REARHAND);
        clearTrickCards();
        clearLastTrickCards();
        // default sorting is grand sorting
        leftOpponentPanel.setSortGameType(GameType.GRAND);
        rightOpponentPanel.setSortGameType(GameType.GRAND);
        userPanel.setSortGameType(GameType.GRAND);

        resetGameData();
    }

    /**
     * Sets the bid value for a player.
     *
     * @param event Bid event
     */
    @Subscribe
    public void setBidValueOn(BidEvent event) {

        log.debug(event.player + " bids: " + event.bid);

        setBidValue(event);
    }

    private void setBidValue(AbstractBidEvent event) {
        biddingPanel.setBid(event.player, event.bid);
        getPlayerPanel(event.player).setBidValue(event.bid);
    }

    /**
     * Sets the bid value for a player.
     *
     * @param event Hold bid event
     */
    @Subscribe
    public void setBidValueOn(HoldBidEvent event) {

        log.debug(event.player + " holds: " + event.bid);

        setBidValue(event);
    }

    /**
     * Starts a game
     */
    public void startGame() {

        clearTable();
    }

    /**
     * Sets the skat
     *
     * @param skat Skat
     */
    public void setSkat(CardList skat) {

        if (ramsch) {
            schieberamschPanel.setSkat(skat);
        } else {
            declaringPanel.setSkat(skat);
        }
    }

    /**
     * Takes a card from skat to user panel
     *
     * @param card Card
     */
    public void takeCardFromSkat(Card card) {
        takeCardFromSkat(userPanel, card);
    }

    /**
     * Takes a card from skat
     *
     * @param player Player
     * @param card   Card
     */
    public void takeCardFromSkat(Player player, Card card) {
        takeCardFromSkat(getPlayerPanel(player), card);
    }

    private void takeCardFromSkat(AbstractHandPanel panel, Card card) {

        if (!panel.isHandFull()) {

            declaringPanel.removeCard(card);
            schieberamschPanel.removeCard(card);
            panel.addCard(card);

        } else {

            log.debug("Player panel full!!!"); //$NON-NLS-1$
        }
    }

    /**
     * Puts a card from the user panel to the skat
     *
     * @param card Card
     */
    public void putCardIntoSkat(Card card) {
        putCardIntoSkat(userPanel, card);
    }

    /**
     * Puts a card into the skat
     *
     * @param player Player
     * @param card   Card
     */
    public void putCardIntoSkat(Player player, Card card) {
        putCardIntoSkat(getPlayerPanel(player), card);
    }

    private void putCardIntoSkat(AbstractHandPanel panel, Card card) {

        if (!declaringPanel.isHandFull()) {

            panel.removeCard(card);
            declaringPanel.addCard(card);
            schieberamschPanel.addCard(card);

        } else {

            log.debug("Discard panel full!!!"); //$NON-NLS-1$
        }
    }

    /**
     * Clears the skat list.
     *
     * @param event Skat series started event
     */
    @Subscribe
    public void clearSkatListOn(SkatSeriesStartedEvent event) {

        skatListTableModel.clearList();
    }

    /**
     * Sets player name
     *
     * @param player Player position
     * @param name   Player name
     */
    public void setPlayerName(Player player, String name) {

        playerNamesAndPositions.put(name, player);
        AbstractHandPanel panel = getHandPanel(player);

        if (panel != null) {
            if (name != null) {
                panel.setPlayerName(name);
            }
        }
    }

    /**
     * Sets player time
     *
     * @param player Player position
     * @param time   Player time
     */
    public void setPlayerTime(Player player, double time) {

        AbstractHandPanel panel = getHandPanel(player);

        if (panel != null) {
            panel.setPlayerTime(time);
        }
    }

    /**
     * Sets player flag for chat enabled yes/no
     *
     * @param playerName    Player name
     * @param isChatEnabled Flag for chat enabled yes/no
     */
    public void setPlayerChatEnabled(String playerName,
                                     boolean isChatEnabled) {

        AbstractHandPanel panel = getHandPanel(playerName);

        if (panel != null) {
            panel.setChatEnabled(isChatEnabled);
        }
    }

    /**
     * Sets player flag for ready to play yes/no
     *
     * @param playerName    Player name
     * @param isReadyToPlay Flag for ready to play yes/no
     */
    public void setPlayerReadyToPlay(String playerName,
                                     boolean isReadyToPlay) {

        AbstractHandPanel panel = getHandPanel(playerName);

        if (panel != null) {
            panel.setReadyToPlay(isReadyToPlay);
        }
    }

    /**
     * Sets player flag for resign
     *
     * @param player Player
     */
    public void setResign(Player player) {

        AbstractHandPanel panel = getHandPanel(player);

        if (panel != null) {
            panel.setResign(true);
        }
    }

    private AbstractHandPanel getHandPanel(String playerName) {

        AbstractHandPanel panel = null;

        if (playerName.equals(userPanel.getPlayerName())) {
            panel = userPanel;
        } else if (playerName.equals(leftOpponentPanel.getPlayerName())) {
            panel = leftOpponentPanel;
        } else if (playerName.equals(rightOpponentPanel.getPlayerName())) {
            panel = rightOpponentPanel;
        }

        return panel;
    }

    private AbstractHandPanel getHandPanel(Player player) {
        AbstractHandPanel panel = null;

        switch (player) {
            case FOREHAND:
                panel = foreHand;
                break;
            case MIDDLEHAND:
                panel = middleHand;
                break;
            case REARHAND:
                panel = rearHand;
                break;
        }
        return panel;
    }

    /**
     * Sets the active player
     *
     * @param event Active player changed event
     */
    @Subscribe
    public void setActivePlayerOn(ActivePlayerChangedEvent event) {
        switch (event.player) {
            case FOREHAND:
                foreHand.setActivePlayer(true);
                middleHand.setActivePlayer(false);
                rearHand.setActivePlayer(false);
                break;
            case MIDDLEHAND:
                foreHand.setActivePlayer(false);
                middleHand.setActivePlayer(true);
                rearHand.setActivePlayer(false);
                break;
            case REARHAND:
                foreHand.setActivePlayer(false);
                middleHand.setActivePlayer(false);
                rearHand.setActivePlayer(true);
                break;
        }
    }

    /**
     * Sets passing of a player
     *
     * @param event Pass bid event
     */
    @Subscribe
    public void setPassOn(PassBidEvent event) {

        log.debug(event.player + " passes"); //$NON-NLS-1$

        playerPassed.put(event.player, Boolean.TRUE);

        getPlayerPanel(event.player).setPass(true);
        biddingPanel.setPass(event.player);
    }

    /**
     * Sets the series state
     *
     * @param state Series state
     */
    public void setSeriesState(SeriesState state) {

        if (SeriesState.SERIES_FINISHED.equals(state)) {

            setContextPanel(ContextPanelType.START);
        }
    }

    /**
     * Sets the bid value to make
     *
     * @param bidValue Bid value
     */
    public void setBidValueToMake(int bidValue) {

        biddingPanel.setBidValueToMake(bidValue);
    }

    /**
     * Sets the bid value to hold
     *
     * @param bidValue Bid value
     */
    public void setBidValueToHold(int bidValue) {

        biddingPanel.setBidValueToHold(bidValue);
    }

    @Override
    protected void setFocus() {
        // FIXME (jan 20.11.2010) set active/inactive actions

    }

    /**
     * Sets the trick number
     *
     * @param trickNumber Trick number
     */
    public void setTrickNumber(int trickNumber) {

        gameInfoPanel.setTrickNumber(trickNumber);
    }

    /**
     * Sets the player names
     *
     * @param upperLeftPlayerName        Upper left player name
     * @param isUpperLeftPlayerAIPlayer  TRUE if the upper left player is an AI player
     * @param upperRightPlayerName       Upper right player name
     * @param isUpperRightPlayerAIPlayer TRUE if the upper right player is an AI player
     * @param lowerPlayerName            Lower player name
     * @param isLowerPlayerAIPlayer      TRUE if the lower player is an AI player
     */
    public void setPlayerNames(String upperLeftPlayerName, boolean isUpperLeftPlayerAIPlayer,
                               String upperRightPlayerName, boolean isUpperRightPlayerAIPlayer, String lowerPlayerName,
                               boolean isLowerPlayerAIPlayer) {
        // FIXME (jan 26.01.2011) possible code duplication with
        // setPlayerInformation()
        leftOpponentPanel.setPlayerName(upperLeftPlayerName);
        leftOpponentPanel.setAIPlayer(isUpperLeftPlayerAIPlayer);
        rightOpponentPanel.setPlayerName(upperRightPlayerName);
        rightOpponentPanel.setAIPlayer(isUpperRightPlayerAIPlayer);
        userPanel.setPlayerName(lowerPlayerName);
        userPanel.setAIPlayer(isLowerPlayerAIPlayer);
        skatListTableModel.setPlayerNames(upperLeftPlayerName,
                upperRightPlayerName, lowerPlayerName);
    }

    /**
     * Gets the declarer player for the table
     *
     * @return Declarer player
     */
    public Player getDeclarer() {
        return declarer;
    }

    /**
     * Sets the declarer player for the table
     *
     * @param declarer Declarer player
     */
    public void setDeclarer(Player declarer) {
        this.declarer = declarer;
    }

    /**
     * Shows the cards of a player
     *
     * @param player Player
     */
    public void showCards(Player player) {

        getPlayerPanel(player).showCards();
    }

    /**
     * Hides the cards of a player
     *
     * @param player Player
     */
    public void hideCards(Player player) {

        getPlayerPanel(player).hideCards();
    }

    /**
     * Sets the schieben of a player
     *
     * @param player Player position
     */
    public void setGeschoben(Player player) {
        getPlayerPanel(player).setGeschoben();
    }

    /**
     * Sets the discarded skat
     *
     * @param player        Player
     * @param skatBefore    Skat before discarding
     * @param discardedSkat Skat after discarding
     */
    public void setDiscardedSkat(Player player,
                                 CardList skatBefore, CardList discardedSkat) {
        getPlayerPanel(player);

        for (int i = 0; i < 2; i++) {
            Card skatCard = skatBefore.get(i);
            takeCardFromSkat(player, skatCard);
        }
        for (int i = 0; i < 2; i++) {
            Card skatCard = discardedSkat.get(i);
            putCardIntoSkat(player, skatCard);
        }
    }

    /**
     * Shows cards of all players
     *
     * @param command Show cards command
     */
    @Subscribe
    public void showCardsOn(ShowCardsCommand command) {
        setCardsForPlayers(command.cards);
        for (Player player : Player.values()) {
            showCards(player);
        }
        gameOverPanel.setDealtSkat(command.skat);
    }

    private void setCardsForPlayers(Map<Player, CardList> cards) {
        for (Entry<Player, CardList> playerCards : cards.entrySet()) {
            removeAllCards(playerCards.getKey());
            getPlayerPanel(playerCards.getKey()).addCards(
                    playerCards.getValue());
            if (replay) {
                showCards(playerCards.getKey());
            }
        }
    }

    @Subscribe
    public void setContraOn(ContraEvent event) {
        getPlayerPanel(event.player).setContra();
        gameInfoPanel.setContra();
    }

    @Subscribe
    public void setReOn(ReEvent event) {
        getPlayerPanel(event.player).setRe();
        gameInfoPanel.setRe();
    }
}
