package org.jskat.control.command.table;

import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the show cards command type.
 */
public class ShowCardsCommand extends AbstractTableCommand {

    /**
     * Cards currently held by each player at the table.
     */
    public final Map<Player, CardList> cards = new HashMap<>();
    /**
     * Cards currently in the skat.
     */
    public final CardList skat = new CardList();

    /**
     * Creates a command to show one player's cards.
     * @param tableName table name value.
     * @param player player whose cards are shown
     * @param cards cards held by that player
     */
    public ShowCardsCommand(String tableName, Player player, CardList cards) {
        this(tableName, Map.of(player, cards), null);
    }

    /**
     * Creates a command to display players' cards and the skat.
     * @param tableName target table name
     * @param cards cards held by each player
     * @param skat cards in the skat
     */
    public ShowCardsCommand(String tableName, Map<Player, CardList> cards, CardList skat) {
        super(tableName);
        this.cards.putAll(cards);
        if (skat != null) {
            this.skat.addAll(skat);
        }
    }
}
