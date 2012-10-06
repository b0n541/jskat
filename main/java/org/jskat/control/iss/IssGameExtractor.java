/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.control.iss;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps in finding interesting games from the game library provided
 * by the ISS team
 */
public class IssGameExtractor {

	private static String filePath;

	private static Logger log = LoggerFactory.getLogger(IssGameExtractor.class);

	public IssGameExtractor() {

	}

	public static void main(final String args[]) throws Exception {

		IssGameExtractor gameExtractor = new IssGameExtractor();
		gameExtractor
				.setFilePath("/home/jan/Projekte/JSkat/iss/issgames-1-2012.sgf"); //$NON-NLS-1$

		filterGameDatabase();
	}

	private static void filterGameDatabase() throws Exception {
		FileInputStream inputStream = new FileInputStream(new File(filePath));
		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		long gameNo = 1;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			try {
				SkatGameData gameData = MessageParser.parseGameSummary(strLine);

				if (GameType.GRAND.equals(gameData.getGameType())) {
					// log.debug("Game no. " + gameNo + ": " + strLine); //$NON-NLS-1$//$NON-NLS-2$
					// log.debug("Game type: " + gameData.getGameType());
				}
			} catch (Exception except) {
				log.debug("Failed reading game no. " + gameNo + ": " + strLine); //$NON-NLS-1$ //$NON-NLS-2$
				// throw except;
			}

			if (gameNo % 10000 == 0) {
				log.debug("Read " + gameNo + " games."); //$NON-NLS-1$//$NON-NLS-2$
			}

			gameNo++;
		}
		// Close the input stream
		in.close();
	}

	/**
	 * Sets the path to the game database
	 * 
	 * @param newFilePath
	 */
	public static void setFilePath(final String newFilePath) {
		filePath = newFilePath;
	}
}
