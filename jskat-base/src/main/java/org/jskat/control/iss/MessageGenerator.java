/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.iss;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.iss.ChatMessage;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

/**
 * Generator for ISS messages
 */
class MessageGenerator {

	String loginName;

	MessageGenerator(final String loginName) {
		this.loginName = loginName;
	}

	String getLoginAndPasswordMessage(final String password) {
		return "login " + loginName + " " + password; //$NON-NLS-1$ //$NON-NLS-2$
	}

	static String getPasswordMessage(final String password) {

		return password;
	}

	String getChatMessage(final ChatMessage message) {
		// FIXME (jan 30.01.2011) refactor ChatMessage with ChatMessageType
		if ("Lobby".equals(message.getChatName())) { //$NON-NLS-1$
			return "yell " + message.getMessage(); //$NON-NLS-1$
		} else {
			return "table " + message.getChatName() + ' ' + loginName + " tell " + message.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static String getTableCreationMessage() {

		// TODO table creation for four player
		return "create / 3"; //$NON-NLS-1$
	}

	static String getJoinTableMessage(final String tableName) {

		return "join " + tableName; //$NON-NLS-1$
	}

	static String getObserveTableMessage(final String tableName) {

		return "observe " + tableName; //$NON-NLS-1$
	}

	String getLeaveTableMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " leave"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String getReadyMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " ready"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String getTalkEnabledMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " gametalk"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String getTableSeatChangeMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " 34"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String getInvitePlayerMessage(final String tableName, final String invitee) {

		return "table " + tableName + ' ' + loginName + " invite " + invitee; //$NON-NLS-1$//$NON-NLS-2$
	}

	String getPassMoveMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " play p"; //$NON-NLS-1$//$NON-NLS-2$
	}

	String getHoldBidMoveMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " play y"; //$NON-NLS-1$//$NON-NLS-2$
	}

	String getBidMoveMessage(final String tableName, final int bidValue) {

		return "table " + tableName + ' ' + loginName + " play " + bidValue; //$NON-NLS-1$//$NON-NLS-2$
	}

	String getPickUpSkatMoveMessage(final String tableName) {
		return "table " + tableName + ' ' + loginName + " play s"; //$NON-NLS-1$//$NON-NLS-2$
	}

	String getGameAnnouncementMoveMessage(final String tableName,
			final GameAnnouncement gameAnnouncement) {

		String gameAnnouncementString = getGameTypeString(
				gameAnnouncement.getGameType(), gameAnnouncement.isHand(),
				gameAnnouncement.isOuvert(), gameAnnouncement.isSchneider(),
				gameAnnouncement.isSchwarz());

		if (!gameAnnouncement.isHand()) {

			final CardList skat = gameAnnouncement.getDiscardedCards();
			gameAnnouncementString += "." + getIssCardString(skat.get(0)) + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ getIssCardString(skat.get(1));
		}

		return "table " + tableName + ' ' + loginName + " play " + gameAnnouncementString; //$NON-NLS-1$//$NON-NLS-2$
	}

	private String getGameTypeString(final GameType gameType,
			final boolean hand, final boolean ouvert, final boolean schneider,
			final boolean schwarz) {

		String result = getGameTypeString(gameType);

		if (hand) {
			result += "H"; //$NON-NLS-1$
		}

		if (ouvert) {
			result += "O"; //$NON-NLS-1$
		}

		if (schneider) {
			result += "S"; //$NON-NLS-1$
		}

		if (schwarz) {
			result += "Z"; //$NON-NLS-1$
		}

		return result;
	}

	private static String getGameTypeString(final GameType gameType) {
		switch (gameType) {
		case CLUBS:
			return "C"; //$NON-NLS-1$
		case SPADES:
			return "S"; //$NON-NLS-1$
		case HEARTS:
			return "H"; //$NON-NLS-1$
		case DIAMONDS:
			return "D"; //$NON-NLS-1$
		case NULL:
			return "N"; //$NON-NLS-1$
		case GRAND:
			return "G"; //$NON-NLS-1$
		default:
			// FIXME (jan 02.11.2010) Ramsch games are not allowed on ISS
			return null;
		}
	}

	String getCardMoveMessage(final String tableName, final Card card) {
		return "table " + tableName + ' ' + loginName + " play " + getIssCardString(card); //$NON-NLS-1$//$NON-NLS-2$
	}

	private static String getIssCardString(final Card card) {
		return card.getSuit().getShortString() + card.getRank().getShortString();
	}

	String getResignMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " play RE"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String getShowCardsMessage(final String tableName) {

		return "table " + tableName + ' ' + loginName + " play SC"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	static String getInvitationAcceptedMessage(final String tableName,
			final String invitationTicket) {

		return "join " + tableName + " " + invitationTicket; //$NON-NLS-1$//$NON-NLS-2$
	}
}
