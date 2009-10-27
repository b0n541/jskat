/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.action.main.ContinueSkatSeriesAction;
import de.jskat.gui.action.main.StartSkatSeriesAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Panel for a skat table
 */
public class SkatTablePanel extends JSkatTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SkatTablePanel.class);

	protected HandPanel foreHand;
	protected HandPanel middleHand;
	protected HandPanel hindHand;
	protected OpponentPanel leftOpponentPanel;
	OpponentPanel rightOpponentPanel;
	PlayerPanel playerPanel;
	protected GameInformationPanel gameInfoPanel;
	protected JPanel gameContextPanel;
	protected Map<ContextPanelTypes, JPanel> contextPanels;
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
	 * @see JSkatTabPanel#JSkatTabPanel(String, JSkatGraphicRepository,
	 *      ActionMap)
	 */
	protected SkatTablePanel(String newTableName,
			JSkatGraphicRepository jskatBitmaps, ActionMap actions,
			ResourceBundle jskatStrings) {

		super(newTableName, jskatBitmaps, actions, jskatStrings);

		log.debug("SkatTablePanel: name: " + newTableName); //$NON-NLS-1$
	}

	/**
	 * @see JSkatTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		this.gameInfoPanel = getGameInfoPanel();
		panel.add(this.gameInfoPanel, "span 2, growx, align center, wrap"); //$NON-NLS-1$
		this.leftOpponentPanel = getOpponentPanel();
		panel.add(this.leftOpponentPanel, "growx, growy, align left"); //$NON-NLS-1$
		this.rightOpponentPanel = getOpponentPanel();
		panel.add(this.rightOpponentPanel, "growx, growy, align right, wrap"); //$NON-NLS-1$
		panel.add(this.getContextPanel(),
				"span 2, growx, growy, align center, wrap"); //$NON-NLS-1$
		this.playerPanel = getPlayerPanel();
		panel.add(this.playerPanel, "span 2, growx, growy, align center, wrap"); //$NON-NLS-1$

		return panel;
	}

	private GameInformationPanel getGameInfoPanel() {

		return new GameInformationPanel();
	}

	private OpponentPanel getOpponentPanel() {

		return new OpponentPanel(this, this.bitmaps, 12);
	}

	private JPanel getContextPanel() {

		this.gameContextPanel = new JPanel();
		this.gameContextPanel.setOpaque(false);
		this.gameContextPanel.setLayout(new CardLayout());

		this.gameContextPanel.add(new GameStartPanel(
				(StartSkatSeriesAction) getActionMap().get(
						JSkatAction.START_LOCAL_SERIES)),
				ContextPanelTypes.START_SERIES.toString());

		this.biddingPanel = new BiddingPanel(getActionMap());
		this.gameContextPanel.add(this.biddingPanel, ContextPanelTypes.BIDDING
				.toString());

		this.gameContextPanel.add(new LookIntoSkatPanel(this),
				ContextPanelTypes.LOOK_INTO_SKAT.toString());

		this.discardPanel = new DiscardPanel(this, this.bitmaps, 4);
		this.gameContextPanel.add(this.discardPanel,
				ContextPanelTypes.DISCARDING.toString());

		this.gameAnnouncePanel = new GameAnnouncePanel(this, this.strings);

		this.gameContextPanel.add(this.gameAnnouncePanel,
				ContextPanelTypes.DECLARING.toString());

		JPanel trickHoldingPanel = new JPanel(new MigLayout("fill", "fill",
				"fill"));
		this.lastTrickPanel = new TrickPlayPanel(this.bitmaps);
		trickHoldingPanel.add(this.lastTrickPanel, "width 25%");
		this.trickPanel = new TrickPlayPanel(this.bitmaps);
		trickHoldingPanel.add(this.trickPanel, "grow");

		this.gameContextPanel.add(trickHoldingPanel,
				ContextPanelTypes.TRICK_PLAYING.toString());

		this.gameContextPanel.add(new GameOverPanel(
				(ContinueSkatSeriesAction) getActionMap().get(
						JSkatAction.CONTINUE_LOCAL_SERIES)),
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
	void setPositions(Player leftPosition, Player rightPosition,
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

	void addCard(Player position, Card card) {

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
	void setTrickCard(Player position, Card card) {

		this.trickPanel.setCard(position, card);
	}

	/**
	 * Clears trick cards
	 */
	void clearTrickCards() {

		this.trickPanel.clearCards();
	}

	/**
	 * Clears last trick cards
	 */
	void clearLastTrickCards() {

		this.lastTrickPanel.clearCards();
	}

	void removeCard(Player position, Card card) {

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

	void clearHand(Player position) {

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
	void setGameAnnouncement(GameAnnouncement ann, boolean hand) {

		this.gameInfoPanel.setText(getGameString(ann, hand));

		this.leftOpponentPanel.setSortGameType(ann.getGameType());
		this.rightOpponentPanel.setSortGameType(ann.getGameType());
		this.playerPanel.setSortGameType(ann.getGameType());
	}

	private String getGameString(GameAnnouncement ann, boolean hand) {

		return ann.getGameType() + (hand ? " Hand" : "")
				+ (ann.isOuvert() ? " Ouvert" : "");
	}

	/**
	 * Sets the game state
	 * 
	 * @param state
	 *            Game state
	 */
	void setGameState(GameStates state) {

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
			this.playerPanel.setGameState(GameStates.DISCARDING);
			break;
		case DECLARING:
			setContextPanel(ContextPanelTypes.DECLARING);
			break;
		case TRICK_PLAYING:
			setContextPanel(ContextPanelTypes.TRICK_PLAYING);
			this.playerPanel.setGameState(GameStates.TRICK_PLAYING);
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
	void addGameResult(SkatGameData data) {

		int gameResult = data.getGameResult();
		Player declarer = data.getDeclarer();

		int foreHandValue = 0;
		int middleHandValue = 0;
		int hindHandValue = 0;

		if (declarer != null) {
			switch (declarer) {
			case FORE_HAND:
				foreHandValue = gameResult;
				break;
			case MIDDLE_HAND:
				middleHandValue = gameResult;
				break;
			case HIND_HAND:
				hindHandValue = gameResult;
				break;
			}
		}

		this.skatListTableModel.addResult(declarer, gameResult);

		Rectangle bounds = this.skatListTable.getCellRect(
				this.skatListTableModel.getRowCount(), 0, true);
		Point loc = bounds.getLocation();
		loc.move(loc.x, loc.y + bounds.height - 1);
		this.skatListScrollPane.getViewport().setViewPosition(loc);

		if (data.getGameType() != GameType.PASSED_IN) {
			this.gameInfoPanel.setText(getGameString(data.getAnnoucement(),
					data.isHand())
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

	void clearTable() {

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

	void setTrickForeHand(Player trickForeHand) {

		this.trickPanel.setTrickForeHand(trickForeHand);
	}

	void setBid(Player player, int bidValue) {

		this.biddingPanel.setBid(player, bidValue);
	}

	void startGame() {

		this.clearTable();
	}

	void setSkat(CardList skat) {
		// TODO maybe this is not needed anymore
		// this.discardPanel.setSkat(skat);
	}

	void takeCardFromSkat(Card card) {

		if (!this.playerPanel.isHandFull()) {

			this.discardPanel.removeCard(card);
			this.playerPanel.addCard(card);
		} else {

			log.debug("Player panel full!!!");
		}
	}

	void putCardIntoSkat(Card card) {

		if (!this.discardPanel.isHandFull()) {

			this.playerPanel.removeCard(card);
			this.discardPanel.addCard(card);
		} else {

			log.debug("Discard panel full!!!");
		}
	}

	void clearSkatList() {

		this.skatListTableModel.clearList();
	}

	/**
	 * Sets maximum number of players
	 * 
	 * @param maxPlayers Maximum number of players
	 */
	protected void setMaxPlayers(int maxPlayers) {

		this.skatListTableModel.setPlayerCount(maxPlayers);
	}

	void setPlayerInformation(HandPanelType type, String name, double time) {

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
			panel.setPlayerName(name);
			panel.setPlayerTime(time);
		}
	}

	void setLastTrick(Player trickForeHand, Card foreHandCard,
			Card middleHandCard, Card hindHandCard) {

		this.lastTrickPanel.setTrickForeHand(trickForeHand);
		this.lastTrickPanel.clearCards();
		this.lastTrickPanel.setCard(trickForeHand, foreHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getLeftNeighbor(),
				middleHandCard);
		this.lastTrickPanel.setCard(trickForeHand.getRightNeighbor(),
				hindHandCard);
	}
}
