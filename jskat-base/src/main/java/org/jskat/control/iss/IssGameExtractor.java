/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private final String fileName;

    public static void main(final String args[]) throws Exception {
        final IssGameExtractor gameExtractor = new IssGameExtractor("/home/jan/Projects/jskat/iss/iss-games-04-2021.sgf");
        gameExtractor.filterGameDatabase(KERMIT_FOREHAND_GAMES);
    }

    public IssGameExtractor(final String fileName) {
        this.fileName = fileName;
    }

    private static final Predicate<SkatGameData> KERMIT_FOREHAND_GAMES =
            it -> it.getDeclarer() == Player.FOREHAND
                    && it.getPlayerName(Player.FOREHAND).startsWith("kermit")
                    && it.isGameWon();

    private static final Function<SkatGameData, String> NETWORK_INPUTS = it ->
            it.getDealtCards().get(Player.FOREHAND) + ": "
                    + asNetworkInputs(it.getDealtCards().get(Player.FOREHAND))
                    + it.getMaxPlayerBid(Player.FOREHAND) + ","
                    + it.getAnnoucement().getGameType() + ","
                    + (it.getAnnoucement().isHand() ? "1" : "0") + ","
                    + (it.getAnnoucement().isOuvert() ? "1" : "0") + ","
                    + (it.getAnnoucement().isSchneider() ? "1" : "0") + ","
                    + (it.getAnnoucement().isSchwarz() ? "1" : "0");

    private void filterGameDatabase(final Predicate<SkatGameData> predicate) throws Exception {

        try (final Stream<String> stream = Files.lines(Paths.get(fileName))) {
            final var filteredGames = stream.map(MessageParser::parseGameSummary)
                    .filter(predicate)
                    .map(NETWORK_INPUTS)
                    .peek(System.out::println)
                    .limit(10000)
                    .collect(Collectors.toList());

            // TODO save into file
        }
    }

    private static String asNetworkInputs(final CardList cards) {
        final StringBuffer result = new StringBuffer();
        Arrays.stream(Card.values()).forEach(it -> result.append(cards.contains(it) ? "1," : "0,"));
        return result.toString();
    }
}
