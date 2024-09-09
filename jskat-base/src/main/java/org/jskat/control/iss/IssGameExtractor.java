package org.jskat.control.iss;

import org.jetbrains.annotations.NotNull;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class helps in finding interesting games from the game library provided
 * by the ISS team.
 */
public class IssGameExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(IssGameExtractor.class);
    private final String sourceFileName;

    public static void main(final String[] args) throws Exception {
        // download ISS game files from https://skatgame.net/iss/
        final IssGameExtractor gameExtractor = new IssGameExtractor("data/iss-games-07-2024.sgf");
        gameExtractor.filterGameDatabase(KERMIT_GAMES, "data/kermit_games.csv");
    }

    public IssGameExtractor(final String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    private void filterGameDatabase(final Predicate<SkatGameData> predicate, final String targetFileName) throws Exception {

        try (final Stream<String> stream = Files.lines(Paths.get(sourceFileName))) {
            final AtomicInteger count = new AtomicInteger();
            final var filteredGames = stream
                    .skip(6_000_000)
                    .peek(logProgress(count))
                    //.peek(System.out::println)
                    // TODO: fix parsing of filtered games
                    .filter(gameString ->
                            !gameString.contains("ID[19897]")
                                    && !gameString.contains("ID[29067]")
                                    && !gameString.contains("ID[55355]")
                                    && !gameString.contains("ID[119716]")
                                    && !gameString.contains("ID[119815]")
                                    && !gameString.contains("ID[1026432]")
                                    && !gameString.contains("ID[1271056]")
                                    && !gameString.contains("ID[1841955]")
                                    && !gameString.contains("ID[1926081]")
                                    && !gameString.contains("ID[1930276]")
                                    && !gameString.contains("ID[1979120]")
                                    && !gameString.contains("ID[2492322]")
                                    && !gameString.contains("ID[5484298]")
                                    && !gameString.contains("ID[5517615]")
                                    && !gameString.contains("ID[5534151]")
                                    && !gameString.contains("ID[5562697]")
                                    && !gameString.contains("ID[7322708]")
                                    && !gameString.contains("ID[7323008]")
                                    && !gameString.contains("ID[7323032]")
                                    && !gameString.contains("ID[7323074]")
                                    && !gameString.contains("ID[8034006]")
                                    && !gameString.contains("ID[8066495]")
                                    && !gameString.contains("ID[8015909]"))
                    .map(MessageParser::parseGameSummary)
                    .filter(skatGameData -> skatGameData != null)
                    .filter(predicate)
                    //.map(SkatGameData::toString)
                    .map(NETWORK_INPUTS)
                    .limit(100_000)
                    .collect(Collectors.toList());

            final var lines = new ArrayList<String>();
            lines.add(headerFields().stream().collect(Collectors.joining(",")));
            lines.addAll(filteredGames);

            Files.write(Paths.get(targetFileName), lines);

            LOG.info("Game extraction completed. " + count.get() + " games processed.");
        }
    }

    @NotNull
    private static Consumer<String> logProgress(final AtomicInteger count) {
        return it -> {
            final var currentCount = count.incrementAndGet();
            if (currentCount % 100_000 == 0) {
                LOG.info("{} games processed", currentCount);
            }
        };
    }

    private static final Predicate<SkatGameData> KERMIT_GAMES =
            it -> isDeclarer(it, "kermit")
                    && it.getGameType() != GameType.PASSED_IN
                    && it.isGameWon();

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
