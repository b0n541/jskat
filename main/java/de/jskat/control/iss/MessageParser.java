/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package de.jskat.control.iss;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStartInformation;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSPlayerStatus;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.data.iss.MovePlayer;
import de.jskat.data.iss.MoveType;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Parses ISS messages
 */
public class MessageParser {

	static Log log = LogFactory.getLog(MessageParser.class);

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
	static ISSTablePanelStatus getTableStatus(String loginName, List<String> params) {

		ISSTablePanelStatus status = new ISSTablePanelStatus();

		status.setLoginName(loginName);

		status.setMaxPlayers(Integer.parseInt(params.get(0)));

		// get player status
		for (int i = 0; i < status.getMaxPlayers(); i++) {
			// parse only non empty seats
			if (!(".".equals(params.get(i * 10 + 5)))) { //$NON-NLS-1$
				// there is player information
				ISSPlayerStatus playerStatus = parsePlayerStatus(params.subList(i * 10 + 5, i * 10 + 16));
				// has player left already
				if (".".equals(params.get(i + 1))) { //$NON-NLS-1$
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
	private static ISSPlayerStatus parsePlayerStatus(List<String> params) {

		ISSPlayerStatus status = new ISSPlayerStatus();

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

	static ISSGameStartInformation getGameStartStatus(String loginName, List<String> params) {

		log.debug("game start parameter: " + params); //$NON-NLS-1$

		ISSGameStartInformation status = new ISSGameStartInformation();

		status.setLoginName(loginName);

		status.setGameNo(Integer.parseInt(params.get(0)));
		status.putPlayerName(Player.FORE_HAND, params.get(1));
		status.putPlayerTime(Player.FORE_HAND, Double.valueOf(params.get(2)));
		status.putPlayerName(Player.MIDDLE_HAND, params.get(3));
		status.putPlayerTime(Player.MIDDLE_HAND, Double.valueOf(params.get(4)));
		status.putPlayerName(Player.HIND_HAND, params.get(5));
		status.putPlayerTime(Player.HIND_HAND, Double.valueOf(params.get(6)));

		return status;
	}

	static ISSMoveInformation getMoveInformation(List<String> params) {

		ISSMoveInformation info = new ISSMoveInformation();

		getMovePlayer(params.get(0), info);

		// FIXME Unhandled moves
		String move = params.get(1);
		log.debug("Move: " + move); //$NON-NLS-1$
		if ("y".equals(move)) { //$NON-NLS-1$
			// holding bid move
			info.setType(MoveType.HOLD_BID);
		} else if ("p".equals(move)) { //$NON-NLS-1$
			// pass move
			info.setType(MoveType.PASS);
		} else if ("s".equals(move)) { //$NON-NLS-1$
			// skat request move
			info.setType(MoveType.SKAT_REQUEST);
		} else if ("??.??".equals(move)) { //$NON-NLS-1$
			// hidden skat given to a player
			info.setType(MoveType.SKAT_LOOKING);
		} else if (move.startsWith("TI.")) { //$NON-NLS-1$
			// time out for player
			info.setType(MoveType.TIME_OUT);
			info.setTimeOutPlayer(parseTimeOut(move));
		} else {
			// extensive parsing needed

			// test card move
			Card card = Card.getCardFromString(move);
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

				} catch (NumberFormatException e) {

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
						info.setType(MoveType.SKAT_LOOKING);
						info.setSkat(parseSkatCards(move));
					} else {
						// game announcement
						info.setType(MoveType.GAME_ANNOUNCEMENT);
						parseGameAnnoucement(info, move);
					}
				}
			}
		}

		// parse player times
		info.putPlayerTime(Player.FORE_HAND, new Double(params.get(params.size() - 3)));
		info.putPlayerTime(Player.MIDDLE_HAND, new Double(params.get(params.size() - 2)));
		info.putPlayerTime(Player.HIND_HAND, new Double(params.get(params.size() - 1)));

		return info;
	}

	private static CardList parseSkatCards(String move) {

		StringTokenizer token = new StringTokenizer(move, "."); //$NON-NLS-1$
		CardList result = new CardList();

		while (token.hasMoreTokens()) {
			result.add(Card.getCardFromString(token.nextToken()));
		}

		return result;
	}

	private static void getMovePlayer(String movePlayer, ISSMoveInformation info) {

		log.debug("Move player: " + movePlayer); //$NON-NLS-1$
		if ("w".equals(movePlayer)) { //$NON-NLS-1$
			// world move
			info.setMovePlayer(MovePlayer.WORLD);
		} else if ("0".equals(movePlayer)) { //$NON-NLS-1$
			// fore hand move
			info.setMovePlayer(MovePlayer.FORE_HAND);
		} else if ("1".equals(movePlayer)) { //$NON-NLS-1$
			// middle hand move
			info.setMovePlayer(MovePlayer.MIDDLE_HAND);
		} else if ("2".equals(movePlayer)) { //$NON-NLS-1$
			// hind hand move
			info.setMovePlayer(MovePlayer.HIND_HAND);
		}
	}

	/**
	 * <game-type> :: (G | C | S | H | D | N) (type Grand .. Null) [O] (ouvert)
	 * [H] (hand, not given if O + trump game) [S] (schneider announced, only in
	 * H games, not if O or Z) [Z] (schwarz announced, only in H games)
	 */
	private static GameAnnouncement parseGameAnnoucement(ISSMoveInformation info, String move) {

		StringTokenizer annToken = new StringTokenizer(move, "."); //$NON-NLS-1$
		String gameType = annToken.nextToken();

		GameAnnouncement ann = new GameAnnouncement();

		// at first the game type
		if (gameType.startsWith("G")) { //$NON-NLS-1$

			ann.setGameType(GameType.GRAND);

		} else if (gameType.startsWith("C")) { //$NON-NLS-1$

			ann.setGameType(GameType.CLUBS);

		} else if (gameType.startsWith("S")) { //$NON-NLS-1$

			ann.setGameType(GameType.SPADES);

		} else if (gameType.startsWith("H")) { //$NON-NLS-1$

			ann.setGameType(GameType.HEARTS);

		} else if (gameType.startsWith("D")) { //$NON-NLS-1$

			ann.setGameType(GameType.DIAMONDS);

		} else if (gameType.startsWith("N")) { //$NON-NLS-1$

			ann.setGameType(GameType.NULL);
		}

		// parse other game modifiers
		for (int i = 1; i < gameType.length(); i++) {

			char mod = gameType.charAt(i);

			if (mod == 'O') {

				ann.setOuvert(true);
			} else if (mod == 'H') {

				ann.setHand(true);
			} else if (mod == 'S') {

				ann.setSchneider(true);
			} else if (mod == 'Z') {

				ann.setSchwarz(true);
			}
		}
		info.setGameAnnouncement(ann);

		if (annToken.hasMoreTokens()) {

			if (!info.getGameAnnouncement().isHand()) {
				Card skatCard0 = Card.getCardFromString(annToken.nextToken());
				Card skatCard1 = Card.getCardFromString(annToken.nextToken());

				CardList skat = new CardList();
				skat.add(skatCard0);
				skat.add(skatCard1);

				info.setSkat(skat);
			} else {

				CardList ouvertCards = new CardList();

				while (annToken.hasMoreTokens() && info.getGameAnnouncement().isOuvert()) {
					// player has shown the cards
					// ouvert game

					ouvertCards.add(Card.getCardFromString(annToken.nextToken()));
				}

				info.setOuvertCards(ouvertCards);
			}
		}

		return ann;
	}

	/**
	 * ??.??.??.??.??.??.??.??.??.??|D9.S9.ST.S8.C9.DT.DQ.CJ.SA.HA|??.??.??.??.?
	 * ?.??.??.??.??.??|??.??
	 * 
	 * ?? - hidden card fore hand cards|middle hand cards|hind hand cards|skat
	 */
	private static List<CardList> parseCardDeal(String move) {

		StringTokenizer handTokens = new StringTokenizer(move, "|"); //$NON-NLS-1$
		List<CardList> result = new ArrayList<CardList>();

		while (handTokens.hasMoreTokens()) {
			result.add(parseHand(handTokens.nextToken()));
		}

		return result;
	}

	private static CardList parseHand(String hand) {

		StringTokenizer cardTokens = new StringTokenizer(hand, "."); //$NON-NLS-1$
		CardList result = new CardList();

		while (cardTokens.hasMoreTokens()) {
			result.add(Card.getCardFromString(cardTokens.nextToken()));
		}

		return result;
	}

	/**
	 * TI.0
	 */
	private static Player parseTimeOut(String timeOut) {

		Player result = null;

		switch (timeOut.charAt(3)) {
		case '0':
			result = Player.FORE_HAND;
			break;
		case '1':
			result = Player.MIDDLE_HAND;
			break;
		case '2':
			result = Player.HIND_HAND;
			break;
		}

		return result;
	}

	static SkatGameData parseGameSummary(String gameSummary) {

		SkatGameData result = new SkatGameData();

		Pattern summaryPartPattern = Pattern.compile("(\\w+)\\[(.*?)\\]"); //$NON-NLS-1$
		Matcher summaryPartMatcher = summaryPartPattern.matcher(gameSummary);

		while (summaryPartMatcher.find()) {

			log.debug(summaryPartMatcher.group());

			String summaryPartMarker = summaryPartMatcher.group(1);
			String summeryPart = summaryPartMatcher.group(2);

			parseSummaryPart(result, summaryPartMarker, summeryPart);
		}

		return result;
	}

	private static void parseSummaryPart(SkatGameData result, String summaryPartMarker, String summaryPart) {

		if ("P0".equals(summaryPartMarker)) { //$NON-NLS-1$

			result.setPlayerName(Player.FORE_HAND, summaryPart);

		} else if ("P1".equals(summaryPartMarker)) { //$NON-NLS-1$

			result.setPlayerName(Player.MIDDLE_HAND, summaryPart);

		} else if ("P2".equals(summaryPartMarker)) { //$NON-NLS-1$

			result.setPlayerName(Player.HIND_HAND, summaryPart);

		} else if ("MV".equals(summaryPartMarker)) { //$NON-NLS-1$

			parseMoves(result, summaryPart);

		} else if ("R".equals(summaryPartMarker)) { //$NON-NLS-1$

			parseGameResult(result, summaryPart);
		}
	}

	private static void parseMoves(SkatGameData result, String summaryPart) {

		StringTokenizer token = new StringTokenizer(summaryPart);

		while (token.hasMoreTokens()) {

			List<String> moveToken = new ArrayList<String>();
			moveToken.add(token.nextToken());
			moveToken.add(token.nextToken());

			// FIXME (jan 06.12.2010) doesn't work this way a.t.m.
			// ISSMoveInformation moveInfo = getMoveInformation(moveToken);
		}
	}

	private static void parseGameResult(SkatGameData result, String summaryPart) {

		StringTokenizer token = new StringTokenizer(summaryPart);

		while (token.hasMoreTokens()) {

			parseResultToken(result, token.nextToken());
		}
	}

	private static void parseResultToken(SkatGameData result, String token) {

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

		if (token.startsWith("d:")) { //$NON-NLS-1$

			parseDeclarerToken(result, token);

		} else if ("penalty".equals(token)) { //$NON-NLS-1$

			// FIXME (jan 07.12.2010) handle this token

		} else if ("loss".equals(token)) { //$NON-NLS-1$

			result.setGameWon(false);

		} else if ("win".equals(token)) { //$NON-NLS-1$

			result.setGameWon(true);

		} else if (token.startsWith("v:")) { //$NON-NLS-1$

			result.setResult(Integer.parseInt(token.substring(2)));

		} else if (token.startsWith("p:")) { //$NON-NLS-1$

			result.setDeclarerScore(Integer.parseInt(token.substring(2)));

		} else if ("overbid".equals(token)) { //$NON-NLS-1$

			result.setOverBidded(true);

		} else if ("s:1".equals(token)) { //$NON-NLS-1$

			result.setSchneider(true);

		} else if ("z:1".equals(token)) { //$NON-NLS-1$

			result.setSchwarz(true);
		}
	}

	private static void parseDeclarerToken(SkatGameData result, String token) {

		if ("d:0".equals(token)) { //$NON-NLS-1$
			result.setDeclarer(Player.FORE_HAND);
		} else if ("d:1".equals(token)) { //$NON-NLS-1$
			result.setDeclarer(Player.MIDDLE_HAND);
		} else if ("d:2".equals(token)) { //$NON-NLS-1$
			result.setDeclarer(Player.HIND_HAND);
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
	static ISSChatMessage getTableChatMessage(String tableName, List<String> detailParams) {

		StringBuffer text = new StringBuffer();

		text.append(detailParams.get(0)).append(": "); //$NON-NLS-1$
		for (int i = 1; i < detailParams.size(); i++) {
			text.append(detailParams.get(i)).append(' ');
		}

		return new ISSChatMessage(tableName, text.toString());
	}
}
