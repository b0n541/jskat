package org.jskat.control.iss;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.data.iss.*;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses ISS messages
 */
public class MessageParser {

    private static final Logger log = LoggerFactory.getLogger(MessageParser.class);

    /**
     * table .1 bar state --> was cut away before <br>
     * --> params: <br>
     * 3 <br>
     * bar xskat xskat:2 . <br>
     * bar . 0 0 0 0 0 0 1 0 <br>
     * xskat $ 0 0 0 0 0 0 1 1 <br>
     * xskat:2 $ 0 0 0 0 0 0 1 1 <br>
     * . . 0 0 0 0 0 0 0 0 false <br>
     */
    static TablePanelStatus getTableStatus(final String loginName,
                                           final List<String> params) {

        final TablePanelStatus status = new TablePanelStatus();

        status.setLoginName(loginName);

        status.setMaxPlayers(Integer.parseInt(params.get(0)));

        // get player status
        for (int i = 0; i < status.getMaxPlayers(); i++) {
            // parse only non empty seats
            if (!".".equals(params.get(i * 10 + 5))) {
                // there is player information
                final PlayerStatus playerStatus = parsePlayerStatus(params
                        .subList(i * 10 + 5, i * 10 + 16));
                // has player left already
                if (".".equals(params.get(i + 1))) {
                    playerStatus.setPlayerLeft(true);
                }
                status.addPlayer(playerStatus.getName(), playerStatus);
            }
        }

        return status;
    }

    /**
     * table .1 bar state <br>
     * 3 <br>
     * bar xskat xskat:2 . <br>
     * bar . 0 0 0 0 0 0 1 0 <br>
     * --> params:<br>
     * xskat $ 0 0 0 0 0 0 1 1 <br>
     * xskat:2 $ 0 0 0 0 0 0 1 1 <br>
     * . . 0 0 0 0 0 0 0 0 false <br>
     */
    private static PlayerStatus parsePlayerStatus(final List<String> params) {

        final PlayerStatus status = new PlayerStatus();

        status.setName(params.get(0));
        status.setIP(params.get(1));
        status.setGamesPlayed(Integer.parseInt(params.get(2)));
        status.setGamesWon(Integer.parseInt(params.get(3)));
        status.setLastGameResult(Integer.parseInt(params.get(4)));
        status.setTotalPoints(Integer.parseInt(params.get(5)));
        status.setSwitch34(Integer.parseInt(params.get(6)) == 1);
        // ignore next information, unknown purpose
        status.setTalkEnabled(Integer.parseInt(params.get(8)) == 1);
        status.setReadyToPlay(Integer.parseInt(params.get(9)) == 1);

        return status;
    }

    static GameStartInformation getGameStartStatus(final String loginName,
                                                   final List<String> params) {

        log.debug("game start parameter: " + params);

        final GameStartInformation status = new GameStartInformation(
                loginName,
                Integer.parseInt(params.get(0)),
                Map.of(Player.FOREHAND, params.get(1),
                        Player.MIDDLEHAND, params.get(3),
                        Player.REARHAND, params.get(5)),
                Map.of(Player.FOREHAND, Double.valueOf(params.get(2)),
                        Player.MIDDLEHAND, Double.valueOf(params.get(4)),
                        Player.REARHAND, Double.valueOf(params.get(6))));

        return status;
    }

    static MoveInformation getMoveInformation(final List<String> params) {

        final MoveInformation info = new MoveInformation();

        getMovePlayer(params.get(0), info);

        // FIXME Unhandled moves
        final String move = params.get(1);
        log.debug("Move: " + move);
        if ("y".equals(move)) {
            // holding bid move
            info.setType(MoveType.HOLD_BID);
        } else if ("p".equals(move)) {
            // pass move
            info.setType(MoveType.PASS);
        } else if ("s".equals(move)) {
            // skat request move
            info.setType(MoveType.SKAT_REQUEST);
        } else if ("??.??".equals(move)) {
            // hidden skat given to a player
            info.setType(MoveType.PICK_UP_SKAT);
        } else if (move.startsWith("TI.")) {
            // time out for player
            info.setType(MoveType.TIME_OUT);
            info.setTimeOutPlayer(parseTimeOut(move));
        } else if (move.equals("RE")) {
            // resigning of player
            info.setType(MoveType.RESIGN);
        } else if (move.startsWith("SC")) {
            // declarer shows cards
            info.setType(MoveType.SHOW_CARDS);
            if (move.length() > 2) {
                // declarer cards follow, SC could also stand alone
                info.setRevealedCards(parseSkatCards(move.substring(move.indexOf(".") + 1)));
            }
        } else if (move.startsWith("LE.")) {
            // one player left the table during the game
            info.setType(MoveType.LEAVE_TABLE);
            info.setLeavingPlayer(parseLeaveTable(move));
        } else {
            // extensive parsing needed

            // test card move
            final Card card = Card.getCardFromString(move);
            if (card != null) {
                // card play move
                info.setType(MoveType.CARD_PLAY);
                info.setCard(card);
            } else {
                // card parsing failed

                // test bidding
                int bid = -1;
                try {

                    bid = Integer.parseInt(move);

                } catch (final NumberFormatException e) {

                    bid = -1;
                }
                if (bid > -1) {
                    // bidding
                    info.setType(MoveType.BID);
                    info.setBidValue(Integer.parseInt(move));
                } else {

                    if (move.length() == 95) {
                        // card dealing
                        info.setType(MoveType.DEAL);
                        info.setDealCards(parseCardDeal(move));
                    } else if (move.length() == 5) {
                        // open skat given to a player
                        info.setType(MoveType.PICK_UP_SKAT);
                        info.setSkat(parseSkatCards(move));
                    } else {
                        // game announcement
                        info.setType(MoveType.GAME_ANNOUNCEMENT);
                        parseGameAnnouncement(info, move);
                    }
                }
            }
        }

        return info;
    }

    static void parsePlayerTimes(final List<String> params,
                                 final MoveInformation info) {
        // parse player times
        info.putPlayerTime(Player.FOREHAND, Double.valueOf(params.get(params.size() - 3)));
        info.putPlayerTime(Player.MIDDLEHAND, Double.valueOf(params.get(params.size() - 2)));
        info.putPlayerTime(Player.REARHAND, Double.valueOf(params.get(params.size() - 1)));
    }

    private static CardList parseSkatCards(final String move) {

        final StringTokenizer token = new StringTokenizer(move, ".");
        final CardList result = new CardList();

        while (token.hasMoreTokens()) {
            result.add(Card.getCardFromString(token.nextToken()));
        }

        return result;
    }

    private static void getMovePlayer(final String movePlayer,
                                      final MoveInformation info) {

        log.debug("Move player: " + movePlayer);
        if ("w".equals(movePlayer)) {
            // world move
            info.setMovePlayer(MovePlayer.WORLD);
        } else if ("0".equals(movePlayer)) {
            // fore hand move
            info.setMovePlayer(MovePlayer.FOREHAND);
        } else if ("1".equals(movePlayer)) {
            // middle hand move
            info.setMovePlayer(MovePlayer.MIDDLEHAND);
        } else if ("2".equals(movePlayer)) {
            // rear hand move
            info.setMovePlayer(MovePlayer.REARHAND);
        }
    }

    /**
     * <game-type> :: (G | C | S | H | D | N) (type Grand .. Null) [O] (ouvert)
     * [H] (hand, not given if O + trump game) [S] (schneider announced, only in
     * H games, not if O or Z) [Z] (schwarz announced, only in H games)
     */
    private static void parseGameAnnouncement(final MoveInformation info, final String move) {

        final StringTokenizer annToken = new StringTokenizer(move, ".");
        final String gameTypeString = annToken.nextToken();

        GameType gameType = null;
        if (gameTypeString.startsWith("G")) {

            gameType = GameType.GRAND;

        } else if (gameTypeString.startsWith("C")) {

            gameType = GameType.CLUBS;

        } else if (gameTypeString.startsWith("S")) {

            gameType = GameType.SPADES;

        } else if (gameTypeString.startsWith("H")) {

            gameType = GameType.HEARTS;

        } else if (gameTypeString.startsWith("D")) {

            gameType = GameType.DIAMONDS;

        } else if (gameTypeString.startsWith("N")) {

            gameType = GameType.NULL;
        }

        boolean hand = false;
        boolean schneider = false;
        boolean schwarz = false;
        boolean ouvert = false;

        // parse other game modifiers
        for (int i = 1; i < gameTypeString.length(); i++) {
            switch (gameTypeString.charAt(i)) {
                case 'O' -> {
                    ouvert = true;
                    // FIXME this should be moved to the GameContract record initialization
                    if (GameType.GRAND_SUIT.contains(gameType)) {
                        hand = true;
                        schneider = true;
                        schwarz = true;
                    }
                }
                case 'H' -> hand = true;
                case 'S' -> schneider = true;
                case 'Z' -> {
                    schwarz = true;
                    // FIXME this should be moved to the GameContract record initialization
                    schneider = true;
                }
                default -> log.warn("Unknown game modifier.");
            }
        }

        final CardList discardedCards = new CardList();
        final CardList ouvertCards = new CardList();

        if (annToken.hasMoreTokens()) {
            final CardList cards = new CardList();

            while (annToken.hasMoreTokens()) {
                cards.add(Card.getCardFromString(annToken.nextToken()));
            }

            if (hand) {
                ouvertCards.addAll(cards);
            } else if (cards.size() == 2) {
                discardedCards.addAll(cards);
            } else {
                discardedCards.add(cards.get(0));
                discardedCards.add(cards.get(1));

                for (int i = 2; i < cards.size(); i++) {
                    ouvertCards.add(cards.get(i));
                }
            }
        }

        info.setGameAnnouncement(
                new GameAnnouncement(
                        new GameContract(
                                gameType,
                                hand,
                                schneider,
                                schwarz,
                                ouvert,
                                ouvertCards),
                        discardedCards));
    }

    private static List<CardList> parseCardDeal(final String move) {

        if (move.contains("|") && move.contains("??")) {
            return parseCardDealFromISSMessage(move);
        }

        return parseCardDealFromSummary(move);
    }

    /**
     * During playing on ISS the deal is send in the following format<br />
     * ??.??.??.??.??.??.??.??.??.??|D9.S9.ST.S8.C9.DT.DQ.CJ.SA.HA|??.??.??.??.?
     * ?.??.??.??.??.??|??.??<br />
     * ?? - hidden card<br />
     * fore hand cards|middle hand cards|rear hand cards|skat
     */
    private static List<CardList> parseCardDealFromISSMessage(final String move) {

        final StringTokenizer handTokens = new StringTokenizer(move, "|");
        final List<CardList> result = new ArrayList<CardList>();

        while (handTokens.hasMoreTokens()) {
            result.add(parseHand(handTokens.nextToken()));
        }

        return result;
    }

    /**
     * In game summary the deal is send in a different format<br />
     * CQ.H7.C8.C9.CT.SQ.DK.H8.H9.SA.S9.HK.HQ.DJ.CJ.S8.DT.HT.ST.D7.CK.S7.C7.DQ.
     * SJ.HA.CA.DA.D8.D9.SK.HJ<br />
     * no hidden cards<br />
     * first 10 cards fore hand<br />
     * next 10 cards middle hand<br />
     * next 10 cards rear hand<br />
     * last 2 cards skat
     *
     * @param move
     */
    private static List<CardList> parseCardDealFromSummary(final String move) {
        final List<CardList> result = new ArrayList<CardList>();

        // fore hand
        result.add(parseHand(move.substring(0, 29)));
        // middle hand
        result.add(parseHand(move.substring(30, 59)));
        // rear hand
        result.add(parseHand(move.substring(60, 89)));
        // skat
        result.add(parseHand(move.substring(90)));

        return result;
    }

    private static CardList parseHand(final String hand) {

        final StringTokenizer cardTokens = new StringTokenizer(hand, ".");
        final CardList result = new CardList();

        while (cardTokens.hasMoreTokens()) {
            result.add(Card.getCardFromString(cardTokens.nextToken()));
        }

        return result;
    }

    /**
     * TI.0
     */
    private static Player parseTimeOut(final String timeOut) {

        return extractPlayer(timeOut);
    }

    /**
     * LE.0
     */
    private static Player parseLeaveTable(final String leaveTable) {

        return extractPlayer(leaveTable);
    }

    private static Player extractPlayer(final String playerAtLastCharacter) {
        Player result = null;

        switch (playerAtLastCharacter.charAt(3)) {
            case '0':
                result = Player.FOREHAND;
                break;
            case '1':
                result = Player.MIDDLEHAND;
                break;
            case '2':
                result = Player.REARHAND;
                break;
        }

        return result;
    }

    static SkatGameData parseGameSummary(final String gameSummary) {

        final SkatGameData result = new SkatGameData();

        final Pattern summaryPartPattern = Pattern.compile("(\\w+)\\[(.*?)\\]");
        final Matcher summaryPartMatcher = summaryPartPattern.matcher(gameSummary);

        while (summaryPartMatcher.find()) {

            log.debug(summaryPartMatcher.group());

            final String summaryPartMarker = summaryPartMatcher.group(1);
            final String summeryPart = summaryPartMatcher.group(2);

            parseSummaryPart(result, summaryPartMarker, summeryPart);
        }

        return result;
    }

    private static void parseSummaryPart(final SkatGameData result,
                                         final String summaryPartMarker, final String summaryPart) {

        if ("P0".equals(summaryPartMarker)) {

            result.setPlayerName(Player.FOREHAND, summaryPart);

        } else if ("P1".equals(summaryPartMarker)) {

            result.setPlayerName(Player.MIDDLEHAND, summaryPart);

        } else if ("P2".equals(summaryPartMarker)) {

            result.setPlayerName(Player.REARHAND, summaryPart);

        } else if ("MV".equals(summaryPartMarker)) {

            parseMoves(result, summaryPart);

        } else if ("R".equals(summaryPartMarker)) {

            parseGameResult(result, summaryPart);
        }
    }

    private static void parseMoves(final SkatGameData result,
                                   final String summaryPart) {

        final StringTokenizer token = new StringTokenizer(summaryPart);

        while (token.hasMoreTokens()) {

            final List<String> moveToken = new ArrayList<>();
            moveToken.add(token.nextToken());
            moveToken.add(token.nextToken());

            final MoveInformation moveInfo = getMoveInformation(moveToken);

            switch (moveInfo.getType()) {
                case DEAL:
                    for (final Player player : Player.values()) {
                        result.addDealtCards(player, moveInfo.getCards(player));
                    }
                    result.setDealtSkatCards(moveInfo.getSkat());
                    break;
                case BID:
                    result.addPlayerBid(moveInfo.getPlayer(), moveInfo.getBidValue());
                    break;
                case HOLD_BID:
                    result.addPlayerBid(moveInfo.getPlayer(), result.getMaxBidValue());
                    break;
                case PASS:
                    result.setPlayerPass(moveInfo.getPlayer(), true);
                    break;
                case SKAT_REQUEST:
                    result.setDeclarer(moveInfo.getPlayer());
                    break;
                case PICK_UP_SKAT:
                    result.setDealtSkatCards(moveInfo.getSkat());
                    result.addPlayerCard(result.getDeclarer(), moveInfo.getSkat().get(0));
                    result.addPlayerCard(result.getDeclarer(), moveInfo.getSkat().get(1));
                    break;
                case GAME_ANNOUNCEMENT:
                    result.setDeclarer(moveInfo.getPlayer());
                    if (!moveInfo.getGameAnnouncement().isHand() && !moveInfo.getGameAnnouncement().getDiscardedCards().isEmpty()) {
                        result.removePlayerCard(moveInfo.getPlayer(), moveInfo.getGameAnnouncement().getDiscardedCards().get(0));
                        result.removePlayerCard(moveInfo.getPlayer(), moveInfo.getGameAnnouncement().getDiscardedCards().get(1));
                        result.setDiscardedSkat(moveInfo.getPlayer(), moveInfo.getGameAnnouncement().getDiscardedCards());
                    }
                    result.setAnnouncement(moveInfo.getGameAnnouncement());
                    break;
                case CARD_PLAY:
                    if (result.getTricks().size() == 0) {
                        // no tricks played so far
                        result.addTrick(new Trick(0, Player.FOREHAND));
                    } else if (result.getCurrentTrick().getThirdCard() != null) {
                        // last card of trick is played
                        // set trick winner
                        result.getCurrentTrick().setTrickWinner(moveInfo.getPlayer());
                        // create next trick
                        result.addTrick(new Trick(result.getTricks().size(), moveInfo.getPlayer()));
                    }
                    result.addTrickCard(moveInfo.getCard());

                    if (result.getTricks().size() == 10 && result.getCurrentTrick().getThirdCard() != null) {
                        // set the trick winner of the last trick
                        final SkatRule skatRules = SkatRuleFactory.getSkatRules(result.getGameType());
                        result.setTrickWinner(9, skatRules.calculateTrickWinner(
                                result.getGameType(), result.getCurrentTrick()));
                    }
                    break;
            }
        }

        if (result.getMaxBidValue() == 0) {
            final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
            factory.setGameType(GameType.PASSED_IN);
            result.setAnnouncement(factory.getAnnouncement());
        }
    }

    private static void parseGameResult(final SkatGameData result,
                                        final String summaryPart) {

        final StringTokenizer token = new StringTokenizer(summaryPart);

        while (token.hasMoreTokens()) {

            parseResultToken(result, token.nextToken());
        }
    }

    private static void parseResultToken(final SkatGameData gameData,
                                         final String token) {

        // from ISS source code
        // return "d:"+declarer + (penalty ? " penalty" : (declValue > 0 ? "
        // win" : " loss"))
        // + " v:" + declValue
        // + " m:" + matadors + (overbid ? " overbid" : " bidok")
        // + " p:" + declCardPoints + " t:" + declTricks
        // + " s:" + (schneider ? '1' : '0') + " z:" + (schwarz ? '1' :
        // '0')
        // + " p0:" + penalty0 + " p1:" + penalty1 + " p2:" + penalty2
        // + " l:" + this.left + " to:" + this.timeout + " r:" + (resigned
        // ? '1' : '0');

        // TODO: or simply "passed"

        if (token.startsWith("d:")) {

            parseDeclarerToken(gameData, token);

        } else if ("penalty".equals(token)) {

            // FIXME (jan 07.12.2010) handle this token

        } else if ("loss".equals(token)) {

            gameData.getResult().setWon(false);

        } else if ("win".equals(token)) {

            gameData.getResult().setWon(true);

        } else if (token.startsWith("v:")) {

            gameData.getResult().setGameValue(Integer.parseInt(token.substring(2)));

        } else if (token.startsWith("m:")) {

            final int matadors = Integer.parseInt(token.substring(2));

            gameData.getGameResult().setPlayWithJacks(matadors > 0);
            gameData.getGameResult().setMatadors(Math.abs(matadors));

        } else if ("overbid".equals(token)) {

            gameData.getResult().setOverBid(true);

        } else if (token.startsWith("p:")) {

            int declarerPoints = 0;
            if (GameType.NULL != gameData.getAnnouncement().contract().gameType() || gameData.isGameLost()) {
                declarerPoints = Integer.parseInt(token.substring(2));
            }
            gameData.setDeclarerScore(declarerPoints);
            gameData.getResult().setFinalDeclarerPoints(declarerPoints);
            gameData.getResult().setFinalOpponentPoints(120 - declarerPoints);

        } else if (token.startsWith("t:")) {

        } else if ("s:1".equals(token)) {

            gameData.getResult().setSchneider(true);

        } else if ("z:1".equals(token)) {

            gameData.getResult().setSchwarz(true);
        }
    }

    private static void parseDeclarerToken(final SkatGameData result,
                                           final String token) {

        if ("d:0".equals(token)) {
            result.setDeclarer(Player.FOREHAND);
        } else if ("d:1".equals(token)) {
            result.setDeclarer(Player.MIDDLEHAND);
        } else if ("d:2".equals(token)) {
            result.setDeclarer(Player.REARHAND);
        }
    }

    /**
     * table .5 foo tell foo asdf jklö<br>
     * <br>
     * table .5 foo tell --> was cut before<br>
     * params: foo -> first is talker<br>
     * remainings is the chat message<br>
     * asdf jklö
     */
    static ChatMessage getTableChatMessage(final String tableName,
                                           final List<String> detailParams) {

        final StringBuffer text = new StringBuffer();

        text.append(detailParams.get(0)).append(": ");
        for (int i = 1; i < detailParams.size(); i++) {
            text.append(detailParams.get(i)).append(' ');
        }

        return new ChatMessage(tableName, text.toString());
    }
}
