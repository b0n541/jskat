package org.jskat.control.iss;

import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class helps in finding interesting games from the game library provided
 * by the ISS team.
 */
public class IssGameExtractor {

    private final String sourceFileName;

    public static void main(final String[] args) throws Exception {
        final IssGameExtractor gameExtractor = new IssGameExtractor("/home/jan/Projects/jskat/iss/iss-games-04-2021.sgf");
        gameExtractor.filterGameDatabase(KERMIT_WON_GAMES, "kermit_won_games.csv");
    }

    public IssGameExtractor(final String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    private void filterGameDatabase(final Predicate<SkatGameData> predicate, final String targetFileName) throws Exception {

        try (final Stream<String> stream = Files.lines(Paths.get(sourceFileName))) {
            final var filteredGames = stream.map(MessageParser::parseGameSummary)
                    .filter(predicate)
                    .map(NETWORK_INPUTS)
                    .peek(System.out::println)
                    //.limit(1000)
                    .collect(Collectors.toList());

            Files.write(Paths.get(targetFileName), filteredGames);
        }
    }

    private static final Predicate<SkatGameData> KERMIT_WON_GAMES =
            it -> isDeclarer(it, "kermit")
                    && it.isGameWon();

    private static final boolean isDeclarer(final SkatGameData gameData, final String playerName) {
        return gameData.getDeclarer() == Player.FOREHAND && gameData.getPlayerName(Player.FOREHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.MIDDLEHAND && gameData.getPlayerName(Player.MIDDLEHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.REARHAND && gameData.getPlayerName(Player.REARHAND).startsWith(playerName);
    }

    private static final Function<SkatGameData, String> NETWORK_INPUTS = it ->
            //it.getDeclarer() + " " + it.getDealtCards().get(it.getDeclarer()) + ": " +
            asNetworkInputs(it.getDeclarer())
                    + asNetworkInputs(it.getDealtCards().get(Player.FOREHAND))
                    + it.getMaxPlayerBid(Player.FOREHAND) + ","
                    + it.getMaxPlayerBid(Player.MIDDLEHAND) + ","
                    + it.getMaxPlayerBid(Player.REARHAND) + ","
                    + it.getAnnouncement().gameType() + ","
                    + (it.getAnnouncement().hand() ? "1" : "0") + ","
                    + (it.getAnnouncement().ouvert() ? "1" : "0") + ","
                    + (it.getAnnouncement().schneider() ? "1" : "0") + ","
                    + (it.getAnnouncement().schwarz() ? "1" : "0") + ","
                    + it.getDeclarerScore() + ","
                    + (it.isSchneider() ? "1" : "0") + ","
                    + (it.isSchwarz() ? "1" : "0");

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
