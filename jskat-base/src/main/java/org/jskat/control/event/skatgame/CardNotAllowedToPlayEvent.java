package org.jskat.control.event.skatgame;

import org.jskat.util.Card;

/**
 * This event is created when a player discards the wrong number of cards.
 */
public record CardNotAllowedToPlayEvent(Card card) {

}
