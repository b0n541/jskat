package org.jskat.gui.swing.table;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    private boolean schneider;
    private boolean schwarzAnnounced;
    private boolean schwarz;
    private boolean overBidded;
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
        strings = JSkatResourceBundle.INSTANCE;

        initPanel();
    }

    private void initPanel() {

        setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

        setOpaque(true);

        label = new JLabel();
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        setGameState(GameState.GAME_START);

        add(label);
    }

    void clear() {

        label.setText(" "); //$NON-NLS-1$
    }

    void setGameState(final GameState newGameState) {

        gameState = newGameState;

        if (gameState.equals(GameState.GAME_START)) {

            resetGameData();
        }

        refreshText();
    }

    void setGameAnnouncement(final GameAnnouncement announcement) {
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
        schneider = false;
        schwarzAnnounced = false;
        schwarz = false;
        overBidded = false;
        contra = false;
        re = false;
        trick = 1;
        gameWon = false;
        declarerPoints = 0;
        opponentPoints = 0;
        ramschLoosers = new HashSet<Player>();
    }

    private void refreshText() {

        StringBuffer text = new StringBuffer();

        appendGameNumber(text);

        text.append(getGameStateString(gameState));

        appendGameType(text);

        appendGameStateDetails(text);

        label.setText(text.toString());
    }

    private void appendGameStateDetails(StringBuffer text) {
        if (gameState.equals(GameState.TRICK_PLAYING)) {
            appendTrickPlayingDetails(text);
        } else if (gameState.equals(GameState.GAME_OVER)) {

            appendGameOverDetails(text);
        }
    }

    private void appendGameOverDetails(StringBuffer text) {
        if (gameType != GameType.PASSED_IN) {
            text.append(" - "); //$NON-NLS-1$
            if (gameWon) {
                text.append(strings.getString("won")); //$NON-NLS-1$
            } else {
                text.append(strings.getString("lost")); //$NON-NLS-1$
            }
        }

        if (gameType == GameType.RAMSCH) {
            text.append(" - "); //$NON-NLS-1$

            Iterator<Player> iterator = ramschLoosers.iterator();
            if (iterator.hasNext()) {
                text.append(strings.getPlayerString(iterator.next()));
            }
            while (iterator.hasNext()) {
                text.append(", ");
                text.append(strings.getPlayerString(iterator.next()));
            }

        } else if (gameType != GameType.NULL && gameType != GameType.PASSED_IN) {
            text.append(" - "); //$NON-NLS-1$

            text.append(
                    declarerPoints + " " + strings.getString("versus") + " ");
            text.append(opponentPoints + " " + strings.getString("points")); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    private void appendTrickPlayingDetails(StringBuffer text) {
        text.append(" " + strings.getString("trick") + " " + trick); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    private void appendGameType(StringBuffer text) {
        if (gameType != null) {
            text.append(" [" + strings.getGameType(gameType)); //$NON-NLS-1$

            if (gameState.equals(GameState.GAME_OVER) && multiplier > 0) {
                if (playWithJacks) {
                    text.append(" " + strings.getString("with")); //$NON-NLS-1$//$NON-NLS-2$
                } else {
                    text.append(" " + strings.getString("without")); //$NON-NLS-1$//$NON-NLS-2$
                }
                text.append(" " + (multiplier - 1)); //$NON-NLS-1$
                text.append(" " + strings.getString("play")); //$NON-NLS-1$//$NON-NLS-2$
                text.append(" " + multiplier); //$NON-NLS-1$
            }

            if (handGame) {
                text.append(" " + strings.getString("hand"));
            }

            if (ouvertGame) {
                text.append(" " + strings.getString("ouvert"));
            }

            if (schneiderAnnounced) {
                text.append(" " + strings.getString("schneider_announced"));
            } else if (schneider) {
                text.append(" " + strings.getString("schneider"));
            }

            if (schwarzAnnounced) {
                text.append(" " + strings.getString("schwarz_announced"));
            } else if (schwarz) {
                text.append(" " + strings.getString("schwarz"));
            }

            if (contra) {
                text.append(" " + strings.getString("contra"));
            }

            if (re) {
                text.append(" " + strings.getString("re"));
            }

            if (overBidded) {
                text.append(" " + strings.getString("overbidded"));
            }

            text.append("]"); //$NON-NLS-1$
        }
    }

    private void appendGameNumber(StringBuffer text) {
        if (gameNumber > 0) {
            text.append(strings.getString("game") + " " + gameNumber + ": "); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
    }

    void setGameSummary(final GameSummary summary) {

        multiplier = summary.getGameMultiplier();
        playWithJacks = summary.isGamePlayedWithJacks();
        gameWon = summary.isGameWon();
        declarerPoints = summary.getFinalDeclarerPoints();
        opponentPoints = summary.getFinalOpponentScore();
        ramschLoosers = summary.getRamschLosers();
        overBidded = summary.gameResult.isOverBidded();
        schneider = summary.gameResult.isSchneider();
        schwarz = summary.gameResult.isSchwarz();
        refreshText();
    }

    String getGameStateString(final GameState state) {

        String result = null;

        switch (state) {
            case BIDDING:
                result = strings.getString("bidding_phase"); //$NON-NLS-1$
                break;
            case CALCULATING_GAME_VALUE:
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
            case PICKING_UP_SKAT:
            case RAMSCH_GRAND_HAND_ANNOUNCING:
            case SCHIEBERAMSCH:
                result = strings.getString("pick_up_skat_phase"); //$NON-NLS-1$
                break;
            case PRELIMINARY_GAME_END:
                result = strings.getString("preliminary_game_end_phase"); //$NON-NLS-1$
                break;
            case TRICK_PLAYING:
                result = strings.getString("trick_playing_phase"); //$NON-NLS-1$
                break;
            case CONTRA:
                result = strings.getString("contra_or_play_phase");
                break;
            case RE:
                result = strings.getString("re_or_play_phase");
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
     * @param trickNumber Trick number
     */
    public void setTrickNumber(final int trickNumber) {
        trick = trickNumber;
        refreshText();
    }

    /**
     * Sets the game number
     *
     * @param newGameNumber Game number
     */
    public void setGameNumber(final int newGameNumber) {
        gameNumber = newGameNumber;
        refreshText();
    }

    public void setContra() {
        contra = true;
        refreshText();
    }

    public void setRe() {
        re = true;
        refreshText();
    }
}
