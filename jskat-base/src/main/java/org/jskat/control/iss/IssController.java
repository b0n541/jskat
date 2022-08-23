package org.jskat.control.iss;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.iss.*;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.iss.IssDisconnectedEvent;
import org.jskat.control.event.skatgame.GameFinishEvent;
import org.jskat.control.event.table.ActivePlayerChangedEvent;
import org.jskat.control.event.table.DeclarerChangedEvent;
import org.jskat.control.event.table.SkatGameStateChangedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.gui.JSkatView;
import org.jskat.data.*;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.iss.*;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls all ISS related actions
 */
public class IssController {

    private static final Logger log = LoggerFactory.getLogger(IssController.class);

    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
    private final JSkatEventBus eventBus = JSkatEventBus.INSTANCE;
    private final JSkatApplicationData appData = JSkatApplicationData.INSTANCE;
    private final JSkatMaster jskat;
    private JSkatView view;
    private IssConnector issConnector;

    private String login;
    private String password;

    private MessageGenerator issMsg;
    private OutputChannel issOut;

    private final Map<String, SkatGameData> gameData;

    /**
     * Constructor
     *
     * @param jskatMaster JSkat master
     */
    public IssController(JSkatMaster jskatMaster) {

        jskat = jskatMaster;
        gameData = new HashMap<>();

        eventBus.register(this);
    }

    /**
     * Sets the view (MVC)
     *
     * @param newView View
     */
    public void setView(JSkatView newView) {

        view = newView;
    }

    /**
     * Disconnects from ISS.
     *
     * @param command ISS disconnect command
     */
    @Subscribe
    public void disconnectFromIssOn(IssDisconnectCommand command) {

        closeConnectionIfOpen();
    }

    /**
     * Connection to ISS was lost.
     *
     * @param event ISS disconnected event
     */
    @Subscribe
    public void closeConnectionOn(IssDisconnectedEvent event) {

        closeConnectionIfOpen();
    }

    private void closeConnectionIfOpen() {
        if (issConnector != null && issConnector.isConnected()) {

            log.debug("connection to ISS still open");

            issConnector.closeConnection();
        }
    }

    /**
     * Shows the login panel for ISS
     */
    public void showISSLoginPanel() {

        view.showISSLogin();
    }

    @Subscribe
    public void establishConnectionOn(IssConnectCommand command) {

        log.debug("connectToISS");

        if (issConnector == null) {
            issConnector = new StreamConnector();
            // issConnector = new WebSocketConnector();
        }

        log.debug("connector created");

        login = command.loginCredentials.getLoginName();
        password = command.loginCredentials.getPassword();

        if (issConnector != null && !issConnector.isConnected()) {

            issConnector.setConnectionData(login, password);
            boolean isConnected = issConnector
                    .establishConnection(this);

            if (isConnected) {
                log.debug("Connection to ISS established: " + issConnector.isConnected());
                issMsg = new MessageGenerator(login);
                issOut = issConnector.getOutputChannel();
                sendToIss(login);
                // sendToIss(issMsg.getLoginAndPasswordMessage(password));
            }
        }
    }

    private void sendToIss(String message) {
        issOut.sendMessage(message);
    }

    /**
     * Updates ISS player list
     *
     * @param playerName  Player name
     * @param language    Language
     * @param gamesPlayed Games played
     * @param strength    Play strength
     */
    public void updateISSPlayerList(String playerName,
                                    String language, long gamesPlayed, double strength) {

        jskat.updateISSPlayer(playerName, language, gamesPlayed, strength);
    }

    /**
     * Removes a player from the ISS player list
     *
     * @param playerName Player name
     */
    public void removeISSPlayerFromList(String playerName) {

        jskat.removeISSPlayer(playerName);
    }

    /**
     * Updates ISS table list
     *
     * @param tableName   Table name
     * @param maxPlayers  Maximum number of players
     * @param gamesPlayed Games played
     * @param player1     Player 1 (? for free seat)
     * @param player2     Player 2 (? for free seat)
     * @param player3     Player 3 (? for free seat)
     */
    public void updateISSTableList(String tableName,
                                   int maxPlayers, long gamesPlayed, String player1,
                                   String player2, String player3) {

        view.updateISSLobbyTableList(tableName, maxPlayers, gamesPlayed,
                player1, player2, player3);
    }

    /**
     * Removes a table from the ISS table list
     *
     * @param tableName Table name
     */
    public void removeISSTableFromList(String tableName) {

        view.removeFromISSLobbyTableList(tableName);
    }

    /**
     * Sends the password to the ISS<br>
     * only used until protocol version 14
     */
    @Deprecated
    public void sendPassword() {
        sendToIss(password);
    }

    /**
     * Sends a chat message to the ISS
     *
     * @param message Chat message
     */
    public void sendChatMessage(ChatMessage message) {

        sendToIss(issMsg.getChatMessage(message));
    }

    /**
     * Adds a chat message to a chat
     *
     * @param messageType Chat message type
     * @param params      Chat message
     */
    public void addChatMessage(ChatMessageType messageType,
                               List<String> params) {

        switch (messageType) {
            case LOBBY:
                addLobbyChatMessage(params);
                break;
            case TABLE:
                addTableChatMessage(params);
                break;
            case USER:
                // TODO implement it
                break;
        }
    }

    void addLobbyChatMessage(List<String> params) {

        log.debug("addLobbyChatMessage");

        StringBuffer message = new StringBuffer();

        // first the sender of the message
        message.append(params.get(0)).append(": ");
        // then the text
        for (int i = 1; i < params.size(); i++) {
            message.append(params.get(i)).append(' ');
        }

        ChatMessage chatMessage = new ChatMessage("Lobby",
                message.toString());

        view.appendISSChatMessage(ChatMessageType.LOBBY, chatMessage);
    }

    void addTableChatMessage(List<String> params) {

        log.debug("addTableChatMessage");

        // first the table for the message
        String tableName = params.get(0);

        StringBuffer message = new StringBuffer();
        // second the sender of the message
        message.append(params.get(1)).append(": ");
        // then the text
        for (int i = 2; i < params.size(); i++) {
            message.append(params.get(i)).append(' ');
        }

        ChatMessage chatMessage = new ChatMessage(tableName,
                message.toString());

        view.appendISSChatMessage(ChatMessageType.TABLE, chatMessage);
    }

    /**
     * Requests the creation of a new table on the ISS
     */
    public void requestTableCreation() {

        sendToIss(MessageGenerator.getTableCreationMessage());
    }

    /**
     * Creates a local representation of an ISS table
     *
     * @param tableName  Table name
     * @param creator    Table creator
     * @param maxPlayers Maximum number of players
     */
    public void createTable(String tableName, String creator,
                            int maxPlayers) {

        eventBus.post(new CreateTableCommand(JSkatViewType.ISS_TABLE,
                tableName));
        jskat.setActiveTable(JSkatViewType.ISS_TABLE, tableName);
    }

    /**
     * Joins a table on ISS
     *
     * @param tableName Table name
     */
    public void joinTable(String tableName) {
        sendToIss(MessageGenerator.getJoinTableMessage(tableName));
    }

    /**
     * Observes a table on ISS
     *
     * @param tableName Table name
     */
    public void observeTable(String tableName) {
        sendToIss(MessageGenerator.getObserveTableMessage(tableName));
    }

    /**
     * Leaves a table on ISS
     *
     * @param tableName Table name
     */
    public void leaveTable(String tableName) {
        sendToIss(issMsg.getLeaveTableMessage(tableName));
    }

    /**
     * Updates a local representation of an ISS table
     *
     * @param tableName Table name
     * @param status    New table status
     */
    public void updateISSTableState(String tableName,
                                    TablePanelStatus status) {
        view.updateISSTable(tableName, status);
    }

    /**
     * Updates a local representation of an ISS table
     *
     * @param tableName Table name
     * @param status    New game status
     */
    public void updateISSGame(String tableName,
                              GameStartInformation status) {

        view.updateISSTable(tableName, appData.getIssLoginName(),
                status);

        gameData.put(tableName, createSkatGameData(status));
    }

    private static SkatGameData createSkatGameData(GameStartInformation status) {

        SkatGameData result = new SkatGameData();

        result.setGameState(GameState.GAME_START);
        for (Player player : Player.values()) {
            result.setPlayerName(player, status.getPlayerName(player));
        }

        return result;
    }

    /**
     * Starts a game on a local representation of an ISS table
     *
     * @param tableName Table name
     */
    public void startGame(String tableName) {

        view.startGame(tableName);
    }

    /**
     * Updates a move on a local representation of an ISS table
     *
     * @param tableName       Table name
     * @param moveInformation Move information
     */
    public void updateMove(String tableName,
                           MoveInformation moveInformation) {

        SkatGameData currGame = gameData.get(tableName);
        updateGameData(currGame, moveInformation);

        view.updateISSMove(tableName, currGame, moveInformation);

        // TODO (jan 19.11.2010) extract this into separate methods
        if (MoveType.DEAL.equals(moveInformation.getType())) {
            JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.MIDDLEHAND));
        } else if (MoveType.BID.equals(moveInformation.getType())
                || MoveType.HOLD_BID.equals(moveInformation.getType())
                || MoveType.PASS.equals(moveInformation.getType())) {

            if (MoveType.BID.equals(moveInformation.getType())) {
                if (MovePlayer.MIDDLEHAND.equals(moveInformation.getMovePlayer())) {
                    JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.FOREHAND));
                } else if (MovePlayer.REARHAND.equals(moveInformation.getMovePlayer())) {
                    if (!currGame.isPlayerPass(Player.FOREHAND)) {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.FOREHAND));
                    } else {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.MIDDLEHAND));
                    }
                }
            } else if (MoveType.HOLD_BID.equals(moveInformation.getType())) {
                if (MovePlayer.FOREHAND.equals(moveInformation.getMovePlayer())) {
                    if (!currGame.isPlayerPass(Player.MIDDLEHAND)) {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.MIDDLEHAND));
                    } else {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.REARHAND));
                    }
                } else if (MovePlayer.MIDDLEHAND.equals(moveInformation.getMovePlayer())) {
                    JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.REARHAND));
                }
            } else if (MoveType.PASS.equals(moveInformation.getType())) {
                if (MovePlayer.FOREHAND.equals(moveInformation.getMovePlayer())
                        || MovePlayer.MIDDLEHAND.equals(moveInformation.getMovePlayer())) {
                    JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.REARHAND));
                } else if (MovePlayer.REARHAND.equals(moveInformation.getMovePlayer())) {
                    if (!currGame.isPlayerPass(Player.FOREHAND)) {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.FOREHAND));
                    } else {
                        JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.MIDDLEHAND));
                    }
                }
            }

            if (isBiddingFinished(currGame)) {
                JSkatEventBus.INSTANCE.post(new DeclarerChangedEvent(tableName, currGame.getDeclarer()));
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.PICKING_UP_SKAT));
            }

        } else if (MoveType.GAME_ANNOUNCEMENT.equals(moveInformation.getType())) {

            JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, Player.FOREHAND));

        } else if (MoveType.CARD_PLAY.equals(moveInformation.getType())) {

            // handle trick playing
            Trick trick = currGame.getCurrentTrick();

            if (trick.getThirdCard() != null) {

                Player trickWinner = SkatRuleFactory.getSkatRules(currGame.getGameType())
                        .calculateTrickWinner(currGame.getGameType(), trick);
                trick.setTrickWinner(trickWinner);
                currGame.addTrick(new Trick(currGame.getTricks().size(), trick.getTrickWinner()));

                JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, currGame.getCurrentTrick().getForeHand()));

            } else if (trick.getSecondCard() != null) {

                JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, trick.getForeHand().getRightNeighbor()));

            } else if (trick.getFirstCard() != null) {

                JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, trick.getForeHand().getLeftNeighbor()));
            }
        }
    }

    private static boolean isBiddingFinished(SkatGameData currGame) {

        boolean result = false;

        if (currGame.getNumberOfPasses() == 2) {
            for (Player currPlayer : Player.values()) {
                if (!currGame.isPlayerPass(currPlayer)) {
                    if (currGame.getMaxPlayerBid(currPlayer) > 0) {
                        result = true;
                        currGame.setDeclarer(currPlayer);
                    }
                }
            }
        }

        return result;
    }

    private static void updateGameData(SkatGameData currGame,
                                       MoveInformation moveInformation) {

        Player movePlayer = moveInformation.getPlayer();

        switch (moveInformation.getType()) {
            case DEAL:
                currGame.setGameState(GameState.DEALING);
                break;
            case BID:
                currGame.setGameState(GameState.BIDDING);
                currGame.addPlayerBid(movePlayer, moveInformation.getBidValue());
                break;
            case HOLD_BID:
                currGame.setGameState(GameState.BIDDING);
                currGame.addPlayerBid(movePlayer, currGame.getMaxBidValue());
                break;
            case PASS:
                currGame.setGameState(GameState.BIDDING);
                currGame.setPlayerPass(movePlayer, true);
                break;
            case SKAT_REQUEST:
                currGame.setGameState(GameState.DISCARDING);
                break;
            case PICK_UP_SKAT:
                currGame.setGameState(GameState.DISCARDING);
                break;
            case GAME_ANNOUNCEMENT:
                currGame.setGameState(GameState.DECLARING);
                currGame.setAnnouncement(moveInformation.getGameAnnouncement());
                currGame.addTrick(new Trick(0, Player.FOREHAND));
                break;
            case CARD_PLAY:
                currGame.setGameState(GameState.TRICK_PLAYING);
                currGame.addTrickCard(moveInformation.getCard());
                break;
            case RESIGN:
            case TIME_OUT:
                currGame.setGameState(GameState.PRELIMINARY_GAME_END);
                break;
        }
    }

    /**
     * Handles end of a game
     *
     * @param tableName   Table name
     * @param newGameData Game data
     */
    public void endGame(String tableName, SkatGameData newGameData) {

        JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.GAME_OVER));
        // FIXME: merge event and command
        eventBus.post(new TableGameMoveEvent(tableName, new GameFinishEvent(newGameData.getGameSummary())));
        eventBus.post(new ShowCardsCommand(tableName, newGameData.getCardsAfterDiscard(), newGameData.getSkat()));
        gameData.put(tableName, newGameData);
    }

    /**
     * Shows a message from ISS.
     *
     * @param message Message
     */
    public void showMessage(String message) {
        view.showMessage(strings.getString("iss_message"), message);
    }

    /**
     * Shows an error message from ISS.
     *
     * @param message Message
     */
    public void showErrorMessage(String message) {
        view.showErrorMessage(
                strings.getString("iss_message"), message);
    }

    /**
     * Invites a player on ISS to play at a table on ISS.
     *
     * @param tableName Table name
     * @param invitee   Invited player
     */
    public void invitePlayer(String tableName, String invitee) {
        sendToIss(issMsg.getInvitePlayerMessage(tableName, invitee));
    }

    /**
     * Sends ready to play signal to ISS
     *
     * @param command ISS ready to play command
     */
    @Subscribe
    public void sendReadyToPlayOn(IssReadyToPlayCommand command) {

        sendToIss(issMsg.getReadyMessage(appData.getActiveTable()));
    }

    /**
     * Send talk enabled signal to ISS
     *
     * @param command ISS toggle talk enabled command
     */
    @Subscribe
    public void sendToggleTalkEnabledOn(
            IssToggleTalkEnabledCommand command) {

        sendToIss(issMsg.getTalkEnabledMessage(appData
                .getActiveTable()));
    }

    /**
     * Sends a resign signal to ISS
     *
     * @param command ISS resign command
     */
    @Subscribe
    public void sendResignOn(IssResignCommand command) {

        sendToIss(issMsg.getResignMessage(appData.getActiveTable()));
    }

    /**
     * Sends a show cards signal to ISS
     *
     * @param command ISS show cards command
     */
    @Subscribe
    public void sendShowCardsOn(IssShowCardsCommand command) {

        sendToIss(issMsg
                .getShowCardsMessage(appData.getActiveTable()));
    }

    /**
     * Send table seat change singal to ISS
     *
     * @param command ISS table seat change command
     */
    @Subscribe
    public void sendTableSeatChangeOn(IssTableSeatChangeCommand command) {
        sendToIss(issMsg.getTableSeatChangeMessage(appData
                .getActiveTable()));
    }

    /**
     * Send pass bid move to ISS
     *
     * @param tableName Table name
     */
    public void sendPassBidMove(String tableName) {
        sendToIss(issMsg.getPassMoveMessage(tableName));
    }

    /**
     * Send hold bid move to ISS
     *
     * @param tableName Table name
     */
    public void sendHoldBidMove(String tableName) {
        sendToIss(issMsg.getHoldBidMoveMessage(tableName));
    }

    /**
     * Send bid move to ISS
     *
     * @param tableName Table name
     */
    public void sendBidMove(String tableName) {
        sendToIss(issMsg
                .getBidMoveMessage(
                        tableName,
                        SkatConstants.getNextBidValue(gameData.get(
                                tableName).getMaxBidValue())));
    }

    /**
     * Send pick up skat move to ISS
     *
     * @param tableName Table name
     */
    public void sendPickUpSkatMove(String tableName) {
        sendToIss(issMsg.getPickUpSkatMoveMessage(tableName));
    }

    /**
     * Send game announcement to ISS
     *
     * @param tableName        Table name
     * @param gameAnnouncement Game announcement
     */
    public void sendGameAnnouncementMove(String tableName,
                                         GameAnnouncement gameAnnouncement) {
        sendToIss(issMsg.getGameAnnouncementMoveMessage(tableName,
                gameAnnouncement));
    }

    /**
     * Send card move to ISS
     *
     * @param tableName Table name
     * @param nextCard  Card
     */
    public void sendCardMove(String tableName, Card nextCard) {
        sendToIss(issMsg.getCardMoveMessage(tableName, nextCard));
    }

    /**
     * Handle an invitation from another player
     *
     * @param invitor          Invitor
     * @param tableName        Table name
     * @param invitationTicket Invitation ticket
     */
    public void handleInvitation(String invitor, String tableName,
                                 String invitationTicket) {
        if (view.showISSTableInvitation(invitor, tableName)) {
            sendToIss(MessageGenerator.getInvitationAcceptedMessage(tableName,
                    invitationTicket));
        }
    }

    /**
     * Updates a chat message on an ISS table
     *
     * @param tableName Table name
     * @param message   Chat message
     */
    public void updateISSTableChatMessage(String tableName,
                                          ChatMessage message) {
        // FIXME (jan 30.01.2011) tableName not needed here?
        view.appendISSChatMessage(ChatMessageType.TABLE, message);
    }

    /**
     * Checks whether a table is joined or not
     *
     * @param tableName Table name
     * @return TRUE iff the table is joined
     */
    public boolean isTableJoined(String tableName) {

        return appData.isTableJoined(tableName);
    }
}
