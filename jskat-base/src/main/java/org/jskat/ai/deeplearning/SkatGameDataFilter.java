package org.jskat.ai.deeplearning;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.jskat.util.Player;

import java.util.Set;
import java.util.function.Predicate;

public class SkatGameDataFilter {

    private static Set<GameType> SUIT_GAMES = Set.of(GameType.CLUBS, GameType.SPADES, GameType.HEARTS, GameType.DIAMONDS);

    public static final Predicate<SkatGameData> KERMIT_WON_SUIT_GAMES =
            it -> isDeclarer(it, "kermit")
                    && it.isGameWon()
                    && SUIT_GAMES.contains(it.getAnnoucement().getGameType());

    public static final Predicate<SkatGameData> KERMIT_WON_GAMES =
            it -> isDeclarer(it, "kermit")
                    && it.isGameWon();

    private static final boolean isDeclarer(final SkatGameData gameData, final String playerName) {
        return gameData.getDeclarer() == Player.FOREHAND && gameData.getPlayerName(Player.FOREHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.MIDDLEHAND && gameData.getPlayerName(Player.MIDDLEHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.REARHAND && gameData.getPlayerName(Player.REARHAND).startsWith(playerName);
    }
}
