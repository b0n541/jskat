package org.jskat.gui.swing.table;

import org.jskat.data.GameSummary;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.SkatListMode;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides a model for the skat list table
 */
class SkatListTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final JSkatResourceBundle strings;

    private SkatListMode mode = SkatListMode.NORMAL;

    private int playerCount = 3;
    private final List<List<Integer>> playerResults;
    private final List<GameSummary> gameResults;
    private final List<List<Boolean>> playerWithChangedPoints;
    private final List<String> columns;
    private final List<List<Integer>> displayValues;

    /**
     * Constructor
     */
    SkatListTableModel() {

        strings = JSkatResourceBundle.INSTANCE;

        playerResults = new ArrayList<>();
        gameResults = new ArrayList<>();
        playerWithChangedPoints = new ArrayList<>();
        displayValues = new ArrayList<>();
        columns = new ArrayList<>();
        setColumns();
    }

    /**
     * @see AbstractTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {

        return columns.size();
    }

    /**
     * @see AbstractTableModel#getRowCount()
     */
    @Override
    public int getRowCount() {

        return gameResults.size();
    }

    /**
     * @see AbstractTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        Object result = null;

        if (rowIndex < getRowCount() && columnIndex < getColumnCount()) {
            if (displayValues.get(rowIndex).get(columnIndex) != null) {
                result = displayValues.get(rowIndex).get(columnIndex);
            } else {
                result = "-"; //$NON-NLS-1$
            }
        }

        return result;
    }

    /**
     * @see AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int col) {

        if (col >= 0 && col < getColumnCount()) {
            return columns.get(col);
        }
        return null;
    }

    /**
     * @see AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(final int col) {

        if (col >= 0 && col < getColumnCount()) {
            return Integer.class;
        }
        return null;
    }

    private void calculateDisplayValues() {

        int currResult = 0;
        final List<Integer> playerResultsSoFar = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            playerResultsSoFar.add(0);
        }

        displayValues.clear();

        for (int game = 0; game < gameResults.size(); game++) {

            displayValues.add(new ArrayList<>());

            // add player values
            for (int player = 0; player < playerCount; player++) {

                int previousResult = 0;
                currResult = 0;

                if ("" != null) {

                    // get previous result for player values
                    previousResult = playerResultsSoFar.get(player);

                    // get player results from current game
                    switch (mode) {
                        case NORMAL:
                            currResult = playerResults.get(player).get(game);
                            break;
                        case TOURNAMENT:
                            final boolean isDeclarer = (playerResults.get(player).get(game) != 0);
                            currResult = SkatConstants.getTournamentGameValue(isDeclarer, gameResults.get(game)
                                    .getGameValue(), playerCount);
                            break;
                        case BIERLACHS:
                            // FIXME jan 31.05.2010 add bierlachs value
                            break;
                    }
                }

                if (currResult != 0) {

                    final int newResult = previousResult + currResult;
                    displayValues.get(game).add(newResult);
                    playerResultsSoFar.set(player, newResult);

                } else {

                    displayValues.get(game).add(null);
                }
            }

            // get game result
            switch (mode) {
                case NORMAL:
                case BIERLACHS:
                    currResult = gameResults.get(game).getGameValue();
                    break;
                case TOURNAMENT:
                    currResult = SkatConstants.getTournamentGameValue(true, gameResults.get(game).getGameValue(),
                            playerCount);
                    break;
            }
            displayValues.get(game).add(currResult);
        }
    }

    /**
     * Adds a game result to the model
     *
     * @param leftOpponent  Position of the upper left opponent
     * @param rightOpponent Position of the upper right opponent
     * @param user          Position of the player
     * @param gameSummary   Game summary
     */
    void addResult(final Player leftOpponent, final Player rightOpponent, final Player user, final GameSummary gameSummary) {

        // FIXME works only on 3 player series
        // FIXME (jansch 21.03.2011) provide only one method for addResult()
        gameResults.add(gameSummary);
        List<Boolean> currPlayerWithChangedPoints = Arrays.asList(false, false, false);

        if (gameSummary.getDeclarer() != null) {
            final int declarerColumn = getDeclarerColumn(leftOpponent, rightOpponent, user, gameSummary.getDeclarer());
            playerResults.get(declarerColumn).add(gameSummary.getGameValue());
            playerResults.get((declarerColumn + 1) % 3).add(0);
            playerResults.get((declarerColumn + 2) % 3).add(0);
            currPlayerWithChangedPoints.set(declarerColumn, true);
        } else if (!gameSummary.getRamschLosers().isEmpty()) {

            Set<Integer> ramschLoserColumns = gameSummary.getRamschLosers().stream()
                    .map(it -> getDeclarerColumn(leftOpponent, rightOpponent, user, it))
                    .collect(Collectors.toSet());

            for (int i = 0; i < playerCount; i++) {
                if (ramschLoserColumns.contains(i)) {
                    playerResults.get(i).add(gameSummary.getGameValue());
                    currPlayerWithChangedPoints.set(i, true);
                } else {
                    playerResults.get(i).add(0);
                }
            }
        } else {
            // game was passed in
            for (int i = 0; i < playerCount; i++) {
                playerResults.get(i).add(0);
            }
        }
        calculateDisplayValues();

        fireTableDataChanged();
    }

    private static int getDeclarerColumn(final Player leftOpponent, final Player rightOpponent, final Player player,
                                         final Player declarer) {

        int result = -1;

        if (declarer == leftOpponent) {
            result = 0;
        } else if (declarer == rightOpponent) {
            result = 1;
        } else if (declarer == player) {
            result = 2;
        }

        return result;
    }

    /**
     * Clears the complete list
     */
    void clearList() {

        for (final List<Integer> currList : playerResults) {

            currList.clear();
        }
        gameResults.clear();
        displayValues.clear();

        fireTableDataChanged();
    }

    private void setColumns() {

        playerResults.clear();
        displayValues.clear();
        columns.clear();

        for (int i = 0; i < playerCount; i++) {
            // FIXME (jan 14.12.2010) get player names
            columns.add("P" + i);
            playerResults.add(new ArrayList<>());
            displayValues.add(new ArrayList<>());
        }
        columns.add(strings.getString("games")); //$NON-NLS-1$
        displayValues.add(new ArrayList<>());
    }

    void setPlayerNames(final String upperLeftPlayer, final String upperRightPlayer, final String lowerPlayer) {

        columns.set(0, upperLeftPlayer);
        columns.set(1, upperRightPlayer);
        columns.set(2, lowerPlayer);
        fireTableStructureChanged();
    }
}
