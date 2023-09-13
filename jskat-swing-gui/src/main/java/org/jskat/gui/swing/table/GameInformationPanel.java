package org.jskat.gui.swing.table;

import org.jskat.data.GameContract;
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

        setLayout(LayoutFactory.getMigLayout("fill"));

        setOpaque(true);

        label = new JLabel();
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        setGameState(GameState.GAME_START);

        add(label);
    }

    void clear() {

        label.setText(" ");
    }

    void setGameState(final GameState newGameState) {

        gameState = newGameState;

        if (gameState.equals(GameState.GAME_START)) {

            resetGameData();
        }

        refreshText();
    }

    void setGameContract(final GameContract contract) {
        gameType = contract.gameType();
        handGame = contract.hand();
        ouvertGame = contract.ouvert();
        schneiderAnnounced = contract.schneider();
        schwarzAnnounced = contract.schwarz();
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

        final StringBuffer text = new StringBuffer();

        appendGameNumber(text);

        text.append(getGameStateString(gameState));

        appendGameType(text);

        appendGameStateDetails(text);

        label.setText(text.toString());
    }

    private void appendGameStateDetails(final StringBuffer text) {
        if (gameState.equals(GameState.TRICK_PLAYING)) {
            appendTrickPlayingDetails(text);
        } else if (gameState.equals(GameState.GAME_OVER)) {

            appendGameOverDetails(text);
        }
    }

    private void appendGameOverDetails(final StringBuffer text) {
        if (gameType != GameType.PASSED_IN) {
            text.append(" - ");
            if (gameWon) {
                text.append(strings.getString("won"));
            } else {
                text.append(strings.getString("lost"));
            }
        }

        if (gameType == GameType.RAMSCH) {
            text.append(" - ");

            final Iterator<Player> iterator = ramschLoosers.iterator();
            if (iterator.hasNext()) {
                text.append(strings.getPlayerString(iterator.next()));
            }
            while (iterator.hasNext()) {
                text.append(", ");
                text.append(strings.getPlayerString(iterator.next()));
            }

        } else if (gameType != GameType.NULL && gameType != GameType.PASSED_IN) {
            text.append(" - ");

            text.append(
                    declarerPoints + " " + strings.getString("versus") + " ");
            text.append(opponentPoints + " " + strings.getString("points"));
        }
    }

    private void appendTrickPlayingDetails(final StringBuffer text) {
        text.append(" " + strings.getString("trick") + " " + trick);
    }

    private void appendGameType(final StringBuffer text) {
        if (gameType != null) {
            text.append(" [" + strings.getGameType(gameType));

            if (gameState.equals(GameState.GAME_OVER) && multiplier > 0) {
                if (playWithJacks) {
                    text.append(" " + strings.getString("with"));
                } else {
                    text.append(" " + strings.getString("without"));
                }
                text.append(" " + (multiplier - 1));
                text.append(" " + strings.getString("play"));
                text.append(" " + multiplier);
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

            text.append("]");
        }
    }

    private void appendGameNumber(final StringBuffer text) {
        if (gameNumber > 0) {
            text.append(strings.getString("game") + " " + gameNumber + ": ");
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
                result = strings.getString("bidding_phase");
                break;
            case CALCULATING_GAME_VALUE:
                result = strings.getString("calc_game_value_phase");
                break;
            case DEALING:
                result = strings.getString("dealing_phase");
                break;
            case DECLARING:
                result = strings.getString("declaring_phase");
                break;
            case DISCARDING:
                result = strings.getString("discarding_phase");
                break;
            case GAME_OVER:
                result = strings.getString("game_over_phase");
                break;
            case GAME_START:
                result = strings.getString("game_start_phase");
                break;
            case PICKING_UP_SKAT:
            case RAMSCH_GRAND_HAND_ANNOUNCING:
            case SCHIEBERAMSCH:
                result = strings.getString("pick_up_skat_phase");
                break;
            case PRELIMINARY_GAME_END:
                result = strings.getString("preliminary_game_end_phase");
                break;
            case TRICK_PLAYING:
                result = strings.getString("trick_playing_phase");
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
