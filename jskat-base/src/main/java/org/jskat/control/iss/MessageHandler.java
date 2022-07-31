package org.jskat.control.iss;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.iss.IssDisconnectedEvent;
import org.jskat.control.event.table.TableRemovedEvent;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.MoveInformation;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Handles messages from ISS
 */
public class MessageHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private StreamConnector connect;
    private final IssController issControl;

    private final JSkatResourceBundle strings;

    private final List<String> messageList;

    private final JSkatEventBus eventBus = JSkatEventBus.INSTANCE;

    private final static int protocolVersion = 14;

    /**
     * Constructor
     *
     * @param conn       Connection to ISS
     * @param controller ISS controller for JSkat
     */
    public MessageHandler(final StreamConnector conn,
                          final IssController controller) {

        this.connect = conn;
        this.issControl = controller;

        this.strings = JSkatResourceBundle.INSTANCE;

        this.messageList = new ArrayList<String>();
    }

    public MessageHandler(final IssController controller) {
        this.issControl = controller;
        this.strings = JSkatResourceBundle.INSTANCE;

        this.messageList = new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            if (this.messageList.size() > 0) {

                final String message = getNextMessage();
                handleMessage(message);
            } else {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    log.warn("Thread.sleep() was interrupted");
                }
            }
        }
    }

    synchronized void addMessage(final String newMessage) {

        this.messageList.add(newMessage);
    }

    private synchronized String getNextMessage() {

        return this.messageList.remove(0);
    }

    void handleMessage(final String message) {

        if (message == null) {
            this.eventBus.post(new IssDisconnectedEvent());
        } else {

            final StringTokenizer tokenizer = new StringTokenizer(message); // get
            // first
            // command
            final String first = tokenizer.nextToken();
            // get all parameters
            final List<String> params = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                params.add(tokenizer.nextToken());
            }

            try {

                handleMessage(first, params);

            } catch (final Exception except) {
                log.error("Error in parsing ISS protocoll", except);
                this.issControl.showErrorMessage(this.strings
                        .getString("iss_error_parsing_iss_protocol"));
            }
        }
    }

    void handleMessage(final String first, final List<String> params) {

        final MessageType type = MessageType.getByString(first);

        if (MessageType.UNKNOWN.equals(type)) {

            log.error("UNHANDLED MESSAGE: " + first + params.toString());
        }
    } else

    {
        // FIXME (jansch 30.05.2011) put message into a queue
        try {
            handleMessageObsolete(type, params);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

    void handleMessageObsolete(final MessageType type, final List<String> params)
            throws Exception {

        switch (type) {
            case PASSWORD:
                handlePasswordMessage();
                break;
            case WELCOME:
                handleWelcomeMessage(params);
                break;
            case VERSION:
                handleVersionMessage(params);
                break;
            case CLIENTS:
                handleClientListMessage(params);
                break;
            case TABLES:
                handleTableListMessage(params);
                break;
            case CREATE:
                handleTableCreateMessage(params);
                break;
            case INVITE:
                handleTableInvitationMessage(params);
                break;
            case TABLE:
                handleTableUpdateMessage(params);
                break;
            case DESTROY:
                handleTableDestroyMessage(params);
                break;
            case ERROR:
                handleErrorMessage(params);
                break;
            case TEXT:
                handleTextMessage(params);
                break;
            case YELL:
                handleLobbyChatMessage(params);
                break;
        }
    }

    void handleLobbyChatMessage(final List<String> params) {

        this.issControl.addChatMessage(ChatMessageType.LOBBY, params);
    }

    void handlePasswordMessage() {
        this.issControl.sendPassword();
    }

    void handleTextMessage(final List<String> params) {
        // FIXME show it to the user
        log.error(params.toString());
    }

    void handleErrorMessage(final List<String> params) {

        log.error(params.toString());
        this.issControl.showErrorMessage(getI18ErrorString(getErrorString(params)));
    }

    private String getErrorString(final List<String> params) {

        for (final String param : params) {
            if (param.startsWith("_")) {
                return param;
            }
        }

        return params.toString();
    }

    private String getI18ErrorString(final String errorString) {

        if ("_id_pw_mismatch".equals(errorString)) {
            return this.strings.getString("iss_login_password_wrong");
        } else if ("_not_your_turn".equals(errorString)) {
            return this.strings.getString("iss_not_your_turn");
        } else if ("_invalid_move_colon".equals(errorString)) {
            return this.strings.getString("iss_invalid_move_colon");
        }

        return errorString;
    }

    void handleTableCreateMessage(final List<String> params) {

        log.debug("table creation message");

        final String tableName = params.get(0);
        final String creator = params.get(1);
        final int seats = Integer.parseInt(params.get(2));
        this.issControl.createTable(tableName, creator, seats);
    }

    void handleTableDestroyMessage(final List<String> params) {

        log.debug("table destroy message");

        final String tableName = params.get(0);

        eventBus.post(new TableRemovedEvent(tableName, JSkatViewType.ISS_TABLE));
    }

    void handleTableInvitationMessage(final List<String> params) {
        log.debug("table destroy message");

        final String invitor = params.get(0);
        final String tableName = params.get(1);
        final String invitationTicket = params.get(2);

        this.issControl.handleInvitation(invitor, tableName, invitationTicket);
    }

    /**
     * table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0
     * 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
     */
    void handleTableUpdateMessage(final List<String> params) {

        log.debug("table update message");

        final String tableName = params.get(0);

        if (this.issControl.isTableJoined(tableName)) {

            // FIXME (jan 18.11.2010) is this the name of the creator or the
            // login name of the current player?
            final String creator = params.get(1);
            final String actionCommand = params.get(2);
            final List<String> detailParams = params.subList(3, params.size());

            if (actionCommand.equals("error")) {

                handleErrorMessage(params.subList(3, params.size()));

            } else if (actionCommand.equals("state")) {

                this.issControl.updateISSTableState(tableName,
                        MessageParser.getTableStatus(creator, detailParams));

            } else if (actionCommand.equals("start")) {

                this.issControl
                        .updateISSGame(tableName, MessageParser
                                .getGameStartStatus(creator, detailParams));

            } else if (actionCommand.equals("go")) {

                this.issControl.startGame(tableName);

            } else if (actionCommand.equals("play")) {

                final MoveInformation moveInfo = MessageParser
                        .getMoveInformation(detailParams);
                MessageParser.parsePlayerTimes(detailParams, moveInfo);
                this.issControl.updateMove(tableName, moveInfo);

            } else if (actionCommand.equals("tell")) {

                this.issControl.updateISSTableChatMessage(tableName, MessageParser
                        .getTableChatMessage(tableName, detailParams));

            } else if (actionCommand.equals("end")) {

                this.issControl.endGame(tableName, getGameInformation(detailParams));

            } else {

                log.debug("unhandled action command: " + actionCommand + " for table " + tableName);  
            }
        }
    }

    private SkatGameData getGameInformation(final List<String> params) {

        // first glue alle params back together
        final String gameResult = glueParams(params);

        return MessageParser.parseGameSummary(gameResult);
    }

    private String glueParams(final List<String> params) {

        String result = "";
        final Iterator<String> paramIterator = params.iterator();

        while (paramIterator.hasNext()) {

            if (result.length() > 0) {
                result += " ";
            }

            result += paramIterator.next();
        }

        return result;
    }

    /**
     * Handles a client list message
     *
     * @param params parameters
     */
    void handleClientListMessage(final List<String> params) {

        final String plusMinus = params.remove(0);

        if (plusMinus.equals("+")) {

            updateClientList(params);

        } else if (plusMinus.equals("-")) {

            removeClientFromList(params);
        }
    }

    /**
     * Adds or updates a client on the client list
     *
     * @param params Player information
     */
    void updateClientList(final List<String> params) {

        final String playerName = params.get(0);
        final String language = params.get(2);
        final long gamesPlayed = Long.parseLong(params.get(3));
        final double strength = Double.parseDouble(params.get(4));

        this.issControl.updateISSPlayerList(playerName, language, gamesPlayed,
                strength);
    }

    /**
     * Removes a client from the client list
     *
     * @param params Player information
     */
    void removeClientFromList(final List<String> params) {

        this.issControl.removeISSPlayerFromList(params.get(0));
    }

    /**
     * Handles the welcome message and checks the protocol version
     *
     * @param params Welcome information
     */
    void handleWelcomeMessage(final List<String> params) {

        final String login = params.get(0);
        final double issProtocolVersion = Double.parseDouble(params.get(params
                .size() - 1));

        log.debug("ISS version: " + issProtocolVersion);
        log.debug("local version: " + protocolVersion);

        if ((int) issProtocolVersion != protocolVersion) {
            // TODO handle this in JSkatMaster
            log.error("Wrong protocol version!!!");
            log.error("iss version: " + issProtocolVersion);
            log.error("local version: " + protocolVersion);
        }

        eventBus.post(new IssConnectedEvent(login));
    }

    /**
     * Handles the version message and checks the protocol version
     *
     * @param params Welcome information
     */
    void handleVersionMessage(final List<String> params) {
        log.debug("ISS version: " + params.get(0));
    }

    /**
     * Handles a table list message
     *
     * @param params Table information
     */
    void handleTableListMessage(final List<String> params) {

        final String plusMinus = params.remove(0);

        if (plusMinus.equals("+")) {

            updateTableList(params);

        } else if (plusMinus.equals("-")) {

            removeTableFromList(params);
        }
    }

    /**
     * Adds or updates a table on the table list
     *
     * @param params Table information
     */
    void updateTableList(final List<String> params) {

        final String tableName = params.get(0);
        final int maxPlayers = Integer.parseInt(params.get(1));
        final long gamesPlayed = Long.parseLong(params.get(2));
        final String player1 = params.get(3);
        final String player2 = params.get(4);
        final String player3 = params.get(5);

        this.issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed,
                player1, player2, player3);
    }

    /**
     * Removes a table from the table list
     *
     * @param params Table information
     */
    void removeTableFromList(final List<String> params) {

        this.issControl.removeISSTableFromList(params.get(0));
    }

}
