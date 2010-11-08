/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncement;
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
	static ISSTablePanelStatus getTableStatus(String loginName,
			List<String> params) {

		ISSTablePanelStatus status = new ISSTablePanelStatus();

		status.setLoginName(loginName);

		status.setMaxPlayers(Integer.parseInt(params.get(0)));

		// get player status
		for (int i = 0; i < status.getMaxPlayers(); i++) {
			// parse only non empty seats
			if (!(".".equals(params.get(i * 10 + 5)))) { //$NON-NLS-1$
				// there is a player
				status.addPlayer(parsePlayerStatus(params.subList(i * 10 + 5,
						i * 10 + 16)));
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

	static ISSGameStartInformation getGameStartStatus(String loginName,
			List<String> params) {

		log.debug("game start parameter: " + params); //$NON-NLS-1$

		ISSGameStartInformation status = new ISSGameStartInformation();

		status.setLoginName(loginName);

		status.setGameNo(Integer.parseInt(params.get(0)));
		status.putPlayerName(Player.FORE_HAND, params.get(1));
		status.putPlayerTime(Player.FORE_HAND, new Double(params.get(2)));
		status.putPlayerName(Player.MIDDLE_HAND, params.get(3));
		status.putPlayerTime(Player.MIDDLE_HAND, new Double(params.get(4)));
		status.putPlayerName(Player.HIND_HAND, params.get(5));
		status.putPlayerTime(Player.HIND_HAND, new Double(params.get(6)));

		return status;
	}

	static ISSMoveInformation getMoveInformation(List<String> params) {

		ISSMoveInformation info = new ISSMoveInformation();

		String movePlayer = params.get(0);
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

		// FIXME Unhandled moves
		String move = params.get(1);
		log.debug("Move: " + move); //$NON-NLS-1$
		if ("y".equals(move)) { //$NON-NLS-1$
			// holding bid move
			info.setType(MoveType.BID);
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
						// TODO parse cards
					} else {
						// game announcement
						info.setType(MoveType.GAME_ANNOUNCEMENT);
						parseGameAnnoucement(info, move);
					}
				}
			}
		}

		// parse player times
		info.putPlayerTime(Player.FORE_HAND,
				new Double(params.get(params.size() - 3)));
		info.putPlayerTime(Player.MIDDLE_HAND,
				new Double(params.get(params.size() - 2)));
		info.putPlayerTime(Player.HIND_HAND,
				new Double(params.get(params.size() - 1)));

		return info;
	}

	/**
	 * <game-type> :: (G | C | S | H | D | N) (type Grand .. Null) [O] (ouvert)
	 * [H] (hand, not given if O + trump game) [S] (schneider announced, only in
	 * H games, not if O or Z) [Z] (schwarz announced, only in H games)
	 */
	private static GameAnnouncement parseGameAnnoucement(
			ISSMoveInformation info, String move) {

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

		Card skatCard0 = Card.getCardFromString(annToken.nextToken());
		Card skatCard1 = Card.getCardFromString(annToken.nextToken());

		info.setSkatCards(skatCard0, skatCard1);

		while (annToken.hasMoreTokens()
				&& info.getGameAnnouncement().isOuvert()) {
			// player has shown the cards
			// ouvert game

			// FIXME show cards
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
}
