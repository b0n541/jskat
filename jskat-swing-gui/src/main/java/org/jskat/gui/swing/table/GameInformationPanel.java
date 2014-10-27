/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

import java.awt.Font;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;

/**
 * Panel for showing game informations
 */
class GameInformationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // private JSkatGraphicRepository bitmaps;
    private final JSkatResourceBundle strings;

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
    private boolean contra;
    private boolean re;
    private int trick;
    private boolean gameWon;
    private int declarerPoints;
    private int opponentPoints;
    private Set<Player> ramschLoosers;

    /**
     * Constructor
     */
    GameInformationPanel() {

        // bitmaps = JSkatGraphicRepository.instance();
		this.strings = JSkatResourceBundle.INSTANCE;

        initPanel();
    }

    private void initPanel() {

        setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

        setOpaque(true);

        this.label = new JLabel();
        this.label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        setGameState(GameState.GAME_START);

        add(this.label);
    }

    void clear() {

        this.label.setText(" "); //$NON-NLS-1$
    }

    void setGameState(final GameState newGameState) {

        this.gameState = newGameState;

        if (this.gameState.equals(GameState.GAME_START)) {

            resetGameData();
        }

        refreshText();
    }

    void setGameAnnouncement(final GameAnnouncement announcement) {
        this.gameType = announcement.getGameType();
        this.handGame = announcement.isHand();
        this.ouvertGame = announcement.isOuvert();
        this.schneiderAnnounced = announcement.isSchneider();
        this.schwarzAnnounced = announcement.isSchwarz();
    }

    private void resetGameData() {
        this.gameType = null;
        this.multiplier = 0;
        this.playWithJacks = false;
        this.handGame = false;
        this.ouvertGame = false;
        this.schneiderAnnounced = false;
        this.schwarzAnnounced = false;
        this.contra = false;
        this.re = false;
        this.trick = 0;
        this.gameWon = false;
        this.declarerPoints = 0;
        this.opponentPoints = 0;
        this.ramschLoosers = new HashSet<Player>();
    }

    private void refreshText() {

        StringBuffer text = new StringBuffer();

        appendGameNumber(text);

        text.append(getGameStateString(this.gameState));

        appendGameType(text);

        appendGameStateDetails(text);

        this.label.setText(text.toString());
    }

    private void appendGameStateDetails(StringBuffer text) {
        if (this.gameState.equals(GameState.TRICK_PLAYING)) {
            appendTrickPlayingDetails(text);
        } else if (this.gameState.equals(GameState.GAME_OVER)) {

            appendGameOverDetails(text);
        }
    }

    private void appendGameOverDetails(StringBuffer text) {
        if (this.gameType != GameType.PASSED_IN) {
            text.append(" - "); //$NON-NLS-1$
            if (this.gameWon) {
                text.append(this.strings.getString("won")); //$NON-NLS-1$
            } else {
                text.append(this.strings.getString("lost")); //$NON-NLS-1$
            }
        }

        if (this.gameType == GameType.RAMSCH) {
            text.append(" - "); //$NON-NLS-1$

            Iterator<Player> iterator = this.ramschLoosers.iterator();
            if (iterator.hasNext()) {
                text.append(this.strings.getPlayerString(iterator.next()));
            }
            while (iterator.hasNext()) {
                text.append(", ");
                text.append(this.strings.getPlayerString(iterator.next()));
            }

        } else if (this.gameType != GameType.NULL && this.gameType != GameType.PASSED_IN) {
            text.append(" - "); //$NON-NLS-1$

            text.append(this.declarerPoints + " " + this.strings.getString("versus")
                    + " ");
            text.append(this.opponentPoints + " " + this.strings.getString("points")); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    private void appendTrickPlayingDetails(StringBuffer text) {
        text.append(" " + this.strings.getString("trick") + " " + this.trick); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    private void appendGameType(StringBuffer text) {
        if (this.gameType != null) {
            text.append(" [" + this.strings.getGameType(this.gameType)); //$NON-NLS-1$

            if (this.gameState.equals(GameState.GAME_OVER) && this.multiplier > 0) {
                if (this.playWithJacks) {
                    text.append(" " + this.strings.getString("with")); //$NON-NLS-1$//$NON-NLS-2$
                } else {
                    text.append(" " + this.strings.getString("without")); //$NON-NLS-1$//$NON-NLS-2$
                }
                text.append(" " + (this.multiplier - 1)); //$NON-NLS-1$
                text.append(" " + this.strings.getString("play")); //$NON-NLS-1$//$NON-NLS-2$
                text.append(" " + this.multiplier); //$NON-NLS-1$

                if (this.contra) {
                    text.append(" " + this.strings.getString("contra"));
                    if (this.re) {
                        text.append(" " + this.strings.getString("re"));
                    }
                }
            }

            if (this.handGame) {
                text.append(" hand");
            }

            if (this.ouvertGame) {
                text.append(" ouvert");
            }

            if (this.schneiderAnnounced) {
                text.append(" schneider");
            }

            if (this.schwarzAnnounced) {
                text.append(" schwarz");
            }
            text.append("]"); //$NON-NLS-1$
        }
    }

    private void appendGameNumber(StringBuffer text) {
        if (this.gameNumber > 0) {
            text.append(this.strings.getString("game") + " " + this.gameNumber + ": "); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
    }

    void setGameSummary(final GameSummary summary) {

        this.multiplier = summary.getGameMultiplier();
        this.playWithJacks = summary.isGamePlayedWithJacks();
        this.gameWon = summary.isGameWon();
        this.declarerPoints = summary.getFinalDeclarerPoints();
        this.opponentPoints = summary.getFinalOpponentScore();
        this.ramschLoosers = summary.getRamschLosers();
        refreshText();
    }

    String getGameStateString(final GameState state) {

        String result = null;

        switch (state) {
        case BIDDING:
            result = this.strings.getString("bidding_phase"); //$NON-NLS-1$
            break;
        case CALCULATING_GAME_VALUE:
            result = this.strings.getString("calc_game_value_phase"); //$NON-NLS-1$
            break;
        case DEALING:
            result = this.strings.getString("dealing_phase"); //$NON-NLS-1$
            break;
        case DECLARING:
            result = this.strings.getString("declaring_phase"); //$NON-NLS-1$
            break;
        case DISCARDING:
            result = this.strings.getString("discarding_phase"); //$NON-NLS-1$
            break;
        case GAME_OVER:
            result = this.strings.getString("game_over_phase"); //$NON-NLS-1$
            break;
        case GAME_START:
            result = this.strings.getString("game_start_phase"); //$NON-NLS-1$
            break;
        case PICKING_UP_SKAT:
        case RAMSCH_GRAND_HAND_ANNOUNCING:
        case SCHIEBERAMSCH:
            result = this.strings.getString("pick_up_skat_phase"); //$NON-NLS-1$
            break;
        case PRELIMINARY_GAME_END:
            result = this.strings.getString("preliminary_game_end_phase"); //$NON-NLS-1$
            break;
        case TRICK_PLAYING:
            result = this.strings.getString("trick_playing_phase"); //$NON-NLS-1$
            break;
        case CONTRA:
            result = this.strings.getString("contra_or_play_phase");
            break;
        case RE:
            result = this.strings.getString("re_or_play_phase");
            break;
        default:
            result = state.name();
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
    public void setTrickNumber(final int trickNumber) {
        this.trick = trickNumber;
        refreshText();
    }

    /**
     * Sets the game number
     *
     * @param newGameNumber
     *            Game number
     */
    public void setGameNumber(final int newGameNumber) {
        this.gameNumber = newGameNumber;
        refreshText();
    }

    public void setContra() {
        this.contra = true;
    }

    public void setRe() {
        this.re = true;
    }
}
