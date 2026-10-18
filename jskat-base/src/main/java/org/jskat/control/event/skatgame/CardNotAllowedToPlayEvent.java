package org.jskat.control.event.skatgame;

import org.jskat.util.Card;

/**
 * This event is created when a player discards the wrong number of cards.
 */
/**
 * Event raised when a player attempts to play an illegal card.
 *
 * @param card card that could not be played
 */
public record CardNotAllowedToPlayEvent(Card card) {

}
