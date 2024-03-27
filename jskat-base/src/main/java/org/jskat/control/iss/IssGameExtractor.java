package org.jskat.control.iss;

import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        final IssGameExtractor gameExtractor = new IssGameExtractor("/home/jan/Projects/jskat/iss/iss-games-10-2023.sgf");
        gameExtractor.filterGameDatabase(KERMIT_GAMES, "kermit_games.csv");
    }

    public IssGameExtractor(final String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    private void filterGameDatabase(final Predicate<SkatGameData> predicate, final String targetFileName) throws Exception {

        try (final Stream<String> stream = Files.lines(Paths.get(sourceFileName))) {
            final var filteredGames = stream
                    .map(MessageParser::parseGameSummary)
                    .filter(skatGameData -> skatGameData != null)
                    .map(SkatGameData::toString)
//                    .filter(predicate)
//                    .map(NETWORK_INPUTS)
                    .limit(100)
                    .collect(Collectors.toList());

            final var lines = new ArrayList<String>();
//            lines.add(headerFields().stream().collect(Collectors.joining(",")));
            lines.addAll(filteredGames);

            Files.write(Paths.get(targetFileName), lines);
        }
    }

    private static final Predicate<SkatGameData> KERMIT_GAMES =
            it -> isDeclarer(it, "kermit") && it.getGameType() != GameType.PASSED_IN;

    private static boolean isDeclarer(final SkatGameData gameData, final String playerName) {
        return gameData.getDeclarer() == Player.FOREHAND && gameData.getPlayerName(Player.FOREHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.MIDDLEHAND && gameData.getPlayerName(Player.MIDDLEHAND).startsWith(playerName)
                || gameData.getDeclarer() == Player.REARHAND && gameData.getPlayerName(Player.REARHAND).startsWith(playerName);
    }

    private static final List<String> headerFields() {
        final var result = new ArrayList<String>();
        result.add("declarer");
        Arrays.stream(Card.values()).toList().forEach(it -> result.add(it.toString()));
        result.add("maxBidForehand");
        result.add("maxBidMiddlehand");
        result.add("maxBidRearhand");
        result.add("gameType");
        result.add("hand");
        result.add("ouvert");
        result.add("annSchneider");
        result.add("annSchwarz");
        result.add("won");
        result.add("declarerScore");
        result.add("schneider");
        result.add("schwarz");
        return result;
    }

    private static final Function<SkatGameData, String> NETWORK_INPUTS = it ->
            it.getDeclarer() + ","
                    + asNetworkInputs(it.getDeclarerCardsBeforeFirstTrick())
                    + it.getMaxPlayerBid(Player.FOREHAND) + ","
                    + it.getMaxPlayerBid(Player.MIDDLEHAND) + ","
                    + it.getMaxPlayerBid(Player.REARHAND) + ","
                    + it.getAnnouncement().contract().gameType() + ","
                    + (it.getAnnouncement().contract().hand() ? "1" : "0") + ","
                    + (it.getAnnouncement().contract().ouvert() ? "1" : "0") + ","
                    + (it.getAnnouncement().contract().schneider() ? "1" : "0") + ","
                    + (it.getAnnouncement().contract().schwarz() ? "1" : "0") + ","
                    + (it.isGameWon() ? "1" : "0") + ","
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
