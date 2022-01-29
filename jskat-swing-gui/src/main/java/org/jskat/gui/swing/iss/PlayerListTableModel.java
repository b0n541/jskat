package org.jskat.gui.swing.iss;

import org.jskat.util.JSkatResourceBundle;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a model for the skat list table
 */
class PlayerListTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final JSkatResourceBundle strings;

    private final List<List<Object>> data;
    private final List<String> columns;

    /**
     * Constructor
     */
    public PlayerListTableModel() {

        this.strings = JSkatResourceBundle.INSTANCE;
        this.data = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.columns.add(this.strings.getString("name"));
        this.columns.add(this.strings.getString("games"));
        this.columns.add(this.strings.getString("strength"));
        this.columns.add(this.strings.getString("language"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {

        if (columnIndex == 1) {
            return Long.class;
        } else if (columnIndex == 2) {
            return Long.class;
        }
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {

        return this.columns.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {

        return this.data.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        return this.data.get(rowIndex).get(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(final int col) {

        return this.columns.get(col);
    }

    /**
     * Updates the player table model
     *
     * @param playerName  Player name
     * @param language    Player language
     * @param gamesPlayed Games played
     * @param strength    Player strength
     */
    public void updatePlayer(final String playerName, final String language,
                             final long gamesPlayed, final double strength) {

        boolean playerFound = false;
        int index = 0;

        // first try to find a player already known
        while (!playerFound && index < this.data.size()) {

            List<Object> currRow = this.data.get(index);
            // check player name
            if (currRow.get(0).equals(playerName)) {
                // player found
                updateRow(0, language, gamesPlayed, strength);
                playerFound = true;
            }
            index++;
        }

        if (!playerFound) {
            // player not found --> new player
            // add this player
            addRow(playerName, language, gamesPlayed, strength);
        }
    }

    private void updateRow(final int index, final String language,
                           final long gamesPlayed, final double strength) {

        List<Object> row = this.data.get(index);
        // set updated values
        row.set(1, Long.toString(gamesPlayed));
        row.set(2, Double.toString(strength));
        row.set(3, language);

        fireTableDataChanged();
    }

    private void addRow(final String playerName, final String language,
                        final long gamesPlayed, final double strength) {

        ArrayList<Object> newLine = new ArrayList<>();
        newLine.add(playerName);
        newLine.add(Long.valueOf(gamesPlayed));
        newLine.add(Double.valueOf(strength));
        newLine.add(language);
        this.data.add(newLine);

        fireTableDataChanged();
    }

    public void removePlayer(final String playerName) {

        int index = 0;
        int removeIndex = 0;
        for (List<Object> currRow : this.data) {

            if (currRow.get(0).equals(playerName)) {

                removeIndex = index;
            }
            index++;
        }
        this.data.remove(removeIndex);

        fireTableDataChanged();
    }
}
