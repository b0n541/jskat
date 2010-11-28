/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.SkatSeriesData.SeriesState;
import de.jskat.gui.AbstractTabPanel;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.action.human.ContinueSkatSeriesAction;
import de.jskat.gui.action.main.StartSkatSeriesAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Panel for a skat table
 */
public class SkatTablePanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SkatTablePanel.class);
	// FIXME (jan 14.11.2010) looks wrong to me, was made static to avoid
	// NullPointerException during table creation
	protected static Map<Player, Boolean> playerPassed = new HashMap<Player, Boolean>();

	protected HandPanel foreHand;
	protected HandPanel middleHand;
	protected HandPanel hindHand;
	protected OpponentPanel leftOpponentPanel;
	protected OpponentPanel rightOpponentPanel;
	protected JSkatUserPanel userPanel;
	protected GameInformationPanel gameInfoPanel;
	protected JPanel gameContextPanel;
	protected Map<ContextPanelTypes, JPanel> contextPanels;
	protected TrickPlayPanel trickPanel;
	protected TrickPlayPanel lastTrickPanel;
	/**
	 * Table model for skat list
	 */
	protected SkatListTableModel skatListTableModel;
	protected JTable skatListTable;
	protected JScrollPane skatListScrollPane;
	protected BiddingContextPanel biddingPanel;
	protected DeclaringContextPanel declaringPanel;

	/**
	 * @see AbstractTabPanel#AbstractTabPanel(String, JSkatGraphicRepository,
	 *      ActionMap, ResourceBundle)
	 */
	public SkatTablePanel(String newTableName,
			JSkatGraphicRepository jskatBitmaps, ActionMap actions,
			ResourceBundle jskatStrings) {

		super(newTableName, jskatBitmaps, actions, jskatStrings);

		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.contextPanels = new HashMap<ContextPanelTypes, JPanel>();

		getActionMap().get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(true);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				getSkatListPanel(), getPlayGroundPanel());
		add(splitPane, "grow"); //$NON-NLS-1$
	}

	private JPanel getSkatListPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.skatListTableModel = new SkatListTableModel();
		this.skatListTable = new JTable(this.skatListTableModel);

		for (int i = 0; i < this.skatListTable.getColumnModel()
				.getColumnCount(); i++) {

			if (i == 3) {

				// game colum is bigger
				this.skatListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(40);
			} else {

				this.skatListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(20);
			}
		}

		this.skatListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.skatListScrollPane = new JScrollPane(this.skatListTable);
		this.skatListScrollPane.setPreferredSize(new Dimension(250, 100));
		this.skatListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.skatListScrollPane);

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
		createGameContextPanel();
		this.userPanel = getPlayerPanel();

		return new PlayGroundPanel(bitmaps, gameInfoPanel, leftOpponentPanel,
				rightOpponentPanel, gameContextPanel, userPanel);
	}

	private GameInformationPanel getGameInfoPanel() {

		return new GameInformationPanel();
	}

	private OpponentPanel getOpponentPanel() {

		return new OpponentPanel(getActionMap(), this.bitmaps, 12);
	}

	protected void addContextPanel(ContextPanelTypes panelType, JPanel panel) {

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

		addContextPanel(ContextPanelTypes.START,
				new StartContextPanel((StartSkatSeriesAction) getActionMap()
						.get(JSkatAction.START_LOCAL_SERIES)));

		this.biddingPanel = new BiddingContextPanel(getActionMap(), bitmaps,
				strings);
		addContextPanel(ContextPanelTypes.BIDDING, this.biddingPanel);

		this.declaringPanel = new DeclaringContextPanel(getActionMap(),
				this.bitmaps, strings, 4);
		addContextPanel(ContextPanelTypes.DECLARING, this.declaringPanel);

		JPanel trickHoldingPanel = new JPanel(new MigLayout(
				"fill", "[shrink][grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		this.lastTrickPanel = new TrickPlayPanel(this.bitmaps, 0.25);
		trickHoldingPanel.add(this.lastTrickPanel, "width 25%"); //$NON-NLS-1$
		this.trickPanel = new TrickPlayPanel(this.bitmaps, 0.5);
		trickHoldingPanel.add(this.trickPanel, "grow"); //$NON-NLS-1$
		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		trickHoldingPanel.add(blankPanel, "width 25%"); //$NON-NLS-1$
		trickHoldingPanel.setOpaque(false);
		addContextPanel(ContextPanelTypes.TRICK_PLAYING, trickHoldingPanel);

		addContextPanel(ContextPanelTypes.GAME_OVER,
				new GameOverPanel((ContinueSkatSeriesAction) getActionMap()
						.get(JSkatAction.CONTINUE_LOCAL_SERIES)));
	}

	private JSkatUserPanel getPlayerPanel() {

		return new JSkatUserPanel(getActionMap(), this.bitmaps, 12);
	}

	private HandPanel getPlayerPanel(Player player) {

		HandPanel result = null;

		switch (player) {
		case FORE_HAND:
			result = foreHand;
			break;
		case MIDDLE_HAND:
			result = middleHand;
			break;
		case HIND_HAND:
			result = hindHand;
			break;
		}

		return result;
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
	public void setPositions(Player leftPosition, Player rightPosition,
			Player playerPosition) {

		this.leftOpponentPanel.setPosition(leftPosition);
		this.rightOpponentPanel.setPosition(rightPosition);
		this.userPanel.setPosition(playerPosition);

		this.biddingPanel.setUserPosition(playerPosition);
		this.trickPanel.setUserPosition(playerPosition);
		this.lastTrickPanel.setUserPosition(playerPosition);

		// FIXME (jansch 09.11.2010) code duplication with
		// BiddingPanel.setPlayerPositions()
		switch (playerPosition) {
		case FORE_HAND:
			this.foreHand = this.userPanel;
			this.middleHand = this.leftOpponentPanel;
			this.hindHand = this.rightOpponentPanel;
			break;
		case MIDDLE_HAND:
			this.foreHand = this.rightOpponentPanel;
			this.middleHand = this.userPanel;
			this.hindHand = this.leftOpponentPanel;
			break;
		case HIND_HAND:
			this.foreHand = this.leftOpponentPanel;
			this.middleHand = this.rightOpponentPanel;
			this.hindHand = this.userPanel;
			break;
		}
	}

	public void addCard(Player position, Card card) {

		getPlayerPanel(position).addCard(card);
	}

	public void addCards(Player position, Collection<Card> cards) {

		getPlayerPanel(position).addCards(cards);
	}

	/**
	 * Sets a card played in a trick
	 * 
	 * @param player
	 *            Player position
	 * @param card
	 *            Card
	 */
	public void setTrickCard(Player player, Card card) {

		this.trickPanel.setCard(player, card);
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

	public void removeCard(Player position, Card card) {

		switch (position) {
		case FORE_HAND:
			this.foreHand.removeCard(card);
			break;
		case MIDDLE_HAND:
			this.middleHand.removeCard(card);
			break;
		case HIND_HAND:
			this.hindHand.removeCard(card);
			break;
		}
	}

	public void clearHand(Player position) {

		getPlayerPanel(position).clearHandPanel();
	}

	/**
	 * Sets the game announcement
	 */
	public void setGameAnnouncement(Player player, GameAnnouncement ann) {

		this.gameInfoPanel.setGameAnnouncement(ann);

		this.leftOpponentPanel.setSortGameType(ann.getGameType());
		this.rightOpponentPanel.setSortGameType(ann.getGameType());
		this.userPanel.setSortGameType(ann.getGameType());

		getPlayerPanel(player).setDeclarer();
	}

	/**
	 * Sets the game state
	 * 
	 * @param state
	 *            Game state
	 */
	public void setGameState(GameState state) {

		log.debug(state);

		this.gameInfoPanel.setGameState(state);

		switch (state) {
		case GAME_START:
			setContextPanel(ContextPanelTypes.START);
			resetGameData();
			break;
		case DEALING:
			setContextPanel(ContextPanelTypes.START);
			break;
		case BIDDING:
			setContextPanel(ContextPanelTypes.BIDDING);
			getActionMap().get(JSkatAction.ANNOUNCE_GAME).setEnabled(false);
			break;
		case LOOK_INTO_SKAT:
			// FIXME show panel only if the human player is looking into the
			// skat
			setContextPanel(ContextPanelTypes.DECLARING);
			getActionMap().get(JSkatAction.ANNOUNCE_GAME).setEnabled(true);
			break;
		case DISCARDING:
			setContextPanel(ContextPanelTypes.DECLARING);
			this.userPanel.setGameState(GameState.DISCARDING);
			break;
		case DECLARING:
			setContextPanel(ContextPanelTypes.DECLARING);
			break;
		case TRICK_PLAYING:
			setContextPanel(ContextPanelTypes.TRICK_PLAYING);
			this.userPanel.setGameState(GameState.TRICK_PLAYING);
			break;
		case CALC_GAME_VALUE:
		case PRELIMINARY_GAME_END:
		case GAME_OVER:
			setContextPanel(ContextPanelTypes.GAME_OVER);
			break;
		}
	}

	private void resetGameData() {

		playerPassed.put(Player.FORE_HAND, Boolean.FALSE);
		playerPassed.put(Player.MIDDLE_HAND, Boolean.FALSE);
		playerPassed.put(Player.HIND_HAND, Boolean.FALSE);
	}

	/**
	 * Sets the context panel
	 * 
	 * @param panelType
	 *            Panel type
	 */
	void setContextPanel(ContextPanelTypes panelType) {

		((CardLayout) this.gameContextPanel.getLayout()).show(
				this.gameContextPanel, panelType.toString());
	}

	/**
	 * Adds a new game result
	 * 
	 * @param data
	 *            Game data
	 */
	public void addGameResult(SkatGameData data) {

		this.skatListTableModel.addResult(this.leftOpponentPanel.getPosition(),
				this.rightOpponentPanel.getPosition(),
				this.userPanel.getPosition(), data.getDeclarer(),
				data.getGameResult());

		// scroll skat list if the new result is out of scope
		Rectangle bounds = this.skatListTable.getCellRect(
				this.skatListTableModel.getRowCount(), 0, true);
		Point loc = bounds.getLocation();
		loc.move(loc.x, loc.y + bounds.height - 1);
		this.skatListScrollPane.getViewport().setViewPosition(loc);

		if (data.getGameType() != GameType.PASSED_IN) {
			this.gameInfoPanel.setGameResult(data);
		}
	}

	Player getHumanPosition() {

		return this.userPanel.getPosition();
	}

	CardPanel getLastClickedCardPanel() {

		return this.userPanel.getLastClickedCardPanel();
	}

	/**
	 * Clears the skat table
	 */
	public void clearTable() {

		this.gameInfoPanel.clear();
		this.biddingPanel.resetPanel();
		this.declaringPanel.resetPanel();
		this.clearHand(Player.FORE_HAND);
		this.clearHand(Player.MIDDLE_HAND);
		this.clearHand(Player.HIND_HAND);
		this.clearTrickCards();
		this.clearLastTrickCards();
		// default sorting is grand sorting
		this.leftOpponentPanel.setSortGameType(GameType.GRAND);
		this.rightOpponentPanel.setSortGameType(GameType.GRAND);
		this.userPanel.setSortGameType(GameType.GRAND);

		resetGameData();
	}

	/**
	 * Sets the fore hand player for the trick
	 * 
	 * @param trickForeHand
	 *            Fore hand player for the trick
	 */
	public void setTrickForeHand(Player trickForeHand) {

		setActivePlayer(trickForeHand);
	}

	/**
	 * Sets the bid value for a player
	 * 
	 * @param player
	 *            Player
	 * @param bidValue
	 *            Bid value
	 * @param madeBid
	 *            TRUE, if the player made the bid<br>
	 *            FALSE, if the player hold the bid
	 */
	public void setBid(Player player, int bidValue, boolean madeBid) {

		log.debug(player + " " + (madeBid ? "bids" : "holds") + ": " + bidValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		this.biddingPanel.setBid(player, bidValue);
		getPlayerPanel(player).setBidValue(bidValue);

		switch (player) {
		case FORE_HAND:
			if (playerPassed.get(Player.MIDDLE_HAND).booleanValue()) {
				setActivePlayer(Player.HIND_HAND);
			} else {
				setActivePlayer(Player.MIDDLE_HAND);
			}
			break;
		case MIDDLE_HAND:
			if (madeBid) {
				setActivePlayer(Player.FORE_HAND);
			} else {
				setActivePlayer(Player.HIND_HAND);
			}
			break;
		case HIND_HAND:
			if (playerPassed.get(Player.FORE_HAND).booleanValue()) {
				setActivePlayer(Player.MIDDLE_HAND);
			} else {
				setActivePlayer(Player.FORE_HAND);
			}
			break;
		}
	}

	public void startGame() {

		this.clearTable();
	}

	public void setSkat(CardList skat) {

		declaringPanel.setSkat(skat);
	}

	public void takeCardFromSkat(Card card) {

		if (!this.userPanel.isHandFull()) {

			this.declaringPanel.removeCard(card);
			this.userPanel.addCard(card);
		} else {

			log.debug("Player panel full!!!");
		}
	}

	public void putCardIntoSkat(Card card) {

		if (!this.declaringPanel.isHandFull()) {

			this.userPanel.removeCard(card);
			this.declaringPanel.addCard(card);
		} else {

			log.debug("Discard panel full!!!");
		}
	}

	public void clearSkatList() {

		this.skatListTableModel.clearList();
	}

	/**
	 * Sets maximum number of players
	 * 
	 * @param maxPlayers
	 *            Maximum number of players
	 */
	protected void setMaxPlayers(int maxPlayers) {

		this.skatListTableModel.setPlayerCount(maxPlayers);
	}

	/**
	 * Sets player information
	 * 
	 * @param player
	 *            Player position
	 * @param name
	 *            Player name
	 * @param time
	 *            Player time
	 */
	public void setPlayerInformation(Player player, String name, double time) {

		HandPanel panel = null;

		switch (player) {
		case FORE_HAND:
			panel = this.foreHand;
			break;
		case MIDDLE_HAND:
			panel = this.middleHand;
			break;
		case HIND_HAND:
			panel = this.hindHand;
			break;
		}

		if (panel != null) {
			if (name != null) {
				panel.setPlayerName(name);
			}
			panel.setPlayerTime(time);
		}
	}

	/**
	 * Sets the last trick
	 * 
	 * @param trickForeHand
	 * @param foreHandCard
	 * @param middleHandCard
	 * @param hindHandCard
	 */
	public void setLastTrick(Player trickForeHand, Card foreHandCard,
			Card middleHandCard, Card hindHandCard) {

		this.lastTrickPanel.clearCards();
		this.lastTrickPanel.setCard(trickForeHand, foreHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getLeftNeighbor(),
				middleHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getRightNeighbor(),
				hindHandCard);
	}

	/**
	 * Sets the active player
	 * 
	 * @param player
	 *            Active player
	 */
	public void setActivePlayer(Player player) {
		switch (player) {
		case FORE_HAND:
			foreHand.setActivePlayer(true);
			middleHand.setActivePlayer(false);
			hindHand.setActivePlayer(false);
			break;
		case MIDDLE_HAND:
			foreHand.setActivePlayer(false);
			middleHand.setActivePlayer(true);
			hindHand.setActivePlayer(false);
			break;
		case HIND_HAND:
			foreHand.setActivePlayer(false);
			middleHand.setActivePlayer(false);
			hindHand.setActivePlayer(true);
			break;
		}
	}

	public void setPass(Player player) {

		log.debug(player + " passes"); //$NON-NLS-1$

		playerPassed.put(player, Boolean.TRUE);

		getPlayerPanel(player).setPass();

		switch (player) {
		case FORE_HAND:
		case MIDDLE_HAND:
			setActivePlayer(Player.HIND_HAND);
			break;
		case HIND_HAND:
			if (playerPassed.get(Player.FORE_HAND).booleanValue()) {
				setActivePlayer(Player.MIDDLE_HAND);
			} else {
				setActivePlayer(Player.FORE_HAND);
			}
			break;
		}
	}

	/**
	 * Sets the series state
	 * 
	 * @param state
	 *            Series state
	 */
	public void setSeriesState(SeriesState state) {

		if (SeriesState.SERIES_FINISHED.equals(state)) {

			setContextPanel(ContextPanelTypes.START);
		}
	}

	/**
	 * Sets the bid value to make
	 * 
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToMake(int bidValue) {

		biddingPanel.setBidValueToMake(bidValue);
	}

	/**
	 * Sets the bid value to hold
	 * 
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToHold(int bidValue) {

		biddingPanel.setBidValueToHold(bidValue);
	}

	@Override
	protected void setFocus() {
		// FIXME (jan 20.11.2010) set active/inactive actions

	}
}
