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
package org.jskat.ai.deeplearning;

import org.jskat.control.iss.MessageParser;
import org.jskat.data.SkatGameData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class helps in finding interesting games from the game library provided by the ISS team.
 */
public class IssGameExtractor {

    public static void main(final String[] args) throws IOException, InterruptedException {
        filterGameDatabase(
                "/home/jan/Projects/jskat/iss/iss-games-04-2021.sgf",
                SkatGameDataFilter.KERMIT_WON_GAMES,
                NetworkInputGenerator.NETWORK_INPUTS,
                100,
                "/home/jan/Projects/jskat/iss/kermit_won_games.cvs");
    }

    public static List<String> filterGameDatabase(final String sourceFileName, final Predicate<SkatGameData> predicate, final Function<SkatGameData, String> networkInputMapper, final int limit, final String targetFileName) {

        try (final Stream<String> stream = Files.lines(Paths.get(sourceFileName))) {
            final var filteredGames = stream.map(MessageParser::parseGameSummary)
                    .filter(predicate)
                    .map(networkInputMapper)
                    //.peek(System.out::println)
                    .limit(limit)
                    .collect(Collectors.toList());

            Files.write(Paths.get(targetFileName), filteredGames);

            return filteredGames;
        } catch (final IOException exception) {
            exception.printStackTrace();
        } finally {
            return Collections.emptyList();
        }
    }
}
