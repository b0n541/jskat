/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.iss;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jskat.data.SkatGameData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps in finding interesting games from the game library provided
 * by the ISS team.
 */
public class IssGameExtractor {
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(IssGameExtractor.class);
	/**
	 * Path to the file with the game informations.
	 */
	private static String filePath;

	public static void main(final String args[]) throws Exception {

		final IssGameExtractor gameExtractor = new IssGameExtractor();
		gameExtractor.setFilePath("/home/jan/Projekte/jskat/iss/issgames-1-2012.sgf"); //$NON-NLS-1$

		filterGameDatabase();
	}

	private static void filterGameDatabase() throws Exception {
		final FileInputStream inputStream = new FileInputStream(new File(filePath));
		final DataInputStream in = new DataInputStream(inputStream);
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		long gameNo = 1;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			try {
				final SkatGameData gameData = MessageParser.parseGameSummary(strLine);
				final int declarerPoints = gameData.getGameResult()
						.getFinalDeclarerPoints();
				if (declarerPoints > 60 && declarerPoints < 65) {
					log.warn("Game no. " + gameNo + ": " + strLine); //$NON-NLS-1$//$NON-NLS-2$
					// log.debug("Game type: " + gameData.getGameType());
				}
			} catch (final Exception except) {
				log.error("Failed reading game no. " + gameNo + ": " + strLine); //$NON-NLS-1$ //$NON-NLS-2$
				log.error(except.toString());
			}

			if (gameNo % 10000 == 0) {
				log.error("Read " + gameNo + " games."); //$NON-NLS-1$//$NON-NLS-2$
			}

			gameNo++;
		}
		// Close the input stream
		in.close();
	}

	/**
	 * Sets the path to the game database.
	 *
	 * @param newFilePath
	 *            File path
	 */
	public static void setFilePath(final String newFilePath) {
		filePath = newFilePath;
	}
}
