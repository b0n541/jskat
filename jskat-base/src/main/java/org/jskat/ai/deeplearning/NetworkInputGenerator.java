package org.jskat.ai.deeplearning;

import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.Arrays;
import java.util.function.Function;

public class NetworkInputGenerator {
    public static final Function<SkatGameData, String> NETWORK_INPUTS = it -> {
        // features
        return it.getDeclarer() + ","
                + asNetworkInputs(it.getCardsAfterDiscard().get(it.getDeclarer())) // 32 features
                + it.getMaxPlayerBid(Player.FOREHAND) + "," // 1 feature
                + it.getMaxPlayerBid(Player.MIDDLEHAND) + "," // 1 feature
                + it.getMaxPlayerBid(Player.REARHAND) + "," // 1 feature
                + asNetworkInputs(it.getDealtSkat()) // 32 features
                + asNetworkInputs(it.getSkat()) // 32 features
                // labels
                + it.getGameResult().getMultiplier() + ","
                + it.getAnnoucement().getGameType() + ","
                + (it.getAnnoucement().isHand() ? "1" : "0") + ","
                + (it.getAnnoucement().isOuvert() ? "1" : "0") + ","
                + (it.getAnnoucement().isSchneider() ? "1" : "0") + ","
                + (it.getAnnoucement().isSchwarz() ? "1" : "0") + ","
                + it.getDeclarerScore() + ","
                + (it.isSchneider() ? "1" : "0") + ","
                + (it.isSchwarz() ? "1" : "0");
    };

    private static String asNetworkInputs(final CardList cards) {
        final StringBuffer result = new StringBuffer();
        Arrays.stream(Card.values()).forEach(it -> result.append(cards.contains(it) ? "1," : "0,"));
        return result.toString();
    }
}
