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

	protected HandPanel foreHand;
	protected HandPanel middleHand;
	protected HandPanel hindHand;
	protected OpponentPanel leftOpponentPanel;
	protected OpponentPanel rightOpponentPanel;
	protected PlayerPanel playerPanel;
	protected GameInformationPanel gameInfoPanel;
	protected JPanel gameContextPanel;
	protected Map<String, JPanel> contextPanels;
	protected GameAnnouncePanel gameAnnouncePanel;
	protected TrickPlayPanel trickPanel;
	protected TrickPlayPanel lastTrickPanel;
	/**
	 * Table model for skat list
	 */
	protected SkatListTableModel skatListTableModel;
	protected JTable skatListTable;
	protected JScrollPane skatListScrollPane;
	protected BiddingPanel biddingPanel;
	protected DiscardPanel discardPanel;

	/**
	 * @see AbstractTabPanel#JSkatTabPanel(String, JSkatGraphicRepository,
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

		this.contextPanels = new HashMap<String, JPanel>();

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

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$

		this.gameInfoPanel = getGameInfoPanel();
		panel.add(this.gameInfoPanel, "span 2, growx, align center, wrap"); //$NON-NLS-1$
		this.leftOpponentPanel = getOpponentPanel();
		panel.add(this.leftOpponentPanel,
				"width 50%, growx, growy, hmin 20%, align left"); //$NON-NLS-1$
		this.rightOpponentPanel = getOpponentPanel();
		panel.add(this.rightOpponentPanel,
				"width 50%, growx, growy, hmin 20%, align right, wrap"); //$NON-NLS-1$
		panel.add(this.getContextPanel(),
				"span 2, growx, growy, align center, wrap"); //$NON-NLS-1$
		this.playerPanel = getPlayerPanel();
		panel.add(this.playerPanel,
				"span 2, growx, growy, hmin 20%, align center, wrap"); //$NON-NLS-1$

		return panel;
	}

	private GameInformationPanel getGameInfoPanel() {

		return new GameInformationPanel();
	}

	private OpponentPanel getOpponentPanel() {

		return new OpponentPanel(this, this.bitmaps, 12);
	}

	protected void addContextPanel(JPanel panel, String name) {

		if (this.contextPanels.containsKey(name)) {
			// remove existing panel first
			this.gameContextPanel.remove(this.contextPanels.get(name));
			this.contextPanels.remove(name);
		}

		this.contextPanels.put(name, panel);
		this.gameContextPanel.add(panel, name);
	}

	private JPanel getContextPanel() {

		this.gameContextPanel = new JPanel();
		this.gameContextPanel.setOpaque(false);
		this.gameContextPanel.setLayout(new CardLayout());

		addContextPanel(
				new GameStartPanel((StartSkatSeriesAction) getActionMap().get(
						JSkatAction.START_LOCAL_SERIES)),
				ContextPanelTypes.START_SERIES.toString());

		this.biddingPanel = new BiddingPanel(getActionMap());
		addContextPanel(this.biddingPanel, ContextPanelTypes.BIDDING.toString());

		this.gameContextPanel.add(new LookIntoSkatPanel(this),
				ContextPanelTypes.LOOK_INTO_SKAT.toString());

		this.discardPanel = new DiscardPanel(this, this.bitmaps, 4);
		addContextPanel(this.discardPanel,
				ContextPanelTypes.DISCARDING.toString());

		this.gameAnnouncePanel = new GameAnnouncePanel(this, this.strings);
		addContextPanel(this.gameAnnouncePanel,
				ContextPanelTypes.DECLARING.toString());

		JPanel trickHoldingPanel = new JPanel(new MigLayout("fill", "fill", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		this.lastTrickPanel = new TrickPlayPanel(this.bitmaps);
		trickHoldingPanel.add(this.lastTrickPanel, "width 25%"); //$NON-NLS-1$
		this.trickPanel = new TrickPlayPanel(this.bitmaps);
		trickHoldingPanel.add(this.trickPanel, "grow"); //$NON-NLS-1$
		addContextPanel(trickHoldingPanel,
				ContextPanelTypes.TRICK_PLAYING.toString());

		addContextPanel(
				new GameOverPanel((ContinueSkatSeriesAction) getActionMap()
						.get(JSkatAction.CONTINUE_LOCAL_SERIES)),
				ContextPanelTypes.GAME_OVER.toString());

		return this.gameContextPanel;
	}

	private PlayerPanel getPlayerPanel() {

		return new PlayerPanel(this, this.bitmaps, 12);
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
		this.playerPanel.setPosition(playerPosition);

		this.biddingPanel.setPlayerPosition(playerPosition);
		this.trickPanel.setUserPosition(playerPosition);
		this.lastTrickPanel.setUserPosition(playerPosition);

		switch (playerPosition) {
		case FORE_HAND:
			this.foreHand = this.playerPanel;
			this.middleHand = this.leftOpponentPanel;
			this.hindHand = this.rightOpponentPanel;
			break;
		case MIDDLE_HAND:
			this.foreHand = this.rightOpponentPanel;
			this.middleHand = this.playerPanel;
			this.hindHand = this.leftOpponentPanel;
			break;
		case HIND_HAND:
			this.foreHand = this.leftOpponentPanel;
			this.middleHand = this.rightOpponentPanel;
			this.hindHand = this.playerPanel;
			break;
		}
	}

	public void addCard(Player position, Card card) {

		switch (position) {
		case FORE_HAND:
			this.foreHand.addCard(card);
			break;
		case MIDDLE_HAND:
			this.middleHand.addCard(card);
			break;
		case HIND_HAND:
			this.hindHand.addCard(card);
			break;
		}
	}

	/**
	 * Sets a card played in a trick
	 * 
	 * @param position
	 *            Player position
	 * @param card
	 *            Card
	 */
	public void setTrickCard(Player position, Card card) {

		this.trickPanel.setCard(position, card);
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

		switch (position) {
		case FORE_HAND:
			this.foreHand.clearHandPanel();
			break;
		case MIDDLE_HAND:
			this.middleHand.clearHandPanel();
			break;
		case HIND_HAND:
			this.hindHand.clearHandPanel();
			break;
		}
	}

	/**
	 * Sets the game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {

		this.gameInfoPanel.setText(getGameString(ann));

		this.leftOpponentPanel.setSortGameType(ann.getGameType());
		this.rightOpponentPanel.setSortGameType(ann.getGameType());
		this.playerPanel.setSortGameType(ann.getGameType());
	}

	private String getGameString(GameAnnouncement ann) {

		// TODO i18n
		String result = ann.getGameType().toString();

		if (ann.isHand()) {
			result += " " + this.strings.getString("hand"); //$NON-NLS-1$//$NON-NLS-2$
		}
		if (ann.isOuvert()) {
			result += " " + this.strings.getString("ouvert"); //$NON-NLS-1$//$NON-NLS-2$
		}
		if (ann.isSchneider()) {
			result += " " + this.strings.getString("schneider"); //$NON-NLS-1$//$NON-NLS-2$
		}
		if (ann.isSchwarz()) {
			result += " " + this.strings.getString("schwarz"); //$NON-NLS-1$//$NON-NLS-2$
		}

		return result;
	}

	/**
	 * Sets the game state
	 * 
	 * @param state
	 *            Game state
	 */
	public void setGameState(GameState state) {

		log.debug(state);

		switch (state) {
		case NEW_GAME:
		case DEALING:
			this.gameInfoPanel.setText(state.toString());
			setContextPanel(ContextPanelTypes.START_SERIES);
			break;
		case BIDDING:
			this.gameInfoPanel.setText(state.toString());
			setContextPanel(ContextPanelTypes.BIDDING);
			break;
		case LOOK_INTO_SKAT:
			this.gameInfoPanel.setText(state.toString());
			// FIXME show panel only if the human player is looking into the
			// skat
			setContextPanel(ContextPanelTypes.LOOK_INTO_SKAT);
			break;
		case DISCARDING:
			this.gameInfoPanel.setText(state.toString());
			setContextPanel(ContextPanelTypes.DISCARDING);
			this.playerPanel.setGameState(GameState.DISCARDING);
			break;
		case DECLARING:
			setContextPanel(ContextPanelTypes.DECLARING);
			break;
		case TRICK_PLAYING:
			setContextPanel(ContextPanelTypes.TRICK_PLAYING);
			this.playerPanel.setGameState(GameState.TRICK_PLAYING);
			break;
		case PRELIMINARY_GAME_END:
		case CALC_GAME_VALUE:
			setContextPanel(ContextPanelTypes.TRICK_PLAYING);
			break;
		case GAME_OVER:
			setContextPanel(ContextPanelTypes.GAME_OVER);
			break;
		}
	}

	/**
	 * Sets the context panel
	 * 
	 * @param panelType
	 *            Panel type
	 */
	void setContextPanel(ContextPanelTypes panelType) {

		if (panelType == ContextPanelTypes.DISCARDING) {

			this.discardPanel.resetPanel();

		} else if (panelType == ContextPanelTypes.DECLARING) {

			this.gameAnnouncePanel.resetPanel();
		}

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
				this.playerPanel.getPosition(), data.getDeclarer(),
				data.getGameResult());

		// scroll skat list if the new result is out of scope
		Rectangle bounds = this.skatListTable.getCellRect(
				this.skatListTableModel.getRowCount(), 0, true);
		Point loc = bounds.getLocation();
		loc.move(loc.x, loc.y + bounds.height - 1);
		this.skatListScrollPane.getViewport().setViewPosition(loc);

		if (data.getGameType() != GameType.PASSED_IN) {
			this.gameInfoPanel.setText(getGameString(data.getAnnoucement())
					+ " won: "
					+ data.isGameWon()
					+ " result: "
					+ data.getGameResult()
					+ " player: "
					+ data.getPlayerPoints(data.getDeclarer())
					+ " opponents: "
					+ (data.getPlayerPoints(data.getDeclarer()
							.getLeftNeighbor()) + data.getPlayerPoints(data
							.getDeclarer().getRightNeighbor())));
		}
	}

	Player getHumanPosition() {

		return this.playerPanel.getPosition();
	}

	CardPanel getLastClickedCardPanel() {

		return this.playerPanel.getLastClickedCardPanel();
	}

	public void clearTable() {

		this.gameInfoPanel.clear();
		this.biddingPanel.clearBids();
		this.discardPanel.clearHandPanel();
		this.clearHand(Player.FORE_HAND);
		this.clearHand(Player.MIDDLE_HAND);
		this.clearHand(Player.HIND_HAND);
		this.clearTrickCards();
		this.clearLastTrickCards();
		// default sorting is grand sorting
		this.leftOpponentPanel.setSortGameType(GameType.GRAND);
		this.rightOpponentPanel.setSortGameType(GameType.GRAND);
		this.playerPanel.setSortGameType(GameType.GRAND);
	}

	public void setTrickForeHand(Player trickForeHand) {

		this.trickPanel.setTrickForeHand(trickForeHand);
	}

	public void setBid(Player player, int bidValue) {

		this.biddingPanel.setBid(player, bidValue);
	}

	public void startGame() {

		this.clearTable();
	}

	void setSkat(CardList skat) {
		// TODO maybe this is not needed anymore
		// this.discardPanel.setSkat(skat);
	}

	public void takeCardFromSkat(Card card) {

		if (!this.playerPanel.isHandFull()) {

			this.discardPanel.removeCard(card);
			this.playerPanel.addCard(card);
		} else {

			log.debug("Player panel full!!!");
		}
	}

	public void putCardIntoSkat(Card card) {

		if (!this.discardPanel.isHandFull()) {

			this.playerPanel.removeCard(card);
			this.discardPanel.addCard(card);
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

	public void setPlayerInformation(HandPanelType type, String name,
			double time) {

		HandPanel panel = null;

		switch (type) {
		case LEFT_OPPONENT:
			panel = this.leftOpponentPanel;
			break;
		case RIGHT_OPPONENT:
			panel = this.rightOpponentPanel;
			break;
		case PLAYER:
			panel = this.playerPanel;
			break;
		}

		if (panel != null) {
			if (name != null) {
				panel.setPlayerName(name);
			}
			panel.setPlayerTime(time);
		}
	}

	public void setLastTrick(Player trickForeHand, Card foreHandCard,
			Card middleHandCard, Card hindHandCard) {

		this.lastTrickPanel.setTrickForeHand(trickForeHand);
		this.lastTrickPanel.clearCards();
		this.lastTrickPanel.setCard(trickForeHand, foreHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getLeftNeighbor(),
				middleHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getRightNeighbor(),
				hindHandCard);
	}

	@Override
	protected void setFocus() {
		// TODO Auto-generated method stub

	}
}
