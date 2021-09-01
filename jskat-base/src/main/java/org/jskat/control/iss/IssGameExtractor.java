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

import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class helps in finding interesting games from the game library provided
 * by the ISS team.
 */
public class IssGameExtractor {

    private static final Logger log = LoggerFactory.getLogger(IssGameExtractor.class);

    private static String fileName;

    public static void main(final String args[]) throws Exception {

        final IssGameExtractor gameExtractor = new IssGameExtractor();
        gameExtractor.setFilePath("/home/jan/Projects/jskat/iss/iss-games-04-2021.sgf"); //$NON-NLS-1$

        filterGameDatabase();
    }

    private static void filterGameDatabase() throws Exception {

        try (final Stream<String> stream = Files.lines(Paths.get(fileName))) {
            final var kermitForehandGames = stream.map(MessageParser::parseGameSummary)
                    .filter(it -> it.getDeclarer() == Player.FOREHAND && it.getPlayerName(Player.FOREHAND).startsWith("kermit"))
                    .filter(it -> it.isGameWon())
                    .limit(100)
                    .collect(Collectors.toList());

            kermitForehandGames.forEach(it -> System.out.println(" "
                    + it.getDealtCards().get(Player.FOREHAND) + " "
                    + it.getMaxPlayerBid(Player.FOREHAND) + " "
                    + it.getAnnoucement().getGameType()));
        }
    }

    public static void setFilePath(final String newFileName) {
        fileName = newFileName;
    }
}
