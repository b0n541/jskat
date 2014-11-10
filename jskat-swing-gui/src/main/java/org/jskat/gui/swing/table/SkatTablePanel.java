/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import org.jskat.control.event.skatgame.AbstractBidEvent;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.control.event.skatgame.HoldBidEvent;
import org.jskat.control.event.table.ActivePlayerChangedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.Trick;
import org.jskat.gui.action.JSkatAction;
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

import com.google.common.eventbus.Subscribe;

/**
 * Panel for a skat table
 */
public class SkatTablePanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(SkatTablePanel.class);

	protected Map<String, Player> playerNamesAndPositions;

	// FIXME (jan 14.11.2010) looks wrong to me, was made static to avoid
	// NullPointerException during ISS table creation
	protected static Map<Player, Boolean> playerPassed = new HashMap<Player, Boolean>();
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

	/**
	 * Panel for a skat table.
	 * 
	 * @param view
	 *            View
	 * @param newTableName
	 *            Table name
	 * @param actions
	 *            Action
	 */
	public SkatTablePanel(final String newTableName, final ActionMap actions) {

		super(newTableName, actions);

		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * Returns the actions for the game over context.
	 * 
	 * @return List of actions for the game over context
	 */
	protected List<JSkatAction> getGameOverActions() {
		return Arrays.asList(JSkatAction.CONTINUE_LOCAL_SERIES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill,insets 0", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.playerNamesAndPositions = new HashMap<String, Player>();

		this.contextPanels = new HashMap<ContextPanelType, JPanel>();

		getActionMap().get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(true);

		final JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, getLeftPanel(),
				getPlayGroundPanel());
		add(splitPane, "grow"); //$NON-NLS-1$
	}

	protected JTabbedPane getLeftPanel() {

		JTabbedPane leftPanel = new JTabbedPane();

		leftPanel.addTab(this.strings.getString("score_sheet"), getScoreListPanel());

		return leftPanel;
	}

	private JPanel getScoreListPanel() {

		final JPanel panel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.skatListTableModel = new SkatListTableModel();
		this.scoreListTable = new JTable(this.skatListTableModel);

		for (int i = 0; i < this.scoreListTable.getColumnModel().getColumnCount(); i++) {

			if (i == 3) {

				// game colum is bigger
				this.scoreListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(40);
			} else {

				this.scoreListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(20);
			}
		}

		this.scoreListTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		this.scoreListScrollPane = new JScrollPane(this.scoreListTable);
		this.scoreListScrollPane.setMinimumSize(new Dimension(150, 100));
		this.scoreListScrollPane.setPreferredSize(new Dimension(300, 100));
		this.scoreListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.scoreListScrollPane, "growy"); //$NON-NLS-1$

		return panel;
	}

	/**
	 * Builds the play ground panel
	 * 
	 * @return Play ground panel
	 */
	protected JPanel getPlayGroundPanel() {

		this.gameInfoPanel = getGameInfoPanel();
		this.leftOpponentPanel = getOpponentPanel();
		this.rightOpponentPanel = getOpponentPanel();
		this.userPanel = createPlayerPanel();
		createGameContextPanel();

		return new PlayGroundPanel(this.gameInfoPanel, this.leftOpponentPanel,
				this.rightOpponentPanel, this.gameContextPanel, this.userPanel);
	}

	private GameInformationPanel getGameInfoPanel() {

		return new GameInformationPanel();
	}

	protected OpponentPanel getOpponentPanel() {

		return new OpponentPanel(getActionMap(), 12, false);
	}

	protected JSkatUserPanel createPlayerPanel() {

		return new JSkatUserPanel(getActionMap(), 12, false);
	}

	protected void addContextPanel(final ContextPanelType panelType,
			final JPanel panel) {

		if (this.contextPanels.containsKey(panelType)) {
			// remove existing panel first
			this.gameContextPanel.remove(this.contextPanels.get(panelType));
			this.contextPanels.remove(panelType);
		}

		this.contextPanels.put(panelType, panel);
		this.gameContextPanel.add(panel, panelType.toString());
	}

	private void createGameContextPanel() {

		this.gameContextPanel = new JPanel();
		this.gameContextPanel.setOpaque(false);
		this.gameContextPanel.setLayout(new CardLayout());

		addContextPanel(ContextPanelType.START,
				new StartContextPanel((StartSkatSeriesAction) getActionMap()
						.get(JSkatAction.START_LOCAL_SERIES)));

		this.biddingPanel = new BiddingContextPanel(getActionMap(),
				this.bitmaps, this.userPanel);
		addContextPanel(ContextPanelType.BIDDING, this.biddingPanel);

		this.declaringPanel = new DeclaringContextPanel(getActionMap(),
				this.userPanel);
		addContextPanel(ContextPanelType.DECLARING, this.declaringPanel);

		this.schieberamschPanel = new SchieberamschContextPanel(getActionMap(),
				this.userPanel, 4);
		addContextPanel(ContextPanelType.SCHIEBERAMSCH, this.schieberamschPanel);

		addContextPanel(ContextPanelType.RE_AFTER_CONTRA,
				createCallReAfterContraPanel(getActionMap()));

		final JPanel trickHoldingPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		this.lastTrickPanel = new TrickPanel(0.6, false);
		trickHoldingPanel.add(this.lastTrickPanel, "width 25%"); //$NON-NLS-1$
		this.trickPanel = new TrickPanel(0.8, true);
		trickHoldingPanel.add(this.trickPanel, "grow"); //$NON-NLS-1$

		trickHoldingPanel.add(getRightPanelForTrickPanel(), "width 25%"); //$NON-NLS-1$
		trickHoldingPanel.setOpaque(false);
		addContextPanel(ContextPanelType.TRICK_PLAYING, trickHoldingPanel);

		this.gameOverPanel = new GameOverPanel(getActionMap(), getGameOverActions());
		addContextPanel(ContextPanelType.GAME_OVER, this.gameOverPanel);
	}

	// FIXME: same code can be found in class SchieberamschContextPanel
	private JPanel createCallReAfterContraPanel(final ActionMap actions) {
		JPanel result = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		JPanel question = new JPanel();
		JLabel questionIconLabel = new JLabel(new ImageIcon(
				JSkatGraphicRepository.INSTANCE.getUserBidBubble()));
		question.add(questionIconLabel);
		JLabel questionLabel = new JLabel(
				this.strings.getString("want_call_re_after_contra")); //$NON-NLS-1$
		questionLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		question.add(questionLabel);
		result.add(question, "center, growx, span 2, wrap"); //$NON-NLS-1$

		final JButton callReButton = new JButton(
				actions.get(JSkatAction.CALL_RE));
		callReButton.setIcon(new ImageIcon(this.bitmaps.getIconImage(Icon.OK,
				IconSize.BIG)));
		callReButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				e.setSource(Boolean.TRUE);
				callReButton.dispatchEvent(e);
			}
		});

		final JButton noReAfterContraButton = new JButton(
				actions.get(JSkatAction.CALL_RE));
		noReAfterContraButton.setText(this.strings.getString("no"));
		noReAfterContraButton.setIcon(new ImageIcon(this.bitmaps.getIconImage(
				Icon.STOP, IconSize.BIG)));
		noReAfterContraButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
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

	private AbstractHandPanel getPlayerPanel(final Player player) {

		final AbstractHandPanel result = getHandPanel(player);

		return result;
	}

	protected JPanel getRightPanelForTrickPanel() {
		final JPanel additionalActionsPanel = new JPanel(
				LayoutFactory.getMigLayout());
		additionalActionsPanel.setOpaque(false);

		final JButton resignButton = new JButton(getActionMap().get(
				JSkatAction.CALL_CONTRA));
		additionalActionsPanel.add(resignButton, "growx, wrap"); //$NON-NLS-1$

		return additionalActionsPanel;
	}

	/**
	 * Sets player positions
	 * 
	 * @param leftPosition
	 *            Upper left position
	 * @param rightPosition
	 *            Upper right position
	 * @param playerPosition
	 *            Player position
	 */
	public void setPositions(final Player leftPosition,
			final Player rightPosition, final Player playerPosition) {

		this.leftOpponentPanel.setPosition(leftPosition);
		this.rightOpponentPanel.setPosition(rightPosition);
		this.userPanel.setPosition(playerPosition);

		this.biddingPanel.setUserPosition(playerPosition);
		this.trickPanel.setUserPosition(playerPosition);
		this.lastTrickPanel.setUserPosition(playerPosition);
		this.gameOverPanel.setUserPosition(playerPosition);

		// FIXME (jansch 09.11.2010) code duplication with
		// BiddingPanel.setPlayerPositions()
		switch (playerPosition) {
		case FOREHAND:
			this.foreHand = this.userPanel;
			this.middleHand = this.leftOpponentPanel;
			this.rearHand = this.rightOpponentPanel;
			break;
		case MIDDLEHAND:
			this.foreHand = this.rightOpponentPanel;
			this.middleHand = this.userPanel;
			this.rearHand = this.leftOpponentPanel;
			break;
		case REARHAND:
			this.foreHand = this.leftOpponentPanel;
			this.middleHand = this.rightOpponentPanel;
			this.rearHand = this.userPanel;
			break;
		}
	}

	/**
	 * Adds a card to a player
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void addCard(final Player player, final Card card) {

		getPlayerPanel(player).addCard(card);
	}

	/**
	 * Adds cards to a player
	 * 
	 * @param player
	 *            Player
	 * @param cards
	 *            Cards
	 */
	public void addCards(final Player player, final CardList cards) {

		getPlayerPanel(player).addCards(cards);
	}

	/**
	 * Sets a card played in a trick
	 * 
	 * @param player
	 *            Player position
	 * @param card
	 *            Card
	 */
	public void setTrickCard(final Player player, final Card card) {

		this.trickPanel.addCard(player, card);
	}

	/**
	 * Clears trick cards
	 */
	public void clearTrickCards() {

		this.trickPanel.clearCards();
	}

	/**
	 * Clears last trick cards
	 */
	void clearLastTrickCards() {

		this.lastTrickPanel.clearCards();
	}

	/**
	 * Removes a card from a player
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void removeCard(final Player player, final Card card) {

		switch (player) {
		case FOREHAND:
			this.foreHand.removeCard(card);
			break;
		case MIDDLEHAND:
			this.middleHand.removeCard(card);
			break;
		case REARHAND:
			this.rearHand.removeCard(card);
			break;
		}
	}

	/**
	 * Removes all cards from a player
	 * 
	 * @param player
	 *            Player
	 */
	public void removeAllCards(final Player player) {
		switch (player) {
		case FOREHAND:
			this.foreHand.removeAllCards();
			break;
		case MIDDLEHAND:
			this.middleHand.removeAllCards();
			break;
		case REARHAND:
			this.rearHand.removeAllCards();
			break;
		}
	}

	/**
	 * Clears the hand of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void clearHand(final Player player) {

		getPlayerPanel(player).clearHandPanel();
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param player
	 *            Player
	 * @param gameAnnouncement
	 *            Game announcement
	 */
	public void setGameAnnouncement(final Player player,
			final GameAnnouncement gameAnnouncement) {

		if (gameAnnouncement.getGameType() == GameType.RAMSCH) {
			this.ramsch = true;
		}

		this.gameInfoPanel.setGameAnnouncement(gameAnnouncement);

		this.leftOpponentPanel.setSortGameType(gameAnnouncement.getGameType());
		this.rightOpponentPanel.setSortGameType(gameAnnouncement.getGameType());
		this.userPanel.setSortGameType(gameAnnouncement.getGameType());

		if (gameAnnouncement.getGameType() != GameType.PASSED_IN
				&& gameAnnouncement.getGameType() != GameType.RAMSCH) {
			getPlayerPanel(player).setDeclarer(true);
		}

		if (gameAnnouncement.isOuvert()) {
			getPlayerPanel(player).showCards();
		}
	}

	/**
	 * Sets the game state
	 * 
	 * @param state
	 *            Game state
	 */
	public void setGameState(final GameState state) {

		log.debug(".setGameState(" + state + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		this.gameInfoPanel.setGameState(state);

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
			this.userPanel.setGameState(state);
			break;
		case SCHIEBERAMSCH:
			setContextPanel(ContextPanelType.SCHIEBERAMSCH);
			this.userPanel.setGameState(state);
			break;
		case PICKING_UP_SKAT:
			if (this.userPanel.getPosition().equals(this.declarer)) {
				setContextPanel(ContextPanelType.DECLARING);
				this.userPanel.setGameState(state);
			}
			break;
		case DISCARDING:
			if (this.userPanel.getPosition().equals(this.declarer)) {
				this.userPanel.setGameState(state);
			}
			break;
		case DECLARING:
			if (this.userPanel.getPosition().equals(this.declarer)) {
				setContextPanel(ContextPanelType.DECLARING);
			}
			break;
		case RE:
			setContextPanel(ContextPanelType.RE_AFTER_CONTRA);
			break;
		case TRICK_PLAYING:
			setContextPanel(ContextPanelType.TRICK_PLAYING);
			this.userPanel.setGameState(state);
			break;
		case CALCULATING_GAME_VALUE:
		case PRELIMINARY_GAME_END:
		case GAME_OVER:
			setContextPanel(ContextPanelType.GAME_OVER);
			this.foreHand.setActivePlayer(false);
			this.middleHand.setActivePlayer(false);
			this.rearHand.setActivePlayer(false);
			break;
		}

		validate();
	}

	private void resetGameData() {

		playerPassed.put(Player.FOREHAND, Boolean.FALSE);
		playerPassed.put(Player.MIDDLEHAND, Boolean.FALSE);
		playerPassed.put(Player.REARHAND, Boolean.FALSE);
		this.ramsch = false;
		this.declarer = null;
	}

	/**
	 * Sets the context panel
	 * 
	 * @param panelType
	 *            Panel type
	 */
	void setContextPanel(final ContextPanelType panelType) {

		((CardLayout) this.gameContextPanel.getLayout()).show(this.gameContextPanel,
				panelType.toString());
		this.gameContextPanel.validate();
	}

	/**
	 * Adds a new game result
	 * 
	 * @param summary
	 *            Game summary
	 */
	public void addGameResult(final GameSummary summary) {

		this.gameOverPanel.setGameSummary(summary);

		this.skatListTableModel.addResult(this.leftOpponentPanel.getPosition(),
				this.rightOpponentPanel.getPosition(), this.userPanel.getPosition(),
				summary.getDeclarer(), summary);
		scrollSkatListToTheEnd();

		this.gameInfoPanel.setGameSummary(summary);
	}

	private void scrollSkatListToTheEnd() {
		// scroll skat list if the new result is out of scope
		final Rectangle bounds = this.scoreListTable.getCellRect(
				this.skatListTableModel.getRowCount() - 1, 0, true);
		final Point loc = bounds.getLocation();
		loc.move(loc.x, loc.y + bounds.height);
		this.scoreListScrollPane.getViewport().setViewPosition(loc);
	}

	Player getHumanPosition() {

		return this.userPanel.getPosition();
	}

	/**
	 * Clears the skat table
	 */
	public void clearTable() {

		this.gameInfoPanel.clear();
		this.biddingPanel.resetPanel();
		this.declaringPanel.resetPanel();
		this.gameOverPanel.resetPanel();
		this.schieberamschPanel.resetPanel();
		clearHand(Player.FOREHAND);
		clearHand(Player.MIDDLEHAND);
		clearHand(Player.REARHAND);
		clearTrickCards();
		clearLastTrickCards();
		// default sorting is grand sorting
		this.leftOpponentPanel.setSortGameType(GameType.GRAND);
		this.rightOpponentPanel.setSortGameType(GameType.GRAND);
		this.userPanel.setSortGameType(GameType.GRAND);

		resetGameData();
	}

	/**
	 * Sets the bid value for a player.
	 */
	@Subscribe
	public void setBidValueOn(final BidEvent event) {

		log.debug(event.player + " bids: " + event.bid);

		setBidValue(event);
	}

	private void setBidValue(final AbstractBidEvent event) {
		this.biddingPanel.setBid(event.player, event.bid);
		getPlayerPanel(event.player).setBidValue(event.bid);
	}

	/**
	 * Sets the bid value for a player.
	 */
	@Subscribe
	public void setBidValueOn(final HoldBidEvent event) {

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
	 * @param skat
	 *            Skat
	 */
	public void setSkat(final CardList skat) {

		if (this.ramsch) {
			this.schieberamschPanel.setSkat(skat);
		} else {
			this.declaringPanel.setSkat(skat);
		}
	}

	/**
	 * Takes a card from skat to user panel
	 * 
	 * @param card
	 *            Card
	 */
	public void takeCardFromSkat(final Card card) {
		takeCardFromSkat(this.userPanel, card);
	}

	/**
	 * Takes a card from skat
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void takeCardFromSkat(final Player player, final Card card) {
		takeCardFromSkat(getPlayerPanel(player), card);
	}

	private void takeCardFromSkat(final AbstractHandPanel panel, final Card card) {

		if (!panel.isHandFull()) {

			this.declaringPanel.removeCard(card);
			this.schieberamschPanel.removeCard(card);
			panel.addCard(card);

		} else {

			log.debug("Player panel full!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Puts a card from the user panel to the skat
	 * 
	 * @param card
	 *            Card
	 */
	public void putCardIntoSkat(final Card card) {
		putCardIntoSkat(this.userPanel, card);
	}

	/**
	 * Puts a card into the skat
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void putCardIntoSkat(final Player player, final Card card) {
		putCardIntoSkat(getPlayerPanel(player), card);
	}

	private void putCardIntoSkat(final AbstractHandPanel panel, final Card card) {

		if (!this.declaringPanel.isHandFull()) {

			panel.removeCard(card);
			this.declaringPanel.addCard(card);
			this.schieberamschPanel.addCard(card);

		} else {

			log.debug("Discard panel full!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Clears the skat list
	 */
	public void clearSkatList() {

		this.skatListTableModel.clearList();
	}

	/**
	 * Sets maximum number of players
	 * 
	 * @param maxPlayers
	 *            Maximum number of players
	 */
	protected void setMaxPlayers(final int maxPlayers) {

		this.skatListTableModel.setPlayerCount(maxPlayers);
	}

	/**
	 * Sets player name
	 * 
	 * @param player
	 *            Player position
	 * @param name
	 *            Player name
	 */
	public void setPlayerName(final Player player, final String name) {

		this.playerNamesAndPositions.put(name, player);
		final AbstractHandPanel panel = getHandPanel(player);

		if (panel != null) {
			if (name != null) {
				panel.setPlayerName(name);
			}
		}
	}

	/**
	 * Sets player time
	 * 
	 * @param player
	 *            Player position
	 * @param time
	 *            Player time
	 */
	public void setPlayerTime(final Player player, final double time) {

		final AbstractHandPanel panel = getHandPanel(player);

		if (panel != null) {
			panel.setPlayerTime(time);
		}
	}

	/**
	 * Sets player flag for chat enabled yes/no
	 * 
	 * @param playerName
	 *            Player name
	 * @param isChatEnabled
	 *            Flag for chat enabled yes/no
	 */
	public void setPlayerChatEnabled(final String playerName,
			final boolean isChatEnabled) {

		final AbstractHandPanel panel = getHandPanel(playerName);

		if (panel != null) {
			panel.setChatEnabled(isChatEnabled);
		}
	}

	/**
	 * Sets player flag for ready to play yes/no
	 * 
	 * @param playerName
	 *            Player name
	 * @param isReadyToPlay
	 *            Flag for ready to play yes/no
	 */
	public void setPlayerReadyToPlay(final String playerName,
			final boolean isReadyToPlay) {

		final AbstractHandPanel panel = getHandPanel(playerName);

		if (panel != null) {
			panel.setReadyToPlay(isReadyToPlay);
		}
	}

	/**
	 * Sets player flag for resign
	 * 
	 * @param player
	 *            Player
	 */
	public void setResign(final Player player) {

		final AbstractHandPanel panel = getHandPanel(player);

		if (panel != null) {
			panel.setResign(true);
		}
	}

	private AbstractHandPanel getHandPanel(final String playerName) {

		AbstractHandPanel panel = null;

		if (playerName.equals(this.userPanel.getPlayerName())) {
			panel = this.userPanel;
		} else if (playerName.equals(this.leftOpponentPanel.getPlayerName())) {
			panel = this.leftOpponentPanel;
		} else if (playerName.equals(this.rightOpponentPanel.getPlayerName())) {
			panel = this.rightOpponentPanel;
		}

		return panel;
	}

	private AbstractHandPanel getHandPanel(final Player player) {
		AbstractHandPanel panel = null;

		switch (player) {
		case FOREHAND:
			panel = this.foreHand;
			break;
		case MIDDLEHAND:
			panel = this.middleHand;
			break;
		case REARHAND:
			panel = this.rearHand;
			break;
		}
		return panel;
	}

	/**
	 * Sets the last trick
	 * 
	 * @param trick
	 *            Last trick
	 */
	public void setLastTrick(final Trick trick) {

		this.lastTrickPanel.clearCards();
		final Player trickForeHand = trick.getForeHand();
		this.lastTrickPanel.addCard(trickForeHand, trick.getFirstCard());
		this.lastTrickPanel.addCard(trickForeHand.getLeftNeighbor(),
				trick.getSecondCard());
		this.lastTrickPanel.addCard(trickForeHand.getRightNeighbor(),
				trick.getThirdCard());
	}

	/**
	 * Sets the active player
	 * 
	 * @param player
	 *            Active player
	 */
	@Subscribe
	public void setActivePlayerOn(final ActivePlayerChangedEvent event) {
		switch (event.player) {
		case FOREHAND:
			this.foreHand.setActivePlayer(true);
			this.middleHand.setActivePlayer(false);
			this.rearHand.setActivePlayer(false);
			break;
		case MIDDLEHAND:
			this.foreHand.setActivePlayer(false);
			this.middleHand.setActivePlayer(true);
			this.rearHand.setActivePlayer(false);
			break;
		case REARHAND:
			this.foreHand.setActivePlayer(false);
			this.middleHand.setActivePlayer(false);
			this.rearHand.setActivePlayer(true);
			break;
		}
	}

	/**
	 * Sets passing of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void setPass(final Player player) {

		log.debug(player + " passes"); //$NON-NLS-1$

		playerPassed.put(player, Boolean.TRUE);

		getPlayerPanel(player).setPass(true);
		this.biddingPanel.setPass(player);
	}

	/**
	 * Sets the series state
	 * 
	 * @param state
	 *            Series state
	 */
	public void setSeriesState(final SeriesState state) {

		if (SeriesState.SERIES_FINISHED.equals(state)) {

			setContextPanel(ContextPanelType.START);
		}
	}

	/**
	 * Sets the bid value to make
	 * 
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToMake(final int bidValue) {

		this.biddingPanel.setBidValueToMake(bidValue);
	}

	/**
	 * Sets the bid value to hold
	 * 
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToHold(final int bidValue) {

		this.biddingPanel.setBidValueToHold(bidValue);
	}

	@Override
	protected void setFocus() {
		// FIXME (jan 20.11.2010) set active/inactive actions

	}

	/**
	 * Sets the trick number
	 * 
	 * @param trickNumber
	 *            Trick number
	 */
	public void setTrickNumber(final int trickNumber) {

		this.gameInfoPanel.setTrickNumber(trickNumber);
	}

	/**
	 * Sets the game number
	 * 
	 * @param gameNumber
	 *            Game number
	 */
	public void setGameNumber(final int gameNumber) {
		this.gameInfoPanel.setGameNumber(gameNumber);
	}

	/**
	 * Sets the player names
	 * 
	 * @param upperLeftPlayerName
	 *            Upper left player name
	 * @param upperRightPlayerName
	 *            Upper right player name
	 * @param lowerPlayerName
	 *            Lower player name
	 */
	public void setPlayerNames(final String upperLeftPlayerName,
			final String upperRightPlayerName, final String lowerPlayerName) {
		// FIXME (jan 26.01.2011) possible code duplication with
		// setPlayerInformation()
		this.leftOpponentPanel.setPlayerName(upperLeftPlayerName);
		this.rightOpponentPanel.setPlayerName(upperRightPlayerName);
		this.userPanel.setPlayerName(lowerPlayerName);
		this.skatListTableModel.setPlayerNames(upperLeftPlayerName,
				upperRightPlayerName, lowerPlayerName);
	}

	/**
	 * Gets the declarer player for the table
	 * 
	 * @return Declarer player
	 */
	public Player getDeclarer() {
		return this.declarer;
	}

	/**
	 * Sets the declarer player for the table
	 * 
	 * @param declarer
	 *            Declarer player
	 */
	public void setDeclarer(final Player declarer) {
		this.declarer = declarer;
	}

	/**
	 * Shows the cards of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void showCards(final Player player) {

		getPlayerPanel(player).showCards();
	}

	/**
	 * Hides the cards of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void hideCards(final Player player) {

		getPlayerPanel(player).hideCards();
	}

	/**
	 * Sets the schieben of a player
	 * 
	 * @param player
	 *            Player position
	 */
	public void setGeschoben(final Player player) {
		getPlayerPanel(player).setGeschoben();
	}

	/**
	 * Sets the discarded skat
	 * 
	 * @param player
	 *            Player
	 * @param skatBefore
	 *            Skat before discarding
	 * @param discardedSkat
	 *            Skat after discarding
	 */
	public void setDiscardedSkat(final Player player,
			final CardList skatBefore, final CardList discardedSkat) {
		getPlayerPanel(player);

		for (int i = 0; i < 2; i++) {
			final Card skatCard = skatBefore.get(i);
			takeCardFromSkat(player, skatCard);
		}
		for (int i = 0; i < 2; i++) {
			final Card skatCard = discardedSkat.get(i);
			putCardIntoSkat(player, skatCard);
		}
	}

	/**
	 * Shows cards of all players
	 * 
	 * @param cards
	 *            Cards of all players
	 */
	public void showCards(final Map<Player, CardList> cards) {
		for (final Player player : cards.keySet()) {
			removeAllCards(player);
			showCards(player);
			addCards(player, cards.get(player));
		}
	}

	public void setContra(Player player) {
		getPlayerPanel(player).setContra();
		this.gameInfoPanel.setContra();
	}

	public void setRe(Player player) {
		getPlayerPanel(player).setRe();
		this.gameInfoPanel.setRe();
	}
}
