package org.jskat.data.iss;

import org.jskat.util.Player;

import java.util.Map;


/**
 * Holds all data for an ISS skat game
 */
public record GameStartInformation(
        String loginName,
        int gameNo,
        Map<Player, String> playerNames,
        Map<Player, Double> playerTimes) {
}