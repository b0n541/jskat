package org.jskat.ai.deeplearning;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

import java.util.function.Predicate;

public class SkatGameDataFilter {

    public static final Predicate<SkatGameData> KERMIT_WON_GAMES =
            it -> isDeclarer(it, "kermit")
                    && it.isGameWon();

    private static final boolean isDeclarer(final SkatGameData gameData, final String playerName) {
        return gameData.getDeclarer() == Player.FOREHAND && gameData.getPlayerName(Player.FOREHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.MIDDLEHAND && gameData.getPlayerName(Player.MIDDLEHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.REARHAND && gameData.getPlayerName(Player.REARHAND).startsWith(playerName);
    }
}
