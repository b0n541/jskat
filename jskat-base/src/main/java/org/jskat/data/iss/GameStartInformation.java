package org.jskat.data.iss;

import org.jskat.util.Player;

import java.util.Map;


/**
 * Details announced by the server when an Internet Skat game starts.
 * Holds all data for an ISS skat game.
 *
 * @param loginName local account name
 * @param gameNo server game number
 * @param playerNames names of the players in seating order
 * @param playerTimes remaining times for the players
 */
public record GameStartInformation(
        String loginName,
        int gameNo,
        Map<Player, String> playerNames,
        Map<Player, Double> playerTimes) {
}
