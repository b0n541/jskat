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
import de.jskat.data.JSkatOptions;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.SkatSeriesData.SeriesState;
import de.jskat.gui.AbstractTabPanel;
import de.jskat.gui.action.JSkatAction;
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
	protected GameOverPanel gameOverPanel;
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
	 *      ActionMap, ResourceBundle, JSkatOptions)
	 */
	public SkatTablePanel(String newTableName,
			JSkatGraphicRepository jskatBitmaps, ActionMap actions,
			ResourceBundle jskatStrings, JSkatOptions newOptions) {

		super(newTableName, jskatBitmaps, actions, jskatStrings, newOptions);

		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		contextPanels = new HashMap<ContextPanelTypes, JPanel>();

		getActionMap().get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(true);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				getSkatListPanel(), getPlayGroundPanel());
		add(splitPane, "grow"); //$NON-NLS-1$
	}

	private JPanel getSkatListPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		skatListTableModel = new SkatListTableModel();
		skatListTable = new JTable(skatListTableModel);

		for (int i = 0; i < skatListTable.getColumnModel().getColumnCount(); i++) {

			if (i == 3) {

				// game colum is bigger
				skatListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(40);
			} else {

				skatListTable.getColumnModel().getColumn(i)
						.setPreferredWidth(20);
			}
		}

		skatListTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		skatListScrollPane = new JScrollPane(skatListTable);
		skatListScrollPane.setMinimumSize(new Dimension(150, 100));
		skatListScrollPane.setPreferredSize(new Dimension(200, 100));
		skatListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(skatListScrollPane);

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
		userPanel = getPlayerPanel();
		createGameContextPanel();

		return new PlayGroundPanel(bitmaps, gameInfoPanel, leftOpponentPanel,
				rightOpponentPanel, gameContextPanel, userPanel);
	}

	private GameInformationPanel getGameInfoPanel() {

		return new GameInformationPanel(bitmaps, strings, options.getCardFace());
	}

	private OpponentPanel getOpponentPanel() {

		return new OpponentPanel(getActionMap(), bitmaps, strings, 12);
	}

	protected void addContextPanel(ContextPanelTypes panelType, JPanel panel) {

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

		addContextPanel(ContextPanelTypes.START,
				new StartContextPanel((StartSkatSeriesAction) getActionMap()
						.get(JSkatAction.START_LOCAL_SERIES)));

		biddingPanel = new BiddingContextPanel(getActionMap(), bitmaps,
				strings, userPanel);
		addContextPanel(ContextPanelTypes.BIDDING, biddingPanel);

		declaringPanel = new DeclaringContextPanel(getActionMap(), bitmaps,
				strings, userPanel, 4);
		addContextPanel(ContextPanelTypes.DECLARING, declaringPanel);

		JPanel trickHoldingPanel = new JPanel(new MigLayout(
				"fill", "[shrink][grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		lastTrickPanel = new TrickPlayPanel(bitmaps, 0.5, false);
		trickHoldingPanel.add(lastTrickPanel, "width 25%"); //$NON-NLS-1$
		trickPanel = new TrickPlayPanel(bitmaps, 0.6, true);
		trickHoldingPanel.add(trickPanel, "grow"); //$NON-NLS-1$
		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		trickHoldingPanel.add(blankPanel, "width 25%"); //$NON-NLS-1$
		trickHoldingPanel.setOpaque(false);
		addContextPanel(ContextPanelTypes.TRICK_PLAYING, trickHoldingPanel);

		gameOverPanel = new GameOverPanel(getActionMap(), bitmaps, strings);
		addContextPanel(ContextPanelTypes.GAME_OVER, gameOverPanel);
	}

	private JSkatUserPanel getPlayerPanel() {

		return new JSkatUserPanel(getActionMap(), bitmaps, strings, 12);
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

		leftOpponentPanel.setPosition(leftPosition);
		rightOpponentPanel.setPosition(rightPosition);
		userPanel.setPosition(playerPosition);

		biddingPanel.setUserPosition(playerPosition);
		trickPanel.setUserPosition(playerPosition);
		lastTrickPanel.setUserPosition(playerPosition);
		gameOverPanel.setUserPosition(playerPosition);

		// FIXME (jansch 09.11.2010) code duplication with
		// BiddingPanel.setPlayerPositions()
		switch (playerPosition) {
		case FORE_HAND:
			foreHand = userPanel;
			middleHand = leftOpponentPanel;
			hindHand = rightOpponentPanel;
			break;
		case MIDDLE_HAND:
			foreHand = rightOpponentPanel;
			middleHand = userPanel;
			hindHand = leftOpponentPanel;
			break;
		case HIND_HAND:
			foreHand = leftOpponentPanel;
			middleHand = rightOpponentPanel;
			hindHand = userPanel;
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
	public void addCard(Player player, Card card) {

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
	public void addCards(Player player, Collection<Card> cards) {

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
	public void setTrickCard(Player player, Card card) {

		trickPanel.setCard(player, card);
	}

	/**
	 * Clears trick cards
	 */
	public void clearTrickCards() {

		trickPanel.clearCards();
	}

	/**
	 * Clears last trick cards
	 */
	void clearLastTrickCards() {

		lastTrickPanel.clearCards();
	}

	/**
	 * Removes a card from a player
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void removeCard(Player player, Card card) {

		switch (player) {
		case FORE_HAND:
			foreHand.removeCard(card);
			break;
		case MIDDLE_HAND:
			middleHand.removeCard(card);
			break;
		case HIND_HAND:
			hindHand.removeCard(card);
			break;
		}
	}

	/**
	 * Clears the hand of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void clearHand(Player player) {

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
	public void setGameAnnouncement(Player player,
			GameAnnouncement gameAnnouncement) {

		gameInfoPanel.setGameAnnouncement(gameAnnouncement);

		leftOpponentPanel.setSortGameType(gameAnnouncement.getGameType());
		rightOpponentPanel.setSortGameType(gameAnnouncement.getGameType());
		userPanel.setSortGameType(gameAnnouncement.getGameType());

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

		gameInfoPanel.setGameState(state);

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
			userPanel.setGameState(GameState.DISCARDING);
			break;
		case DECLARING:
			setContextPanel(ContextPanelTypes.DECLARING);
			break;
		case TRICK_PLAYING:
			setContextPanel(ContextPanelTypes.TRICK_PLAYING);
			userPanel.setGameState(GameState.TRICK_PLAYING);
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

		((CardLayout) gameContextPanel.getLayout()).show(gameContextPanel,
				panelType.toString());
	}

	/**
	 * Adds a new game result
	 * 
	 * @param gameData
	 *            Game data
	 */
	public void addGameResult(SkatGameData gameData) {

		gameOverPanel.setGameResult(gameData);

		skatListTableModel.addResult(leftOpponentPanel.getPosition(),
				rightOpponentPanel.getPosition(), userPanel.getPosition(),
				gameData.getDeclarer(), gameData.getGameResult());

		// scroll skat list if the new result is out of scope
		Rectangle bounds = skatListTable.getCellRect(
				skatListTableModel.getRowCount() - 1, 0, true);
		Point loc = bounds.getLocation();
		loc.move(loc.x, loc.y + bounds.height);
		skatListScrollPane.getViewport().setViewPosition(loc);

		if (gameData.getGameType() != GameType.PASSED_IN) {
			gameInfoPanel.setGameResult(gameData);
		}
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
		clearHand(Player.FORE_HAND);
		clearHand(Player.MIDDLE_HAND);
		clearHand(Player.HIND_HAND);
		clearTrickCards();
		clearLastTrickCards();
		// default sorting is grand sorting
		leftOpponentPanel.setSortGameType(GameType.GRAND);
		rightOpponentPanel.setSortGameType(GameType.GRAND);
		userPanel.setSortGameType(GameType.GRAND);

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

		biddingPanel.setBid(player, bidValue);
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
	public void setSkat(CardList skat) {

		declaringPanel.setSkat(skat);
	}

	/**
	 * Takes a card from skat
	 * 
	 * @param card
	 *            Card
	 */
	public void takeCardFromSkat(Card card) {

		if (!userPanel.isHandFull()) {

			declaringPanel.removeCard(card);
			userPanel.addCard(card);

		} else {

			log.debug("Player panel full!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Puts a card into the skat
	 * 
	 * @param card
	 *            Card
	 */
	public void putCardIntoSkat(Card card) {

		if (!declaringPanel.isHandFull()) {

			userPanel.removeCard(card);
			declaringPanel.addCard(card);

		} else {

			log.debug("Discard panel full!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Clears the skat list
	 */
	public void clearSkatList() {

		skatListTableModel.clearList();
	}

	/**
	 * Sets maximum number of players
	 * 
	 * @param maxPlayers
	 *            Maximum number of players
	 */
	protected void setMaxPlayers(int maxPlayers) {

		skatListTableModel.setPlayerCount(maxPlayers);
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
			panel = foreHand;
			break;
		case MIDDLE_HAND:
			panel = middleHand;
			break;
		case HIND_HAND:
			panel = hindHand;
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

		lastTrickPanel.clearCards();
		lastTrickPanel.setCard(trickForeHand, foreHandCard);
		lastTrickPanel.setCard(trickForeHand.getLeftNeighbor(), middleHandCard);
		lastTrickPanel.setCard(trickForeHand.getRightNeighbor(), hindHandCard);
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

	/**
	 * Sets passing of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void setPass(Player player) {

		log.debug(player + " passes"); //$NON-NLS-1$

		playerPassed.put(player, Boolean.TRUE);

		getPlayerPanel(player).setPass();
		biddingPanel.setPass(player);

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

	/**
	 * Sets the trick number
	 * 
	 * @param trickNumber
	 *            Trick number
	 */
	public void setTrickNumber(int trickNumber) {

		gameInfoPanel.setTrickNumber(trickNumber);
	}
}
