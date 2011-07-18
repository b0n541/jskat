/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

package org.jskat.gui.table;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;

/**
 * Panel for showing game informations
 */
class GameInformationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// private JSkatGraphicRepository bitmaps;
	private JSkatResourceBundle strings;

	private JLabel label;

	private int gameNumber;
	private GameState gameState;
	private GameType gameType;
	private int multiplier;
	private boolean playWithJacks;
	private boolean handGame;
	private boolean ouvertGame;
	private boolean schneiderAnnounced;
	private boolean schwarzAnnounced;
	private int trick;
	private boolean gameWon;
	private int declarerPoints;
	private int opponentPoints;

	/**
	 * Constructor
	 */
	GameInformationPanel() {

		// bitmaps = JSkatGraphicRepository.instance();
		strings = JSkatResourceBundle.instance();

		initPanel();
	}

	private void initPanel() {

		setLayout(new MigLayout("fill"));

		setOpaque(true);

		label = new JLabel();
		label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		setGameState(GameState.GAME_START);

		add(this.label);
	}

	void clear() {

		this.label.setText(" "); //$NON-NLS-1$
	}

	void setGameState(GameState newGameState) {

		gameState = newGameState;

		if (gameState.equals(GameState.GAME_START)) {

			resetGameData();
		}

		refreshText();
	}

	void setGameAnnouncement(GameAnnouncement announcement) {
		gameType = announcement.getGameType();
		handGame = announcement.isHand();
		ouvertGame = announcement.isOuvert();
		schneiderAnnounced = announcement.isSchneider();
		schwarzAnnounced = announcement.isSchwarz();
	}

	private void resetGameData() {
		gameType = null;
		multiplier = 0;
		playWithJacks = false;
		handGame = false;
		ouvertGame = false;
		schneiderAnnounced = false;
		schwarzAnnounced = false;
		trick = 0;
		gameWon = false;
		declarerPoints = 0;
		opponentPoints = 0;
	}

	private void refreshText() {

		String text = ""; //$NON-NLS-1$

		if (gameNumber > 0) {
			text += strings.getString("game") + " " + gameNumber + ": "; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		}

		text += getGameStateString(gameState);

		if (gameType != null) {
			text += " [" + strings.getGameType(gameType); //$NON-NLS-1$

			if (gameState.equals(GameState.GAME_OVER) && multiplier > 0) {
				if (playWithJacks) {
					text += " " + strings.getString("with"); //$NON-NLS-1$//$NON-NLS-2$
				} else {
					text += " " + strings.getString("without"); //$NON-NLS-1$//$NON-NLS-2$
				}
				text += " " + multiplier; //$NON-NLS-1$
				text += " " + strings.getString("play"); //$NON-NLS-1$//$NON-NLS-2$
				text += " " + (multiplier + 1); //$NON-NLS-1$
			}

			if (handGame) {
				text += " hand";
			}

			if (ouvertGame) {
				text += " ouvert";
			}

			if (schneiderAnnounced) {
				text += " schneider";
			}

			if (schwarzAnnounced) {
				text += " schwarz";
			}
			text += "]";
		}

		if (gameState.equals(GameState.TRICK_PLAYING)) {
			text += " " + strings.getString("trick") + " " + trick; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}

		if (gameState.equals(GameState.GAME_OVER)) {

			text += " - "; //$NON-NLS-1$
			if (gameWon) {
				text += strings.getString("won"); //$NON-NLS-1$
			} else {
				text += strings.getString("lost"); //$NON-NLS-1$
			}
			text += " - "; //$NON-NLS-1$

			text += strings.getString("declarer") + ": " + declarerPoints + " " + strings.getString("points"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			text += ", " + strings.getString("opponents") + ": " //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					+ opponentPoints + " " + strings.getString("points"); //$NON-NLS-1$//$NON-NLS-2$
		}

		label.setText(text);
	}

	void setGameResult(SkatGameData data) {

		multiplier = data.getGameResult().getMultiplier();
		playWithJacks = data.getGameResult().isPlayWithJacks();
		gameWon = data.isGameWon();
		declarerPoints = data.getDeclarerScore();
		opponentPoints = data.getOpponentScore();

		refreshText();
	}

	String getGameStateString(GameState state) {

		String result = null;

		switch (state) {
		case BIDDING:
			result = strings.getString("bidding_phase"); //$NON-NLS-1$
			break;
		case CALC_GAME_VALUE:
			result = strings.getString("calc_game_value_phase"); //$NON-NLS-1$
			break;
		case DEALING:
			result = strings.getString("dealing_phase"); //$NON-NLS-1$
			break;
		case DECLARING:
			result = strings.getString("declaring_phase"); //$NON-NLS-1$
			break;
		case DISCARDING:
			result = strings.getString("discarding_phase"); //$NON-NLS-1$
			break;
		case GAME_OVER:
			result = strings.getString("game_over_phase"); //$NON-NLS-1$
			break;
		case GAME_START:
			result = strings.getString("game_start_phase"); //$NON-NLS-1$
			break;
		case PICK_UP_SKAT:
			result = strings.getString("pick_up_skat_phase"); //$NON-NLS-1$
			break;
		case PRELIMINARY_GAME_END:
			result = strings.getString("preliminary_game_end_phase"); //$NON-NLS-1$
			break;
		case TRICK_PLAYING:
			result = strings.getString("trick_playing_phase"); //$NON-NLS-1$
			break;
		}

		return result;
	}

	/**
	 * Sets the trick number
	 * 
	 * @param trickNumber
	 *            Trick number
	 */
	public void setTrickNumber(int trickNumber) {

		trick = trickNumber;
		refreshText();
	}

	/**
	 * Sets the game number
	 * 
	 * @param newGameNumber
	 *            Game number
	 */
	public void setGameNumber(int newGameNumber) {

		gameNumber = newGameNumber;
		refreshText();
	}
}
