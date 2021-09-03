package org.jskat.ai.deeplearning;

import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.Arrays;
import java.util.function.Function;

public class NetworkInputGenerator {
    public static final Function<SkatGameData, String> NETWORK_INPUTS = it ->
            //it.getDeclarer() + " " + it.getDealtCards().get(it.getDeclarer()) + ": " +
            // features
            asNetworkInputs(it.getDeclarer()) // 3 features
                    + asNetworkInputs(it.getDealtCards().get(Player.FOREHAND)) // 32 features
                    + it.getMaxPlayerBid(Player.FOREHAND) + "," // 1 feature
                    + it.getMaxPlayerBid(Player.MIDDLEHAND) + "," // 1 feature
                    + it.getMaxPlayerBid(Player.REARHAND) + "," // 1 feature
                    // labels
                    + it.getAnnoucement().getGameType().ordinal() // + "," // 1 label
//                    + (it.getAnnoucement().isHand() ? "1" : "0") + ","
//                    + (it.getAnnoucement().isOuvert() ? "1" : "0") + ","
//                    + (it.getAnnoucement().isSchneider() ? "1" : "0") + ","
//                    + (it.getAnnoucement().isSchwarz() ? "1" : "0") + ","
//                    + it.getDeclarerScore() + ","
//                    + (it.isSchneider() ? "1" : "0") + ","
//                    + (it.isSchwarz() ? "1" : "0")
            ;

    private static String asNetworkInputs(final CardList cards) {
        final StringBuffer result = new StringBuffer();
        Arrays.stream(Card.values()).forEach(it -> result.append(cards.contains(it) ? "1," : "0,"));
        return result.toString();
    }

    private static String asNetworkInputs(final Player declarer) {
        final StringBuffer result = new StringBuffer();
        Arrays.stream(Player.values()).forEach(it -> result.append(it == declarer ? "1," : "0,"));
        return result.toString();
    }
}
