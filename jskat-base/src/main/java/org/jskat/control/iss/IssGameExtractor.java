/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.control.iss;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;
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

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		IssGameExtractor gameExtractor = new IssGameExtractor();
		gameExtractor
				.setFilePath("/home/jan/Projekte/jskat/iss/issgames-1-2012.sgf"); //$NON-NLS-1$

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
				int declarerPoints = gameData.getGameResult()
						.getFinalDeclarerPoints();
				if (declarerPoints > 60 && declarerPoints < 65) {
					log.warn("Game no. " + gameNo + ": " + strLine); //$NON-NLS-1$//$NON-NLS-2$
					// log.debug("Game type: " + gameData.getGameType());
				}
			} catch (Exception except) {
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
