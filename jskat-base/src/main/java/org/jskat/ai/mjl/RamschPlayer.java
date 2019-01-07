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
package org.jskat.ai.mjl;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.rule.SkatRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
public class RamschPlayer extends AbstractCardPlayer {

	/** log */
	private static Logger log = LoggerFactory.getLogger(RamschPlayer.class);
	private CardList cards;

	/**
	 * Constructor
	 * 
	 * @param cards
	 *            Player's cards
	 * @param id
	 *            playerID
	 * @param rules
	 *            Skat rules
	 */
	public RamschPlayer(final CardList cards, final int id, final SkatRule rules) {
		super(cards);
		log.debug("Constructing new single player.");
		this.playerID = id;
		this.rules = rules;
	}

	/**
	 * Gets the next card, that the player wants to play
	 * 
	 * @param knowledge
	 *            all necessary information about the game
	 * @return index of the card to play
	 */
	@Override
	public Card playNextCard(final ImmutablePlayerKnowledge knowledge) {
		log.debug(".playNextCard(): Processing hand: " + cards);
		log.debug(".playNextCard(): Not really implemented yet...");
		int result = 0;
		if (knowledge.getTrickCards().size() == 0) {
			result = playInitialCard(cards);
			log.info(".playNextCard(): playing " + (cards.get(result)));
			return cards.remove(result);
		} else if (knowledge.getTrickCards().size() == 1) {
			result = playOtherCard(cards, knowledge.getTrickCards().get(0));
		} else {
			// if(trick.getCard(0).beats(trick.getCard(1), GameType.RAMSCH,
			// null, trick.getCard(1))) {
			// result = playOtherCard(cards, trick.getCard(0));
			// }
			// else {
			// if(trick.getCard(1).getRank() == Rank.JACK) {
			// if(trick.getCard(0).getRank() == Rank.JACK) {
			// result = playOtherCard(cards, trick.getCard(1));
			// }
			// else {
			// result = playOtherCard(cards, trick.getCard(0));
			// }
			// }
			// else {
			// result = playOtherCard(cards, trick.getCard(1));
			// }
			// }
		}

		// make sure that the card is allowed
		// if(!rules.isCardAllowed(cards.get(result), cards, trick.getCard(0),
		// null)) {
		// // if it's not allowed, take another one
		// for(int i=0;i<cards.size();i++) {
		// if(rules.isCardAllowed(cards.get(i), cards, trick.getCard(0), null))
		// {
		// result = i;
		// }
		// }
		// }
		log.info(".playNextCard(): playing " + (cards.get(result)));
		return cards.remove(result);
	}

	private int playInitialCard(final CardList cards) {
		return cards.size() - 1;
	}

	private int playOtherCard(final CardList cards, final Card cardToMatch) {
		return cards.size() - 1;
	}

	/**
	 * Gets the player ID
	 * 
	 * @return player id
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Sets the player ID
	 * 
	 * @param id
	 *            Player ID
	 */
	public void setPlayerID(final int id) {
		playerID = id;
	}

	/** player id */
	private int playerID = -1;
	private final SkatRule rules;

}
